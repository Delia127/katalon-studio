package com.kms.katalon.composer.keyword.dialogs;

import javax.naming.InvalidNameException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractEntityDialog;
import com.kms.katalon.composer.keyword.constants.StringConstants;

@SuppressWarnings("restriction")
public class NewRenamePackageDialog extends AbstractEntityDialog {

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
            throw new InvalidNameException(
                    com.kms.katalon.composer.components.impl.constants.StringConstants.DIA_NAME_EXISTED);
        }

        IStatus status = JavaConventionsUtil.validatePackageName(entityName, pkg);
        if (!status.isOK()) {
            throw new InvalidNameException(status.getMessage().replaceAll(" Java", ""));
        }
    }

}
