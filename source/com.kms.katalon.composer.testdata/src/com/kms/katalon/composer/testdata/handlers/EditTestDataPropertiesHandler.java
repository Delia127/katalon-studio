package com.kms.katalon.composer.testdata.handlers;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.CommonEditPropertiesHandler;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testdata.dialog.TestDataPropertiesDialog;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class EditTestDataPropertiesHandler extends CommonEditPropertiesHandler<TestDataTreeEntity> {

    private static EditTestDataPropertiesHandler instance;

    public static EditTestDataPropertiesHandler getInstance() {
        if (instance == null) {
            instance = new EditTestDataPropertiesHandler();
        }
        return instance;
    }

    @Override
    public boolean canExecute() {
        return getSingleSelection() != null;
    }

    @Override
    public void execute() {
        TestDataTreeEntity selectedObject = getSingleSelection();
        if (selectedObject == null) {
            return;
        }
        try {
            DataFileEntity testData = (DataFileEntity) selectedObject.getObject();
            TestDataPropertiesDialog dialog = new TestDataPropertiesDialog(Display.getCurrent().getActiveShell(),
                    testData);
            if (dialog.open() != Window.OK || !dialog.isModified()) {
                return;
            }

            TestDataController.getInstance().updateTestData(testData, testData.getParentFolder());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected Class<TestDataTreeEntity> getTreeEntityClass() {
        return TestDataTreeEntity.class;
    }

}
