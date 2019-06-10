package com.kms.katalon.core.testdata.decorator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.kms.katalon.core.testdata.TestData;

public class TestDataCellDecorator {

    public static String decorateExcelCellAsString(Workbook workbook, Cell curCell) {

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
                    formulaEval = workbook.getCreationHelper().createFormulaEvaluator();
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

    public static Object decorateExcelCellAsIs(Workbook workbook, Cell curCell) {

        if (curCell == null) {
            return null;
        }

        switch (curCell.getCellType()) {
            case Cell.CELL_TYPE_STRING: {
                return curCell.getRichStringCellValue().getString();
            }
            case Cell.CELL_TYPE_NUMERIC: {
                return curCell.getNumericCellValue();
            }
            case Cell.CELL_TYPE_BOOLEAN: {
                return curCell.getBooleanCellValue();
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
                            return cellVal.getNumberValue();
                        default:
                            return cellVal.formatAsString();
                    }
                } catch (Exception ex) {
                    // Try another way
                }

                // Try with number
                try {
                    if (DateUtil.isCellDateFormatted(curCell)) {
                        return curCell.getDateCellValue();
                    } else {
                        return curCell.getNumericCellValue();
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

    private static String getFormatString(String rawFormatString) {
        if (rawFormatString == null || rawFormatString.isEmpty()) {
            return rawFormatString;
        }

        return rawFormatString.replace("_(*", "_(\"\"*");
    }

    /**
     * Decorate a cell in test data. For Test Data of type Excel, cells are decorated
     * as object types determined by POI library if and only if test data has property "readAsIs" equals true.
     * Otherwise cells are decorated as string
     * 
     * @param testData A {@link TestData}
     * @param cell An {@link Object} represents either a cell value or a cell (for Excel) to be decorated
     * @return A decorated {@link Object}
     */
    public static Object decorate(TestData testData, Object cell) {

        if (cell == null || testData == null) {
            return null;
        }

        Object rawValue = null;

        switch (testData.getType()) {
            case EXCEL_FILE:
                try {
                    File excelFile = new File(testData.getSourceUrl());
                    if (!excelFile.exists()) {
                        throw new FileNotFoundException(excelFile.toString());
                    }
                    FileInputStream fis = new FileInputStream(excelFile);
                    Workbook workbook = WorkbookFactory.create(fis);
                    // Ensure backward compatibility for old Excel test data
                    String readAsString = testData.getProperty("readAsString");
                    if (readAsString == null || (Boolean.valueOf(readAsString).booleanValue())) {
                        return decorateExcelCellAsString(workbook, (Cell) cell);
                    }
                    rawValue = decorateExcelCellAsIs(workbook, (Cell) cell);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                rawValue = cell;
        }
        return rawValue;
    }
}
