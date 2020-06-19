package com.kms.katalon.activation.plugin.util;

import com.kms.katalon.activation.plugin.models.KStorePlugin;
import com.kms.katalon.activation.plugin.models.KStoreProductType;

public class PluginHelper {

    public static String idAndVersionKey(KStorePlugin plugin) {
        long pluginId = plugin.getId();
        String version = plugin.getLatestCompatibleVersion().getNumber();
        return pluginId + "-" + version;
    }
    
    public static boolean isCustomKeywordPlugin(KStorePlugin plugin) {
        return plugin.getProduct().getProductType().getName().equalsIgnoreCase(KStoreProductType.CUSTOM_KEYWORD);
    }
}
