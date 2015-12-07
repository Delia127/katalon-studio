package com.kms.katalon.composer.testcase.handlers;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.dialogs.TestCaseReferencesDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.dal.exception.TestCaseIsReferencedByTestSuiteExepception;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class DeleteTestCaseHandler extends AbstractDeleteReferredEntityHandler implements IDeleteEntityHandler {

    @Inject
    protected UISynchronize sync;

    @Inject
    protected IEventBroker eventBroker;

    protected boolean isRemovingRef = false;

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

            final TestCaseEntity testCase = (TestCaseEntity) entity.getObject();
            final String testCaseId = TestCaseController.getInstance().getIdForDisplay(testCase);
            monitor.subTask("Deleting '" + testCaseId + "'...");

            final boolean hasRef = GroovyRefreshUtil.hasReferencesInTestCaseScripts(testCaseId, testCase.getProject());
            sync.syncExec(new Runnable() {

                @Override
                public void run() {
                    // Give a confirmation message for current Test Object to remove its references
                    if (hasRef) {
                        // Depend on the user response, references will be removed or not
                        isRemovingRef = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                                StringConstants.HAND_TITLE_DELETE, StringConstants.HAND_MSG_REMOVE_ENTITY_REF);
                    }
                }
            });

            if (deleteTestCase(testCase, sync, eventBroker, isRemovingRef)) {
                eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, TestCaseController.getInstance()
                        .getIdForDisplay(testCase));
                return true;
            } else {
                return false;
            }
        } catch (TestCaseIsReferencedByTestSuiteExepception e) {
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, e.getMessage());
            return false;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_CASE);
            return false;
        } finally {
            monitor.done();
            eventBroker.post(EventConstants.EXPLORER_REFRESH_ALL_ITEMS, null);
        }
    }

    protected boolean deleteTestCase(final TestCaseEntity testCase, final UISynchronize sync,
            final IEventBroker eventBroker, boolean isRemovingRefInTestCaseScripts) {
        try {
            final List<TestSuiteEntity> testCaseReferences = TestCaseController.getInstance().getTestCaseReferences(
                    testCase);

            if (testCaseReferences.size() > 0) {
                if (!canDelete()) {
                    if (!needToShowPreferenceDialog()) {
                        return false;
                    }

                    final DeleteTestCaseHandler handler = this;

                    sync.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            TestCaseReferencesDialog dialog = new TestCaseReferencesDialog(Display.getCurrent()
                                    .getActiveShell(), testCase, testCaseReferences, handler);
                            dialog.open();
                        }
                    });
                }

                if (canDelete()) {
                    deleteTestCaseReferences(testCase, testCaseReferences, eventBroker);
                } else {
                    return false;
                }
            }

            if (isRemovingRefInTestCaseScripts) {
                // Remove test case references in other Test Cases
                List<IFile> affectedTestCaseScripts = GroovyRefreshUtil.findReferencesInTestCaseScripts(
                        TestCaseController.getInstance().getIdForDisplay(testCase), testCase.getProject());
                GroovyRefreshUtil.removeReferencesInTestCaseScripts(
                        TestCaseController.getInstance().getIdForDisplay(testCase), affectedTestCaseScripts);
            }

            // remove TestCase part from its partStack if it exists
            EntityPartUtil.closePart(testCase);

            TestCaseController.getInstance().deleteTestCase(testCase);

            return true;
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return false;
        }
    }

    private void deleteTestCaseReferences(final TestCaseEntity testCase,
            final List<TestSuiteEntity> testCaseReferences, IEventBroker eventBroker) {
        try {
            String testCaseId = TestCaseController.getInstance().getIdForDisplay(testCase);

            for (TestSuiteEntity testSuite : testCaseReferences) {

                TestSuiteTestCaseLink testCaseLink = TestSuiteController.getInstance().getTestCaseLink(testCaseId,
                        testSuite);
                testSuite.getTestSuiteTestCaseLinks().remove(testCaseLink);

                eventBroker.post(EventConstants.TEST_SUITE_UPDATED, new Object[] { testSuite.getId(), testSuite });
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
