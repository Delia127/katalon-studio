package com.kms.katalon.core.testdata.reader;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.kms.katalon.core.testdata.ExcelData;


public class WorbookProvider implements ExcelProvider {
    @Override
    public ExcelData getExcelData(String sourceUrl, boolean hasHeaders) throws IOException {
        try {
            Workbook workBook = WorkbookFactory.create(new File(sourceUrl));
            return new SheetPOI(sourceUrl, hasHeaders, workBook);
        } catch (InvalidFormatException e) {
            return null;
        }
    }
}