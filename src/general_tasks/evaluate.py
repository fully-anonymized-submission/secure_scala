import json
import subprocess
from pathlib import Path
import os
from dotenv import load_dotenv
load_dotenv()
PASSWD = os.getenv("HUGGINGFACE_PASSWORD")

def evaluate_model(model, base_path_results, methods):
    # the results is located at base_path_results/model/
    path_specific_model = base_path_results.joinpath(model)

    # iterate over the subfolders
    for subfolder in os.listdir(path_specific_model):
        if subfolder not in methods:
            continue
        
        if subfolder == "outputs" or subfolder == "evaluations":
            continue
        scala_filename = subfolder + ".scala"
        print(f"Evaluating model {model} in subfolder {subfolder}")
        results_path = path_specific_model.joinpath(subfolder).joinpath("evaluation.txt")

        output_compiled = subprocess.run([f"scalac", "-d", path_specific_model.joinpath(subfolder), Path.cwd().joinpath('src/general').joinpath('evaluation').joinpath(subfolder).joinpath(scala_filename), path_specific_model.joinpath(subfolder).joinpath('generated_code.scala')],
                       capture_output=True)
        # now run the code
        if output_compiled.returncode != 0:
            print(f"Compilation failed for model {model} in subfolder {subfolder}, error: {output_compiled.stderr.decode()}")
            
        
        else:
            print(f"Running the command: scala -cp {path_specific_model.joinpath(subfolder)} {subfolder} {model} {results_path}")
            # run the code
            output = subprocess.run(["scala", "-cp", path_specific_model.joinpath(subfolder), subfolder, model, results_path],
                                    capture_output=True)
            
            

            # We will save the output to a json file
            results = {
                "stdout": output.stdout.decode(),
                "stderr": output.stderr.decode(),
                "return_code": output.returncode
            }
            with open(path_specific_model.joinpath(subfolder).joinpath("results.json"), "w") as f:
                json.dump(results, f, indent=4)

def combine_evaluation_reports(base_path_results, models, methods, path_to_save):
    combined_results = {}
    for model in models:
        combined_results[model] = {}
        results_model = base_path_results.joinpath(model)
        for method in methods:
            combined_results[model][method] = {}
            results_method = results_model.joinpath(method)

            # if evaluation.txt does not exist, skip
            if not results_method.joinpath('evaluation.txt').exists():
                print(f"Evaluation file for model {model} and method {method} does not exist, skipping")
                continue
            # find evaluation.txt
            with open(results_method.joinpath('evaluation.txt'), 'r') as f:
                lines = f.readlines()
                # score is formatted as "score: <value>"
                for line in lines:
                    name = line.split(':')[0].strip()
                    value = line.split(':')[1].strip()
                    combined_results[model][method][name] = value
    # save combined results to json
    with open(path_to_save, 'w') as f:
        json.dump(combined_results, f, indent=4)
    print(f"Combined results saved to {path_to_save}")
    return combined_results

def main():
    root = Path.cwd()
    base_path_results = root.joinpath("results_robust")

    models = ["Qwen/Qwen2.5-Coder-32B-Instruct", "deepseek-ai/deepseek-coder-33b-instruct", "codellama/CodeLlama-70b-Instruct-hf", "Qwen/Qwen3-8B", "meta-llama/Llama-3.1-8B-Instruct", "deepseek-ai/DeepSeek-R1-Distill-Qwen-7B"]
    #models = ["deepseek-ai/deepseek-coder-33b-instruct"]
    methods = ['average_age', 'convolution', 'fibonacci', 'matrix_multiplication']
    #methods = ['convolution']
    for model in models:
        evaluate_model(model, base_path_results, methods)

    # combine the evaluation reports
    path_to_save = base_path_results.joinpath("combined_results.json")
    combine_evaluation_reports(base_path_results, models, methods, path_to_save)



if __name__ == "__main__":
    main()

