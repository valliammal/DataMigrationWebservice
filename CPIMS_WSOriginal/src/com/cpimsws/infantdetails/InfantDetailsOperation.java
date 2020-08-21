package com.cpimsws.infantdetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.cpimsws.appcode.Constant;
import com.cpimsws.appcode.DatabaseHelper;
import com.cpimsws.appcode.Logs;


@Path("InfantOperation")
public class InfantDetailsOperation {

	private final String returnNothing = "<return>" + "<row>" + "<value0>"
			+ "false" + "</value0>" + "</row>" + "</return>";


	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String respondAsReady() {
		return "Demo service is ready!";
	}

	@GET
	@Path("infantDetails/{infantId}")
	@Produces(MediaType.TEXT_XML)
	public String getInfantDetails(@PathParam("infantId") String infantId){
		try{
			Connection con = DatabaseHelper.connection();
			String query = "select \r\n" + 
					"CONCAT(IAD.FIRST_NAME,' ',IAD.LAST_NAME) as 'Student_Name',\r\n" + 
					"if(IAD.NICKNAME is not null,IAD.NICKNAME,'') AS 'NICK_NAME',\r\n" + 
					"DATE_FORMAT(IAD.DOB,'%d/%m/%Y') as 'S_DOB' ,\r\n" + 
					"if(IAD.PROFILE_PICTURE is not null,IAD.PROFILE_PICTURE,'') AS 'STUDENT_PICS',\r\n" + 
					"IF(IAD.GENDER='F','Female','Male') AS 'GENDER',\r\n" + 
					"if(IAD.STATUS=1,'Active','In Active') AS 'STATUS',\r\n" + 
					"DATE_FORMAT(IAD.ENROLMENT_DATE,'%d/%m/%Y') as 'ENROLMENT_DATE',\r\n" + 
					"if(IAD.CREATION_DATE is not null,DATE_FORMAT(IAD.CREATION_DATE,'%d/%m/%Y'),'') AS 'CREATION_DATE',\r\n" + 
					"if(IAD.TERMINATION_DATE is not null,DATE_FORMAT(IAD.TERMINATION_DATE,'%d/%m/%Y'),'') AS 'TERMINATION_DATE',\r\n" + 
					"CONCAT(PAD.FIRST_NAME,' ',PAD.LAST_NAME) AS 'FATHER_NAME',\r\n" + 
					"PAD.OCCUPATION,PAD.PROFILE_PICTURE AS 'FATHER_PICS',\r\n" + 
					"PAD.STREET_ADDRESS1,\r\n" + 
					"if(PAD.STREET_ADDRESS2 is not null,PAD.STREET_ADDRESS2,'') AS 'STREET_ADDRESS2',\r\n" + 
					"(SELECT STATE FROM state WHERE state.STATE_ID =PAD.STATE_ID) AS 'STATE',\r\n" + 
					"(SELECT CITY FROM CITY WHERE city.CITY_ID =PAD.CITY_ID) AS 'CITY',\r\n" + 
					"IAI.IS_TOILET_TRAINED, \r\n" + 
					"IAI.IS_ON_BREAKFAST_PLAN,\r\n" + 
					"IAI.IS_ON_WAITING_LIST\r\n" + 
					"from infant_account_details as IAD \r\n" + 
					"left join infant_additional_info as IAI on IAD.INFANT_ACCOUNT_DETAILS_ID = IAI.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"inner join parent_account_details as PAD on PAD.PARENT_ACCOUNT_DETAILS_ID = IAD.PARENT_ACCOUNT_DETAILS_ID\r\n" + 
					"WHERE IAD.INFANT_ACCOUNT_DETAILS_ID="+infantId;			
			Statement stmt=con.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			return returnValue(rs);
		}catch(Exception exc){
			exc.printStackTrace();
			Logs.writeLog(exc);
		}
		return Constant.returnNothing;
	}

	@GET
	@Path("changeToiletTrainedStatus/{infantId}")
	@Produces(MediaType.TEXT_XML)
	public void changeToiletTrainedStatus(@PathParam("infantId") String infantId){
		try{
			Connection con = DatabaseHelper.connection();
			String query = "UPDATE infant_additional_info SET IS_TOILET_TRAINED=ABS(IS_TOILET_TRAINED - 1) WHERE INFANT_ACCOUNT_DETAILS_ID=?";			
			PreparedStatement ps=con.prepareStatement(query);
			ps.setString(1, infantId);
			ps.execute();
		}catch(Exception exc){
			exc.printStackTrace();
			Logs.writeLog(exc);
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