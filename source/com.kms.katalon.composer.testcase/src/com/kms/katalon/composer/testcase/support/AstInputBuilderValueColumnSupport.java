package com.kms.katalon.composer.testcase.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstInputBuilderValueColumnSupport extends EditingSupport {
    protected AstBuilderDialog parentDialog;
    protected InputValueType inputValueType;

    public AstInputBuilderValueColumnSupport(ColumnViewer viewer, AstBuilderDialog parentDialog) {
        super(viewer);
        this.parentDialog = parentDialog;
    }

    @Override
    protected void setValue(Object element, Object value) {
        Object object = inputValueType.changeValue(element, value);
        if (object == null || !(object instanceof ASTNodeWrapper)) {
            return;
        }
        if (!object.equals(element)) {
            ASTNodeWrapper oldAstNode = (ASTNodeWrapper) element;
            ASTNodeWrapper newAstNode = (ASTNodeWrapper) object;
            newAstNode.copyProperties(oldAstNode);
            parentDialog.replaceObject(oldAstNode, newAstNode);
        }
        getViewer().refresh();
    }

    @Override
    protected Object getValue(Object element) {
        return inputValueType.getValueToEdit(element);
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return inputValueType.getCellEditorForValue((Composite) getViewer().getControl(), element);
    }

    @Override
    protected boolean canEdit(Object element) {
        if (element instanceof ASTNodeWrapper) {
            inputValueType = AstTreeTableValueUtil.getTypeValue((ASTNodeWrapper) element);
            return inputValueType != null;
        }
        return false;
    }

}
