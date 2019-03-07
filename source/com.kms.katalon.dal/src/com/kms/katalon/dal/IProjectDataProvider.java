package com.kms.katalon.dal;

import java.io.File;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.project.ProjectEntity;

public interface IProjectDataProvider {
    public ProjectEntity newProjectEntity(String name, String description, String projectLocation, boolean legacy)
            throws DALException;
    
	public ProjectEntity addNewProject(String name, String description, short pageLoadTimeout, String projectValue)
			throws Exception;
	
	public ProjectEntity getProject(String projectFileLocation) throws DALException;
	
	public ProjectEntity openProjectWithoutClasspath(String projectValue) throws Exception;

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

    ProjectEntity updateProjectEntity(File projectFile, ProjectEntity newInfo) throws DALException;
}
