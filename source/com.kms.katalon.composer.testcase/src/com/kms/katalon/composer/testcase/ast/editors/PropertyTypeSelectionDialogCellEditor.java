package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.editors.TypeSelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;

public class PropertyTypeSelectionDialogCellEditor extends TypeSelectionDialogCellEditor {
    private PropertyExpressionWrapper propertyExpression;

    public PropertyTypeSelectionDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
    }

    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof PropertyExpressionWrapper || value instanceof IType);
        if (value instanceof IType) {
            Class<?> valueClass = AstKeywordsInputUtil.loadType(((IType) value).getFullyQualifiedName(),
                    propertyExpression.getScriptClass());
            if (valueClass == null) {
                return;
            }
            propertyExpression = AstKeywordsInputUtil.createPropertyExpressionForClass(
                    valueClass.getName(), propertyExpression);
        } else if (value instanceof PropertyExpressionWrapper) {
            propertyExpression = ((PropertyExpressionWrapper) value).clone();
        }
        super.doSetValue(propertyExpression.getText());
    }

    @Override
    protected PropertyExpressionWrapper doGetValue() {
        return propertyExpression;
    }
}
