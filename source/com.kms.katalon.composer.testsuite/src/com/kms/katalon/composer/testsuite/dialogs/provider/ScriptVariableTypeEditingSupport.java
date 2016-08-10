package com.kms.katalon.composer.testsuite.dialogs.provider;

import org.eclipse.jface.viewers.ColumnViewer;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;
import com.kms.katalon.composer.testcase.util.AstValueUtil;
import com.kms.katalon.entity.link.VariableLink;

public class ScriptVariableTypeEditingSupport extends AstInputBuilderValueTypeColumnSupport {

    private ExpressionWrapper expression;

    public ScriptVariableTypeEditingSupport(ColumnViewer viewer) {
        super(viewer, AstInputValueTypeOptionsProvider.getInputValueTypeOptions(InputValueType.Variable));
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element instanceof VariableLink);
    }

    @Override
    protected Object getValue(Object element) {
        expression = GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(((VariableLink) element).getValue());
        return super.getValue(expression);
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (!(value instanceof Integer) || (int) value < 0 || (int) value >= inputValueTypes.length) {
            return;
        }
        InputValueType newValueType = inputValueTypes[(int) value];
        InputValueType oldValueType = AstValueUtil.getTypeValue(expression);
        if (newValueType == oldValueType) {
            return;
        }
        ASTNodeWrapper newAstNode = (ASTNodeWrapper) newValueType.getNewValue(expression != null
                ? expression.getParent() : null);
        if (newAstNode == null) {
            return;
        }
        ((VariableLink) element).setValue(newAstNode.getText());
        getViewer().refresh(element);
    }

}
