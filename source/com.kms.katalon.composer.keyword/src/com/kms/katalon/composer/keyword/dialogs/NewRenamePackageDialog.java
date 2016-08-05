package com.kms.katalon.composer.keyword.dialogs;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.dal.exception.InvalidNameException;

@SuppressWarnings("restriction")
public class NewRenamePackageDialog extends CommonAbstractKeywordDialog {

    private IPackageFragmentRoot rootPackage;

    public NewRenamePackageDialog(Shell parentShell, IPackageFragmentRoot rootPackage, boolean isNew) {
        super(parentShell, null);
        setDialogTitle(StringConstants.DIA_TITLE_PACKAGE);
        if (isNew) {
            setDialogMsg(StringConstants.DIA_MSG_NEW_PACKAGE);
        } else {
            setWindowTitle(StringConstants.DIA_TITLE_RENAME);
            setDialogMsg(StringConstants.DIA_MSG_RENAME_PACKAGE);
        }
        setTitleImage(JavaPluginImages.DESC_WIZBAN_NEWPACK.createImage());
        setFileEntity(false);

        this.rootPackage = rootPackage;
    }

    @Override
    public void validateEntityName(String entityName) throws Exception {
        IPackageFragment pkg = rootPackage.getPackageFragment(entityName);
        if (pkg.exists()) {
            throw new InvalidNameException(NAME_EXISTED);
        }
        validatePackageName(entityName, pkg);
    }

}
