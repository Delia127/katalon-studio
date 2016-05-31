package com.kms.katalon.composer.testcase.handlers;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.CommonEditPropertiesHandler;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.dialogs.TestCasePropertiesDialog;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class EditTestCasePropertiesHandler extends CommonEditPropertiesHandler<TestCaseTreeEntity> {

    private static EditTestCasePropertiesHandler instance;

    public static EditTestCasePropertiesHandler getInstance() {
        if (instance == null) {
            instance = new EditTestCasePropertiesHandler();
        }
        return instance;
    }

    @Override
    public boolean canExecute() {
        return getSingleSelection() != null;
    }

    @Override
    public void execute() {
        TestCaseTreeEntity selectedObject = getSingleSelection();
        if (selectedObject == null) {
            return;
        }
        try {
            TestCaseEntity testcase = (TestCaseEntity) selectedObject.getObject();
            TestCasePropertiesDialog dialog = new TestCasePropertiesDialog(Display.getCurrent().getActiveShell(),
                    testcase);
            if (dialog.open() != Window.OK || !dialog.isModified()) {
                return;
            }

            TestCaseController.getInstance().updateTestCase(dialog.getEntity());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected Class<TestCaseTreeEntity> getTreeEntityClass() {
        return TestCaseTreeEntity.class;
    }

}
