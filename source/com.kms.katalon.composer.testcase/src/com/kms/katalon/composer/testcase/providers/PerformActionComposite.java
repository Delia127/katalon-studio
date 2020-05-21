package com.kms.katalon.composer.testcase.providers;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

public interface PerformActionComposite {
	
	public void performToolItemSelected(ToolItem toolItem, SelectionEvent selectionEvent);
	
	public void performMenuItemSelected(MenuItem menuItem);
}
