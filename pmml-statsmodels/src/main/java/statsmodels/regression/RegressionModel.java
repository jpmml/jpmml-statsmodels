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

import org.jpmml.converter.Feature;
import org.jpmml.converter.Label;
import org.jpmml.converter.ModelEncoder;
import org.jpmml.converter.Schema;
import statsmodels.Model;

abstract
public class RegressionModel extends Model {

	public RegressionModel(String module, String name){
		super(module, name);
	}

	abstract
	public org.dmg.pmml.Model encodeModel(List<? extends Number> coefficients, Number intercept, Schema schema);

	@Override
	public org.dmg.pmml.Model encodeModel(List<? extends Number> params, Schema schema){
		Integer kConstant = getKConstant();

		ModelEncoder encoder = schema.getEncoder();
		Label label = schema.getLabel();
		List<? extends Feature> features = schema.getFeatures();

		List<Number> coefficients = new ArrayList<>(params);
		Number intercept = null;

		// XXX
		int kIndex = 0;

		if(kConstant == 0){
			// Ignored
		} else

		if(kConstant == 1){
			intercept = coefficients.remove(kIndex);

			features = dropInterceptFeature(features, kIndex);

			schema = new Schema(encoder, label, features);
		} else

		{
			throw new IllegalArgumentException();
		}

		return encodeModel(coefficients, intercept, schema);
	}
}