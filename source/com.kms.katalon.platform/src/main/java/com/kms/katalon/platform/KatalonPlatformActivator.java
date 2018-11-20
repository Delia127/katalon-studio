package com.kms.katalon.platform;

import java.util.Hashtable;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import com.katalon.platform.api.Application;
import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.platform.util.PluginManifestParsingUtil;

public class KatalonPlatformActivator {

    public static void boostrapPlatform(IEclipseContext eclipseContext, BundleContext bundleContext) throws BundleException {
        ApplicationServiceImpl.lookupEclipseContext(eclipseContext);


        Bundle bundle = Platform.getBundle("com.katalon.platform");
        bundle.start();
        
        bundleContext.registerService("com.katalon.platform.api.Application", new ApplicationImpl(), new Hashtable<>());

        bundleContext.registerService(IEventBroker.class, eclipseContext.get(IEventBroker.class), new Hashtable<>());
        bundleContext.registerService(EModelService.class, eclipseContext.get(EModelService.class), new Hashtable<>());
        bundleContext.registerService(ECommandService.class, eclipseContext.get(ECommandService.class), new Hashtable<>());
        bundleContext.registerService(MApplication.class, eclipseContext.get(MApplication.class), new Hashtable<>());
        

        Plugin platformPlugin = PluginManifestParsingUtil.parsePlugin(bundle,
                Platform.getExtensionRegistry());

        PluginManagerImpl pluginManager = (PluginManagerImpl) ApplicationManager.getInstance().getPluginManager();
        pluginManager.addPlugin(platformPlugin);

        ExtensionManagerImpl extensionManager = (ExtensionManagerImpl) ApplicationManager.getInstance().getExtensionManager();

        // Register all extensions of this plugin to other plugins
        extensionManager.registerExtensions(platformPlugin);

        // Register all extensions of other plugins to this plugin
        extensionManager.registerExtensionsPoint(platformPlugin);
    }

    public static Bundle activatePlugin(IEclipseContext eclipseContext, BundleContext bundleContext, String location)
            throws BundleException {
        Bundle bundle = bundleContext.installBundle(location);
        bundle.start();

        Plugin userPlugin =  PluginManifestParsingUtil.parsePlugin(Platform.getBundle(bundle.getSymbolicName()),
                Platform.getExtensionRegistry());
        
        PluginManagerImpl pluginManager = (PluginManagerImpl) ApplicationManager.getInstance().getPluginManager();
        pluginManager.addPlugin(userPlugin);

        ExtensionManagerImpl extensionManager = (ExtensionManagerImpl) ApplicationManager.getInstance().getExtensionManager();

        // Register all extensions of this plugin to other plugins
        extensionManager.registerExtensions(userPlugin);

        // Register all extensions of other plugins to this plugin
        extensionManager.registerExtensionsPoint(userPlugin);

        return bundle;
    }

    public static void disablePlugin(IEclipseContext eclipseContext, BundleContext context, String location) throws BundleException {
        Bundle bundle = context.getBundle(location);
        if (bundle == null) {
            return;
        }

        String bundleName = bundle.getSymbolicName();

        Application application = ApplicationManager.getInstance();
        Plugin userPlugin = application.getPluginManager().getPlugin(bundleName);
        ExtensionManagerImpl extensionManager = (ExtensionManagerImpl) application.getExtensionManager();

        // De-register all extensions that is contributing to this plugin.
        extensionManager.deregisterExtensionsPoint(userPlugin);
        userPlugin.extensionsPoint().stream().forEach(p -> extensionManager.removeExtensionPoint(p.extensionPointId()));

        // De-register all extensions of this plugin from other plugins.
        extensionManager.deregisterExtensions(userPlugin);
        userPlugin.extensions().forEach(e -> extensionManager.removeExtension(e));

        PluginManagerImpl pluginManager = (PluginManagerImpl) application.getPluginManager();
        pluginManager.removePlugin(userPlugin);

        bundle.stop();
        bundle.uninstall();
    }
}
