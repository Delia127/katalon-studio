package com.kms.katalon.core.testdata.reader;

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

    private HSSFWorkbook xlsInstance = null;
    private XSSFWorkbook xlsxInstance = null;

    private ArrayList<SheetPOI> sheets = new ArrayList<SheetPOI>();

    public ArrayList<SheetPOI> getSheets() {
        return sheets;
    }

    public AppPOI(String fullFilePath) throws Exception {
        InputStream is = null;
        try {
            File inputFile = new File(fullFilePath);
            if (inputFile.exists()) {
                int dot = fullFilePath.lastIndexOf('.');
                String file_ext = fullFilePath.substring(dot + 1);
                if (file_ext.toLowerCase().equals("xls")) {
                    is = new FileInputStream(inputFile);
                    POIFSFileSystem fs = new POIFSFileSystem(is);
                    xlsInstance = new HSSFWorkbook(fs);
                    loadSheets();
                } else if (file_ext.toLowerCase().equals("xlsx")) {
                    is = new FileInputStream(inputFile);
                    xlsxInstance = new XSSFWorkbook(is);
                    loadSheets();
                } else {
                    throw new Exception(
                            MessageFormat.format(StringConstants.UTIL_EXC_FILE_IS_UNSUPPORTED, fullFilePath));
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private void loadSheets() {
        sheets.clear();
        if (xlsInstance != null) {
            for (int i = 0; i < xlsInstance.getNumberOfSheets(); i++)
                sheets.add(new HFFPOI(xlsInstance, xlsInstance.getSheetAt(i), xlsInstance.getSheetName(i)));
        } else if (xlsxInstance != null) {
            for (int i = 0; i < xlsxInstance.getNumberOfSheets(); i++)
                sheets.add(new XSSPOI(xlsxInstance, xlsxInstance.getSheetAt(i), xlsxInstance.getSheetName(i)));
        }
    }

    public String getCellText(String cellAddress) {
        try {
            CellReference cellRef = new CellReference(cellAddress);
            String sheetName = cellRef.getSheetName();
            for (SheetPOI sheet : sheets) {
                if (sheet.getSheetName().equals(sheetName)) {
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
                if (sheet.getSheetName().equals(sheetName)) {
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