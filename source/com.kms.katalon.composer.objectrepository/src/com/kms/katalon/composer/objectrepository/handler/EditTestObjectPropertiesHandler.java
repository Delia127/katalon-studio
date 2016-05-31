package com.kms.katalon.composer.objectrepository.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.objectrepository.dialog.TestObjectPropertiesDialog;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.repository.WebElementEntity;

public class EditTestObjectPropertiesHandler implements IHandler {

    @CanExecute
    public static boolean canExecute() {
        return getFirstSelection() != null;
    }

    @Execute
    public void execute() {
        WebElementTreeEntity selectedObject = getFirstSelection();
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

    private static WebElementTreeEntity getFirstSelection() {
        Object o = SelectionServiceSingleton.getInstance()
                .getSelectionService()
                .getSelection(IdConstants.EXPLORER_PART_ID);
        if (o == null || !o.getClass().isArray() || ((Object[]) o).length != 1) {
            return null;
        }

        Object selectedObject = ((Object[]) o)[0];
        if (!(selectedObject instanceof WebElementTreeEntity)) {
            return null;
        }

        return (WebElementTreeEntity) selectedObject;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        execute();
        return null;
    }

    @Override
    public boolean isEnabled() {
        return canExecute();
    }

    @Override
    public boolean isHandled() {
        return true;
    }

    @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
        // do nothing
    }

    @Override
    public void dispose() {
        // do nothing
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener) {
        // do nothing
    }

}
