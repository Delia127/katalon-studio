package com.kms.katalon.composer.mobile.objectspy.dialog;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.execution.util.MobileDeviceUIProvider;
import com.kms.katalon.composer.mobile.constants.ImageConstants;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.tree.MobileElementLabelProvider;
import com.kms.katalon.composer.mobile.objectspy.element.tree.MobileElementTreeContentProvider;
import com.kms.katalon.composer.mobile.objectspy.util.MobileElementUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.keyword.AndroidProperties;
import com.kms.katalon.core.mobile.keyword.IOSProperties;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;


public class MobileObjectSpyDialog extends Dialog implements EventHandler {

    public static final Point DIALOG_SIZE = new Point(800, 600);

    private static final String DIALOG_TITLE = StringConstants.DIA_DIALOG_TITLE_MOBILE_OBJ_INSPECTOR;

    private static final int DIALOG_MARGIN_OFFSET = 5;

    private Text txtAppFile, txtObjectName;

    private Combo cbbDevices, cbbAppType;

    private Button btnBrowse;

    private TableViewer attributesTableViewer;

    private CheckboxTreeViewer elementTreeViewer;

    private ToolItem btnStart, btnCapture, btnAdd, btnStop;

    private MobileElement selectedElement;

    private FolderEntity parentFolder;

    private FolderEntity orsRootNode;

    private MobileElement appRootElement;

    private boolean isDisposed;

    private String ANDROID_FILTER_NAMES = "Android Application (*.apk)";

    private String ANDROID_FILTER_EXTS = "*.apk";

    private String IOS_FILTER_NAMES = "iOS Application (*.app, *.ipa)";

    private String IOS_FILTER_EXTS = "*.app;*.ipa";

    private MobileInspectorController inspectorController;

    private List<MobileDeviceInfo> deviceInfos = new ArrayList<>();

    private MobileDeviceDialog deviceView;

    private Composite container;

    private boolean canceledBeforeOpening;

    public boolean isCanceledBeforeOpening() {
        return canceledBeforeOpening;
    }

    public MobileObjectSpyDialog(Shell parentShell) throws Exception {
        super(parentShell);
        setShellStyle(SWT.SHELL_TRIM | SWT.NONE);
        this.isDisposed = false;
        this.inspectorController = new MobileInspectorController();
        this.orsRootNode = FolderController.getInstance().getObjectRepositoryRoot(
                ProjectController.getInstance().getCurrentProject());
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));

        SashForm sashForm = new SashForm(container, SWT.NONE);
        sashForm.setSashWidth(3);
        sashForm.setLayout(new FillLayout());

        Composite explorerComposite = new Composite(sashForm, SWT.NONE);
        explorerComposite.setLayout(new GridLayout());

        addElementTreeToolbar(explorerComposite);

        Label lblCapturedObjects = new Label(explorerComposite, SWT.NONE);
        lblCapturedObjects.setFont(getFontBold(lblCapturedObjects));
        lblCapturedObjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblCapturedObjects.setText(StringConstants.DIA_LBL_CAPTURED_OBJECTS);

        elementTreeViewer = new CheckboxTreeViewer(explorerComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI) {
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

        MobileElementTreeContentProvider contentProvider = new MobileElementTreeContentProvider();
        elementTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

        elementTreeViewer.setContentProvider(contentProvider);
        elementTreeViewer.setLabelProvider(new MobileElementLabelProvider());

        elementTreeViewer.setCellEditors(new CellEditor[] { new TextCellEditor(elementTreeViewer.getTree()) });
        elementTreeViewer.setColumnProperties(new String[] { "col1" });
        elementTreeViewer.setCellModifier(new ICellModifier() {

            public boolean canModify(Object element, String property) {
                return true;
            }

            public Object getValue(Object element, String property) {
                return ((MobileElement) element).getName();
            }

            public void modify(Object element, String property, Object value) {
                element = ((Item) element).getData();
                ((MobileElement) element).setName(value.toString());
                elementTreeViewer.update(element, null);
            }
        });

        TreeViewerFocusCellManager focusCellManager = new TreeViewerFocusCellManager(elementTreeViewer,
                new FocusCellOwnerDrawHighlighter(elementTreeViewer));
        ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(elementTreeViewer) {
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
                        || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
                        || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
                        || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }
        };

        TreeViewerEditor.create(elementTreeViewer, focusCellManager, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
                | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL
                | ColumnViewerEditor.KEYBOARD_ACTIVATION);

        elementTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                if (event.getSelection() instanceof TreeSelection) {
                    TreeSelection treeSelection = (TreeSelection) event.getSelection();
                    if (treeSelection.getFirstElement() instanceof MobileElement) {
                        selectedElement = (MobileElement) treeSelection.getFirstElement();
                        refreshAttributesTable(selectedElement);
                        highLightObject(selectedElement);
                    }
                }
            }

        });

        Composite contentComposite = new Composite(sashForm, SWT.NONE);
        GridLayout glContentComposite = new GridLayout(3, false);
        contentComposite.setLayout(glContentComposite);

        addStartStopToolbar(contentComposite);

        Label lblConfiguration = new Label(contentComposite, SWT.NONE);
        lblConfiguration.setFont(getFontBold(lblConfiguration));
        lblConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, glContentComposite.numColumns, 1));
        lblConfiguration.setText(StringConstants.DIA_LBL_CONFIGURATIONS);

        // Device Name
        Label nameLabel = new Label(contentComposite, SWT.NONE);
        nameLabel.setText(StringConstants.DIA_LBL_DEVICE_NAME);

        cbbDevices = new Combo(contentComposite, SWT.READ_ONLY);
        cbbDevices.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        cbbDevices.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                validateToEnableStartButton();
            }
        });

        // Application Type
        Label typeLabel = new Label(contentComposite, SWT.NONE);
        typeLabel.setText(StringConstants.DIA_LBL_APP_TYPE);

        cbbAppType = new Combo(contentComposite, SWT.READ_ONLY);
        cbbAppType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        cbbAppType.setItems(new String[] { StringConstants.DIA_APP_TYPE_NATIVE_APP });
        cbbAppType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                validateToEnableStartButton();
            }
        });

        // Application File location
        Label appFileLabel = new Label(contentComposite, SWT.NONE);
        appFileLabel.setText(StringConstants.DIA_LBL_APP_FILE);

        txtAppFile = new Text(contentComposite, SWT.READ_ONLY | SWT.BORDER);
        txtAppFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtAppFile.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (selectedElement != null) {
                    selectedElement.setType(txtAppFile.getText());
                }
                validateToEnableStartButton();
            }
        });

        btnBrowse = new Button(contentComposite, SWT.PUSH);
        btnBrowse.setText(StringConstants.DIA_BTN_BROWSE);
        btnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(btnBrowse.getShell());
                dialog.setFilterNames(getFilterNames());
                dialog.setFilterExtensions(getFilterExtensions());
                String absolutePath = dialog.open();
                if (absolutePath == null)
                    return;
                txtAppFile.setText(absolutePath);
            }
        });

        GridData gdMarginTop15 = new GridData(SWT.FILL, SWT.CENTER, true, false, glContentComposite.numColumns, 1);
        gdMarginTop15.verticalIndent = 15;
        Label lblObjectProperties = new Label(contentComposite, SWT.NONE);
        lblObjectProperties.setFont(getFontBold(lblObjectProperties));
        lblObjectProperties.setLayoutData(gdMarginTop15);
        lblObjectProperties.setText(StringConstants.DIA_LBL_OBJECT_PROPERTIES);

        // Object Name
        Label objectNameLabel = new Label(contentComposite, SWT.NONE);
        objectNameLabel.setText(StringConstants.DIA_LBL_OBJECT_NAME);

        txtObjectName = new Text(contentComposite, SWT.BORDER);
        txtObjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        txtObjectName.setToolTipText(StringConstants.DIA_TOOLTIP_OBJECT_NAME);
        txtObjectName.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (selectedElement == null)
                    return;
                switch (e.keyCode) {
                    case SWT.CR:
                        String objectName = txtObjectName.getText();
                        if (isNotBlank(objectName) && !StringUtils.equals(selectedElement.getName(), objectName)) {
                            selectedElement.setName(objectName);
                            elementTreeViewer.update(selectedElement, new String[] { "name" });
                        }
                        break;
                    case SWT.ESC:
                        txtObjectName.setText(selectedElement.getName());
                        break;
                }
            }
        });

        Composite attributesTableComposite = new Composite(contentComposite, SWT.NONE);

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        attributesTableComposite.setLayout(tableColumnLayout);

        GridData attributesTableCompositeGridData = new GridData(SWT.LEFT, SWT.CENTER, true, true,
                glContentComposite.numColumns, 1);
        attributesTableCompositeGridData.heightHint = 10000;
        attributesTableCompositeGridData.widthHint = 10000;
        attributesTableComposite.setLayoutData(attributesTableCompositeGridData);

        attributesTableViewer = new TableViewer(attributesTableComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.FULL_SELECTION | SWT.BORDER);

        createColumns(attributesTableViewer, tableColumnLayout);

        // make lines and header visible
        final Table table = attributesTableViewer.getTable();

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        attributesTableViewer.setContentProvider(ArrayContentProvider.getInstance());

        attributesTableViewer.setInput(Collections.emptyList());

        sashForm.setWeights(new int[] { 1, 1 });

        return container;
    }
    
    private void validateToEnableStartButton() {
        boolean ableToStart = isNotBlank(txtAppFile.getText()) && cbbDevices.getSelectionIndex() >= 0
                && cbbAppType.getSelectionIndex() >= 0;
        btnStart.setEnabled(ableToStart);
    }

    @Override
    public void create() {
        super.create();

        updateDeviceNames();
    }

    private void updateDeviceNames() {
        try {
            new ProgressMonitorDialogWithThread(getShell()).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(StringConstants.DIA_JOB_TASK_LOADING_DEVICES, IProgressMonitor.UNKNOWN);
                    UISynchronizeService.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            ControlUtils.recursiveSetEnabled(container, false);

                        }
                    });
                    final List<String> devices = getAllDevicesName();

                    checkMonitorCanceled(monitor);

                    UISynchronizeService.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            if (!devices.isEmpty()) {
                                cbbDevices.setItems(devices.toArray(new String[] {}));

                                cbbDevices.select(0);
                            }
                            ControlUtils.recursiveSetEnabled(container, true);
                        }
                    });

                    monitor.done();
                }
            });
        } catch (InterruptedException ignored) {
            // User canceled
            canceledBeforeOpening = true;
        } catch (InvocationTargetException e) {
            LoggerSingleton.logError(e);
            Throwable targetException = e.getTargetException();
            MultiStatusErrorDialog.showErrorDialog(targetException,
                    StringConstants.DIA_ERROR_UNABLE_TO_COLLECT_DEVICES, targetException.getClass().getSimpleName());
            canceledBeforeOpening = true;
        }
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

    private List<String> getAllDevicesName() {
        deviceInfos.clear();
        deviceInfos.addAll(MobileDeviceUIProvider.getAllDevices());
        List<String> devicesNameList = new ArrayList<String>();
        for (MobileDeviceInfo deviceInfo : deviceInfos) {
            devicesNameList.add(deviceInfo.getDisplayName());
        }
        return devicesNameList;
    }

    private Font getFontBold(Label label) {
        FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
        return boldDescriptor.createFont(label.getDisplay());
    }

    private void addElementTreeToolbar(Composite explorerComposite) {
        ToolBar elementTreeToolbar = new ToolBar(explorerComposite, SWT.FLAT | SWT.RIGHT);

        btnCapture = new ToolItem(elementTreeToolbar, SWT.NONE);
        btnCapture.setImage(ImageConstants.IMG_24_CAPTURE);
        btnCapture.setText(StringConstants.DIA_TIP_CAPTURE_OBJ);
        btnCapture.setToolTipText(StringConstants.DIA_TIP_CAPTURE_OBJ);
        btnCapture.setEnabled(false);
        btnCapture.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                captureObjectAction();
            }
        });

        btnAdd = new ToolItem(elementTreeToolbar, SWT.NONE);
        btnAdd.setImage(ImageConstants.IMG_24_NEW_TEST_OBJECT);
        btnAdd.setText(StringConstants.DIA_TIP_ADD);
        btnAdd.setToolTipText(StringConstants.DIA_TIP_ADD);
        btnAdd.setEnabled(false);
        btnAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (appRootElement != null) {
                        // eventBroker.send(EventConstants.OBJECT_SPY_RESET_SELECTED_TARGET, "");
                        parentFolder = findSelectedFolderInExplorer();
                        if (parentFolder == null || parentFolder == orsRootNode) {
                            parentFolder = orsRootNode;
                            FolderEntity appRootFolderEntity = FolderController.getInstance().getFolder(
                                    orsRootNode.getLocation() + File.separator + appRootElement.getName());
                            if (appRootFolderEntity == null) {
                                FolderEntity folder = MobileElementUtil.convertPageElementToFolderEntity(
                                        appRootElement, parentFolder);
                                parentFolder = ObjectRepositoryController.getInstance().importWebElementFolder(folder,
                                        parentFolder);
                            } else {
                                parentFolder = appRootFolderEntity;
                            }
                        }
                        for (MobileElement childOfRoot : appRootElement.getChildrenElement()) {
                            addElement(childOfRoot, parentFolder);
                        }
                        // Clear all selected element
                        for (Object obj : elementTreeViewer.getCheckedElements()) {
                            elementTreeViewer.setChecked(obj, false);
                        }
                        IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
                        // Refresh explorer
                        eventBroker.send(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, new FolderTreeEntity(
                                parentFolder, null));
                        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, null);
                    }
                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                    MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, ex.getMessage());
                }
            }
        });

    }

    private void addStartStopToolbar(Composite contentComposite) {
        ToolBar contentToolbar = new ToolBar(contentComposite, SWT.FLAT | SWT.RIGHT);
        GridLayout parentLayout = (GridLayout) contentComposite.getLayout();
        contentToolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, parentLayout.numColumns, 1));

        btnStart = new ToolItem(contentToolbar, SWT.NONE);
        btnStart.setImage(ImageConstants.IMG_24_START_DEVICE);
        btnStart.setText(StringConstants.DIA_TIP_START_APP);
        btnStart.setToolTipText(StringConstants.DIA_TIP_START_APP);
        btnStart.setEnabled(false);
        btnStart.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Validate all required informations are filled
                if (validateData()) {
                    startObjectInspectorAction();
                }
            }
        });

        btnStop = new ToolItem(contentToolbar, SWT.NONE);
        btnStop.setImage(ImageConstants.IMG_24_STOP_DEVICE);
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

    @SuppressWarnings("unchecked")
    private void createColumns(TableViewer viewer, TableColumnLayout tableColumnLayout) {
        TableViewerColumn keyColumn = new TableViewerColumn(viewer, SWT.NONE);
        keyColumn.getColumn().setWidth(100);
        keyColumn.getColumn().setText(StringConstants.DIA_COL_NAME);
        keyColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Entry) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    if (entry.getKey() != null) {
                        return entry.getKey().toString();
                    }
                }
                return "";
            }
        });

        TableViewerColumn valueColumn = new TableViewerColumn(viewer, SWT.NONE);
        valueColumn.getColumn().setWidth(200);
        valueColumn.getColumn().setText(StringConstants.DIA_COL_VALUE);
        valueColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Entry) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    if (entry.getValue() != null) {
                        return entry.getValue().toString();
                    }
                }
                return "";
            }
        });

        valueColumn.setEditingSupport(new EditingSupport(viewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof Entry && value instanceof String) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    entry.setValue(String.valueOf(value));
                    attributesTableViewer.refresh(element);
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof Entry) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    if (entry.getValue() != null) {
                        return entry.getValue().toString();
                    }
                }
                return "";
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element instanceof Entry) {
                    return new TextCellEditor(attributesTableViewer.getTable());
                }
                return null;
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof Entry) {
                    return true;
                }
                return false;
            }
        });

        tableColumnLayout.setColumnData(keyColumn.getColumn(), new ColumnWeightData(20, 100, true));
        tableColumnLayout.setColumnData(valueColumn.getColumn(), new ColumnWeightData(80, 200, true));

    }

    @Override
    protected void handleShellCloseEvent() {
        super.handleShellCloseEvent();
        dispose();
    }

    private void addElement(MobileElement element, FolderEntity parentFolder) throws Exception {
        if (elementTreeViewer.getChecked(element) || elementTreeViewer.getGrayed(element)) {
            WebElementEntity convertedElement = MobileElementUtil.convertElementToWebElementEntity(element, null,
                    parentFolder);
            autoSelectObjectProperties(convertedElement);
            ObjectRepositoryController.getInstance().importWebElement(convertedElement, parentFolder);
        }
        for (MobileElement childElement : element.getChildrenElement()) {
            addElement(childElement, parentFolder);
        }
    }

    public void dispose() {
        isDisposed = true;
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
    }

    @Override
    public void handleEvent(Event event) {
    }

    // Should to allow edit Object name when user select an Object
    private void refreshAttributesTable(MobileElement selectedElement) {
        if (selectedElement != null) {
            txtObjectName.setText(selectedElement.getName());
            attributesTableViewer.setInput(new ArrayList<>(selectedElement.getAttributes().entrySet()));
        } else {
            attributesTableViewer.setInput(Collections.emptyList());
        }
        attributesTableViewer.refresh();
    }

    // Highlight Selected object on captured screenshot
    private void highLightObject(MobileElement selectedElement) {
        if (selectedElement == null) {
            return;
        }

        deviceView.highlightElement(selectedElement);
    }

    public boolean isOutOfBound(Rectangle displayBounds, Point dialogSize, int startX) {
        return startX < 0 || startX + dialogSize.x > displayBounds.width + displayBounds.x;
    }

    public int getDeviceViewStartXIfPlaceRight(Rectangle objectSpyViewBounds) {
        return objectSpyViewBounds.x + objectSpyViewBounds.width + DIALOG_MARGIN_OFFSET;
    }

    public int getDeviceViewStartXIfPlaceLeft(Rectangle objectSpyViewBounds, Point dialogSize) {
        return objectSpyViewBounds.x - dialogSize.x - DIALOG_MARGIN_OFFSET;
    }

    public int getDefaultDeviceViewDialogStartX(Rectangle displayBounds, Point dialogSize) {
        return displayBounds.width - dialogSize.x;
    }

    public Point calculateInitPositionForObjectSpyDialog() {
        Rectangle displayBounds = getShell().getDisplay().getBounds();
        Point dialogSize = MobileObjectSpyDialog.DIALOG_SIZE;
        return new Point(calculateObjectSpyDialogStartX(displayBounds, dialogSize), calculateObjectSpyDialogStartY(
                displayBounds, dialogSize));
    }

    public int calculateObjectSpyDialogStartX(Rectangle displayBounds, Point dialogSize) {
        int dialogsWidth = dialogSize.x + MobileDeviceDialog.DIALOG_SIZE.x;
        int startX = (displayBounds.width - dialogsWidth) / 2 + displayBounds.x;
        return Math.max(startX, 0);
    }

    public int calculateObjectSpyDialogStartY(Rectangle displayBounds, Point dialogSize) {
        int startY = displayBounds.height - dialogSize.y;
        return Math.max(startY, 0) / 2;
    }

    public Point calculateInitPositionForDeviceViewDialog() {
        Rectangle displayBounds = getShell().getMonitor().getBounds();
        Point dialogSize = MobileDeviceDialog.DIALOG_SIZE;
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

    public void openDeviceView() {
        if (deviceView != null && !deviceView.isDisposed()) {
            return;
        }
        deviceView = new MobileDeviceDialog(getShell(), calculateInitPositionForDeviceViewDialog());
        deviceView.open();
        setDeviceView(deviceView);
    }

    private void captureObjectAction() {
        final String appName = FilenameUtils.getName(txtAppFile.getText());
        final ProgressMonitorDialogWithThread dialog = new ProgressMonitorDialogWithThread(getShell());

        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask(StringConstants.DIA_JOB_TASK_CAPTURING_OBJECTS, IProgressMonitor.UNKNOWN);

                dialog.runAndWait(new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        appRootElement = inspectorController.getMobileObjectRoot();
                        appRootElement.setName(appName);
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
                        elementTreeViewer.setInput(new Object[] { appRootElement });
                        elementTreeViewer.refresh();
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
                    exception.getClass()
                    .getSimpleName());
        } finally {
            btnCapture.setEnabled(true);
        }
    }

    private void checkMonitorCanceled(IProgressMonitor monitor) throws InterruptedException {
        if (monitor.isCanceled()) {
            throw new InterruptedException(StringConstants.DIA_ERROR_MSG_OPERATION_CANCELED);
        }
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    public FolderEntity getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(FolderEntity parentFolder) {
        this.parentFolder = parentFolder;
    }

    private void startObjectInspectorAction() {

        // Temporary disable Start button while launching app
        btnStart.setEnabled(false);

        try {
            final MobileDeviceInfo selectDeviceInfo = getMobileDeviceInfo();
            if (selectDeviceInfo == null) {
                return;
            }
            final String appFile = txtAppFile.getText();

            final ProgressMonitorDialogWithThread progressDlg = new ProgressMonitorDialogWithThread(getShell()) {
                @Override
                public void cancelPressed() {
                    super.cancelPressed();
                    finishedRun();
                    getProgressMonitor().done();
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);
                    btnCapture.setEnabled(false);
                    btnAdd.setEnabled(false);
                }
            };

            IRunnableWithProgress processToRun = new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(StringConstants.DIA_LBL_STATUS_APP_STARTING, IProgressMonitor.UNKNOWN);

                    progressDlg.runAndWait(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            try {
                                // Start application using MobileDriver
                                inspectorController.startMobileApp(selectDeviceInfo, appFile, false);
                            } catch (Exception ex) {
                                LoggerSingleton.logError(ex);
                                throw new InvocationTargetException(ex, ex.getMessage());
                            }
                            return null;
                        }
                    });
                    checkMonitorCanceled(monitor);

                    monitor.done();
                }
            };

            progressDlg.run(true, true, processToRun);

            captureObjectAction();
            // If no exception, application has been successful started, enable more features
            btnAdd.setEnabled(true);
            btnCapture.setEnabled(true);
            btnStop.setEnabled(true);
        } catch (Exception ex) {
            // If user intentionally cancel the progress, don't need to show error message
            if (!StringConstants.DIA_ERROR_MSG_OPERATION_CANCELED.equals(ex.getMessage())) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                        StringConstants.DIA_ERROR_MSG_CANNOT_START_APP_ON_CURRENT_DEVICE + ": " + ex.getMessage());
                // Enable start button and show error dialog if application cannot start
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                btnCapture.setEnabled(false);
                btnAdd.setEnabled(false);
            }

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
            btnAdd.setEnabled(false);

            elementTreeViewer.setInput(new Object[] {});
            elementTreeViewer.refresh();
        }

        if (deviceView != null) {
            deviceView.closeApp();
        }

        dispose();
    }

    private boolean validateData() {
        if (cbbDevices.getSelectionIndex() < 0) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.DIA_ERROR_MSG_PLS_CONNECT_AND_SELECT_DEVICE);
            return false;
        }

        if (cbbAppType.getSelectionIndex() < 0) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.DIA_ERROR_MSG_PLS_SELECT_APP_TYPE);
            return false;
        }

        String appFilePath = txtAppFile.getText().trim();

        if (appFilePath.equals("")) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.DIA_ERROR_MSG_PLS_SELECT_APP_FILE);
            return false;
        }
        File appFile = new File(appFilePath);

        if (appFile.isDirectory() && !appFile.getName().endsWith(".app")) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.DIA_ERROR_MSG_APP_FILE_NOT_EXIST);
            return false;
        }
        return true;
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
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
        return super.close();
    }

    private FolderEntity findSelectedFolderInExplorer() throws Exception {
        ESelectionService selectionService = SelectionServiceSingleton.getInstance().getSelectionService();
        Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        if (selectedObjects != null && selectedObjects.length > 0) {
            ITreeEntity parentTreeEntity = getParentTreeEntity(selectedObjects);
            if (parentTreeEntity != null && parentTreeEntity.getObject() instanceof FolderEntity) {
                FolderEntity parentFolder = (FolderEntity) parentTreeEntity.getObject();
                return parentFolder;
            }
        }
        return null;
    }

    private ITreeEntity getParentTreeEntity(Object[] selectedObjects) throws Exception {
        for (Object object : selectedObjects) {
            if (object instanceof ITreeEntity) {
                if (((ITreeEntity) object).getObject() instanceof FolderEntity) {
                    FolderEntity folder = (FolderEntity) ((ITreeEntity) object).getObject();
                    if (folder.getFolderType() == FolderType.WEBELEMENT) {
                        return (ITreeEntity) object;
                    }
                } else if (((ITreeEntity) object).getObject() instanceof WebElementEntity) {
                    return (ITreeEntity) ((ITreeEntity) object).getParent();
                }
            }
        }
        return null;
    }

    private String[] getFilterNames() {
        if (StringUtils.equals(Platform.getOS(), Platform.OS_MACOSX)) {
            return new String[] { ANDROID_FILTER_NAMES, IOS_FILTER_NAMES };
        }
        return new String[] { ANDROID_FILTER_NAMES };
    }

    private String[] getFilterExtensions() {
        if (StringUtils.equals(Platform.getOS(), Platform.OS_MACOSX)) {
            return new String[] { ANDROID_FILTER_EXTS, IOS_FILTER_EXTS };
        }
        return new String[] { ANDROID_FILTER_EXTS };
    }

    @Override
    protected Point getInitialLocation(Point initialSize) {
        Rectangle displayBounds = getShell().getMonitor().getBounds();
        return new Point(calculateObjectSpyDialogStartX(displayBounds, initialSize), calculateObjectSpyDialogStartY(
                displayBounds, initialSize));
    }

    private void autoSelectObjectProperties(WebElementEntity entity) {
        if (getMobileDeviceInfo() == null) {
            return;
        }
        List<String> typicalProps = new ArrayList<String>();
        if (isMobileDriverTypeOf(MobileDriverType.ANDROID_DRIVER)) {
            typicalProps.addAll(Arrays.asList(AndroidProperties.ANDROID_TYPICAL_PROPERTIES));
        } else if (isMobileDriverTypeOf(MobileDriverType.IOS_DRIVER)) {
            typicalProps.addAll(Arrays.asList(IOSProperties.IOS_TYPICAL_PROPERTIES));
        }
        for (WebElementPropertyEntity prop : entity.getWebElementProperties()) {
            if (typicalProps.contains(prop.getName())) {
                prop.setIsSelected(true);
            }
        }
    }

    private MobileDeviceInfo getMobileDeviceInfo() {
        int selectedMobileDeviceIndex = cbbDevices.getSelectionIndex();
        if (selectedMobileDeviceIndex < 0 || selectedMobileDeviceIndex >= deviceInfos.size()) {
            return null;
        }
        return deviceInfos.get(selectedMobileDeviceIndex);
    }

    private boolean isMobileDriverTypeOf(MobileDriverType type) {
        return MobileInspectorController.getMobileDriverType(getMobileDeviceInfo()) == type;
    }

    public MobileDeviceDialog getDeviceView() {
        return deviceView;
    }

    public void setDeviceView(MobileDeviceDialog deviceView) {
        this.deviceView = deviceView;
    }

    private class ProgressMonitorDialogWithThread extends ProgressMonitorDialog {
        public ProgressMonitorDialogWithThread(Shell parent) {
            super(parent);
        }

        private Thread thread;

        private void setThread(Thread thread) {
            this.thread = thread;
        }

        @Override
        protected void cancelPressed() {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }

            super.cancelPressed();
        }

        private void startThreadAndWait() {
            if (thread == null) {
                return;
            }

            thread.run();

            while (thread.isAlive()) {
                // wait for thread complete or interrupted
            }
        }

        public <V> V runAndWait(final Callable<V> callable) throws InterruptedException, InvocationTargetException {
            FutureTask<V> futureTask = new FutureTask<V>(callable);
            setThread(new Thread(futureTask));
            startThreadAndWait();

            try {
                return futureTask.get();
            } catch (ExecutionException e) {
                throw new InvocationTargetException(e);
            }
        }
    }
}
