package com.kms.katalon.composer.objectrepository.view;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class AddPropertyDialog extends Dialog {

    private String name;

    private String value;

    private String condition;

    private Text txtValue;

    private ComboViewer cvName;

    private Combo cbbConditions;

    private static final String[] commonNames = { "xpath", "css", "id", "title", "class" };

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

        cvName = new ComboViewer(container, SWT.DROP_DOWN);
        GridData gdTxtName = new GridData(GridData.FILL_HORIZONTAL);
        gdTxtName.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        gdTxtName.verticalAlignment = SWT.FILL;
        cvName.getCombo().setLayoutData(gdTxtName);
        cvName.setContentProvider(ArrayContentProvider.getInstance());
        cvName.setInput(commonNames);
        new AutoCompleteField(cvName.getCombo(), new ComboContentAdapter(), commonNames);

        Label lblCondition = new Label(container, SWT.NONE);
        lblCondition.setText(StringConstants.VIEW_LBL_MATCH_COND);

        cbbConditions = new Combo(container, SWT.READ_ONLY);
        GridData gdCbbConditions = new GridData(GridData.FILL_HORIZONTAL);
        gdCbbConditions.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        cbbConditions.setLayoutData(gdCbbConditions);
        cbbConditions.setItems(WebElementPropertyEntity.MATCH_CONDITION.getTextVlues());
        cbbConditions.select(0);

        Label lblValue = new Label(container, SWT.NONE);
        GridData gd_lblValue = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gd_lblValue.verticalIndent = 5;
        lblValue.setLayoutData(gd_lblValue);
        lblValue.setText(StringConstants.VIEW_LBL_VALUE);

        txtValue = new Text(container, SWT.BORDER);
        GridData gdTxtValue = new GridData(GridData.FILL_HORIZONTAL);
        gdTxtValue.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        txtValue.setLayoutData(gdTxtValue);

        return area;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.VIEW_LBL_ADD_PROPERTY);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(450, 230);
    }

    @Override
    protected void okPressed() {
        name = cvName.getCombo().getText();
        value = txtValue.getText();
        condition = cbbConditions.getItem(cbbConditions.getSelectionIndex());
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
