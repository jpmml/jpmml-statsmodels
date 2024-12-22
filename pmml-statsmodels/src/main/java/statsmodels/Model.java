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
package statsmodels;

import org.jpmml.converter.Schema;
import org.jpmml.python.PythonObject;
import org.jpmml.statsmodels.StatsModelsEncoder;
import statsmodels.data.ModelData;

abstract
public class Model extends PythonObject {

	public Model(String module, String name){
		super(module, name);
	}

	abstract
	public Schema encodeSchema(StatsModelsEncoder encoder);

	abstract
	public org.dmg.pmml.Model encodeModel(Results results, Schema schema);

	public ModelData getData(){
		return get("data", ModelData.class);
	}

	public Integer getKConstant(){
		return getInteger("k_constant");
	}

	public Integer getKExtra(){
		return getInteger("k_extra");
	}

	public Integer getKVars(){
		return getInteger("k_vars");
	}
}