package com.kms.katalon.core.testdata.reader;

import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class HFFPOI extends SheetPOI {
    private HSSFWorkbook lowerInstance = null; // represent .xls file
    private HSSFSheet lowerSheetInstance = null; // represent sheet instance of
                                                 // .xls file

    public HFFPOI(HSSFWorkbook xlsInstance, HSSFSheet xlsSheetInstance, String sheetName) {
        super(sheetName, xlsSheetInstance);
        this.lowerInstance = xlsInstance;
        this.lowerSheetInstance = xlsSheetInstance;
        if (xlsSheetInstance != null) xlsSheetInstance.setForceFormulaRecalculation(true);
    }

    @Override
    public String internallyGetCellText(int col, int row) {
        HSSFRow curRow = lowerSheetInstance.getRow(row);

        if (curRow == null) {
            return "";
        }

        HSSFCell curCell = curRow.getCell(col);

        if (curCell == null) {
            return "";
        }

        switch (curCell.getCellType()) {
            case Cell.CELL_TYPE_STRING: {
                return curCell.getRichStringCellValue().getString();
            }
            case Cell.CELL_TYPE_NUMERIC: {
                DataFormatter formatter = new DataFormatter(Locale.getDefault());

                return formatter.formatRawCellContents(curCell.getNumericCellValue(), -1,
                        getFormatString(curCell.getCellStyle().getDataFormatString()));
            }
            case Cell.CELL_TYPE_BOOLEAN: {
                return Boolean.toString(curCell.getBooleanCellValue());
            }
            case Cell.CELL_TYPE_FORMULA: {
                // try with String
                FormulaEvaluator formulaEval = null;
                try {
                    formulaEval = lowerInstance.getCreationHelper().createFormulaEvaluator();
                    CellValue cellVal = formulaEval.evaluate(curCell);

                    switch (cellVal.getCellType()) {
                        case Cell.CELL_TYPE_BLANK:
                            return "";
                        case Cell.CELL_TYPE_STRING:
                            return cellVal.getStringValue();
                        case Cell.CELL_TYPE_NUMERIC:
                            DataFormatter formatter = new DataFormatter(Locale.getDefault());

                            return formatter.formatRawCellContents(cellVal.getNumberValue(), -1,
                                    getFormatString(curCell.getCellStyle().getDataFormatString()));
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

                        return formatter.formatRawCellContents(curCell.getNumericCellValue(), -1,
                                getFormatString(curCell.getCellStyle().getDataFormatString()));
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

    @Override
    public String getCellText(String cellAddress) {
        CellReference cellRef = new CellReference(cellAddress);
        int row = cellRef.getRow();
        int col = cellRef.getCol();
        String text = getCellText(col, row);
        return text;
    }

    @Override
    public String[] getRangeText(String rangeAddress) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getMaxRow() {
        return lowerSheetInstance.getLastRowNum();
    }

    @Override
    public int getMaxColumn(int rowIndex) {
        HSSFRow curRow = lowerSheetInstance.getRow(rowIndex);
        if (curRow != null) {
            return curRow.getLastCellNum();
        }
        return -1;
    }
}
