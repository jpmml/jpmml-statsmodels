/*
 * Copyright (c) 2024 Villu Ruusmann
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Iterables;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.OpType;
import org.jpmml.converter.BinaryFeature;
import org.jpmml.converter.ContinuousFeature;
import org.jpmml.converter.ContinuousLabel;
import org.jpmml.converter.Feature;
import org.jpmml.converter.FieldUtil;
import org.jpmml.converter.Label;
import org.jpmml.converter.Schema;
import org.jpmml.statsmodels.InterceptFeature;
import org.jpmml.statsmodels.StatsModelsEncoder;
import statsmodels.data.ModelData;

abstract
public class CrossSectionalModel extends Model {

	public CrossSectionalModel(String module, String name){
		super(module, name);
	}

	abstract
	public org.dmg.pmml.Model encodeModel(List<? extends Number> params, Schema schema);

	@Override
	public Schema encodeSchema(StatsModelsEncoder encoder){
		ModelData data = getData();

		List<String> endogNames;
		List<String> exogNames;

		ModelData.Cache cache = data.getCache();
		if(cache.hasNames()){
			endogNames = cache.getYNames();
			exogNames = cache.getXNames();
		} else

		{
			endogNames = data.getEndogNames();
			exogNames = data.getExogNames();
		}

		Label label = encodeLabel(endogNames, encoder);

		List<Feature> features = encodeFeatures(exogNames, encoder);

		return new Schema(encoder, label, features);
	}

	@Override
	public org.dmg.pmml.Model encodeModel(Results results, Schema schema){
		List<Number> params = results.getParams();

		return encodeModel(params, schema);
	}

	public Label encodeLabel(List<String> endogNames, StatsModelsEncoder encoder){
		String endogName = Iterables.getOnlyElement(endogNames);

		DataField dataField = encoder.createDataField(endogName, OpType.CONTINUOUS, DataType.DOUBLE);

		return new ContinuousLabel(dataField);
	}

	public List<Feature> encodeFeatures(List<String> exogNames, StatsModelsEncoder encoder){
		Integer kConstant = getKConstant();

		List<Feature> features = new ArrayList<>();

		Matcher interceptMatcher = CrossSectionalModel.TERM_INTERCEPT.matcher("");
		Matcher binaryIndicatorMatcher = CrossSectionalModel.TERM_BINARY_INDICATOR.matcher("");

		boolean expectIntercept;

		if(kConstant == 0){
			expectIntercept = false;
		} else

		if(kConstant == 1){
			expectIntercept = true;
		} else

		{
			throw new IllegalArgumentException();
		}

		boolean isFormula = false;

		for(int i = 0; i < exogNames.size(); i++){
			String exogName = exogNames.get(i);

			if((i == 0) && (expectIntercept)){
				interceptMatcher = interceptMatcher.reset(exogName);

				if(interceptMatcher.matches()){
					isFormula = true;

					features.add(new InterceptFeature(encoder, exogName, DataType.DOUBLE));

					continue;
				}
			} // End if

			if(i >= 0){
				binaryIndicatorMatcher = binaryIndicatorMatcher.reset(exogName);

				if(binaryIndicatorMatcher.matches()){
					String name = binaryIndicatorMatcher.group(1);
					String value = binaryIndicatorMatcher.group(2);

					DataField dataField = encoder.getDataField(name);
					if(dataField == null){
						dataField = encoder.createDataField(name, OpType.CATEGORICAL, DataType.STRING);
					} // End if

					if(!isFormula){
						FieldUtil.addValues(dataField, Collections.singletonList(value));
					}

					features.add(new BinaryFeature(encoder, dataField, value));
				} else

				{
					if(("const").equals(exogName) && (expectIntercept)){
						features.add(new InterceptFeature(encoder, exogName, DataType.STRING));
					} else

					{
						DataField dataField = encoder.createDataField(exogName, OpType.CONTINUOUS, DataType.DOUBLE);

						features.add(new ContinuousFeature(encoder, dataField));
					}
				}
			}
		}

		return features;
	}

	static
	protected List<? extends Feature> dropInterceptFeature(List<? extends Feature> features, int index){
		Feature feature = features.get(index);

		if(feature instanceof InterceptFeature){
			InterceptFeature interceptFeature = (InterceptFeature)feature;

			features = new ArrayList<>(features);
			features.remove(interceptFeature);

			return features;
		} else

		{
			throw new IllegalArgumentException();
		}
	}

	private static final Pattern TERM_INTERCEPT = Pattern.compile("Intercept");
	private static final Pattern TERM_BINARY_INDICATOR = Pattern.compile("C\\((.+)\\)\\[T\\.(.+)\\]");
}