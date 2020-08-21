package com.cpimsws.attendance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.json.JSONArray;
import org.json.JSONObject;
import com.cpimsws.appcode.Constant;
import com.cpimsws.appcode.DatabaseHelper;
import com.cpimsws.appcode.Logs;

@Path("AttendanceOperation")
public class AttendanceOperation
{
	private Connection con = null;
	private final String returnNothing = "<return>" + "<row>" + "<value0>"
			+ "false" + "</value0>" + "</row>" + "</return>";

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String respondAsReady() {
		return "Demo service is ready!";
	}

	@GET
	@Path("schoolDetails")
	@Produces(MediaType.TEXT_XML)
	public String schoolDetails() {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "SELECT \r\n" + "    SI.SCHOOL_INFO_ID,\r\n"
					+ "    SI.SCHOOL_BRANCH_NAME\r\n"
					+ "    FROM school_info AS SI";
			ResultSet rs = stmt.executeQuery(query);
			return returnValue(rs);
		} catch (Exception exc) {
			exc.printStackTrace();
			Logs.writeLog(exc);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return returnNothing;
	}

	/**
	 * Get Class Id and Class Name of Particular School.
	 * 
	 * @param school_id
	 * @return
	 */
	@GET
	@Path("classDetails/{school_id}")
	@Produces(MediaType.TEXT_XML)
	public String classDetails(@PathParam("school_id") String school_id) {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "SELECT\r\n" + "    CL.CLASSROOM_ID,\r\n"
					+ "    CL.NAME\r\n" + "    FROM classroom AS CL\r\n"
					+ "    WHERE CL.SCHOOL_INFO_ID=" + school_id;
			ResultSet rs = stmt.executeQuery(query);
			return returnValue(rs);
		} catch (Exception exc) {
			exc.printStackTrace();
			Logs.writeLog(exc);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return returnNothing;
	}

	@GET
	@Path("programDetails")
	@Produces(MediaType.TEXT_XML)
	public String programDetails() {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "SELECT \r\n" + "    PR.PROGRAM_ID,\r\n"
					+ "    PR.PROGRAM_NAME\r\n" + "    FROM program AS PR";
			ResultSet rs = stmt.executeQuery(query);
			return returnValue(rs);
		} catch (Exception exc) {
			exc.printStackTrace();
			Logs.writeLog(exc);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return returnNothing;
	}

	/**
	 * Get Student List of Particular School and Class.
	 * 
	 * @param classId
	 * @return
	 */
	@GET
	@Path("studentList/{classId}")
	@Produces(MediaType.TEXT_XML)
	public String studentList(@PathParam("classId") String classId) {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "SELECT \r\n"
					+ "  IAD.INFANT_ACCOUNT_DETAILS_ID,\r\n"
					+ "	 ENR.Enrollment_Id, \r\n"
					+ "  CONCAT(IAD.FIRST_NAME,' ',IAD.LAST_NAME) AS 'STUDENT_NAME'  \r\n"
					+ "  FROM infant_account_details AS IAD\r\n"
					+ "  INNER JOIN enrollment as ENR \r\n"
					+ "  ON ENR.INFANT_ACCOUNT_DETAILS_ID = IAD.INFANT_ACCOUNT_DETAILS_ID\r\n"
					+ "  WHERE ENR.Classroom_Id = " + classId;
			ResultSet rs = stmt.executeQuery(query);
			return returnValue(rs);
		} catch (Exception exc) {
			exc.printStackTrace();
			Logs.writeLog(exc);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return returnNothing;
	}

	/**
	 * Get Attendance Details of Particular School and Class.
	 * 
	 * @param classId
	 * @return
	 */
	@GET
	@Path("studentListForAttendance/{classId}/{date}")
	@Produces(MediaType.TEXT_XML)
	public String studentListForAttendance(
			@PathParam("classId") String classId, @PathParam("date") String date) {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "select COUNT(*) from attendance as AT\r\n"
					+ "where DATE_FORMAT(AT.ATTENDANCE_DATE_TIME,'%Y-%m-%d')='"
					+ date + "'" + "AND AT.CLASSROOM_ID=" + classId;
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				String count = rs.getString(1);
				if (count.equalsIgnoreCase("0")) {
					query = "SELECT \r\n" + 
							"  ENROLL.Enrollment_Id,\r\n" + 
							"  IAD.INFANT_ACCOUNT_DETAILS_ID,\r\n" + 
							"  CONCAT(IAD.FIRST_NAME,' ',IAD.LAST_NAME) AS 'STUDENT_NAME',\r\n" + 
							"  '' AS 'ATTENDANCE_ID',\r\n" + 
							"  IF((SELECT CHKIN.StudentCheckInId FROM studentcheckin AS CHKIN WHERE CHKIN.INFANT_ACCOUNT_DETAILS_ID = IAD.INFANT_ACCOUNT_DETAILS_ID AND DATE_FORMAT(CHKIN.CheckInDate ,'%Y-%m-%d')='"+date+"' LIMIT 1) IS NULL,\r\n" + 
							"  0,1) AS 'PRESENT'\r\n" + 
							"  from infant_account_details AS IAD\r\n" + 
							"  INNER JOIN enrollment AS ENROLL \r\n" + 
							"  ON IAD.INFANT_ACCOUNT_DETAILS_ID = ENROLL.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
							"  WHERE \r\n" + 
							"  ENROLL.Classroom_Id=" +classId+" \r\n"+ 
							"  AND '"+date+"' BETWEEN DATE_FORMAT( ENROLL.Start_Date, '%Y-%m-%d') AND DATE_FORMAT( ENROLL.End_Date, '%Y-%m-%d') ";

				} else {
					query = "SELECT \r\n"
							+ "ENROLL.Enrollment_Id,\r\n"
							+ "IAD.INFANT_ACCOUNT_DETAILS_ID,\r\n"
							+ "CONCAT(IAD.FIRST_NAME,' ',IAD.LAST_NAME) AS 'STUDENT_NAME',\r\n"
							+ "IATD.INFANT_ATTENDANCE_ID,\r\n"
							+ "IF((SELECT CHKIN.StudentCheckInId FROM studentcheckin AS CHKIN WHERE CHKIN.INFANT_ACCOUNT_DETAILS_ID = IAD.INFANT_ACCOUNT_DETAILS_ID AND DATE_FORMAT(CHKIN.CheckInDate ,'%Y-%m-%d')='"
							+ date
							+ "' LIMIT 1) IS NULL,\r\n"
							+ "IATD.PRESENT,\r\n"
							+ "1) AS 'PRESENT_STATUS'\r\n"
							+ "FROM infant_attendance AS IATD\r\n"
							+ "INNER JOIN infant_account_details AS IAD \r\n"
							+ "ON IAD.INFANT_ACCOUNT_DETAILS_ID = IATD.INFANT_ACCOUNT_DETAIL_ID\r\n"
							+ "INNER JOIN attendance AS ATD \r\n"
							+ "ON ATD.ATTENDANCE_ID = IATD.ATTENDANCE_ID\r\n"
							+ "INNER JOIN enrollment AS ENROLL \r\n"
							+ "ON IATD.INFANT_ACCOUNT_DETAIL_ID= ENROLL.INFANT_ACCOUNT_DETAILS_ID\r\n"
							+ "WHERE \r\n"
							+ "DATE_FORMAT( ATD.ATTENDANCE_DATE_TIME,'%Y-%m-%d')='"+ date + "' \r\n"
							+ "AND ENROLL.Classroom_Id="+ classId+" \r\n" 
							+ "AND '"+date+"' BETWEEN DATE_FORMAT( ENROLL.Start_Date, '%Y-%m-%d') AND DATE_FORMAT( ENROLL.End_Date, '%Y-%m-%d') ";
				}
				System.out.print(query);
				rs = stmt.executeQuery(query);
				return returnValue(rs);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			Logs.writeLog(exc);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return returnNothing;
	}

	/**
	 * Submit Attendance.
	 * 
	 * @param val
	 */
	@POST
	@Path("submitAttendance")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void submitAttendance(MultivaluedMap<String, String> val) {
		try {
			String day = Constant.getCurrentDay();
			String schoolId = val.getFirst("schoolId");
			String classId = val.getFirst("classId");
			String teacherUserId = val.getFirst("teacherUserId");
			String json = val.getFirst("infantAttendanceDetails");
			HashMap<String, String> hmAtt =  new HashMap<String, String>();
			con = DatabaseHelper.connection();
			String query = "insert into attendance(ATTENDANCE_DATE_TIME,DAY,SCHOOL_INFO_ID,CLASSROOM_ID,TEACHER_ACCOUNT_DETAIL_ID)"
					+ "values (NOW(),?,?,?,?)";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, day);
			ps.setString(2, schoolId);
			ps.setString(3, classId);
			ps.setString(4, teacherUserId);
			int confirm = ps.executeUpdate();
			if (confirm > 0) {
				JSONObject jObj = new JSONObject(json);
				if (jObj != null) {
					query = "insert into infant_attendance (ATTENDANCE_ID,INFANT_ACCOUNT_DETAIL_ID,PRESENT) values ((SELECT max(ATTENDANCE_ID) from attendance),?,?)";
					ps = con.prepareStatement(query);
					con.setAutoCommit(false);
					JSONArray authorObject = jObj.getJSONArray("value");
					for (int i = 0; i < authorObject.length(); i++) {
						JSONObject c = authorObject.getJSONObject(i);
						String infantId = c.getString("infantId");
						String presentStatus = c.getString("presentStatus");
						ps.setString(1, infantId);
						ps.setString(2, presentStatus);
						hmAtt.put(infantId, presentStatus);
						ps.addBatch();
					}
					ps.executeBatch();
					con.commit();
				}
			}
			Logs.writeTeacherLog("submitAttendance",day,schoolId,classId,teacherUserId,hmAtt,"","","");
		} catch (Exception e) {
			e.printStackTrace();
			Logs.writeLog(e);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	@POST
	@Path("updateAttendance")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateAttendance(MultivaluedMap<String, String> val) {
		try {
			String json = val.getFirst("infantAttendanceDetails");
			HashMap<String, String> hmAtt =  new HashMap<String, String>();
			con = DatabaseHelper.connection();
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				String query = "update infant_attendance set PRESENT=? where INFANT_ATTENDANCE_ID=?";
				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				JSONArray authorObject = jObj.getJSONArray("value");
				for (int i = 0; i < authorObject.length(); i++) {
					JSONObject c = authorObject.getJSONObject(i);
					String attendanceId = c.getString("attendanceId");
					String presentStatus = c.getString("presentStatus");
					ps.setString(1, presentStatus);
					ps.setString(2, attendanceId);
					hmAtt.put(attendanceId, presentStatus);
					ps.addBatch();
				}
				ps.executeBatch();
				con.commit();
			}
			Logs.writeTeacherLog("updateAttendance","","","","",hmAtt,"","","");
		} catch (Exception e) {
			e.printStackTrace();
			Logs.writeLog(e);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@POST
	@Path("postTeacherNote2Infant")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void postTeacherNote2Infant(MultivaluedMap<String, String> val) {
		try {
			String teacherUserId = val.getFirst("teacherUserId");
			String infantId = val.getFirst("infantId");
			String note = val.getFirst("note");
			con = DatabaseHelper.connection();
			String query = "INSERT INTO infant_notes (TEACHER_ACCOUNT_DETAILS_ID,INFANT_ACCOUNT_DETAILS_ID,NOTES,T_RECEIVE,T_SENT,P_RECEIVE,P_SENT,DATE_TIME )\r\n"
					+ "VALUES(?,?,?,0,1,1,0,now())";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, teacherUserId);
			ps.setString(2, infantId);
			ps.setString(3, note);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			Logs.writeLog(e);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private String returnValue(ResultSet rs) {
		String returnValueToClient = "";
		try {
			if (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				returnValueToClient = "<value>";
				do {
					returnValueToClient += "<row>";
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						returnValueToClient += " <value" + (i - 1) + "> ";
						returnValueToClient += rs.getString(i);
						returnValueToClient += " </value" + (i - 1) + "> ";
					}
					returnValueToClient += "</row>";
				} while (rs.next());
				returnValueToClient += "</value>";
				return returnValueToClient;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnNothing;
	}
}
