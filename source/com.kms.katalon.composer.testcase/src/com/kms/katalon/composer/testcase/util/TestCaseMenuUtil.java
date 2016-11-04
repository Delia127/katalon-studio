package com.kms.katalon.composer.testcase.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants.AddAction;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.keyword.KeywordContributorCollection;

public class TestCaseMenuUtil {
    public static MenuItem addActionSubMenu(Menu menu, AddAction addAction, String MenuText, SelectionListener selectionListener) {
        MenuItem actionMenuItem = new MenuItem(menu, SWT.CASCADE);
        actionMenuItem.setText(MenuText);

        Menu actionMenu = new Menu(menu);
        actionMenuItem.setMenu(actionMenu);

        fillActionMenu(addAction, selectionListener, actionMenu);

        return actionMenuItem;
    }

    public static void fillActionMenu(AddAction addAction, SelectionListener selectionListener, Menu actionMenu) {

        addBuiltInKeywordMenuItems(addAction, selectionListener, actionMenu);

        addNewMenuItem(addAction, selectionListener, actionMenu, TreeTableMenuItemConstants.CUSTOM_KEYWORD_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CUSTOM_KEYWORD_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu, TreeTableMenuItemConstants.CALL_TEST_CASE_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CALL_TEST_CASE_MENU_ITEM_ID, SWT.PUSH);
        
        new MenuItem(actionMenu, SWT.SEPARATOR);

        MenuItem decisionMakingStatementWrappersMenuItem = addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.DECISION_MAKING_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.DECISION_MAKING_STATEMENT_MENU_ITEM_ID, SWT.CASCADE);

        Menu decisionMakingStatementWrappersMenu = new Menu(actionMenu);
        decisionMakingStatementWrappersMenuItem.setMenu(decisionMakingStatementWrappersMenu);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.IF_STATEMENT_MENU_ITEM_LABEL, TreeTableMenuItemConstants.IF_STATEMENT_MENU_ITEM_ID,
                SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.ELSE_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.ELSE_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.ELSE_IF_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.ELSE_IF_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.SWITCH_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.SWITCH_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.CASE_STATEMENT_MENU_ITEM_LABEL, TreeTableMenuItemConstants.CASE_STATMENT_MENU_ITEM_ID,
                SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.DEFAULT_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.DEFAULT_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        MenuItem loopingStatementWrappersMenuItem = addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.LOOPING_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.LOOPING_STATEMENT_MENU_ITEM_ID, SWT.CASCADE);

        Menu loopingMakingStatementWrappersMenu = new Menu(actionMenu);
        loopingStatementWrappersMenuItem.setMenu(loopingMakingStatementWrappersMenu);

        addNewMenuItem(addAction, selectionListener, loopingMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.FOR_STATEMENT_MENU_ITEM_LABEL, TreeTableMenuItemConstants.FOR_STATEMENT_MENU_ITEM_ID,
                SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, loopingMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.WHILE_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.WHILE_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        MenuItem branchingStatementWrappersMenuItem = addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.BRANCHING_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.BRANCHING_STATEMENT_MENU_ITEM_ID, SWT.CASCADE);

        Menu branchingMakingStatementWrappersMenu = new Menu(actionMenu);
        branchingStatementWrappersMenuItem.setMenu(branchingMakingStatementWrappersMenu);

        addNewMenuItem(addAction, selectionListener, branchingMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.BREAK_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.BREAK_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, branchingMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.CONTINUE_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CONTINUE_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, branchingMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.RETURN_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.RETURN_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        MenuItem exceptionHandlingStatementWrappersMenuItem = addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.EXCEPTION_HANDLING_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.EXCEPTION_HANDLING_STATEMENT_MENU_ITEM_ID, SWT.CASCADE);
        
        new MenuItem(actionMenu, SWT.SEPARATOR);

        Menu exceptionHandlingStatementWrappersMenu = new Menu(actionMenu);
        exceptionHandlingStatementWrappersMenuItem.setMenu(exceptionHandlingStatementWrappersMenu);

        addNewMenuItem(addAction, selectionListener, exceptionHandlingStatementWrappersMenu,
                TreeTableMenuItemConstants.TRY_STATEMENT_MENU_ITEM_LABEL, TreeTableMenuItemConstants.TRY_STATEMENT_MENU_ITEM_ID,
                SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, exceptionHandlingStatementWrappersMenu,
                TreeTableMenuItemConstants.CATCH_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CATCH_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, exceptionHandlingStatementWrappersMenu,
                TreeTableMenuItemConstants.FINALLY_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.FINALLY_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, exceptionHandlingStatementWrappersMenu,
                TreeTableMenuItemConstants.THROW_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.THROW_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu, TreeTableMenuItemConstants.BINARY_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.BINARY_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu, TreeTableMenuItemConstants.ASSERT_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.ASSERT_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.CALL_METHOD_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CALL_METHOD_STATEMENT_MENU_ITEM_ID, SWT.PUSH);
        
        new MenuItem(actionMenu, SWT.SEPARATOR);

        addNewMenuItem(addAction, selectionListener, actionMenu, TreeTableMenuItemConstants.METHOD_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.METHOD_MENU_ITEM_ID, SWT.PUSH);
    }

    private static MenuItem addNewMenuItem(AddAction addAction, SelectionListener selectionListener, Menu actionMenu,
            String text, int id, int type) {
        MenuItem newMenuItem = new MenuItem(actionMenu, type);
        newMenuItem.setText(text);
        newMenuItem.addSelectionListener(selectionListener);
        newMenuItem.setID(id);
        newMenuItem.setData(TreeTableMenuItemConstants.MENU_ITEM_ACTION_KEY, addAction);
        return newMenuItem;
    }

    private static void addBuiltInKeywordMenuItems(AddAction addAction, SelectionListener selectionListener, Menu actionMenu) {
        // preBuild
        TreeTableMenuItemConstants.generateBuiltInKeywordMenuItemIDs(KeywordController.getInstance().getBuiltInKeywordClasses());
        for (IKeywordContributor contributor : KeywordContributorCollection.getKeywordContributors()) {
            addNewMenuItem(addAction, selectionListener, actionMenu, contributor.getLabelName(),
                    TreeTableMenuItemConstants.getMenuItemID(contributor.getAliasName()), SWT.PUSH);
        }
    }
}
