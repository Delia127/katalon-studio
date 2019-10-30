package com.kms.katalon.composer.keyword.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;

public class KeywordSelectionDialog extends TreeEntitySelectionDialog {
    private List<Object> checkedItems;
    
    public KeywordSelectionDialog(
            Shell parent,
            IEntityLabelProvider labelProvider,
            ITreeContentProvider contentProvider,
            AbstractEntityViewerFilter entityViewerFilter) {
        
        super(parent, labelProvider, contentProvider, entityViewerFilter);
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
                onStateChangedTreeItem(item.getData(), item.getChecked());
            }
        });
        return treeViewer;
    }
    
    @Override
    protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
        return new ContainerCheckedTreeViewer(new Tree(parent, SWT.CHECK | style));
    }
    
    private void onStateChangedTreeItem(Object element, boolean isChecked) {
        if (element instanceof KeywordTreeEntity) {
            if (isChecked) {
                checkedItems.add(element);
            } else {
                checkedItems.remove(element);
            }
            return;
        }
        if (element instanceof PackageTreeEntity) {
            try {
                for (Object childElement : ((PackageTreeEntity) element).getChildren()) {
                    onStateChangedTreeItem(childElement, isChecked);
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        if (element instanceof FolderTreeEntity) {
            try {
                for (Object childElement : ((FolderTreeEntity) element).getChildren()) {
                    onStateChangedTreeItem(childElement, isChecked);
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
    
    public KeywordTreeEntity[] getSelectedKeywords() throws Exception {
        List<KeywordTreeEntity> selectedTreeEntities = flattenDialogResult(getResult());
        return selectedTreeEntities.toArray(new KeywordTreeEntity[selectedTreeEntities.size()]);
    }

    private List<KeywordTreeEntity> flattenDialogResult(Object[] dialogResult) throws Exception {
        if (dialogResult == null) {
            return Collections.emptyList();
        }
        List<KeywordTreeEntity> selectedTreeEntities = new ArrayList<>();
        for (Object eachResult : dialogResult) {
            if (eachResult instanceof KeywordTreeEntity) {
                selectedTreeEntities.add((KeywordTreeEntity) eachResult);
            } else if (eachResult instanceof FolderTreeEntity) {
                selectedTreeEntities.addAll(flattenDialogResult(((FolderTreeEntity) eachResult).getChildren()));
            } else {
                continue;
            }
        }
        return selectedTreeEntities;
    }
}
