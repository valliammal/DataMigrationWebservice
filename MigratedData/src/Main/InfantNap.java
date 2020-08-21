package Main;
import java.sql.ResultSet;
import java.sql.Statement;

public class InfantNap {

	int id = 0;  // contain the max id 
	boolean flag = false;
	Dabase_Connect conn = new Dabase_Connect();
	GetTableId getId = new GetTableId();
	String msg = "";
	int count = 0;
	int total = 1; // contain total rows of tables.
	int single_count = 0; // execute first time only.

	/**
	 * Insert Infant Nap Records.
	 * @return
	 */
	public String insert_Infant_Expected_Nap()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("InfantExpectedNap"); // take the max id of InfantExpectedNap.

				String query1 = "select * from ( " +
						"	select " +
						"	InfantExpectedNapId, StudentCheckInId,"+
						" replace(convert(varchar, ExpectedNapTime, 120),'/','-') as 'ExpectedNapTime', "+
						" replace(convert(varchar, ExpectedNapDuration, 120),'/','-') as 'ExpectedNapDuration', " +
						"	row_number() over (order by  InfantExpectedNapId) as RowNum,(select count(InfantExpectedNapId) from InfantExpectedNap) as Total " +
						"	from " +
						"	InfantExpectedNap " +
						"	) as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next())
				{
					Object Expected_Nap_Time = GlobalValues.date_format_time(rsSql.getObject(3));
					Object Expected_Nap_Duration = GlobalValues.date_format_time(rsSql.getObject(4));

					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into infant_expected_nap ( " +
							" Infant_Expected_Nap_Id_Old,Student_Check_In_Id,ExpectedNapTime,ExpectedNapDuration ) " +
							" values( " +
							" "+  rsSql.getObject(1) + ", "  +  // InfantExpectedNapId
							" ( select StudentCheckInId from studentcheckin where StudentCheckInIdOld="+ rsSql.getObject(2) + " )  , " +  //StudentCheckInId
							" "+  Expected_Nap_Time + ", "  +  // ExpectedNapTime
							" "+  Expected_Nap_Duration + " "  +  // ExpectedNapDuration
							" ) ";
					try {
						stMySql.executeUpdate(query2);
					} catch (Exception ex) {
						Logs.writeLog(ex,"InfantExpectedNap"+query2);
						ex.printStackTrace();
					}

					flag = true;
					count++;
				}


				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("InfantExpectedNap","infant_expected_nap","Infant_Expected_Nap_Id_Old"); // Update max id of Student.
					total = total-count;
					flag = false;
					System.out.println("InfantExpectedNap records successfully migrated [ Total Rows ] : "+ count);
					msg = "InfantExpectedNap records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in InfantExpectedNap");
					msg = "No new records found in InfantExpectedNap";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"InfantExpectedNap");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Infant Nap Records.
	 * @return
	 */
	public String insert_Infant_Nap()
	{
		try
		{

			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("InfantNap"); // take the max id of InfantExpectedNap.


				String query1 = "select * from ( " +
						" select " +
						"	InfantNapId, StudentCheckOutId, "+
						" replace(convert(varchar, ExpectedTime, 120),'/','-') as 'ExpectedTime', "+
						" replace(convert(varchar, ExpectedDuration, 120),'/','-') as 'ExpectedDuration', "+
						" replace(convert(varchar, StartTime, 120),'/','-') as 'StartTime', "+
						" replace(convert(varchar, EndTime, 120),'/','-') as 'EndTime', "+
						"	Image,  ExpectedNapId,Notes, " +
						"	row_number() over (order by  InfantNapId) as RowNum,(select count(InfantNapId) from InfantNap) as Total " +
						"	from " +
						"	InfantNap " + 
						"	) as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {

					Object Expected_Time = GlobalValues.date_format_time(rsSql.getObject(3));
					Object ExpectedDuration = GlobalValues.date_format_time(rsSql.getObject(4));
					Object StartTime = GlobalValues.date_format_time(rsSql.getObject(5));
					Object EndTime = GlobalValues.date_format_time(rsSql.getObject(6));

					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject(4).toString()) ;
						single_count = 1;
					}
					String query2 = " insert into infant_nap ( " +
							"Infant_Nap_Id_Old,Student_Check_Out_Id,Expected_Time,Expected_Duration, " +
							" Start_Time,End_Time,Image,Expected_Nap_Id )" +
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +  // InfantNapId
							" ( select StudentCheckOutId from studentcheckout where StudentCheckOutIdOld="+ rsSql.getObject(2) + " ) , " +   // StudentCheckOutId
							" "+  Expected_Time + ", "  +  // ExpectedTime
							" "+  ExpectedDuration + ", "  +  // ExpectedDuration
							" "+  StartTime + ", "  +  // StartTime
							" "+  EndTime + ", "  +  // EndTime
							"  "+  rsSql.getObject(7) + ", "  +  // Image
							" ( select Infant_Expected_Nap_Id from infant_expected_nap where Infant_Expected_Nap_Id_Old="+ rsSql.getObject(8) + " )  " +   // ExpectedNapId

					       " ) ";
					try {
						stMySql.executeUpdate(query2);
					} catch (Exception ex) {
						Logs.writeLog(ex,"infant_nap"+query2);
						ex.printStackTrace();
					}

					Object Note =  rsSql.getObject(9);

					if( Note != null &&  !Note.equals("")) {
						Note = Note.toString().replaceAll("'", " ");
						String query3 = " insert into infant_notes(NOTES,INFANT_ACCOUNT_DETAILS_ID,NOTES_DESCRIPTION,DATE_TIME,StudentCheckInIdOld) "+
								" values( " +
								" '"+ Note + "', " +  // OtherNotes
								" ( select infant_account_details_id from studentcheckout where StudentCheckOutIdOld="+ rsSql.getObject(2) + " ) , " +   // ExpectedFoodId
								" 'Infant_Nap' , " +
								" now() ,"+
								" "+  rsSql.getObject(2) + " "  +   // StudentCheckOutId
								" ) ";
						stMySql.executeUpdate(query3);

					}

					flag = true;
					count++;


				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("InfantNap","infant_nap","Infant_Nap_Id_Old"); // Update max id of InfantNap.
					total = total-count;
					flag = false;
					System.out.println("InfantNap records successfully migrated [ Total Rows ] : "+ count);
					msg = "InfantNap records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in InfantNap");
					msg = "No new records found in InfantNap";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"InfantNap");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Infant Diaper Records.
	 * @return
	 */
	public String insert_Infant_Diaper()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("InfantDiaper"); // take the max id of InfantExpectedNap.

				String query1 = "select * from ( " +
						" select " +
						" InfantDiaperId, StudentCheckOutId,"+
						" replace(convert(varchar, TimeChanged, 120),'/','-') as 'TimeChanged', "+
						" DiaperTypeId, Notes, " +
						" row_number() over (order by  InfantDiaperId) as RowNum,(select count(InfantDiaperId) from InfantDiaper) as Total " +
						" from InfantDiaper " +
						" ) as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object TimeChanged = GlobalValues.date_format_time(rsSql.getObject(3)); 

					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into infant_diaper ( " +
							" Infant_Diaper_Id_Old,Student_Check_Out_Id,TimeChanged,Diaper_Type_Id ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + " ,"  +   // InfantDiaperId
							" ( select StudentCheckOutId from studentcheckout where StudentCheckOutIdOld="+ rsSql.getObject(2) + " ) , " +   // StudentCheckOutId
							" "+  TimeChanged + ", "  +   // TimeChanged
							" ( select UnitOfMeasureValueId from unitofmeasurevalue where UnitOfMeasureValueIdOld="+ rsSql.getObject(4) + " )  " +   // DiaperTypeId
							" ) ";
					try {
						stMySql.executeUpdate(query2);
					} catch (Exception ex) {
						Logs.writeLog(ex,"infant_diaper"+query2);
						ex.printStackTrace();
					}

					Object Note= rsSql.getObject(5);

					if( Note != null &&  !Note.equals("")) {
						Note = Note.toString().replaceAll("'", " ");

						String query3 = " insert into infant_notes(NOTES,INFANT_ACCOUNT_DETAILS_ID,NOTES_DESCRIPTION,DATE_TIME,StudentCheckInIdOld) "+
								" values( " +
								" '"+  Note + "', " +  // OtherNotes
								" ( select infant_account_details_id from studentcheckout where StudentCheckOutIdOld="+ rsSql.getObject(2) + " ) , " +   // ExpectedFoodId
								" 'Infant_Diaper' , " +
								" now() ,"+
								" "+  rsSql.getObject(2) + " "  +   // StudentCheckOutId
								" ) ";
						try {
							stMySql.executeUpdate(query3);
						} catch (Exception ex) {
							Logs.writeLog(ex,"infant_notes"+query2);
							ex.printStackTrace();
						}
					}

					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("InfantDiaper","infant_diaper","Infant_Diaper_Id_Old"); // Update max id of InfantDiaper.
					total = total-count;
					flag = false;
					System.out.println("InfantDiaper records successfully migrated [ Total Rows ] : "+ count);
					msg = "InfantDiaper records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in InfantDiaper");
					msg = "No new records found in InfantDiaper";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"InfantDiaper");
			ex.printStackTrace();
		}
		return msg;

	}

	/**
	 * Insert Infant Activity Records.
	 * @return
	 */
	public String insert_Infant_Activity()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("InfantActivity");  // take the max id of InfantActivity.

				String query1 = " select * from ( " +
						"	select " +
						"	InfantActivityId, Name, DisplayText, ViewOrder, IsDeleted, " +
						"	row_number() over (order by  InfantActivityId) as RowNum,(select count(InfantActivityId) from InfantActivity) as Total " +
						"	from InfantActivity " +
						"	) as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object Name = rsSql.getObject(2);
					if(Name != null &&  !Name.equals("")) {
						Name = Name.toString().replaceAll("'", " ");
					}

					Object DisplayText = rsSql.getObject(3);
					if(DisplayText != null &&  !DisplayText.equals("")) {
						DisplayText = DisplayText.toString().replaceAll("'", " ");
					}

					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into infant_activity ( " +
							" Infant_Activity_Id_Old,Name,Display_Text,View_Order,Is_Deleted ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", " +  		// InfantActivityId
							" '"+  Name + "', " +  	// Name
							" '"+  DisplayText + "', " +  	// DisplayText
							" "+  rsSql.getObject(4) + ", " +  		// ViewOrder
							" "+  rsSql.getObject(5) + " " +  		// IsDeleted
							" ) ";
					stMySql.executeUpdate(query2);

					flag = true;
					count++;

				}
				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("InfantActivity","infant_activity","Infant_Activity_Id_Old"); // Update max id of InfantActivity.
					total = total-count;
					flag = false;
					System.out.println("InfantActivity records successfully migrated [ Total Rows ] : "+ count);
					msg = "InfantActivity records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in InfantActivity");
					msg = "No new records found in InfantActivity";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"InfantActivity");
			ex.printStackTrace();
		}
		return msg;

	}

	/**
	 * Insert Student Checkout infant Activity Records.
	 * @return
	 */
	public String insert_Student_CheckOut_Infant_Activity()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{


				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentCheckOutInfantActivity");  // take the max id of StudentCheckOutInfantActivity.

				String query1 =  " select * from ( " +
						"	select " +
						"	StudentCheckOutInfantActivityId, StudentCheckOutId, " + 
						"	InfantActivityId, "+
						"   replace(convert(varchar, ActivityTime, 120),'/','-') as 'ActivityTime', "+
						"   Notes, " +
						"	row_number() over (order by  StudentCheckOutInfantActivityId) as RowNum,(select count(StudentCheckOutInfantActivityId) from StudentCheckOutInfantActivity) as Total " +
						"	from StudentCheckOutInfantActivity " +
						"	) as Result " + 
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object ActivityTime = GlobalValues.date_format_time(rsSql.getObject(4)); 
					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into student_check_out_infant_activity (" +
							" Student_Check_Out_Infant_Activity_Id_Old,Student_Check_Out_Id,Infant_Activity_Id, " +
							"  Activity_Time ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", " +  		// StudentCheckOutInfantActivityId
							" ( select StudentCheckOutId from studentcheckout where StudentCheckOutIdOld="+ rsSql.getObject(2) + " ) , " +   // StudentCheckOutId
							" ( select Infant_Activity_Id from infant_activity where Infant_Activity_Id_Old="+ rsSql.getObject(3) + " ) , " +   // InfantActivityId
							" "+  ActivityTime + " " +  		// ActivityTime
							" ) " ;

					try {
						stMySql.executeUpdate(query2);
					} catch (Exception ex) {
						Logs.writeLog(ex,"student_check_out_infant_activity"+query2);
						ex.printStackTrace();
					}

					Object Note =  rsSql.getObject(5);

					if( Note != null &&  !Note.equals("") ) {
						Note = Note.toString().replaceAll("'", " ");
						String query3 = " insert into infant_notes(NOTES,INFANT_ACCOUNT_DETAILS_ID,NOTES_DESCRIPTION,DATE_TIME,StudentCheckInIdOld) "+
								" values( " +
								" '"+  Note + "', " +  // OtherNotes
								" ( select infant_account_details_id from studentcheckout where StudentCheckOutIdOld="+ rsSql.getObject(2) + " ) , " +   // infant_account_details_id
								" 'StudentCheckOutInfantActivity' , " +
								" now() ,"+
								" "+  rsSql.getObject(2) + " "  +   // StudentCheckOutId
								" ) ";
						stMySql.executeUpdate(query3);

					}

					flag = true;
					count++;


				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentCheckOutInfantActivity","student_check_out_infant_activity","Student_Check_Out_Infant_Activity_Id_Old"); // Update max id of StudentCheckOutInfantActivity.
					total = total-count;
					flag = false;
					System.out.println("StudentCheckOutInfantActivity records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentCheckOutInfantActivity records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentCheckOutInfantActivity");
					msg = "No new records found in StudentCheckOutInfantActivity";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentCheckOutInfantActivity");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert into Student Action Constraint Type Records.
	 * @return
	 */
	public String insert_Student_Action_Constraint_Type()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql =conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentActionConstraintType");  // take the max id of StudentActionConstraintType.

				String query1 = "select * from ( " +
						"	select " +
						"	StudentActionContraintTypeId, Name, " +
						"	row_number() over (order by  StudentActionContraintTypeId) as RowNum,(select count(StudentActionContraintTypeId) from StudentActionConstraintType) as Total " +
						"	from StudentActionConstraintType " +
						" ) as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object Name = rsSql.getObject(2);
					if( Name != null &&  !Name.equals("")) {
						Name = Name.toString().replaceAll("'", " ");
					}

					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into student_action_constraint_type ( " +
							" Student_Action_Contraint_Type_Id_Old,Name ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +   // StudentActionContraintTypeId
							" '"+  Name + "' "  +    // Name
							" ) ";
					stMySql.executeUpdate(query2);

					flag = true;
					count++;

				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentActionConstraintType","student_action_constraint_type","Student_Action_Contraint_Type_Id_Old"); // Update max id of StudentActionConstraintType.
					total = total-count;
					flag = false;
					System.out.println("StudentActionConstraintType records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentActionConstraintType records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentActionConstraintType");
					msg = "No new records found in StudentActionConstraintType";
				}
			}
			single_count = 0;
		}
		catch (Exception ex) 
		{
			Logs.writeLog(ex,"StudentActionConstraintType");
			ex.printStackTrace();
		}
		return msg;
	}


	public String insert_Student_Action_Type()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentActionType");  // take the max id of StudentActionConstraintType.

				String query1 = "select * from ( " +
						"	select " +
						"	StudentActionTypeId, Name, " +
						"	row_number() over (order by  StudentActionTypeId) as RowNum,(select count(StudentActionTypeId) from StudentActionType) as Total " +
						"	from StudentActionType " +
						"	) as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object Name = rsSql.getObject(2);
					if( Name != null &&  !Name.equals("")) {
						Name = Name.toString().replaceAll("'", " ");
					}

					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into  student_action_type ( " +
							" Student_Action_Type_Id_Old,Name ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +    // StudentActionTypeId
							" '"+  Name + "' "  +    // Name
							" ) ";

					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentActionType","student_action_type","Student_Action_Type_Id_Old"); // Update max id of StudentActionType.
					total = total-count;
					flag = false;
					System.out.println("StudentActionType records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentActionType records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentActionType");
					msg = "No new records found in StudentActionType";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentActionType");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Student Action Records.
	 * @return
	 */
	public String insert_Student_Action()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{


				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentAction");  // take the max id of StudentActionConstraintType.

				String query1 = "select * from ( " +
						"	select " +
						"	StudentActionId, StudentActionTypeId, Name, " +
						"	ReportText, IsDeleted, " +
						"	row_number() over (order by  StudentActionId) as RowNum,(select count(StudentActionId) from StudentAction) as Total " +
						"	from StudentAction " +
						"	) as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object Name = rsSql.getObject(3);
					if( Name != null &&  !Name.equals("")) {
						Name = Name.toString().replaceAll("'", " ");
					}

					Object ReportText = rsSql.getObject(4);
					if( ReportText != null &&  !ReportText.equals("")) {
						ReportText = ReportText.toString().replaceAll("'", " ");
					}
					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = "insert into student_action (  " +
							" Student_Action_Id_Old,Student_Action_Type_Id,Name,Report_Text,IsDeleted ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + " ,"  +    // StudentActionId
							" ( select Student_Action_Type_Id from student_action_type where Student_Action_Type_Id_Old="+ rsSql.getObject(2) + " ) , " +   // StudentActionTypeId
							" '"+  Name + "' ,"  +    // Name
							" '"+  ReportText + "', "  +    // ReportText
							" "+  rsSql.getObject(5) + " "  +      // IsDeleted
							" ) ";
					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentAction","student_action","Student_Action_Id_Old"); // Update max id of StudentAction.
					total = total-count;
					flag = false;
					System.out.println("StudentAction records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentAction records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentAction");
					msg = "No new records found in StudentAction";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentAction");
			ex.printStackTrace();
		}
		return msg;
	}


	public String insert_Student_Action_Constraint()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentActionConstraint");  // take the max id of StudentActionConstraint.

				String query1 = "select * from ( " +
						"	select " +
						"	StudentActionConstraintId, StudentActionConstraintTypeId, StudentActionId, " +
						"	Description, Message, "+
						" replace(convert(varchar, StartTime, 120),'/','-') as 'StartTime', "+
						" replace(convert(varchar, EndTime, 120),'/','-') as 'EndTime', "+
						"	row_number() over (order by  StudentActionConstraintId) as RowNum,(select count(StudentActionConstraintId) from StudentActionConstraint) as Total " +
						"	from StudentActionConstraint " +
						"	)as Result"	 +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {

					Object Description = rsSql.getObject(4);
					if( Description != null &&  !Description.equals("")) {
						Description = Description.toString().replaceAll("'", " ");
					}
					Object Message = rsSql.getObject(5);
					if( Message != null &&  !Message.equals("")) {
						Message = Message.toString().replaceAll("'", " ");
					}
					Object StartTime = GlobalValues.date_format_time(rsSql.getObject(6)); 
					Object EndTime = GlobalValues.date_format_time(rsSql.getObject(7)); 

					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into student_action_constraint ( " +
							"  Student_Action_Constraint_Id_Old,Student_Action_Constraint_Type_Id,Student_Action_Id, " +
							"  Description,Message,Start_Time,End_Time )" +
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +      // StudentActionConstraintId
							" ( select Student_Action_Contraint_Type_Id from student_action_constraint_type where Student_Action_Contraint_Type_Id_Old="+ rsSql.getObject(2) + " ) , " +   // StudentActionConstraintTypeId
							" ( select Student_Action_Id from student_action where Student_Action_Id_Old="+ rsSql.getObject(3) + " ) , " +   // StudentActionId
							" '"+  Description + "', "  +      // Description
							" '"+  Message + "', "  +      // Message
							" "+  StartTime + ", "  +      // StartTime
							" "+  EndTime + " "  +      // EndTime

					       " ) ";
					try {
						stMySql.executeUpdate(query2);
					} catch (Exception ex) {
						Logs.writeLog(ex,"student_action_constraint"+query2);
						ex.printStackTrace();
					}

					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentActionConstraint","student_action_constraint","Student_Action_Constraint_Id_Old"); // Update max id of StudentActionConstraint.
					total = total-count;
					flag = false;
					System.out.println("StudentActionConstraint records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentActionConstraint records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentActionConstraint");
					msg = "No new records found in StudentActionConstraint";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentActionConstraint");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Active Student Action Constraints Records.
	 * @return
	 */
	public String insert_Active_Student_Action_Constraint()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();
				id = getId.GetMaxId("ActiveStudentActionConstraint");  // take the max id of ActiveStudentActionConstraint.


				String query1 = "select * from ( " +
						"	select " +
						"	StudentId, "+
						" replace(convert(varchar, LogDate, 120),'/','-') as 'LogDate', "+
						" StudentActionConstraintId, " +
						"	row_number() over (order by  StudentId) as RowNum,(select count(StudentId) from ActiveStudentActionConstraint) as Total " +
						"	from ActiveStudentActionConstraint " +
						"	)as Result" +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object LogDate = GlobalValues.date_format_time(rsSql.getObject(2)); 

					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into active_student_action_constraint ( " +
							" INFANT_ACCOUNT_DETAILS_ID,Log_Date,Student_Action_Constraint_Id,StudentId )  " +	
							" values ( " +
							" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID="+ rsSql.getObject(1) + " ) , " +   // StudentId
							" "+  LogDate + ", "  +      // LogDate
							" ( select Student_Action_Constraint_Id from student_action_constraint where Student_Action_Constraint_Id_Old="+ rsSql.getObject(3) + " ) , " +   // StudentActionConstraintId
							" "+  rsSql.getObject(1) + " "  +      // StudentId
							" ) " ;
					try {
						stMySql.executeUpdate(query2);
					} catch (Exception ex) {
						Logs.writeLog(ex,"active_student_action_constraint"+query2);
						ex.printStackTrace();
					}

					flag = true;
					count++;

				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("ActiveStudentActionConstraint","active_student_action_constraint","StudentId"); // Update max id of ActiveStudentActionConstraint.
					total = total-count;
					flag = false;
					System.out.println("ActiveStudentActionConstraint records successfully migrated [ Total Rows ] : "+ count);
					msg = "ActiveStudentActionConstraint records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in ActiveStudentActionConstraint");
					msg = "No new records found in ActiveStudentActionConstraint";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"ActiveStudentActionConstraint");
			ex.printStackTrace();
		}
		return msg;
	}


	public String insert_Student_Action_Contstraint_Age_Block()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentActionContstraintAgeBlock");  // take the max id of StudentActionContstraintAgeBlock.


				String query1 = "select * from ( " +
						"	select " +
						"	StudentActionConstraintId, AgeInMonths, " +
						"	row_number() over (order by  StudentActionConstraintId) as RowNum,(select count(StudentActionConstraintId) from StudentActionContstraintAgeBlock) as Total " +
						"	from StudentActionContstraintAgeBlock " +
						"	)as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					} 
					String query2 = " insert into student_action_contstraint_age_block ( " +
							"  Student_Action_Constraint_Id,AgeInMonths ) " +
							" values ( " +
							" ( select Student_Action_Constraint_Id from student_action_constraint where Student_Action_Constraint_Id_Old="+ rsSql.getObject(1) + " ) , " +   // StudentActionConstraintId
							" "+  rsSql.getObject(2) + " "  +      // AgeInMonths
							" ) ";

					stMySql.executeUpdate(query2);

					flag = true;
					count++;

				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentActionContstraintAgeBlock","student_action_contstraint_age_block","Student_Action_Constraint_Id"); // Update max id of StudentActionContstraintAgeBlock.
					total = total-count;
					flag = false;
					System.out.println("StudentActionContstraintAgeBlock records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentActionContstraintAgeBlock records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentActionContstraintAgeBlock");
					msg = "No new records found in StudentActionContstraintAgeBlock";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentActionContstraintAgeBlock");
			ex.printStackTrace();
		}
		return msg;
	}
	/**
	 * Insert Constrains Student Action Records.
	 * @return
	 */
	public String insert_Constrained_Student_Actions()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("ConstrainedStudentActions");  // take the max id of ConstrainedStudentActions.


				String query1 = "select * from ( " +
						"	select " +
						"	StudentActionConstraintId, StudentActionId, " +
						"	row_number() over (order by  StudentActionConstraintId) as RowNum,(select count(StudentActionConstraintId) from ConstrainedStudentActions) as Total " +
						"	from ConstrainedStudentActions " +
						" )as Result" +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into constrained_student_actions ( " +
							" Student_Action_Constraint_Id,Student_Action_Id ) " +
							" values ( " +
							" ( select Student_Action_Constraint_Id from student_action_constraint where Student_Action_Constraint_Id_Old="+ rsSql.getObject(1) + " ) , " +   // StudentActionConstraintId
							" ( select Student_Action_Id from student_action where Student_Action_Id_Old="+ rsSql.getObject(2) + " )  " +   // StudentActionId
							" ) ";

					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("ConstrainedStudentActions","constrained_student_actions","Student_Action_Constraint_Id"); // Update max id of ConstrainedStudentActions.
					total = total-count;
					flag = false;
					System.out.println("ConstrainedStudentActions records successfully migrated [ Total Rows ] : "+ count);
					msg = "ConstrainedStudentActions records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in ConstrainedStudentActions");
					msg = "No new records found in ConstrainedStudentActions";
				}
			}
			single_count = 0;
		}
		catch (Exception ex) 
		{
			Logs.writeLog(ex,"ConstrainedStudentActions");
			ex.printStackTrace();
		}
		return msg;
	}
	/**
	 * Insert Student Action unblock
	 * @return
	 */
	public String insert_Student_Action_Unblock()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{


				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentActionUnblock");  // take the max id of StudentActionUnblock.


				String query1 = "select * from ( " +
						"	select " +
						"	StudentActionContraintId, StudentActionId, " +
						"	row_number() over (order by  StudentActionContraintId) as RowNum,(select count(StudentActionContraintId) from StudentActionUnblock) as Total " +
						"	from StudentActionUnblock " +
						" )as Result" +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next())	{
					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into student_action_unblock ( " +
							" Student_Action_Contraint_Id,Student_Action_Id ) " +
							" values ( " +
							" ( select Student_Action_Constraint_Id from student_action_constraint where Student_Action_Constraint_Id_Old="+ rsSql.getObject(1) + " ) , " +   // StudentActionConstraintId
							" ( select Student_Action_Id from student_action where Student_Action_Id_Old="+ rsSql.getObject(2) + " )  " +   // StudentActionId
							" ) ";

					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentActionUnblock","student_action_unblock","Student_Action_Contraint_Id"); // Update max id of StudentActionUnblock.
					total = total-count;
					flag = false;
					System.out.println("StudentActionUnblock records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentActionUnblock records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentActionUnblock");
					msg = "No new records found in StudentActionUnblock";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentActionUnblock");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Student Action Color Records.
	 * @return
	 */
	public String insert_Student_Action_Color()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{


				count=0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql  =  conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentActionColor");  // take the max id of StudentActionColor.


				String query1 = "select * from ( " +
						"	select " +
						"	StudentActionId, ColorName, " +
						"	row_number() over (order by  StudentActionId) as RowNum,(select count(StudentActionId) from StudentActionColor) as Total " +
						"	from StudentActionColor " +
						"	)as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into student_action_color ( " +
							" Student_Action_Id,Color_Name ) " +
							" values ( " +
							" ( select Student_Action_Id from student_action where Student_Action_Id_Old="+ rsSql.getObject(1) + " ) , " +   // StudentActionId
							" '"+  rsSql.getObject(2) + "' "  +      // ColorName
							" ) ";

					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}



				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentActionColor","student_action_color","Student_Action_Id"); // Update max id of StudentActionColor.
					total = total-count;
					flag = false;
					System.out.println("StudentActionColor records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentActionColor records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentActionColor");
					msg = "No new records found in StudentActionColor";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentActionColor");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Student Log Records.
	 * @return
	 */
	public String insert_Student_Log()
	{
		try
		{
			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentLog");  // take the max id of StudentLog.

				String query1 = "select * from ( " +
						"	select " +
						"	StudentLogId,StudentId, "+
						" replace(convert(varchar, LogDate, 111),'/','-') as 'LogDate', "+
						" replace(convert(varchar, EnteredDate, 111),'/','-') as 'EnteredDate', "+
						" LogEntry, " +
						"	Image, IsDeleted, Replicate, IsExpected, " +
						"	ExpectedDate, IsCheckInEntry, IsCompleted,  " +
						"	row_number() over (order by StudentLogId  ) as RowNum,(select count(StudentLogId) from StudentLog) as Total " +
						"	from StudentLog " +
						"	)as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object LogDate = GlobalValues.date_format(rsSql.getObject(3)); 
					Object EnteredDate = GlobalValues.date_format(rsSql.getObject(4)); 

					Object LogEntry = rsSql.getObject(5);
					if( LogEntry != null &&  !LogEntry.equals("")) {
						LogEntry = LogEntry.toString().replaceAll("'", " ");
					}

					Object ExpectedDate = GlobalValues.date_format(rsSql.getObject(10)); 

					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into student_log ( " +
							" Student_Log_Id_Old,INFANT_ACCOUNT_DETAILS_ID,Log_Date,Entered_Date,Log_Entry,  " +
							" Image,IsDeleted,Replicate,IsExpected,Expected_Date,Is_Check_In_Entry,IsCompleted )  " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +      // StudentLogId
							" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID="+ rsSql.getObject(2) + " ) , " +   // StudentId
							" "+  LogDate + ", "  +      // LogDate
							" "+  EnteredDate + ", "  +      // EnteredDate
							" "+  LogEntry + ", "  +      // LogEntry
							" "+  rsSql.getObject(6) + ", "  +        // Image
							" "+  rsSql.getObject(7) + ", "  +        // IsDeleted
							" "+  rsSql.getObject(8) + ", "  +        // Replicate
							" "+  rsSql.getObject(9) + ", "  +        // IsExpected
							" '"+  ExpectedDate + "', "  +     // ExpectedDate
							" "+  rsSql.getObject(11) + ", "  +       // IsCheckInEntry
							" "+  rsSql.getObject(12) + " "  +        // IsCompleted
							" ) " ;
					try {
						stMySql.executeUpdate(query2);
					} catch (Exception ex) {
						Logs.writeLog(ex,"student_log"+query2);
						ex.printStackTrace();
					}

					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentLog","student_log","Student_Log_Id_Old"); // Update max id of StudentLog.
					total = total-count;
					flag = false;
					System.out.println("StudentLog records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentLog records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentLog");
					msg = "No new records found in StudentLog";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentLog");
			ex.printStackTrace();
		}
		return msg;
	}


	public String insert_Student_Log_Student_Action()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{


				count = 0;
				Statement stSql  = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentLogStudentAction");  // take the max id of StudentLogStudentAction.


				String query1 = "select * from ( " +
						"	select  " +
						"	StudentLogId, StudentActionId, EntryOrder, " +
						"	row_number() over (order by StudentLogId  ) as RowNum,(select count(StudentLogId) from StudentLogStudentAction) as Total " +
						"	from StudentLogStudentAction " +
						"	)as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";


				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into student_log_student_action ( " +
							" Student_Log_Id,Student_Action_Id,Entry_Order ) " +
							" values (" +
							" ( select Student_Log_Id from student_log where Student_Log_Id_Old="+ rsSql.getObject(1) + " ) , " +   // StudentLogId
							" ( select Student_Action_Id from student_action where Student_Action_Id_Old="+ rsSql.getObject(2) + " ) , " +   // StudentActionId
							" "+  rsSql.getObject(3) + " "  +        // EntryOrder
							" ) ";
					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}


				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentLogStudentAction","student_log_student_action","Student_Log_Id"); // Update max id of StudentLogStudentAction.
					total = total-count;
					flag = false;
					System.out.println("StudentLogStudentAction records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentLogStudentAction records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentLogStudentAction");
					msg = "No new records found in StudentLogStudentAction";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentLogStudentAction");
			ex.printStackTrace();
		}
		return msg;
	}



}
