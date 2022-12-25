/*
 * Copyright (c) 2022 Villu Ruusmann
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
package statsmodels.discrete;

import java.util.List;

import org.jpmml.converter.Feature;
import org.jpmml.converter.Schema;
import org.jpmml.converter.regression.RegressionModelUtil;

public class LogitResults extends DiscreteResults {

	public LogitResults(String module, String name){
		super(module, name);
	}

	@Override
	public org.dmg.pmml.regression.RegressionModel createRegressionModel(List<? extends Feature> features, List<? extends Number> coefficients, Number intercept, Schema schema){
		return RegressionModelUtil.createBinaryLogisticClassification(features, coefficients, intercept, org.dmg.pmml.regression.RegressionModel.NormalizationMethod.LOGIT, true, schema);
	}
}