package com.kms.katalon.composer.testcase.support;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.parts.TestCaseVariablePart;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.core.groovy.GroovyParser;
import com.kms.katalon.entity.variable.VariableEntity;

public class VariableDefaultValueTypeEditingSupport extends EditingSupport {
    private static final String CUSTOM_TAG = "Test Case Variable";
    private TestCaseVariablePart variablesPart;
    private IInputValueType[] defaultInputValueTypes;
    protected List<String> inputValueTypeNames;

    public VariableDefaultValueTypeEditingSupport(ColumnViewer viewer, TestCaseVariablePart variablesPart,
            IInputValueType[] defaultInputValueTypes) {
        super(viewer);
        this.variablesPart = variablesPart;
        this.defaultInputValueTypes = defaultInputValueTypes;
        inputValueTypeNames = new ArrayList<String>();
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        inputValueTypeNames.clear();
        inputValueTypeNames.addAll(AstTreeTableInputUtil
                .getInputValueTypeStringList(defaultInputValueTypes, CUSTOM_TAG));
        return new ComboBoxCellEditor((Composite) getViewer().getControl(),
                inputValueTypeNames.toArray(new String[inputValueTypeNames.size()]));
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
