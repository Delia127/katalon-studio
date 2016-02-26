package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.VariableExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;

public class VariableTypeSelectionDialogCellEditor extends TypeSelectionDialogCellEditor {
    private VariableExpressionWrapper variableExpression;

    public VariableTypeSelectionDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
    }

    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof VariableExpressionWrapper || value instanceof IType);
        if (value instanceof IType) {
            Class<?> valueClass = AstTreeTableInputUtil.loadType(((IType) value).getFullyQualifiedName(),
                    variableExpression.getScriptClass());
            if (valueClass == null) {
                return;
            }
            variableExpression.setVariable(valueClass.getName());
        } else if (value instanceof VariableExpressionWrapper) {
            variableExpression = (VariableExpressionWrapper) value;
        }
        super.doSetValue(variableExpression.getText());
    }

    @Override
    protected VariableExpressionWrapper doGetValue() {
        return variableExpression;
    }
}
