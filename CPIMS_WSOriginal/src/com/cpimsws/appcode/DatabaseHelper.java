package com.cpimsws.appcode;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * This class used for Creating Database Connection
 * 
 * @EMP ID:104
 * @date 06-05-2014
 */
public class DatabaseHelper {

	private static final String url = "jdbc:mysql://localhost:3308/";//local 
	private static final String dbName = "cpims";//local
	private static final String driver = "com.mysql.jdbc.Driver";//final
	private static final String userName = "root";//final and local
	private static final String password = "123456";//local
	public static Connection connection() {
		try {
			Class.forName(driver).newInstance();
			return DriverManager.getConnection(url + dbName,userName, password);
		} catch (Exception e) {
			e.printStackTrace();
			Logs.writeLog(e);
			return null;
		}
	}
}