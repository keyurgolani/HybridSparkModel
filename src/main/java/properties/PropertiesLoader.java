package properties;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class PropertiesLoader {
	
	public static void loadProperties() {
		try {
			Properties prop = new Properties();
			prop.load(new FileReader(new File("./project.properties")));
			ProjectProperties.sparkAppName = prop.getProperty("sparkAppName");
			ProjectProperties.hadoopBasePath = prop.getProperty("hadoopBasePath");
			ProjectProperties.featuresInUse = prop.getProperty("featuresInUse").split(",");
			System.setProperty("HADOOP_USER_NAME", "hduser");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
