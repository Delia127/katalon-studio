package com.kms.katalon.objectspy.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.folder.dialogs.NewFolderDialog;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.objectspy.constants.StringConstants;

public class AddToObjectRepositoryDialog extends TreeEntitySelectionDialog {
    private int fWidth = 60;

    private int fHeight = 18;

    private TreeViewer treeViewer;

    private FolderEntity rootFolderEntity;

    private FolderTreeEntity rootFolderTreeEntity;

    public AddToObjectRepositoryDialog(Shell parentShell, IEntityLabelProvider labelProvider,
            ITreeContentProvider contentProvider, AbstractEntityViewerFilter entityViewerFilter) {
        super(parentShell, labelProvider, contentProvider, entityViewerFilter);
        setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        setTitle(StringConstants.TITLE_ADD_TO_OBJECT_DIALOG);
        setAllowMultiple(false);
        refresh();
    }

    private void refresh() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject != null) {
            try {
                rootFolderEntity = FolderController.getInstance().getObjectRepositoryRoot(currentProject);
                rootFolderTreeEntity = new FolderTreeEntity(rootFolderEntity, null);
                setInput(new Object[] { rootFolderTreeEntity });
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                MessageDialog.openError(getParentShell(), StringConstants.ERROR, e.getMessage());
            }
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        parent.setLayout(new GridLayout(1, false));

        Label label = new Label(parent, SWT.NONE);
        label.setText(StringConstants.DIA_LBL_SELECT_A_DESTINATION_FOLDER);
        label.setLayoutData(new GridData(SWT.HORIZONTAL));

        treeViewer = createTreeViewer(parent);
        treeViewer.expandToLevel(rootFolderTreeEntity, 1);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = convertWidthInCharsToPixels(fWidth);
        data.heightHint = convertHeightInCharsToPixels(fHeight);

        Tree treeWidget = treeViewer.getTree();
        treeWidget.setLayoutData(data);
        treeWidget.setFont(parent.getFont());
        treeWidget.setEnabled(true);

        return parent;
    }

    private void refreshTreeEntity(Object object) {
        treeViewer.getControl().setRedraw(false);
        Object[] expandedElements = treeViewer.getExpandedElements();
        if (object == null) {
            treeViewer.refresh();
        } else {
            treeViewer.refresh(object);
        }
        for (Object element : expandedElements) {
            treeViewer.setExpandedState(element, true);
        }
        treeViewer.getControl().setRedraw(true);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.dialogs.SelectionDialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // Create New folder button
        Button btnNewFolder = createButton(parent, 22, StringConstants.DIA_BTN_ADD_NEW_FOLDER, false);
        btnNewFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Object selectedObject = getFirstResult();
                if (selectedObject == null) {
                    // if there is no selection, object repository root will be selected
                    selectedObject = rootFolderTreeEntity;
                }
                try {
                    FolderEntity parentFolder = (FolderEntity) ((FolderTreeEntity) selectedObject).getObject();
                    String suggestedName = FolderController.getInstance().getAvailableFolderName(parentFolder,
                            StringConstants.NEW_FOLDER_DEFAULT_NAME);

                    NewFolderDialog newFolderDialog = new NewFolderDialog(getParentShell(), parentFolder);
                    newFolderDialog.setName(suggestedName);
                    newFolderDialog.open();

                    if (newFolderDialog.getReturnCode() == Dialog.OK) {
                        FolderEntity newEntity = FolderController.getInstance().addNewFolder(parentFolder,
                                newFolderDialog.getName());
                        if (newEntity != null) {
                            FolderTreeEntity newFolderTreeEntity = TreeEntityUtil.createSelectedTreeEntityHierachy(
                                    newEntity, rootFolderEntity);
                            refreshTreeEntity(selectedObject);
                            treeViewer.expandToLevel(selectedObject, 1);
                            treeViewer.setSelection(new StructuredSelection(newFolderTreeEntity));
                        }
                    }
                } catch (Exception exception) {
                    LoggerSingleton.logError(exception);
                    MessageDialog.openError(getParentShell(), StringConstants.ERROR, exception.getMessage());
                }

            }

        });

        super.createButtonsForButtonBar(parent);

        // Handle OK button
        getOkButton().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!(getFirstResult() instanceof FolderTreeEntity)) {
                    MessageDialog.openWarning(getParentShell(), StringConstants.WARN,
                            StringConstants.DIA_MSG_PLS_SELECT_A_FOLDER);
                    return;
                }
                setReturnCode(IDialogConstants.OK_ID);
                close();
            }

        });
    }

}
