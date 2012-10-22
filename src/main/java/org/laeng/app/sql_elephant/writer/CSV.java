package org.laeng.app.sql_elephant.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import au.com.bytecode.opencsv.CSVWriter;

public class CSV {

	/**
	 * Iterate over a result set and print each row to a CSV file
	 * 
	 * @param rs
	 * @throws SQLException
	 * @throws IOException
	 */
	public void writeFromResultSet(ResultSet rs, File file) throws SQLException,
			IOException {
		writeFromResultSet(rs, new FileWriter(file));
	}
	
	/**
	 * Iterate over a result set and print each row to a CSV file
	 * 
	 * @param rs
	 * @throws SQLException
	 * @throws IOException
	 */
	public void writeFromResultSet(ResultSet rs, Writer writer) throws SQLException,
			IOException {
		CSVWriter csvWriter = new CSVWriter(writer, ',');

		int colCount = getColumnCount(rs);
		writeHeader(csvWriter, rs, colCount);
		writeData(csvWriter, rs, colCount);
		csvWriter.close();
	}

	/**
	 * Get column count from result set
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private int getColumnCount(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		return meta.getColumnCount();
	}

	/**
	 * Write header row to CSV
	 * 
	 * @param writer
	 * @param rs
	 * @param colCount
	 * @throws SQLException
	 */
	private void writeHeader(CSVWriter writer, ResultSet rs, int colCount)
			throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();

		String[] columnNames = new String[colCount];
		for (int i = 1; i <= colCount; i++) {
			String columnName = meta.getColumnName(i);
			columnNames[i - 1] = columnName;
		}
		writer.writeNext(columnNames);
	}

	/**
	 * Write all body rows to CSV
	 * 
	 * @param writer
	 * @param rs
	 * @param colCount
	 * @throws SQLException
	 */
	private void writeData(CSVWriter writer, ResultSet rs, int colCount)
			throws SQLException {

		Object cell = null;
		String[] rowArray = new String[colCount];

		while (rs.next()) {

			for (int i = 0; i < colCount; i++) {
				cell = rs.getObject(i + 1);
				rowArray[i] = cell == null ? null : cell.toString();
			}

			writer.writeNext(rowArray);
		}
	}
}
