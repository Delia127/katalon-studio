package com.kms.katalon.dal;

import java.io.File;

import com.kms.katalon.entity.project.ProjectEntity;

public interface IProjectDataProvider {
	public ProjectEntity addNewProject(String name, String description, short pageLoadTimeout, String projectValue)
			throws Exception;

	public ProjectEntity getProject(String projectValue) throws Exception;
	
	public ProjectEntity getProjectWithoutClasspath(String projectValue) throws Exception;

	public ProjectEntity updateProject(String name, String description, String projectValue, short pageLoadTimeout)
			throws Exception;
	
	public void updateProject(ProjectEntity projectEntity)
			throws Exception;

	public Boolean isDuplicationProjectName(String name, String projectPK) throws Exception;
	
	public String getSystemTempFolder();
	
	public String getSettingFolder();
	
	public String getExternalSettingFolder();
	
	public String getInternalSettingFolder();

    File getProjectFile(String folderLocation);
}
