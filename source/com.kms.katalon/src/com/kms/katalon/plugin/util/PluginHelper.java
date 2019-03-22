package com.kms.katalon.plugin.util;

import com.kms.katalon.plugin.models.KStorePlugin;

public class PluginHelper {

    public static String idAndVersionKey(KStorePlugin plugin) {
        long pluginId = plugin.getId();
        String version = plugin.getLatestCompatibleVersion().getNumber();
        return pluginId + "-" + version;
    }
}
