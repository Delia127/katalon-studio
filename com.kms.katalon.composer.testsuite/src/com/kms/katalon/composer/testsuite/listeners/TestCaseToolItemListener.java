package com.kms.katalon.composer.testsuite.listeners;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.constants.ToolItemConstants;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

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

        switch (data) {
            case ToolItemConstants.ADD:
                addTestCaseLink(toolItem);
                return;
            case ToolItemConstants.REMOVE:
                removeTestCaseLink(toolItem);
                return;
            case ToolItemConstants.UP:
                moveUpTestCaseLink(toolItem);
                return;
            case ToolItemConstants.DOWN:
                moveDownTestCaseLink(toolItem);
                return;
            default:
                return;
        }
    }

    @SuppressWarnings("unchecked")
    private void moveDownTestCaseLink(ToolItem toolItem) {
        tableViewer.downTestCase(((IStructuredSelection) tableViewer.getSelection()).toList());
    }

    @SuppressWarnings("unchecked")
    private void moveUpTestCaseLink(ToolItem toolItem) {
        tableViewer.upTestCase(((IStructuredSelection) tableViewer.getSelection()).toList());
    }

    @SuppressWarnings("unchecked")
    private void removeTestCaseLink(ToolItem toolItem) {
        try {
            tableViewer.removeTestCases(((IStructuredSelection) tableViewer.getSelection()).toList());
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
    }

    private void addTestCaseLink(ToolItem toolItem) {
        try {
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            if (currentProject == null) {
                return;
            }
            
            EntityProvider entityProvider = new EntityProvider();
            TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(toolItem.getDisplay().getActiveShell(),
                    new EntityLabelProvider(), new EntityProvider(), new EntityViewerFilter(entityProvider));

            dialog.setAllowMultiple(true);
            dialog.setTitle(StringConstants.PA_TITLE_TEST_CASE_BROWSER);
            dialog.setInput(TreeEntityUtil.getChildren(null,
                    FolderController.getInstance().getTestCaseRoot(currentProject)));
            if (dialog.open() == Dialog.OK) {
                Object[] selectedObjects = dialog.getResult();
                for (Object object : selectedObjects) {
                    if (!(object instanceof ITreeEntity)) {
                        continue;
                    }
                    ITreeEntity treeEntity = (ITreeEntity) object;
                    if (treeEntity.getObject() instanceof FolderEntity) {
                        addTestCaseFolderToTable((FolderEntity) treeEntity.getObject());
                    } else if (treeEntity.getObject() instanceof TestCaseEntity) {
                        tableViewer.addTestCase((TestCaseEntity) treeEntity.getObject());
                    }
                }
            }
        } catch (Exception ex) {
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_ADD_TEST_CASES);
            LoggerSingleton.logError(ex);
        }
    }

    private void addTestCaseFolderToTable(FolderEntity folderEntity) throws Exception {
        if (folderEntity.getFolderType() == FolderType.TESTCASE) {
            FolderController folderController = FolderController.getInstance();
            for (Object childObject : folderController.getChildren(folderEntity)) {
                if (childObject instanceof TestCaseEntity) {
                    tableViewer.addTestCase((TestCaseEntity) childObject);
                } else if (childObject instanceof FolderEntity) {
                    addTestCaseFolderToTable((FolderEntity) childObject);
                }
            }
        }
    }
}
