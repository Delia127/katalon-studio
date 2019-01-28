package com.kms.katalon.plugin.store;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.plugin.models.KStoreUsernamePasswordCredentials;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.KStoreToken;
import com.kms.katalon.plugin.util.PluginHelper;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.util.CryptoUtil;

public class PluginPreferenceStore {
    
    private static final String PLUGINS = "plugins";
    
    private static final String PLUGIN_LOCATIONS = "pluginLocations";
    
    private static final String KATALON_STORE_CREDENTIALS = "kStoreCredentials";
    
    private static final String KATALON_STORE_TOKEN = "kStoreToken";
    
    private static final String RELOAD_PLUGINS_BEFORE = "reloadPluginsBefore";
    
    private static final ScopedPreferenceStore store = PreferenceStoreManager.getPreferenceStore(
            PluginPreferenceStore.class);
    
    public List<KStorePlugin> getInstalledPlugins() {
        String pluginListJson = store.getString(PLUGINS);
        if (StringUtils.isBlank(pluginListJson)) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<KStorePlugin>>() {}.getType();
        List<KStorePlugin> plugins = JsonUtil.fromJson(pluginListJson, listType);
        return plugins;
    }
    
    public void setInstalledPlugins(List<KStorePlugin> plugins) throws IOException {
        String pluginListJson = JsonUtil.toJson(plugins);
        store.setValue(PLUGINS, pluginListJson);
        store.save();
    }
    
    public List<String> getPluginLocations() {
        Map<String, String> locationMap = getPluginLocationMap();
        return new ArrayList<>(locationMap.values());
    }
    
    public String getPluginLocation(KStorePlugin plugin) {
        Map<String, String> locationMap = getPluginLocationMap();
        String key = PluginHelper.idAndVersionKey(plugin);
        return locationMap.get(key);
    }
    
    public void removePluginLocation(KStorePlugin plugin) throws IOException {
        Map<String, String> locationMap = getPluginLocationMap();
        String key = PluginHelper.idAndVersionKey(plugin);
        locationMap.remove(key);
        store.setValue(PLUGIN_LOCATIONS, JsonUtil.toJson(locationMap));
        store.save();
    }
    
    public void setPluginLocation(KStorePlugin plugin, String location) throws IOException {
        Map<String, String> locationMap = getPluginLocationMap();
        String key = PluginHelper.idAndVersionKey(plugin);
        locationMap.put(key, location);
        store.setValue(PLUGIN_LOCATIONS, JsonUtil.toJson(locationMap));
        store.save();
    }
    
    private Map<String, String> getPluginLocationMap() {
        String locationMapJson = store.getString(PLUGIN_LOCATIONS);
        if (StringUtils.isEmpty(locationMapJson)) {
            return new HashMap<>();
        }
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> locationMap = JsonUtil.fromJson(locationMapJson, mapType);
        return locationMap;
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
