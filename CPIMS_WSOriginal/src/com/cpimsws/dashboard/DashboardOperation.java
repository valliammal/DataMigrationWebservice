package com.cpimsws.dashboard;

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

import org.json.JSONArray;
import org.json.JSONObject;

import com.cpimsws.appcode.DatabaseHelper;
import com.cpimsws.appcode.Logs;
import com.cpimsws.appcode.SendEmail;

@Path("DashboardOperation")
public class DashboardOperation {

	private Connection con = null;
	private final String returnNothing = "<return>" + "<row>" + "<value0>"
			+ "false" + "</value0>" + "</row>" + "</return>";

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String respondAsReady() {
		return "Demo service is ready!";
	}


	@GET
	@Path("getParentStudentDetails/{parentId}/{date}")
	@Produces(MediaType.TEXT_XML)
	public String getParentStudentDetails(@PathParam("parentId") String parentId,@PathParam("date") String date) {
		try {
			con = DatabaseHelper.connection();
			String query = "SELECT \r\n" + 
					"   PIR.INFANT_ACCOUNT_DETAILS_ID as 'infantId',\r\n" + 
					"   ENROLL.Enrollment_Id as 'Enrollment Id',\r\n" + 
					"   (select concat(IAD.FIRST_NAME,' ',IAD.LAST_NAME) from infant_account_details as IAD where IAD.INFANT_ACCOUNT_DETAILS_ID = PIR.INFANT_ACCOUNT_DETAILS_ID) as 'Student Name',\r\n" + 
					"   ICD.SCHOOL_INFO_ID as 'School Id',\r\n" + 
					"   (select SI.SCHOOL_BRANCH_NAME from school_info as SI where SI.SCHOOL_INFO_ID = ICD.SCHOOL_INFO_ID) as 'School Name',\r\n" + 
					"   ICD.CLASSROOM_ID as 'Classroom Id',\r\n" + 
					"   (select NAME from classroom as CR where CR.CLASSROOM_ID = ICD.CLASSROOM_ID) as 'Class Name',\r\n" + 
					"   (select \r\n" + 
					"   CONCAT('(Age ',\r\n" + 
					"   year(now())-year(dob) , ' years ',\r\n" + 
					"   month(now())-month(dob) , ' months ',\r\n" + 
					"   ROUND((day(now())-day(dob))/7), ' weeks)') as 'age'\r\n" + 
					"   from infant_account_details\r\n" + 
					"   where INFANT_ACCOUNT_DETAILS_ID=PIR.INFANT_ACCOUNT_DETAILS_ID) AS AGE\r\n" + 
					"   from PARENT_ACCOUNT_DETAILS as PAD\r\n" + 
					"	inner join parent_infant_relation as PIR\r\n" + 
					"	on PAD.PARENT_ACCOUNT_DETAILS_ID = PIR.PARENT_ACCOUNT_DETAILS_ID\r\n" + 
					"	inner join infant_course_details as ICD\r\n" + 
					"	on PIR.INFANT_ACCOUNT_DETAILS_ID = ICD.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"   INNER JOIN enrollment as ENROLL \r\n" + 
					"   ON ENROLL.INFANT_ACCOUNT_DETAILS_ID = PIR.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					" 	WHERE PAD.PARENT_ACCOUNT_DETAILS_ID=?\r\n" + 
					"   AND ? BETWEEN DATE_FORMAT( ENROLL.Start_Date, '%Y-%m-%d') AND DATE_FORMAT( ENROLL.End_Date, '%Y-%m-%d')";
			System.out.println(query);
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, parentId);
			ps.setString(2, date);
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


	@POST
	@Path("parentCheckin")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public String parentCheckin(MultivaluedMap<String, String> val) {

		String infant_id =val.getFirst("infant_id");
		String last_ate_date = val.getFirst("last_ate_date");
		String last_ate_food_id = val.getFirst("last_ate_food_id");
		String last_ate_amount_id = val.getFirst("last_ate_amount_id");
		String last_ate_notes = val.getFirst("last_ate_notes");
		String last_diaper_date = val.getFirst("last_diaper_date");
		String last_diaper_id = val.getFirst("last_diaper_id");
		String last_nap_wake_date = val.getFirst("last_nap_wake_date");
		String last_nap_duration = val.getFirst("last_nap_duration");
		String date = val.getFirst("date");
		String giv_datetime = val.getFirst("giv_datetime");
		String med_id = val.getFirst("med_id");
		String med_amount_id = val.getFirst("med_amount_id");
		String reason_id = val.getFirst("reason_id");
		String med_notes = val.getFirst("med_notes");
		try {

			con = DatabaseHelper.connection();
			String query="Select COUNT(*) from studentcheckin AS CHKIN WHERE CHKIN.INFANT_ACCOUNT_DETAILS_ID=? AND DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')=? AND CHKIN.STATUS=1";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infant_id);
			ps.setString(2, date);
			ResultSet rs = ps.executeQuery();
			rs.next();

			if(!rs.getString(1).equalsIgnoreCase("1")){

				//Checkout
				String queryStudentCheckout = "insert into studentcheckout(INFANT_ACCOUNT_DETAILS_ID,CheckOutDate) values(?,NOW())";
				PreparedStatement psCheckOut = con.prepareStatement(queryStudentCheckout);
				psCheckOut.setString(1, infant_id);
				psCheckOut.execute();

				//Check In
				String queryStudentCheckin = "insert into studentcheckin(INFANT_ACCOUNT_DETAILS_ID,LastAteDate,LastAteInfantFoodId,LastAteAmountId,LastAteOther,LastDiaperedDate,LastDiaperTypeId,LastNapWakeDate,LastNapDuration,LastMedicItemId,LastMediAmountId,LastMediReasonId,LastMediGivenTime,LastMedicationOther,CheckInDate) " +
						"				values(?,(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),if(? = 0 ,null, ?),if(? = 0 ,null, ?),?,(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),if(? = 0 ,null, ?),(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),if(? = 0 ,null, ?),if(? = 0 ,null, ?),if(? = 0 ,null, ?),(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),?,NOW())";
				PreparedStatement psCheckin = con.prepareStatement(queryStudentCheckin);
				psCheckin.setString(1, infant_id);
				psCheckin.setString(2, last_ate_date);
				psCheckin.setString(3, last_ate_food_id);
				psCheckin.setString(4, last_ate_food_id);
				psCheckin.setString(5, last_ate_amount_id);
				psCheckin.setString(6, last_ate_amount_id);
				psCheckin.setString(7, last_ate_notes);
				psCheckin.setString(8, last_diaper_date);
				psCheckin.setString(9, last_diaper_id);
				psCheckin.setString(10, last_diaper_id);
				psCheckin.setString(11, last_nap_wake_date);
				psCheckin.setString(12, last_nap_duration);
				psCheckin.setString(13, med_id);
				psCheckin.setString(14, med_id);
				psCheckin.setString(15, med_amount_id);
				psCheckin.setString(16, med_amount_id);
				psCheckin.setString(17, reason_id);
				psCheckin.setString(18, reason_id);
				psCheckin.setString(19, giv_datetime);
				psCheckin.setString(20, med_notes);
				psCheckin.execute();
				return "<return>" + "<row>" + "<value0>"+ "true" + "</value0>" + "</row>" + "</return>";
			} else {
				return returnNothing;
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


	@GET
	@Path("parentCheckOut/{infant_id}")
	@Produces(MediaType.TEXT_XML)
	public void parentCheckOut(@PathParam("infant_id") String infant_id) {
		try {
			con = DatabaseHelper.connection();
			String queryStudentCheckout = "insert into studentcheckout(INFANT_ACCOUNT_DETAILS_ID,CheckOutDate) values(?,NOW())";
			PreparedStatement psCheckOut = con.prepareStatement(queryStudentCheckout);
			psCheckOut.setString(1, infant_id);
			psCheckOut.execute();
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
	@Path("dashboardDetails/{infant_id}/{date}")
	@Produces(MediaType.TEXT_XML)
	public String dashboardDetails(@PathParam("infant_id") String infant_id,@PathParam("date") String date){
		try {
			con = DatabaseHelper.connection();
			String query = " select \r\n" + 
					"'Food',\r\n" + 
					"IF(IGF.Expected_Time IS NULL,'',DATE_FORMAT(IGF.Expected_Time,'%d-%b-%Y (%H:%i)')) AS 'EXP_TIME',\r\n" + 
					"DATE_FORMAT(IGF.Given_Time,'%d-%b-%Y (%H:%i)') AS 'GIVEN_TIME',\r\n" + 
					"(SELECT Name FROM infantfood WHERE infantfood.InfantFoodId =IGF.Food_Id) AS 'FOOD_NAME',\r\n" + 
					"(SELECT Name FROM unitofmeasurevalue AS UMV WHERE UMV.UnitOfMeasureValueId=IGF.Food_Amount_Id) AS 'FOOD_AMOUNT',\r\n" + 
					"'',\r\n" + 
					"IGF.Notes\r\n" + 
					"FROM infant_given_food AS IGF\r\n" + 
					"INNER JOIN studentcheckout AS STUCHK ON STUCHK.StudentCheckOutId = IGF.Student_Check_OutId\r\n" + 
					"INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = "+infant_id+" \r\n" + 
					"AND DATE_FORMAT(IGF.Given_Time,'%Y-%m-%d')=?\r\n" + 
					"\r\n" + 
					"UNION\r\n" + 
					"\r\n" + 
					"SELECT 'Diaper Change',\r\n" + 
					"DATE_FORMAT(TimeChanged,'%d-%b-%Y (%H:%i)'),\r\n" + 
					"(SELECT Name FROM unitofmeasurevalue AS UMV WHERE UMV.UnitOfMeasureValueId=ID.Diaper_Type_Id LIMIT 1),\r\n" + 
					"'','','',\r\n" + 
					"ID.Notes\r\n" + 
					"FROM infant_diaper AS ID \r\n" + 
					"INNER JOIN studentcheckout AS STUCHK ON STUCHK.StudentCheckOutId = ID.Student_Check_Out_Id\r\n" + 
					"INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"WHERE IAD.INFANT_ACCOUNT_DETAILS_ID =" + infant_id + "  \r\n" + 
					"AND DATE_FORMAT(ID.TimeChanged,'%Y-%m-%d')=?\r\n" + 
					"\r\n" + 
					"UNION\r\n" + 
					"\r\n" + 
					"SELECT \r\n" + 
					"'Nap Time',IF(Expected_Time IS NULL,'',DATE_FORMAT(Expected_Time,'%d-%b-%Y (%H:%i)')) AS 'Expected_Time',IF(Expected_Duration IS NULL,'',DATE_FORMAT(Expected_Duration,'%d-%b-%Y (%H:%i)')) AS 'DURATION',DATE_FORMAT(Start_Time,'%d-%b-%Y (%H:%i)'),DATE_FORMAT(End_Time,'%d-%b-%Y (%H:%i)'),'',\r\n" + 
					"INAP.Notes\r\n" + 
					"FROM infant_nap AS INAP\r\n" + 
					"INNER JOIN studentcheckout AS STUCHK ON STUCHK.StudentCheckOutId = INAP.Student_Check_Out_Id\r\n" + 
					"INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = " + infant_id + "  \r\n" + 
					"AND DATE_FORMAT(INAP.Start_Time,'%Y-%m-%d')=?\r\n" + 
					"\r\n" + 
					"UNION\r\n" + 
					"SELECT\r\n" + 
					"'Medication',IF(Expected_Time IS NULL,'',DATE_FORMAT(Expected_Time,'%d-%b-%Y (%H:%i)')) AS 'EXP_TIME',DATE_FORMAT(Given_Time,'%d-%b-%Y (%H:%i)'),\r\n" + 
					"(select Name from infant_medication as IM where IM.Infant_Medication_Id =IGM.Medication_Id) as 'MEDICATION',\r\n" + 
					"(select Name from unitofmeasurevalue as UMV where UMV.UnitOfMeasureValueId = IGM.Medication_Amount_Id) AS  'Medication_Amount_Id',\r\n" + 
					"(SELECT name from unitofmeasurevalue as UVM where UVM.UnitOfMeasureValueId = IGM.Reason_Id) AS 'REASON',\r\n" + 
					"IGM.Notes\r\n" + 
					"from infant_given_medication as IGM\r\n" + 
					"INNER JOIN studentcheckout AS STUCHK ON STUCHK.StudentCheckOutId = IGM.Student_Check_Out_Id\r\n" + 
					"INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = " + infant_id + " \r\n" + 
					"AND DATE_FORMAT(IGM.Given_Time,'%Y-%m-%d')=?\r\n" + 
					"\r\n" + 
					"UNION\r\n" + 
					"SELECT 'Activity',\r\n" + 
					"DATE_FORMAT( IAD.ACTIVITY_DATE_TIME,'%d-%b-%Y (%H:%i)'),\r\n" + 
					"'','','',\r\n" + 
					"IAD.ACTIVITY_REASON,\r\n" + 
					"IAD.REMARKS\r\n" + 
					"from infant_activities_details as IAD\r\n" + 
					"WHERE IAD.INFANT_ACCOUNT_DETAILS_ID =" + infant_id + "  \r\n" + 
					"AND DATE_FORMAT(IAD.ACTIVITY_DATE_TIME,'%Y-%m-%d')=?";
			System.out.println(query);
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, date);
			ps.setString(2, date);
			ps.setString(3, date);
			ps.setString(4, date);
			ps.setString(5, date);
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
	@Path("dashboardDetailsExpected/{infant_id}/{date}")
	@Produces(MediaType.TEXT_XML)
	public String dashboardDetailsExpected(@PathParam("infant_id") String infant_id,@PathParam("date") String date){
		try {
			con = DatabaseHelper.connection();
			String query = "SELECT \r\n" + 
					"    'Food' AS 'TYPE',	 IEF.Infant_Expected_FoodId,\r\n" + 
					"    DATE_FORMAT(IEF.Expected_Eat_Time,'%d-%b-%Y (%H:%i)') AS 'EXP_DATE',\r\n" + 
					"    '' AS 'GIVEN_DATE',\r\n" + 
					"    (SELECT Name FROM infantfood WHERE InfantFoodId =IEF.Expected_Food_Id) AS 'FOOD_NAME',\r\n" + 
					"    (SELECT Name FROM unitofmeasurevalue AS UMV WHERE UMV.UnitOfMeasureValueId=IEF.Expected_Food_Amount_Id) AS 'FOOD_AMOUNT',\r\n" + 
					"    '',\r\n" + 
					"    IEF.Notes\r\n" + 
					"    FROM infant_expected_food AS IEF\r\n" + 
					"    INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = IEF.Student_CheckIn_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE \r\n" + 
					"    IAD.INFANT_ACCOUNT_DETAILS_ID =? \r\n" + 
					"    AND IEF.STATUS=1 \r\n"+
					"    AND STUCHK.STATUS=1\r\n" + 
					"    AND DATE_FORMAT(IEF.Expected_Eat_Time,'%Y-%m-%d')=?    \r\n" + 
					"  	\r\n" + 
					"  UNION\r\n" + 
					"    SELECT\r\n" +
					"   'Medi24' AS 'TYPE',	 LHM.last_24HR_MedicationId,\r\n" +
					"	DATE_FORMAT(LHM.LastMediGivenTime,'%d-%b-%Y (%H:%i)') AS 'EXP_DATE',\r\n" +
					"	'' AS 'GIVEN_DATE',\r\n" +
					"	(select Name from infant_medication as IM where IM.Infant_Medication_Id =LHM.LastMedicItemId) as 'MEDICATION',\r\n+" +
					" 	(select Name from unitofmeasurevalue as UMV where UMV.UnitOfMeasureValueId = LHM.LastMediAmountId) AS  'Medication_Amount_Id',\r\n" +
					" 	(SELECT name from unitofmeasurevalue as UVM where UVM.UnitOfMeasureValueId = LHM.LastMediReasonId) AS 'REASON',\r\n" + 
					"   '' Notes FROM  last_24hr_medication AS LHM \r\n" +
					"	INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = LHM.Student_CheckIn_Id \r\n"+
					"	INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID \r\n" +
					"	WHERE \r\n" + "" +
					"	IAD.INFANT_ACCOUNT_DETAILS_ID =? \r\n" +
					"   AND STUCHK.STATUS=1 AND DATE_FORMAT(LHM.LastMediGivenTime,'%Y-%m-%d')=? \r\n" +
					"UNION\r\n" + 
					"  SELECT \r\n" + 
					"    'Nap Time',\r\n" + 
					"	 IEN.Infant_Expected_Nap_Id,\r\n" + 
					"    DATE_FORMAT(IEN.ExpectedNapTime,'%d-%b-%Y (%H:%i)') AS 'EXP_TIME',\r\n" + 
					"    DATE_FORMAT(IEN.ExpectedNapDuration,'%d-%b-%Y (%H:%i)') AS 'EXP_DUR',\r\n" + 
					"    '' AS 'START_TIME',\r\n" + 
					"    ''  AS 'END_TIME',\r\n" + 
					"    '',\r\n" + 
					"    IEN.Notes AS 'NOTES'\r\n" + 
					"    FROM infant_expected_nap AS IEN\r\n" + 
					"    INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = IEN.Student_Check_In_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = ?\r\n" + 
					"    AND STUCHK.STATUS=1\r\n" + 
					"    AND IEN.STATUS=1 \r\n"+
					"    AND (DATE_FORMAT(IEN.ExpectedNapTime,'%Y-%m-%d')=?\r\n" + 
					"    OR DATE_FORMAT(IEN.ExpectedNapDuration,'%Y-%m-%d')=?)\r\n" + 
					"  UNION\r\n" + 
					"    SELECT\r\n" + 
					"    'Medication',\r\n" + 
					"	 IEM.Infant_Expected_Medication_Id,\r\n" + 
					"    DATE_FORMAT(IEM.ExpectedMedicationTime,'%d-%b-%Y (%H:%i)') AS 'EXP_TIME',\r\n" + 
					"    '' AS 'GIV_TIME',\r\n" + 
					"    (select Name from infant_medication as IM where IM.Infant_Medication_Id =IEM.ExpectedMedicationId) as 'MEDICATION',\r\n" + 
					"    (select Name from unitofmeasurevalue as UMV where UMV.UnitOfMeasureValueId = IEM.ExpectedMedicationAmountId) AS  'Medication_Amount_Id',\r\n" + 
					"    (SELECT name from unitofmeasurevalue as UVM where UVM.UnitOfMeasureValueId = IEM.ExpectedMedicationReasonId) AS 'REASON',\r\n" + 
					"    IEM.Notes AS 'NOTES'\r\n" + 
					"    from infant_expected_medication AS IEM\r\n" + 
					"    INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = IEM.Student_Check_In_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = ?\r\n" + 
					"    AND DATE_FORMAT(IEM.ExpectedMedicationTime,'%Y-%m-%d')=? \r\n\r\n" + 
					"	 AND IEM.STATUS=1 \r\n"+ 
					"    AND STUCHK.STATUS=1";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infant_id);
			ps.setString(2, date);
			ps.setString(3, infant_id);
			ps.setString(4, date);
			ps.setString(5, infant_id);
			ps.setString(6, date);
			ps.setString(7, date);
			ps.setString(8, infant_id);
			ps.setString(9, date);
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
	@Path("dashboardDetailsExpectedForTeacher/{infant_id}/{date}")
	@Produces(MediaType.TEXT_XML)
	public String dashboardDetailsExpectedForTeacher(@PathParam("infant_id") String infant_id,@PathParam("date") String date) {
		try {
			con = DatabaseHelper.connection();
			String query = "SELECT \r\n" + 
					"    'Food' AS 'TYPE',  IEF.Infant_Expected_FoodId,\r\n" + 
					"    DATE_FORMAT(IEF.Expected_Eat_Time,'%d-%b-%Y (%H:%i)') AS 'EXP_DATE',\r\n" + 
					"    '' AS 'GIVEN_DATE',\r\n" + 
					"    (SELECT Name FROM infantfood WHERE InfantFoodId =IEF.Expected_Food_Id) AS 'FOOD_NAME',\r\n" + 
					"    (SELECT Name FROM unitofmeasurevalue AS UMV WHERE UMV.UnitOfMeasureValueId=IEF.Expected_Food_Amount_Id) AS 'FOOD_AMOUNT',\r\n" + 
					"    '',\r\n" + 
					"    IEF.Notes\r\n" + 
					"    FROM infant_expected_food AS IEF\r\n" + 
					"    INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = IEF.Student_CheckIn_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE \r\n" + 
					"    IAD.INFANT_ACCOUNT_DETAILS_ID =? \r\n" + 
					"    AND STUCHK.STATUS=1\r\n" + 
					"	 AND IEF.STATUS=1 \r\n"+ 
					"    AND DATE_FORMAT(IEF.Expected_Eat_Time,'%Y-%m-%d')=?   \r\n" + 
					"    AND (SELECT COUNT(*) FROM infant_given_food AS IGF WHERE IGF.Expected_Food_Id = IEF.Infant_Expected_FoodId)=0\r\n" +
					"UNION\r\n" + 
					"  SELECT \r\n" + 
					"    'Nap Time',\r\n" + 
					"    IEN.Infant_Expected_Nap_Id,\r\n" + 
					"    DATE_FORMAT(IEN.ExpectedNapTime,'%d-%b-%Y (%H:%i)') AS 'EXP_TIME',\r\n" + 
					"    DATE_FORMAT(IEN.ExpectedNapDuration,'%d-%b-%Y (%H:%i)') AS 'EXP_DUR',\r\n" + 
					"    '' AS 'START_TIME',\r\n" + 
					"    ''  AS 'END_TIME',\r\n" + 
					"    '',\r\n" + 
					"    IEN.Notes AS 'NOTES'\r\n" + 
					"    FROM infant_expected_nap AS IEN\r\n" + 
					"    INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = IEN.Student_Check_In_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = ?\r\n" + 
					"    AND STUCHK.STATUS=1\r\n" + 
					"	 AND IEN.STATUS=1 \r\n"+ 
					"    AND (SELECT COUNT(*) FROM infant_nap AS INAP WHERE INAP.Expected_Nap_Id=IEN.Infant_Expected_Nap_Id)=0\r\n" + 
					"    AND (\r\n" + 
					"    DATE_FORMAT(IEN.ExpectedNapTime,'%Y-%m-%d')=?\r\n" + 
					"    OR DATE_FORMAT(IEN.ExpectedNapDuration,'%Y-%m-%d')=?)\r\n" + 
					"  \r\n" + 
					"  UNION\r\n" + 
					"    SELECT\r\n" + 
					"    'Medication',\r\n" + 
					"    IEM.Infant_Expected_Medication_Id,\r\n" + 
					"    DATE_FORMAT(IEM.ExpectedMedicationTime,'%d-%b-%Y (%H:%i)') AS 'EXP_TIME',\r\n" + 
					"    '' AS 'GIV_TIME',\r\n" + 
					"    (select Name from infant_medication as IM where IM.Infant_Medication_Id =IEM.ExpectedMedicationId) as 'MEDICATION',\r\n" + 
					"    (select Name from unitofmeasurevalue as UMV where UMV.UnitOfMeasureValueId = IEM.ExpectedMedicationAmountId) AS  'Medication_Amount_Id',\r\n" + 
					"    (SELECT name from unitofmeasurevalue as UVM where UVM.UnitOfMeasureValueId = IEM.ExpectedMedicationReasonId) AS 'REASON',\r\n" + 
					"    IEM.Notes AS 'NOTES'\r\n" + 
					"    from infant_expected_medication AS IEM\r\n" + 
					"    INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = IEM.Student_Check_In_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = ?\r\n" + 
					"    AND DATE_FORMAT(IEM.ExpectedMedicationTime,'%Y-%m-%d')=?     \r\n" + 
					"    AND STUCHK.STATUS=1\r\n" + 
					"	 AND IEM.STATUS=1 \r\n" + 
					"    AND (SELECT COUNT(*) FROM infant_given_medication AS IGM WHERE IGM.Expected_Medication_Id=IEM.Infant_Expected_Medication_Id)=0"; 
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infant_id);
			ps.setString(2, date);
			ps.setString(3, infant_id);
			ps.setString(4, date);
			ps.setString(5, date);
			ps.setString(6, infant_id);
			ps.setString(7, date);
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
	@Path("dashboardDetailsExpectedForTeacherPrevious/{infant_id}/{date}")
	@Produces(MediaType.TEXT_XML)
	public String dashboardDetailsExpectedForTeacherPrevious(@PathParam("infant_id") String infant_id,@PathParam("date") String date) {
		try {
			con = DatabaseHelper.connection();
			String query = "SELECT \r\n" + 
					"    'Food' AS 'TYPE',  IEF.Infant_Expected_FoodId,\r\n" + 
					"    DATE_FORMAT(IEF.Expected_Eat_Time,'%d-%b-%Y (%H:%i)') AS 'EXP_DATE',\r\n" + 
					"    '' AS 'GIVEN_DATE',\r\n" + 
					"    (SELECT Name FROM infantfood WHERE InfantFoodId =IEF.Expected_Food_Id) AS 'FOOD_NAME',\r\n" + 
					"    (SELECT Name FROM unitofmeasurevalue AS UMV WHERE UMV.UnitOfMeasureValueId=IEF.Expected_Food_Amount_Id) AS 'FOOD_AMOUNT',\r\n" + 
					"    '',\r\n" + 
					"    IEF.Notes\r\n" + 
					"    FROM infant_expected_food AS IEF\r\n" + 
					"    INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = IEF.Student_CheckIn_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE \r\n" + 
					"    IAD.INFANT_ACCOUNT_DETAILS_ID =? \r\n" + 
					"    AND STUCHK.STATUS=1\r\n" + 
					"	 AND IEF.STATUS=1 \r\n"+ 
					"    AND DATE_FORMAT(IEF.Expected_Eat_Time,'%Y-%m-%d')=?   \r\n" + 
					"UNION\r\n" + 
					"  SELECT \r\n" + 
					"    'Nap Time',\r\n" + 
					"    IEN.Infant_Expected_Nap_Id,\r\n" + 
					"    DATE_FORMAT(IEN.ExpectedNapTime,'%d-%b-%Y (%H:%i)') AS 'EXP_TIME',\r\n" + 
					"    DATE_FORMAT(IEN.ExpectedNapDuration,'%d-%b-%Y (%H:%i)') AS 'EXP_DUR',\r\n" + 
					"    '' AS 'START_TIME',\r\n" + 
					"    ''  AS 'END_TIME',\r\n" + 
					"    '',\r\n" + 
					"    IEN.Notes AS 'NOTES'\r\n" + 
					"    FROM infant_expected_nap AS IEN\r\n" + 
					"    INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = IEN.Student_Check_In_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = ?\r\n" + 
					"    AND STUCHK.STATUS=1\r\n" + 
					"	 AND IEN.STATUS=1 \r\n" + 
					"    AND (\r\n" + 
					"    DATE_FORMAT(IEN.ExpectedNapTime,'%Y-%m-%d')=?\r\n" + 
					"    OR DATE_FORMAT(IEN.ExpectedNapDuration,'%Y-%m-%d')=?)\r\n" + 
					"  \r\n" + 
					"  UNION\r\n" + 
					"    SELECT\r\n" + 
					"    'Medication',\r\n" + 
					"    IEM.Infant_Expected_Medication_Id,\r\n" + 
					"    DATE_FORMAT(IEM.ExpectedMedicationTime,'%d-%b-%Y (%H:%i)') AS 'EXP_TIME',\r\n" + 
					"    '' AS 'GIV_TIME',\r\n" + 
					"    (select Name from infant_medication as IM where IM.Infant_Medication_Id =IEM.ExpectedMedicationId) as 'MEDICATION',\r\n" + 
					"    (select Name from unitofmeasurevalue as UMV where UMV.UnitOfMeasureValueId = IEM.ExpectedMedicationAmountId) AS  'Medication_Amount_Id',\r\n" + 
					"    (SELECT name from unitofmeasurevalue as UVM where UVM.UnitOfMeasureValueId = IEM.ExpectedMedicationReasonId) AS 'REASON',\r\n" + 
					"    IEM.Notes AS 'NOTES'\r\n" + 
					"    from infant_expected_medication AS IEM\r\n" + 
					"    INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = IEM.Student_Check_In_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = ?\r\n" + 
					"    AND DATE_FORMAT(IEM.ExpectedMedicationTime,'%Y-%m-%d')=?     \r\n" + 
					"    AND STUCHK.STATUS=1\r\n" + 
					"	 AND IEM.STATUS=1";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infant_id);
			ps.setString(2, date);
			ps.setString(3, infant_id);
			ps.setString(4, date);
			ps.setString(5, date);
			ps.setString(6, infant_id);
			ps.setString(7, date);
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
	@Path("confirmData/{infant_id}")
	@Produces(MediaType.TEXT_XML)
	public String confirmData(@PathParam("infant_id") String infant_id) {
		try {
			con = DatabaseHelper.connection();
			String query = "Select DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d') from studentcheckin AS CHKIN WHERE "+ 
					" CHKIN.INFANT_ACCOUNT_DETAILS_ID=? ORDER BY CHKIN.CheckInDate DESC LIMIT 1";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infant_id);
			ResultSet rs=ps.executeQuery();
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
	@Path("getLastNotes/{infant_id}/{date}")
	@Produces(MediaType.TEXT_XML)
	public String getLastNotes(@PathParam("infant_id") String infant_id, @PathParam("date") String date) {
		try {
			con = DatabaseHelper.connection();
			String query = "SELECT\r\n" + 
					"'LAST' AS 'TYPES',\r\n" + 
					"CHKIN.StudentCheckInId,\r\n" + 
					"CHKIN.LastAteOther,\r\n" + 
					"CHKIN.LastMedicationOther,\r\n" + 
					"'' AS 'NOTES'\r\n" + 
					"FROM studentcheckin AS CHKIN\r\n" + 
					"WHERE CHKIN.INFANT_ACCOUNT_DETAILS_ID=? \r\n" + 
					"AND CHKIN.STATUS=1\r\n" + 
					"AND DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')=? \r\n" + 
					"UNION\r\n" + 
					"SELECT\r\n" + 
					"'FOOD' AS 'TYPES','','','',\r\n" + 
					"IEF.Notes AS 'NOTES'\r\n" + 
					"FROM studentcheckin AS CHKIN\r\n" + 
					"LEFT JOIN infant_expected_food AS IEF\r\n" + 
					"ON CHKIN.StudentCheckInId = IEF.Student_CheckIn_Id\r\n" + 
					"WHERE CHKIN.INFANT_ACCOUNT_DETAILS_ID=? \r\n" + 
					"AND CHKIN.STATUS=1\r\n" + 
					"AND DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')=? \r\n" + 
					"UNION\r\n" + 
					"SELECT\r\n" + 
					"'MEDI' AS 'TYPES','','','',\r\n" + 
					"IEM.Notes AS 'NOTES'\r\n" + 
					"FROM studentcheckin AS CHKIN\r\n" + 
					"LEFT JOIN infant_expected_medication AS IEM\r\n" + 
					"ON CHKIN.StudentCheckInId = IEM.Student_Check_In_Id\r\n" + 
					"WHERE CHKIN.INFANT_ACCOUNT_DETAILS_ID=? \r\n" + 
					"AND CHKIN.STATUS=1\r\n" + 
					"AND DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')=? \r\n" + 
					"UNION \r\n" + 
					"SELECT\r\n" + 
					"'NAP' AS 'TYPES','','','',\r\n" + 
					"IEN.Notes AS 'NOTES'\r\n" + 
					"FROM studentcheckin AS CHKIN\r\n" + 
					"LEFT JOIN infant_expected_nap AS IEN\r\n" + 
					"ON CHKIN.StudentCheckInId = IEN.Student_Check_In_Id\r\n" + 
					"WHERE CHKIN.INFANT_ACCOUNT_DETAILS_ID=? \r\n" + 
					"AND CHKIN.STATUS=1\r\n" + 
					"AND DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')=? \r\n" + 
					"UNION \r\n" + 
					"SELECT \r\n" + 
					"'NOTES' AS 'TYPE','','','',\r\n" + 
					"INOTE.NOTES\r\n" + 
					"FROM infant_notes AS INOTE\r\n" + 
					"WHERE INOTE.INFANT_ACCOUNT_DETAILS_ID =?\r\n" + 
					"AND INOTE.T_RECEIVE=1\r\n" + 
					"AND DATE_FORMAT(INOTE.DATE_TIME,'%Y-%m-%d')=?" ;
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infant_id);
			ps.setString(2, date);
			ps.setString(3, infant_id);
			ps.setString(4, date);
			ps.setString(5, infant_id);
			ps.setString(6, date);
			ps.setString(7, infant_id);
			ps.setString(8, date);
			ps.setString(9, infant_id);
			ps.setString(10, date);
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
	@Path("getLastValuesParent/{infant_id}/{date}")
	@Produces(MediaType.TEXT_XML)
	public String getLastValuesParent(@PathParam("infant_id") String infant_id,@PathParam("date") String date) {
		try {
			con = DatabaseHelper.connection();
			String query = "SELECT\r\n" + 
					"DATE_FORMAT(CHKIN.LastNapWakeDate,'%H:%i') AS 'LastNapWakeDate',\r\n" + 
					"DATE_FORMAT(CHKIN.LastNapDuration,'%H:%i') AS 'LastNapDuration',\r\n" + 
					"DATE_FORMAT(CHKIN.LastAteDate,'%H:%i') AS 'LastAteDate',\r\n" + 
					"(SELECT Name FROM infantfood WHERE InfantFoodId =CHKIN.LastAteInfantFoodId) AS 'FOOD_NAME',\r\n" + 
					"(SELECT Name FROM unitofmeasurevalue AS UMV WHERE UMV.UnitOfMeasureValueId=CHKIN.LastAteAmountId) AS 'FOOD_AMOUNT',\r\n" + 
					"CHKIN.LastAteOther,\r\n" + 
					"DATE_FORMAT(CHKIN.LastDiaperedDate,'%H:%i') AS 'LastDiaperedDate',\r\n" + 
					"(SELECT Name FROM unitofmeasurevalue WHERE UnitOfMeasureValueId=CHKIN.LastDiaperTypeId) AS 'Name',\r\n" + 
					"DATE_FORMAT(CHKIN.LastMediGivenTime,'%H:%i') AS 'LastMediGivenTime',\r\n" + 
					"(select Name from infant_medication as IM where IM.Infant_Medication_Id =CHKIN.LastMedicItemId) as 'MEDICATION',\r\n" + 
					"(select Name from unitofmeasurevalue as UMV where UMV.UnitOfMeasureValueId = CHKIN.LastMediAmountId) AS  'Medication_Amount',\r\n" + 
					"(SELECT name from unitofmeasurevalue as UVM where UVM.UnitOfMeasureValueId = CHKIN.LastMediReasonId) AS 'REASON',\r\n" + 
					"CHKIN.LastMedicationOther,\r\n" + 
					"(SELECT GROUP_CONCAT(INOTE.NOTES SEPARATOR '\n')  FROM infant_notes AS INOTE \r\n" + 
					"WHERE INOTE.INFANT_ACCOUNT_DETAILS_ID =? AND INOTE.P_SENT=1 AND DATE_FORMAT(INOTE.DATE_TIME,'%Y-%m-%d')=? LIMIT 1) AS 'TEACHER_NOTES'\r\n" + 
					"FROM studentcheckin AS CHKIN\r\n" + 
					"WHERE CHKIN.INFANT_ACCOUNT_DETAILS_ID=? \r\n" + 
					"AND CHKIN.STATUS=1\r\n" + 
					"AND DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')=?";
			PreparedStatement ps=con.prepareStatement(query);
			ps.setString(1, infant_id);
			ps.setString(2, date);
			ps.setString(3, infant_id);
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


	@POST
	@Path("insertActivity")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void insertActivity(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String json = val.getFirst("jsonActivityDetails");
		try{
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();
				String query="INSERT INTO infant_activities_details(INFANT_ACCOUNT_DETAILS_ID,ACTIVITY_DATE_TIME, ACTIVITY_REASON, REMARKS) \r\n" + 
						"VALUES(?,(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),?,?);";
				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				JSONArray jsonArray = jObj.getJSONArray("value");
				Logs.writeLogLine(0);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					String activity_datetime = jObjChild.getString("activity_datetime");// 'May 18, 2009 11:59'
					String activity = jObjChild.getString("activity");
					String notes = jObjChild.getString("notes");
					ps.setInt(1, infant_id);
					ps.setString(2, activity_datetime);
					ps.setString(3, activity);
					ps.setString(4, notes);
					Logs.writeLogOper(infant_id + 	"", activity_datetime, activity, notes);
					ps.addBatch();
				}
				Logs.writeLogLine(1);
				ps.executeBatch();
				con.commit();
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
	}


	@POST
	@Path("supplyNeeded")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void supplyNeeded(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String supplyneeded = val.getFirst("supplyneeded");
		String teacherId = val.getFirst("teacherId");
		try{
			con = DatabaseHelper.connection();
			String query = "INSERT INTO supplies_needed(INFANT_ACCOUNT_DETAILS_ID, SUPPLIES_NEEDED, TEACHER_ACCOUNT_DETAILS_ID, DATETIME)\r\n" + 
					"VALUES(?,?,?,NOW());";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, infant_id);
			ps.setString(2, supplyneeded);
			ps.setString(3, teacherId);
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


	@POST
	@Path("viewFoodDetails")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public String viewFoodDetails(MultivaluedMap<String, String> val) {
		String infant_id = val.getFirst("infant_id");
		String date = val.getFirst("date");
		try {
			con = DatabaseHelper.connection();
			String query = "SELECT \r\n" + 
					"    'Food' AS 'TYPE',\r\n" + 
					"    DATE_FORMAT(IEF.Expected_Eat_Time,'%d-%M-%Y (%H:%m)') AS 'EXP_DATE',\r\n" + 
					"    '' AS 'GIVEN_DATE',\r\n" + 
					"    (SELECT Name FROM infantfood WHERE InfantFoodId =IEF.Expected_Food_Id) AS 'FOOD_NAME',\r\n" + 
					"    (SELECT Name FROM unitofmeasurevalue AS UMV WHERE UMV.UnitOfMeasureValueId=IEF.Expected_Food_Amount_Id) AS 'FOOD_AMOUNT',\r\n" + 
					"    '',\r\n" + 
					"    IEF.Notes,\r\n" + 
					"    IEF.SUBMITTED_BY,\r\n" + 
					"    IF(IEF.SUBMITTED_BY = 'Teacher',\r\n" + 
					"        (SELECT CONCAT(TAD.FIRST_NAME,' ',TAD.LAST_NAME) AS 'Name' from teacher_account_details as TAD where TAD.TEACHER_ACCOUNT_DETAILS_ID = IEF.TEACHER_ACCOUNT_DETAILS_ID),\r\n" + 
					"        (SELECT CONCAT(PAD.FIRST_NAME,' ',PAD.LAST_NAME) AS 'Name' from parent_account_details as PAD where PAD.PARENT_ACCOUNT_DETAILS_ID = IEF.PARENT_ACCOUNT_DETAILS_ID)\r\n" + 
					"    ) AS 'Name'\r\n" + 
					"    FROM infant_expected_food AS IEF\r\n" + 
					"    INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = IEF.Student_CheckIn_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE \r\n" + 
					"    IAD.INFANT_ACCOUNT_DETAILS_ID =? \r\n" + 
					"    AND DATE_FORMAT(IEF.Expected_Eat_Time,'%Y/%m/%d')=?    \r\n" + 
					"    UNION\r\n" + 
					"    SELECT \r\n" + 
					"      'Food' AS 'TYPE',\r\n" + 
					"      DATE_FORMAT(IGF.Expected_Time,'%d-%M-%Y (%H:%m)') AS 'EXP_DATE',\r\n" + 
					"      DATE_FORMAT(IGF.Given_Time,'%d-%M-%Y (%H:%m)') AS 'GIVEN_DATE',\r\n" + 
					"      (SELECT Name FROM infantfood WHERE infantfood.InfantFoodId =IGF.Food_Id) AS 'FOOD_NAME',\r\n" + 
					"      (SELECT Name FROM unitofmeasurevalue AS UMV WHERE UMV.UnitOfMeasureValueId=IGF.Food_Amount_Id) AS 'FOOD_AMOUNT',\r\n" + 
					"      '',\r\n" + 
					"      IGF.Notes AS 'NOTES',\r\n" + 
					"      IGF.SUBMITTED_BY,\r\n" + 
					"      IF(IGF.SUBMITTED_BY = 'Teacher',\r\n" + 
					"      (SELECT CONCAT(TAD.FIRST_NAME,' ',TAD.LAST_NAME) AS 'Name' from teacher_account_details as TAD where TAD.TEACHER_ACCOUNT_DETAILS_ID = IGF.TEACHER_ACCOUNT_DETAILS_ID),\r\n" + 
					"      (SELECT CONCAT(PAD.FIRST_NAME,' ',PAD.LAST_NAME) AS 'Name' from parent_account_details as PAD where PAD.PARENT_ACCOUNT_DETAILS_ID = IGF.PARENT_ACCOUNT_DETAILS_ID)\r\n" + 
					"      ) AS 'Name'\r\n" + 
					"      FROM infant_given_food AS IGF\r\n" + 
					"      INNER JOIN studentcheckout AS STUCHK ON STUCHK.StudentCheckOutId = IGF.Student_Check_OutId\r\n" + 
					"      INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"      WHERE \r\n" + 
					"      IAD.INFANT_ACCOUNT_DETAILS_ID =? \r\n" + 
					"      AND DATE_FORMAT(IGF.Expected_Time,'%Y/%m/%d')=?\r\n" + 
					"      OR DATE_FORMAT(IGF.Given_Time,'%Y/%m/%d')=?;";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infant_id);
			ps.setString(2, date);
			ps.setString(3, infant_id);
			ps.setString(4, date);
			ps.setString(5, date);
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


	@POST
	@Path("viewNapDetails")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public String viewNapDetails(MultivaluedMap<String, String> val) {
		String infant_id = val.getFirst("infant_id");
		String date = val.getFirst("date");
		try {
			con = DatabaseHelper.connection();
			String query = "  SELECT \r\n" + 
					"    'Nap Time',\r\n" + 
					"    DATE_FORMAT(IEN.ExpectedNapTime,'%d-%M-%Y (%H:%m)') AS 'EXP_TIME',\r\n" + 
					"    DATE_FORMAT(IEN.ExpectedNapDuration,'%d-%M-%Y (%H:%m)') AS 'EXP_DUR',\r\n" + 
					"    '' AS 'START_TIME',\r\n" + 
					"    ''  AS 'END_TIME',\r\n" + 
					"    '',\r\n" + 
					"    IEN.Notes AS 'NOTES',\r\n" + 
					"    IEN.SUBMITTED_BY,\r\n" + 
					"    IF(IEN.SUBMITTED_BY = 'Teacher',\r\n" + 
					"    (SELECT CONCAT(TAD.FIRST_NAME,' ',TAD.LAST_NAME) AS 'Name' from teacher_account_details as TAD where TAD.TEACHER_ACCOUNT_DETAILS_ID = IEN.TEACHER_ACCOUNT_DETAILS_ID),\r\n" + 
					"    (SELECT CONCAT(PAD.FIRST_NAME,' ',PAD.LAST_NAME) AS 'Name' from parent_account_details as PAD where PAD.PARENT_ACCOUNT_DETAILS_ID = IEN.PARENT_ACCOUNT_DETAILS_ID)\r\n" + 
					"    ) AS 'Name'\r\n" + 
					"    FROM infant_expected_nap AS IEN\r\n" + 
					"    INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = IEN.Student_Check_In_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = ?\r\n" + 
					"    AND \r\n" + 
					"    (DATE_FORMAT(IEN.ExpectedNapTime,'%Y/%m/%d')=?\r\n" + 
					"    OR DATE_FORMAT(IEN.ExpectedNapDuration,'%Y/%m/%d')=?)\r\n" + 
					"    UNION\r\n" + 
					"    SELECT \r\n" + 
					"    'Nap Time',\r\n" + 
					"    DATE_FORMAT(Expected_Time,'%d-%M-%Y (%H:%m)') AS 'EXP_TIME',\r\n" + 
					"    DATE_FORMAT(Expected_Duration,'%d-%M-%Y (%H:%m)') AS 'EXP_DUR',\r\n" + 
					"    DATE_FORMAT(Start_Time,'%d-%M-%Y (%H:%m)') AS 'START_TIME',\r\n" + 
					"    DATE_FORMAT(End_Time,'%d-%M-%Y (%H:%m)') AS 'END_TIME',\r\n" + 
					"    '',\r\n" + 
					"    INAP.Notes AS 'NOTES',\r\n" + 
					"    INAP.SUBMITTED_BY,\r\n" + 
					"    IF(INAP.SUBMITTED_BY = 'Teacher',\r\n" + 
					"    (SELECT CONCAT(TAD.FIRST_NAME,' ',TAD.LAST_NAME) AS 'Name' from teacher_account_details as TAD where TAD.TEACHER_ACCOUNT_DETAILS_ID = INAP.TEACHER_ACCOUNT_DETAILS_ID),\r\n" + 
					"    (SELECT CONCAT(PAD.FIRST_NAME,' ',PAD.LAST_NAME) AS 'Name' from parent_account_details as PAD where PAD.PARENT_ACCOUNT_DETAILS_ID = INAP.PARENT_ACCOUNT_DETAILS_ID)\r\n" + 
					"    ) AS 'Name'\r\n" + 
					"    FROM infant_nap AS INAP\r\n" + 
					"    INNER JOIN studentcheckout AS STUCHK ON STUCHK.StudentCheckOutId = INAP.Student_Check_Out_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = ?\r\n" + 
					"    AND \r\n" + 
					"    (DATE_FORMAT(INAP.Expected_Time,'%Y/%m/%d')=?\r\n" + 
					"    OR DATE_FORMAT(INAP.Expected_Duration,'%Y/%m/%d')=?)";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infant_id);
			ps.setString(2, date);
			ps.setString(3, date);
			ps.setString(4, infant_id);
			ps.setString(5, date);
			ps.setString(6, date);
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

	@POST
	@Path("viewMedicationDetails")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public String viewMedicationDetails(MultivaluedMap<String, String> val) {
		String infant_id = val.getFirst("infant_id");
		String date = val.getFirst("date");
		try {
			con = DatabaseHelper.connection();
			String query = "  SELECT\r\n" + 
					"    'Medication',\r\n" + 
					"    DATE_FORMAT(IEM.ExpectedMedicationTime,'%d-%M-%Y (%H:%m)') AS 'EXP_TIME',\r\n" + 
					"    '' AS 'GIV_TIME',\r\n" + 
					"    (select Name from infant_medication as IM where IM.Infant_Medication_Id =IEM.ExpectedMedicationId) as 'MEDICATION',\r\n" + 
					"    (select Name from unitofmeasurevalue as UMV where UMV.UnitOfMeasureValueId = IEM.ExpectedMedicationAmountId) AS  'Medication_Amount_Id',\r\n" + 
					"    (SELECT name from unitofmeasurevalue as UVM where UVM.UnitOfMeasureValueId = IEM.ExpectedMedicationReasonId) AS 'REASON',\r\n" + 
					"    IEM.Notes AS 'NOTES',\r\n" + 
					"    IEM.SUBMITTED_BY,\r\n" + 
					"    IF(IEM.SUBMITTED_BY = 'Teacher',\r\n" + 
					"    (SELECT CONCAT(TAD.FIRST_NAME,' ',TAD.LAST_NAME) AS 'Name' from teacher_account_details as TAD where TAD.TEACHER_ACCOUNT_DETAILS_ID = IEM.TEACHER_ACCOUNT_DETAILS_ID),\r\n" + 
					"    (SELECT CONCAT(PAD.FIRST_NAME,' ',PAD.LAST_NAME) AS 'Name' from parent_account_details as PAD where PAD.PARENT_ACCOUNT_DETAILS_ID = IEM.PARENT_ACCOUNT_DETAILS_ID)\r\n" + 
					"    ) AS 'Name'\r\n" + 
					"    from infant_expected_medication AS IEM\r\n" + 
					"    INNER JOIN studentcheckin AS STUCHK ON STUCHK.StudentCheckInId = IEM.Student_Check_In_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = ?\r\n" + 
					"    AND \r\n" + 
					"    DATE_FORMAT(IEM.ExpectedMedicationTime,'%Y/%m/%d')=?\r\n" + 
					"    UNION\r\n" + 
					"    SELECT\r\n" + 
					"    'Medication',\r\n" + 
					"    DATE_FORMAT(Expected_Time,'%d-%M-%Y (%H:%m)') AS 'EXP_TIME',\r\n" + 
					"    DATE_FORMAT(Given_Time,'%d-%M-%Y (%H:%m)') AS 'GIV_TIME',\r\n" + 
					"    (select Name from infant_medication as IM where IM.Infant_Medication_Id =IGM.Medication_Id) as 'MEDICATION',\r\n" + 
					"    (select Name from unitofmeasurevalue as UMV where UMV.UnitOfMeasureValueId = IGM.Medication_Amount_Id) AS  'Medication_Amount_Id',\r\n" + 
					"    (SELECT name from unitofmeasurevalue as UVM where UVM.UnitOfMeasureValueId = IGM.Reason_Id) AS 'REASON',\r\n" + 
					"    IGM.Notes AS 'NOTES',\r\n" + 
					"    IGM.SUBMITTED_BY,\r\n" + 
					"    IF(IGM.SUBMITTED_BY = 'Teacher',\r\n" + 
					"    (SELECT CONCAT(TAD.FIRST_NAME,' ',TAD.LAST_NAME) AS 'Name' from teacher_account_details as TAD where TAD.TEACHER_ACCOUNT_DETAILS_ID = IGM.TEACHER_ACCOUNT_DETAILS_ID),\r\n" + 
					"    (SELECT CONCAT(PAD.FIRST_NAME,' ',PAD.LAST_NAME) AS 'Name' from parent_account_details as PAD where PAD.PARENT_ACCOUNT_DETAILS_ID = IGM.PARENT_ACCOUNT_DETAILS_ID)\r\n" + 
					"    ) AS 'Name'\r\n" + 
					"    from infant_given_medication as IGM\r\n" + 
					"    INNER JOIN studentcheckout AS STUCHK ON STUCHK.StudentCheckOutId = IGM.Student_Check_Out_Id\r\n" + 
					"    INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"    WHERE IAD.INFANT_ACCOUNT_DETAILS_ID = ?\r\n" + 
					"    AND \r\n" + 
					"    (DATE_FORMAT(IGM.Expected_Time,'%Y/%m/%d')=?\r\n" + 
					"    OR DATE_FORMAT(IGM.Given_Time,'%Y/%m/%d')=?)";	
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infant_id);
			ps.setString(2, date);
			ps.setString(3, infant_id);
			ps.setString(4, date);
			ps.setString(5, date);
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

	@POST
	@Path("viewDiaperDetails")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public String viewDiaperDetails(MultivaluedMap<String, String> val) {
		String infant_id = val.getFirst("infant_id");
		String date = val.getFirst("date");
		try {
			con = DatabaseHelper.connection();
			String query = "SELECT \r\n" + 
					"'Diaper Change',\r\n" + 
					"DATE_FORMAT(TimeChanged,'%d-%M-%Y (%H:%m)'),\r\n" + 
					"'','','','',\r\n" + 
					"ID.Notes,\r\n" + 
					"ID.SUBMITTED_BY,\r\n" + 
					"IF(ID.SUBMITTED_BY = 'Teacher',\r\n" + 
					"(SELECT CONCAT(TAD.FIRST_NAME,' ',TAD.LAST_NAME) AS 'Name' from teacher_account_details as TAD where TAD.TEACHER_ACCOUNT_DETAILS_ID = ID.TEACHER_ACCOUNT_DETAILS_ID),\r\n" + 
					"(SELECT CONCAT(PAD.FIRST_NAME,' ',PAD.LAST_NAME) AS 'Name' from parent_account_details as PAD where PAD.PARENT_ACCOUNT_DETAILS_ID = ID.PARENT_ACCOUNT_DETAILS_ID)\r\n" + 
					") AS 'Name'\r\n" + 
					"FROM infant_diaper AS ID \r\n" + 
					"INNER JOIN studentcheckout AS STUCHK ON STUCHK.StudentCheckOutId = ID.Student_Check_Out_Id\r\n" + 
					"INNER JOIN infant_account_details AS IAD ON IAD.INFANT_ACCOUNT_DETAILS_ID = STUCHK.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"WHERE IAD.INFANT_ACCOUNT_DETAILS_ID =? \r\n" + 
					"AND DATE_FORMAT(ID.TimeChanged,'%Y/%m/%d')=?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infant_id);
			ps.setString(2, date);
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
	@Path("foodType")
	@Produces(MediaType.TEXT_XML)
	public String foodType() {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "select \r\n" + "  I.InfantFoodId,\r\n"
					+ "  I.Name\r\n" + "from infantfood as I";
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
	@Path("foodAmount")
	@Produces(MediaType.TEXT_XML)
	public String foodAmount() {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "select \r\n" + 
					"UMV.UnitOfMeasureValueId,\r\n" + 
					"UMV.Name\r\n" + 
					"from unitofmeasure as UOM \r\n" + 
					"inner join unitofmeasurevalue as UMV\r\n" + 
					"on UOM.UnitOfMeasureId = UMV.UnitOfMeasureId\r\n" + 
					"where UOM.UnitOfMeasureId in(1,3,4)";
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


	@POST
	@Path("setExpectedFoods")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setExpectedFoods(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String datetime = val.getFirst("datetime");// 'May 18, 2009 11:59'
		int food_id = Integer.parseInt(val.getFirst("food_id"));
		int Food_amount_id = Integer.parseInt(val.getFirst("Food_amount_id"));
		String notes = val.getFirst("notes");
		String teacher_id = val.getFirst("teacher_id");
		try {
			con = DatabaseHelper.connection();
			String query = "insert into infant_expected_food (Student_CheckIn_Id,Expected_Eat_Time,Expected_Food_Id,Expected_Food_Amount_Id,Notes,TEACHER_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n"
					+ "values\r\n"
					+ "((select MAX(StudentCheckInId) from studentcheckin where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n"
					+ "   (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),?,?,?,?,?)";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, infant_id);
			ps.setString(2, datetime);
			ps.setInt(3, food_id);
			ps.setInt(4, Food_amount_id);
			ps.setString(5, notes);
			ps.setString(6, teacher_id);
			ps.setString(7, "Teacher");
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



	@POST
	@Path("setExpectedMedi24Parent")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setExpectedMedi24Parent(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String parent_id = val.getFirst("parent_id");
		String json = val.getFirst("jsonMedi24Details");
		try{
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();
				String query = "insert into last_24hr_medication (Student_CheckIn_Id,LastMedicItemId,LastMediGivenTime,LastMediAmountId,LastMediReasonId,PARENT_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n"
						+ "values\r\n"
						+ "((select MAX(StudentCheckInId) from studentcheckin where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n"
						+ "   ?,(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),?,?,?,'Parent')";
				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				JSONArray jsonArray = jObj.getJSONArray("value");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					String exp_datetime = jObjChild.getString("exp_datetime");// 'May 18, 2009 11:59'
					int Medi24_id = Integer.parseInt(jObjChild.getString("medi24_id"));
					int Medi24_amount_id = Integer.parseInt(jObjChild.getString("medi24_amount_id"));
					int Medi24_reason_id =Integer.parseInt(jObjChild.getString("medi24_reason_id")); 
					ps.setInt(1, infant_id);
					ps.setInt(2, Medi24_id);
					ps.setString(3, exp_datetime);
					ps.setInt(4, Medi24_amount_id);
					ps.setInt(5, Medi24_reason_id);
					ps.setString(6, parent_id);
					ps.addBatch();
				}
				ps.executeBatch();
				con.commit();
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
	}

	@POST
	@Path("setExpectedFoodsParent")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setExpectedFoodsParent(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String parent_id = val.getFirst("parent_id");
		String json = val.getFirst("jsonFoodDetails");
		try{
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();
				String query = "insert into infant_expected_food (Student_CheckIn_Id,Expected_Eat_Time,Expected_Food_Id,Expected_Food_Amount_Id,Notes,PARENT_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n"
						+ "values\r\n"
						+ "((select MAX(StudentCheckInId) from studentcheckin where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n"
						+ "   (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),?,?,?,?,'Parent')";
				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				JSONArray jsonArray = jObj.getJSONArray("value");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					String exp_datetime = jObjChild.getString("exp_datetime");
					int food_id = Integer.parseInt(jObjChild.getString("food_id"));
					int Food_amount_id = Integer.parseInt(jObjChild.getString("food_amount_id"));
					String notes = jObjChild.getString("notes");
					ps.setInt(1, infant_id);
					ps.setString(2, exp_datetime);
					ps.setInt(3, food_id);
					ps.setInt(4, Food_amount_id);
					ps.setString(5, notes);
					ps.setString(6, parent_id);
					ps.addBatch();
				}
				ps.executeBatch();
				con.commit();
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
	}

	@POST
	@Path("setExpectedFoodsParentUpdate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setExpectedFoodsParentUpdate(MultivaluedMap<String, String> val) 
	{
		String json = val.getFirst("jsonFoodDetails");
		try{
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();
				String query = "\r\n" + 
						" UPDATE infant_expected_food SET \r\n" + 
						"  Expected_Food_Id =?,\r\n" + 
						"  Expected_Food_Amount_Id=?,\r\n" + 
						"  Notes=?,\r\n" + 
						"  Expected_Eat_Time=(select STR_TO_DATE(?,'%Y-%m-%d %H:%i'))\r\n"+
						"  where Infant_Expected_FoodId = ?";
				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				JSONArray jsonArray = jObj.getJSONArray("value");
				for (int i = 0; i < jsonArray.length(); i++)
				{
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					String exp_datetime = jObjChild.getString("exp_datetime");// 'May 18, 2009 11:59'
					int food_id = Integer.parseInt(jObjChild.getString("food_id"));
					int Food_amount_id = Integer.parseInt(jObjChild.getString("food_amount_id"));
					String notes = jObjChild.getString("notes");
					int dataid = Integer.parseInt(jObjChild.getString("data_id"));

					ps.setInt(1, food_id);
					ps.setInt(2, Food_amount_id);
					ps.setString(3, notes);
					ps.setString(4, exp_datetime);
					ps.setInt(5, dataid);
					ps.addBatch();
				}
				ps.executeBatch();
				con.commit();
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
	}

	@POST
	@Path("setGivenFoodsTeacher")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void setGivenFoodsTeacher(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String teacher_id = val.getFirst("teacher_id");
		String json = val.getFirst("jsonFoodDetails");
		try{
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();
				String query = "insert into infant_given_food ( Student_Check_OutId,Expected_Time,Given_Time,Food_Id,Food_Amount_Id, Notes, Expected_Food_Id,TEACHER_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n" + 
						"	values(\r\n" + 
						"  (select MAX(StudentCheckOutId) from studentcheckout where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n" + 
						"  (select IEM.Expected_Eat_Time from infant_expected_food AS IEM WHERE IEM.Infant_Expected_FoodId=?),\r\n" + 
						"  (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n" + 
						"  ?,?,?,if(? = 0 ,null, ?),? ,'Teacher')";
				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				JSONArray jsonArray = jObj.getJSONArray("value");
				Logs.writeLogLine(0);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					int exp_id = Integer.parseInt(jObjChild.getString("exp_id"));
					String giv_datetime = jObjChild.getString("giv_datetime");
					int food_id = Integer.parseInt(jObjChild.getString("food_id"));
					int Food_amount_id = Integer.parseInt(jObjChild.getString("food_amount_id"));
					String notes = jObjChild.getString("notes");
					ps.setInt(1, infant_id);
					ps.setInt(2, exp_id);
					ps.setString(3, giv_datetime);
					ps.setInt(4, food_id);
					ps.setInt(5, Food_amount_id);
					ps.setString(6, notes);
					ps.setInt(7, exp_id);
					ps.setInt(8, exp_id);
					ps.setString(9, teacher_id);
					ps.addBatch();

					Logs.writeTeacherLog("setGivenFoodsTeacher",infant_id+"",exp_id+"",giv_datetime,food_id+"",null,Food_amount_id+"",notes,teacher_id);
				}
				Logs.writeLogLine(1);
				ps.executeBatch();
				con.commit();
			}
		}catch (Exception exc) {

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

	@POST
	@Path("setGivenFoods")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setGivenFoods(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String exp_datetime = val.getFirst("exp_datetime");
		String giv_datetime = val.getFirst("giv_datetime");
		int food_id = Integer.parseInt(val.getFirst("food_id"));
		int Food_amount_id = Integer.parseInt(val.getFirst("Food_amount_id"));
		String notes = val.getFirst("notes");
		String teacher_id = val.getFirst("teacher_id");
		try {
			con = DatabaseHelper.connection();
			String query = "insert into infant_given_food ( Student_Check_OutId,Expected_Time,Given_Time,Food_Id,Food_Amount_Id, Notes, Expected_Food_Id,TEACHER_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n"
					+ "	values(\r\n"
					+ "  (select MAX(StudentCheckOutId) from studentcheckout where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n"
					+ "  (select IEF.Expected_Eat_Time from infant_expected_food as IEF where IEF.Student_CheckIn_Id = (SELECT MAX(StudentCheckInId) FROM studentcheckin WHERE INFANT_ACCOUNT_DETAILS_ID=? ) and DATE_FORMAT(IEF.Expected_Eat_Time,'%Y/%m/%d')=DATE_FORMAT(?,'%Y/%m/%d') order by IEF.Infant_Expected_FoodId desc limit 1),\r\n"
					+ "  (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n"
					+ "  ?,?,?,\r\n"
					+ "  (select IEF.Infant_Expected_FoodId from infant_expected_food as IEF where IEF.Student_CheckIn_Id = (SELECT MAX(StudentCheckInId) FROM studentcheckin WHERE INFANT_ACCOUNT_DETAILS_ID=? ) and DATE_FORMAT(IEF.Expected_Eat_Time,'%Y/%m/%d')=DATE_FORMAT(?,'%Y/%m/%d') order by IEF.Infant_Expected_FoodId desc limit 1),\r\n"
					+ "  ?,'Teacher');";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, infant_id);
			ps.setInt(2, infant_id);
			ps.setString(3, exp_datetime);
			ps.setString(4, giv_datetime);
			ps.setInt(5, food_id);
			ps.setInt(6, Food_amount_id);
			ps.setString(7, notes);
			ps.setInt(8, infant_id);
			ps.setString(9, exp_datetime);
			ps.setString(10, teacher_id);
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

	@POST
	@Path("setExpectedNaps")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setExpectedNaps(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String naptime = val.getFirst("naptime");
		String napduration = val.getFirst("napduration");
		String notes = val.getFirst("notes");
		String teacher_id = val.getFirst("teacher_id");
		try {
			con = DatabaseHelper.connection();
			String query = "insert into infant_expected_nap (Student_Check_In_Id,ExpectedNapTime,ExpectedNapDuration,Notes,TEACHER_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n" + 
					"  values((select MAX(StudentCheckInId) from studentcheckin where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n" + 
					"  (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n" + 
					"  (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n" + 
					"  ?,?,?);";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, infant_id);
			ps.setString(2, naptime);
			ps.setString(3, napduration);
			ps.setString(4, notes);
			ps.setString(5, teacher_id);
			ps.setString(6, "Teacher");
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


	@POST
	@Path("setExpectedNapsParent")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setExpectedNapsParent(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String parent_id = val.getFirst("parent_id");
		String json = val.getFirst("jsonNapDetails");
		try{
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();
				String query = "insert into infant_expected_nap (Student_Check_In_Id,ExpectedNapTime,ExpectedNapDuration,Notes,PARENT_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n" + 
						"  values((select MAX(StudentCheckInId) from studentcheckin where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n" + 
						"  (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n" + 
						"  (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n" + 
						"  ?,?,'Parent');";
				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				JSONArray jsonArray = jObj.getJSONArray("value");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					String start_datetime = jObjChild.getString("start_datetime");
					String end_datetime = jObjChild.getString("end_datetime");
					String notes = jObjChild.getString("notes");
					ps.setInt(1, infant_id);
					ps.setString(2, start_datetime);
					ps.setString(3, end_datetime);
					ps.setString(4, notes);
					ps.setString(5, parent_id);
					ps.addBatch();
				}
				ps.executeBatch();
				con.commit();
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
	}



	@POST
	@Path("setExpectedNapsParentUpdate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setExpectedNapsParentUpdate(MultivaluedMap<String, String> val) {

		String json = val.getFirst("jsonNapDetails");
		try{
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();

				String query="UPDATE infant_expected_nap \r\n" + 
						"SET \r\n" + 
						"  ExpectedNapTime = ?,\r\n" + 
						"  ExpectedNapDuration=?,\r\n" + 
						"  Notes = ?\r\n" + 
						"WHERE Infant_Expected_Nap_Id = ?";

				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				JSONArray jsonArray = jObj.getJSONArray("value");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					String start_datetime = jObjChild.getString("start_datetime");
					String end_datetime = jObjChild.getString("end_datetime");
					String notes = jObjChild.getString("notes");
					int dataid = Integer.parseInt(jObjChild.getString("data_id"));

					ps.setString(1, start_datetime);
					ps.setString(2, end_datetime);
					ps.setString(3, notes);
					ps.setInt(4, dataid);
					ps.addBatch();
				}
				ps.executeBatch();
				con.commit();
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
	}


	@POST
	@Path("setGivenNaps")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setGivenNaps(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String exp_nap_datetime = val.getFirst("exp_nap_datetime");
		String exp_nap_duration = val.getFirst("exp_nap_duration");
		String start_time = val.getFirst("start_time");				
		String end_time = val.getFirst("end_time");					
		String notes = val.getFirst("notes");
		String teacher_id = val.getFirst("teacher_id");
		try {
			con = DatabaseHelper.connection();
			String query = "insert into infant_nap(Student_Check_Out_Id,Expected_Time,Expected_Duration,Start_Time,End_Time,NOTES,TEACHER_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n" + 
					"VALUES (\r\n" + 
					"(select MAX(StudentCheckOutId) from studentcheckout where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n" + 
					"(select IEN.ExpectedNapTime  from infant_expected_nap AS IEN WHERE IEN.Student_Check_In_Id  = (SELECT MAX(StudentCheckInId) FROM studentcheckin WHERE INFANT_ACCOUNT_DETAILS_ID=? ) and DATE_FORMAT(IEN.ExpectedNapTime,'%Y/%m/%d')=DATE_FORMAT(?,'%Y/%m/%d') order by IEN.Infant_Expected_Nap_Id desc limit 1),\r\n" + 
					"(select IEN.ExpectedNapDuration  from infant_expected_nap AS IEN WHERE IEN.Student_Check_In_Id  = (SELECT MAX(StudentCheckInId) FROM studentcheckin WHERE INFANT_ACCOUNT_DETAILS_ID=? ) and DATE_FORMAT(IEN.ExpectedNapDuration ,'%Y/%m/%d')=DATE_FORMAT(?,'%Y/%m/%d') order by IEN.Infant_Expected_Nap_Id desc limit 1),\r\n" + 
					"(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n" + 
					"(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n" + 
					"?,?,?);";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, infant_id);
			ps.setInt(2, infant_id);
			ps.setString(3, exp_nap_datetime);	
			ps.setInt(4, infant_id);
			ps.setString(5, exp_nap_duration);	
			ps.setString(6, start_time);		
			ps.setString(7, end_time);			
			ps.setString(8, notes);
			ps.setString(9, teacher_id);
			ps.setString(10, "Teacher");
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


	@POST
	@Path("setGivenNapsTeacher")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setGivenNapsTeacher(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String teacher_id = val.getFirst("teacher_id");
		String json = val.getFirst("jsonNapDetails");
		try {
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();
				String query = "insert into infant_nap(Student_Check_Out_Id,Expected_Time,Expected_Duration,Start_Time,End_Time,NOTES,TEACHER_ACCOUNT_DETAILS_ID,SUBMITTED_BY,Expected_Nap_Id)\r\n" + 
						"VALUES (\r\n" + 
						"(select MAX(StudentCheckOutId) from studentcheckout where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n" + 
						"(select IEN.ExpectedNapTime  from infant_expected_nap AS IEN WHERE IEN.Infant_Expected_Nap_Id=?),\r\n" + 
						"(select IEN.ExpectedNapDuration  from infant_expected_nap AS IEN WHERE IEN.Infant_Expected_Nap_Id=?),\r\n" + 
						"(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n" + 
						"(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n" + 
						"?,?,'Teacher',if(? = 0 ,null, ?));";

				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				Logs.writeLogLine(0);
				JSONArray jsonArray = jObj.getJSONArray("value");

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					int exp_id = Integer.parseInt(jObjChild.getString("exp_id"));
					String start_datetime = jObjChild.getString("start_datetime");
					String end_datetime = jObjChild.getString("end_datetime");
					String notes = jObjChild.getString("notes");
					ps.setInt(1, infant_id);
					ps.setInt(2, exp_id);
					ps.setInt(3, exp_id);
					ps.setString(4, start_datetime);
					ps.setString(5, end_datetime);
					ps.setString(6, notes);
					ps.setString(7, teacher_id);
					ps.setInt(8, exp_id);
					ps.setInt(9, exp_id);
					Logs.writeLogNap(infant_id+"", exp_id+"", start_datetime, end_datetime, notes,teacher_id);
					ps.addBatch();

				}
				ps.executeBatch();
				Logs.writeLogLine(1);
				con.commit();
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
	}

	@POST
	@Path("setGivenNapsCamera")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setGivenNapsCamera(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String json = val.getFirst("jsonNapDetails");
		try {
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();

				String query1 = "insert into infant_live (INFANT_ACCOUNT_DETAILS_ID,START_NAP_TIME,END_NAP_TIME,CAMERA_DETAILS_ID,CRIB_DETAILS_ID,CAMERA_PATH)\r\n" + 
						"VALUES (\r\n" +
						"?,\r\n" + 
						"(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n" + 
						"(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n" + 
						"?,\r\n" +
						"0,\r\n" +
						"?);"; 
				PreparedStatement ps1 = con.prepareStatement(query1);
				con.setAutoCommit(false);
				JSONArray jsonArray = jObj.getJSONArray("value");

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					String start_datetime = jObjChild.getString("start_datetime");
					String end_datetime = jObjChild.getString("end_datetime");
					int cameraid = jObjChild.getInt("cameraid");
					String camerapath = jObjChild.getString("camerapath");
					ps1.setInt(1, infant_id);
					ps1.setString(2, start_datetime);
					ps1.setString(3, end_datetime);
					ps1.setInt(4, cameraid);
					ps1.setString(5, camerapath);
					ps1.addBatch();
				}
				ps1.executeBatch();				
				con.commit();
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
	}


	@POST
	@Path("setDiaperChange")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void setDiaperChange(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String time_changed = val.getFirst("time_changed");
		int diaper_id = Integer.parseInt(val.getFirst("diaper_id"));
		String notes = val.getFirst("notes");
		String teacher_id = val.getFirst("teacher_id");
		try {
			con = DatabaseHelper.connection();
			String query = "insert into infant_diaper (Student_Check_Out_Id,TimeChanged,Diaper_Type_Id,NOTES,TEACHER_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n" + 
					"values(\r\n" + 
					"    (select MAX(StudentCheckOutId) from studentcheckout where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n" + 
					"    (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),?,?,?,?)";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, infant_id);
			ps.setString(2, time_changed);	
			ps.setInt(3, diaper_id);
			ps.setString(4, notes);
			ps.setString(5, teacher_id);
			ps.setString(6, "Teacher");
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


	@POST
	@Path("setDiaperChangeTeacher")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void setDiaperChangeTeacher(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String teacher_id = val.getFirst("teacher_id");
		String json = val.getFirst("jsonDiaperDetails");
		try {
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();
				String query = "insert into infant_diaper (Student_Check_Out_Id,TimeChanged,Diaper_Type_Id,NOTES,TEACHER_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n" + 
						"values(\r\n" + 
						"    (select MAX(StudentCheckOutId) from studentcheckout where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n" + 
						"    (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),?,?,?,'Teacher')";
				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				JSONArray jsonArray = jObj.getJSONArray("value");
				Logs.writeLogLine(0);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					String time_changed = jObjChild.getString("time_changed");
					int diaper_id = Integer.parseInt(jObjChild.getString("diaper_id"));
					String notes = jObjChild.getString("notes");
					ps.setInt(1, infant_id);
					ps.setString(2, time_changed);	
					ps.setInt(3, diaper_id);
					ps.setString(4, notes);
					ps.setString(5, teacher_id);
					Logs.writeLogDiaper(infant_id+"", time_changed, diaper_id+"", notes, teacher_id);
					ps.addBatch();
				}
				ps.executeBatch();
				Logs.writeLogLine(0);
				con.commit();
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
	}

	@GET
	@Path("fillWhatMedication")
	@Produces(MediaType.TEXT_XML)
	public String fillWhatMedication() {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "select \r\n" + "  IM.Infant_Medication_Id,\r\n"
					+ "  IM.Name  \r\n" + "  from infant_medication as IM";
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
	@Path("fillWhyMedication")
	@Produces(MediaType.TEXT_XML)
	public String fillWhyMedication() {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "select \r\n" + 
					"UMV.UnitOfMeasureValueId,\r\n" + 
					"UMV.Name\r\n" + 
					"from unitofmeasure as UOM \r\n" + 
					"inner join unitofmeasurevalue as UMV\r\n" + 
					"on UOM.UnitOfMeasureId = UMV.UnitOfMeasureId\r\n" + 
					"where UOM.UnitOfMeasureId in(12) and UOM.ISDELETED=0";
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
	@Path("fillAmountMedication")
	@Produces(MediaType.TEXT_XML)
	public String fillAmountMedication() {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "select \r\n" + 
					"UMV.UnitOfMeasureValueId,\r\n" + 
					"UMV.Name\r\n" + 
					"from unitofmeasure as UOM \r\n" + 
					"inner join unitofmeasurevalue as UMV\r\n" + 
					"on UOM.UnitOfMeasureId = UMV.UnitOfMeasureId\r\n" + 
					"where UOM.UnitOfMeasureId in (6,7,8,11) and UOM.ISDELETED=0;";
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
	@Path("fillDiaperTypes")
	@Produces(MediaType.TEXT_XML)
	public String fillDiaperTypes() {
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			String query = "SELECT \r\n" + "  UMV.UnitOfMeasureValueId,\r\n"
					+ "  UMV.Name\r\n" + "  from unitofmeasure as UOM \r\n"
					+ "  inner join unitofmeasurevalue as UMV\r\n"
					+ "  on UOM.UnitOfMeasureId = UMV.UnitOfMeasureId\r\n"
					+ "  where \r\n" + "  UOM.UnitOfMeasureId in (5)\r\n"
					+ "  AND UMV.IsDeleted=0\r\n" + "  AND UOM.IsDeleted=0";
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


	@POST
	@Path("fillCameraList")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public String fillCameraList(MultivaluedMap<String, String> val) {
		String schoolId = val.getFirst("schoolId");
		String classId = val.getFirst("classId");
		try {
			con = DatabaseHelper.connection();
			String query = "SELECT \r\n" + 
					"cd.CAMERA_DETAILS_ID,\r\n" +
					"cd.NAME, \r\n" +
					"cd.CAPTURED_VIDEO_PATH\r\n" +
					"FROM camera_details as cd \r\n" +  
					"WHERE cd.SCHOOL_INFO_ID = ? \r\n" +
					"and cd.CLASSROOM_ID = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, schoolId);
			ps.setString(2, classId);
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


	@POST
	@Path("getLatestCamera")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public String getLatestCamera(MultivaluedMap<String, String> val) {
		String infantId = val.getFirst("infantlId");
		try {
			con = DatabaseHelper.connection();

			String query="Select \r\n"+
					"il.CAMERA_PATH \r\n"+
					"From infant_live as il \r\n"+
					"WHERE il.INFANT_ACCOUNT_DETAILS_ID = ? \r\n"+
					"and CURTIME()< date_format(il.END_NAP_TIME,'%H:%i') \r\n"+
					"and date_format(CURDATE(),'%m/%d/%Y') = date_format(il.END_NAP_TIME,'%m/%d/%Y')"+
					"order  by INFANT_LIVE_ID desc limit 1";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infantId);
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


	@POST
	@Path("setExpectedMedication")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setExpectedMedication(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String datetime = val.getFirst("datetime");// 'May 18, 2009 11:59'
		int exp_med_id = Integer.parseInt(val.getFirst("exp_med_id"));
		int exp_med_amt_id = Integer.parseInt(val.getFirst("exp_med_amt_id"));
		int exp_med_reason_id = Integer.parseInt(val
				.getFirst("exp_med_reason_id"));
		String notes = val.getFirst("notes");
		String teacher_id = val.getFirst("teacher_id");
		try {
			con = DatabaseHelper.connection();
			String query = "insert into infant_expected_medication(Student_Check_In_Id,ExpectedMedicationTime,ExpectedMedicationId,ExpectedMedicationAmountId,ExpectedMedicationReasonId,Notes,TEACHER_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n"
					+ "  values(\r\n"
					+ "  (select MAX(StudentCheckInId) from studentcheckin where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n"
					+ "  (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),?,?,?,?,?,?);";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, infant_id);
			ps.setString(2, datetime);
			ps.setInt(3, exp_med_id);
			ps.setInt(4, exp_med_amt_id);
			ps.setInt(5, exp_med_reason_id);
			ps.setString(6, notes);
			ps.setString(7, teacher_id);
			ps.setString(8, "Teacher");
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


	@POST
	@Path("setExpectedMedicationParent")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setExpectedMedicationParent(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String parent_id = val.getFirst("parent_id");
		String json = val.getFirst("jsonMediDetails");
		try{
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();
				String query = "insert into infant_expected_medication(Student_Check_In_Id,ExpectedMedicationTime,ExpectedMedicationId,ExpectedMedicationAmountId,ExpectedMedicationReasonId,Notes,PARENT_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n"
						+ "  values(\r\n"
						+ "  (select MAX(StudentCheckInId) from studentcheckin where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n"
						+ "  (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),?,?,?,?,?,'Parent');";
				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				JSONArray jsonArray = jObj.getJSONArray("value");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					String exp_datetime = jObjChild.getString("exp_datetime");
					String exp_med_id = jObjChild.getString("exp_med_id");
					String exp_med_amt_id = jObjChild.getString("exp_med_amt_id");
					String exp_med_reason_id = jObjChild.getString("exp_med_reason_id");
					String notes = jObjChild.getString("notes");
					ps.setInt(1, infant_id);
					ps.setString(2, exp_datetime);
					ps.setString(3, exp_med_id);
					ps.setString(4, exp_med_amt_id);
					ps.setString(5, exp_med_reason_id);
					ps.setString(6, notes);
					ps.setString(7, parent_id);
					ps.addBatch();
				}
				ps.executeBatch();
				con.commit();
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
	}

	@POST
	@Path("setExpectedMedicationParentUpdate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setExpectedMedicationParentUpdate(MultivaluedMap<String, String> val) {

		String json = val.getFirst("jsonMediDetails");
		try{
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();

				String query = "UPDATE infant_expected_medication SET \r\n" + 
						"  ExpectedMedicationTime = ?,\r\n" + 
						"  ExpectedMedicationId = ?,\r\n" + 
						"  ExpectedMedicationAmountId =?,\r\n" + 
						"  ExpectedMedicationReasonId =?,\r\n" + 
						"  Notes=?\r\n" + 
						"  WHERE Infant_Expected_Medication_Id = ?";

				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				JSONArray jsonArray = jObj.getJSONArray("value");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					String exp_datetime = jObjChild.getString("exp_datetime");
					String exp_med_id = jObjChild.getString("exp_med_id");
					String exp_med_amt_id = jObjChild.getString("exp_med_amt_id");
					String exp_med_reason_id = jObjChild.getString("exp_med_reason_id");
					String notes = jObjChild.getString("notes");
					int dataid = Integer.parseInt(jObjChild.getString("data_id"));

					ps.setString(1, exp_datetime);
					ps.setString(2, exp_med_id);
					ps.setString(3, exp_med_amt_id);
					ps.setString(4, exp_med_reason_id);
					ps.setString(5, notes);
					ps.setInt(6, dataid);
					ps.addBatch();
				}
				ps.executeBatch();
				con.commit();
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
	}


	@POST
	@Path("setGivenMedication")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setGivenMedication(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String exp_datetime = val.getFirst("exp_datetime");
		String giv_datetime = val.getFirst("giv_datetime");
		int med_id = Integer.parseInt(val.getFirst("med_id"));
		int med_amount_id = Integer.parseInt(val.getFirst("med_amount_id"));
		int reason_id = Integer.parseInt(val.getFirst("reason_id"));
		String notes = val.getFirst("notes");
		String teacher_id = val.getFirst("teacher_id");
		try {
			con = DatabaseHelper.connection();
			String query = "insert into infant_given_medication (Student_Check_Out_Id,Expected_Time,Given_Time,Medication_Id,Medication_Amount_Id,Reason_Id,Expected_Medication_Id,Notes,TEACHER_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n"
					+ "values(    \r\n"
					+ "    (select MAX(StudentCheckOutId) from studentcheckout where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n"
					+ "    (select IEM.ExpectedMedicationTime from infant_expected_medication as IEM where IEM.Student_Check_In_Id=(SELECT MAX(StudentCheckInId) FROM studentcheckin WHERE INFANT_ACCOUNT_DETAILS_ID=? ) and DATE_FORMAT(IEM.ExpectedMedicationTime,'%Y/%m/%d')=DATE_FORMAT(?,'%Y/%m/%d') order by IEM.Infant_Expected_Medication_Id desc limit 1),\r\n"
					+ "    (select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n"
					+ "    ?, ?, ?,\r\n"
					+ "    (select IEM.Infant_Expected_Medication_Id from infant_expected_medication as IEM where IEM.Student_Check_In_Id=(SELECT MAX(StudentCheckInId) FROM studentcheckin WHERE INFANT_ACCOUNT_DETAILS_ID=? ) and DATE_FORMAT(IEM.ExpectedMedicationTime,'%Y/%m/%d')=DATE_FORMAT(?,'%Y/%m/%d') order by IEM.Infant_Expected_Medication_Id desc limit 1),\r\n"
					+ "    ?,?,'Teacher')";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, infant_id);
			ps.setInt(2, infant_id);
			ps.setString(3, exp_datetime);
			ps.setString(4, giv_datetime);
			ps.setInt(5, med_id);
			ps.setInt(6, med_amount_id);
			ps.setInt(7, reason_id);
			ps.setInt(8, infant_id);
			ps.setString(9, exp_datetime);
			ps.setString(10, notes);
			ps.setString(11, teacher_id);
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


	@POST
	@Path("setGivenMedicationTeacher")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	public void setGivenMedicationTeacher(MultivaluedMap<String, String> val) {
		int infant_id = Integer.parseInt(val.getFirst("infant_id"));
		String teacher_id = val.getFirst("teacher_id");
		String json = val.getFirst("jsonMediDetails");
		try{
			JSONObject jObj = new JSONObject(json);
			if (jObj != null) {
				con = DatabaseHelper.connection();
				String query = "insert into infant_given_medication (Student_Check_Out_Id,Expected_Time,Given_Time,Medication_Id,Medication_Amount_Id,Reason_Id,Expected_Medication_Id,Notes,TEACHER_ACCOUNT_DETAILS_ID,SUBMITTED_BY)\r\n" + 
						"			values(    \r\n" + 
						"				(select MAX(StudentCheckOutId) from studentcheckout where INFANT_ACCOUNT_DETAILS_ID = ?),\r\n" + 
						"				(select IEM.ExpectedMedicationTime from infant_expected_medication as IEM where IEM.Infant_Expected_Medication_Id=?),\r\n" + 
						"				(select STR_TO_DATE(?,'%M %d,%Y %H:%i')),\r\n" + 
						"				?,?,?,if(? = 0 ,null, ?),?,?,'Teacher')";
				PreparedStatement ps = con.prepareStatement(query);
				con.setAutoCommit(false);
				Logs.writeLogLine(0);
				JSONArray jsonArray = jObj.getJSONArray("value");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObjChild = jsonArray.getJSONObject(i);
					int exp_id = Integer.parseInt(jObjChild.getString("exp_id"));
					String giv_datetime = jObjChild.getString("giv_datetime");
					int medi_type_id = Integer.parseInt(jObjChild.getString("medi_type_id"));
					int medi_amount_id = Integer.parseInt(jObjChild.getString("medi_amount_id"));
					int medi_reason_id = Integer.parseInt(jObjChild.getString("medi_reason_id"));
					String notes = jObjChild.getString("notes");
					ps.setInt(1, infant_id);
					ps.setInt(2, exp_id);
					ps.setString(3, giv_datetime);
					ps.setInt(4, medi_type_id);
					ps.setInt(5, medi_amount_id);
					ps.setInt(6, medi_reason_id);
					ps.setInt(7, exp_id);
					ps.setInt(8, exp_id);
					ps.setString(9, notes);
					ps.setString(10, teacher_id);
					Logs.writeLogMedi(infant_id+"", exp_id+"", giv_datetime, medi_type_id+"", medi_amount_id+"", medi_reason_id+"",notes,teacher_id);
					ps.addBatch();
				}

				Logs.writeLogLine(1);
				ps.executeBatch();
				con.commit();
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

	@GET
	@Path("dayViewClasroom/{classroomId}/{date}")
	@Produces(MediaType.TEXT_XML)
	public String dayViewClasroom(@PathParam("classroomId") String classroomId,@PathParam("date") String date){
		try {
			con = DatabaseHelper.connection();
			String query = "SELECT \r\n" + 
					"IAD.INFANT_ACCOUNT_DETAILS_ID,\r\n" + 
					"CONCAT(IAD.FIRST_NAME,' ',IAD.LAST_NAME) AS 'NAME',\r\n" + 
					"(SELECT \r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(IEF.Expected_Eat_Time,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_expected_food AS IEF \r\n" + 
					"INNER JOIN studentcheckin AS CHKIN\r\n" + 
					"on IEF.Student_CheckIn_Id=CHKIN.StudentCheckInId\r\n" + 
					"WHERE \r\n" + 
					"CHKIN.INFANT_ACCOUNT_DETAILS_ID=IAD.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"AND DATE_FORMAT(IEF.Expected_Eat_Time,'%Y-%m-%d')=DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')\r\n" + 
					"AND DATE_FORMAT(IEF.Expected_Eat_Time,'%Y-%m-%d')=?\r\n" + 
					"AND IEF.STATUS=1 \r\n"+
					"AND CHKIN.STATUS=1) AS 'EXP_FOOD_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT \r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(IEG.Given_Time ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_given_food AS IEG \r\n" + 
					"INNER JOIN studentcheckout AS CHKOUT\r\n" + 
					"on IEG.Student_Check_OutId=CHKOUT.StudentCheckOutId \r\n" + 
					"WHERE \r\n" + 
					"CHKOUT.INFANT_ACCOUNT_DETAILS_ID=IAD.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"AND DATE_FORMAT(IEG.Given_Time,'%Y-%m-%d')=?)AS 'GIV_FOOD_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT\r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(IEN.ExpectedNapTime ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_expected_nap AS IEN\r\n" + 
					"INNER JOIN studentcheckin AS CHKIN\r\n" + 
					"ON IEN.Student_Check_In_Id=CHKIN.StudentCheckInId\r\n" + 
					"WHERE \r\n" + 
					"CHKIN.INFANT_ACCOUNT_DETAILS_ID=IAD.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"AND DATE_FORMAT(IEN.ExpectedNapTime,'%Y-%m-%d')=DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')\r\n" + 
					"AND DATE_FORMAT(IEN.ExpectedNapTime,'%Y-%m-%d')=?\r\n" + 
					"AND IEN.STATUS=1 \r\n"+
					"AND CHKIN.STATUS=1) AS 'EXP_NAP_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT \r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(INAP.Start_Time ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_nap AS INAP \r\n" + 
					"INNER JOIN studentcheckout AS CHKOUT\r\n" + 
					"on INAP.Student_Check_Out_Id=CHKOUT.StudentCheckOutId \r\n" + 
					"WHERE \r\n" + 
					"CHKOUT.INFANT_ACCOUNT_DETAILS_ID=IAD.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"AND DATE_FORMAT(INAP.Start_Time,'%Y-%m-%d')=?) AS 'GIV_NAP_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT\r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(IEM.ExpectedMedicationTime ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_expected_medication AS IEM\r\n" + 
					"INNER JOIN studentcheckin AS CHKIN\r\n" + 
					"ON IEM.Student_Check_In_Id=CHKIN.StudentCheckInId\r\n" + 
					"WHERE \r\n" + 
					"CHKIN.INFANT_ACCOUNT_DETAILS_ID=IAD.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"AND DATE_FORMAT(IEM.ExpectedMedicationTime,'%Y-%m-%d')=DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')\r\n" + 
					"AND DATE_FORMAT(IEM.ExpectedMedicationTime,'%Y-%m-%d')=? \r\n" + 
					"AND IEM.STATUS=1 \r\n"+
					"AND CHKIN.STATUS=1) AS 'EXP_MEDI_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT \r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(IGM.Given_Time ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_given_medication AS IGM \r\n" + 
					"INNER JOIN studentcheckout AS CHKOUT\r\n" + 
					"on IGM.Student_Check_Out_Id=CHKOUT.StudentCheckOutId \r\n" + 
					"WHERE \r\n" + 
					"CHKOUT.INFANT_ACCOUNT_DETAILS_ID=IAD.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"AND DATE_FORMAT(IGM.Given_Time,'%Y-%m-%d')=?) AS 'GIV_MEDI_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT \r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(ID.TimeChanged ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_diaper AS ID \r\n" + 
					"INNER JOIN studentcheckout AS CHKOUT\r\n" + 
					"on ID.Student_Check_Out_Id=CHKOUT.StudentCheckOutId \r\n" + 
					"WHERE \r\n" + 
					"CHKOUT.INFANT_ACCOUNT_DETAILS_ID=IAD.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"AND DATE_FORMAT(ID.TimeChanged,'%Y-%m-%d')=?) AS 'GIV_DIAPER_CHANGEE'\r\n" + 
					"\r\n" + 
					"FROM infant_account_details AS IAD\r\n" + 
					"INNER JOIN enrollment AS ENROLL \r\n" + 
					"ON IAD.INFANT_ACCOUNT_DETAILS_ID= ENROLL.INFANT_ACCOUNT_DETAILS_ID\r\n" + 
					"WHERE ENROLL.CLASSROOM_ID=? " +
					"AND ? BETWEEN DATE_FORMAT( ENROLL.Start_Date, '%Y-%m-%d') AND DATE_FORMAT( ENROLL.End_Date, '%Y-%m-%d') ";
			System.out.print(query);
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, date);
			ps.setString(2, date);
			ps.setString(3, date);
			ps.setString(4, date);
			ps.setString(5, date);
			ps.setString(6, date);
			ps.setString(7, date);
			ps.setString(8, classroomId);
			ps.setString(9, date);
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
	@Path("dayViewClasroomParticular/{infantId}/{date}")
	@Produces(MediaType.TEXT_XML)
	public String dayViewClasroomParticular(@PathParam("infantId") String infantId,@PathParam("date") String date){
		try {
			con = DatabaseHelper.connection();
			String query = "\r\n" + 
					"SELECT \r\n" + 
					"\r\n" + 
					"(SELECT GROUP_CONCAT(DATE_FORMAT(IEF.Expected_Eat_Time,'%T') SEPARATOR ' , ') FROM infant_expected_food AS IEF \r\n" + 
					"INNER JOIN studentcheckin AS CHKIN on IEF.Student_CheckIn_Id=CHKIN.StudentCheckInId WHERE \r\n" + 
					"CHKIN.INFANT_ACCOUNT_DETAILS_ID=? \r\n" + 
					"AND DATE_FORMAT(IEF.Expected_Eat_Time,'%Y-%m-%d')=DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')\r\n" + 
					"AND DATE_FORMAT(IEF.Expected_Eat_Time,'%Y-%m-%d')=?) AS 'EXP_FOOD_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT GROUP_CONCAT(DATE_FORMAT(IEG.Given_Time ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_given_food AS IEG INNER JOIN studentcheckout AS CHKOUT\r\n" + 
					"on IEG.Student_Check_OutId=CHKOUT.StudentCheckOutId WHERE \r\n" + 
					"CHKOUT.INFANT_ACCOUNT_DETAILS_ID=? AND DATE_FORMAT(IEG.Given_Time,'%Y-%m-%d')=?)AS 'GIV_FOOD_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT\r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(IEN.ExpectedNapTime ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_expected_nap AS IEN\r\n" + 
					"INNER JOIN studentcheckin AS CHKIN\r\n" + 
					"ON IEN.Student_Check_In_Id=CHKIN.StudentCheckInId\r\n" + 
					"WHERE \r\n" + 
					"CHKIN.INFANT_ACCOUNT_DETAILS_ID=?\r\n" + 
					"AND DATE_FORMAT(IEN.ExpectedNapTime,'%Y-%m-%d')=DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')\r\n" + 
					"AND DATE_FORMAT(IEN.ExpectedNapTime,'%Y-%m-%d')=?) AS 'EXP_NAP_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT GROUP_CONCAT(DATE_FORMAT(INAP.Start_Time ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_nap AS INAP INNER JOIN studentcheckout AS CHKOUT\r\n" + 
					"on INAP.Student_Check_Out_Id=CHKOUT.StudentCheckOutId WHERE \r\n" + 
					"CHKOUT.INFANT_ACCOUNT_DETAILS_ID=? AND DATE_FORMAT(INAP.Start_Time,'%Y-%m-%d')=?) AS 'GIV_NAP_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT GROUP_CONCAT(DATE_FORMAT(IEM.ExpectedMedicationTime ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_expected_medication AS IEM INNER JOIN studentcheckin AS CHKIN\r\n" + 
					"ON IEM.Student_Check_In_Id=CHKIN.StudentCheckInId WHERE \r\n" + 
					"CHKIN.INFANT_ACCOUNT_DETAILS_ID=? AND DATE_FORMAT(IEM.ExpectedMedicationTime,'%Y-%m-%d')= DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')\r\n" + 
					"AND DATE_FORMAT(IEM.ExpectedMedicationTime,'%Y-%m-%d')=?) AS 'EXP_MEDI_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT GROUP_CONCAT(DATE_FORMAT(IGM.Given_Time ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_given_medication AS IGM INNER JOIN studentcheckout AS CHKOUT\r\n" + 
					"on IGM.Student_Check_Out_Id=CHKOUT.StudentCheckOutId WHERE \r\n" + 
					"CHKOUT.INFANT_ACCOUNT_DETAILS_ID=? AND DATE_FORMAT(IGM.Given_Time,'%Y-%m-%d')=?) AS 'GIV_MEDI_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT GROUP_CONCAT(DATE_FORMAT(ID.TimeChanged ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_diaper AS ID INNER JOIN studentcheckout AS CHKOUT\r\n" + 
					"on ID.Student_Check_Out_Id=CHKOUT.StudentCheckOutId \r\n" + 
					"WHERE \r\n" + 
					"CHKOUT.INFANT_ACCOUNT_DETAILS_ID=?\r\n" + 
					"AND DATE_FORMAT(ID.TimeChanged,'%Y-%m-%d')=?) AS 'GIV_DIAPER_CHANGEE'";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infantId);
			ps.setString(2, date);
			ps.setString(3, infantId);
			ps.setString(4, date);
			ps.setString(5, infantId);
			ps.setString(6, date);
			ps.setString(7, infantId);
			ps.setString(8, date);
			ps.setString(9, infantId);
			ps.setString(10, date);
			ps.setString(11, infantId);
			ps.setString(12, date);
			ps.setString(13, infantId);
			ps.setString(14, date);
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
	@Path("fillStudentHistory/{infantId}")
	@Produces(MediaType.TEXT_XML)
	public String fillStudentHistory(@PathParam("infantId") String infantId){
		try {
			con = DatabaseHelper.connection();
			Statement stmt = con.createStatement();
			stmt.execute("SET @ROW:=0;");
			String query = "SELECT \r\n" + 
					"(@ROW:=@ROW+1) AS RowNum,\r\n" + 
					"CONCAT(DAYNAME(date_format(NOW()- INTERVAL a + b DAY ,'%Y-%m-%d')),' ',date_format(NOW()- INTERVAL a + b DAY ,'% %m/%d/%Y')) AS 'DATE',\r\n" + 
					"(SELECT \r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(IEF.Expected_Eat_Time,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_expected_food AS IEF \r\n" + 
					"INNER JOIN studentcheckin AS CHKIN\r\n" + 
					"on IEF.Student_CheckIn_Id=CHKIN.StudentCheckInId\r\n" + 
					"WHERE \r\n" + 
					"CHKIN.INFANT_ACCOUNT_DETAILS_ID=?\r\n" + 
					"AND DATE_FORMAT(IEF.Expected_Eat_Time,'%Y-%m-%d')=DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')\r\n" + 
					"AND DATE_FORMAT(IEF.Expected_Eat_Time,'%Y-%m-%d')=date_format(NOW()- INTERVAL a + b DAY ,'%Y-%m-%d')\r\n" + 
					"AND IEF.STATUS=1 \r\n" + 
					"AND CHKIN.STATUS=1) AS 'EXP_FOOD_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT \r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(IEG.Given_Time ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_given_food AS IEG \r\n" + 
					"INNER JOIN studentcheckout AS CHKOUT\r\n" + 
					"on IEG.Student_Check_OutId=CHKOUT.StudentCheckOutId \r\n" + 
					"WHERE \r\n" + 
					"CHKOUT.INFANT_ACCOUNT_DETAILS_ID=?\r\n" + 
					"AND DATE_FORMAT(IEG.Given_Time,'%Y-%m-%d')=date_format(NOW()- INTERVAL a + b DAY ,'%Y-%m-%d'))AS 'GIV_FOOD_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT\r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(IEN.ExpectedNapTime ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_expected_nap AS IEN\r\n" + 
					"INNER JOIN studentcheckin AS CHKIN\r\n" + 
					"ON IEN.Student_Check_In_Id=CHKIN.StudentCheckInId\r\n" + 
					"WHERE \r\n" + 
					"CHKIN.INFANT_ACCOUNT_DETAILS_ID=?\r\n" + 
					"AND DATE_FORMAT(IEN.ExpectedNapTime,'%Y-%m-%d')=DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')\r\n" + 
					"AND DATE_FORMAT(IEN.ExpectedNapTime,'%Y-%m-%d')=date_format(NOW()- INTERVAL a + b DAY ,'%Y-%m-%d')\r\n" + 
					"AND IEN.STATUS=1 \r\n" + 
					"AND CHKIN.STATUS=1) AS 'EXP_NAP_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT \r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(INAP.Start_Time ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_nap AS INAP \r\n" + 
					"INNER JOIN studentcheckout AS CHKOUT\r\n" + 
					"on INAP.Student_Check_Out_Id=CHKOUT.StudentCheckOutId \r\n" + 
					"WHERE \r\n" + 
					"CHKOUT.INFANT_ACCOUNT_DETAILS_ID=?\r\n" + 
					"AND DATE_FORMAT(INAP.Start_Time,'%Y-%m-%d')=date_format(NOW()- INTERVAL a + b DAY ,'%Y-%m-%d')) AS 'GIV_NAP_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT\r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(IEM.ExpectedMedicationTime ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_expected_medication AS IEM\r\n" + 
					"INNER JOIN studentcheckin AS CHKIN\r\n" + 
					"ON IEM.Student_Check_In_Id=CHKIN.StudentCheckInId\r\n" + 
					"WHERE \r\n" + 
					"CHKIN.INFANT_ACCOUNT_DETAILS_ID=?\r\n" + 
					"AND DATE_FORMAT(IEM.ExpectedMedicationTime,'%Y-%m-%d')=DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')\r\n" + 
					"AND DATE_FORMAT(IEM.ExpectedMedicationTime,'%Y-%m-%d')=date_format(NOW()- INTERVAL a + b DAY ,'%Y-%m-%d') \r\n" + 
					"AND IEM.STATUS=1 \r\n" + 
					"AND CHKIN.STATUS=1) AS 'EXP_MEDI_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT \r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(IGM.Given_Time ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_given_medication AS IGM \r\n" + 
					"INNER JOIN studentcheckout AS CHKOUT\r\n" + 
					"on IGM.Student_Check_Out_Id=CHKOUT.StudentCheckOutId \r\n" + 
					"WHERE \r\n" + 
					"CHKOUT.INFANT_ACCOUNT_DETAILS_ID=?\r\n" + 
					"AND DATE_FORMAT(IGM.Given_Time,'%Y-%m-%d')=date_format(NOW()- INTERVAL a + b DAY ,'%Y-%m-%d')) AS 'GIV_MEDI_TIME',\r\n" + 
					"\r\n" + 
					"(SELECT \r\n" + 
					"GROUP_CONCAT(DATE_FORMAT(ID.TimeChanged ,'%T') SEPARATOR ' , ') \r\n" + 
					"FROM infant_diaper AS ID \r\n" + 
					"INNER JOIN studentcheckout AS CHKOUT\r\n" + 
					"on ID.Student_Check_Out_Id=CHKOUT.StudentCheckOutId \r\n" + 
					"WHERE \r\n" + 
					"CHKOUT.INFANT_ACCOUNT_DETAILS_ID=?\r\n" + 
					"AND DATE_FORMAT(ID.TimeChanged,'%Y-%m-%d')=date_format(NOW()- INTERVAL a + b DAY ,'%Y-%m-%d')) AS 'GIV_DIAPER_CHANGEE'\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"FROM\r\n" + 
					" (SELECT 0 a UNION SELECT 1 a UNION SELECT 2 UNION SELECT 3\r\n" + 
					"    UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7\r\n" + 
					"    UNION SELECT 8 UNION SELECT 9 ) d,\r\n" + 
					" (SELECT 0 b UNION SELECT 10 UNION SELECT 20 \r\n" + 
					"    UNION SELECT 30 UNION SELECT 40) m\r\n" + 
					"WHERE (NOW() - INTERVAL 30 DAY ) + INTERVAL a + b DAY  <  NOW() \r\n"+
					"ORDER BY a + b ASC ";
			System.out.println(query);
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infantId);
			ps.setString(2, infantId);
			ps.setString(3, infantId);
			ps.setString(4, infantId);
			ps.setString(5, infantId);
			ps.setString(6, infantId);
			ps.setString(7, infantId);
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


	@POST
	@Path("sendCheckoutEmail")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void sendCheckoutEmail(MultivaluedMap<String, String> params){
		String body=params.getFirst("body");
		try{
			String from = "";
			String to = "";
			String cc="";
			String subject = "CPIMS checkout details ";
			SendEmail.sendMail(from, to,cc, subject, body);
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}


	@GET
	@Path("deleteNotes/{infant_id}/{selected_date}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void deleteNotes(@PathParam("infant_id") String infant_id,@PathParam("selected_date") String selected_date){
		try{
			con = DatabaseHelper.connection();
			String query="UPDATE studentcheckin AS CHKIN \r\n" + 
					"SET CHKIN.STATUS=0 \r\n" + 
					"WHERE CHKIN.INFANT_ACCOUNT_DETAILS_ID=? " +
					"AND DATE_FORMAT(CHKIN.CheckInDate,'%Y-%m-%d')=?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, infant_id);
			ps.setString(2, selected_date);
			ps.execute();

			query="DELETE FROM infant_notes WHERE INFANT_ACCOUNT_DETAILS_ID=? AND DATE_FORMAT(DATE_TIME,'%Y-%m-%d')=?";
			ps = con.prepareStatement(query);
			ps.setString(1, infant_id);
			ps.setString(2, selected_date);
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
}