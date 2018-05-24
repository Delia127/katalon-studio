package com.kms.katalon.dal.fileservice.dataprovider;

import java.util.List;

import com.kms.katalon.dal.ITestCaseDataProvider;
import com.kms.katalon.dal.fileservice.manager.TestCaseFileServiceManager;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestCaseFileServiceDataProvider implements ITestCaseDataProvider {

    @Override
    public TestCaseEntity saveNewTestCase(TestCaseEntity newTestCase) throws Exception {
        return TestCaseFileServiceManager.saveNewTestCase(newTestCase);
    }

    @Override
    public TestCaseEntity saveTempTestCase(TestCaseEntity tempTestCase) throws Exception {
        return TestCaseFileServiceManager.saveTempTestCase(tempTestCase);
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
    public TestCaseEntity copyTestCase(TestCaseEntity testCase, FolderEntity parentFolder) throws Exception {
        return TestCaseFileServiceManager.copyTestCase(testCase, parentFolder);
    }

    @Override
    public TestCaseEntity moveTestCase(TestCaseEntity testCase, FolderEntity parentFolder) throws Exception {
        return TestCaseFileServiceManager.moveTestCase(testCase, parentFolder);
    }

    @Override
    public void deleteTestCase(TestCaseEntity testCase) throws Exception {
        TestCaseFileServiceManager.deleteTestCase(testCase);
    }
    
    @Override
    public void deleteTempTestCase(TestCaseEntity testCase) {
        TestCaseFileServiceManager.deleteTempTestCase(testCase);
    }

    @Override
    public TestCaseEntity getTestCaseByDisplayId(String testCaseDisplayId) throws Exception {
        return TestCaseFileServiceManager.getTestCaseByDisplayId(testCaseDisplayId, DataProviderState.getInstance()
                .getCurrentProject());
    }

    @Override
    public String getAvailableTestCaseName(FolderEntity parentFolder, String name) throws Exception {
        return TestCaseFileServiceManager.getAvailableName(parentFolder, name);
    }

    @Override
    public TestCaseEntity getTestCaseByScriptFileName(String scriptFileName) throws Exception {
        return TestCaseFileServiceManager.getTestCaseByScriptFileName(scriptFileName, DataProviderState.getInstance()
                .getCurrentProject());
    }

    @Override
    public TestCaseEntity getTestCaseByScriptFilePath(String scriptFilePath) throws Exception {
        return TestCaseFileServiceManager.getTestCaseByScriptFilePath(scriptFilePath, DataProviderState.getInstance()
                .getCurrentProject());
    }

    @Override
    public List<TestSuiteEntity> getTestCaseReferences(TestCaseEntity testCase) throws Exception {
        return TestCaseFileServiceManager.getTestCaseReferences(testCase);
    }
}
