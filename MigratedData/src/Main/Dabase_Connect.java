package Main;

import java.sql.Connection;
import java.sql.DriverManager;

public class Dabase_Connect {

	Connection conmysql = null;
	Connection connection1 = null;

	public Connection connectionmysql() {
		return conmysql;
	}

	public Connection connectionSqlServer() {
		return connection1;
	}

	public Dabase_Connect() 
	{
		String url = "jdbc:mysql://localhost:3306/";
		String driver = "com.mysql.jdbc.Driver";
		String dbname = "cpnew_migrate";
		String username = "root";
		String pass = "CpImS!&2!#";
		try 
		{
			Class.forName(driver).newInstance();
			conmysql = DriverManager.getConnection(url + dbname, username, pass);
			Logs.writeLogTest("Connection Success");
		}
		catch (Exception e) 
		{
			Logs.writeLogTest("Connection Fail");
			 e.printStackTrace();
			
		}
		// Sql Server Connection
		try
		{
			java.lang.Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			// At our server
			connection1 = java.sql.DriverManager.getConnection("jdbc:sqlserver://192.168.0.3:1433;databaseName=CPS;Instance=SQL2008;user=imadmin;password=admin123;");
			Logs.writeLogTest("Sql Server Connection Success");
		}
		catch (Exception e)
		{
			Logs.writeLogTest("SQL Server Connection Fail"+e);
			e.printStackTrace();
		}
	}
}
