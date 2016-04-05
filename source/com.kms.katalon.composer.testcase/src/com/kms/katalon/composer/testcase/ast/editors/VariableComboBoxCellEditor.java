package com.kms.katalon.composer.testcase.ast.editors;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.editors.StringComboBoxCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.VariableExpressionWrapper;

public class VariableComboBoxCellEditor extends StringComboBoxCellEditor {
    private VariableExpressionWrapper variableExpression;

    public VariableComboBoxCellEditor(Composite parent, List<String> variableStringList) {
        super(parent, variableStringList.toArray(new String[variableStringList.size()]));
    }

    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof VariableExpressionWrapper);
        variableExpression = (VariableExpressionWrapper) value;
        super.doSetValue(variableExpression.getVariable());
    }

    @Override
    protected VariableExpressionWrapper doGetValue() {
        variableExpression.setVariable((String) super.doGetValue());
        return variableExpression;
    }
}
