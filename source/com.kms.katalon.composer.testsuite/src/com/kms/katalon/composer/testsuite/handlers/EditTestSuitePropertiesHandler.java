package com.kms.katalon.composer.testsuite.handlers;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.CommonEditPropertiesHandler;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.dialogs.TestSuitePropertiesDialog;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class EditTestSuitePropertiesHandler extends CommonEditPropertiesHandler<TestSuiteTreeEntity> {

    private static EditTestSuitePropertiesHandler instance;

    public static EditTestSuitePropertiesHandler getInstance() {
        if (instance == null) {
            instance = new EditTestSuitePropertiesHandler();
        }
        return instance;
    }

    @Override
    public boolean canExecute() {
        return getSingleSelection() != null;
    }

    @Override
    public void execute() {
        TestSuiteTreeEntity selectedObject = getSingleSelection();
        if (selectedObject == null) {
            return;
        }
        try {
            TestSuiteEntity testSuite = (TestSuiteEntity) selectedObject.getObject();
            TestSuitePropertiesDialog dialog = new TestSuitePropertiesDialog(Display.getCurrent().getActiveShell(),
                    testSuite);
            if (dialog.open() != Window.OK || !dialog.isModified()) {
                return;
            }

            TestSuiteController.getInstance().updateTestSuite(dialog.getEntity());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected Class<TestSuiteTreeEntity> getTreeEntityClass() {
        return TestSuiteTreeEntity.class;
    }

}
