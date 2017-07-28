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
import org.eclipse.swt.custom.StackLayout;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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
import com.kms.katalon.composer.mobile.objectspy.components.KobitonAppComposite;
import com.kms.katalon.composer.mobile.objectspy.components.LocalAppComposite;
import com.kms.katalon.composer.mobile.objectspy.constant.ComposerMobileObjectspyMessageConstants;
import com.kms.katalon.composer.mobile.objectspy.constant.ImageConstants;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.element.CapturedMobileElementConverter;
import com.kms.katalon.composer.mobile.objectspy.element.MobileDeviceType;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.TreeMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.provider.CapturedElementLabelProvider;
import com.kms.katalon.composer.mobile.objectspy.element.provider.SelectableElementEditingSupport;
import com.kms.katalon.composer.mobile.objectspy.element.tree.MobileElementLabelProvider;
import com.kms.katalon.composer.mobile.objectspy.element.tree.MobileElementTreeContentProvider;
import com.kms.katalon.composer.mobile.objectspy.preferences.MobileObjectSpyPreferencesHelper;
import com.kms.katalon.composer.mobile.objectspy.util.KobitonValidator;
import com.kms.katalon.composer.mobile.objectspy.viewer.CapturedObjectTableViewer;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.keyword.internal.GUIObject;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;

public class MobileObjectSpyDialog extends Dialog implements MobileElementInspectorDialog, MobileAppDialog {

    public static final Point DIALOG_SIZE = new Point(800, 800);

    private static final String DIALOG_TITLE = StringConstants.DIA_DIALOG_TITLE_MOBILE_OBJ_SPY;

    private static final int DIALOG_MARGIN_OFFSET = 5;

    private Combo cbbAppType;

    private CheckboxTreeViewer allElementTreeViewer;

    private ToolItem btnStart, btnCapture, btnAdd, btnStop;

    private TreeMobileElement appRootElement;

    private boolean disposed;

    private MobileInspectorController inspectorController;

    private MobileDeviceDialog deviceView;

    private Composite container;

    private boolean canceledBeforeOpening;

    private CapturedObjectTableViewer capturedObjectsTableViewer;

    private TableColumn tblclmnCapturedObjectsSelection;

    private MobileElementPropertiesComposite propertiesComposite;

    private MobileObjectSpyPreferencesHelper preferencesHelper;

    private LocalAppComposite localAppComposite;

    private KobitonAppComposite kobitonAppComposite;

    private StackLayout stackLayout;

    private Composite appsComposite;

    public boolean isCanceledBeforeOpening() {
        return canceledBeforeOpening;
    }

    public MobileObjectSpyDialog(Shell parentShell) throws Exception {
        super(parentShell);
        setShellStyle(SWT.SHELL_TRIM | SWT.RESIZE);
        this.disposed = false;
        this.inspectorController = new MobileInspectorController();
        this.preferencesHelper = new MobileObjectSpyPreferencesHelper();
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

        propertiesComposite = new MobileElementPropertiesComposite(this);
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

        sashForm.setWeights(new int[] { 4, 6 });

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

        capturedObjectsTableViewer = new CapturedObjectTableViewer(capturedObjectTableComposite,
                SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION, this);
        Table capturedObjectsTable = capturedObjectsTableViewer.getTable();
        capturedObjectsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        capturedObjectsTable.setHeaderVisible(true);
        capturedObjectsTable.setLinesVisible(true);

        TableViewerColumn tbvclCapturedObjectsSelection = new TableViewerColumn(capturedObjectsTableViewer, SWT.NONE);
        tblclmnCapturedObjectsSelection = tbvclCapturedObjectsSelection.getColumn();
        tbvclCapturedObjectsSelection
                .setLabelProvider(new CapturedElementLabelProvider(CapturedElementLabelProvider.SELECTION_COLUMN_IDX));
        tbvclCapturedObjectsSelection
                .setEditingSupport(new SelectableElementEditingSupport(capturedObjectsTableViewer));

        TableViewerColumn tableViewerColumnCapturedObjects = new TableViewerColumn(capturedObjectsTableViewer,
                SWT.NONE);
        TableColumn tblclmnCapturedObjects = tableViewerColumnCapturedObjects.getColumn();
        tblclmnCapturedObjects.setText(StringConstants.NAME);
        tableViewerColumnCapturedObjects
                .setLabelProvider(new CapturedElementLabelProvider(CapturedElementLabelProvider.ELEMENT_COLUMN_IDX));

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
                CapturedMobileElement firstElement = (CapturedMobileElement) selection.getFirstElement();
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
                    highlightObject((CapturedMobileElement) item.getData());
                }
            }
        });

        capturedObjectsTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                CapturedMobileElement[] elements = capturedObjectsTableViewer.getSelectedElements();
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
                CapturedMobileElement mobileElement = capturedObjectsTableViewer.getSelectedElement();
                TreeMobileElement link = mobileElement.getLink();
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

    private void verifyCapturedElementsStates(CapturedMobileElement[] elements) {
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
                allElementTreeViewer.setChecked(foundElement, true);
            }
        }

        allElementTreeViewer.refresh();
        capturedObjectsTableViewer.refresh();
    }

    private void removeSelectedCapturedElements(CapturedMobileElement[] elements) {
        clearAllObjectState(elements);
        allElementTreeViewer.refresh();

        capturedObjectsTableViewer.removeCapturedElements(Arrays.asList(elements));

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
        lblAllObjects.setFont(getFontBold(lblAllObjects));
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

        allElementTreeViewer.setLabelProvider(new MobileElementLabelProvider());
        allElementTreeViewer.setContentProvider(new MobileElementTreeContentProvider());

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
                    highlightObject((MobileElement) item.getData());
                }
            }
        });

        allElementTreeViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                TreeMobileElement selectedElement = (TreeMobileElement) event.getElement();
                if (event.getChecked()) {
                    List<CapturedMobileElement> mobileElements = new ArrayList<>();
                    mobileElements.add(selectedElement.newCapturedElement());
                    capturedObjectsTableViewer.addMobileElements(mobileElements);

                    propertiesComposite.focusAndEditCapturedElementName();
                } else {
                    CapturedMobileElement capturedElement = selectedElement.getCapturedElement();
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

    private void createSettingComposite(Composite parent) {
        Composite settingComposite = new Composite(parent, SWT.NONE);
        settingComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glSettingComposite = new GridLayout(2, false);
        glSettingComposite.horizontalSpacing = 10;
        settingComposite.setLayout(glSettingComposite);

        Label lblConfiguration = new Label(settingComposite, SWT.NONE);
        lblConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        lblConfiguration.setFont(getFontBold(lblConfiguration));
        lblConfiguration.setText(StringConstants.DIA_LBL_CONFIGURATIONS);

        // Application Type
        Label typeLabel = new Label(settingComposite, SWT.NONE);
        typeLabel.setText(StringConstants.DIA_LBL_APP_TYPE);

        cbbAppType = new Combo(settingComposite, SWT.READ_ONLY);
        cbbAppType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        cbbAppType.setItems(getAllDeviceTypeStringValues());
        cbbAppType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                changeAppComposite();
                refreshButtonsState();
            }
        });

        appsComposite = new Composite(settingComposite, SWT.NONE);
        stackLayout = new StackLayout();
        appsComposite.setLayout(stackLayout);
        appsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        localAppComposite = new LocalAppComposite(appsComposite, this, preferencesHelper, SWT.NONE);
        kobitonAppComposite = new KobitonAppComposite(appsComposite, this, SWT.NONE);

        changeAppCompositeToLocal();
    }

    private void changeAppComposite() {
        MobileDeviceType mobileDeviceType = MobileDeviceType.fromDisplayName(cbbAppType.getText());
        changeAppComposite(mobileDeviceType);
    }

    private void changeAppComposite(MobileDeviceType mobileDeviceType) {
        if (mobileDeviceType == MobileDeviceType.Kobiton && stackLayout.topControl != kobitonAppComposite) {
            changeAppCompositeToKobiton();
        } else if (mobileDeviceType == MobileDeviceType.Local && stackLayout.topControl != localAppComposite) {
            changeAppCompositeToLocal();
        }
        appsComposite.layout();
    }

    private void changeAppCompositeToLocal() {
        stackLayout.topControl = localAppComposite;
    }

    private void changeAppCompositeToKobiton() {
        if (!KobitonValidator.validateKobitonIntergration()) {
            cbbAppType.select(MobileDeviceType.indexOf(MobileDeviceType.Local));
            return;
        }
        stackLayout.topControl = kobitonAppComposite;
        updateDeviceNames();
        updateApps();
    }

    private String[] getAllDeviceTypeStringValues() {
        return Arrays.asList(MobileDeviceType.values())
                .stream()
                .filter(deviceType -> deviceType.isSupported())
                .map(deviceType -> deviceType.getDisplayName())
                .toArray(String[]::new);
    }

    /* package */ void updateSelectedElement(CapturedMobileElement selectedElement) {
        capturedObjectsTableViewer.refresh(selectedElement, true);
        TreeMobileElement element = selectedElement.getLink();
        if (element != null) {
            allElementTreeViewer.refresh(element);
            allElementTreeViewer.setSelection(new StructuredSelection(element));
        }
    }

    @Override
    public void refreshButtonsState() {
        if (cbbAppType.getSelectionIndex() < 0) {
            btnStart.setEnabled(false);
            return;
        }
        if (stackLayout.topControl == localAppComposite) {
            btnStart.setEnabled(localAppComposite.isAbleToStart());
        } else if (stackLayout.topControl == kobitonAppComposite) {
            btnStart.setEnabled(kobitonAppComposite.isAbleToStart());
        }
    }

    @Override
    public void create() {
        super.create();

        updateDeviceNames();

        cbbAppType.select(0);
        refreshButtonsState();

        capturedObjectsTableViewer.setCapturedElements(new ArrayList<CapturedMobileElement>());
    }

    @Override
    public void updateDeviceNames() {
        try {
            ControlUtils.recursiveSetEnabled(container, false);
            if (stackLayout.topControl == localAppComposite) {
                localAppComposite.updateLocalDevices();
            } else if (stackLayout.topControl == kobitonAppComposite) {
                kobitonAppComposite.updateKobitonDevices();
            }
        } catch (InterruptedException ignored) {
            // User canceled
            canceledBeforeOpening = true;
        } catch (InvocationTargetException e) {
            LoggerSingleton.logError(e);
            Throwable targetException = e.getTargetException();
            MultiStatusErrorDialog.showErrorDialog(targetException, StringConstants.DIA_ERROR_UNABLE_TO_COLLECT_DEVICES,
                    targetException.getClass().getSimpleName());
            canceledBeforeOpening = true;
        } finally {
            ControlUtils.recursiveSetEnabled(container, true);
            refreshButtonsState();
        }
    }

    private void updateApps() {
        try {
            ControlUtils.recursiveSetEnabled(container, false);
            if (stackLayout.topControl != kobitonAppComposite) {
                return;
            }
            kobitonAppComposite.updateKobitonApps();
        } catch (InterruptedException ignored) {
            // User canceled
            canceledBeforeOpening = true;
        } catch (InvocationTargetException e) {
            LoggerSingleton.logError(e);
            Throwable targetException = e.getTargetException();
            MultiStatusErrorDialog.showErrorDialog(targetException,
                    ComposerMobileObjectspyMessageConstants.ERR_MSG_CANNOT_COLLECT_APPS,
                    targetException.getClass().getSimpleName());
            canceledBeforeOpening = true;
        } finally {
            ControlUtils.recursiveSetEnabled(container, true);
            refreshButtonsState();
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

    private Font getFontBold(Label label) {
        FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
        return boldDescriptor.createFont(label.getDisplay());
    }

    private void addElementTreeToolbar(Composite explorerComposite) {
        ToolBar elementTreeToolbar = new ToolBar(explorerComposite, SWT.FLAT | SWT.RIGHT);
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
                    removeSelectedCapturedElements(
                            capturedObjectsTableViewer.getAllCheckedElements().toArray(new CapturedMobileElement[0]));
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
                for (CapturedMobileElement mobileElement : capturedObjectsTableViewer.getAllCheckedElements()) {
                    WebElementEntity testObject = converter.convert(mobileElement, folder, currentMobileType);
                    objectRepositoryController.updateTestObject(testObject);
                    newTreeEntities.add(new WebElementTreeEntity(testObject, folderTreeEntity));
                }
                return newTreeEntities;
            }
        });
    }

    private MobileDriverType getCurrentMobileDriverType() {
        if (stackLayout.topControl == localAppComposite) {
            return MobileInspectorController.getMobileDriverType(localAppComposite.getSelectedMobileDeviceInfo());
        } else if (stackLayout.topControl == kobitonAppComposite) {
            return MobileInspectorController.getMobileDriverType(kobitonAppComposite.getSelectedKobitonDevice());
        }
        return null;
    }

    private void addStartStopToolbar(Composite contentComposite) {
        Composite toolbarComposite = new Composite(contentComposite, SWT.NONE);
        toolbarComposite.setLayout(new GridLayout(2, false));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        ToolBar contentToolbar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
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
    private void highlightObject(MobileElement selectedElement) {
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
                        allElementTreeViewer.setInput(new Object[] { appRootElement });
                        allElementTreeViewer.refresh();
                        allElementTreeViewer.expandAll();
                        verifyCapturedElementsStates(
                                capturedObjectsTableViewer.getCapturedElements().toArray(new CapturedMobileElement[0]));
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
        if (stackLayout.topControl == localAppComposite) {
            return localAppComposite.getAppName();
        } else if (stackLayout.topControl == kobitonAppComposite) {
            return kobitonAppComposite.getAppName();
        }
        return "";
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
        final ProgressMonitorDialogWithThread progressDlg = new ProgressMonitorDialogWithThread(getShell()) {
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
            if (stackLayout.topControl == localAppComposite
                    && !localAppComposite.startLocalApp(inspectorController, progressDlg)) {
                btnStart.setEnabled(true);
                return;
            } else if (stackLayout.topControl == kobitonAppComposite
                    && !kobitonAppComposite.startKobitonApp(inspectorController, progressDlg)) {
                btnStart.setEnabled(true);
                return;
            }

            captureObjectAction();
            // If no exception, application has been successful started, enable more features
            btnCapture.setEnabled(true);
            btnStop.setEnabled(true);
        } catch (InvocationTargetException | InterruptedException ex) {
            // If user intentionally cancel the progress, don't need to show error message
            if (ex instanceof InvocationTargetException) {
                Throwable targetException = ((InvocationTargetException) ex).getTargetException();
                String message = (targetException instanceof java.util.concurrent.ExecutionException)
                        ? targetException.getCause().getMessage() : targetException.getMessage();
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                        StringConstants.DIA_ERROR_MSG_CANNOT_START_APP_ON_CURRENT_DEVICE + ": " + message);
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
        if (cbbAppType.getSelectionIndex() < 0) {
            MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE,
                    StringConstants.DIA_ERROR_MSG_PLS_SELECT_APP_TYPE);
            return false;
        }
        if (stackLayout.topControl == localAppComposite) {
            return localAppComposite.validateLocalAppSetting();
        } else if (stackLayout.topControl == kobitonAppComposite) {
            return kobitonAppComposite.validateKobitonAppSetting();
        }
        return false;
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
        return super.close();
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

        capturedObjectsTableViewer.addMobileElements(newMobileElements);
        verifyCapturedElementsStates(capturedObjectsTableViewer.getSelectedElements());
    }
}
