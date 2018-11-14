package com.kms.katalon.composer.handlers;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.ProjectPreferenceConstants;
import com.kms.katalon.composer.project.handlers.CloseProjectHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

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
        IPreferenceStore prefs = PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
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
        if (getPartService() != null && getPartService().saveAll(true)) {
            prefs.setValue(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION, confirm.getToggleState());
            try {
                ((IPersistentPreferenceStore) prefs).save();
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
            saveLastestOpenedProject();
            eventBroker.send(EventConstants.PROJECT_CLOSE, null);
            // prevent null pointer when inject IWorkbench
            return workbench.close();
        }
        return workbench.close();
    }

    private static void saveLastestOpenedProject() {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        String projectLoc = project != null ? project.getId() : StringUtils.EMPTY;
        IPreferenceStore store = PreferenceStoreManager.getPreferenceStore(CloseProjectHandler.class);
        store.setValue(ProjectPreferenceConstants.LATEST_OPENED_PROJECT, projectLoc);
        try {
            ((IPersistentPreferenceStore) store).save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }
}
