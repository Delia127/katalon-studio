package com.kms.katalon.composer.testcase.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstValueUtil;

public class AstInputBuilderValueTypeColumnSupport extends EditingSupport {
    protected InputValueType[] inputValueTypes;

    protected String[] readableValueTypeNames;

    protected String customTag;

    public AstInputBuilderValueTypeColumnSupport(ColumnViewer viewer, InputValueType[] defaultInputValueTypes) {
        super(viewer);
        inputValueTypes = defaultInputValueTypes;
        initReadableValueTypeNamesList();
    }

    protected void initReadableValueTypeNamesList() {
        readableValueTypeNames = new String[inputValueTypes.length];
        for (int i = 0; i < inputValueTypes.length; i++) {
            readableValueTypeNames[i] = TreeEntityUtil.getReadableKeywordName(inputValueTypes[i].getName());
        }
    }
    
    protected AstInputBuilderValueTypeColumnSupport(ColumnViewer viewer) {
        super(viewer);
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (!(value instanceof Integer) || (int) value < 0 || (int) value >= inputValueTypes.length) {
            return;
        }
        ASTNodeWrapper oldAstNode = (ASTNodeWrapper) element;
        ASTNodeWrapper parentNode = oldAstNode.getParent();
        if (parentNode == null) {
            return;
        }
        
        ASTNodeWrapper newAstNode = getNewAstNode(value, oldAstNode);
        if (newAstNode == null) {
            return;
        }
        newAstNode.copyProperties(oldAstNode);
        if (parentNode.replaceChild(oldAstNode, newAstNode)) {
            getViewer().refresh();
        }
    }

    protected ASTNodeWrapper getNewAstNode(Object value, ASTNodeWrapper oldAstNode) {
        InputValueType newValueType = inputValueTypes[(int) value];
        InputValueType oldValueType = AstValueUtil.getTypeValue(oldAstNode);
        if (newValueType == oldValueType) {
            return null;
        }
        return (ASTNodeWrapper) newValueType.getNewValue(oldAstNode.getParent());
    }

    @Override
    protected Object getValue(Object element) {
        InputValueType valueType = AstValueUtil.getTypeValue((ASTNodeWrapper) element);
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
