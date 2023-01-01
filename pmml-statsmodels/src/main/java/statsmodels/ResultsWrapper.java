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
package statsmodels;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.jpmml.converter.Schema;
import org.jpmml.python.PythonObject;
import org.jpmml.statsmodels.StatsModelsEncoder;

public class ResultsWrapper extends PythonObject {

	public ResultsWrapper(String module, String name){
		super(module, name);
	}

	public PMML encodePMML(StatsModelsEncoder encoder){
		Results results = getResults();

		return results.encodePMML(encoder);
	}

	public Model encodeModel(Schema schema){
		Results results = getResults();

		return results.encodeModel(schema);
	}

	public Results getResults(){
		return get("_results", Results.class);
	}
}