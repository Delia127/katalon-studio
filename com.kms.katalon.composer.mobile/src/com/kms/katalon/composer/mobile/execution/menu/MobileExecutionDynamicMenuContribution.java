package com.kms.katalon.composer.mobile.execution.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.mobile.constants.ImageConstants;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class MobileExecutionDynamicMenuContribution {
	private static final String IOS_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.mobile.execution.command.ios";
	private static final String ANDROID_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.mobile.execution.command.android";
	private static final String MIXED_MODE_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.mobile.execution.command.mixed";

	@Inject
	private ECommandService commandService;

	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {
		try {
			items.add(getEnvPopupMenuItem(StringConstants.OS_IOS, commandService));
			items.add(getEnvPopupMenuItem(StringConstants.OS_ANDROID, commandService));
			items.add(getEnvPopupMenuItem(StringConstants.MIXED_MODE, commandService));
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	private static MHandledMenuItem getEnvPopupMenuItem(String platForm, ECommandService commandService)
			throws Exception {
		if (platForm != null) {
			String commandId = null;
			String iconURI = null;
			switch (platForm) {
			case StringConstants.OS_IOS:
				commandId = IOS_EXECUTION_COMMAND_ID;
				iconURI = ImageConstants.URL_16_APPLE;
				break;
			case StringConstants.OS_ANDROID:
				commandId = ANDROID_EXECUTION_COMMAND_ID;
				iconURI = ImageConstants.URL_16_ANDROID;
				break;
			case StringConstants.MIXED_MODE:
				commandId = MIXED_MODE_EXECUTION_COMMAND_ID;
				iconURI = ImageConstants.URL_16_MIXED_MODE;
				break;
			default:
				break;
			}
			if (commandId == null) {
				return null;
			}
			MHandledMenuItem envPopupMenuItem = MenuFactory.createPopupMenuItem(
					commandService.createCommand(commandId, null), platForm, ConstantsHelper.getApplicationURI());
			envPopupMenuItem.setIconURI(iconURI);
			return envPopupMenuItem;
		}
		return null;
	}
}