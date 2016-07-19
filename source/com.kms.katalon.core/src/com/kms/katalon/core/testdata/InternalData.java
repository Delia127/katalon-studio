package com.kms.katalon.core.testdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InternalData extends AbstractTestData {

    private List<String[]> data;

    private List<String> columnNames;

    public InternalData(String fileSource, List<String[]> data, List<String> columnNames) {
        super(fileSource, true);
        this.data = data;
        this.columnNames = columnNames;
    }

    @Override
    public String internallyGetValue(String columnName, int rowIndex) throws IOException {
        return data.get(rowIndex)[getColumnIndex(columnName)];
    }

    @Override
    public String internallyGetValue(int columnIndex, int rowIndex) throws IOException {
        return data.get(rowIndex)[columnIndex];
    }

    private int getColumnIndex(String columnName) {
        String[] columnNames = getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public TestDataType getType() {
        return TestDataType.INTERNAL_DATA;
    }

    @Override
    public String[] getColumnNames() {
        return columnNames.toArray(new String[columnNames.size()]);
    }

    @Override
    public int getRowNumbers() {
        return data.size();
    }

    @Override
    public int getColumnNumbers() {
        return columnNames.size();
    }

    public List<String[]> getData() {
        return data;
    }

    @Override
    public List<List<Object>> getAllData() {
        List<List<Object>> data = new ArrayList<List<Object>>();
        for (String[] row : getData()) {
            data.add(new ArrayList<Object>(Arrays.asList(row)));
        }
        return data;
    }

}
