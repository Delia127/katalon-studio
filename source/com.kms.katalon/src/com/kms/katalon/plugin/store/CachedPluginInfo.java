package com.kms.katalon.plugin.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.util.PluginHelper;

public class CachedPluginInfo {

    private static final String PLUGINS = "plugins";

    private static final String PLUGIN_LOCATIONS = "pluginLocations";

    public static List<KStorePlugin> getInstalledPlugins() throws IOException {
        String pluginListJson = getPropertyValue(PLUGINS);
        if (StringUtils.isBlank(pluginListJson)) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<KStorePlugin>>() {
        }.getType();
        List<KStorePlugin> plugins = JsonUtil.fromJson(pluginListJson, listType);
        return plugins;
    }

    public static void setInstalledPlugins(List<KStorePlugin> plugins) throws IOException {
        String pluginListJson = JsonUtil.toJson(plugins);
        setPropertyValue(PLUGINS, pluginListJson);
    }

    public static String getPluginLocation(KStorePlugin plugin) throws IOException {
        Map<String, String> locationMap = getPluginLocationMap();
        String key = PluginHelper.idAndVersionKey(plugin);
        return locationMap.get(key);
    }

    public static void setPluginLocation(KStorePlugin plugin, String location) throws IOException {
        Map<String, String> locationMap = getPluginLocationMap();
        String key = PluginHelper.idAndVersionKey(plugin);
        locationMap.put(key, location);
        setPropertyValue(PLUGIN_LOCATIONS, JsonUtil.toJson(locationMap));
    }

    private static Map<String, String> getPluginLocationMap() throws IOException {
        String locationMapJson = getPropertyValue(PLUGIN_LOCATIONS);
        if (StringUtils.isEmpty(locationMapJson)) {
            return new HashMap<>();
        }
        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> locationMap = JsonUtil.fromJson(locationMapJson, mapType);
        return locationMap;
    }

    private static void setPropertyValue(String propertyKey, String propertyValue) throws IOException {
        Properties prop = loadPreferenceProperties();
        prop.setProperty(propertyKey, propertyValue);
        savePreferenceProperties(prop);
    }

    private static String getPropertyValue(String propertyKey) throws IOException {
        Properties prop = loadPreferenceProperties();
        String value = prop.getProperty(propertyKey);
        return value;
    }

    private static Properties loadPreferenceProperties() throws IOException {
        Properties prop = new Properties();
        File preferenceFile = getPreferenceFile();
        if (preferenceFile.exists()) {
            FileInputStream input = new FileInputStream(preferenceFile);
            prop.load(new FileInputStream(preferenceFile));
            input.close();
        }
        return prop;
    }

    private static void savePreferenceProperties(Properties prop) throws IOException {
        File preferenceFile = getPreferenceFile();
        if (!preferenceFile.exists()) {
            preferenceFile.createNewFile();
        }
        FileOutputStream output = new FileOutputStream(preferenceFile);
        prop.store(output, null);
        output.close();
    }

    private static File getPreferenceFile() {
        File preferenceFile = new File(GlobalStringConstants.APP_USER_DIR_LOCATION, "plugin/preferences.properties");
        return preferenceFile;
    }
}
