package com.kms.katalon.composer.testcase.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractEntityDialog;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.entity.folder.FolderEntity;

public class NewTestCaseDialog extends AbstractEntityDialog {

	public NewTestCaseDialog(Shell parentShell, FolderEntity parentFolder) {
		super(parentShell, parentFolder);
		setDialogTitle(StringConstants.DIA_TITLE_TEST_CASE);
		setDialogMsg(StringConstants.DIA_MSG_CREATE_NEW_TEST_CASE);
	}

}
