package com.kms.katalon.composer.testcase.util;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.core.ast.AstTextValueUtil;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class AstTreeTableTextValueUtil extends AstTextValueUtil {
    private static AstTreeTableTextValueUtil _instance;
    private AstTreeTableTextValueUtil() {
    }
    
    public static AstTreeTableTextValueUtil getInstance() {
        if (_instance == null) {
            _instance = new AstTreeTableTextValueUtil();
        }
        return _instance;
    }
    
    @Override
    public String getTextValue(MethodCallExpression methodCallExpression) {
        if (AstTreeTableInputUtil.isCallTestCaseArgument(methodCallExpression)
                && methodCallExpression.getArguments() instanceof ArgumentListExpression) {
            return getTextValueForTestCaseArgument(methodCallExpression);
        } else if (AstTreeTableInputUtil.isCallTestCaseMethod(methodCallExpression)
                && methodCallExpression.getArguments() instanceof ArgumentListExpression) {
            ArgumentListExpression argumentListExpression = (ArgumentListExpression) methodCallExpression
                    .getArguments();
            if (!argumentListExpression.getExpressions().isEmpty()) {
                return getTextValue(argumentListExpression.getExpressions().get(0));
            }
        } else if (AstTreeTableInputUtil.isObjectArgument(methodCallExpression)
                && methodCallExpression.getArguments() instanceof ArgumentListExpression) {
            return getTextValueForTestObjectArgument(methodCallExpression);
        } else if (AstTreeTableInputUtil.isTestDataArgument(methodCallExpression)
                && methodCallExpression.getArguments() instanceof ArgumentListExpression) {
            return getTextValueForTestDataArgument(methodCallExpression);
        } else if (AstTreeTableInputUtil.isTestDataValueArgument(methodCallExpression)
                && methodCallExpression.getArguments() instanceof ArgumentListExpression) {
            return getTextValueForTestDataValueArgument(methodCallExpression);
        } else if (AstTreeTableUtil.isCustomKeywordMethodCall(methodCallExpression)) {
            processCustomKeywordMethodCall(methodCallExpression);
        }

        String object = getTextValue(methodCallExpression.getObjectExpression());
        String meth = methodCallExpression.getMethod().getText();
        String args = getTextValue(methodCallExpression.getArguments());
        return object + "." + meth + args;
    }

    private void processCustomKeywordMethodCall(MethodCallExpression methodCallExpression) {
        try {
            if (methodCallExpression.getMethod() instanceof ConstantExpression) {
                String meth = KeywordController.getInstance().getCustomKeywordName(
                        methodCallExpression.getMethod().getText());
                methodCallExpression.setMethod(new ConstantExpression(meth));
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private String getTextValueForTestObjectArgument(MethodCallExpression methodCallExpression) {
        ArgumentListExpression argumentListExpression = (ArgumentListExpression) methodCallExpression.getArguments();
        if (!argumentListExpression.getExpressions().isEmpty()) {
            String pk = argumentListExpression.getExpressions().get(0).getText();
            WebElementEntity webElement = null;
            try {
                webElement = ObjectRepositoryController.getInstance().getWebElementByDisplayPk(pk);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
            if (webElement != null) {
                return webElement.getName();
            }
        }
        return "null";
    }

    private String getTextValueForTestDataArgument(MethodCallExpression methodCallExpression) {
        ArgumentListExpression argumentListExpression = (ArgumentListExpression) methodCallExpression.getArguments();
        if (!argumentListExpression.getExpressions().isEmpty()) {
            String pk = argumentListExpression.getExpressions().get(0).getText();
            DataFileEntity dataFile = null;
            try {
                dataFile = TestDataController.getInstance().getTestDataByDisplayId(pk);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
            if (dataFile != null) {
                return dataFile.getName();
            }
            return pk;
        }
        return "null";
    }

    private String getTextValueForTestDataValueArgument(MethodCallExpression methodCallExpression) {
        StringBuilder result = new StringBuilder();
        if (methodCallExpression.getObjectExpression() instanceof MethodCallExpression) {
            result.append(getTextValueForTestDataArgument((MethodCallExpression) methodCallExpression
                    .getObjectExpression()));
        }
        result.append(getTextValue(methodCallExpression.getArguments()));
        return result.toString();
    }

    private String getTextValueForTestCaseArgument(MethodCallExpression methodCallExpression) {
        ArgumentListExpression argumentListExpression = (ArgumentListExpression) methodCallExpression.getArguments();
        if (!argumentListExpression.getExpressions().isEmpty()) {
            String pk = argumentListExpression.getExpressions().get(0).getText();
            TestCaseEntity testCase = null;
            try {
                testCase = TestCaseController.getInstance().getTestCaseByDisplayId(pk);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
            if (testCase != null) {
                return testCase.getName();
            }
            return pk;
        }
        return StringUtils.EMPTY;
    }
}
