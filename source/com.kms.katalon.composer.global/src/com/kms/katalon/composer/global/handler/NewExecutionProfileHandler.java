package com.kms.katalon.composer.global.handler;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.ProfileRootTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ProfileTreeEntity;
import com.kms.katalon.composer.global.constants.ComposerGlobalMessageConstants;
import com.kms.katalon.composer.global.dialog.ExecutionProfileNameDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.tracking.service.Trackings;

public class NewExecutionProfileHandler extends ExecutionProfileTreeRootCatcher {
    @Inject
    private IEventBroker eventBroker;

    @Inject
    private ESelectionService selectionService;

    @Execute
    private void execute() {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        try {

            ProfileRootTreeEntity profileTreeFolder = getProfileTreeFolder(selectionService);
            List<ExecutionProfileEntity> sibblingProfiles = GlobalVariableController.getInstance()
                    .getAllGlobalVariableCollections(project);

            String suggestedName = EntityNameController.getInstance().getAvailableName("New Profile",
                    profileTreeFolder.getObject(), false);
            ExecutionProfileNameDialog dialog = new ExecutionProfileNameDialog(
                    Display.getCurrent().getActiveShell(), suggestedName,
                    sibblingProfiles, ComposerGlobalMessageConstants.DIA_TITLE_NEW_EXECUTION_PROFILE);
            if (dialog.open() != ExecutionProfileNameDialog.OK) {
                return;
            }
            String newName = dialog.getNewName();
            ExecutionProfileEntity newProfile = GlobalVariableController.getInstance().newExecutionProfile(newName, project);
            eventBroker.post(EventConstants.EXECUTION_PROFILE_CREATED, newName);
            
            Trackings.trackCreatingObject("profile");

            ProfileTreeEntity newProfileTree = new ProfileTreeEntity(newProfile, profileTreeFolder);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, profileTreeFolder);
            eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, newProfileTree);
            
            eventBroker.post(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, newProfile);
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog("Unable to create execution profile", e.getMessage(),
                    ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }
}
