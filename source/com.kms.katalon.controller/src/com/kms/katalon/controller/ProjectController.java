package com.kms.katalon.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.custom.factory.CustomKeywordPluginFactory;
import com.kms.katalon.custom.factory.CustomMethodNodeFactory;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.logging.LogUtil;

@Creatable
public class ProjectController extends EntityController {
    private static final short DEFAULT_PAGELOAD_TIMEOUT = 30;

    private static EntityController _instance;

    private static final String RECENT_PROJECT_FILE_LOCATION = Platform.getLocation().toString() + File.separator
            + "recent_projects";

    public static final int NUMBER_OF_RECENT_PROJECTS = 6;

    private static Map<String, URLClassLoader> classLoaderLookup = new HashMap<>();

    private ProjectController() {
        super();
    }

    public static ProjectController getInstance() {
        if (_instance == null) {
            _instance = new ProjectController();
        }
        return (ProjectController) _instance;
    }

    public ProjectEntity addNewProject(String name, String description, String projectLocation) throws Exception {
        ProjectEntity newProject = getDataProviderSetting().getProjectDataProvider().addNewProject(name, description,
                DEFAULT_PAGELOAD_TIMEOUT, projectLocation);

        return newProject;
    }

    public ProjectEntity openProjectForUI(String projectPk, boolean isEnterpriseAccount, IProgressMonitor monitor) throws Exception {
        try {
            if (monitor == null) {
                monitor = new NullProgressMonitor();
            }

            ProjectEntity project = getDataProviderSetting().getProjectDataProvider()
                    .openProjectWithoutClasspath(projectPk);

            if (project != null) {
                monitor.beginTask("Initialzing project's working space...", 10);
                cleanWorkspace();

                if (project.getUUID() == null) {
                    project.setUUID(Util.generateGuid());
                    updateProject(project);
                }
                SubMonitor progress = SubMonitor.convert(monitor, 100);
                DataProviderState.getInstance().setCurrentProject(project);

                // KeywordController.getInstance().loadCustomKeywordInPluginDirectory(project);

                try {
                    addJdtSettings(project);
                    GroovyUtil.initGroovyProject(project,
                            ProjectController.getInstance().getCustomKeywordPlugins(project),
                            isEnterpriseAccount,
                            progress.newChild(40, SubMonitor.SUPPRESS_SUBTASK));
                    updateProjectClassLoader(project);
                } catch (JavaModelException e) {
                    monitor.beginTask("Trying cleaning up Groovy project...", 10);
                    cleanupGroovyProject(project);
                    GroovyUtil.initGroovyProject(project,
                            ProjectController.getInstance().getCustomKeywordPlugins(project),
                            isEnterpriseAccount,
                            progress.newChild(40, SubMonitor.SUPPRESS_SUBTASK));
                }
                GlobalVariableController.getInstance().generateGlobalVariableLibFile(project,
                        progress.newChild(20, SubMonitor.SUPPRESS_SUBTASK));
                KeywordController.getInstance().parseAllCustomKeywords(project,
                        progress.newChild(40, SubMonitor.SUPPRESS_SUBTASK));
            }
            return project;
        } finally {
            if (monitor != null) {
                monitor.done();
            }
        }
    }
    
    public void closeAndCleanupProject(ProjectEntity project) throws Exception {
        try {
            closeProject(project.getId(), new NullProgressMonitor());
            cleanupGroovyProject(project);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

    private void cleanupGroovyProject(ProjectEntity project) {
        File classpathFile = new File(project.getFolderLocation(), ".classpath");
        if (classpathFile.exists()) {
            FileUtils.deleteQuietly(classpathFile);
        }

        File binFolder = new File(project.getFolderLocation(), "bin");
        if (binFolder.exists()) {
            FileUtils.deleteQuietly(binFolder);
        }

        File projectFile = new File(project.getFolderLocation(), ".project");
        if (projectFile.exists()) {
            FileUtils.deleteQuietly(projectFile);
        }
        
        File libsFolder = new File(project.getFolderLocation(), "Libs");
        if (libsFolder.exists()) {
            FileUtils.deleteQuietly(libsFolder);
        }

        File eclipseSettingsFolder = new File(project.getFolderLocation(), ".settings");
        if (eclipseSettingsFolder.exists()) {
            FileUtils.deleteQuietly(eclipseSettingsFolder);
        }
    }

    public void cleanProjectUISettings(ProjectEntity projectEntity) throws CoreException {
        CustomMethodNodeFactory.getInstance().reset();
        GroovyUtil.emptyProjectClasspath(projectEntity);
    }

    public ProjectEntity openProject(String projectPk, boolean isEnterpriseAccount) throws Exception {
        LogUtil.printOutputLine("Cleaning up workspace");
        cleanWorkspace();
        LogUtil.printOutputLine("Opening project file: " + projectPk);
        File projectFile = new File(projectPk);
        String projectFolderLocation = projectFile.getParent();
        String userDirLocation = System.getProperty("user.dir");
        if (userDirLocation.equals(projectFolderLocation)) {
            LogUtil.printErrorLine("Warning! Please run Katalon execution command outside of the project folder.");
        }
        ProjectEntity project = getDataProviderSetting().getProjectDataProvider()
                .openProjectWithoutClasspath(projectPk);
        if (project != null) {
            DataProviderState.getInstance().setCurrentProject(project);
            
            addJdtSettings(project);

            // LogUtil.printOutputLine("Parsing custom keywords in Plugins folder...");
            // KeywordController.getInstance().loadCustomKeywordInPluginDirectory(project);
            GroovyUtil.initGroovyProject(project, ProjectController.getInstance().getCustomKeywordPlugins(project),
                    isEnterpriseAccount, null);

            LogUtil.printOutputLine("Generating global variables...");
            GlobalVariableController.getInstance().generateGlobalVariableLibFile(project, null);

            LogUtil.printOutputLine("Parsing custom keywords...");
            KeywordController.getInstance().parseAllCustomKeywords(project, null);
            LogUtil.printOutputLine(MessageFormat.format("Project ''{0}'' opened", project.getName()));
        }
        return project;
    }

    public static void cleanWorkspace() {
        File externalFolder = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(),
                ".metadata/.plugins/org.eclipse.core.resources/.projects/.org.eclipse.jdt.core.external.folders");

        if (!externalFolder.exists()) {
            externalFolder.mkdirs();
        }
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            try {
                // Remote all existing projects out of workspace
                project.delete(false, true, null);
            } catch (Exception ignored) {}
        }
    }
    
    private static void addJdtSettings(ProjectEntity project) {
        File jdtSettingFile = new File(project.getFolderLocation(), ".settings/org.eclipse.jdt.core.prefs");
        if (jdtSettingFile.exists()) {
            return;
        }
        String fileContent = getFileContent("resources/template/project_jdt_setting.tpl");
        try {
            FileUtils.write(jdtSettingFile, fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LogUtil.logError(e);
        }
    }

    private static String getFileContent(String filePath) {
        URL url = FileLocator.find(FrameworkUtil.getBundle(ProjectController.class), new Path(filePath), null);
        try {
            return StringUtils.join(
                    IOUtils.readLines(new BufferedInputStream(url.openStream()), GlobalStringConstants.DF_CHARSET),
                    "\n");
        } catch (IOException e) {
            LogUtil.logError(e);
            return StringUtils.EMPTY;
        }
    }

    public void closeProject(String projectPk, IProgressMonitor monitor) throws Exception {
        ProjectEntity project = getDataProviderSetting().getProjectDataProvider()
                .openProjectWithoutClasspath(projectPk);
        if (project != null) {
            IProject groovyProject = GroovyUtil.getGroovyProject(project);
            try {
                groovyProject.clearHistory(monitor);
                groovyProject.close(monitor);
            } catch (CoreException e) {
                LogUtil.logError(e);
            }

            if (classLoaderLookup.containsKey(project.getLocation())) {
                classLoaderLookup.remove(project.getLocation());
            }
        }
        DataProviderState.getInstance().setCurrentProject(null);
    }

    public ProjectEntity updateProject(String name, String description, String projectPk) throws Exception {
        return getDataProviderSetting().getProjectDataProvider().updateProject(name, description, projectPk, (short) 0);
    }

    public void updateProject(ProjectEntity projectEntity) throws Exception {
        getDataProviderSetting().getProjectDataProvider().updateProject(projectEntity);
    }

    public List<ProjectEntity> validateRecentProjectLocations(List<String> recentProjectLocations) {
        if (recentProjectLocations != null) {
            List<ProjectEntity> recentProjects = new ArrayList<ProjectEntity>();
            for (String projectLocation : recentProjectLocations) {
                try {
                    ProjectEntity project = getDataProviderSetting().getProjectDataProvider()
                            .getProject(projectLocation);

                    if (project != null) {
                        recentProjects.add(project);
                    }
                } catch (DALException e) {
                    LogUtil.logError(e);
                }
            }
            return recentProjects;
        }
        return new ArrayList<ProjectEntity>();
    }

    @Deprecated
    private List<ProjectEntity> validateRecentProjects(List<ProjectEntity> recentProjects) {
        if (recentProjects != null) {
            List<ProjectEntity> resultList = new ArrayList<ProjectEntity>();
            for (ProjectEntity recentProject : recentProjects) {
                if (recentProject != null) {
                    File projectFolder = new File(recentProject.getFolderLocation());
                    File projectFile = new File(recentProject.getLocation());
                    if (projectFolder.exists() && projectFolder.isDirectory() && projectFile.exists()
                            && projectFile.isFile()) {
                        try {
                            ProjectEntity project = getDataProviderSetting().getProjectDataProvider()
                                    .getProject(projectFile.getAbsolutePath());

                            if (project.getName().equals(recentProject.getName())) {
                                resultList.add(recentProject);
                            }
                        } catch (DALException e) {
                            LogUtil.logError(e);
                        }
                    }
                }
            }
            return resultList;
        }
        return new ArrayList<ProjectEntity>();
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public List<ProjectEntity> getRecentProjects() {
        File recentProjectFile = new File(RECENT_PROJECT_FILE_LOCATION);
        if (recentProjectFile.isFile() && recentProjectFile.exists()) {
            ObjectInputStream inputStream = null;
            List<ProjectEntity> projects = null;
            try {
                inputStream = new ObjectInputStream(new FileInputStream(RECENT_PROJECT_FILE_LOCATION));
                projects = (List<ProjectEntity>) inputStream.readObject();
            } catch (Throwable e) {
                return new ArrayList<>();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ignored) {}
                }
            }
            return validateRecentProjects(projects);
        }
        return new ArrayList<>();
    }

    public ProjectEntity getCurrentProject() {
        return DataProviderState.getInstance().getCurrentProject();
    }

    public boolean validateNewProjectName(String projectParentFolderName, String projectName) throws Exception {
        return !getDataProviderSetting().getProjectDataProvider().isDuplicationProjectName(projectName,
                projectParentFolderName);
    }

    public String getTempDir() {
        return getDataProviderSetting().getProjectDataProvider().getSystemTempFolder();
    }

    public String getWebServiceTempDir() {
        return getTempDir() + File.separator + "Web Services";
    }

    public String getNonremovableTempDir() {
        return new File(getDataProviderSetting().getProjectDataProvider().getSystemTempFolder(), "non-removable")
                .getAbsolutePath();
    }

    public String getInternalSettingDir() {
        return getDataProviderSetting().getProjectDataProvider().getInternalSettingFolder();
    }

    public File getProjectFile(String folderLocation) {
        return getDataProviderSetting().getProjectDataProvider().getProjectFile(folderLocation);
    }

    public ProjectEntity newProjectEntity(String name, String description, String location, boolean legacy)
            throws DALException {
        return getDataProviderSetting().getProjectDataProvider().newProjectEntity(name, description, location, legacy);
    }

    public ProjectEntity updateProjectInfo(File projectFile, ProjectEntity newInfo) throws DALException {
        return getDataProviderSetting().getProjectDataProvider().updateProjectEntity(projectFile, newInfo);
    }

    public ProjectEntity getProject(String projectFileLocation) throws ControllerException {
        try {
            return getDataProviderSetting().getProjectDataProvider().getProject(projectFileLocation);
        } catch (DALException e) {
            throw new ControllerException(e);
        }
    }

    public List<File> getCustomKeywordPlugins(ProjectEntity project) throws ControllerException {
        return CustomKeywordPluginFactory.getInstance().getAllPluginFiles();
    }

    public URLClassLoader getProjectClassLoader(ProjectEntity project) throws MalformedURLException, CoreException {
        String projectLocation = project.getLocation();
        if (classLoaderLookup.containsKey(projectLocation)) {
            return classLoaderLookup.get(projectLocation);
        }
        URLClassLoader classLoader = GroovyUtil.getProjectClasLoader(project);
        classLoaderLookup.put(projectLocation, classLoader);
        return classLoader;
    }

    public void updateProjectClassLoader(ProjectEntity projectEntity) throws MalformedURLException, CoreException {
        String projectLocation = projectEntity.getLocation();
        URLClassLoader classLoader = GroovyUtil.getProjectClasLoader(projectEntity);
        classLoaderLookup.put(projectLocation, classLoader);
    }
}
