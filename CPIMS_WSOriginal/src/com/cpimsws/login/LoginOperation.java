package com.cpimsws.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import com.cpimsws.appcode.Constant;
import com.cpimsws.appcode.DatabaseHelper;
import com.cpimsws.appcode.Logs;
import com.cpimsws.appcode.SendEmail;

@Path("LoginOperation")
public class LoginOperation {

	private final String returnNothing = "<return>" + "<row>" + "<value0>"
			+ "false" + "</value0>" + "</row>" + "</return>";

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String respondAsReady() {
		return "Demo service is ready!";
	}

	@POST
	@Path("loginAuthentication")
	@Produces(MediaType.TEXT_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String teacherLogin(MultivaluedMap<String, String> params){
		String userName=params.getFirst("userName");
		String password=params.getFirst("password");
		try{
			Connection con = DatabaseHelper.connection();
			String query = "SELECT \r\n " + 
					"  CASE CL.USER_TYPE_ID WHEN '1' THEN '1'WHEN '5' THEN'5' ELSE '0' END AS 'USER_TYPE_ID', \r\n " + 
					"  CL.CANDIDATE_ID,   \r\n " + 
					"  CASE CL.USER_TYPE_ID \r\n " + 
					"     WHEN '1' THEN\r\n" + 
					"       (SELECT CONCAT(PAD.FIRST_NAME,' ',PAD.LAST_NAME) AS 'Name' FROM parent_account_details AS PAD WHERE PAD.PARENT_ACCOUNT_DETAILS_ID=CL.CANDIDATE_ID) \r\n " + 
					"     WHEN '5' THEN\r\n" + 
					"       (SELECT CONCAT(TAD.FIRST_NAME,' ',TAD.LAST_NAME) AS 'Name' FROM teacher_account_details AS TAD WHERE TAD.TEACHER_ACCOUNT_DETAILS_ID=CL.CANDIDATE_ID) \r\n " + 
					"  END AS 'NAME' \r\n " + 
					"  FROM candidate_login AS CL \r\n " + 
					"  WHERE binary CL.LOGIN_ID=? " +
					" AND binary CL.PASSWORD=? ";
			PreparedStatement ps=con.prepareStatement(query);
			ps.setString(1, userName);
			ps.setString(2, password);
			ResultSet rs =ps.executeQuery();
			return returnValue(rs);
		}catch(Exception exc){
			exc.printStackTrace();
			Logs.writeLog(exc);
		}
		return Constant.returnNothing;
	}


	@POST
	@Path("changePassword")
	@Produces(MediaType.TEXT_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void changePassword(MultivaluedMap<String, String> params){
		String teacherId=params.getFirst("teacherId");
		String password=params.getFirst("password");
		try{
			Connection con = DatabaseHelper.connection();
			String query = "UPDATE candidate_login SET PASSWORD=? WHERE CANDIDATE_ID=?";
			PreparedStatement ps=con.prepareStatement(query);
			ps.setString(1, password);
			ps.setString(2, teacherId);
			ps.execute();
		}catch(Exception exc){
			exc.printStackTrace();
			Logs.writeLog(exc);
		}
	}

	@POST
	@Path("forgotPassword")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void forgotPassword(MultivaluedMap<String, String> params){
		String email=params.getFirst("email");
		String query="SELECT\r\n" + 
				"CL.PASSWORD\r\n" + 
				"FROM candidate_login AS CL\r\n" + 
				"INNER JOIN teacher_account_details AS TAD\r\n" + 
				"ON CL.CANDIDATE_ID=TAD.TEACHER_ACCOUNT_DETAILS_ID\r\n" + 
				"LEFT JOIN teacher_email_details AS TED\r\n" + 
				"ON TAD.TEACHER_ACCOUNT_DETAILS_ID = TED.TEACHER_ACCOUNT_DETAILS_ID\r\n" + 
				"WHERE TED.EMAIL_ID=?";
		try{
			Connection con = DatabaseHelper.connection();
			PreparedStatement ps=con.prepareStatement(query);
			ps.setString(1, email);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				String password= rs.getString(1);
				String from = "";
				String to = email;
				String cc= "";
				String body = "CPIMS Authentication Info : \n Email Id :"
						+ email + "\n Password :" + password;
				String subject = "CPIMS Password Request ";
				SendEmail.sendMail(from, to,cc, subject, body);
			}
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}

	private String returnValue(ResultSet rs){
		String returnValueToClient="";
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