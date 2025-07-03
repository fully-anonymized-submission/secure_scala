import json
import subprocess
from pathlib import Path
import re
import os
import logging
import yaml
def load_response(path):
    # load the json dictionary with responses
    with open(path, "r") as f:
        responses = json.load(f)
    return responses

def parse_output(response):
    # Take the first match beginning with ```scala and ending with ```
    match = re.search(r"```scala\s*(.*?)```", response, re.DOTALL)
    if match:
        value = match.group(1).strip()
        return value
    else:
        # We want to look for "object GeneratedFunctions {"
        # If it is there, we start counting the number of { and } and we want to return the code between the first { and the last }
        target = "object GeneratedFunctions {"
        start = response.find(target) 
        i = start
        if start == -1:
            return None
        opening_bracket_count = 0
        while i < len(response):
            if response[i] == '{':
                opening_bracket_count += 1
            elif response[i] == '}':
                opening_bracket_count -= 1
                if opening_bracket_count == 0:
                    return response[start:i+1]
            i += 1
    # If we don't find any match, we return None
    return None

def parse_output_responses(responses, path_input, logger):

    for key, value in responses.items():
        subfolder = path_input.joinpath(key)
        # Create the subfolder if it doesn't exist
        if not subfolder.exists():
            subfolder.mkdir(parents=True, exist_ok=True)
        value = parse_output(value)
        if value is None:
            logger.warning(f"No Scala code found in response for key: {key}")
            continue
        # First, we will write the string to a file
        with open(subfolder.joinpath("generated_code.scala"), "w") as f:
            f.write(value)


def evaluate_models(models, base_results_path, logger):
    for model in models:
        if not base_results_path.joinpath(model).exists():
            continue
        logger.info(f"Parsing output for model {model}")
        path_input = base_results_path.joinpath(f"{model}/outputs/responses.json")
        responses = load_response(path_input)
        parse_output_responses(responses, base_results_path.joinpath(model), logger)


def main():
    root = Path.cwd()
    root = Path.cwd()
    with open(root.joinpath("src/stainless/configs.yaml"), "r") as f:
        configs = yaml.safe_load(f)
    path_result = root.joinpath(configs['path_save_results'])
    models = configs['models']
    path_log = path_result.joinpath("log_parsing.txt")
    
    
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(levelname)s - %(message)s',
        handlers=[
            logging.FileHandler(path_result.joinpath("log_parsing.txt")),
            logging.StreamHandler()
        ]
    )

    logger = logging.getLogger(__name__)
    logger.info("Starting the script")

    evaluate_models(models, path_result, logger)
    


if __name__ == "__main__":
    main()

