package org.laeng.app.sql_elephant;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.laeng.app.sql_elephant.writer.CSV;

import com.mysql.jdbc.Connection;


public class Dumper {

	private Connection connection;

	public Dumper() {
	}

	public void dump() throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, IOException {
		connection = db_connection();

		Statement stmt = connection.createStatement(
				java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		String selectquery = "select * from companies c1, companies c2 limit 100000";

		ResultSet rs = stmt.executeQuery(selectquery);
		(new CSV()).write_from_resultset(rs);
		connection.close();
	}

	public static Connection db_connection() throws SQLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Connection connection = null;
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "amee_profile_development";
		String driverName = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "rootytooty";
		Class.forName(driverName).newInstance();
		connection = (Connection) DriverManager.getConnection(url + dbName, userName,
				password);
		return (connection);
	}

}
