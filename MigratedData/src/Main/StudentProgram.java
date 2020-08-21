package Main;
import java.sql.ResultSet;
import java.sql.Statement;

public class StudentProgram {
	int id = 0;  // contain the max id 
	boolean flag = false;
	Dabase_Connect conn  =  new Dabase_Connect();
	GetTableId getId  =  new GetTableId();
	String msg = "";
	int count = 0;
	int total = 1; // contain total rows of tables.
	int single_count = 0; // execute first time only.

	/**
	 * Insert Student Program
	 * @return
	 */
	public String insert_Student_Program()
	{

		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql  =  conn.connectionmysql().createStatement();

				id  =  getId.GetMaxId("Program"); // take the max id of Program.

				String query1 = "select * from ( " +
						"	select " + 
						"	ProgramId, Name, ShortName, AbsentWeeks, Active, " +
						"	row_number() over (order by ProgramId ) as RowNum,(select count(ProgramId) from Program) as Total " +
						"	from Program " +
						"	)as Result " +
						" where RowNum > = "+ (++id) +" and RowNum < "+ (id +=  GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql  =  stSql.executeQuery(query1);

				while(rsSql.next()) {
					
					Object Name = rsSql.getObject(2);
					if( Name !=  null &&  !Name.equals("")) {
						Name = Name.toString().replaceAll("'", " ");
					}

					Object ShortName = rsSql.getObject(3);

					if( ShortName !=  null &&  !ShortName.equals("")) {
						ShortName = ShortName.toString().replaceAll("'", " ");
					}
					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into program ( " +
							" PROGRAM_ID_OLD,PROGRAM_NAME,PROGRAM_SHORT_NAME,ABSENT_WEEKS,ACTIVE )  " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +  			// ProgramId
							" '"+  Name + "', "  +  		// Name
							" '"+  ShortName + "', "  +  		// ShortName
							" "+  rsSql.getObject(4) + ", "  +  			// AbsentWeeks
							" "+  rsSql.getObject(5) + " "  +  			// Active
							" ) ";

					stMySql.executeUpdate(query2);

					flag = true;
					count++;


				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("Program","program","PROGRAM_ID_OLD"); // Update max id of Program.
					total = total-count;
					flag = false;
					System.out.println("Program records successfully migrated [ Total Rows ] : "+ count);
					msg = "Program records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in Program");
					msg = "No new records found in Program";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"Program");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Enrollment Change Type Records.
	 * @return
	 */
	public String insert_Enrollment_Change_Type()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{


				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql  =  conn.connectionmysql().createStatement();

				id  =  getId.GetMaxId("EnrollmentChangeType"); // take the max id of EnrollmentChangeType.

				String query1 = "select * from ( " +
						"	select " + 
						"	EnrollmentChangeTypeId, Name, " +
						"	row_number() over (order by EnrollmentChangeTypeId  ) as RowNum,(select count(EnrollmentChangeTypeId) from EnrollmentChangeType) as Total " +
						"	from EnrollmentChangeType " +
						"	)as Result " +
						" where RowNum > = "+ (++id) +" and RowNum < "+ (id +=  GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql  =  stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object Name = rsSql.getObject(2);

					if( Name !=  null &&  !Name.equals("")) {
						Name = Name.toString().replaceAll("'", " ");
					}

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into enrollment_change_type (" +
							" Enrollment_Change_Type_Id_Old,Name ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +  			// EnrollmentChangeTypeId
							" '"+  Name + "' "  +  		// Name
							" ) ";
					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("EnrollmentChangeType","enrollment_change_type","Enrollment_Change_Type_Id_Old"); // Update max id of EnrollmentChangeType.
					total = total-count;
					flag = false;
					System.out.println("EnrollmentChangeType records successfully migrated [ Total Rows ] : "+ count);
					msg = "EnrollmentChangeType records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in EnrollmentChangeType");
					msg = "No new records found in EnrollmentChangeType";
				}
			}
			single_count = 0;
		}
		catch (Exception ex) 
		{
			Logs.writeLog(ex,"EnrollmentChangeType");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Enrollment End Status Records.
	 * @return
	 */
	public String insert_Enrollment_End_Status()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{


				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql  =  conn.connectionmysql().createStatement();

				id  =  getId.GetMaxId("EnrollmentEndStatus"); // take the max id of EnrollmentChangeType.

				String query1 = "select * from ( " +
						"	select  " +
						"	EnrollmentEndStatusId, Name, " +
						"	row_number() over (order by EnrollmentEndStatusId  ) as RowNum,(select count(EnrollmentEndStatusId) from EnrollmentEndStatus) as Total " +
						"	from EnrollmentEndStatus " +
						"	)as Result  " +
						" where RowNum > = "+ (++id) +" and RowNum < "+ (id +=  GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql  =  stSql.executeQuery(query1);

				while(rsSql.next()) {

					Object Name = rsSql.getObject(2);
					if( Name  !=  null &&  !Name.equals("")) {
						Name = Name.toString().replaceAll("'", " ");
					}

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into enrollment_end_status ( " +
							" Enrollment_End_Status_Id_Old,Name ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +  			// EnrollmentEndStatusId
							" '"+  Name + "' "  +  		// Name
							" ) " ;

					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("EnrollmentEndStatus","enrollment_end_status","Enrollment_End_Status_Id_Old"); // Update max id of EnrollmentEndStatus.
					total = total-count;
					flag = false;
					System.out.println("EnrollmentEndStatus records successfully migrated [ Total Rows ] : "+ count);
					msg = "EnrollmentEndStatus records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in EnrollmentEndStatus");
					msg = "No new records found in EnrollmentEndStatus";
				}
			}

			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"EnrollmentEndStatus");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Enrollment Records.
	 * @return
	 */
	public String insert_Enrollment()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql  =  conn.connectionmysql().createStatement();

				id  =  getId.GetMaxId("Enrollment"); // take the max id of EnrollmentChangeType.

				String query1 = "select * from ( " +
						"	select " +
						"	EnrollmentId,"+
						" 	replace(convert(varchar, EnteredDate, 111),'/','-') as 'EnteredDate',"+
						"   replace(convert(varchar, StartDate, 111),'/','-') as 'StartDate', "+
						"   replace(convert(varchar, EndDate, 111),'/','-') as 'EndDate', " + 
						"	StudentId, ClassroomId, ProgramId, " +
						"	DaysAttending, BreakfastPlan, EnrollmentChangeTypeId, EnrollmentEndStatusId, " +
						"	row_number() over (order by EnrollmentId  ) as RowNum,(select count(EnrollmentId) from Enrollment) as Total " +
						"	from Enrollment " +
						"	)as Result " +
						" where RowNum > = "+ (++id) +" and RowNum < "+ (id +=  GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql  =  stSql.executeQuery(query1);


				while(rsSql.next()) {
					Object EnteredDate  =  GlobalValues.date_format(rsSql.getObject(2));
					Object StartDate  =  GlobalValues.date_format(rsSql.getObject(3));
					Object EndDate  =  GlobalValues.date_format(rsSql.getObject(4)); 

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into enrollment (  " +
							" Enrollment_Id_Old,Entered_Date,Start_Date,End_Date,INFANT_ACCOUNT_DETAILS_ID, " +
							" Classroom_Id,Program_Id,Days_Attending,Break_fast_Plan,Enrollment_Change_Type_Id, " +
							" Enrollment_End_Status_Id ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +  			// EnrollmentId
							" "+  EnteredDate + ", "  +  			// EnteredDate
							" "+  StartDate + ", "  +  			// StartDate
							" "+  EndDate + ", "  +  			// EndDate
							" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID = "+ rsSql.getObject(5) + " limit 1 )  , " +  //StudentId
							" ( select CLASSROOM_ID from classroom where Classroom_id_old = "+ rsSql.getObject(6) + " )  , " +  //ClassroomId
							" ( select PROGRAM_ID from program where PROGRAM_ID_OLD = "+ rsSql.getObject(7) + " )  , " +  //ProgramId
							" "+  rsSql.getObject(8) + ", "  +  			// DaysAttending
							" "+  rsSql.getObject(9) + ", "  +  			// BreakfastPlan
							" ( select Enrollment_Change_Type_Id from enrollment_change_type where Enrollment_Change_Type_Id_Old = "+ rsSql.getObject(10) + " )  , " +  //EnrollmentChangeTypeId
							" ( select Enrollment_End_Status_Id from enrollment_end_status where Enrollment_End_Status_Id_Old = "+ rsSql.getObject(11) + " )   " +  //EnrollmentEndStatusId
							" ) " ;

					try {
						stMySql.executeUpdate(query2);
					} catch (Exception ex) {
						Logs.writeLog(ex,"Insert_Enrollment#enrollment("+query2+")");
						ex.printStackTrace();
					}

					flag = true;
					count++;
				}
				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("Enrollment","enrollment","Enrollment_Id_Old"); // Update max id of Enrollment.
					total = total-count;
					flag = false;
					System.out.println("Enrollment records successfully migrated [ Total Rows ] : "+ count);
					msg = "Enrollment records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in Enrollment");
					msg = "No new records found in Enrollment";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"Enrollment");
			ex.printStackTrace();
		}
		return msg;
	}
	/**
	 * Insert Student Back Records.
	 * @return
	 */
	public String insert_Student_Bak()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql  =  conn.connectionmysql().createStatement();

				id  =  getId.GetMaxId("Studentbak"); // take the max id of Studentbak.

				String query1 = "select * from ( " +
						"	select " +
						"	StudentId,Locationid, SB.ClassroomId, ProgramId, "+
						"   replace(convert(varchar, StartDate, 111),'/','-') as 'StartDate' , DaysPerWeek, IsToiletTrained," + 
						"	IsOnBreakfastPlan,IsOnWaitingList, " +
						"	 Mon, Tue, Wed, Thu, Fri, SB.ImportId, " +
						"	row_number() over (order by StudentId  ) as RowNum,(select count(StudentId) from Studentbak) as Total " +
						"	from Studentbak as SB " +
						"	inner join classroom as cls on SB.ClassroomId = cls.ClassroomId " +
						"	)as Result   " +
						" where RowNum > = "+ (++id) +" and RowNum < "+ (id +=  GlobalValues.rowIncrement) + "  ";


				ResultSet rsSql  =  stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object StartDate  =  GlobalValues.date_format(rsSql.getObject(5));

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into infant_course_details ( " +
							" INFANT_ACCOUNT_DETAILS_ID,SCHOOL_INFO_ID,CLASSROOM_ID,PROGRAM_ID,START_DATE,DAYS_PER_WEEK,STUDENT_ID_OLD ) " +
							" values ( " +
							" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID = "+ rsSql.getObject(1) + " limit 1)  , " +  //StudentId
							" ( select SCHOOL_INFO_ID from school_info where Location_Id = "+ rsSql.getObject(2) + " )  , " +  //Locationid
							" ( select CLASSROOM_ID from classroom where Classroom_id_old = "+ rsSql.getObject(3) + " )  , " +  //ClassroomId
							" ( select PROGRAM_ID from program where PROGRAM_ID_OLD = "+ rsSql.getObject(4) + " )  , " +  //ClassroomId
							" "+  StartDate + ", "  +  			// StartDate
							" "+  rsSql.getObject(6) + ", "  +  			// DaysPerWeek
							" "+  rsSql.getObject(1) + " "  +  			// StudentId

					 	   " ) ";

					try {
						stMySql.executeUpdate(query2);
					} catch (Exception ex) {
						Logs.writeLog(ex,"Insert_Enrollment#infant_course_details("+query2+")");
						ex.printStackTrace();
					}


					// Insert into Infant Additional Info Table.

					String query3 = " insert into infant_additional_info ( " +
							" INFANT_ACCOUNT_DETAILS_ID,IS_TOILET_TRAINED,IS_ON_BREAKFAST_PLAN,IS_ON_WAITING_LIST ) " +
							" values ( " +
							" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID = "+ rsSql.getObject(1) + " )  , " +  //StudentId
							" "+  rsSql.getObject(7) + " ,"  +     // IsToiletTrained
							" "+  rsSql.getObject(8) + " ,"  +     // IsOnBreakfastPlan
							" "+  rsSql.getObject(9) + " "  +     // IsOnWaitingList
							" ) " ;

					stMySql.executeUpdate(query3);

					//------------------------------------------


					// Insert into Infant Breakfast Plan Table.

					Object importid = rsSql.getObject(15);
					Object import_id = 0;
					if( importid  ==  null) {
						import_id = 0;
					} else {
						import_id = importid;
					}

					String query4 = " insert into infant_breakfast_plan ( " +
							" INFANT_ACCOUNT_DETAILS_ID,MON,TUE,WED,THU,FRI,IMPORT_ID ) " +
							" values ( " +
							" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID = "+ rsSql.getObject(1) + " )  , " +  //StudentId
							" "+  rsSql.getObject(10) + " ,"  +     // MON
							" "+  rsSql.getObject(11) + " ,"  +     // TUE
							" "+  rsSql.getObject(12) + " ,"  +     // WED
							" "+  rsSql.getObject(13) + " ,"  +     // THU
							" "+  rsSql.getObject(14) + " ,"  +     // FRI
							" "+  import_id + " "  +     //  IMPORT_ID
							" ) ";

					stMySql.executeUpdate(query4);

					//------------------------------------------

					flag = true;
					count++;
				}




				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("Studentbak","infant_course_details","STUDENT_ID_OLD"); // Update max id of Studentbak.
					total = total-count;
					flag = false;
					System.out.println("Studentbak records successfully migrated [ Total Rows ] : "+ count);
					msg = "Studentbak records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in Studentbak");
					msg = "No new records found in Studentbak";
				}
			}
			single_count = 0;
		}
		catch (Exception ex) 
		{
			Logs.writeLog(ex,"Studentbak");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Student Roll As a Attendance 
	 * @return
	 */
	public String insert_StudentRoll() {
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql  =  conn.connectionmysql().createStatement();

				id  =  getId.GetMaxId("StudentRoll"); // take the max id of StudentRoll.

				String query1 = "select * from (select " +
						"	StudentRollId,  replace(convert(varchar, AttendDate, 111),'/','-') as 'AttendDate' ,"+
						"   Locationid, StudentRoll.ClassroomId, " + 
						"	StudentId,Username, Attended, Expected, Replicate, " +
						"	row_number() over (order by StudentRollId) as RowNum,(select count(StudentRollId) from StudentRoll) as Total " +
						"	from StudentRoll " +
						"	inner join classroom on StudentRoll.ClassroomId = classroom.ClassroomId " +
						"	)as Result  " +
						" where RowNum > = "+ (++id) +" and RowNum < "+ (id +=  GlobalValues.rowIncrement) + "  "; 

				ResultSet rsSql  =  stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object AttendDate  =  GlobalValues.date_format(rsSql.getObject(2));

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into attendance (  " +
							" Student_Roll_Id,ATTENDANCE_DATE_TIME,SCHOOL_INFO_ID ,CLASSROOM_ID )"	+
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +     //  StudentRollId
							" "+  AttendDate + ", "  +     //  AttendDate
							" ( select SCHOOL_INFO_ID from school_info where Location_Id = "+ rsSql.getObject(3) + " )  , " +  //Locationid
							" ( select CLASSROOM_ID from classroom where Classroom_id_old = "+ rsSql.getObject(4) + " )   " +  //ClassroomId

		 			   " ) " ;
					try {
						stMySql.executeUpdate(query2);
					} catch (Exception ex) {
						Logs.writeLog(ex,"Insert_Enrollment#attendance ("+query2+")");
						ex.printStackTrace();
					}

					// Insert into Infant Attendance 

					Object Username = rsSql.getObject(6);

					if( Username  !=  null &&  !Username.equals("")) {
						Username = Username.toString().replaceAll("'", " ");
					}

					String query3 = " insert into infant_attendance (" +
							"STUDENT_ROLL_ID, ATTENDANCE_ID,INFANT_ACCOUNT_DETAIL_ID,USER_NAME,PRESENT,EXPECTED,REPLICATE ) " +
							" values (  " +
							" "+  rsSql.getObject(1) + " ,"  +     //  STUDENT_ROLL_ID
							" (select max(ATTENDANCE_ID) from attendance) ," +
							" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID = "+ rsSql.getObject(5) + " limit 1)  , " +  //StudentId
							" '"+  Username + "', "  +     //  Username
							" "+  rsSql.getObject(7) + ", "  +       //  Attended
							" "+  rsSql.getObject(8) + ", "  +       //  Expected
							" "+  rsSql.getObject(9) + " "  +       //  Replicate

					 	   " ) " ;

					try {
						stMySql.executeUpdate(query3);
					} catch (Exception ex) {
						Logs.writeLog(ex,"Insert_Enrollment#infant_attendance (" + query3 + ")");
						ex.printStackTrace();
					}
					//------------------------------

					flag = true;
					count++;
				}		

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentRoll","attendance","ATTENDANCE_ID"); // Update max id of StudentRoll.
					total = total-count;
					flag = false;
					System.out.println("StudentRoll records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentRoll records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentRoll");
					msg = "No new records found in StudentRoll";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentRoll");
			ex.printStackTrace();
		}
		return msg;

	}

	/**
	 * Insert Student Absent Week Records.
	 * @return
	 */
	public String insert_Student_Absent_Week()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql  =  conn.connectionmysql().createStatement();

				id  =  getId.GetMaxId("StudentAbsentWeek"); // take the max id of StudentAbsentWeek.

				String query1 = "select * from ( "+
						"	select " +
						"	StudentAbsentWeekId, StudentId, "+
						"   replace(convert(varchar, RequestDate, 111),'/','-') as 'RequestDate', " +
						"	replace(convert(varchar, StartDate, 111),'/','-') as 'StartDate', "+
						"   replace(convert(varchar, EndDate, 111),'/','-') as 'EndDate' , " +
						"	row_number() over (order by StudentAbsentWeekId  ) as RowNum,(select count(StudentAbsentWeekId) from StudentAbsentWeek) as Total " +
						"	from StudentAbsentWeek " +
						"	)as Result" +
						" where RowNum > = "+ (++id) +" and RowNum < "+ (id +=  GlobalValues.rowIncrement) + "  "; 

				ResultSet rsSql  =  stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object RequestDate  =  GlobalValues.date_format(rsSql.getObject(3));
					Object StartDate  =  GlobalValues.date_format(rsSql.getObject(4));
					Object EndDate  =  GlobalValues.date_format(rsSql.getObject(5));

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into Student_Absent_Week ( " +
							"Student_Absent_Week_Id_Old, INFANT_ACCOUNT_DETAILS_ID,Request_Date,Start_Date,End_Date ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + " ,"  +       //  StudentAbsentWeekId
							" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID = "+ rsSql.getObject(2) + " )  , " +  //StudentId
							" "+  RequestDate + " ,"  +       //  RequestDate
							" "+  StartDate + " ,"  +       //  StartDate
							" "+  EndDate + " "  +       //  EndDate

					 	   " ) ";

					try {
						stMySql.executeUpdate(query2);
					} catch (Exception ex) {
						Logs.writeLog(ex,"enrollment#Student_Absent_Week("+query2+")");
						ex.printStackTrace();
					}

					flag = true;
					count++;
				}
				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentAbsentWeek","Student_Absent_Week","Student_Absent_Week_Id_Old"); // Update max id of StudentAbsentWeek.
					total = total - count;
					flag = false;
					System.out.println("StudentAbsentWeek records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentAbsentWeek records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentAbsentWeek");
					msg = "No new records found in StudentAbsentWeek";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentAbsentWeek");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Student Pickup Authorized Records.
	 * @return
	 */
	public String insert_Student_Pickup_Authorized()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql  =  conn.connectionmysql().createStatement();

				id  =  getId.GetMaxId("StudentPickupAuthorized"); // take the max id of StudentPickupAuthorized.

				String query1 = "select * from ( " +
						"	select " +
						"	StudentPickupAuthorizedId, StudentId, LastName, " +
						"	FirstName, Phone, StudentRelationTypeId, " +
						"	row_number() over (order by StudentPickupAuthorizedId  ) as RowNum,(select count(StudentPickupAuthorizedId) from StudentPickupAuthorized) as Total " +
						"	from StudentPickupAuthorized " +
						"	)as Result " +
						" where RowNum > = "+ (++id) +" and RowNum < "+ (id +=  GlobalValues.rowIncrement) + "  "; 

				ResultSet rsSql  =  stSql.executeQuery(query1);

				while(rsSql.next()) {

					Object LastName = rsSql.getObject(3);
					if( LastName  !=  null &&  !LastName.equals("")) {
						LastName = LastName.toString().replaceAll("'", " ");
					}

					Object FirstName = rsSql.getObject(4);
					if( FirstName  !=  null &&  !FirstName.equals("")) {
						FirstName = FirstName.toString().replaceAll("'", " ");
					}
					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into student_pickup_authorized ( " +
							" Student_Pickup_Authorized_Id_Old,INFANT_ACCOUNT_DETAILS_ID,Last_Name,First_Name,Phone,  " +
							"  RELATIONS_ID ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + " ,"  +       //  StudentPickupAuthorizedId
							" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID = "+ rsSql.getObject(2) + " )  , " +  //StudentId
							" '"+  LastName + "' ,"  +       //  LastName
							" '"+ FirstName + "' ,"  +       //  FirstName
							" '"+  rsSql.getObject(5) + "' ,"  +       //  Phone
							" ( select RELATIONS_ID from relations where STUDENT_RELATIONS_ID_OLD = "+ rsSql.getObject(6) + " )   " +  //StudentRelationTypeId
							" ) ";

					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}

				stMySql.close();			 


				if(flag) {
					getId.UpdateMaxId("StudentPickupAuthorized","student_pickup_authorized","Student_Pickup_Authorized_Id_Old"); // Update max id of StudentPickupAuthorized.
					total = total-count;
					flag = false;
					System.out.println("StudentPickupAuthorized records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentPickupAuthorized records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentPickupAuthorized");
					msg = "No new records found in StudentPickupAuthorized";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentPickupAuthorized");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Special Condition
	 * @return
	 */
	public String insert_Special_Condition()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql  =  conn.connectionmysql().createStatement();

				id  =  getId.GetMaxId("SpecialCondition"); // take the max id of SpecialCondition.


				String query1 = "select * from ( " +
						"	select " +
						"	SpecialConditionId, Description, Color, ViewOrder, " +
						"	row_number() over (order by SpecialConditionId  ) as RowNum,(select count(SpecialConditionId) from SpecialCondition) as Total " +
						"	from SpecialCondition " +
						"	) as Result " +
						" where RowNum > = "+ (++id) +" and RowNum < "+ (id +=  GlobalValues.rowIncrement) + "  "; 

				ResultSet rsSql  =  stSql.executeQuery(query1);

				while(rsSql.next())
				{
					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					Object Description = rsSql.getObject(2);

					if( Description  !=  null &&  !Description.equals("")) {
						Description = Description.toString().replaceAll("'", " ");
					}

					String query2 = " insert into special_condition (" +
							" Special_Condition_Id_Old,Description,Color,View_Order ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + " ,"  +       //  SpecialConditionId
							" '"+  Description + "' ,"  +       //  Description
							" '"+  rsSql.getObject(3) + "' ,"  +       //  Color
							" "+  rsSql.getObject(4) + " "  +       //  ViewOrder
							" ) " ;

					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}


				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("SpecialCondition","special_condition","Special_Condition_Id_Old"); // Update max id of SpecialCondition.
					total = total-count;
					flag = false;
					System.out.println("SpecialCondition records successfully migrated [ Total Rows ] : "+ count);
					msg = "SpecialCondition records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in SpecialCondition");
					msg = "No new records found in SpecialCondition";
				}
			}
			single_count = 0;
		}
		catch (Exception ex) 
		{
			Logs.writeLog(ex,"SpecialCondition");
			ex.printStackTrace();
		}
		return msg;

	}

	/**
	 * Insert Student Special Condition Records.
	 * @return
	 */
	public String insert_Student_Special_Condition()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql  =  conn.connectionmysql().createStatement();

				id  =  getId.GetMaxId("StudentSpecialCondition"); // take the max id of StudentSpecialCondition.


				String query1 = " select * from ( " +
						"	select " +
						"	SpecialConditionId, StudentId, " +
						"	row_number() over (order by SpecialConditionId  ) as RowNum,(select count(SpecialConditionId) from StudentSpecialCondition) as Total " +
						"	from StudentSpecialCondition " +
						"	) as Result  " +
						" where RowNum > = "+ (++id) +" and RowNum < "+ (id +=  GlobalValues.rowIncrement) + "  "; 

				ResultSet rsSql  =  stSql.executeQuery(query1);

				while(rsSql.next()) {
					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into student_special_condition (Special_Condition_Id,INFANT_ACCOUNT_DETAILS_ID ) " +
							" values ( " +
							" ( select Special_Condition_Id from special_condition where Special_Condition_Id_Old = "+ rsSql.getObject(1) + " ) ,  " +  //SpecialConditionId
							" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID = "+ rsSql.getObject(2) + " )   " +  //StudentId
							" ) ";

					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentSpecialCondition","student_special_condition","Special_Condition_Id"); // Update max id of StudentSpecialCondition			flag = false;
					total = total-count;
					System.out.println("StudentSpecialCondition records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentSpecialCondition records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentSpecialCondition");
					msg = "No new records found in StudentSpecialCondition";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentSpecialCondition");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Student Action Graph Records.
	 * @return
	 */
	public String insert_Student_Action_Graph()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql  =  conn.connectionmysql().createStatement();

				id  =  getId.GetMaxId("StudentActionGraph"); // take the max id of StudentActionGraph.


				String query1 = " select * from ( " +
						"	select " +
						"	StudentActionId, ParentStudentActionId, ViewOrder, " +
						"	row_number() over (order by StudentActionId  ) as RowNum,(select count(StudentActionId) from StudentActionGraph) as Total " +
						"	from StudentActionGraph " +
						"	)as Result  " +
						" where RowNum > = "+ (++id) +" and RowNum < "+ (id +=  GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql  =  stSql.executeQuery(query1);

				while(rsSql.next())
				{
					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into student_action_graph (Student_Action_Id,Parent_Student_Action_Id,View_Order )  " +
							" values (" +
							" ( select Student_Action_Id from student_action where Student_Action_Id_Old = "+ rsSql.getObject(1) + " ) , " +  //StudentActionId
							" ( select Student_Action_Id from student_action where Student_Action_Id_Old = "+ rsSql.getObject(2) + " ) , " +  //StudentActionId
							" "+  rsSql.getObject(3) + " "  +       //  ViewOrder
							" ) " ;

					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentActionGraph","student_action_graph","Student_Action_Id"); // Update max id of StudentActionGraph
					total = total-count;
					System.out.println("StudentActionGraph records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentActionGraph records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentActionGraph");
					msg = "No new records found in StudentActionGraph";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentActionGraph");
			ex.printStackTrace();
		}
		return msg;

	}


}
