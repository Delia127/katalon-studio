package com.kms.katalon.composer.testdata.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.kms.katalon.core.util.AppPOI;
import com.kms.katalon.core.util.SheetPOI;

public class Util {

	public static void loadPreviewExcelData(String dataFilePath, int sheetIndex, List<String> headers, List<List<Object>> data) throws Exception {
		
		//Vector<String> headers = new Vector<String>();
		//Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		if(new File(dataFilePath).exists() == false){
			return;
		}
		
		String path = dataFilePath;

		File temp = new File(path);
		
		if (temp.exists()) {
			AppPOI file = new AppPOI(path);
			SheetPOI sheet = file.getSheets().get(sheetIndex);
			headers.clear();
			int rows = sheet.get_max_row();
			for (int col = 0; col < sheet.getMaxColumn(0); col++) {
				String colName = sheet.getCellText(col, 0);
				headers.add(colName);
			}
			for (int i = 1; i <= rows; i++) {
				List<Object> d = new ArrayList<Object>();
				for (int col = 0; col < sheet.getMaxColumn(0); col++) {
					String colValue = sheet.getCellText(col, i);
					d.add(colValue);
				}
				data.add(d);
			}
		} 
	}
	
	public static List<String> loadSheetName(String path){
		List<String> sheetNames = new ArrayList<String>();
		File file = new File(path);
		if (file.exists()) {
			
			InputStream iStream = null;
			Thread thread = null;
			ClassLoader loader = null;
			
			try{
				iStream = new FileInputStream(file);
				
				thread = Thread.currentThread();
				loader = thread.getContextClassLoader();
				thread.setContextClassLoader(Workbook.class.getClassLoader());
				
				Workbook workbook = WorkbookFactory.create(iStream);				
				for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
					String sheetName = workbook.getSheetName(i);
					sheetNames.add(sheetName);
				}
				iStream.close();
			}
			catch(Exception ex){
			}
			finally{
				try{ 
					if(iStream != null)
						iStream.close();
				}
				catch(Exception inEx){}
				if(thread != null && loader != null){
					thread.setContextClassLoader(loader);	
				}
			}
		}
		return sheetNames;
	}
	
}
