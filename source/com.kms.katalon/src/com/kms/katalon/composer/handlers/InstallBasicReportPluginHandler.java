package com.kms.katalon.composer.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.custom.factory.CustomKeywordPluginFactory;
import com.kms.katalon.custom.keyword.CustomKeywordPlugin;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.license.models.LicenseType;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.Plugin;
import com.kms.katalon.plugin.service.LocalRepository;
import com.kms.katalon.plugin.util.PluginFactory;

public class InstallBasicReportPluginHandler {

    @PostConstruct
    private void registerEventHandler() {
        EventBrokerSingleton.getInstance().getEventBroker().subscribe(EventConstants.WORKSPACE_PLUGIN_LOADED,
                new EventHandler() {
                    @Override
                    public void handleEvent(Event event) {
                        installIfNotAvailable();
                    }
                });
    }

    public void installIfNotAvailable() {
        if (!isBasicReportPluginInstalled()) {
            Job job = new Job("Installing basic report plugins...") {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        KStorePlugin plugin = getPlugin();

                        CustomKeywordPlugin customKeywordPlugin = new CustomKeywordPlugin();
                        customKeywordPlugin.setId(Long.toString(plugin.getId()));
                        customKeywordPlugin.setPluginFile(plugin.getFile());
                        CustomKeywordPluginFactory.getInstance().addPluginFile(plugin.getFile(), customKeywordPlugin);
                        
                        Plugin resolvedPlugin = new Plugin();
                        resolvedPlugin.setName(plugin.getProduct().getName());
                        resolvedPlugin.setOnline(false);
                        resolvedPlugin.setFile(plugin.getFile());
                        
                        PluginFactory.getInstance().addPlugin(resolvedPlugin);

                        refreshProjectClasspath(monitor);

                        if (ApplicationRunningMode.get() == RunningMode.GUI) {
                            EventBrokerSingleton.getInstance().getEventBroker()
                                    .post(EventConstants.BASIC_REPORT_PLUGIN_INSTALLED, null);
                        }
                    } catch (Exception e) {
                        LogUtil.logError(e);
                        return new Status(Status.ERROR, "com.kms.katalon", "Error installing basic report plugins",
                                new Exception(ExceptionsUtil.getStackTraceForThrowable(e)));
                    }
                    return Status.OK_STATUS;
                }
            };

            job.setUser(false);
            job.schedule();
        }
    }

    private KStorePlugin getPlugin() throws IOException {
        Bundle bundle = FrameworkUtil.getBundle(LocalRepository.class);
        Path pluginFolderPath = new Path("/resources/basic-report");
        URL pluginFolderUrl = FileLocator.find(bundle, pluginFolderPath, null);
        File pluginFolder = FileUtils.toFile(FileLocator.toFileURL(pluginFolderUrl));

        File pluginDescriptionFile = FileUtils.getFile(pluginFolder, "description.json");
        String descriptionContent = FileUtils.readFileToString(pluginDescriptionFile);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        KStorePlugin plugin = gson.fromJson(descriptionContent, KStorePlugin.class);

        File pluginFile = FileUtils.getFile(pluginFolder, "katalon-studio-report-plugin.jar");
        plugin.setFile(pluginFile);
        return plugin;
    }

    private void refreshProjectClasspath(IProgressMonitor monitor) throws Exception {
        ProjectController projectController = ProjectController.getInstance();
        ProjectEntity currentProject = projectController.getCurrentProject();
        if (currentProject != null) {
            boolean allowSourceAttachment = LicenseType.valueOf(
                    ApplicationInfo.getAppProperty(ApplicationStringConstants.LICENSE_TYPE)) != LicenseType.FREE;
            GroovyUtil.initGroovyProjectClassPath(currentProject,
                    projectController.getCustomKeywordPlugins(currentProject), false, allowSourceAttachment, monitor);
            projectController.updateProjectClassLoader(currentProject);
            KeywordController.getInstance().parseAllCustomKeywords(currentProject, null);
            if (ApplicationRunningMode.get() == RunningMode.GUI) {
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.KEYWORD_BROWSER_REFRESH, null);
            }
        }
    }

    private boolean isBasicReportPluginInstalled() {
        List<Plugin> plugins = PluginFactory.getInstance().getPlugins();
        return plugins.stream().filter(p -> p.getName().equals("Basic Report")).findFirst().isPresent();
    }
}
