package hybridmodel;

import static logger.Logger.log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.GradientBoostedTrees;
import org.apache.spark.mllib.tree.RandomForest;
import org.apache.spark.mllib.tree.configuration.BoostingStrategy;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.tree.model.GradientBoostedTreesModel;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.mllib.util.MLUtils;

import hybridmodel.entity.HybridModel;
import hybridmodel.preprocessing.CSVToLibSVM;
import hybridmodel.utility.HybridModelUtility;
import hybridmodel.utility.TwoTuple;
import properties.ProjectProperties;
import properties.PropertiesLoader;
import properties.VectorizationProperties;

public class Launch {
	
	static {
		PropertiesLoader.loadProperties();
		VectorizationProperties.loadProperties();
	}
	
	public static List<LabeledPoint> FPs;
	public static List<LabeledPoint> TPs;
	public static List<LabeledPoint> FNs;
	public static List<LabeledPoint> TNs;
	
	public static List<LabeledPoint> ambiguousTPs;
	public static List<LabeledPoint> confidentTPs;
	public static List<LabeledPoint> ambiguousTNs;
	public static List<LabeledPoint> confidentTNs;
	public static List<LabeledPoint> ambiguousFPs;
	public static List<LabeledPoint> confidentFPs;
	public static List<LabeledPoint> ambiguousFNs;
	public static List<LabeledPoint> confidentFNs;
	
	public static List<LabeledPoint> maliciousSamples;
	public static List<LabeledPoint> benignSamples;
	
	public static NaiveBayesModel modelNB = null;
	public static RandomForestModel modelRF = null;
	public static GradientBoostedTreesModel modelGBT = null;
	public static LogisticRegressionModel modelLR = null;
	public static DecisionTreeModel modelDT = null;
	public static SVMModel modelSVM = null;
	
	public static List<LabeledPoint> ambiguousSamples;
	public static List<LabeledPoint> confidentSamples;
	
	public static List<LabeledPoint> dosTPs = new ArrayList<LabeledPoint>();    
	public static List<LabeledPoint> r2lTPs = new ArrayList<LabeledPoint>();    
	public static List<LabeledPoint> u2rTPs = new ArrayList<LabeledPoint>();    
	public static List<LabeledPoint> probingTPs = new ArrayList<LabeledPoint>();
	                                                                            
	public static List<LabeledPoint> dosTNs = new ArrayList<LabeledPoint>();    
	public static List<LabeledPoint> r2lTNs = new ArrayList<LabeledPoint>();    
	public static List<LabeledPoint> u2rTNs = new ArrayList<LabeledPoint>();    
	public static List<LabeledPoint> probingTNs = new ArrayList<LabeledPoint>();
	                                                                            
	public static List<LabeledPoint> dosFPs = new ArrayList<LabeledPoint>();    
	public static List<LabeledPoint> r2lFPs = new ArrayList<LabeledPoint>();    
	public static List<LabeledPoint> u2rFPs = new ArrayList<LabeledPoint>();    
	public static List<LabeledPoint> probingFPs = new ArrayList<LabeledPoint>();
	                                                                            
	public static List<LabeledPoint> dosFNs = new ArrayList<LabeledPoint>();    
	public static List<LabeledPoint> r2lFNs = new ArrayList<LabeledPoint>();    
	public static List<LabeledPoint> u2rFNs = new ArrayList<LabeledPoint>();    
	public static List<LabeledPoint> probingFNs = new ArrayList<LabeledPoint>();
	
	public static SparkConf sparkConf = new SparkConf().setAppName(ProjectProperties.sparkAppName).setMaster("local");
	public static JavaSparkContext jsc = new JavaSparkContext(sparkConf);

	public static void main(String[] args) throws IOException {
		
//		String finalData = ProjectProperties.hadoopBasePath + ProjectProperties.finalDataPath;
		JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(jsc.sc(), CSVToLibSVM.convertCSVToLibSVM("./input.txt")).toJavaRDD();
		
		//All 42 Features
//		String filterString = "duration,protocol_type,service,flag,src_bytes,dst_bytes,land,wrong_fragment,urgent,hot,num_failed_logins,logged_in,num_compromised,root_shell,su_attempted,num_root,num_file_creations,num_shells,num_access_files,num_outbound_cmds,is_host_login,is_guest_login,count,srv_count,serror_rate,srv_serror_rate,rerror_rate,srv_rerror_rate,same_srv_rate,diff_srv_rate,srv_diff_host_rate,dst_host_count,dst_host_srv_count,dst_host_same_srv_rate,dst_host_diff_srv_rate,dst_host_same_src_port_rate,dst_host_srv_diff_host_rate,dst_host_serror_rate,dst_host_srv_serror_rate,dst_host_rerror_rate,dst_host_srv_rerror_rate";
		//Top 10 Plus Top 10 Features
//		String filterString = "duration,protocol_type,service,flag,src_bytes,dst_bytes,land,wrong_fragment,urgent,hot,num_failed_logins,is_host_login,is_guest_login,srv_diff_host_rate,dst_host_count,dst_host_srv_count,dst_host_same_srv_rate,dst_host_diff_srv_rate,dst_host_srv_serror_rate,dst_host_srv_rerror_rate";
		//Top 10 Features
		String filterString = "duration,protocol_type,service,flag,src_bytes,dst_bytes,land,wrong_fragment,urgent,hot";
		
		log("------------------------------------------------ Full Data Processing ---------------------------------------");
		analyseData(data, filterString);
		JavaRDD<LabeledPoint> ambiguousData = jsc.parallelize(ambiguousSamples);
		JavaRDD<LabeledPoint> confidentData = jsc.parallelize(confidentSamples);
		
		
		
		log("------------------------------------------------ Ambiguous Data Processing With Top 10 Plus Top 10 Features ---------------------------------------");
		//All 42 Features
//		String ambiguousFilterString1 = "duration,protocol_type,service,flag,src_bytes,dst_bytes,land,wrong_fragment,urgent,hot,num_failed_logins,logged_in,num_compromised,root_shell,su_attempted,num_root,num_file_creations,num_shells,num_access_files,num_outbound_cmds,is_host_login,is_guest_login,count,srv_count,serror_rate,srv_serror_rate,rerror_rate,srv_rerror_rate,same_srv_rate,diff_srv_rate,srv_diff_host_rate,dst_host_count,dst_host_srv_count,dst_host_same_srv_rate,dst_host_diff_srv_rate,dst_host_same_src_port_rate,dst_host_srv_diff_host_rate,dst_host_serror_rate,dst_host_srv_serror_rate,dst_host_rerror_rate,dst_host_srv_rerror_rate";
		//Top 10 Plus Top 10 Features
		String ambiguousFilterString1 = "duration,protocol_type,service,flag,src_bytes,dst_bytes,land,wrong_fragment,urgent,hot,num_failed_logins,is_host_login,is_guest_login,srv_diff_host_rate,dst_host_count,dst_host_srv_count,dst_host_same_srv_rate,dst_host_diff_srv_rate,dst_host_srv_serror_rate,dst_host_srv_rerror_rate";
		//Top 10 Features
//		String ambiguousFilterString1 = "duration,protocol_type,service,flag,src_bytes,dst_bytes,land,wrong_fragment,urgent,hot";
		analyseData(ambiguousData, ambiguousFilterString1);
		

		
		log("------------------------------------------------ Ambiguous Data Processing With All 41 Features ---------------------------------------");
		//All 42 Features
		String ambiguousFilterString2 = "duration,protocol_type,service,flag,src_bytes,dst_bytes,land,wrong_fragment,urgent,hot,num_failed_logins,logged_in,num_compromised,root_shell,su_attempted,num_root,num_file_creations,num_shells,num_access_files,num_outbound_cmds,is_host_login,is_guest_login,count,srv_count,serror_rate,srv_serror_rate,rerror_rate,srv_rerror_rate,same_srv_rate,diff_srv_rate,srv_diff_host_rate,dst_host_count,dst_host_srv_count,dst_host_same_srv_rate,dst_host_diff_srv_rate,dst_host_same_src_port_rate,dst_host_srv_diff_host_rate,dst_host_serror_rate,dst_host_srv_serror_rate,dst_host_rerror_rate,dst_host_srv_rerror_rate";
		//Top 10 Plus Top 10 Features
//		String ambiguousFilterString2 = "duration,protocol_type,service,flag,src_bytes,dst_bytes,land,wrong_fragment,urgent,hot,num_failed_logins,is_host_login,is_guest_login,srv_diff_host_rate,dst_host_count,dst_host_srv_count,dst_host_same_srv_rate,dst_host_diff_srv_rate,dst_host_srv_serror_rate,dst_host_srv_rerror_rate";
		//Top 10 Features
//		String ambiguousFilterString2 = "duration,protocol_type,service,flag,src_bytes,dst_bytes,land,wrong_fragment,urgent,hot";
		analyseData(ambiguousData, ambiguousFilterString2);
		
		
		
		log("------------------------------------------------ Confident Data Processing ---------------------------------------");
		analyseData(confidentData, filterString);
		
		log("------------------------------------------------ Categorical Accuracy --------------------------------------------");
		log("------------------------- DOS Accuracy ------------------------");
		log("TPR:\t\t\t\t" + ((double)dosTPs.size() / (double)(dosTPs.size() + dosFNs.size())));
		log("TNR:\t\t\t\t" + ((double)dosTNs.size() / (double)(dosTNs.size() + dosFPs.size())));
		log("PPV:\t\t\t\t" + ((double)dosTPs.size() / (double)(dosTPs.size() + dosFPs.size())));
		log("NPV:\t\t\t\t" + ((double)dosTNs.size() / (double)(dosTNs.size() + dosFNs.size())));
		log("FPR:\t\t\t\t" + ((double)dosFPs.size() / (double)(dosFPs.size() + dosTNs.size())));
		log("FNR:\t\t\t\t" + ((double)dosFNs.size() / (double)(dosFNs.size() + dosTPs.size())));
		log("FDR:\t\t\t\t" + ((double)dosFPs.size() / (double)(dosFPs.size() + dosTPs.size())));
		log("ACC:\t\t\t\t" + ((double)(dosTPs.size() + dosTNs.size()) / (double)(dosTPs.size() + dosTNs.size() + dosFPs.size() + dosFNs.size())));
		log("F1:\t\t\t\t" + ((double)(2 * dosTPs.size()) / (double)(2 * dosTPs.size() + dosFPs.size() + dosFNs.size())));
		
		
		log("------------------------- R2L Accuracy ------------------------");
		log("TPR:\t\t\t\t" + ((double)r2lTPs.size() / (double)(r2lTPs.size() + r2lFNs.size())));
		log("TNR:\t\t\t\t" + ((double)r2lTNs.size() / (double)(r2lTNs.size() + r2lFPs.size())));
		log("PPV:\t\t\t\t" + ((double)r2lTPs.size() / (double)(r2lTPs.size() + r2lFPs.size())));
		log("NPV:\t\t\t\t" + ((double)r2lTNs.size() / (double)(r2lTNs.size() + r2lFNs.size())));
		log("FPR:\t\t\t\t" + ((double)r2lFPs.size() / (double)(r2lFPs.size() + r2lTNs.size())));
		log("FNR:\t\t\t\t" + ((double)r2lFNs.size() / (double)(r2lFNs.size() + r2lTPs.size())));
		log("FDR:\t\t\t\t" + ((double)r2lFPs.size() / (double)(r2lFPs.size() + r2lTPs.size())));
		log("ACC:\t\t\t\t" + ((double)(r2lTPs.size() + r2lTNs.size()) / (double)(r2lTPs.size() + r2lTNs.size() + r2lFPs.size() + r2lFNs.size())));
		log("F1:\t\t\t\t" + ((double)(2 * r2lTPs.size()) / (double)(2 * r2lTPs.size() + r2lFPs.size() + r2lFNs.size())));
		
		
		log("------------------------- U2R Accuracy ------------------------");
		log("TPR:\t\t\t\t" + ((double)u2rTPs.size() / (double)(u2rTPs.size() + u2rFNs.size())));
		log("TNR:\t\t\t\t" + ((double)u2rTNs.size() / (double)(u2rTNs.size() + u2rFPs.size())));
		log("PPV:\t\t\t\t" + ((double)u2rTPs.size() / (double)(u2rTPs.size() + u2rFPs.size())));
		log("NPV:\t\t\t\t" + ((double)u2rTNs.size() / (double)(u2rTNs.size() + u2rFNs.size())));
		log("FPR:\t\t\t\t" + ((double)u2rFPs.size() / (double)(u2rFPs.size() + u2rTNs.size())));
		log("FNR:\t\t\t\t" + ((double)u2rFNs.size() / (double)(u2rFNs.size() + u2rTPs.size())));
		log("FDR:\t\t\t\t" + ((double)u2rFPs.size() / (double)(u2rFPs.size() + u2rTPs.size())));
		log("ACC:\t\t\t\t" + ((double)(u2rTPs.size() + u2rTNs.size()) / (double)(u2rTPs.size() + u2rTNs.size() + u2rFPs.size() + u2rFNs.size())));
		log("F1:\t\t\t\t" + ((double)(2 * u2rTPs.size()) / (double)(2 * u2rTPs.size() + u2rFPs.size() + u2rFNs.size())));
		
		
		log("------------------------- Probing Accuracy ------------------------");
		log("TPR:\t\t\t\t" + ((double)probingTPs.size() / (double)(probingTPs.size() + probingFNs.size())));
		log("TNR:\t\t\t\t" + ((double)probingTNs.size() / (double)(probingTNs.size() + probingFPs.size())));
		log("PPV:\t\t\t\t" + ((double)probingTPs.size() / (double)(probingTPs.size() + probingFPs.size())));
		log("NPV:\t\t\t\t" + ((double)probingTNs.size() / (double)(probingTNs.size() + probingFNs.size())));
		log("FPR:\t\t\t\t" + ((double)probingFPs.size() / (double)(probingFPs.size() + probingTNs.size())));
		log("FNR:\t\t\t\t" + ((double)probingFNs.size() / (double)(probingFNs.size() + probingTPs.size())));
		log("FDR:\t\t\t\t" + ((double)probingFPs.size() / (double)(probingFPs.size() + probingTPs.size())));
		log("ACC:\t\t\t\t" + ((double)(probingTPs.size() + probingTNs.size()) / (double)(probingTPs.size() + probingTNs.size() + probingFPs.size() + probingFNs.size())));
		log("F1:\t\t\t\t" + ((double)(2 * probingTPs.size()) / (double)(2 * probingTPs.size() + probingFPs.size() + probingFNs.size())));
		
		
		jsc.stop();
	}
	
	private static void analyseData(JavaRDD<LabeledPoint> data, String filterString) {
		
		JavaRDD<LabeledPoint>[] splits = data.randomSplit(new double[] { 0.5, 0.5 });
		JavaRDD<LabeledPoint> trainingData = splits[0];
		JavaRDD<LabeledPoint> filteredTrainingData = filterData(trainingData, filterString);
		JavaRDD<LabeledPoint> testData = splits[1];
		JavaRDD<LabeledPoint> filteredTestingData = filterData(testData, filterString);
		
		
		Integer numClasses = 5;
		HashMap<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
		Integer numTrees = 20; // Use more in practice.
		String featureSubsetStrategy = "auto"; // auto | all | sqrt | log2 | onethird | 10 | 20 | ...
		String impurity = "gini";
		Integer maxDepth = 5;
		Integer maxBins = 100;
		Integer seed = 12345;
		
		// Logistic Regression Model
		System.out.println("Training Logistic Regression model");
		final long logisticRegressionStartTime = System.currentTimeMillis();
		// Integer numClasses = 23;
		modelLR = new LogisticRegressionWithLBFGS().setNumClasses(numClasses).run(filteredTrainingData.rdd());
		final long logisticRegressionEndTime = System.currentTimeMillis();
		log("Logistic Regression Training Time:\t\t\t\t" + (logisticRegressionEndTime - logisticRegressionStartTime));

		// Random Forest Model
		System.out.println("Training Random Forest model");
		final long randomForestStartTime = System.currentTimeMillis();
		modelRF = RandomForest.trainClassifier(filteredTrainingData, numClasses, categoricalFeaturesInfo, numTrees,
				featureSubsetStrategy, impurity, maxDepth, maxBins, seed);
		final long randomForestEndTime = System.currentTimeMillis();
		log("Random Forest Training Time:\t\t\t\t" + (randomForestEndTime - randomForestStartTime));

		// Naive Bayes Model
		System.out.println("Training Naive Bayes model");
		final long naiveBayesStartTime = System.currentTimeMillis();
		modelNB = NaiveBayes.train(filteredTrainingData.rdd(), 1.0);
		final long naiveBayesEndTime = System.currentTimeMillis();
		log("Naive Bayes Training Time:\t\t\t\t" + (naiveBayesEndTime - naiveBayesStartTime));

		// Gradient Boosting Model
		System.out.println("Training Gradient Boosting model");
		final long gradientBoostingStartTime = System.currentTimeMillis();
		BoostingStrategy boostingStrategy = BoostingStrategy.defaultParams("Classification");
		boostingStrategy.setNumIterations(3); // Note: Use more iterations in
												// practice.
		boostingStrategy.getTreeStrategy().setNumClasses(2);
		boostingStrategy.getTreeStrategy().setMaxDepth(5);
		// HashMap<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
		boostingStrategy.treeStrategy().setCategoricalFeaturesInfo(categoricalFeaturesInfo);
		modelGBT = GradientBoostedTrees.train(filteredTrainingData, boostingStrategy);
		final long gradientBoostingEndTime = System.currentTimeMillis();
		log("Gradient Boosting Training Time:\t\t\t\t" + (gradientBoostingEndTime - gradientBoostingStartTime));

		// Decision Tree Model
		System.out.println("Training Decision Tree model");
		final long decisionTreeStartTime = System.currentTimeMillis();
		// Integer numClasses = 23;
		// HashMap<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
		// String impurity = "gini";
		// Integer maxDepth = 5;
		// Integer maxBins = 100;
		modelDT = DecisionTree.trainClassifier(filteredTrainingData, numClasses, categoricalFeaturesInfo, impurity, maxDepth,
				maxBins);
		final long decisionTreeEndTime = System.currentTimeMillis();
		log("Decision Tree Training Time:\t\t\t\t" + (decisionTreeEndTime - decisionTreeStartTime));

		// Support Vector Model
		// System.out.println("Training Support Vector model");
		// final long supportVectorStartTime = System.currentTimeMillis();
		// int numIterations = 100;
		// modelSVM = SVMWithSGD.train(filteredTrainingData.rdd(), numIterations);
		// final long supportVectorEndTime = System.currentTimeMillis();
		// log("Naive Bayes Training Time:\t\t\t\t" + (supportVectorEndTime -
		// supportVectorStartTime));
		
		HybridModel finalModel = new HybridModel(modelNB, modelRF, modelGBT, modelLR, modelDT, modelSVM);
		
		List<LabeledPoint> filteredTestingList = filteredTestingData.collect();
		List<LabeledPoint> testingList = testData.collect();
		
		maliciousSamples = new ArrayList<LabeledPoint>();
		benignSamples = new ArrayList<LabeledPoint>();
		
		FPs = new ArrayList<LabeledPoint>();         
		TPs = new ArrayList<LabeledPoint>();         
		FNs = new ArrayList<LabeledPoint>();         
		TNs = new ArrayList<LabeledPoint>();         
		                                             
		ambiguousTPs = new ArrayList<LabeledPoint>();
		confidentTPs = new ArrayList<LabeledPoint>();
		ambiguousTNs = new ArrayList<LabeledPoint>();
		confidentTNs = new ArrayList<LabeledPoint>();
		ambiguousFPs = new ArrayList<LabeledPoint>();
		confidentFPs = new ArrayList<LabeledPoint>();
		ambiguousFNs = new ArrayList<LabeledPoint>();
		confidentFNs = new ArrayList<LabeledPoint>();
		
		ambiguousSamples = new ArrayList<LabeledPoint>();
		confidentSamples = new ArrayList<LabeledPoint>();
				
		for (int i = 0; i < filteredTestingList.size(); i++) {
			LabeledPoint currentPoint = testingList.get(i);
			LabeledPoint currentFilteredPoint = filteredTestingList.get(i);
			TwoTuple<Double, Double> predictionTuple = HybridModelUtility.getPredictionAndScore(finalModel, currentFilteredPoint);
			double score = predictionTuple.getOne();
			double prediction = predictionTuple.getTwo();
			
			if (prediction == currentFilteredPoint.label()) {
				if (prediction > 0) {
					TPs.add(currentPoint);
					if(currentFilteredPoint.label() == 1) {
						dosTPs.add(currentPoint);
					} else if(currentFilteredPoint.label() == 2) {
						u2rTPs.add(currentPoint);
					} else if(currentFilteredPoint.label() == 3) {
						r2lTPs.add(currentPoint);
					} else if(currentFilteredPoint.label() == 4) {
						probingTPs.add(currentPoint);
					}
					maliciousSamples.add(currentPoint);
					if(score >= 0.8) {
						confidentTPs.add(currentPoint);
					} else {
						ambiguousTPs.add(currentPoint);
					}
				} else {
					TNs.add(currentPoint);
					if(currentFilteredPoint.label() == 1) {
						dosTNs.add(currentPoint);
					} else if(currentFilteredPoint.label() == 2) {
						u2rTNs.add(currentPoint);
					} else if(currentFilteredPoint.label() == 3) {
						r2lTNs.add(currentPoint);
					} else if(currentFilteredPoint.label() == 4) {
						probingTNs.add(currentPoint);
					}
					benignSamples.add(currentPoint);
					if(score >= 0.8) {
						confidentTNs.add(currentPoint);
					} else {
						ambiguousTNs.add(currentPoint);
					}
				}
			} else {
				if (prediction > 0) {
					FPs.add(currentPoint);
					if(currentFilteredPoint.label() == 1) {
						dosFPs.add(currentPoint);
					} else if(currentFilteredPoint.label() == 2) {
						u2rFPs.add(currentPoint);
					} else if(currentFilteredPoint.label() == 3) {
						r2lFPs.add(currentPoint);
					} else if(currentFilteredPoint.label() == 4) {
						probingFPs.add(currentPoint);
					}
					benignSamples.add(currentPoint);
					if(score >= 0.8) {
						confidentFPs.add(currentPoint);
					} else {
						ambiguousFPs.add(currentPoint);
					}
				} else {
					FNs.add(currentPoint);
					if(currentFilteredPoint.label() == 1) {
						dosFNs.add(currentPoint);
					} else if(currentFilteredPoint.label() == 2) {
						u2rFNs.add(currentPoint);
					} else if(currentFilteredPoint.label() == 3) {
						r2lFNs.add(currentPoint);
					} else if(currentFilteredPoint.label() == 4) {
						probingFNs.add(currentPoint);
					}
					maliciousSamples.add(currentPoint);
					if(score >= 0.8) {
						confidentFNs.add(currentPoint);
					} else {
						ambiguousFNs.add(currentPoint);
					}
				}
			}
		}
		
		log("Total Data Count:\t\t\t\t" + filteredTestingData.count());
		
		log("Malicious Sample Count:\t\t\t\t" + maliciousSamples.size());
		log("Benign Sample Count:\t\t\t\t" + benignSamples.size());
		
		log("TP:\t\t\t\t" + TPs.size());
		log("TN:\t\t\t\t" + TNs.size());
		log("FP:\t\t\t\t" + FPs.size());
		log("FN:\t\t\t\t" + FNs.size());
		
		log("TPR:\t\t\t\t" + ((double)TPs.size() / (double)(TPs.size() + FNs.size())));
		log("TNR:\t\t\t\t" + ((double)TNs.size() / (double)(TNs.size() + FPs.size())));
		log("PPV:\t\t\t\t" + ((double)TPs.size() / (double)(TPs.size() + FPs.size())));
		log("NPV:\t\t\t\t" + ((double)TNs.size() / (double)(TNs.size() + FNs.size())));
		log("FPR:\t\t\t\t" + ((double)FPs.size() / (double)(FPs.size() + TNs.size())));
		log("FNR:\t\t\t\t" + ((double)FNs.size() / (double)(FNs.size() + TPs.size())));
		log("FDR:\t\t\t\t" + ((double)FPs.size() / (double)(FPs.size() + TPs.size())));
		log("ACC:\t\t\t\t" + ((double)(TPs.size() + TNs.size()) / (double)(TPs.size() + TNs.size() + FPs.size() + FNs.size())));
		log("F1:\t\t\t\t" + ((double)(2 * TPs.size()) / (double)(2 * TPs.size() + FPs.size() + FNs.size())));
		
		log("Ambiguous TP:\t\t\t\t" + ambiguousTPs.size());
		log("Confident TP:\t\t\t\t" + confidentTPs.size());
		log("Ambiguous TN:\t\t\t\t" + ambiguousTNs.size());
		log("Confident TN:\t\t\t\t" + confidentTNs.size());
		log("Ambiguous FP:\t\t\t\t" + ambiguousFPs.size());
		log("Confident FP:\t\t\t\t" + confidentFPs.size());
		log("Ambiguous FN:\t\t\t\t" + ambiguousFNs.size());
		log("Confident FN:\t\t\t\t" + confidentFNs.size());
		
		log("Confident TPR:\t\t\t\t" + ((double)confidentTPs.size() / (double)(confidentTPs.size() + confidentFNs.size())));
		log("Confident TNR:\t\t\t\t" + ((double)confidentTNs.size() / (double)(confidentTNs.size() + confidentFPs.size())));
		log("Confident PPV:\t\t\t\t" + ((double)confidentTPs.size() / (double)(confidentTPs.size() + confidentFPs.size())));
		log("Confident NPV:\t\t\t\t" + ((double)confidentTNs.size() / (double)(confidentTNs.size() + confidentFNs.size())));
		log("Confident FPR:\t\t\t\t" + ((double)confidentFPs.size() / (double)(confidentFPs.size() + confidentTNs.size())));
		log("Confident FNR:\t\t\t\t" + ((double)confidentFNs.size() / (double)(confidentFNs.size() + confidentTPs.size())));
		log("Confident FDR:\t\t\t\t" + ((double)confidentFPs.size() / (double)(confidentFPs.size() + confidentTPs.size())));
		log("Confident ACC:\t\t\t\t" + ((double)(confidentTPs.size() + confidentTNs.size()) / (double)(confidentTPs.size() + confidentTNs.size() + confidentFPs.size() + confidentFNs.size())));
		log("Confident F1:\t\t\t\t" + ((double)(2 * confidentTPs.size()) / (double)(2 * confidentTPs.size() + confidentFPs.size() + confidentFNs.size())));
		
		log("Ambiguous TPR:\t\t\t\t" + ((double)ambiguousTPs.size() / (double)(ambiguousTPs.size() + ambiguousFNs.size())));
		log("Ambiguous TNR:\t\t\t\t" + ((double)ambiguousTNs.size() / (double)(ambiguousTNs.size() + ambiguousFPs.size())));
		log("Ambiguous PPV:\t\t\t\t" + ((double)ambiguousTPs.size() / (double)(ambiguousTPs.size() + ambiguousFPs.size())));
		log("Ambiguous NPV:\t\t\t\t" + ((double)ambiguousTNs.size() / (double)(ambiguousTNs.size() + ambiguousFNs.size())));
		log("Ambiguous FPR:\t\t\t\t" + ((double)ambiguousFPs.size() / (double)(ambiguousFPs.size() + ambiguousTNs.size())));
		log("Ambiguous FNR:\t\t\t\t" + ((double)ambiguousFNs.size() / (double)(ambiguousFNs.size() + ambiguousTPs.size())));
		log("Ambiguous FDR:\t\t\t\t" + ((double)ambiguousFPs.size() / (double)(ambiguousFPs.size() + ambiguousTPs.size())));
		log("Ambiguous ACC:\t\t\t\t" + ((double)(ambiguousTPs.size() + ambiguousTNs.size()) / (double)(ambiguousTPs.size() + ambiguousTNs.size() + ambiguousFPs.size() + ambiguousFNs.size())));
		log("Ambiguous F1:\t\t\t\t" + ((double)(2 * ambiguousTPs.size()) / (double)(2 * ambiguousTPs.size() + ambiguousFPs.size() + ambiguousFNs.size())));
		
		ambiguousSamples.addAll(ambiguousFNs);
		ambiguousSamples.addAll(ambiguousFPs);
		ambiguousSamples.addAll(ambiguousTNs);
		ambiguousSamples.addAll(ambiguousTPs);
		
		confidentSamples.addAll(confidentFNs);
		confidentSamples.addAll(confidentFPs);
		confidentSamples.addAll(confidentTNs);
		confidentSamples.addAll(confidentTPs);
		
	}
	
	public static JavaRDD<LabeledPoint> filterData(JavaRDD<LabeledPoint> data, String filterString) {
		return data.map(new Function<LabeledPoint, LabeledPoint>() {
			@Override
			public LabeledPoint call(LabeledPoint point) throws Exception {
				double label = point.label();
				double[] features = point.features().toArray();
				String[] featuresInUse = filterString.split(",");
				double[] filteredFeatures = new double[featuresInUse.length];
				for (int i = 0; i < featuresInUse.length; i++) {
					filteredFeatures[i] = features[Integer.parseInt(VectorizationProperties.getProperty(featuresInUse[i]))];
				}
				return new LabeledPoint(label, Vectors.dense(filteredFeatures));
			}
		});
	}
}
