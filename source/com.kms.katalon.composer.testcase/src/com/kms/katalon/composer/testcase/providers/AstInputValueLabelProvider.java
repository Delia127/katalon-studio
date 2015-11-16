package com.kms.katalon.composer.testcase.providers;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstInputValueLabelProvider extends ColumnLabelProvider {
    private ClassNode scriptClass;

    public AstInputValueLabelProvider(ClassNode scriptClass) {
        this.scriptClass = scriptClass;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ASTNode) {
            IInputValueType typeValue = AstTreeTableValueUtil.getTypeValue(element, scriptClass);
            if (typeValue != null) {
                return typeValue.getDisplayValue(element);
            }
        }
        return "";
    }
}
