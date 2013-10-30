package org.laeng.app.sql_elephant.writer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CSVTest extends TestCase {

	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public CSVTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(CSVTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testWriteFromResultSet() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		ResultSetMetaData meta = mock(ResultSetMetaData.class);
		
		when(rs.getMetaData()).thenReturn(meta);
		when(meta.getColumnCount()).thenReturn(2);
		when(meta.getColumnLabel(1)).thenReturn("id");
		when(meta.getColumnLabel(2)).thenReturn("name");

		StringWriter stringWriter = new StringWriter();
		
		new CSV().writeFromResultSet(rs, stringWriter);
		
		assertEquals("\"id\",\"name\"\n", stringWriter.toString()); 
	}

}
