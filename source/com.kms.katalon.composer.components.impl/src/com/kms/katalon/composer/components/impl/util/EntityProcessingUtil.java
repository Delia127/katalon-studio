package com.kms.katalon.composer.components.impl.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class EntityProcessingUtil {
    public static FolderEntity moveFolder(FolderEntity folder, FolderEntity targetFolder) throws Exception {
        if (targetFolder != null && folder != null && folder.getFolderType() == targetFolder.getFolderType()) {
            // get collection of descendant entities that doesn't include descendant folder entity
            List<FileEntity> allDescendantEntites = new ArrayList<>();
            for (Object descendantEntity : FolderController.getInstance().getAllDescentdantEntities(folder)) {
                if (!(descendantEntity instanceof FolderEntity)) {
                    allDescendantEntites.add((FileEntity) descendantEntity);
                }
            }

            FolderEntity movedFolder = FolderController.getInstance().moveFolder(folder, targetFolder);

            // afterSave
            // send notification event
            String eventTopic = null;
            switch (folder.getFolderType()) {
                case TESTCASE:
                    eventTopic = EventConstants.TESTCASE_UPDATED;
                    break;
                case DATAFILE:
                    eventTopic = EventConstants.TEST_DATA_UPDATED;
                    break;
                case TESTSUITE:
                    eventTopic = EventConstants.TEST_SUITE_UPDATED;
                    break;
                case WEBELEMENT:
                    eventTopic = EventConstants.TEST_OBJECT_UPDATED;
                    break;
                case CHECKPOINT:
                    eventTopic = EventConstants.CHECKPOINT_UPDATED;
                    break;
                default:
                    break;
            }
            if (eventTopic != null) {
                for (FileEntity entity : allDescendantEntites) {
                    EventBrokerSingleton.getInstance()
                            .getEventBroker()
                            .post(eventTopic, new Object[] { entity.getId(), entity });
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
                EventBrokerSingleton.getInstance()
                        .getEventBroker()
                        .post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM,
                                new Object[] { oldIdForDisplay, testCase.getIdForDisplay() });
                EventBrokerSingleton.getInstance()
                        .getEventBroker()
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
                EventBrokerSingleton.getInstance()
                        .getEventBroker()
                        .post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM,
                                new Object[] { oldIdForDisplay, testSuite.getIdForDisplay() });
                EventBrokerSingleton.getInstance()
                        .getEventBroker()
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
                EventBrokerSingleton.getInstance()
                        .getEventBroker()
                        .post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM,
                                new Object[] { oldIdForDisplay, dataFile.getIdForDisplay() });
                EventBrokerSingleton.getInstance()
                        .getEventBroker()
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
                EventBrokerSingleton.getInstance()
                        .getEventBroker()
                        .post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM,
                                new Object[] { oldIdForDisplay, webElement.getIdForDisplay() });
                EventBrokerSingleton.getInstance()
                        .getEventBroker()
                        .post(EventConstants.TEST_OBJECT_UPDATED, new Object[] { oldPk, webElement });
                return webElement;
            }
        }
        return null;
    }

    public static TestSuiteCollectionEntity moveTestSuiteCollection(TestSuiteCollectionEntity testSuiteCollection,
            FolderEntity targetFolder) throws Exception {
        if (testSuiteCollection == null || targetFolder == null) {
            return null;
        }
        TestSuiteCollectionController testSuiteCollectionController = TestSuiteCollectionController.getInstance();
        String oldPk = testSuiteCollection.getId();
        String oldIdForDisplay = testSuiteCollection.getIdForDisplay();
        testSuiteCollection = testSuiteCollectionController.moveTestSuiteCollection(testSuiteCollection, targetFolder);
        String newPk = testSuiteCollection.getId();
        if (!ObjectUtils.equals(oldPk, newPk)) {
            IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
            eventBroker.post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM, new Object[] { oldIdForDisplay,
                    testSuiteCollection.getIdForDisplay() });
            eventBroker.post(EventConstants.TEST_SUITE_COLLECTION_UPDATED, new Object[] { oldPk, testSuiteCollection });
            return testSuiteCollection;
        }
        return null;
    }

    public static CheckpointEntity moveCheckpoint(CheckpointEntity checkpoint, FolderEntity targetFolder)
            throws Exception {
        if (checkpoint == null || targetFolder == null) {
            return null;
        }
        String oldId = checkpoint.getId();
        String oldIdForDisplay = checkpoint.getIdForDisplay();

        checkpoint = CheckpointController.getInstance().move(checkpoint, targetFolder);
        if (StringUtils.equals(oldId, checkpoint.getId())) {
            return null;
        }

        IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
        eventBroker.post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM,
                new Object[] { oldIdForDisplay, checkpoint.getIdForDisplay() });
        eventBroker.post(EventConstants.CHECKPOINT_UPDATED, new Object[] { oldId, checkpoint });
        return checkpoint;
    }
}
