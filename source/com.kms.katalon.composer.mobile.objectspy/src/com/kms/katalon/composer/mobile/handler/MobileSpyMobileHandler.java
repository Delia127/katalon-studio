package com.kms.katalon.composer.mobile.handler;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileDeviceDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileObjectSpyDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;

public class MobileSpyMobileHandler implements EventHandler {

	@Inject
	private IEventBroker eventBroker;

	@Inject
	private ESelectionService selectionService;

	private MobileObjectSpyDialog objectSpyDialog;
	
	private MobileDeviceDialog deviceView;

	private Shell activeShell;
	
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
		try {
			if(this.activeShell == null){
				this.activeShell = activeShell;
			}
			eventBroker.subscribe(EventConstants.OBJECT_SPY_REFRESH_SELECTED_TARGET, this);
			eventBroker.subscribe(EventConstants.OBJECT_SPY_ENSURE_DEVICE_VIEW_DIALOG, this);
			
			if (objectSpyDialog == null || objectSpyDialog.isDisposed()) {
				objectSpyDialog = new MobileObjectSpyDialog(activeShell, LoggerSingleton.getInstance().getLogger(), eventBroker, selectionService);
				objectSpyDialog.open();
			} 						
			if (deviceView == null || deviceView.isDisposed()) {
				deviceView = new MobileDeviceDialog(activeShell, LoggerSingleton.getInstance().getLogger(), eventBroker);
				deviceView.open();
			}			
			//else {
			//	eventBroker.subscribe(EventConstants.OBJECT_SPY_RESET_SELECTED_TARGET, this);
			//	eventBroker.subscribe(EventConstants.OBJECT_SPY_CLOSE_MOBILE_APP, this);
			//	eventBroker.subscribe(EventConstants.OBJECT_SPY_MOBILE_HIGHLIGHT, this);
			//}
		} catch (Exception e) {
			if (objectSpyDialog != null && !objectSpyDialog.isDisposed()) {
				objectSpyDialog.dispose();
				objectSpyDialog.close();
			}
			if (deviceView != null && !deviceView.isDisposed()) {
				deviceView.dispose();
				deviceView.close();
			}
			LoggerSingleton.logError(e);
			MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE, e.getMessage());
		}
	}

	@CanExecute
	private boolean canExecute() throws Exception {
		if (ProjectController.getInstance().getCurrentProject() != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void handleEvent(Event event) { 
		if (event.getTopic().equals(EventConstants.OBJECT_SPY_REFRESH_SELECTED_TARGET)) {
			if (objectSpyDialog != null && !objectSpyDialog.isDisposed()) {
				try {
					FolderEntity parentFolder = objectSpyDialog.getParentFolder();
					eventBroker.send(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, new FolderTreeEntity(parentFolder, null));
				} catch (Exception e) {
					LoggerSingleton.logError(e);
				}
			}
		}
		else if (event.getTopic().equals(EventConstants.OBJECT_SPY_ENSURE_DEVICE_VIEW_DIALOG)) {
			if ((deviceView == null || deviceView.isDisposed()) && activeShell != null) {
				deviceView = new MobileDeviceDialog(activeShell, LoggerSingleton.getInstance().getLogger(), eventBroker);
				deviceView.open();
			}
		}		
	}
	
}