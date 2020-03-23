package com.kms.katalon.composer.testcase.util.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

import com.kms.katalon.composer.testcase.util.TestCaseMenuUtil;
import com.kms.katalon.execution.session.ExecutionSession;
import com.kms.katalon.execution.session.ExecutionSessionSocketServer;

public class TestCaseMenuUtilTest {

    
    private ExecutionSession executionSession = getDummyAvailableExecutionSession();

    @Before
    public void setUp() {
        ExecutionSessionSocketServer executionSessionSocketServer = getExecutionSessionSocketServer();
        executionSessionSocketServer.removeExecutionSession(executionSession);
    }
    
    @Test
    public void testGeneratedRunFromHereMenuItemShouldBeDisabledIfNoExecutionSessionIsAvailable() {
        Display display = Display.getCurrent();
        Shell shell = new Shell(display);
        SelectionListener selectionListener = new SelectionAdapter() {};
        
        Menu menu = new Menu(shell, SWT.NONE);
        MenuItem runFromHereMenuItem = TestCaseMenuUtil.generateExecuteFromTestStepMenuItem(menu, selectionListener);
        assertTrue(runFromHereMenuItem != null);
        assertFalse("RUN_FROM_HERE item should be disabled", runFromHereMenuItem.getEnabled());
    }
    
    @Test
    public void testRunFromHereMenuItemShouldBeEnabledIfThereIsAnyAvailableExecutionSession() {
        Display display = Display.getCurrent();
        Shell shell = new Shell(display);
        SelectionListener selectionListener = new SelectionAdapter() {};
        
        ExecutionSessionSocketServer executionSessionSocketServer = getExecutionSessionSocketServer();
        executionSessionSocketServer.addExecutionSession(executionSession);
        
        Menu menu = new Menu(shell, SWT.NONE);
        MenuItem runFromHereMenuItem = TestCaseMenuUtil.generateExecuteFromTestStepMenuItem(menu, selectionListener);
        assertTrue(runFromHereMenuItem != null);
        assertTrue("RUN_FROM_HERE item should be enabled", runFromHereMenuItem.getEnabled());
    }
    
    private ExecutionSession getDummyAvailableExecutionSession() {
        ExecutionSession executionSession = new ExecutionSession("", "", "", "");
        executionSession.resume(); // invoke resume to make the session available
        return executionSession;
    }

    private ExecutionSessionSocketServer getExecutionSessionSocketServer() {
        return ExecutionSessionSocketServer.getInstance();
    }
}
