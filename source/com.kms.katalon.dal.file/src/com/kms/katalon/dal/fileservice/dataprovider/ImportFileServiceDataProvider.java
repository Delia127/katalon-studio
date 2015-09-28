package com.kms.katalon.dal.fileservice.dataprovider;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.dal.IImportDataProvider;
import com.kms.katalon.dal.fileservice.manager.ImportFileServiceManager;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectImportingEntity;
import com.kms.katalon.entity.util.ImportDuplicateEntityResult;

public class ImportFileServiceDataProvider implements IImportDataProvider {
	private static Map<String, ImportFileServiceManager> importMapping;
	
	public ImportFileServiceDataProvider() {
		if (importMapping == null) {
			importMapping = new HashMap<String, ImportFileServiceManager>();
		}
	}
	
	@Override
	public ProjectEntity importProject(
			ProjectImportingEntity projectImportingInfomation, PropertyChangeListener propertyChangeListener) throws Exception {
		ImportFileServiceManager importManager = new ImportFileServiceManager(
				projectImportingInfomation.getIsCreateNewProject(), 
				projectImportingInfomation.getProjectName(), 
				projectImportingInfomation.getCurrentProject());
		importManager.addPropertyChangeListener(propertyChangeListener);
		importMapping.put(projectImportingInfomation.getImportGUID(), importManager);
		return importManager.executeImport(projectImportingInfomation.getImportDirectory());
	}

	@Override
	public Integer getImportProgress(String importGUID) throws Exception {
		ImportFileServiceManager importManager = importMapping.get(importGUID);
		if (importManager != null ) {
			return importManager.getProgress();
		}
		return 0;
	}

	@Override
	public void cancelImport(String importGUID) throws Exception {
		ImportFileServiceManager importManager = importMapping.get(importGUID);
		if (importManager != null ) {
			importManager.setCancelImportTask(true);
		}
	}

	@Override
	public void setImportDuplicateEntityResult(
			ImportDuplicateEntityResult importDuplicateEntityResult,
			String importGUID) throws Exception {
		ImportFileServiceManager importManager = importMapping.get(importGUID);
		if (importManager != null ) {
			importManager.setImportDuplicateEntityResult(importDuplicateEntityResult);
		}
	}

	@Override
	public void setWaitingConfirmation(Boolean isConfirmed, String importGUID) {
		ImportFileServiceManager importManager = importMapping.get(importGUID);
		if (importManager != null ) {
			importManager.setWaitingConfirmation(isConfirmed);
		}
	}

}
