package com.kms.katalon.composer.testcase.util;

import java.lang.reflect.Type;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Keys;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.CastExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClassExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstructorCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.DeclarationExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.RangeExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.VariableExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;

public class AstInputValueTypeProvider {
    interface InputValueTypeProvider<T extends ASTNodeWrapper> {
        InputValueType getInputValueType(T input);
    }
    
    private static HashMap<String, InputValueTypeProvider<? extends ASTNodeWrapper>> inputClasses;
    
    private static final InputValueTypeProvider<ClassNodeWrapper> classNodeValueTypeProvider = new InputValueTypeProvider<ClassNodeWrapper>() {
        @Override
        public InputValueType getInputValueType(ClassNodeWrapper classNode) {
            return InputValueType.Class;
        }
    };

    private static final InputValueTypeProvider<ConstantExpressionWrapper> constantValueTypeProvider = new InputValueTypeProvider<ConstantExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(ConstantExpressionWrapper constantExpression) {
            if (constantExpression.isFalseExpression() || constantExpression.isTrueExpression()) {
                return InputValueType.Boolean;
            } else if (constantExpression.isNumberExpression()) {
                return InputValueType.Number;
            } else if (constantExpression.isNullExpression()) {
                return InputValueType.Null;
            }
            return InputValueType.String;
        }
    };

    private static final InputValueTypeProvider<VariableExpressionWrapper> variableValueTypeProvider = new InputValueTypeProvider<VariableExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(VariableExpressionWrapper variableExpression) {
            if (variableExpression.getText().equals("this")) {
                return InputValueType.This;
            }
            Type type = AstKeywordsInputUtil.loadType(variableExpression.getText(),
                    variableExpression.getScriptClass());
            if (type != null) {
                return InputValueType.Class;
            }
            return InputValueType.Variable;
        }
    };

    private static final InputValueTypeProvider<BinaryExpressionWrapper> binaryValueTypeProvider = new InputValueTypeProvider<BinaryExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(BinaryExpressionWrapper variableExpression) {
            return InputValueType.Binary;
        }
    };

    private static final InputValueTypeProvider<MethodCallExpressionWrapper> methodValueTypeProvider = new InputValueTypeProvider<MethodCallExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(MethodCallExpressionWrapper methodCallExpression) {
            if (methodCallExpression.isFindTestCaseMethodCall()) {
                return InputValueType.TestCase;
            }
            if (methodCallExpression.isFindTestObjectMethodCall()) {
                return InputValueType.TestObject;
            }
            if (methodCallExpression.isFindTestDataMethodCall()) {
                return InputValueType.TestData;
            }
            if (methodCallExpression.isGetTestDataValueMethodCall()) {
                return InputValueType.TestDataValue;
            }
            if (methodCallExpression.isKeysArgumentExpression()) {
                return InputValueType.Keys;
            }
            return InputValueType.MethodCall;
        }
    };

    private static final InputValueTypeProvider<BooleanExpressionWrapper> booleanValueTypeProvider = new InputValueTypeProvider<BooleanExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(BooleanExpressionWrapper booleanExpression) {
            return InputValueType.Condition;
        }
    };

    private static final InputValueTypeProvider<ClosureExpressionWrapper> closureValueTypeProvider = new InputValueTypeProvider<ClosureExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(ClosureExpressionWrapper closureExpression) {
            return InputValueType.Closure;
        }
    };
    
    private static final InputValueTypeProvider<ClosureListExpressionWrapper> closureListValueTypeProvider = new InputValueTypeProvider<ClosureListExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(ClosureListExpressionWrapper closureListExpression) {
            return InputValueType.ClosureList;
        }
    };

    private static final InputValueTypeProvider<ListExpressionWrapper> listExpressionWrapper = new InputValueTypeProvider<ListExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(ListExpressionWrapper listExpression) {
            return InputValueType.List;
        }
    };

    private static final InputValueTypeProvider<MapExpressionWrapper> mapValueTypeProvider = new InputValueTypeProvider<MapExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(MapExpressionWrapper mapExpression) {
            return InputValueType.Map;
        }
    };

    private static final InputValueTypeProvider<RangeExpressionWrapper> rangeValueTypeProvider = new InputValueTypeProvider<RangeExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(RangeExpressionWrapper rangeExpression) {
            return InputValueType.Range;
        }
    };

    private static final InputValueTypeProvider<PropertyExpressionWrapper> propertyValueTypeProvider = new InputValueTypeProvider<PropertyExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(PropertyExpressionWrapper propertyExpression) {
            if (StringUtils.equals(propertyExpression.getObjectExpressionAsString(),
                    InputValueType.GlobalVariable.name())) {
                return InputValueType.GlobalVariable;
            }
            if (propertyExpression.isObjectExpressionOfClass(Keys.class)) {
                return InputValueType.Key;
            }
            Type type = AstKeywordsInputUtil.loadType(propertyExpression.getText(),
                    propertyExpression.getScriptClass());
            if (type != null) {
                return InputValueType.Class;
            }
            return InputValueType.Property;
        }
    };

    private static final InputValueTypeProvider<ClassExpressionWrapper> classValueTypeProvider = new InputValueTypeProvider<ClassExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(ClassExpressionWrapper classExpression) {
            return InputValueType.Class;
        }
    };

    private static final InputValueTypeProvider<ConstructorCallExpressionWrapper> constructorCallValueTypeProvider = new InputValueTypeProvider<ConstructorCallExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(ConstructorCallExpressionWrapper constructorCallExpression) {
            Class<?> throwableClass = AstKeywordsInputUtil.loadType(constructorCallExpression.getType().getName(),
                    constructorCallExpression.getScriptClass());
            if (Throwable.class.isAssignableFrom(throwableClass)) {
                return InputValueType.Throwable;
            }
            return InputValueType.Null;
        }
    };
    
    private static final InputValueTypeProvider<CastExpressionWrapper> castExpressionValueTypeProvider = new InputValueTypeProvider<CastExpressionWrapper>() {
        @Override
        public InputValueType getInputValueType(CastExpressionWrapper castExpression) {
            if (castExpression.getExpression() instanceof ListExpressionWrapper) {
                return InputValueType.List;
            }
            return InputValueType.Null;
        }
    };

    static {
        inputClasses = new HashMap<>();
        inputClasses.put(ClassNodeWrapper.class.getSimpleName(), classNodeValueTypeProvider);
        inputClasses.put(ConstantExpressionWrapper.class.getSimpleName(), constantValueTypeProvider);
        inputClasses.put(VariableExpressionWrapper.class.getSimpleName(), variableValueTypeProvider);
        inputClasses.put(BinaryExpressionWrapper.class.getSimpleName(), binaryValueTypeProvider);
        inputClasses.put(MethodCallExpressionWrapper.class.getSimpleName(), methodValueTypeProvider);
        inputClasses.put(BooleanExpressionWrapper.class.getSimpleName(), booleanValueTypeProvider);
        inputClasses.put(ClosureExpressionWrapper.class.getSimpleName(), closureValueTypeProvider);
        inputClasses.put(ClosureListExpressionWrapper.class.getSimpleName(), closureListValueTypeProvider);
        inputClasses.put(ListExpressionWrapper.class.getSimpleName(), listExpressionWrapper);
        inputClasses.put(MapExpressionWrapper.class.getSimpleName(), mapValueTypeProvider);
        inputClasses.put(RangeExpressionWrapper.class.getSimpleName(), rangeValueTypeProvider);
        inputClasses.put(PropertyExpressionWrapper.class.getSimpleName(), propertyValueTypeProvider);
        inputClasses.put(ClassExpressionWrapper.class.getSimpleName(), classValueTypeProvider);
        inputClasses.put(ConstructorCallExpressionWrapper.class.getSimpleName(), constructorCallValueTypeProvider);
        inputClasses.put(DeclarationExpressionWrapper.class.getSimpleName(), binaryValueTypeProvider);
        inputClasses.put(CastExpressionWrapper.class.getSimpleName(), castExpressionValueTypeProvider);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ASTNodeWrapper> InputValueType getInputValueTypeForASTNode(T astNode) {
        InputValueTypeProvider<T> provider = (InputValueTypeProvider<T>) inputClasses.get(astNode.getClass()
                .getSimpleName());
        if (provider != null) {
            return provider.getInputValueType(astNode);
        }
        return null;
    }
}
