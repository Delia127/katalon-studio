package com.kms.katalon.composer.testsuite.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testcase.TestCaseEntity;

/**
 * Test Case Selection Dialog for Test Suite
 * 
 * @author antruongnguyen
 *
 */
public class TestCaseSelectionDialog extends TreeEntitySelectionDialog {
    private TestCaseTableViewer tableViewer;

    /**
     * Test Case Selection Dialog for Test Suite
     * 
     * @param parent parent shell
     * @param labelProvider entity label provider
     * @param contentProvider tree content provider
     * @param entityViewerFilter entity viewer filter
     * @param tableViewer test case table viewer
     */
    public TestCaseSelectionDialog(Shell parent, IEntityLabelProvider labelProvider,
            ITreeContentProvider contentProvider, AbstractEntityViewerFilter entityViewerFilter,
            TestCaseTableViewer tableViewer) {
        super(parent, labelProvider, contentProvider, entityViewerFilter);
        this.tableViewer = tableViewer;
        setTitle(StringConstants.DIA_TITLE_TEST_CASE_BROWSER);
        setAllowMultiple(true);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.dialogs.SelectionDialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.SELECT_TYPES_ID, StringConstants.DIA_BTN_ADD_N_CONTINUE, false);
        super.createButtonsForButtonBar(parent);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    @Override
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.SELECT_TYPES_ID == buttonId) {
            addSelectedTestCasesPressed();
        } else {
            super.buttonPressed(buttonId);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.dialogs.SelectionStatusDialog#okPressed()
     */
    @Override
    protected void okPressed() {
        computeResult();
        setReturnCode(OK);
        try {
            updateTestCaseTableViewer();
        } catch (Exception e) {}
        close();
    }

    /**
     * "Add selected test cases" button pressed listener
     */
    private void addSelectedTestCasesPressed() {
        computeResult();

        if (getResult() == null || getResult().length == 0) {
            MessageDialog.openWarning(getParentShell(), StringConstants.WARN_TITLE,
                    StringConstants.DIA_WARN_NO_TEST_CASE_SELECTION);
            return;
        }
        setReturnCode(IDialogConstants.SELECT_TYPES_ID);
        try {
            updateTestCaseTableViewer();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    /**
     * Update test case table viewer
     * 
     * @throws Exception
     */
    public void updateTestCaseTableViewer() throws Exception {
        Object[] selectedObjects = getResult();
        for (Object object : selectedObjects) {
            if (!(object instanceof ITreeEntity)) {
                continue;
            }
            ITreeEntity treeEntity = (ITreeEntity) object;
            if (treeEntity instanceof FolderTreeEntity) {
                addTestCaseFolderToTable((FolderEntity) treeEntity.getObject());
            } else if (treeEntity instanceof TestCaseTreeEntity) {
                tableViewer.addTestCase((TestCaseEntity) treeEntity.getObject());
            }
        }
        removeAddedTestCasesInTreeViewer(getTreeViewer().getTree().getItems(), tableViewer.getTestCasesPKs());
    }

    /**
     * Add test case folder into test case table viewer
     * 
     * @param folderEntity
     * @throws Exception
     */
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

    private void removeAddedTestCasesInTreeViewer(TreeItem[] treeItems, List<String> addedTestCaseIds) throws Exception {
        if (addedTestCaseIds.isEmpty()) {
            return;
        }
        for (TreeItem item : treeItems) {
            Object obj = item.getData();
            if (obj instanceof TestCaseTreeEntity) {
                String tcId = ((TestCaseEntity) ((TestCaseTreeEntity) obj).getObject()).getId();
                if (addedTestCaseIds.contains(tcId)) {
                    // hide test case which already added
                    item.dispose();
                }
            } else if (obj instanceof FolderTreeEntity) {
                getTreeViewer().setExpandedState(obj, true);
                removeAddedTestCasesInTreeViewer(item.getItems(), addedTestCaseIds);
                if (item.getItems().length == 0) {
                    // dispose FolderTreeEntity if it has no child
                    item.dispose();
                }
            }
        }
    }

    @Override
    public TreeViewer createTreeViewer(Composite parent) {
        TreeViewer treeViewer = super.createTreeViewer(parent);
        try {
            removeAddedTestCasesInTreeViewer(treeViewer.getTree().getItems(), tableViewer.getTestCasesPKs());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return treeViewer;
    }

    @Override
    protected void filterSearchedText() {
        final String searchString = txtInput.getText();
        Display.getDefault().timerExec(500, new Runnable() {

            @Override
            public void run() {
                try {
                    if (txtInput.isDisposed()) return;
                    if (searchString.equals(txtInput.getText()) && getTreeViewer().getInput() != null) {
                        String broadcastMessage = getSearchMessage();
                        labelProvider.setSearchString(broadcastMessage);
                        entityViewerFilter.setSearchString(broadcastMessage);
                        getTreeViewer().refresh();
                        if (searchString != null && !searchString.isEmpty()) {
                            isSearched = true;
                            getTreeViewer().expandAll();
                        } else {
                            isSearched = false;
                            getTreeViewer().collapseAll();
                        }
                        removeAddedTestCasesInTreeViewer(getTreeViewer().getTree().getItems(),
                                tableViewer.getTestCasesPKs());
                        updateStatusSearchLabel();
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
            }
        });
    }

}
