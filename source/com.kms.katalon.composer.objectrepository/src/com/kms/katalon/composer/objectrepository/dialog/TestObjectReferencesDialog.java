package com.kms.katalon.composer.objectrepository.dialog;

import java.util.List;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityDialog;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.entity.file.FileEntity;

public class TestObjectReferencesDialog extends AbstractDeleteReferredEntityDialog {

    public TestObjectReferencesDialog(Shell parentShell, String testObjectId, List<FileEntity> affectedEntities,
            boolean showYesNoToAllButtons) {
        super(parentShell);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_OBJECT_REFERENCES);
        setAffectedEntities(affectedEntities);
        setEntityId(testObjectId);
        setShowYesNoToAllButtons(showYesNoToAllButtons);
    }
}
