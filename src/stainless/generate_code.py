from hugchat import hugchat
from hugchat.login import Login
from pathlib import Path
from transformers import AutoModelForCausalLM, AutoTokenizer
import torch
import json
import yaml
import logging
import gc

# get password from .env file
import os
from dotenv import load_dotenv


def get_indices_huggingchat(chatbot):
    name_to_index = {}
    for idx, model in enumerate(chatbot.get_available_llm_models()):
        name_to_index[model.name] = idx
    return name_to_index

def start_chatbot(email, passwd, cookie_path_dir):
    sign = Login(email, passwd)
    cookies = sign.login(cookie_dir_path=cookie_path_dir, save_cookies=True)
    chatbot = hugchat.ChatBot(cookies=cookies.get_dict())
    return chatbot

def create_fewshot_prompt(test_cases, fewshot_examples_path, fewshot_subset = None):
    prompts = {}
    with open(fewshot_examples_path, "r") as f:
        fewshot_examples = json.load(f)
    if fewshot_subset is not None:
        fewshot_examples = {k: v for k, v in fewshot_examples.items() if k in fewshot_subset}
    fewshot_prepend = ''
    for name, prompt_answer in fewshot_examples.items():
        fewshot_prepend += prompt_answer['prompt']
        fewshot_prepend += prompt_answer['answer']
    for name, test_case in test_cases.items():
        prompts[name] = fewshot_prepend + '<question> ' + test_case
    return prompts

def run_prompt_local(text, tokenizer, model, settings, model_name, logger=None):
    """
    Tokenizes the input text using the provided tokenizer.
    
    Args:
        text (str): The input text to tokenize.
        tokenizer: The tokenizer to use for tokenization.
    
    Returns:
        list: A list of tokenized words.
    """
    logging.info(f"Running model {model_name}")
    prompt = [
        {
            "role": "user",
            "content": text
        },
    ]
    prompt_formatted = tokenizer.apply_chat_template(prompt, tokenize=False, add_generation_prompt=True)

    #if tokenizer does not have padding token, add it as eos token
    if tokenizer.pad_token is None:
        tokenizer.pad_token = tokenizer.eos_token  
    # Now we tokenize the prompt
    inputs = tokenizer(prompt_formatted, return_tensors='pt', padding='longest', pad_to_multiple_of=8).to(model.device)
    attention_mask = inputs['attention_mask']
    inputs = inputs['input_ids']

    with torch.no_grad():
        outputs = model.generate(inputs, max_new_tokens=settings['max_new_tokens'], temperature=settings['temperature'], attention_mask=attention_mask, do_sample=True)

    full_output_str = tokenizer.decode(outputs[0], skip_special_tokens=True)
    output_str_new_tokens = tokenizer.decode(outputs[0][inputs.shape[1]:], skip_special_tokens=True)

    if model_name == 'deepseek-ai/DeepSeek-R1-Distill-Qwen-32B' or model_name == "Qwen/Qwen3-8B":
        try:
            output_str_new_tokens = output_str_new_tokens.split('</think>')[1].strip()
        # if there is no </think> tag, we rerun the model
        except IndexError:
            del inputs
            del attention_mask
            del outputs
            logging.info(f"Rerunning model {model_name} due to missing </think> tag in output.")
            return run_prompt_local(text, tokenizer, model, settings, model_name, logger)

    # Clear the memory
    del inputs
    del attention_mask
    del outputs
    return output_str_new_tokens, full_output_str

def main():
    root = Path.cwd()
    with open(root.joinpath("src/stainless/configs.yaml"), "r") as f:
        configs = yaml.safe_load(f)
    mode = configs['mode']

    if mode == 'huggingface':
        load_dotenv()
        PASSWD = os.getenv("HUGGINGFACE_PASSWORD")
        huggingface_email = configs['huggingface_email']
        cookie_path_dir = str(root.joinpath("data/cookies/"))
        chatbot = start_chatbot(huggingface_email, PASSWD, cookie_path_dir)
        name_to_index = get_indices_huggingchat(chatbot)
        models = list(name_to_index.keys())
        # clean cookies
        os.system(f"rm -rf {cookie_path_dir}*")

    elif mode == 'local':
        models = configs['models']
        settings = {
            "max_new_tokens": configs['max_new_tokens'],
            "temperature": configs['temperature'],
        }

    fewshot_examples_path = root.joinpath(configs['path_fewshot_examples'])
    path_test_cases = root.joinpath(configs['path_test_cases'])
    path_result = root.joinpath(configs['path_save_results'])
    if not path_result.exists():
        path_result.mkdir(parents=True)


    with open(path_test_cases, "r") as f:
        test_cases = json.load(f)



    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(levelname)s - %(message)s',
        handlers=[
            logging.FileHandler(path_result.joinpath("log.txt")),
            logging.StreamHandler()
        ]
    )

    for model_name in models:



        responses = {}

        # Create a folder result/model_name
        path_model_result = path_result.joinpath(model_name)
        path_fewshot_prompts = path_model_result.joinpath("fewshot_prompts")
        path_fewshot_prompts.mkdir(parents=True, exist_ok=True)
        path_model_output = path_model_result.joinpath("outputs")
        path_model_output.mkdir(parents=True, exist_ok=True)

        path_save_responses = path_model_output.joinpath("responses.json")
        if path_save_responses.exists():
            logging.info(f"Model {model_name} already ran, skipping")
            continue
        
        logging.info(f"Running model: {model_name}") 
        if mode == 'huggingface':
            index = name_to_index[model_name]
            chatbot.new_conversation(switch_to=True)
            chatbot.switch_llm(index)
        elif mode == 'local':
            logging.info(f"Current memory usage: {torch.cuda.memory_allocated() / (1024 ** 3):.2f} GB")
            model = AutoModelForCausalLM.from_pretrained(model_name, torch_dtype=torch.float16, device_map='auto')
            tokenizer = AutoTokenizer.from_pretrained(model_name, use_fast=True)
            model.eval()


        # Create fewshot prompts
        fewshot_prompts = create_fewshot_prompt(test_cases, fewshot_examples_path, fewshot_subset=[model_name])
        with open(path_fewshot_prompts.joinpath("prompts.json"), "w") as f:
            json.dump(fewshot_prompts, f, indent=4)


        for name, prompt in fewshot_prompts.items():
            logging.info(f"Running prompt for {name} with model {model_name}")
            if mode == 'huggingface':
                query_result = chatbot.chat(prompt)
                result_string = query_result.wait_until_done()
            elif mode == 'local':
                result_string, _ = run_prompt_local(prompt, tokenizer, model, settings, model_name)
            
            responses[name] = result_string

        logging.info(f"Saving results for model {model_name} to {path_save_responses}")
        with open(path_save_responses, "w") as f:
            json.dump(responses, f, indent=4)

        # clear the memory
        if mode == 'local':
            model.to('cpu')
            del model
            del tokenizer
            torch.cuda.empty_cache()
            torch.cuda.ipc_collect()
            gc.collect()
            logging.info("Memory cleared.")
    
if __name__ == "__main__":
    main()