package com.kms.katalon.composer.integration.kobiton.execution.handlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.dialogs.MessageDialogWithLink;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.composer.integration.kobiton.constants.ComposerIntegrationKobitonMessageConstants;
import com.kms.katalon.composer.integration.kobiton.constants.ComposerKobitonStringConstants;
import com.kms.katalon.composer.integration.kobiton.dialog.KobitonAuthenticationDialog;
import com.kms.katalon.composer.integration.kobiton.dialog.KobitonDeviceDialog;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.integration.kobiton.configuration.KobitonRunConfiguration;
import com.kms.katalon.integration.kobiton.entity.KobitonApiKey;
import com.kms.katalon.integration.kobiton.entity.KobitonDevice;
import com.kms.katalon.integration.kobiton.exceptions.KobitonApiException;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;
import com.kms.katalon.integration.kobiton.providers.KobitonApiProvider;

public class KobitonExecutionHandler extends AbstractExecutionHandler {
    @Override
    protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException,
            ExecutionException, InterruptedException {
        KobitonRunConfiguration kobitonRunConfiguration = new KobitonRunConfiguration(projectDir);
        String kobitonUserName = getKobitonUserName();
        if (kobitonUserName == null) {
            return null;
        }
        kobitonRunConfiguration.setUserName(kobitonUserName);
        String kobitonApiKey = getKobitonApiKey();
        if (kobitonApiKey == null) {
            return null;
        }
        kobitonRunConfiguration.setApiKey(kobitonApiKey);
        KobitonDevice kobitonDevice = getKobitonDevice();
        if (kobitonDevice == null) {
            return null;
        }
        kobitonRunConfiguration.setKobitonDevice(kobitonDevice);
        return kobitonRunConfiguration;
    }

    private String getKobitonApiKey() {
        final String kobitonApiKey = KobitonPreferencesProvider.getKobitonApiKey();
        if (StringUtils.isNotEmpty(kobitonApiKey)) {
            return kobitonApiKey;
        }
        final List<KobitonApiKey> apiKeys = new ArrayList<>();
        try {
            new ProgressMonitorDialog(getActiveShell()).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(ComposerIntegrationKobitonMessageConstants.MSG_DLG_PRG_RETRIEVING_KEYS, 1);
                        apiKeys.addAll(KobitonApiProvider.getApiKeyList(KobitonPreferencesProvider.getKobitonToken()));
                        monitor.worked(1);
                    } catch (URISyntaxException | IOException | KobitonApiException e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof KobitonApiException) {
                MessageDialog.openError(getActiveShell(), ComposerKobitonStringConstants.ERROR, cause.getMessage());
                return null;
            }
            LoggerSingleton.logError(cause);
        } catch (InterruptedException e) {
            // ignore this
        }
        if (apiKeys.isEmpty()) {
            MessageDialogWithLink.openWarning(getActiveShell(), ComposerKobitonStringConstants.WARN,
                    ComposerIntegrationKobitonMessageConstants.KobitonPreferencesPage_WARN_MSG_NO_API_KEY);
            return null;
        }
        String apiKey = apiKeys.get(0).getKey();
        KobitonPreferencesProvider.saveKobitonApiKey(apiKey);
        return apiKey;
    }

    private String getKobitonUserName() {
        String savedUserName = KobitonPreferencesProvider.getKobitonUserName();
        String token = KobitonPreferencesProvider.getKobitonToken();
        if (StringUtils.isEmpty(savedUserName) || StringUtils.isEmpty(token)) {
            KobitonAuthenticationDialog authenticateDialog = new KobitonAuthenticationDialog(getActiveShell());
            if (authenticateDialog.open() != Window.OK) {
                return null;
            }
            savedUserName = KobitonPreferencesProvider.getKobitonUserName();
        }
        return savedUserName;
    }

    private Shell getActiveShell() {
        return Display.getCurrent().getActiveShell();
    }

    private KobitonDevice getKobitonDevice() {
        KobitonDeviceDialog deviceDialog = new KobitonDeviceDialog(getActiveShell());
        if (deviceDialog.open() == Window.OK) {
            return deviceDialog.getSelectedDevice();
        }
        return null;
    }
}
