package com.kms.katalon.core.testdata.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.kms.katalon.core.constants.StringConstants;

public class AppPOI {

    private HSSFWorkbook xlsInstance  = null; // for XLS format  (Office 2003)
    private XSSFWorkbook xlsxInstance = null; // for XLSX format (Office 2007 or higher)

    private ArrayList<SheetPOI> sheets = new ArrayList<SheetPOI>();

    public ArrayList<SheetPOI> getSheets() {
        return sheets;
    }

    /**
     * Creates an APPOI instance by reading excel file with
     * <code> fullFilePath <code> absolute path.
     * 
     * @param fullFilePath
     *            absolute path of the excel file.
     * @throws IOException
     *             if system is unable to read the <code>File</code>
     */
    public AppPOI(String fullFilePath) throws IOException {
        InputStream is = null;
        try {
            File inputFile = new File(fullFilePath);
            if (inputFile != null && inputFile.exists()) {
                String fileExt = FilenameUtils.getExtension(fullFilePath);
                if (fileExt.toLowerCase().equals("xls")) {
                    is = new FileInputStream(inputFile);
                    POIFSFileSystem fs = new POIFSFileSystem(is);
                    xlsInstance = new HSSFWorkbook(fs);
                    loadSheets();
                } else if (fileExt.toLowerCase().equals("xlsx")) {
                    is = new FileInputStream(inputFile);
                    xlsxInstance = new XSSFWorkbook(is);
                    loadSheets();
                } else {
                    throw new IllegalArgumentException(MessageFormat.format(
                            StringConstants.UTIL_EXC_FILE_IS_UNSUPPORTED, fullFilePath));
                }
            } else {
                throw new FileNotFoundException(fullFilePath);
            }
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
}