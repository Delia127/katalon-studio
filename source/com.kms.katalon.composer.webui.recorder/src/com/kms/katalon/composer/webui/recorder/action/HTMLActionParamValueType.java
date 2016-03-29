package com.kms.katalon.composer.webui.recorder.action;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueEditorProvider;

public class HTMLActionParamValueType {
    private InputValueEditorProvider editorProvider;
    private Object value;
    
    private HTMLActionParamValueType() {
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
    
    public static HTMLActionParamValueType newInstance(InputValueEditorProvider editorProvider) {
        HTMLActionParamValueType paramType = new HTMLActionParamValueType();
        paramType.setEditorProvider(editorProvider);
        paramType.setValue(editorProvider.newValue());
        
        return paramType;
    }
    
    public static HTMLActionParamValueType newInstance(InputValueEditorProvider editorProvider, Object value) {
        HTMLActionParamValueType paramType = new HTMLActionParamValueType();
        paramType.setEditorProvider(editorProvider);
        paramType.setValue(value);
        
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
