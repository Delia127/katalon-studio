package com.kms.katalon.custom.parser;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.annotation.Keyword;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.custom.factory.CustomKeywordPluginFactory;
import com.kms.katalon.custom.factory.CustomMethodNodeFactory;
import com.kms.katalon.custom.factory.PluginTestListenerFactory;
import com.kms.katalon.custom.keyword.CustomKeywordPlugin;
import com.kms.katalon.custom.keyword.KeywordsManifest;
import com.kms.katalon.logging.LogUtil;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovyjarjarasm.asm.ClassReader;

public class CustomKeywordParser {

    private static final String CUSTOM_KEYWORDS_FILE_NAME = "CustomKeywords.groovy";

    private final static String TEMPLATE_CLASS_NAME = IdConstants.KATALON_CUSTOM_BUNDLE_ID
            + ".generation.CustomKeywordTemplate";

    private final static String GENERATED_KEYWORD_METHOD_NAME = "generateCustomKeywordFile";

    private static CustomKeywordParser _instance;

    private static List<MethodNode> methodNodes = new ArrayList<MethodNode>();

    private CustomKeywordParser() {
    }

    public static CustomKeywordParser getInstance() {
        if (_instance == null) {
            _instance = new CustomKeywordParser();
        }
        return _instance;
    }

    public void parseProjectCustomKeywords(IFolder srcfolder, IFolder libFolder) throws Exception {
        CustomMethodNodeFactory.getInstance().reset();
        List<IFile> customKeywordFiles = getProjectCustomKeywordFiles(srcfolder);
        for (IFile file : customKeywordFiles) {
            parseCustomKeywordFile(file, libFolder, false);
        }

        generateCustomKeywordLibFile(libFolder);
    }

    public void parsePluginKeywords(ClassLoader projectClassLoader, List<File> pluginFiles, IFolder libFolder,
            boolean isDev) throws Exception {
        for (File pluginFile : pluginFiles) {
            if (pluginFile.getName().endsWith(".jar")) {
                JarFile jar = new JarFile(pluginFile);
                try {
                    ZipEntry jsonEntry = jar.getEntry("katalon-plugin.json");

                    if (jsonEntry != null) {
                        Reader reader = new InputStreamReader(jar.getInputStream(jsonEntry));
                        KeywordsManifest manifest = JsonUtil.fromJson(reader, KeywordsManifest.class);
                        CustomKeywordPlugin plugin = null;
                        if (isDev) {
                            plugin = CustomKeywordPluginFactory.getInstance().getDevPlugin();

                            LogUtil.printOutputLine(
                                    MessageFormat.format("Custom keyword plugin found: {0}. Manifest content:\n{1}",
                                            pluginFile.getName(), JsonUtil.toJson(manifest)));
                        } else {
                            plugin = CustomKeywordPluginFactory.getInstance().getByPath(pluginFile.getCanonicalPath());
                        }
                        if (plugin == null) {
                            LogUtil.logErrorMessage(
                                    "Could not load Custom Keywords for plugin: " + pluginFile.getName());
                            return;
                        }
                        plugin.setKeywordsManifest(manifest);

                        Set<String> keywords = new LinkedHashSet<String>();
                        keywords.addAll(manifest.getKeywords());
                        for (String keyword : keywords) {
                            String filePath = pluginFile.getAbsolutePath();
                            Class<?> clazz = null;
                            try {
                                clazz = projectClassLoader.loadClass(keyword);
                            } catch (ClassNotFoundException e) {
                                LogUtil.logError(e,
                                        MessageFormat.format("Could not load class: {0} of custom plugin keyword: {1}",
                                                keyword, pluginFile.getName()));
                                continue;
                            }
                            ClassNode classNode = new ClassNode(clazz);

                            InputStream stream = projectClassLoader
                                    .getResourceAsStream(keyword.replace('.', '/') + ".class");
                            ClassReader classReader = new ClassReader(stream);
                            NamingMethodVisitor visitor = new NamingMethodVisitor(clazz);
                            classReader.accept(visitor, ClassReader.SKIP_FRAMES);

                            Map<String, List<String>> parametersMap = new HashMap<>();
                            classNode.getMethods().forEach(methodNode -> {
                                String typesClassName = MethodUtils.getParametersDescriptor(methodNode);
                                String methodName = methodNode.getName() + "#" + typesClassName;
                                List<String> paramNames = visitor.getParameterNames(methodName);
                                parametersMap.put(methodName, paramNames);
                            });
                            CustomMethodNodeFactory.getInstance().addPluginMethodNodes(classNode.getName(),
                                    classNode.getMethods(), filePath, parametersMap);
                        }
                        
                        List<String> listeners = manifest.getListeners();
                        for (String listener : listeners) {
                            PluginTestListenerFactory.getInstance().addListener(listener);
                        }
                    }
                } catch (Exception e) {
                    LogUtil.printAndLogError(e);
                } finally {
                    jar.close();
                }
            }
        }

        generateCustomKeywordLibFile(libFolder);
    }

    public List<Method> parseProjectCustomKeywordsIntoAst(URLClassLoader classLoader, IFolder srcfolder)
            throws Exception {

        List<Method> allMethods = new ArrayList<Method>();
        List<IFile> customKeywordFiles = getProjectCustomKeywordFiles(srcfolder);
        for (IFile file : customKeywordFiles) {
            allMethods.addAll(parseCustomKeywordFileIntoAst(classLoader, file));
        }

        Collections.sort(allMethods, new Comparator<Method>() {

            @Override
            public int compare(Method methodA, Method methodB) {
                return methodA.getName().compareToIgnoreCase(methodB.getName());
            }

        });

        return allMethods;
    }

    public List<Method> parseCustomKeywordFileIntoAst(URLClassLoader classLoader, IFile file) throws Exception {
        if (file.exists() && classLoader instanceof GroovyClassLoader) {
            String className = file.getName().replace("." + file.getFileExtension(), "");
            IJavaElement javaElement = JavaCore.create(file);
            String packageName = "";
            if (javaElement.getParent() instanceof IPackageFragment) {
                IPackageFragment packageFragment = (IPackageFragment) javaElement.getParent();
                packageName = packageFragment.getElementName();
                if (!packageName.isEmpty()) {
                    packageName += ".";
                }
            }
            Class<?> clazz = ((GroovyClassLoader) classLoader).loadClass(packageName + className);
            if (clazz != null) {
                List<Method> methods = new ArrayList<Method>();
                for (Method method : clazz.getMethods()) {
                    for (Annotation annotation : method.getDeclaredAnnotations()) {
                        if (annotation.annotationType().getName().equals(Keyword.class.getName())) {
                            methods.add(method);
                            break;
                        }
                    }
                }
                return methods;
            }
        }
        return Collections.emptyList();
    }

    public List<Method> parsePluginKeywordsIntoAst(URLClassLoader classLoader, IFolder srcfolder) throws Exception {
        File srcDir = srcfolder.getRawLocation().toFile();
        File[] jarFiles = srcDir.listFiles();
        List<Method> methods = new ArrayList<>();
        for (File jarFile : jarFiles) {
            methods.addAll(parsePluginKeywordJarIntoAst(classLoader, jarFile));
        }
        Collections.sort(methods, new Comparator<Method>() {

            @Override
            public int compare(Method methodA, Method methodB) {
                return methodA.getName().compareToIgnoreCase(methodB.getName());
            }

        });
        return methods;
    }

    public List<Method> parsePluginKeywordJarIntoAst(URLClassLoader classLoader, File pluginFile) throws Exception {
        List<Method> methods = new ArrayList<>();
        if (pluginFile.exists() && pluginFile.getName().endsWith(".jar") && classLoader instanceof GroovyClassLoader) {
            JarFile jar = new JarFile(pluginFile);
            try {
                ZipEntry jsonEntry = jar.getEntry("katalon-plugin.json");

                if (jsonEntry != null) {
                    Reader reader = new InputStreamReader(jar.getInputStream(jsonEntry));
                    KeywordsManifest manifest = JsonUtil.fromJson(reader, KeywordsManifest.class);
                    Set<String> keywords = new LinkedHashSet<String>();
                    keywords.addAll(manifest.getKeywords());
                    for (String keyword : keywords) {
                        Class clazz = classLoader.loadClass(keyword);
                        if (clazz != null) {
                            for (Method method : clazz.getMethods()) {
                                for (Annotation annotation : method.getDeclaredAnnotations()) {
                                    if (annotation.annotationType().getName().equals(Keyword.class.getName())) {
                                        methods.add(method);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                jar.close();
            }
        }
        return methods;
    }

    public void parseCustomKeywordFile(IFile file, IFolder libFolder, boolean generateLibFile) throws Exception {
        try {
            GroovyCompilationUnit unit = (GroovyCompilationUnit) JavaCore.createCompilationUnitFrom(file);
            String filePath = file.getFullPath().toOSString();
            CustomMethodNodeFactory.getInstance().removeMethodNodes(filePath);
            for (ClassNode classNode : unit.getModuleNode().getClasses()) {
                CustomMethodNodeFactory.getInstance().addMethodNodes(classNode.getName(), classNode.getMethods(),
                        filePath);
            }
        } catch (Exception e) {
            // do nothing
        }
        if (generateLibFile) {
            generateCustomKeywordLibFile(libFolder);
        }
    }

    public void parseCustomKeywordFile(ICompilationUnit file, IFolder libFolder, boolean generateLibFile)
            throws Exception {
        try {
            if (file instanceof GroovyCompilationUnit) {
                String filePath = file.getPath().toOSString();
                GroovyCompilationUnit unit = (GroovyCompilationUnit) file;
                CustomMethodNodeFactory.getInstance().removeMethodNodes(filePath);
                for (ClassNode classNode : unit.getModuleNode().getClasses()) {
                    CustomMethodNodeFactory.getInstance().addMethodNodes(classNode.getName(), classNode.getMethods(),
                            filePath);
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        if (generateLibFile) {
            generateCustomKeywordLibFile(libFolder);
        }
    }

    public void parseCustomKeywordInPackage(List<ICompilationUnit> keywordFiles, IFolder libFolder) throws Exception {
        for (ICompilationUnit file : keywordFiles) {
            parseCustomKeywordFile(file, libFolder, false);
        }
        generateCustomKeywordLibFile(libFolder);
    }

    public void removeMethodNodesCustomKeywordFile(IFile file, IFolder libFolder) throws Exception {
        CustomMethodNodeFactory.getInstance().removeMethodNodes(file.getFullPath().toOSString());
        generateCustomKeywordLibFile(libFolder);
    }

    private List<IFile> getProjectCustomKeywordFiles(IFolder folder) throws Exception {
        List<IFile> children = new ArrayList<IFile>();
        for (IResource resource : folder.members()) {
            if (resource instanceof IFile) {
                IFile file = (IFile) resource;
                if ("groovy".equals(file.getFileExtension())) {
                    children.add((IFile) resource);
                }
            } else if (resource instanceof IFolder) {
                children.addAll(getProjectCustomKeywordFiles((IFolder) resource));
            }
        }
        return children;
    }

    @SuppressWarnings("rawtypes")
    private void generateCustomKeywordLibFile(IFolder libFolder) throws Exception {
        File libFolderRaw = new File(libFolder.getRawLocation().toString());
        if (!libFolderRaw.exists()) {
            libFolderRaw.mkdirs();
        }
        IFile iFile = null;
        File file = new File(libFolderRaw, CUSTOM_KEYWORDS_FILE_NAME);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        iFile = libFolder.getFile(file.getName());
        iFile.refreshLocal(IResource.DEPTH_ZERO, null);
        while (!file.canWrite()) {
            // wait for refreshing
            Thread.sleep(5);
        }

        Class clazz = Class.forName(TEMPLATE_CLASS_NAME);
        GroovyObject object = (GroovyObject) clazz.newInstance();
        object.invokeMethod(GENERATED_KEYWORD_METHOD_NAME, new Object[] { file });

        iFile.refreshLocal(IResource.DEPTH_ZERO, null);

        // After generated CustomKeywords.groovy proxy class, parse and list out all its methods for later reuse
        loadAllCustomKeywordProxyMethods(libFolder);
    }

    /**
     * Should not reload Custom Keywords so frequently,
     * if user modified Custom Keywords, open project at the first time,
     * should reload it that that & reuse
     **/
    public List<MethodNode> getAllMethodNodes(IFolder libFolder) {
        if (methodNodes == null || methodNodes.isEmpty()) {
            loadAllCustomKeywordProxyMethods(libFolder);
        }
        return methodNodes;
    }

    /**
     * To load all methods in proxy class CustomKeywords,
     * for best performance, this job should do after loading project for the first time
     * or when user updated/refresh custom keywords node
     **/
    private void loadAllCustomKeywordProxyMethods(IFolder libFolder) {
        IFile iFile = libFolder.getFile(CUSTOM_KEYWORDS_FILE_NAME);
        GroovyCompilationUnit unit = (GroovyCompilationUnit) JavaCore.createCompilationUnitFrom(iFile);
        ClassNode classNode = unit.getModuleNode().getClasses().get(0);
        if (classNode != null && classNode.getModule() != null && classNode.getModule().getMethods() != null) {
            methodNodes = classNode.getModule().getMethods();
            // Sort by ascending method name
            Collections.sort(methodNodes, new Comparator<MethodNode>() {
                @Override
                public int compare(MethodNode methodA, MethodNode methodB) {
                    return (methodA.getName().compareToIgnoreCase(methodB.getName()));
                }
            });
        }
    }
}
