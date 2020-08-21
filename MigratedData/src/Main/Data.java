package Main;


import java.sql.ResultSet;
import java.sql.Statement;


public class Data {

	int id;  // contain the max id 
	boolean flag=false;
	String msg="";
	int count=0;
	int total=1; // contain total rows of tables.
	int single_count=0; // execute first time only.

	Dabase_Connect conn = new Dabase_Connect();
	GetTableId getId = new GetTableId();

	/**
	 * Message display.
	 */
		
	 public void msg()
	 {
		System.out.println("Data Migration Start...");
	 }

	
	/**
	 * Migrate record from state to state.
	 */

	public String insert_State() {

		try 
		{	
			while(total!=0)  // run until all records transfered to new database.
			{
				
			
			count=0;
			Statement stSql =conn.connectionSqlServer().createStatement();
			Statement stMySql = conn.connectionmysql().createStatement();
			// -------------------------- Select record from old database
			id = getId.GetMaxId("state"); // take the max id of state.
			String query = " select * from (select distinct StateName ,StateAbbreviation , "
					+ " row_number() over (order by StateId) as RowNum,(select count(StateName) from states) as Total "
					+ " from States) as Result "
					+ " where RowNum>="
					+ (++id)
					+ " and RowNum < " + (id += GlobalValues.rowIncrement) + " ";
			 ResultSet rsSql = stSql.executeQuery(query);
			// ------------------------- End Old Work -------------------------------------
			// -------------------- Insert into new table
			while (rsSql.next()) {
			if(single_count==0)
			{
				total=Integer.parseInt(rsSql.getObject(4).toString()) ;
				single_count=1;
			}
			
			
			
			
			String query1 = " insert into state(STATE,STATEABBREVIATION)values('" + rsSql.getObject(1) + "' , '" + rsSql.getObject(2) +"') "; 
			stMySql.executeUpdate(query1);
			flag=true;
			count++;
				// -----------------------End New Work------------------------
			}
			if(flag)
			{
				
				getId.UpdateMaxId("state", "state", "STATE_ID"); // Update max id of state.
				
				total=total-count;
				
				flag=false;
				System.out.println("State records successfully migrated [ Total Rows ] : "+ count);
				msg="State records successfully migrated [ Total Rows ] : "+ count;
			}
			else
			{
				total=0;
				System.out.println("No new rexords found in state");
				msg="No new records found in state";
			}
			
			
		}
			
			single_count=0;
			
			
			
		} 
		
		catch (Exception ex) {
			 Logs.writeLog(ex,"State");
			ex.printStackTrace();
		}
		
		return msg;

	}

	
	/**
	 * Migrate record from location to city. 
	 */
	
	public String insert_City()
	{
		
		try 
		{
			total=1;
			while(total!=0)  // run until all records transfered to new database.
			{
			
			count=0;

			Statement stSql =conn.connectionSqlServer().createStatement();
			Statement stMySql = conn.connectionmysql().createStatement();
			
			id = getId.GetMaxId("City"); // take the max id of city.
			
			String query="select * from( select distinct City,State, row_number() over(order by locationid) as RowNum,(select count(City) from Location) as Total from Location) as Result "+
					     " where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";
			
			ResultSet rsSql = stSql.executeQuery(query);
			
			
			
			while(rsSql.next())
			{
				if(single_count==0)
				{
					total=Integer.parseInt(rsSql.getObject("Total").toString()) ;
					single_count=1;
				}
				
				
				Object value=rsSql.getObject(1);
				if( value != null &&  !value.equals("") )
				{
					
				String query1=" insert into city(CITY,STATE_ID) "+
			                  " values('" + rsSql.getObject(1) +"' , (select state_id  from state where STATEABBREVIATION='"+ rsSql.getObject(2) +"') ) ";
					
				stMySql.executeUpdate(query1);
				
				flag=true;
				count++;
				}
				
				
			}
			stMySql.close();
			
			
			if(flag)
			{
				getId.UpdateMaxId("City","city","CITY_ID"); // Update max id of city.
				
				total=total-count;
				
				flag=false;
				System.out.println("City records successfully migrated [ Total Rows ] : "+ count);
				msg="City records successfully migrated [ Total Rows ] : "+ count;
			}
			else
			{
				total=0;
				System.out.println("No new rexords found in city");
				msg="No new rexords found in city";
			}
			
			}
			
			single_count=0;
		}
		catch (Exception ex) {
			 Logs.writeLog(ex,"City");
			ex.printStackTrace();
		}
		
		return msg;
	}

	/**
	 * Migrate record from CustomerRealtion to Relations.
	 */
	
	public String insert_CustomerRelation()
	{
		try
		{
			count=0;
			Statement stSql =conn.connectionSqlServer().createStatement();
			Statement stMySql = conn.connectionmysql().createStatement();
			
			id = getId.GetMaxId("CustomerRelation"); // take the max id of CustomerRelation.
			
		    String query="select * from ( select distinct reltype,rellabel,CustomerRelationId, row_number() over (order by RelLabel) as RowNum from  CustomerRelation) as Result "+
				   		" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";
		    
		    ResultSet rsSql = stSql.executeQuery(query);
			
			while(rsSql.next())
			{
				String query1=" insert into relations (reltype,relation,CUSTOMER_RELATION_ID_OLD) values ( "+
							  "  " + rsSql.getObject(1) + ",  " +
							  " '" + rsSql.getObject(2) + "',  " +
							  "  " + rsSql.getObject(3) + "  " +
			                  " ) ";
				stMySql.executeUpdate(query1);
				
				flag=true;
				count++;
			}
			stMySql.close();
			if(flag)
			{
				getId.UpdateMaxIdSingle("CustomerRelation", --id); // Update max id of CustomerRelation.
				flag=false;
				System.out.println("CustomerRelation records successfully migrated [ Total Rows ] : "+ count);
				msg="CustomerRelation records successfully migrated [ Total Rows ] : "+ count;
			}
			else
			{
				System.out.println("No new records found in CustomerRelation");
				msg="No new records found in CustomerRelation";
			}
			
			
			
		}
		catch (Exception ex) 
		{
			 Logs.writeLog(ex,"Relation");
			ex.printStackTrace();
		}
		
		return msg;
	}

	/**
	 * Migrate record from StudentRelationType to Relations.
	 */
	
	public String insert_StudentRelation()
	{
		try
		{
			count=0;
			Statement stSql =conn.connectionSqlServer().createStatement();
			Statement stMySql = conn.connectionmysql().createStatement();
			
			id = getId.GetMaxId("StudentRelation"); // take the max id of StudentRelation.
			
		    String query="select * from ( select distinct reltype,rellabel,StudentRelationTypeId, row_number() over (order by RelLabel) as RowNum from  StudentRelationType) as Result "+
				   		" where RowNum>="+ (++id) +" and RowNum< "+ (id += GlobalValues.rowIncrement) + "  ";
		    
		    ResultSet rsSql = stSql.executeQuery(query);
			
			while(rsSql.next())
			{
				String query1=" insert into relations (reltype,relation,STUDENT_RELATIONS_ID_OLD) values ( "+
							  "  " + rsSql.getObject(1) + ",  " +
							  " '" + rsSql.getObject(2) + "',  " +
							  " " + rsSql.getObject(3) + "  " +
			                  " ) ";
				stMySql.executeUpdate(query1);
				
				flag=true;
				count++;
			}
			stMySql.close();
			if(flag)
			{
				getId.UpdateMaxIdSingle("StudentRelation", --id); // Update max id of CustomerRelation.
				flag=false;
				System.out.println("StudentRelation records successfully migrated :" + count);
				msg="StudentRelation records successfully migrated [ Total Rows ] : "+ count;
			}
			else
			{
				System.out.println("No new records found in StudentRelation");
				msg="No new records found in StudentRelation";
			}
			
		}
		catch (Exception ex) 
		{
			Logs.writeLog(ex,"Relation");
			ex.printStackTrace();
		}
		
		return msg;
	}
}

