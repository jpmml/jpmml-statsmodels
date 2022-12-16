from pandas import DataFrame
from statsmodels.api import Logit, OLS, WLS
from statsmodels.formula.api import logit, ols, wls

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

audit_df = load_csv("Audit")
print(audit_df.dtypes)

audit_X, audit_y = split_csv(audit_df)

audit_formula = "Adjusted ~ Age + C(Employment) + C(Education) + C(Marital) + C(Occupation) + Income + Hours"

def build_audit(model, name):
	result = model.fit()
	print(result.summary())

	store_pkl(result, name)

	adjusted_proba = DataFrame(result.predict(audit_X), columns = ["probability(1)"])
	adjusted_proba["probability(0)"] = 1 - adjusted_proba["probability(1)"]
	store_csv(adjusted_proba, name)

build_audit(logit(formula = audit_formula, data = audit_df), "LogitFormulaAudit")

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
build_auto(WLS(auto_y, auto_X), "WLSAuto")
build_auto(wls(formula = auto_formula, data = auto_df), "WLSFormulaAuto")
