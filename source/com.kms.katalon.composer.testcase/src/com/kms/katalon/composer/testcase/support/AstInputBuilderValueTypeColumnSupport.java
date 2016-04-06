package com.kms.katalon.composer.testcase.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstInputBuilderValueTypeColumnSupport extends EditingSupport {
    protected AstBuilderDialog parentDialog;

    protected InputValueType[] inputValueTypes;

    protected String[] readableValueTypeNames;

    protected String customTag;
    
    public AstInputBuilderValueTypeColumnSupport(ColumnViewer viewer, InputValueType[] defaultInputValueTypes) {
        super(viewer);
        inputValueTypes = defaultInputValueTypes;
        readableValueTypeNames = new String[defaultInputValueTypes.length];
        for (int i = 0; i < defaultInputValueTypes.length; i++) {
            readableValueTypeNames[i] = TreeEntityUtil.getReadableKeywordName(defaultInputValueTypes[i].getName());
        }
    }

    public AstInputBuilderValueTypeColumnSupport(ColumnViewer viewer, InputValueType[] defaultInputValueTypes,
            AstBuilderDialog parentDialog) {
        this(viewer, defaultInputValueTypes);
        this.parentDialog = parentDialog;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (!(value instanceof Integer) || (int) value < 0 || (int) value >= inputValueTypes.length) {
            return;
        }
        ASTNodeWrapper oldAstNode = (ASTNodeWrapper) element;
        InputValueType newValueType = inputValueTypes[(int) value];
        InputValueType oldValueType = AstTreeTableValueUtil.getTypeValue(oldAstNode);
        if (newValueType == oldValueType) {
            return;
        }
        ASTNodeWrapper newAstNode = (ASTNodeWrapper) newValueType.getNewValue(oldAstNode.getParent());
        if (newAstNode == null) {
            return;
        }
        newAstNode.copyProperties(oldAstNode);
        newAstNode.setParent(oldAstNode.getParent());
        if (parentDialog != null) {
            parentDialog.replaceObject(oldAstNode, newAstNode);
        }
        getViewer().refresh();
    }

    @Override
    protected Object getValue(Object element) {
        InputValueType valueType = AstTreeTableValueUtil.getTypeValue((ASTNodeWrapper) element);
        if (valueType == null) {
            return 0;
        }
        for (int index = 0; index < inputValueTypes.length; index++) {
            if (valueType.equals(inputValueTypes[index])) {
                return index;
            }
        }
        return 0;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return new ComboBoxCellEditor((Composite) getViewer().getControl(), readableValueTypeNames);
    }

    @Override
    protected boolean canEdit(Object element) {
        if (element instanceof ASTNodeWrapper) {
            return true;
        }
        return false;
    }

}
