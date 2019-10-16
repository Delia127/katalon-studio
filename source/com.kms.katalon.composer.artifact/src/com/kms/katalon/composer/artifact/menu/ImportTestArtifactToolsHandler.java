
package com.kms.katalon.composer.artifact.menu;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.katalon.platform.api.model.ProjectEntity;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.artifact.constant.StringConstants;
import com.kms.katalon.composer.artifact.core.util.PlatformUtil;
import com.kms.katalon.composer.artifact.handler.ImportTestArtifactHandler;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.license.models.LicenseType;

public class ImportTestArtifactToolsHandler {

    @CanExecute
    public boolean canExecute() {
        if (ProjectController.getInstance().getCurrentProject() != null && LicenseType
                .valueOf(ApplicationInfo.getAppProperty(ApplicationStringConstants.LICENSE_TYPE)) != LicenseType.FREE) {
            return true;
        }
        return false;
    }

    @Execute
    public void execute() {
        ProjectEntity project = PlatformUtil.getCurrentProject();
        if (project != null) {
            ImportTestArtifactHandler handler = new ImportTestArtifactHandler(Display.getCurrent().getActiveShell());
            handler.execute();
        } else {
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), StringConstants.INFO,
                    StringConstants.MSG_OPEN_A_PROJECT);
        }

    }

}
