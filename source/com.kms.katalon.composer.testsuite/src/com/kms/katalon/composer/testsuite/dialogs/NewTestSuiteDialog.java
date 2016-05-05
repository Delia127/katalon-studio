package com.kms.katalon.composer.testsuite.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.CommonNewEntityDialog;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.entity.folder.FolderEntity;

public class NewTestSuiteDialog extends CommonNewEntityDialog {

    public NewTestSuiteDialog(Shell parentShell, FolderEntity parentFolder, String suggestedName) {
        super(parentShell, parentFolder, suggestedName);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_SUITE);
        setDialogMsg(StringConstants.DIA_MSG_CREATE_NEW_TEST_SUITE);
    }

}
