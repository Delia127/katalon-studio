package com.kms.katalon.composer.mobile.recorder.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;

@SuppressWarnings("restriction")
public class MobileRecorderKobitonDeviceMenuContribution {

    private static final String KOBITON_ICON_URI = ImageManager.getImageURLString(IImageKeys.KOBITON_16);

    private static final String MOBILE_RECORDER_KOBITON_DEVICE_COMMAND_ID = "com.kms.katalon.composer.mobile.recorder.command.openKobitonRecorder";

    @Inject
    private ECommandService commandService;

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        if (!KobitonPreferencesProvider.isKobitonPluginInstalled()) {
            return;
        }
        menuItems.add(createMobileRecorderKobitonDeviceMenuItem());
    }

    private MHandledMenuItem createMobileRecorderKobitonDeviceMenuItem() {
        try {
            MHandledMenuItem item = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(MOBILE_RECORDER_KOBITON_DEVICE_COMMAND_ID, null), "Kobiton Devices",
                    ConstantsHelper.getApplicationURI());
            item.setIconURI(KOBITON_ICON_URI);
            item.setTooltip("Record mobile applications on Kobiton cloud devices");
            return item;
        } catch (Exception e) {
            return null;
        }
    }

}
