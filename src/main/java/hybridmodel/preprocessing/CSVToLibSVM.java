package hybridmodel.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import properties.ProjectProperties;
import properties.VectorizationProperties;

public class CSVToLibSVM {
	
	public static String convertCSVToLibSVM(String filePath) {
		System.out.println("Starting conversion..!");
		return convertVectorizedCSVToLibSVM(vectorize(filePath));
	}
	
	public static String convertVectorizedCSVToLibSVM(String filePath) {
		System.out.println("Converting vectorized data to LibSVM..!");
		String processedDataFilePath = "processedData.txt";
		File processedFile = new File(processedDataFilePath);
		try {
			if(!processedFile.exists() || processedFile.isDirectory()) {
				File inputFile = new File(filePath);
				if(inputFile.exists() && inputFile.isFile()) {
					BufferedReader br = new BufferedReader(new FileReader(inputFile));
					BufferedWriter bw = new BufferedWriter(new FileWriter(processedFile));
					String line;
					while((line = br.readLine()) != null) {
						bw.append(processRecord(line));
						bw.newLine();
					}
					br.close();
					bw.flush();
					bw.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return processedDataFilePath;
	}
	
	public static String vectorize(String filePath) {
		System.out.println("Vectorizing Data..!");
		String vectorizedFilePath = "vectorized.txt";
		File vectorizedFile = new File(vectorizedFilePath);
		try {
			if(!vectorizedFile.exists() || vectorizedFile.isDirectory()) {
				File inputFile = new File(filePath);
				if(inputFile.exists() && inputFile.isFile()) {
					BufferedReader br = new BufferedReader(new FileReader(filePath));
					BufferedWriter bw = new BufferedWriter(new FileWriter(vectorizedFilePath));
					String line = "";
					while ((line = br.readLine()) != null) {
						bw.append(convertCharToNumbers(line));
						bw.newLine();
					}
					br.close();
					bw.flush();
					bw.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vectorizedFilePath;
	}

	public static String processRecord(String featureString) {
		String tokens[] = featureString.split(",");
		StringBuilder result = new StringBuilder();
		result.append(Double.parseDouble(tokens[0]));
		double featureValue = 0;
		for (int idx = 1; idx < tokens.length; idx++) {
			featureValue = Double.parseDouble(tokens[idx]);
			if (featureValue != 0)
				result.append(" ").append(idx).append(":").append(featureValue);
		}
		return result.toString();
	}
	
	private static String convertCharToNumbers(String line) {
		String[] featuresInUse = line.split(",");
		featuresInUse = getFeaturesInUse(featuresInUse);
		String processedData = "";
		String label = featuresInUse[featuresInUse.length - 1];
		for (int i = 0; i < featuresInUse.length - 1; i++) {
			featuresInUse[featuresInUse.length - 1 - i] = featuresInUse[featuresInUse.length - 2 - i];
		}
		featuresInUse[0] = label;
		for (int i = 0; i < featuresInUse.length; i++) {
			if (!isNumeric(featuresInUse[i].trim())) {
				featuresInUse[i] = VectorizationProperties.getProperty(featuresInUse[i].trim());
				if(featuresInUse[i] == null) {
					featuresInUse[i] = "39";
				}
			}
			processedData = processedData + featuresInUse[i];
			if (i != featuresInUse.length - 1) {
				processedData = processedData + ",";
			}
		}
		return processedData;
	}
	
	private static String[] getFeaturesInUse(String[] dataList) {
		Vector<Integer> featuresInUse = new Vector<Integer>();
		for (String feature : ProjectProperties.featuresInUse) {
			featuresInUse.add(Integer.parseInt(VectorizationProperties.getProperty(feature)));
		}
		String[] returnArray = new String[featuresInUse.size()];
		for (int i = 0; i < featuresInUse.size(); i++) {
			returnArray[i] = dataList[featuresInUse.get(i)];
		}
		return returnArray;
	}
	
	private static boolean isNumeric(String data) {
		return data.matches("\\d+(\\.\\d+)?");
	}
	
}