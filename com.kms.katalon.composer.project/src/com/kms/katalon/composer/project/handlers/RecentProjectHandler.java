package com.kms.katalon.composer.project.handlers;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.internal.events.EventBroker;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;

@SuppressWarnings("restriction")
public class RecentProjectHandler {
	@Inject 
	EventBroker eventBroker;
	
	@Execute
	public void execute(@Optional @Named(IdConstants.OPEN_RECENT_PROJECT_COMMAND_PARAMETER_ID) String projectPk)
			throws IOException {
		if (projectPk != null) {
        	eventBroker.post(EventConstants.PROJECT_OPEN, projectPk); 
        }
	}
}
