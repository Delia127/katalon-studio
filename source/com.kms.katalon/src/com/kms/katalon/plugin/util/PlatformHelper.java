package com.kms.katalon.plugin.util;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import com.katalon.platform.internal.api.PluginInstaller;
import com.kms.katalon.plugin.models.KStorePlugin;

@SuppressWarnings("restriction")
public class PlatformHelper {
    
    public static Bundle installPlugin(KStorePlugin plugin) throws BundleException {
        BundleContext bundleContext = InternalPlatform.getDefault().getBundleContext();
        String bundlePath = plugin.getFile().toURI().toString();
        Bundle existingBundle = bundleContext.getBundle(bundlePath);
        if (existingBundle == null) {
            PluginInstaller pluginInstaller = getPluginInstaller();
            Bundle bundle = pluginInstaller.installPlugin(bundleContext, bundlePath);
            return bundle;
        } else {
            return existingBundle;
        }
    }
    
    public static Bundle uninstallPlugin(KStorePlugin plugin) throws BundleException {
        BundleContext bundleContext = InternalPlatform.getDefault().getBundleContext();
        String bundlePath = plugin.getFile().toURI().toString();
        Bundle existingBundle = bundleContext.getBundle(bundlePath);
        if (existingBundle != null) {
            PluginInstaller pluginInstaller = getPluginInstaller();
            Bundle bundle = pluginInstaller.uninstallPlugin(bundleContext, bundlePath);
            return bundle;
        } else {
            return existingBundle;
        }
    }
    
    private static PluginInstaller getPluginInstaller() {
        BundleContext context = InternalPlatform.getDefault().getBundleContext();
        PluginInstaller pluginInstaller = context.getService(context.getServiceReference(PluginInstaller.class));
        return pluginInstaller;
    }
        
}
