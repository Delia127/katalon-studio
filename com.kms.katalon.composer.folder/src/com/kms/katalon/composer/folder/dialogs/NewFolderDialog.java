package com.kms.katalon.composer.folder.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.NewEntityDialog;
import com.kms.katalon.composer.folder.constants.StringConstants;
import com.kms.katalon.entity.folder.FolderEntity;

public class NewFolderDialog extends NewEntityDialog {

	public NewFolderDialog(Shell parentShell, FolderEntity parentFolder) {
		super(parentShell, parentFolder);
		setDialogTitle(StringConstants.DIA_FOLDER_NEW_TITLE);
		setDialogMsg(StringConstants.DIA_FOLDER_NEW_MSG);
		setFileCreating(false);
	}

}
