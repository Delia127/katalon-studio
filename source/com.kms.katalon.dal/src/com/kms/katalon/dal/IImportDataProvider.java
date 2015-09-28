package com.kms.katalon.dal;

import java.beans.PropertyChangeListener;

import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectImportingEntity;
import com.kms.katalon.entity.util.ImportDuplicateEntityResult;

public interface IImportDataProvider{
	public ProjectEntity importProject(ProjectImportingEntity projectImportingInfomation, 
			PropertyChangeListener propertyChangeListener) throws Exception;
	public Integer getImportProgress(String importGUID) throws Exception;
	public void cancelImport(String importGUID) throws Exception;
	public void setImportDuplicateEntityResult(ImportDuplicateEntityResult importDuplicateEntityResult, 
			String importGUID) throws Exception;
	public void setWaitingConfirmation(Boolean isConfirmed, String importGUID);
}
