package com.kms.katalon.core.application;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.addons.CommandBindingRemover;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.composer.components.ComponentBundleActivator;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.handlers.CloseHandler;
import com.kms.katalon.composer.handlers.QuitHandler;
import com.kms.katalon.composer.handlers.ResetPerspectiveHandler;
import com.kms.katalon.composer.handlers.SaveHandler;
import com.kms.katalon.composer.handlers.SearchHandler;
import com.kms.katalon.composer.handlers.WorkbenchSaveHandler;
import com.kms.katalon.composer.initializer.CommandBindingInitializer;
import com.kms.katalon.composer.initializer.ContentAssistProposalInitializer;
import com.kms.katalon.composer.initializer.CucumberEditorColorInitializer;
import com.kms.katalon.composer.initializer.DefaultLineNumberInitializer;
import com.kms.katalon.composer.initializer.DefaultTextFontInitializer;
import com.kms.katalon.composer.initializer.DisplayInitializer;
import com.kms.katalon.composer.initializer.ProblemViewImageInitializer;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GroovyTemplatePreferenceConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.tracking.core.TrackingManager;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.ComposerActivationInfoCollector;

@SuppressWarnings("restriction")
public class LifeCycleManager {

    private static boolean WORKSPACE_CREATED = false;
    
    public static boolean isWorkspaceCreated() {
        return WORKSPACE_CREATED; 
    }

    private void startUpGUIMode() throws Exception {
        ProjectController.cleanWorkspace();
        setUpDefaultTheme();
        setupHandlers();
        setupPreferences();
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.WORKSPACE_CREATED, "");
        WORKSPACE_CREATED = true;
    }

    protected void setupHandlers() throws BundleException {
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
                    new CommandBindingInitializer().resetDeleteKeyBinding();
                }
            }

            @Override
            public void partActivated(IWorkbenchPartReference partRef) {
                if (IdConstants.ECLIPSE_CONSOLE_PART_ID.equals(partRef.getId())) {
                    Trackings.trackOpenConsoleView();
                }
            }
        });

        new CommandBindingInitializer().setup();
        new CommandBindingRemover().setup();
        new ContentAssistProposalInitializer().setup();
        new ProblemViewImageInitializer().setup();
        new DefaultTextFontInitializer().setup();
        new DisplayInitializer().setup();
        new CucumberEditorColorInitializer().setup();
        new DefaultLineNumberInitializer().setup();
    }

    private void setupPreferences() {
        setupResourcePlugin();
        setupGroovyTemplatePlugin();
    }

    private void setupGroovyTemplatePlugin() {
        try {
            ScopedPreferenceStore prefStore = getPreferenceStore(
                    GroovyTemplatePreferenceConstants.ORG_CODEHAUS_GROOVY_ECLIPSE_QUICKFIX_PLUGIN_ID);

            if (!prefStore.getBoolean(GroovyTemplatePreferenceConstants.FIRST_TIME_SET_UP)) {
                // prevent user clear all or remove the predefined templates
                prefStore.setDefault(GroovyTemplatePreferenceConstants.GROOVY_PREF_KEY,
                        GroovyTemplatePreferenceConstants.GROOVY_TEMPLATES);
                prefStore.setToDefault(GroovyTemplatePreferenceConstants.GROOVY_PREF_KEY);
                prefStore.setValue(GroovyTemplatePreferenceConstants.FIRST_TIME_SET_UP, true);
            }
            prefStore.save();
        } catch (IOException e) {
            logError(e);
        }

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
    
    private void setUpDefaultTheme() {
        IThemeEngine engine = ComponentBundleActivator.getThemeEngine(Display.getCurrent());
        if (engine.getActiveTheme() == null) {
            engine.setTheme("com.kms.katalon.theme.default", true);
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

                    ApplicationStaupHandler.scheduleCollectingStatistics();

                } catch (Exception e) {
                    logError(e);
                }

                try {
                    if (ApplicationStaupHandler.checkActivation(true)) {
                        eventBroker.post(EventConstants.ACTIVATION_CHECKED, null);
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
                        PlatformUI.getWorkbench().close();
                    }
                }
            }

            private boolean checkActivation(final IEventBroker eventBroker) throws Exception {
                // if (VersionUtil.isInternalBuild()) {
                // return true;
                // }
                if (!(ComposerActivationInfoCollector.checkActivation(true))) {
                    eventBroker.send(EventConstants.PROJECT_CLOSE, null);
                    PlatformUI.getWorkbench().close();
                    return false;
                }

                // Executors.newSingleThreadExecutor().submit(() -> UsageInfoCollector
                // .collect(UsageInfoCollector.getActivatedUsageInfo(UsageActionTrigger.OPEN_APPLICATION,
                // RunningMode.GUI)));
                // sendEventForTracking();
                try {
                    Trackings.trackOpenApplication(false, "gui");
                } catch (Exception ignored) {

                }

                return true;
            }

            private void scheduleCollectingStatistics() {
                int trackingTime = TrackingManager.getInstance().getTrackingTime();
                Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
                    Trackings.trackProjectStatistics(ProjectController.getInstance().getCurrentProject(),
                            !ActivationInfoCollector.isActivated(), "gui");
                }, trackingTime, trackingTime, TimeUnit.SECONDS);
            }
        });
    }
}
