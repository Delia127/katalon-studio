package com.kms.katalon.composer.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.katalon.platform.internal.api.PluginInstaller;
import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
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
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.plugin.models.Plugin;
import com.kms.katalon.plugin.service.LocalRepository;
import com.kms.katalon.plugin.util.PluginFactory;

public class InstallApplitoolsPluginHandler {
    private static final String APPLITOOLS_CUSTOM_KEYWORD_ID = Long.toString(44);
    private static final String APPLITOOLS_PLUGIN_NAME = "Applitools Integration";
    private static final String APPLITOOLS_ID = "Applitools";
    private static final String APPLITOOLS_PREFERENCE_ID = "com.kms.katalon.keyword.Applitools-Keywords";
    private IEclipseContext context;

    @Inject
    public InstallApplitoolsPluginHandler(IEclipseContext context) {
        this.context = context;
    }
    
    @Inject
    private PluginInstaller pluginInstaller;

    @PostConstruct
    private void registerEventHandler() {
        EventBrokerSingleton.getInstance().getEventBroker().subscribe(EventConstants.WORKSPACE_PLUGIN_LOADED,
                new EventHandler() {
                    @Override
                    public void handleEvent(Event event) {
                        installOfflineApplitoolsPluginIfNotAvailable();
                    }
                });
    }

    public void installOfflineApplitoolsPluginIfNotAvailable() {
        // Do not change logic for free version
        if (LicenseUtil.isNotFreeLicense()) {
            doUninstallApplitoolsPluginFromStore();
            if (!isAnyApplitoolsPluginInstalled()) {
                Job job = new Job("Installing Applitools plugins...") {
                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        try {
                            doInstallApplitoolsPlugin(monitor);
                            if (ApplicationRunningMode.get() == RunningMode.GUI) {
                                uninstallApplitoolsPluginToolItem();
                                installApplitoolsPluginToolItem();
                                EventBrokerSingleton.getInstance()
                                        .getEventBroker()
                                        .post(EventConstants.APPLITOOLS_PLUGIN_INSTALLED, null);
                            }
                        } catch (Exception e) {
                            LogUtil.logError(e);
                            return new Status(Status.ERROR, "com.kms.katalon", "Error installing Applitools plugin",
                                    new Exception(ExceptionsUtil.getStackTraceForThrowable(e)));
                        }
                        return Status.OK_STATUS;
                    }
                };

                job.setUser(false);
                job.schedule();
            }
        }
    }

    private void doInstallApplitoolsPlugin(IProgressMonitor monitor) throws IOException, Exception {
        CustomKeywordPlugin customKeywordPlugin = new CustomKeywordPlugin();
        customKeywordPlugin.setId(APPLITOOLS_CUSTOM_KEYWORD_ID);
        customKeywordPlugin.setPluginFile(getPluginFile());
        CustomKeywordPluginFactory.getInstance().addPluginFile(getPluginFile(), customKeywordPlugin);

        Plugin resolvedPlugin = new Plugin();
        resolvedPlugin.setName(APPLITOOLS_PLUGIN_NAME);
        resolvedPlugin.setOnline(false);
        resolvedPlugin.setFile(getPluginFile());

        PluginFactory.getInstance().addPlugin(resolvedPlugin);

        refreshProjectClasspath(monitor);
    }

    private void doUninstallApplitoolsPluginFromStore() {
        PluginFactory.getInstance()
                .getPlugins()
                .stream()
                .filter(plugin -> plugin.isOnline() && APPLITOOLS_PLUGIN_NAME.equals(plugin.getName()))
                .findAny()
                .ifPresent(onlineApplitoolsPlugin -> {
                    CustomKeywordPlugin customKeywordPlugin = new CustomKeywordPlugin();
                    customKeywordPlugin.setId(onlineApplitoolsPlugin.getFile().getAbsolutePath());
                    File pluginFile = onlineApplitoolsPlugin.getFile();
                    customKeywordPlugin.setPluginFile(pluginFile);
                    CustomKeywordPluginFactory.getInstance().removePluginFile(pluginFile, customKeywordPlugin);
                    // Remove it from PluginFactory to maintain consistency
                    PluginFactory.getInstance().removePluginByName(APPLITOOLS_PLUGIN_NAME);
                });
    }

    private File getPluginFile() throws IOException {
        Bundle bundle = FrameworkUtil.getBundle(LocalRepository.class);
        Path pluginFolderPath = new Path("/resources/applitools");
        URL pluginFolderUrl = FileLocator.find(bundle, pluginFolderPath, null);
        File pluginFolder = FileUtils.toFile(FileLocator.toFileURL(pluginFolderUrl));
        File pluginFile = FileUtils.getFile(pluginFolder, "katalon-studio-applitools-plugin.jar");
        return pluginFile;
    }

    private void refreshProjectClasspath(IProgressMonitor monitor) throws Exception {
        ProjectController projectController = ProjectController.getInstance();
        ProjectEntity currentProject = projectController.getCurrentProject();
        if (currentProject != null) {
            boolean allowSourceAttachment = LicenseUtil.isNotFreeLicense();
            GroovyUtil.initGroovyProjectClassPath(currentProject,
                    projectController.getCustomKeywordPlugins(currentProject), false, allowSourceAttachment, monitor);
            projectController.updateProjectClassLoader(currentProject);
            KeywordController.getInstance().parseAllCustomKeywords(currentProject, null);
            if (ApplicationRunningMode.get() == RunningMode.GUI) {
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.KEYWORD_BROWSER_REFRESH, null);
            }
        }
    }

    private boolean isAnyApplitoolsPluginInstalled() {
        List<CustomKeywordPlugin> plugins = CustomKeywordPluginFactory.getInstance().getPlugins();
        return plugins.stream().filter(p -> p.getId().equals(APPLITOOLS_ID)).findFirst().isPresent();
    }

    @SuppressWarnings("restriction")
    public void uninstallApplitoolsPluginToolItem() {
        EModelService modelService = context.get(EModelService.class);
        ECommandService commandService = context.get(ECommandService.class);
        MApplication application = context.get(MApplication.class);
        if (modelService == null || commandService == null || application == null) {
            return;
        }
        MUIElement element = modelService.find(APPLITOOLS_ID, application);
        if (element != null) {
            Command command = commandService.getCommand(APPLITOOLS_ID);
            command.setHandler(null);

            UISynchronizeService.syncExec(() -> {
                MElementContainer<MUIElement> parent = element.getParent();
                element.setToBeRendered(false);
                element.setVisible(false);
                parent.getChildren().remove(element);
            });
        }
    }

    @SuppressWarnings("restriction")
    public void installApplitoolsPluginToolItem() {
        EModelService modelService = context.get(EModelService.class);
        ECommandService commandService = context.get(ECommandService.class);
        MApplication application = context.get(MApplication.class);

        if (modelService == null || commandService == null || application == null) {
            return;
        }
        MUIElement groupElement = modelService.find("com.kms.katalon.composer.toolbar", application);

        if (!(groupElement instanceof MElementContainer)) {
            return;
        }

        Category category = commandService.defineCategory("empty", "", "");
        Command command = commandService.defineCommand(APPLITOOLS_ID, "", "", category, new IParameter[0]);
        command.setHandler(new AbstractHandler() {
            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public Object execute(ExecutionEvent event) throws ExecutionException {
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.PROJECT_SETTINGS_PAGE,
                        APPLITOOLS_CUSTOM_KEYWORD_ID);
                return null;
            }
        });

        ParameterizedCommand parameterizedCommand = new ParameterizedCommand(command, new Parameterization[0]);

        MElementContainer<?> container = (MElementContainer<?>) groupElement;

        if (!(container instanceof MToolBar)) {
            return;
        }

        MToolBar toolbar = (MToolBar) container;
        MHandledToolItem toolItem = MMenuFactory.INSTANCE.createHandledToolItem();
        toolItem.setLabel(APPLITOOLS_ID);
        toolItem.setWbCommand(parameterizedCommand);
        toolItem.setCommand(MCommandsFactory.INSTANCE.createCommand());
        toolItem.setIconURI(FileLocator.find(FrameworkUtil.getBundle(InstallApplitoolsPluginHandler.class),
                new Path("/resources/icons/applitools_active_32x24.png"), null).toString());
        toolItem.setElementId(APPLITOOLS_ID);

        UISynchronizeService.syncExec(() -> {
            toolbar.getChildren().add(toolItem);
        });
    }
}
