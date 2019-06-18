package com.kms.katalon.core.testdata.reader;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.testdata.ExcelData;
import com.kms.katalon.core.testdata.decorator.TestDataCellDecorator;

public class SheetPOI extends ExcelData {
    private static final int COLUMN_HEADER_ROW_NUMBER = 0;

    private Workbook workbook;

    private Sheet sheet;

    private int columnCount = -1;

    private String sheetName;

    private List<CellRangeAddress> mergedRegionsList;

    private String[] columnNames;

    public SheetPOI(String sourceUrl, boolean hasHeaders, Workbook workbook) throws IOException {
        super(sourceUrl, hasHeaders);
        this.workbook = workbook;
    }

    /**
     * Get the sheet name of this excel data
     * 
     * @return the sheet name
     */
    @Override
    public String getSheetName() {
        return sheetName;
    }

    private Sheet getSheet() {
        return sheet;
    }

    private List<CellRangeAddress> getMergedRegions() {
        if (mergedRegionsList == null) {
            mergedRegionsList = new ArrayList<CellRangeAddress>();
            for (int i = 0; i < getSheet().getNumMergedRegions(); i++) {
                mergedRegionsList.add(getSheet().getMergedRegion(i));
            }
        }

        return mergedRegionsList;
    }

    @Override
    protected Object internallyGetValue(int col, int row) throws IOException {
        int maxRow = getRowNumbers();
        if (row > maxRow) {
            throw new IllegalArgumentException(
                    MessageFormat.format(StringConstants.EXCEL_INVALID_ROW_NUMBER, row, maxRow));
        }

        int maxColumnAtRow = getMaxColumn(row + getHeaderRowIdx());

        if (maxColumnAtRow < 0) {
            return null;
        }

        if (col > maxColumnAtRow) {
            // throw new IllegalArgumentException(MessageFormat.format(StringConstants.EXCEL_INVALID_COL_NUMBER, col,
            // maxColumnAtRow));
            return null;
        }

        // check if cell index is in a merged region
        for (CellRangeAddress mergedRegion : getMergedRegions()) {
            // If the region does contain the cell index
            if (mergedRegion.isInRange(row, col)) {
                // Now, you need to get the cell from the top left hand corner of this
                return getCellAt(mergedRegion.getFirstColumn(), mergedRegion.getFirstRow());
            }
        }

        return getCellAt(col, row + getHeaderRowIdx());
    }
    
    private Cell getCellAt(int col, int row) {
        Row curRow = sheet.getRow(row);

        if (curRow == null) {
            return null;
        }

        Cell curCell = curRow.getCell(col);

        if (curCell == null) {
            return null;
        }
        return curCell;
    }

    protected int getColumnIndex(String colName) throws IOException {
        if (colName == null) {
            throw new IllegalArgumentException("Column name cannot be null");
        }

        String[] columnNames = getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            if (colName.equals(columnNames[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected Object internallyGetValue(String colName, int row) throws IOException {
        int col = getColumnIndex(colName);

        if (col < 0) {
            throw new IllegalArgumentException("Column not found");
        }

        Object value = internallyGetValue(col, row);
        return value;
    }

    /**
     * Get the max column of a row
     * 
     * @param rowIndex the row index
     * @return the max column of a row, or -1 if the row index is invalid
     * @throws IOException
     */
    @Override
    public int getMaxColumn(int rowIndex) {
        Row curRow = sheet.getRow(rowIndex);
        if (curRow != null) {
            return curRow.getLastCellNum();
        }
        return -1;
    }

    /**
     * Get all column names of the test data
     * 
     * @return an array that contains names of all columns
     * @throws IOException if any io errors happened
     */
    @Override
    public String[] getColumnNames() throws IOException {
        if (ArrayUtils.isEmpty(columnNames)) {
            int maxColumnCounts = getColumnCount();

            if (maxColumnCounts < 0) {
                return new String[0];
            }

            columnNames = new String[maxColumnCounts];
            if (hasHeaders) {
                for (int i = 0; i < maxColumnCounts; i++) {
                    columnNames[i] = TestDataCellDecorator.decorateExcelCellAsString(workbook, getCellAt(i, COLUMN_HEADER_ROW_NUMBER));
                }
            }
        }
        return columnNames;
    }

    private int getColumnCount() throws IOException {
        if (columnCount < 0) {
            for (int rowIndex = 0; rowIndex < getRowNumbers(); rowIndex++) {
                int maxColumnRow = getMaxColumn(rowIndex);
                if (maxColumnRow > columnCount) {
                    columnCount = maxColumnRow;
                }
            }
        }
        return Math.max(0, columnCount);
    }

    /**
     * Get all sheet names of the parent excel file
     * 
     * @return an array contains all the sheet name
     */
    @Override
    public String[] getSheetNames() {
        int numberOfSheets = workbook.getNumberOfSheets();
        String[] sheetNames = new String[numberOfSheets];
        for (int i = 0; i < numberOfSheets; i++) {
            sheetNames[i] = workbook.getSheetName(i);
        }

        return sheetNames;
    }

    /**
     * Change this excel data to use another sheet with the specify sheet name
     * 
     * @param sheetName the new sheet name
     */
    @Override
    public void changeSheet(String sheetName) {
        sheet = workbook.getSheet(sheetName);
        mergedRegionsList = null;
        columnNames = null;
        columnCount = -1;
    }

    /**
     * Get total rows of the test data
     * 
     * @return total rows of the test data
     */
    @Override
    public int getRowNumbers() throws IOException {
        int totalRows = getSheet().getLastRowNum() + 1;
        return totalRows - getHeaderRowIdx();
    }

    /**
     * Get total column of the test data
     * 
     * @return total columns of the test data
     * @throws IOException if any io errors happened
     */
    @Override
    public int getColumnNumbers() throws IOException {
        return getColumnCount();
    }
}
