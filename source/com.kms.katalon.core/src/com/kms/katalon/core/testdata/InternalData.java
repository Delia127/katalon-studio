package com.kms.katalon.core.testdata;

import java.util.List;

public class InternalData extends AbstractTestData {

	private List<String[]> data;
	private List<String> columnNames;
	private String fileSource;

	public InternalData(String fileSource, List<String[]> data, List<String> columnNames) {
		this.fileSource = fileSource;
		this.data = data;
		this.columnNames = columnNames;
	}

	@Override
	public String getValue(String columnName, int rowIndex) {
		verifyColumnName(columnName);
		verifyRowIndex(rowIndex);
		//return data.get(rowIndex - 1)[getColumnIndex(columnName)];
		return data.get(rowIndex)[getColumnIndex(columnName)];
	}

	@Override
	public String getValue(int columnIndex, int rowIndex) throws IllegalArgumentException {
		verifyColumnIndex(columnIndex);
		verifyRowIndex(rowIndex);
		//return data.get(rowIndex - 1)[columnIndex - 1];
		return data.get(rowIndex)[columnIndex - 1];
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
	public String getSourceUrl() {
		return fileSource;
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
}
