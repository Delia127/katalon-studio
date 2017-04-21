package com.kms.katalon.composer.mobile.objectspy.handler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileObjectSpyDialog;
import com.kms.katalon.composer.mobile.util.MobileUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.repository.WebElementEntity;

public class MobileSpyMobileHandler {
    private MobileObjectSpyDialog objectSpyDialog;

    private Shell activeShell;

    @Inject
    private IEventBroker eventBroker;

    private static MobileSpyMobileHandler instance;

    public MobileSpyMobileHandler() {
        instance = this;
    }

    public static MobileSpyMobileHandler getInstance() {
        return instance;
    }

    @PostConstruct
    public void registerEvent() {
        eventBroker.subscribe(EventConstants.OBJECT_SPY_MOBILE, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                if (!canExecute()) {
                    return;
                }
                execute(Display.getCurrent().getActiveShell());
            }
        });
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
        MobileUtil.detectAppiumAndNodeJs(activeShell);
        openAndAddElements(activeShell, new ArrayList<WebElementEntity>());
    }

    public void openAndAddElements(Shell activeShell, List<WebElementEntity> webElements) {
        if (!openObjectSpyDialog(activeShell)) {
            return;
        }
        objectSpyDialog.addElements(webElements);
    }

    private boolean openObjectSpyDialog(Shell activeShell) {
        try {
            if (this.activeShell == null) {
                this.activeShell = activeShell;
            }

            if (!isObjectSpyDialogRunning()) {
                objectSpyDialog = new MobileObjectSpyDialog(activeShell);
                objectSpyDialog.open();
            }

            if (!objectSpyDialog.isCanceledBeforeOpening()) {
                objectSpyDialog.getShell().forceActive();
            }
            return true;
        } catch (Exception e) {
            if (isObjectSpyDialogRunning()) {
                objectSpyDialog.dispose();
                objectSpyDialog.close();
            }
            LoggerSingleton.logError(e);
            MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE, e.getMessage());
            return false;
        }
    }

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    public boolean isObjectSpyDialogRunning() {
        return objectSpyDialog != null && !objectSpyDialog.isDisposed();
    }
}
