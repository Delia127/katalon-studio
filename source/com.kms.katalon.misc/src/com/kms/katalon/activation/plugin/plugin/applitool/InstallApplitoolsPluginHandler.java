package com.kms.katalon.activation.plugin.plugin.applitool;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.activation.plugin.models.Plugin;
import com.kms.katalon.activation.plugin.util.PluginFactory;
import com.kms.katalon.custom.factory.CustomKeywordPluginFactory;
import com.kms.katalon.custom.keyword.CustomKeywordPlugin;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.KSEFeature;

public class InstallApplitoolsPluginHandler {
    
    private static final String APPLITOOLS_CUSTOM_KEYWORD_ID = Long.toString(44);
    private static final String APPLITOOLS_PLUGIN_NAME = "Applitools Integration";
    
    private IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();

    public void doinstallApplitoolsPlugin() throws IOException {
        if (featureService.canUse(KSEFeature.APPLITOOLS_PLUGIN)) {
            CustomKeywordPlugin customKeywordPlugin = new CustomKeywordPlugin();
            customKeywordPlugin.setId(APPLITOOLS_CUSTOM_KEYWORD_ID);
            File pluginFile = getPluginFile();
            customKeywordPlugin.setPluginFile(pluginFile);
            CustomKeywordPluginFactory.getInstance().addPluginFile(pluginFile, customKeywordPlugin);
    
            Plugin resolvedPlugin = new Plugin();
            resolvedPlugin.setName(APPLITOOLS_PLUGIN_NAME);
            resolvedPlugin.setOnline(false);
            resolvedPlugin.setFile(pluginFile);
    
            CustomKeywordPluginFactory.getInstance().addPluginFile(pluginFile, customKeywordPlugin);
            PluginFactory.getInstance().addPlugin(resolvedPlugin);
        }
    }

    private File getPluginFile() throws IOException {
        Bundle bundle = FrameworkUtil.getBundle(InstallApplitoolsPluginHandler.class);
        Path pluginFolderPath = new Path("/resources/applitools");
        URL pluginFolderUrl = FileLocator.find(bundle, pluginFolderPath, null);
        File pluginFolder = FileUtils.toFile(FileLocator.toFileURL(pluginFolderUrl));
        File pluginFile = FileUtils.getFile(pluginFolder, "katalon-studio-applitools-plugin.jar");
        return pluginFile;
    }
}
