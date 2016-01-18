package com.kms.katalon.objectspy.dialog;

import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
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
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLFrameElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;
import com.kms.katalon.objectspy.element.tree.CheckboxTreeSelectionHelper;
import com.kms.katalon.objectspy.element.tree.HTMLElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.HTMLElementTreeContentProvider;
import com.kms.katalon.objectspy.util.HTMLElementUtil;

public class AddToObjectRepositoryDialog extends TreeEntitySelectionDialog {
    private int fWidth = 60;

    private int fHeight = 18;

    private TreeViewer treeViewer;

    private CheckboxTreeViewer htmlElementTreeViewer;

    private FolderEntity rootFolderEntity;

    private FolderTreeEntity rootFolderTreeEntity;

    private List<HTMLPageElement> htmlElements;
    
    private Object[] expandedHTMLElements;

    private IEventBroker eventBroker;
    

    public AddToObjectRepositoryDialog(Shell parentShell, List<HTMLPageElement> htmlElements, IEventBroker eventBroker, Object[] expandedHTMLElements) {
        super(parentShell, new EntityLabelProvider(), new FolderProvider(),
                new EntityViewerFilter(new FolderProvider()));
        this.htmlElements = htmlElements;
        this.eventBroker = eventBroker;
        this.expandedHTMLElements = expandedHTMLElements;
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

        SashForm form = new SashForm(parent, SWT.HORIZONTAL);

        Composite htmlObjectTreeComposite = new Composite(form, SWT.NONE);
        GridLayout gl_htmlObjectComposite = new GridLayout();
        gl_htmlObjectComposite.marginBottom = 5;
        gl_htmlObjectComposite.horizontalSpacing = 0;
        gl_htmlObjectComposite.marginWidth = 0;
        gl_htmlObjectComposite.marginHeight = 0;
        htmlObjectTreeComposite.setLayout(gl_htmlObjectComposite);

        htmlElementTreeViewer = new CheckboxTreeViewer(htmlObjectTreeComposite, SWT.BORDER | SWT.MULTI);
        HTMLElementTreeContentProvider contentProvider = new HTMLElementTreeContentProvider();
        CheckboxTreeSelectionHelper.attach(htmlElementTreeViewer, contentProvider);
        htmlElementTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

        htmlElementTreeViewer.setContentProvider(contentProvider);
        htmlElementTreeViewer.setLabelProvider(new HTMLElementLabelProvider());

        ColumnViewerToolTipSupport.enableFor(htmlElementTreeViewer, ToolTip.NO_RECREATE);
        htmlElementTreeViewer.setInput(htmlElements);
        if (expandedHTMLElements != null && expandedHTMLElements.length > 0) {
            htmlElementTreeViewer.getControl().setRedraw(false);
            for (Object expandedHTMLElement : expandedHTMLElements) {
                htmlElementTreeViewer.setExpandedState(expandedHTMLElement, true);
            }
            htmlElementTreeViewer.getControl().setRedraw(true);
        }
        
        Composite objectRepositoryComposite = new Composite(form, SWT.NONE);

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
     * 
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
                Object[] checkedHTMLElements = htmlElementTreeViewer.getCheckedElements();
                if (!(checkedHTMLElements != null && checkedHTMLElements.length > 0)) {
                    MessageDialog.openWarning(getParentShell(), StringConstants.WARN,
                            StringConstants.DIA_MSG_PLS_SELECT_ELEMENT);
                    return;
                }
                try {
                    addElementToObjectRepository((FolderTreeEntity) getFirstResult());
                } catch (Exception exception) {
                    LoggerSingleton.logError(exception);
                    MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, exception.getMessage());
                    return;
                }
                setReturnCode(IDialogConstants.OK_ID);
                close();
            }

        });
        
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }

    private void addElementToObjectRepository(FolderTreeEntity targetFolderTreeEntity) throws Exception {
        FolderEntity parentFolder = (FolderEntity) (targetFolderTreeEntity).getObject();
        for (HTMLPageElement pageElement : htmlElements) {
            if (htmlElementTreeViewer.getChecked(pageElement) || htmlElementTreeViewer.getGrayed(pageElement)) {
                for (HTMLElement childElement : pageElement.getChildElements()) {
                    addCheckedElement(childElement, parentFolder, null);
                }
            }
        }
        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, targetFolderTreeEntity.getParent());
        eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, targetFolderTreeEntity);
        eventBroker.send(EventConstants.EXPLORER_EXPAND_TREE_ENTITY, targetFolderTreeEntity);
    }

    private void addCheckedElement(HTMLElement element, FolderEntity parentFolder, WebElementEntity refElement)
            throws Exception {
        WebElementEntity importedElement = null;
        if (htmlElementTreeViewer.getChecked(element) || htmlElementTreeViewer.getGrayed(element)) {
            importedElement = ObjectRepositoryController.getInstance().importWebElement(
                    HTMLElementUtil.convertElementToWebElementEntity(element, refElement, parentFolder), parentFolder);
        }
        if (element instanceof HTMLFrameElement) {
            for (HTMLElement childElement : ((HTMLFrameElement) element).getChildElements()) {
                addCheckedElement(childElement, parentFolder, importedElement);
            }
        }
    }
}
