package com.kms.katalon.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SheetPOI {
	private static final int COLUMN_HEADER_ROW_NUMBER = 0;
	private HSSFWorkbook xls_instance = null;
	private XSSFWorkbook xlsx_instance = null;
	private HSSFSheet xls_sheet_instance = null;
	private XSSFSheet xlsx_sheet_instance = null;
	private String sheet_name = null;

	public SheetPOI(HSSFWorkbook xls_instance, HSSFSheet xls_sheet_instance, String sheet_name) {
		this.xls_instance = xls_instance;
		this.xls_sheet_instance = xls_sheet_instance;
		this.sheet_name = sheet_name;
		if (xls_sheet_instance != null)
			xls_sheet_instance.setForceFormulaRecalculation(true);
	}

	public SheetPOI(XSSFWorkbook xlsx_instance, XSSFSheet xlsx_sheet_instance, String sheet_name) {
		this.xlsx_instance = xlsx_instance;
		this.xlsx_sheet_instance = xlsx_sheet_instance;
		this.sheet_name = sheet_name;
		if (xlsx_sheet_instance != null)
			xlsx_sheet_instance.setForceFormulaRecalculation(true);
	}

	public String get_Name() {
		return sheet_name;
	}

	public void rename(String new_name) {
		if (xls_instance != null) {
			xls_instance.setSheetName(xls_instance.getSheetIndex(sheet_name), new_name);
			sheet_name = new_name;
		} else if (xlsx_instance != null) {
			xlsx_instance.setSheetName(xlsx_instance.getSheetIndex(sheet_name), new_name);
			sheet_name = new_name;
		}
	}

	public void set_col_autosize(int colIndex) {
		if (xls_sheet_instance != null)
			xls_sheet_instance.autoSizeColumn(colIndex);
		else if (xlsx_sheet_instance != null)
			xlsx_sheet_instance.autoSizeColumn(colIndex);
	}

	public String get_col_name(int colIndex) {
		return CellReference.convertNumToColString(colIndex);
	}

	public int get_col_index(String colName) {
		return CellReference.convertColStringToIndex(colName);
	}

	public int getMaxColumn(int row_index) {
		if (xls_sheet_instance != null) {
			HSSFRow curRow = xls_sheet_instance.getRow(row_index);
			if (curRow != null)
				return curRow.getLastCellNum();
		} else if (xlsx_sheet_instance != null) {
			XSSFRow curRow = xlsx_sheet_instance.getRow(row_index);
			if (curRow != null)
				return curRow.getLastCellNum();
		}
		return -1;
	}

	public int get_max_row() {
		if (xls_sheet_instance != null)
			return xls_sheet_instance.getLastRowNum();
		else if (xlsx_sheet_instance != null)
			return xlsx_sheet_instance.getLastRowNum();
		return -1;
	}

	public void setCellText(int col, int row, String insertStr) {
		if (xls_sheet_instance != null) {
			HSSFCell cur_cell = xls_instance_get_cell_at(col, row);
			if (cur_cell != null) {
				cur_cell.setCellValue(insertStr);
			}
		} else if (xlsx_sheet_instance != null) {
			XSSFCell cur_cell = xlsx_instance_get_cell_at(col, row);
			if (cur_cell != null) {
				cur_cell.setCellValue(insertStr);
			}
		}
	}

	public void setCellComment(int col, int row, String insertStr) {
		if (xls_sheet_instance != null) {
			HSSFCell cur_cell = xls_instance_get_cell_at(col, row);
			if (cur_cell != null) {
				ClientAnchor anchor = xls_instance.getCreationHelper().createClientAnchor();
				anchor.setCol1(col);
				anchor.setCol2(col + 1);
				anchor.setRow1(row);
				anchor.setRow2(row + 1);
				anchor.setDx1(100);
				anchor.setDx2(100);
				anchor.setDy1(100);
				anchor.setDy2(100);
				Comment comment = xls_sheet_instance.createDrawingPatriarch().createCellComment(anchor);
				RichTextString str = xls_instance.getCreationHelper().createRichTextString(insertStr);
				str.clearFormatting();
				comment.setString(str);
				comment.setAuthor("KMS Auto");
				cur_cell.setCellComment(comment);
			}
		} else if (xlsx_sheet_instance != null) {
			XSSFCell cur_cell = xlsx_instance_get_cell_at(col, row);
			if (cur_cell != null) {
				ClientAnchor anchor = xlsx_instance.getCreationHelper().createClientAnchor();
				anchor.setCol1(col);
				anchor.setCol2(col + 1);
				anchor.setRow1(row);
				anchor.setRow2(row + 1);
				anchor.setDx1(100);
				anchor.setDx2(100);
				anchor.setDy1(100);
				anchor.setDy2(100);
				Comment comment = xlsx_sheet_instance.createDrawingPatriarch().createCellComment(anchor);
				RichTextString str = xlsx_instance.getCreationHelper().createRichTextString(insertStr);
				comment.setString(str);
				comment.setAuthor("KMS Auto");
				cur_cell.setCellComment(comment);
			}
		}
	}

	public void setCellInt(int col, int row, int numeric) {
		if (xls_sheet_instance != null) {
			HSSFCell cur_cell = xls_instance_get_cell_at(col, row);
			if (cur_cell != null) {
				cur_cell.setCellValue(numeric);
			}
		} else if (xlsx_sheet_instance != null) {
			XSSFCell cur_cell = xlsx_instance_get_cell_at(col, row);
			if (cur_cell != null) {
				cur_cell.setCellValue(numeric);
			}
		}
	}

	public void setCellMethod(int col, int row, String methodStr) {
		if (xls_sheet_instance != null) {
			HSSFCell cur_cell = xls_instance_get_cell_at(col, row);
			if (cur_cell != null) {
				cur_cell.setCellFormula(methodStr);
			}
		} else if (xlsx_sheet_instance != null) {
			XSSFCell cur_cell = xlsx_instance_get_cell_at(col, row);
			if (cur_cell != null) {
				cur_cell.setCellFormula(methodStr);
			}
		}
	}

	public void setCellFormat(int col, int row, Object styleObject) {
		if (xls_sheet_instance != null && xls_instance != null && styleObject != null
				&& styleObject instanceof HSSFCellStyle) {
			HSSFCell curCell = xls_instance_get_cell_at(col, row);
			curCell.setCellStyle((HSSFCellStyle) styleObject);
		} else if (xlsx_sheet_instance != null && xlsx_instance != null && styleObject != null
				&& styleObject instanceof XSSFCellStyle) {
			XSSFCell curCell = xlsx_instance_get_cell_at(col, row);
			curCell.setCellStyle((XSSFCellStyle) styleObject);
		}
	}

	public Object getCellFormat(int col, int row) {
		if (xls_sheet_instance != null && xls_instance != null) {
			HSSFCell curCell = xls_instance_get_cell_at(col, row);
			return curCell.getCellStyle();
		} else if (xlsx_sheet_instance != null && xlsx_instance != null) {
			XSSFCell curCell = xlsx_instance_get_cell_at(col, row);
			return curCell.getCellStyle();
		}
		return null;
	}

	public Double getCellNumber(int col, int row) {
		if (xls_sheet_instance != null) {
			if (row <= xls_sheet_instance.getLastRowNum()) {
				HSSFRow cur_row = xls_sheet_instance.getRow(row);
				if (cur_row != null && col <= cur_row.getLastCellNum()) {
					HSSFCell cur_cell = cur_row.getCell(col);
					if (cur_cell != null) {
						switch (cur_cell.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(cur_cell)) {
								return 0.0;
							} else {
								return cur_cell.getNumericCellValue();
							}
						case Cell.CELL_TYPE_FORMULA: {
							try {
								FormulaEvaluator formulaEval = xls_instance.getCreationHelper()
										.createFormulaEvaluator();
								CellValue cellVal = formulaEval.evaluate(cur_cell);
								switch (cellVal.getCellType()) {
								case Cell.CELL_TYPE_NUMERIC:
									if (DateUtil.isCellDateFormatted(cur_cell)) {
										return 0.0;
									} else {
										return cellVal.getNumberValue();
									}
								default:
									return 0.0;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								if (DateUtil.isCellDateFormatted(cur_cell)) {
									return 0.0;
								} else {
									return cur_cell.getNumericCellValue();
								}
							} catch (Exception e1) {
							}

							return 0.0;
						}
						default:
							return 0.0;
						}
					}
				}
			}
		} else if ((xlsx_sheet_instance != null) && (row <= xlsx_sheet_instance.getLastRowNum())) {

			XSSFRow cur_row = xlsx_sheet_instance.getRow(row);
			if (cur_row != null && col <= cur_row.getLastCellNum()) {
				XSSFCell cur_cell = cur_row.getCell(col);
				if (cur_cell != null) {
					switch (cur_cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cur_cell)) {
							return 0.0;
						} else {
							return cur_cell.getNumericCellValue();
						}
					case Cell.CELL_TYPE_FORMULA: {
						// try with number
						try {
							if (DateUtil.isCellDateFormatted(cur_cell)) {
								return 0.0;
							} else {
								return cur_cell.getNumericCellValue();
							}
						} catch (Exception e1) {
						}

						return 0.0;
					}
					default:
						return 0.0;
					}
				}

			}
		}
		return null;
	}

	public Date getCellDateValue(int col, int row) {
		if (xls_sheet_instance != null) {
			if (row <= xls_sheet_instance.getLastRowNum()) {
				HSSFRow cur_row = xls_sheet_instance.getRow(row);
				if (cur_row != null && col <= cur_row.getLastCellNum()) {
					HSSFCell cur_cell = cur_row.getCell(col);
					if (cur_cell != null) {
						switch (cur_cell.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(cur_cell)) {
								Date date_value = cur_cell.getDateCellValue();
								return date_value;
							}
						default:
							return null;
						}
					}
				}
			}
		} else if ((xlsx_sheet_instance != null) && (row <= xlsx_sheet_instance.getLastRowNum())) {

			XSSFRow cur_row = xlsx_sheet_instance.getRow(row);
			if (cur_row != null && col <= cur_row.getLastCellNum()) {
				XSSFCell cur_cell = cur_row.getCell(col);
				if (cur_cell != null) {
					switch (cur_cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cur_cell)) {
							Date date_value = cur_cell.getDateCellValue();
							return date_value;
						}
					default:
						return null;
					}
				}
			}

		}
		return null;
	}

	public String getCellText(int col, int row) {
		if (xls_sheet_instance != null) {
			if (row <= xls_sheet_instance.getLastRowNum()) {
				HSSFRow cur_row = xls_sheet_instance.getRow(row);
				if (cur_row != null && col <= cur_row.getLastCellNum()) {
					HSSFCell cur_cell = cur_row.getCell(col);
					if (cur_cell != null) {
						switch (cur_cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							return cur_cell.getRichStringCellValue().getString();
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(cur_cell)) {
								String cellFormatString = cur_cell.getCellStyle().getDataFormatString(xls_instance);
								CellDateFormatter dateFormater = new CellDateFormatter(cellFormatString);
								Date date_value = cur_cell.getDateCellValue();
								return dateFormater.simpleFormat(date_value);
							} else {
								double cel_value = cur_cell.getNumericCellValue();
								if (cel_value == (long) cel_value)
									return Integer.toString((int) cel_value);
								else
									return Double.toString(cur_cell.getNumericCellValue());
							}
						case Cell.CELL_TYPE_BOOLEAN:
							return Boolean.toString(cur_cell.getBooleanCellValue());
						case Cell.CELL_TYPE_FORMULA: {
							// try with String
							try {
								FormulaEvaluator formulaEval = xls_instance.getCreationHelper()
										.createFormulaEvaluator();
								CellValue cellVal = formulaEval.evaluate(cur_cell);
								switch (cellVal.getCellType()) {
								case Cell.CELL_TYPE_BLANK:
									return "";
								case Cell.CELL_TYPE_STRING:
									return cellVal.getStringValue();
								case Cell.CELL_TYPE_NUMERIC:
									if (DateUtil.isCellDateFormatted(cur_cell)) {
										String cellFormatString = cur_cell.getCellStyle().getDataFormatString();
										return new CellDateFormatter(cellFormatString).simpleFormat(cur_cell
												.getDateCellValue());
									} else {
										double cel_value = cellVal.getNumberValue();
										if (cel_value == (long) cel_value)
											return Integer.toString((int) cel_value);
										else
											return Double.toString(cur_cell.getNumericCellValue());
									}
								default:
									return cellVal.formatAsString();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							// try with number
							try {
								if (DateUtil.isCellDateFormatted(cur_cell)) {
									String cellFormatString = cur_cell.getCellStyle().getDataFormatString();
									return new CellDateFormatter(cellFormatString).simpleFormat(cur_cell
											.getDateCellValue());
								} else {
									double cel_value = cur_cell.getNumericCellValue();
									if (cel_value == (long) cel_value)
										return Integer.toString((int) cel_value);
									else
										return Double.toString(cur_cell.getNumericCellValue());
								}
							} catch (Exception e1) {
							}
							// try with bool
							try {
								return Boolean.toString(cur_cell.getBooleanCellValue());
							} catch (Exception e) {
							}

							return cur_cell.getCellFormula();
						}
						default:
							return cur_cell.getStringCellValue();
						}
					}
				}
			}
		} else if ((xlsx_sheet_instance != null) && (row <= xlsx_sheet_instance.getLastRowNum())) {

			XSSFRow cur_row = xlsx_sheet_instance.getRow(row);
			if (cur_row != null && col <= cur_row.getLastCellNum()) {
				XSSFCell cur_cell = cur_row.getCell(col);
				if (cur_cell != null) {
					switch (cur_cell.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						return cur_cell.getRichStringCellValue().getString();
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cur_cell)) {
							return cur_cell.getDateCellValue().toString();
						} else {
							double cel_value = cur_cell.getNumericCellValue();
							if (cel_value == (long) cel_value)
								return Integer.toString((int) cel_value);
							else
								return Double.toString(cur_cell.getNumericCellValue());
						}
					case Cell.CELL_TYPE_BOOLEAN:
						return Boolean.toString(cur_cell.getBooleanCellValue());
					case Cell.CELL_TYPE_FORMULA: {
						// try with String
						try {
							FormulaEvaluator formulaEval = xlsx_instance.getCreationHelper().createFormulaEvaluator();
							CellValue cellVal = formulaEval.evaluate(cur_cell);
							switch (cellVal.getCellType()) {
							case Cell.CELL_TYPE_BLANK:
								return "";
							case Cell.CELL_TYPE_STRING:
								return cellVal.getStringValue();
							case Cell.CELL_TYPE_NUMERIC:
								if (DateUtil.isCellDateFormatted(cur_cell)) {
									String cellFormatString = cur_cell.getCellStyle().getDataFormatString();
									return new CellDateFormatter(cellFormatString).simpleFormat(cur_cell
											.getDateCellValue());
								} else {
									double cel_value = cellVal.getNumberValue();
									if (cel_value == (long) cel_value)
										return Integer.toString((int) cel_value);
									else
										return Double.toString(cur_cell.getNumericCellValue());
								}
							default:
								return cellVal.formatAsString();
							}
						} catch (Exception e) {
						}
						// try with number
						try {
							if (DateUtil.isCellDateFormatted(cur_cell)) {
								return cur_cell.getDateCellValue().toString();
							} else {
								double cel_value = cur_cell.getNumericCellValue();
								if (cel_value == (long) cel_value)
									return Integer.toString((int) cel_value);
								else
									return Double.toString(cur_cell.getNumericCellValue());
							}
						} catch (Exception e1) {
						}
						// try with bool
						try {
							return Boolean.toString(cur_cell.getBooleanCellValue());
						} catch (Exception e) {
						}

						return cur_cell.getCellFormula();
					}
					default:
						return cur_cell.getStringCellValue();
					}
				}
			}

		}
		return null;
	}

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
		if (col < 0)
			return null;

		String text = getCellText(col, row);
		return text;
	}

	public String getCellText(String cellAddress) {
		CellReference cellRef = new CellReference(cellAddress);
		int row = cellRef.getRow();
		int col = cellRef.getCol();
		String text = getCellText(col, row);
		return text;
	}

	public ArrayList<String> getRangeText(String rangeAddress) {
		AreaReference aref = new AreaReference(rangeAddress);
		org.apache.poi.ss.util.CellReference[] crefs = aref.getAllReferencedCells();
		ArrayList<String> values = new ArrayList<String>();

		for (int i = 0; i < crefs.length; i++) {
			org.apache.poi.ss.util.CellReference cellRef = crefs[i];
			int row = cellRef.getRow();
			int col = cellRef.getCol();
			String text = getCellText(col, row);
			values.add(text);
		}

		return values;
	}

	public ArrayList<String> getColumnText(String colName) {
		int col = getColumnIndex(colName);
		int maxRow = get_max_row();
		ArrayList<String> values = new ArrayList<String>();

		for (int i = 1; i <= maxRow; i++) {
			String text = getCellText(col, i);
			values.add(text);
		}

		return values;
	}

	public ArrayList<String> getColumnNames() {
		ArrayList<String> cols = new ArrayList<String>();

		for (int i = 0; i < getMaxColumn(COLUMN_HEADER_ROW_NUMBER); i++) {
			String col = getCellText(i, COLUMN_HEADER_ROW_NUMBER);
			cols.add(col);
		}
		return cols;
	}

	public int getMergeCells_RowCount(int col, int row) {
		if (xls_sheet_instance != null) {
			for (int i = 0; i <= xls_sheet_instance.getNumMergedRegions(); i++) {
				CellRangeAddress mRegion = xls_sheet_instance.getMergedRegion(i);
				if (mRegion != null && col >= mRegion.getFirstColumn() && col <= mRegion.getLastColumn()
						&& row >= mRegion.getFirstRow() && row <= mRegion.getLastRow())
					return mRegion.getLastRow() - mRegion.getFirstRow() + 1;
			}
			return 1;
		} else if (xlsx_sheet_instance != null) {
			for (int i = 0; i <= xlsx_sheet_instance.getNumMergedRegions(); i++) {
				CellRangeAddress mRegion = xlsx_sheet_instance.getMergedRegion(i);
				if (mRegion != null && col >= mRegion.getFirstColumn() && col <= mRegion.getLastColumn()
						&& row >= mRegion.getFirstRow() && row <= mRegion.getLastRow())
					return mRegion.getLastRow() - mRegion.getFirstRow() + 1;
			}
			return 1;
		}
		return 0;
	}

	public boolean copyColumn(int sourceCol, int targetCol) {
		if (xls_sheet_instance != null) {
			for (int iRow = 0; iRow <= xls_sheet_instance.getLastRowNum(); iRow++) {
				HSSFCell old_cell = xls_instance_get_cell_at(sourceCol, iRow);
				HSSFCell new_cell = xls_instance_get_cell_at(targetCol, iRow);

				// Copy format
				new_cell.setCellStyle(old_cell.getCellStyle());

				// Copy content
				switch (old_cell.getCellType()) {
				case HSSFCell.CELL_TYPE_STRING:
					new_cell.setCellValue(old_cell.getStringCellValue());
					break;
				case HSSFCell.CELL_TYPE_NUMERIC:
					new_cell.setCellValue(old_cell.getNumericCellValue());
					break;
				case HSSFCell.CELL_TYPE_BLANK:
					new_cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
					break;
				case HSSFCell.CELL_TYPE_BOOLEAN:
					new_cell.setCellValue(old_cell.getBooleanCellValue());
					break;
				case HSSFCell.CELL_TYPE_ERROR:
					new_cell.setCellErrorValue(old_cell.getErrorCellValue());
					break;
				case HSSFCell.CELL_TYPE_FORMULA:
					new_cell.setCellFormula(old_cell.getCellFormula());
					break;
				default:
					break;
				}
			}
			return true;
		} else if (xlsx_instance != null) {
			for (int iRow = 0; iRow < xlsx_sheet_instance.getLastRowNum(); iRow++) {
				XSSFCell old_cell = xlsx_instance_get_cell_at(sourceCol, iRow);
				XSSFCell new_cell = xlsx_instance_get_cell_at(targetCol, iRow);

				// Copy format
				new_cell.setCellStyle(old_cell.getCellStyle());

				// Copy content
				switch (old_cell.getCellType()) {
				case HSSFCell.CELL_TYPE_STRING:
					new_cell.setCellValue(old_cell.getStringCellValue());
					break;
				case HSSFCell.CELL_TYPE_NUMERIC:
					new_cell.setCellValue(old_cell.getNumericCellValue());
					break;
				case HSSFCell.CELL_TYPE_BLANK:
					new_cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
					break;
				case HSSFCell.CELL_TYPE_BOOLEAN:
					new_cell.setCellValue(old_cell.getBooleanCellValue());
					break;
				case HSSFCell.CELL_TYPE_ERROR:
					new_cell.setCellErrorValue(old_cell.getErrorCellValue());
					break;
				case HSSFCell.CELL_TYPE_FORMULA:
					new_cell.setCellFormula(old_cell.getCellFormula());
					break;
				default:
					break;
				}
			}
			return true;
		}

		return false;
	}

	public void clearColumnContent(int colIndex) {
		if (xls_sheet_instance != null) {
			for (int iRow = 0; iRow <= xls_sheet_instance.getLastRowNum(); iRow++) {
				HSSFCell old_cell = xls_instance_get_cell_at(colIndex, iRow);
				old_cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
			}
		} else if (xlsx_instance != null) {
			for (int iRow = 0; iRow < xlsx_sheet_instance.getLastRowNum(); iRow++) {
				XSSFCell old_cell = xlsx_instance_get_cell_at(colIndex, iRow);
				old_cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
			}
		}
	}

	private HSSFCell xls_instance_get_cell_at(int col, int row) {
		if (xls_sheet_instance != null) {
			// Add new Row if need
			if (row > xls_sheet_instance.getLastRowNum())
				xls_sheet_instance.createRow(row);
			HSSFRow cur_row = xls_sheet_instance.getRow(row);
			if (cur_row == null) {
				xls_sheet_instance.createRow(row);
				cur_row = xls_sheet_instance.getRow(row);
			}
			if (cur_row != null) {
				if (col > cur_row.getLastCellNum())
					cur_row.createCell(col);
				HSSFCell res_cell = cur_row.getCell(col);
				if (res_cell == null) {
					cur_row.createCell(col);
					res_cell = cur_row.getCell(col);
				}
				return cur_row.getCell(col);
			}
		}
		return null;
	}

	private XSSFCell xlsx_instance_get_cell_at(int col, int row) {
		if (xlsx_sheet_instance != null) {
			// Add new Row if need
			if (row > xlsx_sheet_instance.getLastRowNum())
				xlsx_sheet_instance.createRow(row);
			XSSFRow cur_row = xlsx_sheet_instance.getRow(row);
			if (cur_row == null) {
				xls_sheet_instance.createRow(row);
				cur_row = xlsx_sheet_instance.getRow(row);
			}
			if (cur_row != null) {
				if (col > cur_row.getLastCellNum())
					cur_row.createCell(col);
				XSSFCell res_cell = cur_row.getCell(col);
				if (res_cell == null) {
					cur_row.createCell(col);
					res_cell = cur_row.getCell(col);
				}
				return cur_row.getCell(col);
			}
		}
		return null;
	}

	public int getLastRowNotNullInColumn(int colIdx) {
		if (xls_sheet_instance != null)
			return getLastRowColumnInSheet(xls_sheet_instance, colIdx);
		else if (xlsx_sheet_instance != null)
			return getLastRowColumnInSheet(xlsx_sheet_instance, colIdx);
		return -1;
	}

	private int getLastRowColumnInSheet(Sheet sheet, int colIdx) {
		int numRow = 0;
		if (sheet != null) {
			Iterator<Row> rowIter = sheet.rowIterator();
			while (rowIter.hasNext()) {
				Row row = (Row) rowIter.next();

				Iterator<Cell> cellIter = row.cellIterator();
				while (cellIter.hasNext()) {
					Cell cell = (Cell) cellIter.next();
					if (cell.getColumnIndex() == colIdx) {
						numRow++;
					}
				}
			}
		}
		return numRow;
	}
}