package com.kms.katalon.core.testdata.reader;

import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XSSPOI extends SheetPOI {
    private XSSFWorkbook workbookInstance = null; // represent .xlsx file
    private XSSFSheet sheetInstance = null; // represent sheet instance of
                                            // .xlsx file

    public XSSPOI(XSSFWorkbook workbookInstance, XSSFSheet sheetInstance, String sheetName) {
        super(sheetName);
        setWorkbookInstance(workbookInstance);
        setSheetInstance(sheetInstance);
    }

    public void setWorkbookInstance(XSSFWorkbook workbookInstance) {
        this.workbookInstance = workbookInstance;
    }

    public void setSheetInstance(XSSFSheet sheetInstance) {
        this.sheetInstance = sheetInstance;
    }

    @Override
    public String internallyGetCellText(int col, int row) {
        XSSFRow curRow = sheetInstance.getRow(row);

        if (curRow == null) {
            return "";
        }

        XSSFCell curCell = curRow.getCell(col);
        if (curCell == null) {
            return "";
        }

        switch (curCell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return curCell.getRichStringCellValue().getString();
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(curCell)) {
                    return curCell.getDateCellValue().toString();
                } else {
                    double cel_value = curCell.getNumericCellValue();
                    if (cel_value == (long) cel_value)
                        return Integer.toString((int) cel_value);
                    else
                        return Double.toString(curCell.getNumericCellValue());
                }
            case Cell.CELL_TYPE_BOOLEAN:
                return Boolean.toString(curCell.getBooleanCellValue());
            case Cell.CELL_TYPE_FORMULA: {
                // try with String
                try {
                    FormulaEvaluator formulaEval = workbookInstance.getCreationHelper().createFormulaEvaluator();
                    CellValue cellVal = formulaEval.evaluate(curCell);
                    switch (cellVal.getCellType()) {
                        case Cell.CELL_TYPE_BLANK:
                            return "";
                        case Cell.CELL_TYPE_STRING:
                            return cellVal.getStringValue();
                        case Cell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(curCell)) {
                                String cellFormatString = curCell.getCellStyle().getDataFormatString();
                                return new CellDateFormatter(cellFormatString).simpleFormat(curCell.getDateCellValue());
                            } else {
                                double cel_value = cellVal.getNumberValue();
                                if (cel_value == (long) cel_value)
                                    return Integer.toString((int) cel_value);
                                else
                                    return Double.toString(curCell.getNumericCellValue());
                            }
                        default:
                            return cellVal.formatAsString();
                    }
                } catch (Exception e) {
                }
                // try with number
                try {
                    if (DateUtil.isCellDateFormatted(curCell)) {
                        return curCell.getDateCellValue().toString();
                    } else {
                        double cel_value = curCell.getNumericCellValue();
                        if (cel_value == (long) cel_value)
                            return Integer.toString((int) cel_value);
                        else
                            return Double.toString(curCell.getNumericCellValue());
                    }
                } catch (Exception e1) {
                }
                // try with bool
                try {
                    return Boolean.toString(curCell.getBooleanCellValue());
                } catch (Exception e) {
                }

                return curCell.getCellFormula();
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
        return sheetInstance.getLastRowNum();
    }

    @Override
    public int getMaxColumn(int rowIndex) {
        XSSFRow curRow = sheetInstance.getRow(rowIndex);
        if (curRow != null) {
            return curRow.getLastCellNum();
        } else {
            return -1;
        }
    }
}
