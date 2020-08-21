package com.cpimsws.appcode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class used for Maintaining Exception's Log.
 * 
 * @EMP ID:104
 * @date 06-06-2014
 */
public class Logs {

	private static String pathException = "C:\\CPSIMS\\Logs\\Log.txt";


	public static void writeLog(Exception exc) {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathException,true))) {
			writer.newLine();
			String time=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
			writer.write(time);
			writer.newLine();
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

	public static void writeTeacherLog(String type,
			String day,String schoolId,String classId,
			String teacherUserId,HashMap<String, String> hmAtt,
			String Food_amount_id,String notes,String teacher_id) {

		String pathTeacher = "C:\\CPSIMS\\Logs\\LogTeacher";
		String time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
		String time1 = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		pathTeacher = pathTeacher + time1 + ".txt";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathTeacher,true))) {
			writer.newLine();
			writer.write(time);
			writer.newLine();			
			if(type.equals("submitAttendance")) {
				writer.newLine();
				writer.write("Submit Attendance");
				writer.newLine();
				writer.newLine();
				writer.write("Day=" + day + " School_Id=" + schoolId + " Class_Id=" + classId + " Teacher_User_Id=" + teacherUserId);
				Set<Entry<String, String>> set = hmAtt.entrySet();
				Iterator<Entry<String, String>> i = set.iterator();
				while(i.hasNext()) {

					Map.Entry<String, String> me = (Map.Entry<String, String>)i.next();
					System.out.print(me.getKey() + ": ");
					System.out.println(me.getValue());
					writer.write("infantId=" + me.getKey() + " presentStatus=" + me.getValue());
					writer.newLine();

				}

			} else if(type.equals("updateAttendance")) {

				writer.write("Update Attendance");
				writer.newLine();
				writer.newLine();
				Set<Entry<String, String>> set = hmAtt.entrySet();
				Iterator<Entry<String, String>> i = set.iterator();
				while(i.hasNext()) {

					Map.Entry<String, String> me = (Map.Entry<String, String>)i.next();
					System.out.print(me.getKey() + ": ");
					System.out.println(me.getValue());
					writer.write("infantId=" + me.getKey() + " presentStatus=" + me.getValue());
					writer.newLine();

				}
			}
			writer.write("===========================================================================================");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeLogFood(String infant_id,String exp_id,
			String giv_datetime,String food_id,String Food_amount_id,
			String notes,String teacher_id) {
		String pathTeacher = "C:\\CPSIMS\\Logs\\LogTeacher";
		String time1 = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		pathTeacher= pathTeacher + time1 + ".txt";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathTeacher,true))) {
			writer.newLine();
			writer.newLine();
			writer.write("Infant Id=" + infant_id);
			writer.newLine();
			writer.write("Expected Id=" + exp_id);
			writer.newLine();
			writer.write("Given Datetime=" + giv_datetime);
			writer.newLine();
			writer.write("Food id=" + food_id);
			writer.newLine();
			writer.write("Food amount id=" + Food_amount_id);
			writer.newLine();
			writer.write("notes=" + notes);
			writer.newLine();
			writer.write("Teacher id=" + teacher_id);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeLogDiaper(String infant_id,String time_changed,String diaper_id,String notes,String teacher_id) {

		String pathTeacher = "C:\\CPSIMS\\Logs\\LogTeacher";
		String time1 = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		pathTeacher= pathTeacher + time1 + ".txt";

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathTeacher,true))) {
			writer.newLine();
			writer.newLine();
			writer.write("Infant Id="+infant_id);
			writer.newLine();
			writer.write("Time Changed="+time_changed);
			writer.newLine();
			writer.write("Diaper Id="+diaper_id);
			writer.newLine();
			writer.write("notes="+notes);
			writer.newLine();
			writer.write("Teacher id="+teacher_id);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeLogNap(String infant_id, String exp_id, String start_datetime, 
			String end_datetime, String notes,
			String teacher_id) {
		String pathTeacher = "C:\\CPSIMS\\Logs\\LogTeacher";
		String time1=new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		pathTeacher = pathTeacher + time1 + ".txt";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathTeacher,true))) {
			writer.newLine();
			writer.newLine();
			writer.write("Infant Id=" + infant_id);
			writer.newLine();
			writer.write("Expected id=" + exp_id);
			writer.newLine();
			writer.write("Start Date Time=" + start_datetime);
			writer.newLine();
			writer.write(" End Date Time=" + end_datetime);
			writer.newLine();
			writer.write("Teacher id=" + teacher_id);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeLogMedi(String infant_id, String exp_id, String giv_datetime, 
			String medi_type_id, String medi_amount_id,
			String medi_reason_id,String notes,String teacher_id) {
		String pathTeacher = "C:\\CPSIMS\\Logs\\LogTeacher";
		String time1=new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		pathTeacher = pathTeacher + time1 + ".txt";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathTeacher,true))) {
			writer.newLine();
			writer.newLine();
			writer.write("Infant Id=" + infant_id);
			writer.newLine();
			writer.write("Expected id=" + exp_id);
			writer.newLine();
			writer.write("Given Date Time=" + giv_datetime);
			writer.newLine();
			writer.write("Medi Type Id=" + medi_type_id);
			writer.newLine();
			writer.write("Medi Amount Id=" + medi_amount_id);
			writer.newLine();
			writer.write("Medi Reason id=" + medi_reason_id);
			writer.newLine();
			writer.write("Notes=" + notes);
			writer.newLine();
			writer.write("Teacher id=" + teacher_id);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeLogOper(String infant_id, String activity_datetime,String activity,String notes) {
		String pathTeacher = "C:\\CPSIMS\\Logs\\LogTeacher";
		String time1=new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		pathTeacher=pathTeacher+time1+".txt";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathTeacher,true))) {
			writer.newLine();
			writer.newLine();
			writer.write("Infant Id=" + infant_id);
			writer.newLine();
			writer.write("Activity Date Time=" + activity_datetime);
			writer.newLine();
			writer.write("Activity=" + activity);
			writer.newLine();
			writer.write("Notes=" + notes);
			writer.newLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void writeLogLine(int val) {
		String pathTeacher = "C:\\CPSIMS\\Logs\\LogTeacher";
		String time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
		String time1 = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		pathTeacher = pathTeacher + time1 + ".txt";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathTeacher,true))) {
			writer.newLine();
			if (val==0) {
				writer.newLine();
				writer.write("===========================================================================================");
				writer.write(time);
			} else {
				writer.newLine();
				writer.write("===========================================================================================");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
