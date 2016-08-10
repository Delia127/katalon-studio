package com.kms.katalon.composer.checkpoint.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;

import com.kms.katalon.composer.checkpoint.dialogs.NewCheckpointDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class NewCheckpointFromTestDataHandler extends NewCheckpointHandler {

    private static NewCheckpointFromTestDataHandler instance;

    public static NewCheckpointFromTestDataHandler getInstance() {
        if (instance == null) {
            instance = new NewCheckpointFromTestDataHandler();
        }
        return instance;
    }

    @CanExecute
    public boolean canExecute() {
        return getProject() != null && getFirstSelection() instanceof TestDataTreeEntity;
    }

    @Override
    protected NewCheckpointDialog getNewCheckpointDialog() throws Exception {
        DataFileEntity testdata = (DataFileEntity) getFirstSelection().getObject();
        return new NewCheckpointDialog(parentShell, testdata, getSuggestedName(testdata.getName()));
    }

    @Override
    public ITreeEntity findParentSelection() throws Exception {
        // return Checkpoint root folder
        FolderEntity checkpointEntity = FolderController.getInstance().getCheckpointRoot(getProject());
        return new FolderTreeEntity(checkpointEntity, null);
    }

}
