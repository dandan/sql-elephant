package org.laeng.app.sql_elephant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.*;
import java.util.Map;

import org.laeng.app.sql_elephant.writer.CSV;
import org.yaml.snakeyaml.Yaml;

public class Dumper {

    public Dumper() {
    }

    /**
     * Dump query to file as defined in the configFile
     * 
     * @param configFile
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void dump(File configFile) throws SQLException, InstantiationException,
            IllegalAccessException, ClassNotFoundException, IOException {
        Map<String, String> config = readConfig(configFile);
        dump(config);
    }

    public void dump(Map<String, String> config) throws SQLException, InstantiationException,
            IllegalAccessException, ClassNotFoundException, IOException {

        if (config.get("iterate_sql") != null)
            iterativeDump(config);
        else
            simpleDump(config);

    }

    /**
     * Dump query to a file as defined in the config Map
     * 
     * @param config
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void simpleDump(Map<String, String> config) throws SQLException, InstantiationException,
            IllegalAccessException, ClassNotFoundException, IOException {
        Connection connection = db_connection(config);

        Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                java.sql.ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(Integer.MIN_VALUE);
        String selectquery = config.get("export_sql");

        ResultSet rs = stmt.executeQuery(selectquery);
        (new CSV()).writeFromResultSet(rs, new File(config.get("output_file")));
        connection.close();
    }

    public void iterativeDump(Map<String, String> config) throws SQLException,
            InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
        Connection iteratorConnection = db_connection(config);
        Connection batchConnection = db_connection(config);

        Statement iteratorStmt = iteratorConnection.createStatement(
                java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
        iteratorStmt.setFetchSize(Integer.MIN_VALUE);

        PreparedStatement preparedStatement = batchConnection.prepareStatement(config
                .get("export_sql"));

        ResultSet rs = iteratorStmt.executeQuery(config.get("iterate_sql"));
        FileWriter writer = new FileWriter(new File(config.get("output_file")));

        Long minId = null;
        Long id = null;        
        long batchSize = ((Integer)(Object)config.get("batch_size")).intValue();
        long batchPos = 0;
        long batchCount = 0;
        while (rs.next()) {
            batchPos++;
            id = new Long(rs.getLong(1));
            if (minId == null)
                minId = id;

            if (batchPos == batchSize) {
                batchCount++;
                System.out.printf("Outputting batch %d -- IDs: %d-%d\n", batchCount, minId.longValue(), id.longValue());
                dumpIterativeBatch(preparedStatement, writer, minId.longValue(), id.longValue(), (batchCount == 1));
                batchPos = 0;
                minId = null;
                id = null;
            }
        }
        if (minId != null) {
            System.out.printf("Outputting batch %d -- IDs: %d-%d\n", batchCount, minId.longValue(), id.longValue());
            dumpIterativeBatch(preparedStatement, writer, minId.longValue(), id.longValue(), (batchCount == 1));
        }
        
        writer.close();
        iteratorConnection.close();
    }

    public void dumpIterativeBatch(PreparedStatement preparedStatement, Writer writer, long minId,
            long maxId, boolean outputHeader) throws SQLException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, IOException {

        preparedStatement.setLong(1, minId);
        preparedStatement.setLong(2, maxId);
        ResultSet rs = preparedStatement.executeQuery();
        (new CSV()).writeFromResultSet(rs, writer, outputHeader);
    }

    /**
     * Connect to the DB using the connection parameters in the map Connection
     * Params in config: - db_url:
     * jdbc:mysql://localhost:3306/amee_profile_development - db_driver:
     * com.mysql.jdbc.Driver - db_user: database user - db_password: password
     * 
     * @param config
     * @return
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static Connection db_connection(Map<String, String> config) throws SQLException,
            InstantiationException, IllegalAccessException, ClassNotFoundException {

        Connection connection = null;
        Class.forName(config.get("db_driver")).newInstance();
        connection = (Connection) DriverManager.getConnection(config.get("db_url"),
                config.get("db_user"), config.get("db_password"));
        return (connection);
    }

    /**
     * Read configuration from yaml file
     * 
     * @param configFile
     * @return configuration map
     * @throws FileNotFoundException
     */
    private Map<String, String> readConfig(File configFile) throws FileNotFoundException {
        InputStream input = new FileInputStream(configFile);
        Yaml yaml = new Yaml();
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) yaml.load(input);
        return map;
    }

}
