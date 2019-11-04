package com.kms.katalon.groovy.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.eclipse.core.model.GroovyRuntime;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.internal.resources.ModelObjectWriter;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ClasspathAttribute;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.keyword.internal.IKeywordContributor;
import com.kms.katalon.core.keyword.internal.KeywordContributorCollection;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.selenium.TempClass;

import groovy.lang.GroovyClassLoader;

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
    
    private static final String TEST_LISTENERS_ROOT_FOLDER_NAME = "Test Listeners";

    private static final String GLOBAL_VARIABLE_ROOT_FOLDER_NAME = "Profiles";

    private static final String GROOVY_NATURE = "org.eclipse.jdt.groovy.core.groovyNature";

    private static final String KEYWORD_LIB_FOLDER_NAME = "Libs";

    private static final String KEYWORD_LIB_OUTPUT_FOLDER_NAME = "lib";

    private static final String DRIVERS_FOLDER_NAME = "Drivers";

    public static final String PLUGINS_FOLDER_NAME = "Plugins";

    private static final String JDT_LAUNCHING = "org.eclipse.jdt.launching.JRE_CONTAINER";

    private static final String[] KAT_PROJECT_NATURES = new String[] { GROOVY_NATURE, JavaCore.NATURE_ID };

    private static final String RESOURCE_REGEX_FILTER = "org.eclipse.core.resources.regexFilterMatcher";

    private static final String RESOURCE_FILE_NAME_REGEX = "(.*\\.svn-base$)|(.*\\.png$)|(.*\\.log$)|(.*\\.xlsx$)|(.*\\.xls$)|(.*\\.csv$)|(.*\\.txt$)";

    private static final String RESOURCE_FOLDER_NAME_REGEX = ".*\\.svn$";

    private static final String API_SOURCE_EXTENSION = "-sources.jar";

    public static IProject getGroovyProject(ProjectEntity projectEntity) {
        return ResourcesPlugin.getWorkspace()
                .getRoot()
                .getProject(getProjectNameIdFromLocation(projectEntity.getLocation()));
    }

    private static String getProjectNameIdFromLocation(String location) {
        return location.replace(File.separator, "%").replace(":", "%");
    }

    public static IPackageFragment getPackageFragmentFromLocation(String pkgRelativeLocationToProject,
            boolean isDefaultPackage, ProjectEntity projectEntity) throws CoreException {
        IFolder packageFolder = getGroovyProject(projectEntity).getFolder(pkgRelativeLocationToProject);
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

    public static void initGroovyProject(ProjectEntity projectEntity, List<File> customKeywordPluginFiles,
            boolean isEnterpriseAccount, IProgressMonitor monitor) throws CoreException, IOException, BundleException {
        SubProgressMonitor subProgressDescription = null;
        SubProgressMonitor subProgressClasspath = null;
        if (monitor != null) {
            monitor.beginTask("Initializing project's classpath...", 10);
            subProgressDescription = new SubProgressMonitor(monitor, 1,
                    SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
            subProgressClasspath = new SubProgressMonitor(monitor, 9, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
        }
        initGroovyProjectDescription(projectEntity, subProgressDescription);
        initGroovyProjectClassPath(projectEntity, customKeywordPluginFiles, true, isEnterpriseAccount, subProgressClasspath);
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

    public static void emptyProjectClasspath(ProjectEntity projectEntity) throws CoreException {
        IProject groovyProject = getGroovyProject(projectEntity);

        IFolder outputParentFolder = groovyProject.getFolder(OUTPUT_FOLDER_NAME);
        if (!outputParentFolder.exists()) {
            outputParentFolder.create(true, true, new NullProgressMonitor());
        }

        IJavaProject javaProject = JavaCore.create(getGroovyProject(projectEntity));
        javaProject.setOutputLocation(outputParentFolder.getFullPath(), new NullProgressMonitor());
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
        entries.add(JavaCore.newContainerEntry(new Path(JDT_LAUNCHING)));
        javaProject.setRawClasspath(new IClasspathEntry[0], new NullProgressMonitor());
        GroovyRuntime.addGroovyClasspathContainer(javaProject);
    }

    public static void initGroovyProjectClassPath(ProjectEntity projectEntity, List<File> pluginFiles, boolean isNew,
            boolean isEnterpriseAccount, IProgressMonitor monitor) throws CoreException, IOException, BundleException {
        IProject groovyProject = getGroovyProject(projectEntity);
        groovyProject.clearHistory(new NullProgressMonitor());
        groovyProject.refreshLocal(IResource.DEPTH_ONE, monitor);

        IFolder listenerSourceFolder = groovyProject.getFolder("Test Listeners");
        if (!listenerSourceFolder.exists()) {
            listenerSourceFolder.create(true, true, null);
        }

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
                // cleanDirectory(keywordLibFolder.getRawLocation().toFile());
            }
        }

        IFolder sourceMainGroovy = groovyProject.getFolder("Include/scripts/groovy");
        if (!sourceMainGroovy.exists()) {
            File sourceMainGroovyFolder = new File(sourceMainGroovy.getRawLocationURI());
            sourceMainGroovyFolder.mkdirs();
            sourceMainGroovy.refreshLocal(IResource.DEPTH_INFINITE, null);
        }

        IFolder driversFolder = groovyProject.getFolder(DRIVERS_FOLDER_NAME);
        if (!driversFolder.exists()) {
            driversFolder.create(true, true, null);
        }

        IFolder pluginsFolder = groovyProject.getFolder(PLUGINS_FOLDER_NAME);
        if (!pluginsFolder.exists()) {
            pluginsFolder.create(true, true, null);
        }

        IFolder outputParentFolder = groovyProject.getFolder(OUTPUT_FOLDER_NAME);
        if (!outputParentFolder.exists()) {
            outputParentFolder.create(true, true, null);
        }

        IFolder outputListenerFolder = outputParentFolder.getFolder("listener");
        if (!outputListenerFolder.exists()) {
            outputListenerFolder.create(true, true, null);
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

        IFolder outputSourceMainGroovy = outputParentFolder.getFolder("groovy");
        File outputSourceMainGroovyFolder = new File(outputSourceMainGroovy.getRawLocationURI());
        outputSourceMainGroovyFolder.mkdirs();
        outputSourceMainGroovy.refreshLocal(IResource.DEPTH_INFINITE, null);

        IJavaProject javaProject = JavaCore.create(groovyProject);
        javaProject.setOutputLocation(outputParentFolder.getFullPath(), null);

        // groovy project classpath list
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

        // add JRE to classpath
        entries.add(JavaCore.newContainerEntry(new Path(JDT_LAUNCHING)));

        // add source and output folder to classpath
        IPackageFragmentRoot keywordPackageRoot = javaProject.getPackageFragmentRoot(keywordSourceFolder);
        entries.add(JavaCore.newSourceEntry(keywordPackageRoot.getPath(), new Path[] {}, new Path[] {},
                outputKeywordFolder.getFullPath()));

        // add source and output folder to classpath
        IPackageFragmentRoot listenerPackageRoot = javaProject.getPackageFragmentRoot(listenerSourceFolder);
        entries.add(JavaCore.newSourceEntry(listenerPackageRoot.getPath(), new Path[] {}, new Path[] {},
                outputListenerFolder.getFullPath()));

        IPackageFragmentRoot keywordLibPackageRoot = javaProject.getPackageFragmentRoot(keywordLibFolder);
        entries.add(JavaCore.newSourceEntry(keywordLibPackageRoot.getPath(), new Path[] {}, new Path[] {},
                outputKWLibFolder.getFullPath()));

        IPackageFragmentRoot sourceMainGroovyPackageRoot = javaProject.getPackageFragmentRoot(sourceMainGroovy);
        entries.add(JavaCore.newSourceEntry(sourceMainGroovyPackageRoot.getPath(), new Path[] {}, new Path[] {},
                outputSourceMainGroovy.getFullPath()));

        // add groovy plugin to classpath
        Bundle bundle = Platform.getBundle(GROOVY_BUNDLE_ID);
        if (bundle != null) {
            String groovyLocation = bundle.getLocation()
                    .replace(BUNDLE_LOCATION_PREFIX, "")
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
                if (!desXercesBundleFile.exists()) {
                    FileUtils.copyFile(xercesBundleFile, desXercesBundleFile);
                }
                if (!desXmlApisBundleFile.exists()) {
                    FileUtils.copyFile(xmlApisBundleFile, desXmlApisBundleFile);
                }
            }
        }

        addClassPathOfCoreBundleToJavaProject(entries, isEnterpriseAccount);

        // Add class path for external jars
        File driversDir = driversFolder.getRawLocation().toFile();
        File pluginsDir = pluginsFolder.getRawLocation().toFile();

        List<File> allJarFiles = new ArrayList<>();
        allJarFiles.addAll(Arrays.asList(driversDir.listFiles()));
        allJarFiles.addAll(Arrays.asList(pluginsDir.listFiles()));
        allJarFiles.addAll(pluginFiles);

        for (File jarFile : allJarFiles) {
            IClasspathEntry oldEntry = null;
            if (jarFile.isFile() && jarFile.getName().endsWith(".jar")) {
                for (IClasspathEntry e : javaProject.getRawClasspath()) {
                    if (e.getEntryKind() == IClasspathEntry.CPE_LIBRARY
                            && e.getPath().toFile().getAbsolutePath().equals(jarFile.getAbsolutePath())) {
                        oldEntry = e;
                        break;
                    }
                }
                if (oldEntry != null) {
                    addJarFileToClasspath(jarFile, oldEntry, entries);
                } else {
                    addJarFileToClasspath(jarFile, entries);
                }
            }
        }

        javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), monitor);
        GroovyRuntime.addGroovyClasspathContainer(javaProject);
    }

    private static void addClassPathOfCoreBundleToJavaProject(List<IClasspathEntry> entries, boolean isEnterpriseAccount)
            throws IOException, BundleException {
        addClassPathOfCoreBundleToJavaProject(entries, Platform.getBundle(IdConstants.KATALON_CORE_BUNDLE_ID), isEnterpriseAccount);

        addClassPathOfCoreBundleToJavaProject(entries, FrameworkUtil.getBundle(TempClass.class), isEnterpriseAccount);
        addClassPathOfCoreBundleToJavaProject(entries, Platform.getBundle("com.kms.katalon.core.appium"), isEnterpriseAccount);
        addClassPathOfCoreBundleToJavaProject(entries, Platform.getBundle("com.kms.katalon.constant"), isEnterpriseAccount);
        addClassPathOfCoreBundleToJavaProject(entries, Platform.getBundle("com.kms.katalon.util"), isEnterpriseAccount);
        addClassPathOfCoreBundleToJavaProject(entries, Platform.getBundle("org.eclipse.equinox.common"), isEnterpriseAccount);
        addClassPathOfCoreBundleToJavaProject(entries, Platform.getBundle("com.kms.katalon.netlightbody"), isEnterpriseAccount);
        addClassPathOfCoreBundleToJavaProject(entries, Platform.getBundle("com.kms.katalon.poi"), isEnterpriseAccount);
        for (IKeywordContributor contributor : KeywordContributorCollection.getKeywordContributors()) {
            Bundle coreBundle = FrameworkUtil.getBundle(contributor.getClass());
            addClassPathOfCoreBundleToJavaProject(entries, coreBundle, isEnterpriseAccount);
        }
    }

    private static void addClassPathOfCoreBundleToJavaProject(List<IClasspathEntry> entries, Bundle coreBundle, boolean isEnterpriseAccount)
            throws IOException, BundleException {
        if (coreBundle == null)
            return;

        File customBundleFile = FileLocator.getBundleFile(coreBundle).getAbsoluteFile();

        if (customBundleFile == null || !customBundleFile.exists())
            return;

        if (customBundleFile.isDirectory()) { // built by IDE
            addSourceFolderToClassPath(customBundleFile, entries);
        } else {
            addJarFileToClasspath(customBundleFile, entries, coreBundle, isEnterpriseAccount);

            File libDir = getPlatformLibDir();

            if (libDir.isDirectory()) {
                for (File jarFile : libDir.listFiles()) {
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
                        addJarFileToClasspath(requiredBundleLocation, entries, requiredBundle, isEnterpriseAccount);
                    }
                }
            }
        }
    }

    private static File getConfigurationFolder() throws IOException {
        return new File(FileLocator.resolve(Platform.getConfigurationLocation().getURL()).getFile());
    }

    /**
     * @return Returns resources folder of the current Katalon installed folder.
     * @throws IOException
     */
    private static File getPlatformResourcesDir() throws IOException {
        return new File(getConfigurationFolder(), "resources");
    }

    /**
     * @return Returns API document folder of the current Katalon installed folder.
     * @throws IOException
     */
    private static File getPlatformAPIDocDir() throws IOException {
        return new File(getPlatformResourcesDir(), "apidocs");
    }

    /**
     * @return Returns libraries folder of the current Katalon installed folder.
     * @throws IOException
     */
    private static File getPlatformLibDir() throws IOException {
        return new File(getPlatformResourcesDir(), "lib");
    }
    
    /**
     * @return Returns source folder of the current Katalon installed folder.
     * @throws IOException
     */
    private static File getPlatformSourceDir() throws IOException {
        return new File(getPlatformResourcesDir(), "source");
    }

    /**
     * Adds the given <code>customBundleFile</code> if it isn't in the given <code>entries</code>. Also attaches source
     * folder for the given <code>customBundleFile</code>.
     * 
     * Note: For debug only.
     * 
     * @param customBundleFile
     * @param entries
     */
    protected static void addSourceFolderToClassPath(File customBundleFile, List<IClasspathEntry> entries) {
        entries.add(JavaCore.newLibraryEntry(new Path(new File(customBundleFile, "bin").getAbsolutePath()),
                new Path(new File(customBundleFile, "src").getAbsolutePath()), null));

        File resourceFolder = new File(customBundleFile, "resources" + File.separator + "lib");
        if (resourceFolder.exists() && resourceFolder.isDirectory()) {
            for (File jarFile : resourceFolder.listFiles()) {
                addJarFileToClasspath(jarFile, entries);
            }
        }
    }

    /**
     * Adds the given <code>jarFile</code> if it is valid and isn't in the given <code>entries</code>.
     * 
     * @param jarFile
     * @param entries
     */
    private static void addJarFileToClasspath(File jarFile, List<IClasspathEntry> entries) {
        if (checkRequiredBundleLocation(jarFile, entries)) {
            IClasspathEntry entry = JavaCore.newLibraryEntry(new Path(jarFile.getAbsolutePath()), null, null);
            if (entry != null && !entries.contains(entry)) {
                entries.add(entry);
            }
        }
    }

    private static void addJarFileToClasspath(File jarFile, IClasspathEntry oldEntry, List<IClasspathEntry> entries) {
        if (checkRequiredBundleLocation(jarFile, entries)) {
            IClasspathEntry entry = JavaCore.newLibraryEntry(new Path(jarFile.getAbsolutePath()),
                    oldEntry.getSourceAttachmentPath(), oldEntry.getSourceAttachmentRootPath(),
                    oldEntry.getAccessRules(), oldEntry.getExtraAttributes(), oldEntry.isExported());
            if (entry != null && !entries.contains(entry)) {
                entries.add(entry);
            }
        }
    }

    /**
     * Adds the given <code>jarFile</code> if it is valid and isn't in the given <code>entries</code>. Also adds javadoc
     * to the new {@link IClasspathEntry} if the given <code>jarFile</code> has groovydoc.
     * 
     * @param jarFile
     * @param entries
     * @param bundle
     * @throws IOException
     */
    private static void addJarFileToClasspath(File jarFile, List<IClasspathEntry> entries, Bundle bundle, boolean isEnterpriseAccount)
            throws IOException {
        if (checkRequiredBundleLocation(jarFile, entries)) {
            File javaDocDir = new File(getPlatformAPIDocDir(), bundle.getSymbolicName());
            String javadocLoc = javaDocDir.toURI().toString();
            IClasspathAttribute[] attributes = null;
            if (FileLocator.getBundleFile(bundle).isFile() && javaDocDir.isDirectory() && javaDocDir.exists()) {
                attributes = new IClasspathAttribute[] { new ClasspathAttribute("javadoc_location", javadocLoc) };
            }
            
            File javaSourceDir = new File(getPlatformSourceDir(), bundle.getSymbolicName());
            IPath sourcePath = null;
            if (FileLocator.getBundleFile(bundle).isFile() && 
                    javaSourceDir.isDirectory() && 
                    javaSourceDir.exists() && 
                    bundle.getSymbolicName().startsWith("com.kms.katalon.core")
                    && isEnterpriseAccount) {
                javaSourceDir = new File(javaSourceDir, bundle.getSymbolicName() + API_SOURCE_EXTENSION);
                sourcePath = new Path(javaSourceDir.getAbsolutePath());
            }
            IClasspathEntry entry = JavaCore.newLibraryEntry(new Path(jarFile.getAbsolutePath()), sourcePath, null, null,
                    attributes, false);
            if (entry != null && !entries.contains(entry)) {
                entries.add(entry);
            }
        }
    }

    private static boolean checkRequiredBundleLocation(File requiredBundleLocation, List<IClasspathEntry> entries) {
        String bundleName = FilenameUtils.getBaseName(requiredBundleLocation.getName());

        if (bundleName == null || bundleName.isEmpty())
            return false;

        if (bundleName.contains("_")) {
            bundleName = bundleName.split("_")[0];
            if (bundleName == null || bundleName.isEmpty())
                return false;
        }

        if ("org.eclipse.core.runtime".equalsIgnoreCase(bundleName))
            return false;
        if ("com.kms.katalon.custom".equalsIgnoreCase(bundleName))
            return false;

        for (IClasspathEntry childEntry : entries) {
            if ((childEntry.getPath() != null) && (childEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
                    && FilenameUtils.getBaseName(childEntry.getPath().toString()).equals(bundleName)) {
                return false;
            }
        }
        return true;
    }

    private static void initGroovyProjectDescription(ProjectEntity projectEntity, IProgressMonitor monitor)
            throws CoreException, IOException {
        IProject groovyProject = getGroovyProject(projectEntity);
        if (groovyProject == null) {
            return;
        }
        if (!groovyProject.exists()) {
            IProjectDescription projectDescription = ResourcesPlugin.getWorkspace()
                    .newProjectDescription(projectEntity.getName());
            projectDescription.setLocation(new Path(projectEntity.getFolderLocation()));

            groovyProject.create(projectDescription, null);

            createFilters(groovyProject);

            groovyProject.open(null);

        } else if (!groovyProject.isOpen()) {
            // If user has unintentionally deleted .project file
            File descriptionFile = new File(projectEntity.getFolderLocation(),
                    IProjectDescription.DESCRIPTION_FILE_NAME);
            if (!descriptionFile.exists()) {
                IProjectDescription projectDescription = ((Project) groovyProject).internalGetDescription();
                projectDescription.setLocation(new Path(projectEntity.getFolderLocation()));
                projectDescription.setNatureIds(KAT_PROJECT_NATURES);
                if (((ProjectDescription) projectDescription).getFilters() == null) {
                    createFilters(groovyProject);
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                new ModelObjectWriter().write(projectDescription, out, System.lineSeparator());
                FileUtils.writeByteArrayToFile(descriptionFile, out.toByteArray(), false);
            }
            groovyProject.open(null);
        }

        IProjectDescription projectDescription = groovyProject.getDescription();
        projectDescription.setNatureIds(new String[] { GROOVY_NATURE, JavaCore.NATURE_ID });
        org.eclipse.core.resources.ICommand[] commands = new ICommand[] { projectDescription.newCommand() };
        commands[0].setBuilderName(org.eclipse.jdt.core.JavaCore.BUILDER_ID);
        projectDescription.setBuildSpec(commands);
        groovyProject.setDescription(projectDescription, monitor);
        groovyProject.refreshLocal(IResource.DEPTH_ZERO, monitor);
    }

    public static void updateGroovyProject(ProjectEntity projectEntity, IProject oldGroovyProject)
            throws CoreException {
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

    public static List<IPackageFragment> getAllPackageInFolder(ProjectEntity projectEntity, String folderPath)
            throws JavaModelException {
        IProject groovyProject = getGroovyProject(projectEntity);
        List<IPackageFragment> packageFragments = new ArrayList<IPackageFragment>();
        IPackageFragmentRoot root = JavaCore.create(groovyProject)
                .getPackageFragmentRoot(groovyProject.getFolder(folderPath));
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

    public static IPackageFragment getDefaultPackageForKeyword(ProjectEntity projectEntity) {
        IProject groovyProject = getGroovyProject(projectEntity);
        return JavaCore.create(groovyProject)
                .getPackageFragmentRoot(groovyProject.getFolder(KEYWORD_SOURCE_FOLDER_NAME))
                .getPackageFragment("");
    }

    public static IPackageFragment getDefaultPackageForTestCase(ProjectEntity projectEntity) {
        IProject groovyProject = getGroovyProject(projectEntity);
        return JavaCore.create(groovyProject)
                .getPackageFragmentRoot(groovyProject.getFolder(TEST_SCRIPT_SOURCE_FOLDER_NAME))
                .getPackageFragment("");
    }

    public static List<ICompilationUnit> getAllGroovyClasses(IPackageFragment packageFragment) throws CoreException {
        if (packageFragment == null) {
            return Collections.emptyList();
        }
        packageFragment.getResource().refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
        List<ICompilationUnit> groovyClassFiles = new ArrayList<>();
        for (IJavaElement javaElement : packageFragment.getChildren()) {
            if (javaElement instanceof GroovyCompilationUnit) {
                groovyClassFiles.add((GroovyCompilationUnit) javaElement);
            }
        }
        return groovyClassFiles;
    }

    public static void openGroovyProject(ProjectEntity projectEntity, List<File> customKeywordPluginFiles, boolean isEnterpriseAccount)
            throws CoreException, IOException, BundleException {
        initGroovyProject(projectEntity, customKeywordPluginFiles, isEnterpriseAccount, null);
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
    
    public static IFolder getTestListenerSourceFolder(ProjectEntity project) {
        IProject groovyProject = getGroovyProject(project);
        return groovyProject.getFolder(TEST_LISTENERS_ROOT_FOLDER_NAME);
    }

    public static IFolder getTestCaseSourceFolder(ProjectEntity project) {
        IProject groovyProject = getGroovyProject(project);
        return groovyProject.getFolder(TEST_CASE_ROOT_FOLDER_NAME);
    }

    public static IFolder getGlobalVariableSourceFolder(ProjectEntity project) {
        IProject groovyProject = getGroovyProject(project);
        return groovyProject.getFolder(GLOBAL_VARIABLE_ROOT_FOLDER_NAME);
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
            throws JavaModelException {
        IProject groovyProject = getGroovyProject(targetFolder.getProject());
        IPackageFragmentRoot packageFragmentRoot = JavaCore.create(groovyProject)
                .getPackageFragmentRoot(groovyProject.getFolder(KEYWORD_SOURCE_FOLDER_NAME));
        packageFragment.copy(packageFragmentRoot, null, newName, false, null);
    }

    public static void copyKeyword(IFile keywordFile, IPackageFragment targetPackageFragment, String newName)
            throws JavaModelException {
        GroovyCompilationUnit compilationUnit = (GroovyCompilationUnit) JavaCore.create(keywordFile);
        compilationUnit.copy(targetPackageFragment, null,
                newName != null ? newName + GroovyConstants.GROOVY_FILE_EXTENSION : newName, false, null);
    }

    public static void moveKeyword(IFile keywordFile, IPackageFragment targetPackageFragment, String newName)
            throws JavaModelException {
        GroovyCompilationUnit compilationUnit = (GroovyCompilationUnit) JavaCore.create(keywordFile);
        compilationUnit.move(targetPackageFragment, null,
                newName != null ? newName + GroovyConstants.GROOVY_FILE_EXTENSION : newName, false, null);
    }

    public static String getGroovyClassName(TestCaseEntity testCase) {
        return "Script" + System.currentTimeMillis();
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

    public static String getScriptNameForTestCase(TestCaseEntity testCase) {
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

    public static void updateTestCasePasted(TestCaseEntity updatedTestCase) throws IOException, CoreException {
        // Ensure new folder for script file
        getTestCaseScriptFolder(updatedTestCase);

        IProject groovyProject = getGroovyProject(updatedTestCase.getProject());
        IFolder testCaseScriptFolder = groovyProject
                .getFolder(getScriptPackageRelativePathForTestCase(updatedTestCase));
        String scriptFileName = getScriptNameForTestCase(updatedTestCase);
        if (scriptFileName == null) {
            scriptFileName = getGroovyClassName(updatedTestCase);
        }
        IFile scriptFile = testCaseScriptFolder.getFile(scriptFileName + GroovyConstants.GROOVY_FILE_EXTENSION);
        if (!scriptFile.exists()) {
            scriptFile.getLocation().toFile().createNewFile();
            scriptFile.refreshLocal(IResource.DEPTH_ZERO, null);
        }
        byte[] scriptContents = updatedTestCase.getScriptContents();
        if (scriptContents != null) {
            scriptFile.setContents(new ByteArrayInputStream(scriptContents), true, false, null);
        }
    }

    public static void updateTestCaseFolderDeleted(FolderEntity folder, FolderEntity testCaseRootProject)
            throws CoreException {
        IFolder scriptFolder = getGroovyProject(folder.getProject()).getFolder(getRelativePathForFolder(folder));
        if (scriptFolder.exists()) {
            scriptFolder.delete(true, null);
            getGroovyProject(folder.getProject()).refreshLocal(IResource.DEPTH_INFINITE, null);
        }
    }

    public static void updateTestCaseDeleted(TestCaseEntity testCase, FolderEntity testCaseRootProject)
            throws CoreException {
        try {
            IProject groovyProject = getGroovyProject(testCase.getProject());
            IFolder folder = groovyProject.getFolder(getScriptPackageRelativePathForTestCase(testCase));
            folder.delete(true, null);
            folder.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (JavaModelException ex) {
            return;
        }
    }

    public static URLClassLoader getProjectClasLoader(IJavaProject project, String[] classPathEntries)
            throws MalformedURLException {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(project.getClass().getClassLoader());
        for (int i = 0; i < classPathEntries.length; i++) {
            String entry = classPathEntries[i];
            IPath path = new Path(entry);
            URL url = path.toFile().toURI().toURL();
            groovyClassLoader.addURL(url);
        }
        return groovyClassLoader;
    }

    private static URLClassLoader getProjectClassLoader(IJavaProject project, ClassLoader parent,
            String[] classPathEntries) throws MalformedURLException {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(parent);
        for (int i = 0; i < classPathEntries.length; i++) {
            String entry = classPathEntries[i];
            IPath path = new Path(entry);
            URL url = path.toFile().toURI().toURL();
            groovyClassLoader.addURL(url);
        }
        return groovyClassLoader;
    }

    public static void loadScriptContentIntoTestCase(TestCaseEntity testCase) throws CoreException, IOException {
        if (testCase == null) {
            return;
        }

        ProjectEntity project = testCase.getProject();
        String scriptFolderPath = project.getFolderLocation() + File.separator
                + getScriptPackageRelativePathForTestCase(testCase);
        String scriptFileName = getScriptNameForTestCase(testCase);
        File scriptFile = new File(
                scriptFolderPath + File.separator + scriptFileName + GroovyConstants.GROOVY_FILE_EXTENSION);
        if (!scriptFile.exists()) {
            return;
        }
        try (StringReader stringReader = new StringReader(
                FileUtils.readFileToString(scriptFile, GlobalStringConstants.DF_CHARSET))) {
            testCase.setScriptContents(IOUtils.toByteArray(stringReader, GlobalStringConstants.DF_CHARSET));
        }
    }

    public static IFile getTempScriptIFile(File scriptFile, ProjectEntity project) throws CoreException {
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
            throws CoreException {
        IProject groovyProject = getGroovyProject(projectEntity);
        IFolder scriptFolderOfTestCaseFolder = groovyProject
                .getFolder(getScriptPackageRelativePathForFolder(testCaseFolder));
        scriptFolderOfTestCaseFolder.refreshLocal(IResource.DEPTH_ONE, null);
    }

    public static void refreshInfiniteScriptTestCaseClasspath(ProjectEntity projectEntity, FolderEntity testCaseFolder,
            IProgressMonitor monitor) throws CoreException {
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

    public static List<IFile> getAllFiles(IFolder parentFolder, String extension) throws CoreException {
        parentFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
        List<IFile> listFiles = new ArrayList<IFile>();
        for (IResource childResource : parentFolder.members()) {
            if (childResource instanceof IFile) {
                if (extension.equals(childResource.getFileExtension())) {
                    listFiles.add((IFile) childResource);
                }
            } else if (childResource instanceof IFolder) {
                listFiles.addAll(getAllFiles((IFolder) childResource, extension));
            }
        }
        return listFiles;
    }

    public static List<IFile> getAllScriptFiles(IFolder parentFolder) throws CoreException {
        return getAllFiles(parentFolder, "groovy");
    }

    public static List<IFile> getAllGlobalVariableFiles(ProjectEntity projectEntity) throws CoreException {
        IFolder globalVariableRootFolder = getGlobalVariableSourceFolder(projectEntity);
        return getAllFiles(globalVariableRootFolder, "glbl");
    }

    public static List<IFile> getAllTestCaseFiles(ProjectEntity projectEntity) throws CoreException {
        IFolder testCaseRootFolder = getTestCaseSourceFolder(projectEntity);
        return getAllFiles(testCaseRootFolder, "tc");
    }

    public static List<IFile> getAllScriptFiles(ProjectEntity projectEntity) throws CoreException {
        List<IFile> scriptFiles = new ArrayList<>();
        scriptFiles.addAll(getAllTestCaseScripts(projectEntity));
        scriptFiles.addAll(getAllCustomKeywordsScripts(projectEntity));
        scriptFiles.addAll(getAllTestListenerScripts(projectEntity));
        return scriptFiles;
    }

    public static List<IFile> getAllTestCaseScripts(ProjectEntity projectEntity) throws CoreException {
        IFolder testCaseRootFolder = GroovyUtil.getTestCaseScriptSourceFolder(projectEntity);
        return getAllScriptFiles(testCaseRootFolder);
    }

    public static List<IFile> getAllCustomKeywordsScripts(ProjectEntity projectEntity) throws CoreException {
        IFolder customKeywordRootFolder = GroovyUtil.getCustomKeywordSourceFolder(projectEntity);
        return getAllScriptFiles(customKeywordRootFolder);
    }

    public static List<IFile> getAllTestListenerScripts(ProjectEntity projectEntity) throws CoreException {
        IFolder testListenersRootFolder = GroovyUtil.getTestListenerSourceFolder(projectEntity);
        return getAllScriptFiles(testListenersRootFolder);
    }

    private static void createFilters(IProject groovyProject) throws CoreException {
        // Exclude user created files/SVN meta files when loading project
        groovyProject.createFilter(IResourceFilterDescription.EXCLUDE_ALL | IResourceFilterDescription.FOLDERS,
                new FileInfoMatcherDescription(RESOURCE_REGEX_FILTER, RESOURCE_FOLDER_NAME_REGEX), IResource.NONE,
                new NullProgressMonitor());
        groovyProject.createFilter(IResourceFilterDescription.EXCLUDE_ALL | IResourceFilterDescription.FILES,
                new FileInfoMatcherDescription(RESOURCE_REGEX_FILTER, RESOURCE_FILE_NAME_REGEX), IResource.NONE,
                new NullProgressMonitor());
    }

    public static URLClassLoader getProjectClasLoader(ProjectEntity projectEntity)
            throws MalformedURLException, CoreException {
        IJavaProject project = JavaCore.create(GroovyUtil.getGroovyProject(projectEntity));
        return GroovyUtil.getProjectClasLoader(project, JavaRuntime.computeDefaultRuntimeClassPath(project));
    }
    
    public static URLClassLoader getProjectClassLoader(ProjectEntity projectEntity, ClassLoader parent)
            throws MalformedURLException, CoreException {
        IJavaProject project = JavaCore.create(GroovyUtil.getGroovyProject(projectEntity));
        return GroovyUtil.getProjectClassLoader(project, parent, JavaRuntime.computeDefaultRuntimeClassPath(project));
    }

    public static URLClassLoader getClassLoaderFromParent(ProjectEntity projectEntity, ClassLoader parent)
            throws MalformedURLException, CoreException {
        IJavaProject project = JavaCore.create(GroovyUtil.getGroovyProject(projectEntity));
        return GroovyUtil.getProjectClassLoader(project, parent,
                JavaRuntime.computeDefaultRuntimeClassPath(project));
    }

    public static IFolder getPluginsFolder(ProjectEntity project) {
        IProject groovyProject = getGroovyProject(project);
        return groovyProject.getFolder(PLUGINS_FOLDER_NAME);
    }
}
