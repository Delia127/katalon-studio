package com.kms.katalon.dal.fileservice.dataprovider;

import com.kms.katalon.dal.TestSuiteCollectionDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.exception.InvalidNameException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.dal.fileservice.manager.EntityFileServiceManager;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class TestRunFileServiceDataProvider implements TestSuiteCollectionDataProvider {

    private EntityService getEntityService() throws DALException {
        try {
            return EntityService.getInstance();
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public TestSuiteCollectionEntity get(String testSuiteCollectionId) throws DALException {
        try {
            return (TestSuiteCollectionEntity) getEntityService().getEntityByPath(testSuiteCollectionId);
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

            TestSuiteCollectionEntity newTestRun = new TestSuiteCollectionEntity();
            newTestRun.setParentFolder(parentFolder);
            newTestRun.setProject(parentFolder.getProject());
            newTestRun.setName(newName);

            getEntityService().saveEntity(newTestRun);

            return newTestRun;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public TestSuiteCollectionEntity rename(String testSuiteCollectionId, String newName) throws DALException {
        try {
            getEntityService().validateName(newName);

            TestSuiteCollectionEntity currentTestRun = get(testSuiteCollectionId);
            if (currentTestRun == null) {
                return null;
            }

            checkDuplicate(currentTestRun.getParentFolder(), newName);

            getEntityService().deleteEntity(currentTestRun);

            currentTestRun.setName(newName);

            getEntityService().saveEntity(currentTestRun);

            return currentTestRun;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public void update(TestSuiteCollectionEntity newTestRun) throws DALException {
        try {
            getEntityService().saveEntity(newTestRun);
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

        if (testSuiteCollection.getParentFolder().equals(location)) {
            return null;
        }

        try {
            return EntityFileServiceManager.copy(testSuiteCollection, location);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }
}
