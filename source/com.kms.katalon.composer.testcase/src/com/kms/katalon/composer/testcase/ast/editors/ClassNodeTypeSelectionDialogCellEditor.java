package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;

public class ClassNodeTypeSelectionDialogCellEditor extends TypeSelectionDialogCellEditor {
    private ClassNodeWrapper classNodeWrapper;

    public ClassNodeTypeSelectionDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
    }

    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof ClassNodeWrapper || value instanceof IType);
        if (value instanceof IType) {
            Class<?> valueClass = AstTreeTableInputUtil.loadType(((IType) value).getFullyQualifiedName(),
                    classNodeWrapper.getScriptClass());
            if (valueClass == null) {
                return;
            }
            classNodeWrapper.setType(valueClass);
        } else if (value instanceof ClassNodeWrapper) {
            classNodeWrapper = (ClassNodeWrapper) value;
        }
        super.doSetValue(classNodeWrapper.getName());
    }

    @Override
    protected ClassNodeWrapper doGetValue() {
        return classNodeWrapper;
    }
}
