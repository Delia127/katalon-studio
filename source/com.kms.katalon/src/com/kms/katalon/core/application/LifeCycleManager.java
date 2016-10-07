package com.kms.katalon.core.application;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
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
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.handlers.CloseHandler;
import com.kms.katalon.composer.handlers.QuitHandler;
import com.kms.katalon.composer.handlers.ResetPerspectiveHandler;
import com.kms.katalon.composer.handlers.SaveHandler;
import com.kms.katalon.composer.handlers.SearchHandler;
import com.kms.katalon.composer.handlers.WorkbenchSaveHandler;
import com.kms.katalon.composer.initializer.CommandBindingInitializer;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.usagetracking.UsageInfoCollector;
import com.kms.katalon.util.ActivationInfoCollector;
import com.kms.katalon.util.VersionInfo;
import com.kms.katalon.util.VersionUtil;

@SuppressWarnings("restriction")
public class LifeCycleManager {
    private static final String DF_TEXT_FONT = "Courier New";

    private static final String PREF_FIST_TIME_SETUP_COMPLETED = "firstTimeSetupCompleted";

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
                EventBrokerSingleton.getInstance()
                        .getEventBroker()
                        .post(EventConstants.ECLIPSE_EDITOR_CLOSED, partRef.getPart(true));
            }

            @Override
            public void partBroughtToTop(IWorkbenchPartReference partRef) {
                if (NewSearchUI.SEARCH_VIEW_ID.equals(partRef.getId())) {
                    SearchHandler.openSearchView();
                }
            }

            @Override
            public void partActivated(IWorkbenchPartReference partRef) {
            }
        });

        new CommandBindingInitializer().setup();
        new CommandBindingRemover().setup();
    }

    private void setupPreferences() {
        setupResourcePlugin();
        setupWorkbenchPlugin();
    }

    private void setupWorkbenchPlugin() {
        ScopedPreferenceStore store = PreferenceStoreManager.getPreferenceStore(IdConstants.WORKBENCH_WINDOW_ID);

        if (store.getBoolean(PREF_FIST_TIME_SETUP_COMPLETED)) {
            return;
        }

        int fontSize = (Platform.OS_MACOSX.equals(Platform.getOS())) ? 15 : 11;
        FontData defaultFont = FontDescriptor.createFrom(DF_TEXT_FONT, fontSize, SWT.NORMAL).getFontData()[0];
        store.setValue(JFaceResources.TEXT_FONT, defaultFont.toString());
        store.setValue(PREF_FIST_TIME_SETUP_COMPLETED, true);

        try {
            store.save();
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
                    if (isInternalBuild()) {
                        return;
                    }
                    if (!(ActivationInfoCollector.checkActivation())) {
                        eventBroker.send(EventConstants.PROJECT_CLOSE, null);
                        PlatformUI.getWorkbench().close();
                        return;
                    }
                    alertNewVersion();
                    startCollectUsageInfo();

                } catch (Exception e) {
                    logError(e);
                }
            }
        });
    }

    private boolean isInternalBuild() {
        VersionInfo version = VersionUtil.getCurrentVersion();
        return VersionInfo.MINIMUM_VERSION.equals(version.getVersion()) || version.getBuildNumber() == 0;
    }

    private void alertNewVersion() {
        IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
        boolean checkNewVersion = prefStore.contains(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION)
                ? prefStore.getBoolean(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION) : true;
        if (!checkNewVersion) {
            return;
        }
        Executors.newSingleThreadExecutor().submit(new Runnable() {

            @Override
            public void run() {
                if (!VersionUtil.hasNewVersion()) {
                    return;
                }
                Display.getDefault().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        boolean wantDownload = MessageDialog.openConfirm(null,
                                MessageConstants.DIA_UPDATE_NEW_VERSION_TITLE,
                                MessageConstants.DIA_UPDATE_NEW_VERSION_MESSAGE);
                        if (wantDownload) {
                            VersionUtil.gotoDownloadPage();
                        }
                    }

                });
            }
        });
    }

    private void startCollectUsageInfo() {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                UsageInfoCollector.colllect();
            }
        });
    }

}
