package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;

public class AstCustomKeywordTreeTableNode extends AstAbstractKeywordTreeTableNode {

    public AstCustomKeywordTreeTableNode(ExpressionStatement methodCallStatement, AstTreeTableNode parentNode,
            ASTNode parentObject, ClassNode scriptClass) {
        super(methodCallStatement, parentNode, parentObject, scriptClass);
        if (methodCall.getMethod() instanceof ConstantExpression) {
            methodCall.setMethod(new ConstantExpression(KeywordController.getInstance().getCustomKeywordName(
                    methodCall.getMethod().getText())));
        }
    }

    @Override
    public Object getItem() {
        try {
            List<MethodNode> customKeywordMethods = KeywordController.getInstance().getCustomKeywords(
                    ProjectController.getInstance().getCurrentProject());
            for (MethodNode keywordMethodNode : customKeywordMethods) {
                if (keywordMethodNode.getName().equals(
                        KeywordController.getInstance().getRawCustomKeywordName(getKeyword()))) {
                    return customKeywordMethods.indexOf(keywordMethodNode);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return -1;
    }

    @Override
    protected List<String> getKeywordNames() {
        List<String> keywordNames = new ArrayList<String>();
        try {
            List<MethodNode> customKeywordMethods = KeywordController.getInstance().getCustomKeywords(
                    ProjectController.getInstance().getCurrentProject());
            for (MethodNode keywordMethodNode : customKeywordMethods) {
                keywordNames.add(keywordMethodNode.getName());
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return keywordNames;
    }
    
    @Override
    protected List<String> getKeywordToolTips() {
        return getKeywordNames();
    }

    @Override
    public boolean setItem(Object item) {
        if (item instanceof Integer) {
            try {
                int keywordIndex = (int) item;
                List<MethodNode> customKeywordMethods = KeywordController.getInstance().getCustomKeywords(
                        ProjectController.getInstance().getCurrentProject());
                if (keywordIndex >= 0 && keywordIndex < customKeywordMethods.size()) {
                    String newKeyword = KeywordController.getInstance().getCustomKeywordName(
                            customKeywordMethods.get(keywordIndex).getName());
                    if (!getKeyword().equals(newKeyword)) {
                        setKeyword(newKeyword);
                        return true;
                    }
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        return false;
    }

    @Override
    public void generateArguments() {
        try {
            AstTreeTableInputUtil.generateCustomKeywordArguments(methodCall);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public boolean isInputEditable() {
        try {
            MethodNode keywordMethodNode = KeywordController.getInstance().getCustomKeywordByName(
                    methodCall.getObjectExpression().getText(), getItemText(),
                    ProjectController.getInstance().getCurrentProject());

            if (keywordMethodNode != null) {
                int count = 0;
                for (int i = 0; i < keywordMethodNode.getParameters().length; i++) {
                    if (!AstTreeTableInputUtil.isObjectClass(keywordMethodNode.getParameters()[i].getType())) {
                        count++;
                    }
                }
                return count > 0;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Override
    public String getInputText() {
        ArgumentListExpression arguments = (ArgumentListExpression) methodCall.getArguments();
        if (arguments.getExpressions().size() > 0) {
            try {
                StringBuilder displayString = new StringBuilder();
                MethodNode keywordMethodNode = KeywordController.getInstance().getCustomKeywordByName(
                        methodCall.getObjectExpression().getText(), getItemText(),
                        ProjectController.getInstance().getCurrentProject());
                if (keywordMethodNode != null) {
                    int count = 0;
                    for (int i = 0; i < keywordMethodNode.getParameters().length; i++) {
                        if (!AstTreeTableInputUtil.isObjectClass(keywordMethodNode.getParameters()[i].getType())) {
                            if (i < arguments.getExpressions().size()) {
                                if (count > 0) {
                                    displayString.append("; ");
                                }
                                Expression inputExpression = arguments.getExpression(i);
                                displayString.append(AstTreeTableTextValueUtil.getInstance().getTextValue(
                                        inputExpression));
                                count++;
                            }
                        }
                    }
                }
                return displayString.toString();
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        return "";
    }

    @Override
    public Object getInput() {
        ArgumentListExpression argumentList = (ArgumentListExpression) methodCall.getArguments();
        if (argumentList != null) {
            try {
                return AstTreeTableInputUtil
                        .getCustomKeywordInputParameters(getClassName(), getKeyword(), argumentList);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        return null;
    }

    @Override
    public boolean setInput(Object input) {
        if (input instanceof List<?>) {
            try {
                MethodNode keywordMethod = KeywordController.getInstance().getCustomKeywordByName(getClassName(),
                        getKeyword(), ProjectController.getInstance().getCurrentProject());
                if (keywordMethod != null) {
                    List<?> inputParameters = (List<?>) input;
                    ArgumentListExpression argumentListExpression = new ArgumentListExpression();
                    for (int i = 0; i < inputParameters.size(); i++) {
                        argumentListExpression.addExpression(AstTreeTableInputUtil
                                .getArgumentExpression((InputParameter) inputParameters.get(i)));
                    }
                    methodCall.setArguments(argumentListExpression);
                    return true;
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        return false;
    }

    @Override
    protected int getObjectArgumentIndex() throws Exception {
        MethodNode keywordMethodNode = KeywordController.getInstance().getCustomKeywordByName(
                methodCall.getObjectExpression().getText(), getItemText(),
                ProjectController.getInstance().getCurrentProject());
        if (keywordMethodNode != null) {
            for (int i = 0; i < keywordMethodNode.getParameters().length; i++) {
                if (AstTreeTableInputUtil.isObjectClass(keywordMethodNode.getParameters()[i].getType())) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean isOutputEditatble() {
        try {
            MethodNode keywordMethodNode = KeywordController.getInstance().getCustomKeywordByName(
                    methodCall.getObjectExpression().getText(), getItemText(),
                    ProjectController.getInstance().getCurrentProject());
            if (keywordMethodNode != null && !AstTreeTableInputUtil.isVoidClass(keywordMethodNode.getReturnType())) {
                return true;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Override
    protected VariableExpression createNewOutput(String output) throws Exception {
        MethodNode keywordMethodNode = KeywordController.getInstance().getCustomKeywordByName(
                methodCall.getObjectExpression().getText(), getItemText(),
                ProjectController.getInstance().getCurrentProject());
        if (!AstTreeTableInputUtil.isVoidClass(keywordMethodNode.getReturnType())) {
            return new VariableExpression(output, new ClassNode(keywordMethodNode.getReturnType()));
        }
        return null;
    }

    private String getClassName() {
        return methodCall.getObjectExpression().getText();
    }

    @Override
    public String getItemTextForDisplay() {
        return super.getItemText();
    }
}
