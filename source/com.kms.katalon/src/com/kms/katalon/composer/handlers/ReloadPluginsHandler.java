package com.kms.katalon.composer.handlers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.plugin.dialog.KStorePluginsDialog;
import com.kms.katalon.plugin.models.KStoreUsernamePasswordCredentials;
import com.kms.katalon.plugin.models.KStoreClientAuthException;
import com.kms.katalon.plugin.models.ResultItem;
import com.kms.katalon.plugin.service.PluginService;
import com.kms.katalon.plugin.store.PluginPreferenceStore;

public class ReloadPluginsHandler extends RequireAuthorizationHandler {

    @Inject
    private IEventBroker eventBroker;

    private boolean isReloading = false;

    private PluginPreferenceStore store;
    
    
    @PostConstruct
    public void registerEventListener() {
        store = new PluginPreferenceStore();
        //auto reload on startup
        eventBroker.subscribe(EventConstants.ACTIVATION_CHECKED, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                if (store.hasReloadedPluginsBefore()) {
                    reloadPlugins(true);
                }
            }
        });
    }

    @CanExecute
    public boolean canExecute() {
        if (isReloading) {
            return false;
        } else {
            return true;
        }
    }

    @Execute
    public void execute() {
        reloadPlugins(false);
    }

    private void reloadPlugins(boolean silenceMode) {
        Job reloadPluginsJob = new Job("Reloading plugins...") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    KStoreUsernamePasswordCredentials[] credentials = new KStoreUsernamePasswordCredentials[1];
//                    UISynchronizeService.syncExec(() -> {
//                        try {
//                            credentials[0] = getUsernamePasswordCredentials();
//                        } catch (KStoreClientAuthException e) {
//                            LoggerSingleton.logError(e);
//                        }
//                    });
//                    if (credentials[0] != null) {
//                        if (!silenceMode) {
//                            UISynchronizeService.syncExec(() -> openResultDialog(result));
//                        }
//                        
//                        if (!store.hasReloadedPluginsBefore()) {
//                            store.markFirstTimeReloadPlugins();
//                        }
//                    }
                    credentials[0] = new KStoreUsernamePasswordCredentials();
                    credentials[0].setUsername("abc");
                    credentials[0].setPassword("123");
                    List<ResultItem> result = PluginService.getInstance().reloadPlugins(credentials[0], monitor);
                    if (!silenceMode) {
                        UISynchronizeService.syncExec(() -> openResultDialog(result));
                    }
                    
                    if (!store.hasReloadedPluginsBefore()) {
                        store.markFirstTimeReloadPlugins();
                    }
                } catch (InterruptedException e) {
                    return Status.CANCEL_STATUS;
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    return new Status(Status.ERROR, "com.kms.katalon",
                            "Error reloading plugins", e);
                }
                return Status.OK_STATUS;
            }
        };

        reloadPluginsJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void aboutToRun(IJobChangeEvent event) {
                isReloading = true;
            }

            @Override
            public void done(IJobChangeEvent event) {
                isReloading = false;
                event.getJob().removeJobChangeListener(this);
            }
        });

        if (silenceMode) {
            reloadPluginsJob.setUser(false);
        } else {
            reloadPluginsJob.setUser(true);
        }
        reloadPluginsJob.schedule();
    }

    private void openResultDialog(List<ResultItem> result) {
        if (result.size() > 0) {
            KStorePluginsDialog dialog = new KStorePluginsDialog(Display.getCurrent().getActiveShell(), result);
            dialog.open();
        } else {
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), StringConstants.INFO,
                    StringConstants.HAND_INFO_NO_PLUGINS_FOUND);
        }
    }
}
