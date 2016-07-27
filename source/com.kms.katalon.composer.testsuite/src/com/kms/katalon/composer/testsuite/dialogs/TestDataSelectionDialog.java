package com.kms.katalon.composer.testsuite.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.constants.StringConstants;

public class TestDataSelectionDialog extends TreeEntitySelectionDialog {

    private List<Object> checkedItems;

    public TestDataSelectionDialog(Shell parent, IEntityLabelProvider labelProvider,
            ITreeContentProvider contentProvider, AbstractEntityViewerFilter entityViewerFilter) {
        super(parent, labelProvider, contentProvider, entityViewerFilter);
        setTitle(StringConstants.LIS_TITLE_TEST_DATA_BROWSER);
        setAllowMultiple(false);
        setDoubleClickSelects(false);
        checkedItems = new ArrayList<>();
    }

    @Override
    public TreeViewer createTreeViewer(Composite parent) {
        final ContainerCheckedTreeViewer treeViewer = (ContainerCheckedTreeViewer) super.createTreeViewer(parent);
        treeViewer.getTree().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.detail != SWT.CHECK) {
                    return;
                }
                TreeItem item = (TreeItem) e.item;
                treeViewer.getTree().setSelection(item);
                onStageChangedTreeItem(item.getData(), item.getChecked());
            }
        });
        return treeViewer;
    }

    @Override
    protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
        return new ContainerCheckedTreeViewer(new Tree(parent, SWT.CHECK | style));
    }

    /**
     * Check/Uncheck TreeItem action
     * 
     * @param element {@link TreeItem#getData()}
     * @param isChecked whether TreeItem is checked or not
     */
    private void onStageChangedTreeItem(Object element, boolean isChecked) {
        if (element instanceof TestDataTreeEntity) {
            if (isChecked) {
                checkedItems.add(element);
            } else {
                checkedItems.remove(element);
            }
            return;
        }

        if (element instanceof FolderTreeEntity) {
            try {
                for (Object childElement : TreeEntityUtil.getChildren((FolderTreeEntity) element)) {
                    onStageChangedTreeItem(childElement, isChecked);
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    @Override
    protected void computeResult() {
        ContainerCheckedTreeViewer treeViewer = (ContainerCheckedTreeViewer) getTreeViewer();
        List<Object> grayedItems = Arrays.asList(treeViewer.getGrayedElements());
        checkedItems.removeAll(grayedItems);
        setResult(checkedItems);
    }

}
