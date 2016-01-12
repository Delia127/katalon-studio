package com.kms.katalon.controller.util;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class EntityControllerUtil {
    public static FolderEntity moveFolder(FolderEntity folder, FolderEntity targetFolder) throws Exception {
        if (targetFolder != null && folder != null && folder.getFolderType() == targetFolder.getFolderType()) {

            // get collection of descendant entities that doesn't include descendant folder entity
            List<Object> allDescendantEntites = new ArrayList<Object>();
            for (Object descendantEntity : FolderController.getInstance().getAllDescentdantEntities(folder)) {
                if (!(descendantEntity instanceof FolderEntity)) {
                    allDescendantEntites.add(descendantEntity);
                }
            }

            List<String> lstDescendantEntityLocations = new ArrayList<String>();
            if (folder.getFolderType() == FolderType.TESTCASE) {
                for (Object child : allDescendantEntites) {
                    if (child != null && child instanceof TestCaseEntity) {
                        lstDescendantEntityLocations.add(((TestCaseEntity) child).getId());
                    }
                }
            } else if (folder.getFolderType() == FolderType.DATAFILE) {
                for (Object child : allDescendantEntites) {
                    if (child != null && child instanceof DataFileEntity) {
                        lstDescendantEntityLocations.add(((DataFileEntity) child).getId());
                    }
                }
            } else if (folder.getFolderType() == FolderType.TESTSUITE) {
                for (Object child : allDescendantEntites) {
                    if (child != null && child instanceof TestSuiteEntity) {
                        lstDescendantEntityLocations.add(((TestSuiteEntity) child).getId());
                    }
                }
            } else if (folder.getFolderType() == FolderType.WEBELEMENT) {
                for (Object child : allDescendantEntites) {
                    if (child != null && child instanceof WebElementEntity) {
                        lstDescendantEntityLocations.add(((WebElementEntity) child).getId());
                    }
                }
            }

            FolderEntity movedFolder = FolderController.getInstance().moveFolder(folder, targetFolder);
            // afterSave
            // send notification event
            if (folder.getFolderType() == FolderType.TESTCASE) {
                for (int i = 0; i < lstDescendantEntityLocations.size(); i++) {
                    EventBrokerSingleton
                            .getInstance()
                            .getEventBroker()
                            .post(EventConstants.TESTCASE_UPDATED,
                                    new Object[] { lstDescendantEntityLocations.get(i), allDescendantEntites.get(i) });
                }
            } else if (folder.getFolderType() == FolderType.DATAFILE) {
                for (int i = 0; i < lstDescendantEntityLocations.size(); i++) {
                    EventBrokerSingleton
                            .getInstance()
                            .getEventBroker()
                            .post(EventConstants.TEST_DATA_UPDATED,
                                    new Object[] { lstDescendantEntityLocations.get(i), allDescendantEntites.get(i) });
                }
            } else if (folder.getFolderType() == FolderType.TESTSUITE) {
                for (int i = 0; i < lstDescendantEntityLocations.size(); i++) {
                    EventBrokerSingleton
                            .getInstance()
                            .getEventBroker()
                            .post(EventConstants.TEST_SUITE_UPDATED,
                                    new Object[] { lstDescendantEntityLocations.get(i), allDescendantEntites.get(i) });
                }
            } else if (folder.getFolderType() == FolderType.WEBELEMENT) {
                for (int i = 0; i < lstDescendantEntityLocations.size(); i++) {
                    EventBrokerSingleton
                            .getInstance()
                            .getEventBroker()
                            .post(EventConstants.TEST_OBJECT_UPDATED,
                                    new Object[] { lstDescendantEntityLocations.get(i), allDescendantEntites.get(i) });
                }
            }

            if (movedFolder != null) {
                return movedFolder;
            }
        }
        return null;
    }

    public static TestCaseEntity moveTestCase(TestCaseEntity testCase, FolderEntity targetFolder) throws Exception {
        if (testCase != null) {
            TestCaseController testCaseController = TestCaseController.getInstance();
            String oldPk = testCase.getId();
            String oldIdForDisplay = testCase.getIdForDisplay();
            testCase = testCaseController.moveTestCase(testCase, targetFolder);
            String newPk = testCase.getId();
            if (!oldPk.equals(newPk)) {
                EventBrokerSingleton
                        .getInstance()
                        .getEventBroker()
                        .post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM,
                                new Object[] { oldIdForDisplay, testCase.getIdForDisplay() });
                EventBrokerSingleton.getInstance().getEventBroker()
                        .post(EventConstants.TESTCASE_UPDATED, new Object[] { oldPk, testCase });
                return testCase;
            }
        }
        return null;
    }

    public static TestSuiteEntity moveTestSuite(TestSuiteEntity testSuite, FolderEntity targetFolder) throws Exception {
        if (testSuite != null) {
            TestSuiteController testSuiteController = TestSuiteController.getInstance();
            String oldPk = testSuite.getId();
            String oldIdForDisplay = testSuite.getIdForDisplay();
            testSuite = testSuiteController.moveTestSuite(testSuite, targetFolder);
            String newPk = testSuite.getId();
            if (!oldPk.equals(newPk)) {
                EventBrokerSingleton
                        .getInstance()
                        .getEventBroker()
                        .post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM,
                                new Object[] { oldIdForDisplay, testSuite.getIdForDisplay() });
                EventBrokerSingleton.getInstance().getEventBroker()
                        .post(EventConstants.TEST_SUITE_UPDATED, new Object[] { oldPk, testSuite });
                return testSuite;
            }
        }
        return null;
    }

    public static DataFileEntity moveTestData(DataFileEntity dataFile, FolderEntity targetFolder) throws Exception {
        if (dataFile != null) {
            TestDataController testDataController = TestDataController.getInstance();
            String oldPk = dataFile.getId();
            String oldIdForDisplay = dataFile.getIdForDisplay();
            dataFile = testDataController.moveDataFile(dataFile, targetFolder);
            String newPk = dataFile.getId();
            if (!oldPk.equals(newPk)) {
                EventBrokerSingleton
                        .getInstance()
                        .getEventBroker()
                        .post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM,
                                new Object[] { oldIdForDisplay, dataFile.getIdForDisplay() });
                EventBrokerSingleton.getInstance().getEventBroker()
                        .post(EventConstants.TEST_DATA_UPDATED, new Object[] { oldPk, dataFile });
                return dataFile;
            }
        }
        return null;
    }

    public static WebElementEntity moveTestObject(WebElementEntity webElement, FolderEntity targetFolder)
            throws Exception {
        if (webElement != null) {
            ObjectRepositoryController objectRepositoryController = ObjectRepositoryController.getInstance();
            String oldPk = webElement.getId();
            String oldIdForDisplay = webElement.getIdForDisplay();
            webElement = objectRepositoryController.moveWebElement(webElement, targetFolder);
            String newPk = webElement.getId();
            if (!oldPk.equals(newPk)) {
                EventBrokerSingleton
                        .getInstance()
                        .getEventBroker()
                        .post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM,
                                new Object[] { oldIdForDisplay, webElement.getIdForDisplay() });
                EventBrokerSingleton.getInstance().getEventBroker()
                        .post(EventConstants.TEST_OBJECT_UPDATED, new Object[] { oldPk, webElement });
                return webElement;
            }
        }
        return null;
    }
}
