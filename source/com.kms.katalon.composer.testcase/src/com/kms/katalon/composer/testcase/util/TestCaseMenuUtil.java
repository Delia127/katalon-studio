package com.kms.katalon.composer.testcase.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants.AddAction;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.keyword.internal.IKeywordContributor;
import com.kms.katalon.core.keyword.internal.KeywordContributorCollection;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.session.ExecutionSession;
import com.kms.katalon.execution.session.ExecutionSessionSocketServer;
import com.kms.katalon.execution.session.RemoteMobileExecutionSession;

public class TestCaseMenuUtil {
    private static final int DEFAULT_MAX_EXISTING_SESSION_TITLE = 20;

    public static MenuItem addActionSubMenu(Menu menu, AddAction addAction, String MenuText,
            SelectionListener selectionListener) {
        MenuItem actionMenuItem = new MenuItem(menu, SWT.CASCADE);
        actionMenuItem.setText(MenuText);

        Menu actionMenu = new Menu(menu);
        actionMenuItem.setMenu(actionMenu);

        fillActionMenu(addAction, selectionListener, actionMenu);

        return actionMenuItem;
    }

    public static void fillActionMenu(AddAction addAction, SelectionListener selectionListener, Menu actionMenu,
            int[] excludes) {
        fillActionMenu(addAction, selectionListener, actionMenu);
        Set<Integer> excludeIds = new HashSet<>(Arrays.asList(ArrayUtils.toObject(excludes)));
        for (MenuItem item : actionMenu.getItems()) {
            if (excludeIds.contains(item.getID())) {
                item.dispose();
            }
        }
    }

    public static void fillActionMenu(AddAction addAction, SelectionListener selectionListener, Menu actionMenu) {

        addBuiltInKeywordMenuItems(addAction, selectionListener, actionMenu);

        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.CUSTOM_KEYWORD_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CUSTOM_KEYWORD_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.CALL_TEST_CASE_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CALL_TEST_CASE_MENU_ITEM_ID, SWT.PUSH);

        new MenuItem(actionMenu, SWT.SEPARATOR);

        MenuItem decisionMakingStatementWrappersMenuItem = addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.DECISION_MAKING_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.DECISION_MAKING_STATEMENT_MENU_ITEM_ID, SWT.CASCADE);

        Menu decisionMakingStatementWrappersMenu = new Menu(actionMenu);
        decisionMakingStatementWrappersMenuItem.setMenu(decisionMakingStatementWrappersMenu);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.IF_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.IF_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

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
                TreeTableMenuItemConstants.CASE_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CASE_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.DEFAULT_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.DEFAULT_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        MenuItem loopingStatementWrappersMenuItem = addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.LOOPING_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.LOOPING_STATEMENT_MENU_ITEM_ID, SWT.CASCADE);

        Menu loopingMakingStatementWrappersMenu = new Menu(actionMenu);
        loopingStatementWrappersMenuItem.setMenu(loopingMakingStatementWrappersMenu);

        addNewMenuItem(addAction, selectionListener, loopingMakingStatementWrappersMenu,
                TreeTableMenuItemConstants.FOR_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.FOR_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

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
                TreeTableMenuItemConstants.TRY_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.TRY_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, exceptionHandlingStatementWrappersMenu,
                TreeTableMenuItemConstants.CATCH_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CATCH_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, exceptionHandlingStatementWrappersMenu,
                TreeTableMenuItemConstants.FINALLY_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.FINALLY_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, exceptionHandlingStatementWrappersMenu,
                TreeTableMenuItemConstants.THROW_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.THROW_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.BINARY_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.BINARY_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.ASSERT_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.ASSERT_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.CALL_METHOD_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CALL_METHOD_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        new MenuItem(actionMenu, SWT.SEPARATOR);

        addNewMenuItem(addAction, selectionListener, actionMenu, TreeTableMenuItemConstants.METHOD_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.METHOD_MENU_ITEM_ID, SWT.PUSH);
    }

    public static void generateExecuteFromTestStepSubMenu(Menu menu, SelectionListener selectionListener) {
        generateExecuteFromTestStepSubMenu(menu, selectionListener, -1);
    }

    public static void generateExecuteFromTestStepSubMenu(Menu menu, SelectionListener selectionListener,
            int menuIndex) {
        List<ExecutionSession> allAvailableExecutionSessions = ExecutionSessionSocketServer.getInstance()
                .getAllAvailableExecutionSessions();
        boolean isExecutionSessionsEmpty = allAvailableExecutionSessions.isEmpty();

        MenuItem executeFromTestStepMenuItem = menuIndex >= 0
                ? new MenuItem(menu, isExecutionSessionsEmpty ? SWT.PUSH : SWT.CASCADE, menuIndex)
                : new MenuItem(menu, isExecutionSessionsEmpty ? SWT.PUSH : SWT.CASCADE);
        executeFromTestStepMenuItem.setText(ComposerTestcaseMessageConstants.ADAP_MENU_CONTEXT_EXECUTE_FROM_TEST_STEP);
        executeFromTestStepMenuItem.addSelectionListener(selectionListener);
        if (isExecutionSessionsEmpty) {
            executeFromTestStepMenuItem.setEnabled(false);
            return;
        }
        Map<String, Integer> labelMap = new HashMap<>();
        Menu executeSessionMenu = new Menu(executeFromTestStepMenuItem);
        for (ExecutionSession executionSession : allAvailableExecutionSessions) {
            MenuItem executionSessionMenuItem = new MenuItem(executeSessionMenu, SWT.PUSH);
            String menuLabel = getLabelForExecutionSession(executionSession);
            if (labelMap.containsKey(menuLabel)) {
                Integer numberOfInstances = labelMap.get(menuLabel) + 1;
                labelMap.put(menuLabel, numberOfInstances);
                menuLabel += " (" + numberOfInstances + ")";
            } else {
                labelMap.put(menuLabel, 1);
            }
            executionSessionMenuItem.setText(menuLabel);
            executionSessionMenuItem.addSelectionListener(selectionListener);
            executionSessionMenuItem.setID(TreeTableMenuItemConstants.EXECUTE_FROM_TEST_STEP_MENU_ITEM_ID);
            executionSessionMenuItem.setData(executionSession);
            executionSessionMenuItem.setImage(getImageForDriverType(executionSession));
        }
        executeFromTestStepMenuItem.setMenu(executeSessionMenu);
    }

    private static String getLabelForExecutionSession(ExecutionSession executionSession) {
        String executionTitle = executionSession.getTitle();
        if (executionTitle.isEmpty()) {
            executionTitle = ComposerTestcaseMessageConstants.LBL_EXECUTION_EXISTING_SESSION_BLANK_TITLE;
        }
        return StringUtils.abbreviate(executionTitle, DEFAULT_MAX_EXISTING_SESSION_TITLE);
    }

    private static Image getImageForDriverType(ExecutionSession executionSession) {
        String driverTypeName = executionSession.getDriverTypeName();
        if (WebUIDriverType.KOBITON_WEB_DRIVER.toString().equals(driverTypeName)
                || (executionSession instanceof RemoteMobileExecutionSession
                        && ((RemoteMobileExecutionSession) executionSession).getRemoteType()
                                .equals(WebUIDriverType.KOBITON_WEB_DRIVER.getName()))) {
            return ImageManager.getImage(IImageKeys.KOBITON_16);
        }
        if (WebUIDriverType.ANDROID_DRIVER.toString().equals(driverTypeName)) {
            return ImageManager.getImage(IImageKeys.ANDROID_16);
        }
        if (WebUIDriverType.CHROME_DRIVER.toString().equals(driverTypeName)) {
            return ImageManager.getImage(IImageKeys.CHROME_16);
        }
        if (WebUIDriverType.EDGE_DRIVER.toString().equals(driverTypeName)) {
            return ImageManager.getImage(IImageKeys.EDGE_16);
        }
        if (WebUIDriverType.FIREFOX_DRIVER.toString().equals(driverTypeName)) {
            return ImageManager.getImage(IImageKeys.FIREFOX_16);
        }
        if (WebUIDriverType.HEADLESS_DRIVER.toString().equals(driverTypeName)) {
            return ImageManager.getImage(IImageKeys.CHROME_HEADLESS_16);
        }
        if (WebUIDriverType.IE_DRIVER.toString().equals(driverTypeName)) {
            return ImageManager.getImage(IImageKeys.IE_16);
        }
        if (WebUIDriverType.IOS_DRIVER.toString().equals(driverTypeName)) {
            return ImageManager.getImage(IImageKeys.APPLE_16);
        }
        if (WebUIDriverType.SAFARI_DRIVER.toString().equals(driverTypeName)) {
            return ImageManager.getImage(IImageKeys.SAFARI_16);
        }
        if (WebUIDriverType.FIREFOX_HEADLESS_DRIVER.toString().equals(driverTypeName)) {
            return ImageManager.getImage(IImageKeys.FIREFOX_HEADLESS_16);
        }
        return null;
    }
    
//    --------------------------
    public static void fillActionMenuAddTestSuite(AddAction addAction, SelectionListener selectionListener, Menu actionMenu) {
        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.ADD_TO_AN_EXISTING_TEST_SUITE_LABEL,
                TreeTableMenuItemConstants.ADD_TO_AN_EXISTING_TEST_SUITE_ID, SWT.PUSH);
        
        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.ADD_TO_A_NEW_TEST_SUITE_LABEL,
                TreeTableMenuItemConstants.ADD_TO_A_NEW_TEST_SUITE_ID, SWT.PUSH);
    }
//    ----------------------------
    private static MenuItem addNewMenuItem(AddAction addAction, SelectionListener selectionListener, Menu actionMenu,
            String text, int id, int type) {
        MenuItem newMenuItem = new MenuItem(actionMenu, type);
        newMenuItem.setText(text);
        newMenuItem.addSelectionListener(selectionListener);
        newMenuItem.setID(id);
        newMenuItem.setData(TreeTableMenuItemConstants.MENU_ITEM_ACTION_KEY, addAction);
        return newMenuItem;
    }

    private static void addBuiltInKeywordMenuItems(AddAction addAction, SelectionListener selectionListener,
            Menu actionMenu) {
        // preBuild
        TreeTableMenuItemConstants
                .generateBuiltInKeywordMenuItemIDs(KeywordController.getInstance().getBuiltInKeywordClasses());
        for (IKeywordContributor contributor : KeywordContributorCollection.getKeywordContributors()) {
            addNewMenuItem(addAction, selectionListener, actionMenu, contributor.getLabelName(),
                    TreeTableMenuItemConstants.getMenuItemID(contributor.getAliasName()), SWT.PUSH);
        }
    }
}
