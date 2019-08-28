package com.kms.katalon.composer.handlers;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.plugin.dialog.KStoreLoginDialog;
import com.kms.katalon.plugin.models.KStoreBasicCredentials;
import com.kms.katalon.plugin.models.KStoreClientAuthException;
import com.kms.katalon.plugin.util.KStoreTokenService;
import com.kms.katalon.util.CryptoUtil;

public abstract class RequireAuthorizationHandler {

    public KStoreBasicCredentials getBasicCredentials() throws KStoreClientAuthException {
        try {
            String username = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
            String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
            if (StringUtils.isBlank(username) || StringUtils.isBlank(encryptedPassword)) {
                Shell shell = Display.getCurrent().getActiveShell();
                KStoreLoginDialog dialog = new KStoreLoginDialog(shell);
                if (dialog.open() == Dialog.OK) {
                    username = dialog.getUsername();
                    String password = dialog.getPassword();
                    
                    ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_EMAIL, username, true);
                    encryptedPassword = CryptoUtil.encode(CryptoUtil.getDefault(password));
                    ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_PASSWORD, encryptedPassword, true);

                    String token = dialog.getToken();
                    KStoreTokenService.getInstance().createNewToken(token);
                }
            }
            String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
            KStoreBasicCredentials credentials = new KStoreBasicCredentials();
            credentials.setUsername(username);
            credentials.setPassword(password);
            return credentials;
        } catch (IOException | GeneralSecurityException e) {
            throw new KStoreClientAuthException(e);
        }
    }
}
