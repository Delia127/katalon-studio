package com.kms.katalon.composer.testsuite.collection.part.editor;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.impl.editors.CustomDialogCellEditor;
import com.kms.katalon.composer.testsuite.collection.part.dialog.RunConfigurationSelectionDialog;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;

public class RunConfigurationSelectionCellEditor extends CustomDialogCellEditor {
    private RunConfigurationDescription configuration;
    
    public RunConfigurationSelectionCellEditor(Composite parent) {
        super(parent);
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        RunConfigurationSelectionDialog dialog = new RunConfigurationSelectionDialog(cellEditorWindow.getShell(),
                configuration);
        
        if (dialog.open() != Dialog.OK) {
            return null;
        }
        return dialog.getSelectedConfiguration();
    }
    
    protected void updateContents(Object value) {
       if (value instanceof RunConfigurationDescription) {
           configuration = (RunConfigurationDescription) value;
           defaultLabel.setText(configuration != null ? configuration.getRunConfigurationId() : StringUtils.EMPTY);
       }
    }
}
