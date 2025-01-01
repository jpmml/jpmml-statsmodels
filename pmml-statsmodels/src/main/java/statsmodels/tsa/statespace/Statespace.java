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

import org.jpmml.python.CythonObject;

public class Statespace extends CythonObject {

	public Statespace(String module, String name){
		super(module, name);
	}

	@Override
	public void __init__(Object[] args){
		super.__setstate__(INIT_ATTRBUTES, args);
	}

	private static final String[] INIT_ATTRBUTES = {
		"obs",
		"design",
		"obs_intercept",
		"obs_cov",
		"transition",
		"state_intercept",
		"selection",
		"state_cov",
		"diagonal_obs_cov"
	};
}