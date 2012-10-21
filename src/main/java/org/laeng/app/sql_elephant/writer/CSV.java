package org.laeng.app.sql_elephant.writer;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import au.com.bytecode.opencsv.CSVWriter;


public class CSV {
	
	public void write_from_resultset(ResultSet rs) throws SQLException,
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
