package com.kms.katalon.core.testdata;

import java.text.MessageFormat;
import java.util.ArrayList;

import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.util.AppPOI;
import com.kms.katalon.core.util.SheetPOI;

public class ExcelData extends AbstractTestData {
	private String sourceUrl;
	private AppPOI appPoi;
	private SheetPOI sheetPoi;

	public ExcelData(String sheetName, String sourceUrl) throws Exception {
		this.sourceUrl = sourceUrl;
		this.appPoi = new AppPOI(new String(sourceUrl));

		for (SheetPOI sheet : this.appPoi.getSheets()) {
			if (sheet.get_Name().equals(sheetName)) {
				sheetPoi = sheet;
			}
		}

		if (sheetPoi == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					StringConstants.XML_LOG_ERROR_SHEET_NAME_X_NOT_EXISTS, sheetName));
		}
	}

	@Override
	public String[] getColumnNames() {
		ArrayList<String> columnNames = sheetPoi.getColumnNames();
		return columnNames.toArray(new String[columnNames.size()]);
	}

	@Override
	public String getValue(String columnName, int rowIndex) throws IllegalArgumentException {
		verifyRowIndex(rowIndex);
		verifyColumnName(columnName);
		return sheetPoi.getCellText(columnName, rowIndex);
	}

	@Override
	public String getValue(int columnIndex, int rowIndex) throws IllegalArgumentException {
		verifyColumnIndex(columnIndex);
		verifyRowIndex(rowIndex);
		return sheetPoi.getCellText(columnIndex - 1, rowIndex);
	}

	@Override
	public TestDataType getType() {
		return TestDataType.EXCEL_FILE;
	}

	@Override
	public String getSourceUrl() {
		return sourceUrl;
	}

	@Override
	public int getRowNumbers() {
		return sheetPoi.get_max_row();
	}

	@Override
	public int getColumnNumbers() {
		return getColumnNames().length;
	}

}
