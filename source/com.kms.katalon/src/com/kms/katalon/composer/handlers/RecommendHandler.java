package com.kms.katalon.composer.handlers;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.plugin.models.KStoreClientAuthException;
import com.kms.katalon.plugin.models.KStoreUsernamePasswordCredentials;
import com.kms.katalon.plugin.models.ReloadItem;
import com.kms.katalon.plugin.service.PluginService;
import com.kms.katalon.plugin.store.PluginPreferenceStore;

public class RecommendHandler extends RequireAuthorizationHandler{
    @Inject
    private IEventBroker eventBroker;
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.PROJECT_OPEN_LATEST, new EventServiceAdapter() {

            @Override
            public void handleEvent(Event event) {
                reloadPlugins(true);
            }
        });
    }
        public void reloadPlugins(boolean silenceMode) {
            PluginPreferenceStore store = new PluginPreferenceStore();
            List<ReloadItem>[] resultHolder = new List[1];
            Job reloadPluginsJob = new Job("Reloading plugins...") {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        KStoreUsernamePasswordCredentials[] credentials = new KStoreUsernamePasswordCredentials[1];
                        UISynchronizeService.syncExec(() -> {
                            try {
                                credentials[0] = getUsernamePasswordCredentials();
                            } catch (KStoreClientAuthException e) {
                                LoggerSingleton.logError(e);
                            }
                        });
                        if (credentials[0] != null) {
                            LoggerSingleton.logInfo("Credentials found. Reloading plugins.");
                            resultHolder[0] = PluginService.getInstance().reloadRecommendPlugins(credentials[0], monitor);
                            if (!store.hasReloadedPluginsBefore()) {
                                store.markFirstTimeReloadPlugins();
                            }
                        } else {
                            LoggerSingleton.logError("Credentials not found.");
                            return Status.CANCEL_STATUS;
                        }
                    } catch (InterruptedException e) {
                        return Status.CANCEL_STATUS;
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                        return new Status(Status.ERROR, "com.kms.katalon", "Error reloading plugins", e);
                    }
                    LoggerSingleton.logInfo("Reloaded plugins successfully.");
                    return Status.OK_STATUS;
                }
            };

            reloadPluginsJob.addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.WORKSPACE_PLUGIN_LOADED, null);

                    if (!reloadPluginsJob.getResult().isOK()) {
                        LoggerSingleton.logError("Failed to reload plugins.");
                        return;
                    }

                }
            });

            reloadPluginsJob.setUser(true);
            reloadPluginsJob.schedule();
        }

}
