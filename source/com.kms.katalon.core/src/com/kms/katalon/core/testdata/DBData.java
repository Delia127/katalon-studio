package com.kms.katalon.core.testdata;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.ObjectUtils;

import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.db.DatabaseConnection;
import com.kms.katalon.core.db.SqlRunner;

public class DBData extends AbstractTestData {
    private String query;

    private DatabaseConnection databaseConnection;

    private List<List<String>> fetchedData;

    private List<String> columnNames;

    public DBData(String sourceUrl, DatabaseConnection databaseConnection, String query) throws SQLException {
        super(sourceUrl, false);
        this.query = query;
        this.databaseConnection = databaseConnection;
        this.fetchedData = fetchData();
    }

    @Override
    public TestDataType getType() {
        return TestDataType.DB_DATA;
    }

    @Override
    public String[] getColumnNames() {
        return columnNames.toArray(new String[getColumnNumbers()]);
    }

    @Override
    public int getRowNumbers() {
        return fetchedData.size();
    }

    @Override
    public int getColumnNumbers() {
        return columnNames.size();
    }

    @Override
    protected String internallyGetValue(int columnIndex, int rowIndex) throws IndexOutOfBoundsException {
        return fetchedData.get(rowIndex).get(columnIndex);
    }

    @Override
    protected String internallyGetValue(String columnName, int rowIndex) throws IndexOutOfBoundsException {
        return internallyGetValue(columnNames.indexOf(columnName), rowIndex);
    }

    @Override
    protected void verifyColumnName(String columnName) throws IOException {
        if (columnNames.indexOf(columnName) == -1) {
            throw new IllegalArgumentException(MessageFormat.format(
                    StringConstants.TD_COLUMN_NAME_X_FOR_TEST_DATA_Y_INVALID, columnName, getSourceUrl(),
                    columnNames.toString()));
        }
    }

    private List<List<String>> fetchData() throws SQLException {
        try (SqlRunner sqlRunner = new SqlRunner(databaseConnection, query)) {
            return sqlRunner.query(new ResultSetHandler<List<List<String>>>() {

                @Override
                public List<List<String>> handle(ResultSet rs) throws SQLException {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int cols = metaData.getColumnCount();

                    // Handle column names
                    columnNames = new ArrayList<String>();
                    for (int i = 0; i < cols; i++) {
                        // beware of getColumnLabel() and getColumnName()
                        columnNames.add(metaData.getColumnLabel(i));
                    }

                    // Handle result set
                    List<List<String>> result = new ArrayList<List<String>>();
                    while (rs.next()) {
                        List<String> row = new ArrayList<String>();
                        for (int i = 0; i < cols; i++) {
                            row.add(ObjectUtils.toString(rs.getObject(i + 1), null));
                        }
                        result.add(row);
                    }

                    return result;
                }
            });
        }
    }

}
