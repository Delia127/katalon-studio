package com.kms.katalon.entity.checkpoint;

import com.kms.katalon.entity.file.ClonableObject;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;

public class CheckpointSourceInfo extends ClonableObject {

    private static final long serialVersionUID = 6207338109094018009L;

    /** Checkpoint indicator. Whether data will be fetched from existing Test Data of new defined source */
    private boolean fromTestData;

    /** sourceUrl could be Test-Data-displayed-ID, DB Connection String or Excel, CSV file location */
    private String sourceUrl;

    /** DataFileDriverType.ExcelFile, DataFileDriverType.CSV, DataFileDriverType.DBData (unsupported internal data) */
    private DataFileDriverType sourceType;

    protected CheckpointSourceInfo() {
        // created empty constructor as JAXB required
    }

    /**
     * Checkpoint source info for Test Data
     * 
     * @param displayedTestDataId
     */
    public CheckpointSourceInfo(String displayedTestDataId) {
        this.fromTestData = true;
        this.sourceUrl = displayedTestDataId;
    }

    public boolean isFromTestData() {
        return fromTestData;
    }

    public void setFromTestData(boolean isFromTestData) {
        this.fromTestData = isFromTestData;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public DataFileDriverType getSourceType() {
        return sourceType;
    }

    public void setSourceType(DataFileDriverType sourceType) {
        this.sourceType = sourceType;
    }
}
