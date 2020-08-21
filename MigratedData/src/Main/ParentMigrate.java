
package Main;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

public class ParentMigrate
{
	int id = 0;  // contain the max id 
	boolean flag = false;
	String randomUUIDString = "";
	Dabase_Connect conn = new Dabase_Connect();
	GetTableId getId = new GetTableId();
	String msg = "";
	int count = 0;
	int total = 1; // contain total rows of tables.
	int single_count = 0; // execute first time only.


	public String insert_Parent_details()
	{
		try
		{
			total = 1;
			while(total != 0)  // run until all records transfered to new database.
			{
				count = 0;
				Statement stSql = conn.connectionSqlServer().createStatement();
				Statement stMySql = conn.connectionmysql().createStatement();

				id = getId.GetMaxId("Contact"); // take the max id of StudentRelation.

				String query=" select * from ( " +
						" select " + 
						" Cust.LastName,Cust.FirstName,MiddleInitial,WorkPhoneExt, " + 
						" DOB,DriverLicense, DriverLicenseState,StreetAddress1, " +
						" StreetAddress2,State,City, Zip, " +
						" WorkDays,WorkStart, " +
						" WorkEnd, Occupation,CustRelationId, MaritalStatus, " +
						" PartnerId,Email, " +
						" WorkPhone,PCSPhone,HomePhone, AlternatePhone,Cust.CustomerId, " +
						" row_number() over (order by ContactId) as RowNum,(select count(CustomerId) from Customer) as Total " +
						" from Customer as Cust " +
						" left join contact as con on Cust.CustomerId=con.CustomerId " +
						" and Cust.LastName = con.LastName"+
						" and Cust.FirstName = con.FirstName"+
						" ) as Result " +
						" where RowNum >="+ (++id) +" and RowNum < "+ (id += GlobalValues.rowIncrement) + "  ";

				ResultSet rsSql = stSql.executeQuery(query);

				while(rsSql.next()) {
					
					Object stateabb=rsSql.getObject(7);
					if( stateabb != null &&  !stateabb.equals("")) {
						stateabb=rsSql.getObject(7);
					} else {
						stateabb="";
					}

					Object Streat1 = rsSql.getObject(8);
					Object Streat2 = rsSql.getObject(9);

					if( Streat1 != null &&  !Streat1.equals("")) {
						Streat1 = rsSql.getObject(8).toString().replaceAll("'", " ");
					}

					if( Streat2 != null &&  !Streat2.equals("")) {
						Streat2 = rsSql.getObject(9).toString().replaceAll("'", " ");
					}

					Object Occup = rsSql.getObject(16);

					if( Occup != null &&  !Occup.equals("")) {
						Occup = rsSql.getObject(16).toString().replaceAll("'", " ");
					}

					Object city = rsSql.getObject(11);

					if( city != null &&  !city.equals("")) {
						city = rsSql.getObject(11).toString().replaceAll("'", " ");
					} else {
						city = "";
					}


					Object dob = rsSql.getObject(5);
					if( dob != null &&  !dob.equals("") )
					{
						dob = rsSql.getObject(5);
					} else {
						dob = "1990-01-01";
					}


					Object mi=rsSql.getObject(3);
					if( mi != null &&  !mi.equals("")) {
						mi=rsSql.getObject(3);
					} else {
						mi="";
					}

					if(single_count == 0) {
						total = Integer.parseInt(rsSql.getObject("Total").toString()) ;
						single_count = 1;
					}

					UUID uuid = UUID.randomUUID();
					randomUUIDString = uuid.toString();

					String query1=" insert into parent_account_details (PARENT_ACCOUNT_DETAILS_ID,USER_TYPE_ID,LAST_NAME,FIRST_NAME," +
							" MiddleInitial, WorkPhoneExt, DOB,DRIVER_LICENSE,DRIVER_LICENSE_STATE, STREET_ADDRESS1,STREET_ADDRESS2, " +
							" STATE_ID,CITY_ID,ZIP,WORK_DAYS,WORK_START,WORK_END,OCCUPATION,MARRIED,  " +
							" GENDER,PROFILE_PICTURE,STATUS, " +
							" CREATION_DATE,CREATOR_ID,PARENT_ID" +
							" ) values (  " +
							" '" + randomUUIDString + "',  " +
							" " + 3 + " , " +
							" '" + rsSql.getObject(1).toString().replaceAll("'", " ") + "',  " +  // LAST_NAME
							" '" + rsSql.getObject(2).toString().replaceAll("'", " ") + "',  " +  // FIRST_NAME
							" '" + mi + "',  " +  // MiddleInitial
							" '" + rsSql.getObject(4) + "',  " +  // WorkPhoneExt
							"  '" + dob + "',  " +   // DOB
							" '" + rsSql.getObject(6) + "',  " +  // DRIVER_LICENSE
							" '" + stateabb + "',  " +  // DRIVER_LICENSE_STATE
							" '" +Streat1 + "',  " +  // STREET_ADDRESS1
							" '" + Streat2 + "',  " +  // STREET_ADDRESS2
							" ( select state_Id from state where STATEABBREVIATION='"+ rsSql.getObject(10) + "' )  ,  " +  // STATE_ID
							" ( select city_id from city where city='"+ city +"' )  ,  " +  // CITY_ID

								  " '" + rsSql.getObject(12) + "',  " +  // ZIP
								  " '" + rsSql.getObject(13) + "',  " +  // WORK_DAYS
								  " '" + rsSql.getObject(14) + "',  " +  // WORK_START
								  " '" + rsSql.getObject(15) + "',  " +  // WORK_END
								  " '" + Occup + "',  " +  // OCCUPATION
								  // 17 is CustRelationId
								  " " + rsSql.getObject(18) + ",  " +  // MARRIED
								  // 19 is PartnerId
								  " 'Male' ,  " +  // Male
								  " '' ,  " +  // PROFILE_PICTURE
								  " " + true + ", "+  // STATUS
								  "  now() , " +  // CREATION_DATE
								  "  '1eaba3ed-5c97-4b43-818f-01b1f48e863e' ," +  // CREATOR_ID 
								  "  "+ rsSql.getObject(25) +"  " +  // PARENT_ID 

				                  " ) ";
					stMySql.executeUpdate(query1);


					// ----------------- Insert records in  PARENT_EMAIL_DETAILS -----------------

					String query2=" insert into parent_email_details(PARENT_ACCOUNT_DETAILS_ID, EMAIL_ID ) values('" + randomUUIDString + "', '" + rsSql.getObject(20) + "') ";

					stMySql.executeUpdate(query2);

					//--------------------------------End-----------------------------------------


					// Insert records in parent_contact_details ----------------------------------
					Object val21=rsSql.getObject(21);
					Object val22=rsSql.getObject(22);
					Object val23=rsSql.getObject(23);
					Object val24=rsSql.getObject(24);

					if(val21 != null &&  !val21.equals("")) {
						String query3=" insert into parent_contact_details( PARENT_ACCOUNT_DETAILS_ID,CONTACT_NUMBER,CONTACT_DESCRIPTION ) " +
								" values ( " +
								"  '"+ randomUUIDString +"', "+
								"  '"+val21 + "', " +
								"  'WorkPhone' ) ";
						stMySql.executeUpdate(query3);  

					}

					if(val22 != null && !val22.equals("")) {
						String query4=" insert into parent_contact_details( PARENT_ACCOUNT_DETAILS_ID,CONTACT_NUMBER,CONTACT_DESCRIPTION ) " +
								" values ( " +
								"  '"+ randomUUIDString +"', "+
								"  '"+val22 + "', " +
								"  'PCSPhone' ) ";
						stMySql.executeUpdate(query4);  

					}

					if(val23 != null && !val23.equals("")) {
						String query5=" insert into parent_contact_details( PARENT_ACCOUNT_DETAILS_ID,CONTACT_NUMBER,CONTACT_DESCRIPTION ) " +
								" values ( " +
								"  '"+ randomUUIDString +"', "+
								"  '"+val23 + "', " +
								"  'HomePhone' ) ";

						stMySql.executeUpdate(query5);  
					}

					if(val24 != null && !val24.equals("")) {
						String query5=" insert into parent_contact_details( PARENT_ACCOUNT_DETAILS_ID,CONTACT_NUMBER,CONTACT_DESCRIPTION ) " +
								" values ( " +
								"  '" + randomUUIDString + "', "+
								"  '" +val24 + "', " +
								"  'AlternatePhone' ) ";

						stMySql.executeUpdate(query5);  
					}
					//--------------------------------End-----------------------------------------

					flag = true;
					count++;
				}
				stMySql.close();

				if(flag) {
					getId.UpdateMaxId("Contact","parent_account_details","PARENT_ID"); // Update max id of CustomerRelation.
					total = total-count;
					flag = false;
					System.out.println("Contact records successfully migrated [ Total Rows ] : "+ count);
					msg = "Contact records successfully migrated [ Total Rows ] : "+ count;
				} else {
					total = 0;
					System.out.println("No new records found in Contact");
					msg = "No new records found in Contact";
				}
			}
			single_count = 0;
		} catch (Exception ex) {
			Logs.writeLog(ex,"Parent_Details");
			ex.printStackTrace();
		}

		return msg;
	}

}
