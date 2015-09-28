package com.kms.katalon.controller;

import java.beans.PropertyChangeListener;
import java.io.File;

import org.eclipse.e4.core.services.log.Logger;

import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectImportingEntity;
import com.kms.katalon.entity.util.ImportDuplicateEntityResult;

@SuppressWarnings("restriction")
public class ImportProjectIntoProjectController extends AbstractImportController {
	private PropertyChangeListener propertyChangeListener;

	public ImportProjectIntoProjectController(String guid, ProjectEntity project, String directory,
			Logger logger, PropertyChangeListener propertyChangeListener) {
		super(guid, project, directory, logger);
		this.propertyChangeListener = propertyChangeListener;
	}

	@Override
	public boolean execute() throws Exception {
		ProjectImportingEntity projectImportingInfomation = new ProjectImportingEntity();
		projectImportingInfomation.setIsCreateNewProject(false);
		projectImportingInfomation.setCurrentProject(project);
		projectImportingInfomation.setImportGUID(guid);
		projectImportingInfomation.setImportDirectory(new File(directory));
		ProjectEntity mergedProject = dataProviderSetting.getImportDataProvider().importProject(
				projectImportingInfomation, propertyChangeListener);
		if (mergedProject != null) {
			return true;
		}
		return false;
	}
	
	public void setImportDuplicateEntityResult(ImportDuplicateEntityResult result) throws Exception {
		dataProviderSetting.getImportDataProvider().setImportDuplicateEntityResult(
				result, guid);
	}
}
