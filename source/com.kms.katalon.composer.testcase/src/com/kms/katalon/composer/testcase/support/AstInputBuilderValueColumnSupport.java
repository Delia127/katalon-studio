package com.kms.katalon.composer.testcase.support;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.syntax.Token;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstInputBuilderValueColumnSupport extends EditingSupport {
    protected AstBuilderDialog parentDialog;
    private ClassNode scriptClass;

    public AstInputBuilderValueColumnSupport(ColumnViewer viewer, AstBuilderDialog parentDialog, ClassNode scriptClass) {
        super(viewer);
        this.parentDialog = parentDialog;
        this.scriptClass = scriptClass;
    }

    @Override
    protected void setValue(Object element, Object value) {
        IInputValueType inputValueType = AstTreeTableValueUtil.getTypeValue(element, scriptClass);
        if (inputValueType != null) {
            Object object = inputValueType.changeValue(element, value, scriptClass);
            if (object != null) {
                parentDialog.changeObject(element, object);
                getViewer().refresh();
            }
        }
    }

    @Override
    protected Object getValue(Object element) {
        IInputValueType inputValueType = AstTreeTableValueUtil.getTypeValue(element, scriptClass);
        if (inputValueType != null) {
            return inputValueType.getValueToEdit(element, scriptClass);
        }
        return null;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        IInputValueType inputValueType = AstTreeTableValueUtil.getTypeValue(element, scriptClass);
        if (inputValueType != null) {
            return inputValueType.getCellEditorForValue((Composite) getViewer().getControl(), element, scriptClass);
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        if (element instanceof ASTNode || element instanceof Token) {
            return true;
        }
        return false;
    }

}
