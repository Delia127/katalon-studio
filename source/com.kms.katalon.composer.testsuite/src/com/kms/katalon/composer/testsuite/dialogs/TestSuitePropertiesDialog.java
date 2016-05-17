package com.kms.katalon.composer.testsuite.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.CommonPropertiesDialog;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuitePropertiesDialog extends CommonPropertiesDialog {

    private Text txtCreatedDate;

    private Text txtModifiedDate;

    public TestSuitePropertiesDialog(Shell parentShell, TestSuiteEntity entity) {
        super(parentShell, entity);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_SUITE_PROPERTIES);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = (Composite) super.createDialogContainer(parent);

        Label lblCreatedDate = new Label(container, SWT.NONE);
        lblCreatedDate.setText(StringConstants.PA_LBL_CREATED_DATE);

        txtCreatedDate = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        txtCreatedDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblModifiedDate = new Label(container, SWT.NONE);
        lblModifiedDate.setText(StringConstants.PA_LBL_LAST_UPDATED);

        txtModifiedDate = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        txtModifiedDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        return container;
    }

    @Override
    protected void setInput() {
        super.setInput();
        txtCreatedDate.setText(getEntity().getDateCreated().toString());
        txtModifiedDate.setText(getEntity().getDateModified().toString());
    }

    @Override
    public TestSuiteEntity getEntity() {
        return (TestSuiteEntity) super.getEntity();
    }

}
