package com.kms.katalon.composer.report.addons;

import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.report.handlers.DeleteReportCollectionHandler;
import com.kms.katalon.composer.report.handlers.DeleteReportHandler;
import com.kms.katalon.composer.report.handlers.EvaluateIntegrationContributionViewHandler;
import com.kms.katalon.composer.report.handlers.OpenReportCollectionHandler;
import com.kms.katalon.composer.report.handlers.OpenReportHandler;
import com.kms.katalon.composer.report.handlers.RefreshReportHandler;
import com.kms.katalon.composer.report.handlers.RenameReportHandler;
import com.kms.katalon.composer.report.integration.ReportComposerIntegrationFactory;
import com.kms.katalon.composer.report.platform.ExportReportProviderPlugin;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.reporting.ExportReportProvider;
import com.kms.katalon.custom.factory.CustomKeywordPluginFactory;
import com.kms.katalon.custom.keyword.CustomKeywordPlugin;

public class ReportInjectionManagerAddon {

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(DeleteReportHandler.class, context);
        ContextInjectionFactory.make(DeleteReportCollectionHandler.class, context);
        ContextInjectionFactory.make(OpenReportHandler.class, context);
        ContextInjectionFactory.make(OpenReportCollectionHandler.class, context);
        ContextInjectionFactory.make(RefreshReportHandler.class, context);
        ContextInjectionFactory.make(EvaluateIntegrationContributionViewHandler.class, context);
        ContextInjectionFactory.make(RenameReportHandler.class, context);

        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                ReportComposerIntegrationFactory.getInstance().onProjectChanged();
                List<CustomKeywordPlugin> plugins = CustomKeywordPluginFactory.getInstance().getPlugins();
                for (CustomKeywordPlugin plugin : plugins) {
                    if (plugin.getKeywordsManifest() != null && plugin.getKeywordsManifest().getReport() != null
                            && plugin.getKeywordsManifest().getReport().getExportProviderClassName() != null) {
                        loadExportReportProvider(plugin,
                                plugin.getKeywordsManifest().getReport().getExportProviderClassName());
                    }
                }
            }
        });
    }

    private void loadExportReportProvider(CustomKeywordPlugin plugin, String exportReportProviderClassName) {
        try {
            URLClassLoader classLoader = ProjectController.getInstance()
                    .getProjectClassLoader(ProjectController.getInstance().getCurrentProject());
            Class<?> exportProviderClass = classLoader.loadClass(ExportReportProvider.class.getName());
            Class<?> clazz = classLoader.loadClass(exportReportProviderClassName);

            if (!exportProviderClass.isAssignableFrom(clazz)) {
                LoggerSingleton.logInfo(MessageFormat.format(
                        "Class {0} is not an instance of ExportReportProvider. Please check again.",
                        exportReportProviderClassName));
                return;
            }
            ReportComposerIntegrationFactory.getInstance()
                    .addExportReportProvider(new ExportReportProviderPlugin(plugin, clazz.newInstance()));
        } catch (MalformedURLException | CoreException | ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            LoggerSingleton.logError(e);
        }
    }
}
