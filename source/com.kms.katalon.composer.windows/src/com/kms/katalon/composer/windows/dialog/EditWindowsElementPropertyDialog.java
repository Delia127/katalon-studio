package com.kms.katalon.composer.windows.dialog;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class EditWindowsElementPropertyDialog extends AbstractDialog {

    private WebElementPropertyEntity editingProperty;

    private Text txtName;

    private Text txtValue;

    public EditWindowsElementPropertyDialog(Shell parentShell, WebElementPropertyEntity editingProperty) {
        super(parentShell);
        this.editingProperty = editingProperty;
    }

    @Override
    protected void registerControlModifyListeners() {
        txtName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                getButton(OK).setEnabled(canPressOk());
            }
        });
    }

    @Override
    protected void setInput() {
        txtName.setText(StringUtils.defaultString(editingProperty.getName()));
        txtValue.setText(StringUtils.defaultString(editingProperty.getValue()));

        getButton(OK).setEnabled(canPressOk());
    }

    public boolean canPressOk() {
        return StringUtils.isNotEmpty(txtName.getText());
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.verticalSpacing = 7;
        gridLayout.horizontalSpacing = 15;
        composite.setLayout(gridLayout);

        Label lblName = new Label(composite, SWT.NONE);
        lblName.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        lblName.setText("Name");

        txtName = new Text(composite, SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Label lblValue = new Label(composite, SWT.NONE);
        lblValue.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        lblValue.setText("Value");

        txtValue = new Text(composite, SWT.BORDER);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.heightHint = 100;
        txtValue.setLayoutData(gridData);

        return composite;
    }

    @Override
    protected void okPressed() {
        editingProperty.setName(txtName.getText());
        editingProperty.setValue(txtValue.getText());
        super.okPressed();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, super.getInitialSize().y);
    }

    @Override
    protected int getShellStyle() {
        return SWT.RESIZE | super.getShellStyle();
    }

    public WebElementPropertyEntity getProperty() {
        return editingProperty;
    }

    @Override
    public String getDialogTitle() {
        return "Edit Property";
    }
}
