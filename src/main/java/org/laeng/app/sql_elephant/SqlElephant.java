package org.laeng.app.sql_elephant;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import au.com.bytecode.opencsv.CSVWriter;

public class SqlElephant {

	private Connection connection;

	public SqlElephant() {
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
		connection = DriverManager.getConnection(url + dbName, userName,
				password);
		return (connection);
	}

	public void dump_to_csv() throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, IOException {
		connection = db_connection();

		Statement stmt = connection.createStatement(
				java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		String selectquery = "select * from companies c1, companies c2 limit 100000";

		ResultSet rs = stmt.executeQuery(selectquery);
		write_csv_from_resultset(rs);
		connection.close();
	}

	private void write_csv_from_resultset(ResultSet rs) throws SQLException,
			IOException {
		CSVWriter writer = new CSVWriter(new FileWriter("out.csv"), ',');

		ResultSetMetaData meta = rs.getMetaData();
		int colCount = meta.getColumnCount();
		String[] rowArray = new String[colCount];
		Object cell = null;

		String[] columnNames = new String[colCount];
		for (int i = 1; i < colCount; i++) {
			String columnName = meta.getColumnName(i);
			columnNames[i - 1] = columnName;
		}
		writer.writeNext(columnNames);

		while (rs.next()) {

			for (int i = 0; i < colCount; i++) {
				cell = rs.getObject(i + 1);
				rowArray[i] = cell == null ? null : cell.toString();
			}

			writer.writeNext(rowArray);
			// System.out.print("ID :" + rs.getInt(1) + " ");
			// System.out.println("Name :" + rs.getString(2));
		}

		writer.close();
	}

}
