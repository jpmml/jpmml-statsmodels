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

import java.util.function.Predicate;

import com.google.common.base.Equivalence;
import org.jpmml.evaluator.ResultField;
import org.jpmml.evaluator.testing.Batch;
import org.jpmml.evaluator.testing.IntegrationTest;
import org.jpmml.evaluator.testing.PMMLEquivalence;

abstract
public class StatsModelsTest extends IntegrationTest {

	public StatsModelsTest(){
		super(new PMMLEquivalence(1e-13, 1e-13));
	}

	@Override
	protected Batch createBatch(String name, String dataset, Predicate<ResultField> predicate, Equivalence<Object> equivalence){
		Batch result = new StatsModelsTestBatch(name, dataset, predicate, equivalence){

			@Override
			public StatsModelsTest getIntegrationTest(){
				return StatsModelsTest.this;
			}
		};

		return result;
	}
}