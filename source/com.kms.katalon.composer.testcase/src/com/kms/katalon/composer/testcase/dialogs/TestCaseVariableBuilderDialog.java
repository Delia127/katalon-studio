package com.kms.katalon.composer.testcase.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.VariableBuilderDialog;
import com.kms.katalon.composer.components.impl.model.VariableDialogModel;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestCaseVariableBuilderDialog extends VariableBuilderDialog {
    private VariableEntity param;

    public TestCaseVariableBuilderDialog(Shell parentShell) {
        super(parentShell, DialogType.NEW, null);
    }

    public TestCaseVariableBuilderDialog(Shell parentShell, VariableEntity param) {
        super(parentShell, DialogType.EDIT, new VariableDialogModel(param.getName(), param.getDefaultValue()));
        this.param = param;
    }

    @Override
    protected void okPressed() {
        super.okPressed();
        if (param == null) {
            param = new VariableEntity();
        }
        param.setName(getVariable().getName());
        param.setDefaultValue(getVariable().getValue());
    }

    public VariableEntity getParam() {
        return param;
    }
}
