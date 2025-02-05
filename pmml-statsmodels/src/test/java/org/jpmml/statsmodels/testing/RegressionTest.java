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
package org.jpmml.statsmodels.testing;

import org.jpmml.converter.testing.Datasets;
import org.junit.jupiter.api.Test;

public class RegressionTest extends StatsModelsEncoderBatchTest implements Datasets {

	@Test
	public void evaluateGLMAuto() throws Exception {
		evaluate("GLM", AUTO);
	}

	@Test
	public void evaluateGLMConstAuto() throws Exception {
		evaluate("GLMConst", AUTO);
	}

	@Test
	public void evaluateGLMElasticNetAuto() throws Exception {
		evaluate("GLMElasticNet", AUTO);
	}

	@Test
	public void evaluateGLMFormulaAuto() throws Exception {
		evaluate("GLMFormula", AUTO);
	}

	@Test
	public void evaluateOLSAuto() throws Exception {
		evaluate("OLS", AUTO);
	}

	@Test
	public void evaluateOLSConstAuto() throws Exception {
		evaluate("OLSConst", AUTO);
	}

	@Test
	public void evaluateOLSElasticNetAuto() throws Exception {
		evaluate("OLSElasticNet", AUTO);
	}

	@Test
	public void evaluateOLSFormulaAuto() throws Exception {
		evaluate("OLSFormula", AUTO);
	}

	@Test
	public void evaluateQuantReg5Auto() throws Exception {
		evaluate("QuantReg5", AUTO);
	}

	@Test
	public void evaluateQuantReg95Auto() throws Exception {
		evaluate("QuantReg95", AUTO);
	}

	@Test
	public void evaluateQuantRegFormula95Auto() throws Exception {
		evaluate("QuantRegFormula95", AUTO);
	}

	@Test
	public void evaluateWLSAuto() throws Exception {
		evaluate("WLS", AUTO);
	}

	@Test
	public void evaluateWLSConstAuto() throws Exception {
		evaluate("WLSConst", AUTO);
	}

	@Test
	public void evaluateWLSElasticNetAuto() throws Exception {
		evaluate("WLSElasticNet", AUTO);
	}

	@Test
	public void evaluateWLSFormulaAuto() throws Exception {
		evaluate("WLSFormula", AUTO);
	}

	@Test
	public void evaluateGLMFormulaVisit() throws Exception {
		evaluate("GLMFormula", VISIT);
	}

	@Test
	public void evaluatePoissonFormulaVisit() throws Exception {
		evaluate("PoissonFormula", VISIT);
	}
}