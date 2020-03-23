package com.kms.katalon.composer.testcase.menu;

import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import com.kms.katalon.composer.components.impl.util.KeyEventUtil;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.util.TestCaseMenuUtil;
import com.kms.katalon.core.model.FailureHandling;

public class TestStepTableContextMenuWrapper {

    private Tree parentTableTree;

    private SelectionListener selectionListener;

    private boolean isFreeLicense;

    private Menu menu;

    public TestStepTableContextMenuWrapper(Tree parentTableTree, SelectionListener selectionListener,
            boolean isFreeLicense) {

        this.parentTableTree = parentTableTree;
        this.selectionListener = selectionListener;
        this.isFreeLicense = isFreeLicense;

        createMenu();
    }

    private void createMenu() {
        menu = new Menu(parentTableTree);

        if (parentTableTree.getSelectionCount() == 1) {
            MenuItem executeFromTestStepMenuItem = TestCaseMenuUtil.generateExecuteFromTestStepMenuItem(menu, selectionListener);
            if (isFreeLicense && executeFromTestStepMenuItem.isEnabled()) {
                executeFromTestStepMenuItem.setEnabled(false);
            }
           
            new MenuItem(menu, SWT.SEPARATOR);

            // Add step add
            TestCaseMenuUtil.addActionSubMenu(menu, TreeTableMenuItemConstants.AddAction.Add,
                    StringConstants.ADAP_MENU_CONTEXT_ADD, selectionListener);

            MenuItem insertMenuItem = new MenuItem(menu, SWT.CASCADE);
            insertMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_INSERT);

            Menu insertMenu = new Menu(menu);
            insertMenuItem.setMenu(insertMenu);

            // Add step before
            TestCaseMenuUtil.addActionSubMenu(insertMenu, TreeTableMenuItemConstants.AddAction.InsertBefore,
                    StringConstants.ADAP_MENU_CONTEXT_INSERT_BEFORE, selectionListener);

            // Add step after
            TestCaseMenuUtil.addActionSubMenu(insertMenu, TreeTableMenuItemConstants.AddAction.InsertAfter,
                    StringConstants.ADAP_MENU_CONTEXT_INSERT_AFTER, selectionListener);
        }

        MenuItem removeMenuItem = new MenuItem(menu, SWT.PUSH);
        removeMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_REMOVE,
                KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.DEL_NAME })));
        removeMenuItem.addSelectionListener(selectionListener);
        removeMenuItem.setID(TreeTableMenuItemConstants.REMOVE_MENU_ITEM_ID);

        MenuItem copyMenuItem = new MenuItem(menu, SWT.PUSH);
        copyMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_COPY,
                KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "C" }))); //$NON-NLS-1$
        copyMenuItem.addSelectionListener(selectionListener);
        copyMenuItem.setID(TreeTableMenuItemConstants.COPY_MENU_ITEM_ID);

        MenuItem cutMenuItem = new MenuItem(menu, SWT.PUSH);
        cutMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_CUT,
                KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "X" }))); //$NON-NLS-1$
        cutMenuItem.addSelectionListener(selectionListener);
        cutMenuItem.setID(TreeTableMenuItemConstants.CUT_MENU_ITEM_ID);

        MenuItem pasteMenuItem = new MenuItem(menu, SWT.PUSH);
        pasteMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_PASTE,
                KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "V" }))); //$NON-NLS-1$
        pasteMenuItem.addSelectionListener(selectionListener);
        pasteMenuItem.setID(TreeTableMenuItemConstants.PASTE_MENU_ITEM_ID);

        new MenuItem(menu, SWT.SEPARATOR);

        addFailureHandlingSubMenu(menu);

        MenuItem disableMenuItem = new MenuItem(menu, SWT.PUSH);
        disableMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_DISABLE,
                KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "/" }))); //$NON-NLS-1$
        disableMenuItem.addSelectionListener(selectionListener);
        disableMenuItem.setID(TreeTableMenuItemConstants.DISABLE_MENU_ITEM_ID);
        if (isFreeLicense) {
            disableMenuItem.setEnabled(false);
        }

        MenuItem enableMenuItem = new MenuItem(menu, SWT.PUSH);
        enableMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_ENABLE,
                KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.ALT_NAME, IKeyLookup.M1_NAME, "/" }))); //$NON-NLS-1$
        enableMenuItem.addSelectionListener(selectionListener);
        enableMenuItem.setID(TreeTableMenuItemConstants.ENABLE_MENU_ITEM_ID);
        if (isFreeLicense) {
            enableMenuItem.setEnabled(false);
        }
    }

    private String createMenuItemLabel(String text, String keyCombination) {
        return text + "\t" + keyCombination; //$NON-NLS-1$
    }

    private void addFailureHandlingSubMenu(Menu menu) {
        MenuItem failureHandlingMenuItem = new MenuItem(menu, SWT.CASCADE);
        failureHandlingMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_CHANGE_FAILURE_HANDLING);
        failureHandlingMenuItem.addSelectionListener(selectionListener);

        Menu failureHandlingMenu = new Menu(menu);

        MenuItem failureStopMenuItem = new MenuItem(failureHandlingMenu, SWT.NONE);
        failureStopMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_STOP_ON_FAILURE);
        failureStopMenuItem.addSelectionListener(selectionListener);
        failureStopMenuItem.setID(TreeTableMenuItemConstants.CHANGE_FAILURE_HANDLING_MENU_ITEM_ID);
        failureStopMenuItem.setData(TreeTableMenuItemConstants.FAILURE_HANDLING_KEY, FailureHandling.STOP_ON_FAILURE);

        MenuItem failureContinueMenuItem = new MenuItem(failureHandlingMenu, SWT.NONE);
        failureContinueMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_CONTINUE_ON_FAILURE);
        failureContinueMenuItem.addSelectionListener(selectionListener);
        failureContinueMenuItem.setID(TreeTableMenuItemConstants.CHANGE_FAILURE_HANDLING_MENU_ITEM_ID);
        failureContinueMenuItem.setData(TreeTableMenuItemConstants.FAILURE_HANDLING_KEY,
                FailureHandling.CONTINUE_ON_FAILURE);

        MenuItem optionalMenuItem = new MenuItem(failureHandlingMenu, SWT.NONE);
        optionalMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_OPTIONAL);
        optionalMenuItem.addSelectionListener(selectionListener);
        optionalMenuItem.setID(TreeTableMenuItemConstants.CHANGE_FAILURE_HANDLING_MENU_ITEM_ID);
        optionalMenuItem.setData(TreeTableMenuItemConstants.FAILURE_HANDLING_KEY, FailureHandling.OPTIONAL);

        failureHandlingMenuItem.setMenu(failureHandlingMenu);
    }

    public Menu getMenu() {
        return menu;
    }
}
