package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;

public class ReportFolder {
	
    private List<String> reportFolders = new ArrayList<>();;
	
    private boolean runTestSuite = false;
	
    public ReportFolder() {
    }
	
    //For run test suite
    public ReportFolder(String path) {
        runTestSuite = true;
        reportFolders.add(path);
    }
	
    //For run test suite collection
    public ReportFolder(List<String> reports) {
        runTestSuite = false;
        reportFolders = reports;
    }
		
    public void setReportFolders(List<String> reportFolders) {
        this.reportFolders = reportFolders;
    }
	
    public List<String> getReportFolders() {
        return reportFolders;
    }
	
    public void setRunTestSuite(boolean runTestSuite) {
        this.runTestSuite = runTestSuite;
    }
	
    public boolean isRunTestSuite() {
        return runTestSuite;
    }
}
