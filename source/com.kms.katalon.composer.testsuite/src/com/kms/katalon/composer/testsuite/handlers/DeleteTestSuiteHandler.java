package com.kms.katalon.composer.testsuite.handlers;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.dialogs.TestSuiteReferencesDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class DeleteTestSuiteHandler extends AbstractDeleteReferredEntityHandler {

    private static TestSuiteController testSuiteController = TestSuiteController.getInstance();

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return TestSuiteTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        try {
            if (treeEntity == null || !(treeEntity instanceof TestSuiteTreeEntity)) {
                return false;
            }

            String taskName = "Deleting " + treeEntity.getTypeName() + " '" + treeEntity.getText() + "'...";
            monitor.beginTask(taskName, 1);

            TestSuiteEntity testSuite = (TestSuiteEntity) treeEntity.getObject();
            performDeleteTestSuite(testSuite);

            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, testSuite.getIdForDisplay());
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_SUITE);
            return false;
        } finally {
            monitor.done();
        }
    }

    protected boolean performDeleteTestSuite(TestSuiteEntity testSuite) throws Exception {
        if (testSuite == null) {
            return false;
        }

        if (!isDeleteReferenceConfirmed(testSuite)) {
            return false;
        }

        UISynchronizeService.syncExec(() -> EntityPartUtil.closePart(testSuite));

        testSuiteController.deleteTestSuite(testSuite);
        return true;
    }

    private boolean isDeleteReferenceConfirmed(final TestSuiteEntity testSuite) throws DALException {
        final List<TestSuiteCollectionEntity> affectedTestSuites = testSuiteController.getTestSuiteCollectionReferences(testSuite);
        if (affectedTestSuites.isEmpty()) {
            return true;
        }
        if (isDefaultResponse()) {
            sync.syncExec(new Runnable() {

                @Override
                public void run() {

                    TestSuiteReferencesDialog dialog = new TestSuiteReferencesDialog(getActiveShell(),
                            testSuite.getIdForDisplay(), affectedTestSuites, needYesNoToAllButtons());
                    setResponse(dialog.open());
                }
            });
        }

        if (isCancelResponse()) {
            return false;
        }

        if (isYesResponse()) {
            // remove test suite collection references in test suite
            testSuiteController.removeTestSuiteCollectionReferences(testSuite, affectedTestSuites);
        }
        return true;
    }

    private Shell getActiveShell() {
        return Display.getCurrent().getActiveShell();
    }
}
