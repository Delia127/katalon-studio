package com.kms.katalon.core.testdata;

import java.io.IOException;

import com.kms.katalon.core.testdata.reader.SpreadSheet;

public abstract class ExcelData extends AbstractTestData implements SpreadSheet {

    public ExcelData(String sourceUrl, boolean hasHeaders)  throws IOException {
        super(sourceUrl, hasHeaders);
    }

    public abstract int getMaxColumn(int rowIndex) throws IOException;
    
    @Override
    public final TestDataType getType() {
        return TestDataType.EXCEL_FILE;
    }
}
