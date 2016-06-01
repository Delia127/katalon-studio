package com.kms.katalon.controller;

import com.kms.katalon.controller.constants.StringConstants;
import com.kms.katalon.entity.project.ProjectEntity;


public abstract class AbstractExportController extends EntityController implements IImportExportController {

	protected static final String EXPORT_PROJECT_ERROR_MESSAGE = StringConstants.CTRL_ERROR_MSG_CANNOT_EXPORT_PROJ;
	
	protected String guid;
	protected ProjectEntity project;
	protected String directory;
	
	public AbstractExportController(String guid, ProjectEntity project, String directory) {
		this.guid = guid;
		this.project = project;
		this.directory = directory;
	};

	@Override
	public void cancel() throws Exception {
		getDataProviderSetting().getExportDataProvider().cancelExport(guid);
	}

	@Override
	public int getProgress() throws Exception {
		return getDataProviderSetting().getExportDataProvider().getExportProgress(guid);
	}
	
	@Override
	public String getErrorMessage() {
		return EXPORT_PROJECT_ERROR_MESSAGE;
	}
}
