package com.kms.katalon.core.logging.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;

public class TestSuiteLogRecord extends AbstractLogRecord {
    
    private String deviceName;

    private String devicePlatform;

    private String logFolder;

    private Map<String, String> runData;

    private List<XmlLogRecord> xmlLogRecords;

    public TestSuiteLogRecord(String name, String logFolder) {
        super(name);
        this.logFolder = logFolder;
        runData = new HashMap<String, String>();
    }

    public String getBrowser() {
        return (getRunData().containsKey("browser")) ? getRunData().get("browser") : "";
    }

    public String getLogFolder() {
        return logFolder;
    }

    public int getTotalTestCases() {
        return getTotalTestCasesWithTestStatusValue(null);
    }

    public int getTotalPassedTestCases() {
        return getTotalTestCasesWithTestStatusValue(TestStatusValue.PASSED);
    }

    public int getTotalFailedTestCases() {
        return getTotalTestCasesWithTestStatusValue(TestStatusValue.FAILED);
    }

    public int getTotalErrorTestCases() {
        return getTotalTestCasesWithTestStatusValue(TestStatusValue.ERROR);
    }

    public int getTotalIncompleteTestCases() {
        return getTotalTestCasesWithTestStatusValue(TestStatusValue.INCOMPLETE);
    }

    private int getTotalTestCasesWithTestStatusValue(TestStatusValue testStatusValue) {
        ILogRecord[] childLogRecords = getChildRecords();
        int total = 0;
        for (ILogRecord childLogRecord : childLogRecords) {
            if (childLogRecord instanceof TestCaseLogRecord) {
                TestCaseLogRecord testCaseLog = (TestCaseLogRecord) childLogRecord;
                if (testStatusValue == null || testCaseLog.getStatus().statusValue == testStatusValue) {
                    total++;
                }
            }
        }
        return total;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDevicePlatform() {
        return devicePlatform;
    }

    public void setDevicePlatform(String devicePlatform) {
        this.devicePlatform = devicePlatform;
    }

    public String getOs() {
        return (getRunData().containsKey(RunConfiguration.HOST_OS)) ? getRunData().get(RunConfiguration.HOST_OS) : "";
    }

    public String getHostName() {
        return (getRunData().containsKey(RunConfiguration.HOST_NAME)) ? getRunData().get(RunConfiguration.HOST_NAME)
                : "";
    }

    public String getAppVersion() {
        return (getRunData().containsKey(RunConfiguration.APP_VERSION)) ? getRunData().get(RunConfiguration.APP_VERSION)
                : "";
    }

    public Map<String, String> getRunData() {
        return runData;
    }

    public void addRunData(Map<String, String> runData) {
        this.runData.putAll(runData);
    }

    public <T extends ILogRecord> int getChildIndex(T child) {
        return Arrays.asList(getChildRecords()).indexOf(child);
    }

    public List<String> getLogFiles() {
        List<String> logFiles = new ArrayList<String>();
        for (String childFile : new File(getLogFolder()).list()) {
            if (!FilenameUtils.getExtension(childFile).equals("log")) {
                continue;
            }
            logFiles.add(childFile);
        }
        return logFiles;
    }
    
    
    public List<XmlLogRecord> getXmlLogRecords() {
        return xmlLogRecords;
    }

    public void addXmlLogRecords(List<XmlLogRecord> xmlLogRecords) {
        if(this.xmlLogRecords == null){
            this.xmlLogRecords = new ArrayList<XmlLogRecord>();
        }
        this.xmlLogRecords.addAll(xmlLogRecords);
    }
}
