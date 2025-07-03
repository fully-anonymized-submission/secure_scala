import json
import subprocess
from pathlib import Path
import re
import os
from generate_code import start_chatbot, get_indices_huggingchat
from dotenv import load_dotenv
import yaml

def load_response(path):
    # load the json dictionary with responses
    with open(path, "r") as f:
        responses = json.load(f)
    return responses

def evaluate(responses, base_path):
    results = {}
    for key, value in responses.items():

        subfolder = base_path.joinpath(key)
        # Create the subfolder if it doesn't exist
        if not subfolder.exists():
            subfolder.mkdir(parents=True, exist_ok=True)

        # if generated_code.scala exists, don't rewrite it
        if not subfolder.joinpath("generated_code.scala").exists():
            match = re.search(r"```scala\s*(.*?)```", value, re.DOTALL)
            if match:
                value = match.group(1).strip()
            else:
                print(f"No Scala code found in response for key: {key}")
                continue
            # First, we will write the string to a file
            with open(subfolder.joinpath("generated_code.scala"), "w") as f:
                f.write(value)
        # Now we will run the compiler, use stainless to compile the file
        # We will capture the output of the compiler
        result = subprocess.run(["stainless", subfolder.joinpath("generated_code.scala")], capture_output=True)
        # We will save the output to a file
        with open(subfolder.joinpath(f"stdout.txt"), "w") as f:
            f.write(result.stdout.decode())
        # We will save the error to a file
        with open(subfolder.joinpath(f"stderr.txt"), "w") as f:
            f.write(result.stderr.decode())
        # We will save the return code to a file
        with open(subfolder.joinpath(f"return_code.txt"), "w") as f:
            f.write(str(result.returncode))
        # We will save the result to a dictionary
        results[key] = {
            "stdout": result.stdout.decode(),
            "stderr": result.stderr.decode(),
            "return_code": result.returncode
        }
    # We will save the results to a json file
    with open(base_path.joinpath("results.json"), "w") as f:
        json.dump(results, f, indent=4)


def evaluate_models(models, base_results_path):
    for model in models:
        if base_results_path.joinpath(model).joinpath("results.json").exists():
            print(f"Model {model} already evaluated")
            continue
        else:
            print(f"Evaluating model {model}")
            path_input = base_results_path.joinpath(f"{model}/outputs/responses.json")
            responses = load_response(path_input)
            evaluate(responses, base_results_path.joinpath(model))


def main():
    root = Path.cwd()
    with open(root.joinpath("src/stainless/configs.yaml"), "r") as f:
        configs = yaml.safe_load(f)
    huggingface_email = configs['huggingface_email']
    cookie_path_dir = str(root.joinpath("data/cookies/")) 
    base_path_results = root.joinpath(configs['path_save_results'])
    mode = configs['mode']
    if mode == 'huggingface':
        load_dotenv()
        PASSWD = os.getenv("HUGGINGFACE_PASSWORD")
        chatbot = start_chatbot(huggingface_email, PASSWD, cookie_path_dir)
        name_to_index = get_indices_huggingchat(chatbot)
        all_models = list(name_to_index.keys())
    
    elif mode == 'local':
        all_models = configs['models']

    evaluate_models(all_models, base_path_results)

if __name__ == "__main__":
    main()

