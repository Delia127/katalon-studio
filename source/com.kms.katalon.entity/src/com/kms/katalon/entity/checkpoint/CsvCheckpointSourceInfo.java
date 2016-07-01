package com.kms.katalon.entity.checkpoint;

import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;

/**
 * Checkpoint CSV source info
 */
public class CsvCheckpointSourceInfo extends ExcelCheckpointSourceInfo {

    protected CsvCheckpointSourceInfo() {
        super();
    }

    /**
     * Checkpoint CSV source info
     * 
     * @param sourceUrl File location
     * @param separator CSV separator
     * @param usingRelativePath is using relative path
     * @param usingFirstRowAsHeader is using first row as header
     */
    public CsvCheckpointSourceInfo(String sourceUrl, String separator, boolean usingRelativePath,
            boolean usingFirstRowAsHeader) {
        super(sourceUrl, separator, usingRelativePath, usingFirstRowAsHeader);
        setSourceType(DataFileDriverType.CSV);
    }

}
