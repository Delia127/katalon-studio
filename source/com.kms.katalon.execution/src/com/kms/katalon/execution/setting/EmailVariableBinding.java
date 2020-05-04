package com.kms.katalon.execution.setting;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.logging.model.TestSuiteCollectionLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.util.internal.DateUtil;

public class EmailVariableBinding {

    public static Map<String, Object> getVariablesForTestSuiteEmail(TestSuiteLogRecord logRecord) {
        Map<String, Object> binding = new HashMap<>();
        binding.put("hostName", logRecord.getHostName());
        binding.put("os", logRecord.getOs());
        binding.put("browser", logRecord.getBrowser());
        binding.put("suiteId", logRecord.getId());
        binding.put("suiteName", logRecord.getName());
        binding.put("deviceId", logRecord.getDeviceId());
        binding.put("deviceName", logRecord.getDeviceName());
        binding.put("totalPassed", logRecord.getTotalPassedTestCases());
        binding.put("totalFailed", logRecord.getTotalFailedTestCases());
        binding.put("totalError", logRecord.getTotalErrorTestCases());
        binding.put("totalTestCases", logRecord.getTotalTestCases());
        return binding;
    }

    public static Map<String, Object> getVariablesForTestSuiteCollectionEmail(TestSuiteCollectionLogRecord logRecord) {
        List<TestSuiteLogRecord> testSuiteRecords = logRecord.getTestSuiteRecords();
        if (testSuiteRecords == null || testSuiteRecords.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> binding = new HashMap<>();
        
        TestSuiteLogRecord childRecord =  testSuiteRecords.get(0);
        if (testSuiteRecords != null && testSuiteRecords.size() > 0) {
            childRecord = testSuiteRecords.get(0);
        }
        binding.put("hostName", childRecord != null ? childRecord.getHostName() : StringUtils.EMPTY);
        binding.put("os", childRecord != null ? childRecord.getOs() : StringUtils.EMPTY);

        long startTime = logRecord.getStartTime();
        long endTime = logRecord.getEndTime();
        binding.put("startTime", DateUtil.getDateTimeFormatted(startTime));
        binding.put("duration", DateUtil.getElapsedTime(startTime, endTime));

        binding.put("suiteCollectionName", logRecord.getTestSuiteCollectionId());
        binding.put("totalPassed", logRecord.getTotalPassedTestCases());
        binding.put("totalFailed", logRecord.getTotalFailedTestCases());
        binding.put("totalError", logRecord.getTotalErrorTestCases());
        binding.put("totalTestCases", logRecord.getTotalTestCases());
        return binding;
    }

    public static Map<String, Object> getTestEmailVariables() {
        Map<String, Object> binding = new HashMap<>();
        binding.put("hostName", "localhost");
        binding.put("os", "Windows 7");
        binding.put("browser", "Chrome 59.0");
        binding.put("suiteId", "Test Suites/Sample Suite");
        binding.put("suiteName", "Sample Suite");
        binding.put("deviceName", "My device");
        binding.put("deviceId", "------");
        binding.put("totalPassed", 1);
        binding.put("totalFailed", 0);
        binding.put("totalError", 0);
        binding.put("totalTestCases", 1);
        return binding;
    }
}
