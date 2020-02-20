package com.kms.katalon.composer.testcase.menu.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.Before;
import org.junit.Test;

import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.menu.TestStepTableContextMenuWrapper;
import com.kms.katalon.execution.session.ExecutionSession;
import com.kms.katalon.execution.session.ExecutionSessionSocketServer;

public class TestStepTableContextMenuWrapperTest {

    private static final String RUN_FROM_HERE_ITEM_LABEL = ComposerTestcaseMessageConstants.ADAP_MENU_CONTEXT_EXECUTE_FROM_TEST_STEP;

    private static final String ENABLE_ITEM_LABEL = ComposerTestcaseMessageConstants.ADAP_MENU_CONTEXT_ENABLE;

    private static final String DISABLE_ITEM_LABEL = ComposerTestcaseMessageConstants.ADAP_MENU_CONTEXT_DISABLE;

    private Tree testStepTree;
    
    private ExecutionSession executionSession = getDummyAvailableExecutionSession();
    
    
    @Before
    public void setUp() {
        Display display = Display.getCurrent();
        Shell shell = new Shell(display);
        testStepTree = new Tree(shell, SWT.NONE);
        new TreeItem(testStepTree, SWT.NONE);
        ExecutionSessionSocketServer executionSessionSocketServer = getExecutionSessionSocketServer();
        executionSessionSocketServer.removeExecutionSession(executionSession);
    }

    @Test
    public void testRunFromHereMenuItemShouldNotBeDisplayedIfNoStepIsSelected() {
        boolean isFree = true;
        Menu menu = createMenu(testStepTree, isFree);
        assertFalse("RUN_FROM_HERE item should not be displayed if no step is selected - Free user",
                menuItemWithLabelExists(menu, RUN_FROM_HERE_ITEM_LABEL));

        isFree = false;
        menu = createMenu(testStepTree, isFree);
        assertFalse("RUN_FROM_HERE item should not be displayed if no step is selected - Not free user",
                menuItemWithLabelExists(menu, RUN_FROM_HERE_ITEM_LABEL));
    }
    
    @Test
    public void testRunFromHereMenuItemShouldBeDisabledIfNoExecutionSessionIsAvailable() {
        testStepTree.select(testStepTree.getItem(0));
        
        boolean isFree = true;
        Menu menu = createMenu(testStepTree, isFree);
        MenuItem runFromHereMenuItem = getMenuItemWithLabel(menu, RUN_FROM_HERE_ITEM_LABEL);
        assertTrue("RUN_FROM_HERE item should be displayed if no execution session is available - Free user", runFromHereMenuItem != null);
        assertFalse("RUN_FROM_HERE item should be disabled if no execution session is available - Free user", runFromHereMenuItem.getEnabled());
        
        isFree = false;
        menu = createMenu(testStepTree, isFree);
        runFromHereMenuItem = getMenuItemWithLabel(menu, RUN_FROM_HERE_ITEM_LABEL);
        assertTrue("RUN_FROM_HERE item should be displayed if no execution session is available - Not free user", runFromHereMenuItem != null);
        assertFalse("RUN_FROM_HERE item should be disabled if no execution session is available - Not free user", runFromHereMenuItem.getEnabled());
    }

    @Test
    public void testRunFromHereMenuItemShouldBeDisabledForFreeUser() {
        boolean isFree = true;
        testStepTree.select(testStepTree.getItem(0));
        ExecutionSessionSocketServer executionSessionSocketServer = getExecutionSessionSocketServer();
        executionSessionSocketServer.addExecutionSession(executionSession);
        
        Menu menu = createMenu(testStepTree, isFree);
        MenuItem runFromHereMenuItem = getMenuItemWithLabel(menu, RUN_FROM_HERE_ITEM_LABEL);
        assertTrue(runFromHereMenuItem != null);
        assertFalse(runFromHereMenuItem.getEnabled());
    }

    @Test
    public void testRunFromHereMenuItemShouldBeEnabledForNotFreeUser() {
        boolean isFree = false;
        testStepTree.select(testStepTree.getItem(0));
        ExecutionSessionSocketServer executionSessionSocketServer = getExecutionSessionSocketServer();
        executionSessionSocketServer.addExecutionSession(executionSession);
        
        Menu menu = createMenu(testStepTree, isFree);
        MenuItem runFromHereMenuItem = getMenuItemWithLabel(menu, RUN_FROM_HERE_ITEM_LABEL);
        assertTrue(runFromHereMenuItem != null);
        assertTrue(runFromHereMenuItem.getEnabled());
    }

    @Test
    public void testEnableMenuItemShouldBeDisabledForFreeUser() {
        boolean isFree = true;
        Menu menu = createMenu(testStepTree, isFree);
        MenuItem enableMenuItem = getMenuItemWithLabel(menu, ENABLE_ITEM_LABEL);
        assertTrue(enableMenuItem != null);
        assertFalse(enableMenuItem.getEnabled());
    }

    @Test
    public void testEnableMenuItemShouldBeEnabledForNotFreeUser() {
        boolean isFree = false;
        Menu menu = createMenu(testStepTree, isFree);
        MenuItem enableMenuItem = getMenuItemWithLabel(menu, ENABLE_ITEM_LABEL);
        assertTrue(enableMenuItem != null);
        assertTrue(enableMenuItem.getEnabled());
    }

    @Test
    public void testDisableMenuItemShouldBeDisabledForFreeUser() {
        boolean isFree = true;
        Menu menu = createMenu(testStepTree, isFree);
        MenuItem disableMenuItem = getMenuItemWithLabel(menu, DISABLE_ITEM_LABEL);
        assertTrue(disableMenuItem != null);
        assertFalse(disableMenuItem.getEnabled());
    }

    @Test
    public void testDisableMenuItemShouldBeEnabledForNotFreeUser() {
        boolean isFree = false;
        Menu menu = createMenu(testStepTree, isFree);
        MenuItem disableMenuItem = getMenuItemWithLabel(menu, DISABLE_ITEM_LABEL);
        assertTrue(disableMenuItem != null);
        assertTrue(disableMenuItem.getEnabled());
    }

    private Menu createMenu(Tree testStepTree, boolean isFree) {
        SelectionListener selectionListener = new SelectionAdapter() {}; // a selection listener that does nothing
        TestStepTableContextMenuWrapper menuWrapper = new TestStepTableContextMenuWrapper(testStepTree,
                selectionListener, isFree);
        Menu menu = menuWrapper.getMenu();
        return menu;
    }
    
    private ExecutionSession getDummyAvailableExecutionSession() {
        ExecutionSession executionSession = new ExecutionSession("", "", "", "");
        executionSession.resume(); // invoke resume to make the session available
        return executionSession;
    }
    
    private boolean menuItemWithLabelExists(Menu menu, String menuItemLabel) {
        return getMenuItemWithLabel(menu, menuItemLabel) != null;
    }

    private MenuItem getMenuItemWithLabel(Menu menu, String menuItemLabel) {
        MenuItem[] menuItems = menu.getItems();
        for (MenuItem menuItem : menuItems) {
            if (menuItem.getText().startsWith(menuItemLabel)) {
                return menuItem;
            }
        }
        return null;
    }
    
    private ExecutionSessionSocketServer getExecutionSessionSocketServer() {
        return ExecutionSessionSocketServer.getInstance();
    }
}
