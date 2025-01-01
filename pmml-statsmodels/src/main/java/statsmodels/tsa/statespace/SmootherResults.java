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
package statsmodels.tsa.statespace;

import org.jpmml.python.HasArray;
import org.jpmml.python.PythonObject;

public class SmootherResults extends PythonObject {

	public SmootherResults(String module, String name){
		super(module, name);
	}

	public HasArray getDesign(){
		return getArray("design");
	}

	public HasArray getObsIntercept(){
		return getArray("obs_intercept");
	}

	public HasArray getTransition(){
		return getArray("transition");
	}
}