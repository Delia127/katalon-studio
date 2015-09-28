package com.kms.katalon.dal;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public interface ITestCaseDataProvider {
	public TestCaseEntity addNewTestCase(FolderEntity parentFolder, String testCaseName) throws Exception;

	public TestCaseEntity getTestCase(String testCaseValue) throws Exception;

	public TestCaseEntity updateTestCase(TestCaseEntity testCase) throws Exception;

	public TestCaseEntity copyTestCase(TestCaseEntity testCase, FolderEntity parentFolder) throws Exception;

	public TestCaseEntity moveTestCase(TestCaseEntity testCase, FolderEntity parentFolder) throws Exception;

	public void deleteTestCase(TestCaseEntity testCase) throws Exception;

	//public void uploadTestCaseToQTest(String token, TestCaseEntity testCase, ProjectEntity project) throws Exception;

	public String getIdForDisplay(TestCaseEntity testCase) throws Exception;

	public TestCaseEntity getTestCaseByDisplayId(String testCaseId) throws Exception;
	
	public String getAvailableTestCaseName(FolderEntity parentFolder, String name) throws Exception;
	
	public TestCaseEntity getTestCaseByScriptFileName(String scriptFile) throws Exception;
	
	public TestCaseEntity getTestCaseByScriptFilePath(String scriptFilePath) throws Exception;
}
