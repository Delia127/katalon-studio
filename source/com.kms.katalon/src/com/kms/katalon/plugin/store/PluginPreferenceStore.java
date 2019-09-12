package com.kms.katalon.plugin.store;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.KatalonStoreToken;
import com.kms.katalon.plugin.models.KStoreBasicCredentials;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.util.CryptoUtil;

public class PluginPreferenceStore {
    
    private static final String KATALON_STORE_CREDENTIALS = "kStoreCredentials";
    
    private static final String KATALON_STORE_TOKEN = "kStoreToken";
    
    private static final String RELOAD_PLUGINS_BEFORE = "reloadPluginsBefore";
    
    private static final ScopedPreferenceStore store = PreferenceStoreManager.getPreferenceStore(
            PluginPreferenceStore.class);
    
    public List<KStorePlugin> getInstalledPlugins() throws IOException {
        return CachedPluginInfo.getInstalledPlugins();
    }
    
    public void setInstalledPlugins(List<KStorePlugin> plugins) throws IOException {
        CachedPluginInfo.setInstalledPlugins(plugins);
    }
    
    public String getPluginLocation(KStorePlugin plugin) throws IOException {
        return CachedPluginInfo.getPluginLocation(plugin);
    }
    
    public void setPluginLocation(KStorePlugin plugin, String location) throws IOException {
        CachedPluginInfo.setPluginLocation(plugin, location);
    }
    
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
    
    public boolean hasReloadedPluginsBefore() {
        return store.getBoolean(RELOAD_PLUGINS_BEFORE);
    }
    
    public void markFirstTimeReloadPlugins() {
        store.setValue(RELOAD_PLUGINS_BEFORE, true);
    }
}
