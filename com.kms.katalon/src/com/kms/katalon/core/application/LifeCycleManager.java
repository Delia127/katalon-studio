package com.kms.katalon.core.application;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.handlers.CloseHandler;
import com.kms.katalon.composer.handlers.SaveHandler;
import com.kms.katalon.composer.handlers.SearchHandler;
import com.kms.katalon.core.application.ApplicationRunningMode.RunningMode;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.execution.launcher.manager.ConsoleMain;

@SuppressWarnings("restriction")
public class LifeCycleManager {
	private void startUpGUIMode() throws Exception {
		refreshAllProjects();
		setupHandlers();
		setupPreferences();
		EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.WORKSPACE_CREATED, "");
	}

	protected void setupHandlers() {
		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getService(IHandlerService.class);
		handlerService.activateHandler(IWorkbenchCommandConstants.FILE_SAVE, new SaveHandler());
		handlerService.activateHandler(IWorkbenchCommandConstants.FILE_CLOSE, new CloseHandler());
		handlerService.activateHandler("org.eclipse.search.ui.openSearchDialog", new SearchHandler());
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(new IPartListener2() {
			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
			}

			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
			}

			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
			}

			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
			}

			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
			}

			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				EventBrokerSingleton.getInstance().getEventBroker()
						.post(EventConstants.ECLIPSE_EDITOR_CLOSED, partRef.getPart(true));
			}

			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
				if (NewSearchUI.SEARCH_VIEW_ID.equals(partRef.getId())) {
					SearchHandler.openSearchView();
				};
			}

			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void setupPreferences() {
		IPreferenceStore runtimePrefStore = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
				ResourcesPlugin.PLUGIN_PREFERENCE_SCOPE);
		if (runtimePrefStore.getBoolean(ResourcesPlugin.PREF_AUTO_REFRESH)) {
			runtimePrefStore.setValue(ResourcesPlugin.PREF_AUTO_REFRESH, false);
		}
		
		if (runtimePrefStore.getBoolean(ResourcesPlugin.PREF_AUTO_BUILDING)) {
			runtimePrefStore.setValue(ResourcesPlugin.PREF_AUTO_BUILDING, false);
		}
	}

	private void startUpConsoleMode() throws Exception {
		PlatformUI.getWorkbench().getDisplay().getActiveShell().setVisible(false);
		new ConsoleMain().launch(ApplicationRunningMode.getInstance().getRunArguments());
	}

	private void refreshAllProjects() throws Exception {
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			checkProjectLocationDeleted(project);
		}
	}

	void checkProjectLocationDeleted(IProject project) throws Exception {
		if (!project.exists()) {
			return;
		}
		IFileInfo location = IDEResourceInfoUtils.getFileInfo(project.getLocationURI());
		if (!location.exists()) {
			project.delete(true, true, null);
		}
	}

	@PostContextCreate
	void postContextCreate(final IEventBroker eventBroker) {
		// register for startup completed event and activate handler for
		// workbench windows
		eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new EventHandler() {
			@Override
			public void handleEvent(Event event) {
				if (ApplicationRunningMode.getInstance().getRunnningMode() == RunningMode.Console) {
					// PlatformUI.getWorkbench().getDisplay().getActiveShell().setVisible(false);
					try {
						startUpConsoleMode();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (ApplicationRunningMode.getInstance().getRunnningMode() == RunningMode.GUI) {
					try {
						startUpGUIMode();
					} catch (Exception e) {
						LoggerSingleton.getInstance().getLogger().error(e);
					}
				}

			}
		});
	}
}
