package com.kms.katalon.composer.testcase.support;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.ast.variable.operations.ChangeVariableNameOperation;
import com.kms.katalon.composer.testcase.parts.TableActionOperator;
import com.kms.katalon.entity.variable.VariableEntity;

public class VariableNameEditingSupport extends EditingSupport {
    
    private TableActionOperator variablesPart;
    
    public VariableNameEditingSupport(ColumnViewer viewer, TableActionOperator variablesPart) {
        super(viewer);
        this.variablesPart = variablesPart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return new CustomTextCellEditor((Composite) this.getViewer().getControl());
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof VariableEntity) {
            return ((VariableEntity) element).getName();
        }
        return StringUtils.EMPTY;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element != null && element instanceof VariableEntity && value != null && value instanceof String) {
            VariableEntity param = (VariableEntity) element;
            String newParamName = (String) value;
            if (!newParamName.equals(param.getName())) {
                variablesPart.executeOperation(new ChangeVariableNameOperation(variablesPart, param, newParamName));
            }
        }
    }
    private class CustomTextCellEditor extends TextCellEditor {
        
        public CustomTextCellEditor(Composite parent) {
            super(parent);
        }

        @Override
        public LayoutData getLayoutData() {
            LayoutData result = super.getLayoutData();
            result.minimumHeight =10;
            return result;
        }
    }
}
