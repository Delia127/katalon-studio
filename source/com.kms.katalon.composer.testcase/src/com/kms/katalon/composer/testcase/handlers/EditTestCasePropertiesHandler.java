package com.kms.katalon.composer.testcase.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.testcase.dialogs.TestCasePropertiesDialog;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class EditTestCasePropertiesHandler implements IHandler {

    @CanExecute
    public static boolean canExecute() {
        return getFirstSelection() != null;
    }

    @Execute
    public void execute() {
        TestCaseTreeEntity selectedObject = getFirstSelection();
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
    public Object execute(ExecutionEvent event) throws ExecutionException {
        execute();
        return null;
    }

    private static TestCaseTreeEntity getFirstSelection() {
        Object o = SelectionServiceSingleton.getInstance()
                .getSelectionService()
                .getSelection(IdConstants.EXPLORER_PART_ID);
        if (o == null || !o.getClass().isArray() || ((Object[]) o).length != 1) {
            return null;
        }

        Object selectedObject = ((Object[]) o)[0];
        if (!(selectedObject instanceof TestCaseTreeEntity)) {
            return null;
        }

        return (TestCaseTreeEntity) selectedObject;
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
