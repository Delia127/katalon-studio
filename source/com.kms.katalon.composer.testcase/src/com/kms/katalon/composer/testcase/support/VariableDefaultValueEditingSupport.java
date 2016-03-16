package com.kms.katalon.composer.testcase.support;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.parts.TestCaseVariablePart;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.core.ast.GroovyParser;
import com.kms.katalon.entity.variable.VariableEntity;

public class VariableDefaultValueEditingSupport extends EditingSupport {

    private TestCaseVariablePart variablesPart;

    public VariableDefaultValueEditingSupport(ColumnViewer viewer, TestCaseVariablePart variablesPart) {
        super(viewer);
        this.variablesPart = variablesPart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        ClassNode scriptClass = variablesPart.getParentTestCaseCompositePart().getChildTestCasePart()
                .getTreeTableInput().getMainClassNode();
        try {
            ASTNode astNode = GroovyParser.parseGroovyScriptAndGetFirstItem(((VariableEntity) element)
                    .getDefaultValue());
            IInputValueType inputValueType = AstTreeTableValueUtil.getTypeValue(astNode, scriptClass);
            if (inputValueType != null) {
                return inputValueType.getCellEditorForValue((Composite) getViewer().getControl(), astNode, scriptClass);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        if (element instanceof VariableEntity) {
            return true;
        }
        return false;
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof VariableEntity) {
            ClassNode scriptClass = variablesPart.getParentTestCaseCompositePart().getChildTestCasePart()
                    .getTreeTableInput().getMainClassNode();
            try {
                ASTNode astNode = GroovyParser.parseGroovyScriptAndGetFirstItem(((VariableEntity) element)
                        .getDefaultValue());
                IInputValueType inputValueType = AstTreeTableValueUtil.getTypeValue(astNode, scriptClass);
                if (inputValueType != null) {
                    return inputValueType.getValueToEdit(astNode, scriptClass);
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
            return ((VariableEntity) element).getDefaultValue();
        }
        return StringUtils.EMPTY;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element != null && element instanceof VariableEntity && value != null) {
            try {
                ASTNode astNode = GroovyParser.parseGroovyScriptAndGetFirstItem(((VariableEntity) element)
                        .getDefaultValue());
                IInputValueType inputValueType = AstTreeTableValueUtil
                        .getTypeValue(astNode, variablesPart.getParentTestCaseCompositePart().getChildTestCasePart()
                                .getTreeTableInput().getMainClassNode());
                if (inputValueType != null) {
                    Object object = inputValueType.changeValue(
                            astNode instanceof ExpressionStatement ? ((ExpressionStatement) astNode).getExpression()
                                    : astNode, value, variablesPart.getParentTestCaseCompositePart()
                                    .getChildTestCasePart().getTreeTableInput().getMainClassNode());
                    if (object instanceof ASTNode) {
                        ASTNode newAstNode = (ASTNode) object;
                        StringBuilder stringBuilder = new StringBuilder();
                        GroovyParser groovyParser = new GroovyParser(stringBuilder);
                        groovyParser.parse(newAstNode);
                        ((VariableEntity) element).setDefaultValue(stringBuilder.toString());
                        variablesPart.setDirty(true);
                        this.getViewer().update(element, null);
                    }
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

}
