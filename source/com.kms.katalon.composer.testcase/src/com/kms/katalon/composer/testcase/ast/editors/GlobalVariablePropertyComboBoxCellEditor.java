package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;

public class GlobalVariablePropertyComboBoxCellEditor extends PropertyComboBoxCellEditor {  
    public GlobalVariablePropertyComboBoxCellEditor(Composite parent) throws Exception {
        super(parent, GlobalVariableController.getInstance().getAllGlobalVariableNames(
                ProjectController.getInstance().getCurrentProject()));
    }
    
    public void applyEditingValue() {
        fireApplyEditorValue();
    }
}
