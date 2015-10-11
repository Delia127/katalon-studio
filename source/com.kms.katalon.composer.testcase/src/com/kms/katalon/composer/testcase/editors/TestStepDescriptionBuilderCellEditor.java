package com.kms.katalon.composer.testcase.editors;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.testcase.dialogs.TestStepDescriptionBuilderDialog;

public class TestStepDescriptionBuilderCellEditor extends AbstractDialogCellEditor {

    public TestStepDescriptionBuilderCellEditor(Composite parent) {
        super(parent, null);
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        if (getValue() instanceof String) {
            TestStepDescriptionBuilderDialog dialog = new TestStepDescriptionBuilderDialog(Display.getCurrent().getActiveShell(), (String) getValue());
            if (dialog.open() == Window.OK && dialog.getDescription() != null) {
                return dialog.getDescription();
            }
        }
        return null;
    }
}
