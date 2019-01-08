package com.kms.katalon.composer.webservice.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import com.kms.katalon.composer.components.log.LoggerSingleton;

public class ExcelHelper {
	/*
	 * This method ASSUMES that the excel file has at least 2 columns (key - value)
	 */
	public static Map<String, String> readFrom(String filePath) {
		Map<String, String> map = new HashMap<>();
		try {
			FileInputStream file = new FileInputStream(new File(filePath));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				int i = 0;
				String key = "";
				String value = "";
				while (cellIterator.hasNext() && i < 2) {
					Cell cell = cellIterator.next();
					if (i == 0) {
						key = cell.getStringCellValue();
					} else {
						value = cell.getStringCellValue();
					}
					i++;
				}
				map.put(key, value);
			}
			file.close();
		} catch (Exception e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
					"Not an excel file or of expected format (two columns: key - value)");
			LoggerSingleton.logError(e);
		}
		return map;
	}
}
