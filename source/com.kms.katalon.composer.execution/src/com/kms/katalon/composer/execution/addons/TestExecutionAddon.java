package com.kms.katalon.composer.execution.addons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.internal.runtime.AdapterManager;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.internal.ui.commands.actions.StepIntoCommandHandler;
import org.eclipse.debug.internal.ui.commands.actions.StepOverCommandHandler;
import org.eclipse.debug.internal.ui.commands.actions.StepReturnCommandHandler;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.sourcelookup.ISourceDisplay;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MDynamicMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.PartListenerAdapter;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.debug.CustomSourceLookupService;
import com.kms.katalon.composer.execution.debug.handler.ToggleBreakpointHandler;
import com.kms.katalon.composer.execution.handlers.EvaluateDriverConnectorEditorContributionsHandler;
import com.kms.katalon.composer.execution.handlers.ExistingExecutionHandler;
import com.kms.katalon.composer.execution.jobs.ExecuteTestCaseJob;
import com.kms.katalon.composer.execution.menu.CustomExecutionMenuContribution;
import com.kms.katalon.composer.execution.menu.ExecutionHandledMenuItem;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.model.ExecuteFromTestStepEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.collector.ConsoleOptionCollector;
import com.kms.katalon.execution.configuration.ExistingRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.session.ExecutionSessionSocketServer;

@SuppressWarnings("restriction")
public class TestExecutionAddon implements EventHandler {

    private static final String TOGGLE_BREAKPOINT_COMMAND = "org.eclipse.debug.ui.commands.ToggleBreakpoint";

    private static final String STEP_RETURN_COMMAND = "org.eclipse.debug.ui.commands.StepReturn";

    private static final String STEP_OVER_COMMAND = "org.eclipse.debug.ui.commands.StepOver";

    private static final String STEP_INTO_COMMAND = "org.eclipse.debug.ui.commands.StepInto";

    @Inject
    private IEclipseContext context;

    @Inject
    private MApplication application;

    @Inject
    private EModelService modelService;

    @Inject
    private UISynchronize sync;

    @Inject
    private EPartService partService;

    @PostConstruct
    public void initHandlers(IEventBroker eventBroker) {
        ContextInjectionFactory.make(EvaluateDriverConnectorEditorContributionsHandler.class, context);
        eventBroker.subscribe(EventConstants.WORKSPACE_CREATED, this);
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, this);
        initCustomRunConfigurationSubMenu(IdConstants.RUN_TOOL_ITEM_ID, StringConstants.CUSTOM_RUN_MENU_ID);
        initCustomRunConfigurationSubMenu(IdConstants.DEBUG_TOOL_ITEM_ID, StringConstants.CUSTOM_DEBUG_MENU_ID);
        startSessionServer();
        eventBroker.subscribe(EventConstants.EXECUTE_FROM_TEST_STEP, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (!(object instanceof ExecuteFromTestStepEntity)) {
                    return;
                }
                if (partService.saveAll(true) && partService.getDirtyParts().isEmpty()) {
                    executeTestCaseFromTestStep((ExecuteFromTestStepEntity) object);
                }
            }
        });
    }

    private void initCustomRunConfigurationSubMenu(String parentToolItemId, String elementId) {
        MToolItem parentToolItem = (MToolItem) modelService.find(parentToolItemId, application);
        MMenu menu = parentToolItem.getMenu();
        MMenu subMenu = MMenuFactory.INSTANCE.createMenu();
        subMenu.setLabel(StringConstants.CUSTOM_RUN_MENU_LABEL);
        subMenu.setElementId(elementId);

        MDynamicMenuContribution dynamicMenuContributor = MMenuFactory.INSTANCE.createDynamicMenuContribution();

        dynamicMenuContributor.setElementId(StringConstants.CUSTOM_RUN_CONFIG_CONTRIBUTOR_ID);
        dynamicMenuContributor.setContributionURI(StringConstants.KATALON_COMPOSER_EXECUTION_BUNDLE_URI
                + CustomExecutionMenuContribution.class.getName());
        subMenu.getChildren().add(dynamicMenuContributor);
        menu.getChildren().add(MMenuFactory.INSTANCE.createMenuSeparator());
        menu.getChildren().add(subMenu);
    }

    @Override
    public void handleEvent(Event event) {
        // init Debug context for workbench
        if (EventConstants.WORKSPACE_CREATED.equals(event.getTopic())) {
            activeDefaultIconForExecution();
            activeDebugHandlers();

            // Register new Part Image to prevent Eclipse replace its old image on Legacy Part
            registerDebugImages();
            registerPartListeners();

            // Register CustomSourceLookupService
            activeSourceLookupService();
        } else if (EventConstants.PROJECT_OPENED.equals(event.getTopic())) {
            writeDefaultConsolePropertyFile();
        }
    }

    private void activeSourceLookupService() {
        AdapterManager adapterManager = AdapterManager.getDefault();
        adapterManager.registerAdapters(new IAdapterFactory() {
            
            @Override
            public Class<?>[] getAdapterList() {
                return new Class[] { ISourceDisplay.class };
            }
            
            @SuppressWarnings("unchecked")
            @Override
            public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
                return (T) new CustomSourceLookupService(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
            }
        }, JDIStackFrame.class);
    }

    private void registerPartListeners() {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            return;
        }
        activeWorkbenchWindow
                .getPartService()
                .addPartListener(new PartListenerAdapter() {
                    @Override
                    public void partActivated(IWorkbenchPartReference partRef) {
                        if (IDebugUIConstants.ID_DEBUG_VIEW.equals(partRef.getId())) {
                            registerDebugImages();
                        }
                    }
                });
    }

    private void registerDebugImages() {
        modelService.find(IDebugUIConstants.ID_DEBUG_VIEW, application).getTransientData().put(
                IPresentationEngine.OVERRIDE_ICON_IMAGE_KEY, ImageManager.getImage(IImageKeys.DEBUG_PERSPECTIVE_16));
    }

    private void writeDefaultConsolePropertyFile() {
        try {
            ConsoleOptionCollector.getInstance()
                    .writeDefaultPropertyFile(ProjectController.getInstance().getCurrentProject());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void activeDefaultIconForExecution() {
        wrapExecutionMenuItemsProcessor(IdConstants.RUN_TOOL_ITEM_ID);
        wrapExecutionMenuItemsProcessor(IdConstants.DEBUG_TOOL_ITEM_ID);
    }

    private void activeDebugHandlers() {
        IWorkbenchWindow activeWorkbenchWindow =  PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            return;
        }
        IHandlerService handlerService = (IHandlerService) activeWorkbenchWindow
                .getService(IHandlerService.class);
        if (handlerService == null) {
            return;
        }
        handlerService.activateHandler(STEP_INTO_COMMAND, new StepIntoCommandHandler());
        handlerService.activateHandler(STEP_OVER_COMMAND, new StepOverCommandHandler());
        handlerService.activateHandler(STEP_RETURN_COMMAND, new StepReturnCommandHandler());
        handlerService.activateHandler(TOGGLE_BREAKPOINT_COMMAND, new ToggleBreakpointHandler());
    }

    private void wrapExecutionMenuItemsProcessor(String menuItemId) {
        MToolItem runToolItem = (MToolItem) modelService.find(menuItemId, application);
        if (runToolItem == null)
            return;

        MMenu menu = runToolItem.getMenu();
        if (menu == null) {
            return;
        }

        convertChildrenToExecutionHandledElements(menu.getChildren());
    }

    private void convertChildrenToExecutionHandledElements(List<MMenuElement> originalElements) {
        List<MMenuElement> menuItems = newElements(originalElements);
        originalElements.clear();
        originalElements.addAll(menuItems);
    }

    private List<MMenuElement> newElements(List<MMenuElement> originalElements) {
        if (originalElements == null) {
            return Collections.emptyList();
        }
        List<MMenuElement> menuItems = new ArrayList<MMenuElement>();
        for (MMenuElement item : originalElements) {
            MMenuElement wrappedItem = item;
            if (item instanceof MHandledMenuItem) {
                wrappedItem = new ExecutionHandledMenuItem((MHandledMenuItem) item);
            }
            if (item instanceof MMenu) {
                convertChildrenToExecutionHandledElements(((MMenu) item).getChildren());
            }
            menuItems.add(wrappedItem);
        }
        return menuItems;
    }

    private void startSessionServer() {
        new Thread(ExecutionSessionSocketServer.getInstance()).start();
    }

    private void executeTestCaseFromTestStep(ExecuteFromTestStepEntity executeFromTestStepEntity) {
        if (executeFromTestStepEntity == null || executeFromTestStepEntity.getTestCase() == null) {
            return;
        }
        try {
            IRunConfiguration runConfiguration = new ExistingRunConfiguration(
                    ProjectController.getInstance().getCurrentProject().getFolderLocation());
            ExistingRunConfiguration existingRunConfiguration = (ExistingRunConfiguration) runConfiguration;
            existingRunConfiguration.setSessionId(executeFromTestStepEntity.getSessionId());
            existingRunConfiguration.setRemoteUrl(executeFromTestStepEntity.getRemoteServerUrl());
            existingRunConfiguration.setDriverName(executeFromTestStepEntity.getDriverTypeName());
            ((DefaultExecutionSetting) existingRunConfiguration.getExecutionSetting())
                    .setRawScript(executeFromTestStepEntity.getRawScript());
            ExistingExecutionHandler handler = new ExistingExecutionHandler();
            handler.setExistingRunConfig(existingRunConfiguration);
            Job job = new ExecuteTestCaseJob(StringConstants.HAND_JOB_LAUNCHING_TEST_CASE,
                    executeFromTestStepEntity.getTestCase(), LaunchMode.RUN, sync, handler);
            job.setUser(true);
            job.schedule();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
