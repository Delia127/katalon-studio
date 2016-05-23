package com.kms.katalon.controller;

import com.kms.katalon.controller.constants.StringConstants;
import com.kms.katalon.entity.project.ProjectEntity;

public class ExportProjectController extends AbstractExportController implements IImportExportController {

	public ExportProjectController(String guid, ProjectEntity project, String directory) {
		super(guid, project, directory);
	}

	private static final String EXPORT_PROJECT_DISPLAY_TEXT = StringConstants.CTRL_TXT_EXPORT_PROJ;

	@Override
	public boolean execute() throws Exception {
		return getDataProviderSetting().getExportDataProvider().exportProject(
				getDataProviderSetting().getEntityPk(project), guid, directory);
	}
	
	@Override
	public String getDisplayText() {
		return EXPORT_PROJECT_DISPLAY_TEXT;
	}
}
