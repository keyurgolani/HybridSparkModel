package logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class Logger {
	
	static private BufferedWriter bw = null;
	
	public static void log(String logString) {
		try {
			bw = new BufferedWriter(new FileWriter(new File("./log.log"), true));
			bw.newLine();
			bw.write(getTimeStamp() + "\t" + logString);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void log() {
		try {
			bw = new BufferedWriter(new FileWriter(new File("./log.log"), true));
			bw.newLine();
			bw.write(""+getTimeStamp());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void log(Object obj) {
		try {
			bw = new BufferedWriter(new FileWriter(new File("./log.log"), true));
			bw.newLine();
			bw.write(getTimeStamp() + "\t" + obj.toString());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void logAppend(String logString) {
		try {
			bw = new BufferedWriter(new FileWriter(new File("./log.log"), true));
			bw.write(logString);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void logAppend(Object obj) {
		try {
			bw = new BufferedWriter(new FileWriter(new File("./log.log"), true));
			bw.write(obj.toString());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static Timestamp getTimeStamp() {
		return new Timestamp(new Date().getTime());
	}

}
