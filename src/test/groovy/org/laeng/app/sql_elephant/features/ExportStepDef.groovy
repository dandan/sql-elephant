package org.laeng.app.sql_elephant.features
import groovy.sql.Sql
import org.laeng.app.sql_elephant.Dumper
import org.supercsv.io.CsvMapReader
import org.supercsv.io.ICsvMapReader
import org.supercsv.prefs.CsvPreference

import java.sql.DatabaseMetaData
import java.sql.ResultSet

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

class Constants {
    static final String COMPANIES_OUTPUT = "companies.csv"
    static final String[] COLUMNS = ["id", "varchar_col", "varchar_col2", "integer_col", "date_col"]
}

class CukesUtilWorld {

    Sql sql

    public CukesUtilWorld() {
        ConfigObject config = getDBProperties()
        getDBConnection(config)
    }

    Sql getSql() {
        return sql;
    }

    void printClassPath(classLoader) {
        def urlPaths = classLoader.getURLs()
        println "classLoader: $classLoader"
        println urlPaths*.toString()
        if (classLoader.parent) {
            printClassPath(classLoader.parent)
        }
    }

    private ConfigObject getDBProperties() {
        ConfigObject config = new ConfigSlurper().parse(getClass().getClassLoader().getResource("db.test.properties").toURI().toURL())
        return config
    }

    private void getDBConnection(ConfigObject config) {
        sql = Sql.newInstance(config.jdbcUrl, config.jdbcUsername, config.jdbcPassword, config.jdbcClass)
    }
}

World {
    new CukesUtilWorld()
}

Before() {
    sql = getSql()

    DatabaseMetaData md = sql.connection.metaData
    ResultSet rs = md.getTables(null, null, "%", null)

    while (rs.next()) {
        tableName = rs.getString(3)

        println "DROPPING TABLE ${tableName}..."
        sql.execute("DROP TABLE " + tableName)
    }

    File outputCsv = new File(Constants.COMPANIES_OUTPUT);
    outputCsv.delete()
    assert outputCsv.exists() == false
}

After() {}

Given(~'^The following database table:$') { table ->
    createTable()
    insertIntoTable(table)
}

When(~'^I run the data export$') {->
    File outputCsv = new File(Constants.COMPANIES_OUTPUT);
    new Dumper().dump(new File("sample_configs/cukes_test_dump.yml"));
    assert outputCsv.exists()
}

Then(~'^I should output a batch dump file with the following values:$') { table ->
    def expectedCompanies = table.asMaps()

    File outputCsv = new File(Constants.COMPANIES_OUTPUT);

    outputCsv.withReader { reader ->

        ICsvMapReader csvReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE)
        skipCsvHeader(csvReader)

        numRows = 0
        while ((csvRow = csvReader.read(Constants.COLUMNS)) != null) {

            expectedCompany = expectedCompanies[numRows]

            expectedCompany.keySet().each{ expectedColumn ->
                assert (csvRow.get(expectedColumn) == null ? "" : csvRow.get(expectedColumn)) == expectedCompany.get(expectedColumn),
                       "Unmatched values for column ${expectedColumn}, csv: [${csvRow.get(expectedColumn)}], expected: [${expectedCompany.get(expectedColumn)}]"
            }

            assert expectedCompany.keySet().size() == csvRow.keySet().size(), "Expected the number of columns in the csv & expected results to match"
            numRows++
        }

        assert numRows == expectedCompanies.size()
    }
}

private void skipCsvHeader(CsvMapReader csvReader) {
    csvReader.getHeader(true)
}

def createTable() {
    sql.execute('''create table SQL_ELEPHANT_TEST (
    id integer not null primary key,
    varchar_col varchar(20),
    varchar_col2 varchar(20),
    integer_col integer,
    date_col datetime) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci''')
}

def insertIntoTable(table){

    def placeholders = ["?","?","?","?","?"]
    def tableName = "SQL_ELEPHANT_TEST"

    table.raw.eachWithIndex { row, i ->
        if (i > 0) {
            def insertStatement = "insert into " + tableName + " (" + Constants.COLUMNS.join(",") + ") values (" + placeholders.join(",") + ")"
            sql.execute(insertStatement, row)
        }
    }

    countRow = sql.firstRow("select count(*) count from " + tableName)
    assert countRow.get("count") == table.raw.size() -1
}