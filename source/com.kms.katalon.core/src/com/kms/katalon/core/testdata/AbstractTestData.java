package com.kms.katalon.core.testdata;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;

import com.kms.katalon.core.constants.StringConstants;

public abstract class AbstractTestData implements TestData {

    protected String sourceUrl;
    protected boolean hasHeaders;

    protected AbstractTestData(String sourceUrl, boolean hasHeaders) {
        this.sourceUrl = sourceUrl;
        this.hasHeaders = hasHeaders;
    }

    @Override
    public final String getSourceUrl() {
        return sourceUrl;
    }

    @Override
    public final boolean hasHeaders() {
        return hasHeaders;
    }

    protected void verifyRowIndex(int rowIndex) throws IOException {
        int rowNumber = getRowNumbers();
        if (rowIndex > rowNumber || rowIndex < BASE_INDEX) {
            throw new IllegalArgumentException(MessageFormat.format(
                    StringConstants.TD_ROW_INDEX_X_FOR_TEST_DATA_Y_INVALID, rowIndex, getSourceUrl(), rowNumber));
        }
    }

    protected void verifyColumnIndex(int columnIndex) throws IOException {
        int columnNumber = getColumnNumbers();
        if (columnIndex > columnNumber || columnIndex < BASE_INDEX) {
            throw new IllegalArgumentException(MessageFormat.format(
                    StringConstants.TD_COLUMN_INDEX_X_FOR_TEST_DATA_Y_INVALID, columnIndex, getSourceUrl(),
                    columnNumber));
        }
    }

    protected void verifyColumnName(String columnName) throws IOException {
        boolean isPresent = false;
        String[] columnNames = getColumnNames();
        for (String name : getColumnNames()) {
            if (name != null && name.equals(columnName)) {
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            throw new IllegalArgumentException(MessageFormat.format(
                    StringConstants.TD_COLUMN_NAME_X_FOR_TEST_DATA_Y_INVALID, columnName, getSourceUrl(),
                    Arrays.toString(columnNames)));
        }
    }

    @Override
    public final String getValue(int columnIndex, int rowIndex) throws IOException {
        verifyColumnIndex(columnIndex);
        verifyRowIndex(rowIndex);
        return internallyGetValue(columnIndex - BASE_INDEX, rowIndex - BASE_INDEX);
    }

    @Override
    public final String getValue(String columnName, int rowIndex) throws IOException {
        verifyRowIndex(rowIndex);
        verifyColumnName(columnName);
        return internallyGetValue(columnName, rowIndex - BASE_INDEX);
    }

    protected abstract String internallyGetValue(int columnIndex, int rowIndex) throws IOException;

    protected abstract String internallyGetValue(String columnName, int rowIndex) throws IOException;
    
    protected int getHeaderRowIdx() {
        return hasHeaders ? 1 : 0;
    }

    @Override
    public void activeHeaders(boolean active) throws IOException {
        hasHeaders = active;
    }
}
