package com.kms.katalon.plugin.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.kms.katalon.plugin.models.KStoreCredentials;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.OfflinePlugin;
import com.kms.katalon.plugin.models.Plugin;
import com.kms.katalon.plugin.models.ResolutionException;
import com.kms.katalon.plugin.models.ResolutionItem;

public class PluginResolver {

    private static PluginResolver instance;

    private PluginResolver() {
    };

    public static PluginResolver getInstance() {
        if (instance == null) {
            instance = new PluginResolver();
        }
        return instance;
    }

    public List<ResolutionItem> resolveOnlinePlugins(List<KStorePlugin> onlinePlugins, KStoreCredentials credentials,
            IProgressMonitor monitor) throws ResolutionException {
        return LocalRepository.getInstance().resolvePlugins(onlinePlugins, credentials, monitor);
    }

    public List<ResolutionItem> resolveOfflinePlugins(List<OfflinePlugin> offlinePlugins,
            IProgressMonitor progressMonitor) {
        SubMonitor monitor = SubMonitor.convert(progressMonitor);
        monitor.beginTask("", 100);

        List<ResolutionItem> resolutionItems = new ArrayList<>();

        int totalWork = offlinePlugins.size();
        int progress = 0;
        for (OfflinePlugin plugin : offlinePlugins) {
            Plugin resolvedPlugin = new Plugin();
            resolvedPlugin.setName(plugin.getName());
            resolvedPlugin.setFile(plugin.getFile());
            resolvedPlugin.setCustomKeywordPlugin(plugin.isCustomKeywordPlugin());
            
            ResolutionItem resolutionItem = new ResolutionItem();
            resolutionItem.setPlugin(resolvedPlugin);
            resolutionItems.add(resolutionItem);

            progress++;
            int subWork = Math.round((float) progress * 100 / totalWork);
            monitor.worked(subWork);
        }

        return resolutionItems;
    }
}
