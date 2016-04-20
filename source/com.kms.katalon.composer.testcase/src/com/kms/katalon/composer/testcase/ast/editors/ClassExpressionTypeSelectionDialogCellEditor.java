package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.editors.TypeSelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClassExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;

public class ClassExpressionTypeSelectionDialogCellEditor extends TypeSelectionDialogCellEditor {
    private ClassExpressionWrapper classExpressionWrapper;

    public ClassExpressionTypeSelectionDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
    }

    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof ClassExpressionWrapper || value instanceof IType);
        if (value instanceof IType) {
            Class<?> valueClass = AstKeywordsInputUtil.loadType(((IType) value).getFullyQualifiedName(),
                    classExpressionWrapper.getScriptClass());
            if (valueClass == null) {
                return;
            }
            classExpressionWrapper.setType(valueClass);
        } else if (value instanceof ClassExpressionWrapper) {
            classExpressionWrapper = ((ClassExpressionWrapper) value).clone();
        }
        super.doSetValue(classExpressionWrapper.getText());
    }

    @Override
    protected ClassExpressionWrapper doGetValue() {
        return classExpressionWrapper;
    }
}
