package com.kms.katalon.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.e4.core.di.annotations.Creatable;

import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

@Creatable
public class ProjectController extends EntityController {
    private static EntityController _instance;

    private static final String RECENT_PROJECT_FILE_LOCATION = Platform.getLocation().toString() + File.separator
            + "recent_projects";
    private static final int NUMBER_OF_RECENT_PROJECTS = 5;

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
        ProjectEntity newProject = dataProviderSetting.getProjectDataProvider().addNewProject(name, description,
                TestEnvironmentController.getInstance().getPageLoadTimeOutDefaultValue(), projectLocation);
        addRecentProject(newProject);
        GlobalVariableController.getInstance().generateGlobalVariableLibFile(newProject, null);
        KeywordController.getInstance().parseAllCustomKeywordsWithoutRefreshing(newProject);
        return newProject;
    }

    public ProjectEntity openProjectForUI(String projectPk, IProgressMonitor monitor) throws Exception {
        if (monitor == null) monitor = new NullProgressMonitor();

        ProjectEntity project = dataProviderSetting.getProjectDataProvider().getProjectWithoutClasspath(projectPk);

        if (project != null) {
            monitor.beginTask("Initialzing project's working space...", 10);
            GroovyUtil.initGroovyProject(project, FolderController.getInstance().getTestCaseRoot(project),
                    new SubProgressMonitor(monitor, 8));
            addRecentProject(project);
            GlobalVariableController.getInstance().generateGlobalVariableLibFile(project,
                    new SubProgressMonitor(monitor, 1));
            KeywordController.getInstance().parseAllCustomKeywords(project, new SubProgressMonitor(monitor, 1));
            monitor.done();
        }
        return project;
    }

    public ProjectEntity openProject(String projectPk) throws Exception {
        ProjectEntity project = dataProviderSetting.getProjectDataProvider().getProject(projectPk);

        if (project != null) {
            addRecentProject(project);
            KeywordController.getInstance().parseAllCustomKeywords(project, null);
            GlobalVariableController.getInstance().generateGlobalVariableLibFile(project, null);
        }
        return project;
    }

    public void closeProject(String projectPk, IProgressMonitor monitor) throws Exception {
        ProjectEntity project = dataProviderSetting.getProjectDataProvider().getProjectWithoutClasspath(projectPk);
        if (project != null) {
            GroovyUtil.getGroovyProject(project).close(monitor);
        }
    }

    public void updateProject(String name, String description, String projectPk) throws Exception {
        ProjectEntity project = dataProviderSetting.getProjectDataProvider().updateProject(name, description,
                projectPk, (short) 0);
        if (project != null) {
            addRecentProject(project);
        }
    }

    public void updateProject(ProjectEntity projectEntity) throws Exception {
        dataProviderSetting.getProjectDataProvider().updateProject(projectEntity);
    }

    public void addRecentProject(ProjectEntity project) throws Exception {
        List<ProjectEntity> recentProjects = getRecentProjects();
        int existedProjectIndex = -1;
        for (int i = 0; i < recentProjects.size(); i++) {
            if (dataProviderSetting.getEntityPk(project).equals(dataProviderSetting.getEntityPk(recentProjects.get(i)))) {
                existedProjectIndex = i;
                break;
            }
        }
        if (existedProjectIndex > -1 && existedProjectIndex < recentProjects.size()) {
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
                        resultList.add(recentProject);
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
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (Exception e) {
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
        return !dataProviderSetting.getProjectDataProvider().isDuplicationProjectName(projectName,
                projectParentFolderName);
    }

    public String getTempDir() {
        return dataProviderSetting.getProjectDataProvider().getSystemTempFolder();
    }

}
