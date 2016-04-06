package com.kms.katalon.composer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.constants.StringConstants;

public class QuitHandler {
    @Inject
    IEventBroker eventBroker;

    @Execute
    public boolean execute(IWorkbench workbench, Shell shell, EPartService partService) {
        IPreferenceStore prefs = PlatformUI.getPreferenceStore();
        MessageDialogWithToggle confirm = MessageDialogWithToggle.open(MessageDialogWithToggle.CONFIRM, shell,
                StringConstants.HAND_QUIT_DIA_TITLE, StringConstants.HAND_QUIT_DIA_MSG,
                StringConstants.HAND_QUIT_DIA_MSG_AUTO_RESTORE_SESSION,
                prefs.getBoolean(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION), prefs,
                PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION, SWT.NONE);
        if (confirm.getReturnCode() == Window.OK) {
            if (partService.saveAll(true)) {
                prefs.setValue(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION, confirm.getToggleState());
                eventBroker.send(EventConstants.PROJECT_CLOSE, null);
                workbench.close();
                return true;
            }
        }
        return false;
    }
}
