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
import org.jpmml.converter.Feature;
import org.jpmml.converter.Label;
import org.jpmml.converter.ModelUtil;
import org.jpmml.converter.Schema;
import org.jpmml.converter.general_regression.GeneralRegressionModelUtil;
import org.jpmml.statsmodels.StatsModelsEncoder;
import statsmodels.regression.RegressionModel;

public class GLM extends RegressionModel {

	public GLM(String module, String name){
		super(module, name);
	}

	@Override
	public Label encodeLabel(List<String> yNames, StatsModelsEncoder encoder){
		Family family = getFamily();

		String yName = Iterables.getOnlyElement(yNames);

		String familyName = family.getPythonName();
		switch(familyName){
			case "Binomial":
				{
					DataField dataField = encoder.createDataField(yName, OpType.CATEGORICAL, DataType.INTEGER, Arrays.asList(0, 1));

					return new CategoricalLabel(dataField);
				}
			case "Gaussian":
			case "Poisson":
				{
					DataField dataField = encoder.createDataField(yName, OpType.CONTINUOUS, DataType.DOUBLE);

					return new ContinuousLabel(dataField);
				}
			default:
				throw new IllegalArgumentException(familyName);
		}
	}

	@Override
	public org.dmg.pmml.general_regression.GeneralRegressionModel encodeModel(List<? extends Number> coefficients, Number intercept, Schema schema){
		Family family = getFamily();

		Link link = family.getLink();

		Label label = schema.getLabel();
		List<? extends Feature> features = schema.getFeatures();

		Object targetCategory;

		String familyName = family.getPythonName();
		switch(familyName){
			case "Binomial":
				{
					CategoricalLabel categoricalLabel = (CategoricalLabel)label;

					targetCategory = categoricalLabel.getValue(1);
				}
				break;
			case "Gaussian":
			case "Poisson":
				{
					ContinuousLabel continuousLabel = (ContinuousLabel)label;

					targetCategory = null;
				}
				break;
			default:
				throw new IllegalArgumentException(familyName);
		}

		GeneralRegressionModel generalRegressionModel = new GeneralRegressionModel(GeneralRegressionModel.ModelType.GENERALIZED_LINEAR, (targetCategory != null ? MiningFunction.CLASSIFICATION : MiningFunction.REGRESSION), ModelUtil.createMiningSchema(label), null, null, null)
				.setDistribution(parseDistribution(family))
				.setLinkFunction(parseLinkFunction(link))
				.setLinkParameter(parseLinkParameter(link));

		GeneralRegressionModelUtil.encodeRegressionTable(generalRegressionModel, features, coefficients, intercept, targetCategory);

		switch(familyName){
			case "Binomial":
				{
					CategoricalLabel categoricalLabel = (CategoricalLabel)label;

					generalRegressionModel.setOutput(ModelUtil.createProbabilityOutput(DataType.DOUBLE, categoricalLabel));
				}
				break;
			default:
				break;
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
				throw new IllegalArgumentException(familyName);
		}
	}

	static
	private GeneralRegressionModel.LinkFunction parseLinkFunction(Link link){
		String linkName = link.getPythonName();

		switch(linkName){
			case "identity":
				return GeneralRegressionModel.LinkFunction.IDENTITY;
			case "Log":
				return GeneralRegressionModel.LinkFunction.LOG;
			case "Logit":
				return GeneralRegressionModel.LinkFunction.LOGIT;
			default:
				throw new IllegalArgumentException(linkName);
		}
	}

	static
	private Number parseLinkParameter(Link link){
		String linkName = link.getPythonName();

		switch(linkName){
			case "identity":
			case "Log":
			case "Logit":
				return null;
			default:
				throw new IllegalArgumentException(linkName);
		}
	}
}