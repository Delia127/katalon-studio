package com.kms.katalon.composer.testcase.ast.treetable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.groovy.util.GroovyUtil;

public class AstBuiltInKeywordTreeTableNode extends AstAbstractKeywordTreeTableNode {

    public AstBuiltInKeywordTreeTableNode(ExpressionStatement methodCallStatement, AstTreeTableNode parentNode,
            ASTNode parentObject, ClassNode scriptClass) {
        super(methodCallStatement, parentNode, parentObject, scriptClass);
    }

    protected String getBuiltInKWClassSimpleName() throws Exception {
        Expression expression = parentStatement.getExpression();
        if (expression instanceof MethodCallExpression) {
            return ((MethodCallExpression) parentStatement.getExpression()).getObjectExpression().getText();
        } else if (expression instanceof BinaryExpression) {
            MethodCallExpression methodCallExpresion = (MethodCallExpression) ((BinaryExpression) parentStatement
                    .getExpression()).getRightExpression();
            return methodCallExpresion.getObjectExpression().getText();
        } else {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public Object getItem() {
        try {
            String className = getBuiltInKWClassSimpleName();
            List<Method> builtInKeywordMethods = KeywordController.getInstance().getBuiltInKeywords(className);
            for (Method keywordMethod : builtInKeywordMethods) {
                if (keywordMethod.getName().equals(getKeyword())) {
                    return builtInKeywordMethods.indexOf(keywordMethod);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return -1;
    };

    // Parse java doc html into plain text
    private static String parseJavaDocHTML(String javadocHTML) {
        if (javadocHTML == null) {
            return "";
        }
        // replace and put line break into javadoc html
        String replace = javadocHTML.replace("<br/>", "<br/>\n").replace("<BR/>", "<BR/>\n").replace("</DT>", "</DT> ")
                .replace("</dt>", "</dt> ").replace("<code>", "\n<code>").replace("<CODE>", "\n<CODE>")
                .replace("</p>", "</p>\n").replace("</P>", "</P>\n").replace("<DL>", "<DL>\n")
                .replace("<dl>", "<dl>\n").replace("</DD>", "</DD>\n").replace("</dd>", "</dd>\n")
                .replace("<b>", "\n<b>").replace("<B>", "\n<B>").replaceAll("<H4>.*<\\/H4>", "")
                .replaceAll("<h4>.*<\\/h4>", "");
        // decode any encoded html, preventing &lt;script&gt; to be rendered as <script>
        String html = StringEscapeUtils.unescapeHtml(replace);
        // remove all html tags, but maintain line breaks
        String clean = Jsoup.clean(html, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        // decode html again to convert character entities back into text
        return StringEscapeUtils.unescapeHtml(clean).trim().replaceAll("(?m)(^ *| +(?= |$))", "")
                .replaceAll("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+", "$1");
    }

    @Override
    public String getItemTooltipText() {
        try {
            Class<?> keywordType = AstTreeTableInputUtil.loadType(getBuiltInKWClassSimpleName(), scriptClass);
            IProject groovyProject = GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject());
            IJavaProject javaProject = JavaCore.create(groovyProject);
            IType builtinKeywordType = javaProject.findType(keywordType.getName());
            if (builtinKeywordType != null) {
                for (IMethod keywordMethod : builtinKeywordType.getMethods()) {
                    if (keywordMethod.getElementName().equals(getKeyword())) {
                        return parseJavaDocHTML(keywordMethod.getAttachedJavadoc(null));
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return super.getItemTooltipText();
    }

    @Override
    public boolean setItem(Object item) {
        if (item instanceof Integer) {
            try {
                int keywordIndex = (int) item;
                String className = getBuiltInKWClassSimpleName();
                List<Method> builtInKeywordMethods = KeywordController.getInstance().getBuiltInKeywords(className);
                if (keywordIndex >= 0 && keywordIndex < builtInKeywordMethods.size()) {
                    if (!getKeyword().equals(builtInKeywordMethods.get(keywordIndex).getName())) {
                        setKeyword(builtInKeywordMethods.get(keywordIndex).getName());
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
    protected List<String> getKeywordNames() {
        List<String> keywordNames = new ArrayList<String>();
        try {
            List<Method> builtInKeywordMethods = KeywordController.getInstance().getBuiltInKeywords(
                    getBuiltInKWClassSimpleName());
            for (Method keywordMethod : builtInKeywordMethods) {
                keywordNames.add(keywordMethod.getName());
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return keywordNames;
    }

    @Override
    public void generateArguments() {
        try {
            AstTreeTableInputUtil.generateBuiltInKeywordArguments(methodCall);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public boolean isInputEditable() {
        try {
            Method keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
                    getBuiltInKWClassSimpleName(), getKeyword());
            if (keywordMethod != null) {
                int count = 0;
                List<Class<?>> paramClasses = AstTreeTableInputUtil.getParamClasses(keywordMethod);
                for (int i = 0; i < paramClasses.size(); i++) {
                    // if (paramClasses.get(i) != TestObject.class) {
                    if (!TestObject.class.isAssignableFrom(paramClasses.get(i))) {
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
        if (arguments != null && arguments.getExpressions().size() > 0) {
            try {
                StringBuilder displayString = new StringBuilder();
                Method keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
                        getBuiltInKWClassSimpleName(), getKeyword());
                if (keywordMethod != null) {
                    int count = 0;
                    List<Class<?>> paramClasses = AstTreeTableInputUtil.getParamClasses(keywordMethod);
                    for (int i = 0; i < paramClasses.size(); i++) {
                        // if (paramClasses.get(i) != TestObject.class &&
                        // paramClasses.get(i) != FailureHandling.class) {
                        if (!TestObject.class.isAssignableFrom(paramClasses.get(i))
                                && paramClasses.get(i) != FailureHandling.class) {
                            if (i < arguments.getExpressions().size()) {
                                if (count > 0) {
                                    displayString.append("; ");
                                }
                                Expression inputExpression = arguments.getExpression(i);
                                IInputValueType typeValue = AstTreeTableValueUtil.getTypeValue(inputExpression,
                                        scriptClass);
                                if (typeValue != null) {
                                    displayString.append(typeValue.getDisplayValue(inputExpression));
                                } else {
                                    displayString.append(AstTreeTableTextValueUtil.getInstance().getTextValue(
                                            inputExpression));
                                }
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
                return AstTreeTableInputUtil.getBuiltInKeywordInputParameters(getBuiltInKWClassSimpleName(),
                        getKeyword(), argumentList);
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
                List<?> inputParameters = (List<?>) input;
                Method keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
                        getBuiltInKWClassSimpleName(), getKeyword());
                if (keywordMethod != null) {
                    List<Class<?>> paramClasses = AstTreeTableInputUtil.getParamClasses(keywordMethod);
                    ArgumentListExpression argumentListExpression = new ArgumentListExpression();
                    for (int i = 0; i < paramClasses.size(); i++) {
                        argumentListExpression.addExpression(AstTreeTableInputUtil
                                .getArgumentExpression((InputParameter) inputParameters.get(i)));

                    }
                    if (!AstTreeTableValueUtil.compareAstNode(argumentListExpression, methodCall.getArguments())) {
                        methodCall.setArguments(argumentListExpression);
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
    protected int getObjectArgumentIndex() throws Exception {
        Method keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(getBuiltInKWClassSimpleName(),
                getKeyword());
        if (keywordMethod != null) {
            List<Class<?>> paramClasses = AstTreeTableInputUtil.getParamClasses(keywordMethod);
            for (int i = 0; i < paramClasses.size(); i++) {
                if (TestObject.class.isAssignableFrom(paramClasses.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean isOutputEditatble() {
        try {
            Method keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
                    getBuiltInKWClassSimpleName(), getKeyword());
            if (keywordMethod != null && keywordMethod.getReturnType() != Void.class
                    && keywordMethod.getReturnType() != Void.TYPE) {
                return true;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

        return false;
    }

    @Override
    protected VariableExpression createNewOutput(String output) throws Exception {
        Method keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(getBuiltInKWClassSimpleName(),
                getKeyword());
        if (keywordMethod != null && keywordMethod.getReturnType() != Void.class
                && keywordMethod.getReturnType() != Void.TYPE) {
            return new VariableExpression(output, new ClassNode(keywordMethod.getReturnType()));
        }
        return null;
    }
}
