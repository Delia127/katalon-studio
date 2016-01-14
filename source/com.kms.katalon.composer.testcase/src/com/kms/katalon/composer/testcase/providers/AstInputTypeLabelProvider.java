package com.kms.katalon.composer.testcase.providers;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ClassNode;
import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstInputTypeLabelProvider extends ColumnLabelProvider {
    ClassNode scriptClass;

    public AstInputTypeLabelProvider(ClassNode scriptClass) {
        super();
        this.scriptClass = scriptClass;
    }

    @Override
    public String getText(Object element) {
        IInputValueType typeValue = AstTreeTableValueUtil.getTypeValue(element, scriptClass);
        if (typeValue != null) {
            return TreeEntityUtil.getReadableKeywordName(typeValue.getName());
        }
        return StringUtils.EMPTY;
    }
}
