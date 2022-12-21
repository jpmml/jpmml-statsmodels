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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Iterables;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Model;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMML;
import org.jpmml.converter.BinaryFeature;
import org.jpmml.converter.ContinuousFeature;
import org.jpmml.converter.ContinuousLabel;
import org.jpmml.converter.Feature;
import org.jpmml.converter.Label;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.converter.PMMLUtil;
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
		Schema schema = encodeSchema(encoder);

		Model model = encodeModel(schema);

		return encoder.encodePMML(model);
	}

	public Schema encodeSchema(StatsModelsEncoder encoder){
		RegressionModel regressionModel = getModel();

		ModelData modelData = regressionModel.getData();

		ModelData.Cache cache = modelData.getCache();

		List<String> xnames = cache.getXNames();
		List<String> ynames = cache.getYNames();

		Label label = encodeLabel(ynames, encoder);

		List<Feature> features = encodeFeatures(xnames, encoder);

		return new Schema(encoder, label, features);
	}

	public Label encodeLabel(List<String> ynames, StatsModelsEncoder encoder){
		String yname = Iterables.getOnlyElement(ynames);

		DataField dataField = encoder.createDataField(yname, OpType.CONTINUOUS, DataType.DOUBLE);

		return new ContinuousLabel(dataField);
	}

	public List<Feature> encodeFeatures(List<String> xnames, StatsModelsEncoder encoder){
		Integer kConstant = getKConstant();

		List<Feature> features = new ArrayList<>();

		Matcher interceptMatcher = RegressionResults.TERM_INTERCEPT.matcher("");
		Matcher binaryIndicatorMatcher = RegressionResults.TERM_BINARY_INDICATOR.matcher("");

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

		for(int i = 0; i < xnames.size(); i++){
			String xname = xnames.get(i);

			if((i == 0) && (expectIntercept)){
				interceptMatcher = interceptMatcher.reset(xname);

				if(interceptMatcher.matches()){
					isFormula = true;

					features.add(new InterceptFeature(encoder, xname, DataType.DOUBLE));

					continue;
				}
			} // End if

			if(i >= 0){
				binaryIndicatorMatcher = binaryIndicatorMatcher.reset(xname);

				if(binaryIndicatorMatcher.matches()){
					String name = binaryIndicatorMatcher.group(1);
					String value = binaryIndicatorMatcher.group(2);

					DataField dataField = encoder.getDataField(name);
					if(dataField == null){
						dataField = encoder.createDataField(name, OpType.CATEGORICAL, DataType.STRING);
					} // End if

					if(!isFormula){
						PMMLUtil.addValues(dataField, Collections.singletonList(value));
					}

					features.add(new BinaryFeature(encoder, dataField, value));
				} else

				{
					if(("const").equals(xname) && (expectIntercept)){
						features.add(new InterceptFeature(encoder, xname, DataType.STRING));
					} else

					{
						DataField dataField = encoder.createDataField(xname, OpType.CONTINUOUS, DataType.DOUBLE);

						features.add(new ContinuousFeature(encoder, dataField));
					}
				}
			}
		}

		return features;
	}

	public Model encodeModel(Schema schema){
		Integer kConstant = getKConstant();
		List<Number> params = getParams();

		PMMLEncoder encoder = schema.getEncoder();

		Label label = schema.getLabel();
		List<? extends Feature> features = schema.getFeatures();

		Number intercept = 0d;

		if(kConstant == 0){
			// Ignored
		} else

		if(kConstant == 1){
			Feature feature = features.get(0);

			if(feature instanceof InterceptFeature){
				InterceptFeature interceptFeature = (InterceptFeature)feature;

				params = new ArrayList<>(params);
				intercept = params.remove(0);

				features = new ArrayList<>(features);
				features.remove(0);

				schema = new Schema(encoder, label, features);
			} else

			{
				throw new IllegalArgumentException();
			}
		} else

		{
			throw new IllegalArgumentException();
		}

		return createRegressionModel(features, params, intercept, schema);
	}

	public org.dmg.pmml.regression.RegressionModel createRegressionModel(List<? extends Feature> features, List<? extends Number> coefficients, Number intercept, Schema schema){
		return RegressionModelUtil.createRegression(features, coefficients, intercept, org.dmg.pmml.regression.RegressionModel.NormalizationMethod.NONE, schema);
	}

	public RegressionModel getModel(){
		return get("model", RegressionModel.class);
	}

	public List<Number> getParams(){
		return getNumberArray("params");
	}

	public Integer getKConstant(){
		return getInteger("k_constant");
	}

	private static final Pattern TERM_INTERCEPT = Pattern.compile("Intercept");
	private static final Pattern TERM_BINARY_INDICATOR = Pattern.compile("C\\((.+)\\)\\[T\\.(.+)\\]");
}