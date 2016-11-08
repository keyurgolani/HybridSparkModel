package properties;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class VectorizationProperties {
	
	public static Properties prop;
	
	public static void loadProperties() {
		try {
			prop = new Properties();
			prop.load(new FileReader("./vectorization.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getProperty(String key) {
		return prop.getProperty(key);
	}

}
