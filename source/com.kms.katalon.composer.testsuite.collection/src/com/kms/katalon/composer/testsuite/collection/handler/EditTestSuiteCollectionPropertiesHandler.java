package com.kms.katalon.composer.testsuite.collection.handler;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.handler.CommonEditPropertiesHandler;
import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.collection.constant.StringConstants;
import com.kms.katalon.composer.testsuite.collection.dialog.TestSuiteCollectionPropertiesDialog;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class EditTestSuiteCollectionPropertiesHandler extends
        CommonEditPropertiesHandler<TestSuiteCollectionTreeEntity> {

    @Override
    protected Class<TestSuiteCollectionTreeEntity> getTreeEntityClass() {
        return TestSuiteCollectionTreeEntity.class;
    }

    @Override
    public boolean canExecute() {
        return getSingleSelection() != null;
    }

    @Override
    public void execute() {
        TestSuiteCollectionTreeEntity tsCollectionTree = getSingleSelection();
        try {
            TestSuiteCollectionEntity testSuiteCollection = (TestSuiteCollectionEntity) tsCollectionTree.getObject();

            if (testSuiteCollection == null) {
                return;
            }

            TestSuiteCollectionPropertiesDialog propertiesDialog = new TestSuiteCollectionPropertiesDialog(Display
                    .getCurrent().getActiveShell(), testSuiteCollection);

            if (propertiesDialog.open() != Dialog.OK || !propertiesDialog.isModified()) {
                return;
            }

            TestSuiteCollectionController.getInstance().updateTestSuiteCollection(propertiesDialog.getEntity());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e,
                    StringConstants.HDL_MSG_UNABLE_TO_UPDATE_TEST_SUITE_COLLECTION_PROPERTIES, e.getMessage());
        }
    }
}
