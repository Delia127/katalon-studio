package com.kms.katalon.composer.testsuite.editors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.impl.editors.CustomDialogCellEditor;
import com.kms.katalon.composer.testsuite.dialogs.VariableBindingScriptBuilderDialog;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;

public class VariableBindingScriptBuilderEditor extends CustomDialogCellEditor {

    private VariableLink variableLink;

    private TestSuiteTestCaseLink testCaseLink;

    public VariableBindingScriptBuilderEditor(Composite parent, TestSuiteTestCaseLink testCaseLink,
            VariableLink variableLink) {
        super(parent);
        this.testCaseLink = testCaseLink;
        this.variableLink = variableLink;
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        VariableBindingScriptBuilderDialog dialog = new VariableBindingScriptBuilderDialog(cellEditorWindow.getShell(),
                testCaseLink, variableLink);
        if (dialog.open() != Dialog.OK) {
            return null;
        }
        return dialog.getNewValue().getValue();
    }

    @Override
    protected void updateContents(Object value) {
        if (value instanceof String) {
            defaultLabel.setText((String) value);
        }
    }

}
