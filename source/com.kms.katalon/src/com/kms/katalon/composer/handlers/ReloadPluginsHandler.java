package com.kms.katalon.composer.handlers;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.plugin.dialog.ReloadPluginsHelpDialog;
import com.kms.katalon.plugin.dialog.ReloadPluginsResultDialog;
import com.kms.katalon.plugin.models.KStoreBasicCredentials;
import com.kms.katalon.plugin.models.KStoreClientAuthException;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.Plugin;
import com.kms.katalon.plugin.models.ReloadItem;
import com.kms.katalon.plugin.service.PluginService;

public class ReloadPluginsHandler extends RequireAuthorizationHandler {

    private static final long DIALOG_CLOSED_DELAY_MILLIS = 500L;

    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute() {
        reloadPlugins(false);
    }

    public void reloadPlugins(boolean silenceMode) {
    	ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
    	if (currentProject == null) {
    		openHelpDialog();
    		return;
    	}
    	
        List<ReloadItem>[] resultHolder = new List[1];
        Job reloadPluginsJob = new Job("Reloading plugins...") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    KStoreBasicCredentials[] credentials = new KStoreBasicCredentials[1];
                    UISynchronizeService.syncExec(() -> {
                        try {
                            credentials[0] = getBasicCredentials();
                        } catch (KStoreClientAuthException e) {
                            LoggerSingleton.logError(e);
                        }
                    });
                    if (credentials[0] != null) {
                        LoggerSingleton.logInfo("Credentials found. Reloading plugins.");
                        resultHolder[0] = PluginService.getInstance().reloadPlugins(credentials[0], monitor);
                        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.RELOAD_PLUGINS, null);
                    } else {
                        LoggerSingleton.logError("Credentials not found.");
                        return Status.CANCEL_STATUS;
                    }
                    LoggerSingleton.logInfo("Reloading plugins.");
                    resultHolder[0] = PluginService.getInstance().reloadPlugins(credentials[0], monitor);
                } catch (InterruptedException e) {
                    return Status.CANCEL_STATUS;
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    return new Status(Status.ERROR, "com.kms.katalon", "Error reloading plugins",
                            new Exception(ExceptionsUtil.getStackTraceForThrowable(e)));
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
                

                List<ReloadItem> results = resultHolder[0];

                if (silenceMode && !checkExpire(results)) {
                    return;
                }

                Executors.newSingleThreadExecutor().submit(() -> {
                    try {
                        // wait for Reloading Plugins dialog to close
                        TimeUnit.MILLISECONDS.sleep(DIALOG_CLOSED_DELAY_MILLIS);
                    } catch (InterruptedException ignored) {}
                    UISynchronizeService.syncExec(() -> {
                        openResultDialog(resultHolder[0]);
                    });
                });
            }
        });

        reloadPluginsJob.setUser(true);
        reloadPluginsJob.schedule();
    }

    private boolean checkExpire(List<ReloadItem> reloadItems) {
        return reloadItems.stream().filter(i -> {
            Plugin plugin = i.getPlugin();
            if (plugin.isOnline()) {
                KStorePlugin onlinePlugin = plugin.getOnlinePlugin();
                return onlinePlugin.isExpired() || (onlinePlugin.isTrial() && onlinePlugin.getRemainingDay() <= 14);
            }
            return false;
        }).findAny().isPresent();
    }

    private void openResultDialog(List<ReloadItem> result) {
        ReloadPluginsResultDialog dialog = new ReloadPluginsResultDialog(Display.getCurrent().getActiveShell(), result);
        dialog.open();
    }

    private void openHelpDialog() {
        ReloadPluginsHelpDialog dialog = new ReloadPluginsHelpDialog(Display.getCurrent().getActiveShell());
        dialog.open();
    }
}
