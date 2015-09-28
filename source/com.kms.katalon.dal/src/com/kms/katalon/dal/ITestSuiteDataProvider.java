package com.kms.katalon.dal;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

/**
 * @author duyluong
 * 
 */
public interface ITestSuiteDataProvider {

	public TestSuiteEntity getTestSuite(String testSuiteValue) throws Exception;

	public void deleteTestSuite(TestSuiteEntity testSuite) throws Exception;

	public TestSuiteEntity updateTestSuite(TestSuiteEntity testSuite) throws Exception;

	public TestSuiteEntity copyTestSuite(TestSuiteEntity testSuite, FolderEntity destinationFolder) throws Exception;
	
	public TestSuiteEntity moveTestSuite(TestSuiteEntity testSuite, FolderEntity destinationFolder) throws Exception;

	public TestSuiteEntity addNewTestSuite(FolderEntity parentFolder, String testSuiteName, short timeOut)
			throws Exception;

	public String getIdForDisplay(TestSuiteEntity entity) throws Exception;
	
	public TestSuiteTestCaseLink getTestCaseLink(TestSuiteEntity testSuite, String testCaseId);
	
	public String getAvailableTestSuiteName(FolderEntity parentFolder, String name) throws Exception;
}
