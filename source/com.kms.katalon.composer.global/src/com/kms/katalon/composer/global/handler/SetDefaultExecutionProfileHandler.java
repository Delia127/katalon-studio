package com.kms.katalon.composer.global.handler;

import java.text.MessageFormat;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.handler.CommonExplorerHandler;
import com.kms.katalon.composer.components.impl.tree.ProfileTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class SetDefaultExecutionProfileHandler extends CommonExplorerHandler {

    @Inject
    private IEventBroker eventBroker;

    @Override
    public boolean canExecute() {
        ITreeEntity firstSelection = getFirstSelection();
        if (firstSelection == null || !(firstSelection instanceof ProfileTreeEntity)) {
            return false;
        }
        ExecutionProfileEntity selectedProfile;
        try {
            selectedProfile = (ExecutionProfileEntity) firstSelection.getObject();
        } catch (Exception e) {
            return false;
        }
        return !selectedProfile.isDefaultProfile();
    }

    @Override
    public void execute() {
        ProfileTreeEntity selectedProfileTree = (ProfileTreeEntity) getFirstSelection();
        ExecutionProfileEntity selectedProfile = null;
        try {
            selectedProfile = selectedProfileTree.getObject();
        } catch (Exception ignored) { }
        if (selectedProfile == null) {
            return;
        }

        try {
            selectedProfile.setDefaultProfile(true);
            GlobalVariableController.getInstance().updateExecutionProfile(selectedProfile);
            
            String selectedProfileName = selectedProfile.getName();
            ProjectEntity project = ProjectController.getInstance().getCurrentProject();
            GlobalVariableController.getInstance()
                    .getAllGlobalVariableCollections(project)
                    .stream()
                    .filter(profile -> !profile.getName().equals(selectedProfileName))
                    .forEach(profile -> {
                        profile.setDefaultProfile(false);
                        try {
                            GlobalVariableController.getInstance().updateExecutionProfile(profile);
                        } catch (ControllerException exception) {
                            LoggerSingleton.logError(exception);
                        }
                    });

            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, selectedProfileTree.getParent());
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(
                    MessageFormat.format("Unable to set default execution profile to '{0}'", selectedProfile.getName()),
                    e.getMessage(), ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }

    private ITreeEntity getFirstSelection() {
        List<ITreeEntity> selectedElements = getElementSelection(ITreeEntity.class);
        if (selectedElements == null) {
            return null;
        }
        return selectedElements.size() == 1 ? selectedElements.get(0) : null;
    }
}
