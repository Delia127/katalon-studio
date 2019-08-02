package com.kms.katalon.entity.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.file.FileEntity;

public class ReportCollectionEntity extends FileEntity {

    private static final long serialVersionUID = -9052925453132895425L;

    public static final String FILE_EXTENSION = ".rp";
    
    private String testSuiteCollectionId;
    
    private List<ReportItemDescription> reportItemDescriptions;
    
    private String displayName;

    @Override
    public String getLocation() {
        return parentFolder.getLocation() + File.separator + name + FILE_EXTENSION;
    }

    @Override
    public String getFileExtension() {
        return FILE_EXTENSION;
    }

    public List<ReportItemDescription> getReportItemDescriptions() {
        if (reportItemDescriptions == null) {
            reportItemDescriptions = new ArrayList<>();
        }
        return reportItemDescriptions;
    }

    public void setReportItemDescriptions(List<ReportItemDescription> reportItemDescriptions) {
        this.reportItemDescriptions = reportItemDescriptions;
    }

    public String getDisplayName() {
        if (displayName == null) {
            return getName();
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTestSuiteCollectionId() {
        return testSuiteCollectionId;
    }
    
    public void setTestSuiteCollectionId(String testSuiteCollectionId) {
        this.testSuiteCollectionId = testSuiteCollectionId;
    }
}
