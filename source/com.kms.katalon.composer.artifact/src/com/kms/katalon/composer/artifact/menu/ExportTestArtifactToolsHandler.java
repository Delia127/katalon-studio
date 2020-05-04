
package com.kms.katalon.composer.artifact.menu;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.katalon.platform.api.model.ProjectEntity;
import com.kms.katalon.composer.artifact.constant.StringConstants;
import com.kms.katalon.composer.artifact.core.util.PlatformUtil;
import com.kms.katalon.composer.artifact.handler.ExportTestArtifactHandler;
import com.kms.katalon.composer.components.impl.handler.KSEFeatureAccessHandler;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.KSEFeature;

public class ExportTestArtifactToolsHandler {

    private IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();

    @CanExecute
    public boolean canExecute() {
        if (ProjectController.getInstance().getCurrentProject() != null) {
            return true;
        }
        return false;
    }

    @Execute
    public void execute() {
        if (featureService.canUse(KSEFeature.EXPORT_TEST_ARTIFACTS)) {
            ProjectEntity project = PlatformUtil.getCurrentProject();
            if (project != null) {
                ExportTestArtifactHandler handler = new ExportTestArtifactHandler(Display.getCurrent().getActiveShell());
                handler.execute();
            } else {
                MessageDialog.openInformation(Display.getCurrent().getActiveShell(), StringConstants.INFO,
                        StringConstants.MSG_OPEN_A_PROJECT);
            }
        } else {
            KSEFeatureAccessHandler.handleUnauthorizedAccess(KSEFeature.EXPORT_TEST_ARTIFACTS);
        }
    }

}
