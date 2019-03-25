package com.kms.katalon.composer.execution;

import java.lang.reflect.Field;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.spi.RegistryContributor;
import org.osgi.framework.BundleContext;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.launcher.provider.IDELauncherProviderImpl;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.execution.launcher.provider.IDELauncherProvider;

@SuppressWarnings("restriction")
public class ExecutionComposerPlugin extends Plugin {

    private static final String MASTER_TOKEN_FIELD = "masterToken";

    private static final String DEBUG_MODEL_PRESENTATIONS = "org.eclipse.debug.ui.debugModelPresentations";

    public void start(BundleContext context) throws Exception {
        super.start(context);

        context.registerService(IDELauncherProvider.class, new IDELauncherProviderImpl(),
                null);

        activeDebugExtension();
    }

    private void activeDebugExtension() {
        try {
            IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
            IConfigurationElement[] configsForDebugView = extensionRegistry.getConfigurationElementsFor(DEBUG_MODEL_PRESENTATIONS);
            for (IConfigurationElement configurationElementForDebugView : configsForDebugView) {
                RegistryContributor debugViewContributor = (RegistryContributor) configurationElementForDebugView.getContributor();
                if (IdConstants.COMPOSER_EXECUTION_BUNDLE_ID.equals(debugViewContributor.getName())) {
                    continue;
                }
                extensionRegistry.removeExtension(configurationElementForDebugView.getDeclaringExtension(),
                        getMasterToken(extensionRegistry));
            }
        } catch (ReflectiveOperationException e) {
            LoggerSingleton.logError(e);
        }
    }

    /**
     * See <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=295035">
     * https://bugs.eclipse.org/bugs/show_bug.cgi?id=295035</a>
     * 
     * @param extensionRegistry
     * @return value of <code>masterToken</code> field of {@link ExtensionRegistry}
     * @throws ReflectiveOperationException
     */
    private Object getMasterToken(IExtensionRegistry extensionRegistry) throws ReflectiveOperationException {
        Field field = ExtensionRegistry.class.getDeclaredField(MASTER_TOKEN_FIELD);
        field.setAccessible(true);
        return field.get(extensionRegistry);
    }
}
