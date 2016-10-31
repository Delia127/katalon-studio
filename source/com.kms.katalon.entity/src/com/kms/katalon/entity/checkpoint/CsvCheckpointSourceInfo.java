package com.kms.katalon.entity.checkpoint;

import com.kms.katalon.entity.constants.StringConstants;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;

/**
 * Checkpoint CSV source info
 */
public class CsvCheckpointSourceInfo extends ExcelCheckpointSourceInfo {

    private static final long serialVersionUID = 6385215618023670146L;

    public CsvCheckpointSourceInfo() {
        // COMMA is CSVSeparator.COMMA.name() because of dependency issue
        this(StringConstants.EMPTY, "COMMA", false, true);
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

    @Override
    public CsvCheckpointSourceInfo clone() {
        return (CsvCheckpointSourceInfo) super.clone();
    }
}
