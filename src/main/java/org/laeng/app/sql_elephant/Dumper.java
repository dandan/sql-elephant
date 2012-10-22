package org.laeng.app.sql_elephant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.laeng.app.sql_elephant.writer.CSV;
import org.yaml.snakeyaml.Yaml;

import com.mysql.jdbc.Connection;

public class Dumper {

	private Connection connection;

	public Dumper() {
	}

	/**
	 * Dump query to file as defined in the configFile
	 * @param configFile
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void dump(File configFile) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Map<String, String> config = readConfig(configFile);
		dump(config);
	}
	
	
	/**
	 * Dump query to a file as defined in the config Map
	 * @param config
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void dump(Map<String, String> config) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, IOException {
		connection = db_connection(config);

		Statement stmt = connection.createStatement(
				java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		String selectquery = config.get("export_sql");

		ResultSet rs = stmt.executeQuery(selectquery);
		(new CSV()).writeFromResultSet(rs, new File("out.csv"));
		connection.close();
	}

	/**
	 * Connect to the DB using the connection parameters in the map
	 * Connection Params in config:
	 * - db_url: jdbc:mysql://localhost:3306/amee_profile_development
	 * - db_driver: com.mysql.jdbc.Driver
	 * - db_user: database user
	 * - db_password: password
	 * @param config
	 * @return
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static Connection db_connection(Map<String, String> config) throws SQLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {

		Connection connection = null;
		Class.forName(config.get("db_driver")).newInstance();
		connection = (Connection) DriverManager.getConnection(
				config.get("db_url"), config.get("db_user"), config.get("db_password"));
		return (connection);
	}

	/**
	 * Read configuration from yaml file
	 * @param configFile
	 * @return configuration map
	 * @throws FileNotFoundException
	 */
	private Map<String, String> readConfig(File configFile)
			throws FileNotFoundException {
		InputStream input = new FileInputStream(configFile);
		Yaml yaml = new Yaml();
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) yaml.load(input);
		return map;
	}

}
