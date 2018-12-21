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
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.plugin.dialog.ReloadPluginsResultDialog;
import com.kms.katalon.plugin.models.KStoreAccount;
import com.kms.katalon.plugin.models.KStoreClientAuthException;
import com.kms.katalon.plugin.models.ResultItem;
import com.kms.katalon.plugin.service.PluginService;

public class ReloadPluginsHandler extends RequireAuthorizationHandler {

    @Inject
    private IEventBroker eventBroker;

    private boolean isReloading = false;

    @PostConstruct
    public void registerEventListener() {
        //auto reload on startup
        eventBroker.subscribe(EventConstants.ACTIVATION_CHECKED, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                reloadPlugins(true);
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
                    KStoreAccount[] accounts = new KStoreAccount[1];
                    UISynchronizeService.syncExec(() -> {
                        try {
                            accounts[0] = getAccount();
                        } catch (KStoreClientAuthException e) {
                            LoggerSingleton.logError(e);
                        }
                    });
                    if (accounts[0] != null) {
                        List<ResultItem> result = PluginService.getInstance().reloadPlugins(accounts[0], monitor);
                        if (!silenceMode) {
                            UISynchronizeService.syncExec(() -> openResultDialog(result));
                        }
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
        ReloadPluginsResultDialog dialog = new ReloadPluginsResultDialog(Display.getCurrent().getActiveShell(), result);
        dialog.open();
    }
}
