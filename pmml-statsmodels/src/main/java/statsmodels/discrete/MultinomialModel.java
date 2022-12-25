/*
 * Copyright (c) 2022 Villu Ruusmann
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
package statsmodels.discrete;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.OpType;
import org.jpmml.converter.CategoricalLabel;
import org.jpmml.converter.Label;
import org.jpmml.converter.TypeUtil;
import org.jpmml.statsmodels.StatsModelsEncoder;

public class MultinomialModel extends DiscreteModel {

	public MultinomialModel(String module, String name){
		super(module, name);
	}

	@Override
	public Label encodeLabel(List<String> yNames, StatsModelsEncoder encoder){
		Map<Integer, ?> yNamesMap = getYNamesMap();

		String yName = Iterables.getOnlyElement(yNames);

		List<Object> categories = new ArrayList<>();

		for(int i = 0; i < yNamesMap.size(); i++){
			Object category = yNamesMap.get(i);

			if(category == null){
				throw new IllegalArgumentException();
			}

			categories.add(category);
		}

		DataType dataType = TypeUtil.getDataType(categories, DataType.STRING);

		DataField dataField = encoder.createDataField(yName, OpType.CATEGORICAL, dataType, categories);

		return new CategoricalLabel(dataField);
	}

	public Integer getJ(){
		return getInteger("J");
	}

	public Integer getK(){
		return getInteger("K");
	}

	public Map<Integer, ?> getYNamesMap(){
		return (Map)getDict("_ynames_map");
	}
}