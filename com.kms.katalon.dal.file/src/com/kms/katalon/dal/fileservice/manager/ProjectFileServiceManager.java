package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class ProjectFileServiceManager {
	public static ProjectEntity addNewProject(String name, String description, short pageLoadTimeout,
			String projectValue) throws Exception {

		if (projectValue.endsWith("\\")) {
			projectValue = projectValue.substring(0, projectValue.length() - 1);
		}
		ProjectEntity project = new ProjectEntity();
		project.setFolderLocation(projectValue + File.separator + name);
		project.setName(name);
		project.setDescription(description);
		project.setPageLoadTimeout(pageLoadTimeout);

		EntityService.getInstance().saveEntity(project);
		FolderFileServiceManager.initRootEntityFolders(project);
		ReportFileServiceManager.initReportFolder(project.getFolderLocation());
		createSettingFolder(project);

		GroovyUtil.initGroovyProject(project, FolderFileServiceManager
				.loadAllTestCaseDescendants(FolderFileServiceManager.getTestCaseRoot(project)), null);

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
			DataProviderState.getInstance().setCurrentProject(project);
			FolderFileServiceManager.initRootEntityFolders(project);		
			return project;
		}
		return null;
	}

	public static void updateProject(String name, String description, String projectFileLocation, short pageLoadTimeout) throws Exception {
		ProjectEntity project = getProject(projectFileLocation);
		project.setName(name);
		project.setDescription(description);
		project.setPageLoadTimeout(pageLoadTimeout);
		createSettingFolder(project);

		// name changed
		if (!project.getLocation().equals(projectFileLocation)) {
			EntityService.getInstance().getEntityCache().remove(project, false);
			File projectFolder = new File(projectFileLocation).getParentFile();
			boolean isUpdated = projectFolder.renameTo(projectFolder = new File(projectFolder.getParentFile(), name));
			if (isUpdated) {
				project.setFolderLocation(projectFolder.getPath());
				String oldName = new File(projectFileLocation).getName();
				File oldProjectFile = new File(projectFolder.getPath() + File.separator + oldName);
				isUpdated = oldProjectFile.renameTo(new File(projectFolder.getPath() + File.separator + name
						+ ProjectEntity.getProjectFileExtension()));
			}
			if (!isUpdated) {
				throw new Exception(StringConstants.MNG_EXC_FAILED_TO_UPDATE_PROJ);
			}
		}

		EntityService.getInstance().saveEntity(project);
	}

	public static boolean isDuplicationProjectName(String name, String projectPK) throws Exception {
	    EntityService.getInstance().validateName(name);
		return (getProject(projectPK + File.separator + name + File.separator + name + ProjectEntity.getProjectFileExtension()) != null);
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
