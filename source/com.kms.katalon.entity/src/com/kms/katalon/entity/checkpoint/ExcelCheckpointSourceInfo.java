package com.kms.katalon.entity.checkpoint;

import com.kms.katalon.entity.constants.StringConstants;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;

/**
 * Checkpoint Excel source info
 */
public class ExcelCheckpointSourceInfo extends CheckpointSourceInfo {

    private static final long serialVersionUID = -7156970395932671391L;

    /** Excel sheet name or CSV separator */
    private String sheetNameOrSeparator;

    /** Whether Excel or CSV file location is relative or not */
    private boolean usingRelativePath;

    /** Excel and CSV header indicator */
    private boolean usingFirstRowAsHeader;

    public ExcelCheckpointSourceInfo() {
        this(StringConstants.EMPTY, null, false, true);
    }

    /**
     * Checkpoint Excel source info
     * 
     * @param sourceUrl File location
     * @param sheetName Excel sheet name or CSV separator
     * @param usingRelativePath is using relative path
     * @param usingFirstRowAsHeader is using first row as header
     */
    public ExcelCheckpointSourceInfo(String sourceUrl, String sheetName, boolean usingRelativePath,
            boolean usingFirstRowAsHeader) {
        setSourceUrl(sourceUrl);
        setSourceType(DataFileDriverType.ExcelFile);
        this.sheetNameOrSeparator = sheetName;
        this.usingRelativePath = usingRelativePath;
        this.usingFirstRowAsHeader = usingFirstRowAsHeader;
    }

    public String getSheetNameOrSeparator() {
        return sheetNameOrSeparator;
    }

    public void setSheetNameOrSeparator(String sheetNameOrSeparator) {
        this.sheetNameOrSeparator = sheetNameOrSeparator;
    }

    public boolean isUsingRelativePath() {
        return usingRelativePath;
    }

    public void setUsingRelativePath(boolean usingRelativePath) {
        this.usingRelativePath = usingRelativePath;
    }

    public boolean isUsingFirstRowAsHeader() {
        return usingFirstRowAsHeader;
    }

    public void setUsingFirstRowAsHeader(boolean usingFirstRowAsHeader) {
        this.usingFirstRowAsHeader = usingFirstRowAsHeader;
    }

}
