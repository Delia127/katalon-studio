package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;

public class ParameterSelectionDialogCellEditor extends TypeSelectionDialogCellEditor {
    private ParameterWrapper parameter;

    public ParameterSelectionDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
    }

    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof ParameterWrapper || value instanceof IType);
        if (value instanceof IType) {
            Class<?> valueClass = AstTreeTableInputUtil.loadType(((IType) value).getFullyQualifiedName(),
                    parameter.getScriptClass());
            if (valueClass == null) {
                return;
            }
            parameter.getType().setType(valueClass);
        } else if (value instanceof ParameterWrapper) {
            parameter = (ParameterWrapper) value;
        }
        super.doSetValue(parameter.getType().getName());
    }

    @Override
    protected ParameterWrapper doGetValue() {
        return parameter;
    }
}
