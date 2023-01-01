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

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Iterables;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.OpType;
import org.jpmml.converter.CategoricalLabel;
import org.jpmml.converter.Label;
import org.jpmml.statsmodels.StatsModelsEncoder;

abstract
public class BinaryModel extends DiscreteModel {

	public BinaryModel(String module, String name){
		super(module, name);
	}

	@Override
	public Label encodeLabel(List<String> yNames, StatsModelsEncoder encoder){
		String yName = Iterables.getOnlyElement(yNames);

		DataField dataField = encoder.createDataField(yName, OpType.CATEGORICAL, DataType.INTEGER, Arrays.asList(0, 1));

		return new CategoricalLabel(dataField);
	}
}