package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.dal.IProjectDataProvider;
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
	public ProjectEntity getProject(String projectValue) throws Exception {
		return ProjectFileServiceManager.openProject(projectValue);
	}

	@Override
	public ProjectEntity getProjectWithoutClasspath(String projectValue) throws Exception {
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
            if (('.' + FilenameUtils.getExtension(file.getAbsolutePath())).equals(ProjectEntity.getProjectFileExtension())) {
                return file;
            }
        }
        return null;
    }
}
