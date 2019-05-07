package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.dal.IProjectDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.manager.ProjectFileServiceManager;
import com.kms.katalon.entity.project.ProjectEntity;

public class ProjectFileServiceDataProvider implements IProjectDataProvider {
    @Override
    public ProjectEntity addNewProject(String name, String description, short pageLoadTimeout, String projectValue)
            throws Exception {
        return ProjectFileServiceManager.addNewProject(name, description, pageLoadTimeout, projectValue);
    }

    @Override
    public ProjectEntity openProjectWithoutClasspath(String projectValue) throws Exception {
        return ProjectFileServiceManager.openProjectWithoutClasspath(projectValue);
    }

    @Override
    public ProjectEntity updateProject(String newName, String description, String projectPk, short pageLoadTimeout)
            throws Exception {
        return ProjectFileServiceManager.updateProject(newName, description, projectPk, pageLoadTimeout);
    }

    @Override
    public Boolean isDuplicationProjectName(String name, String projectPK) throws Exception {
        return ProjectFileServiceManager.isDuplicationProjectName(name, projectPK);
    }

    @Override
    public String getSystemTempFolder() {
        return FileServiceConstant.TEMP_DIR;
    }

    @Override
    public String getSettingFolder() {
        return FileServiceConstant.SETTING_DIR;
    }

    @Override
    public String getExternalSettingFolder() {
        return FileServiceConstant.EXTERNAL_SETTING_DIR;
    }

    @Override
    public String getInternalSettingFolder() {
        return FileServiceConstant.INTERNAL_SETTING_DIR;
    }

    @Override
    public void updateProject(ProjectEntity projectEntity) throws Exception {
        EntityService.getInstance().saveEntity(projectEntity);

    }

    @Override
    public File getProjectFile(String folderLocation) {
        if (folderLocation == null) {
            return null;
        }
        File folder = new File(folderLocation);
        if (!folder.exists()) {
            return null;
        }
        for (File file : folder.listFiles()) {
            if (('.' + FilenameUtils.getExtension(file.getAbsolutePath()))
                    .equals(ProjectEntity.getProjectFileExtension())) {
                return file;
            }
        }
        return null;
    }

    @Override
    public ProjectEntity getProject(String projectFileLocation) throws DALException {
        try {
            return (ProjectEntity) EntityService.getInstance().getEntityByPath(projectFileLocation);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public ProjectEntity newProjectEntity(String name, String description, String projectLocation, boolean legacy)
            throws DALException {
        return ProjectFileServiceManager.newProjectEntity(name, description, projectLocation, legacy);
    }

    @Override
    public ProjectEntity updateProjectEntity(File projectFile, ProjectEntity newInfo) throws DALException {
        try {
            ProjectEntity project = (ProjectEntity) EntityService.getInstance()
                    .getEntityByPath(projectFile.getAbsolutePath());
            project.setFolderLocation(projectFile.getParentFile().getAbsolutePath());
            project.setName(newInfo.getName());
            project.setDescription(newInfo.getDescription());
            FileUtils.deleteQuietly(projectFile);
            EntityService.getInstance().saveEntity(project);

            return project;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }
}
