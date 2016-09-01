package com.kms.katalon.composer.keyword.dialogs;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.keyword.constants.StringConstants;

public class RenameKeywordDialog extends CommonAbstractKeywordDialog {

    private IPackageFragment parentPackage;

    public RenameKeywordDialog(Shell parentShell, IPackageFragment parentPackage) {
        super(parentShell, null);
        setWindowTitle(StringConstants.DIA_TITLE_RENAME);
        setDialogTitle(StringConstants.DIA_TITLE_KEYWORD);
        setDialogMsg(StringConstants.DIA_MSG_RENAME_KEYWORD);
        this.parentPackage = parentPackage;
    }

    @Override
    public void validateEntityName(String entityName) throws Exception {
        validateKeywordName(entityName, parentPackage);
    }

}
