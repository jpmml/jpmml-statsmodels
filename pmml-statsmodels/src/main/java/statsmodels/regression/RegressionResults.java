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

import java.util.List;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.jpmml.converter.Schema;
import org.jpmml.python.PythonObject;
import org.jpmml.statsmodels.StatsModelsEncoder;

public class RegressionResults extends PythonObject {

	public RegressionResults(String module, String name){
		super(module, name);
	}

	public PMML encodePMML(StatsModelsEncoder encoder){
		RegressionModel regressionModel = getModel();
		List<Number> params = getParams();

		Schema schema = regressionModel.encodeSchema(encoder);

		Model model = regressionModel.encodeModel(params, schema);

		return encoder.encodePMML(model);
	}

	public RegressionModel getModel(){
		return get("model", RegressionModel.class);
	}

	public List<Number> getParams(){
		return getNumberArray("params");
	}
}