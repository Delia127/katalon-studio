package com.kms.katalon.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.dal.TestSuiteCollectionDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class TestSuiteCollectionController extends EntityController {

    private static TestSuiteCollectionController instance;

    private TestSuiteCollectionController() {
        super();
    }

    public static TestSuiteCollectionController getInstance() {
        if (instance == null) {
            instance = new TestSuiteCollectionController();
        }
        return instance;
    }

    public TestSuiteCollectionEntity getTestSuiteCollection(String id) throws DALException {
        return getTestSuiteCollectionDataProvider().get(id);
    }
    
    public List<TestSuiteCollectionEntity> getAllTestSuiteCollectionsInProject(ProjectEntity project) throws DALException {
        return getTestSuiteCollectionDataProvider().getAll(project);
    }
    
    private TestSuiteCollectionDataProvider getTestSuiteCollectionDataProvider() {
        return getDataProviderSetting().getTestSuiteCollectionDataProvider();
    }

    public TestSuiteCollectionEntity getTestRunByDisplayId(String testSuiteCollectionDisplayId) throws DALException {
        if (testSuiteCollectionDisplayId == null) {
            return null;
        }
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject == null) {
            return null;
        }
        String projectDir = currentProject.getFolderLocation();
        String id = projectDir + File.separator
                + testSuiteCollectionDisplayId.replace(GlobalStringConstants.ENTITY_ID_SEPARATOR, File.separator)
                + TestSuiteCollectionEntity.FILE_EXTENSION;
        return getTestSuiteCollectionDataProvider().get(id);
    }

    public TestSuiteCollectionEntity newTestSuiteCollection(FolderEntity parentFolder, String newName)
            throws DALException {
        return getTestSuiteCollectionDataProvider().add(parentFolder, newName);
    }

    public void updateTestSuiteCollection(TestSuiteCollectionEntity testSuiteCollection) throws DALException {
        getTestSuiteCollectionDataProvider().update(testSuiteCollection);
    }

    public void renameTestSuiteCollection(String newName, TestSuiteCollectionEntity testSuiteCollection)
            throws DALException {
        getTestSuiteCollectionDataProvider().rename(testSuiteCollection.getId(), newName);
    }

    public void deleteTestSuiteCollection(TestSuiteCollectionEntity testSuiteCollection) throws DALException {
        getTestSuiteCollectionDataProvider().delete(testSuiteCollection.getId());
    }

    public TestSuiteCollectionEntity moveTestSuiteCollection(TestSuiteCollectionEntity testSuiteCollection,
            FolderEntity newLocation) throws DALException {
        return getTestSuiteCollectionDataProvider().move(testSuiteCollection.getId(), newLocation);
    }

    public TestSuiteCollectionEntity copyTestSuiteCollection(TestSuiteCollectionEntity testSuiteCollection,
            FolderEntity newLocation) throws DALException {
        return getTestSuiteCollectionDataProvider().copy(testSuiteCollection.getId(), newLocation);
    }
    
    public List<TestSuiteCollectionEntity> updateProfileNameInAllTestSuiteCollections(ProjectEntity project, String oldProfileName, String newProfileName) 
            throws DALException {
        List<TestSuiteCollectionEntity> testSuiteCollections = getAllTestSuiteCollectionsInProject(project);
        List<TestSuiteCollectionEntity> updated = new ArrayList<>();
        for (TestSuiteCollectionEntity testSuiteCollection : testSuiteCollections) {
            List<TestSuiteRunConfiguration> testSuiteRunConfigurations = testSuiteCollection
                    .getTestSuiteRunConfigurations();
            
            boolean profileFound = false;
            for (TestSuiteRunConfiguration testSuiteRunConfiguration : testSuiteRunConfigurations) {
                String profileName = testSuiteRunConfiguration.getConfiguration().getProfileName();
                if (profileName.equals(oldProfileName)) {
                    testSuiteRunConfiguration.getConfiguration().setProfileName(newProfileName);
                    profileFound = true;
                }
            }
            
            if (profileFound) {
                updateTestSuiteCollection(testSuiteCollection);
                updated.add(testSuiteCollection);
                
            }
        }
       return updated;
    }
}
