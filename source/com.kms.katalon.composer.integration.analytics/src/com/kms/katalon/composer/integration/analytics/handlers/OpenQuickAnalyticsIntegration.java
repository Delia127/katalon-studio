package com.kms.katalon.composer.integration.analytics.handlers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.integration.analytics.dialog.QuickAnalyticsIntegrationDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.entity.repository.WebElementEntity;

public class OpenQuickAnalyticsIntegration {
	@Inject
	private IEventBroker eventBroker;

	@PostConstruct
	public void registerEventHandler() {
		eventBroker.subscribe(EventConstants.ANALYTIC_QUICK_INTEGRATION_DIALOG_OPEN, new EventServiceAdapter() {
			@Override
			public void handleEvent(Event event) {
				execute();
			}
		});
	}

	@CanExecute
	public boolean canExecute() {
		return ProjectController.getInstance().getCurrentProject() != null;
	}

	@Execute
	public void execute() {
//		ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
		
		QuickAnalyticsIntegrationDialog quickStartDialog = new QuickAnalyticsIntegrationDialog(Display.getCurrent().getActiveShell());
		quickStartDialog.open();
	}
}
