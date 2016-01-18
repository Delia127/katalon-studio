package com.kms.katalon.composer.webui.model;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.openqa.selenium.Keys;

import com.kms.katalon.composer.testcase.model.ICustomInputValueType;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.composer.webui.component.KeysInputCellEditor;

public class KeysInputValueType implements ICustomInputValueType {

    private static final String NAME = "Keys";

    public static final String KEYS_CHORDS_METHOD_NAME = "chord";

    public static final String TAG_KEYS = NAME;

    @Override
    public CellEditor getCellEditorForValue(Composite parent, Object astObject, ClassNode scriptClass) {
        return new KeysInputCellEditor(parent, AstTreeTableTextValueUtil.getInstance().getTextValue(astObject),
                scriptClass);
    }

    @Override
    public boolean isEditable(Object astObject, ClassNode scriptClass) {
        if (astObject instanceof MethodCallExpression) {
            MethodCallExpression methodCallExpression = (MethodCallExpression) astObject;
            String objectExpressionString = methodCallExpression.getObjectExpression().getText();
            if ((objectExpressionString.equals(Keys.class.getName()) || objectExpressionString.equals(Keys.class
                    .getSimpleName())) && methodCallExpression.getMethodAsString().equals(KEYS_CHORDS_METHOD_NAME)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public String[] getTags() {
        return new String[] { ICustomInputValueType.TAG_KEYWORD_INPUT };
    }

    @Override
    public Object getNewValue(Object existingValue) {
        return new MethodCallExpression(new ClassExpression(new ClassNode(Keys.class)),
                KeysInputValueType.KEYS_CHORDS_METHOD_NAME, new ArgumentListExpression());
    }

    @Override
    public Object getValueToEdit(Object astObject, ClassNode scriptClass) {
        return AstTreeTableValueUtil.getValue(astObject, scriptClass);
    }

    @Override
    public Object changeValue(Object astObject, Object newValue, ClassNode scriptClass) {
        return AstTreeTableValueUtil.setValue(astObject, newValue, scriptClass);
    }

    @Override
    public String getDisplayValue(Object astObject) {
        if (astObject instanceof MethodCallExpression) {
            return AstTreeTableTextValueUtil.getInstance().getTextValue(
                    ((MethodCallExpression) astObject).getArguments());
        }
        return AstTreeTableTextValueUtil.getInstance().getTextValue(astObject);
    }

}
