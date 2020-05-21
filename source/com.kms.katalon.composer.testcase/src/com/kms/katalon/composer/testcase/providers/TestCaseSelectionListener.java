package com.kms.katalon.composer.testcase.providers;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.testcase.parts.TestStepManualComposite;

public class TestCaseSelectionListener extends SelectionAdapter {

	PerformActionComposite performActionComposite;

    public TestCaseSelectionListener(PerformActionComposite performActionComposite) {
        this.performActionComposite = performActionComposite;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        Object item = e.getSource();
        if (item instanceof ToolItem) {
        	performActionComposite.performToolItemSelected((ToolItem) e.getSource(), e);
            return;
        }
        if (item instanceof MenuItem) {
        	performActionComposite.performMenuItemSelected((MenuItem) e.getSource());
        }
    }
}
