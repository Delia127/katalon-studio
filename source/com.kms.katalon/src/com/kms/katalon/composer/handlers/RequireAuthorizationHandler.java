package com.kms.katalon.composer.handlers;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.plugin.dialog.KStoreLoginDialog;
import com.kms.katalon.plugin.models.KStoreClientAuthException;
import com.kms.katalon.plugin.models.KStoreUsernamePasswordCredentials;
import com.kms.katalon.plugin.store.PluginPreferenceStore;
import com.kms.katalon.plugin.util.KStoreTokenService;

public abstract class RequireAuthorizationHandler {

    private static PluginPreferenceStore store = new PluginPreferenceStore();

    public KStoreUsernamePasswordCredentials getUsernamePasswordCredentials() throws KStoreClientAuthException {
        try {
            KStoreUsernamePasswordCredentials credentials = store.getKStoreUsernamePasswordCredentials();
            if (credentials == null) {
                Shell shell = Display.getCurrent().getActiveShell();
                KStoreLoginDialog dialog = new KStoreLoginDialog(shell);
                if (dialog.open() == Dialog.OK) {
                    credentials = new KStoreUsernamePasswordCredentials();
                    String username = dialog.getUsername();
                    String password = dialog.getPassword();
                    credentials.setUsername(username);
                    credentials.setPassword(password);
                    store.setKStoreUsernamePasswordCredentials(credentials);

                    String token = dialog.getToken();
                    KStoreTokenService.getInstance().createNewToken(token);
                }
            }
            return credentials;
        } catch (IOException | GeneralSecurityException e) {
            throw new KStoreClientAuthException(e);
        }
    }
}
