
from pathlib import Path
import json
from helpers import run_prompt, create_planning_prompts, create_implementation_prompt
import torch
from transformers import AutoModelForCausalLM, AutoTokenizer
import logging
from parse_code import parse_output
import subprocess
import yaml
import sys
import os

def create_prompts(test_cases):
    prompts = {}
    prepend = 'You are a Scala code generator. You will be given a task description and you will generate the code for it. The code should start with ```scala and end with ```. The task is: '
    for name, test_case in test_cases.items():
        prompts[name] = prepend + "\n" + test_case
    return prompts

################## AGENTIC SETTING ##################
def prompt_for_vulnerabilities(test_case, previous_code):
    prepend = 'You will be given a task description and generated code. Your task is to find potential vulnerabilities in the code that could lead to security issues or unexpected behavior. Solely describe the vulnerabilities, do not give me any code.'
    prompt = f"{prepend}\n\nHere is the task: {test_case}\n\nHere is the previous code: {previous_code}"
    return prompt

def create_final_prompt_vulnerabilities(task, previous_code, vulnerabilities):
    prepend = 'You are a Scala code generator. You will be given a task description, generated code, and vulnerabilities that should be addressed. Your task is to improve the code by using the Scala type system - for example ADTs, refined types, traits, sealed traits - to address the vulnerabilities. The code should start with ```scala and end with ```.'
    prompt = f"{prepend}\n\nHere is the task: {task}\n\nHere is the previous code: {previous_code}\n\nHere are the vulnerabilities: {vulnerabilities}"
    return prompt

################### ZERO-SHOT ROBUST SETTING ##################
def safe_prompting(test_cases):
    prompts = {}
    prepend = 'You are a scala code generator. You will be given a task description and you will generate the code for it. The code should start with ```scala and end with ```. Pay attention to the safety and robustness of the code, and leverage the Scala type system where needed to make the code safer. The task is: '
    for name, test_case in test_cases.items():
        prompts[name] = prepend + "\n" + test_case
    return prompts

################### SAFEPILOT SETTING ##################
def create_final_prompt_vulnerabilities_notypes(task, previous_code, vulnerabilities):
    prepend = 'You are a Scala code generator. You will be given a task description, generated code, and vulnerabilities that should be addressed. Your task is to improve the code. The code should start with ```scala and end with ```.'
    prompt = f"{prepend}\n\nHere is the task: {task}\n\nHere is the previous code: {previous_code}\n\nHere are the vulnerabilities: {vulnerabilities}"
    return prompt

def main():
    # Set up a logger, that will log to a file and to the console
    # parse first argument given to the script
    config_path = sys.argv[1] if len(sys.argv) > 1 else "src/general_tasks/configs.yaml"

    with open(config_path, "r") as f:
        configs = yaml.safe_load(f)
    path_test_cases = Path(configs['path_test_cases'])
    path_result = Path(configs['path_save_results'])
    if not path_result.exists():
        path_result.mkdir(parents=True)
    models = configs['models']
    test_cases_filtered = configs['test_cases_filtered']
    parse = configs['parse']
    compile = configs['compile']
    generation_mode = configs['mode']

    settings = {
        "max_new_tokens": configs['max_new_tokens'],
        "temperature": configs['temperature'],
    }
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(levelname)s - %(message)s',
        handlers=[
            logging.FileHandler(path_result.joinpath("log.txt")),
            logging.StreamHandler()
        ]
    )
    logger = logging.getLogger(__name__)
    logger.info("Starting the script, running in mode: " + generation_mode)

    if generation_mode not in ['baseline', 'robust', 'planning', 'safepilot', 'agentic']:
        raise ValueError(f"Invalid generation mode: {generation_mode}. Choose from 'baseline', 'robust', 'planning', 'safepilot', 'agentic'.")
    
    if generation_mode == 'planning':
        save_planning_path = path_result.joinpath("planning_outputs.json")
        plannings = {}

    with open(path_test_cases, "r") as f:
        test_cases = json.load(f)

    if len(test_cases_filtered) > 0:
        test_cases = {name: test_case for name, test_case in test_cases.items() if name in test_cases_filtered}

    for model_name in models:
        logger.info(f"Running model: {model_name}")

        gpu_memory = torch.cuda.memory_allocated() / (1024 ** 3)
        logger.info(f"GPU memory usage: {gpu_memory:.2f} GB")

        responses = {}

        # Create a folder result/model_name
        path_model_result = path_result.joinpath(model_name)
        path_model_output = path_model_result.joinpath("outputs")
        path_model_output.mkdir(parents=True, exist_ok=True)

        path_save_responses = path_model_output.joinpath("responses.json")
        if path_save_responses.exists():
            logger.info(f"Model {model_name} already ran, skipping")
            
        else:
            model = AutoModelForCausalLM.from_pretrained(model_name, torch_dtype=torch.float16, device_map='auto')
            tokenizer = AutoTokenizer.from_pretrained(model_name, use_fast=True)
            model.eval()

            prompts = create_prompts(test_cases)

            if generation_mode == 'robust':
                prompts_robust = safe_prompting(test_cases)
            
            if generation_mode == 'planning':
                prompts_planning = create_planning_prompts(test_cases)

            for name, test_case in prompts.items():
                logger.info(f"Running test case: {name}")
                # If we are in robust mode, we will use the robust prompts
                if generation_mode == 'robust':
                    test_case = prompts_robust[name]
                    response, full_response = run_prompt(test_case, tokenizer, model, settings, model_name, logger=logger)
                elif generation_mode == 'planning':
                    test_case = prompts_planning[name]
                    response_planning, _ = run_prompt(test_case, tokenizer, model, settings, model_name, logger=logger)
                    plannings[name] = response_planning
                    prompt_implementation = create_implementation_prompt(response_planning, test_case)
                    response, full_response = run_prompt(prompt_implementation, tokenizer, model, settings, model_name, logger=logger)
                else:
                    response, full_response = run_prompt(test_case, tokenizer, model, settings, model_name, logger=logger)

                if parse:    
                    response = parse_output(response)
                    while response is None:
                        logger.info(f"Parsed output is None, trying again")
                        response, full_response = run_prompt(test_case, tokenizer, model, settings, model_name, logger=logger)
                        response = parse_output(response)
                # Now we want to save it to a .scala file to see whether it compiles
                subfolder = path_model_result.joinpath(name)
                # Create the subfolder if it doesn't exist
                if not subfolder.exists():
                    subfolder.mkdir(parents=True, exist_ok=True)
                if generation_mode == 'baseline' or generation_mode == 'robust' or generation_mode == 'planning':
                    code_path = subfolder.joinpath("generated_code.scala")
                else:
                    code_path = subfolder.joinpath("initial_code.scala")
                with open(code_path, "w") as f:
                    f.write(response)
                                # Now we want to find the input constraints

                if generation_mode == 'agentic' or generation_mode == 'safepilot':
                    logger.info("Running in agentic mode")
                    # First we want to find the vulnerabilities
                    prompt_vulnerabilities = prompt_for_vulnerabilities(test_case, response)
                    vulnerabilities, _ = run_prompt(prompt_vulnerabilities, tokenizer, model, settings, model_name, logger=logger)

                    # save the vulnerabilities to a file
                    with open(subfolder.joinpath("vulnerabilities.txt"), "w") as f:
                        f.write(vulnerabilities)

                    # Now we want to create the final prompt
                    if generation_mode == 'agentic':
                        final_prompt = create_final_prompt_vulnerabilities(test_case, response, vulnerabilities)
                    else:  # safepilot
                        final_prompt = create_final_prompt_vulnerabilities_notypes(test_case, response, vulnerabilities)
                    response, full_response = run_prompt(final_prompt, tokenizer, model, settings, model_name, logger=logger)

                    if parse:
                        response = parse_output(response)
                        while response is None:
                            logger.info(f"Parsed output is None, trying again")
                            new_code, full_response = run_prompt(final_prompt, tokenizer, model, settings, model_name, logger=logger)
                            response = parse_output(new_code)

                    code_path = subfolder.joinpath("generated_code.scala")
                    with open(code_path, "w") as f:
                            f.write(response)

                if compile:
                    # Now we will run the code and see if it compiles
                    compilation = subprocess.run([f"scalac", "-d", subfolder, subfolder.joinpath('generated_code.scala')], 
                        capture_output=True)
                    return_code = compilation.returncode
                    for i in range(configs['num_compilation_attempts']):
                        if return_code == 0:
                            break
                        logger.info(f"Compilation failed for test case: {name}, trying again, attempt {i}")
    
                        response, full_response = run_prompt(test_case, tokenizer, model, settings, model_name, logger=logger)
                        response = parse_output(response)
                        while response is None:
                            logger.info(f"Parsed output is None, trying again")
                            response, full_response = run_prompt(test_case, tokenizer, model, settings, model_name, logger=logger)
                            response = parse_output(response)
                        with open(code_path, "w") as f:
                            f.write(response)
                        compilation = subprocess.run([f"scalac", "-d", subfolder, subfolder.joinpath('generated_code.scala')], 
                            capture_output=True)
                        return_code = compilation.returncode
                    if return_code != 0:
                        logger.info(f"Compilation failed for test case: {name}")

                responses[name] = response

            with open(path_save_responses, "w") as f:
                json.dump(responses, f, indent=4)

            logger.info(f"Responses saved to {path_save_responses}")

            # clear memory
            del model
            del tokenizer
            torch.cuda.empty_cache()
            

    logger.info("All models finished running")
    logger.info("Script finished")
        
if __name__ == "__main__":
    main()