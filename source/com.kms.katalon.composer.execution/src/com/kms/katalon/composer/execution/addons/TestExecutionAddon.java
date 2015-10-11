package com.kms.katalon.composer.execution.addons;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MDynamicMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.handlers.EvaluateDriverConnectorEditorContributionsHandler;
import com.kms.katalon.composer.execution.menu.CustomExecutionMenuContribution;
import com.kms.katalon.constants.EventConstants;

public class TestExecutionAddon implements EventHandler {

    private static final String KATALON_COMPOSER_EXECUTION_BUNDLE_URI = "bundleclass://com.kms.katalon.composer.execution/";

    private static final String KATALON_COMPOSER_EXECUTION_ID = "com.kms.katalon.composer.execution";

    private static final String CUSTOM_RUN_MENU_ID = KATALON_COMPOSER_EXECUTION_ID + ".run.custom";

    private static final String CUSTOM_RUN_MENU_LABEL = "Custom";

    private static final String RUN_TOOL_ITEM_ID = KATALON_COMPOSER_EXECUTION_ID + ".handledtoolitem.run";

    private static final String CUSTOM_RUN_CONFIG_CONTRIBUTOR_ID = CUSTOM_RUN_MENU_ID + ".contributor";

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
        initCustomRunConfigurationSubMenu();

    }

    private void initCustomRunConfigurationSubMenu() {
        MToolItem runToolItem = (MToolItem) modelService.find(RUN_TOOL_ITEM_ID, application);
        MMenu menu = runToolItem.getMenu();
        MMenu subMenu = MMenuFactory.INSTANCE.createMenu();
        subMenu.setLabel(CUSTOM_RUN_MENU_LABEL);
        subMenu.setElementId(CUSTOM_RUN_MENU_ID);

        MDynamicMenuContribution dynamicMenuContributor = MMenuFactory.INSTANCE.createDynamicMenuContribution();

        dynamicMenuContributor.setElementId(CUSTOM_RUN_CONFIG_CONTRIBUTOR_ID);
        dynamicMenuContributor.setContributionURI(KATALON_COMPOSER_EXECUTION_BUNDLE_URI
                + CustomExecutionMenuContribution.class.getName());
        subMenu.getChildren().add(dynamicMenuContributor);
        menu.getChildren().add(MMenuFactory.INSTANCE.createMenuSeparator());
        menu.getChildren().add(subMenu);
    }

    @Override
    public void handleEvent(Event event) {
        // init Debug context for workbench
        if (event.getTopic().equals(EventConstants.WORKSPACE_CREATED)) {
            try {
                Platform.getBundle(KATALON_COMPOSER_EXECUTION_ID).start();
            } catch (BundleException e) {
                LoggerSingleton.logError(e);
            }
        }
    }
}
