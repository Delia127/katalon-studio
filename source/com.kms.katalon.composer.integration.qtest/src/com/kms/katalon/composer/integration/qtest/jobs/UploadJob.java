package com.kms.katalon.composer.integration.qtest.jobs;

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public abstract class UploadJob extends Job {
	private List<IntegratedFileEntity> fileEntities;
	protected String token;
	protected ProjectEntity projectEntity;
	
	public UploadJob(String name) {
		super(name);
	}

	public List<IntegratedFileEntity> getFileEntities() {
		return fileEntities;
	}

	public void setFileEntities(List<IntegratedFileEntity> fileEntities) {
		this.fileEntities = fileEntities;
	}
	
	public void doTask() {
		projectEntity = ProjectController.getInstance().getCurrentProject();

		token = QTestSettingStore.getToken(projectEntity.getFolderLocation());
		if (token == null || token.isEmpty()) {
			MessageDialog.openWarning(null, "Warning", "Please enter a valid authentication.");
			return;
		}
		schedule();
	}
	
	/**
	 * Sometimes, test case's name is very long and we need to wrap it smaller.
	 */
	protected String getWrappedName(String name) {
		String wrappedName = name;
		if (name.length() > 80) {
			name = name.substring(name.length() - 80, name.length());
		}
		
		return wrappedName;
	}
}
