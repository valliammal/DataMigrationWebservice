package Main;
import java.sql.ResultSet;
import java.sql.Statement;

public class SchoolMigrate {
	int id;  // contain the max id 
	boolean flag=false;
	String msg="";
	int count=0;
	int total=1; // contain total rows of tables.
	int single_count=0; // execute first time only.

	Dabase_Connect conn = new Dabase_Connect();
	GetTableId getId = new GetTableId();

	/**
	 * Insert School records.
	 * @return
	 */
	public String insert_SchoolInfo()
	{
		try 
		{
			total=1;
			while(total != 0)  // run until all records transfered to new database.
			{


				count= 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				// -------------------------- Select record from old database

				id = getId.GetMaxId("Location"); // take the max id of state.

				String query= "Select * from( select  " +
						"	LocationId, LocationName, Description, " +
						"	AccountingDatabaseId, RoleName, Address1, " +
						"	Address2, City, State, Zip,  " +
						"	ViewOrder, Active, ImportId,Phone, " +
						"	row_number() over (order by  LocationId) as RowNum,(select count(LocationId) from Location) as Total " +
						"	from Location) as Result " +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";


				ResultSet rsSql = stSql.executeQuery(query);

				while(rsSql.next()) {

					Object importid = rsSql.getObject(13);
					Object import_id = 0;
					if( importid == null) {
						import_id=0;
					} else {
						import_id=importid;
					}

					if(single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query1= " insert into school_info ( Location_Id,SCHOOL_BRANCH_NAME,DESCRIPTION,ACCOUNTING_DATABASE_ID,  " +
							" ROLL_NAME,ADDRESS1,ADDRESS2,CITY_ID,STATE_ID,ZIP,VIEWORDER,STATUS,IMPORTID) " +
							" values ( "+
							" "+ rsSql.getObject(1) +", " +     // LocationId
							" '"+ rsSql.getObject(2) +"', " +   // LocationName
							" '"+ rsSql.getObject(3) +"', " +   // Description
							"  "+ rsSql.getObject(4) +", " +    // AccountDatabaseId
							" '"+ rsSql.getObject(5) +"', " +   // RollName
							" '"+ rsSql.getObject(6) +"', " +   // Address1
							" '"+ rsSql.getObject(7) +"', " +   // Address2
							" ( select city_id from city where city='"+ rsSql.getObject(8) +"' ) , " +   // City
							" ( select state_Id from state where STATEABBREVIATION='"+ rsSql.getObject(9) + "' )  ,  " +  // STATE_ID
							" '"+ rsSql.getObject(10) +"', " +  // Zip
							"  "+ rsSql.getObject(11) +", " +   // ViewOrder
							"  "+ rsSql.getObject(12) +", " +   // Active
							" "+ import_id +" " +  // Import Id

						       " ) ";

					stMySql.executeUpdate(query1);

					// Insert record into School_Contact_Details

					String query3=" insert into school_contact_details(SCHOOL_INFO_ID,CONTACT_NUMBER,CONTACT_DESCRIPTION  )  "+
							" values((select max(SCHOOL_INFO_ID) from school_info), '"+ rsSql.getObject(14)+"','' )"; 

					stMySql.executeUpdate(query3);
					//-------------------------------------------
					flag=true;
					count++;

				}
				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("Location", "school_info", "Location_Id"); // Update max id of Location.
					total = total-count;
					flag = false;
					System.out.println("Location records successfully migrated [ Total Rows ] : "+ count);
					msg="Location records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new rexords found in Location");
					msg = "No new records found in Location";
				}
			}
			single_count = 0;
		} 

		catch (Exception ex) {
			Logs.writeLog(ex,"Location");
			ex.printStackTrace();
		}

		return msg;
	}

	/**
	 * Insert classroom records.
	 * @return
	 */
	public String insert_ClassroomInfo()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				count=0;
				Statement stSql =conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				// -------------------------- Select record from old database

				id = getId.GetMaxId("Classroom"); // take the max id of classroom.

				String query=" Select * from( " + 
						" select  " +
						"  LocationId, Name, " +
						"	MaxSize, TeacherChildRatio, LowMinAgeMonths, " +
						"	HighMinAgeMonths, LowMaxAgeMonths, HighMaxAgeMonths, "+
						"	StartMonth, EndMonth, ViewOrder, Active, ImportId,ClassroomId, " +
						" row_number() over (order by  ClassroomId) as RowNum,(select count(ClassroomId) from Classroom) as Total "+
						"	from Classroom " +
						"	)as Result " +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";


				ResultSet rsSql = stSql.executeQuery(query);

				while(rsSql.next()) {

					Object importid = rsSql.getObject(13);
					Object import_id = 0;
					if( importid == null ) {
						import_id = 0;
					} else {
						import_id = importid;
					}
					if (single_count==0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query1 = " insert into  classroom  ( " +
							" SCHOOL_INFO_ID,NAME,MAX_SIZE,TEACHER_CHILD_RATIO,LowMinAgeMonths,HighMinAgeMonths,  "+
							" LowMaxAgeMonths,HighMaxAgeMonths,START_MONTH,END_MONTH,ViewOrder,STATUS,ImportId,Classroom_id_old )" +
							" values ( " +
							" ( select SCHOOL_INFO_ID from school_info where Location_Id='"+ rsSql.getObject(1) +"' ), "+     // LocationId
							" '"+ rsSql.getObject(2).toString().replaceAll("'", " ") +"',  "+  //  Name
							"  "+ rsSql.getObject(3) +",  "+   //  MaxSize
							"  "+ rsSql.getObject(4) +", "+    //  TeacherChildRatio
							"  "+ rsSql.getObject(5) +", "+    //  LowMinAgeMonths
							"  "+ rsSql.getObject(6) +", "+    //  HighMinAgeMonths
							"  "+ rsSql.getObject(7) +","+     //  LowMaxAgeMonths
							" "+ rsSql.getObject(8) +", " +    //  HighMaxAgeMonths
							" '"+ rsSql.getObject(9) +"', " +  //  START_MONTH
							" '"+ rsSql.getObject(10) +"', " + //  EndMonth
							" "+ rsSql.getObject(11) +",  " +  //  ViewOrder
							" "+ rsSql.getObject(12) +", " +   //  Active
							" "+ import_id +", " +   //  ImportId
							" "+ rsSql.getObject(14) +" " +   //  Classroom_id_old
							" ) ";

					stMySql.executeUpdate(query1);
					flag = true;
					count++;
				}
				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("Classroom", "classroom", "Classroom_id_old"); // Update max id of Classroom.
					total = total-count;
					flag = false;
					System.out.println("Classroom records successfully migrated [ Total Rows ] : "+ count);
					msg = "Classroom records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new rexords found in Classroom");
					msg = "No new records found in Classroom";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"Classroom");
			ex.printStackTrace();
		}

		return msg;


	}

	/**
	 * Insert Email Setting Records
	 * @return
	 */
	public String insert_Email_Setting()
	{
		try
		{
			total=1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("EmailSetting");


				String query1 = "Select * from( select " +
						"	LocationId, Purpose, FromAddress, " +
						"	ToAddress, IsDefault, TestAddress, TestIPAddress, " +
						"	row_number() over (order by  EmailSettingId) as RowNum,(select count(EmailSettingId) from EmailSetting) as Total " +
						"	from EmailSetting) as Result " +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into emailsetting ( LocationId,Purpose,FromAddress,ToAddress, " +
							" IsDefault,TestAddress,TestIPAddress) " +
							" values ( " +
							" ( select SCHOOL_INFO_ID from school_info where Location_Id='"+ rsSql.getObject(1) +"' ), "+ //LocationId
							" '"+ rsSql.getObject(2) +"', " +  // Purpose
							" '"+ rsSql.getObject(3) +"', " +  // FromAddress
							" '"+ rsSql.getObject(4) +"', " +  // ToAddress
							"  "+ rsSql.getObject(5) +" ,"  +  // IsDefault
							" '"+ rsSql.getObject(6) +"', " +  // TestAddress
							" '"+ rsSql.getObject(7) +"' " +   // TestIPAddress
							" ) ";

					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}

				stMySql.close();
				if(flag) {
					getId.UpdateMaxId("EmailSetting", "emailsetting", "EmailSettingId"); // Update max id of state.
					total = total-count;
					flag = false;
					System.out.println("EmailSetting records successfully migrate [ Total Rows ] : "+ count);
					msg = "EmailSetting records successfully migrate [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new rexords found in EmailSetting");
					msg = "No new records found in EmailSetting";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"EmailSetting");
			ex.printStackTrace();
		}

		return msg;
	}

	/**
	 * Insert Camera Details.	 * @return
	 */
	public String insert_Camera_Details()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{

				count=0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("Camera");


				String query1 = " Select * from(  " +
						" select " + 
						" CameraId, ClassroomId, Name, IP, InUse, UN, PW, " + 
						" WebServerPort, ImageTransferPort, PublicIP, DeviceSerialNo, " +
						" row_number() over (order by  CameraId) as RowNum,(select count(CameraId) from camera) as Total " +
						" from " + 
						" camera) " +
						" as Result " +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";


				ResultSet rsSql = stSql.executeQuery(query1);


				while(rsSql.next())
				{
					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2=" insert into camera_details ( CAMERA_ID_OLD,CLASSROOM_ID,NAME,IP,IN_USE,UN,PW, " +
							" WEB_SERVER_PORT,IMAGE_TRANSFER_PORT,PUBLIC_IP,DEVICE_SERIAL_NO,SCHOOL_INFO_ID)  " + 
							" VALUES( " +
							" " + rsSql.getObject(1) + ", " +  // CameraId
							" ( select CLASSROOM_ID from classroom where Classroom_id_old='"+ rsSql.getObject(2) +"' ), " + // ClassRoomId
							" '" + rsSql.getObject(3) + "', " +    // Name
							" '" + rsSql.getObject(4) + "', " +    // IP
							" " + rsSql.getObject(5) + ", " +      // InUse
							" '" + rsSql.getObject(6) + "', " +    // UN
							" '" + rsSql.getObject(7) + "', " +    // PW
							" " + rsSql.getObject(8) + ", " +    // WebServerPort
							" " + rsSql.getObject(9) + ", " +     // ImageTransferPort
							" '" + rsSql.getObject(10) + "', " +   // PublicIP
							" '" + rsSql.getObject(11) + "', " +    // DeviceSerialNo
							" (select school_info_id from classroom where classroom_id=(select CLASSROOM_ID from classroom where Classroom_id_old='"+ rsSql.getObject(2) +"') ) " + // ClassRoomId
							" ) "; 

					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}
				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("Camera", "camera_details", "CAMERA_ID_OLD"); // Update max id of state.
					total = total-count;
					flag = false;
					System.out.println("Camera records successfully migrate [ Total Rows ] : "+ count);
					msg = "Camera records successfully migrate [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new rexords found in Camera");
					msg = "No new records found in Camera";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"Camra");
			ex.printStackTrace();
		}

		return msg;
	}
}
