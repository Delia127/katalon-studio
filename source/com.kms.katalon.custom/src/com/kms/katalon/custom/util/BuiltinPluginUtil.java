package com.kms.katalon.custom.util;

import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.constants.IdConstants;

public class BuiltinPluginUtil {
    public static boolean isTestNGPluginInstalled() {
        Plugin plugin = ApplicationManager.getInstance().getPluginManager().getPlugin(IdConstants.TESTNG_PLUGIN_ID);
        return plugin != null;
    }
}
