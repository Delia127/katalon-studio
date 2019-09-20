package com.kms.katalon.composer.windows.dialog;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.kms.katalon.composer.components.controls.HelpCompositeForDialog;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WindowsElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.mobile.objectspy.constant.ImageConstants;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.AddElementToObjectRepositoryDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.AppiumMonitorDialog;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.windows.element.BasicWindowsElement;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.element.CapturedWindowsElementConverter;
import com.kms.katalon.composer.windows.element.TreeWindowsElement;
import com.kms.katalon.composer.windows.spy.CapturedWindowsElementLabelProvider;
import com.kms.katalon.composer.windows.spy.CapturedWindowsObjectTableViewer;
import com.kms.katalon.composer.windows.spy.SelectableWindowsElementEditingSupport;
import com.kms.katalon.composer.windows.spy.WindowsElementLabelProvider;
import com.kms.katalon.composer.windows.spy.WindowsElementPropertiesComposite;
import com.kms.katalon.composer.windows.spy.WindowsElementTreeContentProvider;
import com.kms.katalon.composer.windows.spy.WindowsInspectorController;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.WindowsElementController;
import com.kms.katalon.core.mobile.keyword.internal.GUIObject;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity;
import com.kms.katalon.tracking.service.Trackings;

public class WindowsSpyObjectDialog extends Dialog implements WindowsObjectDialog {

    public static final Point DIALOG_SIZE = new Point(800, 800);

    private static final String DIALOG_TITLE = "Spy Windows Objects";

    private static final int DIALOG_MARGIN_OFFSET = 5;

    private CheckboxTreeViewer allElementTreeViewer;

    private ToolItem btnStart, btnCapture, btnAdd, btnStop;

    private TreeWindowsElement appRootElement;

    private boolean disposed;

    private WindowsInspectorController inspectorController;

    private WindowsDeviceDialog deviceView;

    private Composite container;

    private boolean canceledBeforeOpening;

    private CapturedWindowsObjectTableViewer capturedObjectsTableViewer;

    private TableColumn tblclmnCapturedObjectsSelection;

    private WindowsElementPropertiesComposite propertiesComposite;

    private Composite appsComposite;

    private WindowsAppComposite mobileComposite;

    private static WindowsSpyObjectDialog instance;

    public boolean isCanceledBeforeOpening() {
        return canceledBeforeOpening;
    }

    public WindowsSpyObjectDialog(Shell parentShell, WindowsAppComposite mobileComposite) {
        super(parentShell);
        setShellStyle(SWT.SHELL_TRIM | SWT.CENTER);
        this.disposed = false;
        this.inspectorController = new WindowsInspectorController();
        this.mobileComposite = mobileComposite;
        instance = this;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        container = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container.setLayout(layout);

        SashForm sashForm = new SashForm(container, SWT.NONE);
        sashForm.setSashWidth(3);
        sashForm.setLayout(new FillLayout());
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        ScrolledComposite leftSashForm = new ScrolledComposite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL);
        leftSashForm.setExpandHorizontal(true);
        leftSashForm.setExpandVertical(true);
        leftSashForm.setMinSize(180, 400);

        Composite explorerComposite = new Composite(leftSashForm, SWT.BORDER);
        explorerComposite.setLayout(layout);

        addElementTreeToolbar(explorerComposite);

        SashForm hSashForm = new SashForm(explorerComposite, SWT.VERTICAL);
        hSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createCapturedObjectsComposite(hSashForm);

        propertiesComposite = new WindowsElementPropertiesComposite(this);
        propertiesComposite.createObjectPropertiesComposite(hSashForm);

        hSashForm.setWeights(new int[] { 1, 1 });
        leftSashForm.setContent(explorerComposite);

        ScrolledComposite rightSashForm = new ScrolledComposite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL);
        rightSashForm.setExpandHorizontal(true);
        rightSashForm.setExpandVertical(true);
        rightSashForm.setMinSize(280, 400);

        Composite contentComposite = new Composite(rightSashForm, SWT.BORDER);
        contentComposite.setLayout(layout);

        addStartStopToolbar(contentComposite);

        createSettingComposite(contentComposite);

        createAllObjectsComposite(contentComposite);
        rightSashForm.setContent(contentComposite);

        sashForm.setWeights(new int[] { 5, 5 });

        new HelpCompositeForDialog(container, DocumentationMessageConstants.DIALOG_OBJECT_SPY_MOBILE);

        return container;
    }

    private void createCapturedObjectsComposite(SashForm hSashForm) {
        Composite capturedObjectsComposite = new Composite(hSashForm, SWT.NONE);
        capturedObjectsComposite.setLayout(new GridLayout());

        Label lblCapturedObjects = new Label(capturedObjectsComposite, SWT.NONE);
        lblCapturedObjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblCapturedObjects.setText(StringConstants.DIA_LBL_CAPTURED_OBJECTS);
        ControlUtils.setFontToBeBold(lblCapturedObjects);

        Composite capturedObjectTableComposite = new Composite(capturedObjectsComposite, SWT.NONE);
        capturedObjectTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        TableColumnLayout tbclCapturedObjects = new TableColumnLayout();
        capturedObjectTableComposite.setLayout(tbclCapturedObjects);

        capturedObjectsTableViewer = new CapturedWindowsObjectTableViewer(capturedObjectTableComposite,
                SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION, this);
        Table capturedObjectsTable = capturedObjectsTableViewer.getTable();
        capturedObjectsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        capturedObjectsTable.setHeaderVisible(true);
        capturedObjectsTable.setLinesVisible(ControlUtils.shouldLineVisble(capturedObjectsTable.getDisplay()));

        TableViewerColumn tbvclCapturedObjectsSelection = new TableViewerColumn(capturedObjectsTableViewer, SWT.NONE);
        tblclmnCapturedObjectsSelection = tbvclCapturedObjectsSelection.getColumn();
        tbvclCapturedObjectsSelection
                .setLabelProvider(new CapturedWindowsElementLabelProvider(CapturedWindowsElementLabelProvider.SELECTION_COLUMN_IDX));
        tbvclCapturedObjectsSelection
                .setEditingSupport(new SelectableWindowsElementEditingSupport(capturedObjectsTableViewer));

        TableViewerColumn tableViewerColumnCapturedObjects = new TableViewerColumn(capturedObjectsTableViewer,
                SWT.NONE);
        TableColumn tblclmnCapturedObjects = tableViewerColumnCapturedObjects.getColumn();
        tblclmnCapturedObjects.setText(StringConstants.NAME);
        tableViewerColumnCapturedObjects
                .setLabelProvider(new CapturedWindowsElementLabelProvider(CapturedWindowsElementLabelProvider.ELEMENT_COLUMN_IDX));

        capturedObjectsTableViewer.setContentProvider(ArrayContentProvider.getInstance());

        int selectionColMinWidth = Platform.OS_MACOSX.equals(Platform.getOS()) ? 21 : 30;
        tbclCapturedObjects.setColumnData(tblclmnCapturedObjectsSelection,
                new ColumnWeightData(0, selectionColMinWidth, false));
        tbclCapturedObjects.setColumnData(tblclmnCapturedObjects, new ColumnWeightData(60, 250 - selectionColMinWidth));

        capturedObjectsTable.setToolTipText(StringUtils.EMPTY);
        ColumnViewerToolTipSupport.enableFor(capturedObjectsTableViewer);

        capturedObjectsTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                CapturedWindowsElement firstElement = (CapturedWindowsElement) selection.getFirstElement();
                propertiesComposite.setEditingElement(firstElement);
            }
        });

        capturedObjectsTableViewer.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button != 1) {
                    return;
                }
                Point pt = new Point(e.x, e.y);
                TableItem item = capturedObjectsTableViewer.getTable().getItem(pt);
                if (item != null) {
                    highlightObject((CapturedWindowsElement) item.getData());
                }
            }
        });

        capturedObjectsTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                CapturedWindowsElement[] elements = capturedObjectsTableViewer.getSelectedElements();
                if (elements == null || elements.length == 0) {
                    return;
                }
                switch (e.keyCode) {
                    case SWT.DEL: {
                        removeSelectedCapturedElements(elements);
                        break;
                    }
                    case SWT.F5: {
                        verifyCapturedElementsStates(elements);
                        break;
                    }
                    case SWT.F2: {
                        if (elements.length == 1) {
                            propertiesComposite.focusAndEditCapturedElementName();
                        }
                        break;
                    }
                }
            }
        });

        capturedObjectsTableViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                CapturedWindowsElement mobileElement = capturedObjectsTableViewer.getSelectedElement();
                TreeWindowsElement link = mobileElement.getLink();
                if (link != null) {
                    allElementTreeViewer.setSelection(new StructuredSelection(link));
                    allElementTreeViewer.getTree().setFocus();
                }
            }
        });

        tblclmnCapturedObjectsSelection.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                capturedObjectsTableViewer.checkAllElements(!capturedObjectsTableViewer.isAllElementChecked());
            }
        });
    }

    public void updateCapturedElementSelectingColumnHeader() {
        tblclmnCapturedObjectsSelection.setImage(capturedObjectsTableViewer.isAllElementChecked()
                ? ImageConstants.IMG_16_CHECKED : ImageConstants.IMG_16_UNCHECKED);
        btnAdd.setEnabled(capturedObjectsTableViewer.isAnyElementChecked());
    }

    private void verifyCapturedElementsStates(CapturedWindowsElement[] elements) {
        // clear previous state
        clearAllObjectState(elements);

        if (appRootElement != null) {
            for (CapturedWindowsElement needToVerify : elements) {
                TreeWindowsElement foundElement = appRootElement.findBestMatch(needToVerify);
                if (foundElement == null) {
                    continue;
                }
                needToVerify.setLink(foundElement);
                foundElement.setCapturedElement(needToVerify);
                allElementTreeViewer.setChecked(foundElement, true);
            }
        }

        allElementTreeViewer.refresh();
        capturedObjectsTableViewer.refresh();
    }

    private void removeSelectedCapturedElements(CapturedWindowsElement[] elements) {
        clearAllObjectState(elements);
        allElementTreeViewer.refresh();

        capturedObjectsTableViewer.removeCapturedElements(Arrays.asList(elements));

        propertiesComposite.setEditingElement(null);
    }

    private void clearAllObjectState(CapturedWindowsElement[] elements) {
        for (CapturedWindowsElement captured : elements) {
            TreeWindowsElement treeElementLink = captured.getLink();
            if (treeElementLink == null) {
                continue;
            }
            treeElementLink.setCapturedElement(null);
            captured.setLink(null);

            Tree elementTree = allElementTreeViewer.getTree();
            if (elementTree != null && !elementTree.isDisposed() && allElementTreeViewer.getChecked(treeElementLink)) {
                allElementTreeViewer.setChecked(treeElementLink, false);
            }
        }
    }

    private void createAllObjectsComposite(Composite parentComposite) {
        Composite allObjectsComposite = new Composite(parentComposite, SWT.NONE);
        allObjectsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        allObjectsComposite.setLayout(new GridLayout());

        Label lblAllObjects = new Label(allObjectsComposite, SWT.NONE);
        lblAllObjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        ControlUtils.setFontToBeBold(lblAllObjects);
        lblAllObjects.setText(StringConstants.DIA_LBL_ALL_OBJECTS);

        allElementTreeViewer = new CheckboxTreeViewer(allObjectsComposite,
                SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI) {
            @Override
            public boolean setSubtreeChecked(Object element, boolean state) {
                Widget widget = internalExpand(element, false);
                if (widget instanceof TreeItem) {
                    TreeItem item = (TreeItem) widget;
                    item.setChecked(state);
                    return true;
                }
                return false;
            }
        };
        Tree tree = allElementTreeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        allElementTreeViewer.setLabelProvider(new WindowsElementLabelProvider());
        allElementTreeViewer.setContentProvider(new WindowsElementTreeContentProvider());

        tree.setToolTipText(StringUtils.EMPTY);
        ColumnViewerToolTipSupport.enableFor(allElementTreeViewer, ToolTip.NO_RECREATE);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button != 1) {
                    return;
                }
                Point pt = new Point(e.x, e.y);
                TreeItem item = tree.getItem(pt);
                if (item != null) {
                    highlightObject((TreeWindowsElement) item.getData());
                }
            }
        });

        allElementTreeViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                TreeWindowsElement selectedElement = (TreeWindowsElement) event.getElement();
                if (event.getChecked()) {
                    captureWindowsElement(selectedElement);
                    return;
                } else {
                    CapturedWindowsElement capturedElement = selectedElement.getCapturedElement();
                    if (capturedObjectsTableViewer.contains(capturedElement)) {
                        capturedObjectsTableViewer.removeCapturedElement(capturedElement);
                        selectedElement.setCapturedElement(null);
                        propertiesComposite.setEditingElement(null);
                    }
                }
                allElementTreeViewer.refresh(selectedElement);
            }
        });
    }
    
    private void captureWindowsElement(TreeWindowsElement selectedElement) {
        
        ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getShell());
        try {
            monitorDialog.run(true, false, new IRunnableWithProgress() {
                
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask("Capturing Windows element", 3);
                        List<CapturedWindowsElement> mobileElements = new ArrayList<>();
                        mobileElements.add(selectedElement.newCapturedElement(inspectorController.getDriver()));
                        monitor.worked(2);
                        UISynchronizeService.syncExec(() -> {
                            capturedObjectsTableViewer.addWindowsElements(mobileElements);
        
                            propertiesComposite.focusAndEditCapturedElementName();
                            allElementTreeViewer.refresh(selectedElement);
                        });
                        monitor.worked(1);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (InterruptedException ignored) {
        } catch (InvocationTargetException e) {
            MultiStatusErrorDialog.showErrorDialog(e, "Error", "Unable to capture object");
        }
    }

    private void createSettingComposite(Composite parent) {
        Composite settingComposite = new Composite(parent, SWT.NONE);
        settingComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glSettingComposite = new GridLayout(2, false);
        glSettingComposite.horizontalSpacing = 10;
        settingComposite.setLayout(glSettingComposite);

        Label lblConfiguration = new Label(settingComposite, SWT.NONE);
        lblConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        ControlUtils.setFontToBeBold(lblConfiguration);
        lblConfiguration.setText(StringConstants.DIA_LBL_CONFIGURATIONS);

        appsComposite = new Composite(settingComposite, SWT.NONE);
        appsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        appsComposite.setLayout(new FillLayout());

        mobileComposite.createComposite(appsComposite, SWT.NONE, this);
    }

    public void updateSelectedElement(CapturedWindowsElement selectedElement) {
        capturedObjectsTableViewer.refresh(selectedElement, true);
        TreeWindowsElement element = selectedElement.getLink();
        if (element != null) {
            allElementTreeViewer.refresh(element);
            allElementTreeViewer.setSelection(new StructuredSelection(element));
        }
    }

    public void refreshButtonsState() {
        btnStart.setEnabled(mobileComposite.isAbleToStart());
    }

    @Override
    public void create() {
        super.create();

        initializeData();

        refreshButtonsState();

        capturedObjectsTableViewer.setCapturedElements(new ArrayList<CapturedWindowsElement>());
    }

    private void initializeData() {
        UISynchronizeService.asyncExec(() -> {
            try {
                mobileComposite.setInput();
            } catch (InvocationTargetException exception) {
                Throwable targetException = exception.getTargetException();
                LoggerSingleton.logError(targetException);
                MultiStatusErrorDialog.showErrorDialog(targetException, "Error",
                        targetException.getClass().getSimpleName());
            } catch (InterruptedException ignored) {
                // ignore this
            } finally {
                refreshButtonsState();
            }
        });
    }

    @Override
    public int open() {
        try {
            canceledBeforeOpening = false;
            return super.open();
        } finally {
            if (canceledBeforeOpening) {
                close();
            }
        }
    }

    private void addElementTreeToolbar(Composite explorerComposite) {
        ToolBar elementTreeToolbar = new ToolBar(explorerComposite, SWT.FLAT | SWT.RIGHT);
        elementTreeToolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        layoutData.horizontalIndent = 2;
        layoutData.minimumWidth = 180;
        elementTreeToolbar.setLayoutData(layoutData);

        btnAdd = new ToolItem(elementTreeToolbar, SWT.NONE);
        btnAdd.setImage(ImageConstants.IMG_24_ADD_TO_OBJECT_REPOSITORY);
        btnAdd.setDisabledImage(ImageConstants.IMG_24_ADD_TO_OBJECT_REPOSITORY_DISABLED);
        btnAdd.setText(StringConstants.DIA_TIP_ADD);
        btnAdd.setToolTipText(StringConstants.DIA_TIP_ADD);
        btnAdd.setEnabled(false);
        btnAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveCapturedObjectsToObjectRepository();
            }
        });
    }

    private void addStartStopToolbar(Composite contentComposite) {
        Composite toolbarComposite = new Composite(contentComposite, SWT.NONE);
        toolbarComposite.setLayout(new GridLayout(2, false));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        ToolBar contentToolbar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
        contentToolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        contentToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        btnCapture = new ToolItem(contentToolbar, SWT.NONE);
        btnCapture.setImage(ImageManager.getImage(IImageKeys.REFRESH_24));
        btnCapture.setDisabledImage(ImageManager.getImage(IImageKeys.REFRESH_DISABLED_24));
        btnCapture.setText("Refresh Screen");
        btnCapture.setEnabled(false);
        btnCapture.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                captureObjectAction();
            }
        });

        btnStart = new ToolItem(contentToolbar, SWT.NONE);
        btnStart.setImage(ImageManager.getImage(IImageKeys.PLAY_24));
        btnStart.setDisabledImage(ImageManager.getImage(IImageKeys.PLAY_DISABLED_24));
        btnStart.setText(StringConstants.DIA_TIP_START_APP);
        btnStart.setToolTipText(StringConstants.DIA_TIP_START_APP);
        btnStart.setEnabled(false);
        btnStart.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Validate all required informations are filled
                if (validateAppSetting()) {
                    startObjectInspectorAction();
                }
            }
        });

        btnStop = new ToolItem(contentToolbar, SWT.NONE);
        btnStop.setImage(ImageManager.getImage(IImageKeys.STOP_24));
        btnStop.setDisabledImage(ImageManager.getImage(IImageKeys.STOP_DISABLED_24));
        btnStop.setText(StringConstants.DIA_TIP_STOP);
        btnStop.setToolTipText(StringConstants.DIA_TIP_STOP);
        btnStop.setEnabled(false);
        btnStop.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                stopObjectInspectorAction();
            }
        });
    }
    
    @Override
    protected void okPressed() {
        saveCapturedObjectsToObjectRepository();
        super.okPressed();
    }
    
    private void saveCapturedObjectsToObjectRepository() {
        if (capturedObjectsTableViewer.getAllCheckedElements().size() > 0) {
            try {
                AddElementToObjectRepositoryDialog dialog = new AddElementToObjectRepositoryDialog(
                        getParentShell());
                if (dialog.open() != Dialog.OK) {
                    return;
                }
                FolderTreeEntity folderTreeEntity = dialog.getSelectedFolderTreeEntity();
                FolderEntity folder = folderTreeEntity.getObject();
                List<ITreeEntity> newTreeEntities = addElementsToRepository(folderTreeEntity, folder);
                Trackings.trackSaveSpy("windows", newTreeEntities.size());
                removeSelectedCapturedElements(
                        capturedObjectsTableViewer.getAllCheckedElements().toArray(new CapturedWindowsElement[0]));
                updateExplorerState(folderTreeEntity, newTreeEntities);
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
                MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, ex.getMessage());
            }
        }
    }
    
    private void updateExplorerState(FolderTreeEntity folderTreeEntity, List<ITreeEntity> newTreeEntities) {
        IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
        eventBroker.send(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, folderTreeEntity);
        eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEMS, newTreeEntities.toArray());
    }

    private List<ITreeEntity> addElementsToRepository(FolderTreeEntity folderTreeEntity, FolderEntity folder)
            throws Exception {
        CapturedWindowsElementConverter converter = new CapturedWindowsElementConverter();
        List<ITreeEntity> newTreeEntities = new ArrayList<>();

        WindowsElementController objectRepositoryController = WindowsElementController.getInstance();
        for (CapturedWindowsElement mobileElement : capturedObjectsTableViewer.getAllCheckedElements()) {
            WindowsElementEntity testObject = converter.convert(mobileElement);
            testObject.setParentFolder(folder);
            testObject.setProject(folder.getProject());
            objectRepositoryController.updateWindowsElementEntity(testObject);
            newTreeEntities.add(new WindowsElementTreeEntity(testObject, folderTreeEntity));
        }
        return newTreeEntities;
    }

    @Override
    protected void handleShellCloseEvent() {
        super.handleShellCloseEvent();
        dispose();
    }

    public void dispose() {
        disposed = true;
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return DIALOG_SIZE;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(DIALOG_TITLE);
        shell.setMinimumSize(500, 500);
    }

    // Highlight Selected object on captured screenshot
    private void highlightObject(BasicWindowsElement selectedElement) {
        if (selectedElement == null || deviceView == null || deviceView.isDisposed()) {
            return;
        }

        deviceView.highlightElement(selectedElement);
    }

    private boolean isOutOfBound(Rectangle displayBounds, Point dialogSize, int startX) {
        return startX < 0 || startX + dialogSize.x > displayBounds.width + displayBounds.x;
    }

    private int getDeviceViewStartXIfPlaceRight(Rectangle objectSpyViewBounds) {
        return objectSpyViewBounds.x + objectSpyViewBounds.width + DIALOG_MARGIN_OFFSET;
    }

    private int getDeviceViewStartXIfPlaceLeft(Rectangle objectSpyViewBounds, Point dialogSize) {
        return objectSpyViewBounds.x - dialogSize.x - DIALOG_MARGIN_OFFSET;
    }

    private int getDefaultDeviceViewDialogStartX(Rectangle displayBounds, Point dialogSize) {
        return displayBounds.width - dialogSize.x;
    }

    private int calculateObjectSpyDialogStartX(Rectangle displayBounds, Point dialogSize) {
        int dialogsWidth = dialogSize.x + WindowsDeviceDialog.DIALOG_WIDTH;
        int startX = (displayBounds.width - dialogsWidth) / 2 + displayBounds.x;
        return Math.max(startX, 0);
    }

    private int calculateObjectSpyDialogStartY(Rectangle displayBounds, Point dialogSize) {
        int startY = displayBounds.height - dialogSize.y;
        return Math.max(startY, 0) / 2;
    }

    private Point calculateInitPositionForDeviceViewDialog() {
        Rectangle displayBounds = getShell().getMonitor().getBounds();
        Point dialogSize = new Point(WindowsDeviceDialog.DIALOG_WIDTH, WindowsDeviceDialog.DIALOG_HEIGHT);
        Rectangle objectSpyViewBounds = getShell().getBounds();
        int startX = getDeviceViewStartXIfPlaceRight(objectSpyViewBounds);
        if (isOutOfBound(displayBounds, dialogSize, startX)) {
            startX = getDeviceViewStartXIfPlaceLeft(objectSpyViewBounds, dialogSize);
            if (isOutOfBound(displayBounds, dialogSize, startX)) {
                startX = getDefaultDeviceViewDialogStartX(displayBounds, dialogSize);
            }
        }
        return new Point(startX, objectSpyViewBounds.y);
    }

    private void openDeviceView() {
        if (deviceView != null && !deviceView.isDisposed()) {
            return;
        }
        deviceView = new WindowsDeviceDialog(getParentShell(), this, calculateInitPositionForDeviceViewDialog());

        deviceView.setBlockOnOpen(false);
        deviceView.open();
        setDeviceView(deviceView);
    }

    public void setSelectedElementByLocation(int x, int y) {
        if (appRootElement == null) {
            return;
        }
        final TreeWindowsElement foundElement = recursivelyFindElementByLocation(appRootElement, x, y);
        if (foundElement == null) {
            return;
        }
        highlightObject(foundElement);
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                getShell().setFocus();
                allElementTreeViewer.getTree().setFocus();
                allElementTreeViewer.setSelection(new StructuredSelection(foundElement));
            }
        });
    }

    /**
     * Recursively find element that is positioned at [x, y]. This is by assumed that mobile elements don't overlap each
     * other.
     * 
     * @param currentElement
     * @param x
     * @param y
     * @return element that were found
     */
    private TreeWindowsElement recursivelyFindElementByLocation(TreeWindowsElement currentElement, int x, int y) {
        for (TreeWindowsElement childElement : currentElement.getChildren()) {
            Map<String, String> attributes = childElement.getProperties();
            Double elementX = Double.parseDouble(attributes.get(GUIObject.X));
            Double elementY = Double.parseDouble(attributes.get(GUIObject.Y));
            Double elementWidth = Double.parseDouble(attributes.get(GUIObject.WIDTH));
            Double elementHeight = Double.parseDouble(attributes.get(GUIObject.HEIGHT));
            Rectangle rectangle = new Rectangle(safeRoundDouble(elementX), safeRoundDouble(elementY),
                    safeRoundDouble(elementWidth), safeRoundDouble(elementHeight));
            if (rectangle.contains(x, y)) {
                return recursivelyFindElementByLocation(childElement, x, y);
            }
        }
        return currentElement;
    }

    private void captureObjectAction() {
        final ProgressMonitorDialogWithThread dialog = new ProgressMonitorDialogWithThread(getShell());

        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask(StringConstants.DIA_JOB_TASK_CAPTURING_OBJECTS, IProgressMonitor.UNKNOWN);

                dialog.runAndWait(new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        appRootElement = inspectorController.getWindowsObjectRoot();
                        return null;
                    }
                });

                checkMonitorCanceled(monitor);

                refreshTreeElements(dialog);

                String imgPath = captureImage();

                checkMonitorCanceled(monitor);

                refreshDeviceView(imgPath);

                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        deviceView.getShell().forceActive();
                    }
                });
                monitor.done();
            }

            private void refreshTreeElements(final ProgressMonitorDialogWithThread dialog) {
                // Root element should be named as .apk file name
                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setCancelable(false);
                        if (appRootElement != null) {
                            allElementTreeViewer.setInput(new Object[] { appRootElement });
                            allElementTreeViewer.refresh();
                            allElementTreeViewer.expandAll();
                            verifyCapturedElementsStates(
                                    capturedObjectsTableViewer.getCapturedElements().toArray(new CapturedWindowsElement[0]));
                        }
                        dialog.setCancelable(true);
                    }
                });
            }

            private void refreshDeviceView(String imgPath) {
                File imgFile = new File(imgPath);
                if (imgFile.exists()) {
                    deviceView.refreshDialog(imgFile);
                }
            }

            private String captureImage() throws InvocationTargetException {
                // Render Screenshot
                try {
                    return inspectorController.captureScreenshot();
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                }
            }
        };

        try {
            btnCapture.setEnabled(false);
            openDeviceView();
            dialog.run(true, true, runnable);
        } catch (InterruptedException ignored) {
            // User canceled
        } catch (InvocationTargetException e) {
            LoggerSingleton.logError(e);
            Throwable exception = e.getTargetException();
            MultiStatusErrorDialog.showErrorDialog(exception, StringConstants.DIA_ERROR_UNABLE_TO_CAPTURE_OBJECTS,
                    exception.getClass().getSimpleName());
        } finally {
            btnCapture.setEnabled(true);
        }
    }

    public String getAppName() {
        return mobileComposite.getAppName();
    }

    private void checkMonitorCanceled(IProgressMonitor monitor) throws InterruptedException {
        if (monitor.isCanceled()) {
            throw new InterruptedException(StringConstants.DIA_ERROR_MSG_OPERATION_CANCELED);
        }
    }

    public boolean isDisposed() {
        return disposed;
    }

    private void startObjectInspectorAction() {

        // Temporary disable Start button while launching app
        btnStart.setEnabled(false);
        final AppiumMonitorDialog progressDlg = new AppiumMonitorDialog(getShell()) {
            @Override
            public void cancelPressed() {
                super.cancelPressed();
                finishedRun();
                getProgressMonitor().done();
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                btnCapture.setEnabled(false);
            }
        };
        try {
            inspectorController.setStreamHandler(progressDlg);
            mobileComposite.startApp(inspectorController, progressDlg);

            captureObjectAction();
            // If no exception, application has been successful started, enable more features
            btnCapture.setEnabled(true);
            btnStop.setEnabled(true);

            // send event for tracking
            Trackings.trackSpy("windows");
        } catch (InvocationTargetException | InterruptedException ex) {
            // If user intentionally cancel the progress, don't need to show error message
            if (ex instanceof InvocationTargetException) {
                Throwable targetException = ((InvocationTargetException) ex).getTargetException();
                String message = (targetException instanceof java.util.concurrent.ExecutionException)
                        ? targetException.getCause().getMessage() : targetException.getMessage();
                UISynchronizeService.syncExec(() -> {
                    MultiStatusErrorDialog.showErrorDialog("Unable to start application", message,
                            ExceptionsUtil.getStackTraceForThrowable(targetException));
                });
                LoggerSingleton.logError(targetException);
            }

            // Enable start button and show error dialog if application cannot start
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnCapture.setEnabled(false);
        }
    }

    private void stopObjectInspectorAction() {
        // Close application
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Quit Driver
                inspectorController.closeApp();
            }
        });
        thread.start();

        if (!getShell().isDisposed()) {
            // Update UI
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnCapture.setEnabled(false);

            if (allElementTreeViewer != null) {
                allElementTreeViewer.setInput(new Object[] {});
                allElementTreeViewer.refresh();
            }
        }

        if (deviceView != null) {
            deviceView.closeApp();
        }

        dispose();
    }

    private boolean validateAppSetting() {
        return mobileComposite.validateSetting();
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(newShellStyle | SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
        setBlockOnOpen(false);
    }

    @Override
    public boolean close() {
        stopObjectInspectorAction();
        boolean result = super.close();
        Trackings.trackCloseSpy("windows");
        instance = null;
        return result;
    }

    @Override
    protected Point getInitialLocation(Point initialSize) {
        Rectangle displayBounds = getShell().getMonitor().getBounds();
        return new Point(calculateObjectSpyDialogStartX(displayBounds, initialSize),
                calculateObjectSpyDialogStartY(displayBounds, initialSize));
    }

    private void setDeviceView(WindowsDeviceDialog deviceView) {
        this.deviceView = deviceView;
    }

    public void addElements(List<WindowsElementEntity> webElements) {
        if (webElements == null) {
            return;
        }

        CapturedWindowsElementConverter converter = new CapturedWindowsElementConverter();
        List<CapturedWindowsElement> newWindowsElements = new ArrayList<>();
        for (WindowsElementEntity webElement : webElements) {
            newWindowsElements.add(converter.revert(webElement));
        }

        capturedObjectsTableViewer.addWindowsElements(newWindowsElements);
        verifyCapturedElementsStates(capturedObjectsTableViewer.getSelectedElements());
    }

    public static WindowsSpyObjectDialog getInstance() {
        return instance;
    }

    public static int safeRoundDouble(double d) {
        long rounded = Math.round(d);
        return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, rounded));
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }
}
