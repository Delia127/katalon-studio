package com.kms.katalon.composer.testcase.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.openqa.selenium.Keys;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.editors.BinaryCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.BooleanCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.BooleanConstantComboBoxCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ClosureListInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.EnumPropertyComboBoxCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.GlobalVariablePropertyComboBoxCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.KeyInputComboBoxCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.KeysInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ListInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.MapInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.MethodCallInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.NumberConstantCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.PropertyInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.PropertyTypeSelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.RangeInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.StringConstantCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestCaseSelectionMethodDialogCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestDataSelectionMethodDialogCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestDataValueCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestObjectCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ThrowableInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TypeSelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.VariableComboBoxCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.VariableTypeSelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.FieldNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClassExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstructorCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.RangeExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.VariableExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.core.model.FailureHandling;

/**
 * Utility class to handle changing value for ast nodes
 *
 */
public class AstTreeTableValueUtil {
    public static final String KEYS_CHORDS_METHOD_NAME = "chord";

    public static InputValueType getTypeValue(Object object) {
        if (object instanceof ExpressionWrapper) {
            return getInputValueTypeForExpressionWrapper((ExpressionWrapper) object);
        } else if (object instanceof ClassNodeWrapper) {
            return InputValueType.Class;
        }
        return null;
    }

    private static InputValueType getInputValueTypeForExpressionWrapper(ExpressionWrapper expressionWrapper) {
        if (expressionWrapper instanceof ConstantExpressionWrapper) {
            ConstantExpressionWrapper constantExpressionWrapper = (ConstantExpressionWrapper) expressionWrapper;
            if (constantExpressionWrapper.isFalseExpression() || constantExpressionWrapper.isTrueExpression()) {
                return InputValueType.Boolean;
            } else if (constantExpressionWrapper.isNumberExpression()) {
                return InputValueType.Number;
            } else if (constantExpressionWrapper.isNullExpression()) {
                return InputValueType.Null;
            }
            return InputValueType.String;
        } else if (expressionWrapper instanceof VariableExpressionWrapper) {
            if (expressionWrapper.getText().equals("this")) {
                return InputValueType.This;
            }
            Type type = AstTreeTableInputUtil.loadType(expressionWrapper.getText(), expressionWrapper.getScriptClass());
            if (type != null) {
                return InputValueType.Class;
            }
            return InputValueType.Variable;
        } else if (expressionWrapper instanceof BinaryExpressionWrapper) {
            return InputValueType.Binary;
        } else if (expressionWrapper instanceof MethodCallExpressionWrapper) {
            MethodCallExpressionWrapper methodCallExpressionWrapper = (MethodCallExpressionWrapper) expressionWrapper;
            if (AstEntityInputUtil.isCallTestCaseArgument(methodCallExpressionWrapper)) {
                return InputValueType.TestCase;
            }
            if (AstEntityInputUtil.isObjectArgument(methodCallExpressionWrapper)) {
                return InputValueType.TestObject;
            }
            if (AstEntityInputUtil.isTestDataArgument(methodCallExpressionWrapper)) {
                return InputValueType.TestData;
            }
            if (AstEntityInputUtil.isTestDataValueArgument(methodCallExpressionWrapper)) {
                return InputValueType.TestDataValue;
            }
            if (isKeysArgumentExpression(methodCallExpressionWrapper)) {
                return InputValueType.Keys;
            }
            return InputValueType.MethodCall;
        } else if (expressionWrapper instanceof BooleanExpressionWrapper) {
            return InputValueType.Condition;
        } else if (expressionWrapper instanceof ClosureListExpressionWrapper) {
            return InputValueType.ClosureList;
        } else if (expressionWrapper instanceof ListExpressionWrapper) {
            return InputValueType.List;
        } else if (expressionWrapper instanceof MapExpressionWrapper) {
            return InputValueType.Map;
        } else if (expressionWrapper instanceof RangeExpressionWrapper) {
            return InputValueType.Range;
        } else if (expressionWrapper instanceof PropertyExpressionWrapper) {
            PropertyExpressionWrapper propertyExprs = (PropertyExpressionWrapper) expressionWrapper;
            if (propertyExprs.getObjectExpression() instanceof VariableExpressionWrapper
                    && propertyExprs.getObjectExpressionAsString().equals(InputValueType.GlobalVariable.name())) {
                return InputValueType.GlobalVariable;
            }
            if (propertyExprs.isObjectExpressionOfClass(Keys.class)) {
                return InputValueType.Key;
            }
            Type type = AstTreeTableInputUtil.loadType(propertyExprs.getText(), propertyExprs.getScriptClass());
            if (type != null) {
                return InputValueType.Class;
            }
            return InputValueType.Property;
        } else if (expressionWrapper instanceof ClassExpressionWrapper) {
            return InputValueType.Class;
        } else if (expressionWrapper instanceof ConstructorCallExpressionWrapper) {
            ConstructorCallExpressionWrapper contructorCallExpressionWrapper = (ConstructorCallExpressionWrapper) expressionWrapper;
            Class<?> throwableClass = AstTreeTableInputUtil.loadType(contructorCallExpressionWrapper.getType()
                    .getName(), contructorCallExpressionWrapper.getScriptClass());
            if (Throwable.class.isAssignableFrom(throwableClass)) {
                return InputValueType.Throwable;
            }
        }
        return InputValueType.Null;
    }

    public static boolean compareAstNode(Object astNode, Object anotherAstNode) {
        StringBuilder stringBuilder = new StringBuilder();
        new GroovyWrapperParser(stringBuilder).parse(astNode);
        String scriptValue = stringBuilder.toString();

        stringBuilder = new StringBuilder();
        new GroovyWrapperParser(stringBuilder).parse(anotherAstNode);
        String anotherScriptValue = stringBuilder.toString();
        return scriptValue.equals(anotherScriptValue);
    }

    public static CellEditor getCellEditorForExpression(Composite parent, ExpressionWrapper expression) {
        if (expression instanceof BinaryExpressionWrapper) {
            return getCellEditorForBinaryExpression(parent, (BinaryExpressionWrapper) expression);
        } else if (expression instanceof MethodCallExpressionWrapper) {
            MethodCallExpressionWrapper methodCallExpressionWrapper = (MethodCallExpressionWrapper) expression;
            if (AstEntityInputUtil.isCallTestCaseArgument(methodCallExpressionWrapper)) {
                return getCellEditorForCallTestCase(parent, methodCallExpressionWrapper);
            }
            if (AstEntityInputUtil.isObjectArgument(methodCallExpressionWrapper)) {
                return getCellEditorForTestObject(parent, methodCallExpressionWrapper);
            }
            if (AstEntityInputUtil.isTestDataArgument(methodCallExpressionWrapper)) {
                return getCellEditorForTestData(parent, methodCallExpressionWrapper);
            }
            if (AstEntityInputUtil.isTestDataValueArgument(methodCallExpressionWrapper)) {
                return getCellEditorForTestDataValue(parent, methodCallExpressionWrapper);
            }
            if (isKeysArgumentExpression(methodCallExpressionWrapper)) {
                return getCellEditorForKeysExpression(parent, methodCallExpressionWrapper);
            }
            return getCellEditorForMethodCallExpression(parent, methodCallExpressionWrapper);
        } else if (expression instanceof BooleanExpressionWrapper) {
            return getCellEditorForBooleanExpression(parent, (BooleanExpressionWrapper) expression);
        } else if (expression instanceof ConstantExpressionWrapper) {
            return getCellEditorForConstantExpression(parent, (ConstantExpressionWrapper) expression);
        } else if (expression instanceof VariableExpressionWrapper) {
            VariableExpressionWrapper variableExpressionWrapper = (VariableExpressionWrapper) expression;
            if (variableExpressionWrapper.getText().equals("this")) {
                return null;
            }
            Type type = AstTreeTableInputUtil.loadType(variableExpressionWrapper.getText(),
                    variableExpressionWrapper.getScriptClass());
            if (type != null) {
                return new VariableTypeSelectionDialogCellEditor(parent, variableExpressionWrapper.getText());
            }
            return getCellEditorForVariableExpression(parent, variableExpressionWrapper);
        } else if (expression instanceof RangeExpressionWrapper) {
            return getCellEditorForRangeExpression(parent, (RangeExpressionWrapper) expression);
        } else if (expression instanceof ClosureListExpressionWrapper) {
            return getCellEditorForClosureListExpression(parent, (ClosureListExpressionWrapper) expression);
        } else if (expression instanceof ListExpressionWrapper) {
            return getCellEditorForListExpression(parent, (ListExpressionWrapper) expression);
        } else if (expression instanceof MapExpressionWrapper) {
            return getCellEditorForMapExpression(parent, (MapExpressionWrapper) expression);
        } else if (expression instanceof PropertyExpressionWrapper) {
            PropertyExpressionWrapper propertyExpressionWrapper = (PropertyExpressionWrapper) expression;
            if (AstTreeTableInputUtil.isGlobalVariablePropertyExpression(propertyExpressionWrapper)) {
                return getCellEditorForGlobalVariableExpression(parent);
            }
            Class<?> type = AstTreeTableInputUtil.loadType(propertyExpressionWrapper.getObjectExpressionAsString(),
                    propertyExpressionWrapper.getScriptClass());
            if (type != null && type.isEnum() && type.getName().equals(FailureHandling.class.getName())) {
                return getNewCellEditorForFailureHandling(parent);
            }
            if (propertyExpressionWrapper.isObjectExpressionOfClass(Keys.class)) {
                return getCellEditorForKeyExpression(parent);
            }
            type = AstTreeTableInputUtil.loadType(propertyExpressionWrapper.getText(),
                    propertyExpressionWrapper.getScriptClass());
            if (type != null) {
                return new PropertyTypeSelectionDialogCellEditor(parent, propertyExpressionWrapper.getText());
            }
            return getCellEditorForPropertyExpression(parent, propertyExpressionWrapper);
        } else if (expression instanceof ClassExpressionWrapper) {
            return getCellEditorForClassExpression(parent, (ClassExpressionWrapper) expression);
        } else if (expression instanceof ConstructorCallExpressionWrapper) {
            return getCellEditorForConstructorCallExpression(parent, (ConstructorCallExpressionWrapper) expression);
        }
        return null;
    }

    public static CellEditor getCellEditorForKeysExpression(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        return new KeysInputCellEditor(parent, methodCallExpressionWrapper.getObjectExpressionAsString());
    }

    public static boolean isKeysArgumentExpression(MethodCallExpressionWrapper methodCallExpressionWrapper) {
        return (methodCallExpressionWrapper.isObjectExpressionOfClass(Keys.class)
                && methodCallExpressionWrapper.getMethodAsString().equals(KEYS_CHORDS_METHOD_NAME));
    }

    public static CellEditor getCellEditorForKeyExpression(Composite parent) {
        return new KeyInputComboBoxCellEditor(parent);
    }

    public static CellEditor getCellEditorForConstructorCallExpression(Composite parent,
            ConstructorCallExpressionWrapper contructorCallExpressionWrapper) {
        Class<?> throwableClass = AstTreeTableInputUtil.loadType(contructorCallExpressionWrapper.getType().getName(),
                contructorCallExpressionWrapper.getScriptClass());
        if (Throwable.class.isAssignableFrom(throwableClass)) {
            return getCellEditorForThrowable(parent, contructorCallExpressionWrapper);
        }
        return null;
    }

    public static CellEditor getCellEditorForThrowable(Composite parent,
            ConstructorCallExpressionWrapper contructorCallExpressionWrapper) {
        return new ThrowableInputCellEditor(parent, contructorCallExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForClassExpression(Composite parent,
            ClassExpressionWrapper classExpressionWrapper) {
        return new TypeSelectionDialogCellEditor(parent, classExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForPropertyExpression(Composite parent,
            PropertyExpressionWrapper propertyExpressionWrapper) {
        return new PropertyInputCellEditor(parent, propertyExpressionWrapper.getText());
    }

    public static CellEditor getNewCellEditorForFailureHandling(Composite parent) {
        return new EnumPropertyComboBoxCellEditor(parent, FailureHandling.class);
    }

    public static CellEditor getCellEditorForBinaryExpression(Composite parent,
            BinaryExpressionWrapper binaryExpressionWrapper) {
        return new BinaryCellEditor(parent, binaryExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForMethodCallExpression(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        return new MethodCallInputCellEditor(parent, methodCallExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForTestDataValue(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        return new TestDataValueCellEditor(parent, methodCallExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForTestData(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        ArgumentListExpressionWrapper argumentListExpressionWrapper = (ArgumentListExpressionWrapper) methodCallExpressionWrapper
                .getArguments();
        if (argumentListExpressionWrapper.getExpressions().isEmpty()) {
            return null;
        }
        String pk = argumentListExpressionWrapper.getExpressions().get(0).getText();
        return new TestDataSelectionMethodDialogCellEditor(parent, pk);
    }

    public static CellEditor getCellEditorForBooleanExpression(Composite parent,
            BooleanExpressionWrapper booleanExpressionWrapper) {
        return new BooleanCellEditor(parent, booleanExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForRangeExpression(Composite parent,
            RangeExpressionWrapper rangeExpressionWrapper) {
        return new RangeInputCellEditor(parent, rangeExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForClosureListExpression(Composite parent,
            ClosureListExpressionWrapper closureListExpressionWrapper) {
        return new ClosureListInputCellEditor(parent, closureListExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForConstantExpression(Composite parent,
            ConstantExpressionWrapper constantExpressionWrapper) {
        if (constantExpressionWrapper.isFalseExpression() || constantExpressionWrapper.isTrueExpression()) {
            return getCellEditorForBooleanConstantExpression(parent);
        }
        if (constantExpressionWrapper.getValue() instanceof Number) {
            return getCellEditorForNumberConstantExpression(parent);
        }
        return getCellEditorForStringConstantExpression(parent);
    }

    public static CellEditor getCellEditorForStringConstantExpression(Composite parent) {
        return new StringConstantCellEditor(parent);
    }

    public static CellEditor getCellEditorForNumberConstantExpression(Composite parent) {
        return new NumberConstantCellEditor(parent);
    }

    public static CellEditor getCellEditorForBooleanConstantExpression(Composite parent) {
        return new BooleanConstantComboBoxCellEditor(parent);
    }

    public static CellEditor getCellEditorForVariableExpression(Composite parent,
            VariableExpressionWrapper variableExpressionWrapper) {
        List<String> variableStringList = new ArrayList<String>();
        ScriptNodeWrapper scriptClass = variableExpressionWrapper.getScriptClass();
        if (scriptClass != null) {
            for (FieldNodeWrapper field : scriptClass.getFields()) {
                variableStringList.add(field.getName());
            }
            return new VariableComboBoxCellEditor(parent, variableStringList);
        }
        return new VariableComboBoxCellEditor(parent, variableStringList);
    }

    public static CellEditor getCellEditorForListExpression(Composite parent,
            ListExpressionWrapper listExpressionWrapper) {
        return new ListInputCellEditor(parent, listExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForMapExpression(Composite parent, MapExpressionWrapper mapExpressionWrapper) {
        return new MapInputCellEditor(parent, mapExpressionWrapper.getText());
    }

    public static CellEditor getCellEditorForCallTestCase(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        String testCasePk = null;
        ExpressionWrapper callTestCaseValueExpressionWrapper = AstEntityInputUtil
                .getCallTestCaseParam(methodCallExpressionWrapper);
        if (callTestCaseValueExpressionWrapper instanceof ConstantExpressionWrapper
                && ((ConstantExpressionWrapper) callTestCaseValueExpressionWrapper).getValue() instanceof String) {
            testCasePk = (String) ((ConstantExpressionWrapper) callTestCaseValueExpressionWrapper).getValue();
        }
        return new TestCaseSelectionMethodDialogCellEditor(parent, testCasePk);
    }

    public static CellEditor getCellEditorForTestObject(Composite parent,
            MethodCallExpressionWrapper methodCallExpressionWrapper) {
        return new TestObjectCellEditor(parent, methodCallExpressionWrapper.getText(), false);
    }

    public static CellEditor getCellEditorForGlobalVariableExpression(Composite parent) {
        try {
            return new GlobalVariablePropertyComboBoxCellEditor(parent);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }
}
