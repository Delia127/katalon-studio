package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import com.kms.katalon.dal.ITestSuiteDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
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
    public TestSuiteEntity saveNewTestSuite(TestSuiteEntity newTestSuite) throws Exception {
        return TestSuiteFileServiceManager.saveNewTestSuite(newTestSuite);
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
    public String getAvailableTestSuiteName(FolderEntity parentFolder, String name) throws Exception {
        return TestSuiteFileServiceManager.getAvailableTestSuiteName(parentFolder, name);
    }

    @Override
    public File getTestSuiteScriptFile(TestSuiteEntity testSuite) throws DALException {
        File scriptTestSuiteFolder = getTestSuiteScriptFolder(testSuite);
        if (scriptTestSuiteFolder.exists()) {
            File[] scripts = scriptTestSuiteFolder.listFiles(new FilenameFilter() {
                
                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir, name).isFile() && name.matches("Script\\d{13}\\.groovy");
                }
            });
            if (scripts != null && scripts.length > 0) {
                return scripts[0];
            }
        }
        return null;
    }

    private File getTestSuiteScriptFolder(TestSuiteEntity testSuite) {
        String scripTSRootLoc = FileServiceConstant.getTestScriptFolder(testSuite.getProject().getFolderLocation());
        return new File(scripTSRootLoc, testSuite.getIdForDisplay());
    }

    @Override
    public File newTestSuiteScriptFile(TestSuiteEntity testSuite) throws DALException {
        File scriptTestSuiteFolder = getTestSuiteScriptFolder(testSuite);
        scriptTestSuiteFolder.mkdirs();
        File script = new File(scriptTestSuiteFolder, "Script" + System.currentTimeMillis() + ".groovy");
        try {
            script.createNewFile();
        } catch (IOException e) {
            throw new DALException(e);
        }
        return script;
    }
}
