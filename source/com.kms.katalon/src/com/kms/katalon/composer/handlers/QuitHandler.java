package com.kms.katalon.composer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.constants.StringConstants;

public class QuitHandler extends AbstractHandler {

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        // this will be overridden by @Execute openConfirmMessageDialog() below
    }

    @Override
    public Object execute(ExecutionEvent executionEvent) throws ExecutionException {
        // macOS Command+Q will trigger this
        return openConfirmMessageDialog();
    }

    @Execute
    public boolean openConfirmMessageDialog() {
        IPreferenceStore prefs = PlatformUI.getPreferenceStore();
        IWorkbench workbench = getActiveWorkbench();
        MessageDialogWithToggle confirm = MessageDialogWithToggle.open(MessageDialogWithToggle.CONFIRM,
                workbench.getDisplay().getActiveShell(), StringConstants.HAND_QUIT_DIA_TITLE,
                StringConstants.HAND_QUIT_DIA_MSG, StringConstants.HAND_QUIT_DIA_MSG_AUTO_RESTORE_SESSION,
                prefs.getBoolean(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION), prefs,
                PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION, SWT.NONE);
        if (confirm.getReturnCode() != Window.OK) {
            return false;
        }
        EventBrokerSingleton.getInstance().getEventBroker().send(EventConstants.WORKSPACE_CLOSED, null);
        if (partService.saveAll(true)) {
            prefs.setValue(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION, confirm.getToggleState());
            eventBroker.send(EventConstants.PROJECT_CLOSE, null);
            // prevent null pointer when inject IWorkbench
            return workbench.close();
        }
        return false;
    }

}
