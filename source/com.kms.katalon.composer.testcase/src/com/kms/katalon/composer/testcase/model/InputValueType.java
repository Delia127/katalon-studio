package com.kms.katalon.composer.testcase.model;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public enum InputValueType implements IInputValueType {
    String, Number, Boolean, Null, Variable, MethodCall, List, Map, ClosureList, Condition, Binary, Range, Property, GlobalVariable, TestDataValue, TestCase, TestObject, TestData, Class, This;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public boolean isEditable(Object astObject, ClassNode scriptClass) {
        return AstTreeTableValueUtil.getTypeValue(astObject, scriptClass) != null;
    }

    @Override
    public CellEditor getCellEditorForValue(Composite parent, Object astObject, ClassNode scriptClass) {
        return AstTreeTableInputUtil.getCellEditorForAstObject(parent, astObject, scriptClass);
    }

    @Override
    public Object getNewValue(Object existingValue) {
        switch (this) {
        case String:
            return AstTreeTableEntityUtil.getNewStringConstantExpression();
        case Number:
            return AstTreeTableEntityUtil.getNewNumberConstantExpression();
        case Boolean:
            return AstTreeTableEntityUtil.getNewBooleanConstantExpression();
        case Null:
            return new ConstantExpression(null);
        case Binary:
            return AstTreeTableEntityUtil.getNewBinaryExpression();
        case Variable:
            return AstTreeTableEntityUtil.getNewVariableExpression();
        case MethodCall:
            return AstTreeTableEntityUtil.getNewMethodCallExpression();
        case Condition:
            return AstTreeTableEntityUtil.getNewBooleanExpression();
        case List:
            return AstTreeTableEntityUtil.getNewListExpression();
        case Map:
            return AstTreeTableEntityUtil.getNewMapExpression();
        case ClosureList:
            return AstTreeTableEntityUtil.getNewClosureListExpression();
        case Range:
            return AstTreeTableEntityUtil.getNewRangeExpression();
        case Property:
            return AstTreeTableEntityUtil.getNewPropertyExpression();
        case GlobalVariable:
            return AstTreeTableEntityUtil.getNewGlobalVariablePropertyExpression();
        case TestObject:
            return AstTreeTableInputUtil.generateObjectMethodCall(null);
        case Class:
            return AstTreeTableEntityUtil.createNewClassExpressionFromType(String.class);
        case TestDataValue:
            return AstTreeTableEntityUtil.getNewTestDataValueExpression(
                    AstTreeTableEntityUtil.getNewTestDataExpression(new ConstantExpression(null)),
                    new ConstantExpression(null), new ConstantExpression(0));
        case TestData:
            return AstTreeTableEntityUtil.getNewTestDataExpression(new ConstantExpression(null));
        case TestCase:
            return AstTreeTableEntityUtil.getNewTestCaseExpression();
        case This:
            return new VariableExpression("this");
        default:
            return new ConstantExpression(null);
        }
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
        return AstTreeTableTextValueUtil.getTextValue(astObject);
    }
}
