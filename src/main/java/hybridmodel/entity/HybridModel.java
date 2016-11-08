package hybridmodel.entity;

import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.tree.model.GradientBoostedTreesModel;
import org.apache.spark.mllib.tree.model.RandomForestModel;

public class HybridModel {
	
	private NaiveBayesModel model1;
	private RandomForestModel model2;
	private GradientBoostedTreesModel model3;
	private LogisticRegressionModel model4;
	private DecisionTreeModel model5;
	private SVMModel model6;
	
	public HybridModel(NaiveBayesModel model1, RandomForestModel model2, GradientBoostedTreesModel model3, LogisticRegressionModel model4, DecisionTreeModel model5, SVMModel model6) {
		this.model1 = model1;
		this.model2 = model2;
		this.model3 = model3;
		this.model4 = model4;
		this.model5 = model5;
		this.model6 = model6;
	}

	public NaiveBayesModel getModel1() {
		return model1;
	}

	public void setModel1(NaiveBayesModel model1) {
		this.model1 = model1;
	}

	public RandomForestModel getModel2() {
		return model2;
	}

	public void setModel2(RandomForestModel model2) {
		this.model2 = model2;
	}

	public GradientBoostedTreesModel getModel3() {
		return model3;
	}

	public void setModel3(GradientBoostedTreesModel model3) {
		this.model3 = model3;
	}

	public LogisticRegressionModel getModel4() {
		return model4;
	}

	public void setModel4(LogisticRegressionModel model4) {
		this.model4 = model4;
	}

	public DecisionTreeModel getModel5() {
		return model5;
	}

	public void setModel5(DecisionTreeModel model5) {
		this.model5 = model5;
	}

	public SVMModel getModel6() {
		return model6;
	}

	public void setModel6(SVMModel model6) {
		this.model6 = model6;
	}

}
