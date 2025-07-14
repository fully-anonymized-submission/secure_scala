# Leveraging the Scala type system for secure LLM-generated code
This repository provides the source code and the results for the RANLP submission titled *Leveraging the Scala type system for secure LLM-generated code*.

## Repository structure :books:
-- **src** \
&nbsp;&nbsp;&nbsp;&nbsp;|--- *general_tasks* \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- ```configs.yaml```: Configuration file for generation of code. \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- ```parse_code.py```: Script to parse the output of LLMs and extract the code. \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- ```helpers.py```: Helper functions to interact with the Huggingchat API and to run prompts through local LLMs. \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- ```generate_code.py```: The actual script to generate the code, based on the provided configuration file. \
&nbsp;&nbsp;&nbsp;&nbsp;|--- *stainless* \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- ```configs.py```: Configuration file for generating code with LLMs for the stainless framework. \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- ```parse_code.py```: Script to parse the output of LLMs and extract the code for stainless. \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- ```generate_code.py```: The actual script to generate the code for stainless, based on the provided configuration file. \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- ```evaluate.py```: Script to automatically run the generated code using stainless. \
-- **results** \
&nbsp;&nbsp;&nbsp;&nbsp;|--- *general_tasks* \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- ```injection```: Results for the code injection scenarios, for the baseline, robust and agentic settings. \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- ```input_constraings```: Results for the input constraints scenarios, for the baseline, robust and agentic settings. \
&nbsp;&nbsp;&nbsp;&nbsp;|--- *stainless* \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- ```generated_code```: The generated code in the experiments with the stainless framework. \
-- **scripts** \
&nbsp;&nbsp;&nbsp;&nbsp;|--- ```run_general_tasks.sh```: Script to run the experiments for the general tasks when using Slurm. \
&nbsp;&nbsp;&nbsp;&nbsp;|--- ```run_stainless.sh```: Script to run the experiments for the stainless framework when using Slurm. \
-- **data** \
&nbsp;&nbsp;&nbsp;&nbsp;|--- ```general_tasks```: Contains the test cases used for the general tasks. \
&nbsp;&nbsp;&nbsp;&nbsp;|--- ```stainless```: Contains the test cases used for the stainless framework. \


## Setting up the environment :mag:
To set up the environment, you can use the provided `requirements.txt` file, which includes the minimal dependencies required to run the code. If using conda, you can create a new environment and install the dependencies as follows:

```bash
conda create -n secure_scala python=3.11.7
conda activate secure_scala
pip install -r requirements.txt
```

Depending on the models that you want to use, you should pay attention that you have the right hardware available. 

## Running experiments :memo:
This repository provides scripts to run experiments on LLM-generated Scala code. There are two different tasks: 

* Generating code for the formal verification framework stainless, investigating the proficiency of LLMs in this domain.
* Generating code for general tasks, targeting vulnerabilities related to input constraints and code injection.

You can run the code using the following steps:

1. **Set up the environment**: Follow the instructions above to set up the environment.
2. **Determine your test cases**: The test cases that are used in the paper are located in the `data` directory. You can modify the test cases as needed for your use case.
3. **Set up your configuration file**: The configuration files for the experiments are located in the `src/general_tasks/configs` and `src/stainless/configs` directories. You can modify these files to change the settings for the experiments. 
4. **Run the experiments**: We provide scripts for running the experiments using Slurm in the folder `scripts`. If you want to directly run the code without Slurm, you can directly run the scripts in the `src` directory. For example, to run the general tasks, you can use: ```python src/general_tasks/generate_code.py your_config_file.yaml```

For the Stainless framework, we also provide the option to use models through Huggingchat. For this, you should add your Huggingface password in a `.env` file in the root directory of the repository, as `HUGGINGFACE_PASSWORD=your_password`. Then, in the configuration file, set the `mode` to `huggingface`. 

## Evaluation of the generated code
For the evaluation of the generated code related to Stainless, we provide a script to automatically run the generated code (`src/stainless/evaluate.py`). For the general tasks, this is more complex, as we do not want to restrict the LLM too much in the code generation. Therefore, we opted for manual evaluation of the generated code. However, we do check whether the code compiles, and you can specify in the configuration file how many attempts teh LLM should have to generate compilable code. The results corresponding to the experiments in the paper are located in the `results` directory.



