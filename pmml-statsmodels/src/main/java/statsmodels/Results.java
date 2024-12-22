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

import java.util.List;

import org.dmg.pmml.PMML;
import org.jpmml.converter.Schema;
import org.jpmml.python.PythonObject;
import org.jpmml.statsmodels.StatsModelsEncoder;

public class Results extends PythonObject {

	public Results(String module, String name){
		super(module, name);
	}

	public PMML encodePMML(StatsModelsEncoder encoder){
		Model model = getModel();

		Schema schema = model.encodeSchema(encoder);

		org.dmg.pmml.Model pmmlModel;

		if(model instanceof CrossSectionalModel){
			CrossSectionalModel crossSectionalModel = (CrossSectionalModel)model;

			List<Number> params = getParams();

			pmmlModel = crossSectionalModel.encodeModel(params, schema);
		} else

		{
			throw new IllegalArgumentException();
		}

		ensureAlgorithmName(pmmlModel, model.getClassName());

		return encoder.encodePMML(pmmlModel);
	}

	public org.dmg.pmml.Model encodeModel(Schema schema){
		Model model = getModel();

		org.dmg.pmml.Model pmmlModel;

		if(model instanceof CrossSectionalModel){
			CrossSectionalModel crossSectionalModel = (CrossSectionalModel)model;

			List<Number> params = getParams();

			pmmlModel = crossSectionalModel.encodeModel(params, schema);
		} else

		{
			throw new IllegalArgumentException();
		}

		ensureAlgorithmName(pmmlModel, model.getClassName());

		return pmmlModel;
	}

	public Model getModel(){
		return get("model", Model.class);
	}

	public List<Number> getParams(){
		return getNumberArray("params");
	}

	static
	private void ensureAlgorithmName(org.dmg.pmml.Model pmmlModel, String pythonClassName){
		String algorithmName = pmmlModel.getAlgorithmName();

		if(algorithmName == null){
			algorithmName = pythonClassName;

			pmmlModel.setAlgorithmName(algorithmName);
		}
	}
}