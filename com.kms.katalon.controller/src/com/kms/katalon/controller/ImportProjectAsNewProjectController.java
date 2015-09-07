package com.kms.katalon.controller;

import java.io.File;

import org.eclipse.e4.core.services.log.Logger;

import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectImportingEntity;

@SuppressWarnings("restriction")
public class ImportProjectAsNewProjectController extends AbstractImportController {
	
	private String projectName;
	
	private ProjectEntity newProject;

	public ImportProjectAsNewProjectController(String guid, ProjectEntity project, String directory,
			Logger logger, String projectName) {
		super(guid, project, directory, logger);
		this.projectName = projectName;
	}

	@Override
	public boolean execute() throws Exception {
		ProjectImportingEntity projectImportingInfomation = new ProjectImportingEntity();
		projectImportingInfomation.setIsCreateNewProject(true);
		projectImportingInfomation.setProjectName(projectName);
		projectImportingInfomation.setCurrentProject(project);
		projectImportingInfomation.setImportGUID(guid);
		projectImportingInfomation.setImportDirectory(new File(directory));
		ProjectEntity newProject = dataProviderSetting.getImportDataProvider().importProject(projectImportingInfomation, null);
		if (newProject != null) {
			return true;
		}
		return false;
	}

	public ProjectEntity getNewProject() {
		return newProject;
	}

	public void setNewProject(ProjectEntity newProject) {
		this.newProject = newProject;
	}
}
