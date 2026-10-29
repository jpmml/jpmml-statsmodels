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

import org.junit.jupiter.api.Test;

public class RegressionTest extends StatsModelsEncoderBatchTest implements StatsModelsAlgorithms, StatsModelsDatasets {

	@Test
	public void evaluateGLMAuto() throws Exception {
		evaluate(GLM, AUTO);
	}

	@Test
	public void evaluateGLMConstAuto() throws Exception {
		evaluate(GLM + "Const", AUTO);
	}

	@Test
	public void evaluateGLMElasticNetAuto() throws Exception {
		evaluate(GLM + "ElasticNet", AUTO);
	}

	@Test
	public void evaluateGLMFormulaAuto() throws Exception {
		evaluate(GLM + "Formula", AUTO);
	}

	@Test
	public void evaluateGLMInversePowerFormulaAuto() throws Exception {
		evaluate(GLM + "InversePowerFormula", AUTO);
	}

	@Test
	public void evaluateOLSAuto() throws Exception {
		evaluate(OLS, AUTO);
	}

	@Test
	public void evaluateOLSConstAuto() throws Exception {
		evaluate(OLS + "Const", AUTO);
	}

	@Test
	public void evaluateOLSElasticNetAuto() throws Exception {
		evaluate(OLS + "ElasticNet", AUTO);
	}

	@Test
	public void evaluateOLSFormulaAuto() throws Exception {
		evaluate(OLS + "Formula", AUTO);
	}

	@Test
	public void evaluateOLSFormulaUncachedAuto() throws Exception {
		evaluate(OLS + "FormulaUncached", AUTO);
	}

	@Test
	public void evaluateOLSUncachedAuto() throws Exception {
		evaluate(OLS + "Uncached", AUTO);
	}

	@Test
	public void evaluateQuantReg5Auto() throws Exception {
		evaluate(QUANT_REG + "5", AUTO);
	}

	@Test
	public void evaluateQuantReg95Auto() throws Exception {
		evaluate(QUANT_REG + "95", AUTO);
	}

	@Test
	public void evaluateQuantRegFormula95Auto() throws Exception {
		evaluate(QUANT_REG + "Formula95", AUTO);
	}

	@Test
	public void evaluateWLSAuto() throws Exception {
		evaluate(WLS, AUTO);
	}

	@Test
	public void evaluateWLSConstAuto() throws Exception {
		evaluate(WLS + "Const", AUTO);
	}

	@Test
	public void evaluateWLSElasticNetAuto() throws Exception {
		evaluate(WLS + "ElasticNet", AUTO);
	}

	@Test
	public void evaluateWLSFormulaAuto() throws Exception {
		evaluate(WLS + "Formula", AUTO);
	}

	@Test
	public void evaluateGLMFormulaVisit() throws Exception {
		evaluate(GLM + "Formula", VISIT);
	}

	@Test
	public void evaluateGLMSqrtFormulaVisit() throws Exception {
		evaluate(GLM + "SqrtFormula", VISIT);
	}

	@Test
	public void evaluatePoissonFormulaVisit() throws Exception {
		evaluate(POISSON + "Formula", VISIT);
	}
}