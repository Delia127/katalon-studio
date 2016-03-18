package com.kms.katalon.core.testdata.reader;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.ArrayUtils;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.testdata.ExcelData;

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

    public String internallyGetValue(int col, int row) throws IOException {
        int maxRow = getRowNumbers();
        if (row > maxRow) {
            throw new IllegalArgumentException(MessageFormat.format(StringConstants.EXCEL_INVALID_ROW_NUMBER, row,
                    maxRow));
        }

        int maxColumnAtRow = getMaxColumn(row);

        if (maxColumnAtRow < 0) {
            return "";
        }

        if (col > maxColumnAtRow) {
            // throw new IllegalArgumentException(MessageFormat.format(StringConstants.EXCEL_INVALID_COL_NUMBER, col,
            // maxColumnAtRow));
            return "";
        }

        // check if cell index is in a merged region
        for (CellRangeAddress mergedRegion : getMergedRegions()) {
            // If the region does contain the cell index
            if (mergedRegion.isInRange(row, col)) {
                // Now, you need to get the cell from the top left hand corner of this
                return internallyGetCellText(mergedRegion.getFirstColumn(), mergedRegion.getFirstRow());
            }
        }

        return internallyGetCellText(col, row + getHeaderRowIdx());
    }

    private String internallyGetCellText(int col, int row) {
        Row curRow = sheet.getRow(row);

        if (curRow == null) {
            return "";
        }

        Cell curCell = curRow.getCell(col);

        if (curCell == null) {
            return "";
        }

        switch (curCell.getCellType()) {
        case Cell.CELL_TYPE_STRING: {
            return curCell.getRichStringCellValue().getString();
        }
        case Cell.CELL_TYPE_NUMERIC: {
            DataFormatter formatter = new DataFormatter(Locale.getDefault());

            return formatter.formatRawCellContents(curCell.getNumericCellValue(), -1, getFormatString(curCell
                    .getCellStyle().getDataFormatString()));
        }
        case Cell.CELL_TYPE_BOOLEAN: {
            return Boolean.toString(curCell.getBooleanCellValue());
        }
        case Cell.CELL_TYPE_FORMULA: {
            // try with String
            FormulaEvaluator formulaEval = null;
            try {
                formulaEval = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellVal = formulaEval.evaluate(curCell);

                switch (cellVal.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    return "";
                case Cell.CELL_TYPE_STRING:
                    return cellVal.getStringValue();
                case Cell.CELL_TYPE_NUMERIC:
                    DataFormatter formatter = new DataFormatter(Locale.getDefault());

                    return formatter.formatRawCellContents(cellVal.getNumberValue(), -1, getFormatString(curCell
                            .getCellStyle().getDataFormatString()));
                default:
                    return cellVal.formatAsString();
                }
            } catch (Exception ex) {
                // Try another way
            }

            // Try with number
            try {
                if (DateUtil.isCellDateFormatted(curCell)) {
                    String cellFormatString = curCell.getCellStyle().getDataFormatString();
                    return new CellDateFormatter(cellFormatString).simpleFormat(curCell.getDateCellValue());
                } else {
                    DataFormatter formatter = new DataFormatter(Locale.getDefault());

                    return formatter.formatRawCellContents(curCell.getNumericCellValue(), -1, getFormatString(curCell
                            .getCellStyle().getDataFormatString()));
                }
            } catch (Exception ex) {
                // Try another way
            }

            return curCell.getStringCellValue();
        }
        default:
            return curCell.getStringCellValue();
        }
    }

    public int getColumnIndex(String colName) throws IOException {
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

    public String internallyGetValue(String colName, int row) throws IOException {
        int col = getColumnIndex(colName);

        if (col < 0) {
            throw new IllegalArgumentException("Column not found");
        }

        String text = internallyGetValue(col, row);
        return text;
    }

    @Override
    public int getMaxColumn(int rowIndex) {
        Row curRow = sheet.getRow(rowIndex);
        if (curRow != null) {
            return curRow.getLastCellNum();
        }
        return -1;
    }

    public String[] getColumnNames() throws IOException {
        if (ArrayUtils.isEmpty(columnNames)) {
            int maxColumnCounts = getColumnCount();

            if (maxColumnCounts < 0) {
                return new String[0];
            }

            columnNames = new String[maxColumnCounts];
            if (hasHeaders) {
                for (int i = 0; i < maxColumnCounts; i++) {
                    columnNames[i] = internallyGetCellText(i, COLUMN_HEADER_ROW_NUMBER);
                }
            }
        }
        return columnNames;
    }

    public int getColumnCount() throws IOException {
        if (columnCount < 0) {
            for (int rowIndex = 0; rowIndex < getRowNumbers(); rowIndex++) {
                int maxColumnRow = getMaxColumn(rowIndex);
                if (maxColumnRow > columnCount) {
                    columnCount = maxColumnRow;
                }
            }
        }
        return columnCount;
    }

    protected String getFormatString(String rawFormatString) {
        if (rawFormatString == null || rawFormatString.isEmpty()) {
            return rawFormatString;
        }

        return rawFormatString.replace("_(*", "_(\"\"*");
    }

    @Override
    public String[] getSheetNames() {
        int numberOfSheets = workbook.getNumberOfSheets();
        String[] sheetNames = new String[numberOfSheets];
        for (int i = 0; i < numberOfSheets; i++) {
            sheetNames[i] = workbook.getSheetName(i);
        }

        return sheetNames;
    }

    @Override
    public void changeSheet(String sheetName) {
        sheet = workbook.getSheet(sheetName);
        mergedRegionsList = null;
        columnNames = null;
    }

    @Override
    public int getRowNumbers() throws IOException {
        int totalRows = getSheet().getLastRowNum() + 1;
        return totalRows - getHeaderRowIdx();
    }

    @Override
    public int getColumnNumbers() throws IOException {
        return getColumnCount();
    }
}