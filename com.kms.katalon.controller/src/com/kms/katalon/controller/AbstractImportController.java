package com.kms.katalon.controller;

import org.eclipse.e4.core.services.log.Logger;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.util.ImportDuplicateEntityResult;

@SuppressWarnings("restriction")
public abstract class AbstractImportController extends EntityController implements IImportExportController,
		EventHandler {

	protected static final String IMPORT_PROJECT_ERROR_MESSAGE = StringConstants.CTRL_ERROR_MSG_CANNOT_IMPORT_PROJ;
	protected static final String IMPORTING_PROJECT_DISPLAY_TEXT = StringConstants.CTRL_TXT_IMPORTING_PROJ;

	protected String guid;
	protected ProjectEntity project;
	protected String directory;
	protected Logger logger;
	
	public AbstractImportController(String guid, ProjectEntity project, String directory, Logger logger) {
		this.guid = guid;
		this.project = project;
		this.directory = directory;
		this.logger = logger;
	};

	@Override
	public String getDisplayText() {
		return IMPORTING_PROJECT_DISPLAY_TEXT;
	}

	@Override
	public String getErrorMessage() {
		return IMPORT_PROJECT_ERROR_MESSAGE;
	}

	@Override
	public void cancel() throws Exception {
		dataProviderSetting.getImportDataProvider().cancelImport(guid);
	}

	@Override
	public int getProgress() throws Exception {
		return dataProviderSetting.getImportDataProvider().getImportProgress(guid);
	}

	@Override
	public void handleEvent(Event event) {
		if (event.getTopic().equalsIgnoreCase(EventConstants.IMPORT_DUPLICATE_ENTITY_RESULT)) {
			Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
			if (object != null && object instanceof ImportDuplicateEntityResult) {
				try {
					dataProviderSetting.getImportDataProvider().setImportDuplicateEntityResult(
							(ImportDuplicateEntityResult) object, guid);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
	}
}
