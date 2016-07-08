package com.kms.katalon.composer.checkpoint.handlers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.components.dialogs.CWizardDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.CheckpointTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.wizard.RenameWizard;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.exception.NullAttributeException;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;

public class RenameCheckpointHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell parentShell;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAME_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (!(object instanceof CheckpointTreeEntity)) {
                    return;
                }
                execute((CheckpointTreeEntity) object);
            }
        });
    }

    private void execute(CheckpointTreeEntity checkpointTreeEntity) {
        try {
            CheckpointEntity checkpoint = checkpointTreeEntity.getObject();
            if (checkpoint == null) {
                throw new NullAttributeException(StringConstants.HAND_EXC_CHECKPOINT_ENTITY_IS_NULL);
            }

            List<String> existingNames = FolderController.getInstance().getChildrenNames(checkpoint.getParentFolder());
            RenameWizard renameWizard = new RenameWizard(checkpointTreeEntity, existingNames);
            CWizardDialog wizardDialog = new CWizardDialog(parentShell, renameWizard);

            if (wizardDialog.open() != Window.OK) {
                return;
            }

            String oldName = checkpoint.getName();
            String newName = renameWizard.getNewNameValue();
            String id = checkpoint.getId();
            String oldDisplayedId = checkpoint.getIdForDisplay();

            if (StringUtils.isBlank(newName) || StringUtils.equals(newName, oldName)) {
                return;
            }

            try {
                // Update new name
                checkpoint.setName(newName);
                CheckpointController.getInstance().update(checkpoint);
                partService.saveAll(false);
                eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, new Object[] { oldDisplayedId,
                        checkpoint.getIdForDisplay() });
                eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, checkpointTreeEntity.getParent());
                eventBroker.post(EventConstants.CHECKPOINT_UPDATED, new Object[] { id, checkpoint });
            } catch (DALException e) {
                // Rollback old name if involved into any error
                checkpoint.setName(oldName);
                throw e;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.ERROR,
                    StringConstants.HAND_MSG_UNABLE_TO_RENAME_CHECKPOINT);
        }
    }
}
