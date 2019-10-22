package com.kms.katalon.composer.checkpoint.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.DatabaseConnectionAbstractDialog;
import com.kms.katalon.core.util.internal.Base64;
import com.kms.katalon.entity.checkpoint.DatabaseCheckpointSourceInfo;

public class EditCheckpointDatabaseDialog extends DatabaseConnectionAbstractDialog {

    private DatabaseCheckpointSourceInfo sourceInfo;

    public EditCheckpointDatabaseDialog(Shell parentShell, DatabaseCheckpointSourceInfo sourceInfo) {
        super(parentShell);
        this.sourceInfo = sourceInfo;
    }

    public DatabaseCheckpointSourceInfo getSourceInfo() {
        return sourceInfo;
    }

    @Override
    protected void setInput() {
        showDriverComposite();

        chkGlobalDBSetting.setSelection(sourceInfo.isUsingGlobalDBSetting());

        chkSecureUserPassword.setSelection(sourceInfo.isSecureUserAccount());

        enableCustomDBConnection(!sourceInfo.isUsingGlobalDBSetting());

        txtUser.setText(StringUtils.defaultString(sourceInfo.getUser()));

        // password could be null in some database
        // don't try to set null value into Text field
        String plainPassword = Base64.decode(sourceInfo.getPassword());
        if (plainPassword != null) {
            txtPassword.setText(plainPassword);
        }

        txtConnectionURL.setText(sourceInfo.getSourceUrl());
        txtQuery.setText(sourceInfo.getQuery());
        txtDriverClassName.setText(StringUtils.defaultString(sourceInfo.getDriverClassName()));
    }

    @Override
    protected void updateChanges() {
        if (!isChanged()) {
            return;
        }
        sourceInfo.setUsingGlobalDBSetting(chkGlobalDBSetting.getSelection());
        sourceInfo.setSecureUserAccount(chkSecureUserPassword.getSelection());
        sourceInfo.setUser(StringUtils.trimToEmpty(txtUser.getText()));
        sourceInfo.setPassword(Base64.encode(txtPassword.getText())); // encrypt
        sourceInfo.setSourceUrl(StringUtils.trimToEmpty(txtConnectionURL.getText()));
        sourceInfo.setQuery(StringUtils.trimToEmpty(txtQuery.getText()));
    }

}
