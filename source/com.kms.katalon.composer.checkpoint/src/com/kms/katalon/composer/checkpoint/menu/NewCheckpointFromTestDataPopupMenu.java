package com.kms.katalon.composer.checkpoint.menu;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.checkpoint.constants.IdConstants;
import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.checkpoint.handlers.NewCheckpointFromTestDataHandler;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class NewCheckpointFromTestDataPopupMenu extends NewCheckpointPopupMenu {

    private static final String NEW_CHECKPOINT_FROM_TEST_DATA_MENU_ITEM_LABEL = StringConstants.MENU_LBL_CHECKPOINT_FROM_TEST_DATA;

    private static final String NEW_CHECKPOINT_FROM_TEST_DATA_COMMAND_ID = IdConstants.NEW_CHECKPOINT_FROM_TEST_DATA_COMMAND_ID;

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        if (!(NewCheckpointFromTestDataHandler.getInstance().getFirstSelection() instanceof TestDataTreeEntity)) {
            return;
        }

        try {
            MHandledMenuItem newCheckpointMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(NEW_CHECKPOINT_FROM_TEST_DATA_COMMAND_ID, null),
                    NEW_CHECKPOINT_FROM_TEST_DATA_MENU_ITEM_LABEL, ConstantsHelper.getApplicationURI());
            if (newCheckpointMenuItem == null) {
                return;
            }
            menuItems.add(newCheckpointMenuItem);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
