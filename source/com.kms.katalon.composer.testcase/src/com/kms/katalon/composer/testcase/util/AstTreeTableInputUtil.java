package com.kms.katalon.composer.testcase.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ImportNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.MethodNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArrayExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.CastExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.VariableExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputParameterClass;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.ast.GroovyParser;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.custom.keyword.KeywordMethod;
import com.kms.katalon.custom.keyword.KeywordParameter;
import com.kms.katalon.groovy.util.GroovyUtil;

/**
 * Utility class to process ast input classes for keywords
 *
 */
public class AstTreeTableInputUtil {
    private static final String CUSTOM_KEYWORDS_CLASS_NAME = "CustomKeywords";

    public static final String BUILT_IN_KEYWORDS_CLASS_NAME = "BuiltInKeywords";

    public static boolean isBuiltInKeywordMethodCall(MethodCallExpressionWrapper methodCallExpression) {
        if (methodCallExpression == null || methodCallExpression.getObjectExpression() == null) {
            return false;
        }
        for (KeywordClass keywordClass : KeywordController.getInstance().getBuiltInKeywordClasses()) {
            if (methodCallExpression.isObjectExpressionOfClass(keywordClass.getType())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCustomKeywordMethodCall(MethodCallExpressionWrapper methodCallExpression) {
        return methodCallExpression != null && methodCallExpression.getObjectExpression() != null
                && methodCallExpression.getObjectExpressionAsString().equals(CUSTOM_KEYWORDS_CLASS_NAME);
    }

    public static ExpressionStatementWrapper getNewCustomKeyword(ASTNodeWrapper parentNode) throws Exception {
        List<MethodNode> customKeywords = KeywordController.getInstance().getCustomKeywords(
                ProjectController.getInstance().getCurrentProject());
        if (customKeywords == null || customKeywords.isEmpty()) {
            return null;
        }
        String rawKeywordName = customKeywords.get(0).getName();
        return AstTreeTableInputUtil.createCustomKeywordMethodCall(customKeywords.get(0).getDeclaringClass().getName(),
                KeywordController.getInstance().getCustomKeywordName(rawKeywordName), parentNode);
    }

    public static ExpressionStatementWrapper createBuiltInKeywordMethodCall(String classSimpleName, String keyword,
            ASTNodeWrapper parentNode) {
        MethodCallExpressionWrapper keywordMethodCallExpression = new MethodCallExpressionWrapper(classSimpleName,
                keyword, null);
        generateBuiltInKeywordArguments(keywordMethodCallExpression);
        return new ExpressionStatementWrapper(keywordMethodCallExpression, parentNode);
    }

    public static ExpressionStatementWrapper createCustomKeywordMethodCall(String keywordClass, String keywordName,
            ASTNodeWrapper parentNode) throws Exception {
        MethodCallExpressionWrapper keywordMethodCallExpression = new MethodCallExpressionWrapper(keywordClass,
                keywordName, null);
        generateCustomKeywordArguments(keywordMethodCallExpression);
        return new ExpressionStatementWrapper(keywordMethodCallExpression, parentNode);
    }

    public static List<InputParameter> generateBuiltInKeywordInputParameters(String buitInKWClassSimpleName,
            String keyword, ArgumentListExpressionWrapper argumentListExpression) throws IOException {
        if (argumentListExpression == null) {
            return null;
        }
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(buitInKWClassSimpleName,
                keyword);
        if (keywordMethod == null || keywordMethod.getParameters().length == 0) {
            return Collections.emptyList();
        }
        return generateBuiltInKeywordInputParameters(keywordMethod, argumentListExpression);
    }

    public static List<InputParameter> generateBuiltInKeywordInputParameters(KeywordMethod keywordMethod,
            ArgumentListExpressionWrapper argumentListExpression) {
        List<InputParameter> inputParameters = new ArrayList<InputParameter>();
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            KeywordParameter keywordParam = keywordMethod.getParameters()[i];
            InputParameterClass inputParameterClass = convertToInputParameterClass(keywordParam.getType());
            if (argumentListExpression.getExpressions().size() > i) {
                inputParameters.add(getInputParameter(keywordParam.getName(), inputParameterClass,
                        argumentListExpression.getExpression(i)));
            } else {
                inputParameters.add(getDefaultInputParameter(keywordParam.getName(), inputParameterClass));
            }
        }
        return inputParameters;
    }

    public static List<InputParameter> generateCustomKeywordInputParameters(String className, String keywordName,
            ArgumentListExpressionWrapper argumentListExpression) throws Exception {
        if (argumentListExpression == null) {
            return null;
        }
        MethodNode keywordMethod = KeywordController.getInstance().getCustomKeywordByName(className, keywordName,
                ProjectController.getInstance().getCurrentProject());
        return generateInputParameters(argumentListExpression, new MethodNodeWrapper(keywordMethod, null));
    }

    public static List<InputParameter> generateInputParameters(ArgumentListExpressionWrapper argumentListExpression,
            MethodNodeWrapper method) {
        if (method == null || argumentListExpression == null || method.getParameters().length == 0) {
            return Collections.emptyList();
        }
        List<InputParameter> inputParameters = new ArrayList<InputParameter>();
        for (int i = 0; i < method.getParameters().length; i++) {
            InputParameterClass inputParameterClass = convertToInputParameterClass(method.getParameters()[i].getType());
            if (argumentListExpression.getExpressions().size() > i) {
                inputParameters.add(getInputParameter(method.getParameters()[i].getName(), inputParameterClass,
                        argumentListExpression.getExpression(i)));
            } else {
                inputParameters.add(getDefaultInputParameter(method.getParameters()[i].getName(), inputParameterClass));
            }
        }
        return inputParameters;
    }

    public static List<InputParameter> generateInputParameters(ArgumentListExpressionWrapper argumentListExpression,
            Method method) {
        if (method == null || argumentListExpression == null || method.getParameterTypes().length == 0) {
            return Collections.emptyList();
        }
        List<InputParameter> inputParameters = new ArrayList<InputParameter>();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            InputParameterClass inputParameterClass = convertToInputParameterClass(method.getParameterTypes()[i]);
            if (argumentListExpression.getExpressions().size() > i) {
                inputParameters.add(getInputParameter(method.getParameterTypes()[i].getSimpleName(),
                        inputParameterClass, argumentListExpression.getExpression(i)));
            } else {
                inputParameters.add(getDefaultInputParameter(method.getParameterTypes()[i].getSimpleName(),
                        inputParameterClass));
            }
        }
        return inputParameters;
    }

    public static List<Class<?>> getParamClasses(Method method) {
        if (method == null) {
            return Collections.emptyList();
        }
        List<Class<?>> parameterClasses = new ArrayList<Class<?>>();
        for (Type type : method.getGenericParameterTypes()) {
            if (type instanceof Class<?>
                    || (type instanceof ParameterizedType
                            && ((ParameterizedType) type).getRawType() instanceof Class<?> && ((Class<?>) ((ParameterizedType) type).getRawType()).getName()
                            .equals(Map.class.getName()))) {
                parameterClasses.add(((Class<?>) type));
            }
        }
        return parameterClasses;
    }

    public static List<Class<?>> getParamClasses(MethodNodeWrapper method) {
        if (method == null) {
            return Collections.emptyList();
        }
        List<Class<?>> parameterClasses = new ArrayList<Class<?>>();
        for (ParameterWrapper param : method.getParameters()) {
            parameterClasses.add(param.getType().getTypeClass());
        }
        return parameterClasses;
    }

    public static InputParameterClass convertToInputParameterClass(Type type) {
        Class<?> clazz = null;
        if (type instanceof Class<?>) {
            clazz = (Class<?>) type;
        } else if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() instanceof Class<?>) {
            clazz = (Class<?>) ((ParameterizedType) type).getRawType();
        }
        if (clazz == null) {
            return null;
        }

        InputParameterClass inputParameterClass = new InputParameterClass(clazz.getName(), clazz.getSimpleName());
        inputParameterClass.setModifiers(clazz.getModifiers());
        if (clazz.isArray() && clazz.getComponentType() != null) {
            inputParameterClass.setArray(true);
            Class<?> componentType = clazz.getComponentType();
            inputParameterClass.setComponentType(new InputParameterClass(componentType.getName(),
                    componentType.getSimpleName()));
        }
        if (clazz.isEnum()) {
            inputParameterClass.setEnum(true);
            inputParameterClass.setEnumConstants(clazz.getEnumConstants());
        }
        List<InputParameterClass> typeList = new ArrayList<InputParameterClass>();
        if (type instanceof ParameterizedType) {
            for (Type actualTypeArgument : ((ParameterizedType) type).getActualTypeArguments()) {
                if (actualTypeArgument instanceof Class<?>) {
                    typeList.add(convertToInputParameterClass(actualTypeArgument));
                }
            }
        } else {
            typeList.add(new InputParameterClass(Object.class.getName(), Object.class.getSimpleName()));
        }
        inputParameterClass.setActualTypeArguments(typeList);
        return inputParameterClass;
    }

    public static InputParameterClass convertToInputParameterClass(ClassNodeWrapper classNode) {
        if (classNode == null) {
            return null;
        }
        InputParameterClass inputParameterClass = new InputParameterClass(classNode.getName(),
                classNode.getNameWithoutPackage());
        inputParameterClass.setModifiers(classNode.getModifiers());
        if (classNode.isArray()) {
            inputParameterClass.setArray(true);
            inputParameterClass.setComponentType(new InputParameterClass(classNode.getComponentType().getName(),
                    classNode.getComponentType().getNameWithoutPackage()));
        }
        if (classNode.isEnum()) {
            inputParameterClass.setEnum(true);
            inputParameterClass.setEnumConstants(classNode.getTypeClass().getEnumConstants());
        }
        List<InputParameterClass> typeList = new ArrayList<InputParameterClass>();
        typeList.add(new InputParameterClass(Object.class.getName(), Object.class.getSimpleName()));
        typeList.add(new InputParameterClass(Object.class.getName(), Object.class.getSimpleName()));
        inputParameterClass.setActualTypeArguments(typeList);
        return inputParameterClass;
    }

    public static InputParameter getDefaultInputParameter(String paramName, InputParameterClass parameterClass) {
        return new InputParameter(paramName, parameterClass, null);
    }

    public static InputParameter getInputParameter(String paramName, InputParameterClass paramClass,
            ExpressionWrapper expression) {
        if (expression instanceof CastExpressionWrapper) {
            return getInputParameter(paramName, paramClass, (CastExpressionWrapper) expression);
        }
        if (expression instanceof ArrayExpressionWrapper) {
            return getInputParameter(paramName, paramClass, (ArrayExpressionWrapper) expression);
        } else if (expression != null) {
            return new InputParameter(paramName, paramClass, expression);
        }
        return getDefaultInputParameter(paramName, paramClass);
    }

    public static InputParameter getInputParameter(String paramName, InputParameterClass paramClass,
            CastExpressionWrapper castExpression) {
        return getInputParameter(paramName, paramClass, castExpression.getExpression());
    }

    public static InputParameter getInputParameter(String paramName, InputParameterClass paramClass,
            ArrayExpressionWrapper arrayExpression) {
        return new InputParameter(paramName, paramClass, new ArrayList<ExpressionWrapper>(
                arrayExpression.getExpressions()));
    }

    public static PropertyExpressionWrapper createPropertyExpressionForClass(String className, ASTNodeWrapper parentNode) {
        if (className.equals(FailureHandling.class.getName())
                || className.equals(FailureHandling.class.getSimpleName())) {
            return getNewFailureHandlingPropertyExpression(parentNode);
        }
        return createPropertyExpressionForClassFromPath(className, null);
    }

    public static PropertyExpressionWrapper getNewFailureHandlingPropertyExpression(ASTNodeWrapper parentNode) {
        return new PropertyExpressionWrapper(FailureHandling.class.getSimpleName(),
                TestCasePreferenceDefaultValueInitializer.getDefaultFailureHandling().name(), parentNode);
    }

    // recursively creating property expression
    public static PropertyExpressionWrapper createPropertyExpressionForClassFromPath(String parentPath, String childName) {
        if (parentPath.contains(".")) {
            PropertyExpressionWrapper childPropertyExpression = createPropertyExpressionForClassFromPath(
                    parentPath.substring(0, parentPath.lastIndexOf(".")),
                    parentPath.substring(parentPath.lastIndexOf(".") + 1));
            if (childName != null) {
                PropertyExpressionWrapper newPropertyExpression = new PropertyExpressionWrapper(null);
                newPropertyExpression.setObjectExpression(childPropertyExpression);
                newPropertyExpression.setProperty(new ConstantExpressionWrapper(childName, newPropertyExpression));
                childPropertyExpression.setParent(newPropertyExpression);
                return newPropertyExpression;
            }
            return childPropertyExpression;
        } else {
            return new PropertyExpressionWrapper(parentPath, childName, null);
        }
    }

    public static ExpressionWrapper getArgumentExpression(InputParameter inputParameter, ASTNodeWrapper parent) {
        if (inputParameter == null) {
            return new ConstantExpressionWrapper(parent);
        }
        if (inputParameter.getValue() instanceof ExpressionWrapper) {
            return (ExpressionWrapper) inputParameter.getValue();
        }
        InputParameterClass paramClass = inputParameter.getParamType();
        if (inputParameter.getValue() instanceof ListExpressionWrapper && paramClass.isArray()) {
            return new CastExpressionWrapper(new ClassNode(new ClassNode(paramClass.getComponentType().getFullName(),
                    paramClass.getModifiers(), new ClassNode(Object.class))),
                    (ListExpressionWrapper) inputParameter.getValue(), parent);
        }
        return new ConstantExpressionWrapper(parent);
    }

    public static void generateBuiltInKeywordArguments(MethodCallExpressionWrapper keywordCallExpression) {
        if (keywordCallExpression == null || keywordCallExpression.getMethod() == null) {
            return;
        }
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
                keywordCallExpression.getObjectExpressionAsString(), keywordCallExpression.getMethodAsString());
        if (keywordMethod != null) {
            generateMethodCallArguments(keywordCallExpression, keywordMethod);
        }
    }

    public static void generateMethodCallArguments(MethodCallExpressionWrapper keywordCallExpression,
            KeywordMethod keywordMethod) {
        List<ExpressionWrapper> exisitingArgumentList = ((ArgumentListExpressionWrapper) keywordCallExpression.getArguments()).getExpressions();
        List<ExpressionWrapper> newArgumentList = new ArrayList<ExpressionWrapper>();
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            KeywordParameter keywordParam = keywordMethod.getParameters()[i];
            newArgumentList.add(generateArgument((i < exisitingArgumentList.size()) ? exisitingArgumentList.get(i)
                    : null, keywordMethod.getName(), keywordParam.getName(), keywordParam.getType().getSimpleName(),
                    keywordParam.getType().getName(), keywordParam.getType().isArray(),
                    keywordParam.getType().isEnum(), keywordCallExpression.getArguments()));
        }
        ((ArgumentListExpressionWrapper) keywordCallExpression.getArguments()).setExpressions(newArgumentList);
    }

    public static void generateMethodCallArguments(MethodCallExpressionWrapper keywordCallExpression, Method method) {
        List<ExpressionWrapper> exisitingArgumentList = ((ArgumentListExpressionWrapper) keywordCallExpression.getArguments()).getExpressions();
        List<ExpressionWrapper> newArgumentList = new ArrayList<ExpressionWrapper>();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> keywordParam = method.getParameterTypes()[i];
            newArgumentList.add(generateArgument((i < exisitingArgumentList.size()) ? exisitingArgumentList.get(i)
                    : null, method.getName(), keywordParam.getName(), keywordParam.getSimpleName(),
                    keywordParam.getName(), keywordParam.isArray(), keywordParam.isEnum(),
                    keywordCallExpression.getArguments()));
        }
        ((ArgumentListExpressionWrapper) keywordCallExpression.getArguments()).setExpressions(newArgumentList);
    }

    public static void generateCustomKeywordArguments(MethodCallExpressionWrapper keywordCallExpression)
            throws Exception {
        if (keywordCallExpression == null || keywordCallExpression.getMethod() == null
                || !(keywordCallExpression.getArguments() instanceof ArgumentListExpressionWrapper)) {
            return;
        }
        MethodNode keywordMethod = KeywordController.getInstance().getCustomKeywordByName(
                keywordCallExpression.getObjectExpressionAsString(), keywordCallExpression.getMethodAsString(),
                ProjectController.getInstance().getCurrentProject());
        if (keywordMethod == null) {
            return;
        }
        List<ExpressionWrapper> exisitingArgumentList = ((ArgumentListExpressionWrapper) keywordCallExpression.getArguments()).getExpressions();
        List<ExpressionWrapper> newArgumentList = new ArrayList<ExpressionWrapper>();
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            if (keywordMethod.getParameters()[i].getType() == null) {
                return;
            }
            ClassNode classNode = keywordMethod.getParameters()[i].getType();
            newArgumentList.add(generateArgument((i < exisitingArgumentList.size()) ? exisitingArgumentList.get(i)
                    : null, keywordMethod.getName(), null, classNode.getNameWithoutPackage(), classNode.getName(),
                    classNode.isArray(), classNode.isEnum(), keywordCallExpression.getArguments()));
        }
        ((ArgumentListExpressionWrapper) keywordCallExpression.getArguments()).setExpressions(newArgumentList);
    }

    public static ExpressionWrapper generateArgument(ExpressionWrapper existingParam, String methodName,
            String paramName, String paramClassSimpleName, String paramClassFullName, boolean isArrayParam,
            boolean isEnumParam, ASTNodeWrapper parentNode) {
        if (isDelaySecondParam(methodName, paramName)) {
            return generateArgumentForDelaySecondParam(existingParam, parentNode);
        }
        if (existingParam != null
                && !(existingParam instanceof PropertyExpressionWrapper && isFailureHandlingPropertyExpression((PropertyExpressionWrapper) existingParam))
                && isClassAssignable(existingParam.getType().getName(), paramClassSimpleName, paramClassFullName)) {
            return existingParam;
        }
        if (isEnumParam) {
            return generateArgumentForEnumParam(existingParam, paramClassSimpleName, paramClassFullName, parentNode);
        }

        if (isClassAssignable(paramClassFullName, TestObject.class)) {
            return generateArgumentForTestObjectParam(existingParam, parentNode);
        }

        if (isClassAssignable(paramClassFullName, List.class) || isArrayParam) {
            return generateArgumentForListParam(existingParam, parentNode);
        }

        ExpressionWrapper newExpression = generateArgumentForConstantParam(existingParam, parentNode,
                paramClassFullName);
        if (newExpression != null) {
            return newExpression;
        }
        if (existingParam instanceof ExpressionWrapper
                && !(existingParam instanceof PropertyExpressionWrapper && isFailureHandlingPropertyExpression((PropertyExpressionWrapper) existingParam))) {
            return existingParam;
        }
        return new ConstantExpressionWrapper(parentNode);
    }

    private static ExpressionWrapper generateArgumentForDelaySecondParam(ExpressionWrapper existingParam,
            ASTNodeWrapper parentNode) {
        if (existingParam == null) {
            return new ConstantExpressionWrapper(0, parentNode);
        }
        if (existingParam instanceof ConstantExpressionWrapper) {
            try {
                Integer.parseInt(existingParam.getText());
                return existingParam;
            } catch (NumberFormatException e) {
                // if parse into number fail then ignore
            }
        }
        if (isUnknowTypeParam(existingParam)) {
            return existingParam;
        }
        return new ConstantExpressionWrapper(0, parentNode);
    }

    private static boolean isDelaySecondParam(String methodName, String paramName) {
        return StringUtils.equals(methodName, "delay") && StringUtils.equals(paramName, "second");
    }

    private static ExpressionWrapper generateArgumentForConstantParam(ExpressionWrapper existingParam,
            ASTNodeWrapper parentNode, String paramClassName) {
        if (isClassAssignable(paramClassName, Boolean.class)) {
            return generateArgumentForBooleanConstantParam(existingParam, parentNode);
        }
        if (isClassAssignable(paramClassName, String.class) || isClassAssignable(paramClassName, Character.class)) {
            return generateArgumentForStringConstantParam(existingParam, parentNode);
        }
        if (isClassAssignable(paramClassName, Number.class)) {
            return generateArgumentForNumberConstantExpression(existingParam, parentNode);
        }
        return null;
    }

    private static ExpressionWrapper generateArgumentForNumberConstantExpression(ExpressionWrapper existingParam,
            ASTNodeWrapper parentNode) {
        if (existingParam != null
                && (isClassAssignable(existingParam.getType().getName(), Number.class) || isUnknowTypeParam(existingParam))) {
            return existingParam;
        }
        return new ConstantExpressionWrapper(0, parentNode);
    }

    private static ExpressionWrapper generateArgumentForStringConstantParam(ExpressionWrapper existingParam,
            ASTNodeWrapper parentNode) {
        if (existingParam != null
                && (isClassAssignable(existingParam.getType().getName(), String.class) || isClassAssignable(
                        existingParam.getType().getName(), Character.class)) || isUnknowTypeParam(existingParam)) {
            return existingParam;
        }
        return new ConstantExpressionWrapper("", parentNode);
    }

    private static ExpressionWrapper generateArgumentForBooleanConstantParam(ExpressionWrapper existingParam,
            ASTNodeWrapper parentNode) {
        if ((existingParam != null && isClassAssignable(existingParam.getType().getName(), Boolean.class))
                || isUnknowTypeParam(existingParam)) {
            return existingParam;
        }
        return new ConstantExpressionWrapper(Boolean.FALSE, parentNode);
    }

    private static ExpressionWrapper generateArgumentForListParam(ExpressionWrapper existingParam,
            ASTNodeWrapper parentNode) {
        if (existingParam instanceof ListExpressionWrapper
                || (existingParam instanceof CastExpressionWrapper && ((CastExpressionWrapper) existingParam).getExpression() instanceof ListExpressionWrapper)
                || isUnknowTypeParam(existingParam)) {
            return existingParam;
        }
        return new ListExpressionWrapper(parentNode);
    }

    private static ExpressionWrapper generateArgumentForTestObjectParam(ExpressionWrapper existingParam,
            ASTNodeWrapper parentNode) {
        if ((existingParam instanceof MethodCallExpressionWrapper && AstEntityInputUtil.isObjectArgument((MethodCallExpressionWrapper) existingParam))
                || (isUnknowTypeParam(existingParam))) {
            return existingParam;
        }
        return AstEntityInputUtil.generateObjectMethodCall(null, parentNode);
    }

    private static boolean isUnknowTypeParam(ExpressionWrapper existingParam) {
        return (existingParam instanceof VariableExpressionWrapper
                || existingParam instanceof MapExpressionWrapper
                || existingParam instanceof CastExpressionWrapper
                || existingParam instanceof BinaryExpressionWrapper
                || (existingParam instanceof PropertyExpressionWrapper && StringUtils.equals(
                        ((PropertyExpressionWrapper) existingParam).getObjectExpressionAsString(),
                        InputValueType.GlobalVariable.getName())) || (existingParam instanceof MethodCallExpressionWrapper && !AstEntityInputUtil.isObjectArgument((MethodCallExpressionWrapper) existingParam)));
    }

    private static ExpressionWrapper generateArgumentForEnumParam(ExpressionWrapper existingParam,
            String paramClassSimpleName, String paramClassFullName, ASTNodeWrapper parentNode) {
        if (existingParam instanceof PropertyExpressionWrapper
                && isClassAssignable(((PropertyExpressionWrapper) existingParam).getObjectExpressionAsString(),
                        paramClassSimpleName, paramClassFullName)) {
            return existingParam;
        }
        if (paramClassFullName.equals(FailureHandling.class.getName())) {
            return getNewFailureHandlingPropertyExpression(parentNode);
        }
        return new ConstantExpressionWrapper(parentNode);
    }

    private static boolean isClassAssignable(String fromClassName, String toClassSimpleName, String toClassFullName) {
        Class<?> targetClass = loadType(fromClassName, null);
        Class<?> paramClass = loadType(toClassFullName, null);
        if (targetClass != null && paramClass != null) {
            return isClassAssignable(targetClass, paramClass);
        }
        return StringUtils.equals(toClassSimpleName, fromClassName)
                && StringUtils.equals(toClassFullName, fromClassName);
    }

    private static boolean isClassAssignable(String fromClassName, Class<?> toClass) {
        Class<?> targetClass = loadType(fromClassName, null);
        if (targetClass != null && toClass != null) {
            return isClassAssignable(targetClass, toClass);
        }
        return StringUtils.equals(toClass.getName(), fromClassName);
    }

    private static boolean isClassAssignable(Class<?> fromClass, Class<?> toClass) {
        return ClassUtils.isAssignable(fromClass, toClass, true);
    }

    public static boolean isVoidClass(Class<?> clazz) {
        return (clazz.getName().equals(Void.class.getName()) || clazz.getName().equals(Void.class.getSimpleName()) || clazz.getName()
                .equals(Void.TYPE.getName()));
    }

    public static boolean isVoidClass(ClassNodeWrapper classNode) {
        return (classNode.getName().equals(Void.class.getName())
                || classNode.getName().equals(Void.class.getSimpleName()) || classNode.getName().equals(
                Void.TYPE.getName()));
    }

    public static boolean isVoidClass(ClassNode classNode) {
        return (classNode.getName().equals(Void.class.getName())
                || classNode.getName().equals(Void.class.getSimpleName()) || classNode.getName().equals(
                Void.TYPE.getName()));
    }

    public static Class<?> loadType(String typeName, ClassNodeWrapper classNode) {
        Class<?> type = null;
        try {
            type = Class.forName(typeName);
            return type;
        } catch (ClassNotFoundException e) {
            // find nothing, continue
        }
        URLClassLoader classLoader = null;
        try {
            classLoader = GroovyUtil.getProjectClasLoader(ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            // find nothing, continue
        }
        if (classLoader == null) {
            return null;
        }
        try {
            type = classLoader.loadClass(typeName);
            return type;
        } catch (ClassNotFoundException e) {
            for (String importedPackage : GroovyParser.GROOVY_IMPORTED_PACKAGES) {
                try {
                    type = classLoader.loadClass(importedPackage + "." + typeName);
                    return type;
                } catch (ClassNotFoundException ex) {
                    continue;
                }
            }
        }
        try {
            type = ClassUtils.getClass(typeName);
        } catch (ClassNotFoundException e) {
            // cannot find class, continue
        }
        if (classNode == null) {
            return type;
        }
        for (ImportNodeWrapper importNode : classNode.getImports()) {
            if (!importNode.getClassName().endsWith("." + typeName)) {
                continue;
            }
            try {
                type = classLoader.loadClass(importNode.getClassName());
                return type;
            } catch (ClassNotFoundException ex) {
                // cannot find class, continue
            }
        }
        return type;
    }

    public static boolean isGlobalVariablePropertyExpression(PropertyExpressionWrapper propertyExprs) {
        if (!(propertyExprs.getObjectExpression() instanceof VariableExpressionWrapper))
            return false;
        return (propertyExprs.getObjectExpressionAsString().equals(InputValueType.GlobalVariable.name()) && propertyExprs.getProperty() instanceof ConstantExpressionWrapper);
    }

    public static boolean isFailureHandlingPropertyExpression(PropertyExpressionWrapper propertyExprs) {
        if (!(propertyExprs.getObjectExpression() instanceof VariableExpressionWrapper))
            return false;
        return (propertyExprs.isObjectExpressionOfClass(FailureHandling.class) && propertyExprs.getProperty() instanceof ConstantExpressionWrapper);
    }

}
