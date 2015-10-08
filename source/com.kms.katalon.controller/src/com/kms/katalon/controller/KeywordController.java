package com.kms.katalon.controller;

import java.lang.reflect.Method;
import java.util.List;

import org.codehaus.groovy.ast.MethodNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;

import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.custom.factory.BuiltInMethodNodeFactory;
import com.kms.katalon.custom.factory.CustomMethodNodeFactory;
import com.kms.katalon.custom.parser.CustomKeywordParser;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class KeywordController extends EntityController {
    private static EntityController _instance;

    private KeywordController() {
        super();
    }

    public static KeywordController getInstance() {
        if (_instance == null) {
            _instance = new KeywordController();
        }
        return (KeywordController) _instance;
    }

    public List<Class<?>> getBuiltInKeywordClasses() {
        return BuiltInMethodNodeFactory.getInstance().getKeywordClasses();
    }

    public List<IKeywordContributor> getBuiltInKeywordContributors() {
        return BuiltInMethodNodeFactory.getInstance().getKeywordContributors();
    }

    public IKeywordContributor getBuiltInKeywordContributor(String builtInKeywordClassName) {
        for (IKeywordContributor contributor : BuiltInMethodNodeFactory.getInstance().getKeywordContributors()) {
            if (contributor.getKeywordClass().getName().equals(builtInKeywordClassName)) {
                return contributor;
            }
        }
        return null;
    }

    public boolean isCustomKeywordClass(String className) {
        if (className == null || className.isEmpty()) return false;
        return CustomMethodNodeFactory.getInstance().isCustomKeywordClass(className);
    }

    public boolean isBuiltinKeywordClassName(String className) {
        if (className == null || className.isEmpty()) return false;
        return BuiltInMethodNodeFactory.getInstance().getKeywordClassNames().contains(className);
    }

    public Class<?> getBuiltInKeywordClass(String className) {
        if (className == null || className.isEmpty()) return null;
        for (IKeywordContributor contributor : BuiltInMethodNodeFactory.getInstance().getKeywordContributors()) {
            Class<?> clazz = contributor.getKeywordClass();
            if (className.equals(clazz.getName()) || className.equals(clazz.getSimpleName())) {
                return clazz;
            }
        }
        return null;
    }

    public Method getBuiltInKeywordByName(String className, String keywordName) throws Exception {
        List<Method> methods = getBuiltInKeywords(className);
        for (Method method : methods) {
            if (method.getName().equals(keywordName)) {
                return method;
            }
        }

        if (keywordName.equals(BuiltInMethodNodeFactory.CALL_TEST_CASE_METHOD_NAME)) {
            return BuiltInMethodNodeFactory.getInstance().getCallTestCaseMethod(className);
        }
        return null;
    }

    public List<String> getParameterName(Method method) throws Exception {
        return BuiltInMethodNodeFactory.getInstance().getParameterNames(method);
    }

    public List<Method> getBuiltInKeywords(String builtInKeywordClassName) throws Exception {
        return BuiltInMethodNodeFactory.getInstance().getSortedMethods(builtInKeywordClassName);
    }

    public List<MethodNode> getCustomKeywords(ProjectEntity project) throws Exception {
        IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
        return CustomKeywordParser.getInstance().getAllMethodNodes(libFolder);
    }

    public MethodNode getCustomKeywordByName(String className, String methodName, ProjectEntity project)
            throws Exception {
        List<MethodNode> methodNodes = getCustomKeywords(project);
        if (methodNodes != null && methodNodes.size() > 0) {
            for (MethodNode methodNode : methodNodes) {
                if ((methodNode.getDeclaringClass().getNameWithoutPackage().equals(className) || methodNode
                        .getDeclaringClass().getName().equals(className))
                        && methodNode.getName().equals(getRawCustomKeywordName(methodName))) {
                    return methodNode;
                }
            }
        }
        return null;
    }

    /**
     * @param customKeywordName
     * @return remove quotes in custom keyword name if existed
     */
    public String getRawCustomKeywordName(String customKeywordName) {
        return customKeywordName.replace("'", "").replace("\"", "");
    }

    /**
     * 
     * @param customKeywordName
     * @return enclose name of a custom keyword in 2 single quotes if it doesn't
     */
    public String getCustomKeywordName(String customKeywordName) {
        if (!customKeywordName.startsWith("'") && !customKeywordName.startsWith("\"")) {
            return "'" + customKeywordName + "'";
        } else {
            return customKeywordName;
        }
    }

    public void parseAllCustomKeywordsWithoutRefreshing(ProjectEntity project) throws Exception {
        IFolder srcFolder = GroovyUtil.getCustomKeywordSourceFolder(project);
        IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
        CustomKeywordParser.getInstance().parseAllCustomKeywords(srcFolder, libFolder);
    }

    public void parseAllCustomKeywords(ProjectEntity project, IProgressMonitor monitor) throws Exception {
        try {
            if (monitor != null) {
                monitor.beginTask("Parsing custom keywords...", 1);
            }
            IFolder srcFolder = GroovyUtil.getCustomKeywordSourceFolder(project);
            IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
            CustomKeywordParser.getInstance().parseAllCustomKeywords(srcFolder, libFolder);
            refreshCustomKeywordLibFile(project, monitor);
        } finally {
            if (monitor != null) {
                monitor.done();
            }
        }
    }

    public List<Method> getAllCustomKeywordsAsAst(ProjectEntity project) throws Exception {
        IFolder srcFolder = GroovyUtil.getCustomKeywordSourceFolder(project);
        return CustomKeywordParser.getInstance().parseAllCustomKeywordsIntoAst(
                GroovyUtil.getProjectClasLoader(project), srcFolder);
    }

    public void parseCustomKeywordFile(IFile file, ProjectEntity project) throws Exception {
        IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
        CustomKeywordParser.getInstance().parseCustomKeywordFile(file, libFolder, true);
        refreshCustomKeywordLibFile(project, null);
    }

    public void removeMethodNodesCustomKeywordFile(IFile file, ProjectEntity project) throws Exception {
        IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
        CustomKeywordParser.getInstance().removeMethodNodesCustomKeywordFile(file, libFolder);
        refreshCustomKeywordLibFile(project, null);
    }

    public void parseCustomKeywordInPackage(IPackageFragment packageFragment, ProjectEntity project) throws Exception {
        IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
        CustomKeywordParser.getInstance().parseCustomKeywordInPackage(GroovyUtil.getAllGroovyClasses(packageFragment),
                libFolder);
        refreshCustomKeywordLibFile(project, null);
    }

    private void refreshCustomKeywordLibFile(ProjectEntity project, IProgressMonitor monitor) throws Exception {
        GroovyUtil.getCustomKeywordLibFolder(project).refreshLocal(IResource.DEPTH_ONE, monitor);
    }
}
