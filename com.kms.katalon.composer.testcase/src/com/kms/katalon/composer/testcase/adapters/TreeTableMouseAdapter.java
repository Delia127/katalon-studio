package com.kms.katalon.composer.testcase.adapters;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.core.model.FailureHandling;

public class TreeTableMouseAdapter extends MouseAdapter {
	private TreeViewer treeViewer;
	private SelectionListener selectionListener;

	public TreeTableMouseAdapter(TreeViewer treeViewer, SelectionListener selectionListener) {
		super();
		this.treeViewer = treeViewer;
		this.selectionListener = selectionListener;
	}

	public void mouseDown(MouseEvent event) {
		if (event.button == 3) {
			Menu menu = treeViewer.getTree().getMenu();
			if (menu != null) {
				menu.dispose();
			}
			final TreeItem treeItem = treeViewer.getTree().getItem(new Point(event.x, event.y));
			if (treeItem == null) {
				return;
			} else {
				menu = new Menu(treeViewer.getTree());
			}

			if (treeViewer.getTree().getSelectionCount() == 1) {
				// Add step add
				AstTreeTableEntityUtil.addActionSubMenu(menu, TreeTableMenuItemConstants.AddAction.Add,
						StringConstants.ADAP_MENU_CONTEXT_ADD, selectionListener);
			
				MenuItem insertMenuItem = new MenuItem(menu, SWT.CASCADE);
				insertMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_INSERT);

				Menu insertMenu = new Menu(menu);
				insertMenuItem.setMenu(insertMenu);

				// Add step before
				AstTreeTableEntityUtil.addActionSubMenu(insertMenu, TreeTableMenuItemConstants.AddAction.InsertBefore,
						StringConstants.ADAP_MENU_CONTEXT_INSERT_BEFORE, selectionListener);

				// Add step after
				AstTreeTableEntityUtil.addActionSubMenu(insertMenu, TreeTableMenuItemConstants.AddAction.InsertAfter,
						StringConstants.ADAP_MENU_CONTEXT_INSERT_AFTER, selectionListener);
			}

			MenuItem removeMenuItem = new MenuItem(menu, SWT.PUSH);
			removeMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_REMOVE);
			removeMenuItem.addSelectionListener(selectionListener);
			removeMenuItem.setID(TreeTableMenuItemConstants.REMOVE_MENU_ITEM_ID);

			MenuItem copyMenuItem = new MenuItem(menu, SWT.PUSH);
			copyMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_COPY);
			copyMenuItem.addSelectionListener(selectionListener);
			copyMenuItem.setID(TreeTableMenuItemConstants.COPY_MENU_ITEM_ID);

			MenuItem cutMenuItem = new MenuItem(menu, SWT.PUSH);
			cutMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_CUT);
			cutMenuItem.addSelectionListener(selectionListener);
			cutMenuItem.setID(TreeTableMenuItemConstants.CUT_MENU_ITEM_ID);

			MenuItem pasteMenuItem = new MenuItem(menu, SWT.PUSH);
			pasteMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_PASTE);
			pasteMenuItem.addSelectionListener(selectionListener);
			pasteMenuItem.setID(TreeTableMenuItemConstants.PASTE_MENU_ITEM_ID);

			addFailureHandlingSubMenu(menu);

			treeViewer.getTree().setMenu(menu);
		}
	}

	public void addFailureHandlingSubMenu(Menu menu) {
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

}
