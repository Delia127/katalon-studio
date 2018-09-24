package com.kms.katalon.composer.webservice.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.webservice.view.ApiQuickStartDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;

public class OpenApiQuickStartHandler {

    @Inject
    @Optional
    public void onNewWsProjectOpnened(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, 
            @UIEventTopic(EventConstants.API_QUICK_START_DIALOG_OPEN) Object object) {
        ApiQuickStartDialog quickStartDialog = new ApiQuickStartDialog(shell);
        quickStartDialog.open();      
    }
}
