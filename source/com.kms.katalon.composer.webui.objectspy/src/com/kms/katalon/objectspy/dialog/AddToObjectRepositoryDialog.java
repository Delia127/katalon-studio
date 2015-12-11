package com.kms.katalon.objectspy.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.objectspy.constants.StringConstants;

public class AddToObjectRepositoryDialog extends TreeEntitySelectionDialog {
    private static final String DIALOG_TITLE = StringConstants.TITLE_ADD_TO_OBJECT_DIALOG;

    private AddToObjectRepositoryDialog _instance;
    private int fWidth = 60;
    private int fHeight = 18;

    private ToolItem tltmAdd;
    private TreeViewer treeViewer;
    private FolderEntity objectRepositoryRoot;

    public AddToObjectRepositoryDialog(Shell parentShell, IEntityLabelProvider labelProvider,
            ITreeContentProvider contentProvider, AbstractEntityViewerFilter entityViewerFilter) {
        super(parentShell, labelProvider, contentProvider, entityViewerFilter);
        setAllowMultiple(false);
        refresh();
        _instance = this;
    }

    private void refresh() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject != null) {
            try {
                objectRepositoryRoot = FolderController.getInstance().getObjectRepositoryRoot(currentProject);
                setInput(TreeEntityUtil.getChildren(new FolderTreeEntity(objectRepositoryRoot, null), objectRepositoryRoot));
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

        final Composite objectFinderComposite = new Composite(parent, SWT.NONE);
        objectFinderComposite.setLayout(new GridLayout(1, false));

        Composite compositeToolbar = new Composite(objectFinderComposite, SWT.NONE);
        compositeToolbar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_compositeToolbar = new GridLayout(1, false);
        gl_compositeToolbar.marginWidth = 0;
        gl_compositeToolbar.marginHeight = 0;
        compositeToolbar.setLayout(gl_compositeToolbar);
        compositeToolbar.setBackground(ColorUtil.getCompositeBackgroundColor());

        ToolBar toolBar = new ToolBar(compositeToolbar, SWT.FLAT);

        tltmAdd = new ToolItem(toolBar, SWT.NONE);
        tltmAdd.setText(StringConstants.DIA_BTN_ADD_NEW_FOLDER);
        tltmAdd.setToolTipText(StringConstants.DIA_BTN_ADD_NEW_FOLDER);
        tltmAdd.setImage(ImageConstants.IMG_16_ADD);

        tltmAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object selectedObject = getFirstResult();
                if (selectedObject instanceof FolderTreeEntity) {
                    try {
                        FolderEntity parentFolder = (FolderEntity) ((FolderTreeEntity) selectedObject).getObject();
                        String suggestedName = FolderController.getInstance().getAvailableFolderName(parentFolder,
                                StringConstants.NEW_FOLDER_DEFAULT_NAME);

                        FolderEntity newEntity = FolderController.getInstance().addNewFolder(parentFolder,
                                suggestedName);

                        if (newEntity != null) {
                            FolderTreeEntity newFolderTreeEntity = createSelectedTreeEntityHierachy(newEntity,
                                    objectRepositoryRoot);
                            refreshTreeEntity(selectedObject);
                            treeViewer.expandToLevel(selectedObject, 1);
                            treeViewer.setSelection(new StructuredSelection(newFolderTreeEntity));
                        }
                    } catch (Exception exception) {
                        LoggerSingleton.logError(exception);
                        MessageDialog.openError(getParentShell(), StringConstants.ERROR, exception.getMessage());
                    }
                }
            }
        });

        treeViewer = createTreeViewer(objectFinderComposite);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = convertWidthInCharsToPixels(fWidth);
        data.heightHint = convertHeightInCharsToPixels(fHeight);

        Tree treeWidget = treeViewer.getTree();
        treeWidget.setLayoutData(data);
        treeWidget.setFont(parent.getFont());
        treeWidget.setEnabled(true);

        return objectFinderComposite;
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

    private FolderTreeEntity createSelectedTreeEntityHierachy(FolderEntity folderEntity, FolderEntity rootFolder) {
        if (folderEntity == null || folderEntity.equals(rootFolder)) {
            return null;
        }
        return new FolderTreeEntity(folderEntity, createSelectedTreeEntityHierachy(folderEntity.getParentFolder(),
                rootFolder));
    }

    protected void createButtonsForButtonBar(Composite parent) {
        Button btnOK = createButton(parent, 102, IDialogConstants.OK_LABEL, true);
        btnOK.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!(getFirstResult() instanceof FolderTreeEntity)) {
                    MessageDialog.openWarning(getParentShell(), StringConstants.WARN, "Please select a folder");
                    return;
                }
                _instance.close();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getDialogTitle());
    }

    public String getDialogTitle() {
        return DIALOG_TITLE;
    }
}
