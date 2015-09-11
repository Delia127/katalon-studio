package com.kms.katalon.custom.parser;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import com.kms.katalon.custom.factory.CustomMethodNodeFactory;

public class CustomKeywordParser {
    private final static String TEMPLATE_CLASS_NAME = IdConstants.KATALON_CUSTOM_BUNDLE_ID
            + ".generation.CustomKeywordTemplate";
    private final static String GENERATED_KEYWORD_METHOD_NAME = "generateCustomKeywordFile";
    private static CustomKeywordParser _instance;
    private ClassNode classNode;

    private CustomKeywordParser() {
    }

    public static CustomKeywordParser getInstance() {
        if (_instance == null) {
            _instance = new CustomKeywordParser();
        }
        return _instance;
    }

    public void parseAllCustomKeywords(IFolder srcfolder, IFolder libFolder) throws Exception {
        CustomMethodNodeFactory.getInstance().reset();
        List<IFile> customKeywordFiles = getAllCustomKeywordFiles(srcfolder);
        for (IFile file : customKeywordFiles) {
            parseCustomKeywordFile(file, libFolder, false);
        }

        generateCustomKeywordLibFile(libFolder);
    }

    public List<Method> parseAllCustomKeywordsIntoAst(URLClassLoader classLoader, IFolder srcfolder) throws Exception {
        CustomMethodNodeFactory.getInstance().reset();
        List<Method> allMethods = new ArrayList<Method>();
        List<IFile> customKeywordFiles = getAllCustomKeywordFiles(srcfolder);
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

    public void parseCustomKeywordFile(IFile file, IFolder libFolder, boolean generateLibFile) throws Exception {
        try {
            GroovyCompilationUnit unit = (GroovyCompilationUnit) JavaCore.createCompilationUnitFrom(file);
            for (ClassNode classNode : unit.getModuleNode().getClasses()) {
                CustomMethodNodeFactory.getInstance().addMethodNodes(classNode.getName(), classNode.getMethods());
            }
        } catch (Exception e) {
            // do nothing
        }
        if (generateLibFile)
            generateCustomKeywordLibFile(libFolder);
    }

    public void parseCustomKeywordFile(ICompilationUnit file, IFolder libFolder, boolean generateLibFile)
            throws Exception {
        try {
            if (file instanceof GroovyCompilationUnit) {
                GroovyCompilationUnit unit = (GroovyCompilationUnit) file;
                for (ClassNode classNode : unit.getModuleNode().getClasses()) {
                    CustomMethodNodeFactory.getInstance().addMethodNodes(classNode.getName(), classNode.getMethods());
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        if (generateLibFile)
            generateCustomKeywordLibFile(libFolder);
    }

    public void parseCustomKeywordInPackage(List<ICompilationUnit> keywordFiles, IFolder libFolder) throws Exception {
        for (ICompilationUnit file : keywordFiles) {
            parseCustomKeywordFile(file, libFolder, false);
        }
        generateCustomKeywordLibFile(libFolder);
    }

    public void removeMethodNodesCustomKeywordFile(IFile file, IFolder libFolder) throws Exception {
        GroovyCompilationUnit unit = (GroovyCompilationUnit) JavaCore.createCompilationUnitFrom(file);
        for (ClassNode classNode : unit.getModuleNode().getClasses()) {
            CustomMethodNodeFactory.getInstance().removeMethodNodes(classNode.getName());
        }
        generateCustomKeywordLibFile(libFolder);
    }

    private List<IFile> getAllCustomKeywordFiles(IFolder folder) throws Exception {
        List<IFile> children = new ArrayList<IFile>();
        for (IResource resource : folder.members()) {
            if (resource instanceof IFile) {
                IFile file = (IFile) resource;
                if ("groovy".equals(file.getFileExtension())) {
                    children.add((IFile) resource);
                }
            } else if (resource instanceof IFolder) {
                children.addAll(getAllCustomKeywordFiles((IFolder) resource));
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
        File file = new File(libFolderRaw, "CustomKeywords.groovy");
        if (!file.exists()) {
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
        GroovyCompilationUnit unit = (GroovyCompilationUnit) JavaCore.createCompilationUnitFrom(iFile);
        classNode = unit.getModuleNode().getClasses().get(0);
    }

    public List<MethodNode> getAllMethodNodes(IFolder libFolder) throws Exception {
        if (classNode != null && classNode.getModule() != null && classNode.getModule().getMethods() != null) {
            List<MethodNode> methodNodes = classNode.getModule().getMethods();

            // Sort by ascending method name
            Collections.sort(methodNodes, new Comparator<MethodNode>() {

                @Override
                public int compare(MethodNode methodA, MethodNode methodB) {
                    return (methodA.getName().compareToIgnoreCase(methodB.getName()));
                }
            });
            return methodNodes;
        }
        return Collections.emptyList();
    }
}
