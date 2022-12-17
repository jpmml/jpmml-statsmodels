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
import java.util.Map;

import com.google.common.collect.Iterables;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.Model;
import org.dmg.pmml.OpType;
import org.dmg.pmml.regression.RegressionTable;
import org.jpmml.converter.CategoricalLabel;
import org.jpmml.converter.Feature;
import org.jpmml.converter.FortranMatrixUtil;
import org.jpmml.converter.Label;
import org.jpmml.converter.ModelUtil;
import org.jpmml.converter.Schema;
import org.jpmml.converter.TypeUtil;
import org.jpmml.converter.regression.RegressionModelUtil;
import org.jpmml.statsmodels.StatsModelsEncoder;

public class MultinomialResults extends DiscreteResults {

	public MultinomialResults(String module, String name){
		super(module, name);
	}

	@Override
	public Label encodeLabel(List<String> ynames, StatsModelsEncoder encoder){
		MultinomialModel model = getModel();

		String yname = Iterables.getOnlyElement(ynames);

		Map<Integer, ?> ynamesMap = model.getYNamesMap();

		List<Object> categories = new ArrayList<>();

		for(int i = 0; i < ynamesMap.size(); i++){
			Object category = ynamesMap.get(i);

			if(category == null){
				throw new IllegalArgumentException();
			}

			categories.add(category);
		}

		DataType dataType = TypeUtil.getDataType(categories, DataType.STRING);

		DataField dataField = encoder.createDataField(yname, OpType.CATEGORICAL, dataType, categories);

		return new CategoricalLabel(dataField);
	}

	@Override
	public Model encodeModel(Schema schema){
		Integer j = getJ();
		Integer k = getK();

		List<Number> params = getParams();

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

		// Rows one up from the base case
		for(int i = 0; i < (categoricalLabel.size() - 1); i++){
			List<Number> coefficients = FortranMatrixUtil.getRow(params, (categoricalLabel.size() - 1), k, i);

			RegressionTable regressionTable = RegressionModelUtil.createRegressionTable(features, coefficients, null)
				.setTargetCategory(categoricalLabel.getValue(i + 1));

			regressionTables.add(regressionTable);
		}

		org.dmg.pmml.regression.RegressionModel regressionModel = new org.dmg.pmml.regression.RegressionModel(MiningFunction.CLASSIFICATION, ModelUtil.createMiningSchema(categoricalLabel), regressionTables)
			.setNormalizationMethod(org.dmg.pmml.regression.RegressionModel.NormalizationMethod.SOFTMAX)
			.setOutput(ModelUtil.createProbabilityOutput(DataType.DOUBLE, categoricalLabel));

		return regressionModel;
	}

	@Override
	public MultinomialModel getModel(){
		return get("model", MultinomialModel.class);
	}

	public Integer getJ(){
		return getInteger("J");
	}

	public Integer getK(){
		return getInteger("K");
	}
}