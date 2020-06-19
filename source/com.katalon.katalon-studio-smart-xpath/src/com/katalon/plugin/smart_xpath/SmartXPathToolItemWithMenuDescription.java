package com.katalon.plugin.smart_xpath;

import java.io.IOException;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.katalon.platform.api.extension.ToolItemWithMenuDescription;
import com.katalon.platform.api.model.Entity;
import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.plugin.smart_xpath.constant.SmartXPathConstants;
import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.katalon.plugin.smart_xpath.controller.AutoHealingController;
import com.katalon.plugin.smart_xpath.logger.LoggerSingleton;
import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.handler.KSEFeatureAccessHandler;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.KSEFeature;

public class SmartXPathToolItemWithMenuDescription implements ToolItemWithMenuDescription {
    private Menu newMenu;

    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    @Override
    public Menu getMenu(Control parent) {
        newMenu = new Menu(parent);
        evaluateAndAddMenuItem(newMenu);
        // This is intentional, updating static menu's item is troublesome, so
        // I'd display MenuItem on clicking on ToolItem
        return null;
    }

    @Override
    public void defaultEventHandler() {
        if (newMenu != null) {
            evaluateAndAddMenuItem(newMenu);
            newMenu.setVisible(true);
        }
    }

    private void evaluateAndAddMenuItem(Menu newMenu) {
        for (MenuItem item : newMenu.getItems()) {
            item.dispose();
        }

        Entity currentProject = ApplicationManager.getInstance().getProjectManager().getCurrentProject();
        if (currentProject != null) {
            AutoHealingController.createXPathFilesIfNecessary(currentProject);
            WebUiExecutionSettingStore webUIExecutionSettingStore = new WebUiExecutionSettingStore(
                    ProjectController.getInstance().getCurrentProject());

            if (canUseSelfHealing() && webUIExecutionSettingStore.getSelfHealingEnabled(canUseSelfHealing())) {
                addDisableSelfHealingMenuItem(newMenu, true);
            } else {
                addEnableSelfHealingMenuItem(newMenu, true);
            }
            addGoToSelfHealingSettingsMenuItem(newMenu, true);
            addOpenSelfHealingInsightsMenuItem(newMenu, true);
        }
    }

    private MenuItem addEnableSelfHealingMenuItem(Menu parentMenu, boolean enable) {
        MenuItem selfHealingEnable = new MenuItem(parentMenu, SWT.PUSH);
        selfHealingEnable.setEnabled(enable);
        selfHealingEnable.setText(SmartXPathMessageConstants.LBL_ENABLE_SELF_HEALING);
        selfHealingEnable.setToolTipText(SmartXPathMessageConstants.LBL_ENABLE_SELF_HEALING);
        selfHealingEnable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (canUseSelfHealing()) {
                    try {
                        WebUiExecutionSettingStore preferenceStore = new WebUiExecutionSettingStore(
                                ProjectController.getInstance().getCurrentProject());
                        preferenceStore.setEnableSelfHealing(true);
                    } catch (IOException e1) {
                        LoggerSingleton.logError(e1);
                    }
                } else {
                    KSEFeatureAccessHandler.handleUnauthorizedAccess(KSEFeature.SELF_HEALING);
                }
            }
        });
        return selfHealingEnable;
    }

    private MenuItem addDisableSelfHealingMenuItem(Menu parentMenu, boolean enable) {
        MenuItem selfHealingDisable = new MenuItem(parentMenu, SWT.PUSH);
        selfHealingDisable.setEnabled(enable);
        selfHealingDisable.setText(SmartXPathMessageConstants.LBL_DISABLE_SELF_HEALING);
        selfHealingDisable.setToolTipText(SmartXPathMessageConstants.LBL_DISABLE_SELF_HEALING);
        selfHealingDisable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (canUseSelfHealing()) {
                    try {
                        WebUiExecutionSettingStore preferenceStore = new WebUiExecutionSettingStore(
                                ProjectController.getInstance().getCurrentProject());
                        preferenceStore.setEnableSelfHealing(false);
                    } catch (IOException e1) {
                        LoggerSingleton.logError(e1);
                    }
                } else {
                    KSEFeatureAccessHandler.handleUnauthorizedAccess(KSEFeature.SELF_HEALING);
                }
            }
        });
        return selfHealingDisable;
    }

    private MenuItem addGoToSelfHealingSettingsMenuItem(Menu parentMenu, boolean enable) {
        MenuItem selfHealingSettingsMenuItem = new MenuItem(parentMenu, SWT.PUSH);
        selfHealingSettingsMenuItem.setEnabled(enable);
        selfHealingSettingsMenuItem.setText(SmartXPathMessageConstants.LBL_SELF_HEALING_SETTINGS);
        selfHealingSettingsMenuItem.setToolTipText(SmartXPathMessageConstants.LBL_SELF_HEALING_SETTINGS);
        selfHealingSettingsMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventBroker.post(EventConstants.PROJECT_SETTINGS_PAGE,
                        SmartXPathConstants.SELF_HEALING_EXECUTION_SETTINGS_PAGE_ID);
            }
        });
        return selfHealingSettingsMenuItem;
    }

    private MenuItem addOpenSelfHealingInsightsMenuItem(Menu parentMenu, boolean enable) {
        MenuItem selfHealingInsightsMenuItem = new MenuItem(parentMenu, SWT.PUSH);
        selfHealingInsightsMenuItem.setEnabled(enable);
        selfHealingInsightsMenuItem.setText(SmartXPathMessageConstants.LBL_SELF_HEALING_INSIGHTS);
        selfHealingInsightsMenuItem.setToolTipText(SmartXPathMessageConstants.LBL_SELF_HEALING_INSIGHTS);
        selfHealingInsightsMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                EModelService modelService = ModelServiceSingleton.getInstance().getModelService();
                MApplication application = ApplicationSingleton.getInstance().getApplication();
                EPartService partService = PartServiceSingleton.getInstance().getPartService();

                // Show console part stack
                List<MPerspectiveStack> psList = modelService.findElements(application, null, MPerspectiveStack.class,
                        null);
                MPartStack consolePartStack = (MPartStack) modelService.find(IdConstants.CONSOLE_PART_STACK_ID,
                        psList.get(0).getSelectedElement());
                consolePartStack.getTags().remove("Minimized");
                consolePartStack.setVisible(true);
                if (!consolePartStack.isToBeRendered()) {
                    consolePartStack.setToBeRendered(true);
                }

                // Focus to Self-Healing Insights part
                MPart selfHealingInsightsPart = (MPart) modelService
                        .find(SmartXPathConstants.SELF_HEALING_INSIGHTS_PART_ID, consolePartStack);
                if (!consolePartStack.getChildren().contains(selfHealingInsightsPart)) {
                    partService.createPart(SmartXPathConstants.SELF_HEALING_INSIGHTS_PART_ID);
                    consolePartStack.getChildren().add(selfHealingInsightsPart);
                }

                if (!selfHealingInsightsPart.isToBeRendered()) {
                    selfHealingInsightsPart.setToBeRendered(true);
                }
                selfHealingInsightsPart.setVisible(true);

                consolePartStack.setSelectedElement(selfHealingInsightsPart);
                partService.activate(selfHealingInsightsPart, true);
            }
        });
        return selfHealingInsightsMenuItem;
    }

    @Override
    public String iconUrl() {
        return SmartXPathConstants.SELF_HEALING_TOOLBAR_MENU_ICON;
    }

    @Override
    public String name() {
        return SmartXPathMessageConstants.LBL_SELF_HEALING;
    }

    @Override
    public String toolItemId() {
        return SmartXPathConstants.SELF_HEALING_TOOLBAR_MENU_ID;
    }
    
    private boolean canUseSelfHealing() {
        IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();
        return featureService.canUse(KSEFeature.SELF_HEALING);
    }

}
