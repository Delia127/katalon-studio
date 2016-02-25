package com.kms.katalon.core.testdata;

import java.text.MessageFormat;

import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.testdata.reader.AppPOI;
import com.kms.katalon.core.testdata.reader.SheetPOI;

public class ExcelData extends AbstractTestData {
    private String sourceUrl;
    private AppPOI appPoi;
    private SheetPOI sheetPoi;

    public ExcelData(String sheetName, String sourceUrl) throws Exception {
        this.sourceUrl = sourceUrl;
        this.appPoi = new AppPOI(new String(sourceUrl));

        sheetPoi = appPoi.getSheetPOI(sheetName);
        
        if (sheetPoi == null) {
            throw new IllegalArgumentException(MessageFormat.format(
                    StringConstants.XML_LOG_ERROR_SHEET_NAME_X_NOT_EXISTS, sheetName));
        }
    }

    @Override
    public String[] getColumnNames() {
        return sheetPoi.getColumnNames();
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
    	//POI max row is 0-based, getRowNumbers should return 1-based row count
        return sheetPoi.getMaxRow() + 1;
    }

    @Override
    public int getColumnNumbers() {
        return sheetPoi.getColumnCount();
    }

}
