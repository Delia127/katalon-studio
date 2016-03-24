package com.kms.katalon.core.testdata;

import java.io.IOException;
import java.util.List;

import com.kms.katalon.core.testdata.reader.CSVReader;
import com.kms.katalon.core.testdata.reader.CSVSeparator;

public class CSVData extends AbstractTestData {

    private CSVReader reader;
    private CSVSeparator separator;

    public CSVData(String sourceUrl, boolean containsHeader, CSVSeparator separator) throws IOException {
        super(sourceUrl, containsHeader);
        this.separator = separator;
    }
    
    private CSVReader getReader() throws IOException {
        if (reader == null) {
            reader = new CSVReader(sourceUrl, separator, hasHeaders);
        }
        return reader;
    }

    @Override
    public String internallyGetValue(String columnName, int rowIndex) throws IOException {
        return getReader().getData().get(rowIndex)[getReader().getColumnIndex(columnName)];
    }

    @Override
    public String internallyGetValue(int columnIndex, int rowIndex) throws IOException {
        return getReader().getData().get(rowIndex)[columnIndex];
    }

    @Override
    public TestDataType getType() {
        return TestDataType.CSV_FILE;
    }

    @Override
    public String[] getColumnNames() throws IOException {
        if (getReader().getColumnNames() != null) {
            return getReader().getColumnNames();
        } else {
            return new String[0];
        }
    }

    @Override
    public int getRowNumbers() throws IOException {
        return getReader().getData().size();
    }

    @Override
    public int getColumnNumbers() throws IOException {
        return getColumnNames().length;
    }

    @Override
    public void activeHeaders(boolean active) throws IOException {
        super.activeHeaders(active);
        reader = null;
    }
    
    public List<String[]> getData() throws IOException {
        return getReader().getData();
    }
}
