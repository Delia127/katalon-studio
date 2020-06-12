package com.kms.katalon.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.groovy.ast.MethodNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;

import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.custom.factory.BuiltInMethodNodeFactory;
import com.kms.katalon.custom.factory.CustomKeywordPluginFactory;
import com.kms.katalon.custom.factory.CustomMethodNodeFactory;
import com.kms.katalon.custom.factory.PluginTestListenerFactory;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.custom.keyword.KeywordMethod;
import com.kms.katalon.custom.parser.CustomKeywordParser;
import com.kms.katalon.custom.util.BuiltinPluginUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class KeywordController extends EntityController {
    private static final String TESTNG_BUILTIN_KEYWORDS_CLASS_NAME = "TestNGBuiltinKeywords";
    
    public static final String CUSTOM_KEYWORD_CLASS_NAME = "CustomKeywords";
    
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

    public List<KeywordClass> getBuiltInKeywordClasses() {
        List<KeywordClass> keywordClasses = BuiltInMethodNodeFactory.getKeywordClasses();
        boolean isTestNGPluginInstalled = BuiltinPluginUtil.isTestNGPluginInstalled();
        if (isTestNGPluginInstalled) {
            return keywordClasses;
        }
        List<KeywordClass> filteredClasses = new ArrayList<>();
        for (KeywordClass keywordClass : keywordClasses) {
            if (!TESTNG_BUILTIN_KEYWORDS_CLASS_NAME.equals(keywordClass.getSimpleName())) {
                filteredClasses.add(keywordClass);
            }
        }
        return filteredClasses;
    }

    public KeywordClass getBuiltInKeywordClassByName(String builtInKeywordClassName) {
        return BuiltInMethodNodeFactory.findClass(builtInKeywordClassName);
    }

    public boolean isCustomKeywordClass(String className) {
        if (className == null || className.isEmpty())
            return false;
        return CustomMethodNodeFactory.getInstance().isCustomKeywordClass(className);
    }

    public boolean isBuiltinKeywordClassName(String className) {
        if (className == null || className.isEmpty()) {
            return false;
        }
        return BuiltInMethodNodeFactory.findClass(className) != null;
    }

    public KeywordMethod getBuiltInKeywordByName(String className, String keywordName, String[] paramTypes) {
        return BuiltInMethodNodeFactory.findMethod(className, keywordName, paramTypes);
    }

    public KeywordMethod getBuiltInKeywordByName(KeywordClass keywordClass, String keywordName) {
        return BuiltInMethodNodeFactory.findMethod(keywordClass.getType().getName(), keywordName, null);
    }

    public KeywordMethod getBuiltInKeywordByName(String className, String keywordName) {
        return BuiltInMethodNodeFactory.findMethod(className, keywordName, null);
    }

    public List<KeywordMethod> getBuiltInKeywords(String builtInKeywordClassName) {
        return BuiltInMethodNodeFactory.getFilteredMethods(builtInKeywordClassName);
    }

    public List<KeywordMethod> getBuiltInKeywords(String builtInKeywordClassName, boolean excludeFlowControl) {
        return BuiltInMethodNodeFactory.getFilteredMethods(builtInKeywordClassName, excludeFlowControl);
    }

    public List<MethodNode> getCustomKeywords(ProjectEntity project) {
        return CustomKeywordParser.getInstance().getAllMethodNodes(GroovyUtil.getCustomKeywordLibFolder(project));
    }

    public MethodNode getCustomKeywordByName(String className, String methodName, ProjectEntity project) {
        List<MethodNode> methodNodes = getCustomKeywords(project);
        if (methodNodes != null && methodNodes.size() > 0) {
            for (MethodNode methodNode : methodNodes) {
                if ((methodNode.getDeclaringClass().getNameWithoutPackage().equals(className)
                        || methodNode.getDeclaringClass().getName().equals(className))
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
        CustomKeywordParser.getInstance().parseProjectCustomKeywords(srcFolder, libFolder);
    }

    public void parseAllCustomKeywords(ProjectEntity project, IProgressMonitor monitor) throws Exception {
        try {
            if (monitor != null) {
                monitor.beginTask("Parsing custom keywords...", 1);
            }
            PluginTestListenerFactory.getInstance().clear();
            IFolder srcFolder = GroovyUtil.getCustomKeywordSourceFolder(project);
            IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
            CustomKeywordParser.getInstance().parseProjectCustomKeywords(srcFolder, libFolder);

//            parseCustomKeywordInPluginDirectory(project, libFolder);

            parseCustomKeywordInStore(project, libFolder);

            refreshCustomKeywordLibFile(project, monitor);
        } finally {
            if (monitor != null) {
                monitor.done();
            }
        }
    }

    private void parseCustomKeywordInStore(ProjectEntity project, IFolder libFolder)
            throws MalformedURLException, CoreException, Exception, ControllerException {
        ClassLoader projectClassLoader = ProjectController.getInstance().getProjectClassLoader(project);
        CustomKeywordParser.getInstance().parsePluginKeywords(projectClassLoader,
                CustomKeywordPluginFactory.getInstance().getStoredPluginFiles(), libFolder, false);
    }

    public void loadCustomKeywordInPluginDirectory(ProjectEntity project) throws Exception {
        CustomKeywordPluginFactory.getInstance().clearDevPlugin();
        File pluginDir = new File (project.getFolderLocation(), GroovyUtil.PLUGINS_FOLDER_NAME);
        if (!pluginDir.exists()) {
            return;
        }
        File[] jarFiles = pluginDir.listFiles();
        if (jarFiles == null) {
            return;
        }
        File firstJar = Arrays.asList(jarFiles)
                .stream()
                .filter(f -> f.getName().endsWith(".jar"))
                .findFirst()
                .orElse(null);
        if (firstJar != null) {
            CustomKeywordPluginFactory.getInstance().addDevPluginFile(firstJar);
        }
    }

    private void parseCustomKeywordInPluginDirectory(ProjectEntity project, IFolder libFolder) throws Exception {
        IFolder pluginFolder = GroovyUtil.getPluginsFolder(project);
        ClassLoader projectClassLoader = ProjectController.getInstance().getProjectClassLoader(project);
        File pluginDir = pluginFolder.getRawLocation().toFile();
        File[] jarFiles = pluginDir.listFiles();
        File firstJar = Arrays.asList(jarFiles)
                .stream()
                .filter(f -> f.getName().endsWith(".jar"))
                .findFirst()
                .orElse(null);
        if (firstJar != null) {
            CustomKeywordParser.getInstance().parsePluginKeywords(projectClassLoader, Arrays.asList(firstJar),
                    libFolder, true);
        }
    }

    public void parseCustomKeywordFile(IFile file, ProjectEntity project) throws Exception {
        synchronized (file) {
            ProjectController.getInstance().updateProjectClassLoader(project);
            IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
            CustomKeywordParser.getInstance().parseCustomKeywordFile(file, libFolder, true);
            refreshCustomKeywordLibFile(project, null);
        }
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
