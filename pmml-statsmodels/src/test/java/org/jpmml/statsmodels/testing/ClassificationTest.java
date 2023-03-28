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
package org.jpmml.statsmodels.testing;

import org.jpmml.converter.testing.Datasets;
import org.jpmml.converter.testing.Fields;
import org.junit.Test;

public class ClassificationTest extends StatsModelsEncoderBatchTest implements Datasets, Fields {

	@Test
	public void evaluateGLMFormulaAudit() throws Exception {
		evaluate("GLMFormula", AUDIT, excludeFields(AUDIT_ADJUSTED));
	}

	@Test
	public void evaluateLogitFormulaAudit() throws Exception {
		evaluate("LogitFormula", AUDIT, excludeFields(AUDIT_ADJUSTED));
	}

	@Test
	public void evaluateLogitLassoFormulaAudit() throws Exception {
		evaluate("LogitLassoFormula", AUDIT, excludeFields(AUDIT_ADJUSTED));
	}

	@Test
	public void evaluateMNLogitIris() throws Exception {
		evaluate("MNLogit", IRIS, excludeFields(IRIS_SPECIES));
	}

	@Test
	public void evaluateMNLogitConstIris() throws Exception {
		evaluate("MNLogitConst", IRIS, excludeFields(IRIS_SPECIES));
	}

	@Test
	public void evaluateMNLogitLassoIris() throws Exception {
		evaluate("MNLogitLasso", IRIS, excludeFields(IRIS_SPECIES));
	}
}