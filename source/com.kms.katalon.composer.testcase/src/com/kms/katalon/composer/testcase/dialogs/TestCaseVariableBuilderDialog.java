package com.kms.katalon.composer.testcase.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.VariableBuilderDialog;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestCaseVariableBuilderDialog extends VariableBuilderDialog {
    private VariableEntity param;

    public TestCaseVariableBuilderDialog(Shell parentShell) {
        super(parentShell, DialogType.NEW);
    }

    public TestCaseVariableBuilderDialog(Shell parentShell, VariableEntity param) {
        super(parentShell, DialogType.EDIT);
        this.param = param;
    }

    @Override
    protected void okPressed() {
        if (param == null) {
            param = new VariableEntity();
        }
        param.setName(textVariableName.getText());
        param.setDefaultValue(textDefaultValue.getText());
        super.okPressed();
    }

    @Override
    public void create() {
        super.create();
        setInput();
    }

    private void setInput() {
        if (param != null) {
            textVariableName.setText(param.getName());
            textDefaultValue.setText(param.getDefaultValue());
        }
    }

    public VariableEntity getParam() {
        return param;
    }
}
