package com.kms.katalon.core.testdata;

import java.io.IOException;

public interface TestData {

    int BASE_INDEX = 1;

    /**
     * Get String value
     * 
     * @param columnName column name
     * @param rowIndex row index (row number), starting from 1
     * @return String value
     * @throws IllegalArgumentException
     */
    String getValue(String columnName, int rowIndex) throws IOException;

    /**
     * Get String value
     * 
     * @param columnIndex column index (column number), starting from 1
     * @param rowIndex row index (row number), starting from 1
     * @return String value
     * @throws IllegalArgumentException
     */
    String getValue(int columnIndex, int rowIndex) throws IOException;

    /**
     * Get Object value
     * 
     * @param columnName column name
     * @param rowIndex row index (row number), starting from 1
     * @return Object value
     * @throws IllegalArgumentException
     */
    Object getObjectValue(String columnName, int rowIndex) throws IOException;

    /**
     * Get Object value
     * 
     * @param columnIndex index of the column, starting from 1
     * @param rowIndex row number
     * @return Object value
     * @throws IllegalArgumentException
     */
    Object getObjectValue(int columnIndex, int rowIndex) throws IOException;

    /**
     * @see TestDataType
     * @return type of test data
     */
    TestDataType getType();

    /**
     * 
     * @return url of test data
     */
    String getSourceUrl();

    /**
     * @return an array that contains name of all columns
     */
    String[] getColumnNames() throws IOException;

    /**
     * 
     * @return total rows of the test data
     */
    int getRowNumbers() throws IOException;

    /**
     * 
     * @return total columns of the test data
     */
    int getColumnNumbers() throws IOException;

    /**
     * @return uses headers or not
     */
    boolean hasHeaders();

    void activeHeaders(boolean active) throws IOException;
    
    /**
     * Used for logging
     * @return the information of the current test data, can be null if not implemented.
     */
    TestDataInfo getDataInfo();
}
