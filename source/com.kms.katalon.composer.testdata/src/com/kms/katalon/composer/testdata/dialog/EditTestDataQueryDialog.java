package com.kms.katalon.composer.testdata.dialog;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.DatabaseConnectionAbstractDialog;
import com.kms.katalon.core.util.internal.Base64;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class EditTestDataQueryDialog extends DatabaseConnectionAbstractDialog {

    private DataFileEntity testData;

    public EditTestDataQueryDialog(Shell parentShell, DataFileEntity testData) {
        super(parentShell);
        this.testData = testData;
    }

    public DataFileEntity getTestData() {
        return testData;
    }

    @Override
    protected void setInput() {
        showDriverComposite();

        chkGlobalDBSetting.setSelection(testData.isUsingGlobalDBSetting());

        chkSecureUserPassword.setSelection(testData.isSecureUserAccount());

        enableCustomDBConnection(!testData.isUsingGlobalDBSetting());

        txtUser.setText(StringUtils.defaultString(testData.getUser()));

        // password could be null in some database
        // don't try to set null value into Text field
        String plainPassword = Base64.decode(testData.getPassword());
        if (plainPassword != null) {
            txtPassword.setText(plainPassword);
        }

        txtConnectionURL.setText(testData.getDataSourceUrl());
        txtQuery.setText(testData.getQuery());
        if (testData.getDriverClassName() != null){
        	txtDriverClassName.setText(testData.getDriverClassName());
        }  
    }

    @Override
	protected void updateChanges() {
		if (!isChanged()) {
			return;
		}
		testData.setUsingGlobalDBSetting(chkGlobalDBSetting.getSelection());
		testData.setSecureUserAccount(chkSecureUserPassword.getSelection());
		testData.setUser(StringUtils.trimToEmpty(txtUser.getText()));
		testData.setPassword(Base64.encode(txtPassword.getText())); // encrypt
		testData.setDataSourceUrl(StringUtils.trimToEmpty(txtConnectionURL.getText()));
		testData.setQuery(StringUtils.trimToEmpty(txtQuery.getText()));
		testData.setDriverClassName(StringUtils.trimToEmpty(txtDriverClassName.getText()));
	}

}
