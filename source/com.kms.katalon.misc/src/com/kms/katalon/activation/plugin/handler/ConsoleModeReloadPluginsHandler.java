package com.kms.katalon.activation.plugin.handler;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.kms.katalon.activation.plugin.constant.ActivationMessageConstants;
import com.kms.katalon.activation.plugin.models.KStoreApiKeyCredentials;
import com.kms.katalon.activation.plugin.models.ReloadItem;
import com.kms.katalon.activation.plugin.service.PluginService;
import com.kms.katalon.logging.LogUtil;

public class ConsoleModeReloadPluginsHandler {

    public void reload(String apiKey) {
        try {
            LogUtil.printOutputLine("Start reloading plugins...");
            
            KStoreApiKeyCredentials credentials = new KStoreApiKeyCredentials();
            credentials.setApiKey(apiKey);
            List<ReloadItem> reloadPluginResults = PluginService.getInstance().reloadPlugins(credentials,
                    new NullProgressMonitor());
            reloadPluginResults.stream().forEach(result -> {
                String pluginName = result.getPlugin().getName();
                if (result.isPluginInstalled()) {
                    LogUtil.printOutputLine(
                            MessageFormat.format(ActivationMessageConstants.MSG_PLUGIN_HAS_BEEN_INSTALLED, pluginName));
                } else {
                    LogUtil.printOutputLine(
                            MessageFormat.format(ActivationMessageConstants.MSG_PLUGIN_HAS_BEEN_UNINSTALLED, pluginName));
                }
            });
        } catch (Exception e) {
            LogUtil.printAndLogError(e, "Failed to reload plugins.");
        }
    }
}
