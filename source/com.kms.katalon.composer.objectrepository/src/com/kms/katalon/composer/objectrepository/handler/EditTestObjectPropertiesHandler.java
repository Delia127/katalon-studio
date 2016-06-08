package com.kms.katalon.composer.objectrepository.handler;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.CommonEditPropertiesHandler;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.objectrepository.dialog.TestObjectPropertiesDialog;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.repository.WebElementEntity;

public class EditTestObjectPropertiesHandler extends CommonEditPropertiesHandler<WebElementTreeEntity> {

    private static EditTestObjectPropertiesHandler instance;

    public static EditTestObjectPropertiesHandler getInstance() {
        if (instance == null) {
            instance = new EditTestObjectPropertiesHandler();
        }
        return instance;
    }

    @Override
    public boolean canExecute() {
        return getSingleSelection() != null;
    }

    @Override
    public void execute() {
        WebElementTreeEntity selectedObject = getSingleSelection();
        if (selectedObject == null) {
            return;
        }
        try {
            WebElementEntity testObject = (WebElementEntity) selectedObject.getObject();
            TestObjectPropertiesDialog dialog = new TestObjectPropertiesDialog(Display.getCurrent().getActiveShell(),
                    testObject);
            if (dialog.open() != Window.OK || !dialog.isModified()) {
                return;
            }

            ObjectRepositoryController.getInstance().updateTestObject(dialog.getEntity());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected Class<WebElementTreeEntity> getTreeEntityClass() {
        return WebElementTreeEntity.class;
    }

}
