package com.kms.katalon.composer.testcase.dialogs;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.control.TreeEntitySelectionComposite;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.FolderEntityTreeViewerFilter;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;

public class TestCaseFolderSelectionDialog extends AbstractDialog {
    
    private static final String DEFAULT_DIALOG_NAME = 
                ComposerTestcaseMessageConstants.DIALOG_TITLE_TEST_CASE_FOLDER_SELECTION;
    
    private TreeEntitySelectionComposite folderTreeComposite;
    
    private FolderTreeEntity selectedFolder;
    
    private String dialogName;
    
    public TestCaseFolderSelectionDialog(Shell parentShell) {
        this(parentShell, null);
    }

    public TestCaseFolderSelectionDialog(Shell parentShell, String dialogName) {
        super(parentShell);
        this.dialogName = !StringUtils.isBlank(dialogName) ? dialogName : DEFAULT_DIALOG_NAME;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        
        EntityProvider contentProvider = new EntityProvider();
        folderTreeComposite = new TreeEntitySelectionComposite(container, SWT.BORDER, contentProvider,
                new FolderEntityTreeViewerFilter(contentProvider), new EntityLabelProvider());
        folderTreeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout treeLayout = new GridLayout(1, false);
        treeLayout.marginWidth = 0;
        treeLayout.marginHeight = 0;
        folderTreeComposite.setLayout(treeLayout);
        folderTreeComposite.getTreeViewer().setAutoExpandLevel(TreeViewer.ALL_LEVELS);
        
        return container;
    }

    @Override
    protected void registerControlModifyListeners() {
        TreeViewer treeViewer = folderTreeComposite.getTreeViewer();
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                selectedFolderChanged();
            }
        });

        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object firstElement = treeViewer.getStructuredSelection().getFirstElement();
                treeViewer.setExpandedState(firstElement, !treeViewer.getExpandedState(firstElement));
            }
        });
    }
    
    private void selectedFolderChanged() {
        selectedFolder = (FolderTreeEntity) folderTreeComposite.getTreeViewer()
                .getStructuredSelection()
                .getFirstElement();
    }

    @Override
    protected void setInput() {
        try {
            FolderEntity testCaseRoot = FolderController.getInstance()
                    .getTestCaseRoot(ProjectController.getInstance().getCurrentProject());

            selectedFolder = TreeEntityUtil.createSelectedTreeEntityHierachy(testCaseRoot, testCaseRoot);
            folderTreeComposite.setInput(new Object[] { selectedFolder });
            folderTreeComposite.getTreeViewer().setSelection(new StructuredSelection(selectedFolder));
            selectedFolderChanged();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
    
    public FolderTreeEntity getSelectedFolder() {
        return selectedFolder;
    }
    
    @Override
    protected Point getInitialSize() {
        return new Point(500, 400);
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }
    
    @Override
    public String getDialogTitle() {
        return dialogName;
    }
}
