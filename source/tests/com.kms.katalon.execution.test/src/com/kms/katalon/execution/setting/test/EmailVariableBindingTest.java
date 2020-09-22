package com.kms.katalon.execution.setting.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.kms.katalon.core.logging.model.TestSuiteCollectionLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.util.internal.DateUtil;
import com.kms.katalon.execution.setting.EmailVariableBinding;

public class EmailVariableBindingTest {

	@Test
	public void getVariablesForTestSuiteEmailTest(){
		TestSuiteLogRecord logRecord = new TestSuiteLogRecord("test", "testFolder");
		Map<String, Object> bindings = EmailVariableBinding.getVariablesForTestSuiteEmail(logRecord);
		
		assertTrue(!bindings.isEmpty());
		assertEquals(bindings.get("suiteName"), "test");
		assertEquals(bindings.get("totalTestCases"), 0);
	}
	
	@Test
	public void getVariablesForTestSuiteCollectionEmailTest(){
		TestSuiteCollectionLogRecord logRecord = createTestLogRecord();
		Map<String, Object> bindings = EmailVariableBinding.getVariablesForTestSuiteCollectionEmail(logRecord);
		
		assertTrue(!bindings.isEmpty());
		assertEquals(bindings.get("suiteCollectionName"), "S1");
		assertEquals(bindings.get("totalTestCases"), "1");
		assertEquals(bindings.get("totalPassed"), "1");
		assertEquals(bindings.get("totalFailed"), "0");
		assertEquals(bindings.get("totalError"), "0");
		assertEquals(bindings.get("duration"),DateUtil.getElapsedTime(1000, 2000));
	}
	
	private TestSuiteCollectionLogRecord createTestLogRecord(){
        TestSuiteCollectionLogRecord logRecord = new TestSuiteCollectionLogRecord();
        logRecord.setTestSuiteCollectionId("S1");
        logRecord.setStartTime(1000);
        logRecord.setEndTime(2000);
        logRecord.setReportLocation("testLocation");
        logRecord.setTotalTestCases("1");
        logRecord.setTotalPassedTestCases("1");
        logRecord.setTotalFailedTestCases("0");
        logRecord.setTotalSkippedTestCases("0");
		logRecord.setTotalErrorTestCases("0");
		
		List<TestSuiteLogRecord> testSuiteRecords = new ArrayList<>();
		TestSuiteLogRecord test = new TestSuiteLogRecord("logRecord", "logFolder");
		testSuiteRecords.add(test);
		logRecord.setTestSuiteRecords(testSuiteRecords);
		return logRecord;
	}
}
