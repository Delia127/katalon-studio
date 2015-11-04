package com.kms.katalon.composer.objectrepository.dialog;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractEntityDialog;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.entity.folder.FolderEntity;

public class NewTestObjectDialog extends AbstractEntityDialog {

	public NewTestObjectDialog(Shell parentShell, FolderEntity parentFolder) {
		super(parentShell, parentFolder);
		setDialogTitle(StringConstants.DIA_TITLE_TEST_OBJECT);
		setDialogMsg(StringConstants.DIA_MSG_CREATE_NEW_TEST_OBJECT);
	}

}
