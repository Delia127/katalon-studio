package com.kms.katalon.core.application;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.addons.CommandBindingRemover;
import com.kms.katalon.application.RunningMode;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.application.usagetracking.UsageInfoCollector;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.handlers.CloseHandler;
import com.kms.katalon.composer.handlers.QuitHandler;
import com.kms.katalon.composer.handlers.ResetPerspectiveHandler;
import com.kms.katalon.composer.handlers.SaveHandler;
import com.kms.katalon.composer.handlers.SearchHandler;
import com.kms.katalon.composer.handlers.WorkbenchSaveHandler;
import com.kms.katalon.composer.initializer.CommandBindingInitializer;
import com.kms.katalon.composer.initializer.ContentAssistProposalInitializer;
import com.kms.katalon.composer.initializer.DefaultTextFontInitializer;
import com.kms.katalon.composer.initializer.DisplayInitializer;
import com.kms.katalon.composer.initializer.ProblemViewImageInitializer;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.util.ComposerActivationInfoCollector;

@SuppressWarnings("restriction")
public class LifeCycleManager {

    private void startUpGUIMode() throws Exception {
        refreshAllProjects();
        setupHandlers();
        setupPreferences();
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.WORKSPACE_CREATED, "");
    }

    protected void setupHandlers() {
        IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow()
                .getService(IHandlerService.class);
        handlerService.activateHandler(IWorkbenchCommandConstants.FILE_SAVE, new SaveHandler());
        handlerService.activateHandler(IWorkbenchCommandConstants.FILE_CLOSE, new CloseHandler());
        handlerService.activateHandler(IWorkbenchCommandConstants.FILE_EXIT, new QuitHandler());
        handlerService.activateHandler(IdConstants.SEARCH_COMMAND_ID, new SearchHandler());
        handlerService.activateHandler(IdConstants.RESET_PERSPECTIVE_HANDLER_ID, new ResetPerspectiveHandler());

        IContextService contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
        contextService.activateContext(IdConstants.KATALON_CONTEXT_ID);

        MTrimmedWindow model = (MTrimmedWindow) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow()
                .getService(MTrimmedWindow.class);
        IEclipseContext context = model.getContext();
        context.set(ISaveHandler.class, new WorkbenchSaveHandler());

        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(new IPartListener2() {
            @Override
            public void partVisible(IWorkbenchPartReference partRef) {
            }

            @Override
            public void partOpened(IWorkbenchPartReference partRef) {
            }

            @Override
            public void partInputChanged(IWorkbenchPartReference partRef) {
            }

            @Override
            public void partHidden(IWorkbenchPartReference partRef) {
            }

            @Override
            public void partDeactivated(IWorkbenchPartReference partRef) {
            }

            @Override
            public void partClosed(IWorkbenchPartReference partRef) {
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.ECLIPSE_EDITOR_CLOSED,
                        partRef.getPart(true));
            }

            @Override
            public void partBroughtToTop(IWorkbenchPartReference partRef) {
                if (NewSearchUI.SEARCH_VIEW_ID.equals(partRef.getId())) {
                    SearchHandler.openSearchView();
                    return;
                }
                if (IdConstants.GROOVY_EDITOR_URI.equals(partRef.getId())) {
                    EventUtil.post(EventConstants.PROPERTIES_ENTITY, null);
                }
            }

            @Override
            public void partActivated(IWorkbenchPartReference partRef) {
            }
        });

        new CommandBindingInitializer().setup();
        new CommandBindingRemover().setup();
        new ContentAssistProposalInitializer().setup();
        new ProblemViewImageInitializer().setup();
        new DefaultTextFontInitializer().setup();
        new DisplayInitializer().setup();
    }

    private void setupPreferences() {
        setupResourcePlugin();
    }

    private void setupResourcePlugin() {
        try {
            ScopedPreferenceStore runtimePrefStore = getPreferenceStore(ResourcesPlugin.PI_RESOURCES);
            if (!runtimePrefStore.getBoolean(ResourcesPlugin.PREF_AUTO_BUILDING)) {
                runtimePrefStore.setValue(ResourcesPlugin.PREF_AUTO_BUILDING, true);
            }

            // Prevent out-of-sync resources when accessing
            if (!runtimePrefStore.getBoolean(ResourcesPlugin.PREF_LIGHTWEIGHT_AUTO_REFRESH)) {
                runtimePrefStore.setValue(ResourcesPlugin.PREF_LIGHTWEIGHT_AUTO_REFRESH, true);
            }
            runtimePrefStore.save();
        } catch (IOException e) {
            logError(e);
        }
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
                try {
                    startUpGUIMode();

                    if (checkActivation(eventBroker)) {
                        eventBroker.post(EventConstants.ACTIVATION_CHECKED, null);
                    }

                } catch (Exception e) {
                    logError(e);
                }
            }

            private boolean checkActivation(final IEventBroker eventBroker) {
//                if (VersionUtil.isInternalBuild()) {
//                    return true;
//                }
                if (!(ComposerActivationInfoCollector.checkActivation(eventBroker))) {
                    eventBroker.send(EventConstants.PROJECT_CLOSE, null);
                    PlatformUI.getWorkbench().close();
                    return false;
                }

                Executors.newSingleThreadExecutor().submit(() -> UsageInfoCollector
                        .collect(UsageInfoCollector.getActivatedUsageInfo(UsageActionTrigger.OPEN_APPLICATION,
                                RunningMode.GUI)));
                return true;
            }
        });
    }

}
