package Main;
import java.io.*;
import java.util.Calendar;


public class Logs {

	private static String path = "C:\\MigrateDataLog\\Log.txt";

	public static void writeLog(Exception exc, String appName) {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(path,true))) {
			writer.newLine();
			writer.write("Project Name :\t" + appName +"Date : "+ Calendar.getInstance().getTime() );
			StackTraceElement[] stackTrace = exc.getStackTrace();
			for (StackTraceElement excDetails : stackTrace) {
				writer.write("\t"+excDetails );
				writer.newLine();
			}		
			writer.write("===========================================================================================");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeLogTest(String appName) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(path,true))) {
			writer.newLine();
			writer.write("Project Name :\t" + appName +"Date : "+ Calendar.getInstance().getTime() );
			writer.write("\t"+appName );
			writer.newLine();
			writer.write("===========================================================================================");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
