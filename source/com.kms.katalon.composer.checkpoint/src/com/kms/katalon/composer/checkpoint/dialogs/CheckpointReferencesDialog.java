package com.kms.katalon.composer.checkpoint.dialogs;

import java.util.List;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityDialog;
import com.kms.katalon.entity.file.FileEntity;

public class CheckpointReferencesDialog extends AbstractDeleteReferredEntityDialog {

    public CheckpointReferencesDialog(Shell parentShell, String checkpointId, List<FileEntity> affectedEntities,
            boolean showYesNoToAllButtons) {
        super(parentShell);
        setDialogTitle(StringConstants.DIA_TITLE_CHECKPOINT_REFERENCES);
        setAffectedEntities(affectedEntities);
        setEntityId(checkpointId);
        setShowYesNoToAllButtons(showYesNoToAllButtons);
    }

}
