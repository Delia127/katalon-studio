package com.kms.katalon.composer.webui.recorder.dialog;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.BindException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TableDropTargetEffect;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.osgi.framework.Bundle;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.components.impl.control.DropdownToolItemSelectionListener;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionParamValueType;
import com.kms.katalon.composer.webui.recorder.action.HTMLSynchronizeAction;
import com.kms.katalon.composer.webui.recorder.action.HTMLValidationAction;
import com.kms.katalon.composer.webui.recorder.action.IHTMLAction;
import com.kms.katalon.composer.webui.recorder.constants.ComposerWebuiRecorderMessageConstants;
import com.kms.katalon.composer.webui.recorder.constants.ImageConstants;
import com.kms.katalon.composer.webui.recorder.constants.RecorderPreferenceConstants;
import com.kms.katalon.composer.webui.recorder.constants.StringConstants;
import com.kms.katalon.composer.webui.recorder.core.HTMLElementRecorderServer;
import com.kms.katalon.composer.webui.recorder.core.RecordSession;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.composer.webui.recorder.util.RecordSessionUtil;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.objectspy.components.CapturedHTMLElementsComposite;
import com.kms.katalon.objectspy.dialog.AddToObjectRepositoryDialog;
import com.kms.katalon.objectspy.dialog.GoToAddonStoreMessageDialog;
import com.kms.katalon.objectspy.dialog.AddToObjectRepositoryDialog.AddToObjectRepositoryDialogResult;
import com.kms.katalon.objectspy.dialog.ObjectSpyDialog;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLFrameElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;
import com.kms.katalon.objectspy.element.tree.HTMLElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.HTMLElementTreeContentProvider;
import com.kms.katalon.objectspy.exception.IEAddonNotInstalledException;
import com.kms.katalon.objectspy.util.WinRegistry;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

@SuppressWarnings("restriction")
public class RecorderDialog extends Dialog {
    private static final String IE_WINDOW_CLASS = "IEFrame"; //$NON-NLS-1$

    private static final String relativePathToIEAddonSetup = File.separator + "extensions" + File.separator + "IE" //$NON-NLS-1$ //$NON-NLS-2$
            + File.separator + RecordSession.RECORDER_ADDON_NAME + File.separator + "setup.exe"; //$NON-NLS-1$

    private static final String RESOURCES_FOLDER_NAME = "resources"; //$NON-NLS-1$

    private static final String IE_ADDON_BHO_KEY = "{FEA8CA38-7979-4F6A-83E4-2949EDEA96EF}"; //$NON-NLS-1$

    public static final String DIA_INSTANT_BROWSER_CHROME_RECORDER_EXTENSION_PATH = "<Katalon build path>/Resources/extensions/Chrome/Recorder Packed"; //$NON-NLS-1$

    public static final String RECORDER_CHROME_ADDON_URL = "https://chrome.google.com/webstore/detail/katalon-recorder/bnaalgpdhfjepeanejkicnidgbpbmkhh"; //$NON-NLS-1$

    public static final String RECORDER_FIREFOX_ADDON_URL = "https://addons.mozilla.org/en-US/firefox/addon/katalon-recorder/"; //$NON-NLS-1$

    private static final int ANY_PORT_NUMBER = 0;

    private static final String TABLE_COLUMN_ELEMENT_TITLE = StringConstants.DIA_COL_ELEMENT;

    private static final String TABLE_COLUMN_ACTION_DATA_TITLE = StringConstants.DIA_COL_ACTION_DATA;

    private static final String TABLE_COLUMN_ACTION_TITLE = StringConstants.DIA_COL_ACTION;

    private static final String TABLE_COLUMN_NO_TITLE = StringConstants.DIA_COL_NO;

    private static final String RESUME_TOOL_ITEM_LABEL = StringConstants.DIA_TOOLITEM_RESUME;

    private static final String STOP_TOOL_ITEM_LABEL = StringConstants.DIA_TOOLITEM_STOP;

    private static final String PAUSE_TOOL_ITEM_LABEL = StringConstants.DIA_TOOLITEM_PAUSE;

    private static final String START_TOOL_ITEM_LABEL = StringConstants.DIA_TOOLITEM_START;

    private HTMLElementRecorderServer server;

    private Logger logger;

    private List<HTMLPageElement> elements;

    private List<HTMLActionMapping> recordedActions;

    private boolean isPausing;

    private TableViewer actionTableViewer;

    private ToolBar toolBar;

    private ToolItem toolItemBrowserDropdown, tltmPause, tltmStop;

    private RecordSession session;

    private AddToObjectRepositoryDialogResult targetFolderSelectionResult;

    private CapturedHTMLElementsComposite capturedObjectComposite;

    private WebUIDriverType selectedBrowser;

    private ToolItem tltmDelete;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public RecorderDialog(Shell parentShell, Logger logger, IEventBroker eventBroker) {
        super(parentShell);
        setShellStyle(SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
        this.logger = logger;
        elements = new ArrayList<HTMLPageElement>();
        recordedActions = new ArrayList<HTMLActionMapping>();
        isPausing = false;
    }

    private void startBrowser() {
        startBrowser(false);
    }

    private void startBrowser(boolean isInstant) {
        try {
            if (selectedBrowser == WebUIDriverType.IE_DRIVER) {
                checkIEAddon();
            }
            if (isInstant) {
                startServer(getInstantBrowsersPort());
                if (selectedBrowser == WebUIDriverType.IE_DRIVER) {
                    runInstantIE();
                }
            } else {
                startServer();
                startRecordSession(selectedBrowser);
            }

            tltmPause.setEnabled(true);
            tltmStop.setEnabled(true);
            resume();
            resetInput();
        } catch (final IEAddonNotInstalledException e) {
            stop();
            showMessageForMissingIEAddon();
            try {
                runIEAddonInstaller();
            } catch (IOException iOException) {
                LoggerSingleton.logError(iOException);
            }
        } catch (Exception e) {
            logger.error(e);
            MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, e.getMessage());
        }
    }

    protected void runInstantIE() throws Exception {
        session = new RecordSession(server, selectedBrowser, ProjectController.getInstance().getCurrentProject(),
                logger);
        session.setupIE();
        HWND hwnd = User32.INSTANCE.FindWindow(IE_WINDOW_CLASS, null);
        if (hwnd == null) {
            return;
        }
        shiftFocusToWindow(hwnd);
    }

    private void shiftFocusToWindow(HWND hwnd) {
        User32.INSTANCE.ShowWindow(hwnd, 9);        // SW_RESTORE
        User32.INSTANCE.SetForegroundWindow(hwnd);   // bring to front
    }

    private void checkIEAddon() throws IllegalAccessException, InvocationTargetException, IEAddonNotInstalledException {
        if (checkRegistryKey(ObjectSpyDialog.IE_WINDOWS_32BIT_BHO_REGISTRY_KEY)
                || checkRegistryKey(ObjectSpyDialog.IE_WINDOWS_BHO_REGISTRY_KEY)) {
            return;
        }
        throw new IEAddonNotInstalledException(RecordSession.RECORDER_ADDON_NAME);
    }

    private boolean checkRegistryKey(String parentKey) throws IllegalAccessException, InvocationTargetException {
        List<String> bhos = WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE, parentKey);
        if (bhos == null || bhos.isEmpty()) {
            return false;
        }
        for (String bho : bhos) {
            if (bho.toLowerCase().equals(IE_ADDON_BHO_KEY.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void showMessageForMissingIEAddon() {
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openInformation(Display.getCurrent().getActiveShell(), StringConstants.INFO,
                        StringConstants.DIALOG_CANNOT_START_IE_MESSAGE);
            }
        });
    }

    private File getResourcesDirectory() throws IOException {
        Bundle bundleExec = Platform.getBundle(IdConstants.KATALON_WEB_UI_RECORDER_BUNDLE_ID);
        File bundleFile = FileLocator.getBundleFile(bundleExec);
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(bundleFile + File.separator + RESOURCES_FOLDER_NAME);
        }
        // run as product
        return new File(ClassPathResolver.getConfigurationFolder() + File.separator + RESOURCES_FOLDER_NAME);
    }

    private void runIEAddonInstaller() throws IOException {
        String ieAddonSetupPath = getResourcesDirectory().getAbsolutePath() + relativePathToIEAddonSetup;
        Desktop desktop = Desktop.getDesktop();
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        desktop.open(new File(ieAddonSetupPath));
    }

    private void resetInput() {
        elements.clear();
        recordedActions.clear();
        actionTableViewer.refresh();
    }

    private void startRecordSession(WebUIDriverType webUiDriverType) throws Exception {
        stopRecordSession();
        session = new RecordSession(server, webUiDriverType, ProjectController.getInstance().getCurrentProject(),
                logger);
        new Thread(session).start();
    }

    private int getInstantBrowsersPort() {
        return PreferenceStoreManager.getPreferenceStore(RecorderPreferenceConstants.WEBUI_RECORDER_QUALIFIER)
                .getInt(RecorderPreferenceConstants.WEBUI_RECORDER_INSTANT_BROWSER_PORT);
    }

    private void startServer(int port) throws Exception {
        if (server != null && server.isStarted() && isCurrentServerPortUsable(port)) {
            return;
        }
        stopServer();
        try {
            server = new HTMLElementRecorderServer(port, logger, this);
            server.start();
        } catch (BindException e) {
            MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE,
                    MessageFormat.format(ComposerWebuiRecorderMessageConstants.ERR_DLG_PORT_FOR_RECORD_IN_USE, port));
            server = null;
        }
    }

    private void stopServer() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    public boolean isCurrentServerPortUsable(int port) {
        return port == ANY_PORT_NUMBER || port == server.getServerPort();
    }

    private void startServer() throws Exception {
        startServer(ANY_PORT_NUMBER);
    }

    class StartItemDropdownSelectionListener extends DropdownToolItemSelectionListener {

        private Menu menu;

        public StartItemDropdownSelectionListener(Menu menu) {
            this.menu = menu;
        }

        @Override
        protected Menu getMenu() {
            return menu;
        }

        @Override
        protected void centerWigetSelected(SelectionEvent event) {
            ToolItem item = (ToolItem) event.widget;
            if (item.getText().equals(StringConstants.INSTANT_BROWSER_PREFIX)) {
                startBrowser(true);
                return;
            }
            if (item.getText().equals(START_TOOL_ITEM_LABEL)) {
                changeBrowser(WebUIDriverType.FIREFOX_DRIVER);
            }
            startBrowser();
        }
    }

    private void pause() {
        isPausing = true;
        tltmPause.setText(RESUME_TOOL_ITEM_LABEL);
        tltmPause.setImage(ImageConstants.IMG_24_PLAY);
        toolBar.getParent().layout();
    }

    private void resume() {
        isPausing = false;
        tltmPause.setText(PAUSE_TOOL_ITEM_LABEL);
        tltmPause.setImage(ImageConstants.IMG_24_PAUSE);
        toolBar.getParent().layout();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // Set window title for dialog
        if (getShell() != null)
            getShell().setText(StringConstants.DIA_TITLE_RECORD);

        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gl_container = new GridLayout();
        container.setLayout(gl_container);

        createToolbar(container);

        Composite bodyComposite = (Composite) super.createDialogArea(container);
        bodyComposite.setLayout(new FillLayout(SWT.VERTICAL));

        SashForm hSashForm = new SashForm(bodyComposite, SWT.NONE);
        hSashForm.setSashWidth(5);

        Composite objectComposite = new Composite(hSashForm, SWT.NONE);
        GridLayout gl_objectComposite = new GridLayout();
        gl_objectComposite.marginLeft = 5;
        gl_objectComposite.marginWidth = 0;
        gl_objectComposite.marginBottom = 0;
        gl_objectComposite.marginHeight = 0;
        gl_objectComposite.verticalSpacing = 0;
        gl_objectComposite.horizontalSpacing = 0;
        objectComposite.setLayout(gl_objectComposite);

        createLeftPanel(objectComposite);

        Composite htmlDomComposite = new Composite(hSashForm, SWT.NONE);
        GridLayout gl_htmlDomComposite = new GridLayout();
        gl_htmlDomComposite.marginBottom = 5;
        gl_htmlDomComposite.marginRight = 5;
        gl_htmlDomComposite.marginWidth = 0;
        gl_htmlDomComposite.marginHeight = 0;
        gl_htmlDomComposite.horizontalSpacing = 0;
        htmlDomComposite.setLayout(gl_htmlDomComposite);

        createRightPanel(htmlDomComposite);

        hSashForm.setWeights(new int[] { 3, 8 });

        return container;
    }

    private void createLeftPanel(Composite parent) {
        capturedObjectComposite = new CapturedHTMLElementsComposite(parent, SWT.NONE);

        TreeViewer elementTreeViewer = capturedObjectComposite.getElementTreeViewer();
        elementTreeViewer.setInput(elements);
        elementTreeViewer.expandAll();

        addContextMenuForElementTree();

        capturedObjectComposite.getElementNameText().addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (capturedObjectComposite.getSelectedElement() != null) {
                    actionTableViewer.refresh();
                }
            }
        });
    }

    protected void addContextMenuForElementTree() {
        final TreeViewer treeViewer = capturedObjectComposite.getElementTreeViewer();
        treeViewer.getTree().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent event) {
                if (event.button == 3) {
                    Menu menu = treeViewer.getTree().getMenu();
                    if (menu != null) {
                        menu.dispose();
                    }
                    final TreeItem treeItem = treeViewer.getTree().getItem(new Point(event.x, event.y));
                    if (treeItem == null) {
                        return;
                    } else {
                        menu = new Menu(treeViewer.getTree());
                    }

                    createDeleteMenu(menu);
                    createAddValidationPointContextMenu(menu);
                    createAddSynchronizePointContextMenu(menu);

                    treeViewer.getTree().setMenu(menu);
                }

            }

            private void createDeleteMenu(Menu menu) {
                MenuItem deleteMenuItem = new MenuItem(menu, SWT.PUSH);
                deleteMenuItem.setText(StringConstants.DELETE);
                deleteMenuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        if (treeViewer.getSelection() instanceof ITreeSelection) {
                            ITreeSelection selection = (ITreeSelection) treeViewer.getSelection();
                            for (TreePath treePath : selection.getPaths()) {
                                if (treePath.getLastSegment() instanceof HTMLElement) {
                                    HTMLElement element = (HTMLElement) treePath.getLastSegment();
                                    HTMLFrameElement frameElement = element.getParentElement();
                                    if (frameElement != null) {
                                        frameElement.getChildElements().remove(element);
                                        capturedObjectComposite.refreshElementTree(frameElement);
                                    } else if (element instanceof HTMLPageElement) {
                                        elements.remove(element);
                                        capturedObjectComposite.refreshElementTree(null);
                                    }
                                    removeDeletedElementsFromAction(element);
                                    if (capturedObjectComposite.getSelectedElement().equals(element)) {
                                        capturedObjectComposite.refreshAttributesTable(null);
                                    }
                                }
                            }
                        }
                    }
                });
            }

            private void createAddValidationPointContextMenu(Menu menu) {
                MenuItem addValidationPointMenuItem = new MenuItem(menu, SWT.PUSH);
                addValidationPointMenuItem.setText(StringConstants.DIA_MENU_ADD_VALIDATION_POINT);
                addValidationPointMenuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        if (treeViewer.getSelection() instanceof ITreeSelection) {
                            ITreeSelection selection = (ITreeSelection) treeViewer.getSelection();
                            if (selection.getFirstElement() instanceof HTMLElement
                                    && !(selection.getFirstElement() instanceof HTMLPageElement)) {
                                addValidationPoint((HTMLElement) selection.getFirstElement());
                            }
                        }
                    }
                });
            }

            private void createAddSynchronizePointContextMenu(Menu menu) {
                MenuItem addValidationPointMenuItem = new MenuItem(menu, SWT.PUSH);
                addValidationPointMenuItem.setText(StringConstants.DIA_MENU_ADD_SYNCHRONIZE_POINT);
                addValidationPointMenuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        if (treeViewer.getSelection() instanceof ITreeSelection) {
                            ITreeSelection selection = (ITreeSelection) treeViewer.getSelection();
                            if (selection.getFirstElement() instanceof HTMLElement
                                    && !(selection.getFirstElement() instanceof HTMLPageElement)) {
                                addSynchonizationPoint((HTMLElement) selection.getFirstElement());
                            }
                        }
                    }
                });
            }
        });
    }

    private void createDeleteItem(Item deleteMenuItem) {
        deleteMenuItem.setText(StringConstants.DELETE);
        deleteMenuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (actionTableViewer.getSelection() instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) actionTableViewer.getSelection();
                    for (Object selectedObject : selection.toArray()) {
                        if (selectedObject instanceof HTMLActionMapping) {
                            HTMLActionMapping selectedActionMapping = (HTMLActionMapping) selectedObject;
                            recordedActions.remove(selectedActionMapping);
                        }
                    }
                    actionTableViewer.refresh();
                }
            }
        });
    }

    private void createAddActionItem(Item addActionMenuItem) {
        addActionMenuItem.setText(StringConstants.DIA_MENU_ADD_BASIC_ACTION);
        addActionMenuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                addDefaultAction(getFirstSelectedHTMLAction());
            }
        });
    }

    private void createAddValidationPointItem(Item addValidationPointMenuItem) {
        addValidationPointMenuItem.setText(StringConstants.DIA_MENU_ADD_VALIDATION_POINT);
        addValidationPointMenuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                HTMLActionMapping firstSelectedHTMLAction = getFirstSelectedHTMLAction();
                addValidationPoint(firstSelectedHTMLAction != null ? firstSelectedHTMLAction.getTargetElement() : null,
                        firstSelectedHTMLAction);
            }
        });
    }

    private void createAddSynchronizationItem(Item addSynchronizationPointMenuItem) {
        addSynchronizationPointMenuItem.setText(StringConstants.DIA_MENU_ADD_SYNCHRONIZE_POINT);
        addSynchronizationPointMenuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                HTMLActionMapping firstSelectedHTMLAction = getFirstSelectedHTMLAction();
                addSynchonizationPoint(
                        firstSelectedHTMLAction != null ? firstSelectedHTMLAction.getTargetElement() : null,
                        firstSelectedHTMLAction);
            }
        });
    }

    private HTMLActionMapping getFirstSelectedHTMLAction() {
        ISelection selection = actionTableViewer.getSelection();
        if (!(selection instanceof IStructuredSelection)) {
            return null;
        }

        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        Object firstElement = structuredSelection.getFirstElement();
        if (firstElement instanceof HTMLActionMapping) {
            return (HTMLActionMapping) firstElement;
        }
        return null;
    }

    protected void addContextMenuForActionTable() {
        actionTableViewer.getTable().addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                Menu menu = actionTableViewer.getTable().getMenu();
                if (menu != null) {
                    menu.dispose();
                }

                Point point = Display.getCurrent().map(null, actionTableViewer.getTable(), event.x, event.y);
                if (actionTableViewer.getTable().getItem(point) == null) {
                    return;
                }

                menu = new Menu(actionTableViewer.getTable());
                createDeleteActionContextMenu(menu);
                createAddValidationPointContextMenu(menu);
                createAddSynchronizePointContextMenu(menu);
                actionTableViewer.getTable().setMenu(menu);
            }

            private void createDeleteActionContextMenu(Menu menu) {
                MenuItem deleteMenuItem = new MenuItem(menu, SWT.PUSH);
                createDeleteItem(deleteMenuItem);
            }

            private void createAddValidationPointContextMenu(Menu menu) {
                MenuItem addValidationPointMenuItem = new MenuItem(menu, SWT.PUSH);
                createAddValidationPointItem(addValidationPointMenuItem);
            }

            private void createAddSynchronizePointContextMenu(Menu menu) {
                MenuItem addSynchronizationPointMenuItem = new MenuItem(menu, SWT.PUSH);
                createAddSynchronizationItem(addSynchronizationPointMenuItem);
            }
        });
    }

    private void hookDragEvent() {
        int operations = DND.DROP_MOVE;

        DragSource dragSource = new DragSource(actionTableViewer.getTable(), operations);
        dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });

        dragSource.addDragListener(new DragSourceListener() {
            public void dragStart(DragSourceEvent event) {
                if (actionTableViewer.getSelection() instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) actionTableViewer.getSelection();
                    if (selection.size() == 1 && selection.getFirstElement() instanceof HTMLActionMapping) {
                        event.doit = true;
                        return;
                    }
                }
                event.doit = false;
            }

            public void dragSetData(DragSourceEvent event) {
                IStructuredSelection selection = (IStructuredSelection) actionTableViewer.getSelection();
                HTMLActionMapping actionMapping = (HTMLActionMapping) selection.getFirstElement();
                int actionMappingIndex = recordedActions.indexOf(actionMapping);
                if (actionMappingIndex >= 0 && actionMappingIndex < recordedActions.size()) {
                    event.data = String.valueOf(actionMappingIndex);
                }
            }

            public void dragFinished(DragSourceEvent event) {
                // do nothing
            }

        });
    }

    private void hookDropEvent() {
        DropTarget dt = new DropTarget(actionTableViewer.getTable(), DND.DROP_MOVE);
        dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });
        dt.addDropListener(new TableDropTargetEffect(actionTableViewer.getTable()) {
            @Override
            public void drop(DropTargetEvent event) {
                if (event.data instanceof String) {
                    try {
                        int actionMappingIndex = Integer.valueOf((String) event.data);
                        if (actionMappingIndex < 0 && actionMappingIndex > recordedActions.size()) {
                            return;
                        }
                        HTMLActionMapping sourceActionMapping = recordedActions.get(actionMappingIndex);
                        HTMLActionMapping destinationActionMapping = getHoveredActionMapping(event);
                        if (destinationActionMapping == null && recordedActions.indexOf(destinationActionMapping) < 0) {
                            return;
                        }
                        recordedActions.remove(actionMappingIndex);
                        int destinationIndex = recordedActions.indexOf(destinationActionMapping);
                        if (getFeedBackByLocation(event) != DND.FEEDBACK_INSERT_BEFORE) {
                            destinationIndex++;
                        }
                        recordedActions.add(destinationIndex, sourceActionMapping);
                        actionTableViewer.refresh();
                    } catch (NumberFormatException e) {
                        LoggerSingleton.logError(e);
                    }
                }
            }

            @Override
            public void dragOver(DropTargetEvent event) {
                event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
                event.feedback |= getFeedBackByLocation(event);
            }

            private int getFeedBackByLocation(DropTargetEvent event) {
                Point point = Display.getCurrent().map(null, actionTableViewer.getTable(), event.x, event.y);
                TableItem tableItem = actionTableViewer.getTable().getItem(point);
                if (tableItem != null) {
                    Rectangle bounds = tableItem.getBounds();
                    if (point.y < bounds.y + bounds.height / 3) {
                        return DND.FEEDBACK_INSERT_BEFORE;
                    } else if (point.y > bounds.y + 2 * bounds.height / 3) {
                        return DND.FEEDBACK_INSERT_AFTER;
                    }
                }
                return DND.FEEDBACK_SELECT;
            }

            private HTMLActionMapping getHoveredActionMapping(DropTargetEvent event) {
                Point point = Display.getCurrent().map(null, actionTableViewer.getTable(), event.x, event.y);
                TableItem treeItem = actionTableViewer.getTable().getItem(point);
                return (treeItem != null && treeItem.getData() instanceof HTMLActionMapping)
                        ? (HTMLActionMapping) treeItem.getData() : null;
            }
        });
    }

    private void addValidationPoint(HTMLElement element, HTMLActionMapping selectedHTMLActionMapping) {
        String windowId = (selectedHTMLActionMapping != null) ? selectedHTMLActionMapping.getWindowId() : null;
        int addIndex = (selectedHTMLActionMapping != null) ? recordedActions.indexOf(selectedHTMLActionMapping) + 1
                : recordedActions.size();
        addAction(HTMLActionUtil.getDefaultValidationAction(), element, windowId, addIndex);
    }

    private void addValidationPoint(HTMLElement element) {
        addValidationPoint(element,
                recordedActions.size() > 0 ? recordedActions.get(recordedActions.size() - 1) : null);
    }

    private void addSynchonizationPoint(HTMLElement element, HTMLActionMapping selectedHTMLActionMapping) {
        String windowId = (selectedHTMLActionMapping != null) ? selectedHTMLActionMapping.getWindowId() : null;
        int addIndex = (selectedHTMLActionMapping != null) ? recordedActions.indexOf(selectedHTMLActionMapping) + 1
                : recordedActions.size();
        addAction(HTMLActionUtil.getDefaultSynchronizeAction(), element, windowId, addIndex);
    }

    private void addSynchonizationPoint(HTMLElement element) {
        addSynchonizationPoint(element,
                recordedActions.size() > 0 ? recordedActions.get(recordedActions.size() - 1) : null);
    }

    private void addDefaultAction(HTMLActionMapping selectedHTMLActionMapping) {
        IHTMLAction defaultHTMLAction = HTMLActionUtil.getAllHTMLActions().get(0);
        addAction(defaultHTMLAction,
                selectedHTMLActionMapping != null ? selectedHTMLActionMapping.getTargetElement() : null,
                selectedHTMLActionMapping != null ? selectedHTMLActionMapping.getWindowId() : null,
                selectedHTMLActionMapping != null ? recordedActions.indexOf(selectedHTMLActionMapping) + 1
                        : Math.max(0, recordedActions.size() - 1));
    }

    private void addAction(IHTMLAction newAction, HTMLElement element, String windowId, int selectedActionIndex) {
        if (newAction == null) {
            return;
        }
        HTMLActionMapping newActionMapping = new HTMLActionMapping(newAction,
                (newAction.hasElement()) ? element : null);
        newActionMapping.setWindowId(windowId);
        if (selectedActionIndex >= 0 && selectedActionIndex < recordedActions.size()) {
            recordedActions.add(selectedActionIndex, newActionMapping);
        } else {
            recordedActions.add(newActionMapping);
        }
        actionTableViewer.refresh();
        actionTableViewer.setSelection(new StructuredSelection(newActionMapping));
        actionTableViewer.getTable().setFocus();
    }

    private void removeDeletedElementsFromAction(HTMLElement element) {
        for (HTMLActionMapping actionMapping : recordedActions) {
            if (element.equals(actionMapping.getTargetElement()) || (element instanceof HTMLFrameElement
                    && ((HTMLFrameElement) element).contains(actionMapping.getTargetElement()))) {
                actionMapping.setTargetElement(null);
            }
        }
        actionTableViewer.refresh();
    }

    private void createActionToolbar(Composite parent) {
        ToolBar actionToolBar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        actionToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        ToolItem tltmAdd = new ToolItem(actionToolBar, SWT.DROP_DOWN);
        tltmAdd.setImage(ImageConstants.IMG_16_ADD);
        tltmAdd.setText(StringConstants.ADD);
        tltmAdd.addSelectionListener(new DropdownToolItemSelectionListener() {
            @Override
            protected Menu getMenu() {
                Menu addMenu = new Menu(getShell());

                MenuItem addActionItem = new MenuItem(addMenu, SWT.PUSH);
                createAddActionItem(addActionItem);

                MenuItem addValidationPointItem = new MenuItem(addMenu, SWT.PUSH);
                createAddValidationPointItem(addValidationPointItem);

                MenuItem addSynchronizationPointItem = new MenuItem(addMenu, SWT.PUSH);
                createAddSynchronizationItem(addSynchronizationPointItem);
                return addMenu;
            }
        });

        tltmDelete = new ToolItem(actionToolBar, SWT.PUSH);
        tltmDelete.setImage(ImageConstants.IMG_16_DELETE);
        tltmDelete.setEnabled(false);
        createDeleteItem(tltmDelete);
    }

    private boolean isAnyTableItemSelected() {
        if (actionTableViewer == null) {
            return false;
        }

        ISelection selection = actionTableViewer.getSelection();
        return selection != null && !selection.isEmpty();
    }

    private void createRightPanel(Composite parent) {
        Label lblRecordedActions = new Label(parent, SWT.NONE);
        lblRecordedActions.setFont(getFontBold(lblRecordedActions));
        lblRecordedActions.setText(StringConstants.DIA_LBL_RECORED_ACTIONS);

        createActionToolbar(parent);

        Composite tableComposite = new Composite(parent, SWT.None);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        actionTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        actionTableViewer.getTable().setHeaderVisible(true);
        actionTableViewer.getTable().setLinesVisible(true);
        ColumnViewerToolTipSupport.enableFor(actionTableViewer);

        ColumnViewerUtil.setTableActivation(actionTableViewer);

        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(actionTableViewer, SWT.NONE);
        TableColumn tableViewerNo = tableViewerColumnNo.getColumn();
        tableViewerNo.setText(TABLE_COLUMN_NO_TITLE);

        TableViewerColumn tableViewerColumnAction = new TableViewerColumn(actionTableViewer, SWT.NONE);
        TableColumn tableColumnAction = tableViewerColumnAction.getColumn();
        tableColumnAction.setText(TABLE_COLUMN_ACTION_TITLE);

        TableViewerColumn tableViewerColumnActionData = new TableViewerColumn(actionTableViewer, SWT.NONE);
        TableColumn tableColumnActionData = tableViewerColumnActionData.getColumn();
        tableColumnActionData.setText(TABLE_COLUMN_ACTION_DATA_TITLE);

        TableViewerColumn tableViewerColumnElement = new TableViewerColumn(actionTableViewer, SWT.NONE);
        TableColumn tableColumnElement = tableViewerColumnElement.getColumn();
        tableColumnElement.setText(TABLE_COLUMN_ELEMENT_TITLE);

        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(tableViewerNo, new ColumnWeightData(0, 40));
        tableLayout.setColumnData(tableColumnAction, new ColumnWeightData(40, 150));
        tableLayout.setColumnData(tableColumnActionData, new ColumnWeightData(30, 100));
        tableLayout.setColumnData(tableColumnElement, new ColumnWeightData(30, 100));

        tableComposite.setLayout(tableLayout);

        actionTableViewer.setContentProvider(ArrayContentProvider.getInstance());

        tableViewerColumnNo.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof HTMLActionMapping) {
                    return String.valueOf(recordedActions.indexOf((HTMLActionMapping) element) + 1);
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnAction.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof HTMLActionMapping && ((HTMLActionMapping) element).getAction() != null) {
                    return TreeEntityUtil.getReadableKeywordName(((HTMLActionMapping) element).getAction().getName());
                }
                return StringUtils.EMPTY;
            }

            @Override
            public String getToolTipText(Object element) {
                if (element instanceof HTMLActionMapping && ((HTMLActionMapping) element).getAction() != null) {
                    return StringUtils.defaultIfEmpty(((HTMLActionMapping) element).getAction().getDescription(), null);
                }
                return StringUtils.defaultIfEmpty(super.getToolTipText(element), null);
            }

        });

        tableViewerColumnAction.setEditingSupport(new EditingSupport(actionTableViewer) {
            private List<String> actionNames = new ArrayList<String>();

            private List<IHTMLAction> htmlActions = new ArrayList<IHTMLAction>();

            @Override
            protected void setValue(Object element, Object value) {
                if (value instanceof Integer) {
                    HTMLActionMapping actionMapping = (HTMLActionMapping) element;
                    IHTMLAction newAction = htmlActions.get((int) value);
                    if (!actionMapping.getAction().getName().equals(newAction.getName())) {
                        actionMapping.setAction(newAction);
                        actionTableViewer.refresh(actionMapping);
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                HTMLActionMapping actionMapping = (HTMLActionMapping) element;
                for (int i = 0; i < htmlActions.size(); i++) {
                    if (actionMapping.getAction().getName().equals(htmlActions.get(i).getName())) {
                        return i;
                    }
                }
                return 0;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                actionNames.clear();
                htmlActions.clear();
                IHTMLAction action = ((HTMLActionMapping) element).getAction();
                if (action instanceof HTMLSynchronizeAction) {
                    htmlActions.addAll(HTMLActionUtil.getAllHTMLSynchronizeActions());
                } else if (action instanceof HTMLValidationAction) {
                    htmlActions.addAll(HTMLActionUtil.getAllHTMLValidationActions());
                } else {
                    htmlActions.addAll(HTMLActionUtil.getAllHTMLActions());
                }

                // remove duplicate keyword
                Map<String, IHTMLAction> keywords = new LinkedHashMap<>();
                for (int i = 0; i < htmlActions.size(); ++i) {
                    action = htmlActions.get(i);
                    if (!keywords.containsKey(action.getName())) {
                        keywords.put(action.getName(), htmlActions.get(i));
                    }
                }
                htmlActions.clear();
                htmlActions.addAll(keywords.values());

                for (IHTMLAction htmlAction : htmlActions) {
                    actionNames.add(TreeEntityUtil.getReadableKeywordName(htmlAction.getName()));
                }
                return new ComboBoxCellEditor((Composite) getViewer().getControl(),
                        actionNames.toArray(new String[actionNames.size()]));
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element instanceof HTMLActionMapping && ((HTMLActionMapping) element).getAction() != null);
            }
        });

        tableViewerColumnActionData.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof HTMLActionMapping && ((HTMLActionMapping) element).getAction() != null) {
                    HTMLActionMapping actionMapping = (HTMLActionMapping) element;
                    if (actionMapping.getAction() != null && actionMapping.getAction().hasInput()
                            && actionMapping.getData() != null) {
                        StringBuilder displayString = new StringBuilder("["); //$NON-NLS-1$
                        boolean isFirst = true;
                        for (HTMLActionParamValueType dataObject : actionMapping.getData()) {
                            if (!isFirst) {
                                displayString.append(", "); //$NON-NLS-1$
                            } else {
                                isFirst = false;
                            }
                            displayString.append(dataObject.getValueToDisplay());
                        }
                        displayString.append("]"); //$NON-NLS-1$
                        return displayString.toString();
                    }
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnActionData.setEditingSupport(new EditingSupport(actionTableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (value instanceof HTMLActionParamValueType[]) {
                    ((HTMLActionMapping) element).setData((HTMLActionParamValueType[]) value);
                    actionTableViewer.refresh(element);
                }
            }

            @Override
            protected Object getValue(Object element) {
                return ((HTMLActionMapping) element).getData();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                final HTMLActionMapping actionMapping = (HTMLActionMapping) element;
                return new AbstractDialogCellEditor(actionTableViewer.getTable(),
                        actionMapping.getData() instanceof Object[] ? Arrays.toString(actionMapping.getData()) : "") { //$NON-NLS-1$
                    @Override
                    protected Object openDialogBox(Control cellEditorWindow) {
                        HTMLActionDataBuilderDialog dialog = new HTMLActionDataBuilderDialog(getParentShell(),
                                actionMapping);
                        int returnCode = dialog.open();
                        if (returnCode == Window.OK) {
                            return dialog.getActionData();
                        }
                        return null;
                    }
                };
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof HTMLActionMapping && ((HTMLActionMapping) element).getAction() != null) {
                    HTMLActionMapping actionMapping = (HTMLActionMapping) element;
                    if (actionMapping.getAction() != null && actionMapping.getAction().hasInput()) {
                        return true;
                    }
                }
                return false;
            }
        });

        tableViewerColumnElement.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (!(element instanceof HTMLActionMapping) || ((HTMLActionMapping) element).getAction() == null
                        || !((HTMLActionMapping) element).getAction().hasElement()) {
                    return StringUtils.EMPTY;
                }
                HTMLActionMapping actionMapping = (HTMLActionMapping) element;
                if (actionMapping.getTargetElement() != null) {
                    return actionMapping.getTargetElement().getName();
                }
                return StringConstants.NULL;
            }
        });

        tableViewerColumnElement.setEditingSupport(new EditingSupport(actionTableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (value instanceof HTMLElement) {
                    HTMLActionMapping actionMapping = (HTMLActionMapping) element;
                    HTMLElement newElement = (HTMLElement) value;
                    if (!newElement.equals(actionMapping.getTargetElement())) {
                        actionMapping.setTargetElement(newElement);
                        actionTableViewer.refresh(actionMapping);
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                return ((HTMLActionMapping) element).getTargetElement();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                HTMLElement htmlElement = ((HTMLActionMapping) element).getTargetElement();
                return new AbstractDialogCellEditor(actionTableViewer.getTable(),
                        htmlElement != null ? htmlElement.getName() : StringConstants.NULL) {
                    @Override
                    protected Object openDialogBox(Control cellEditorWindow) {
                        ElementTreeSelectionDialog treeDialog = new ElementTreeSelectionDialog(getParentShell(),
                                new HTMLElementLabelProvider(), new HTMLElementTreeContentProvider());
                        treeDialog.setInput(elements);
                        treeDialog.setInitialSelection(getValue());
                        treeDialog.setAllowMultiple(false);
                        treeDialog.setTitle(StringConstants.DIA_TITLE_CAPTURED_OBJECTS);
                        treeDialog.setMessage(StringConstants.DIA_MESSAGE_SELECT_ELEMENT);
                        treeDialog.setValidator(new ISelectionStatusValidator() {

                            @Override
                            public IStatus validate(Object[] selection) {
                                if (selection.length == 1 && !(selection[0] instanceof HTMLPageElement)) {
                                    return new StatusInfo();
                                }
                                return new StatusInfo(IStatus.ERROR, StringConstants.DIA_ERROR_MESSAGE_SELECT_ELEMENT);
                            }
                        });
                        if (treeDialog.open() == Window.OK) {
                            return treeDialog.getFirstResult();
                        }
                        return null;
                    }
                };
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof HTMLActionMapping && ((HTMLActionMapping) element).getAction() != null) {
                    HTMLActionMapping actionMapping = (HTMLActionMapping) element;
                    if (actionMapping.getAction() != null && actionMapping.getAction().hasElement()) {
                        return true;
                    }
                }
                return false;
            }
        });
        actionTableViewer.setInput(recordedActions);
        addContextMenuForActionTable();
        hookDragEvent();
        hookDropEvent();

        actionTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                tltmDelete.setEnabled(isAnyTableItemSelected());
            }
        });
    }

    private void createToolbar(Composite parent) {
        toolBar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
        toolBar.setLayout(new FillLayout(SWT.HORIZONTAL));

        toolItemBrowserDropdown = new ToolItem(toolBar, SWT.DROP_DOWN);
        toolItemBrowserDropdown.setText(START_TOOL_ITEM_LABEL);
        toolItemBrowserDropdown.setImage(ImageConstants.IMG_24_RECORD);

        Menu browserMenu = new Menu(toolBar.getShell());

        addBrowserMenuItem(browserMenu, WebUIDriverType.FIREFOX_DRIVER);
        addBrowserMenuItem(browserMenu, WebUIDriverType.CHROME_DRIVER);
        if (Platform.getOS().equals(Platform.OS_WIN32)) {
            addBrowserMenuItem(browserMenu, WebUIDriverType.IE_DRIVER);
        }

        createInstantBrowserMenu(browserMenu);

        toolItemBrowserDropdown.addSelectionListener(new StartItemDropdownSelectionListener(browserMenu));

        tltmPause = new ToolItem(toolBar, SWT.PUSH);
        tltmPause.setText(PAUSE_TOOL_ITEM_LABEL);
        tltmPause.setImage(ImageConstants.IMG_24_PAUSE);
        tltmPause.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isPausing) {
                    resume();
                } else {
                    pause();
                }
            }

        });
        tltmPause.setEnabled(false);

        tltmStop = new ToolItem(toolBar, SWT.PUSH);
        tltmStop.setText(STOP_TOOL_ITEM_LABEL);
        tltmStop.setImage(ImageConstants.IMG_24_STOP);
        tltmStop.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                stop();
            }
        });
        tltmStop.setEnabled(false);
    }

    public void stop() {
        try {
            stopServer();
            stopRecordSession();
        } catch (Exception e) {
            logger.error(e);
            MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, e.getMessage());
        }
        resume();
        tltmPause.setEnabled(false);
        tltmStop.setEnabled(false);
    }

    private void createInstantBrowserMenu(Menu browserMenu) {
        final MenuItem instantBrowserMenuItem = new MenuItem(browserMenu, SWT.CASCADE);
        instantBrowserMenuItem.setText(StringConstants.MENU_ITEM_INSTANT_BROWSERS);

        final Menu instantBrowserMenu = new Menu(browserMenu);
        instantBrowserMenuItem.setMenu(instantBrowserMenu);

        addInstantBrowserMenuItem(instantBrowserMenu, WebUIDriverType.CHROME_DRIVER);
        addInstantBrowserMenuItem(instantBrowserMenu, WebUIDriverType.FIREFOX_DRIVER);
        if (Platform.getOS().equals(Platform.OS_WIN32)) {
            addInstantBrowserMenuItem(instantBrowserMenu, WebUIDriverType.IE_DRIVER);
        }
    }

    private MenuItem createBrowserMenuItem(Menu browserMenu, final WebUIDriverType webUIDriverType) {
        MenuItem menuItem = new MenuItem(browserMenu, SWT.NONE);
        menuItem.setText(webUIDriverType.toString());
        menuItem.setImage(getWebUIDriverImage(webUIDriverType));
        return menuItem;
    }

    private void addBrowserMenuItem(Menu browserMenu, final WebUIDriverType webUIDriverType) {
        MenuItem menuItem = createBrowserMenuItem(browserMenu, webUIDriverType);
        menuItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                changeBrowser(webUIDriverType);
                startBrowser();
            }
        });
    }

    private void addInstantBrowserMenuItem(Menu browserMenu, final WebUIDriverType webUIDriverType) {
        MenuItem menuItem = createBrowserMenuItem(browserMenu, webUIDriverType);
        menuItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                try {
                    if (webUIDriverType != WebUIDriverType.IE_DRIVER
                            && !RecordSessionUtil.isNotShowingInstantBrowserDialog() && !showInstantBrowserDialog()) {
                        return;
                    }
                    changeBrowser(webUIDriverType, true);
                    startBrowser(true);
                } catch (IOException | URISyntaxException e) {
                    LoggerSingleton.logError(e);
                }
            }

            protected void showMessageForStartingInstantIE() {
                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        MessageDialogWithToggle messageDialogWithToggle = MessageDialogWithToggle.openInformation(
                                Display.getCurrent().getActiveShell(), StringConstants.HAND_INSTANT_BROWSERS_DIA_TITLE,
                                StringConstants.DIALOG_RUNNING_INSTANT_IE_MESSAGE,
                                StringConstants.HAND_INSTANT_BROWSERS_DIA_TOOGLE_MESSAGE, false, null, null);
                        RecordSessionUtil.setNotShowingInstantBrowserDialog(messageDialogWithToggle.getToggleState());
                    }
                });
            }

            private boolean showInstantBrowserDialog() throws IOException, URISyntaxException {
                if (webUIDriverType == WebUIDriverType.IE_DRIVER) {
                    showMessageForStartingInstantIE();
                    return true;
                }
                MessageDialogWithToggle messageDialogWithToggle = new GoToAddonStoreMessageDialog(getParentShell(),
                        StringConstants.HAND_INSTANT_BROWSERS_DIA_TITLE, MessageFormat
                                .format(StringConstants.HAND_INSTANT_BROWSERS_DIA_MESSAGE, webUIDriverType.toString()),
                        StringConstants.HAND_INSTANT_BROWSERS_DIA_TOOGLE_MESSAGE) {
                    @Override
                    protected String getNoButtonLabel() {
                        return ComposerWebuiRecorderMessageConstants.LBL_DLG_CONTINUE_WITH_RECORDING;
                    }
                };
                int returnCode = messageDialogWithToggle.open();
                RecordSessionUtil.setNotShowingInstantBrowserDialog(messageDialogWithToggle.getToggleState());
                if (returnCode == IDialogConstants.NO_ID) {
                    return true;
                }
                if (returnCode != IDialogConstants.YES_ID) {
                    return false;
                }
                openBrowserToAddonUrl();
                return true;
            }

            private void openBrowserToAddonUrl() throws IOException, URISyntaxException {
                String url = getAddonUrl(webUIDriverType);
                if (url == null || !Desktop.isDesktopSupported()) {
                    return;
                }
                Desktop.getDesktop().browse(new URI(url));
            }

            private String getAddonUrl(final WebUIDriverType webUIDriverType) {
                if (webUIDriverType == WebUIDriverType.CHROME_DRIVER) {
                    return RECORDER_CHROME_ADDON_URL;
                }
                if (webUIDriverType == WebUIDriverType.FIREFOX_DRIVER) {
                    return RECORDER_FIREFOX_ADDON_URL;
                }
                return null;
            }

        });
    }

    private void changeBrowser(final WebUIDriverType browserType) {
        changeBrowser(browserType, false);
    }

    private void changeBrowser(final WebUIDriverType browserType, final boolean isInstant) {
        if (browserType == null) {
            return;
        }
        selectedBrowser = browserType;
        UISynchronizeService.getInstance().getSync().asyncExec(new Runnable() {
            @Override
            public void run() {
                // Set browser name into toolbar item label
                String label = browserType.toString();
                if (isInstant) {
                    label = StringConstants.INSTANT_BROWSER_PREFIX;
                }
                toolItemBrowserDropdown.setText(label);
                // reload layout
                toolItemBrowserDropdown.getParent().getParent().layout(true, true);
            }
        });
    }

    private Font getFontBold(Label label) {
        FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
        return boldDescriptor.createFont(label.getDisplay());
    }

    private Image getWebUIDriverImage(WebUIDriverType webUIDriverType) {
        if (webUIDriverType == WebUIDriverType.FIREFOX_DRIVER) {
            return ImageConstants.IMG_16_FIREFOX;
        } else if (webUIDriverType == WebUIDriverType.CHROME_DRIVER) {
            return ImageConstants.IMG_16_CHROME;
        } else if (webUIDriverType == WebUIDriverType.IE_DRIVER) {
            return ImageConstants.IMG_16_IE;
        }
        return null;
    }

    public void dispose() {
        if (server != null && server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        stopRecordSession();
    }

    private void stopRecordSession() {
        if (session != null && session.isRunning()) {
            session.stop();
        }
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
    }

    @Override
    protected void okPressed() {
        AddToObjectRepositoryDialog addToObjectRepositoryDialog = new AddToObjectRepositoryDialog(getParentShell(),
                true, elements, capturedObjectComposite.getElementTreeViewer().getExpandedElements());
        if (addToObjectRepositoryDialog.open() == Window.OK) {
            targetFolderSelectionResult = addToObjectRepositoryDialog.getDialogResult();
            super.okPressed();
            dispose();
        }
    }

    @Override
    protected void cancelPressed() {
        super.cancelPressed();
        dispose();
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(800, 600);
    }

    @Override
    protected void handleShellCloseEvent() {
        super.handleShellCloseEvent();
        dispose();
    }

    public void addNewActionMapping(final HTMLActionMapping newAction) {
        if (isPausing || !HTMLActionUtil.verifyActionMapping(newAction, recordedActions)) {
            return;
        }
        if (newAction.getTargetElement() != null) {
            addNewElement(newAction.getTargetElement(), newAction);
        }
        recordedActions.add(newAction);
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                actionTableViewer.refresh();
                actionTableViewer.reveal(newAction);
                capturedObjectComposite.refreshElementTree(null);
                capturedObjectComposite.getElementTreeViewer().reveal(newAction.getTargetElement());
            }
        });
    }

    private void addNewElement(HTMLElement newElement, HTMLActionMapping newAction) {
        HTMLPageElement parentPageElement = newElement.getParentPageElement();
        if (parentPageElement != null) {
            if (elements.contains(parentPageElement)) {
                addNewElement(elements.get(elements.indexOf(parentPageElement)),
                        parentPageElement.getChildElements().get(0), parentPageElement, newAction);
            } else {
                elements.add(parentPageElement);
            }
        }
    }

    private void addNewElement(HTMLFrameElement parentElement, HTMLElement newElement, HTMLPageElement pageElement,
            HTMLActionMapping newAction) {
        if (parentElement.getChildElements().contains(newElement)) {
            if (newElement instanceof HTMLFrameElement) {
                HTMLFrameElement frameElement = (HTMLFrameElement) newElement;
                HTMLFrameElement existingFrameElement = (HTMLFrameElement) (parentElement.getChildElements()
                        .get(parentElement.getChildElements().indexOf(newElement)));
                addNewElement(existingFrameElement, frameElement.getChildElements().get(0), pageElement, newAction);
            } else {
                for (HTMLElement element : parentElement.getChildElements()) {
                    if (element.equals(newElement)) {
                        newAction.setTargetElement(element);
                        break;
                    }
                }
            }
        } else {
            parentElement.getChildElements().add(newElement);
            newElement.setParentElement(parentElement);
            return;
        }
    }

    public List<HTMLActionMapping> getActions() {
        return recordedActions;
    }

    public List<HTMLPageElement> getElements() {
        return elements;
    }

    public AddToObjectRepositoryDialogResult getTargetFolderTreeEntity() {
        return targetFolderSelectionResult;
    }

}
