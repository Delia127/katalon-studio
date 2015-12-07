package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class ProjectFileServiceManager {
    public static ProjectEntity addNewProject(String name, String description, short pageLoadTimeout,
            String projectLocation) throws Exception {

        // remove the "\\" post-fix
        if (projectLocation.endsWith(File.separator)) {
            projectLocation = projectLocation.substring(0, projectLocation.length() - 1);
        }

        ProjectEntity project = new ProjectEntity();
        project.setFolderLocation(projectLocation + File.separator + name);
        project.setName(name);
        project.setDescription(description);
        project.setPageLoadTimeout(pageLoadTimeout);

        EntityService.getInstance().saveEntity(project);
        FolderFileServiceManager.initRootEntityFolders(project);
        ReportFileServiceManager.initReportFolder(project.getFolderLocation());
        createSettingFolder(project);

        GroovyUtil.initGroovyProject(project,
                FolderFileServiceManager.loadAllTestCaseDescendants(FolderFileServiceManager.getTestCaseRoot(project)),
                null);

        return project;
    }

    public static ProjectEntity getProject(String projectFileLocation) throws Exception {
        File projectFile = new File(projectFileLocation);
        if (projectFile.isFile() && projectFile.exists()) {
            ProjectEntity project = (ProjectEntity) EntityService.getInstance().getEntityByPath(projectFileLocation);
            project.setFolderLocation(projectFile.getParent());
            createSettingFolder(project);
            return project;
        }
        return null;
    }

    public static ProjectEntity openProject(String projectFileLocation) throws Exception {
        ProjectEntity project = openProjectWithoutClasspath(projectFileLocation);
        if (project != null) {
            GroovyUtil.openGroovyProject(project, FolderFileServiceManager
                    .loadAllTestCaseDescendants(FolderFileServiceManager.getTestCaseRoot(project)));
        }
        return project;
    }

    public static ProjectEntity openProjectWithoutClasspath(String projectFileLocation) throws Exception {
        File projectFile = new File(projectFileLocation);
        if (projectFile.isFile() && projectFile.exists()) {
            ProjectEntity project = (ProjectEntity) EntityService.getInstance().getEntityByPath(projectFileLocation);
            project.setFolderLocation(projectFile.getParent());
            createSettingFolder(project);
            FolderFileServiceManager.initRootEntityFolders(project);
            return project;
        }
        return null;
    }

    public static ProjectEntity updateProject(String name, String description, String projectFileLocation, short pageLoadTimeout)
            throws Exception {
        ProjectEntity project = getProject(projectFileLocation);

        IProject oldGroovyProject = GroovyUtil.getGroovyProject(project);

        project.setName(name);
        project.setDescription(description);
        project.setPageLoadTimeout(pageLoadTimeout);

        // name changed
        if (!project.getLocation().equals(projectFileLocation)) {
            EntityService.getInstance().getEntityCache().remove(project, true);
            try {
                GroovyUtil.updateGroovyProject(project, oldGroovyProject);
            } catch (CoreException ex) {
                throw new DALException(StringConstants.MNG_EXC_FAILED_TO_UPDATE_PROJ);
            }
        }

        EntityService.getInstance().saveEntity(project);
        
        return project;
    }

    public static boolean isDuplicationProjectName(String name, String projectFolderLocation) throws Exception {
        EntityService.getInstance().validateName(name);
        return (getProject(projectFolderLocation + File.separator + name + File.separator + name
                + ProjectEntity.getProjectFileExtension()) != null);
    }

    private static void createSettingFolder(ProjectEntity project) throws IOException {
        File settingFolder = new File(project.getFolderLocation() + File.separator + FileServiceConstant.SETTING_DIR);
        if (!settingFolder.exists()) {
            settingFolder.mkdir();
        }

        File externalSettingFolder = new File(project.getFolderLocation() + File.separator
                + FileServiceConstant.EXTERNAL_SETTING_DIR);
        if (!externalSettingFolder.exists()) {
            externalSettingFolder.mkdir();
        }

        File internalSettingFolder = new File(project.getFolderLocation() + File.separator
                + FileServiceConstant.INTERNAL_SETTING_DIR);
        if (!internalSettingFolder.exists()) {
            internalSettingFolder.mkdir();
        }
    }
}
