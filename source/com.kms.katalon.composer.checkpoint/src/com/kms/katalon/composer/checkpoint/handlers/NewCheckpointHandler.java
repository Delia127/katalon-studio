package com.kms.katalon.composer.checkpoint.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.checkpoint.dialogs.wizard.NewCheckpointWizard;
import com.kms.katalon.composer.checkpoint.dialogs.wizard.NewCheckpointWizardDialog;
import com.kms.katalon.composer.components.impl.tree.CheckpointTreeEntity;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;

public class NewCheckpointHandler {

    @Inject
    private IEventBroker eventBroker;

    @Named(IServiceConstants.ACTIVE_SHELL)
    protected Shell parentShell;

    private FolderEntity parentFolder;

    private static NewCheckpointHandler instance;

    public static NewCheckpointHandler getInstance() {
        if (instance == null) {
            instance = new NewCheckpointHandler();
        }
        return instance;
    }

    @CanExecute
    public boolean canExecute() {
        return getProject() != null;
    }

    @Execute
    public void execute() {
        try {
            ITreeEntity parent = findParentSelection();
            if (parent == null) {
                parent = new FolderTreeEntity(FolderController.getInstance().getCheckpointRoot(getProject()), null);
            }

            parentFolder = (FolderEntity) parent.getObject();
            CheckpointEntity checkpoint = getCheckpointFromDialog(parentFolder);
            if (checkpoint == null) {
                // cancel or close the new checkpoint dialog
                return;
            }

            checkpoint = CheckpointController.getInstance().create(checkpoint);
            if (checkpoint == null) {
                MessageDialog.openError(parentShell, StringConstants.ERROR,
                        StringConstants.HAND_MSG_UNABLE_TO_CREATE_CHECKPOINT);
                return;
            }

            try {
                CheckpointController.getInstance().takeSnapshot(checkpoint);
            } catch (Exception e) {
                // Ignore all exception on first screen. User may put the wrong info.
                // Should not disturb them by any message of taking snapshot on creation.
            }

            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parent);
            eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, new CheckpointTreeEntity(checkpoint, parent));
            eventBroker.post(EventConstants.CHECKPOINT_OPEN, checkpoint);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(parentShell, StringConstants.ERROR, e.getMessage());
        }
    }

    protected CheckpointEntity getCheckpointFromDialog(FolderEntity parentFolder) throws Exception {
        NewCheckpointWizard newCheckpointWizard = new NewCheckpointWizard(getSuggestedName(StringConstants.CHECKPOINT),
                parentFolder);
        NewCheckpointWizardDialog dialog = new NewCheckpointWizardDialog(parentShell, newCheckpointWizard);
        if (dialog.open() != Dialog.OK) {
            return null;
        }
        return newCheckpointWizard.getCheckpoint();
    }

    protected static ProjectEntity getProject() {
        return ProjectController.getInstance().getCurrentProject();
    }

    protected String getSuggestedName(String suggestedName) throws Exception {
        suggestedName = StringUtils.defaultIfBlank(suggestedName, StringConstants.CHECKPOINT);
        return CheckpointController.getInstance().getAvailableName(parentFolder, suggestedName);
    }

    public ITreeEntity findParentSelection() throws Exception {
        ITreeEntity selectedTreeEntity = getFirstSelection();
        if (selectedTreeEntity == null) {
            return null;
        }
        Object entityObject = selectedTreeEntity.getObject();

        if (entityObject instanceof FolderEntity
                && ((FolderEntity) entityObject).getFolderType() == FolderType.CHECKPOINT) {
            return selectedTreeEntity;
        }

        if (entityObject instanceof CheckpointEntity) {
            return selectedTreeEntity.getParent();
        }

        return null;
    }

    public ITreeEntity getFirstSelection() {
        Object o = SelectionServiceSingleton.getInstance()
                .getSelectionService()
                .getSelection(IdConstants.EXPLORER_PART_ID);
        if (o == null || !o.getClass().isArray()) {
            return null;
        }

        Object[] selectedObjects = (Object[]) o;
        if (selectedObjects.length != 1) {
            return null;
        }

        Object selectedObject = selectedObjects[0];
        if (!(selectedObject instanceof ITreeEntity)) {
            return null;
        }

        return (ITreeEntity) selectedObject;
    }

    @Inject
    @Optional
    private void execute(@UIEventTopic(EventConstants.CHECKPOINT_NEW) Object eventData) {
        if (!canExecute()) {
            return;
        }
        execute();
    }

}
