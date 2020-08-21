package Main;
import java.sql.ResultSet;
import java.sql.Statement;

public class InfantUnitMesure {

	int id = 0;  // contain the max id 
	boolean flag = false;
	Dabase_Connect conn = new Dabase_Connect();
	GetTableId getId = new GetTableId();
	String msg = "";
	int count = 0;
	int total = 1; // contain total rows of tables.
	int single_count = 0; // execute first time only.

	/**
	 * Insert Unit measure records.
	 * @return
	 */
	public String insert_Unit_Measure()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("UnitOfMeasure"); // take the max id of UnitOfMeasure.
				String query1=" Select * from( " + 
						"	select  " +
						"	UnitOfMeasureId, Name, Abbreviation, IsDeleted, " +
						"	row_number() over (order by  UnitOfMeasureId) as RowNum,(select count(UnitOfMeasureId) from UnitOfMeasure) as Total " +
						"	from " +
						"	UnitOfMeasure)as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into unitofmeasure (  " +
							" UnitOfMeasureIdOld,  Name, Abbreviation,IsDeleted) " +
							" values( " +
							" "+  rsSql.getObject(1) + ", "  +
							" '"+ rsSql.getObject(2).toString().replaceAll("'", " ") + "', "  +
							" '"+ rsSql.getObject(3) + "', "  +
							" "+ rsSql.getObject(4) + " "  +
							" ) ";

					stMySql.executeUpdate(query2);
					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("UnitOfMeasure","unitofmeasure","UnitOfMeasureIdOld"); // Update max id of Student.
					total = total-count;
					flag = false;
					System.out.println("UnitOfMeasure records successfully migrated [ Total Rows ] : "+ count);
					msg = "UnitOfMeasure records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in UnitOfMeasure");
					msg = "No new records found in UnitOfMeasure";
				}

			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"UnitOfMeasure");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Unit measure values records.
	 * @return
	 */
	public String insert_Unit_Value()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				count=0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("UnitOfMeasureValue"); // take the max id of UnitOfMeasure.


				String query1 = " Select * from( " +
						"	select " + 
						"	UnitOfMeasureValueId, UnitOfMeasureId, Name, ViewOrder, IsDeleted, " +
						"	row_number() over (order by  UnitOfMeasureValueId) as RowNum,(select count(UnitOfMeasureValueId) from UnitOfMeasureValue) as Total " +
						"	from  " +
						"	UnitOfMeasureValue) as Result" +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);


				while(rsSql.next()) {
					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2=" insert into unitofmeasurevalue " +
							"  ( UnitOfMeasureValueIdOld,UnitOfMeasureId,Name,ViewOrder,IsDeleted ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +
							" ( select UnitOfMeasureId from unitofmeasure where UnitOfMeasureIdOld="+ rsSql.getObject(2) + " )  , " + 
							" '"+  rsSql.getObject(3).toString().replaceAll("'", " ") + "', "  +
							" "+  rsSql.getObject(4) + ", "  +
							" "+  rsSql.getObject(5) + " "  +
							" ) " ;
					stMySql.executeUpdate(query2);
					flag = true;
					count++;
				}
				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("UnitOfMeasureValue","unitofmeasurevalue","UnitOfMeasureValueIdOld"); // Update max id of Student.
					total = total-count;
					flag = false;
					System.out.println("UnitOfMeasureValue records successfully migrated [ Total Rows ] : "+ count);
					msg = "UnitOfMeasureValue records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in UnitOfMeasureValue");
					msg = "No new records found in UnitOfMeasureValue";
				}

			}
			single_count=0;

		} catch (Exception ex) {
			Logs.writeLog(ex,"UnitOfMeasureValue");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert food records.
	 * @return
	 */
	public String insert_Food()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("Food"); // take the max id of Food.


				String query1=" Select * from( " + 
						"	select  " +
						"	InfantFoodId, Name, InfantFoodUnitOfMeasureId, ViewOrder, IsDeleted, " +
						"	row_number() over (order by  InfantFoodId) as RowNum,(select count(InfantFoodId) from InfantFood) as Total " +
						"	from " +
						"	InfantFood)as Result" + 
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next())
				{
					if(single_count==0)
					{
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into infantfood ( InfantFoodIdOld,Name,InfantFoodUnitOfMeasureId,ViewOrder,IsDeleted )" +
							" values ( "+
							" "+  rsSql.getObject(1) + ", "  +
							" '"+  rsSql.getObject(2) + "', "  +
							" ( select UnitOfMeasureId from unitofmeasure where UnitOfMeasureIdOld="+ rsSql.getObject(3) + " )  , " +
							" "+  rsSql.getObject(4) + ", "  +
							" "+  rsSql.getObject(5) + " "  +
							" ) " ;
					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}
				stMySql.close();  

				if(flag) {
					getId.UpdateMaxId("Food","infantfood","InfantFoodIdOld"); // Update max id of Student.
					total = total-count;
					flag = false;
					System.out.println("Food records successfully migrated [ Total Rows ] : "+ count);
					msg = "Food records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in Food");
					msg = "No new records found in Food";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"UnitOfMeasureValue");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Student Check In Records.
	 * @return
	 */
	public String inset_Student_Check_In()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentCheckIn"); // take the max id of StudentCheckIn.

				String query1 = " Select * from( " + 
						"	select " +
						"	StudentCheckInId, StudentId, CheckInDate," +
						" LastAteDate, LastAteInfantFoodId, " + 
						"	LastAteAmountId, LastAteOther,LastDiaperedDate, LastDiaperTypeId, " +
						"	 LastDiaperedOther,LastNapWakeDate," +
						" LastNapDuration, LastMedicationOther, " + 
						"	ExpectedFoodOther, ExpectedMedicationOther, ExpectedNapOther, OtherNotes, Replicate, " +
						"	row_number() over (order by  StudentCheckInId) as RowNum,(select count(StudentCheckInId) from StudentCheckIn) as Total " +
						"	from " +
						"	StudentCheckIn)as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {

					Object check_in_date = rsSql.getObject(3);
					if( check_in_date != null &&  !check_in_date.equals("")) {
						check_in_date = rsSql.getObject(3);
					} else {
						check_in_date = "1500-01-01";
					}

					Object Last_ate_date = rsSql.getObject(4);

					if( Last_ate_date != null &&  !Last_ate_date.equals("")) {
						Last_ate_date = rsSql.getObject(4);
					} else {
						Last_ate_date = "1500-01-01";
					}

					Object Last_Diapered_Date = rsSql.getObject(8);

					if(Last_Diapered_Date != null &&  !Last_Diapered_Date.equals("")) {
						Last_Diapered_Date = rsSql.getObject(8);
					} else {
						Last_Diapered_Date = "1500-01-01";
					}

					Object Last_Nap_Wake_Date = rsSql.getObject(11);

					if( Last_Nap_Wake_Date != null &&  !Last_Nap_Wake_Date.equals("")) {
						Last_Nap_Wake_Date = rsSql.getObject(11);
					} else {
						Last_Nap_Wake_Date = "1500-01-01";
					}

					Object Last_Nap_Duration = rsSql.getObject(12);

					if( Last_Nap_Duration != null &&  !Last_Nap_Duration.equals(""))  {
						Last_Nap_Duration = rsSql.getObject(12);
					} else {
						Last_Nap_Duration = "1500-01-01";
					}

					Object Expected_Food_Other = rsSql.getObject(14);

					if( Expected_Food_Other != null &&  !Expected_Food_Other.equals("")) {
						Expected_Food_Other = Expected_Food_Other.toString().replaceAll("'", " ");
					}

					Object Expected_Medication_Other=rsSql.getObject(15);

					if( Expected_Medication_Other != null &&  !Expected_Medication_Other.equals("")) {
						Expected_Medication_Other = Expected_Medication_Other.toString().replaceAll("'", " ");
					}


					Object Expected_Nap_Other = rsSql.getObject(16);

					if( Expected_Nap_Other != null &&  !Expected_Nap_Other.equals("")) {
						Expected_Nap_Other = Expected_Nap_Other.toString().replaceAll("'", " ");
					}

					Object Last_Medication_Other = rsSql.getObject(13);
					if( Last_Medication_Other != null &&  !Last_Medication_Other.equals("")) {
						Last_Medication_Other = Last_Medication_Other.toString().replaceAll("'", " ");
					}

					Object Last_Ate_Other = rsSql.getObject(7);

					if( Last_Ate_Other != null &&  !Last_Ate_Other.equals("")) {
						Last_Ate_Other = Last_Ate_Other.toString().replaceAll("'", " ");
					}

					Object Last_Diapered_Other = rsSql.getObject(10);

					if( Last_Diapered_Other != null &&  !Last_Diapered_Other.equals("")) {
						Last_Diapered_Other = Last_Diapered_Other.toString().replaceAll("'", " ");
					}


					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count= 1;
					}


					String query2 = " insert into studentcheckin ( " +
							" StudentCheckInIdOld,INFANT_ACCOUNT_DETAILS_ID,CheckInDate,LastAteDate,LastAteInfantFoodId, " +
							" LastAteAmountId,LastAteOther,LastDiaperedDate,LastDiaperTypeId,LastDiaperedOther,LastNapWakeDate, " +
							" LastNapDuration,LastMedicationOther,ExpectedFoodOther,ExpectedMedicationOther,ExpectedNapOther,  " +
							" Replicate ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +   // StudentCheckInId
							" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID="+ rsSql.getObject(2) + " )  , " +  //StudentId
							" '"+  check_in_date + "', "  +   // CheckInDate
							" '"+ Last_ate_date + "', "  +   // LastAteDate
							" ( select InfantFoodId from infantfood where InfantFoodIdOld="+ rsSql.getObject(5) + " )  , " +   // LastAteInfantFoodId
							" ( select UnitOfMeasureValueId from unitofmeasurevalue where UnitOfMeasureValueIdOld="+ rsSql.getObject(6) + " )  , " +   // LastAteAmountId
							" '"+  Last_Ate_Other + "', "  +  // LastAteOther
							" '"+  Last_Diapered_Date + "', "  +   // LastDiaperedDate
							" ( select UnitOfMeasureValueId from unitofmeasurevalue where UnitOfMeasureValueIdOld="+ rsSql.getObject(9) + " )  , " +   // LastDiaperTypeId
							" '"+  Last_Diapered_Other + "', "  +   	// LastDiaperedOther
							" '"+  Last_Nap_Wake_Date + "', "  +   		// LastNapWakeDate
							" '"+  Last_Nap_Duration + "', "  +   		// LastNapDuration
							" '"+  Last_Medication_Other + "', "  +   	// LastMedicationOther
							" '"+  Expected_Food_Other + "', "  +  	 	// ExpectedFoodOther
							" '"+  Expected_Medication_Other + "', "  +   	// ExpectedMedicationOther
							" '"+  Expected_Nap_Other + "', "  +   	// ExpectedNapOther
							//  " '"+  rsSql.getObject(17) + "', "  +   	// OtherNotes
							" "+  rsSql.getObject(18) + " "     +      	// Replicate

					 		" ) ";

					stMySql.executeUpdate(query2);


					//---------------------- INSERT NOTES ---------------------------
					Object notes = rsSql.getObject(17);

					if( notes != null &&  !notes.equals("")) {

						notes=notes.toString().replaceAll("'", " ");
						String query3=" insert into infant_notes(NOTES,INFANT_ACCOUNT_DETAILS_ID,NOTES_DESCRIPTION,DATE_TIME,StudentCheckInIdOld) "+
								" values( " +
								" '"+  notes + "', " +  // OtherNotes
								" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID="+ rsSql.getObject(2) + " )  , " +  //StudentId
								" 'StudentCheckIn' , " +
								" now() ,"+
								" "+  rsSql.getObject(1) + " "  +   // StudentCheckInId
								" ) ";
						stMySql.executeUpdate(query3);
					}
					//---------------------------------------------------------------
					flag = true;
					count++;
				}			

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("StudentCheckIn","studentcheckin","StudentCheckInIdOld"); // Update max id of Student.
					total = total-count;
					flag = false;
					System.out.println("StudentCheckIn records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentCheckIn records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentCheckIn");
					msg = "No new records found in StudentCheckIn";
				}
			}
			single_count = 0;

		}
		catch (Exception ex) 
		{
			Logs.writeLog(ex,"StudentCheckIn");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Student Check Out Records.
	 * @return
	 */
	public String insert_Student_CheckOut()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentCheckOut"); // take the max id of StudentCheckIn.

				String query1 = "Select * from( " + 
						"	select " +
						"	StudentCheckOutId, StudentId, CheckOutDate, CheckOutTime, " +
						"	Activities, Supplies, SuppliesNeededByDate,  " +
						"	Replicate,TeacherNotes, ParentNotes, " +
						"	row_number() over (order by  StudentCheckOutId) as RowNum,(select count(StudentCheckOutId) from StudentCheckOut) as Total " +
						"	from " +
						"	StudentCheckOut)as Result "+
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {

					Object Check_Out_Date = rsSql.getObject(3);
					if( Check_Out_Date != null &&  !Check_Out_Date.equals("") )
					{
						Check_Out_Date = rsSql.getObject(3);
					} else {
						Check_Out_Date="1500-01-01";
					}

					Object Check_Out_Time= rsSql.getObject(4);
					if( Check_Out_Time != null &&  !Check_Out_Time.equals("") )
					{
						Check_Out_Time= rsSql.getObject(4);
					} else {
						Check_Out_Time="1500-01-01";
					}


					Object Supplies_Needed_By_Date= rsSql.getObject(7);
					if( Supplies_Needed_By_Date != null &&  !Supplies_Needed_By_Date.equals("")) {
						Supplies_Needed_By_Date = rsSql.getObject(7);
					} else {
						Supplies_Needed_By_Date = "1500-01-01";
					}

					Object Supplies = rsSql.getObject(6);
					if( Supplies != null &&  !Supplies.equals("")) {
						Supplies = rsSql.getObject(6).toString().replaceAll("'", " ");
					}

					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2=" insert into studentcheckout ( " +
							"  StudentCheckOutIdOld,INFANT_ACCOUNT_DETAILS_ID,CheckOutDate,CheckOutTime, " +
							"  Activities,Supplies,SuppliesNeededByDate,Replicate) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", "  +   // StudentCheckOutId
							" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID="+ rsSql.getObject(2) + " )  , " +  //StudentId
							" '"+  Check_Out_Date + "', "  +   // CheckOutDate
							" '"+  Check_Out_Time + "', "  +   // CheckOutTime
							" '"+  rsSql.getObject(5) + "', "  +   // Activities
							" '"+  Supplies + "', "  +   // Supplies
							" '"+ Supplies_Needed_By_Date + "', "  +   // SuppliesNeededByDate
							"  "+  rsSql.getObject(8) + " "  +   // Replicate
							" ) " ;

					stMySql.executeUpdate(query2);

					Object TecNote = rsSql.getObject(9);
					Object ParNote = rsSql.getObject(10);

					if( TecNote != null &&  !TecNote.equals("")) {

						TecNote =  TecNote.toString().replaceAll("'", " ");

						String query3 =" insert into infant_notes(NOTES,INFANT_ACCOUNT_DETAILS_ID,NOTES_DESCRIPTION,DATE_TIME,StudentCheckInIdOld) "+
								" values( " +
								" '"+  TecNote + "', " +  // OtherNotes
								" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID="+ rsSql.getObject(2) + " )  , " +  //StudentId
								" 'TecherNotes' , " +
								" now() ,"+
								" "+  rsSql.getObject(1) + " "  +   // StudentCheckOutId
								" ) ";
						stMySql.executeUpdate(query3);

					}

					if( ParNote != null &&  !ParNote.equals("") )
					{
						ParNote =  ParNote.toString().replaceAll("'", " ");

						String query4 =" insert into infant_notes(NOTES,INFANT_ACCOUNT_DETAILS_ID,NOTES_DESCRIPTION,DATE_TIME,StudentCheckInIdOld) "+
								" values( " +
								" '"+  ParNote + "', " +  // OtherNotes
								" ( select INFANT_ACCOUNT_DETAILS_ID from infant_account_details where STUDENT_ID="+ rsSql.getObject(2) + " )  , " +  //StudentId
								" 'ParentNotes' , " +
								" now() ,"+
								" "+  rsSql.getObject(1) + " "  +   // StudentCheckOutId
								" ) ";
						stMySql.executeUpdate(query4);

					}
					flag = true;
					count++;
				}
				stMySql.close();
				if(flag) {
					getId.UpdateMaxId("StudentCheckOut","studentcheckout","StudentCheckOutIdOld"); // Update max id of Student.
					total = total-count;
					flag = false;
					System.out.println("StudentCheckOut records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentCheckOut records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentCheckOut");
					msg = "No new records found in StudentCheckOut";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentCheckOut");
			ex.printStackTrace();
		}
		return msg;

	}

	/**
	 * Insert Infant Expected Food Records.
	 */
	public String insert_Infant_ExpectedFood()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("InfantExpectedFood"); // take the max id of InfantExpectedFood.

				String query1 = "Select * from( " + 
						"	select  " +
						"	InfantExpectedFoodId, StudentIdCheckInId, ExpectedEatTime, ExpectedFoodId, ExpectedFoodAmountId, " +
						"	row_number() over (order by  InfantExpectedFoodId) as RowNum,(select count(InfantExpectedFoodId) from InfantExpectedFood) as Total " +
						"	from  " +
						"	InfantExpectedFood)as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object Expected_Eat_Time=rsSql.getObject(3);
					if( Expected_Eat_Time != null &&  !Expected_Eat_Time.equals("")) {
						Expected_Eat_Time=rsSql.getObject(3);
					} else {
						Expected_Eat_Time="1500-01-01";
					}

					if(single_count == 0) {
						total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count=1;
					}

					String query2 = " insert into infant_expected_food (Infant_Expected_FoodId_Old,Student_CheckIn_Id,  " +
							" Expected_Eat_Time,Expected_Food_Id,Expected_Food_Amount_Id) " +	
							" values( " +
							" "+  rsSql.getObject(1) + ", " +  // InfantExpectedFoodId
							" ( select StudentCheckInId from studentcheckin where StudentCheckInIdOld="+ rsSql.getObject(2) + " )  , " +  //StudentId
							" '"+ Expected_Eat_Time + "', " +  // ExpectedEatTime
							" ( select InfantFoodId from infantfood where InfantFoodIdOld="+ rsSql.getObject(4) + " )  , " +   // ExpectedFoodId
							" ( select UnitOfMeasureValueId from unitofmeasurevalue where UnitOfMeasureValueIdOld="+ rsSql.getObject(5) + " )  " +   // ExpectedFoodAmountId
							" ) ";
					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}
				stMySql.close();


				if(flag) {
					getId.UpdateMaxId("InfantExpectedFood","infant_expected_food","Infant_Expected_FoodId_Old"); // Update max id of InfantExpectedFood.
					total = total-count;
					flag = false;
					System.out.println("InfantExpectedFood records successfully migrated [ Total Rows ] : "+ count);
					msg = "InfantExpectedFood records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in InfantExpectedFood");
					msg = "No new records found in InfantExpectedFood";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"InfantExpectedFood");
			ex.printStackTrace();
		}
		return msg;


	}

	/**
	 * Insert Infant Given Food Records.
	 * @return
	 */
	public String insert_Infant_Given_Food()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("InfantGivenFood"); // take the max id of InfantGivenFood.

				String query1 = " Select * from( " + 
						"	select " +
						"	InfantGivenFoodId, StudentCheckOutId, ExpectedTime, " +
						"	GivenTime, FoodId, FoodAmountId,ExpectedFoodId, Notes, " +
						"	row_number() over (order by  InfantGivenFoodId) as RowNum,(select count(InfantGivenFoodId) from InfantGivenFood) as Total " +
						"	from " +
						"	InfantGivenFood)as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {

					Object Expected_Time = rsSql.getObject(3);
					if( Expected_Time != null &&  !Expected_Time.equals("")) {
						Expected_Time = rsSql.getObject(3);
					} else {
						Expected_Time="1500-01-01";
					}
					Object Given_Time=rsSql.getObject(4);
					if( Given_Time != null &&  !Given_Time.equals("")) {
						Given_Time = rsSql.getObject(4);
					} else {
						Given_Time = "1500-01-01";
					}

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into infant_given_food (  " +
							"  Infant_Given_FoodId_Old,Student_Check_OutId,Expected_Time,Given_Time,Food_Id,  " +
							"  Food_Amount_Id,Expected_Food_Id ) " +
							"  values (  " +
							" "+  rsSql.getObject(1) + ", " +        //InfantGivenFoodId
							" ( select StudentCheckOutId from studentcheckout where StudentCheckOutIdOld="+ rsSql.getObject(2) + " )  , " +   		// StudentCheckOutId
							" '"+  Expected_Time + "', " +        //ExpectedTime
							" '"+  Given_Time + "', " +        //GivenTime
							" ( select InfantFoodId from infantfood where InfantFoodIdOld="+ rsSql.getObject(5) + " )  , " +   						//FoodId
							" ( select UnitOfMeasureValueId from unitofmeasurevalue where UnitOfMeasureValueIdOld="+ rsSql.getObject(6) + " ) , " +   // FoodAmountId
							" ( select Infant_Expected_FoodId from infant_expected_food where Infant_Expected_FoodId_Old="+ rsSql.getObject(7) + " )  " +   // ExpectedFoodId

		                   " ) ";

					stMySql.executeUpdate(query2);


					Object Note = rsSql.getObject(8);

					if( Note != null &&  !Note.equals("")) {
						Note = Note.toString().replaceAll("'", " ");

						String query3 = " insert into infant_notes(NOTES,INFANT_ACCOUNT_DETAILS_ID,NOTES_DESCRIPTION,DATE_TIME,StudentCheckInIdOld) "+
								" values( " +
								" '"+  Note + "', " +  // OtherNotes
								" ( select infant_account_details_id from studentcheckout where StudentCheckOutIdOld="+ rsSql.getObject(2) + " ) , " +   // ExpectedFoodId
								" 'CheckoutNotes' , " +
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
					getId.UpdateMaxId("InfantGivenFood","infant_given_food","Infant_Given_FoodId_Old"); // Update max id of InfantGivenFood.
					total = total-count;
					flag = false;
					System.out.println("InfantGivenFood records successfully migrated [ Total Rows ] : "+ count);
					msg = "InfantGivenFood records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in InfantGivenFood");
					msg = "No new records found in InfantGivenFood";
				}

			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"InfantGivenFood");
			ex.printStackTrace();
		}
		return msg;
	}


	/**
	 * Insert Infant Mediation Records.
	 * @return
	 */
	public String insert_Infant_Medication()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("InfantMedication"); // take the max id of InfantGivenFood.

				String query1=" Select * from ( " +
						"	select " +
						"	InfantMedicationId, Name, InfantMedicationUnitOfMeasureId, " + 
						"	ViewOrder, IsDeleted, " +
						"	row_number() over (order by  InfantMedicationId) as RowNum,(select count(InfantMedicationId) from InfantMedication) as Total " +
						"	from  " +
						"	InfantMedication ) Result "  +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";	

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {

					Object Name= rsSql.getObject(2);
					if( Name != null &&  !Name.equals("")) {
						Name = Name.toString().replaceAll("'", " ");
					}

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into infant_medication (" +
							" Infant_Medication_Id_Old,Name,Infant_Medication_UnitOf_MeasureId,ViewOrder, " +
							" IsDeleted ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", " +        //InfantMedicationId
							" '"+  Name + "', " +        //Name
							" ( select UnitOfMeasureId from unitofmeasure where UnitOfMeasureIdOld="+ rsSql.getObject(3) + " ) , " +   // InfantMedicationUnitOfMeasureId
							" "+  rsSql.getObject(4) + ", " +        //ViewOrder
							" "+  rsSql.getObject(5) + " " +        //IsDeleted
							" ) ";

					stMySql.executeUpdate(query2);
					flag= true;
					count++;

				}
				stMySql.close();
				if(flag) {
					getId.UpdateMaxId("InfantMedication","infant_medication","Infant_Medication_Id_Old"); // Update max id of InfantGivenFood.
					total = total-count;
					flag = false;
					System.out.println("InfantMedication records successfully migrated [ Total Rows ] : "+ count);
					msg = "InfantMedication records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in InfantMedication");
					msg = "No new records found in InfantMedication";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"InfantGivenFood");
			ex.printStackTrace();
		}
		return msg;

	}

	/**
	 * Insert Infant Expected Mediation
	 * @return
	 */
	public String insert_Infant_Expected_Medication()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("InfantExpectedMedication"); // take the max id of InfantExpectedMedication.


				String query1 = "Select * from ( " +
						"	select " +
						"	InfantExpectedMedicationId, StudentCheckInId, ExpectedMedicationTime, " +
						"	ExpectedMedicationId, ExpectedMedicationAmountId, ExpectedMedicationReasonId, " +
						"	row_number() over (order by  InfantExpectedMedicationId) as RowNum,(select count(InfantExpectedMedicationId) from InfantExpectedMedication) as Total " +
						"	from " +
						"	InfantExpectedMedication ) as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next())
				{
					Object Expected_Medication_Time=rsSql.getObject(3);
					if( Expected_Medication_Time != null &&  !Expected_Medication_Time.equals("")) {
						Expected_Medication_Time=rsSql.getObject(3);
					} else {
						Expected_Medication_Time="1500-01-01";
					}

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into infant_expected_medication ( " +
							" Infant_Expected_Medication_Id_Old, Student_Check_In_Id, ExpectedMedicationTime,  " +
							" ExpectedMedicationId, ExpectedMedicationAmountId, ExpectedMedicationReasonId ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", " +        // IsDeleted
							" ( select StudentCheckInId from studentcheckin where StudentCheckInIdOld="+ rsSql.getObject(2) + " ) , " +   // StudentCheckInId
							" '"+  Expected_Medication_Time + "', " +     // ExpectedMedicationTime
							" ( select Infant_Medication_Id from infant_medication where Infant_Medication_Id_Old="+ rsSql.getObject(4) + " ) , " +   // ExpectedMedicationId
							" ( select UnitOfMeasureValueId from unitofmeasurevalue where UnitOfMeasureValueIdOld="+ rsSql.getObject(5) + " ) , " +   // ExpectedMedicationAmountId
							" ( select UnitOfMeasureValueId from unitofmeasurevalue where UnitOfMeasureValueIdOld="+ rsSql.getObject(6) + " )  " +   // ExpectedMedicationReasonId
						   " ) ";

					stMySql.executeUpdate(query2);
					flag = true;
					count++;

				}
				stMySql.close();

				if (flag) {
					getId.UpdateMaxId("InfantExpectedMedication","infant_expected_medication","Infant_Expected_Medication_Id_Old"); // Update max id of InfantGivenFood.
					total = total-count;
					flag = false;
					System.out.println("InfantExpectedMedication records successfully migrated [ Total Rows ] : "+ count);
					msg = "InfantExpectedMedication records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in InfantExpectedMedication");
					msg = "No new records found in InfantExpectedMedication";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"InfantExpectedMedication");
			ex.printStackTrace();
		}
		return msg;
	}

	/**
	 * Insert Infant Given Mediation
	 * @return
	 */
	public String infant_Infant_Given_Medication()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("InfantGivenMedication"); // take the max id of InfantGivenMedication.

				String query1 = "Select * from ( " +
						"	select  " +
						"	InfantGivenMedicationId, StudentCheckOutId, ExpectedTime, " + 
						"	GivenTime, MedicationId, MedicationAmountId, " +
						"	ReasonId, ExpectedMedicationId,Notes, " +
						"	row_number() over (order by  InfantGivenMedicationId) as RowNum,(select count(InfantGivenMedicationId) from InfantGivenMedication) as Total " +
						"	from " +
						"	InfantGivenMedication " +
						"	) as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object Expected_Time = rsSql.getObject(3);
					if( Expected_Time != null &&  !Expected_Time.equals("")) {
						Expected_Time=rsSql.getObject(3);
					} else {
						Expected_Time="1500-01-01";
					}

					Object Given_Time=rsSql.getObject(4);
					if( Given_Time != null &&  !Given_Time.equals("")) {
						Given_Time=rsSql.getObject(4);
					} else {
						Given_Time="1500-01-01";
					}

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2=" insert into infant_given_medication ( " +
							" Infant_Given_Medication_Id_Old, Student_Check_Out_Id, Expected_Time, " +
							" Given_Time, Medication_Id,Medication_Amount_Id, Reason_Id, Expected_Medication_Id ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", " +        // InfantGivenMedicationId
							" ( select StudentCheckOutId from studentcheckout where StudentCheckOutIdOld="+ rsSql.getObject(2) + " ) , " +   // StudentCheckOutId
							" '"+  Expected_Time + "', " +        // ExpectedTime
							" '"+  Given_Time + "', " +        // GivenTime
							" ( select Infant_Medication_Id from infant_medication where Infant_Medication_Id_Old="+ rsSql.getObject(5) + " ) , " +   // MedicationId
							" ( select UnitOfMeasureValueId from unitofmeasurevalue where UnitOfMeasureValueIdOld="+ rsSql.getObject(6) + " ) , " +   // MedicationAmountId
							" ( select UnitOfMeasureValueId from unitofmeasurevalue where UnitOfMeasureValueIdOld="+ rsSql.getObject(7) + " ) , " +   // ReasonId
							" ( select Infant_Expected_Medication_Id from infant_expected_medication where Infant_Expected_Medication_Id_Old="+ rsSql.getObject(8) + " )  " +   // ExpectedMedicationId
							" ) ";

					stMySql.executeUpdate(query2);

					Object Note= rsSql.getObject(9);

					if( Note != null &&  !Note.equals("")) {
						Note = Note.toString().replaceAll("'", " ");

						String query3 = " insert into infant_notes(NOTES,INFANT_ACCOUNT_DETAILS_ID,NOTES_DESCRIPTION,DATE_TIME,StudentCheckInIdOld) "+
								" values( " +
								" '"+  Note + "', " +  // OtherNotes
								" ( select infant_account_details_id from studentcheckout where StudentCheckOutIdOld="+ rsSql.getObject(2) + " ) , " +   // ExpectedFoodId
								" 'GivenMedicationNotes' , " +
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
					getId.UpdateMaxId("InfantGivenMedication","infant_given_medication","Infant_Given_Medication_Id_Old"); // Update max id of InfantGivenMedication.
					total = total-count;
					flag = false;
					System.out.println("InfantGivenMedication records successfully migrated [ Total Rows ] : "+ count);
					msg = "InfantGivenMedication records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in InfantGivenMedication");
					msg = "No new records found in InfantGivenMedication";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"InfantGivenMedication");
			ex.printStackTrace();
		}
		return msg;
	}

	public String insert_Student_CheckIn_Infant_Medication()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("StudentCheckInInfantMedication"); // take the max id of StudentCheckInInfantMedication.

				String query1 = " Select * from ( " +
						"	select " +
						"	StudentCheckInInfantMedicationId, StudentCheckInId, InfantMedicationId, " + 
						"	InfantMedicationAmountId, InfantMedicationReasonId, InfantMedicationDate, " +
						"	row_number() over (order by  StudentCheckInInfantMedicationId) as RowNum,(select count(StudentCheckInInfantMedicationId) from StudentCheckInInfantMedication) as Total " +
						"	from " +
						"	StudentCheckInInfantMedication " +
						"	) as Result" +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next())
				{

					Object Infant_Medication_Date = rsSql.getObject(6);
					if( Infant_Medication_Date != null &&  !Infant_Medication_Date.equals("")) 	{
						Infant_Medication_Date=rsSql.getObject(6);
					} else {
						Infant_Medication_Date="1500-01-01";
					}

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into student_checkin_infant_medication ( " +
							" Student_CheckIn_Infant_Medication_Id_Old,Student_CheckIn_Id,Infant_Medication_Id, " +
							" Infant_Medication_Amount_Id,Infant_Medication_Reason_Id,Infant_Medication_Date ) " +
							" values ( " +
							" "+  rsSql.getObject(1) + ", " +        // StudentCheckInInfantMedicationId
							" ( select StudentCheckInId from studentcheckin where StudentCheckInIdOld="+ rsSql.getObject(2) + " ) , " +   // StudentCheckInId
							" ( select Infant_Medication_Id from infant_medication where Infant_Medication_Id_Old="+ rsSql.getObject(3) + " ) , " +   // InfantMedicationId
							" ( select UnitOfMeasureValueId from unitofmeasurevalue where UnitOfMeasureValueIdOld="+ rsSql.getObject(4) + " ) , " +   // InfantMedicationAmountId
							" ( select UnitOfMeasureValueId from unitofmeasurevalue where UnitOfMeasureValueIdOld="+ rsSql.getObject(5) + " ) , " +   // InfantMedicationReasonId
							" '"+ Infant_Medication_Date + "' " +        // InfantMedicationDate
							" ) ";

					stMySql.executeUpdate(query2);
					flag = true;
					count++;
				}
				stMySql.close();
				if(flag) {
					getId.UpdateMaxId("StudentCheckInInfantMedication","student_checkin_infant_medication","Student_CheckIn_Infant_Medication_Id_Old"); // Update max id of StudentCheckInInfantMedication.
					total = total-count;
					flag = false;
					System.out.println("StudentCheckInInfantMedication records successfully migrated [ Total Rows ] : "+ count);
					msg = "StudentCheckInInfantMedication records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in StudentCheckInInfantMedication");
					msg = "No new records found in StudentCheckInInfantMedication";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"StudentCheckInInfantMedication");
			ex.printStackTrace();
		}
		return msg;
	}
}
