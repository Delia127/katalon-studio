package com.katalon.plugin.smart_xpath;

import java.io.IOException;

import org.eclipse.e4.core.services.events.IEventBroker;
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
import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.handler.KSEFeatureAccessHandler;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.feature.KSEFeature;

public class SmartXPathToolItemWithMenuDescription implements ToolItemWithMenuDescription {
	private Menu newMenu;
	private MenuItem selfHealingEnable;
	private MenuItem selfHealingDisable;
	private MenuItem autoHealing;
	private Control parent;
	
	private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

	@Override
	public Menu getMenu(Control arg0) {
		parent = arg0;
		newMenu = new Menu(arg0);
		evaluateAndAddMenuItem(newMenu);
		// This is intentional, updating static menu's item is troublesome, so
		// I'd display MenuItem on clicking on ToolItem
		return null;
	}

	@Override
	public void defaultEventHandler() {
		if (newMenu != null) {
			evaluateAndAddMenuItem(newMenu);
			// Display menu at the mouse position (guaranteed to be within the
			// ToolItem icon)
			newMenu.setVisible(true);
		}
	}

	private void evaluateAndAddMenuItem(Menu newMenu) {
		// Dispose all items
		for (MenuItem item : newMenu.getItems()) {
			item.dispose();
		}
		selfHealingEnable = null;
		selfHealingDisable = null;

		// Re-evaluate the PreferenceStore and add the appropriate menu item
		try {
			Entity currentProject = ApplicationManager.getInstance().getProjectManager().getCurrentProject();
			if (currentProject != null) {
				AutoHealingController.createXPathFilesIfNecessary(currentProject);
				WebUiExecutionSettingStore webUIExecutionSettingStore = new WebUiExecutionSettingStore(ProjectController.getInstance().getCurrentProject());

				if (webUIExecutionSettingStore.isEnableSelfHHealing()) {
					addDisableSelfHealingMenuItem(newMenu, true);
				} else {
					addEnableSelfHealingMenuItem(newMenu, true);
				}
				addGoToSelfHealingSettingsMenuItem(newMenu, true);
			}
		} catch (IOException e) {
			LoggerSingleton.logError(e);
		}
	}

	private MenuItem addEnableSelfHealingMenuItem(Menu parentMenu, boolean enable) {
		selfHealingEnable = new MenuItem(parentMenu, SWT.PUSH);
		selfHealingEnable.setEnabled(enable);
		selfHealingEnable.setText(SmartXPathMessageConstants.LBL_ENABLE_SELF_HEALING);
		selfHealingEnable.setToolTipText(SmartXPathMessageConstants.LBL_ENABLE_SELF_HEALING);
		selfHealingEnable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			    if (LicenseUtil.isNotFreeLicense()) {
    				try {
    					// Retrieve PreferenceStore on click in case user installed
    					// this plug-in when no project was opened
    	                WebUiExecutionSettingStore preferenceStore = new WebUiExecutionSettingStore(ProjectController.getInstance().getCurrentProject());
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
		selfHealingDisable = new MenuItem(parentMenu, SWT.PUSH);
        selfHealingDisable.setEnabled(enable);
        selfHealingDisable.setText(SmartXPathMessageConstants.LBL_DISABLE_SELF_HEALING);
        selfHealingDisable.setToolTipText(SmartXPathMessageConstants.LBL_DISABLE_SELF_HEALING);
        selfHealingDisable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			    if (LicenseUtil.isNotFreeLicense()) {
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
        selfHealingSettingsMenuItem.setImage(ImageManager.getImage(IImageKeys.CONFIG_16));
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

}
