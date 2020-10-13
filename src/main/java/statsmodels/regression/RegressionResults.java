/*
 * Copyright (c) 2019 Villu Ruusmann
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
package statsmodels.regression;

import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.jpmml.converter.Feature;
import org.jpmml.converter.Label;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.converter.Schema;
import org.jpmml.converter.regression.RegressionModelUtil;
import org.jpmml.python.PythonObject;
import org.jpmml.statsmodels.InterceptFeature;
import org.jpmml.statsmodels.StatsModelsEncoder;
import statsmodels.data.ModelData;

public class RegressionResults extends PythonObject {

	public RegressionResults(String module, String name){
		super(module, name);
	}

	public PMML encodePMML(StatsModelsEncoder encoder){
		RegressionModel regressionModel = getModel();

		ModelData modelData = regressionModel.getData();

		Schema schema = modelData.toSchema(encoder);

		Model model = encodeModel(schema);

		return encoder.encodePMML(model);
	}

	public Model encodeModel(Schema schema){
		List<Number> params = getParams();
		Number intercept = 0d;

		PMMLEncoder encoder = schema.getEncoder();
		Label label = schema.getLabel();
		List<? extends Feature> features = schema.getFeatures();

		if(features.size() > 0){
			Feature feature = features.get(0);

			if(feature instanceof InterceptFeature){
				InterceptFeature interceptFeature = (InterceptFeature)feature;

				params = new ArrayList<>(params);
				intercept = params.remove(0);

				features = new ArrayList<>(features);
				features.remove(0);

				schema = new Schema(encoder, label, features);
			}
		}

		return RegressionModelUtil.createRegression(features, params, intercept, org.dmg.pmml.regression.RegressionModel.NormalizationMethod.NONE, schema);
	}

	public RegressionModel getModel(){
		return get("model", RegressionModel.class);
	}

	public List<Number> getParams(){
		return getNumberArray("params");
	}
}