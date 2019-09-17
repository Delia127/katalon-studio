package com.kms.katalon.composer.windows.action;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueEditorProvider;

public class WindowsActionParamValueType {
    private InputValueEditorProvider editorProvider;

    private String paramName;

    private Object value;

    private WindowsActionParamValueType() {
        // Disable default constructor
    }

    public InputValueEditorProvider getEditorProvider() {
        return editorProvider;
    }

    public void setEditorProvider(InputValueEditorProvider valueType) {
        this.editorProvider = valueType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getParamName() {
        return paramName;
    }

    public static WindowsActionParamValueType newInstance(InputValueEditorProvider editorProvider, String paramName) {
        WindowsActionParamValueType paramType = new WindowsActionParamValueType();
        paramType.setEditorProvider(editorProvider);
        paramType.setValue(editorProvider.newValue());
        paramType.paramName = paramName;
        return paramType;
    }

    public static WindowsActionParamValueType newInstance(InputValueEditorProvider editorProvider, String paramName,
            Object value) {
        WindowsActionParamValueType paramType = new WindowsActionParamValueType();
        paramType.setEditorProvider(editorProvider);
        paramType.setValue(value);
        paramType.paramName = paramName;
        return paramType;
    }

    public ExpressionWrapper toExpressionWrapper() {
        ASTNodeWrapper rawWrapper = editorProvider.toASTNodeWrapper(value);
        return (rawWrapper instanceof ASTNodeWrapper) ? (ExpressionWrapper) rawWrapper : null;
    }

    public String getValueToDisplay() {
        return editorProvider.getValueToDisplay(value);
    }

    public Object getValueToEdit() {
        return editorProvider.getValueToEdit(value);
    }
}
