package com.kms.katalon.composer.testdata.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.dal.exception.TestDataReferredException;
import com.kms.katalon.entity.dal.exception.EntityIsReferencedException;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteTestCaseLinkPair;

public class DeleteTestDataHandler {

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_DELETE_SELECTED_ITEM, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof TestDataTreeEntity) {
                    execute((TestDataTreeEntity) object);
                }
            }
        });
    }

    private void execute(TestDataTreeEntity testDataTreeEntity) {
        try {
            DataFileEntity testData = (DataFileEntity) testDataTreeEntity.getObject();
            
            if (testData == null) {
                return;
            }
            
            // remove TestCase part from its partStack if it exists
            EntityPartUtil.closePart(testData);
            
            TestDataController.getInstance().deleteDataFile(testData);
            
            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, TestDataController.getInstance()
                    .getIdForDisplay(testData));
        } catch (TestDataReferredException e) {
            MultiStatusErrorDialog.showErrorDialog(new EntityIsReferencedException(
                    getMessageFormTestDataReferencedException(e)),
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_DATA,
                    "Test data is being referred by test suites.");
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_DATA);
        }
    }

    private String getMessageFormTestDataReferencedException(TestDataReferredException e) {
        try {
            StringBuilder referenceString = new StringBuilder();
            for (TestSuiteTestCaseLinkPair pair : e.getTestSuiteTestCaseLinkPairs()) {
                String testSuiteId = TestSuiteController.getInstance().getIdForDisplay(pair.getTestSuiteEntity());
                String testCaseId = pair.getTestCaseLink().getTestCaseId();
                referenceString.append("Test suite: " + testSuiteId + " - Test Case: " + testCaseId).append("\n");
            }

            return referenceString.toString();
        } catch (Exception ex) {
            return "";
        }
    }

}
