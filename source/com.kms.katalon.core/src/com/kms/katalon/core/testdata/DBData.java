package com.kms.katalon.core.testdata;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.db.DatabaseConnection;
import com.kms.katalon.core.db.ListObjectResultSetHandler;
import com.kms.katalon.core.db.SqlRunner;

public class DBData extends AbstractTestData {
    private String query;

    private DatabaseConnection databaseConnection;

    private List<List<Object>> fetchedData;

    private List<String> columnNames;

    private ListObjectResultSetHandler rsHandler;

    private Date retrievedDate;

    public DBData(DatabaseConnection databaseConnection, String query) throws SQLException {
        super(databaseConnection.getConnectionUrl(), true);
        this.query = query;
        this.databaseConnection = databaseConnection;
        this.rsHandler = new ListObjectResultSetHandler();
        this.fetchedData = fetchData();
    }

    public List<List<Object>> getData() {
        return fetchedData;
    }

    public Date getRetrievedDate() {
        return retrievedDate;
    }

    @Override
    public TestDataType getType() {
        return TestDataType.DB_DATA;
    }

    @Override
    public String[] getColumnNames() {
        return rsHandler.getColumnNames().toArray(new String[getColumnNumbers()]);
    }

    @Override
    public int getRowNumbers() {
        return fetchedData.size();
    }

    @Override
    public int getColumnNumbers() {
        return rsHandler.getColumnCount();
    }

    @Override
    protected Object internallyGetValue(int columnIndex, int rowIndex) throws IndexOutOfBoundsException {
        return fetchedData.get(rowIndex).get(columnIndex);
    }

    @Override
    protected Object internallyGetValue(String columnName, int rowIndex) throws IndexOutOfBoundsException {
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

    private List<List<Object>> fetchData() throws SQLException {
        try (SqlRunner sqlRunner = new SqlRunner(databaseConnection, query)) {
            return sqlRunner.query(rsHandler);
        } finally {
            retrievedDate = new Date();
        }
    }

}
