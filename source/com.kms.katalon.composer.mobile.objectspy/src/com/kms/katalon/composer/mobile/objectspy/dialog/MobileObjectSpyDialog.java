package com.kms.katalon.composer.mobile.objectspy.dialog;

import static com.kms.katalon.composer.mobile.objectspy.dialog.MobileDeviceDialog.safeRoundDouble;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.FontDescriptor;
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
import org.eclipse.swt.graphics.Font;
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
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.mobile.objectspy.components.MobileAppComposite;
import com.kms.katalon.composer.mobile.objectspy.composites.MobileAllObjectsWithCheckboxComposite;
import com.kms.katalon.composer.mobile.objectspy.composites.MobileCapturedObjectsComposite;
import com.kms.katalon.composer.mobile.objectspy.composites.MobileConfigurationsComposite;
import com.kms.katalon.composer.mobile.objectspy.composites.MobileElementPropertiesComposite;
import com.kms.katalon.composer.mobile.objectspy.constant.ImageConstants;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.element.CapturedMobileElementConverter;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.TreeMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.provider.CapturedElementLabelProvider;
import com.kms.katalon.composer.mobile.objectspy.element.provider.SelectableElementEditingSupport;
import com.kms.katalon.composer.mobile.objectspy.element.tree.MobileElementLabelProvider;
import com.kms.katalon.composer.mobile.objectspy.element.tree.MobileElementTreeContentProvider;
import com.kms.katalon.composer.mobile.objectspy.preferences.MobileObjectSpyPreferencesHelper;
import com.kms.katalon.composer.mobile.objectspy.viewer.CapturedObjectTableViewer;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.keyword.internal.GUIObject;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.tracking.service.Trackings;

public class MobileObjectSpyDialog extends Dialog implements MobileElementInspectorDialog, MobileAppDialog, MobileElementDialog {

    public static final Point DIALOG_SIZE = new Point(800, 800);

    private static final String DIALOG_TITLE = StringConstants.DIA_DIALOG_TITLE_MOBILE_OBJ_SPY;

    private static final int DIALOG_MARGIN_OFFSET = 5;

    private ToolItem btnStart, btnCapture, btnAdd, btnStop;

    private TreeMobileElement appRootElement;

    private boolean disposed;

    private MobileInspectorController inspectorController;

    private MobileDeviceDialog deviceView;

    private Composite container;

    private boolean canceledBeforeOpening;
    
    private MobileConfigurationsComposite configurationsComposite;

    private MobileElementPropertiesComposite propertiesComposite;
    
    private MobileCapturedObjectsComposite capturedObjectsComposite;
    
    private MobileAllObjectsWithCheckboxComposite allObjectsComposite;

    private MobileObjectSpyPreferencesHelper preferencesHelper;

    private Composite appsComposite;

    private MobileAppComposite mobileComposite;
    
    private static MobileObjectSpyDialog instance;

    public boolean isCanceledBeforeOpening() {
        return canceledBeforeOpening;
    }

    public MobileObjectSpyDialog(Shell parentShell, MobileAppComposite mobileComposite) throws Exception {
        super(parentShell);
        setShellStyle(SWT.SHELL_TRIM | SWT.RESIZE);
        this.disposed = false;
        this.inspectorController = new MobileInspectorController();
        this.preferencesHelper = new MobileObjectSpyPreferencesHelper();
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

        createLeftPanel(sashForm);
        createRightPanel(sashForm);

        sashForm.setWeights(new int[] { 4, 6 });

        new HelpCompositeForDialog(container, DocumentationMessageConstants.DIALOG_OBJECT_SPY_MOBILE);

        return container;
    }
    
    private void createLeftPanel(Composite parent) {
        ScrolledComposite leftPanelScrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        leftPanelScrolledComposite.setExpandHorizontal(true);
        leftPanelScrolledComposite.setExpandVertical(true);
        leftPanelScrolledComposite.setMinSize(180, 400);

        Composite leftPanelComposite = new Composite(leftPanelScrolledComposite, SWT.BORDER);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        leftPanelComposite.setLayout(layout);

        addElementTreeToolbar(leftPanelComposite);

        SashForm hSashForm = new SashForm(leftPanelComposite, SWT.VERTICAL);
        hSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createCapturedObjectsComposite(hSashForm);
        createElementPropertiesComposite(hSashForm);

        hSashForm.setWeights(new int[] { 1, 1 });
        leftPanelScrolledComposite.setContent(leftPanelComposite);
    }
    
    private void createRightPanel(Composite parent) {
        ScrolledComposite rightPanelScrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        rightPanelScrolledComposite.setExpandHorizontal(true);
        rightPanelScrolledComposite.setExpandVertical(true);
        rightPanelScrolledComposite.setMinSize(280, 400);

        Composite rightPanelComposite = new Composite(rightPanelScrolledComposite, SWT.BORDER);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        rightPanelComposite.setLayout(layout);

        addStartStopToolbar(rightPanelComposite);
        createSettingComposite(rightPanelComposite);
        createAllObjectsComposite(rightPanelComposite);
        
        rightPanelScrolledComposite.setContent(rightPanelComposite);
    }

    private void createCapturedObjectsComposite(Composite parent) {
        capturedObjectsComposite = new MobileCapturedObjectsComposite(this, parent);
    }
    
    private void createElementPropertiesComposite(Composite parent) {
        propertiesComposite = new MobileElementPropertiesComposite(this, parent);
    }

    public void handleCapturedObjectsTableSelectionChange() {
        capturedObjectsComposite.updateCheckAllCheckboxState();
        btnAdd.setEnabled(capturedObjectsComposite.isAnyElementChecked());
    }

    @Override
    public void verifyCapturedElementsStates(CapturedMobileElement[] elements) {
        // clear previous state
        clearAllObjectState(elements);

        if (appRootElement != null) {
            for (CapturedMobileElement needToVerify : elements) {
                TreeMobileElement foundElement = appRootElement.findBestMatch(needToVerify);
                if (foundElement == null) {
                    continue;
                }
                needToVerify.setLink(foundElement);
                foundElement.setCapturedElement(needToVerify);
                allObjectsComposite.getAllElementTreeViewer().setChecked(foundElement, true);
            }
        }

        allObjectsComposite.getAllElementTreeViewer().refresh();
        capturedObjectsComposite.refresh();
    }

    @Override
    public void removeSelectedCapturedElements(CapturedMobileElement[] elements) {
        clearAllObjectState(elements);
        allObjectsComposite.getAllElementTreeViewer().refresh();
        capturedObjectsComposite.removeElements(Arrays.asList(elements));
        propertiesComposite.setEditingElement(null);
    }

    private void clearAllObjectState(CapturedMobileElement[] elements) {
        for (CapturedMobileElement captured : elements) {
            TreeMobileElement treeElementLink = captured.getLink();
            if (treeElementLink == null) {
                continue;
            }
            treeElementLink.setCapturedElement(null);
            captured.setLink(null);

            CheckboxTreeViewer allElementTreeViewer = allObjectsComposite.getAllElementTreeViewer();
            Tree elementTree = allElementTreeViewer.getTree();
            if (elementTree != null && !elementTree.isDisposed() && allElementTreeViewer.getChecked(treeElementLink)) {
                allElementTreeViewer.setChecked(treeElementLink, false);
            }
        }
    }

    private void createAllObjectsComposite(Composite parent) {
        allObjectsComposite = new MobileAllObjectsWithCheckboxComposite(this, parent);
    }

    private void createSettingComposite(Composite parent) {
        configurationsComposite = new MobileConfigurationsComposite(this, parent, mobileComposite);
    }

    public void updateSelectedElement(CapturedMobileElement selectedElement) {
        capturedObjectsComposite.refresh(selectedElement, true);
        TreeMobileElement element = selectedElement.getLink();
        if (element != null) {
            allObjectsComposite.getAllElementTreeViewer().refresh(element);
            allObjectsComposite.getAllElementTreeViewer().setSelection(new StructuredSelection(element));
        }
    }

    @Override
    public void refreshButtonsState() {
        btnStart.setEnabled(mobileComposite.isAbleToStart());
    }

    @Override
    public void create() {
        super.create();

        initializeData();

        refreshButtonsState();
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
    public void updateDeviceNames() {
        UISynchronizeService.asyncExec(() -> {
            try {
                mobileComposite.loadDevices();
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
                try {
                    AddElementToObjectRepositoryDialog dialog = new AddElementToObjectRepositoryDialog(
                            getParentShell());
                    if (dialog.open() != Dialog.OK) {
                        return;
                    }
                    FolderTreeEntity folderTreeEntity = dialog.getSelectedFolderTreeEntity();
                    FolderEntity folder = folderTreeEntity.getObject();
                    List<ITreeEntity> newTreeEntities = addElementsToRepository(folderTreeEntity, folder);
                    Trackings.trackSaveSpy("mobile", newTreeEntities.size());
                    removeSelectedCapturedElements(
                            capturedObjectsComposite.getAllCheckedElements().toArray(new CapturedMobileElement[0]));
                    updateExplorerState(folderTreeEntity, newTreeEntities);
                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                    MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, ex.getMessage());
                }
            }

            private void updateExplorerState(FolderTreeEntity folderTreeEntity, List<ITreeEntity> newTreeEntities) {
                IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
                eventBroker.send(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, folderTreeEntity);
                eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEMS, newTreeEntities.toArray());
            }

            private List<ITreeEntity> addElementsToRepository(FolderTreeEntity folderTreeEntity, FolderEntity folder)
                    throws Exception {
                CapturedMobileElementConverter converter = new CapturedMobileElementConverter();
                List<ITreeEntity> newTreeEntities = new ArrayList<>();

                ObjectRepositoryController objectRepositoryController = ObjectRepositoryController.getInstance();
                MobileDriverType currentMobileType = getCurrentMobileDriverType();
                for (CapturedMobileElement mobileElement : capturedObjectsComposite.getAllCheckedElements()) {
                    WebElementEntity testObject = converter.convert(mobileElement, folder, currentMobileType);
                    objectRepositoryController.updateTestObject(testObject);
                    newTreeEntities.add(new WebElementTreeEntity(testObject, folderTreeEntity));
                }
                return newTreeEntities;
            }
        });
    }

    private MobileDriverType getCurrentMobileDriverType() {
        return mobileComposite.getSelectedDriverType();
    }

    private void addStartStopToolbar(Composite contentComposite) {
        Composite toolbarComposite = new Composite(contentComposite, SWT.NONE);
        toolbarComposite.setLayout(new GridLayout(2, false));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        ToolBar contentToolbar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
        contentToolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        contentToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        btnCapture = new ToolItem(contentToolbar, SWT.NONE);
        btnCapture.setImage(ImageConstants.IMG_24_CAPTURE);
        btnCapture.setDisabledImage(ImageConstants.IMG_24_CAPTURE_DISABLED);
        btnCapture.setText(StringConstants.DIA_TIP_CAPTURE_OBJ);
        btnCapture.setToolTipText(StringConstants.DIA_TIP_CAPTURE_OBJ);
        btnCapture.setEnabled(false);
        btnCapture.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                captureObjectAction();
            }
        });

        btnStart = new ToolItem(contentToolbar, SWT.NONE);
        btnStart.setImage(ImageConstants.IMG_24_START_DEVICE);
        btnStart.setDisabledImage(ImageConstants.IMG_24_START_DEVICE_DISABLED);
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
        btnStop.setImage(ImageConstants.IMG_24_STOP_DEVICE);
        btnStop.setDisabledImage(ImageConstants.IMG_24_STOP_DEVICE_DISABLED);
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
    @Override
    public void highlightElement(MobileElement selectedElement) {
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
        int dialogsWidth = dialogSize.x + MobileDeviceDialog.DIALOG_WIDTH;
        int startX = (displayBounds.width - dialogsWidth) / 2 + displayBounds.x;
        return Math.max(startX, 0);
    }

    private int calculateObjectSpyDialogStartY(Rectangle displayBounds, Point dialogSize) {
        int startY = displayBounds.height - dialogSize.y;
        return Math.max(startY, 0) / 2;
    }

    private Point calculateInitPositionForDeviceViewDialog() {
        Rectangle displayBounds = getShell().getMonitor().getBounds();
        Point dialogSize = new Point(MobileDeviceDialog.DIALOG_WIDTH, MobileDeviceDialog.DIALOG_HEIGHT);
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
        deviceView = new MobileDeviceDialog(getParentShell(), this, calculateInitPositionForDeviceViewDialog());
        deviceView.open();
        setDeviceView(deviceView);
    }

    @Override
    public void setSelectedElementByLocation(int x, int y) {
        if (appRootElement == null) {
            return;
        }
        final TreeMobileElement foundElement = recursivelyFindElementByLocation(appRootElement, x, y);
        if (foundElement == null) {
            return;
        }
        highlightElement(foundElement);
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                getShell().setFocus();
                allObjectsComposite.getAllElementTreeViewer().getTree().setFocus();
                allObjectsComposite.getAllElementTreeViewer().setSelection(new StructuredSelection(foundElement));
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
    private TreeMobileElement recursivelyFindElementByLocation(TreeMobileElement currentElement, int x, int y) {
        for (TreeMobileElement childElement : currentElement.getChildrenElement()) {
            Map<String, String> attributes = childElement.getAttributes();
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
        final String appName = getAppName();
        final ProgressMonitorDialogWithThread dialog = new ProgressMonitorDialogWithThread(getShell());

        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask(StringConstants.DIA_JOB_TASK_CAPTURING_OBJECTS, IProgressMonitor.UNKNOWN);

                dialog.runAndWait(new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        appRootElement = inspectorController.getMobileObjectRoot();
                        if (appRootElement != null) {
                            appRootElement.setName(appName);
                        }
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
                        allObjectsComposite.getAllElementTreeViewer().setInput(new Object[] { appRootElement });
                        allObjectsComposite.getAllElementTreeViewer().refresh();
                        allObjectsComposite.getAllElementTreeViewer().expandAll();
                        verifyCapturedElementsStates(
                                capturedObjectsComposite.getCapturedElements().toArray(new CapturedMobileElement[0]));
                        dialog.setCancelable(true);
                    }
                });
            }

            private void refreshDeviceView(String imgPath) {
                File imgFile = new File(imgPath);
                if (imgFile.exists()) {
                    deviceView.refreshDialog(imgFile, appRootElement);
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
            if (!mobileComposite.startApp(inspectorController, progressDlg)) {
                btnStart.setEnabled(true);
                return;
            }

            captureObjectAction();
            // If no exception, application has been successful started, enable more features
            btnCapture.setEnabled(true);
            btnStop.setEnabled(true);
            
            // send event for tracking
            Trackings.trackSpy("mobile");
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

            if (allObjectsComposite.getAllElementTreeViewer() != null) {
                allObjectsComposite.getAllElementTreeViewer().setInput(new Object[] {});
                allObjectsComposite.getAllElementTreeViewer().refresh();
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
    protected Control createButtonBar(Composite parent) {
        // No need bottom Button bar
        return parent;
    }

    @Override
    public boolean close() {
        stopObjectInspectorAction();
        try {
            preferencesHelper.save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        boolean result = super.close();
        Trackings.trackCloseSpy("mobile");
        instance = null;
        return result;
    }

    @Override
    protected Point getInitialLocation(Point initialSize) {
        Rectangle displayBounds = getShell().getMonitor().getBounds();
        return new Point(calculateObjectSpyDialogStartX(displayBounds, initialSize),
                calculateObjectSpyDialogStartY(displayBounds, initialSize));
    }

    private void setDeviceView(MobileDeviceDialog deviceView) {
        this.deviceView = deviceView;
    }

    public void addElements(List<WebElementEntity> webElements) {
        if (webElements == null) {
            return;
        }

        CapturedMobileElementConverter converter = new CapturedMobileElementConverter();
        List<CapturedMobileElement> newMobileElements = new ArrayList<>();
        for (WebElementEntity webElement : webElements) {
            newMobileElements.add(converter.revert(webElement));
        }

        capturedObjectsComposite.addElements(newMobileElements);
        verifyCapturedElementsStates(capturedObjectsComposite.getSelectedElements());
    }

    @Override
    public MobileObjectSpyPreferencesHelper getPreferencesHelper() {
        return preferencesHelper;
    }

    public static MobileObjectSpyDialog getInstance() {
        return instance;
    }

    public static void setInstance(MobileObjectSpyDialog instance) {
        MobileObjectSpyDialog.instance = instance;
    }

    @Override
    public MobileElementPropertiesComposite getPropertiesComposite() {
        return propertiesComposite;
    }

    @Override
    public void setSelectedElement(CapturedMobileElement element) {
        TreeMobileElement link = element.getLink();
        if (link != null) {
            allObjectsComposite.getAllElementTreeViewer().setSelection(new StructuredSelection(link));
            allObjectsComposite.getAllElementTreeViewer().getTree().setFocus();
        }
    }

    @Override
    public MobileCapturedObjectsComposite getCapturedObjectsComposite() {
        return capturedObjectsComposite;
    }

    @Override
    public void targetElementChanged(CapturedMobileElement mobileElement) {
        propertiesComposite.setEditingElement(mobileElement);
    }
}
