package com.kms.katalon.composer.objectrepository.view;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.adapter.CComboContentAdapter;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class AddPropertyDialog extends Dialog {

    private String name;

    private String value;

    private String condition;

    private Text txtValue;

    private CCombo ccbName;

    private CCombo ccbConditions;

    private static final String[] commonNames = { "class", "css", "id", "name", "title", "xpath" };

    public AddPropertyDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout gl_container = new GridLayout(2, false);
        gl_container.horizontalSpacing = 15;
        gl_container.verticalSpacing = 7;
        container.setLayout(gl_container);

        Label lblName = new Label(container, SWT.NONE);
        lblName.setText(StringConstants.VIEW_LBL_NAME);

        ccbName = new CCombo(container, SWT.BORDER | SWT.FLAT);
        ccbName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ccbName.setItems(commonNames);
        new AutoCompleteField(ccbName, new CComboContentAdapter(), commonNames);

        Label lblCondition = new Label(container, SWT.NONE);
        lblCondition.setText(StringConstants.VIEW_LBL_MATCH_COND);

        ccbConditions = new CCombo(container, SWT.BORDER | SWT.READ_ONLY);
        ccbConditions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ccbConditions.setItems(WebElementPropertyEntity.MATCH_CONDITION.getTextVlues());
        ccbConditions.select(0);

        Label lblValue = new Label(container, SWT.NONE);
        lblValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblValue.setText(StringConstants.VIEW_LBL_VALUE);

        txtValue = new Text(container, SWT.BORDER);
        txtValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return area;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.VIEW_LBL_ADD_PROPERTY);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(450, 200);
    }

    @Override
    protected void okPressed() {
        name = ccbName.getText();
        value = txtValue.getText();
        condition = ccbConditions.getItem(ccbConditions.getSelectionIndex());
        if (name.trim().equals("")) {
            MessageDialog.openWarning(getParentShell(), StringConstants.WARN_TITLE,
                    StringConstants.VIEW_WARN_MSG_PROPERTY_CANNOT_BE_BLANK);
        } else {
            super.okPressed();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
