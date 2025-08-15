
import torch
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

def create_planning_prompts(test_cases):
    format_prompt = """
    You are a Scala code generator. Your task is to compose a plan for how you will implement the given task. I will first give you several examples, and will then give you a task to compose a plan for. Only return the plan, do not return any code.
    
    Instruction: 
    Write a function to filter out all strings shorter than a given length from a list.
    Plan:
    1. Loop through the list of strings.
    2. Check if each string's length is greater than or equal to the given length.
    3. Keep only those strings that satisfy the condition.
    4. Return the resulting list.

    Instruction:
    Write a Scala function that reads a text file and returns the number of lines it contains.
    Plan:
    1. Check if the file exists and is readable.
    2. Open the file using a buffered reader.
    3. Count the number of lines as you read them.
    4. Close the file safely after reading.
    5. Return the count.

    Instruction:
    Write a Scala function to safely divide two numbers, returning an Option[Double] that is None if the denominator is zero.
    Plan:
    1. Check if the denominator is zero.
    2. If zero, return None.
    3. Otherwise, perform the division.
    4. Return the result wrapped in Some.

    Instruction:
    {prompt}
    Plan:
    """
    prompts = {}
    for name, test_case in test_cases.items():
        formatted_prompt = format_prompt.format(prompt=test_case)
        prompts[name] = formatted_prompt.strip()
    return prompts

def create_implementation_prompt(prompt, plan):
    return f"""
    You are a Scala code generator. You will be given a task, and a plan for how to implement it. Your task is to follow the plan and write the Scala code to implement the task. You should pay attention to the safety and security, and you should only return the code. The code should start with ```scala and end with ```.

    Task: {prompt}
    Plan: {plan}
    Answer:
    """.strip()