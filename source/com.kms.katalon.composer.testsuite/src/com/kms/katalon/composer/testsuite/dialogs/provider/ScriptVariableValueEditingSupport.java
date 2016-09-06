package com.kms.katalon.composer.testsuite.dialogs.provider;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;

import com.kms.katalon.composer.components.impl.support.TypeCheckedEditingSupport;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstValueUtil;
import com.kms.katalon.entity.link.VariableLink;

public class ScriptVariableValueEditingSupport extends TypeCheckedEditingSupport<VariableLink> {

    private ExpressionWrapper expression;
    
    private CellEditor editor;
    
    public ScriptVariableValueEditingSupport(ColumnViewer viewer) {
        super(viewer);
    }

    @Override
    protected Class<VariableLink> getElementType() {
        return VariableLink.class;
    }

    @Override
    protected CellEditor getCellEditorByElement(VariableLink element) {
        editor = null;
        expression = GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(element.getValue());
        if (expression == null) {
            return null;
        }
        InputValueType inputValueType = AstValueUtil.getTypeValue(expression);
        if (inputValueType != null) {
            editor = inputValueType.getCellEditorForValue(getComposite(), expression);
            return editor;
        }
        return null;
    }

    @Override
    protected boolean canEditElement(VariableLink element) {
        return true;
    }

    @Override
    protected Object getElementValue(VariableLink element) {
        InputValueType inputValueType = AstValueUtil.getTypeValue(expression);
        if (inputValueType != null) {
            return inputValueType.getValueToEdit(expression);
        }
        return null;
    }

    @Override
    protected void setElementValue(VariableLink element, Object value) {
        if (value == null) {
            return;
        }
        InputValueType inputValueType = AstValueUtil.getTypeValue(expression);
        if (inputValueType == null) {
            return;
        }
        Object object = inputValueType.changeValue(expression, value);
        if (!(object instanceof ASTNodeWrapper)) {
            return;
        }
        element.setValue(((ASTNodeWrapper) object).getText());
        getViewer().refresh(element);
    }
    
    public CellEditor getEditor() {
        return editor;
    }
}
