package com.kms.katalon.composer.mobile.objectspy.dialog;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.control.TreeEntitySelectionComposite;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.FolderEntityTreeViewerFilter;
import com.kms.katalon.composer.folder.handlers.NewFolderHandler;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;

public class AddElementToObjectRepositoryDialog extends AbstractDialog {

    private static final int NEW_FOLDER_ID = 1025;

    private TreeEntitySelectionComposite treeComposite;

    private FolderTreeEntity selectedFolderTreeEntity;

    public AddElementToObjectRepositoryDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void registerControlModifyListeners() {
        final TreeViewer treeViewer = treeComposite.getTreeViewer();
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (selection == null || selection.isEmpty() || selection.size() != 1) {
                    getButton(OK).setEnabled(false);
                    getButton(NEW_FOLDER_ID).setEnabled(false);
                    return;
                }
                getButton(OK).setEnabled(true);
                getButton(NEW_FOLDER_ID).setEnabled(true);
                selectedFolderTreeEntity = (FolderTreeEntity) selection.getFirstElement();
            }
        });

        getButton(NEW_FOLDER_ID).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                try {
                    FolderTreeEntity newFolderTreeEntity = new NewFolderHandler().createNewFolderEntity(getShell(),
                            selection.toArray(), EventBrokerSingleton.getInstance().getEventBroker());
                    if (newFolderTreeEntity == null) {
                        return;
                    }
                    updateTreeEntityInput();
                    treeViewer.setSelection(new StructuredSelection(newFolderTreeEntity));
                    treeViewer.getControl().setFocus();
                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });
    }

    @Override
    protected void setInput() {
        getButton(OK).setEnabled(false);
        getButton(NEW_FOLDER_ID).setEnabled(false);

        updateTreeEntityInput();
    }

    private void updateTreeEntityInput() {
        treeComposite.setInput(getTreeInput());
        treeComposite.getTreeViewer().expandToLevel(2);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);

        EntityProvider contentProvider = new EntityProvider();
        container.setLayout(new FillLayout(SWT.HORIZONTAL));
        treeComposite = new TreeEntitySelectionComposite(container, SWT.BORDER, contentProvider,
                new FolderEntityTreeViewerFilter(contentProvider), new EntityLabelProvider());

        return container;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(super.getInitialSize().x, 400);
    }

    private Object[] getTreeInput() {
        try {
            return new Object[] { new FolderTreeEntity(FolderController.getInstance().getObjectRepositoryRoot(
                    ProjectController.getInstance().getCurrentProject()), null) };
        } catch (Exception veryImportantException) {
            LoggerSingleton.logError(veryImportantException);
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, NEW_FOLDER_ID, StringConstants.DIA_BTN_NEW_FOLDER, true);
        super.createButtonsForButtonBar(parent);
    }

    public FolderTreeEntity getSelectedFolderTreeEntity() {
        return selectedFolderTreeEntity;
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_FOLDER_BROWSER;
    }
}
