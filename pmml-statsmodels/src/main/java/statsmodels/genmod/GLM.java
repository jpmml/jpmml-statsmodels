/*
 * Copyright (c) 2023 Villu Ruusmann
 *
 * This file is part of JPMML-StatsModels
 *
 * JPMML-StatsModels is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-StatsModels is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-StatsModels.  If not, see <http://www.gnu.org/licenses/>.
 */
package statsmodels.genmod;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Iterables;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.OpType;
import org.dmg.pmml.general_regression.GeneralRegressionModel;
import org.jpmml.converter.CategoricalLabel;
import org.jpmml.converter.ContinuousLabel;
import org.jpmml.converter.ExceptionUtil;
import org.jpmml.converter.Feature;
import org.jpmml.converter.Label;
import org.jpmml.converter.ModelUtil;
import org.jpmml.converter.ScalarLabel;
import org.jpmml.converter.Schema;
import org.jpmml.converter.general_regression.GeneralRegressionModelUtil;
import org.jpmml.statsmodels.StatsModelsEncoder;
import org.jpmml.statsmodels.StatsModelsException;
import statsmodels.regression.RegressionModel;

public class GLM extends RegressionModel {

	public GLM(String module, String name){
		super(module, name);
	}

	@Override
	public Label encodeLabel(List<String> endogNames, StatsModelsEncoder encoder){
		Family family = getFamily();

		String endogName = Iterables.getOnlyElement(endogNames);

		String familyName = family.getPythonName();
		switch(familyName){
			case "Binomial":
				{
					DataField dataField = encoder.createDataField(endogName, OpType.CATEGORICAL, DataType.INTEGER, Arrays.asList(0, 1));

					return new CategoricalLabel(dataField);
				}
			case "Gaussian":
			case "Poisson":
				{
					DataField dataField = encoder.createDataField(endogName, OpType.CONTINUOUS, DataType.DOUBLE);

					return new ContinuousLabel(dataField);
				}
			default:
				throw new StatsModelsException("Distribution family " + ExceptionUtil.formatParameter(familyName) + " is not supported");
		}
	}

	@Override
	public org.dmg.pmml.general_regression.GeneralRegressionModel encodeModel(List<? extends Number> coefficients, Number intercept, Schema schema){
		Family family = getFamily();

		Link link = family.getLink();

		ScalarLabel scalarLabel = schema.requireScalarLabel();
		List<? extends Feature> features = schema.getFeatures();

		Object targetCategory;

		if(scalarLabel instanceof CategoricalLabel){
			CategoricalLabel categoricalLabel = (CategoricalLabel)scalarLabel;

			targetCategory = categoricalLabel.getValue(1);
		} else

		if(scalarLabel instanceof ContinuousLabel){
			ContinuousLabel continuousLabel = (ContinuousLabel)scalarLabel;

			targetCategory = null;
		} else

		{
			throw new IllegalArgumentException();
		}

		GeneralRegressionModel generalRegressionModel = new GeneralRegressionModel(GeneralRegressionModel.ModelType.GENERALIZED_LINEAR, (targetCategory != null ? MiningFunction.CLASSIFICATION : MiningFunction.REGRESSION), ModelUtil.createMiningSchema(scalarLabel), null, null, null)
			.setLinkFunction(parseLinkFunction(link))
			.setLinkParameter(parseLinkParameter(link))
			.setDistribution(parseDistribution(family));

		GeneralRegressionModelUtil.encodeRegressionTable(generalRegressionModel, features, coefficients, intercept, targetCategory);

		if(scalarLabel instanceof CategoricalLabel){
			CategoricalLabel categoricalLabel = (CategoricalLabel)scalarLabel;

			generalRegressionModel.setOutput(ModelUtil.createProbabilityOutput(DataType.DOUBLE, categoricalLabel));
		}

		return generalRegressionModel;
	}

	public Family getFamily(){
		return get("family", Family.class);
	}

	static
	private GeneralRegressionModel.Distribution parseDistribution(Family family){
		String familyName = family.getPythonName();

		switch(familyName){
			case "Binomial":
				return GeneralRegressionModel.Distribution.BINOMIAL;
			case "Gaussian":
				return GeneralRegressionModel.Distribution.NORMAL;
			case "Poisson":
				return GeneralRegressionModel.Distribution.POISSON;
			default:
				throw new StatsModelsException("Distribution family " + ExceptionUtil.formatParameter(familyName) + " is not supported");
		}
	}

	static
	private GeneralRegressionModel.LinkFunction parseLinkFunction(Link link){
		String linkName = link.getPythonName();

		// StatsModels 0.13.5
		switch(linkName){
			case "cloglog":
				return GeneralRegressionModel.LinkFunction.CLOGLOG;
			case "identity":
				return GeneralRegressionModel.LinkFunction.IDENTITY;
			case "inverse_power":
				return GeneralRegressionModel.LinkFunction.POWER;
			case "log":
				return GeneralRegressionModel.LinkFunction.LOG;
			case "logit":
				return GeneralRegressionModel.LinkFunction.LOGIT;
			case "loglog":
				return GeneralRegressionModel.LinkFunction.LOGLOG;
			case "probit":
				return GeneralRegressionModel.LinkFunction.PROBIT;
			case "sqrt":
				return GeneralRegressionModel.LinkFunction.POWER;
		}

		// StatsModels 0.14.0+
		switch(linkName){
			case "CLogLog":
				return GeneralRegressionModel.LinkFunction.CLOGLOG;
			case "Identity":
				return GeneralRegressionModel.LinkFunction.IDENTITY;
			case "InversePower":
				return GeneralRegressionModel.LinkFunction.POWER;
			case "Log":
				return GeneralRegressionModel.LinkFunction.LOG;
			case "LogC":
				return GeneralRegressionModel.LinkFunction.LOGC;
			case "Logit":
				return GeneralRegressionModel.LinkFunction.LOGIT;
			case "LogLog":
				return GeneralRegressionModel.LinkFunction.LOGLOG;
			case "Probit":
				return GeneralRegressionModel.LinkFunction.PROBIT;
			case "Sqrt":
				return GeneralRegressionModel.LinkFunction.POWER;
			default:
				throw new StatsModelsException("Link function " + ExceptionUtil.formatParameter(linkName) + " is not supported");
		}
	}

	static
	private Number parseLinkParameter(Link link){
		String linkName = link.getPythonName();

		// StatsModels 0.13.5
		switch(linkName){
			case "cloglog":
			case "identity":
				return null;
			case "inverse_power":
				return link.getPower();
			case "log":
			case "logit":
			case "loglog":
			case "probit":
				return null;
			case "sqrt":
				return link.getPower();
		}

		// StatsModels 0.14.0+
		switch(linkName){
			case "CLogLog":
			case "Identity":
				return null;
			case "InversePower":
				return link.getPower();
			case "Log":
			case "LogC":
			case "Logit":
			case "LogLog":
			case "Probit":
				return null;
			case "Sqrt":
				return link.getPower();
			default:
				throw new StatsModelsException("Link function " + ExceptionUtil.formatParameter(linkName) + " is not supported");
		}
	}
}
