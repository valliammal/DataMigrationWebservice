package Main;
import java.sql.ResultSet;
import java.sql.Statement;

public class TaskDept {

	int id = 0;  // contain the max id 
	boolean flag = false;
	String msg = "";
	int count = 0;
	int total = 1; // contain total rows of tables.
	int single_count = 0; // execute first time only.

	Dabase_Connect conn = new Dabase_Connect();
	GetTableId getId = new GetTableId();

	public String insert_Task_Dept()
	{
		try {
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{

				count=0;
				Statement stSql =conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				// -------------------------- Select record from old database

				id = getId.GetMaxId("TaskDept"); // take the max id of TaskDept.

				String query1 = "select * from ( " +
						"	select  " +
						"	TaskDeptId, Name, RoleName, " +
						"	row_number() over (order by TaskDeptId  ) as RowNum,(select count(TaskDeptId) from TaskDept) as Total " +
						"	from TaskDept " +
						"	)as Result" +
						" where RowNum>=" + (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next())
				{

					Object Name = rsSql.getObject(2);

					if( Name != null &&  !Name.equals("") ) {
						Name=Name.toString().replaceAll("'", " ");
					}

					Object RoleName=rsSql.getObject(3);

					if( RoleName != null &&  !RoleName.equals("") ) {
						RoleName=RoleName.toString().replaceAll("'", " ");
					}

					if(single_count==0) {
						total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count=1;
					}

					String query2=" insert into task_dept ( " +
							" Task_Dept_Id_Old,Name,Role_Name ) " +
							" values ( "+
							" "+ rsSql.getObject(1) +", " +         // TaskDeptId
							" '"+ Name +"', " +       // Name
							" '"+ RoleName +"' " +       // RoleName
							" ) ";

					stMySql.executeUpdate(query2);

					flag=true;
					count++;
				}



				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("TaskDept", "task_dept", "Task_Dept_Id_Old"); // Update max id of TaskDept.
					total = total-count;
					flag = false;
					System.out.println("TaskDept records successfully migrated [ Total Rows ] : "+ count);
					msg = "TaskDept records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new rexords found in TaskDept");
					msg = "No new records found in TaskDept";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"TaskDept");
			ex.printStackTrace();
		}

		return msg;
	}
	/**
	 * Insert Task Records.
	 * @return
	 */
	public String insert_Task_Type() {
		try {
			total = 1;
			while (total != 0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				// -------------------------- Select record from old database

				id = getId.GetMaxId("TaskType"); // take the max id of TaskType.

				String query1="select * from ( " +
						"	select " +
						"	TaskTypeId, TaskDeptId, Name, " +
						"	row_number() over (order by TaskTypeId  ) as RowNum,(select count(TaskTypeId) from TaskType) as Total " +
						"	from TaskType " +
						"	)as Result" +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);
				while(rsSql.next()) {

					Object Name = rsSql.getObject(3);

					if( Name != null &&  !Name.equals("") ) {
						Name=Name.toString().replaceAll("'", " ");
					}

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = "insert into task_type ( " +
							" Task_Type_Id_Old,Task_Dept_Id,Name ) " +
							"  values ( " +
							" "+ rsSql.getObject(1) +", " +         // TaskTypeId
							" ( select Task_Dept_Id from task_dept where Task_Dept_Id_Old="+ rsSql.getObject(2) + " )  , " + // Task_Dept_Id
							" '"+ Name+"' " +         // Name
							" ) ";

					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}


				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("TaskType", "task_type", "Task_Type_Id_Old"); // Update max id of TaskType.
					total = total-count;
					flag = false;
					System.out.println("TaskType records successfully migrated [ Total Rows ] : " + count);
					msg = "TaskType records successfully migrated [ Total Rows ] : " + count;
				} else {
					total = 0;
					System.out.println("No new rexords found in TaskType");
					msg = "No new records found in TaskType";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"TaskType");
			ex.printStackTrace();
		}

		return msg;
	}

	/**
	 * Insert Task Status Records.
	 * @return
	 */
	public String insert_Task_Status()
	{
		try {
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				// -------------------------- Select record from old database

				id = getId.GetMaxId("TaskStatus"); // take the max id of TaskStatus.

				String query1="select * from ( " +
						"  select " + 
						"	TaskStatusId, Name, " +
						"	row_number() over (order by TaskStatusId  ) as RowNum,(select count(TaskStatusId) from TaskStatus) as Total " +
						"	from TaskStatus " +
						"	)as Result" +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object Name=rsSql.getObject(2);

					if( Name != null &&  !Name.equals("")) {
						Name=Name.toString().replaceAll("'", " ");
					}
					if(single_count==0) {
						total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count=1;
					}
					String query2=" insert into task_status ("+
							" Task_Status_Id_Old,Name ) " +
							" values ( " +
							" "+ rsSql.getObject(1) +", " +         // TaskStatusId
							" '"+ Name +"' " +         // Name
							" ) ";

					stMySql.executeUpdate(query2);

					flag=true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("TaskStatus", "task_status", "Task_Status_Id_Old"); // Update max id of TaskStatus.
					total = total-count;
					flag = false;
					System.out.println("TaskStatus records successfully migrated [ Total Rows ] : " + count);
					msg = "TaskStatus records successfully migrated [ Total Rows ] : " + count;
				} else {
					total = 0;
					System.out.println("No new rexords found in TaskStatus");
					msg = "No new records found in TaskStatus";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"TaskStatus");
			ex.printStackTrace();
		}

		return msg;
	}

	public String insert_Task()
	{
		try {

			total = 1;
			while(total!=0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				// -------------------------- Select record from old database

				id = getId.GetMaxId("Task"); // take the max id of Task.

				String query1= "select * from ( " +
						"	select " +
						"	TaskId, TaskTypeId, OwnerId, Owner, TaskStatusId,  " +
						"	CreatedDate, TaskDeptId, CreatorId, Creator, " +
						"	NeededByDate, ETA, CompletedDate, Subject, " +
						"	Notes, LocationIds, Locations, LocationNote, " +
						"	LastModifiedDate, LastModifiedById, LastModifiedBy, " +
						"	row_number() over (order by TaskId  ) as RowNum,(select count(TaskId) from Task) as Total " +
						"	from Task " +
						"	)as Result " +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";


				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next())
				{
					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					Object Owner = rsSql.getObject(4);

					if( Owner != null &&  !Owner.equals("")) {
						Owner=Owner.toString().replaceAll("'", " ");
					}

					Object Created_Date=rsSql.getObject(6);

					if( Created_Date != null &&  !Created_Date.equals("")) {
						Created_Date=rsSql.getObject(6);
					} else {
						Created_Date="1500-01-01";
					}

					Object Creator=rsSql.getObject(9);

					if(Creator != null &&  !Creator.equals("")) {
						Creator=Creator.toString().replaceAll("'", " ");
					}

					Object NeededByDate=rsSql.getObject(10);

					if( NeededByDate != null &&  !NeededByDate.equals("")) {
						NeededByDate=rsSql.getObject(10);
					} else {
						NeededByDate="1500-01-01";
					}

					Object ETA=rsSql.getObject(11);

					if (ETA != null &&  !ETA.equals("")) {
						ETA=rsSql.getObject(11);
					} else {
						ETA="1500-01-01";
					}

					Object CompletedDate=rsSql.getObject(12);

					if( CompletedDate != null &&  !CompletedDate.equals(""))  {
						CompletedDate = rsSql.getObject(12);
					} else {
						CompletedDate = "1500-01-01";
					}

					Object Subject=rsSql.getObject(13);

					if( Subject != null &&  !Subject.equals("")) {
						Subject=Subject.toString().replaceAll("'", " ");
					}

					Object Notes=rsSql.getObject(14);

					if( Notes != null &&  !Notes.equals("")) {
						Notes=Notes.toString().replaceAll("'", " ");
					}

					Object Locations=rsSql.getObject(16);

					if( Locations != null &&  !Locations.equals("")) {
						Locations=Locations.toString().replaceAll("'", " ");
					}

					Object LocationNote=rsSql.getObject(17);

					if( LocationNote != null &&  !LocationNote.equals("")) {
						LocationNote=LocationNote.toString().replaceAll("'", " ");
					}

					Object LastModifiedDate=rsSql.getObject(18);

					if( LastModifiedDate != null &&  !LastModifiedDate.equals("")) {
						LastModifiedDate=rsSql.getObject(18);
					} else {
						LastModifiedDate="1500-01-01";
					}

					Object Last_Modified_By=rsSql.getObject(20);

					if( Last_Modified_By != null &&  !Last_Modified_By.equals("")) {
						Last_Modified_By=Last_Modified_By.toString().replaceAll("'", " ");
					}

					String query2=" insert into task ( " +
							" Task_Id_Old,Task_Type_Id,Owner_Id,Owner,Task_Status_Id,Created_Date,Task_Dept_Id, " +
							" Creator_Id,Creator,Needed_By_Date,ETA,Completed_Date,Subject,Notes,Location_Ids,Locations, " +
							" Location_Note,Last_Modified_Date,Last_Modified_By_Id,Last_Modified_By ) " +
							" values ( " +
							" "+ rsSql.getObject(1) +", " +         // TaskId
							" ( select Task_Type_Id from task_type where Task_Type_Id_Old="+ rsSql.getObject(2) + " )  , " +   //TaskTypeId
							" "+ rsSql.getObject(3) +", " +         // OwnerId
							" '"+ Owner +"', " +         // Owner
							" ( select Task_Status_Id from task_status where Task_Status_Id_Old="+ rsSql.getObject(5) + " )  , " +   //Task_Status_Id
							" '"+ rsSql.getObject(6) +"', " +         // Created_Date
							" ( select Task_Dept_Id from task_dept where Task_Dept_Id_Old="+ rsSql.getObject(7) + " )  , " +  //Task_Dept_Id
							" "+ rsSql.getObject(8) +", " +         // CreatorId
							" '"+ Creator +"', " +         // Creator
							" '"+ NeededByDate +"', " +         // NeededByDate 
							" '"+ ETA +"', " +         // ETA
							" '"+ CompletedDate +"', " +         // CompletedDate
							" '"+ Subject +"', " +         // Subject
							" '"+ Notes +"', " +         // Notes
							" "+ rsSql.getObject(15) +", " +           // LocationIds
							" '"+ Locations +"', " +         // Locations
							" '"+ LocationNote +"', " +         // LocationNote
							" '"+ LastModifiedDate +"', " +         // LastModifiedDate
							"  "+ rsSql.getObject(19) +", " +         // Last_Modified_By_Id
							" '"+ Last_Modified_By +"' " +         // Last_Modified_By
							" ) ";
					stMySql.executeUpdate(query2);

					flag = true;
					count++;
				}


				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("Task", "task", "Task_Id_Old"); // Update max id of Task.
					total = total-count;
					flag = false;
					System.out.println("Task records successfully migrated [ Total Rows ] : "+ count);
					msg = "Task records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new rexords found in Task");
					msg = "No new records found in Task";
				}
			}
			single_count=0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"Task");
			ex.printStackTrace();
		}

		return msg;

	}

	/**
	 * Insert Task User setting Records.
	 * @return
	 */
	public String insert_Task_User_Settings() {
		try 		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{

				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				// -------------------------- Select record from old database

				id = getId.GetMaxId("TaskUserSettings"); // take the max id of TaskUserSettings.

				String query1 = "select * from ( " +
						"	select " +
						"	TaskUserSettingsId, UserId, AllowUpdateEmails, " +
						"	ReceiveDailySummary, ReceiveWeeklySummary, " +
						"	row_number() over (order by TaskUserSettingsId  ) as RowNum,(select count(TaskUserSettingsId) from TaskUserSettings) as Total " +
						"	from TaskUserSettings " +
						"	)as Result " +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);
				while(rsSql.next()) {
					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into task_user_settings ( " +
							" Task_User_Settings_Id_Old,User_Id,Allow_Update_Emails,Receive_Daily_Summary,Receive_Weekly_Summary ) " +
							" values ( " +
							"  "+ rsSql.getObject(1) +", " +          // TaskUserSettingsId
							"  "+ rsSql.getObject(2) +", " +          // UserId
							"  "+ rsSql.getObject(3) +", " +          // AllowUpdateEmails
							"  "+ rsSql.getObject(4) +", " +          // ReceiveDailySummary
							"  "+ rsSql.getObject(5) +" " +           // ReceiveWeeklySummary
							" ) ";
					stMySql.executeUpdate(query2);
					flag = true;
					count++;
				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("TaskUserSettings", "task_user_settings", "Task_User_Settings_Id_Old"); // Update max id of TaskUserSettings.
					total = total-count;
					flag = false;
					System.out.println("TaskUserSettings records successfully migrated [ Total Rows ] : "+ count);
					msg = "TaskUserSettings records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new rexords found in TaskUserSettings");
					msg = "No new records found in TaskUserSettings";
				}
			}
			single_count=0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"TaskUserSettings");
			ex.printStackTrace();
		}

		return msg;
	}
	/**
	 * Insert Task Read By Records.
	 * @return
	 */
	public String insert_Task_ReadBy()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				// -------------------------- Select record from old database

				id = getId.GetMaxId("TaskReadBy"); // take the max id of TaskReadBy.

				String query1="select * from ( " +
						"	select " +
						"	TaskReadById, TaskId, UserId, LastReadDate, " +
						"	row_number() over (order by TaskReadById  ) as RowNum,(select count(TaskReadById) from TaskReadBy) as Total " +
						"	from TaskReadBy " +
						"	)as Result" +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object LastReadDate = rsSql.getObject(4);

					if( LastReadDate != null &&  !LastReadDate.equals("")) {
						LastReadDate=rsSql.getObject(4);
					} else {
						LastReadDate="1500-01-01";
					}

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into task_read_by ( " + 
							" Task_Read_By_Id_Old, Task_Id,UserId,Last_Read_Date )" +
							" values ( "+
							"  "+ rsSql.getObject(1) +", " +           // TaskReadById
							" ( select Task_Id from task where Task_Id_Old="+ rsSql.getObject(2) + " )  , " +  //TaskId
							"  "+ rsSql.getObject(3) +", " +           // UserId
							"  '"+ LastReadDate +"' " +           // LastReadDate
							")";
					stMySql.executeUpdate(query2);
					flag = true;
					count++;
				}


				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("TaskReadBy", "task_read_by", "Task_Read_By_Id_Old"); // Update max id of TaskReadBy.
					total = total-count;
					flag = false;
					System.out.println("TaskReadBy records successfully migrated [ Total Rows ] : "+ count);
					msg = "TaskReadBy records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new rexords found in TaskReadBy");
					msg = "No new records found in TaskReadBy";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"TaskReadBy");
			ex.printStackTrace();
		}

		return msg;
	}

	/**
	 * Insert Task Resource Records.
	 * @return
	 */
	public String insert_Task_Resource()
	{
		try
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
				count=0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				// -------------------------- Select record from old database

				id = getId.GetMaxId("TaskResource"); // take the max id of TaskResource.

				String query1="select * from ( " +
						"	select " +
						"	ResourceUserId,TaskId, ResourceName, " +
						"	row_number() over (order by TaskId  ) as RowNum,(select count(ResourceUserId) from TaskResource) as Total " +
						"	from TaskResource " +
						"	)as Result" +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object ResourceName = rsSql.getObject(3);

					if( ResourceName != null &&  !ResourceName.equals("")) {
						ResourceName=rsSql.getObject(3);
					} else {
						ResourceName="1500-01-01";
					}
					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}
					String query2 = " insert into task_resource ( " +
							" Resource_User_Id_Old,Task_Id,Resource_Name ) " +
							" values ( " +
							"  "+ rsSql.getObject(1) +", " +           // ResourceUserId
							" ( select Task_Id from task where Task_Id_Old="+ rsSql.getObject(2) + " )  , " +  //TaskId
							"  '"+ ResourceName +"' " +           // ResourceName
							" ) ";

					stMySql.executeUpdate(query2);
					flag = true;
					count++;

				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("TaskResource", "task_resource", "Resource_User_Id_Old"); // Update max id of TaskResource.
					total = total-count;
					flag = false;
					System.out.println("TaskResource records successfully migrated [ Total Rows ] : "+ count);
					msg = "TaskResource records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new rexords found in TaskResource");
					msg = "No new records found in TaskResource";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"TaskResource");
			ex.printStackTrace();
		}

		return msg;
	}
}
