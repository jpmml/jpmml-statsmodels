JPMML-StatsModels [![Build Status](https://github.com/jpmml/jpmml-statsmodels/workflows/maven/badge.svg)](https://github.com/jpmml/jpmml-statsmodels/actions?query=workflow%3A%22maven%22)
=================

Java library and command-line application for converting [StatsModels](https://www.statsmodels.org/) models to PMML.

# Features #

* Supported model types:
  * Linear Regression:
    * [Ordinary Least Squares](https://www.statsmodels.org/dev/generated/statsmodels.regression.linear_model.OLS.html)
    * [Weighted Least Squares](https://www.statsmodels.org/dev/generated/statsmodels.regression.linear_model.WLS.html)
  * Generalized Linear Regression:
    * [Generalized Linear Models](https://www.statsmodels.org/stable/generated/statsmodels.genmod.generalized_linear_model.GLM.html)
  * Regression with Discrete Dependent Variable:
    * [Logit](https://www.statsmodels.org/dev/generated/statsmodels.discrete.discrete_model.Logit.html)
    * [Multinomial Logit](https://www.statsmodels.org/dev/generated/statsmodels.discrete.discrete_model.MNLogit.html)
    * [Poisson](https://www.statsmodels.org/dev/generated/statsmodels.discrete.discrete_model.Poisson.html)
* Production quality:
  * Complete test coverage.
  * Fully compliant with the [JPMML-Evaluator](https://github.com/jpmml/jpmml-evaluator) library.

# Installation #

Enter the project root directory and build using [Apache Maven](https://maven.apache.org/):
```
mvn clean install
```

The build produces a library JAR file `pmml-statsmodels/target/pmml-statsmodels-1.0-SNAPSHOT.jar`, and an executable uber-JAR file `pmml-statsmodels-example/target/pmml-statsmodels-example-executable-1.0-SNAPSHOT.jar`.

# Usage #

A typical workflow can be summarized as follows:

1. Use Python to fit a model.
2. Save the model fitting results in `pickle` data format to a file in a local filesystem.
3. Use the JPMML-StatsModels command-line converter application to turn the pickle file to a PMML file.

### The Python side of operations

Loading data to a `pandas.DataFrame` object:

```python
import pandas

auto_df = pandas.read_csv("Auto.csv")
```

Fitting a regression model using an R-style formula:

```python
from statsmodels.formula.api import ols

model = ols(formula = "mpg ~ C(cylinders) + displacement + horsepower + weight + acceleration + C(model_year) + C(origin)", data = auto_df)
results = model.fit()
print(results.summary())
```

Storing the fitted `RegressionResults(Wrapper)` object in `pickle` data format:

```python
results.save("model.pkl", remove_data = True)
```

### The JPMML-StatsModels side of operations

Converting the model fitting results pickle file `model.pkl` to a PMML file `model.pmml`:
```
java -jar pmml-statsmodels-example/target/pmml-statsmodels-example-executable-1.0-SNAPSHOT.jar --pkl-input model.pkl --pmml-output model.pmml
```

Getting help:
```
java -jar pmml-statsmodels-example/target/pmml-statsmodels-example-executable-1.0-SNAPSHOT.jar --help
```

# License #

JPMML-StatsModels is licensed under the terms and conditions of the [GNU Affero General Public License, Version 3.0](https://www.gnu.org/licenses/agpl-3.0.html).

If you would like to use JPMML-StatsModels in a proprietary software project, then it is possible to enter into a licensing agreement which makes JPMML-StatsModels available under the terms and conditions of the [BSD 3-Clause License](https://opensource.org/licenses/BSD-3-Clause) instead.

# Additional information #

JPMML-StatsModels is developed and maintained by Openscoring Ltd, Estonia.

Interested in using [Java PMML API](https://github.com/jpmml) software in your company? Please contact [info@openscoring.io](mailto:info@openscoring.io)