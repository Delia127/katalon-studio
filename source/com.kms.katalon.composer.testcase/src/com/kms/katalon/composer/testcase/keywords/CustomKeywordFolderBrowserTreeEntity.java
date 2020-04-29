package com.kms.katalon.composer.testcase.keywords;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFolder;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.annotation.Keyword;
import com.kms.katalon.custom.parser.CustomKeywordParser;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class CustomKeywordFolderBrowserTreeEntity extends KeywordBrowserFolderTreeEntity {
    private static final String KEYWORD_OBJECT_ANNOTATION_METHOD = "keywordObject";

    private static final long serialVersionUID = 1L;

    private static final String TREE_ITEM_LABEL = StringConstants.KEYWORD_BROWSER_CUSTOM_KEYWORD_ROOT_TREE_ITEM_LABEL;

    private static final String CUSTOM_KEYWORD_CLASS_NAME = "CustomKeywords";

    public CustomKeywordFolderBrowserTreeEntity(IKeywordBrowserTreeEntity parent) {
        super(TREE_ITEM_LABEL, parent);
    }

    @Override
    public boolean hasChildren() {
        try {
            if (ProjectController.getInstance().getCurrentProject() != null
                    && KeywordController.getInstance()
                            .getCustomKeywords(ProjectController.getInstance().getCurrentProject()).size() > 0) {
                return true;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Override
    public Object[] getChildren() {
        try {
            return getKeywordByKeywordObject().toArray();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    private List<IKeywordBrowserTreeEntity> getKeywordByKeywordObject() throws Exception {
        List<IKeywordBrowserTreeEntity> childTreeEntityList = new ArrayList<IKeywordBrowserTreeEntity>();
        if (ProjectController.getInstance().getCurrentProject() != null) {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            URLClassLoader classLoader = ProjectController.getInstance().getProjectClassLoader(projectEntity);
            IFolder srcFolder = GroovyUtil.getCustomKeywordSourceFolder(projectEntity);
            IFolder pluginFolder = GroovyUtil.getPluginsFolder(projectEntity);
            List<Method> allKeywordMethod = CustomKeywordParser.getInstance().parseProjectCustomKeywordsIntoAst(
            		classLoader, srcFolder);
            
            allKeywordMethod.addAll(CustomKeywordParser.getInstance().parsePluginKeywordsIntoAst(
                    classLoader, pluginFolder));
            
            for (File customKeywordPlugin : ProjectController.getInstance().getCustomKeywordPlugins(projectEntity)) {
                allKeywordMethod.addAll(CustomKeywordParser.getInstance().parsePluginKeywordJarIntoAst(classLoader,
                        customKeywordPlugin));
            }
            
            Map<String, List<Method>> methodActionMap = new HashMap<String, List<Method>>();

            for (Method method : allKeywordMethod) {
                String declareClassName = method.getDeclaringClass().getName();
                List<Method> methodList = methodActionMap.get(declareClassName);
                if (methodList == null) {
                    methodList = new ArrayList<Method>();
                    methodActionMap.put(declareClassName, methodList);
                }
                methodList.add(method);
            }
            List<String> packageList = new ArrayList<String>();
            Iterator<Entry<String, List<Method>>> it = methodActionMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, List<Method>> pair = (Entry<String, List<Method>>) it.next();

                String declareClassName = pair.getKey();
                int lastIndexOfDot = declareClassName.lastIndexOf(".");
                String className = declareClassName.substring(lastIndexOfDot + 1, declareClassName.length());
                String packageName = declareClassName.substring(0, lastIndexOfDot);
                KeywordBrowserFolderTreeEntity packageFolder = new KeywordBrowserFolderTreeEntity(packageName, this);
                KeywordBrowserFolderTreeEntity keywordFolder = new KeywordBrowserFolderTreeEntity(className,
                        packageFolder);
                packageFolder.children.add(keywordFolder);
                for (Method method : pair.getValue()) {
                    keywordFolder.children.add(new KeywordBrowserTreeEntity(CUSTOM_KEYWORD_CLASS_NAME,
                            CUSTOM_KEYWORD_CLASS_NAME, method.getName(), true, keywordFolder));
                }

                Collections.sort(keywordFolder.children, new Comparator<IKeywordBrowserTreeEntity>() {

                    @Override
                    public int compare(IKeywordBrowserTreeEntity keywordA, IKeywordBrowserTreeEntity keywordB) {
                        return keywordA.getName().compareToIgnoreCase(keywordB.getName());
                    }
                });
                
                if (packageList.contains(packageName)) {
                    KeywordBrowserFolderTreeEntity classFolder = (KeywordBrowserFolderTreeEntity) childTreeEntityList
                            .get(packageList.indexOf(packageName));
                    classFolder.appendNewChild(keywordFolder);
                } else {
                    packageList.add(packageName);
                    childTreeEntityList.add(packageFolder);
                }
            }
        }
        return childTreeEntityList;
    }
}
