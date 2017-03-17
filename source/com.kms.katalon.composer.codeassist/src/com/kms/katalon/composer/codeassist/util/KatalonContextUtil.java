package com.kms.katalon.composer.codeassist.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.StyledString;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;
import static com.kms.katalon.constants.GlobalStringConstants.CR_DOT;

/**
 * Provides a set of utility methods for {@link ContentAssistContext}
 */
public class KatalonContextUtil {
    private static final String CUSTOM_KEYWORDS_CLASS_NODE_PREFIX = GroovyConstants.CUSTOM_KEYWORD_LIB_FILE_NAME
            + CR_DOT;

    private static KeywordController keywordController = KeywordController.getInstance();

    private KatalonContextUtil() {
        // Disable default constructor
    }

    public static boolean isCustomKeywordCompletionClassNode(ContentAssistContext context) {
        if (context.getPerceivedCompletionNode() instanceof ClassExpression) {
            String className = ((ClassExpression) context.getPerceivedCompletionNode()).getType().getName();
            if (className == null) {
                return false;
            }

            if (GroovyConstants.CUSTOM_KEYWORD_LIB_FILE_NAME.equals(className)) {
                return true;
            }
        }

        return CUSTOM_KEYWORDS_CLASS_NODE_PREFIX
                .equals(StringUtils.defaultString(context.fullCompletionExpression).trim());
    }

    public static boolean isBuiltinKeywordCompletionClassNode(ContentAssistContext context) {
        return getBuiltInKeywordCompletionClassNode(context) != null;
    }

    public static KeywordClass getBuiltInKeywordCompletionClassNode(ContentAssistContext context) {
        String className = null;
        if (context.getPerceivedCompletionNode() instanceof ClassExpression) {
            className = ((ClassExpression) context.getPerceivedCompletionNode()).getType().getName();
        } else {
            className = StringUtils
                    .substringBeforeLast(StringUtils.defaultString(context.fullCompletionExpression), CR_DOT).trim();
        }
        if (className == null) {
            return null;
        }
        return keywordController.getBuiltInKeywordClassByName(className);
    }

    @SuppressWarnings("restriction")
    public static TestCaseEntity isTestCaseScriptContext(ContentAssistContext context) {
        if (context == null || context.unit == null || !(context.unit.getResource() instanceof IFile)) {
            return null;
        }
        String scriptFilePath = ((IFile) context.unit.getResource()).getRawLocation().toString();
        if (!GroovyUtil.isScriptFile(scriptFilePath, ProjectController.getInstance().getCurrentProject())) {
            return null;
        }
        try {
            return TestCaseController.getInstance().getTestCaseByScriptFilePath(scriptFilePath);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    public static List<String> getTestCaseVariableStrings(TestCaseEntity testCaseEntity) {
        if (testCaseEntity == null) {
            return Collections.emptyList();
        }
        List<String> testCaseVariableStrings = new ArrayList<String>();
        for (VariableEntity variableEntity : testCaseEntity.getVariables()) {
            testCaseVariableStrings.add(variableEntity.getName());
        }
        return testCaseVariableStrings;
    }

    public static boolean isKeywordMethodNode(MethodNode methodNode) {
        try {
            return keywordController.getBuiltInKeywordByName(methodNode.getDeclaringClass().getName(),
                    methodNode.getName(), getAstParameterTypes(methodNode.getParameters())) != null;
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return false;
        }
    }

    public static StyledString getKatalonSignature() {
        return new StyledString(" (Katalon)", StyledString.DECORATIONS_STYLER);
    }

    public static StyledString getClassSignature(KeywordClass keywordClass) {
        if (keywordClass == null) {
            return new StyledString();
        }
        return new StyledString(" \t - " + keywordClass.getType().getName(), StyledString.QUALIFIER_STYLER);
    }

    public static String[] getAstParameterTypes(Parameter[] params) {
        if (params == null) {
            return new String[] {};
        }
        String[] paramTypes = new String[params.length];
        for (int i = 0; i < paramTypes.length; i++) {
            try {
                Class<?> clazz = ClassUtils.getClass(params[i].getType().getName());
                paramTypes[i] = clazz.isPrimitive() ? ClassUtils.primitiveToWrapper(clazz).getName() : clazz.getName();
            } catch (ClassNotFoundException cnf) {
                paramTypes[i] = params[i].getType().getName();
            }
        }
        return paramTypes;
    }

}
