package Main;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

public class InfantMigrate {
	
	int id=0;  // contain the max id 
	boolean flag=false;
	String randomUUIDString="";
	Dabase_Connect conn = new Dabase_Connect();
	GetTableId getId = new GetTableId();
	String msg="";
	int count=0;
	int total=1; // contain total rows of tables.
	int single_count=0; // execute first time only.
	
	/**
	 * Insert infant details and allergy
	 */
	public String infant_Details_Insert()
	{
		try
		{
			while(total!=0)  // run until all records transfered to new database.
			{
				
			
			count=0;
			Statement stSql =conn.connectionSqlServer().createStatement();
			Statement stMySql = conn.connectionmysql().createStatement();
			
			id = getId.GetMaxId("Student"); // take the max id of StudentRelation.
			
			String query= "select * from( select  CustomerId, LastName, FirstName, NickName," +
						  " replace(convert(varchar, DateOfBirth, 111),'/','-') as 'DateOfBirth', Gender, replace(convert(varchar, EnrollmentDate, 111),'/','-') as 'EnrollmentDate', replace(convert(varchar, TerminationDate, 111),'/','-') as 'TerminationDate', Active, Allergies,StudentId, "+
						  " row_number() over (order by StudentId) as RowNum,(select count(StudentId) from Student) as Total "+
						  " from Student )as Result " +
						  " where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";
			
			 ResultSet rsSql = stSql.executeQuery(query);
			 
			 while(rsSql.next())
			 {
				 Object term_date = GlobalValues.date_format(rsSql.getObject(8));
				 Object enroll_date = GlobalValues.date_format(rsSql.getObject(7));
				 
				 Object nick_name=rsSql.getObject(4);
				 
				 if( nick_name != null &&  !nick_name.equals("") )
				 {
					 nick_name=rsSql.getObject(4).toString().replaceAll("'", " ");
				 }
				 
				 if(single_count==0)
				 {
						total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count=1;
				 }
				 	UUID uuid = UUID.randomUUID();
	                randomUUIDString = uuid.toString();
	                
	                String query1="insert into infant_account_details(PARENT_ACCOUNT_DETAILS_ID,LAST_NAME,FIRST_NAME,NICKNAME,DOB,GENDER," +
	                				 "ENROLMENT_DATE,TERMINATION_DATE,STATUS,ENROLMENT_NUMBER, " +
	                			  "  PROFILE_PICTURE,CREATION_DATE,CREATOR_ID,STUDENT_ID )"+
	                		      " values(  "+
	                		      " ( select PARENT_ACCOUNT_DETAILS_ID from PARENT_ACCOUNT_DETAILS where PARENT_ID="+ rsSql.getObject(1) + " limit 1)  , " +  //CustomerId
	                		      "  '"+rsSql.getObject(2).toString().replaceAll("'", " ") + "', " +  // Last Name
	                			  " '" + rsSql.getObject(3).toString().replaceAll("'", " ") + "', "+   // First Name
	                			  " '" + nick_name + "', " +  // NickName
	                			  " str_to_date('" + rsSql.getObject(5) + "','%Y-%m-%d'), " +  // DOB
	                			  " '" + rsSql.getObject(6) + "', " +  // Gender
	                			  	enroll_date+" , " +  // EnrollDate
	                			  	term_date+" , " +  // TerminationDate
	                			  "  " + rsSql.getObject(9) + ", " +  // Active
	                			  " '', " +  // ENROLMENT_NUMBER
	                			  " '', " +  // PROFILE_PICTURE
	                			  "  now() , " +  // CREATION_DATE
	                			  "  '1eaba3ed-5c97-4b43-818f-01b1f48e863e', "+  // CREATOR_ID 
	                			  " '" + rsSql.getObject(11) + "' " +  // TerminationDate
	                			  " ) ";
	                try
	                {
	                	stMySql.executeUpdate(query1);
	                }
	                catch (Exception ex) 
	        		{
	        			 Logs.writeLog(ex,"InfantDetails#infant_account_details("+query1+")");
	        			ex.printStackTrace();
	        		}
	               
	                
	                // Insert infant Allergies
	                
	                
	                Object allerigies=rsSql.getObject(10);
	                if( allerigies != null &&  !allerigies.equals("") )
	                {
	                	allerigies=rsSql.getObject(10).toString().replaceAll("'", " ");
	                }
	                
	                String query2=" insert into infant_allergies(ALLERGY,INFANT_ACCOUNT_DETAILS_ID) "+
	                              " values('"+ allerigies +"',(select max(infant_account_details_id) from infant_account_details))  ";
	                			  
	                try
	                {
	                	stMySql.executeUpdate(query2);
	                }
	                catch (Exception ex) 
	        		{
	        			 Logs.writeLog(ex,"InfantDetails#infant_allergies("+query2+")");
	        			ex.printStackTrace();
	        		}
	              
	                // INSERT INTO INFANT RELATION 
	                
	                String query3=" insert into parent_infant_relation ( " +
	                			  " PARENT_ACCOUNT_DETAILS_ID,INFANT_ACCOUNT_DETAILS_ID ) " +
	                			  " values ( "+
	                			  " ( select PARENT_ACCOUNT_DETAILS_ID from PARENT_ACCOUNT_DETAILS where PARENT_ID="+ rsSql.getObject(1) + " limit 1)  , " +  //CustomerId
	                			  " (select max(infant_account_details_id) from infant_account_details) " +  //INFANT_ACCOUNT_DETAILS_ID
	                			  
	                			  " ) " ;
	                try
	                {
	                	stMySql.executeUpdate(query3);
	                }
	                catch (Exception ex) 
	        		{
	        			 Logs.writeLog(ex,"InfantDetails#infant_allergies("+query3+")");
	        			ex.printStackTrace();
	        		}
	                
	                
	                flag=true;
	                count++;
	                
			 }
			  stMySql.close();
			  
			 if(flag)
				{
					getId.UpdateMaxId("Student","infant_account_details","STUDENT_ID"); // Update max id of Student.
					total=total-count;
					flag=false;
					System.out.println("Student records successfully migrated [ Total Rows ] : "+ count);
					msg="Student records successfully migrated [ Total Rows ] : "+ count;
				}
				else
				{
					total=0;
					System.out.println("No new records found in Student");
					msg="No new records found in Student";
				}
			}
			single_count=0;
		}
		catch (Exception ex) 
		{
			 Logs.writeLog(ex,"InfantDetails");
			ex.printStackTrace();
		}
		
		return msg;
	}
	
	
	

}
