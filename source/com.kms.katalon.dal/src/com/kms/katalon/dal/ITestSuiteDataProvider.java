package com.kms.katalon.dal;

import java.io.File;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

/**
 * @author duyluong
 * 
 */
public interface ITestSuiteDataProvider {

    public TestSuiteEntity getTestSuite(String testSuiteValue) throws Exception;

    public void deleteTestSuite(TestSuiteEntity testSuite) throws Exception;
    
    public TestSuiteEntity renameTestSuite(String newName, TestSuiteEntity testSuite) throws DALException;

    public TestSuiteEntity updateTestSuite(TestSuiteEntity testSuite) throws Exception;

    public TestSuiteEntity copyTestSuite(TestSuiteEntity testSuite, FolderEntity destinationFolder) throws Exception;

    public TestSuiteEntity moveTestSuite(TestSuiteEntity testSuite, FolderEntity destinationFolder) throws Exception;

    public TestSuiteEntity saveNewTestSuite(TestSuiteEntity newTestSuite) throws Exception;

    public String getIdForDisplay(TestSuiteEntity entity) throws Exception;
    
    public String getTestSuiteCollectionIdForDisplay(TestSuiteCollectionEntity entity) throws Exception;

    public TestSuiteTestCaseLink getTestCaseLink(TestSuiteEntity testSuite, String testCaseId);

    public String getAvailableTestSuiteName(FolderEntity parentFolder, String name) throws Exception;
    
    public File getTestSuiteScriptFile(TestSuiteEntity testSuite) throws DALException;
    
    public File newTestSuiteScriptFile(TestSuiteEntity testSuite) throws DALException;
}
