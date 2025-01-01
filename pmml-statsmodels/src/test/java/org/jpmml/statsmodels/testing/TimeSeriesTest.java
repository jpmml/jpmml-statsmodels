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
package org.jpmml.statsmodels.testing;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Equivalence;
import com.google.common.collect.Iterables;
import org.jpmml.evaluator.ResultField;
import org.jpmml.evaluator.testing.Batch;
import org.jpmml.evaluator.testing.BatchUtil;
import org.jpmml.evaluator.testing.Conflict;
import org.jpmml.evaluator.time_series.SeriesForecast;
import org.junit.Test;

public class TimeSeriesTest extends StatsModelsEncoderBatchTest {

	@Override
	public void evaluate(Batch batch) throws Exception {
		Function<Map<String, ?>, List<Map<String, ?>>> function = new Function<Map<String, ?>, List<Map<String, ?>>>(){

			@Override
			public List<Map<String, ?>> apply(Map<String, ?> map){
				Map.Entry<String, ?> entry = Iterables.getOnlyElement(map.entrySet());

				String name = entry.getKey();
				SeriesForecast seriesForecast = (SeriesForecast)entry.getValue();

				List<Double> values = seriesForecast.getValues();

				return values.stream()
					.map(value -> Collections.singletonMap(name, value))
					.collect(Collectors.toList());
			}
		};

		List<Conflict> conflicts = BatchUtil.evaluateSingleton(batch, function);

		checkConflicts(conflicts);
	}

	@Override
	public StatsModelsEncoderBatch createBatch(String algorithm, String dataset, Predicate<ResultField> predicate, Equivalence<Object> equivalence){
		StatsModelsEncoderBatch result = new StatsModelsEncoderBatch(algorithm, dataset, predicate, equivalence){

			@Override
			public TimeSeriesTest getArchiveBatchTest(){
				return TimeSeriesTest.this;
			}

			@Override
			public List<? extends Map<String, ?>> getInput() throws IOException {
				String algorithm = getAlgorithm();

				// XXX
				if("SSM".equals(algorithm)){
					return Collections.singletonList(Collections.singletonMap("horizon", 12));
				}

				return super.getInput();
			}
		};

		return result;
	}

	@Test
	public void evaluateSSMAirline() throws Exception {
		evaluate("SSM", "Airline");
	}
}