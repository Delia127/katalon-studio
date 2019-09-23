package com.kms.katalon.plugin.util;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import com.katalon.platform.internal.api.PluginInstaller;
import com.kms.katalon.plugin.models.Plugin;

@SuppressWarnings("restriction")
public class PlatformHelper {
    
    private static boolean isComposerArtifactBundleInstalled = false;
    
    public static Bundle installPlugin(Plugin plugin) throws BundleException {
        BundleContext bundleContext = InternalPlatform.getDefault().getBundleContext();
        String bundlePath = plugin.getFile().toURI().toString();
        PluginInstaller pluginInstaller = getPluginInstaller();
        Bundle bundle = pluginInstaller.installPlugin(bundleContext, bundlePath);
        return bundle;       
    }
    
    public static Bundle uninstallPlugin(Plugin plugin) throws BundleException {
        BundleContext bundleContext = InternalPlatform.getDefault().getBundleContext();
        String bundlePath = plugin.getFile().toURI().toString();
        PluginInstaller pluginInstaller = getPluginInstaller();
        Bundle bundle = pluginInstaller.uninstallPlugin(bundleContext, bundlePath);
        return bundle;
    }
    
    public synchronized static Bundle installComposerArtifactBundle() throws BundleException {
        Bundle bundle = Platform.getBundle("com.kms.katalon.composer.artifact");
        if (bundle != null && !isComposerArtifactBundleInstalled) {
            bundle.start();
            PluginInstaller pluginInstaller = getPluginInstaller();
            pluginInstaller.register(bundle);
            isComposerArtifactBundleInstalled = true;
        }
        return bundle;
    }
    
    private static PluginInstaller getPluginInstaller() {
        BundleContext context = InternalPlatform.getDefault().getBundleContext();
        PluginInstaller pluginInstaller = context.getService(context.getServiceReference(PluginInstaller.class));
        return pluginInstaller;
    }
}
