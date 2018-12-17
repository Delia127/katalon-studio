package com.kms.katalon.composer.integration.qtest.activation;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.activation.ActivationService;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.activation.dialog.QTestActivationDialog;
import com.kms.katalon.composer.integration.qtest.activation.dialog.QTestActivationSuccessDialog;
import com.kms.katalon.composer.integration.qtest.activation.dialog.QTestEditionAboutDialog;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.integration.qtest.helper.QTestActivationHelper;

public class QTestActivationService implements ActivationService {

    @Override
    public boolean checkActivation(Shell activateShell) {
        switch (QTestActivationHelper.checkActivationStatus()) {
            case EXPIRED: {
                QTestActivationDialog dialog = new QTestActivationDialog(activateShell, true);
                if (dialog.open() != QTestActivationDialog.OK) {
                    return false;
                }
                try {
                    QTestActivationSuccessDialog successDialog = new QTestActivationSuccessDialog(activateShell,
                            QTestActivationHelper.getActivationPayload());

                    successDialog.open();
                } catch (IOException | GeneralSecurityException e) {
                    LoggerSingleton.logError(e);
                }
                return true;
            }
            case VALIDATED:
                return true;
            default: {
                QTestActivationDialog dialog = new QTestActivationDialog(activateShell, false);
                if (dialog.open() != QTestActivationDialog.OK) {
                    return false;
                }
                try {
                    QTestActivationSuccessDialog successDialog = new QTestActivationSuccessDialog(activateShell,
                            QTestActivationHelper.getActivationPayload());

                    successDialog.open();
                } catch (IOException | GeneralSecurityException e) {
                    LoggerSingleton.logError(e);
                }
                return true;
            }
        }
    }

    @Override
    public void openAboutDialog(Shell activeShell) {
        try {
            QTestEditionAboutDialog aboutDialog = new QTestEditionAboutDialog(activeShell,
                    QTestActivationHelper.getActivationPayload());
            aboutDialog.open();
        } catch (IOException | GeneralSecurityException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog("Unable to open About Dialog", e.getMessage(),
                    ExceptionsUtil.getMessageForThrowable(e));
        }
    }
}
