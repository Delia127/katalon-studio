package com.kms.katalon.plugin.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.kms.katalon.plugin.models.KatalonStoreToken;
import com.kms.katalon.plugin.store.PluginPreferenceStore;

public class KStoreTokenService {
    private static final int TOKEN_VALID_HOURS = 23;
    
    private static KStoreTokenService instance;
    
    private PluginPreferenceStore store;
    
    public static KStoreTokenService getInstance() {
        if (instance == null) {
            instance = new KStoreTokenService();
        }
        return instance;
    }
    
    private KStoreTokenService() {
        store = new PluginPreferenceStore();
    }
    
    public  KatalonStoreToken createNewToken(String tokenString) throws IOException, GeneralSecurityException {
        KatalonStoreToken token = new KatalonStoreToken();
        Date currentDate = new Date();
        token.setToken(tokenString);
        token.setExpirationDate(DateUtils.addHours(currentDate, TOKEN_VALID_HOURS));
        store.setToken(token);
        return token;
    }

    public KatalonStoreToken getToken() throws GeneralSecurityException, IOException {
        return store.getToken();
    }
    
    public boolean isTokenExpired(KatalonStoreToken token) {
        Date currentDate = new Date();
        return currentDate.after(token.getExpirationDate());
    }
}
