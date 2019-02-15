package com.kms.katalon.composer.testsuite.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.testsuite.handlers.NewTestSuiteHandler;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;


@SuppressWarnings("restriction")
public class NewFilteringTestSuitePopupMenuContribution {

    private static final String NEW_FILTERING_TEST_SUITE_COMMAND = "com.kms.katalon.composer.testsuite.command.newFilteringTestSuite";

    @Inject
    private ESelectionService selectionService;

    @Inject
    private ECommandService commandService;

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);

            if (selectedObjects == null || selectedObjects.length != 1) {
                return;
            }

            Plugin plugin = ApplicationManager.getInstance()
                    .getPluginManager()
                    .getPlugin("com.katalon.katalon-studio-tags-plugin");
            if (NewTestSuiteHandler.findParentTreeEntity(selectedObjects) != null
                    && plugin != null) {
                MHandledMenuItem newTestSuitePopupMenuItem = MenuFactory.createPopupMenuItem(
                        commandService.createCommand(NEW_FILTERING_TEST_SUITE_COMMAND, null), "Dynamic Querying Test Suite",
                        ConstantsHelper.getApplicationURI());
                if (newTestSuitePopupMenuItem != null) {
                    menuItems.add(newTestSuitePopupMenuItem);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
