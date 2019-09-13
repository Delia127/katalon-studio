package com.kms.katalon.composer.testcase.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.custom.factory.BuiltInMethodNodeFactory;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.custom.keyword.KeywordMethod;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class TestCaseEntityUtil {
    private static boolean reloadJavaDoc = false;
    /**
     * Copy Test Case properties without meta info (Comment, Tag, and Description) by default
     * 
     * @param src source Test Case Entity
     * @param des destination Test Case Entity
     * @see #copyTestCaseProperties(TestCaseEntity, TestCaseEntity, boolean)
     */
    public static void copyTestCaseProperties(TestCaseEntity src, TestCaseEntity des) {
        copyTestCaseProperties(src, des, true);
    }

    /**
     * Copy Test Case properties
     * 
     * @param src source Test Case Entity
     * @param des destination Test Case Entity
     * @param ignoreMetaInfo ignore tag, description, and comment from copy
     */
    public static void copyTestCaseProperties(TestCaseEntity src, TestCaseEntity des, boolean ignoreMetaInfo) {
        des.setParentFolder(src.getParentFolder());
        des.setProject(src.getProject());

        des.setName(src.getName());

        if (!ignoreMetaInfo) {
            des.setComment(src.getComment());
            des.setTag(src.getTag());
            des.setDescription(src.getDescription());
        }

        des.getDataFileLocations().clear();
        des.getDataFiles().clear();
        for (DataFileEntity dataFile : src.getDataFiles()) {
            des.getDataFiles().add(dataFile);
            des.getDataFileLocations().add(dataFile.getRelativePath());
        }

        des.getVariables().clear();
        for (VariableEntity variable : src.getVariables()) {
            VariableEntity variableName = new VariableEntity();
            variableName.setName(variable.getName());
            des.getVariables().add(variableName);
        }

        des.getIntegratedEntities().clear();
        for (IntegratedEntity integratedEntity : src.getIntegratedEntities()) {
            des.getIntegratedEntities().add(integratedEntity);
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
    
    private static Map<String, Map<String, String>> keywordMethodJavaDocMap;

    public static Map<String, Map<String, String>> getKeywordMethodJavaDocMap() {
        if (keywordMethodJavaDocMap == null || reloadJavaDoc) {
            initKeywordJavaDocMap();
        }
        return keywordMethodJavaDocMap;
    }

    private static void initKeywordJavaDocMap() {
        keywordMethodJavaDocMap = new HashMap<String, Map<String, String>>();
        reloadJavaDoc = false;
        IProject groovyProject = getGroovyProject();
        if (groovyProject == null) {
            reloadJavaDoc = true;
            return;
        }
        IJavaProject javaProject = JavaCore.create(groovyProject);
        
        for (KeywordClass keywordClass : KeywordController.getInstance().getBuiltInKeywordClasses()) {
            Map<String, String> allKeywordJavaDocMap = new HashMap<String, String>();
            String keywordClassName = keywordClass.getSimpleName();
            keywordMethodJavaDocMap.put(keywordClassName, allKeywordJavaDocMap);
            try {
                Class<?> keywordType = AstKeywordsInputUtil.loadType(keywordClass.getName(), null);
                if (keywordType == null) {
                    continue;
                }
                IType builtinKeywordType = javaProject.findType(keywordType.getName());
                List<KeywordMethod> builtInKeywordMethods = KeywordController.getInstance().getBuiltInKeywords(
                        keywordClassName, true);
                builtInKeywordMethods.add(BuiltInMethodNodeFactory.findCallTestCaseMethod(keywordClassName));
                for (KeywordMethod method : builtInKeywordMethods) {
                    IMethod builtInMethod = findBuiltinMethods(builtinKeywordType, method.getName(), javaProject);
                    if (builtInMethod != null) {
                        String attachedJavaDoc = builtInMethod.getAttachedJavadoc(null);
                        attachedJavaDoc = attachedJavaDoc == null ? "" : attachedJavaDoc;
                        allKeywordJavaDocMap.put(method.getName(), attachedJavaDoc);
                    }
                }
            } catch (JavaModelException e) {
                LoggerSingleton.logError(e);
            }
        }
    }
    
    private static IProject getGroovyProject() {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        
        if (projectEntity != null) {
            return GroovyUtil.getGroovyProject(projectEntity);
        }
        return null;
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

    public static String getKeywordJavaDocText(String keywordClassName, String methodName) {
        Map<String, String> keywordClassMethodJavaDocMap = getKeywordMethodJavaDocMap().get(keywordClassName);
        if (keywordClassMethodJavaDocMap == null || keywordClassMethodJavaDocMap.get(methodName) == null) {
            return "";
        }
        return keywordClassMethodJavaDocMap.get(methodName);
    }

    /**
     * Get Test Case Entity list from their script file
     * 
     * @param scriptFiles
     * list of test case script files
     * @return List of TestCaseEntity
     * @throws Exception
     */
    public static List<TestCaseEntity> getTestCaseEntities(List<IFile> scriptFiles) throws Exception {
        List<TestCaseEntity> testCaseEntities = new ArrayList<TestCaseEntity>();
        if (scriptFiles == null || scriptFiles.isEmpty()) {
            return testCaseEntities;
        }
        for (IFile file : scriptFiles) {
            testCaseEntities.add(TestCaseController.getInstance().getTestCaseByScriptFilePath(
                    file.getRawLocation().toString()));
        }
        return testCaseEntities;
    }
}
