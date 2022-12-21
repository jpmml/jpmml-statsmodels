from pandas import DataFrame
from statsmodels.api import MNLogit, OLS, WLS
from statsmodels.formula.api import logit, ols, poisson, wls

import pandas

def load_csv(name):
	return pandas.read_csv("csv/" + name + ".csv", na_values = ["N/A", "NA"])

def split_csv(df):
	columns = df.columns.tolist()
	return (df[columns[: -1]], df[columns[-1]])

def store_csv(df, name):
	df.to_csv("csv/" + name + ".csv", index = False)

def store_pkl(results, name):
	results.save("pkl/" + name + ".pkl", remove_data = True)

audit_df = load_csv("Audit")
print(audit_df.dtypes)

audit_X, audit_y = split_csv(audit_df)

audit_formula = "Adjusted ~ Age + C(Employment) + C(Education) + C(Marital) + C(Occupation) + Income + Hours"

def build_audit(model, name):
	results = model.fit()
	print(results.summary())

	store_pkl(results, name)

	adjusted_proba = DataFrame(results.predict(audit_X), columns = ["probability(1)"])
	adjusted_proba["probability(0)"] = 1 - adjusted_proba["probability(1)"]
	store_csv(adjusted_proba, name)

build_audit(logit(formula = audit_formula, data = audit_df), "LogitFormulaAudit")

iris_df = load_csv("Iris")
print(iris_df.dtypes)

iris_X, iris_y = split_csv(iris_df)

def build_iris(model, name):
	results = model.fit()
	print(results.summary())

	store_pkl(results, name)

	species_proba = results.predict(iris_X)
	species_proba.columns = ["probability(setosa)", "probability(versicolor)", "probability(virginica)"]
	store_csv(species_proba, name)

build_iris(MNLogit(iris_y, iris_X), "MNLogitIris")

auto_df = load_csv("Auto")
print(auto_df.dtypes)

auto_X, auto_y = split_csv(auto_df)

auto_formula = "mpg ~ C(cylinders) + displacement + horsepower + weight + acceleration + C(model_year) + C(origin)"

def build_auto(model, name):
	results = model.fit()
	print(results.summary())

	store_pkl(results, name)

	mpg = DataFrame(results.predict(auto_X), columns = ["mpg"])
	store_csv(mpg, name)

build_auto(OLS(auto_y, auto_X), "OLSAuto")
build_auto(WLS(auto_y, auto_X), "WLSAuto")

build_auto(ols(formula = auto_formula, data = auto_df), "OLSFormulaAuto")
build_auto(wls(formula = auto_formula, data = auto_df), "WLSFormulaAuto")

visit_df = load_csv("Visit")
print(visit_df.dtypes)

visit_X, visit_y = split_csv(visit_df)

visit_formula = "docvis ~ C(edlevel) + C(outwork) + C(female) + C(married) + C(kids) + hhninc + educ + C(self)"

def build_visit(model, name):
	results = model.fit()
	print(results.summary())

	store_pkl(results, name)

	docvis = DataFrame(results.predict(visit_X), columns = ["docvis"])
	store_csv(docvis, name)

build_visit(poisson(formula = visit_formula, data = visit_df), "PoissonFormulaVisit")