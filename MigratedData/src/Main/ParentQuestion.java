package Main;
import java.sql.ResultSet;
import java.sql.Statement;

public class ParentQuestion {

	int id;  // contain the max id 
	boolean flag=false;
	String msg="";
	int count=0;int total=1; // contain total rows of tables.
	int single_count=0; // execute first time only.

	Dabase_Connect conn = new Dabase_Connect();
	GetTableId getId = new GetTableId();

	public String insert_Question_Type()
	{
		try {
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				// -------------------------- Select record from old database

				id = getId.GetMaxId("QuestionType"); // take the max id of QuestionType.

				String query1 = "select * from ( " +
						"	select " +
						"	QuestionTypeId, Type, AdminEmail, "+ 
						"	Active, BackofficeOnly, " +
						"	row_number() over (order by QuestionTypeId  ) as RowNum,(select count(QuestionTypeId) from QuestionType) as Total " +
						"	from QuestionType " +
						"	)as Result   " +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next())
				{

					Object Type=rsSql.getObject(2);

					if( Type != null &&  !Type.equals("")) {
						Type=Type.toString().replaceAll("'", " ");
					}

					Object AdminEmail=rsSql.getObject(3);

					if( AdminEmail != null &&  !AdminEmail.equals("")) {
						AdminEmail=AdminEmail.toString().replaceAll("'", " ");
					}

					if(single_count==0) {
						total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count=1;
					}
					String query2=" insert into question_type ( " +
							" Question_Type_Id_Old,Type,Admin_Email,Active,Back_office_Only ) "	+	
							" values ( " +
							" "+ rsSql.getObject(1) +", " +       // QuestionTypeId
							" '"+ Type +"', " +     // Type
							" '"+AdminEmail +"', " +     // AdminEmail
							" "+ rsSql.getObject(4) +", " +       // Active
							" "+ rsSql.getObject(5) +" " +        // BackofficeOnly
							" ) ";

					stMySql.executeUpdate(query2);

					flag=true;
					count++;
				}


				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("QuestionType", "question_type", "Question_Type_Id_Old"); // Update max id of QuestionType.
					total = total-count;
					flag = false;
					System.out.println("QuestionType records successfully migrated [ Total Rows ] : "+ count);
					msg = "QuestionType records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new rexords found in QuestionType");
					msg = "No new records found in QuestionType";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"QuestionType");
			ex.printStackTrace();
		}

		return msg;
	}

	public String insert_Question()
	{
		try {

			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				// -------------------------- Select record from old database
				id = getId.GetMaxId("Question"); // take the max id of Question.
				String query1 = "select * from ( " +
						"	select " +
						"	QuestionId, QuestionTypeId, CustomerId, " + 
						"	QuestionText, SentDate, IsDeleted, " +
						"	row_number() over (order by QuestionId  ) as RowNum,(select count(QuestionId) from Question) as Total " +
						"	from Question " +
						"	)as Result  " +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while (rsSql.next())
				{
					Object QuestionText=rsSql.getObject(4);

					if(QuestionText != null &&  !QuestionText.equals("")) {
						QuestionText=QuestionText.toString().replaceAll("'", " ");
					}

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2=" insert into question (  " +
							"  Question_Id_Old,Question_Type_Id,PARENT_ACCOUNT_DETAILS_ID,Question_Text,Sent_Date,Is_Deleted ) " +
							" values ( "+
							" "+ rsSql.getObject(1) +", " +       // QuestionId
							" ( select Question_Type_Id from question_type where Question_Type_Id_Old="+ rsSql.getObject(2) + " )  , " +
							" ( select PARENT_ACCOUNT_DETAILS_ID from parent_account_details where PARENT_ID="+ rsSql.getObject(3) + " )  , " +
							" '"+ QuestionText +"', " +       // QuestionText
							" '"+ rsSql.getObject(5) +"', " +       // SentDate
							"  "+ rsSql.getObject(6) +" " +        // IsDeleted
 					 	    " ) ";

					stMySql.executeUpdate(query2);
					flag = true;
					count++;

				}


				stMySql.close();

				if (flag)
				{
					getId.UpdateMaxId("Question", "question", "Question_Id_Old"); // Update max id of Question.
					total = total-count;
					flag = false;
					System.out.println("Question records successfully migrated [ Total Rows ] : "+ count);
					msg = "Question records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new rexords found in Question");
					msg = "No new records found in Question";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"Question");
			ex.printStackTrace();
		}

		return msg;
	}

	/**
	 * Insert Answer Records.
	 * @return
	 */
	public String insert_Answer()
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

				id = getId.GetMaxId("Answer"); // take the max id of Answer.

				String query1=" select * from ( " +
						"	select " +
						"	AnswerId, QuestionId, AnswerText, " +
						"	SentByUserName, AnswerDate, ReadByStaff, " + 
						"	ReadByCustomer, IsDeleted, " +
						"	row_number() over (order by AnswerId  ) as RowNum,(select count(AnswerId) from Answer) as Total " +
						"	from Answer " +
						"	)as Result  " +
						" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query1);

				while(rsSql.next()) {
					Object AnswerText=rsSql.getObject(3);
					if( AnswerText != null &&  !AnswerText.equals("")) {
						AnswerText=AnswerText.toString().replaceAll("'", " ");
					}

					Object SentByUserName=rsSql.getObject(4);

					if( SentByUserName != null &&  !SentByUserName.equals("")) {
						SentByUserName=SentByUserName.toString().replaceAll("'", " ");
					}

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					String query2 = " insert into answer ( " +
							" Answer_Id_Old,Question_Id,Answer_Text,Sent_By_User_Name,Answer_Date,Read_By_Staff, " +	
							" Read_By_Customer,IsDeleted ) " +
							" values ( " +
							"  "+ rsSql.getObject(1) +", " +        // AnswerId
							" ( select Question_Id from question where Question_Id_Old="+ rsSql.getObject(2) + " )  , " +    //QuestionId
							"  '"+ AnswerText +"', " +        // AnswerText
							"  '"+ SentByUserName +"', " +        // SentByUserName 
							"  '"+ rsSql.getObject(5) +"', " +        // AnswerDate
							"  "+ rsSql.getObject(6) +", " +          // ReadByStaff
							"  "+ rsSql.getObject(7) +", " +          // ReadByCustomer
							"  "+ rsSql.getObject(8) +" " +           // IsDeleted
							" ) ";

					stMySql.executeUpdate(query2);

					flag = true;
					count++;

				}

				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("Answer", "answer", "Answer_Id_Old"); // Update max id of Answer.
					total = total-count;
					flag = false;
					System.out.println("Answer records successfully migrated [ Total Rows ] : "+ count);
					msg = "Answer records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new rexords found in Answer");
					msg = "No new records found in Answer";
				}
			}
		} catch (Exception ex) {
			Logs.writeLog(ex,"Answer");
			ex.printStackTrace();
		}

		return msg;
	}


}
