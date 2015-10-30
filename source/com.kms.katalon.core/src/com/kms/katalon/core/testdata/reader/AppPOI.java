package com.kms.katalon.core.testdata.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.kms.katalon.core.constants.StringConstants;

public class AppPOI {
    private HSSFWorkbook xlsInstance = null; // for XLS format (Office 2003)
    private XSSFWorkbook xlsxInstance = null; // for XLSX format (Office 2007 or higher)

    /**
     * Creates an APPOI instance by reading excel file with <code> fullFilePath <code> absolute path.
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
                if ("xls".equalsIgnoreCase(fileExt)) {
                    is = new FileInputStream(inputFile);
                    POIFSFileSystem fs = new POIFSFileSystem(is);
                    xlsInstance = new HSSFWorkbook(fs);
                } else if ("xlsx".equalsIgnoreCase(fileExt)) {
                    is = new FileInputStream(inputFile);
                    xlsxInstance = new XSSFWorkbook(is);
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

    public SheetPOI getSheetPOI(String sheetName) {
        if (xlsInstance != null) {
            return new HFFPOI(xlsInstance, xlsInstance.getSheet(sheetName), sheetName);
        } else if (xlsxInstance != null) {
            return new XSSPOI(xlsxInstance, xlsxInstance.getSheet(sheetName), sheetName);
        }
        return null;
    }

    public String[] getSheetNames() {
        int numberOfSheets = 0;
        if (xlsInstance != null) {
            numberOfSheets = xlsInstance.getNumberOfSheets();
        } else if (xlsxInstance != null) {
            numberOfSheets = xlsxInstance.getNumberOfSheets();
        }
        
        String[] sheetNames = new String[numberOfSheets];
        if (xlsInstance != null) {
            for (int i = 0; i < numberOfSheets; i++) {
                sheetNames[i] = xlsInstance.getSheetName(i);
            }
        } else if (xlsxInstance != null) {
            for (int i = 0; i < numberOfSheets; i++) {
                sheetNames[i] = xlsxInstance.getSheetName(i);
            }
        }

        return sheetNames;
    }
}