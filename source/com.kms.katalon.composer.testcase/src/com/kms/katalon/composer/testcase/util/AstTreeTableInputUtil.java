package com.kms.katalon.composer.testcase.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.editors.StringComboBoxCellEditor;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.editors.BinaryCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.BooleanCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.CaseCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.CatchCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ClosureListInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ForInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ListInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.MapInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.MethodCallInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.PropertyInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.RangeInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.SwitchCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestDataValueCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestObjectCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ThrowInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ThrowableInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TypeInputCellEditor;
import com.kms.katalon.composer.testcase.editors.CallTestCaseCellEditor;
import com.kms.katalon.composer.testcase.editors.NumberCellEditor;
import com.kms.katalon.composer.testcase.model.CustomInputValueTypeCollector;
import com.kms.katalon.composer.testcase.model.ICustomInputValueType;
import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputParameterClass;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.composer.testsuite.editors.TestDataCellEditor;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.ast.AstTextValueUtil;
import com.kms.katalon.core.ast.GroovyParser;
import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCase;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.custom.factory.BuiltInMethodNodeFactory;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class AstTreeTableInputUtil {
    public static CellEditor getCellEditorForAstObject(Composite parent, Object astObject, ClassNode scriptClass) {
        CellEditor customCellEditor = getCellEditorFromCustomCollector(parent, astObject, scriptClass);
        if (customCellEditor != null) {
            return customCellEditor;
        }
        if (astObject instanceof Expression) {
            return getCellEditorForExpression(parent, (Expression) astObject, scriptClass);
        } else if (astObject instanceof Statement) {
            return getCellEditorForStatement(parent, (Statement) astObject, scriptClass);
        } else if (astObject instanceof Token) {
            return getCellEditorForToken(parent, (Token) astObject);
        } else if (astObject instanceof Parameter) {
            return getCellEditorForParameter(parent, (Parameter) astObject);
        } else if (astObject instanceof ClassNode) {
            return new TypeInputCellEditor(parent, AstTextValueUtil.getTextValue(astObject));
        }
        return null;
    }

    public static CellEditor getCellEditorFromCustomCollector(Composite parent, Object astObject, ClassNode scriptClass) {
        for (IInputValueType customValueType : CustomInputValueTypeCollector.getInstance()
                .getAllCustomInputValueTypes()) {
            if (customValueType.isEditable(astObject, scriptClass)) {
                return customValueType.getCellEditorForValue(parent, astObject, scriptClass);
            }
        }
        return null;
    }

    public static CellEditor getCellEditorForParameter(Composite parent, Parameter parameter) {
        return new TextCellEditor(parent);
    }

    public static CellEditor getCellEditorForToken(Composite parent, Token token) {
        String[] names = new String[AstTreeTableValueUtil.OPERATION_CODES.length];

        for (int i = 0; i < AstTreeTableValueUtil.OPERATION_CODES.length; i++) {
            names[i] = Types.getText(AstTreeTableValueUtil.OPERATION_CODES[i]);
        }
        return new ComboBoxCellEditor(parent, names);
    }

    public static CellEditor getCellEditorForStatement(Composite parent, Statement statement, ClassNode scriptClass) {
        if (statement instanceof AssertStatement) {
            return new BooleanCellEditor(parent, AstTextValueUtil.getTextValue(((AssertStatement) statement)
                    .getBooleanExpression()), scriptClass);
        } else if (statement instanceof ExpressionStatement) {
            return getCellEditorForExpression(parent, ((ExpressionStatement) statement).getExpression(), scriptClass);
        } else if (statement instanceof IfStatement) {
            return new BooleanCellEditor(parent, AstTextValueUtil.getTextValue(((IfStatement) statement)
                    .getBooleanExpression()), scriptClass);
        } else if (statement instanceof ForStatement) {
            return new ForInputCellEditor(parent,
                    AstTextValueUtil.getInputTextValue((ForStatement) statement), scriptClass);
        } else if (statement instanceof WhileStatement) {
            return new BooleanCellEditor(parent, AstTextValueUtil.getTextValue(((WhileStatement) statement)
                    .getBooleanExpression()), scriptClass);
        } else if (statement instanceof SwitchStatement) {
            return new SwitchCellEditor(parent, AstTextValueUtil.getTextValue(((SwitchStatement) statement)
                    .getExpression()), scriptClass);
        } else if (statement instanceof CaseStatement) {
            return new CaseCellEditor(parent, AstTextValueUtil.getTextValue(((CaseStatement) statement)
                    .getExpression()), scriptClass);
        } else if (statement instanceof CatchStatement) {
            return new CatchCellEditor(parent, AstTextValueUtil.getTextValue(((CatchStatement) statement)
                    .getVariable()), scriptClass);
        } else if (statement instanceof ThrowStatement) {
            return new ThrowInputCellEditor(parent, AstTextValueUtil.getTextValue(((ThrowStatement) statement)
                    .getExpression()), scriptClass);
        }
        return null;
    }

    public static CellEditor getCellEditorForExpression(Composite parent, Expression expression, ClassNode scriptClass) {
        if (expression instanceof BinaryExpression) {
            return getCellEditorForBinaryExpression(parent, (BinaryExpression) expression, scriptClass);
        } else if (expression instanceof MethodCallExpression) {
            return getCellEditorForMethodCallExpression(parent, (MethodCallExpression) expression, scriptClass);
        } else if (expression instanceof BooleanExpression) {
            return getCellEditorForBooleanExpression(parent, (BooleanExpression) expression, scriptClass);
        } else if (expression instanceof ConstantExpression) {
            return getCellEditorForConstantExpression(parent, (ConstantExpression) expression);
        } else if (expression instanceof VariableExpression) {
            return getCellEditorForVariableExpression(parent, (VariableExpression) expression, scriptClass);
        } else if (expression instanceof RangeExpression) {
            return getCellEditorForRangeExpression(parent, (RangeExpression) expression, scriptClass);
        } else if (expression instanceof ClosureListExpression) {
            return getCellEditorForClosureListExpression(parent, (ClosureListExpression) expression, scriptClass);
        } else if (expression instanceof ListExpression) {
            return getCellEditorForListExpression(parent, (ListExpression) expression, scriptClass);
        } else if (expression instanceof MapExpression) {
            return getCellEditorForMapExpression(parent, (MapExpression) expression, scriptClass);
        } else if (expression instanceof PropertyExpression) {
            return getCellEditorForPropertyExpression(parent, (PropertyExpression) expression, scriptClass);
        } else if (expression instanceof ClassExpression) {
            return getCellEditorForClassExpression(parent, (ClassExpression) expression, scriptClass);
        } else if (expression instanceof ConstructorCallExpression) {
            return getCellEditorForConstructorCallExpression(parent, (ConstructorCallExpression) expression,
                    scriptClass);
        }
        return null;
    }

    private static CellEditor getCellEditorForConstructorCallExpression(Composite parent,
            ConstructorCallExpression contructorCallExpression, ClassNode scriptClass) {
        Class<?> throwableClass = AstTreeTableInputUtil.loadType(contructorCallExpression.getType().getName(), scriptClass);
        if (Throwable.class.isAssignableFrom(throwableClass)) {
            return new ThrowableInputCellEditor(parent,
                    AstTextValueUtil.getTextValue(contructorCallExpression), scriptClass);
        }
        return null;
    }

    private static CellEditor getCellEditorForClassExpression(Composite parent, ClassExpression classExpression,
            ClassNode scriptClass) {
        return new TypeInputCellEditor(parent, AstTextValueUtil.getTextValue(classExpression));
    }

    protected static CellEditor getCellEditorForPropertyExpression(Composite parent,
            PropertyExpression propertyExpression, ClassNode scriptClass) {
        if (isGlobalVariablePropertyExpression(propertyExpression)) {
            return getCellEditorForGlobalVariableExpression(parent);
        }
        Class<?> type = loadType(propertyExpression.getObjectExpression().getText(), scriptClass);
        if (type != null && type.isEnum() && type.getName().equals(FailureHandling.class.getName())) {
            List<String> enumValues = new ArrayList<String>();
            for (Object enumObject : type.getEnumConstants()) {
                enumValues.add(enumObject.toString());
            }
            return new ComboBoxCellEditor(parent, enumValues.toArray(new String[enumValues.size()]));
        }
        type = loadType(propertyExpression.getText(), scriptClass);
        if (type != null) {
            return new TypeInputCellEditor(parent, AstTextValueUtil.getTextValue(propertyExpression));
        }
        return new PropertyInputCellEditor(parent, AstTextValueUtil.getTextValue(propertyExpression),
                scriptClass);
    }

    private static CellEditor getCellEditorForBinaryExpression(Composite parent, BinaryExpression binaryExpression,
            ClassNode scriptClass) {
        return new BinaryCellEditor(parent, AstTextValueUtil.getTextValue(binaryExpression), scriptClass);
    }

    private static CellEditor getCellEditorForMethodCallExpression(Composite parent,
            MethodCallExpression methodCallExpression, ClassNode scriptClass) {
        if (isCallTestCaseArgument(methodCallExpression)) {
            return getCellEditorForCallTestCase(parent, methodCallExpression);
        }
        if (isObjectArgument(methodCallExpression)) {
            return getCellEditorForTestObject(parent, methodCallExpression, scriptClass);
        }
        if (isTestDataArgument(methodCallExpression)) {
            return getCellEditorForTestData(parent, methodCallExpression);
        }
        if (isTestDataValueArgument(methodCallExpression)) {
            return new TestDataValueCellEditor(parent, AstTextValueUtil.getTextValue(methodCallExpression),
                    scriptClass);
        }
        return new MethodCallInputCellEditor(parent, AstTextValueUtil.getTextValue(methodCallExpression),
                scriptClass);
    }

    private static CellEditor getCellEditorForTestData(Composite parent, MethodCallExpression methodCallExpression) {
        ArgumentListExpression argumentListExpression = (ArgumentListExpression) methodCallExpression.getArguments();
        if (!argumentListExpression.getExpressions().isEmpty()) {
            String pk = argumentListExpression.getExpressions().get(0).getText();
            return new TestDataCellEditor(parent, AstTextValueUtil.getTextValue(methodCallExpression), pk);
        }
        return null;
    }

    private static CellEditor getCellEditorForBooleanExpression(Composite parent, BooleanExpression booleanExpression,
            ClassNode scriptClass) {
        return new BooleanCellEditor(parent, AstTextValueUtil.getTextValue(booleanExpression), scriptClass);
    }

    private static CellEditor getCellEditorForRangeExpression(Composite parent, RangeExpression rangeExpression,
            ClassNode scriptClass) {
        return new RangeInputCellEditor(parent, AstTextValueUtil.getTextValue(rangeExpression), scriptClass);
    }

    private static CellEditor getCellEditorForClosureListExpression(Composite parent,
            ClosureListExpression closureListExpression, ClassNode scriptClass) {
        return new ClosureListInputCellEditor(parent, AstTextValueUtil.getTextValue(closureListExpression),
                scriptClass);
    }

    private static CellEditor getCellEditorForConstantExpression(Composite parent, ConstantExpression constantExpression) {
        if (constantExpression.getValue() instanceof Boolean) {
            return new ComboBoxCellEditor(parent, new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() });
        }
        if (constantExpression.getValue() instanceof Number) {
            return new NumberCellEditor(parent);
        }
        return new TextCellEditor(parent);
    }

    private static CellEditor getCellEditorForVariableExpression(Composite parent,
            VariableExpression variableExpression, ClassNode scriptClass) {
        if (variableExpression.getText().equals("this")) {
            return null;
        }
        Type type = loadType(variableExpression.getText(), scriptClass);
        if (type != null) {
            return new TypeInputCellEditor(parent, AstTextValueUtil.getTextValue(variableExpression));
        }
        List<String> variableStringList = new ArrayList<String>();
        for (FieldNode field : scriptClass.getFields()) {
            variableStringList.add(field.getName());
        }
        return new StringComboBoxCellEditor(parent, variableStringList.toArray(new String[variableStringList.size()]));
    }

    private static CellEditor getCellEditorForListExpression(Composite parent, ListExpression listExpression,
            ClassNode scriptClass) {
        return new ListInputCellEditor(parent, AstTextValueUtil.getTextValue(listExpression), scriptClass);
    }

    private static CellEditor getCellEditorForMapExpression(Composite parent, MapExpression mapExpression,
            ClassNode scriptClass) {
        return new MapInputCellEditor(parent, AstTextValueUtil.getTextValue(mapExpression), scriptClass);
    }

    private static CellEditor getCellEditorForCallTestCase(Composite parent, MethodCallExpression methodCallExpression) {
        String testCasePk = null;
        Expression callTestCaseValueExpression = AstTreeTableInputUtil.getCallTestCaseParam(methodCallExpression);
        if (callTestCaseValueExpression instanceof ConstantExpression
                && ((ConstantExpression) callTestCaseValueExpression).getValue() instanceof String) {
            testCasePk = (String) ((ConstantExpression) callTestCaseValueExpression).getValue();
        }
        return new CallTestCaseCellEditor(parent, AstTextValueUtil.getTextValue(methodCallExpression),
                testCasePk);
    }

    private static CellEditor getCellEditorForTestObject(Composite parent, MethodCallExpression methodCallExpression,
            ClassNode scriptClass) {
        return new TestObjectCellEditor(parent, AstTextValueUtil.getTextValue(methodCallExpression),
                scriptClass, false);
    }

    private static CellEditor getCellEditorForGlobalVariableExpression(Composite parent) {
        try {
            String[] names = GlobalVariableController.getInstance().getAllGlobalVariableNames(
                    ProjectController.getInstance().getCurrentProject());
            return new ComboBoxCellEditor(parent, names);
        } catch (Exception e) {
            return new ComboBoxCellEditor(parent, new String[0]);
        }
    }

    public static List<IInputValueType> getInputValueTypeList(IInputValueType[] defaultInputValueTypes, String customTag) {
        List<IInputValueType> inputValueTypeList = new ArrayList<IInputValueType>();
        for (IInputValueType builtinValueType : defaultInputValueTypes) {
            inputValueTypeList.add(builtinValueType);
        }
        for (ICustomInputValueType customInputValueType : CustomInputValueTypeCollector.getInstance()
                .getAllCustomInputValueTypes()) {
            if (containsTag(customInputValueType, customTag)) {
                inputValueTypeList.add(customInputValueType);
            }
        }
        return inputValueTypeList;
    }

    public static List<String> getInputValueTypeStringList(IInputValueType[] defaultInputValueTypes, String customTag) {
        List<String> inputValueTypeStringList = new ArrayList<String>();
        for (IInputValueType builtinValueType : defaultInputValueTypes) {
            inputValueTypeStringList.add(builtinValueType.getName());
        }
        for (ICustomInputValueType customInputValueType : CustomInputValueTypeCollector.getInstance()
                .getAllCustomInputValueTypes()) {
            if (containsTag(customInputValueType, customTag)) {
                inputValueTypeStringList.add(customInputValueType.getName());
            }
        }
        return inputValueTypeStringList;
    }

    private static boolean containsTag(ICustomInputValueType valueType, String tag) {
        if (tag.equalsIgnoreCase(ICustomInputValueType.TAG_ALL)) {
            return true;
        }
        for (String valueTypeTag : valueType.getTags()) {
            if (valueTypeTag.equalsIgnoreCase(ICustomInputValueType.TAG_ALL) || tag.equalsIgnoreCase(valueTypeTag)) {
                return true;
            }
        }
        return false;
    }

    public static IInputValueType getInputValueTypeFromString(String inputValueTypeName) {
        for (IInputValueType customInputValueType : CustomInputValueTypeCollector.getInstance()
                .getAllCustomInputValueTypes()) {
            if (customInputValueType.getName().equals(inputValueTypeName)) {
                return customInputValueType;
            }
        }
        for (IInputValueType builtinValueType : InputValueType.values()) {
            if (builtinValueType.getName().equals(inputValueTypeName)) {
                return builtinValueType;
            }
        }
        return null;
    }

    public static ExpressionStatement createBuiltInKeywordMethodCall(String classSimpleName, String keyword)
            throws Exception {
        List<Expression> expressionArguments = new ArrayList<Expression>();
        MethodCallExpression keywordMethodCallExpression = new MethodCallExpression(new VariableExpression(
                classSimpleName), keyword, new ArgumentListExpression(expressionArguments));
        generateBuiltInKeywordArguments(keywordMethodCallExpression);
        return new ExpressionStatement(keywordMethodCallExpression);
    }

    public static ExpressionStatement createCustomKeywordMethodCall(String keywordClass, String keywordName)
            throws Exception {
        List<Expression> expressionArguments = new ArrayList<Expression>();
        MethodCallExpression keywordMethodCallExpression = new MethodCallExpression(
                new VariableExpression(keywordClass), keywordName, new ArgumentListExpression(expressionArguments));
        generateCustomKeywordArguments(keywordMethodCallExpression);
        return new ExpressionStatement(keywordMethodCallExpression);
    }

    public static List<InputParameter> getBuiltInKeywordInputParameters(String buitInKWClassSimpleName, String keyword,
            ArgumentListExpression argumentListExpression) throws Exception {
        if (argumentListExpression != null) {
            Method keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(buitInKWClassSimpleName,
                    keyword);
            return generateInputParameters(argumentListExpression, keywordMethod, KeywordController.getInstance()
                    .getParameterName(keywordMethod));
        }
        return null;
    }

    public static List<InputParameter> getCustomKeywordInputParameters(String className, String keywordName,
            ArgumentListExpression argumentListExpression) throws Exception {
        if (argumentListExpression != null) {
            MethodNode keywordMethod = KeywordController.getInstance().getCustomKeywordByName(className, keywordName,
                    ProjectController.getInstance().getCurrentProject());
            return generateInputParameters(argumentListExpression, keywordMethod);
        }
        return null;
    }

    public static List<InputParameter> generateInputParameters(ArgumentListExpression argumentListExpression,
            Method method) throws Exception {
        if (method != null) {
            List<String> paramNames = getParameterNames(method);
            return generateInputParameters(argumentListExpression, method, paramNames);
        }
        return null;
    }

    public static List<InputParameter> generateInputParameters(ArgumentListExpression argumentListExpression,
            Method method, List<String> paramNames) {
        List<Type> paramTypes = getParamTypes(method);
        if (paramNames != null && paramNames.size() == paramTypes.size()) {
            List<InputParameter> inputParameters = new ArrayList<InputParameter>();
            for (int i = 0; i < paramTypes.size(); i++) {
                InputParameterClass inputParameterClass = convertToInputParameterClass(paramTypes.get(i));
                if (argumentListExpression.getExpressions().size() > i) {
                    inputParameters.add(getInputParameter(paramNames.get(i), inputParameterClass,
                            argumentListExpression.getExpression(i)));
                } else {
                    inputParameters.add(getDefaultInputParameter(paramNames.get(i), inputParameterClass));
                }
            }
            return inputParameters;
        }
        return null;
    }

    public static List<InputParameter> generateInputParameters(ArgumentListExpression argumentListExpression,
            MethodNode method) {
        if (method != null) {
            List<InputParameter> inputParameters = new ArrayList<InputParameter>();
            for (int i = 0; i < method.getParameters().length; i++) {
                InputParameterClass inputParameterClass = convertToInputParameterClass(method.getParameters()[i]
                        .getType());
                if (argumentListExpression.getExpressions().size() > i) {
                    inputParameters.add(getInputParameter(method.getParameters()[i].getName(), inputParameterClass,
                            argumentListExpression.getExpression(i)));
                } else {
                    inputParameters.add(getDefaultInputParameter(method.getParameters()[i].getName(),
                            inputParameterClass));
                }
            }
            return inputParameters;
        }
        return null;
    }

    public static List<String> getParameterNames(Method method) throws IOException {
        if (method != null) {
            List<String> parameterNames = new ArrayList<String>();
            for (Type type : method.getGenericParameterTypes()) {
                if (type instanceof Class<?>) {
                    Class<?> clazz = (Class<?>) type;
                    if (clazz.isArray()) {
                        if (clazz.getComponentType() != null) {
                            parameterNames.add(clazz.getComponentType().getSimpleName() + "[]");
                        } else {
                            parameterNames.add(Object.class.getSimpleName() + "[]");
                        }
                    } else {
                        parameterNames.add(clazz.getSimpleName());
                    }
                }
            }
            return parameterNames;
        }
        return Collections.emptyList();
    }

    public static List<Class<?>> getParamClasses(Method method) {
        if (method != null) {
            List<Class<?>> parameterClasses = new ArrayList<Class<?>>();
            for (Type type : method.getGenericParameterTypes()) {
                if (type instanceof Class<?>) {
                    parameterClasses.add(((Class<?>) type));
                } else if (type instanceof ParameterizedType
                        && ((ParameterizedType) type).getRawType() instanceof Class<?>) {
                    Class<?> clazz = (Class<?>) ((ParameterizedType) type).getRawType();
                    if (clazz.getName().equals(Map.class.getName())) {
                        parameterClasses.add(clazz);
                    }
                }
            }
            return parameterClasses;
        }
        return Collections.emptyList();
    }

    public static List<Class<?>> getParamClasses(MethodNode method) {
        if (method != null) {
            List<Class<?>> parameterClasses = new ArrayList<Class<?>>();
            for (Parameter param : method.getParameters()) {
                parameterClasses.add(param.getType().getTypeClass());
            }
            return parameterClasses;
        }
        return Collections.emptyList();
    }

    public static List<Type> getParamTypes(Method method) {
        if (method != null) {
            List<Type> parameterTypes = new ArrayList<Type>();
            for (Type type : method.getGenericParameterTypes()) {
                if (type instanceof Class<?>) {
                    parameterTypes.add(type);
                } else if (type instanceof ParameterizedType
                        && ((ParameterizedType) type).getRawType() instanceof Class<?>) {
                    Class<?> clazz = (Class<?>) ((ParameterizedType) type).getRawType();
                    if (clazz.getName().equals(Map.class.getName())) {
                        parameterTypes.add(type);
                    }
                }
            }
            return parameterTypes;
        }
        return Collections.emptyList();
    }

    public static InputParameterClass convertToInputParameterClass(Type type) {
        Class<?> clazz = null;
        if (type instanceof Class<?>) {
            clazz = (Class<?>) type;
        } else if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() instanceof Class<?>) {
            clazz = (Class<?>) ((ParameterizedType) type).getRawType();
        }
        if (clazz != null) {
            InputParameterClass inputParameterClass = new InputParameterClass(clazz.getName(), clazz.getSimpleName());
            inputParameterClass.setModifiers(clazz.getModifiers());
            if (clazz.isArray() && clazz.getComponentType() != null) {
                inputParameterClass.setArray(true);
                Class<?> componentType = clazz.getComponentType();
                inputParameterClass.setComponentType(new InputParameterClass(componentType.getName(), componentType
                        .getSimpleName()));
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
                typeList.add(new InputParameterClass(Object.class.getName(), Object.class.getSimpleName()));
            }
            inputParameterClass.setActualTypeArguments(typeList);
            return inputParameterClass;
        }
        return null;
    }

    public static InputParameterClass convertToInputParameterClass(ClassNode classNode) {
        if (classNode != null) {
            InputParameterClass inputParameterClass = new InputParameterClass(classNode.getName(),
                    classNode.getNameWithoutPackage());
            inputParameterClass.setModifiers(classNode.getModifiers());
            if (classNode.isArray() && classNode.getComponentType() != null) {
                inputParameterClass.setArray(true);
                ClassNode componentType = classNode.getComponentType();
                inputParameterClass.setComponentType(new InputParameterClass(componentType.getName(), componentType
                        .getNameWithoutPackage()));
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
        return null;
    }

    public static InputParameter getDefaultInputParameter(String paramName, InputParameterClass parameterClass) {
        return new InputParameter(paramName, parameterClass, null);
    }

    public static InputParameter getInputParameter(String paramName, InputParameterClass paramClass,
            Expression expression) {
        if (expression instanceof CastExpression) {
            return getInputParameter(paramName, paramClass, (CastExpression) expression);
        }
        if (expression instanceof ArrayExpression) {
            return getInputParameter(paramName, paramClass, (ArrayExpression) expression);
        } else if (expression != null) {
            return new InputParameter(paramName, paramClass, expression);
        }
        return getDefaultInputParameter(paramName, paramClass);
    }

    public static InputParameter getInputParameter(String paramName, InputParameterClass paramClass,
            CastExpression castExpression) {
        return getInputParameter(paramName, paramClass, castExpression.getExpression());
    }

    public static InputParameter getInputParameter(String paramName, InputParameterClass paramClass,
            ArrayExpression arrayExpression) {
        return new InputParameter(paramName, paramClass, new ArrayList<Expression>(arrayExpression.getExpressions()));
    }

    public static Expression createPropertyExpressionForClass(String className) {
        if (className.equals(FailureHandling.class.getName())
                || className.equals(FailureHandling.class.getSimpleName())) {
            return AstTreeTableInputUtil.createPropertyExpressionForClass(FailureHandling.class.getSimpleName(),
                    TestCasePreferenceDefaultValueInitializer.getDefaultFailureHandling().name());
        }
        return createPropertyExpressionForClass(className, null);
    }

    public static Expression createPropertyExpressionForClass(String parentPath, String childName) {
        if (parentPath.contains(".")) {
            return new PropertyExpression(createPropertyExpressionForClass(
                    parentPath.substring(0, parentPath.lastIndexOf(".")),
                    parentPath.substring(parentPath.lastIndexOf(".") + 1)), childName);
        } else {
            return new PropertyExpression(new VariableExpression(parentPath), childName);
        }
    }

    public static Expression getArgumentExpression(InputParameter inputParameter) {
        if (inputParameter != null) {
            InputParameterClass paramClass = inputParameter.getParamType();
            if (inputParameter.getValue() instanceof ListExpression) {
                if (paramClass.isArray() && paramClass.getComponentType() != null) {
                    return new CastExpression(new ClassNode(new ClassNode(paramClass.getComponentType().getFullName(),
                            paramClass.getModifiers(), new ClassNode(Object.class))),
                            (ListExpression) inputParameter.getValue());
                }
            }
            if (inputParameter.getValue() instanceof Expression) {
                return (Expression) inputParameter.getValue();
            }
        }
        return new ConstantExpression(null);
    }

    public static boolean isObjectArgument(MethodCallExpression objectMethodCallExpression) {
        if ((objectMethodCallExpression.getObjectExpression().getText().equals(ObjectRepository.class.getName()) || objectMethodCallExpression
                .getObjectExpression().getText().equals(ObjectRepository.class.getSimpleName()))
                && objectMethodCallExpression.getArguments() instanceof ArgumentListExpression) {
            return true;
        }
        return false;
    }

    public static boolean isTestDataArgument(MethodCallExpression objectMethodCallExpression) {
        if ((objectMethodCallExpression.getObjectExpression().getText().equals(TestDataFactory.class.getName()) || objectMethodCallExpression
                .getObjectExpression().getText().equals(TestDataFactory.class.getSimpleName()))
                && objectMethodCallExpression.getMethodAsString().equals("findTestData")
                && objectMethodCallExpression.getArguments() instanceof ArgumentListExpression) {
            return true;
        }
        return false;
    }

    public static boolean isTestDataValueArgument(MethodCallExpression objectMethodCallExpression) {
        if (objectMethodCallExpression.getObjectExpression() instanceof MethodCallExpression
                && isTestDataArgument((MethodCallExpression) objectMethodCallExpression.getObjectExpression())
                && objectMethodCallExpression.getMethodAsString().equals("getValue")) {
            return true;
        }
        return false;
    }

    public static boolean isCallTestCaseMethod(MethodCallExpression methodCallExpression) {
        if (methodCallExpression.getObjectExpression().getText().contains("BuiltInKeywords")
                && methodCallExpression.getMethod().getText().equals("callTestCase")
                && methodCallExpression.getArguments() instanceof ArgumentListExpression) {
            return true;
        }
        return false;
    }

    public static boolean isCallTestCaseArgument(MethodCallExpression callTestCaseMethodCallExpression) {
        if (callTestCaseMethodCallExpression != null
                && (callTestCaseMethodCallExpression.getObjectExpression().getText()
                        .equals(TestCaseFactory.class.getName()) || callTestCaseMethodCallExpression
                        .getObjectExpression().getText().equals(TestCaseFactory.class.getSimpleName()))
                && callTestCaseMethodCallExpression.getMethodAsString().equals("findTestCase")
                && callTestCaseMethodCallExpression.getArguments() instanceof ArgumentListExpression) {
            return true;
        }
        return false;
    }

    private static ArgumentListExpression getCallTestCaseArgumentList(MethodCallExpression methodCallExpression) {
        if (isCallTestCaseArgument(methodCallExpression)) {
            return (ArgumentListExpression) methodCallExpression.getArguments();
        }
        return null;
    }

    public static Expression getCallTestCaseParam(MethodCallExpression methodCallExpression) {
        ArgumentListExpression objectArguments = getCallTestCaseArgumentList(methodCallExpression);
        if (objectArguments != null && objectArguments.getExpressions() != null
                && objectArguments.getExpressions().size() > 0) {
            return objectArguments.getExpression(0);
        }
        return null;
    }

    public static Expression getObjectParam(MethodCallExpression methodCallExpression) {
        if (isObjectArgument(methodCallExpression)) {
            ArgumentListExpression argumentList = (ArgumentListExpression) methodCallExpression.getArguments();
            if (argumentList.getExpressions() != null && !argumentList.getExpressions().isEmpty()) {
                return argumentList.getExpression(0);
            }
        }
        return null;
    }

    public static Expression getTestDataObject(MethodCallExpression methodCallExpression) {
        if (isTestDataArgument(methodCallExpression)) {
            ArgumentListExpression argumentList = (ArgumentListExpression) methodCallExpression.getArguments();
            if (argumentList.getExpressions() != null && !argumentList.getExpressions().isEmpty()) {
                return argumentList.getExpression(0);
            }
        }
        return null;
    }

    public static ArgumentListExpression getTestDataValueArgument(MethodCallExpression methodCallExpression) {
        if (isTestDataValueArgument(methodCallExpression)
                && methodCallExpression.getArguments() instanceof ArgumentListExpression) {
            return (ArgumentListExpression) methodCallExpression.getArguments();
        }
        return null;
    }

    public static Expression getTestDataValueObject(MethodCallExpression methodCallExpression) {
        if (isTestDataValueArgument(methodCallExpression)
                && methodCallExpression.getObjectExpression() instanceof MethodCallExpression) {
            return getTestDataObject((MethodCallExpression) methodCallExpression.getObjectExpression());
        }
        return null;
    }

    public static void generateBuiltInKeywordArguments(MethodCallExpression keywordCallExpression) throws Exception {
        if (keywordCallExpression != null && keywordCallExpression.getMethod() != null) {
            String className = keywordCallExpression.getObjectExpression().getText();
            Method keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(className,
                    keywordCallExpression.getMethod().getText());
            if (keywordMethod != null) {
                generateMethodCallArguments(keywordCallExpression, keywordMethod);
            }
        }
    }

    public static void generateMethodCallArguments(MethodCallExpression keywordCallExpression, Method keywordMethod) {
        List<Expression> exisitingArgumentList = ((ArgumentListExpression) keywordCallExpression.getArguments())
                .getExpressions();
        List<Expression> newArgumentList = new ArrayList<Expression>();
        List<Class<?>> paramClasses = getParamClasses(keywordMethod);
        for (int i = 0; i < paramClasses.size(); i++) {
            Class<?> paramClass = paramClasses.get(i);
            if (paramClass != null) {
                newArgumentList.add(generateArgument((i < exisitingArgumentList.size()) ? exisitingArgumentList.get(i)
                        : null, paramClass.getSimpleName(), paramClass.getName(), paramClass.isArray(), paramClass
                        .isEnum()));
            }
        }
        keywordCallExpression.setArguments(new ArgumentListExpression(newArgumentList));
    }

    public static void generateMethodCallArguments(MethodCallExpression keywordCallExpression, MethodNode keywordMethod) {
        List<Expression> exisitingArgumentList = ((ArgumentListExpression) keywordCallExpression.getArguments())
                .getExpressions();
        List<Expression> newArgumentList = new ArrayList<Expression>();
        List<Class<?>> paramClasses = getParamClasses(keywordMethod);
        for (int i = 0; i < paramClasses.size(); i++) {
            Class<?> paramClass = paramClasses.get(i);
            if (paramClass != null) {
                newArgumentList.add(generateArgument((i < exisitingArgumentList.size()) ? exisitingArgumentList.get(i)
                        : null, paramClass.getSimpleName(), paramClass.getName(), paramClass.isArray(), paramClass
                        .isEnum()));
            }
        }
        keywordCallExpression.setArguments(new ArgumentListExpression(newArgumentList));
    }

    //
    public static void generateCustomKeywordArguments(MethodCallExpression keywordCallExpression) throws Exception {
        if (keywordCallExpression != null && keywordCallExpression.getMethod() != null) {
            MethodNode keywordMethod = KeywordController.getInstance().getCustomKeywordByName(
                    keywordCallExpression.getObjectExpression().getText(), keywordCallExpression.getMethod().getText(),
                    ProjectController.getInstance().getCurrentProject());
            if (keywordMethod != null) {
                List<Expression> exisitingArgumentList = ((ArgumentListExpression) keywordCallExpression.getArguments())
                        .getExpressions();
                List<Expression> newArgumentList = new ArrayList<Expression>();
                for (int i = 0; i < keywordMethod.getParameters().length; i++) {
                    ClassNode classNode = keywordMethod.getParameters()[i].getType();
                    if (classNode != null) {
                        newArgumentList.add(generateArgument(
                                (i < exisitingArgumentList.size()) ? exisitingArgumentList.get(i) : null,
                                classNode.getNameWithoutPackage(), classNode.getName(), classNode.isArray(),
                                classNode.isEnum()));
                    }
                }
                keywordCallExpression.setArguments(new ArgumentListExpression(newArgumentList));
            }
        }
    }

    public static Expression generateArgument(Expression existingParam, String classSimpleName, String classFullName,
            boolean isArray, boolean isEnum) {
        if (isEnum) {
            if (existingParam instanceof PropertyExpression) {
                String valueClassName = ((PropertyExpression) existingParam).getObjectExpression().getText();
                if (classSimpleName.equals(valueClassName) || classFullName.equals(valueClassName)) {
                    return existingParam;
                }
            } else if (classFullName.equals(FailureHandling.class.getName())) {
                return createPropertyExpressionForClass(FailureHandling.class.getSimpleName(),
                        TestCasePreferenceDefaultValueInitializer.getDefaultFailureHandling().name());
            }
        }

        if (existingParam instanceof MethodCallExpression) {
            if (isObjectArgument((MethodCallExpression) existingParam)) {
                Class<?> objectClass = null;
                try {
                    objectClass = Class.forName(classFullName);
                } catch (ClassNotFoundException e) {
                    // Class not found, do nothing
                }
                if (objectClass != null && TestObject.class.isAssignableFrom(objectClass)) {
                    return existingParam;
                } else {
                    return new ConstantExpression(null);
                }
            }
            if (isCallTestCaseArgument((MethodCallExpression) existingParam)) {
                Class<?> testCaseClass = null;
                try {
                    testCaseClass = Class.forName(classFullName);
                } catch (ClassNotFoundException e) {
                    // Class not found, do nothing
                }
                if (testCaseClass != null && TestCase.class.isAssignableFrom(testCaseClass)) {
                    return existingParam;
                } else {
                    return new ConstantExpression(null);
                }
            }

            if (isTestDataValueArgument((MethodCallExpression) existingParam)) {
                return existingParam;
            }
        }
        if (existingParam instanceof VariableExpression || existingParam instanceof MapExpression
                || existingParam instanceof CastExpression || existingParam instanceof BinaryExpression) {
            return existingParam;
        }
        if (classFullName.equals(List.class.getName()) || isArray) {
            if (existingParam instanceof ListExpression) {
                return existingParam;
            } else if (existingParam instanceof CastExpression
                    && ((CastExpression) existingParam).getExpression() instanceof ListExpression) {
                return existingParam;
            }
        }

        if (existingParam instanceof PropertyExpression) {
            String valueClassName = ((PropertyExpression) existingParam).getObjectExpression().getText();
            if (valueClassName.equals(InputValueType.GlobalVariable.name())) {
                return existingParam;
            }
            if (valueClassName.equals(FailureHandling.class.getName())
                    || valueClassName.equals(FailureHandling.class.getSimpleName())) {
                if (classFullName.equals(FailureHandling.class.getName())
                        || classFullName.equals(FailureHandling.class.getSimpleName())) {
                    return existingParam;
                } else {
                    existingParam = null;
                }
            } else {
                return existingParam;
            }
        }

        String paramClassName = classFullName;
        String existingParamClassName = (existingParam != null) ? existingParam.getType().getName() : "";
        if (paramClassName.equals(Boolean.class.getName()) || paramClassName.equals(Boolean.TYPE.getName())) {
            if (existingParamClassName.equals(Boolean.class.getName())
                    || existingParamClassName.equals(Boolean.TYPE.getName())) {
                return existingParam;
            } else if (existingParamClassName.isEmpty()) {
                return new ConstantExpression(Boolean.FALSE);
            }
        } else if (paramClassName.equals(Character.class.getName()) || paramClassName.equals(String.class.getName())
                || paramClassName.equals(Character.TYPE.getName())) {
            if (existingParamClassName.equals(Character.class.getName())
                    || existingParamClassName.equals(String.class.getName())
                    || existingParamClassName.equals(Character.TYPE.getName())) {
                return existingParam;
            } else if (existingParamClassName.isEmpty()) {
                return new ConstantExpression("");
            }
        } else if (paramClassName.equals(Byte.class.getName()) || paramClassName.equals(Byte.TYPE.getName())
                || paramClassName.equals(Short.class.getName()) || paramClassName.equals(Short.TYPE.getName())
                || paramClassName.equals(Integer.class.getName()) || paramClassName.equals(Integer.TYPE.getName())
                || paramClassName.equals(Long.class.getName()) || paramClassName.equals(Long.TYPE.getName())
                || paramClassName.equals(Float.class.getName()) || paramClassName.equals(Float.TYPE.getName())
                || paramClassName.equals(Double.class.getName()) || paramClassName.equals(Double.TYPE.getName())
                || paramClassName.equals(BigInteger.class.getName())
                || paramClassName.equals(BigDecimal.class.getName())) {
            if (existingParamClassName.equals(Byte.class.getName())
                    || existingParamClassName.equals(Byte.TYPE.getName())
                    || existingParamClassName.equals(Short.class.getName())
                    || existingParamClassName.equals(Short.TYPE.getName())
                    || existingParamClassName.equals(Integer.class.getName())
                    || existingParamClassName.equals(Integer.TYPE.getName())
                    || existingParamClassName.equals(Long.class.getName())
                    || existingParamClassName.equals(Long.TYPE.getName())
                    || existingParamClassName.equals(Float.class.getName())
                    || existingParamClassName.equals(Float.TYPE.getName())
                    || existingParamClassName.equals(Double.class.getName())
                    || existingParamClassName.equals(Double.TYPE.getName())
                    || existingParamClassName.equals(BigInteger.class.getName())
                    || existingParamClassName.equals(BigDecimal.class.getName())) {
                return existingParam;
            } else {
                return new ConstantExpression(0);
            }
        }

        if ((classFullName.equals(TestObject.class.getName()) || classFullName.equals(TestObject.class.getSimpleName()))) {
            return generateObjectMethodCall(null);
        }

        if (existingParam instanceof Expression) {
            return existingParam;
        }
        return new ConstantExpression(null);
    }

    public static MethodCallExpression generateObjectMethodCall(String objectPk) {
        List<Expression> expressionArguments = new ArrayList<Expression>();
        expressionArguments.add(new ConstantExpression(objectPk));
        MethodCallExpression objectMethodCall = new MethodCallExpression(new VariableExpression(
                ObjectRepository.class.getSimpleName()), "findTestObject", new ArgumentListExpression(
                expressionArguments));
        return objectMethodCall;
    }

    public static ExpressionStatement generateCallTestCaseExpresionStatement(TestCaseEntity testCase) throws Exception {
        IKeywordContributor defaultBuiltinKeywordContributor = TestCasePreferenceDefaultValueInitializer
                .getDefaultKeywordType();

        List<Expression> expressionArguments = new ArrayList<Expression>();
        MethodCallExpression keywordMethodCallExpression = new MethodCallExpression(new VariableExpression(
                defaultBuiltinKeywordContributor.getKeywordClass().getSimpleName()),
                BuiltInMethodNodeFactory.CALL_TEST_CASE_METHOD_NAME, new ArgumentListExpression(expressionArguments));
        generateBuiltInKeywordArguments(keywordMethodCallExpression);

        ExpressionStatement statement = new ExpressionStatement(keywordMethodCallExpression);

        ArgumentListExpression argumentList = (ArgumentListExpression) keywordMethodCallExpression.getArguments();

        MethodCallExpression testCaseMethodCallEprs = generateTestCaseMethodCall(TestCaseController.getInstance()
                .getIdForDisplay(testCase));
        MapExpression mapExpression = generateTestCaseVariableBindingExpression(testCase);
        PropertyExpression propertyExprs = (PropertyExpression) argumentList.getExpression(argumentList
                .getExpressions().size() - 1);
        argumentList = new ArgumentListExpression(testCaseMethodCallEprs, mapExpression, propertyExprs);
        keywordMethodCallExpression.setArguments(argumentList);
        return statement;
    }

    public static MethodCallExpression generateTestCaseMethodCall(String testCasePk) {
        List<Expression> expressionArguments = new ArrayList<Expression>();
        expressionArguments.add(new ConstantExpression(testCasePk));
        MethodCallExpression objectMethodCall = new MethodCallExpression(new VariableExpression(
                TestCaseFactory.class.getSimpleName()), "findTestCase", new ArgumentListExpression(expressionArguments));
        return objectMethodCall;
    }

    public static MapExpression generateTestCaseVariableBindingExpression(TestCaseEntity testCase) {
        boolean generateDefaultValue = TestCasePreferenceDefaultValueInitializer.isSetGenerateVariableDefaultValue();
        List<MapEntryExpression> variableExpressions = new ArrayList<MapEntryExpression>();
        for (VariableEntity variableEntity : testCase.getVariables()) {
            ConstantExpression keyExpression = new ConstantExpression(variableEntity.getName());
            String variableValue = variableEntity.getDefaultValue();
            if (!generateDefaultValue) {
                variableValue = variableEntity.getName();
            }
            Expression valueExpression = (variableValue == null || variableValue.isEmpty()) ? new ConstantExpression(
                    null) : new VariableExpression(variableValue);
            variableExpressions.add(new MapEntryExpression(keyExpression, valueExpression));
        }
        return new MapExpression(variableExpressions);
    }

    public static boolean isObjectClass(ClassNode classNode) {
        return (TestObject.class.getName().equals(classNode.getTypeClass().getName())
                || TestObject.class.getSimpleName().equals(classNode.getTypeClass().getSimpleName()) || TestObject.class
                    .isAssignableFrom(classNode.getTypeClass()));
    }

    public static boolean isCallTestCaseClass(ClassNode classNode) {
        return (classNode.getName().equals(TestCase.class.getName()) || classNode.getName().equals(
                TestCase.class.getSimpleName()));
    }

    public static boolean isVoidClass(ClassNode classNode) {
        return (classNode.getName().equals(Void.class.getName())
                || classNode.getName().equals(Void.class.getSimpleName()) || classNode.getName().equals(
                Void.TYPE.getName()));
    }

    public static boolean isGlobalVariablePropertyExpression(PropertyExpression propertyExprs) {
        if (!(propertyExprs.getObjectExpression() instanceof VariableExpression))
            return false;
        if (propertyExprs.getObjectExpression().getText().equals(InputValueType.GlobalVariable.name())) {
            return true;
        } else {
            return false;
        }
    }

    public static String getGlobalVariableNameFromPropertyExpression(PropertyExpression propertyExprs) {
        if (isGlobalVariablePropertyExpression(propertyExprs)) {
            return propertyExprs.getPropertyAsString();
        }
        return "";
    }

    public static int getGlobalVariableIndex(PropertyExpression propertyExpression) {
        try {
            String[] names = GlobalVariableController.getInstance().getAllGlobalVariableNames(
                    ProjectController.getInstance().getCurrentProject());
            String variableName = getGlobalVariableNameFromPropertyExpression(propertyExpression);
            return Math.max(ArrayUtils.indexOf(names, variableName), 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public static PropertyExpression getGlobalVariableExpression(int variableIndex) {
        try {
            String[] names = GlobalVariableController.getInstance().getAllGlobalVariableNames(
                    ProjectController.getInstance().getCurrentProject());
            if (variableIndex >= 0 && variableIndex < names.length) {
                return new PropertyExpression(new VariableExpression(InputValueType.GlobalVariable.name()),
                        new ConstantExpression(names[variableIndex]));
            }
        } catch (Exception e) {
            // Do nothing
        }
        return new PropertyExpression(new VariableExpression(InputValueType.GlobalVariable.name()),
                new ConstantExpression(null));
    }

    public static Class<?> loadType(String typeName, ClassNode scriptClass) {
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
        for (ImportNode importNode : scriptClass.getModule().getImports()) {
            if (importNode.getClassName().endsWith("." + typeName)) {
                try {
                    type = classLoader.loadClass(importNode.getClassName());
                } catch (ClassNotFoundException ex) {
                    continue;
                }
            }
        }
        return type;
    }
}
