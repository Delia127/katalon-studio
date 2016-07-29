package com.kms.katalon.composer.testdata.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.CommonPropertiesDialog;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class TestDataPropertiesDialog extends CommonPropertiesDialog<DataFileEntity> {

    private Text txtDataType;

    public TestDataPropertiesDialog(Shell parentShell, DataFileEntity testData) {
        super(parentShell, testData);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_DATA_PROPERTIES);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = (Composite) super.createDialogContainer(parent);

        Label lblDataType = new Label(container, SWT.NONE);
        lblDataType.setText(StringConstants.DIA_LBL_DATA_TYPE);

        txtDataType = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        txtDataType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        return container;
    }

    @Override
    protected void setInput() {
        super.setInput();
        txtDataType.setText(getEntity().getDriver().toString());
    }

}
