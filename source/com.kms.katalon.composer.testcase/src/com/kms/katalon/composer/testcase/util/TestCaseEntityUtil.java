package com.kms.katalon.composer.testcase.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class TestCaseEntityUtil {
    public static void copyTestCaseProperties(TestCaseEntity src, TestCaseEntity des) {
        des.setParentFolder(src.getParentFolder());
        des.setProject(src.getProject());

        des.setName(src.getName());
        des.setComment(src.getComment());
        des.setTag(src.getTag());
        des.setDescription(src.getDescription());

        des.getDataFileLocations().clear();
        des.getDataFiles().clear();
        for (DataFileEntity dataFile : src.getDataFiles()) {
            des.getDataFiles().add(dataFile);
            des.getDataFileLocations().add(dataFile.getRelativePath());
        }

        des.getVariables().clear();
        for (VariableEntity variable : src.getVariables()) {
            des.getVariables().add(variable);
        }

        des.getIntegratedEntities().clear();
        for (IntegratedEntity integratedEntity : src.getIntegratedEntities()) {
            des.getIntegratedEntities().add(integratedEntity);
        }
    }

    public static boolean isClassChildOf(String parentClassName, String childClassName) {
        try {
            return Class.forName(parentClassName).isAssignableFrom(Class.forName(childClassName));
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public static List<TestCaseEntity> getTestCasesFromFolderTree(FolderTreeEntity folderTree) {
        List<TestCaseEntity> lstTestCases = new ArrayList<TestCaseEntity>();
        try {
            for (Object child : folderTree.getChildren()) {
                if (child instanceof TestCaseTreeEntity) {
                    lstTestCases.add((TestCaseEntity) ((TestCaseTreeEntity) child).getObject());
                } else if (child instanceof FolderTreeEntity) {
                    lstTestCases.addAll(getTestCasesFromFolderTree((FolderTreeEntity) child));
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return lstTestCases;
    }

    public static List<VariableEntity> getCallTestCaseVariables(ArgumentListExpression argumentListExpression)
            throws Exception {
        if (!TestCasePreferenceDefaultValueInitializer.isSetGenerateVariableDefaultValue()
                && TestCasePreferenceDefaultValueInitializer.isSetAutoExportVariables()) {
            MethodCallExpression methodCallExpression = (MethodCallExpression) argumentListExpression.getExpression(0);
            ConstantExpression constantExpression = (ConstantExpression) AstTreeTableInputUtil
                    .getCallTestCaseParam(methodCallExpression);
            String calledTestCaseId = constantExpression.getText();
            MapExpression mapExpression = (MapExpression) argumentListExpression.getExpression(1);
            List<VariableEntity> variableEntities = new ArrayList<VariableEntity>();
            for (MapEntryExpression entryExpression : mapExpression.getMapEntryExpressions()) {
                String variableName = entryExpression.getKeyExpression().getText();

                VariableEntity variableInCalledTestCase = TestCaseController.getInstance().getVariable(
                        calledTestCaseId, variableName);

                VariableEntity newVariable = new VariableEntity();
                newVariable.setName(variableName);
                newVariable.setDefaultValue(variableInCalledTestCase.getDefaultValue());

                variableEntities.add(newVariable);
            }

            return variableEntities;
        } else {
            return Collections.emptyList();
        }
    }

    // Parse java doc html into plain text
    public static String parseJavaDocHTML(String javadocHTML) {
        if (javadocHTML == null) {
            return "";
        }
        // replace and put line break into javadoc html
        String replace = javadocHTML.replace("<br/>", "<br/>\n").replace("<BR/>", "<BR/>\n").replace("</DT>", "</DT> ")
                .replace("</dt>", "</dt> ").replace("<code>", "\n<code>").replace("<CODE>", "\n<CODE>")
                .replace("</p>", "</p>\n").replace("</P>", "</P>\n").replace("<DL>", "<DL>\n")
                .replace("<dl>", "<dl>\n").replace("</DD>", "</DD>\n").replace("</dd>", "</dd>\n")
                .replace("<b>", "\n<b>").replace("<B>", "\n<B>")
                .replaceAll("(?s)<(h|H)4>.*<\\/(h|H)4>", "");
        // decode any encoded html, preventing &lt;script&gt; to be rendered as <script>
        String html = StringEscapeUtils.unescapeHtml(replace);
        // remove all html tags, but maintain line breaks
        String clean = Jsoup.clean(html, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        // decode html again to convert character entities back into text
        return StringEscapeUtils.unescapeHtml(clean).trim().replaceAll("(?m)(^ *| +(?= |$))", "").replaceAll("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+", "$1");
    }

    public static List<String> getAllKeywordJavaDocText(String keywordClassName, ClassNode scriptClass) {
        List<String> allKeywordJavaDocs = new ArrayList<String>();
        try {
            Class<?> keywordType = AstTreeTableInputUtil.loadType(keywordClassName, scriptClass);
            if (keywordType == null) {
                return allKeywordJavaDocs;
            }
            IProject groovyProject = GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject());
            IJavaProject javaProject = JavaCore.create(groovyProject);
            IType builtinKeywordType = javaProject.findType(keywordType.getName());
            List<Method> builtInKeywordMethods = KeywordController.getInstance().getBuiltInKeywords(keywordClassName);
            for (Method method : builtInKeywordMethods) {
                IMethod builtInMethod = findBuiltinMethods(builtinKeywordType, method.getName(), javaProject);
                if (builtInMethod != null) {
                    allKeywordJavaDocs.add(parseJavaDocHTML(builtInMethod.getAttachedJavadoc(null)));
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return allKeywordJavaDocs;
    }

    private static IMethod findBuiltinMethods(IType type, String methodName, IJavaProject javaProject)
            throws JavaModelException {
        for (IMethod keywordMethod : type.getMethods()) {
            if (keywordMethod.getElementName().equals(methodName)) {
                return keywordMethod;
            }
        }
        if (!type.getSuperclassName().equals(Object.class.getName())) {
            return findBuiltinMethods(javaProject.findType(type.getSuperclassName()), methodName, javaProject);
        }
        return null;
    }

    public static String getKeywordJavaDocText(String keywordClassName, String methodName, ClassNode scriptClass) {
        try {
            Class<?> keywordType = AstTreeTableInputUtil.loadType(keywordClassName, scriptClass);
            if (keywordType == null) {
                return "";
            }
            IProject groovyProject = GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject());
            IJavaProject javaProject = JavaCore.create(groovyProject);
            IType builtinKeywordType = javaProject.findType(keywordType.getName());
            if (builtinKeywordType != null) {
                IMethod builtInMethod = findBuiltinMethods(builtinKeywordType, methodName, javaProject);
                if (builtInMethod != null) {
                    return parseJavaDocHTML(builtInMethod.getAttachedJavadoc(null));
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return "";
    }

    /**
     * Get Test Case Entity list from their script file
     * 
     * @param scriptFiles
     *            list of test case script files
     * @return List of TestCaseEntity
     * @throws Exception
     */
    public static List<TestCaseEntity> getTestCaseEntities(List<IFile> scriptFiles) throws Exception {
        List<TestCaseEntity> testCaseEntities = new ArrayList<TestCaseEntity>();
        if (scriptFiles != null && !scriptFiles.isEmpty()) {
            for (IFile file : scriptFiles) {
                testCaseEntities.add(TestCaseController.getInstance().getTestCaseByScriptFilePath(
                        file.getRawLocation().toString()));
            }
        }
        return testCaseEntities;
    }
}
