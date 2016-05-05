package com.kms.katalon.composer.objectrepository.dialog;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.CommonNewEntityDialog;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.entity.folder.FolderEntity;

public class NewTestObjectDialog extends CommonNewEntityDialog {

    public NewTestObjectDialog(Shell parentShell, FolderEntity parentFolder, String suggestedName) {
        super(parentShell, parentFolder, suggestedName);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_OBJECT);
        setDialogMsg(StringConstants.DIA_MSG_CREATE_NEW_TEST_OBJECT);
    }

}
