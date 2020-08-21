package com.cpimsws.appcode;

import java.util.Calendar;

public class Constant {
	public final static int loadResult=15;

	public static final String returnNothing="<return>" +
			"<row>" +
			"<value0>" +
			"false" +
			"</value0>" +
			"</row>" +
			"</return>";

	public static String getCurrentDay() {

		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		String returnVal = "";
		switch (day) {
		case Calendar.SUNDAY:
			returnVal = "SUNDAY";
		case Calendar.MONDAY:
			returnVal = "SUNDAY";
		case Calendar.TUESDAY:
			returnVal = "TUESDAY";
		case Calendar.WEDNESDAY:
			returnVal = "WEDNESDAY";
		case Calendar.THURSDAY:
			returnVal =  "THURSDAY";
		case Calendar.FRIDAY:
			returnVal = "FRIDAY";
		default:
			returnVal = "SATURDAY";
		}
		return returnVal;
	}
}
