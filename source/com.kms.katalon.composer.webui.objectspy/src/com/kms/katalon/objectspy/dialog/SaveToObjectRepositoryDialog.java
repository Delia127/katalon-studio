package com.kms.katalon.objectspy.dialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.ConflictWebElementWrapper;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebElement.WebElementType;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.element.tree.CheckboxTreeSelectionHelper;
import com.kms.katalon.objectspy.element.tree.ConflictStatusWebElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.ResolveConflictWebElementTreeContentProvider;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class SaveToObjectRepositoryDialog extends TreeEntitySelectionDialog {
    private int fWidth = 60;

    private int fHeight = 18;

    private TreeViewer treeViewer;

    private boolean isCheckable;

    private TreeViewer htmlElementTreeViewer;

    private FolderEntity rootFolderEntity;

    private FolderTreeEntity rootFolderTreeEntity;

    private List<ConflictWebElementWrapper> wrapConflictStatusPages;

    private ScopedPreferenceStore store;

    private boolean modified;

    private Button btnAddNewObject, btnReplaceObject, btnMergeObject, btnPageAsFolder;

    private StyledText conflictDesciptionLabel;

    private SashForm form;

    private Composite radioGroup;

    private boolean createFolderAsPageNameAllowed;

    private ConflictOptions selectedConflictOptions;

    private ResolveConflictWebElementTreeContentProvider leftTreeContentProvider;

    private CheckboxTreeSelectionHelper checkboxSelectionHelper = null;

    private final int HIGHLIGHTED_LENGTH = "Highlighted".length();
    
    private int selectedHtmlElementCount = 0;
    
    public SaveToObjectRepositoryDialog(Shell parentShell, boolean isCheckable, List<WebPage> pages,
            Object[] expandedHTMLElements) {
        super(parentShell, new EntityLabelProvider(), new FolderProvider(),
                new EntityViewerFilter(new FolderProvider()));
        List<WebPage> tmpPages = new ArrayList<>();
        
        for(Object wp : expandedHTMLElements){
        	if(wp instanceof WebPage){
            	tmpPages.add((WebPage) wp );
        	}
        }
        
        this.isCheckable = isCheckable;
        this.wrapConflictStatusPages = convertToConflictWebPageWrapper(tmpPages);      
        this.store = PreferenceStoreManager.getPreferenceStore(this.getClass());
        this.leftTreeContentProvider = new ResolveConflictWebElementTreeContentProvider(true);
        setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
        setTitle(StringConstants.TITLE_ADD_TO_OBJECT_DIALOG);
        setAllowMultiple(false);
        refresh();
    }

    private List<ConflictWebElementWrapper> convertToConflictWebPageWrapper(List<WebPage> inputPages) {
        List<ConflictWebElementWrapper> conflictPages = new ArrayList<>();
        List<WebPage> flatPages = flattenWebPages(inputPages);
        
        for (WebPage webPage : flatPages) {
            ConflictWebElementWrapper webPageWrapper = new ConflictWebElementWrapper(webPage, false);
            List<ConflictWebElementWrapper> childListWrapper = new ArrayList<>();

            for (WebElement webElement : webPage.getChildren()) {            	
                ConflictWebElementWrapper childWrapper = new ConflictWebElementWrapper(webElement, false);
                childWrapper.setParent(webPageWrapper);
                childListWrapper.add(childWrapper);
            }
            webPageWrapper.setChildren(childListWrapper);
            conflictPages.add(webPageWrapper);
        }
        return conflictPages;
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
        form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createLeftPanel(form);

        createRightPanel(form);

        createConflictOptionsPanel(composite);

        registerConflictOptionsPanelListeners();

        updateInput();

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
        createFolderAsPageNameAllowed = store
                .getBoolean(ObjectSpyPreferenceConstants.WEBUI_DIA_CREATE_FOLDER_AS_PAGE_NAME);
        btnPageAsFolder.setSelection(createFolderAsPageNameAllowed);

        modified = false;
        selectedConflictOptions = Enum.valueOf(ConflictOptions.class,
                store.getString(ObjectSpyPreferenceConstants.WEBUI_DIA_CONFLICT_OPTION));
        btnAddNewObject.setSelection(selectedConflictOptions == ConflictOptions.CREATE_NEW_OBJECT);
        btnMergeObject.setSelection(selectedConflictOptions == ConflictOptions.MERGE_CHANGE_TO_EXISTING_OBJECT);
        btnReplaceObject.setSelection(selectedConflictOptions == ConflictOptions.REPLACE_EXISTING_OBJECT);
    }

    private void createRightPanel(Composite parent) {
        Composite objectRepositoryComposite = new Composite(parent, SWT.NONE);

        Label label = new Label(objectRepositoryComposite, SWT.NONE);
        label.setText(StringConstants.DIA_LBL_SELECT_A_DESTINATION_FOLDER);
        label.setLayoutData(new GridData(SWT.HORIZONTAL));

        treeViewer = createTreeViewer(objectRepositoryComposite);
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                reloadStatusCheckboxViewTree();
            }

        });
        treeViewer.expandToLevel(rootFolderTreeEntity, 1);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = convertWidthInCharsToPixels(fWidth);
        data.heightHint = convertHeightInCharsToPixels(fHeight);

        Tree treeWidget = treeViewer.getTree();
        treeWidget.setLayoutData(data);
        treeWidget.setFont(parent.getFont());
        treeWidget.setEnabled(true);
    }

    private void reloadStatusCheckboxViewTree() {
        // Get current status
        CheckboxTreeViewer checkboxViewer = (CheckboxTreeViewer) htmlElementTreeViewer;
        Object[] checkedlst = checkboxViewer.getCheckedElements();

        checkConflictObjects(wrapConflictStatusPages, createFolderAsPageNameAllowed);

        // Reset the status
        htmlElementTreeViewer.setInput(wrapConflictStatusPages);
        checkboxViewer.setCheckedElements(checkedlst);
        htmlElementTreeViewer.expandAll();
    }

    private void createLeftPanel(Composite parent) {
        Composite htmlObjectTreeComposite = new Composite(parent, SWT.NONE);
        GridLayout glHtmlObjectComposite = new GridLayout();
        glHtmlObjectComposite.marginTop = 5;
        glHtmlObjectComposite.marginBottom = 5;
        glHtmlObjectComposite.horizontalSpacing = 0;
        glHtmlObjectComposite.marginWidth = 0;
        glHtmlObjectComposite.marginHeight = 0;
        htmlObjectTreeComposite.setLayout(glHtmlObjectComposite);

        btnPageAsFolder = new Button(htmlObjectTreeComposite, SWT.CHECK);
        btnPageAsFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnPageAsFolder.setText(StringConstants.DIA_CHCK_BTN_CREATE_FOLDER_AS_PAGE_NAME);

        leftTreeContentProvider.setCreatedNewFolderAsPageName(
                store.getBoolean(ObjectSpyPreferenceConstants.WEBUI_DIA_CREATE_FOLDER_AS_PAGE_NAME));
        if (isCheckable) {
            htmlElementTreeViewer = new CheckboxTreeViewer(htmlObjectTreeComposite, SWT.BORDER | SWT.MULTI);
            checkboxSelectionHelper = CheckboxTreeSelectionHelper.attach((CheckboxTreeViewer) htmlElementTreeViewer,
                    leftTreeContentProvider);
        } else {
            htmlElementTreeViewer = new TreeViewer(htmlObjectTreeComposite, SWT.BORDER | SWT.MULTI);
        }
        htmlElementTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        htmlElementTreeViewer.setContentProvider(leftTreeContentProvider);
        htmlElementTreeViewer.setLabelProvider(new ConflictStatusWebElementLabelProvider());

        ColumnViewerToolTipSupport.enableFor(htmlElementTreeViewer, ToolTip.NO_RECREATE);
        htmlElementTreeViewer.setInput(wrapConflictStatusPages);
        htmlElementTreeViewer.expandAll();

        if (checkboxSelectionHelper != null) {
            // TODO Double check this function
            checkboxSelectionHelper.setCheckAllItems(true);
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

    private void createConflictOptionsPanel(Composite parent) {
        parent.setBackgroundMode(SWT.INHERIT_FORCE);
        conflictDesciptionLabel = new StyledText(parent, SWT.NONE);
        conflictDesciptionLabel.setText(StringConstants.DIA_MSG_RESOLVE_CONFLICT_DES);
        conflictDesciptionLabel.setLayoutData(new GridData(SWT.HORIZONTAL));

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        StyleRange styleRange = new StyleRange();
        styleRange.start = 0;
        styleRange.length = HIGHLIGHTED_LENGTH;
        styleRange.foreground = red;
        conflictDesciptionLabel.setStyleRange(styleRange);
        conflictDesciptionLabel.setBackground(conflictDesciptionLabel.getParent().getBackground());

        radioGroup = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginLeft = 20;
        radioGroup.setLayout(gridLayout);

        btnMergeObject = new Button(radioGroup, SWT.RADIO);
        btnMergeObject.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        btnMergeObject.setText(ObjectspyMessageConstants.DIA_MSG_RADIO_MERGE);

        btnAddNewObject = new Button(radioGroup, SWT.RADIO);
        btnAddNewObject.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        btnAddNewObject.setText(ObjectspyMessageConstants.DIA_MSG_RADIO_DUPLICATE);

        btnReplaceObject = new Button(radioGroup, SWT.RADIO);
        btnReplaceObject.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        btnReplaceObject.setText(ObjectspyMessageConstants.DIA_MSG_RADIO_REPLACE);
    }

    private void setConflictOptionPanelEnable(boolean enabled) {
        btnMergeObject.setEnabled(enabled);
        btnAddNewObject.setEnabled(enabled);
        btnReplaceObject.setEnabled(enabled);
        if (!enabled) {
            Color red = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
            StyleRange styleRange = new StyleRange();
            styleRange.start = 0;
            styleRange.length = conflictDesciptionLabel.getText().length();
            styleRange.foreground = red;

            conflictDesciptionLabel.setBackground(conflictDesciptionLabel.getParent().getBackground());
            conflictDesciptionLabel.setStyleRange(styleRange);
        } else {
            Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
            StyleRange styleRange = new StyleRange();
            styleRange.start = 0;
            styleRange.length = HIGHLIGHTED_LENGTH;
            styleRange.foreground = red;
            conflictDesciptionLabel.setStyleRange(styleRange);
            conflictDesciptionLabel.setBackground(conflictDesciptionLabel.getParent().getBackground());
        }
    }

    private void registerConflictOptionsPanelListeners() {
        btnPageAsFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                createFolderAsPageNameAllowed = btnPageAsFolder.getSelection();
                leftTreeContentProvider.setCreatedNewFolderAsPageName(createFolderAsPageNameAllowed);

                reloadStatusCheckboxViewTree();
                if (createFolderAsPageNameAllowed) {
                    checkboxSelectionHelper.setCheckAllItems(true);
                }
                store.setValue(ObjectSpyPreferenceConstants.WEBUI_DIA_CREATE_FOLDER_AS_PAGE_NAME,
                        createFolderAsPageNameAllowed);
                modified = true;
            }
        });

        SelectionListener selectionListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                btnAddNewObject.setSelection(e.widget == btnAddNewObject);
                btnMergeObject.setSelection(e.widget == btnMergeObject);
                btnReplaceObject.setSelection(e.widget == btnReplaceObject);
                selectedConflictOptions = getSelectedConflictOption();
                store.setValue(ObjectSpyPreferenceConstants.WEBUI_DIA_CONFLICT_OPTION,
                        selectedConflictOptions.toString());
                modified = true;
            }
        };

        btnAddNewObject.addSelectionListener(selectionListener);
        btnReplaceObject.addSelectionListener(selectionListener);
        btnMergeObject.addSelectionListener(selectionListener);
    }

    private ConflictOptions getSelectedConflictOption() {
        return btnAddNewObject.getSelection() ? ConflictOptions.CREATE_NEW_OBJECT : (btnMergeObject.getSelection()
                ? ConflictOptions.MERGE_CHANGE_TO_EXISTING_OBJECT : ConflictOptions.REPLACE_EXISTING_OBJECT);
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
                    FolderEntity parentFolder = ((FolderTreeEntity) selectedObject).getObject();
                    String suggestedName = FolderController.getInstance().getAvailableFolderName(parentFolder,
                            StringConstants.NEW_FOLDER_DEFAULT_NAME);

                    NewFolderDialog newFolderDialog = new NewFolderDialog(getParentShell(), parentFolder);
                    newFolderDialog.setName(suggestedName);
                    newFolderDialog.open();

                    if (newFolderDialog.getReturnCode() == Dialog.OK) {
                        FolderEntity newEntity = FolderController.getInstance().addNewFolder(parentFolder,
                                newFolderDialog.getName());
                        if (newEntity != null) {
                            FolderTreeEntity newFolderTreeEntity = TreeEntityUtil
                                    .createSelectedTreeEntityHierachy(newEntity, rootFolderEntity);
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
                    } else {
                        selectedHtmlElementCount = checkedHTMLElements.length;
                    }

                    removeUncheckedElements(wrapConflictStatusPages);
                }

                setReturnCode(IDialogConstants.OK_ID);
                close();
            }

        });

        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Flatten and just check duplicate object level.
     */
    private boolean checkConflictObjects(List<ConflictWebElementWrapper> newPages, boolean createFolderAsPageNameAllowed) {
        boolean foundConflict = false;
        try {
            for (ConflictWebElementWrapper wrapPage : newPages) {
                for (ConflictWebElementWrapper webElement : wrapPage.getChildren()) {
                    if (webElement.getType() == WebElementType.PAGE)
                        continue;
                    
                    if (webElement instanceof ConflictWebElementWrapper) {
                        ConflictWebElementWrapper wrapElement = (ConflictWebElementWrapper) webElement;
                        WebElement originalWebElement = wrapElement.getOriginalWebElement();

                    FolderTreeEntity selectedParentFolder = (FolderTreeEntity) getFirstResult();
                    String fileRelativePath = selectedParentFolder.getObject().getRelativePath() + File.separator;

                    if (createFolderAsPageNameAllowed) {
                        fileRelativePath = fileRelativePath + StringUtils.trim(wrapPage.getOriginalWebElement().getName()) + File.separator
                                + StringUtils.trim(originalWebElement.getName());
                    } else {
                        fileRelativePath += StringUtils.trim(originalWebElement.getName());
                    }

                    WebElementEntity hitWebElementEntity = ObjectRepositoryController.getInstance()
                            .getWebElementByDisplayPk(fileRelativePath);
                    if (hitWebElementEntity != null) {
                        foundConflict = true;
                    }
                    wrapElement.setIsConflicted(hitWebElementEntity != null);
                    wrapPage.setIsConflicted(hitWebElementEntity != null);
                  
                }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(getParentShell(), StringConstants.ERROR, e.getMessage());
        }

        setConflictOptionPanelEnable(foundConflict);
        return foundConflict;
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

    public enum ConflictOptions {
        CREATE_NEW_OBJECT, REPLACE_EXISTING_OBJECT, MERGE_CHANGE_TO_EXISTING_OBJECT
    }

    private void removeUncheckedElements(List<ConflictWebElementWrapper> elementList) {
        int i = 0;
        while (i < elementList.size()) {
            ConflictWebElementWrapper childElement = elementList.get(i);
            if (!(((CheckboxTreeViewer) htmlElementTreeViewer).getChecked(childElement)
                    || ((CheckboxTreeViewer) htmlElementTreeViewer).getGrayed(childElement))) {
                if (createFolderAsPageNameAllowed || !(childElement.getType() == WebElementType.PAGE)) {
                    elementList.remove(i);
                    continue;
                }
            } else {
                if (childElement.getType() == WebElementType.FRAME || childElement.getType() == WebElementType.PAGE) {
                    removeUncheckedElements(((ConflictWebElementWrapper) childElement).getChildren());
                }
            }
            i++;
        }
    }

    public SaveToObjectRepositoryDialogResult getDialogResult() throws Exception {
        SaveToObjectRepositoryDialogResult dialogResult = new SaveToObjectRepositoryDialogResult(
                createFolderAsPageNameAllowed, getClonePages(), (FolderTreeEntity) getFirstResult(),
                selectedConflictOptions, selectedHtmlElementCount);
        return dialogResult;
    }

    public FolderTreeEntity getSelectedParentFolderResult() {
        return (FolderTreeEntity) getFirstResult();
    }

    public List<ConflictWebElementWrapper> getClonePages() {
        return wrapConflictStatusPages.stream().map(page -> page.softClone()).collect(Collectors.toList());
    }

    private List<WebPage> flattenWebPages(List<WebPage> webPages) {
        List<WebPage> flatPages = new ArrayList<>();
        for (WebPage webPage : webPages) {
            WebPage fPage = new WebPage(webPage.getName());

            List<WebElement> flattenChilds = new ArrayList<>();
            for (WebElement webElement : webPage.getChildren()) {
                flattenChilds.add(webElement);
            }
            fPage.setChildren(flattenChilds);
            flatPages.add(fPage);
        }
        return flatPages;
    }

    public class SaveToObjectRepositoryDialogResult {

        private final boolean createFolderAsPageNameAllowed;

        private final List<ConflictWebElementWrapper> allSelectedPages;

        private final FolderTreeEntity selectedParentFolder;

        private ConflictOptions selectedConflictOption;

        private Map<WebElement, FileEntity> entitySavedMap;
        
        private int selectedHtmlElementCount;

        public SaveToObjectRepositoryDialogResult(boolean createFolderAsPageNameAllowed, List<ConflictWebElementWrapper> selectedPages,
                FolderTreeEntity selectedParentFolder, ConflictOptions selectedConflictOption, int selectedHtmlElementCount) {

            this.createFolderAsPageNameAllowed = createFolderAsPageNameAllowed;
            this.selectedParentFolder = selectedParentFolder;
            this.allSelectedPages = selectedPages;
            this.selectedConflictOption = selectedConflictOption;
            this.selectedHtmlElementCount = selectedHtmlElementCount;
            entitySavedMap = new HashMap<>();
        }

        public boolean isCreateFolderAsPageNameAllowed() {
            return createFolderAsPageNameAllowed;
        }

        public List<ConflictWebElementWrapper> getAllSelectedPages() {
            return allSelectedPages;
        }

        public FolderTreeEntity getSelectedParentFolder() {
            return selectedParentFolder;
        }

        public ConflictOptions getSelectedConflictOption() {
            return selectedConflictOption;
        }

        public Map<WebElement, FileEntity> getEntitySavedMap() {
            return entitySavedMap;
        }
        
        public int getSelectedHtmlElementCount() {
            return selectedHtmlElementCount;
        }
    }
}
