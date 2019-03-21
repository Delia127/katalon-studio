package com.kms.katalon.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.e4.core.di.annotations.Creatable;

import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.annotation.Keyword;
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

        addRecentProject(newProject);
        return newProject;
    }

    public ProjectEntity openProjectForUI(String projectPk, IProgressMonitor monitor) throws Exception {
        try {
            if (monitor == null)
                monitor = new NullProgressMonitor();

            ProjectEntity project = getDataProviderSetting().getProjectDataProvider()
                    .openProjectWithoutClasspath(projectPk);

            if (project != null) {
                monitor.beginTask("Initialzing project's working space...", 10);
                if (project.getUUID() == null) {
                    project.setUUID(Util.generateGuid());
                    updateProject(project);
                }
                SubMonitor progress = SubMonitor.convert(monitor, 100);
                DataProviderState.getInstance().setCurrentProject(project);

                KeywordController.getInstance().loadCustomKeywordInPluginDirectory(project);

                GroovyUtil.initGroovyProject(project, ProjectController.getInstance().getCustomKeywordPlugins(project),
                        progress.newChild(40, SubMonitor.SUPPRESS_SUBTASK));
                addRecentProject(project);
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

    public void cleanProjectUISettings(ProjectEntity projectEntity) throws CoreException {
        CustomMethodNodeFactory.getInstance().reset();
        GroovyUtil.emptyProjectClasspath(projectEntity);
    }

    public ProjectEntity openProject(String projectPk) throws Exception {
        LogUtil.printOutputLine("Opening project file: " + projectPk);
        File projectFile = new File(projectPk);
        String projectFolderLocation = projectFile.getParent();
        String userDirLocation = System.getProperty("user.dir");
        if (userDirLocation.equals(projectFolderLocation)) {
            LogUtil.printErrorLine("Warning! Please run katalon command execution outside of the project folder");
        }
        ProjectEntity project = getDataProviderSetting().getProjectDataProvider()
                .openProjectWithoutClasspath(projectPk);
        if (project != null) {
            DataProviderState.getInstance().setCurrentProject(project);
            
            LogUtil.printOutputLine("Parsing custom keywords in Plugins folder...");
            KeywordController.getInstance().loadCustomKeywordInPluginDirectory(project);

            GroovyUtil.initGroovyProject(project, ProjectController.getInstance().getCustomKeywordPlugins(project), null);
            addRecentProject(project);
            LogUtil.printOutputLine("Generating global variables...");
            GlobalVariableController.getInstance().generateGlobalVariableLibFile(project, null);

            LogUtil.printOutputLine("Parsing custom keywords...");
            KeywordController.getInstance().parseAllCustomKeywords(project, null);
            LogUtil.printOutputLine(MessageFormat.format("Project ''{0}'' opened", project.getName()));
        }
        return project;
    }

    public void closeProject(String projectPk, IProgressMonitor monitor) throws Exception {
        ProjectEntity project = getDataProviderSetting().getProjectDataProvider()
                .openProjectWithoutClasspath(projectPk);
        if (project != null) {
            IProject groovyProject = GroovyUtil.getGroovyProject(project);
            groovyProject.clearHistory(monitor);
            groovyProject.close(monitor);
        }
        DataProviderState.getInstance().setCurrentProject(null);
    }

    public void updateProject(String name, String description, String projectPk) throws Exception {
        ProjectEntity project = getDataProviderSetting().getProjectDataProvider().updateProject(name, description,
                projectPk, (short) 0);
        if (project != null) {
            addRecentProject(project);
            validateRecentProjects(getRecentProjects());
        }
    }

    public void updateProject(ProjectEntity projectEntity) throws Exception {
        getDataProviderSetting().getProjectDataProvider().updateProject(projectEntity);
    }

    public void addRecentProject(ProjectEntity project) throws Exception {
        List<ProjectEntity> recentProjects = getRecentProjects();
        int existedProjectIndex = -1;
        for (int i = 0; i < recentProjects.size(); i++) {
            if (getDataProviderSetting().getEntityPk(project)
                    .equals(getDataProviderSetting().getEntityPk(recentProjects.get(i)))) {
                existedProjectIndex = i;
                break;
            }
        }

        if (existedProjectIndex > -1 && existedProjectIndex < recentProjects.size()) {
            project.setRecentExpandedTreeEntityIds(
                    recentProjects.get(existedProjectIndex).getRecentExpandedTreeEntityIds());
            project.setRecentOpenedTreeEntityIds(
                    recentProjects.get(existedProjectIndex).getRecentOpenedTreeEntityIds());
            recentProjects.remove(existedProjectIndex);
        }

        recentProjects.add(0, project);
        if (recentProjects.size() > NUMBER_OF_RECENT_PROJECTS) {
            recentProjects = recentProjects.subList(0, NUMBER_OF_RECENT_PROJECTS);
        }
        saveRecentProjects(new ArrayList<ProjectEntity>(recentProjects));
    }

    private List<ProjectEntity> validateRecentProjects(List<ProjectEntity> recentProjects) throws Exception {
        if (recentProjects != null) {
            List<ProjectEntity> resultList = new ArrayList<ProjectEntity>();
            for (ProjectEntity recentProject : recentProjects) {
                if (recentProject != null) {
                    File projectFolder = new File(recentProject.getFolderLocation());
                    File projectFile = new File(recentProject.getLocation());
                    if (projectFolder.exists() && projectFolder.isDirectory() && projectFile.exists()
                            && projectFile.isFile()) {
                        ProjectEntity project = getDataProviderSetting().getProjectDataProvider()
                                .getProject(projectFile.getAbsolutePath());
                        if (project.getName().equals(recentProject.getName())) {
                            resultList.add(recentProject);
                        }
                    }
                }
            }
            if (resultList.size() != recentProjects.size()) {
                saveRecentProjects(resultList);
            }
            return resultList;
        }
        return new ArrayList<ProjectEntity>();
    }

    @SuppressWarnings("unchecked")
    public List<ProjectEntity> getRecentProjects() throws Exception {
        ObjectInputStream inputStream = null;
        List<ProjectEntity> projects = null;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(RECENT_PROJECT_FILE_LOCATION));
            projects = (List<ProjectEntity>) inputStream.readObject();
        } catch (FileNotFoundException fileNotFoundException) {} catch (Exception e) {
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return validateRecentProjects(projects);
    }

    public void saveRecentProjects(List<ProjectEntity> recentProjects) throws Exception {
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(RECENT_PROJECT_FILE_LOCATION));
            outputStream.writeObject(recentProjects);
        } catch (Exception e) {
            throw e;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
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

    public void clearWorkingStateOfRecentProjects() {
        // Clear working state of recent projects
        try {
            List<ProjectEntity> recentProjects = getRecentProjects();
            if (recentProjects == null || recentProjects.isEmpty()) {
                return;
            }
            for (ProjectEntity project : recentProjects) {
                project.setRecentExpandedTreeEntityIds(null);
                project.setRecentOpenedTreeEntityIds(null);
            }
            saveRecentProjects(recentProjects);
        } catch (Exception e) {}
    }

    public void keepStateOfExpandedTreeEntities(List<String> expandedTreeEntityIds) throws Exception {
        if (getCurrentProject() == null) {
            return;
        }
        List<ProjectEntity> recentProjects = getRecentProjects();
        if (recentProjects == null || recentProjects.isEmpty()) {
            return;
        }
        recentProjects.get(0).setRecentExpandedTreeEntityIds(expandedTreeEntityIds);
        saveRecentProjects(recentProjects);
    }

    public void keepStateOfOpenedEntities(List<String> openedEntityIds) throws Exception {
        if (getCurrentProject() == null) {
            return;
        }
        List<ProjectEntity> recentProjects = getRecentProjects();
        if (recentProjects == null || recentProjects.isEmpty()) {
            return;
        }
        recentProjects.get(0).setRecentOpenedTreeEntityIds(openedEntityIds);
        saveRecentProjects(recentProjects);
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
}
