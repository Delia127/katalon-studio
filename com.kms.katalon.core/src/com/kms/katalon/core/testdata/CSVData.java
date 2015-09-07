package com.kms.katalon.core.testdata;

import java.io.IOException;

import com.kms.katalon.core.util.CSVReader;
import com.kms.katalon.core.util.CSVSeperator;

public class CSVData extends AbstractTestData {

    private CSVReader reader;
    private String sourceUrl;

    public CSVData(String sourceUrl, boolean containHeader, CSVSeperator seperator) throws Exception {
        reader = new CSVReader(sourceUrl, seperator, containHeader);
        this.sourceUrl = sourceUrl;
    }

    @Override
    public String getValue(String columnName, int rowIndex) {
    	verifyColumnName(columnName);
    	verifyRowIndex(rowIndex);
        return reader.getData().get(rowIndex - 1)[reader.getColumnIndex(columnName)];
    }
    
	@Override
	public String getValue(int columnIndex, int rowIndex) throws IllegalArgumentException {
		verifyColumnIndex(columnIndex);
		verifyRowIndex(rowIndex);
		return reader.getData().get(rowIndex - 1)[columnIndex - 1];
	}

    @Override
    public TestDataType getType() {
        return TestDataType.CSV_FILE;
    }

    @Override
    public String getSourceUrl() {
        return sourceUrl;
    }

    @Override
    public String[] getColumnNames() {
        if (reader.getColumnNames() != null) {
            return reader.getColumnNames();
        } else {
            return new String[0];
        }
    }

    @Override
    public int getRowNumbers() {
        return reader.getData().size();
    }
	@Override
	public int getColumnNumbers() {
		try {
			return reader.getColumnCount();
		} catch (IOException e) {
			return 0;
		}
	}
}
