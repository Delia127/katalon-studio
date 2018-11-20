package com.kms.katalon.platform.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.osgi.framework.Bundle;

import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.extension.Extension;
import com.katalon.platform.api.extension.ExtensionListener;
import com.katalon.platform.api.extension.ExtensionPoint;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.platform.ExtensionImpl;
import com.kms.katalon.platform.ExtensionManagerImpl;
import com.kms.katalon.platform.ExtensionPointImpl;
import com.kms.katalon.platform.KatalonPluginImpl;

public class PluginManifestParsingUtil {

    public static Plugin parsePlugin(Bundle bundle, IExtensionRegistry extensionRegistry) {
        ExtensionManagerImpl extensionManager = (ExtensionManagerImpl) ApplicationManager.getInstance()
                .getExtensionManager();
        String symbolicName = bundle.getSymbolicName();
        KatalonPluginImpl pluginImpl = new KatalonPluginImpl(symbolicName);

        IExtension[] extensions = extensionRegistry.getExtensions(symbolicName);
        for (IExtension e : extensions) {
            if (e.getExtensionPointUniqueIdentifier().equals("com.katalon.platform.extensions")) {
                try {
                    String pluginId = e.getNamespaceIdentifier();
                    String extensionId = e.getConfigurationElements()[0].getAttribute("extensionId");
                    String extensionPointId = e.getConfigurationElements()[0].getAttribute("extensionPointId");
                    Object implementationClass = e.getConfigurationElements()[0]
                            .createExecutableExtension("implementationClass");

                    Extension newExtension = new ExtensionImpl(pluginId, extensionId, extensionPointId,
                            implementationClass);

                    pluginImpl.addExtension(newExtension);

                    extensionManager.addExtension(extensionPointId, newExtension);
                } catch (InvalidRegistryObjectException | CoreException ex) {
                    ex.printStackTrace(System.err);
                }
            }

            if (e.getExtensionPointUniqueIdentifier().equals("com.katalon.platform.extensions_point")) {
                try {
                    String pluginId = e.getNamespaceIdentifier();
                    String extensionPointId = e.getConfigurationElements()[0].getAttribute("extensionPointId");
                    String interfaceClassName = e.getConfigurationElements()[0].getAttribute("interfaceClassName");

                    ExtensionListener serviceClass = null;
                    if (e.getConfigurationElements()[0].getAttribute("serviceClass") != null) {
                        serviceClass = (ExtensionListener) e.getConfigurationElements()[0]
                                .createExecutableExtension("serviceClass");
                    }

                    ExtensionPoint newExtensionPoint = new ExtensionPointImpl(pluginId, extensionPointId,
                            interfaceClassName, serviceClass);

                    pluginImpl.addExtensionPoint(newExtensionPoint);

                    extensionManager.addExtensionPoint(extensionPointId, newExtensionPoint);
                } catch (InvalidRegistryObjectException | CoreException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
        return pluginImpl;
    }
}
