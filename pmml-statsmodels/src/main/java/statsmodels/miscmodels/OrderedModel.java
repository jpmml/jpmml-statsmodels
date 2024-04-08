/*
 * Copyright (c) 2023 Villu Ruusmann
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
package statsmodels.miscmodels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Iterables;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.OpType;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.mining.Segmentation;
import org.dmg.pmml.regression.RegressionModel;
import org.jpmml.converter.ContinuousFeature;
import org.jpmml.converter.ContinuousLabel;
import org.jpmml.converter.Feature;
import org.jpmml.converter.Label;
import org.jpmml.converter.ModelEncoder;
import org.jpmml.converter.ModelUtil;
import org.jpmml.converter.OrdinalLabel;
import org.jpmml.converter.Schema;
import org.jpmml.converter.ValueUtil;
import org.jpmml.converter.mining.MiningModelUtil;
import org.jpmml.converter.regression.RegressionModelUtil;
import org.jpmml.statsmodels.StatsModelsEncoder;
import scipy.stats.RVContinuous;
import statsmodels.Model;

public class OrderedModel extends Model {

	public OrderedModel(String module, String name){
		super(module, name);
	}

	@Override
	public org.dmg.pmml.Model encodeModel(List<? extends Number> params, Schema schema){
		RVContinuous distr = getDistr();
		Integer kExtra = getKExtra();
		Integer kLevels = getKLevels();
		Number offset = getOffset();

		if(kExtra != (kLevels - 1)){
			throw new IllegalArgumentException();
		}

		ModelEncoder encoder = (ModelEncoder)schema.getEncoder();

		OrdinalLabel ordinalLabel = (OrdinalLabel)schema.getLabel();
		List<? extends Feature> features = schema.getFeatures();

		List<? extends Number> varsParams = params.subList(0, params.size() - (kLevels - 1));
		List<? extends Number> thParams = params.subList(params.size() - (kLevels - 1), params.size());

		List<Double> thresholds = new ArrayList<>();

		double prevThreshold = Double.NaN;

		for(int i = 0; i < thParams.size(); i++){
			double threshold = (thParams.get(i)).doubleValue();

			if(i > 0){
				threshold = Math.exp(threshold) + prevThreshold;
			}

			thresholds.add(threshold);

			prevThreshold = threshold;
		}

		Schema segmentSchema = schema.toAnonymousRegressorSchema(DataType.DOUBLE);

		RegressionModel firstRegressionModel = RegressionModelUtil.createRegression(features, varsParams, (offset != null ? offset : 0d), RegressionModel.NormalizationMethod.NONE, segmentSchema)
			.setTargets(ModelUtil.createRescaleTargets(-1d, null, (ContinuousLabel)segmentSchema.getLabel()));

		OutputField linpredOutputField = ModelUtil.createPredictedField("linpred", OpType.CONTINUOUS, DataType.DOUBLE);

		DerivedField linpredField = encoder.createDerivedField(firstRegressionModel, linpredOutputField, true);

		Feature feature = new ContinuousFeature(encoder, linpredField);

		RegressionModel secondRegressionModel = RegressionModelUtil.createOrdinalClassification(feature, thresholds, parseNormalizationMethod(distr), true, schema);

		return MiningModelUtil.createModelChain(Arrays.asList(firstRegressionModel, secondRegressionModel), Segmentation.MissingPredictionTreatment.RETURN_MISSING);
	}

	@Override
	public Label encodeLabel(List<String> endogNames, StatsModelsEncoder encoder){
		List<Integer> labels = ValueUtil.asIntegers(getLabels());

		String endogName = Iterables.getOnlyElement(endogNames);

		DataField dataField = encoder.createDataField(endogName, OpType.ORDINAL, DataType.INTEGER, labels);

		return new OrdinalLabel(dataField);
	}

	@Override
	public List<Feature> encodeFeatures(List<String> exogNames, StatsModelsEncoder encoder){
		Integer kLevels = getKLevels();

		exogNames = exogNames.subList(0, exogNames.size() - (kLevels - 1));

		return super.encodeFeatures(exogNames, encoder);
	}

	public RVContinuous getDistr(){
		return get("distr", RVContinuous.class);
	}

	public Integer getKLevels(){
		return getInteger("k_levels");
	}

	public List<Number> getLabels(){
		return getNumberArray("labels");
	}

	public Number getOffset(){
		return (Number)getOptionalScalar("offset");
	}

	static
	private RegressionModel.NormalizationMethod parseNormalizationMethod(RVContinuous rvContinuous){
		String name = rvContinuous.getName();
		if(name == null){
			throw new IllegalArgumentException();
		}

		switch(name){
			case "logistic":
				return RegressionModel.NormalizationMethod.LOGIT;
			case "norm":
				return RegressionModel.NormalizationMethod.PROBIT;
			default:
				throw new IllegalArgumentException(name);
		}
	}
}