from pandas import DataFrame
from statsmodels.api import OLS
from statsmodels.formula.api import ols

import pandas

def load_csv(name):
	return pandas.read_csv("csv/" + name + ".csv", na_values = ["N/A", "NA"])

def split_csv(df):
	columns = df.columns.tolist()
	return (df[columns[: -1]], df[columns[-1]])

def store_csv(df, name):
	df.to_csv("csv/" + name + ".csv", index = False)

def store_pkl(result, name):
	result.save("pkl/" + name + ".pkl", remove_data = True)

auto_df = load_csv("Auto")
print(auto_df.dtypes)

auto_X, auto_y = split_csv(auto_df)

auto_formula = "mpg ~ C(cylinders) + displacement + horsepower + weight + acceleration + C(model_year) + C(origin)"

def build_auto(model, name):
	result = model.fit()
	print(result.summary())

	store_pkl(result, name)

	mpg = DataFrame(result.predict(auto_X), columns = ["mpg"])
	store_csv(mpg, name)

build_auto(OLS(auto_y, auto_X), "OLSAuto")
build_auto(ols(formula = auto_formula, data = auto_df), "OLSFormulaAuto")