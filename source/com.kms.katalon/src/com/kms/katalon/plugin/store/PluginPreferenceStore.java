package com.kms.katalon.plugin.store;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.KStoreToken;
import com.kms.katalon.plugin.models.KStoreUsernamePasswordCredentials;
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
    
    public KStoreUsernamePasswordCredentials getKStoreUsernamePasswordCredentials() throws GeneralSecurityException, IOException {
        String encryptedCredentialsJson = store.getString(KATALON_STORE_CREDENTIALS);
        if (encryptedCredentialsJson == null) {
            return null;
        }
        String credentialsJson = CryptoUtil.decode(CryptoUtil.getDefault(encryptedCredentialsJson));
        KStoreUsernamePasswordCredentials credentials = JsonUtil.fromJson(credentialsJson, KStoreUsernamePasswordCredentials.class);
        return credentials;
    }
    
    public void setKStoreUsernamePasswordCredentials(KStoreUsernamePasswordCredentials credentials) throws GeneralSecurityException, IOException {
        String credentialsJson = JsonUtil.toJson(credentials);
        String encryptedCredentialsJson = CryptoUtil.encode(CryptoUtil.getDefault(credentialsJson));
        store.setValue(KATALON_STORE_CREDENTIALS, encryptedCredentialsJson);
        store.save();
    }
    
    public KStoreToken getToken() {
        String tokenJson = store.getString(KATALON_STORE_TOKEN);
        if (StringUtils.isBlank(tokenJson)) {
            return null;
        }
        return JsonUtil.fromJson(tokenJson, KStoreToken.class);
    }
    
    public void setToken(KStoreToken token) throws IOException {
        store.setValue(KATALON_STORE_TOKEN, JsonUtil.toJson(token));
        store.save();
    }
    
    public boolean hasReloadedPluginsBefore() {
        return store.getBoolean(RELOAD_PLUGINS_BEFORE);
    }
    
    public void markFirstTimeReloadPlugins() {
        store.setValue(RELOAD_PLUGINS_BEFORE, true);
    }
}
