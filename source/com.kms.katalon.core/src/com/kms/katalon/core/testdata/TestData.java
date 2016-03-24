package com.kms.katalon.core.testdata;

import java.io.IOException;


public interface TestData {
    
    int BASE_INDEX = 1;
    
    /**
     * @param columnName: name of column
     * @param rowIndex: index of the row, starting from 1
     * @return cell value
     * @throws IllegalArgumentException
     */
    String getValue(String columnName, int rowIndex) throws IOException;
    
    /**
     * @param columnIndex: index of the column, starting from 1
     * @param row: row number
     * @return cell value
     * @throws IllegalArgumentException
     */
    String getValue(int columnIndex, int rowIndex) throws IOException;
    
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
}
