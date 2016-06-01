package com.kms.katalon.composer.testcase.model;

import java.util.ArrayList;

import org.codehaus.groovy.ast.ClassNode;

import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArrayExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.CastExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ListExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.core.testcase.TestCase;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.custom.keyword.KeywordParameter;

public class InputParameter {
    private Object value;

    private String paramName;

    private InputParameterClass paramType;

    public InputParameter(String paramName, InputParameterClass paramType) {
        this(paramName, paramType, null);
    }

    public InputParameter(String paramName, InputParameterClass paramType, Object value) {
        this.paramName = paramName;
        this.paramType = paramType;
        initValue(value);
    }

    public void initValue(Object valueObject) {
        if (valueObject instanceof CastExpressionWrapper) {
            value = ((CastExpressionWrapper) valueObject).getExpression();
        }
        if (valueObject instanceof ArrayExpressionWrapper) {
            value = new ArrayList<ExpressionWrapper>(((ArrayExpressionWrapper) valueObject).getExpressions());
        }
        value = valueObject;
    }

    public InputParameter(KeywordParameter keywordParam, Object value) {
        paramName = keywordParam.getName();
        paramType = new InputParameterClass(keywordParam.getType());
        initValue(value);
    }

    public InputParameter(Class<?> inputParamType, Object value) {
        paramName = inputParamType.getSimpleName();
        paramType = new InputParameterClass(inputParamType);
        initValue(value);
    }

    public InputParameter(ParameterWrapper parameter, Object value) {
        paramName = parameter.getName();
        paramType = new InputParameterClass(parameter.getType());
        initValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public InputParameterClass getParamType() {
        return paramType;
    }

    public void setParamType(InputParameterClass paramType) {
        this.paramType = paramType;
    }

    public ExpressionWrapper getValueAsExpression() {
        InputParameterClass paramClass = getParamType();
        Object inputValue = getValue();
        if (inputValue instanceof ListExpressionWrapper && paramClass.isArray()) {
            return getValueAsCastExpression(paramClass, inputValue);
        }
        if (inputValue instanceof ExpressionWrapper) {
            return (ExpressionWrapper) inputValue;
        }
        return new ConstantExpressionWrapper();
    }

    private static ExpressionWrapper getValueAsCastExpression(InputParameterClass paramClass, Object inputValue) {
        ListExpressionWrapper listExpression = (ListExpressionWrapper) inputValue;
        ClassNode convertedTypeClass = new ClassNode(new ClassNode(paramClass.getComponentType().getFullName(),
                paramClass.getModifiers(), new ClassNode(Object.class)));
        return new CastExpressionWrapper(convertedTypeClass, listExpression,
                listExpression.getParent());
    }

    public boolean isEditable() {
        return getParamType() != null
                && getParamType().getFullName() != null
                && !(AstEntityInputUtil.isClassChildOf(TestObject.class.getName(), getParamType().getFullName()) || AstEntityInputUtil.isClassChildOf(
                        TestCase.class.getName(), getParamType().getFullName()));
    }

    public boolean isFailureHandlingInputParameter() {
        return (getParamType().isFailureHandlingTypeClass() && getValue() instanceof ExpressionWrapper && AstKeywordsInputUtil.isFailureHandlingExpression((ExpressionWrapper) getValue()));
    }
}
