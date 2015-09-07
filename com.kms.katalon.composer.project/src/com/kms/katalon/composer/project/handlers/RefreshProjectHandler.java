package com.kms.katalon.composer.project.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class RefreshProjectHandler {
	@Inject
	private IEventBroker eventBroker;
	
	@SuppressWarnings("restriction")
	@CanExecute
	public boolean canExecute() {
		try {
			return (ProjectController.getInstance().getCurrentProject() != null)
					&& !LauncherManager.getInstance().isAnyLauncherRunning();
		} catch (CoreException e) {
			LoggerSingleton.getInstance().getLogger().error(e);
			return false;
		}
	}
	
	@Execute
	public void execute() {
		eventBroker.post(EventConstants.EXPLORER_REFRESH_ALL_ITEMS, null);
	}
}
