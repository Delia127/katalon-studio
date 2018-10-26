package com.kms.katalon.composer.integration.qtest.activation.dialog;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.about.dialog.KatalonAboutDialog;
import com.kms.katalon.composer.integration.qtest.constant.ComposerIntegrationQtestMessageConstants;
import com.kms.katalon.integration.qtest.helper.ActivationPayload;

public class QTestEditionAboutDialog extends KatalonAboutDialog {

    private ActivationPayload activationPayload;

    public QTestEditionAboutDialog(Shell parentShell, ActivationPayload activationPayload) {
        super(parentShell);
        this.activationPayload = activationPayload;
    }

    @Override
    protected String updateVersionInfo(String aboutText) {
        
        LocalDateTime localDateTime = LocalDateTime
                .ofInstant(Instant.ofEpochMilli(activationPayload.getExp().getTime()), ZoneId.systemDefault());
        String nextAboutText = aboutText.replace(VERSION_UPDATE,
                String.format("%s %s\n\n%s", ComposerIntegrationQtestMessageConstants.DIA_LBL_EXPIRATION_DATE,
                        localDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                        VERSION_UPDATE));
        
        return super.updateVersionInfo(nextAboutText);
    }
}
