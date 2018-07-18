package com.kms.katalon.composer.testcase.support;

import org.eclipse.jface.viewers.ColumnViewer;

import com.kms.katalon.composer.testcase.ast.variable.operations.ChangeVariableDefaultValueOperation;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.parts.TableActionOperator;
import com.kms.katalon.composer.testcase.util.AstValueUtil;
import com.kms.katalon.entity.variable.VariableEntity;

public class VariableDefaultValueTypeEditingSupport extends AstInputBuilderValueTypeColumnSupport {
    private TableActionOperator variablesPart;

    private ExpressionWrapper expression;

    public VariableDefaultValueTypeEditingSupport(ColumnViewer viewer, TableActionOperator variablesPart,
            InputValueType[] defaultInputValueTypes) {
        super(viewer, defaultInputValueTypes);
        this.variablesPart = variablesPart;
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element instanceof VariableEntity);
    }

    @Override
    protected Object getValue(Object element) {
        expression = GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(((VariableEntity) element).getDefaultValue());
        if (expression == null) {
            return 0;
        }
        return super.getValue(GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(((VariableEntity) element).getDefaultValue()));
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
        StringBuilder stringBuilder = new StringBuilder();
        GroovyWrapperParser groovyParser = new GroovyWrapperParser(stringBuilder);
        groovyParser.parse(newAstNode);
        variablesPart.executeOperation(new ChangeVariableDefaultValueOperation(variablesPart, (VariableEntity) element,
                stringBuilder.toString()));
    }

}
