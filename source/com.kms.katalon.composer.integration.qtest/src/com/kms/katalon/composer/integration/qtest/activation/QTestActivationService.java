package com.kms.katalon.composer.integration.qtest.activation;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.activation.ActivationService;
import com.kms.katalon.composer.integration.qtest.activation.dialog.QTestActivationDialog;
import com.kms.katalon.integration.qtest.helper.QTestActivationHelper;

public class QTestActivationService implements ActivationService {

    @Override
    public boolean checkActivation(Shell activateShell) {
        switch (QTestActivationHelper.checkActivationStatus()) {
            case EXPIRED: {
                QTestActivationDialog dialog = new QTestActivationDialog(activateShell, true);
                return dialog.open() == QTestActivationDialog.OK;
            }
            case VALIDATED:
                return true;
            default: {
                QTestActivationDialog dialog = new QTestActivationDialog(activateShell, false);
                return dialog.open() == QTestActivationDialog.OK;
            }
        }
    }
}
