package com.kms.katalon.composer.testcase.support;

import java.util.ArrayList;

import org.codehaus.groovy.ast.ASTNode;
import org.eclipse.jface.viewers.ColumnViewer;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.parts.TestCaseVariablePart;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.core.ast.GroovyParser;
import com.kms.katalon.entity.variable.VariableEntity;

public class VariableDefaultValueTypeEditingSupport extends AstInputBuilderValueTypeColumnSupport {
    private static final String CUSTOM_TAG = "Test Case Variable";
    private TestCaseVariablePart variablesPart;


    public VariableDefaultValueTypeEditingSupport(ColumnViewer viewer, TestCaseVariablePart variablesPart,
            IInputValueType[] defaultInputValueTypes) {
        super(viewer, defaultInputValueTypes, CUSTOM_TAG, null, null);
        this.variablesPart = variablesPart;
        this.defaultInputValueTypes = defaultInputValueTypes;
        inputValueTypeNames = new ArrayList<String>();
        readableValueTypeNames = new ArrayList<String>();
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
            try {
                ASTNode astNode = GroovyParser.parseGroovyScriptAndGetFirstItem(((VariableEntity) element)
                        .getDefaultValue());
                IInputValueType valueType = AstTreeTableValueUtil
                        .getTypeValue(astNode, variablesPart.getParentTestCaseCompositePart().getChildTestCasePart()
                                .getTreeTableInput().getMainClassNode());
                return inputValueTypeNames.indexOf(valueType.getName());
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        return 0;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof VariableEntity && value instanceof Integer && (int) value > -1
                && (int) value < inputValueTypeNames.size()) {
            try {
                ASTNode astNode = GroovyParser.parseGroovyScriptAndGetFirstItem(((VariableEntity) element)
                        .getDefaultValue());
                String newValueTypeString = inputValueTypeNames.get((int) value);
                IInputValueType newValueType = AstTreeTableInputUtil.getInputValueTypeFromString(newValueTypeString);
                IInputValueType oldValueType = AstTreeTableValueUtil
                        .getTypeValue(astNode, variablesPart.getParentTestCaseCompositePart().getChildTestCasePart()
                                .getTreeTableInput().getMainClassNode());
                if (newValueType != oldValueType) {
                    ASTNode newAstNode = (ASTNode) newValueType.getNewValue(astNode);
                    StringBuilder stringBuilder = new StringBuilder();
                    GroovyParser groovyParser = new GroovyParser(stringBuilder);
                    groovyParser.parse(newAstNode);
                    ((VariableEntity) element).setDefaultValue(stringBuilder.toString());
                    variablesPart.setDirty(true);
                    this.getViewer().update(element, null);
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }

        }
    }

}
