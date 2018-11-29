package com.kms.katalon.composer.testcase.support;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.ast.variable.operations.ChangeVariableDefaultValueOperation;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.composer.testcase.parts.TableActionOperator;
import com.kms.katalon.composer.testcase.util.AstValueUtil;
import com.kms.katalon.entity.variable.VariableEntity;

public class VariableDefaultValueEditingSupport extends EditingSupport {
    private TableActionOperator variablesPart;
    
    private ExpressionWrapper expression;
    
    private ITestCasePart testCasePart;

    public VariableDefaultValueEditingSupport(ColumnViewer viewer, TableActionOperator variablesPart) {
        this(viewer, variablesPart, null);
    }
    public VariableDefaultValueEditingSupport(ColumnViewer viewer, TableActionOperator variablesPart, ITestCasePart testCasePart) {
        super(viewer);
        this.variablesPart = variablesPart;
        this.testCasePart = testCasePart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        expression = GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(((VariableEntity) element).getDefaultValue());
        if (expression == null) {
            return null;
        }
        InputValueType inputValueType = AstValueUtil.getTypeValue(expression);
        if (inputValueType != null) {
            return inputValueType.getCellEditorForValue((Composite) getViewer().getControl(), expression, testCasePart);
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element instanceof VariableEntity);
    }

    @Override
    protected Object getValue(Object element) {
        InputValueType inputValueType = AstValueUtil.getTypeValue(expression);
        if (inputValueType != null) {
            return inputValueType.getValueToEdit(expression);
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value) {
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
        VariableEntity variableEntity = (VariableEntity) element;
        ASTNodeWrapper newAstNode = (ASTNodeWrapper) object;
        StringBuilder stringBuilder = new StringBuilder();
        GroovyWrapperParser groovyParser = new GroovyWrapperParser(stringBuilder);
        groovyParser.parse(newAstNode);
        if (!StringUtils.equals(variableEntity.getDefaultValue(), stringBuilder.toString())) {
            variablesPart.executeOperation(new ChangeVariableDefaultValueOperation(variablesPart, variableEntity,
                    stringBuilder.toString()));
        }
    }
}
