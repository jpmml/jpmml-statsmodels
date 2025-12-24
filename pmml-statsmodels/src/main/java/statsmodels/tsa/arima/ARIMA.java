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
package statsmodels.tsa.arima;

import java.util.List;

import org.dmg.pmml.Array;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.MiningField.UsageType;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.time_series.InterceptVector;
import org.dmg.pmml.time_series.MeasurementMatrix;
import org.dmg.pmml.time_series.StateSpaceModel;
import org.dmg.pmml.time_series.StateVector;
import org.dmg.pmml.time_series.TransitionMatrix;
import org.jpmml.converter.CMatrix;
import org.jpmml.converter.Matrix;
import org.jpmml.converter.ModelUtil;
import org.jpmml.converter.PMMLUtil;
import org.jpmml.converter.Schema;
import org.jpmml.python.HasArray;
import org.jpmml.statsmodels.StatsModelsEncoder;
import statsmodels.Results;
import statsmodels.tsa.TimeSeriesModel;
import statsmodels.tsa.statespace.SmootherResults;

public class ARIMA extends TimeSeriesModel {

	public ARIMA(String module, String name){
		super(module, name);
	}

	@Override
	public Schema encodeSchema(StatsModelsEncoder encoder){
		Schema schema = super.encodeSchema(encoder);

		@SuppressWarnings("unused")
		DataField dataField = encoder.createDataField("horizon", OpType.CONTINUOUS, DataType.INTEGER);

		return schema;
	}

	@Override
	public org.dmg.pmml.time_series.TimeSeriesModel encodeModel(Results results, Schema schema){
		HasArray predictedState = results.getArray("predicted_state");
		SmootherResults smootherResults = results.get("smoother_results", SmootherResults.class);

		HasArray design = smootherResults.getDesign();
		HasArray obsIntercept = smootherResults.getObsIntercept();
		HasArray transition = smootherResults.getTransition();

		MiningSchema miningSchema = ModelUtil.createMiningSchema(schema)
			.addMiningFields(ModelUtil.createMiningField("horizon", UsageType.SUPPLEMENTARY));

		StateVector stateVector = new StateVector(createRealArray(predictedState, -1));

		MeasurementMatrix measurementMatrix = new MeasurementMatrix(createMatrix(design));

		TransitionMatrix transitionMatrix = new TransitionMatrix(createMatrix(transition));

		InterceptVector interceptVector = new InterceptVector(createRealArray(obsIntercept, -1))
			.setType(InterceptVector.Type.OBSERVATION);

		StateSpaceModel stateSpaceModel = new StateSpaceModel()
			.setStateVector(stateVector)
			.setMeasurementMatrix(measurementMatrix)
			.setTransitionMatrix(transitionMatrix)
			.setInterceptVector(interceptVector);

		org.dmg.pmml.time_series.TimeSeriesModel timeSeriesModel = new org.dmg.pmml.time_series.TimeSeriesModel(MiningFunction.TIME_SERIES, org.dmg.pmml.time_series.TimeSeriesModel.Algorithm.STATE_SPACE_MODEL, miningSchema)
			.setStateSpaceModel(stateSpaceModel);

		return timeSeriesModel;
	}

	static
	private Array createRealArray(HasArray hasArray, int column){
		Matrix<?> matrix = toMatrix(hasArray);

		List<? extends Number> columnValues;

		if(column >= 0){
			columnValues = (List)matrix.getColumnValues(column);
		} else

		{
			columnValues = (List)matrix.getColumnValues(matrix.getColumns() + column);
		}

		return PMMLUtil.createRealArray(columnValues);
	}

	static
	private org.dmg.pmml.Matrix createMatrix(HasArray hasArray){
		Matrix<?> matrix = toMatrix(hasArray);

		org.dmg.pmml.Matrix result = new org.dmg.pmml.Matrix()
			.setNbRows(matrix.getRows())
			.setNbCols(matrix.getColumns());

		for(int row = 0; row < matrix.getRows(); row++){
			List<? extends Number> rowValues = (List)matrix.getRowValues(row);

			result.addArrays(PMMLUtil.createRealArray(rowValues));
		}

		return result;
	}

	static
	private Matrix<?> toMatrix(HasArray hasArray){
		int[] shape = hasArray.getArrayShape();
		List<?> values = hasArray.getArrayContent();

		if(shape.length == 3){

			if(shape[2] != 1){
				throw new IllegalArgumentException();
			}
		}

		return new CMatrix(values, shape[0], shape[1]);
	}

	private static final String NAME_INDEX = "index";
}