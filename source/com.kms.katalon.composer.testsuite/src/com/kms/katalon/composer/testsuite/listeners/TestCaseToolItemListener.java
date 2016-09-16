package com.kms.katalon.composer.testsuite.listeners;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.constants.ToolItemConstants;
import com.kms.katalon.composer.testsuite.dialogs.TestCaseSelectionDialog;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class TestCaseToolItemListener extends SelectionAdapter {

    private TestCaseTableViewer tableViewer;

    public TestCaseToolItemListener(TestCaseTableViewer tableViewer) {
        setTableViewer(tableViewer);
    }

    private void setTableViewer(TestCaseTableViewer tableViewer) {
        this.tableViewer = tableViewer;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() == null) return;

        if (e.getSource() instanceof ToolItem) {
            toolItemSelected(e);
        }
    }

    private void toolItemSelected(SelectionEvent e) {
        ToolItem toolItem = (ToolItem) e.getSource();
        String data = (String) toolItem.getData();

        if (data == null || data.isEmpty()) {
            return;
        }
        if (ToolItemConstants.ADD.equals(data)) {
            addTestCaseLink();
            return;
        }
        if (ToolItemConstants.REMOVE.equals(data)) {
            removeTestCaseLink();
            return;
        }
        if (ToolItemConstants.UP.equals(data)) {
            moveUpTestCaseLink();
            return;
        }
        if (ToolItemConstants.DOWN.equals(data)) {
            moveDownTestCaseLink();
            return;
        }
    }

    @SuppressWarnings("unchecked")
    private void moveDownTestCaseLink() {
        tableViewer.downTestCase(((IStructuredSelection) tableViewer.getSelection()).toList());
    }

    @SuppressWarnings("unchecked")
    private void moveUpTestCaseLink() {
        tableViewer.upTestCase(((IStructuredSelection) tableViewer.getSelection()).toList());
    }

    @SuppressWarnings("unchecked")
    private void removeTestCaseLink() {
        try {
            tableViewer.removeTestCases(((IStructuredSelection) tableViewer.getSelection()).toList());
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
    }

    public void addTestCaseLink() {
        try {
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            if (currentProject == null) {
                return;
            }

            TestCaseSelectionDialog dialog = new TestCaseSelectionDialog(null, new EntityLabelProvider(),
                    new EntityProvider(), new EntityViewerFilter(new EntityProvider()), tableViewer);
            dialog.setInput(TreeEntityUtil.getChildren(null,
                    FolderController.getInstance().getTestCaseRoot(currentProject)));
            dialog.open();
        } catch (Exception ex) {
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_ADD_TEST_CASES);
            LoggerSingleton.logError(ex);
        }
    }

}
