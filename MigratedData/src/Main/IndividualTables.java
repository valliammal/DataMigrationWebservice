package Main;
import java.sql.ResultSet;
import java.sql.Statement;


public class IndividualTables {
	
	int id=0;  // contain the max id 
	boolean flag=false;
	Dabase_Connect conn = new Dabase_Connect();
	GetTableId getId = new GetTableId();
	String msg="";
	int count=0;
	int total=1; // contain total rows of tables.
	int single_count=0; // execute first time only.
	
	/**
	 * Insert Audit Records.
	 * @return
	 */
	public String insert_Audit()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				
			
		count=0;
		Statement stSql =conn.connectionSqlServer().createStatement();
		Statement stMySql = conn.connectionmysql().createStatement();
		
		id = getId.GetMaxId("Audits"); // take the max id of Audits.
		
		String query1="select * from ( " +
					"	select " +
					"	Id, Title, Action, " + 
					"	TableName, TableKey, UserName, " +
					"	 AuditDate,Replicate, " +
					"	row_number() over (order by Id  ) as RowNum,(select count(Id) from Audits) as Total " +
					"	from Audits " +
					"	)as Result"	 +
					 " where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
					
					ResultSet rsSql = stSql.executeQuery(query1);
					
		while(rsSql.next())
		{
			if(single_count==0)
			{
				total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
				single_count=1;
			}
			 Object Title=rsSql.getObject(2);
			 
			 if( Title != null &&  !Title.equals("") )
			 {
				 Title=Title.toString().replaceAll("'", " ");
			 }
			 
			 Object Action=rsSql.getObject(3);
			 
			 if( Action != null &&  !Action.equals("") )
			 {
				 Action=Action.toString().replaceAll("'", " ");
			 }
			 
			 Object TableName=rsSql.getObject(4);
			 
			 if( TableName != null &&  !TableName.equals("") )
			 {
				 TableName=TableName.toString().replaceAll("'", " ");
			 }
			 
			 Object UserName=rsSql.getObject(6);
			 
			 if( UserName != null &&  !UserName.equals("") )
			 {
				 UserName=UserName.toString().replaceAll("'", " ");
			 }
			 
			 Object AuditDate=rsSql.getObject(7);
			 
			 if( AuditDate != null &&  !AuditDate.equals("") )
			 {
				 AuditDate=rsSql.getObject(7);
			 }
			 else
			 {
				 AuditDate="1500-01-01";
			 }
			
			String query2=" insert into audits ( " +
						  " Id_Old,Title,Action,Table_Name,Table_Key,User_Name,AuditDate,Replicate ) " +
						  " values ( " +
						  " "+  rsSql.getObject(1) + ", "  +  				// Id
						  " '"+  Title + "', "  +  			// Title
						  " '"+  Action + "', "  +  			// Action
						  " '"+  TableName + "', "  +  			// TableName
						  " "+  rsSql.getObject(5) + ", "  +  				// TableKey
						  " '"+  UserName + "', "  +  			// UserName
						  " '"+  AuditDate + "', "  +  				// AuditDate
						  " "+  rsSql.getObject(8) + " "  +  				// Replicate
						 
						  " ) ";
			
			 stMySql.executeUpdate(query2);
				
			 flag=true;
	         count++;
		}
		
		stMySql.close();
		
		if(flag)
		{
			getId.UpdateMaxId("Audits","audits","Id_Old"); // Update max id of Audits.
			total=total-count;
			flag=false;
			System.out.println("Audits records successfully migrated [ Total Rows ] : "+ count);
			msg="Audits records successfully migrated [ Total Rows ] : "+ count;
		}
		else
		{
			total=0;
			System.out.println("No new records found in Audits");
			msg="No new records found in Audits";
		}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
				Logs.writeLog(ex,"Audits");
				ex.printStackTrace();
		}
		return msg;
	}
	
	/**
	 * Insert Audit Values Records.
	 * @return
	 */
	public String insert_Audit_Values()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				
			
		count=0;
		Statement stSql =conn.connectionSqlServer().createStatement();
		Statement stMySql = conn.connectionmysql().createStatement();
		
		id = getId.GetMaxId("AuditValues"); // take the max id of AuditValues.
		
		String query1="select * from ( " +
					"	select " +
					"	Id, AuditId, MemberName, OldValue, NewValue, " +
					"	row_number() over (order by Id  ) as RowNum,(select count(Id) from AuditValues) as Total " +
					"	from AuditValues " +
					"	)as Result" +
					" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
					
					ResultSet rsSql = stSql.executeQuery(query1);
					
		while(rsSql.next())
		{
			if(single_count==0)
			{
				total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
				single_count=1;
			}
			
			 Object MemberName=rsSql.getObject(3);
			 
			 if( MemberName != null &&  !MemberName.equals("") )
			 {
				 MemberName=MemberName.toString().replaceAll("'", " ");
			 }
			 
			 Object OldValue=rsSql.getObject(4);
			 
			 if( OldValue != null &&  !OldValue.equals("") )
			 {
				 OldValue=OldValue.toString().replaceAll("'", " ");
			 }
			
			 
			 Object NewValue=rsSql.getObject(5);
			 
			 if( NewValue != null &&  !NewValue.equals("") )
			 {
				 NewValue=NewValue.toString().replaceAll("'", " ");
			 }
			
			
			String query2="insert into audit_values ( " +
						  " Id_Old,Audit_Id,Member_Name,Old_Value,New_Value ) " +
						  " values ( " +
						  " "+  rsSql.getObject(1) + ", "  +  				// Id
						  " ( select Id from audits where Id_Old="+ rsSql.getObject(2) + " )  , " +  //AuditId
						  " '"+  MemberName + "', "  +  				//MemberName
						  " '"+  OldValue + "', "  +  				//OldValue
						  " '"+  NewValue + "' "  +  				    //NewValue
						  " ) ";
			
			 stMySql.executeUpdate(query2);
				
			 flag=true;
	         count++;
	         
		}
		
		stMySql.close();
		
		if(flag)
		{
			getId.UpdateMaxId("AuditValues","audit_values","Id_Old"); // Update max id of AuditValues.
			total=total-count;
			flag=false;
			System.out.println("AuditValues records successfully migrated [ Total Rows ] : "+ count);
			msg="AuditValues records successfully migrated [ Total Rows ] : "+ count;
		}
		else
		{
			total=0;
			System.out.println("No new records found in AuditValues");
			msg="No new records found in AuditValues";
		}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
				Logs.writeLog(ex,"AuditValues");
				ex.printStackTrace();
		}
		return msg;
		
	}
	
	public String insert_Date_Lookup()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				
		
		count=0;
		Statement stSql =conn.connectionSqlServer().createStatement();
		Statement stMySql = conn.connectionmysql().createStatement();
		
		id = getId.GetMaxId("DateLookup"); // take the max id of DateLookup.
		
		String query1="select * from ( " +
					"	select " +
					"	DateKey, DateFull, CharacterDate, FullYear, " + 
					"	QuarterNumber, WeekNumber, WeekDayName, MonthDay, " +
					"	MonthName, YearDay, DateDefinition, WeekDay, MonthNumber, " +
					"	row_number() over (order by DateKey  ) as RowNum,(select count(DateKey) from DateLookup) as Total " +
					"	from DateLookup " +
					"	)as Result " +
					" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
					
					ResultSet rsSql = stSql.executeQuery(query1);
					
		while(rsSql.next())
		{
			if(single_count==0)
			{
				total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
				single_count=1;
			}
			 Object DateFull=rsSql.getObject(2);
			 
			 if( DateFull != null &&  !DateFull.equals("") )
			 {
				 DateFull=rsSql.getObject(2);
			 }
			 else
			 {
				 DateFull="1500-01-01";
			 }
			 
			String query2=" insert into date_lookup ( " +
						  "Date_Key_Old,Date_Full,Character_Date,Full_Year,Quarter_Number,Week_Number,  " +
						  " Week_Day_Name,Month_Day,Month_Name,Year_Day,Date_Definition,Week_Day,Month_Number,Date_Key ) " +
						  " values ( " +
						  " "+  rsSql.getObject(1) + ", "  +  				//DateKey
						  " '"+  DateFull + "', "  +  			//DateFull
						  " '"+  rsSql.getObject(3) + "', "  +  			//CharacterDate
						  " '"+  rsSql.getObject(4) + "', "  +  			//FullYear
						  " "+  rsSql.getObject(5) + ", "  +  				//QuarterNumber
						  " "+  rsSql.getObject(6) + ", "  +  				//WeekNumber
						  " '"+  rsSql.getObject(7) + "', "  +  			//WeekDayName
						  " "+  rsSql.getObject(8) + ", "  +  				//MonthDay
						  " '"+  rsSql.getObject(9) + "', "  +  			//MonthName
						  " "+  rsSql.getObject(10) + ", "  +  				//YearDay
						  " '"+  rsSql.getObject(11) + "', "  +  			//DateDefinition
						  " "+  rsSql.getObject(12) + ", "  +  				//WeekDay
						  " "+  rsSql.getObject(13) + ", "  +  				//MonthNumber
						  " "+  rsSql.getObject(1) + " "  +  				//DateKey
						  " ) ";
			 stMySql.executeUpdate(query2);
				
			 flag=true;
	         count++;
		}
		
		stMySql.close();
		
		if(flag)
		{
			getId.UpdateMaxId("DateLookup","date_lookup","Date_Key_Old"); // Update max id of DateLookup.
			total=total-count;
			flag=false;
			System.out.println("DateLookup records successfully migrated [ Total Rows ] : "+ count);
			msg="DateLookup records successfully migrated [ Total Rows ] : "+ count;
		}
		else
		{
			total=0;
			System.out.println("No new records found in DateLookup");
			msg="No new records found in DateLookup";
		}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
				Logs.writeLog(ex,"DateLookup");
				ex.printStackTrace();
		}
		return msg;
	}
	
	public String insert_Computer_Location()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				
			
		count=0;
		Statement stSql =conn.connectionSqlServer().createStatement();
		Statement stMySql = conn.connectionmysql().createStatement();
		
		id = getId.GetMaxId("ComputerLocation"); // take the max id of ComputerLocation.
		
		String query1="select * from ( " +
					"	select " +
					"	ComputerLocationId, ComputerName, LocationId, ClassroomId, " +
					"	row_number() over (order by ComputerLocationId  ) as RowNum,(select count(ComputerLocationId) from ComputerLocation) as Total " +
					"	from ComputerLocation " +
					"	)as Result " +
					" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
					
					ResultSet rsSql = stSql.executeQuery(query1);
					
		while(rsSql.next())
		{
			if(single_count==0)
			{
				total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
				single_count=1;
			}
			 Object ComputerName=rsSql.getObject(2);
			 
			 if( ComputerName != null &&  !ComputerName.equals("") )
			 {
				 ComputerName=ComputerName.toString().replaceAll("'", " ");
			 }
			 
			String query2=" insert into computer_location ( " +
					      " Computer_Location_Id_Old,Computer_Name,Location_Id,Classroom_Id ) " +
						  " values ( " +
						  " "+  rsSql.getObject(1) + ", "  +  				//ComputerLocationId
						  " '"+  ComputerName + "', "  +  				//ComputerName
						  " "+  rsSql.getObject(3) + ", "  +  				//LocationId
						  " "+  rsSql.getObject(4) + " "  +  				//ClassroomId
					      " ) ";
			 stMySql.executeUpdate(query2);
				
			 flag=true;
	         count++;
		}
		
		stMySql.close();
		
		if(flag)
		{
			getId.UpdateMaxId("ComputerLocation","computer_location","Computer_Location_Id_Old"); // Update max id of ComputerLocation.
			total=total-count;
			flag=false;
			System.out.println("ComputerLocation records successfully migrated [ Total Rows ] : "+ count);
			msg="ComputerLocation records successfully migrated [ Total Rows ] : "+ count;
		}
		else
		{
			total=0;
			System.out.println("No new records found in ComputerLocation");
			msg="No new records found in ComputerLocation";
		}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
				Logs.writeLog(ex,"ComputerLocation");
				ex.printStackTrace();
		}
		return msg;
		
		
	}
	
	public String insert_Note_Type()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				
			
		count=0;
		Statement stSql =conn.connectionSqlServer().createStatement();
		Statement stMySql = conn.connectionmysql().createStatement();
		
		id = getId.GetMaxId("NoteType"); // take the max id of NoteType.
		
		String query1="select * from ( " +
					"	select " +
					"	NoteTypeId, Description, " +
					"	row_number() over (order by NoteTypeId  ) as RowNum,(select count(NoteTypeId) from NoteType) as Total " +
					"	from NoteType " +
					"	)as Result " +
					" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
					
					ResultSet rsSql = stSql.executeQuery(query1);
					
		while(rsSql.next())
		{
			if(single_count==0)
			{
				total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
				single_count=1;
			}
			
			Object Description=rsSql.getObject(2);
			 
			 if( Description != null &&  !Description.equals("") )
			 {
				 Description=Description.toString().replaceAll("'", " ");
			 }
			 
			String query2=" insert into note_type (" +
						  " Note_Type_Id_Old,Description ) " +
						  " values ( " +
						  " "+  rsSql.getObject(1) + ", "  +  				//NoteTypeId
						  " '"+  Description + "' "  +  				//Description
						  " ) " ;
			 stMySql.executeUpdate(query2);
				
			 flag=true;
	         count++;
			
		}
						
		
		stMySql.close();
		
		if(flag)
		{
			getId.UpdateMaxId("NoteType","note_type","Note_Type_Id_Old"); // Update max id of NoteType.
			total=total-count;
			flag=false;
			System.out.println("NoteType records successfully migrated [ Total Rows ] : "+ count);
			msg="NoteType records successfully migrated [ Total Rows ] : "+ count;
		}
		else
		{
			total=0;
			System.out.println("No new records found in NoteType");
			msg="No new records found in NoteType";
		}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
				Logs.writeLog(ex,"NoteType");
				ex.printStackTrace();
		}
		return msg;
		
	}
	/**
	 * Insert Note Records
	 * @return
	 */
	public String insert_Note()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				
			
		count=0;
		Statement stSql =conn.connectionSqlServer().createStatement();
		Statement stMySql = conn.connectionmysql().createStatement();
		
		id = getId.GetMaxId("Note"); // take the max id of Note.
		
		String query1="select * from ( " +
					"	select " +
					"	NoteId, NoteTypeId, ReferenceId, DateEntered, " + 
					"	UserName, Note, " +
					"	row_number() over (order by NoteId  ) as RowNum,(select count(NoteId) from Note) as Total " +
					"	from Note " +
					"	)as Result" +
					" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
					
					ResultSet rsSql = stSql.executeQuery(query1);
					
		while(rsSql.next())
		{
			if(single_count==0)
			{
				total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
				single_count=1;
			}
			 Object DateEntered=rsSql.getObject(4);
			 
			 if( DateEntered != null &&  !DateEntered.equals("") )
			 {
				 DateEntered=rsSql.getObject(4);
			 }
			 else
			 {
				 DateEntered="1500-01-01";
			 }
			
			 Object UserName=rsSql.getObject(5);
			 
			 if( UserName != null &&  !UserName.equals("") )
			 {
				 UserName=UserName.toString().replaceAll("'", " ");
			 }
			 
			 Object Note=rsSql.getObject(6);
			 
			 if( Note != null &&  !Note.equals("") )
			 {
				 Note=Note.toString().replaceAll("'", " ");
			 }
			 
			String query2=" insert into note ( " +
						  " Note_Id_Old,Note_Type_Id,Reference_Id,DateEntered,User_Name,Note ) " +
						  " values (  " +
						  " "+  rsSql.getObject(1) + ", "  +  				//NoteId
						  " ( select Note_Type_Id from note_type where Note_Type_Id_Old="+ rsSql.getObject(2) + " )  , " +  //NoteTypeId
						  " "+  rsSql.getObject(3) + ", "  +  				//ReferenceId
						  " '"+ DateEntered + "', "  +  			//DateEntered
						  " '"+  UserName + "', "  +  			//UserName
						  " '"+  Note + "' "  +  			//Note
						  " ) ";
			 stMySql.executeUpdate(query2);
				
			 flag=true;
	         count++;
		}
		
		
		stMySql.close();
		
		if(flag)
		{
			getId.UpdateMaxId("Note","note","Note_Id_Old"); // Update max id of Note.
			total=total-count;
			flag=false;
			System.out.println("Note records successfully migrated [ Total Rows ] : "+ count);
			msg="Note records successfully migrated [ Total Rows ] : "+ count;
		}
		else
		{
			total=0;
			System.out.println("No new records found in Note");
			msg="No new records found in Note";
		}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
				Logs.writeLog(ex,"Note");
				ex.printStackTrace();
		}
		return msg;
	}
	/**
	 * Insert Note Setting Records. 
	 * @return
	 */
	public String insert_Note_Setting()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				
			
		count=0;
		Statement stSql =conn.connectionSqlServer().createStatement();
		Statement stMySql = conn.connectionmysql().createStatement();
		
		id = getId.GetMaxId("NoteSetting"); // take the max id of Note.
		
		String query1="select * from ( " +
					"	select " +
					"	NoteSettingId, Name, UnitOfMeasureId, " +
					"	row_number() over (order by NoteSettingId  ) as RowNum,(select count(NoteSettingId) from NoteSetting) as Total " +
					"	from NoteSetting " +
					"	)as Result " +
					" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
					
					ResultSet rsSql = stSql.executeQuery(query1);
					
		while(rsSql.next())
		{
			Object Name=rsSql.getObject(2);
			 
			 if( Name != null &&  !Name.equals("") )
			 {
				 Name=Name.toString().replaceAll("'", " ");
			 }
			 
			 if(single_count==0)
				{
					total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
					single_count=1;
				}
			String query2=" insert into note_setting ( " +
						  " Note_Setting_Id_Old,Name,Unit_Of_Measure_Id ) " +
						  " values ( " +
						  " "+  rsSql.getObject(1) + ", "  +  				//NoteSettingId
						  " '"+  Name + "', "  +  				//Name
						  " ( select UnitOfMeasureId from unitofmeasure where UnitOfMeasureIdOld="+ rsSql.getObject(3) + " )   " +  //UnitOfMeasureId
						  " ) " ;
			 stMySql.executeUpdate(query2);
				
			 flag=true;
	         count++;
		}
					
		
		
		stMySql.close();
		
		if(flag)
		{
			getId.UpdateMaxId("NoteSetting","note_setting","Note_Setting_Id_Old"); // Update max id of Note.
			total=total-count;
			flag=false;
			System.out.println("NoteSetting records successfully migrated [ Total Rows ] : "+ count);
			msg="NoteSetting records successfully migrated [ Total Rows ] : "+ count;
		}
		else
		{
			total=0;
			System.out.println("No new records found in NoteSetting");
			msg="No new records found in NoteSetting";
		}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
				Logs.writeLog(ex,"Note");
				ex.printStackTrace();
		}
		return msg;
	}
	
	public String insert_Child_Session()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				
			
		count=0;
		Statement stSql =conn.connectionSqlServer().createStatement();
		Statement stMySql = conn.connectionmysql().createStatement();
		
		id = getId.GetMaxId("ChildSession"); // take the max id of ChildSession.
		
		String query1="select * from ( " +
					"	select " +
					"	StudentId, CameraId, StartTime, EndTime, " +
					"	row_number() over (order by id  ) as RowNum,(select count(StudentId) from ChildSession) as Total " +
					"	from ChildSession " +
					"	)as Result" +
					" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
					
					ResultSet rsSql = stSql.executeQuery(query1);
					
		while(rsSql.next())
		{
			
			 Object StartTime=rsSql.getObject(3);
			 
			 if( StartTime != null &&  !StartTime.equals("") )
			 {
				 StartTime=rsSql.getObject(3);
			 }
			 else
			 {
				 StartTime="1500-01-01";
			 }
			 
			 Object EndTime=rsSql.getObject(4);
			 
			 if( EndTime != null &&  !EndTime.equals("") )
			 {
				 EndTime=rsSql.getObject(4);
			 }
			 else
			 {
				 EndTime="1500-01-01";
			 }
			
			 if(single_count==0)
				{
					total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
					single_count=1;
				}
			
			String query2=" insert into infant_live ( " +
						  " INFANT_ACCOUNT_DETAILS_ID,CAMERA_DETAILS_ID,START_NAP_TIME,END_NAP_TIME ) " +
						  " values ( " +
						  " ( select INFANT_ACCOUNT_DETAILS_ID from INFANT_ACCOUNT_DETAILS where STUDENT_ID="+ rsSql.getObject(1) + " )  , " +  //StudentId
						  " ( select CAMERA_DETAILS_ID from camera_details where CAMERA_ID_OLD="+ rsSql.getObject(2) + " )  , " +  //CameraId
						  " '"+  StartTime + "', "  +  				//StartTime
						  " '"+  EndTime + "' "  +  				//EndTime
						  " ) " ;
			
			 stMySql.executeUpdate(query2);
				
			 flag=true;
	         count++;
		}
		
		stMySql.close();
		
		if(flag)
		{
			getId.UpdateMaxId("ChildSession","infant_live","INFANT_LIVE_ID"); // Update max id of ChildSession.
			total=total-count;
			flag=false;
			System.out.println("ChildSession records successfully migrated [ Total Rows ] : "+ count);
			msg="ChildSession records successfully migrated [ Total Rows ] : "+ count;
		}
		else
		{
			total=0;
			System.out.println("No new records found in ChildSession");
			msg="No new records found in ChildSession";
		}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
				Logs.writeLog(ex,"Note");
				ex.printStackTrace();
		}
		return msg;
	}
	
	public String insert_Customer_Email_Statement()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				
			
		count=0;
		Statement stSql =conn.connectionSqlServer().createStatement();
		Statement stMySql = conn.connectionmysql().createStatement();
		
		id = getId.GetMaxId("CustomerEmailStatement"); // take the max id of CustomerEmailStatement.
		
		String query1="select * from ( " +
					"	select " +
					"	CustomerId, StatementMonth, SentOn, SentByUsername, " +
					"	row_number() over (order by CustomerId  ) as RowNum,(select count(CustomerId) from CustomerEmailStatement) as Total " +
					"	from CustomerEmailStatement " +
					"	)as Result " +
					" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
					
					ResultSet rsSql = stSql.executeQuery(query1);
					
		while(rsSql.next())
		{
			if(single_count==0)
			{
				total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
				single_count=1;
			}
			 Object StatementMonth=rsSql.getObject(2);
			 
			 if( StatementMonth != null &&  !StatementMonth.equals("") )
			 {
				 StatementMonth=rsSql.getObject(2);
			 }
			 else
			 {
				 StatementMonth="1500-01-01";
			 }
			 
			 Object SentOn=rsSql.getObject(3);
			 
			 if( SentOn != null &&  !SentOn.equals("") )
			 {
				 SentOn=rsSql.getObject(3);
			 }
			 else
			 {
				 SentOn="1500-01-01";
			 }
			 
			 Object SentByUsername=rsSql.getObject(4);
			 
			 if( SentByUsername != null &&  !SentByUsername.equals("") )
			 {
				 SentByUsername=SentByUsername.toString().replaceAll("'", " ");
			 }
			
			String query2=" insert into customer_email_statement ( " +
						  " PARENT_ACCOUNT_DETAILS_ID,Statement_Month,Sent_On,SentByUsername ) " +
					      " values ( " +
					      " ( select PARENT_ACCOUNT_DETAILS_ID from parent_account_details where PARENT_ID="+ rsSql.getObject(1) + " )  , " +  //CustomerId
					      " '"+  StatementMonth + "', "  +  				//StatementMonth
					      " '"+  SentOn + "', "  +  				//SentOn
					      " '"+  SentByUsername + "' "  +  				//SentByUsername
						  " ) ";
			
			 stMySql.executeUpdate(query2);
				
			 flag=true;
	         count++;
		}
		
		stMySql.close();
		
		if(flag)
		{
			getId.UpdateMaxId("CustomerEmailStatement","customer_email_statement","ID"); // Update max id of CustomerEmailStatement.
			total=total-count;
			flag=false;
			System.out.println("CustomerEmailStatement records successfully migrated [ Total Rows ] : "+ count);
			msg="CustomerEmailStatement records successfully migrated [ Total Rows ] : "+ count;
		}
		else
		{
			total=0;
			System.out.println("No new records found in CustomerEmailStatement");
			msg="No new records found in CustomerEmailStatement";
		}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
				Logs.writeLog(ex,"CustomerEmailStatement");
				ex.printStackTrace();
		}
		return msg;
	}
	
	/**
	 * Insert into Classroom Progression Records.
	 * @return
	 */
	public String insert_Classroom_Progression()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				
		
		count=0;
		Statement stSql =conn.connectionSqlServer().createStatement();
		Statement stMySql = conn.connectionmysql().createStatement();
		
		id = getId.GetMaxId("ClassroomProgression"); // take the max id of ClassroomProgression.
		
		String query1="select * from ( " +
					"	select " +
					"	ClassroomProgressionId, ClassroomId, NextClassroomId, " +
					"	row_number() over (order by ClassroomProgressionId  ) as RowNum,(select count(ClassroomProgressionId) from ClassroomProgression) as Total " +
					"	from ClassroomProgression " +
					"	)as Result"	+
					" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
					
					ResultSet rsSql = stSql.executeQuery(query1);
					
		while(rsSql.next())
		{
			if(single_count==0)
			{
				total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
				single_count=1;
			}
			
			String query2="insert into classroom_progression ( " +
						  " Classroom_Progression_Id_Old,Classroom_Id,Next_Classroom_Id )  " +
					      " values  ( " +
					      " "+  rsSql.getObject(1) + ", "  +  	   // ClassroomProgressionId
					      " ( select CLASSROOM_ID from classroom where Classroom_id_old="+ rsSql.getObject(2) + " )  , " +  //ClassroomId
					      " ( select CLASSROOM_ID from classroom where Classroom_id_old="+ rsSql.getObject(3) + " )   " +  //ClassroomId
						  " ) " ;
			 stMySql.executeUpdate(query2);
				
			 flag=true;
	         count++;
		}
		
		stMySql.close();
		
		if(flag)
		{
			getId.UpdateMaxId("ClassroomProgression","classroom_progression","Classroom_Progression_Id_Old"); // Update max id of ClassroomProgression.
			total=total-count;
			flag=false;
			System.out.println("ClassroomProgression records successfully migrated [ Total Rows ] : "+ count);
			msg="ClassroomProgression records successfully migrated [ Total Rows ] : "+ count;
		}
		else
		{
			total=0;
			System.out.println("No new records found in ClassroomProgression");
			msg="No new records found in ClassroomProgression";
		}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
				Logs.writeLog(ex,"ClassroomProgression");
				ex.printStackTrace();
		}
		return msg;
	}
	
	public String insert_Area()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				
			
		count=0;
		Statement stSql =conn.connectionSqlServer().createStatement();
		Statement stMySql = conn.connectionmysql().createStatement();
		
		id = getId.GetMaxId("Area"); // take the max id of Area.
		
		String query1=" select * from ( " +
					"	select " +
					"	AreaId, LocationId, Name, Active, " +
					"	row_number() over (order by AreaId  ) as RowNum,(select count(AreaId) from Area) as Total " +
					"	from Area " +
					"	)as Result " +
					" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
					
					ResultSet rsSql = stSql.executeQuery(query1);
					
		while(rsSql.next())
		{
			Object Name=rsSql.getObject(3);
			 
			 if( Name != null &&  !Name.equals("") )
			 {
				 Name=Name.toString().replaceAll("'", " ");
			 }
			 if(single_count==0)
				{
					total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
					single_count=1;
				}
			 
			String query2=" insert into area (" +
						  " Area_Id_Old,SCHOOL_INFO_ID,Name,Active ) " +
						  " values ( " +
						  " "+  rsSql.getObject(1) + ", "  +  	   // AreaId
						  " ( select SCHOOL_INFO_ID from school_info where Location_Id='"+ rsSql.getObject(2) +"' ), "+     // LocationId
						  " '"+  Name + "', "  +  	   // Name
						  " "+  rsSql.getObject(4) + " "  +  	   // Active
						  " ) ";
			 stMySql.executeUpdate(query2);
				
			 flag=true;
	         count++;
		}
		
		stMySql.close();
		
		if(flag)
		{
			getId.UpdateMaxId("Area","area","Area_Id_Old"); // Update max id of Area.
			total=total-count;
			flag=false;
			System.out.println("Area records successfully migrated [ Total Rows ] : "+ count);
			msg="Area records successfully migrated [ Total Rows ] : "+ count;
		}
		else
		{
			total=0;
			System.out.println("No new records found in Area");
			msg="No new records found in Area";
		}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
				Logs.writeLog(ex,"Area");
				ex.printStackTrace();
		}
		return msg;
	}
	
	public String insert_Numbers()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				
			
		count=0;
		Statement stSql =conn.connectionSqlServer().createStatement();
		Statement stMySql = conn.connectionmysql().createStatement();
		
		id = getId.GetMaxId("Numbers"); // take the max id of Numbers.
		
		String query1="select * from ( " +
					  "	select " +
					  "	Number, " +
					  "	row_number() over (order by Number  ) as RowNum,(select count(Number) from Numbers) as Total  "+
					  "	from Numbers " +
					  "	)as Result" +
					  " where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
						
						ResultSet rsSql = stSql.executeQuery(query1);
						
			while(rsSql.next())
			{
				if(single_count==0)
				{
					total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
					single_count=1;
				}
				String query2="insert into numbers ( " +
							  " Number ) " +
						      " values ( " +
						      " "+  rsSql.getObject(1) + " "  +  				//Number
							  " ) ";
				
				 stMySql.executeUpdate(query2);
					
				 flag=true;
		         count++;
			}
		
		stMySql.close();
		
		if(flag)
		{
			getId.UpdateMaxId("Numbers","numbers","Number"); // Update max id of Numbers.
			total=total-count;
			flag=false;
			System.out.println("Numbers records successfully migrated [ Total Rows ] : "+ count);
			msg="Numbers records successfully migrated [ Total Rows ] : "+ count;
		}
		else
		{
			total=0;
			System.out.println("No new records found in Numbers");
			msg="No new records found in Numbers";
		}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
				Logs.writeLog(ex,"Numbers");
				ex.printStackTrace();
		}
		return msg;
	}

}
