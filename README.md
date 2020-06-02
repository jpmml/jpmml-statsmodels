JPMML-StatsModels
=================

Java library and command-line application for converting [StatsModels](https://www.statsmodels.org/) models to PMML.

# Features #

* Supported model types:
  * Ordinary Least Squares (OLS)
  * Weighted Least Squares (WLS)
* Production quality:
  * Complete test coverage.
  * Fully compliant with the [JPMML-Evaluator](https://github.com/jpmml/jpmml-evaluator) library.

# Installation #

Enter the project root directory and build using [Apache Maven](https://maven.apache.org/):
```
mvn clean install
```

The build produces an executable uber-JAR file `target/jpmml-statsmodels-executable-1.0-SNAPSHOT.jar`.

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
java -jar target/jpmml-statsmodels-executable-1.0-SNAPSHOT.jar --pkl-input model.pkl --pmml-output model.pmml
```

Getting help:
```
java -jar target/jpmml-statsmodels-executable-1.0-SNAPSHOT.jar --help
```

# License #

JPMML-StatsModels is licensed under the terms and conditions of the [GNU Affero General Public License, Version 3.0](https://www.gnu.org/licenses/agpl-3.0.html).

If you would like to use JPMML-StatsModels in a proprietary software project, then it is possible to enter into a licensing agreement which makes JPMML-StatsModels available under the terms and conditions of the [BSD 3-Clause License](https://opensource.org/licenses/BSD-3-Clause) instead.

# Additional information #

JPMML-StatsModels is developed and maintained by Openscoring Ltd, Estonia.

Interested in using [Java PMML API](https://github.com/jpmml) software in your company? Please contact [info@openscoring.io](mailto:info@openscoring.io)