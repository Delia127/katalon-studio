package com.kms.katalon.composer.testcase.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.composer.testcase.util.TestCaseEntityUtil.getTestCaseEntities;
import static com.kms.katalon.groovy.util.GroovyRefreshUtil.findReferencesInTestCaseScripts;
import static com.kms.katalon.groovy.util.GroovyRefreshUtil.removeReferencesInTestCaseScripts;
import static java.text.MessageFormat.format;
import static org.eclipse.jface.dialogs.MessageDialog.openError;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.dialogs.TestCaseReferencesDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class DeleteTestCaseHandler extends AbstractDeleteReferredEntityHandler {
    @Override
    public Class<? extends ITreeEntity> entityType() {
        return TestCaseTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity entity, IProgressMonitor monitor) {
        try {
            if (entity == null || !(entity instanceof TestCaseTreeEntity)) {
                return false;
            }

            TestCaseEntity testCase = (TestCaseEntity) entity.getObject();
            String testCaseId = testCase.getIdForDisplay();
            monitor.subTask(format(StringConstants.HAND_JOB_DELETING, testCaseId));

            if (performDeleteTestCase(testCase, findReferencesInTestCaseScripts(testCaseId, testCase.getProject()))) {
                eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, testCaseId);
                return true;
            }
        } catch (Exception e) {
            logError(e);
            openError(null, StringConstants.ERROR_TITLE, StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_CASE);
        } finally {
            monitor.done();
        }
        return false;
    }

    protected boolean performDeleteTestCase(final TestCaseEntity testCase, final List<IFile> affectedTestCaseScripts) {
        isDeleted = false;
        sync.syncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    List<FileEntity> affectedObjects = new ArrayList<FileEntity>();
                    TestCaseController tcController = TestCaseController.getInstance();
                    List<TestSuiteEntity> affectedTestSuites = tcController.getTestCaseReferences(testCase);
                    affectedObjects.addAll(affectedTestSuites);
                    affectedObjects.addAll(getTestCaseEntities(affectedTestCaseScripts));

                    if (!affectedObjects.isEmpty()) {
                        if (isDefaultResponse()) {
                            TestCaseReferencesDialog dialog = new TestCaseReferencesDialog(null,
                                    testCase.getIdForDisplay(), affectedObjects, needYesNoToAllButtons());
                            setResponse(dialog.open());
                        }

                        if (isCancelResponse()) {
                            return;
                        }

                        if (isYesResponse()) {
                            // remove test case references in test suite
                            removeReferencesInTestSuite(testCase, affectedTestSuites);

                            // remove references (calling) in test case
                            removeReferencesInTestCaseScripts(testCase.getIdForDisplay(), affectedTestCaseScripts);
                        }
                    }

                    // remove TestCase part from its partStack if it exists
                    EntityPartUtil.closePart(testCase);

                    // remove test case
                    tcController.deleteTestCase(testCase);

                    if (!isYesNoToAllResponse()) {
                        resetResponse();
                    }

                    isDeleted = true;
                } catch (Exception e) {
                    logError(e);
                }
            }
        });
        return isDeleted;
    }

    /**
     * Remove test case references in test suite
     * 
     * @param testCase Test Case to be removed
     * @param affectedTestSuites List of affected Test Suite Entity
     * @param eventBroker
     * @throws Exception
     */
    private void removeReferencesInTestSuite(TestCaseEntity testCase, List<TestSuiteEntity> affectedTestSuites)
            throws Exception {
        TestSuiteController tsController = TestSuiteController.getInstance();
        for (TestSuiteEntity testSuite : affectedTestSuites) {
            TestSuiteTestCaseLink testCaseLink = tsController.getTestCaseLink(testCase.getIdForDisplay(), testSuite);
            testSuite.getTestSuiteTestCaseLinks().remove(testCaseLink);
            tsController.updateTestSuite(testSuite);
            eventBroker.post(EventConstants.TEST_SUITE_UPDATED, new Object[] { testSuite.getId(), testSuite });
        }
    }
}
