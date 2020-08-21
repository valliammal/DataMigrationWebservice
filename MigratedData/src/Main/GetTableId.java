package Main;

import java.sql.ResultSet;
import java.sql.Statement;

public class GetTableId {

	int id = 0;

	Dabase_Connect conn = new Dabase_Connect();

	/**
	 * Get max id of table.
	 * 
	 * @param tableName
	 * @return
	 */

	public int GetMaxId(String tableName) {
		try {
			Statement stMySql = conn.connectionmysql().createStatement();

			// -------------------- Get Max Id From Table
			String idGet = " select Max_Id from id_manager where Table_Name='"
					+ tableName + "' ";
			ResultSet MaxId = stMySql.executeQuery(idGet);
			if (MaxId.next()) {
				id = Integer.parseInt(MaxId.getString(1));
			}
			return id;
			// ------------------------------------------------------------------------
		} catch (Exception ex) {
			 Logs.writeLog(ex,"MaxId");
			ex.printStackTrace();
			return 0;
		}

	}

	/**
	 * Update max is of table.
	 * 
	 * @param tableName
	 * @param maxId
	 */
	public void UpdateMaxId(String tableName, String newTableName,String colName) {
		try {

			Statement stMySql = conn.connectionmysql().createStatement();
			
			// Select count from new table.
			
			String q1=" select count(" + colName + ") from " + newTableName + " ";
			
			ResultSet MaxId = stMySql.executeQuery(q1);
			
			int nextId=0;
			
			while(MaxId.next())
			{
				 nextId= Integer.parseInt(MaxId.getString(1));
			}
			
						
			//-----------------------------
			
			
			String query = " update id_manager set Max_Id=" + nextId + "  where Table_Name='" + tableName + "'  ";
					
			stMySql.executeUpdate(query);
		} 
		
		catch (Exception ex) 
		{
			 Logs.writeLog(ex,"UpdateMaxId");
			ex.printStackTrace();

		}
	}

	
	/**
	 * Update max is of table.
	 * 
	 * @param tableName
	 * @param maxId
	 */
	public void UpdateMaxIdSingle(String tableName, int maxId) {
		try {

			Statement stMySql = conn.connectionmysql().createStatement();
						
			String query = " update id_manager set Max_Id=" + maxId + "  where Table_Name='" + tableName + "'  ";
					
			stMySql.executeUpdate(query);
		} catch (Exception ex) {
			 Logs.writeLog(ex,"UpdateMaxId");
			ex.printStackTrace();

		}
	}

}
