package com.kms.katalon.objectspy.dialog;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.egit.ui.UIUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.core.HTMLElementCollector;
import com.kms.katalon.objectspy.dialog.ObjectRepositoryService.SaveActionResult;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebElement.WebElementType;
import com.kms.katalon.objectspy.element.WebFrame;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.element.tree.WebElementTreeContentProvider;
import com.kms.katalon.objectspy.util.UtilitiesAddonUtil;
import com.kms.katalon.objectspy.util.WebElementUtils;
import com.kms.katalon.objectspy.websocket.AddonSocket;
import com.kms.katalon.objectspy.websocket.AddonSocketServer;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.listener.EventListener;

@SuppressWarnings("restriction")
public class NewObjectSpyDialog extends Dialog
        implements EventHandler, HTMLElementCollector, EventListener<ObjectSpyEvent> {

    private static final String DIA_BOUNDS_SET = "DIALOG_BOUNDS_SET";

    private static final Point MIN_SIZE = new Point(500, 720);

    private static final int CAPTURED_OBJECT_VIEW_MIN_SIZE = 100;

    private List<WebPage> pages;

    private Logger logger;

    private ToolBar mainToolbar;

    private ToolItem addPageElementToolItem;

    private ToolItem addFrameElementToolItem;

    private ToolItem addElementToolItem;

    private ToolItem removeElementToolItem;

    private ToolItem addElmtToObjRepoToolItem;

    private IEventBroker eventBroker;

    private boolean isDisposed;

    private ObjectSpyUrlView urlView;

    private CapturedObjectsView capturedObjectsView;

    private ObjectPropertiesView objectPropertiesView;

    private Shell shell;

    private ObjectVerifyAndHighlightView verifyView;

    private ObjectSpySelectorEditor selectorEditor;

    private Composite bodyComposite;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     * @param logger
     * @param eventBroker
     * @throws Exception
     */
    public NewObjectSpyDialog(Shell parentShell, Logger logger, IEventBroker eventBroker) throws Exception {
        super(parentShell);
        boolean onTop = getPreferenceStore().getBoolean(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_PIN_WINDOW);
        if (onTop && !Platform.OS_LINUX.equals(Platform.getOS())) {
            setShellStyle(SWT.SHELL_TRIM | SWT.ON_TOP | SWT.CENTER);
        } else {
            setShellStyle(SWT.SHELL_TRIM | SWT.CENTER);
        }

        this.shell = getShell();
        this.logger = logger;
        this.eventBroker = eventBroker;
        eventBroker.subscribe(EventConstants.OBJECT_SPY_HTML_ELEMENT_CAPTURED, this);
        eventBroker.subscribe(EventConstants.WORKSPACE_CLOSED, this);
        isDisposed = false;
        pages = new ArrayList<>();
        startSocketServer();
    }

    private void startSocketServer() {
        try {
            new ProgressMonitorDialog(getParentShell()).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(ObjectspyMessageConstants.MSG_DLG_INIT_OBJECT_SPY, 1);
                    AddonSocketServer.getInstance().start(AddonSocket.class,
                            UtilitiesAddonUtil.getInstantBrowsersPort());
                }
            });
        } catch (InvocationTargetException e) {
            LoggerSingleton.logError(e.getTargetException());
        } catch (InterruptedException e) {
            // Ignore this
        }
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        bodyComposite = new Composite(parent, SWT.NONE);
        bodyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        bodyComposite.setLayout(new GridLayout(1, false));

        Composite toolbarComposite = new Composite(bodyComposite, SWT.NONE);
        toolbarComposite.setLayout(new GridLayout());
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

        urlView = new ObjectSpyUrlView(this);
        urlView.addStartBrowserToolbar(toolbarComposite);

        addElementTreeToolbar(toolbarComposite);

        createCapturedObjectsAndPropertiesView(bodyComposite);

        selectorEditor = new ObjectSpySelectorEditor();
        selectorEditor.createObjectSelectorEditor(bodyComposite);

        Composite bottomComposite = new Composite(bodyComposite, SWT.NONE);
        GridLayout bottomLayout = new GridLayout(2, false);
        bottomLayout.marginHeight = 0;
        bottomLayout.marginWidth = 0;
        bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        bottomComposite.setLayout(bottomLayout);

        verifyView = new ObjectVerifyAndHighlightView();
        verifyView.createVerifyAndHighlightView(bottomComposite, GridData.FILL_HORIZONTAL);

        addControlListeners();

        registerObjectSpyEventListeners();
        return bodyComposite;
    }

    private void registerObjectSpyEventListeners() {
        urlView.addListener(verifyView,
                Arrays.asList(ObjectSpyEvent.ADDON_SESSION_STARTED, ObjectSpyEvent.SELENIUM_SESSION_STARTED));

        capturedObjectsView.addListener(objectPropertiesView, Arrays.asList(ObjectSpyEvent.SELECTED_ELEMENT_CHANGED));
        capturedObjectsView.addListener(this, Arrays.asList(ObjectSpyEvent.SELECTED_ELEMENT_CHANGED));

        selectorEditor.addListener(verifyView, Arrays.asList(ObjectSpyEvent.SELECTOR_HAS_CHANGED));
        objectPropertiesView.addListener(selectorEditor, Arrays.asList(ObjectSpyEvent.ELEMENT_PROPERTIES_CHANGED));
        objectPropertiesView.addListener(verifyView, Arrays.asList(ObjectSpyEvent.ELEMENT_PROPERTIES_CHANGED));
        objectPropertiesView.addListener(this, Arrays.asList(ObjectSpyEvent.REQUEST_DIALOG_RESIZE));
    }

    private void createCapturedObjectsAndPropertiesView(Composite bodyComposite) {
        capturedObjectsView = new CapturedObjectsView(bodyComposite, SWT.NONE, eventBroker);
        Sash sash = new Sash(bodyComposite, SWT.HORIZONTAL);
        GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
        sash.setLayoutData(layoutData);
        sash.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                GridLayout parentLayout = (GridLayout) capturedObjectsView.getParent().getLayout();
                int newHeight = e.y - capturedObjectsView.getBounds().y - parentLayout.verticalSpacing;
                if (newHeight < CAPTURED_OBJECT_VIEW_MIN_SIZE) {
                    e.doit = false;
                    return;
                }
                GridData gridData = (GridData) capturedObjectsView.getLayoutData();
                gridData.heightHint = newHeight;
                capturedObjectsView.getParent().layout();
            }
        });
        objectPropertiesView = new ObjectPropertiesView(bodyComposite, SWT.NONE);

        capturedObjectsView.setInput(pages);
    }

    private void addControlListeners() {
        addPageElementToolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                WebPage page = WebElementUtils.createWebPage();
                pages.add(page);
                capturedObjectsView.refreshTree(null);
                setSelectedTreeItem(page);
            }
        });

        addFrameElementToolItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = capturedObjectsView.getSelection();
                if (!(selection.getFirstElement() instanceof WebElement)) {
                    return;
                }

                WebFrame parent = getElementParent(selection);
                if (parent == null) {
                    return;
                }

                WebFrame webFrame = WebElementUtils.createWebFrame(parent);
                capturedObjectsView.refreshTree(parent);
                setSelectedTreeItem(webFrame);
            }
        });

        addElementToolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = capturedObjectsView.getSelection();
                if (!(selection.getFirstElement() instanceof WebElement)) {
                    return;
                }
                WebFrame parent = getElementParent(selection);
                if (parent == null) {
                    return;
                }

                WebElement webElement = WebElementUtils.createWebElement(parent);
                capturedObjectsView.refreshTree(parent);
                setSelectedTreeItem(webElement);
            }
        });

        removeElementToolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = capturedObjectsView.getSelection();
                @SuppressWarnings("rawtypes")
                Iterator iterator = selection.iterator();
                List<WebElement> removedElements = new ArrayList<>();
                while (iterator.hasNext()) {
                    WebElement webElement = (WebElement) iterator.next();
                    WebFrame parent = webElement.getParent();
                    removedElements.add(webElement);
                    if (webElement.getParent() == null) {
                        pages.remove(webElement);
                        continue;
                    }
                    
                    int index = -1;
                    List<WebElement> children = parent.getChildren();
                    for (int i = 0; i < children.size(); i++) {
                        WebElement child = children.get(i);
                        if (child == webElement) {
                            index = i;
                            break;
                        }
                    }
                    if (index >= 0) {
                        children.remove(index);
                    }
                }
                
                removeElements(removedElements.toArray());
                
                objectPropertiesView.refreshTable(null);
            }
        });

        addElmtToObjRepoToolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    addElementToObjectRepository(shell);
                } catch (Exception exception) {
                    logger.error(exception);
                    MessageDialog.openError(shell, StringConstants.ERROR_TITLE, exception.getMessage());
                }
            }
        });

        objectPropertiesView.setRefreshCapturedObjectsTree(new Runnable() {

            @Override
            public void run() {
                capturedObjectsView.refreshTree(null);
            }
        });
    }
    
    private void removeElements(Object[] removedElements) {
        TreeViewer treeViewer = capturedObjectsView.getTreeViewer();
        treeViewer.getControl().setRedraw(false);
        Object[] expandedElements = treeViewer.getExpandedElements();
        treeViewer.remove(removedElements);
        for (Object element : expandedElements) {
            treeViewer.setExpandedState(element, true);
        }
        treeViewer.getControl().setRedraw(true);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        return parent;
    }

    private void refreshTree(TreeViewer treeViewer, Object object) {
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

    private void setSelectedTreeItem(Object object) {
        TreeViewer treeViewer = capturedObjectsView.getTreeViewer();
        WebElementTreeContentProvider dataProvider = (WebElementTreeContentProvider) treeViewer.getContentProvider();
        treeViewer.setSelection(new TreeSelection(dataProvider.getTreePath(object)), true);
        treeViewer.setExpandedState(object, true);
    }

    private WebFrame getElementParent(IStructuredSelection selection) {
        WebElement element = (WebElement) selection.getFirstElement();
        WebFrame parentElement = null;
        if (element instanceof WebFrame) {
            parentElement = (WebFrame) element;
        } else if (element.getParent() != null) {
            parentElement = element.getParent();
        }
        return parentElement;
    }

    private void addElementTreeToolbar(Composite explorerComposite) {
        mainToolbar = new ToolBar(explorerComposite, SWT.FLAT | SWT.RIGHT);
        mainToolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        mainToolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        addPageElementToolItem = new ToolItem(mainToolbar, SWT.NONE);
        addPageElementToolItem.setImage(ImageConstants.IMG_24_NEW_PAGE_ELEMENT);
        addPageElementToolItem.setDisabledImage(ImageConstants.IMG_24_NEW_PAGE_ELEMENT_DISABLED);
        addPageElementToolItem.setText(StringConstants.DIA_TOOLITEM_NEW_PAGE);
        addPageElementToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_NEW_PAGE_ELEMENT);

        addFrameElementToolItem = new ToolItem(mainToolbar, SWT.NONE);
        addFrameElementToolItem.setImage(ImageConstants.IMG_24_NEW_FRAME_ELEMENT);
        addFrameElementToolItem.setDisabledImage(ImageConstants.IMG_24_NEW_FRAME_ELEMENT_DISABLED);
        addFrameElementToolItem.setText(StringConstants.DIA_TOOLITE_NEW_FRAME);
        addFrameElementToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_NEW_FRAME_ELEMENT);
        addFrameElementToolItem.setEnabled(false);

        addElementToolItem = new ToolItem(mainToolbar, SWT.NONE);
        addElementToolItem.setImage(ImageConstants.IMG_24_NEW_ELEMENT);
        addElementToolItem.setDisabledImage(ImageConstants.IMG_24_NEW_ELEMENT_DISABLED);
        addElementToolItem.setText(StringConstants.DIA_TOOLITEM_NEW_OBJECT);
        addElementToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_NEW_ELEMENT);
        addElementToolItem.setEnabled(false);

        new ToolItem(mainToolbar, SWT.SEPARATOR);

        removeElementToolItem = new ToolItem(mainToolbar, SWT.NONE);
        removeElementToolItem.setImage(ImageConstants.IMG_24_DELETE);
        removeElementToolItem.setDisabledImage(ImageConstants.IMG_24_DELETE_DISABLED);
        removeElementToolItem.setText(StringConstants.DIA_TOOLITEM_DELETE);
        removeElementToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_REMOVE_ELEMENT);
        removeElementToolItem.setEnabled(false);
        new ToolItem(mainToolbar, SWT.SEPARATOR);

        addElmtToObjRepoToolItem = new ToolItem(mainToolbar, SWT.NONE);
        addElmtToObjRepoToolItem.setImage(ImageConstants.IMG_24_ADD_TO_OBJECT_REPOSITORY);
        addElmtToObjRepoToolItem.setDisabledImage(ImageConstants.IMG_24_ADD_TO_OBJECT_REPOSITORY_DISABLED);
        addElmtToObjRepoToolItem.setText(StringConstants.DIA_TOOLITEM_TIP_ADD_ELEMENT_TO_OBJECT_REPO);
        addElmtToObjRepoToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_ADD_ELEMENT_TO_OBJECT_REPO);
    }

    private List<WebPage> getCloneCapturedObjects(final List<WebPage> pages) {
        return pages.stream().map(page -> page.softClone()).collect(Collectors.toList());
    }

    private void addElementToObjectRepository(Shell parentShell) throws Exception {
        TreeViewer capturedTreeViewer = capturedObjectsView.getTreeViewer();
        SaveToObjectRepositoryDialog addToObjectRepositoryDialog = new SaveToObjectRepositoryDialog(parentShell, true,
                getCloneCapturedObjects(pages), capturedTreeViewer.getExpandedElements());
        if (addToObjectRepositoryDialog.open() != Window.OK) {
            return;
        }
        ObjectRepositoryService objectRepositoryService = new ObjectRepositoryService();
        
        SaveActionResult saveResult = objectRepositoryService.saveObject(addToObjectRepositoryDialog.getDialogResult());
       
        Trackings.trackSaveSpy("web", saveResult.getSavedObjectCount());
        
        // Refresh tree explorer
        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, addToObjectRepositoryDialog.getSelectedParentFolderResult());
        
        //Refesh updated object.
        for (Object[] testObj : saveResult.getUpdatedTestObjectIds()) {
            eventBroker.post(EventConstants.TEST_OBJECT_UPDATED, testObj);
        }
        if (saveResult.getNewSelectionOnExplorer() == null) {
            return;
        }
        selectSelectedElements(capturedTreeViewer, pages);
        eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEMS, saveResult.getNewSelectionOnExplorer().toArray());
    }

    private void selectSelectedElements(TreeViewer capturedTreeViewer, List<WebPage> htmlElements) {
        List<WebElement> selectedElements = new ArrayList<>();
        htmlElements.forEach(page -> selectedElements.addAll(flatten(page)));
        capturedTreeViewer.setSelection(new StructuredSelection(selectedElements));
    }

    private List<WebElement> flatten(WebElement element) {
        List<WebElement> elements = new ArrayList<>();
        elements.add(element);

        if (element instanceof WebFrame) {
            WebFrame frame = (WebFrame) element;
            frame.getChildren().forEach(child -> elements.addAll(flatten(child)));
        }
        return elements;
    }

    private Collection<ITreeEntity> addCheckedElements(WebElement element, FolderTreeEntity parentTreeFolder,
            WebElementEntity refElement) throws Exception {
        FolderEntity parentFolder = parentTreeFolder.getObject();
        WebElementEntity importedElement = ObjectRepositoryController.getInstance().importWebElement(
                WebElementUtils.convertWebElementToTestObject(element, refElement, parentFolder), parentFolder);

        List<ITreeEntity> newTreeWebElements = new ArrayList<>();
        newTreeWebElements.add(new WebElementTreeEntity(importedElement, parentTreeFolder));
        if (element instanceof WebFrame) {
            for (WebElement childElement : ((WebFrame) element).getChildren()) {
                newTreeWebElements.addAll(addCheckedElements(childElement, parentTreeFolder, importedElement));
            }
        }
        return newTreeWebElements;
    }

    @Override
    protected void handleShellCloseEvent() {
        super.handleShellCloseEvent();
        stop();
    }

    public void stop() {
        if (urlView != null) {
            urlView.stop();
        }
        eventBroker.unsubscribe(this);
        isDisposed = true;
    }

    @Override
    protected Point getInitialSize() {
        IDialogSettings dialogBoundSettings = getDialogBoundsSettings();
        Point size = null;
        if (dialogBoundSettings.getBoolean(DIA_BOUNDS_SET)) {
            size = super.getInitialSize();
        } else {
            dialogBoundSettings.put(DIA_BOUNDS_SET, true);
            size = MIN_SIZE;
        }
        return size;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.DIA_TITLE_OBJ_SPY);
    }

    public void addObjectsFromObjectRepository(Object[] selectedObjects) {
        WebPage generatingPageElement = null;
        Map<String, WebElement> generatingElementMap = new HashMap<>();
        for (Object selectedObject : selectedObjects) {
            if (selectedObject instanceof WebElementEntity) {
                if (generatingPageElement == null) {
                    generatingPageElement = WebElementUtils.createWebPage();
                    pages.add(generatingPageElement);
                }
                WebElementUtils.createWebElementFromTestObject((WebElementEntity) selectedObject, false,
                        generatingPageElement, generatingElementMap);
                continue;
            }
            if (selectedObject instanceof FolderEntity) {
                pages.addAll(WebElementUtils.createWebElementFromFolder((FolderEntity) selectedObject,
                        generatingElementMap));
            }
        }
        TreeViewer elementTreeViewer = capturedObjectsView.getTreeViewer();
        refreshTree(elementTreeViewer, null);
        elementTreeViewer.setSelection(new StructuredSelection(generatingElementMap.values().toArray()));
    }

    private void setExpandForParentElement(WebFrame pageElement, TreeViewer capturedElementTreeViewer) {
        List<WebElement> childElements = pageElement.getChildren();
        if (childElements.isEmpty()) {
            return;
        }
        capturedElementTreeViewer.setExpandedState(pageElement, true);
        for (WebElement element : childElements) {
            if (element instanceof WebFrame) {
                setExpandForParentElement((WebFrame) element, capturedElementTreeViewer);
            }
        }
    }

    @SuppressWarnings("unused")
    private void addNewElement(WebFrame frame, WebElement element, WebFrame page) {
        List<WebElement> frameChildren = frame.getChildren();
        if (frameChildren.contains(element)) {
            if (!(element instanceof WebFrame)) {
                return;
            }
            WebFrame frameElement = (WebFrame) element;
            WebFrame existingFrameElement = (WebFrame) (frameChildren.get(frameChildren.indexOf(element)));
            addNewElement(existingFrameElement, frameElement.getChildren().get(0), page);
        } else {
            element.setParent(frame);
        }
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    @Override
    public int open() {
        if (getShell() == null) {
            create();
        }
        getShell().setMinimumSize(MIN_SIZE);
        return super.open();
    }

    @Override
    public boolean close() {
        if (urlView != null) {
            urlView.save();
        }
        boolean result = super.close();
        Trackings.trackCloseSpy("web");
        return result;
    }

    @Override
    public void handleEvent(Event event) {
        Object dataObject = EventUtil.getData(event);
        String topic = event.getTopic();
        switch (topic) {
            case EventConstants.OBJECT_SPY_HTML_ELEMENT_CAPTURED: {
                if (!(dataObject instanceof WebElement)) {
                    return;
                }
                addNewElement((WebElement) dataObject);
                return;
            }
            case EventConstants.WORKSPACE_CLOSED: {
                cancelPressed();
                return;
            }
        }
    }

    private WebPage findPage(WebElement webElement) {
        if (webElement == null) {
            return null;
        }
        if (webElement instanceof WebPage) {
            return (WebPage) webElement;
        }
        WebFrame parent = webElement.getParent();
        if (parent == null) {
            return null;
        }
        return findPage(parent);
    }

    private int indexOf(List<WebElement> elements, WebElement frame) {
        for (int index = 0; index < elements.size(); index++) {
            if (frame.isSameProperties(elements.get(index))) {
                return index;
            }
        }
        return -1;
    }

    private WebFrame merge(WebFrame oldFrame, WebFrame newFrame) {
        List<WebElement> oldChildren = oldFrame.getChildren();
        newFrame.getChildren().forEach(newChild -> {
            int index = indexOf(oldChildren, newChild);
            if (index < 0) {
//                oldFrame.addChild(newChild);
                newChild.setParent(oldFrame);
                return;
            }

            if (newChild.getType() == WebElementType.ELEMENT) {
                return;
            }

            WebFrame oldChildFrame = (WebFrame) oldChildren.get(index);
            WebElement mergedChildFrame = merge(oldChildFrame, (WebFrame) newChild);
            oldChildren.remove(index);
            oldChildren.add(index, mergedChildFrame);
            mergedChildFrame.setParentOnly(oldFrame);
        });
        return oldFrame;
    }

    @Override
    public void addNewElement(WebElement newElement) {
        if (newElement == null || newElement.getParent() == null) {
            return;
        }
        WebPage pageOfElement = findPage(newElement);
        if (pages.contains(pageOfElement)) {
            int index = pages.indexOf(pageOfElement);
            WebPage oldPage = pages.get(index);
            WebPage newPage = (WebPage) merge(oldPage, pageOfElement);
            pages.remove(index);
            pages.add(index, newPage);
        } else {
            pages.add(pageOfElement);
        }
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                TreeViewer capturedElementTreeViewer = capturedObjectsView.getTreeViewer();
                refreshTree(capturedElementTreeViewer, null);
                setExpandForParentElement(pageOfElement, capturedElementTreeViewer);
            }
        });
    }

    private ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER);
    }

    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return UIUtils.getDialogBoundSettings(getClass());
    }

    @Override
    public void handleEvent(ObjectSpyEvent event, Object object) {
        switch (event) {
            case SELECTED_ELEMENT_CHANGED:
                addElementToolItem.setEnabled(object instanceof WebElement);
                addFrameElementToolItem.setEnabled(object instanceof WebElement);
                removeElementToolItem.setEnabled(object instanceof WebElement);
                return;
            case REQUEST_DIALOG_RESIZE:
                Shell shell = getShell();
                shell.setSize(shell.getSize().x, getInitialSize().y);
                return;
            default:
                break;
        }

    }

}
