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

import java.util.function.Predicate;

import com.google.common.base.Equivalence;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.ResultField;
import org.jpmml.python.testing.PythonEncoderBatch;
import org.jpmml.statsmodels.StatsModelsEncoder;
import statsmodels.ResultsWrapper;

abstract
public class StatsModelsEncoderBatch extends PythonEncoderBatch {

	public StatsModelsEncoderBatch(String algorithm, String dataset, Predicate<ResultField> predicate, Equivalence<Object> equivalence){
		super(algorithm, dataset, predicate, equivalence);
	}

	@Override
	abstract
	public StatsModelsEncoderBatchTest getArchiveBatchTest();

	@Override
	public PMML getPMML() throws Exception {
		StatsModelsEncoder encoder = new StatsModelsEncoder();

		ResultsWrapper resultsWrapper = loadPickle(ResultsWrapper.class);

		PMML pmml = resultsWrapper.encodePMML(encoder);

		validatePMML(pmml);

		return pmml;
	}
}