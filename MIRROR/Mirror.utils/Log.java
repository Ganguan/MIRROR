package MIRROR.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Log {
	public static FileWriter logger;
	public static File f;
	
	public static void writeError(Exception e) {
		try {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			logger.append(errors.toString());
			logger.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	public static void refreshLog() {
		// TODO Auto-generated method stub
		try {
			if (f == null) {
//				f = new File(System.getProperty("user.dir") + "/QMove.log");
				f = new File(System.getProperty("user.dir") + "/MyRef.log");
			}
			if (f.exists()) {
				f.delete();
			}
			
//			logger = new FileWriter(System.getProperty("user.dir") + "/QMove.log", true);
			logger = new FileWriter(System.getProperty("user.dir") + "/MyRef.log", true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeLog(String message) {
		try {

			logger.append(message);
			logger.append("\n");
			logger.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
