package hybridmodel.utility;

import java.util.ArrayList;

import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.tree.model.RandomForestModel;

import hybridmodel.entity.HybridModel;

public class HybridModelUtility {

	public static double getPredictionScore(RandomForestModel model, LabeledPoint testLabelPoint) {
		DecisionTreeModel[] decisionTreeModels = model.trees();
		Double positives = 0.0;
		for (DecisionTreeModel decisionTreeModel : decisionTreeModels) {
			if(decisionTreeModel.predict(testLabelPoint.features()) > 0) {
				positives ++;
			}
		}
		return (positives/(double)model.numTrees());
	}
	
	public static TwoTuple<Double, Double> getPredictionAndScore(HybridModel model, LabeledPoint testLabelPoint) {
		ArrayList<Double> predictions = new ArrayList<Double>();
		if(model.getModel1() != null)predictions.add(model.getModel1().predict(testLabelPoint.features()));
		if(model.getModel2() != null)predictions.add(model.getModel2().predict(testLabelPoint.features()));
		if(model.getModel3() != null)predictions.add(model.getModel3().predict(testLabelPoint.features()));
		if(model.getModel4() != null)predictions.add(model.getModel4().predict(testLabelPoint.features()));
		if(model.getModel5() != null)predictions.add(model.getModel5().predict(testLabelPoint.features()));
		if(model.getModel6() != null)predictions.add(model.getModel6().predict(testLabelPoint.features()));
		Double[] predictionsArray = new Double[predictions.size()];
		for (int i = 0; i < predictions.size(); i++) {
			predictionsArray[i] = predictions.get(i);
		}
		TwoTuple<Double, Integer> modeCount = ProjectUtility.getMode(predictionsArray);
		Double decision = modeCount.getOne();
		int count = modeCount.getTwo();
		return new TwoTuple<Double, Double>((double)count/(double)predictions.size(), decision);
	}

}
