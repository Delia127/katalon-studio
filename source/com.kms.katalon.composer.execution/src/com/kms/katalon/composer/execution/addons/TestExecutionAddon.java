package com.kms.katalon.composer.execution.addons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.debug.internal.ui.commands.actions.StepIntoCommandHandler;
import org.eclipse.debug.internal.ui.commands.actions.StepOverCommandHandler;
import org.eclipse.debug.internal.ui.commands.actions.StepReturnCommandHandler;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MDynamicMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.debug.handler.ToggleBreakpointHandler;
import com.kms.katalon.composer.execution.handlers.EvaluateDriverConnectorEditorContributionsHandler;
import com.kms.katalon.composer.execution.menu.CustomExecutionMenuContribution;
import com.kms.katalon.composer.execution.menu.ExecutionHandledMenuItem;
import com.kms.katalon.composer.execution.util.ComposerExecutionUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.collector.ConsoleOptionCollector;

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

    @PostConstruct
    public void initHandlers(IEventBroker eventBroker) {
        ContextInjectionFactory.make(EvaluateDriverConnectorEditorContributionsHandler.class, context);
        eventBroker.subscribe(EventConstants.WORKSPACE_CREATED, this);
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, this);
        initCustomRunConfigurationSubMenu(IdConstants.RUN_TOOL_ITEM_ID, StringConstants.CUSTOM_RUN_MENU_ID);
        initCustomRunConfigurationSubMenu(IdConstants.DEBUG_TOOL_ITEM_ID, StringConstants.CUSTOM_DEBUG_MENU_ID);
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
        } else if (EventConstants.PROJECT_OPENED.equals(event.getTopic())) {
            writeDefaultConsolePropertyFile();
        }
    }

    private void writeDefaultConsolePropertyFile() {
        try {
            ConsoleOptionCollector.getInstance().writeDefaultPropertyFile(
                    ProjectController.getInstance().getCurrentProject());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void activeDefaultIconForExecution() {
        wrapExecutionMenuItemsProcessor(IdConstants.RUN_TOOL_ITEM_ID);
        wrapExecutionMenuItemsProcessor(IdConstants.DEBUG_TOOL_ITEM_ID);
    }

    private void activeDebugHandlers() {
        IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow()
                .getService(IHandlerService.class);
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
        if (menu == null || menu.getChildren() == null || menu.getChildren().isEmpty())
            return;

        List<MMenuElement> menuItems = new ArrayList<MMenuElement>();
        for (MMenuElement item : menu.getChildren()) {
            MMenuElement wrappedItem = item;
            if (item instanceof MHandledMenuItem) {
                wrappedItem = new ExecutionHandledMenuItem((MHandledMenuItem) item);
            }
            menuItems.add(wrappedItem);
        }
        menu.getChildren().clear();
        menu.getChildren().addAll(menuItems);

        ComposerExecutionUtil.updateDefaultLabelForRunDropDownItem();
    }
}
