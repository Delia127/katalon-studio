package com.kms.katalon.composer.keyword.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.handlers.NewStepDefinitionHandler;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class NewStepDefinitionPopupMenuContribution {

    private static final String NEW_KEYWORD_POPUP_MENUITEM_LABEL = "Step Definition";

    private static final String NEW_KEYWORD_COMMAND_ID = "com.kms.katalon.composer.keyword.command.addNewStepDefinition";

    @Inject
    private ECommandService commandService;

    @Inject
    private ESelectionService selectionService;

    @Inject
    public void init() {
        selectionService.addSelectionListener(new ISelectionListener() {
            @Override
            public void selectionChanged(MPart part, Object selection) {
                if (part.getElementId().equals(IdConstants.EXPLORER_PART_ID)) {
                    selectionService.setSelection(null);
                    selectionService.setSelection(selection);
                }
            }
        });
    }
    
    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            if (!NewStepDefinitionHandler.isUnderSourceFolder(selectedObjects)) {
                return;
            }

            ITreeEntity treeEntity = NewStepDefinitionHandler.findParentTreeEntity(selectedObjects);
            if (treeEntity != null) {
                MHandledMenuItem newKeywordPopupMenuItem = MenuFactory.createPopupMenuItem(
                        commandService.createCommand(NEW_KEYWORD_COMMAND_ID, null), NEW_KEYWORD_POPUP_MENUITEM_LABEL,
                        ConstantsHelper.getApplicationURI());
                if (newKeywordPopupMenuItem != null) {
                    menuItems.add(newKeywordPopupMenuItem);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
