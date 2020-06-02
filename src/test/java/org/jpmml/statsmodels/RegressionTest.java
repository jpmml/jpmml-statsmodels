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
package org.jpmml.statsmodels;

import org.junit.Test;

public class RegressionTest extends StatsModelsTest {

	@Test
	public void evaluateOLSAuto() throws Exception {
		evaluate("OLS", "Auto");
	}

	@Test
	public void evaluateOLSFormulaAuto() throws Exception {
		evaluate("OLSFormula", "Auto");
	}

	@Test
	public void evaluateWLSAuto() throws Exception {
		evaluate("WLS", "Auto");
	}

	@Test
	public void evaluateWLSFormulaAuto() throws Exception {
		evaluate("WLSFormula", "Auto");
	}
}