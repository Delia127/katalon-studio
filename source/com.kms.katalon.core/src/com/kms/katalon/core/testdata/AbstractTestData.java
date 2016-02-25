package com.kms.katalon.core.testdata;

import java.text.MessageFormat;
import java.util.Arrays;

import com.kms.katalon.core.constants.StringConstants;

public abstract class AbstractTestData implements TestData {

	protected void verifyRowIndex(int rowIndex) throws IllegalArgumentException {
		int rowNumber = getRowNumbers();
		//if (rowIndex > rowNumber || rowIndex <= 0) {
		if (rowIndex > rowNumber || rowIndex < 0) {
			throw new IllegalArgumentException(MessageFormat.format(
					StringConstants.TD_ROW_INDEX_X_FOR_TEST_DATA_Y_INVALID, rowIndex, getSourceUrl(), rowNumber));
		}
	}

	protected void verifyColumnIndex(int columnIndex) throws IllegalArgumentException {
		int columnNumber = getColumnNumbers();
		if (columnIndex >= columnNumber + 1 || columnIndex <= 0) {
			throw new IllegalArgumentException(MessageFormat.format(
					StringConstants.TD_COLUMN_INDEX_X_FOR_TEST_DATA_Y_INVALID, columnIndex, getSourceUrl(),
					columnNumber));
		}
	}

	protected void verifyColumnName(String columnName) throws IllegalArgumentException {
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

}
