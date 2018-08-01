package com.kms.katalon.composer.integration.cucumber.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.tree.SystemFileTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.integration.cucumber.handler.FeatureTreeRootCatcher;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class NewFeatureMenuContribution extends FeatureTreeRootCatcher {

    private static final String NEW_FEATURE_COMMAND_ID =
            "com.kms.katalon.composer.integration.cucumber.command.new";
    
    private static final String NEW_EMPTY_COMMAND_ID =
            "com.kms.katalon.composer.integration.cucumber.command.newEmpty";

    @Inject
    private ECommandService commandService;

    @Inject
    private ESelectionService selectionService;

    private boolean isSystemFileSelected() {
        Object selectedObj = getFirstSelection(selectionService);
        return selectedObj instanceof SystemFileTreeEntity;
    }
    

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            if (getParentFeatureTreeFolder(selectionService, false) == null) {
                if (isSystemFileSelected()) {
                    MHandledMenuItem newTestSuitePopupMenuItem = MenuFactory.createPopupMenuItem(
                            commandService.createCommand(NEW_EMPTY_COMMAND_ID, null),
                            "(empty)",
                            ConstantsHelper.getApplicationURI());
                    if (newTestSuitePopupMenuItem != null) {
                        menuItems.add(newTestSuitePopupMenuItem);
                    }
                }
                return;
            }

            MHandledMenuItem newTestSuitePopupMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(NEW_FEATURE_COMMAND_ID, null),
                    "New Feature File",
                    ConstantsHelper.getApplicationURI());
            if (newTestSuitePopupMenuItem != null) {
                menuItems.add(newTestSuitePopupMenuItem);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
