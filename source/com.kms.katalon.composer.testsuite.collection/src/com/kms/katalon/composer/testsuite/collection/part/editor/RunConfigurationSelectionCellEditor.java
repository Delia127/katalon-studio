package com.kms.katalon.composer.testsuite.collection.part.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.execution.collection.dialog.RunConfigurationSelectionDialog;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;

public class RunConfigurationSelectionCellEditor extends AbstractDialogCellEditor {
    private RunConfigurationDescription configuration;

    private String defaultContent;

    public RunConfigurationSelectionCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
        this.defaultContent = defaultContent;
    }

    @Override
    protected RunConfigurationDescription openDialogBox(Control cellEditorWindow) {
        RunConfigurationSelectionDialog dialog = new RunConfigurationSelectionDialog(
                Display.getCurrent().getActiveShell(), configuration);

        if (dialog.open() != Dialog.OK) {
            return null;
        }
        return dialog.getSelectedConfiguration();
    }

    protected void updateContents(Object value) {
        if (defaultContent != null) {
            super.updateContents(defaultContent);
        } else {
            super.updateContents(value);
        }
    }
}
