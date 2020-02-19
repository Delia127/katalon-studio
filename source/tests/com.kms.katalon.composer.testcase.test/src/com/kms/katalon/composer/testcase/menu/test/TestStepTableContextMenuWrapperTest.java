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

public class TestStepTableContextMenuWrapperTest {

    private static final String RUN_FROM_HERE_ITEM_LABEL = ComposerTestcaseMessageConstants.ADAP_MENU_CONTEXT_EXECUTE_FROM_TEST_STEP;

    private static final String ENABLE_ITEM_LABEL = ComposerTestcaseMessageConstants.ADAP_MENU_CONTEXT_ENABLE;

    private static final String DISABLE_ITEM_LABEL = ComposerTestcaseMessageConstants.ADAP_MENU_CONTEXT_DISABLE;

    private Tree testStepTree;
    
    @Before
    public void setUp() {
        Display display = Display.getCurrent();
        Shell shell = new Shell(display);
        testStepTree = new Tree(shell, SWT.NONE);
        new TreeItem(testStepTree, SWT.NONE);
    }

    @Test
    public void testRunFromHereMenuItemShouldNotBeDisplayedIfNoStepIsSelected() {
        boolean isFree = true;
        Menu menu = createMenu(testStepTree, isFree);
        assertFalse("RUN_FROM_HERE item should not be display if no step is selected - Free user",
                menuItemWithLabelExists(menu, RUN_FROM_HERE_ITEM_LABEL));

        isFree = false;
        menu = createMenu(testStepTree, isFree);
        assertFalse("RUN_FROM_HERE item should not be display if no step is selected - Not free user",
                menuItemWithLabelExists(menu, RUN_FROM_HERE_ITEM_LABEL));
    }

    @Test
    public void testRunFromHereMenuItemShouldNotBeDisplayedForFreeUser() {
        boolean isFree = true;
        testStepTree.select(testStepTree.getItem(0));
        Menu menu = createMenu(testStepTree, isFree);
        assertFalse(menuItemWithLabelExists(menu, RUN_FROM_HERE_ITEM_LABEL));
    }

    @Test
    public void testRunFromHereMenuItemShouldBeDisplayedForNotFreeUser() {
        boolean isFree = false;
        testStepTree.select(testStepTree.getItem(0));
        Menu menu = createMenu(testStepTree, isFree);
        assertTrue(menuItemWithLabelExists(menu, RUN_FROM_HERE_ITEM_LABEL));
    }

    @Test
    public void testEnableMenuItemShouldNotBeDisplayedForFreeUser() {
        boolean isFree = true;
        Menu menu = createMenu(testStepTree, isFree);
        assertFalse(menuItemWithLabelExists(menu, ENABLE_ITEM_LABEL));
    }

    @Test
    public void testEnableMenuItemShouldBeDisplayedForNotFreeUser() {
        boolean isFree = false;
        Menu menu = createMenu(testStepTree, isFree);
        assertTrue(menuItemWithLabelExists(menu, ENABLE_ITEM_LABEL));
    }

    @Test
    public void testDisableMenuItemShouldNotBeDisplayedForFreeUser() {
        boolean isFree = true;
        Menu menu = createMenu(testStepTree, isFree);
        assertFalse(menuItemWithLabelExists(menu, DISABLE_ITEM_LABEL));
    }

    @Test
    public void testDisableMenuItemShouldBeDisplayedForNotFreeUser() {
        boolean isFree = false;
        Menu menu = createMenu(testStepTree, isFree);
        assertTrue(menuItemWithLabelExists(menu, DISABLE_ITEM_LABEL));
    }

    private Menu createMenu(Tree testStepTree, boolean isFree) {
        SelectionListener selectionListener = new SelectionAdapter() {}; // a selection listener that does nothing
        TestStepTableContextMenuWrapper menuWrapper = new TestStepTableContextMenuWrapper(testStepTree,
                selectionListener, isFree);
        Menu menu = menuWrapper.getMenu();
        return menu;
    }

    private boolean menuItemWithLabelExists(Menu menu, String menuItemLabel) {
        MenuItem[] menuItems = menu.getItems();
        for (MenuItem menuItem : menuItems) {
            if (menuItem.getText().startsWith(menuItemLabel)) {
                return true;
            }
        }
        return false;
    }
}
