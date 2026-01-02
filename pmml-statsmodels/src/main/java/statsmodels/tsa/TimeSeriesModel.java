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
package statsmodels.tsa;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Iterables;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.OpType;
import org.jpmml.converter.ContinuousLabel;
import org.jpmml.converter.ExceptionUtil;
import org.jpmml.converter.Feature;
import org.jpmml.converter.Label;
import org.jpmml.statsmodels.StatsModelsEncoder;
import org.jpmml.statsmodels.StatsModelsException;
import statsmodels.Model;

abstract
public class TimeSeriesModel extends Model {

	public TimeSeriesModel(String module, String name){
		super(module, name);
	}

	@Override
	public Label encodeLabel(List<String> endogNames, StatsModelsEncoder encoder){
		String endogName = Iterables.getOnlyElement(endogNames);

		DataField dataField = encoder.createDataField(endogName, OpType.CONTINUOUS, DataType.DOUBLE);

		return new ContinuousLabel(dataField);
	}

	@Override
	public List<Feature> encodeFeatures(List<String> exogNames, StatsModelsEncoder encoder){
		Integer kConstant = getKConstant();

		if(kConstant == 0){
			// Ignored
		} else

		if(kConstant == 1){

			if(!Objects.equals(Collections.singletonList("const"), exogNames)){
				throw new StatsModelsException("Variable " + ExceptionUtil.formatName("const") + " not in " + ExceptionUtil.formatNameList(exogNames));
			}
		} else

		{
			throw new StatsModelsException("Expected at most one constant term, got " + kConstant);
		}

		return Collections.emptyList();
	}
}