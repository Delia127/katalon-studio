package com.kms.katalon.composer.testsuite.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class DeleteTestSuiteHandler implements IDeleteEntityHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    MApplication application;

    @Inject
    EModelService modelService;

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        try {
            if (treeEntity == null || !(treeEntity instanceof TestSuiteTreeEntity)) {
                return false;
            }
            
            String taskName = "Deleting " + treeEntity.getTypeName() + " '" + treeEntity.getText()
                    + "'...";
            monitor.beginTask(taskName, 1);
            
            TestSuiteEntity testSuite = (TestSuiteEntity) treeEntity.getObject();

            // remove TestSuite part from its partStack if it exists
            EntityPartUtil.closePart(testSuite);

            TestSuiteController.getInstance().deleteTestSuite(testSuite);

            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, TestSuiteController.getInstance()
                    .getIdForDisplay(testSuite));
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_SUITE);
            return false;
        } finally {
            monitor.done();
        }
    }

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return TestSuiteTreeEntity.class;
    }
}
