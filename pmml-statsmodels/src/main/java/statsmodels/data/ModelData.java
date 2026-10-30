/*
 * Copyright (c) 2020 Villu Ruusmann
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
package statsmodels.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jpmml.python.PythonObject;
import pandas.core.BlockManager;
import pandas.core.DataFrame;
import pandas.core.Index;
import pandas.core.Series;

public class ModelData extends PythonObject {

	public ModelData(String module, String name){
		super(module, name);
	}

	public List<String> getEndogNames(){
		Object origEndog = getOrigEndog();

		if(origEndog instanceof Series){
			Series series = (Series)origEndog;

			String name = series.getName();

			return Collections.singletonList(name);
		} else

		if(origEndog instanceof DataFrame){
			DataFrame dataFrame = (DataFrame)origEndog;

			return getColumnNames(dataFrame);
		} else

		{
			throw new IllegalArgumentException();
		}
	}

	public List<String> getExogNames(){
		DataFrame origExog = getOrigExog();

		return getColumnNames(origExog);
	}

	public boolean hasOrigData(){
		Object origEndog = getOptionalObject("orig_endog");
		Object origExog = getOptionalObject("orig_exog");

		return (origEndog != null) && (origExog != null);
	}

	public Cache getCache(){
		Map<String, ?> dict = getDict("_cache");

		Cache cache = new Cache(getPythonModule() + "." + getPythonName(), "_cache");
		cache.update(dict);

		return cache;
	}

	public Object getOrigEndog(){
		return getObject("orig_endog");
	}

	public DataFrame getOrigExog(){
		return get("orig_exog", DataFrame.class);
	}

	static
	private List<String> getColumnNames(DataFrame dataFrame){
		BlockManager data = dataFrame.getData();

		Index columnAxis = data.getColumnAxis();

		// XXX
		return (List)columnAxis.getValues();
	}

	public class Cache extends PythonObject {

		public Cache(String module, String name){
			super(module, name);
		}

		public boolean hasNames(){
			return hasattr("xnames") && hasattr("ynames");
		}

		public List<String> getXNames(){
			return getStringList("xnames");
		}

		public List<String> getYNames(){
			Object yNames = getObject("ynames");

			if(yNames instanceof String){
				return Collections.singletonList((String)yNames);
			}

			return getStringList("ynames");
		}
	}
}