package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public interface TestSuiteCollectionDataProvider {
    TestSuiteCollectionEntity get(String id) throws DALException;

    void delete(String id) throws DALException;

    TestSuiteCollectionEntity add(FolderEntity parentFolder, String newName) throws DALException;

    TestSuiteCollectionEntity rename(String id, String newName) throws DALException;

    void update(TestSuiteCollectionEntity newTestRun) throws DALException;

    TestSuiteCollectionEntity move(String id, FolderEntity newLocation) throws DALException;

    TestSuiteCollectionEntity copy(String id, FolderEntity location) throws DALException;

    void updateTestSuiteCollectionReferences(TestSuiteEntity testSuite, ProjectEntity project) throws DALException;

    List<TestSuiteCollectionEntity> getTestSuiteCollectionReferences(TestSuiteEntity testSuite, ProjectEntity project)
            throws DALException;

    void removeTestSuiteCollectionReferences(TestSuiteEntity testSuite,
            List<TestSuiteCollectionEntity> testSuiteReferences) throws DALException;
}
