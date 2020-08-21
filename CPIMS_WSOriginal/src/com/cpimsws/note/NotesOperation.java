package com.cpimsws.note;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import com.cpimsws.appcode.DatabaseHelper;
import com.cpimsws.appcode.Logs;

@Path("NotesOperation")
public class NotesOperation {

	private Connection con = null;
	private final String returnNothing = "<return>" + "<row>" + "<value0>"
			+ "false" + "</value0>" + "</row>" + "</return>";

	@GET
	@Produces(MediaType.TEXT_XML)
	public String respondAsReady() {
		return returnNothing;
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
			String query = "INSERT INTO infant_notes (TEACHER_ACCOUNT_DETAILS_ID,INFANT_ACCOUNT_DETAILS_ID,NOTES,T_RECEIVE,T_SENT,P_RECEIVE,P_SENT,DATE_TIME,PARENT_ACCOUNT_DETAILS_ID )\r\n"
					+ "VALUES(?,?,?,0,1,1,0,now(),(SELECT PIR.PARENT_ACCOUNT_DETAILS_ID FROM parent_infant_relation AS PIR WHERE PIR.INFANT_ACCOUNT_DETAILS_ID=? LIMIT 1))";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, teacherUserId);
			ps.setString(2, infantId);
			ps.setString(3, note);
			ps.setString(4, infantId);
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


	@POST
	@Path("postParentNote2Teacher")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void postParentNote2Teacher(MultivaluedMap<String, String> val) {
		try {
			String parentId = val.getFirst("parentId");
			String infantId = val.getFirst("infantId");
			String note = val.getFirst("note");
			con = DatabaseHelper.connection();

			String query ="DELETE FROM INFANT_NOTES WHERE INFANT_ACCOUNT_DETAILS_ID =" + infantId + 
					" and DATE_FORMAT(DATE_TIME,'%Y-%m-%d') = DATE_FORMAT(now(),'%Y-%m-%d')";
			query = "INSERT INTO INFANT_NOTES (\r\n" + 
					"TEACHER_ACCOUNT_DETAILS_ID,T_SENT,T_RECEIVE,INFANT_ACCOUNT_DETAILS_ID,PARENT_ACCOUNT_DETAILS_ID,P_SENT,P_RECEIVE,NOTES,DATE_TIME)\r\n" + 
					"VALUES(\r\n" + 
					"(SELECT TEACHER_ACCOUNT_DETAILS_ID FROM teacher_class_relation AS TCR WHERE TCR.CLASSROOM_ID = \r\n" + 
					"(SELECT Classroom_Id FROM enrollment AS ENROLL WHERE ENROLL.INFANT_ACCOUNT_DETAILS_ID=? limit 1) limit 1),\r\n" + 
					"0,1,?,?,1,0,?,now());";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infantId);
			ps.setString(2, infantId);
			ps.setString(3, parentId);
			ps.setString(4, note);
			ps.execute();
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


	@GET
	@Path("inboxNotesAndSupply/{infantId}/{date}")
	@Produces(MediaType.TEXT_XML)
	public String inboxNotesAndSupply(@PathParam("infantId") String infantId,@PathParam("date") String date) {
		try {
			con = DatabaseHelper.connection();
			String query = "SELECT\r\n" + 
					"'NOTES' AS TYPE,\r\n" + 
					"INOTES.NOTES\r\n" + 
					"FROM infant_notes AS INOTES\r\n" + 
					"WHERE INOTES.INFANT_ACCOUNT_DETAILS_ID=?\r\n" + 
					"AND INOTES.P_RECEIVE=1\r\n" + 
					"AND DATE_FORMAT(INOTES.DATE_TIME,'%Y-%m-%d')=?\r\n" + 
					"UNION\r\n" + 
					"SELECT \r\n" + 
					"'SUPPLY' AS TYPE,\r\n" + 
					"SN.SUPPLIES_NEEDED\r\n" + 
					"FROM supplies_needed AS SN\r\n" + 
					"WHERE SN.INFANT_ACCOUNT_DETAILS_ID=?\r\n" + 
					"AND DATE_FORMAT(SN.`DATETIME` ,'%Y-%m-%d')=?";
			PreparedStatement ps=con.prepareStatement(query);
			ps.setString(1, infantId);
			ps.setString(2, date);
			ps.setString(3, infantId);
			ps.setString(4, date);
			ResultSet rs = ps.executeQuery();
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
	@Path("inbox/{teacherId}")
	@Produces(MediaType.TEXT_XML)
	public String inbox(@PathParam("teacherId") String teacherId) {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "SELECT\r\n" +
					"INOTS.INFANT_NOTES_ID, " + 
					"(SELECT GROUP_CONCAT(CONCAT(FIRST_NAME,' ',LAST_NAME)  SEPARATOR ',' ) AS PARENT FROM parent_account_details WHERE PARENT_ACCOUNT_DETAILS_ID IN (SELECT PARENT_ACCOUNT_DETAILS_ID FROM parent_infant_relation WHERE INFANT_ACCOUNT_DETAILS_ID=INOTS.INFANT_ACCOUNT_DETAILS_ID)) AS PARENT_NAME,\r\n" + 
					"INOTS.NOTES,\r\n" + 
					"DATE_FORMAT(INOTS.DATE_TIME,'%d-%M-%Y') as 'DATE',\r\n" + 
					"DATE_FORMAT(INOTS.DATE_TIME,'%r') 'TIME',\r\n" + 
					"CONCAT(IAD.FIRST_NAME,' ',IAD.LAST_NAME) AS 'STUDENT_NAME',(SELECT NAME FROM classroom WHERE CLASSROOM_ID=ENROL.Classroom_Id) AS CLASS_NAME," +
					"(SELECT SCHOOL_BRANCH_NAME FROM SCHOOL_INFO WHERE SCHOOL_INFO_ID=(SELECT SCHOOL_INFO_ID FROM classroom WHERE classroom.CLASSROOM_ID = ENROL.Classroom_Id)) AS LOCATION," +
					"(SELECT PROGRAM_NAME FROM PROGRAM WHERE PROGRAM_ID=ENROL.PROGRAM_ID) AS PROGRAM_NAME, " +
					" INOTS.READ_STATUS " +
					" FROM infant_notes AS INOTS\r\n" + 
					"inner join infant_account_details as IAD " +
					"ON IAD.INFANT_ACCOUNT_DETAILS_ID = INOTS.INFANT_ACCOUNT_DETAILS_ID " +
					"INNER JOIN enrollment AS ENROL ON INOTS.INFANT_ACCOUNT_DETAILS_ID = ENROL.INFANT_ACCOUNT_DETAILS_ID \r\n" + 
					" WHERE\r\n" + 
					"DATE_FORMAT(INOTS.DATE_TIME,'%Y/%m/%d')  between DATE_SUB( date_format(now(),'%Y/%m/%d'), INTERVAL 1 MONTH) AND date_format(now(),'%Y/%m/%d')\r\n" + 
					"AND INOTS.T_RECEIVE!=''\r\n" + 
					"AND INOTS.TEACHER_ACCOUNT_DETAILS_ID = '"+teacherId+"' " +
					"ORDER BY INOTS.INFANT_NOTES_ID DESC";
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
	@Path("updateReadStatus/{notesId}")
	@Produces(MediaType.TEXT_XML)
	public void updateReadStatus(@PathParam("notesId") String notesId) {
		try {
			con = DatabaseHelper.connection();
			String query = "UPDATE infant_notes AS INOT SET INOT.READ_STATUS=1 WHERE INOT.INFANT_NOTES_ID=?";
			PreparedStatement ps=con.prepareStatement(query);
			ps.setInt(1, Integer.parseInt(notesId));
			ps.execute();
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
	}


	@GET
	@Path("sent/{teacherId}")
	@Produces(MediaType.TEXT_XML)
	public String sent(@PathParam("teacherId") String teacherId) {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "SELECT\r\n" +
					"INOTS.INFANT_NOTES_ID," + 
					"(SELECT GROUP_CONCAT(CONCAT(FIRST_NAME,' ',LAST_NAME)  SEPARATOR ',' ) AS PARENT FROM parent_account_details WHERE PARENT_ACCOUNT_DETAILS_ID IN (SELECT PARENT_ACCOUNT_DETAILS_ID FROM parent_infant_relation WHERE INFANT_ACCOUNT_DETAILS_ID=INOTS.INFANT_ACCOUNT_DETAILS_ID)) AS PARENT_NAME,\r\n" + 
					"INOTS.NOTES,\r\n" + 
					"DATE_FORMAT(INOTS.DATE_TIME,'%d-%M-%Y') as 'DATE',\r\n" + 
					"DATE_FORMAT(INOTS.DATE_TIME,'%r') 'TIME',\r\n" +
					"CONCAT(IAD.FIRST_NAME,' ',IAD.LAST_NAME) AS 'STUDENT_NAME'," + 
					"(SELECT NAME FROM classroom WHERE CLASSROOM_ID=ENROL.Classroom_Id) AS CLASS_NAME,\r\n" + 
					"(SELECT SCHOOL_BRANCH_NAME FROM SCHOOL_INFO WHERE SCHOOL_INFO_ID=(SELECT SCHOOL_INFO_ID FROM classroom WHERE classroom.CLASSROOM_ID = ENROL.Classroom_Id)) AS LOCATION,\r\n" + 
					"(SELECT PROGRAM_NAME FROM PROGRAM WHERE PROGRAM_ID=ENROL.PROGRAM_ID) AS PROGRAM_NAME\r\n" + 
					"FROM infant_notes AS INOTS\r\n" + 
					"inner join infant_account_details as IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = INOTS.INFANT_ACCOUNT_DETAILS_ID \r\n" + 
					"INNER JOIN enrollment AS ENROL ON INOTS.INFANT_ACCOUNT_DETAILS_ID = ENROL.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"WHERE\r\n" + 
					"DATE_FORMAT(INOTS.DATE_TIME,'%Y/%m/%d')  between DATE_SUB( date_format(now(),'%Y/%m/%d'), INTERVAL 1 MONTH) AND date_format(now(),'%Y/%m/%d')\r\n" + 
					"AND INOTS.T_SENT!=''\r\n" + 
					"AND INOTS.TEACHER_ACCOUNT_DETAILS_ID = '" + teacherId + "' " +
					"ORDER BY INOTS.INFANT_NOTES_ID DESC;";
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
	@Path("viewParticularInfantNotes/{infantId}")
	@Produces(MediaType.TEXT_XML)
	public String viewParticularInfantNotes(@PathParam("infantId") String infantId) {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "SELECT \r\n" + 
					"  INFANT_NOTES_ID,\r\n" + 
					"  NOTES,\r\n" + 
					"  DATE_FORMAT(INOTS.DATE_TIME,'%d-%M-%Y')  as 'date',\r\n" + 
					"  IF(T_RECEIVE=1,'TRUE','FALSE') AS 'STATUS' \r\n" + 
					"  FROM infant_notes AS INOTS\r\n" + 
					"  WHERE \r\n" + 
					"  INOTS.INFANT_ACCOUNT_DETAILS_ID ="+infantId+" \r\n" + 
					"  AND \r\n" + 
					"  DATE_FORMAT(INOTS.DATE_TIME,'%Y/%m/%d')  between DATE_SUB( date_format(now(),'%Y/%m/%d'), INTERVAL 6 MONTH) AND date_format(now(),'%Y/%m/%d')\r\n" + 
					"  ORDER BY INOTS.DATE_TIME DESC";
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
