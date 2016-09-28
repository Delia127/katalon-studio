package com.kms.katalon.composer.testsuite.listeners;

import java.util.List;

import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import com.kms.katalon.composer.components.impl.util.KeyEventUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;

public class TestCaseTableKeyListener extends KeyAdapter {

    private TestCaseTableViewer viewer;

    public TestCaseTableKeyListener(TestCaseTableViewer viewer) {
        this.viewer = viewer;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void keyPressed(KeyEvent e) {
        // Control/Command + N
        if (KeyEventUtil.isKeysPressed(e, new String[] { IKeyLookup.M1_NAME, "N" })) {
            try {
                viewer.addNewItem();
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
            }
            return;
        }

        List<TestSuiteTestCaseLink> selectedRows = ((IStructuredSelection) viewer.getSelection()).toList();
        if (selectedRows == null || selectedRows.isEmpty()) {
            e.doit = false;
        }

        // Delete
        if (KeyEventUtil.isKeysPressed(e, IKeyLookup.DEL_NAME)) {
            try {
                viewer.removeSelectedItems();
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
            }
            return;
        }

        // Control/Command + Up
        if (KeyEventUtil.isKeysPressed(e, SWT.MOD1, SWT.ARROW_UP)) {
            viewer.moveSelectedItemsUp();
            return;
        }

        // Control/Command + Down
        if (KeyEventUtil.isKeysPressed(e, SWT.MOD1, SWT.ARROW_DOWN)) {
            viewer.moveSelectedItemsDown();
        }
    }

}
