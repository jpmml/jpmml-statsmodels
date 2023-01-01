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
import org.dmg.pmml.OpType;
import org.jpmml.converter.CategoricalLabel;
import org.jpmml.converter.ContinuousLabel;
import org.jpmml.converter.Label;
import org.jpmml.converter.Schema;
import org.jpmml.converter.regression.RegressionModelUtil;
import org.jpmml.statsmodels.StatsModelsEncoder;
import statsmodels.genmod.families.Binomial;
import statsmodels.genmod.families.Gaussian;
import statsmodels.genmod.families.Poisson;
import statsmodels.regression.RegressionModel;

public class GLM extends RegressionModel {

	public GLM(String module, String name){
		super(module, name);
	}

	@Override
	public Label encodeLabel(List<String> yNames, StatsModelsEncoder encoder){
		Family family = getFamily();

		String yName = Iterables.getOnlyElement(yNames);

		if(family instanceof Binomial){
			DataField dataField = encoder.createDataField(yName, OpType.CATEGORICAL, DataType.INTEGER, Arrays.asList(0, 1));

			return new CategoricalLabel(dataField);
		} else

		if((family instanceof Gaussian) || (family instanceof Poisson)){
			DataField dataField = encoder.createDataField(yName, OpType.CONTINUOUS, DataType.DOUBLE);

			return new ContinuousLabel(dataField);
		} else

		{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public org.dmg.pmml.regression.RegressionModel encodeModel(List<? extends Number> coefficients, Number intercept, Schema schema){
		Family family = getFamily();

		if(family instanceof Binomial){
			return RegressionModelUtil.createBinaryLogisticClassification(schema.getFeatures(), coefficients, intercept, org.dmg.pmml.regression.RegressionModel.NormalizationMethod.LOGIT, true, schema);
		} else

		if(family instanceof Gaussian){
			return RegressionModelUtil.createRegression(schema.getFeatures(), coefficients, intercept, org.dmg.pmml.regression.RegressionModel.NormalizationMethod.NONE, schema);
		} else

		if(family instanceof Poisson){
			return RegressionModelUtil.createRegression(schema.getFeatures(), coefficients, intercept, org.dmg.pmml.regression.RegressionModel.NormalizationMethod.EXP, schema);
		} else

		{
			throw new IllegalArgumentException();
		}
	}

	public Family getFamily(){
		return get("family", Family.class);
	}
}