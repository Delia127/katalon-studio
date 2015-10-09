package com.kms.katalon.groovy.util;

import groovy.lang.GroovyClassLoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.eclipse.core.model.GroovyRuntime;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.groovy.GroovyParser;
import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.custom.factory.BuiltInMethodNodeFactory;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.helper.GroovyCompilationHelper;
import com.kms.katalon.selenium.TempClass;

@SuppressWarnings("restriction")
public class GroovyUtil {
    private static final String BUNDLE_LOCATION_INITIAL_PREFIX = "initial@";
    private static final String OUTPUT_FOLDER_NAME = "bin";
    private static final String TEST_CASE_OUTPUT_FOLDER_NAME = "testcase";
    private static final String KEYWORD_OUTPUT_FOLDER_NAME = "keyword";
    private static final String GROOVY_BUNDLE_LIB_FOLDER_NAME = "lib";
    private static final String GROOVY_BUNDLE_PLUGIN_FOLDER_NAME = "plugins";
    private static final String BUNDLE_LOCATION_PREFIX = "reference:file:";
    private static final String GROOVY_BUNDLE_ID = "org.codehaus.groovy";
    private static final String KEYWORD_SOURCE_FOLDER_NAME = "Keywords";
    private static final String TEST_SCRIPT_SOURCE_FOLDER_NAME = "Scripts";
    private static final String TEST_CASE_ROOT_FOLDER_NAME = "Test Cases";
    private static final String GROOVY_NATURE = "org.eclipse.jdt.groovy.core.groovyNature";

    private static final String KEYWORD_LIB_FOLDER_NAME = "Libs";
    private static final String KEYWORD_LIB_OUTPUT_FOLDER_NAME = "lib";

    private static final String DRIVERS_FOLDER_NAME = "Drivers";

    public static IProject getGroovyProject(ProjectEntity projectEntity) {
        return ResourcesPlugin.getWorkspace().getRoot()
                .getProject(getProjectNameIdFromLocation(projectEntity.getLocation()));
    }

    private static String getProjectNameIdFromLocation(String location) {
        return location.replace(File.separator, "%").replace(":", "%");
    }

    public static IPackageFragment getPackageFragmentFromLocation(String packageLocation, boolean isDefaultPackage)
            throws Exception {
        IFolder packageFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(packageLocation));
        packageFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
        if (packageFolder != null && packageFolder.exists()) {
            IJavaElement javaElement = JavaCore.create(packageFolder);
            if (javaElement instanceof IPackageFragment) {
                IPackageFragment packageFragment = (IPackageFragment) javaElement;
                if (!packageFragment.isOpen()) {
                    packageFragment.open(null);
                }
                return packageFragment;
            } else if (javaElement instanceof IPackageFragmentRoot && isDefaultPackage) {
                return ((IPackageFragmentRoot) javaElement).getPackageFragment(IPackageFragment.DEFAULT_PACKAGE_NAME);
            }
        }
        return null;
    }

    public static void initGroovyProject(ProjectEntity projectEntity, FolderEntity testCaseRootFolder,
            IProgressMonitor monitor) throws Exception {
        SubProgressMonitor subProgressDescription = null;
        SubProgressMonitor subProgressClasspath = null;
        if (monitor != null) {
            monitor.beginTask("Initialzing project's classpath...", 10);
            subProgressDescription = new SubProgressMonitor(monitor, 1,
                    SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
            subProgressClasspath = new SubProgressMonitor(monitor, 9, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
        }
        initGroovyProjectDescription(projectEntity, subProgressDescription);
        initGroovyProjectClassPath(projectEntity, testCaseRootFolder, true, subProgressClasspath);
    }

    private static void cleanDirectory(File folder) {
        if (!folder.exists()) {
            return;
        }
        long timeout = 30000;
        long startTime = System.currentTimeMillis();
        while (true && (System.currentTimeMillis() - startTime) <= timeout) {
            try {
                FileUtils.cleanDirectory(folder);
                return;
            } catch (IOException e) {

            }
        }
    }

    public static void initGroovyProjectClassPath(ProjectEntity projectEntity, FolderEntity testCaseRootFolder,
            boolean isNew, IProgressMonitor monitor) throws Exception {
        IProject groovyProject = getGroovyProject(projectEntity);
        IFolder keywordSourceFolder = groovyProject.getFolder(KEYWORD_SOURCE_FOLDER_NAME);
        if (!keywordSourceFolder.exists()) {
            keywordSourceFolder.create(true, true, null);
        }

        IFolder testCaseSourceFolder = groovyProject.getFolder(TEST_SCRIPT_SOURCE_FOLDER_NAME);
        if (!testCaseSourceFolder.exists()) {
            testCaseSourceFolder.create(true, true, null);
        }

        IFolder keywordLibFolder = groovyProject.getFolder(KEYWORD_LIB_FOLDER_NAME);
        if (!keywordLibFolder.exists()) {
            keywordLibFolder.create(true, true, null);
        } else {
            if (isNew) {
                keywordLibFolder.clearHistory(null);
                cleanDirectory(keywordLibFolder.getRawLocation().toFile());
            }
        }

        IFolder driversFolder = groovyProject.getFolder(DRIVERS_FOLDER_NAME);
        if (!driversFolder.exists()) {
            driversFolder.create(true, true, null);
        }

        IFolder outputParentFolder = groovyProject.getFolder(OUTPUT_FOLDER_NAME);
        if (!outputParentFolder.exists()) {
            outputParentFolder.create(true, true, null);
        }

        IFolder outputKeywordFolder = outputParentFolder.getFolder(KEYWORD_OUTPUT_FOLDER_NAME);
        if (!outputKeywordFolder.exists()) {
            outputKeywordFolder.create(true, true, null);
        }

        IFolder outputTestCaseFolder = outputParentFolder.getFolder(TEST_CASE_OUTPUT_FOLDER_NAME);
        if (!outputTestCaseFolder.exists()) {
            outputTestCaseFolder.create(true, true, null);
        } else if (isNew) {
            outputTestCaseFolder.clearHistory(null);
        }

        IFolder outputKWLibFolder = outputParentFolder.getFolder(KEYWORD_LIB_OUTPUT_FOLDER_NAME);
        if (!outputKWLibFolder.exists()) {
            outputKWLibFolder.create(true, true, null);
        } else if (isNew) {
            outputKWLibFolder.clearHistory(null);
        }

        IJavaProject javaProject = JavaCore.create(groovyProject);
        javaProject.setOutputLocation(outputParentFolder.getFullPath(), null);

        // groovy project classpath list
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

        // add JRE to classpath
        entries.add(JavaCore.newContainerEntry(JavaRuntime.getDefaultJREContainerEntry().getPath()));

        // add source and output folder to classpath
        IPackageFragmentRoot keywordPackageRoot = javaProject.getPackageFragmentRoot(keywordSourceFolder);
        entries.add(JavaCore.newSourceEntry(keywordPackageRoot.getPath(), new Path[] {}, new Path[] {},
                outputKeywordFolder.getFullPath()));

        // initTestCaseFolder(javaProject, testCaseRootFolder,
        // testCaseSourceFolder, outputTestCaseFolder, entries);

        IPackageFragmentRoot keywordLibPackageRoot = javaProject.getPackageFragmentRoot(keywordLibFolder);
        entries.add(JavaCore.newSourceEntry(keywordLibPackageRoot.getPath(), new Path[] {}, new Path[] {},
                outputKWLibFolder.getFullPath()));

        // add groovy plugin to classpath
        Bundle bundle = Platform.getBundle(GROOVY_BUNDLE_ID);
        if (bundle != null) {
            String groovyLocation = bundle.getLocation().replace(BUNDLE_LOCATION_PREFIX, "")
                    .replace(BUNDLE_LOCATION_INITIAL_PREFIX, "");
            if (groovyLocation.startsWith(GROOVY_BUNDLE_PLUGIN_FOLDER_NAME)) {
                groovyLocation = Platform.getInstallLocation().getURL().getPath() + groovyLocation;
            }

            File libFolder = new File(groovyLocation + File.separator + GROOVY_BUNDLE_LIB_FOLDER_NAME);
            // copy xerces and xmlApis bundle to groovy lib
            File xercesBundleFile = FileLocator.getBundleFile(Platform.getBundle(IdConstants.XERCES_BUNDLE_ID))
                    .getAbsoluteFile();
            File xmlApisBundleFile = FileLocator.getBundleFile(Platform.getBundle(IdConstants.XML_APIS_BUNDLE_ID))
                    .getAbsoluteFile();

            if (libFolder.exists()) {
                File desXercesBundleFile = new File(libFolder, xercesBundleFile.getName());
                File desXmlApisBundleFile = new File(libFolder, xmlApisBundleFile.getName());
                FileUtils.copyFile(xercesBundleFile, desXercesBundleFile);
                FileUtils.copyFile(xmlApisBundleFile, desXmlApisBundleFile);
            }
        }

        addClassPathOfCoreBundleToJavaProject(entries);

        // Add class path for external jars
        File driversDir = driversFolder.getRawLocation().toFile();
        for (File jarFile : driversDir.listFiles()) {
            if (jarFile.isFile() && jarFile.getName().endsWith(".jar")) {
                addJarFileToClasspath(jarFile, entries);
            }
        }

        javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), monitor);
        GroovyRuntime.addGroovyClasspathContainer(javaProject);
    }

    private static void addClassPathOfCoreBundleToJavaProject(List<IClasspathEntry> entries) throws IOException,
            BundleException {
        addClassPathOfCoreBundleToJavaProject(entries, Platform.getBundle(IdConstants.KATALON_CORE_BUNDLE_ID));

        addClassPathOfCoreBundleToJavaProject(entries, FrameworkUtil.getBundle(TempClass.class));
        for (IKeywordContributor contributor : BuiltInMethodNodeFactory.getInstance().getKeywordContributors()) {
            Bundle coreBundle = FrameworkUtil.getBundle(contributor.getClass());
            addClassPathOfCoreBundleToJavaProject(entries, coreBundle);
        }
    }

    private static void addClassPathOfCoreBundleToJavaProject(List<IClasspathEntry> entries, Bundle coreBundle)
            throws IOException, BundleException {
        if (coreBundle == null) return;

        File customBundleFile = FileLocator.getBundleFile(coreBundle).getAbsoluteFile();

        if (customBundleFile == null || !customBundleFile.exists()) return;

        if (customBundleFile.isDirectory()) { // built by IDE
            addSourceFolderToClassPath(customBundleFile, entries);
        } else {
            addJarFileToClasspath(customBundleFile, entries);

            // add all required bundles of katalon in configuration folder to
            // class path
            File eclipseDir = Platform.getLocation().toFile().getParentFile();

            if (Platform.getOS().equals(Platform.OS_MACOSX)) {
                // On MacOS, location of the current platform is <installed
                // folder>/Katalon.app/Contents/MacOS/config.
                // Therefore, we must move to <installed folder>.
                eclipseDir = eclipseDir.getParentFile().getParentFile().getParentFile();
            }

            File resourceDir = new File(eclipseDir, "configuration" + File.separator + "resources" + File.separator
                    + "lib");

            if (resourceDir.isDirectory()) {
                for (File jarFile : resourceDir.listFiles()) {
                    addJarFileToClasspath(jarFile, entries);
                }
            }
        }

        // add all required bundles of custom plug-in to class path
        String requireBundle = coreBundle.getHeaders().get(Constants.REQUIRE_BUNDLE);

        ManifestElement[] elements = ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH, requireBundle);
        if (elements == null) {
            return;
        }
        for (ManifestElement manifestElement : elements) {
            Bundle requiredBundle = Platform.getBundle(manifestElement.getValue());
            if (requiredBundle != null) {
                File requiredBundleLocation = FileLocator.getBundleFile(requiredBundle).getAbsoluteFile();
                if (requiredBundleLocation != null && requiredBundleLocation.exists()) {
                    if (requiredBundleLocation.isFile()) {
                        addJarFileToClasspath(requiredBundleLocation, entries);
                    }
                }
            }
        }
    }

    protected static void addSourceFolderToClassPath(File customBundleFile, List<IClasspathEntry> entries) {
        entries.add(JavaCore.newLibraryEntry(new Path(new File(customBundleFile, "bin").getAbsolutePath()), null, null));

        File resourceFolder = new File(customBundleFile, "resources" + File.separator + "lib");
        if (resourceFolder.exists() && resourceFolder.isDirectory()) {
            for (File jarFile : resourceFolder.listFiles()) {
                addJarFileToClasspath(jarFile, entries);
            }
        }
    }

    private static void addJarFileToClasspath(File jarFile, List<IClasspathEntry> entries) {
        if (checkRequiredBundleLocation(jarFile, entries)) {
            IClasspathEntry entry = JavaCore.newLibraryEntry(new Path(jarFile.getAbsolutePath()), null, null);
            if (entry != null && !entries.contains(entry)) {
                entries.add(entry);
            }
        }
    }

    private static boolean checkRequiredBundleLocation(File requiredBundleLocation, List<IClasspathEntry> entries) {
        String bundleName = FilenameUtils.getBaseName(requiredBundleLocation.getName());

        if (bundleName == null || bundleName.isEmpty()) return false;

        if (bundleName.contains("_")) {
            bundleName = bundleName.split("_")[0];
            if (bundleName == null || bundleName.isEmpty()) return false;
        }

        if ("org.eclipse.core.runtime".equalsIgnoreCase(bundleName)) return false;
        if ("com.kms.katalon.custom".equalsIgnoreCase(bundleName)) return false;

        for (IClasspathEntry childEntry : entries) {
            if ((childEntry.getPath() != null) && (childEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
                    && FilenameUtils.getBaseName(childEntry.getPath().toString()).equals(bundleName)) {
                return false;
            }
        }
        return true;
    }

    private static void initGroovyProjectDescription(ProjectEntity projectEntity, IProgressMonitor monitor)
            throws CoreException {
        IProject groovyProject = getGroovyProject(projectEntity);
        if (groovyProject == null || !groovyProject.exists()) {
            IProjectDescription projectDescription = ResourcesPlugin.getWorkspace().newProjectDescription(
                    projectEntity.getName());
            projectDescription.setLocation(new Path(projectEntity.getFolderLocation()));

            groovyProject.create(projectDescription, null);
            groovyProject.open(null);

        } else if (!groovyProject.isOpen()) {
            groovyProject.open(null);
        }

        IProjectDescription projectDescription = groovyProject.getDescription();
        projectDescription.setNatureIds(new String[] { GROOVY_NATURE, JavaCore.NATURE_ID });
        groovyProject.setDescription(projectDescription, monitor);
        groovyProject.refreshLocal(IResource.DEPTH_ZERO, monitor);
    }

    public static void updateGroovyProject(ProjectEntity projectEntity, IProject oldGroovyProject) throws CoreException {
        IProjectDescription projectDescription = oldGroovyProject.getDescription();
        projectDescription.setName(getProjectNameIdFromLocation(projectEntity.getLocation()));
        oldGroovyProject.move(projectDescription, true, null);
    }

    public static boolean isKeywordFile(String filePath, ProjectEntity projectEntity) {
        return filePath.contains(getKeywordSourceRootFolder(projectEntity).getLocation().toString());
    }

    public static boolean isScriptFile(String filePath, ProjectEntity projectEntity) {
        return filePath.contains(getTestCaseScriptSourceFolder(projectEntity).getLocation().toString());
    }

    public static IFolder getKeywordSourceRootFolder(ProjectEntity projectEntity) {
        return getGroovyProject(projectEntity).getFolder(KEYWORD_SOURCE_FOLDER_NAME);
    }

    public static List<IPackageFragment> getAllPackageInKeywordFolder(ProjectEntity projectEntity) throws Exception {
        IProject groovyProject = getGroovyProject(projectEntity);
        List<IPackageFragment> packageFragments = new ArrayList<IPackageFragment>();
        IPackageFragmentRoot root = JavaCore.create(groovyProject).getPackageFragmentRoot(
                groovyProject.getFolder(KEYWORD_SOURCE_FOLDER_NAME));
        for (IJavaElement javaElement : root.getChildren()) {
            if (javaElement instanceof IPackageFragment) {
                IPackageFragment packageFragment = (IPackageFragment) javaElement;
                if (packageFragment.getCompilationUnits().length > 0 || !packageFragment.hasSubpackages()) {
                    for (ICompilationUnit compilationUnit : packageFragment.getCompilationUnits()) {
                        if (compilationUnit.isWorkingCopy()) {
                            compilationUnit.discardWorkingCopy();
                        }
                    }
                    packageFragments.add((IPackageFragment) javaElement);
                }
            }
        }
        return packageFragments;
    }

    public static IPackageFragment getDefaultPackageForKeyword(ProjectEntity projectEntity) throws Exception {
        IProject groovyProject = getGroovyProject(projectEntity);
        return JavaCore.create(groovyProject)
                .getPackageFragmentRoot(groovyProject.getFolder(KEYWORD_SOURCE_FOLDER_NAME)).getPackageFragment("");
    }

    public static IPackageFragment getDefaultPackageForTestCase(ProjectEntity projectEntity) throws Exception {
        IProject groovyProject = getGroovyProject(projectEntity);
        return JavaCore.create(groovyProject)
                .getPackageFragmentRoot(groovyProject.getFolder(TEST_SCRIPT_SOURCE_FOLDER_NAME)).getPackageFragment("");
    }

    public static List<ICompilationUnit> getAllGroovyClasses(IPackageFragment packageFragment) throws Exception {
        List<ICompilationUnit> groovyClassFiles = new ArrayList<ICompilationUnit>();
        for (IJavaElement javaElement : packageFragment.getChildren()) {
            if (javaElement instanceof GroovyCompilationUnit) {
                groovyClassFiles.add((GroovyCompilationUnit) javaElement);
            }
        }
        return groovyClassFiles;
    }

    public static void openGroovyProject(ProjectEntity projectEntity, FolderEntity testCaseRootFolder) throws Exception {
        initGroovyProject(projectEntity, testCaseRootFolder, null);
        IProject groovyProject = getGroovyProject(projectEntity);
        if (groovyProject.exists() && !groovyProject.isOpen()) {
            groovyProject.open(null);
        }
    }

    public static IFolder getCustomKeywordSourceFolder(ProjectEntity project) {
        IProject groovyProject = getGroovyProject(project);
        return groovyProject.getFolder(KEYWORD_SOURCE_FOLDER_NAME);
    }

    public static IFolder getTestCaseScriptSourceFolder(ProjectEntity project) {
        IProject groovyProject = getGroovyProject(project);
        return groovyProject.getFolder(TEST_SCRIPT_SOURCE_FOLDER_NAME);
    }

    public static String getTestCaseIdByEditor(ProjectEntity projectEntity, IEditorPart editor) {
        IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
        String editorPath = input.getFile().getRawLocation().toFile().getAbsolutePath();
        return getTestCaseIdByScriptPath(editorPath, projectEntity);
    }

    public static String getTestCaseIdByScriptPath(String scriptFilePath, ProjectEntity projectEntity) {
        String testCaseScriptFolderPath = (new File(scriptFilePath)).getParent();

        String projectLocation = projectEntity.getFolderLocation();
        String testCaseScriptFolderRelativePath = testCaseScriptFolderPath.substring(projectLocation.length());
        return testCaseScriptFolderRelativePath.substring(1).replaceFirst(TEST_SCRIPT_SOURCE_FOLDER_NAME,
                TEST_CASE_ROOT_FOLDER_NAME);
    }

    public static IFolder getCustomKeywordLibFolder(ProjectEntity project) {
        IProject groovyProject = getGroovyProject(project);
        return groovyProject.getFolder(KEYWORD_LIB_FOLDER_NAME);
    }

    public static void copyPackage(IPackageFragment packageFragment, FolderEntity targetFolder, String newName)
            throws Exception {
        IProject groovyProject = getGroovyProject(targetFolder.getProject());
        IPackageFragmentRoot packageFragmentRoot = JavaCore.create(groovyProject).getPackageFragmentRoot(
                groovyProject.getFolder(KEYWORD_SOURCE_FOLDER_NAME));
        packageFragment.copy(packageFragmentRoot, null, newName, false, null);
    }

    public static void copyKeyword(IFile keywordFile, IPackageFragment targetPackageFragment, String newName)
            throws Exception {
        GroovyCompilationUnit compilationUnit = (GroovyCompilationUnit) JavaCore.create(keywordFile);
        compilationUnit.copy(targetPackageFragment, null, newName != null ? newName
                + GroovyConstants.GROOVY_FILE_EXTENSION : newName, false, null);
    }

    public static void moveKeyword(IFile keywordFile, IPackageFragment targetPackageFragment, String newName)
            throws Exception {
        GroovyCompilationUnit compilationUnit = (GroovyCompilationUnit) JavaCore.create(keywordFile);
        compilationUnit.move(targetPackageFragment, null, newName != null ? newName
                + GroovyConstants.GROOVY_FILE_EXTENSION : newName, false, null);
    }

    public static String getGroovyClassName(TestCaseEntity testCase) {
        return "Script" + System.currentTimeMillis();
    }

    public static ICompilationUnit createGroovyScript(IPackageFragment parentPackage, String typeName) throws Exception {
        return GroovyCompilationHelper.createGroovyType(parentPackage, typeName, false, false);
    }

    public static String getScriptPackageRelativePathForFolder(FolderEntity folder) {
        if (folder.getParentFolder() != null) {
            String relativeFolderLocation = folder.getParentFolder().getRelativePath();
            if (relativeFolderLocation.startsWith(TEST_CASE_ROOT_FOLDER_NAME)) {
                relativeFolderLocation = relativeFolderLocation.substring(TEST_CASE_ROOT_FOLDER_NAME.length(),
                        relativeFolderLocation.length());
            }
            return TEST_SCRIPT_SOURCE_FOLDER_NAME + relativeFolderLocation + File.separator + folder.getName();
        } else {
            return TEST_SCRIPT_SOURCE_FOLDER_NAME;
        }
    }

    public static String getScriptPackageRelativePathForTestCase(String testCaseRelativeId) {
        return TEST_SCRIPT_SOURCE_FOLDER_NAME
                + testCaseRelativeId.substring(TEST_CASE_ROOT_FOLDER_NAME.length(), testCaseRelativeId.length());
    }

    public static String getScriptPackageRelativePathForTestCase(TestCaseEntity testCase) {
        String relativeFolderLocation = testCase.getParentFolder().getRelativePath();
        if (relativeFolderLocation.startsWith(TEST_CASE_ROOT_FOLDER_NAME)) {
            relativeFolderLocation = relativeFolderLocation.substring(TEST_CASE_ROOT_FOLDER_NAME.length(),
                    relativeFolderLocation.length());
        }
        relativeFolderLocation = TEST_SCRIPT_SOURCE_FOLDER_NAME + relativeFolderLocation + File.separator
                + testCase.getName();
        return relativeFolderLocation;
    }

    public static String getRelativePathForFolder(FolderEntity folder) {
        String relativeFolderLocation = folder.getRelativePath();
        if (relativeFolderLocation.startsWith(TEST_CASE_ROOT_FOLDER_NAME)) {
            relativeFolderLocation = relativeFolderLocation.substring(TEST_CASE_ROOT_FOLDER_NAME.length(),
                    relativeFolderLocation.length());
        }
        relativeFolderLocation = TEST_SCRIPT_SOURCE_FOLDER_NAME + relativeFolderLocation;
        return relativeFolderLocation;
    }

    public static String getScriptPackageAbsolutePathForTestCase(TestCaseEntity testCase) {
        return testCase.getParentFolder().getProject().getFolderLocation() + File.separator
                + getScriptPackageRelativePathForTestCase(testCase);
    }

    public static File initTestCaseScriptFolder(FolderEntity folder) {
        if (folder.getParentFolder() == null) {
            return new File(folder.getProject().getFolderLocation() + File.separator + TEST_SCRIPT_SOURCE_FOLDER_NAME);
        } else {
            File folderFile = new File(initTestCaseScriptFolder(folder.getParentFolder()).getAbsolutePath()
                    + File.separator + folder.getName());
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            return folderFile;
        }
    }

    public static File getTestCaseScriptFolder(TestCaseEntity testCase) {
        File folderFile = initTestCaseScriptFolder(testCase.getParentFolder());
        File testCaseFolderFile = new File(folderFile.getAbsolutePath() + File.separator + testCase.getName());
        if (!testCaseFolderFile.exists()) {
            testCaseFolderFile.mkdirs();
        }
        return testCaseFolderFile;
    }

    public static IPackageFragment getParentPackageForTestCase(TestCaseEntity testCase, IProject groovyProject)
            throws Exception {
        // IFolder testCaseSourceFolder =
        // groovyProject.getFolder(getScriptPackageRelativePathForTestCase(testCase));
        //
        // IFolder parentSourceFolder = (IFolder)
        // testCaseSourceFolder.getParent();
        // parentSourceFolder.refreshLocal(IResource.DEPTH_ONE, null);
        //
        // if (!testCaseSourceFolder.exists()) {
        // createTestCaseFolder(testCaseSourceFolder, parentSourceFolder,
        // testCase);
        // }
        //
        // testCaseSourceFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
        //
        // IJavaProject javaProject =
        // JavaCore.create(groovyProject).getJavaProject();
        // IPackageFragmentRoot testCasePackageRoot =
        // javaProject.getPackageFragmentRoot(testCaseSourceFolder);
        // if (!testCasePackageRoot.exists()) {
        // testCasePackageRoot.createPackageFragment(PackageFragment.DEFAULT_PACKAGE_NAME,
        // true, null);
        // String testCaseClassName = getScriptNameForTestCase(testCase);
        // if (testCaseClassName != null) {
        //
        // IFolder outputTestCaseFolder =
        // getTestCaseRootOutputFolder(groovyProject).getFolder(testCaseClassName);
        // if (!outputTestCaseFolder.exists()) {
        // outputTestCaseFolder.create(true, true, null);
        // }
        //
        // IClasspathEntry classPathEntry =
        // JavaCore.newSourceEntry(testCasePackageRoot.getPath(), new Path[] {},
        // new Path[] {}, outputTestCaseFolder.getFullPath());
        // List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
        // entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
        // entries.add(classPathEntry);
        //
        // javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[0]),
        // null);
        // javaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE,
        // null);
        // }
        // }
        // return
        // testCasePackageRoot.getPackageFragment(PackageFragment.DEFAULT_PACKAGE_NAME);
        return null;
    }

    public static String getScriptNameForTestCase(TestCaseEntity testCase) throws Exception {
        File testCaseFolderFile = new File(getScriptPackageAbsolutePathForTestCase(testCase));
        if (testCaseFolderFile.exists() && testCaseFolderFile.isDirectory()) {
            for (File file : testCaseFolderFile.listFiles()) {
                if (file.isFile() && file.getName().endsWith(GroovyConstants.GROOVY_FILE_EXTENSION)) {
                    return file.getName().substring(0,
                            file.getName().length() - GroovyConstants.GROOVY_FILE_EXTENSION.length());
                }
            }
        }
        return null;
    }

    public static ICompilationUnit getGroovyScriptForTestCase(TestCaseEntity testCase) throws Exception {
        getTestCaseScriptFolder(testCase);
        IProject groovyProject = getGroovyProject(testCase.getProject());

        String parentRelativeFolder = getScriptPackageRelativePathForTestCase(testCase);
        IFolder parentFolder = groovyProject.getFolder(parentRelativeFolder);
        if (!parentFolder.exists()) {
            parentFolder.getParent().refreshLocal(IResource.DEPTH_ONE, null);
        }
        parentFolder.refreshLocal(IResource.DEPTH_ONE, null);
        String scriptFileName = getScriptNameForTestCase(testCase);
        IFile scriptFile = null;

        if (scriptFileName == null) {
            scriptFileName = getGroovyClassName(testCase);
            scriptFile = parentFolder.getFile(scriptFileName + GroovyConstants.GROOVY_FILE_EXTENSION);
            scriptFile.getLocation().toFile().createNewFile();

            GroovyCompilationUnit newCompilationunit = (GroovyCompilationUnit) GroovyCompilationHelper
                    .createGroovyType(getDefaultPackageForTestCase(testCase.getProject()), scriptFileName);
            StringBuilder importBuilder = new StringBuilder();
            GroovyParser parser = new GroovyParser(importBuilder);
            parser.parseGroovyAstIntoScript(Arrays.asList(newCompilationunit.getModuleNode().getClasses().get(0)));
            FileUtils.writeStringToFile(scriptFile.getLocation().toFile(), importBuilder.toString());
            scriptFile.refreshLocal(IResource.DEPTH_ZERO, null);
            newCompilationunit.getResource().delete(true, null);
        } else {
            scriptFile = parentFolder.getFile(scriptFileName + GroovyConstants.GROOVY_FILE_EXTENSION);
        }

        return JavaCore.createCompilationUnitFrom(scriptFile);
    }

    public static void updateTestCasePasted(TestCaseEntity updatedTestCase) throws Exception {
        ICompilationUnit compilationUnit = getGroovyScriptForTestCase(updatedTestCase);
        if (compilationUnit != null && compilationUnit.getResource() instanceof IFile
                && updatedTestCase.getScriptContents() != null) {
            IFile scriptFile = (IFile) compilationUnit.getResource();
            scriptFile.setContents(new ByteArrayInputStream(updatedTestCase.getScriptContents()), true, false, null);
        }
    }

    public static void updateTestCaseFolderDeleted(FolderEntity folder, FolderEntity testCaseRootProject)
            throws Exception {
        IFolder scriptFolder = getGroovyProject(folder.getProject()).getFolder(getRelativePathForFolder(folder));
        if (scriptFolder.exists()) {
            scriptFolder.delete(true, null);
            getGroovyProject(folder.getProject()).refreshLocal(IResource.DEPTH_INFINITE, null);
        }
    }

    public static void updateTestCaseDeleted(TestCaseEntity testCase, FolderEntity testCaseRootProject)
            throws Exception {
        try {
            IFile scriptFile = ResourcesPlugin.getWorkspace().getRoot()
                    .getFile(GroovyUtil.getGroovyScriptForTestCase(testCase).getPath());
            if (scriptFile != null && scriptFile.getParent() instanceof IFolder) {
                IFolder folder = (IFolder) scriptFile.getParent();
                folder.delete(true, null);
                folder.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
            }
        } catch (JavaModelException ex) {
            return;
        }

    }

    public static RenameSupport getRenameSupportForRenamingGroovyClass(IPackageFragment parentPackage,
            IFile compilationFile) throws Exception {
        return RenameSupport.create(parentPackage.getCompilationUnit(compilationFile.getName()), null,
                RenameSupport.UPDATE_REFERENCES);
    }

    public static URLClassLoader getProjectClasLoader(ProjectEntity projectEntity) throws Exception {
        IJavaProject project = JavaCore.create(getGroovyProject(projectEntity));
        String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(project);
        List<URL> urlList = new ArrayList<URL>();
        for (int i = 0; i < classPathEntries.length; i++) {
            String entry = classPathEntries[i];
            IPath path = new Path(entry);
            URL url = path.toFile().toURI().toURL();
            urlList.add(url);
        }
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(project.getClass().getClassLoader());
        for (URL url : urlList) {
            groovyClassLoader.addURL(url);
        }
        return groovyClassLoader;
    }

    public static void loadScriptContentIntoTestCase(TestCaseEntity testCase) throws Exception {
        IFile scriptFile = ResourcesPlugin.getWorkspace().getRoot()
                .getFile(getGroovyScriptForTestCase(testCase).getPath());
        if (scriptFile != null && scriptFile.exists()) {
            InputStream scriptInputStream = scriptFile.getContents();
            try {
                testCase.setScriptContents(IOUtils.toByteArray(scriptInputStream));
            } finally {
                if (scriptInputStream != null) {
                    scriptInputStream.close();
                }
            }
        }
    }

    public static IFile getTempScriptIFile(File scriptFile, ProjectEntity project) throws Exception {
        IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
        for (IResource resource : libFolder.members()) {
            if (resource instanceof IFile
                    && resource.getRawLocation().toFile().getAbsolutePath().equals(scriptFile.getAbsolutePath())) {
                return (IFile) resource;
            }
        }
        return null;
    }

    public static List<IFile> getAllLibFiles(ProjectEntity project) throws CoreException {
        List<IFile> files = new ArrayList<IFile>();
        IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
        for (IResource resource : libFolder.members()) {
            if (resource instanceof IFile
                    && FilenameUtils.getExtension(resource.getLocation().toString()).equals("groovy")) {
                files.add((IFile) resource);
            }
        }
        return files;
    }

    public static void refreshScriptTestCaseClasspath(ProjectEntity projectEntity, FolderEntity testCaseFolder)
            throws Exception {
        IProject groovyProject = getGroovyProject(projectEntity);
        IFolder scriptFolderOfTestCaseFolder = groovyProject
                .getFolder(getScriptPackageRelativePathForFolder(testCaseFolder));
        scriptFolderOfTestCaseFolder.refreshLocal(IResource.DEPTH_ONE, null);
    }

    public static void refreshInfiniteScriptTestCaseClasspath(ProjectEntity projectEntity, FolderEntity testCaseFolder,
            IProgressMonitor monitor) throws Exception {
        IProject groovyProject = getGroovyProject(projectEntity);
        IFolder scriptFolderOfTestCaseFolder = groovyProject
                .getFolder(getScriptPackageRelativePathForFolder(testCaseFolder));
        scriptFolderOfTestCaseFolder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
    }

    public static IFolder getTestCaseRootOutputFolder(IProject groovyProject) {
        IFolder outputParentFolder = groovyProject.getFolder(OUTPUT_FOLDER_NAME);
        return outputParentFolder.getFolder(TEST_CASE_OUTPUT_FOLDER_NAME);
    }

    /**
     * Create test case source script folder if it does not exist. Clean all variant that equalsIgnoreCase with name of
     * the the given test case.
     * 
     * @param testCaseSourceFolder
     * @param parentSourceFolder
     * @param testCase
     * @throws CoreException
     */
    public static void createTestCaseFolder(IFolder testCaseSourceFolder, IFolder parentSourceFolder,
            TestCaseEntity testCase) throws CoreException {
        if (!testCaseSourceFolder.exists()) {
            parentSourceFolder.refreshLocal(IResource.DEPTH_ONE, null);
            int index = 0;
            while (index < parentSourceFolder.members().length) {
                IResource resource = parentSourceFolder.members()[index];
                if (resource.getName().equalsIgnoreCase(testCase.getName())) {
                    resource.delete(true, null);
                    parentSourceFolder.refreshLocal(IResource.DEPTH_ONE, null);
                } else {
                    index++;
                }
            }
            testCaseSourceFolder.create(true, true, null);
        }
    }

    public static List<IFile> getAllScriptFiles(IFolder parentFolder) throws CoreException {
        List<IFile> listTestCaseFiles = new ArrayList<IFile>();
        for (IResource childResource : parentFolder.members()) {
            if (childResource instanceof IFile) {
                if ("groovy".equals(childResource.getFileExtension())) {
                    listTestCaseFiles.add((IFile) childResource);
                }
            } else if (childResource instanceof IFolder) {
                listTestCaseFiles.addAll(getAllScriptFiles((IFolder) childResource));
            }
        }
        return listTestCaseFiles;
    }
}
