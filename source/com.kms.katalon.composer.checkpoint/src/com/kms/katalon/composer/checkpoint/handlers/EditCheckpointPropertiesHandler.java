package com.kms.katalon.composer.checkpoint.handlers;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.checkpoint.dialogs.CheckpointPropertiesDialog;
import com.kms.katalon.composer.components.impl.handler.CommonEditPropertiesHandler;
import com.kms.katalon.composer.components.impl.tree.CheckpointTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;

public class EditCheckpointPropertiesHandler extends CommonEditPropertiesHandler<CheckpointTreeEntity> {

    private static EditCheckpointPropertiesHandler instance;

    public static EditCheckpointPropertiesHandler getInstance() {
        if (instance == null) {
            instance = new EditCheckpointPropertiesHandler();
        }
        return instance;
    }

    @Override
    protected Class<CheckpointTreeEntity> getTreeEntityClass() {
        return CheckpointTreeEntity.class;
    }

    @Override
    public boolean canExecute() {
        return getSingleSelection() != null;
    }

    @Override
    public void execute() {
        CheckpointTreeEntity selectedObject = getSingleSelection();
        if (selectedObject == null) {
            return;
        }
        try {
            CheckpointEntity checkpoint = selectedObject.getObject();
            CheckpointPropertiesDialog dialog = new CheckpointPropertiesDialog(Display.getCurrent().getActiveShell(),
                    checkpoint);
            if (dialog.open() != Window.OK || !dialog.isModified()) {
                return;
            }

            CheckpointController.getInstance().update(checkpoint);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
