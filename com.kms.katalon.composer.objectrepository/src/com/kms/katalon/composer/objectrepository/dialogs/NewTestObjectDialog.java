package com.kms.katalon.composer.objectrepository.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.NewEntityDialog;
import com.kms.katalon.composer.objectrepository.constants.StringConstants;
import com.kms.katalon.entity.folder.FolderEntity;

public class NewTestObjectDialog extends NewEntityDialog {

	public NewTestObjectDialog(Shell parentShell, FolderEntity parentFolder) {
		super(parentShell, parentFolder);
		setDialogTitle(StringConstants.DIA_TITLE_TEST_OBJECT);
		setDialogMsg(StringConstants.DIA_MSG_CREATE_NEW_TEST_OBJECT);
	}

}
