package com.kms.katalon.composer.testcase.support;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstInputBuilderValueTypeColumnSupport extends EditingSupport {
    protected AstBuilderDialog parentDialog;
    protected ClassNode scriptClass;
    protected List<String> inputValueTypeNames;
    protected String customTag;
    protected IInputValueType[] defaultInputValueTypes;

    public AstInputBuilderValueTypeColumnSupport(ColumnViewer viewer, IInputValueType[] defaultInputValueTypes, String customTag, AstBuilderDialog parentDialog,
            ClassNode scriptClass) {
        super(viewer);
        this.defaultInputValueTypes = defaultInputValueTypes;
        this.customTag = customTag;
        this.parentDialog = parentDialog;
        this.scriptClass = scriptClass;
        inputValueTypeNames = new ArrayList<String>();
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof ASTNode && value instanceof Integer && (int) value > -1
                && (int) value < inputValueTypeNames.size()) {
            String newValueTypeString = inputValueTypeNames.get((int) value);
            IInputValueType newValueType = AstTreeTableInputUtil.getInputValueTypeFromString(newValueTypeString);
            IInputValueType oldValueType = AstTreeTableValueUtil.getTypeValue((ASTNode) element, scriptClass);
            if (newValueType != oldValueType) {
                ASTNode astNode = (ASTNode) newValueType.getNewValue(element);
                parentDialog.changeObject(element, astNode);
                getViewer().refresh();
            }
        }
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof ASTNode) {
            IInputValueType valueType = AstTreeTableValueUtil.getTypeValue((ASTNode) element, scriptClass);
            if (valueType != null) {
                return inputValueTypeNames.indexOf(valueType.getName());
            }
        }
        return 0;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        inputValueTypeNames.clear();
        inputValueTypeNames.addAll(AstTreeTableInputUtil.getInputValueTypeStringList(defaultInputValueTypes, customTag));
        return new ComboBoxCellEditor((Composite) getViewer().getControl(),
                inputValueTypeNames.toArray(new String[inputValueTypeNames.size()]));
    }

    @Override
    protected boolean canEdit(Object element) {
        if (element instanceof ASTNode) {
            return true;
        }
        return false;
    }

}
