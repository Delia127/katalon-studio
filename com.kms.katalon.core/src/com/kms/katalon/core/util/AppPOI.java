package com.kms.katalon.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.kms.katalon.core.constants.StringConstants;

public class AppPOI {

	private HSSFWorkbook xls_instance = null;
	private XSSFWorkbook xlsx_instance = null;

	private ArrayList<SheetPOI> sheets = new ArrayList<SheetPOI>();

	public ArrayList<SheetPOI> getSheets() {
		return sheets;
	}

	public AppPOI(String fullFilePath) throws Exception {
		try {
			File input_file = new File(fullFilePath);
			if (input_file.exists()) {
				int dot = fullFilePath.lastIndexOf('.');
				String file_ext = fullFilePath.substring(dot + 1);
				if (file_ext.toLowerCase().equals("xls")) {
					InputStream myxls = new FileInputStream(input_file);
					POIFSFileSystem fs = new POIFSFileSystem(myxls);
					xls_instance = new HSSFWorkbook(fs);
					loadSheets();
					myxls.close();
				} else if (file_ext.toLowerCase().equals("xlsx")) {
					InputStream myxls = new FileInputStream(input_file);
					xlsx_instance = new XSSFWorkbook(myxls);
					loadSheets();
					myxls.close();
				} else {
					throw new Exception(MessageFormat.format(StringConstants.UTIL_EXC_FILE_IS_UNSUPPORTED, fullFilePath));
				}
			}
		} catch (Exception ex) {
			throw ex;
		}
	}

	private void loadSheets() {
		sheets.clear();
		if (xls_instance != null) {
			for (int i = 0; i < xls_instance.getNumberOfSheets(); i++)
				sheets.add(new SheetPOI(xls_instance, xls_instance.getSheetAt(i), xls_instance.getSheetName(i)));
		} else if (xlsx_instance != null) {
			for (int i = 0; i < xlsx_instance.getNumberOfSheets(); i++)
				sheets.add(new SheetPOI(xlsx_instance, xlsx_instance.getSheetAt(i), xlsx_instance.getSheetName(i)));
		}
	}

	public String getCellText(String cellAddress) {
		try {
			CellReference cellRef = new CellReference(cellAddress);
			String sheetName = cellRef.getSheetName();
			for (SheetPOI sheet : sheets) {
				if (sheet.get_Name().equals(sheetName)) {
					String text = sheet.getCellText(cellAddress);
					return text;
				}
			}
		} catch (Exception ex) {
			throw ex;
		}
		return null;
	}

	public String getCellText(CellReference cellRef) {
		try {
			String sheetName = cellRef.getSheetName();
			for (SheetPOI sheet : sheets) {
				if (sheet.get_Name().equals(sheetName)) {
					int row = cellRef.getRow();
					int col = cellRef.getCol();
					String text = sheet.getCellText(col, row);
					return text;
				}
			}
		} catch (Exception ex) {
			throw ex;
		}
		return null;
	}

	public ArrayList<String> getRangeText(String rangeAddress) {
		try {
			AreaReference aref = new AreaReference(rangeAddress);
			CellReference[] crefs = aref.getAllReferencedCells();
			ArrayList<String> values = new ArrayList<String>();

			for (int i = 0; i < crefs.length; i++) {
				CellReference cellRef = crefs[i];
				String text = getCellText(cellRef);
				values.add(text);
			}
			return values;
		} catch (Exception ex) {
			throw ex;
		}
	}
}