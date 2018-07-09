package com.kms.katalon.composer.testsuite.support;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.StructuredSelection;

import com.kms.katalon.composer.components.impl.support.TypeCheckedEditingSupport;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testsuite.parts.TestSuitePartDataBindingView;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;

public class VariableTypeEditingSupport extends TypeCheckedEditingSupport<VariableLink> {
    private static final List<String> variableTypes = VariableType.getValueStrings();

    private TestSuitePartDataBindingView mpart;

    public VariableTypeEditingSupport(ColumnViewer viewer, TestSuitePartDataBindingView mpart) {
        super(viewer);
        this.mpart = mpart;
    }

    @Override
    protected Class<VariableLink> getElementType() {
        return VariableLink.class;
    }

    @Override
    protected CellEditor getCellEditorByElement(VariableLink element) {
        return new ComboBoxCellEditor(getComposite(), variableTypes.toArray(new String[0]));
    }

    @Override
    protected boolean canEditElement(VariableLink element) {
        return true;
    }

    @Override
    protected Object getElementValue(VariableLink element) {
        return variableTypes.indexOf(element.getType().toString());
    }

    @Override
    protected void setElementValue(VariableLink variableLink, Object value) {
        if (!(value instanceof Integer)) {
            return;
        }

        int selectedIndex = (int) value;

        if (selectedIndex < 0) {
            return;
        }
        VariableType variableType = VariableType.fromValue(variableTypes.get(selectedIndex));
        if (variableType == variableLink.getType()) {
            return;
        }

        switch (variableType) {
            case DATA_COLUMN:
            case DATA_COLUMN_INDEX:
                variableLink.setType(variableType);
                variableLink.setValue(StringUtils.EMPTY);
            case DEFAULT:
                variableLink.setTestDataLinkId(StringUtils.EMPTY);
                variableLink.setValue(StringUtils.EMPTY);
                break;
            case SCRIPT_VARIABLE:
                variableLink.setTestDataLinkId(StringUtils.EMPTY);
                Object newValue = InputValueType.Null.newValue();
                variableLink.setValue(((ASTNodeWrapper) newValue).getInputText());
                break;
        }
        variableLink.setType(variableType);
        getViewer().update(variableLink, null);
        getViewer().setSelection(new StructuredSelection(variableLink));
        mpart.setDirty(true);
    }

}
