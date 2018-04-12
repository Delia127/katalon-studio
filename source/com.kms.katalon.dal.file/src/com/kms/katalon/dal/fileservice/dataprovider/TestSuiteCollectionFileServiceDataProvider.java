package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.dal.TestSuiteCollectionDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.exception.InvalidNameException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.dal.fileservice.manager.EntityFileServiceManager;
import com.kms.katalon.dal.fileservice.manager.FolderFileServiceManager;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity.ExecutionMode;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuiteCollectionFileServiceDataProvider implements TestSuiteCollectionDataProvider {

    private EntityService getEntityService() throws DALException {
        try {
            return EntityService.getInstance();
        } catch (Exception e) {
            throw new DALException(e);
        }
    }
    
    @Override
    public List<TestSuiteCollectionEntity> getAll(ProjectEntity project) throws DALException {
        try {
            List<TestSuiteCollectionEntity> allTestSuiteCollections = EntityFileServiceManager.getDescendants(
                    FolderFileServiceManager.getTestSuiteRoot(project), TestSuiteCollectionEntity.class);
            return allTestSuiteCollections;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public TestSuiteCollectionEntity get(String testSuiteCollectionId) throws DALException {
        try {
            TestSuiteCollectionEntity testSuiteCollection = (TestSuiteCollectionEntity) EntityFileServiceManager
                    .get(new File(testSuiteCollectionId));
            if (testSuiteCollection != null) {
                testSuiteCollection.setProject(DataProviderState.getInstance().getCurrentProject());
            }
            return testSuiteCollection;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public void delete(String testSuiteCollectionId) throws DALException {
        TestSuiteCollectionEntity testSuiteCollection = get(testSuiteCollectionId);
        if (testSuiteCollection == null) {
            return;
        }

        try {
            EntityService.getInstance().deleteEntity(testSuiteCollection);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public TestSuiteCollectionEntity add(FolderEntity parentFolder, String newName) throws DALException {
        try {
            getEntityService().validateName(newName);
            checkDuplicate(parentFolder, newName);

            TestSuiteCollectionEntity testSuiteCollection = new TestSuiteCollectionEntity();
            testSuiteCollection.setExecutionMode(ExecutionMode.SEQUENTIAL);
            testSuiteCollection.setParentFolder(parentFolder);
            testSuiteCollection.setProject(parentFolder.getProject());
            testSuiteCollection.setName(newName);

            getEntityService().saveEntity(testSuiteCollection);

            return testSuiteCollection;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public TestSuiteCollectionEntity rename(String testSuiteCollectionId, String newName) throws DALException {
        try {
            getEntityService().validateName(newName);

            TestSuiteCollectionEntity currentTestSuiteCollection = get(testSuiteCollectionId);
            if (currentTestSuiteCollection == null) {
                return null;
            }

            checkDuplicate(currentTestSuiteCollection.getParentFolder(), newName);

            getEntityService().deleteEntity(currentTestSuiteCollection);

            currentTestSuiteCollection.setName(newName);

            getEntityService().saveEntity(currentTestSuiteCollection);

            return currentTestSuiteCollection;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public void update(TestSuiteCollectionEntity newTestSuiteCollection) throws DALException {
        try {
            getEntityService().saveEntity(newTestSuiteCollection);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    private void checkDuplicate(FolderEntity parentFolder, String newName) throws DALException {
        if (!newName.equals(getEntityService().getAvailableName(parentFolder.getId(), newName, true))) {
            throw new InvalidNameException(StringConstants.DP_EXC_NAME_ALREADY_EXISTED);
        }
    }

    @Override
    public TestSuiteCollectionEntity move(String testSuiteCollectionId, FolderEntity newLocation) throws DALException {
        TestSuiteCollectionEntity testSuiteCollection = get(testSuiteCollectionId);

        if (testSuiteCollection.getParentFolder().equals(newLocation)) {
            return null;
        }

        try {
            return EntityFileServiceManager.move(testSuiteCollection, newLocation);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public TestSuiteCollectionEntity copy(String testSuiteCollectionId, FolderEntity location) throws DALException {
        TestSuiteCollectionEntity testSuiteCollection = get(testSuiteCollectionId);

        try {
            return EntityFileServiceManager.copy(testSuiteCollection, location);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public List<TestSuiteCollectionEntity> getTestSuiteCollectionReferences(TestSuiteEntity testSuite,
            ProjectEntity project) throws DALException {
        try {
            List<TestSuiteCollectionEntity> allTestSuiteCollections = EntityFileServiceManager.getDescendants(
                    FolderFileServiceManager.getTestSuiteRoot(project), TestSuiteCollectionEntity.class);
            List<TestSuiteCollectionEntity> testSuiteReferences = new ArrayList<>();
            for (TestSuiteCollectionEntity potential : allTestSuiteCollections) {
                if (potential.hasTestSuiteReferences(testSuite)) {
                    testSuiteReferences.add(potential);
                }
            }
            return testSuiteReferences;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public void removeTestSuiteCollectionReferences(TestSuiteEntity testSuite,
            List<TestSuiteCollectionEntity> testSuiteReferences) throws DALException {
        try {
            for (TestSuiteCollectionEntity referralCollection : testSuiteReferences) {
                if (!referralCollection.hasTestSuiteReferences(testSuite)) {
                    continue;
                }
                referralCollection.getTestSuiteRunConfigurations()
                        .removeAll(referralCollection.findRunConfigurations(testSuite));
                update(referralCollection);
            }
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public void updateTestSuiteCollectionReferences(TestSuiteEntity testSuite, ProjectEntity project)
            throws DALException {
        try {
            for (TestSuiteCollectionEntity referralCollection : getTestSuiteCollectionReferences(testSuite, project)) {
                getEntityService().saveEntity(referralCollection);
            }
        } catch (Exception e) {
            throw new DALException(e);
        }
    }
}
