package com.kms.katalon.core.logging.model;

import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;


public class TestSuiteLogRecord extends AbstractLogRecord {
	private String browser;
	private String deviceName;
	private String devicePlatform;
	private String os;
	private String logFolder;
	private String hostName;
	private Map<String, String> runData;
	
	public TestSuiteLogRecord(String name, String logFolder) {
		super(name);
		this.logFolder = logFolder;
		runData = new HashMap<String, String>();
	}
	
	public String getBrowser() {
		return browser;
	}
	public void setBrowser(String browser) {
		this.browser = browser;
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
		if (os == null) os = "";
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

    public Map<String, String> getRunData() {
        return runData;
    }
    
    public void addRunData(String dataKey, String dataValue) {
        runData.put(dataKey, dataValue);
    }
    
    public void addRunDatas(Map<String, String> runData) {
        this.runData.putAll(runData);
    }
}
