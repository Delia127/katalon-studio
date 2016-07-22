package com.kms.katalon.composer.testcase.util;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.composer.util.groovy.GroovyGuiUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.ast.GroovyParser;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.custom.keyword.KeywordMethod;
import com.kms.katalon.custom.keyword.KeywordParameter;

/**
 * Utility class to process ast input classes for keywords
 *
 */
public class AstKeywordsInputUtil {
    public static ExpressionStatementWrapper createNewCustomKeywordStatement(ASTNodeWrapper parentNode) {
        List<MethodNode> customKeywords = KeywordController.getInstance().getCustomKeywords(
                ProjectController.getInstance().getCurrentProject());
        if (customKeywords == null || customKeywords.isEmpty()) {
            return null;
        }
        MethodNode defaultCustomKeyword = customKeywords.get(0);
        return createNewCustomKeywordStatement(defaultCustomKeyword.getDeclaringClass().getName(),
                KeywordController.getInstance().getCustomKeywordName(defaultCustomKeyword.getName()), parentNode);
    }

    public static ExpressionStatementWrapper createNewCustomKeywordStatement(String keywordClass, String keywordName,
            ASTNodeWrapper parentNode) {
        MethodCallExpressionWrapper keywordMethodCallExpression = new MethodCallExpressionWrapper(keywordClass,
                keywordName, parentNode);
        generateCustomKeywordArguments(keywordMethodCallExpression);
        return new ExpressionStatementWrapper(keywordMethodCallExpression, parentNode);
    }

    public static ExpressionStatementWrapper createBuiltInKeywordStatement(String classSimpleName, String keyword,
            ASTNodeWrapper parentNode) {
        MethodCallExpressionWrapper keywordMethodCallExpression = new MethodCallExpressionWrapper(classSimpleName,
                keyword, parentNode);
        generateBuiltInKeywordArguments(keywordMethodCallExpression);
        return new ExpressionStatementWrapper(keywordMethodCallExpression, parentNode);
    }

    public static List<InputParameter> generateBuiltInKeywordInputParameters(String buitInKWClassSimpleName,
            String keyword, ArgumentListExpressionWrapper argumentListExpression) {
        if (argumentListExpression == null) {
            return null;
        }
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(buitInKWClassSimpleName,
                keyword, argumentListExpression.getArgumentListParameterTypes());
        if (keywordMethod == null || keywordMethod.getParameters().length == 0) {
            return Collections.emptyList();
        }
        return generateInputParameters(keywordMethod, argumentListExpression);
    }

    public static List<InputParameter> generateCustomKeywordInputParameters(String className, String keywordName,
            ArgumentListExpressionWrapper argumentListExpression) {
        if (argumentListExpression == null) {
            return null;
        }
        MethodNode keywordMethod = KeywordController.getInstance().getCustomKeywordByName(className, keywordName,
                ProjectController.getInstance().getCurrentProject());
        return generateInputParameters(new MethodNodeWrapper(keywordMethod, null), argumentListExpression);
    }

    public static List<InputParameter> generateInputParameters(KeywordMethod keywordMethod,
            ArgumentListExpressionWrapper argumentListExpression) {
        List<InputParameter> inputParameters = new ArrayList<InputParameter>();
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            KeywordParameter keywordParam = keywordMethod.getParameters()[i];
            Object value = (argumentListExpression.getExpressions().size() > i)
                    ? argumentListExpression.getExpression(i) : null;
            inputParameters.add(new InputParameter(keywordParam, value));
        }
        return inputParameters;
    }

    public static List<InputParameter> generateInputParameters(MethodNodeWrapper method,
            ArgumentListExpressionWrapper argumentListExpression) {
        if (method == null || argumentListExpression == null || method.getParameters().length == 0) {
            return Collections.emptyList();
        }
        List<InputParameter> inputParameters = new ArrayList<InputParameter>();
        for (int i = 0; i < method.getParameters().length; i++) {
            ParameterWrapper inputParameter = method.getParameters()[i];
            Object value = (argumentListExpression.getExpressions().size() > i)
                    ? argumentListExpression.getExpression(i) : null;
            inputParameters.add(new InputParameter(inputParameter, value));
        }
        return inputParameters;
    }

    public static List<InputParameter> generateInputParameters(Method method,
            ArgumentListExpressionWrapper argumentListExpression) {
        if (method == null || argumentListExpression == null || method.getParameterTypes().length == 0) {
            return Collections.emptyList();
        }
        List<InputParameter> inputParameters = new ArrayList<InputParameter>();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> inputParameterType = method.getParameterTypes()[i];
            Object value = (argumentListExpression.getExpressions().size() > i)
                    ? argumentListExpression.getExpression(i) : null;
            inputParameters.add(new InputParameter(inputParameterType, value));
        }
        return inputParameters;
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
    private static PropertyExpressionWrapper createPropertyExpressionForClassFromPath(String parentPath,
            String childName) {
        if (!parentPath.contains(".")) {
            return new PropertyExpressionWrapper(parentPath, childName);
        }
        PropertyExpressionWrapper childPropertyExpression = createPropertyExpressionForClassFromPath(
                parentPath.substring(0, parentPath.lastIndexOf(".")),
                parentPath.substring(parentPath.lastIndexOf(".") + 1));
        if (childName != null) {
            PropertyExpressionWrapper newPropertyExpression = new PropertyExpressionWrapper();
            newPropertyExpression.setObjectExpression(childPropertyExpression);
            newPropertyExpression.setProperty(new ConstantExpressionWrapper(childName, newPropertyExpression));
            childPropertyExpression.setParent(newPropertyExpression);
            return newPropertyExpression;
        }
        return childPropertyExpression;
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
        ArgumentListExpressionWrapper parentNode = keywordCallExpression.getArguments();
        List<ExpressionWrapper> exisitingArgumentList = parentNode.getExpressions();
        List<ExpressionWrapper> newArgumentList = new ArrayList<ExpressionWrapper>();
        String keywordName = keywordMethod.getName();
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            ExpressionWrapper existingParam = (i < exisitingArgumentList.size()) ? exisitingArgumentList.get(i) : null;
            KeywordParameter keywordParam = keywordMethod.getParameters()[i];
            newArgumentList.add(generateExpressionForKeyword(keywordName, keywordParam, existingParam, parentNode));
        }
        parentNode.setExpressions(newArgumentList);
    }

    public static void generateMethodCallArguments(MethodCallExpressionWrapper keywordCallExpression, Method method) {
        ArgumentListExpressionWrapper parentNode = keywordCallExpression.getArguments();
        List<ExpressionWrapper> exisitingArgumentList = parentNode.getExpressions();
        List<ExpressionWrapper> newArgumentList = new ArrayList<ExpressionWrapper>();
        String methodName = method.getName();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> keywordParam = method.getParameterTypes()[i];
            ExpressionWrapper existingParam = (i < exisitingArgumentList.size()) ? exisitingArgumentList.get(i) : null;
            newArgumentList.add(generateExpressionForKeyword(methodName, keywordParam, existingParam, parentNode));
        }
        parentNode.setExpressions(newArgumentList);
    }

    public static void generateCustomKeywordArguments(MethodCallExpressionWrapper keywordCallExpression) {
        if (keywordCallExpression == null || keywordCallExpression.getMethod() == null) {
            return;
        }
        MethodNode keywordMethod = KeywordController.getInstance().getCustomKeywordByName(
                keywordCallExpression.getObjectExpressionAsString(), keywordCallExpression.getMethodAsString(),
                ProjectController.getInstance().getCurrentProject());
        if (keywordMethod == null) {
            return;
        }
        ArgumentListExpressionWrapper parentNode = keywordCallExpression.getArguments();
        String methodName = keywordMethod.getName();
        List<ExpressionWrapper> exisitingArgumentList = parentNode.getExpressions();
        List<ExpressionWrapper> newArgumentList = new ArrayList<ExpressionWrapper>();
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            ClassNode classNode = keywordMethod.getParameters()[i].getType();
            if (classNode == null) {
                continue;
            }
            ExpressionWrapper existingParam = (i < exisitingArgumentList.size()) ? exisitingArgumentList.get(i) : null;
            newArgumentList.add(generateExpressionForKeyword(methodName, classNode, existingParam, parentNode));
        }
        parentNode.setExpressions(newArgumentList);
    }

    private static ExpressionWrapper generateExpressionForKeyword(String keywordName, KeywordParameter keywordParam,
            ExpressionWrapper existingParam, ASTNodeWrapper parentNode) {
        Class<?> keywordParamType = keywordParam.getType();
        return generateArgument(keywordName, keywordParam.getName(), keywordParamType.getSimpleName(),
                keywordParamType.getName(), keywordParamType.isArray(), keywordParamType.isEnum(), existingParam,
                parentNode);
    }

    private static ExpressionWrapper generateExpressionForKeyword(String methodName, Class<?> keywordParam,
            ExpressionWrapper existingParam, ArgumentListExpressionWrapper parentNode) {
        return generateArgument(methodName, keywordParam.getName(), keywordParam.getSimpleName(),
                keywordParam.getName(), keywordParam.isArray(), keywordParam.isEnum(), existingParam, parentNode);
    }

    private static ExpressionWrapper generateExpressionForKeyword(String methodName, ClassNode classNode,
            ExpressionWrapper existingParam, ArgumentListExpressionWrapper parentNode) {
        String classNameWithoutPackage = classNode.getNameWithoutPackage();
        return generateArgument(methodName, classNameWithoutPackage, classNameWithoutPackage, classNode.getName(),
                classNode.isArray(), classNode.isEnum(), existingParam, parentNode);
    }

    public static ExpressionWrapper generateArgument(String methodName, String paramName, String paramClassSimpleName,
            String paramClassFullName, boolean isArrayParam, boolean isEnumParam, ExpressionWrapper existingParam,
            ASTNodeWrapper parentNode) {
        if (isDelaySecondParam(methodName, paramName)) {
            return generateArgumentForDelaySecondParam(existingParam, parentNode);
        }
        if (existingParam != null && !isFailureHandlingExpression(existingParam)
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
        if (existingParam instanceof ExpressionWrapper && !isFailureHandlingExpression(existingParam)) {
            return existingParam;
        }
        return new ConstantExpressionWrapper(parentNode);
    }

    public static boolean isFailureHandlingExpression(ExpressionWrapper expression) {
        return expression instanceof PropertyExpressionWrapper
                && isFailureHandlingPropertyExpression((PropertyExpressionWrapper) expression);
    }

    private static boolean isFailureHandlingPropertyExpression(PropertyExpressionWrapper propertyExpression) {
        if (!(propertyExpression.getObjectExpression() instanceof VariableExpressionWrapper))
            return false;
        return (propertyExpression.isObjectExpressionOfClass(FailureHandling.class) && propertyExpression.getProperty() instanceof ConstantExpressionWrapper);
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
        if (isFindTestObjectMethodCall(existingParam) || (isUnknowTypeParam(existingParam))) {
            return existingParam;
        }
        return AstEntityInputUtil.createNewFindTestObjectMethodCall(null, parentNode);
    }

    private static boolean isFindTestObjectMethodCall(ExpressionWrapper existingParam) {
        return existingParam instanceof MethodCallExpressionWrapper
                && ((MethodCallExpressionWrapper) existingParam).isFindTestObjectMethodCall();
    }

    private static boolean isUnknowTypeParam(ExpressionWrapper existingParam) {
        return (existingParam instanceof VariableExpressionWrapper || existingParam instanceof MapExpressionWrapper
                || existingParam instanceof CastExpressionWrapper || existingParam instanceof BinaryExpressionWrapper
                || isGlobalVariableExpression(existingParam) || (existingParam instanceof MethodCallExpressionWrapper && !isFindTestObjectMethodCall(existingParam)));
    }

    private static boolean isGlobalVariableExpression(ExpressionWrapper existingParam) {
        return existingParam instanceof PropertyExpressionWrapper
                && StringUtils.equals(((PropertyExpressionWrapper) existingParam).getObjectExpressionAsString(),
                        InputValueType.GlobalVariable.getName());
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
            classLoader = GroovyGuiUtil.getProjectClasLoader(ProjectController.getInstance().getCurrentProject());
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
            String className = importNode.getClassName();
            if (className == null || !className.endsWith("." + typeName)) {
                continue;
            }
            try {
                return classLoader.loadClass(className);
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
}
