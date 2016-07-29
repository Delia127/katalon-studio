package com.kms.katalon.composer.checkpoint.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.checkpoint.constants.IdConstants;
import com.kms.katalon.composer.checkpoint.handlers.NewCheckpointHandler;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class NewCheckpointPopupMenu {
    private static final String NEW_CHECKPOINT_MENU_ITEM_LABEL = StringConstants.CHECKPOINT;

    private static final String NEW_CHECKPOINT_COMMAND_ID = IdConstants.NEW_CHECKPOINT_COMMAND_ID;

    @Inject
    protected ECommandService commandService;

    @Inject
    protected ESelectionService selectionService;

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            if (NewCheckpointHandler.getInstance().findParentSelection() == null) {
                return;
            }

            MHandledMenuItem newCheckpointMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(NEW_CHECKPOINT_COMMAND_ID, null), NEW_CHECKPOINT_MENU_ITEM_LABEL,
                    ConstantsHelper.getApplicationURI());
            if (newCheckpointMenuItem == null) {
                return;
            }
            menuItems.add(newCheckpointMenuItem);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
