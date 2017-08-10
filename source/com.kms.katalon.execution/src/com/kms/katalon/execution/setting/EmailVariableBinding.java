package com.kms.katalon.execution.setting;

import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.logging.model.TestSuiteLogRecord;

public class EmailVariableBinding {
    private final TestSuiteLogRecord testSuiteLogRecord;

    public EmailVariableBinding(TestSuiteLogRecord testSuiteLogRecord) {
        this.testSuiteLogRecord = testSuiteLogRecord;
    }

    public Map<String, Object> getVariables() {
        Map<String, Object> binding = new HashMap<>();
        binding.put("hostName", testSuiteLogRecord.getHostName());
        binding.put("os", testSuiteLogRecord.getOs());
        binding.put("browser", testSuiteLogRecord.getBrowser());
        binding.put("suiteId", testSuiteLogRecord.getId());
        binding.put("suiteName", testSuiteLogRecord.getName());
        binding.put("deviceId", testSuiteLogRecord.getDeviceId());
        binding.put("deviceName", testSuiteLogRecord.getDeviceName());
        binding.put("totalPassed", testSuiteLogRecord.getTotalPassedTestCases());
        binding.put("totalFailed", testSuiteLogRecord.getTotalFailedTestCases());
        binding.put("totalError", testSuiteLogRecord.getTotalErrorTestCases());
        binding.put("totalTestCases", testSuiteLogRecord.getTotalTestCases());
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
