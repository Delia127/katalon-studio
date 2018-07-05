package com.kms.katalon.composer.keyword.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class ImportFolderPopupMenuContribution {

    private static final String IMPORT_KEYWORD_FOLDER_POPUP_MENUITEM_LABEL = StringConstants.MENU_CONTEXT_IMPORT_FOLDER;

    private static final String IMPORT_KEYWORD_FOLDER_COMMAND_ID = "com.kms.katalon.composer.keyword.command.importfolder";

    @Inject
    private ECommandService commandService;

    @Inject
    private ESelectionService selectionService;

    @Inject
    public void init() {
        selectionService.addSelectionListener((MPart part, Object selection) -> {
            if (part.getElementId().equals(IdConstants.EXPLORER_PART_ID)) {
                selectionService.setSelection(null);
                selectionService.setSelection(selection);
            }
        });
    }

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {

            MHandledMenuItem importKeywordFolderPopupMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(IMPORT_KEYWORD_FOLDER_COMMAND_ID, null),
                    IMPORT_KEYWORD_FOLDER_POPUP_MENUITEM_LABEL, ConstantsHelper.getApplicationURI());
            if (importKeywordFolderPopupMenuItem != null) {
                menuItems.add(importKeywordFolderPopupMenuItem);
            }

        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
    }

}
