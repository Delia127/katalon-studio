package com.kms.katalon.core.testdata.reader;

import java.text.MessageFormat;

import com.kms.katalon.core.constants.StringConstants;

public abstract class SheetPOI {
    private static final int COLUMN_HEADER_ROW_NUMBER = 0;

    private String sheetName;

    public SheetPOI(String sheetName) {
        setSheetName(sheetName);
    }

    public String getSheetName() {
        return sheetName;
    }

    protected void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getCellText(int col, int row) {
        int maxRow = getMaxRow();
        if (row > maxRow) {
            throw new IllegalArgumentException(MessageFormat.format(StringConstants.EXCEL_INVALID_ROW_NUMBER, row,
                    maxRow));
        }

        int maxColumnAtRow = getMaxColumn(row);

        if (maxColumnAtRow < 0) {
            return "";
        }

        if (col > maxColumnAtRow) {
//            throw new IllegalArgumentException(MessageFormat.format(StringConstants.EXCEL_INVALID_COL_NUMBER, col,
//                    maxColumnAtRow));
            return "";
        }

        return internallyGetCellText(col, row);
    }

    protected abstract String internallyGetCellText(int col, int row);

    public int getColumnIndex(String colName) {
        int col = -1;
        if (colName != null) {
            for (int i = 0; i < getMaxColumn(COLUMN_HEADER_ROW_NUMBER); i++) {
                String header = getCellText(i, COLUMN_HEADER_ROW_NUMBER);
                if (header != null && header.equals(colName)) {
                    col = i;
                    break;
                }
            }
        }
        return col;
    }

    public String getCellText(String colName, int row) {
        int col = getColumnIndex(colName);
        if (col < 0) return null;

        String text = getCellText(col, row);
        return text;
    }

    public abstract String getCellText(String cellAddress);

    public abstract String[] getRangeText(String rangeAddress);

    public abstract int getMaxRow();

    public abstract int getMaxColumn(int rowIndex);

    public String[] getColumnNames() {
        int maxColumnCounts = getMaxColumn(COLUMN_HEADER_ROW_NUMBER);
        if (maxColumnCounts < 0) {
            return new String[0];
        }

        String[] columnNames = new String[maxColumnCounts];
        for (int i = 0; i < getMaxColumn(COLUMN_HEADER_ROW_NUMBER); i++) {
            columnNames[i] = getCellText(i, COLUMN_HEADER_ROW_NUMBER);

        }
        return columnNames;
    }
}