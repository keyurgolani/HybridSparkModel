package hybridmodel.utility;

import java.util.HashMap;

public class ProjectUtility {

	

	

	

	public static TwoTuple<Double, Integer> getMode(Double[] array) {
		HashMap<Double, Integer> occuranceCounts = new HashMap<Double, Integer>();
		int max = 1;
		Double temp = array[0];
		for (int i = 0; i < array.length; i++) {
			if (occuranceCounts.get(array[i]) != null) {
				int count = occuranceCounts.get(array[i]);
				occuranceCounts.put(array[i], ++count);
				if (count > max) {
					max = count;
					temp = array[i];
				}
			} else {
				occuranceCounts.put(array[i], 1);
			}
		}
		return new TwoTuple<Double, Integer>(temp, occuranceCounts.get(temp));
	}

	public static int[] initializeWithZero(int[] ambiguousSampleCount) {
		for (int i = 0; i < ambiguousSampleCount.length; i++) {
			ambiguousSampleCount[i] = 0;
		}
		return ambiguousSampleCount;
	}

	public static String[] initializeWithIndexFileNames(String[] fileNames, double[] ambiguousTrigger) {
		for (int i = 0; i < fileNames.length; i++) {
			fileNames[i] = "ambiguous" + ambiguousTrigger[i] + ".txt";
		}
		return fileNames;
	}

}
