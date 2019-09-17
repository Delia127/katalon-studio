package com.kms.katalon.plugin.store;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.plugin.models.KStoreBasicCredentials;
import com.kms.katalon.plugin.models.KatalonStoreToken;
import com.kms.katalon.util.CryptoUtil;

public class PluginPreferenceStore {
    
    public KStoreBasicCredentials getKStoreBasicCredentials() throws GeneralSecurityException, IOException {
        String username = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
        String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
        String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
        KStoreBasicCredentials credentials = new KStoreBasicCredentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        return credentials;
    }
    
    public KatalonStoreToken getToken() throws GeneralSecurityException, IOException {
        String encryptedToken = ApplicationInfo.getAppProperty(ApplicationStringConstants.STORE_TOKEN);
        if (encryptedToken == null) {
            return null;
        }
        String tokenJson = CryptoUtil.decode(CryptoUtil.getDefault(encryptedToken));
        if (StringUtils.isBlank(tokenJson)) {
            return null;
        }
        return JsonUtil.fromJson(tokenJson, KatalonStoreToken.class);
    }
    
    public void setToken(KatalonStoreToken token) throws IOException, GeneralSecurityException {
        String encryptedToken = CryptoUtil.encode(CryptoUtil.getDefault(JsonUtil.toJson(token)));
        ApplicationInfo.setAppProperty(ApplicationStringConstants.STORE_TOKEN, encryptedToken, true);
    }
}
