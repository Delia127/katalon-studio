package com.kms.katalon.composer.integration.git.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.integration.git.constants.GitIdConstant;
import com.kms.katalon.composer.integration.git.constants.GitStringConstants;
import com.kms.katalon.composer.integration.git.handlers.BranchHandler;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class GitMenuContribution {
    @Inject
    protected ECommandService commandService;

    @AboutToShow
    public void aboutToShow(List<MMenuElement> items) {
        MMenuFactory menuFactory = MMenuFactory.INSTANCE;

        addChildMenuItem(items, GitIdConstant.GIT_CLONE_COMMAND_ID, GitStringConstants.GIT_CLONE_MENU_ITEM_LABEL);
        addChildMenuItem(items, GitIdConstant.GIT_SHARE_PROJECT_COMMAND_ID,
                GitStringConstants.GIT_SHARE_PROJECT_MENU_ITEM_LABEL);
        addChildMenuItem(items, GitIdConstant.GIT_SHOW_HISTORY_COMMAND_ID,
                GitStringConstants.GIT_SHOW_HISTORY_MENU_ITEM_LABEL);

        addGitBranchMenu(menuFactory, items);

        addChildMenuItem(items, GitIdConstant.GIT_COMMIT_PROJECT_COMMAND_ID,
                GitStringConstants.GIT_COMMIT_MENU_ITEM_LABEL);
        addChildMenuItem(items, GitIdConstant.GIT_PUSH_PROJECT_COMMAND_ID, GitStringConstants.GIT_PUSH_MENU_ITEM_LABEL);
        addChildMenuItem(items, GitIdConstant.GIT_PULL_PROJECT_COMMAND_ID, GitStringConstants.GIT_PULL_MENU_ITEM_LABEL);
        addChildMenuItem(items, GitIdConstant.GIT_FETCH_PROJECT_COMMAND_ID,
                GitStringConstants.GIT_FETCH_MENU_ITEM_LABEL);
        for (MMenuElement menuElement : items) {
            menuElement.setTooltip(StringConstants.EMPTY);
        }
    }

    public MDirectMenuItem createDisableMenuItem(MMenuFactory menuFactory, final String menuItemLabel) {
        MDirectMenuItem disabledMenuItem = menuFactory.createDirectMenuItem();
        disabledMenuItem.setLabel(menuItemLabel);
        disabledMenuItem.setEnabled(false);
        return disabledMenuItem;
    }

    public void addGitBranchMenu(MMenuFactory menuFactory, List<MMenuElement> items) {
        if (!new BranchHandler().canExecute()) {
            items.add(createDisableMenuItem(menuFactory, GitStringConstants.GIT_BRANCH_MENU_LABEL));
            return;
        }
        MMenu gitBranchMenu = menuFactory.createMenu();
        gitBranchMenu.setLabel(GitStringConstants.GIT_BRANCH_MENU_LABEL);
        List<MMenuElement> manageBranchChildren = gitBranchMenu.getChildren();
        addChildMenuItem(manageBranchChildren, GitIdConstant.GIT_NEW_BRANCH_PROJECT_COMMAND_ID,
                GitStringConstants.GIT_NEW_BRANCH_MENU_ITEM_LABEL);
        addChildMenuItem(manageBranchChildren, GitIdConstant.GIT_CHECKOUT_BRANCH_PROJECT_COMMAND_ID,
                GitStringConstants.GIT_CHECKOUT_BRANCH_MENU_ITEM_LABEL);
        addChildMenuItem(manageBranchChildren, GitIdConstant.GIT_DELETE_BRANCH_PROJECT_COMMAND_ID,
                GitStringConstants.GIT_DELETE_BRANCH_MENU_ITEM_LABEL);
        addChildMenuItem(manageBranchChildren, GitIdConstant.GIT_BRANCH_PROJECT_COMMAND_ID,
                GitStringConstants.GIT_ADVANCE_BRANCH_MENU_ITEM_LABEL);
        items.add(gitBranchMenu);
    }

    private void addChildMenuItem(List<MMenuElement> items, String commandId, String menuItemLabel) {
        MHandledMenuItem shareProjectmenuItem = MenuFactory.createPopupMenuItem(
                commandService.createCommand(commandId, null), menuItemLabel, ConstantsHelper.getApplicationURI());
        items.add(shareProjectmenuItem);
    }

}
