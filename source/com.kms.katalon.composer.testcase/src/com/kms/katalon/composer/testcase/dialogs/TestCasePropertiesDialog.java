package com.kms.katalon.composer.testcase.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.CommonPropertiesDialog;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCasePropertiesDialog extends CommonPropertiesDialog {

    private Text txtTag;

    public TestCasePropertiesDialog(Shell parentShell, TestCaseEntity testCase) {
        super(parentShell, testCase);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_CASE_PROPERTIES);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = (Composite) super.createDialogContainer(parent);

        Label lblTag = new Label(container, SWT.NONE);
        lblTag.setText(StringConstants.TAG);

        txtTag = new Text(container, SWT.BORDER);
        txtTag.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        return container;
    }

    @Override
    protected void setInput() {
        super.setInput();
        txtTag.setText(getEntity().getTag());
    }

    @Override
    protected void registerControlModifyListeners() {
        super.registerControlModifyListeners();
        txtTag.addModifyListener(modifyListener);
    }

    @Override
    protected void updateChanges() {
        super.updateChanges();
        getEntity().setTag(txtTag.getText());
    }

    @Override
    public TestCaseEntity getEntity() {
        return (TestCaseEntity) super.getEntity();
    }
}
