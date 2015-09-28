package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;

import com.kms.katalon.dal.ITestCaseDataProvider;
import com.kms.katalon.dal.fileservice.manager.TestCaseFileServiceManager;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseFileServiceDataProvider implements ITestCaseDataProvider {
	@Override
	public TestCaseEntity addNewTestCase(FolderEntity parentFolder,
			String testCaseName) throws Exception {
		return TestCaseFileServiceManager.addNewTestCase(parentFolder, testCaseName);
	}

	@Override
	public TestCaseEntity getTestCase(String testCasePk) throws Exception {
		return TestCaseFileServiceManager.getTestCase(testCasePk);
	}

	// Merge is update
	@Override
	public TestCaseEntity updateTestCase(TestCaseEntity testCase) throws Exception {
		return TestCaseFileServiceManager.updateTestCase(testCase);
	}
	
	@Override
	public TestCaseEntity copyTestCase(TestCaseEntity testCase, FolderEntity parentFolder)
			throws Exception {
		return TestCaseFileServiceManager.copyTestCase(testCase, parentFolder);
	}
	
	@Override
	public TestCaseEntity moveTestCase(TestCaseEntity testCase, FolderEntity parentFolder)
			throws Exception {
		return TestCaseFileServiceManager.moveTestCase(testCase, parentFolder);
	}

	@Override
	public void deleteTestCase(TestCaseEntity testCase) throws Exception {
		TestCaseFileServiceManager.deleteTestCase(testCase);
	}
	
	@Override
	public String getIdForDisplay(TestCaseEntity entity) {
		return entity.getRelativePathForUI().replace(File.separator, "/");
	}

	@Override
	public TestCaseEntity getTestCaseByDisplayId(String testCaseDisplayId) throws Exception {
		ProjectEntity projectEntity = DataProviderState.getInstance().getCurrentProject();
		return TestCaseFileServiceManager.getTestCaseByDisplayId(testCaseDisplayId, projectEntity);
	}

	@Override
	public String getAvailableTestCaseName(FolderEntity parentFolder, String name)
			throws Exception {
		return TestCaseFileServiceManager.getAvailableName(parentFolder, name);
	}
	
	@Override
	public TestCaseEntity getTestCaseByScriptFileName(String scriptFileName) throws Exception {
		ProjectEntity projectEntity =  DataProviderState.getInstance().getCurrentProject();
		return TestCaseFileServiceManager.getTestCaseByScriptFileName(scriptFileName, projectEntity);
	}

	@Override
	public TestCaseEntity getTestCaseByScriptFilePath(String scriptFilePath) throws Exception {
		ProjectEntity projectEntity =  DataProviderState.getInstance().getCurrentProject();
		return TestCaseFileServiceManager.getTestCaseByScriptFilePath(scriptFilePath, projectEntity);
	}

}
