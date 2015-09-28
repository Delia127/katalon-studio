package com.kms.katalon.core.testdata;


public interface TestData {
    /**
     * @param columnName: name of column
     * @param rowIndex: index of the row, starting from 1
     * @return cell value
     * @throws IllegalArgumentException
     */
    public String getValue(String columnName, int rowIndex) throws IllegalArgumentException;
    
    /**
     * @param columnIndex: index of the column, starting from 1
     * @param row: row number
     * @return cell value
     * @throws IllegalArgumentException
     */
    public String getValue(int columnIndex, int rowIndex) throws IllegalArgumentException;
    
    /**
     * @see TestDataType
     * @return type of test data
     */
    public TestDataType getType();
    
    /**
     * 
     * @return url of test data 
     */
    public String getSourceUrl();

    /** 
     * @return an array that contains name of all columns
     */
    public String[] getColumnNames();
    
    /**
     * 
     * @return total rows of the test data
     */
    public int getRowNumbers();
    
    /**
     * 
     * @return total columns of the test data
     */
    public int getColumnNumbers();
}
