from hugchat.login import Login
from hugchat import hugchat
import torch

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

def run_prompt(text, tokenizer, model, settings, model_name, logger=None):
    """
    Tokenizes the input text using the provided tokenizer.
    
    Args:
        text (str): The input text to tokenize.
        tokenizer: The tokenizer to use for tokenization.
    
    Returns:
        list: A list of tokenized words.
    """
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
            return run_prompt(prompt, tokenizer, model, settings, model_name)

    # Clear the memory
    del inputs
    del attention_mask
    del outputs
    return output_str_new_tokens, full_output_str