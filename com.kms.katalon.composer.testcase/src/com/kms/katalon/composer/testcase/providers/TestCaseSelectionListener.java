package com.kms.katalon.composer.testcase.providers;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.testcase.parts.TestCasePart;

public class TestCaseSelectionListener implements SelectionListener {
    private TestCasePart parentTestCasePart;

    public TestCaseSelectionListener(TestCasePart parentTestCasePart) {
        this.parentTestCasePart = parentTestCasePart;
    }
    
    @Override
    public void widgetSelected(SelectionEvent e) {
        // TODO Auto-generated method stub
        if (e.getSource() instanceof Button) {
        	parentTestCasePart.performButtonSelected((Button) e.getSource());
        } else if (e.getSource() instanceof ToolItem) {
        	parentTestCasePart.performToolItemSelected((ToolItem) e.getSource(), e);
        } else if (e.getSource() instanceof MenuItem) {
        	parentTestCasePart.performMenuItemSelected((MenuItem) e.getSource());
        }
    }
    
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub
        
    }

}
