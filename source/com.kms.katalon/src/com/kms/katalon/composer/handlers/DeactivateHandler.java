package com.kms.katalon.composer.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.MachineUtil;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.core.application.ApplicationStaupHandler;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.CryptoUtil;

public class DeactivateHandler {
    @Execute
    public void execute() {
        try {
            ActivationInfoCollector.setActivated(false);
            ActivationInfoCollector.clearFeatures();

            IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
            eventBroker.send(EventConstants.PROJECT_CLOSE, null);

            Shell shell = Display.getCurrent().getActiveShell();
            new ProgressMonitorDialog(shell).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(StringConstants.MSG_DEACTIVATE, IProgressMonitor.UNKNOWN);
                    try {
                        String username = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
                        String encryptedPassword = ApplicationInfo
                                .getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
                        String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
                        String machineId = MachineUtil.getMachineId();
                        ActivationInfoCollector.deactivate(username, password, machineId);
                    } catch (Exception e) {
                        LogUtil.logError(e);
                    }
                }
            });

            if (ApplicationStaupHandler.checkActivation(false)) {
                eventBroker.post(EventConstants.ACTIVATION_CHECKED, null);
            }
        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }
}
