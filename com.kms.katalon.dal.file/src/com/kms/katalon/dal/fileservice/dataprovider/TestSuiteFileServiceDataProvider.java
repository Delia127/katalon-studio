package com.kms.katalon.dal.fileservice.dataprovider;

import com.kms.katalon.dal.ITestSuiteDataProvider;
import com.kms.katalon.dal.fileservice.manager.TestSuiteFileServiceManager;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuiteFileServiceDataProvider implements ITestSuiteDataProvider {

	@Override
	public TestSuiteEntity getTestSuite(String testSuitePk) throws Exception {
		return TestSuiteFileServiceManager.getTestSuite(testSuitePk);
	}

	@Override
	public void deleteTestSuite(TestSuiteEntity testSuite) throws Exception {
		TestSuiteFileServiceManager.deleteTestSuite(testSuite);
	}

	@Override
	public TestSuiteEntity updateTestSuite(TestSuiteEntity testSuite) throws Exception {
		return TestSuiteFileServiceManager.updateTestSuite(testSuite);
	}

	@Override
	public TestSuiteEntity copyTestSuite(TestSuiteEntity testSuite, FolderEntity destinationFolder) throws Exception {
		return TestSuiteFileServiceManager.copyTestSuite(testSuite, destinationFolder);	
	}

	@Override
	public TestSuiteEntity moveTestSuite(TestSuiteEntity testSuite, FolderEntity destinationFolder) throws Exception {
		return TestSuiteFileServiceManager.moveTestSuite(testSuite, destinationFolder);
	}

	@Override
	public TestSuiteEntity addNewTestSuite(FolderEntity parentFolder, String testSuiteName, short timeOut) throws Exception {
		return TestSuiteFileServiceManager.addNewTestSuite(parentFolder, testSuiteName, timeOut);
	}

    @Override
    public String getIdForDisplay(TestSuiteEntity entity) throws Exception {
        return entity.getRelativePathForUI();
    }

    @Override
    public TestSuiteTestCaseLink getTestCaseLink(TestSuiteEntity testSuite, String testCaseId) {
        for (TestSuiteTestCaseLink testCaseLink : testSuite.getTestSuiteTestCaseLinks()) {
            if (testCaseLink.getTestCaseId().equals(testCaseId)) {
                return testCaseLink;
            }
        }
        return null;
        
    }

	@Override
	public String getAvailableTestSuiteName(FolderEntity parentFolder, String name)
			throws Exception {
		return TestSuiteFileServiceManager.getAvailableTestSuiteName(parentFolder, name);
	}
}
