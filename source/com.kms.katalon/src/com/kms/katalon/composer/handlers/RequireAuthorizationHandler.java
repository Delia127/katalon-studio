package com.kms.katalon.composer.handlers;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.plugin.dialog.KStoreLoginDialog;
import com.kms.katalon.plugin.models.KStoreClientAuthException;
import com.kms.katalon.plugin.models.KStoreAccount;
import com.kms.katalon.plugin.store.PluginPreferenceStore;
import com.kms.katalon.plugin.util.KStoreTokenService;

public abstract class RequireAuthorizationHandler {

    private static PluginPreferenceStore store = new PluginPreferenceStore();

    public KStoreAccount getAccount() throws KStoreClientAuthException {
        try {
            KStoreAccount account = store.getKStoreAccount();
            if (account == null) {
                Shell shell = Display.getCurrent().getActiveShell();
                KStoreLoginDialog dialog = new KStoreLoginDialog(shell);
                if (dialog.open() == Dialog.OK) {
                    account = new KStoreAccount();
                    String username = dialog.getUsername();
                    String password = dialog.getPassword();
                    account.setUsername(username);
                    account.setPassword(password);
                    store.setKStoreAccount(account);

                    String token = dialog.getToken();
                    KStoreTokenService.getInstance().createNewToken(token);
                }
            }
            return account;
        } catch (IOException | GeneralSecurityException e) {
            throw new KStoreClientAuthException(e);
        }
    }
}
