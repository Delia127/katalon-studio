package com.kms.katalon.composer.testsuite.listeners;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;

public class TestCaseTableKeyListener implements KeyListener {

	private TestCaseTableViewer viewer;
	
	public TestCaseTableKeyListener(TestCaseTableViewer viewer) {
		this.viewer = viewer;
	}
	
    @SuppressWarnings({ "unchecked" })
	@Override
    public void keyPressed(KeyEvent e) {
    	switch (e.keyCode) {
        case SWT.DEL:
        	try {
                viewer.removeTestCases(((IStructuredSelection)viewer.getSelection()).toList());
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
            }
        	break;
    	}
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
