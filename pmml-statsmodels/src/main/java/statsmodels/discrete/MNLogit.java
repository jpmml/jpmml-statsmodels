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

import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml.DataType;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.regression.RegressionTable;
import org.jpmml.converter.CategoricalLabel;
import org.jpmml.converter.Feature;
import org.jpmml.converter.FortranMatrixUtil;
import org.jpmml.converter.Label;
import org.jpmml.converter.ModelUtil;
import org.jpmml.converter.Schema;
import org.jpmml.converter.regression.RegressionModelUtil;

public class MNLogit extends MultinomialModel {

	public MNLogit(String module, String name){
		super(module, name);
	}

	@Override
	public org.dmg.pmml.regression.RegressionModel encodeModel(List<? extends Number> params, Schema schema){
		Integer j = getJ();
		Integer k = getK();
		Integer kConstant = getKConstant();

		Label label = schema.getLabel();
		List<? extends Feature> features = schema.getFeatures();

		CategoricalLabel categoricalLabel = (CategoricalLabel)label;

		List<RegressionTable> regressionTables = new ArrayList<>();

		// The base case
		{
			RegressionTable regressionTable = new RegressionTable(0d)
				.setTargetCategory(categoricalLabel.getValue(0));

			regressionTables.add(regressionTable);
		}

		int rows = (categoricalLabel.size() - 1);
		int columns = k;

		// XXX
		int kIndex = 0;

		if(kConstant == 0){
			// Ignored
		} else

		if(kConstant == 1){
			features = dropInterceptFeature(features, kIndex);
		} else

		{
			throw new IllegalArgumentException();
		}

		// Rows one up from the base case
		for(int i = 0; i < rows; i++){
			List<? extends Number> coefficients = new ArrayList<>(FortranMatrixUtil.getRow(params, rows, columns, i));
			Number intercept = null;

			if(kConstant == 0){
				// Ignored
			} else

			if(kConstant == 1){
				intercept = coefficients.remove(kIndex);
			} else

			{
				throw new IllegalArgumentException();
			}

			RegressionTable regressionTable = RegressionModelUtil.createRegressionTable(features, coefficients, intercept)
				.setTargetCategory(categoricalLabel.getValue(i + 1));

			regressionTables.add(regressionTable);
		}

		org.dmg.pmml.regression.RegressionModel regressionModel = new org.dmg.pmml.regression.RegressionModel(MiningFunction.CLASSIFICATION, ModelUtil.createMiningSchema(categoricalLabel), regressionTables)
			.setNormalizationMethod(org.dmg.pmml.regression.RegressionModel.NormalizationMethod.SOFTMAX)
			.setOutput(ModelUtil.createProbabilityOutput(DataType.DOUBLE, categoricalLabel));

		return regressionModel;
	}
}