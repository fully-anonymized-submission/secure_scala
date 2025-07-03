
from pathlib import Path
import json
from helpers import run_prompt
import torch
from transformers import AutoModelForCausalLM, AutoTokenizer
import logging
from parse_code import parse_output
import subprocess
import yaml

def create_prompts(test_cases):
    prompts = {}
    prepend = 'You are a Scala code generator. You will be given a task description and you will generate the code for it. The code should start with ```scala and end with ```. The task is: '
    for name, test_case in test_cases.items():
        prompts[name] = prepend + "\n" + test_case
    return prompts

################## GENERAL SETTING ##################
def prompt_for_vulnerabilities(test_case, previous_code):
    prepend = 'You will be given a task description and generated code. Your task is to find potential vulnerabilities in the code that could lead to security issues or unexpected behavior. Solely describe the vulnerabilities, do not give me any code.'
    prompt = f"{prepend}\n\nHere is the task: {test_case}\n\nHere is the previous code: {previous_code}"
    return prompt

def create_final_prompt_vulnerabilities(task, previous_code, vulnerabilities):
    prepend = 'You are a Scala code generator. You will be given a task description, generated code, and vulnerabilities that should be addressed. Your task is to improve the code by using the Scala type system - for example ADTs, refined types, traits, sealed traits - to address the vulnerabilities. The code should start with ```scala and end with ```.'
    prompt = f"{prepend}\n\nHere is the task: {task}\n\nHere is the previous code: {previous_code}\n\nHere are the vulnerabilities: {vulnerabilities}"
    return prompt

################## INPUT CONSTRAINTS SETTING ##################
def prompt_for_input_constraints(test_case, previous_code):
    prepend = 'You will be given a task description and generated code. Your task is to make the code more robust by finding input constraints that are currently not implemented in the code. You should give me the input constraints for the code that will make it robust to all possible input. Solely describe to me what the input constraints should be, do not give me any code.'
    prompt = f"{prepend}\n\nHere is the task: {test_case}\n\nHere is the previous code: {previous_code}"
    return prompt

def create_final_prompt(task, previous_code, input_constraints):
    prepend = 'You are a Scala code generator. You will be given a task description, generated code, and input constraints that should be used to make the code more robust. Your task is to improve the code by implementing the input constraints. You can use the "require" function to implement the constraints. The code should start with ```scala and end with ```.'
    prompt = f"{prepend}\n\nHere is the task: {task}\n\nHere is the previous code: {previous_code}\n\nHere are the input constraints: {input_constraints}"
    return prompt

################### ZERO-SHOT ROBUST SETTING ##################
def safe_prompting(test_cases):
    prompts = {}
    prepend = 'You are a scala code generator. You will be given a task description and you will generate the code for it. The code should start with ```scala and end with ```. Pay attention to the safety and robustness of the code, and leverage the Scala type system where needed to make the code safer. The task is: '
    for name, test_case in test_cases.items():
        prompts[name] = prepend + "\n" + test_case
    return prompts

################################################################

def main():
    # Set up a logger, that will log to a file and to the console

    root = Path.cwd()
    with open(root.joinpath("configs.yaml"), "r") as f:
        configs = yaml.safe_load(f)
    path_test_cases = configs['path_test_cases']
    path_result = configs['path_save_results']
    if not path_result.exists():
        path_result.mkdir(parents=True)
    models = configs['models']
    test_cases_filtered = configs['test_cases_filtered']
    parse = configs['parse']
    compile = configs['compile']
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
    logger.info("Starting the script, running in mode: " + configs['generation_mode'])

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

            for name, test_case in prompts.items():
                logger.info(f"Running test case: {name}")
                response, full_response = run_prompt(test_case, tokenizer, model, settings, model_name, logger=logger)
                if parse:
                    parsed_output = parse_output(response)
                    while parsed_output is None:
                        logger.info(f"Parsed output is None, the full raw response is: {full_response}, trying again")
                        response, full_response = run_prompt(test_case, tokenizer, model, settings, model_name, logger=logger)
                        parsed_output = parse_output(response)
                    # Now we want to save it to a .scala file to see whether it compiles
                    subfolder = path_model_result.joinpath(name)
                    # Create the subfolder if it doesn't exist
                    if not subfolder.exists():
                        subfolder.mkdir(parents=True, exist_ok=True)
                    if configs['generation_mode'] == 'baseline':
                        code_path = subfolder.joinpath("generated_code.scala")
                    else:
                        code_path = subfolder.joinpath("initial_code.scala")
                    with open(code_path, "w") as f:
                        f.write(parsed_output)
                                    # Now we want to find the input constraints
                    if configs['generation_mode'] == 'input_constraints':
                        logger.info("Running in input constraints mode")
                        prompt_input_constraint = prompt_for_input_constraints(test_case, parsed_output)
                        input_constraints, _ = run_prompt(prompt_input_constraint, tokenizer, model, settings, model_name, logger=logger)

                        # save the input constraints to a file
                        with open(subfolder.joinpath("input_constraints.txt"), "w") as f:
                            f.write(input_constraints)

                        # Now we want to create the final prompt
                        final_prompt = create_final_prompt(test_case, parsed_output, input_constraints)
                        new_code, _ = run_prompt(final_prompt, tokenizer, model, settings, model_name, logger=logger)

                    elif configs['generation_mode'] == 'general_vulnerabilities':
                        logger.info("Running in general vulnerabilities mode")
                        # First we want to find the vulnerabilities
                        prompt_vulnerabilities = prompt_for_vulnerabilities(test_case, parsed_output)
                        vulnerabilities, _ = run_prompt(prompt_vulnerabilities, tokenizer, model, settings, model_name, logger=logger)

                        # save the vulnerabilities to a file
                        with open(subfolder.joinpath("vulnerabilities.txt"), "w") as f:
                            f.write(vulnerabilities)

                        # Now we want to create the final prompt
                        final_prompt = create_final_prompt_vulnerabilities(test_case, parsed_output, vulnerabilities)
                        new_code, full_response = run_prompt(final_prompt, tokenizer, model, settings, model_name, logger=logger)

                    parsed_output = parse_output(new_code)

                    while parsed_output is None:
                        logger.info(f"Parsed output is None, the full raw response is: {full_response}, trying again")
                        new_code, full_response = run_prompt(final_prompt, tokenizer, model, settings, model_name, logger=logger)
                        parsed_output = parse_output(new_code)



                    code_path = subfolder.joinpath("generated_code.scala")
                    with open(code_path, "w") as f:
                        f.write(parsed_output)
                    


                if compile:
                    # Now we will run the code and see if it compiles
                    compilation = subprocess.run([f"scalac", "-d", subfolder, subfolder.joinpath('generated_code.scala')], 
                        capture_output=True)
                    return_code = compilation.returncode
                    for i in range(15):
                        if return_code == 0:
                            break
                        logger.info(f"Compilation failed for test case: {name}, trying again, attempt {i}")
                        logger.info(f"Parsed output is: {parsed_output}")
                        logger.info(f"Full response is: {full_response}")
                        response, full_response = run_prompt(test_case, tokenizer, model, settings, model_name, logger=logger)
                        parsed_output = parse_output(response)
                        while parsed_output is None:
                            logger.info(f"Parsed output is None, the full raw response is: {full_response}, trying again")
                            response, full_response = run_prompt(test_case, tokenizer, model, settings, model_name, logger=logger)
                            parsed_output = parse_output(response)
                        with open(code_path, "w") as f:
                            f.write(parsed_output)
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