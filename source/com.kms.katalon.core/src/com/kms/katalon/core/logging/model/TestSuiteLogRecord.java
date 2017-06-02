package com.kms.katalon.core.logging.model;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;

public class TestSuiteLogRecord extends AbstractLogRecord {

    private String deviceName;

    private String devicePlatform;

    private String logFolder;

    private Map<String, String> runData;

    public TestSuiteLogRecord(String name, String logFolder) {
        super(name);
        this.logFolder = logFolder;
        runData = new HashMap<String, String>();
        setType(ILogRecord.LOG_TYPE_TEST_SUITE);
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
        long count = children.parallelStream()
                .filter(item -> (item instanceof TestCaseLogRecord) && (testStatusValue == null
                        || ((TestCaseLogRecord) item).getStatus().statusValue == testStatusValue))
                .count();
        return Math.toIntExact(count);
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
        return getChildren().indexOf(child);
    }

    public List<String> getLogFiles() {
        return Arrays.asList(new File(getLogFolder()).list())
                .stream()
                .filter(item -> FilenameUtils.getExtension(item).equals("log"))
                .collect(Collectors.toList());
    }

    @Override
    public String getSystemOutMsg() {
        return getJUnitMessage();
    }

    @Override
    public String getSystemErrorMsg() {
        TestStatus status = getStatus();
        String stackTrace = status.getStackTrace();
        if (status.getStatusValue().isError()) {
            return getJUnitMessage() + stackTrace;
        }
        return stackTrace;
    }
}
