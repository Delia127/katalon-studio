package com.kms.katalon.composer.testcase.handlers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.dialogs.TestCaseReferencesDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.dal.exception.TestCaseIsReferencedByTestSuiteExepception;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class DeleteTestCaseHandler {

    @Inject
    private IEventBroker eventBroker;
    
    @Named(IServiceConstants.ACTIVE_SHELL) 
    private Shell activeShell;

    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_DELETE_SELECTED_ITEM, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                // Do nothing
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof TestCaseTreeEntity) {
                    excute((TestCaseTreeEntity) object);
                }
            }
        });
    }

    private void excute(TestCaseTreeEntity testCaseTreeEntity) {
        try {
            TestCaseEntity testCase = (TestCaseEntity) testCaseTreeEntity.getObject();
            List<TestSuiteEntity> testCaseReferences = TestCaseController.getInstance().getTestCaseReferences(testCase);

            if (!testCaseReferences.isEmpty()) {
                TestCaseReferencesDialog dialog = new TestCaseReferencesDialog(activeShell,
                        testCase, testCaseReferences);
                if (dialog.open() == Dialog.OK) {
                    String testCaseId = TestCaseController.getInstance().getIdForDisplay(testCase);
                    for (TestSuiteEntity testSuite : testCaseReferences) {

                        TestSuiteTestCaseLink testCaseLink = TestSuiteController.getInstance().getTestCaseLink(
                                testCaseId, testSuite);
                        testSuite.getTestSuiteTestCaseLinks().remove(testCaseLink);

                        eventBroker.post(EventConstants.TEST_SUITE_UPDATED,
                                new Object[] { testSuite.getId(), testSuite });
                    }
                } else {
                    return;
                }
            }

            // remove TestCase part from its partStack if it exists
            EntityPartUtil.closePart(testCase);

            TestCaseController.getInstance().deleteTestCase(testCase);

            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, TestCaseController.getInstance()
                    .getIdForDisplay(testCase));
        } catch (TestCaseIsReferencedByTestSuiteExepception e) {
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, e.getMessage());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_CASE);
        }
    }
}
