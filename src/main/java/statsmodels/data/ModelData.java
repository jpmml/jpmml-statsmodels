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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Iterables;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.OpType;
import org.jpmml.converter.BinaryFeature;
import org.jpmml.converter.ContinuousFeature;
import org.jpmml.converter.ContinuousLabel;
import org.jpmml.converter.Feature;
import org.jpmml.converter.Label;
import org.jpmml.converter.PMMLUtil;
import org.jpmml.converter.Schema;
import org.jpmml.python.PythonObject;
import org.jpmml.statsmodels.InterceptFeature;
import org.jpmml.statsmodels.StatsModelsEncoder;

public class ModelData extends PythonObject {

	public ModelData(String module, String name){
		super(module, name);
	}

	public Schema toSchema(StatsModelsEncoder encoder){
		Cache cache = getCache();

		List<String> xnames = cache.getXNames();
		List<String> ynames = cache.getYNames();

		Label label;

		{
			String yname = Iterables.getOnlyElement(ynames);

			DataField dataField = encoder.createDataField(FieldName.create(yname), OpType.CONTINUOUS, DataType.DOUBLE);

			label = new ContinuousLabel(dataField);
		}

		List<Feature> features = new ArrayList<>();

		Matcher interceptMatcher = ModelData.TERM_INTERCEPT.matcher("");
		Matcher binaryIndicatorMatcher = ModelData.TERM_BINARY_INDICATOR.matcher("");

		boolean hasIntercept = false;

		for(int i = 0; i < xnames.size(); i++){
			String xname = xnames.get(i);

			if(i == 0){
				interceptMatcher = interceptMatcher.reset(xname);

				if(interceptMatcher.matches()){
					hasIntercept = true;

					features.add(new InterceptFeature(encoder, FieldName.create(xname), DataType.DOUBLE));

					continue;
				}
			} // End if

			if(i >= 0){
				binaryIndicatorMatcher = binaryIndicatorMatcher.reset(xname);

				if(binaryIndicatorMatcher.matches()){
					String name = binaryIndicatorMatcher.group(1);
					String value = binaryIndicatorMatcher.group(2);

					DataField dataField = encoder.getDataField(FieldName.create(name));
					if(dataField == null){
						dataField = encoder.createDataField(FieldName.create(name), OpType.CATEGORICAL, DataType.STRING);
					} // End if

					if(!hasIntercept){
						PMMLUtil.addValues(dataField, Collections.singletonList(value));
					}

					features.add(new BinaryFeature(encoder, dataField, value));
				} else

				{
					DataField dataField = encoder.createDataField(FieldName.create(xname), OpType.CONTINUOUS, DataType.DOUBLE);

					features.add(new ContinuousFeature(encoder, dataField));
				}
			}
		}

		return new Schema(label, features);
	}

	public Cache getCache(){
		Map<String, ?> map = get("_cache", Map.class);

		Cache cache = new Cache(getPythonModule() + "." + getPythonName(), "_cache");
		cache.putAll(map);

		return cache;
	}

	public class Cache extends PythonObject {

		public Cache(String module, String name){
			super(module, name);
		}

		public List<String> getXNames(){
			return getList("xnames", String.class);
		}

		public List<String> getYNames(){
			Object ynames = get("ynames");

			if(ynames instanceof String){
				return Collections.singletonList((String)ynames);
			}

			return getList("ynames", String.class);
		}
	}

	private static final Pattern TERM_INTERCEPT = Pattern.compile("Intercept");
	private static final Pattern TERM_BINARY_INDICATOR = Pattern.compile("C\\((.+)\\)\\[T\\.(.+)\\]");
}