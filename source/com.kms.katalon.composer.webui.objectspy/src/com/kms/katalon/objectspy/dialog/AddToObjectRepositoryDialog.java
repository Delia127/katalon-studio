package com.kms.katalon.objectspy.dialog;

import static com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants.WEBUI_DIA_CREATE_FOLDER_AS_PAGE_NAME;

import java.io.IOException;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.explorer.providers.FolderProvider;
import com.kms.katalon.composer.folder.dialogs.NewFolderDialog;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLFrameElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.element.tree.CheckboxTreeSelectionHelper;
import com.kms.katalon.objectspy.element.tree.HTMLElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.HTMLElementTreeContentProvider;
import com.kms.katalon.objectspy.util.HTMLElementUtil;
import com.kms.katalon.objectspy.util.WebElementUtils;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class AddToObjectRepositoryDialog extends TreeEntitySelectionDialog {
    private int fWidth = 60;

    private int fHeight = 18;

    private TreeViewer treeViewer;

    private boolean isCheckable;

    private TreeViewer htmlElementTreeViewer;

    private FolderEntity rootFolderEntity;

    private FolderTreeEntity rootFolderTreeEntity;

    private List<HTMLPageElement> htmlElements;
    private Button btnPageAsFolder;

    private ScopedPreferenceStore store;

    private boolean modified;

    private boolean createFolderAsPageNameAllowed;

    private SashForm form;

    public AddToObjectRepositoryDialog(Shell parentShell, boolean isCheckable, List<HTMLPageElement> htmlElements,
            Object[] expandedHTMLElements) {
        super(parentShell, new EntityLabelProvider(), new FolderProvider(),
                new EntityViewerFilter(new FolderProvider()));
        this.isCheckable = isCheckable;
        this.htmlElements = htmlElements;
        this.store = PreferenceStoreManager.getPreferenceStore(this.getClass());
        setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
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
                setInitialSelection(rootFolderTreeEntity);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                MessageDialog.openError(getParentShell(), StringConstants.ERROR, e.getMessage());
            }
        }
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = createMainDialogArea(parent);

        form = new SashForm(composite, SWT.HORIZONTAL);
        form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createLeftPanel(form);

        createRightPanel(form);

        updateInput();

        registerControlModifyListeners();

        return composite;
    }

    private Composite createMainDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        applyDialogFont(composite);
        return composite;
    }

    private void updateInput() {
        createFolderAsPageNameAllowed = store.getBoolean(WEBUI_DIA_CREATE_FOLDER_AS_PAGE_NAME);
        btnPageAsFolder.setSelection(createFolderAsPageNameAllowed);

        modified = false;
    }

    private void registerControlModifyListeners() {
        btnPageAsFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                createFolderAsPageNameAllowed = btnPageAsFolder.getSelection();
                store.setValue(WEBUI_DIA_CREATE_FOLDER_AS_PAGE_NAME, createFolderAsPageNameAllowed);
                modified = true;
            }
        });
    }

    private void createRightPanel(Composite parent) {
        Composite objectRepositoryComposite = new Composite(parent, SWT.NONE);

        Label label = new Label(objectRepositoryComposite, SWT.NONE);
        label.setText(StringConstants.DIA_LBL_SELECT_A_DESTINATION_FOLDER);
        label.setLayoutData(new GridData(SWT.HORIZONTAL));

        treeViewer = createTreeViewer(objectRepositoryComposite);

        treeViewer.expandToLevel(rootFolderTreeEntity, 1);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = convertWidthInCharsToPixels(fWidth);
        data.heightHint = convertHeightInCharsToPixels(fHeight);

        Tree treeWidget = treeViewer.getTree();
        treeWidget.setLayoutData(data);
        treeWidget.setFont(parent.getFont());
        treeWidget.setEnabled(true);
    }

    private void createLeftPanel(Composite parent) {
        Composite htmlObjectTreeComposite = new Composite(parent, SWT.NONE);
        GridLayout gl_htmlObjectComposite = new GridLayout();
        gl_htmlObjectComposite.marginTop = 5;
        gl_htmlObjectComposite.marginBottom = 5;
        gl_htmlObjectComposite.horizontalSpacing = 0;
        gl_htmlObjectComposite.marginWidth = 0;
        gl_htmlObjectComposite.marginHeight = 0;
        htmlObjectTreeComposite.setLayout(gl_htmlObjectComposite);

        btnPageAsFolder = new Button(htmlObjectTreeComposite, SWT.CHECK);
        btnPageAsFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnPageAsFolder.setText(StringConstants.DIA_CHCK_BTN_CREATE_FOLDER_AS_PAGE_NAME);

        HTMLElementTreeContentProvider contentProvider = new HTMLElementTreeContentProvider();
        CheckboxTreeSelectionHelper checkboxSelectionHelper = null;
        if (isCheckable) {
            htmlElementTreeViewer = new CheckboxTreeViewer(htmlObjectTreeComposite, SWT.BORDER | SWT.MULTI);
            checkboxSelectionHelper = CheckboxTreeSelectionHelper.attach((CheckboxTreeViewer) htmlElementTreeViewer,
                    contentProvider);
        } else {
            htmlElementTreeViewer = new TreeViewer(htmlObjectTreeComposite, SWT.BORDER | SWT.MULTI);
        }
        htmlElementTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        htmlElementTreeViewer.setContentProvider(contentProvider);
        htmlElementTreeViewer.setLabelProvider(new HTMLElementLabelProvider());

        ColumnViewerToolTipSupport.enableFor(htmlElementTreeViewer, ToolTip.NO_RECREATE);
        htmlElementTreeViewer.setInput(htmlElements);
        htmlElementTreeViewer.expandAll();
        if (checkboxSelectionHelper != null) {
            checkboxSelectionHelper.checkAllItemInTree(htmlElements);
        }
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

        Button okButton = createButton(parent, 55, IDialogConstants.OK_LABEL, false);
        // Handle OK button
        okButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!(getFirstResult() instanceof FolderTreeEntity)) {
                    MessageDialog.openWarning(getParentShell(), StringConstants.WARN,
                            StringConstants.DIA_MSG_PLS_SELECT_A_FOLDER);
                    return;
                }
                if (isCheckable) {
                    Object[] checkedHTMLElements = ((CheckboxTreeViewer) htmlElementTreeViewer).getCheckedElements();
                    if (!(checkedHTMLElements != null && checkedHTMLElements.length > 0)) {
                        MessageDialog.openWarning(getParentShell(), StringConstants.WARN,
                                StringConstants.DIA_MSG_PLS_SELECT_ELEMENT);
                        return;
                    }
                    removeUncheckedElements(htmlElements);
                }
                setReturnCode(IDialogConstants.OK_ID);
                close();
            }

        });

        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    private void updatePreferenceStore() {
        if (modified) {
            try {
                store.save();
            } catch (IOException ex) {
                LoggerSingleton.logError(ex);
            }
        }
    }

    @Override
    public boolean close() {
        updatePreferenceStore();
        return super.close();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(600, 600);
    }

    private void removeUncheckedElements(List<? extends HTMLElement> elementList) {
        int i = 0;
        while (i < elementList.size()) {
            HTMLElement childElement = elementList.get(i);
            if (!(((CheckboxTreeViewer) htmlElementTreeViewer).getChecked(childElement) || ((CheckboxTreeViewer) htmlElementTreeViewer).getGrayed(childElement))) {
                elementList.remove(i);
            } else {
                if (childElement instanceof HTMLFrameElement) {
                    removeUncheckedElements(((HTMLFrameElement) childElement).getChildElements());
                }
                i++;
            }
        }
    }

    public List<HTMLPageElement> getHtmlElements() {
        return htmlElements;
    }

    public AddToObjectRepositoryDialogResult getDialogResult() {
        return new AddToObjectRepositoryDialogResult((FolderTreeEntity) getFirstResult(), createFolderAsPageNameAllowed);
    }

    public class AddToObjectRepositoryDialogResult {
        private final boolean createFolderAsPageNameAllowed;

        private final FolderTreeEntity selectedParentFolder;

        public AddToObjectRepositoryDialogResult(FolderTreeEntity selectedParentFolder,
                boolean createFolderAsPageNameAllowed) {
            this.createFolderAsPageNameAllowed = createFolderAsPageNameAllowed;
            this.selectedParentFolder = selectedParentFolder;
        }

        public FolderTreeEntity getSelectedParentFolder() {
            return selectedParentFolder;
        }

        public FolderTreeEntity createTreeFolderForPageElement(WebPage pageElement) throws Exception {
            if (createFolderAsPageNameAllowed) {
                return new FolderTreeEntity(createFolderForPageElement(pageElement), selectedParentFolder);
            }
            return selectedParentFolder;
        }

        public FolderEntity createFolderForPageElement(WebPage pageElement) throws Exception {
            FolderEntity parentFolder = (FolderEntity) (getSelectedParentFolder()).getObject();
            return createFolderAsPageNameAllowed ? newPageWebElementAsFolder(parentFolder, pageElement) : parentFolder;
        }

        private FolderEntity newPageWebElementAsFolder(FolderEntity parentFolder, WebPage pageElement)
                throws Exception {
            return WebElementUtils.convertWebPageToFolder(pageElement, parentFolder);
        }
    }
}
