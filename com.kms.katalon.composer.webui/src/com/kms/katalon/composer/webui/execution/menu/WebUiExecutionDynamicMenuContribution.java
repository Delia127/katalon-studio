package com.kms.katalon.composer.webui.execution.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.webui.constants.ImageConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

@SuppressWarnings("restriction")
public class WebUiExecutionDynamicMenuContribution {
	private static final String FIREFOX_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.webui.execution.command.firefox";
	private static final String CHROME_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.webui.execution.command.chrome";
	private static final String IE_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.webui.execution.command.ie";
	private static final String SAFARI_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.webui.execution.command.safari";
	private static final String REMOTE_WEB_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.webui.execution.command.remoteweb";

	@Inject
	private ECommandService commandService;

	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {
		try {
			for (WebUIDriverType webUIDriverType : WebUIDriverType.values()) {
				MHandledMenuItem envPopupMenuItem = getEnvPopupMenuItem(webUIDriverType, commandService);
				if (envPopupMenuItem != null) {
					items.add(envPopupMenuItem);
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	private static MHandledMenuItem getEnvPopupMenuItem(WebUIDriverType webUIDriverType,
			ECommandService commandService) throws Exception {
		if (webUIDriverType != null) {
			String commandId = null;
			String iconURI = null;
			switch (webUIDriverType) {
			case CHROME_DRIVER:
				commandId = CHROME_EXECUTION_COMMAND_ID;
				iconURI = ImageConstants.URL_16_CHROME;
				break;
			case FIREFOX_DRIVER:
				commandId = FIREFOX_EXECUTION_COMMAND_ID;
				iconURI = ImageConstants.URL_16_FIREFOX;
				break;
			case IE_DRIVER:
				commandId = IE_EXECUTION_COMMAND_ID;
				iconURI = ImageConstants.URL_16_IE;
				break;
			case SAFARI_DRIVER:
				commandId = SAFARI_EXECUTION_COMMAND_ID;
				iconURI = ImageConstants.URL_16_SAFARI;
				break;
			case REMOTE_WEB_DRIVER:
				commandId = REMOTE_WEB_EXECUTION_COMMAND_ID;
				iconURI = ImageConstants.URL_16_REMOTE_WEB;
				break;
			default:
				break;
			}
			if (commandId == null) {
				return null;
			}
			MHandledMenuItem envPopupMenuItem = MenuFactory.createPopupMenuItem(
					commandService.createCommand(commandId, null), webUIDriverType.toString(),
					ConstantsHelper.getApplicationURI());
			envPopupMenuItem.setIconURI(iconURI);
			return envPopupMenuItem;
		}
		return null;
	}
}