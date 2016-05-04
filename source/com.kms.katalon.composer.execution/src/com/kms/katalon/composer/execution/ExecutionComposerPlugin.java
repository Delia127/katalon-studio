package com.kms.katalon.composer.execution;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.spi.RegistryContributor;
import org.osgi.framework.BundleContext;

import com.kms.katalon.constants.IdConstants;

@SuppressWarnings("restriction")
public class ExecutionComposerPlugin extends Plugin {
    
    private static final String DEBUG_MODEL_PRESENTATIONS = "org.eclipse.debug.ui.debugModelPresentations";

    public void start(BundleContext context) throws Exception {
        super.start(context);
        
        activeDebugExtension();
    }
    
    private void activeDebugExtension() {
        IConfigurationElement[] configsForDebugView = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(DEBUG_MODEL_PRESENTATIONS);
        for (IConfigurationElement configurationElementForDebugView : configsForDebugView) {
            RegistryContributor debugViewContributor = (RegistryContributor) configurationElementForDebugView.getContributor();
            if (!IdConstants.COMPOSER_EXECUTION_BUNDLE_ID.equals(debugViewContributor.getName())) {
                ((ExtensionRegistry) Platform.getExtensionRegistry()).remove(debugViewContributor.getId());
            }
        }
    }
}
