package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.openqa.selenium.Keys;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ImportNodeCollection;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.checkpoint.CheckpointFactory;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.groovy.constant.GroovyConstants;

public class MethodCallExpressionWrapper extends ExpressionWrapper {
    public static final String TO_STRING_METHOD_NAME = "toString";

    public static final String THIS_VARIABLE = "this";

    public static final String CALL_TEST_CASE_METHOD_NAME = "callTestCase";

    public static final String FIND_TEST_CASE_METHOD_NAME = GroovyConstants.FIND_TEST_CASE_METHOD_NAME;

    public static final String FIND_TEST_DATA_METHOD_NAME = GroovyConstants.FIND_TEST_DATA_METHOD_NAME;

    public static final String FIND_TEST_OBJECT_METHOD_NAME = GroovyConstants.FIND_TEST_OBJECT_METHOD_NAME;
    
    public static final String FIND_WINDOWS_OBJECT_METHOD_NAME = GroovyConstants.FIND_WINDOWS_OBJECT_METHOD_NAME;

    public static final String FIND_CHECKPOINT_METHOD_NAME = GroovyConstants.FIND_CHECKPOINT_METHOD_NAME;

    public static final String GET_VALUE_METHOD_NAME = "getValue";

    public static final String KEYS_CHORDS_METHOD_NAME = "chord";

    private ExpressionWrapper objectExpression;

    private ExpressionWrapper method;

    private ArgumentListExpressionWrapper arguments;

    private boolean spreadSafe = false;

    private boolean safe = false;

    public MethodCallExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        this(THIS_VARIABLE, TO_STRING_METHOD_NAME, parentNodeWrapper);
    }

    public MethodCallExpressionWrapper(String classSimpleName, String method, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.objectExpression = new VariableExpressionWrapper(classSimpleName, this);
        this.method = new ConstantExpressionWrapper(method, this);
        this.arguments = new ArgumentListExpressionWrapper(this);
    }

    public MethodCallExpressionWrapper(Class<?> clazz, String method, ASTNodeWrapper parentNodeWrapper) {
        this(clazz != null ? clazz.getSimpleName() : THIS_VARIABLE, method, parentNodeWrapper);
    }

    public MethodCallExpressionWrapper(MethodCallExpression expression, ASTNodeWrapper parentNodeWrapper) {
        super(expression, parentNodeWrapper);
        objectExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression.getObjectExpression(),
                this);
        method = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression.getMethod(), this);
        if (expression.getArguments() instanceof ArgumentListExpression) {
            arguments = new ArgumentListExpressionWrapper((ArgumentListExpression) expression.getArguments(), this);
        } else {
            arguments = new ArgumentListExpressionWrapper(this);
        }
    }

    public MethodCallExpressionWrapper(MethodCallExpressionWrapper methodCallExpressionWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(methodCallExpressionWrapper, parentNodeWrapper);
        copyMethodCallProperties(methodCallExpressionWrapper);
    }

    private void copyMethodCallProperties(MethodCallExpressionWrapper methodCallExpressionWrapper) {
        objectExpression = methodCallExpressionWrapper.getObjectExpression().copy(this);
        method = methodCallExpressionWrapper.getMethod().copy(this);
        arguments = new ArgumentListExpressionWrapper(methodCallExpressionWrapper.getArguments(), this);
    }

    public ExpressionWrapper getObjectExpression() {
        return objectExpression;
    }

    public void setObjectExpression(ExpressionWrapper objectExpression) {
        if (objectExpression == null) {
            return;
        }
        objectExpression.setParent(this);
        this.objectExpression = objectExpression;
    }

    public String getObjectExpressionAsString() {
        if (!(objectExpression instanceof ConstantExpressionWrapper)) {
            return objectExpression.getText();
        }
        return ((ConstantExpressionWrapper) objectExpression).getValue().toString();
    }

    public boolean isObjectExpressionOfClass(Class<?> clazz) {
        String objectExpressionString = getObjectExpressionAsString();
        return StringUtils.equals(objectExpressionString, clazz.getName())
                || StringUtils.equals(objectExpressionString, clazz.getSimpleName());
    }

    public boolean isObjectExpressionOfClass(String className) {
        return StringUtils.equals(getObjectExpressionAsString(), className);
    }

    /**
     * This method returns the method name as String if it is no dynamic calculated method name, but a constant.
     */
    public String getMethodAsString() {
        if (!(method instanceof ConstantExpressionWrapper)) {
            return method.getText();
        }
        return ((ConstantExpressionWrapper) method).getValueAsString();
    }

    public ExpressionWrapper getMethod() {
        return method;
    }

    public boolean setMethod(ExpressionWrapper method) {
        if (method == null || method.isEqualsTo(this.method)) {
            return false;
        }
        method.copyProperties(this.method);
        method.setParent(this);
        this.method = method;
        return true;
    }

    public boolean setMethod(String method) {
        if (StringUtils.equals(method, getMethodAsString())) {
            return false;
        }
        ConstantExpressionWrapper newConstant = new ConstantExpressionWrapper(method, this);
        newConstant.copyProperties(this.method);
        this.method = newConstant;
        return true;
    }

    public ArgumentListExpressionWrapper getArguments() {
        return arguments;
    }

    public boolean setArguments(ArgumentListExpressionWrapper arguments) {
        if (arguments == null || arguments.isEqualsTo(this.arguments)) {
            return false;
        }
        arguments.setParent(this);
        arguments.copyProperties(this.arguments);
        this.arguments = arguments;
        return true;
    }

    public boolean isSpreadSafe() {
        return spreadSafe;
    }

    public void setSpreadSafe(boolean spreadSafe) {
        this.spreadSafe = spreadSafe;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    @Override
    public String getText() {
        String text = "";
        if (objectExpression instanceof BinaryExpressionWrapper) {
            text = "(" + objectExpression.getText() + ")";
        } else {
            text = objectExpression.getText();
        }
        return (THIS_VARIABLE.equals(text) ? "" : text + ".")
                + ((method instanceof ConstantExpressionWrapper) ? ((ConstantExpressionWrapper) method).getValue()
                        : method.getText()) + arguments.getText();

    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(objectExpression);
        astNodeWrappers.add(method);
        astNodeWrappers.add(arguments);
        return astNodeWrappers;
    }

    @Override
    public MethodCallExpressionWrapper clone() {
        return new MethodCallExpressionWrapper(this, getParent());
    }

    @Override
    public boolean isInputEditatble() {
        return true;
    }

    @Override
    public ASTNodeWrapper getInput() {
        return this;
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (!(input instanceof MethodCallExpressionWrapper) || this.isEqualsTo(input)) {
            return false;
        }
        copyMethodCallProperties((MethodCallExpressionWrapper) input);
        return true;
    }

    @Override
    public boolean replaceChild(ASTNodeWrapper oldChild, ASTNodeWrapper newChild) {
        if (oldChild == getObjectExpression() && newChild instanceof ExpressionWrapper) {
            setObjectExpression((ExpressionWrapper) newChild);
            return true;
        } else if (oldChild == getMethod() && newChild instanceof ExpressionWrapper) {
            setMethod((ExpressionWrapper) newChild);
            return true;
        } else if (oldChild == getArguments() && newChild instanceof ArgumentListExpressionWrapper) {
            setArguments((ArgumentListExpressionWrapper) newChild);
            return true;
        }
        return super.replaceChild(oldChild, newChild);
    }

    public static MethodCallExpressionWrapper newLocalMethod(String method, ASTNodeWrapper parentNodeWrapper) {
        return new MethodCallExpressionWrapper(THIS_VARIABLE, method, parentNodeWrapper);
    }

    public boolean isKeyword(String className) {
        KeywordClass keywordClass = KeywordController.getInstance().getBuiltInKeywordClassByName(className);
        if (keywordClass == null) {
            return false;
        }
        return KeywordController.getInstance().getBuiltInKeywordByName(keywordClass, getMethodAsString()) != null;
    }

    public boolean isBuiltInKeywordMethodCall() {
        if (getObjectExpression() == null) {
            return false;
        }

        for (KeywordClass keywordClass : KeywordController.getInstance().getBuiltInKeywordClasses()) {
            String classAliasName = keywordClass.getAliasName();
            if ((isObjectExpressionOfClass(keywordClass.getType()) && isKeyword(keywordClass.getSimpleName()))
                    || (importHasAliasName(classAliasName) && isObjectExpressionOfClass(classAliasName) && isKeyword(classAliasName))) {
                return true;
            }
        }
        return false;
    }

    private boolean importHasAliasName(String aliasName) {
        ImportNodeCollection importNodeCollection = getImportNodeCollection();
        return importNodeCollection != null && importNodeCollection.isImported(aliasName);
    }

    private ImportNodeCollection getImportNodeCollection() {
        ScriptNodeWrapper scriptClass = getScriptClass();
        return scriptClass != null ? scriptClass.getImportNodeCollection() : null;
    }

    public boolean isMethodNameImportedAsStatic(String methodName) {
        ImportNodeCollection importNodeCollection = getImportNodeCollection();
        return importNodeCollection != null && isQualifiedNameImported(methodName)
                && StringUtils.equals(methodName, importNodeCollection.getQualifierForAlias(getMethodAsString()));
    }

    private boolean isQualifiedNameImported(String qualifiedName) {
        return isObjectExpressionOfClass(THIS_VARIABLE) && getImportNodeCollection().hasAlias(qualifiedName);
    }

    public boolean isCustomKeywordMethodCall() {
        return getObjectExpression() != null
                && StringConstants.CUSTOM_KEYWORD_CLASS_NAME.equals(getObjectExpressionAsString());
    }

    public boolean isCallTestCaseMethodCall() {
        if (getObjectExpression() == null) {
            return false;
        }

        for (KeywordClass keywordClass : KeywordController.getInstance().getBuiltInKeywordClasses()) {
            String classAliasName = keywordClass.getAliasName();
            if ((isObjectExpressionOfClass(keywordClass.getType()))
                    || (importHasAliasName(classAliasName) && isObjectExpressionOfClass(classAliasName))) {
                return CALL_TEST_CASE_METHOD_NAME.equals(getMethodAsString())
                        && getArguments().getExpressions().size() > 1;
            }
        }
        return false;
    }

    public boolean isFindTestCaseMethodCall() {
        return isFindTestArtifactMethodCall(TestCaseFactory.class, FIND_TEST_CASE_METHOD_NAME);
    }

    public boolean isFindTestDataMethodCall() {
        return isFindTestArtifactMethodCall(TestDataFactory.class, FIND_TEST_DATA_METHOD_NAME);
    }

    public boolean isFindTestObjectMethodCall() {
        return isFindTestArtifactMethodCall(ObjectRepository.class, FIND_TEST_OBJECT_METHOD_NAME);
    }
    
    public boolean isFindWindowsObjectMethodCall() {
        return isFindTestArtifactMethodCall(ObjectRepository.class, FIND_WINDOWS_OBJECT_METHOD_NAME);
    }

    public boolean isFindCheckpointMethodCall() {
        return isFindTestArtifactMethodCall(CheckpointFactory.class, FIND_CHECKPOINT_METHOD_NAME);
    }

    private boolean isFindTestArtifactMethodCall(Class<?> clazzToFind, String methodName) {
        String fullMethodName = clazzToFind.getName() + "." + methodName;
        return (isObjectExpressionOfClass(clazzToFind) && getMethodAsString().equals(methodName))
                || (isMethodNameImportedAsStatic(fullMethodName)) 
                    && getArguments() != null 
                    && getArguments().getExpressions().size() >= 1;
    }

    public boolean isGetTestDataValueMethodCall() {
        return getObjectExpression() instanceof MethodCallExpressionWrapper
                && ((MethodCallExpressionWrapper) getObjectExpression()).isFindTestDataMethodCall()
                && GET_VALUE_METHOD_NAME.equals(getMethodAsString());
    }

    public boolean isKeysArgumentExpression() {
        return isObjectExpressionOfClass(Keys.class) && getMethodAsString().equals(KEYS_CHORDS_METHOD_NAME);
    }
    
    public boolean isStaticMethodCall(ClassLoader classLoader) {
        String objectAsString = objectExpression.getText();
        if (StringUtils.isEmpty(objectAsString)) {
            return false;
        }
        Class<?> callerClass = objectExpression.resolveType(classLoader);
        if (callerClass == null) {
            return false;
        }

        String fullCallerClassName = callerClass.getName();
        if (fullCallerClassName.equals(objectAsString) || callerClass.getSimpleName().equals(objectAsString)) {
            return true;
        }

        if (fullCallerClassName.equals(getImportNodeCollection().getQualifierForAlias(objectAsString))) {
            return true;
        }
        return false;
    }

    @Override
    public Class<?> resolveType(ClassLoader classLoader) {
        return resolveObjectType(classLoader, false);
    }
    
    private Class<?> resolveObjectType(ClassLoader classLoader, boolean parentResolved) {
        if (THIS_VARIABLE.equals(getObjectExpressionAsString())) {
            String methodAsString = getMethodAsString();
            ImportNodeCollection importNodeCollection = getImportNodeCollection();
            if (importNodeCollection == null || !importHasAliasName(methodAsString)) {
                return null;
            }
            return importNodeCollection.resolve(importNodeCollection.getQualifierForAlias(methodAsString));
        }
        if (objectExpression instanceof MethodCallExpressionWrapper) {
            return ((MethodCallExpressionWrapper) objectExpression).resolveObjectType(classLoader, true);
        }
        Class<?> objectExpressionResult = objectExpression.resolveType(classLoader);
        if (objectExpressionResult == null) {
            return null;
        }
        if (!parentResolved) {
            return objectExpressionResult;
        }
        return AstKeywordsInputUtil.getFirstAccessibleMethodReturnType(objectExpressionResult, getMethodAsString(),
                isStaticMethodCall(classLoader));
    }
}
