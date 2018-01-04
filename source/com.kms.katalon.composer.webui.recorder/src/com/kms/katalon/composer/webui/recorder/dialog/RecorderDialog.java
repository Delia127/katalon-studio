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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.osgi.framework.Bundle;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.controls.HelpCompositeForDialog;
import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.components.impl.control.Dropdown;
import com.kms.katalon.composer.components.impl.control.DropdownGroup;
import com.kms.katalon.composer.components.impl.control.DropdownItemSelectionListener;
import com.kms.katalon.composer.components.impl.control.DropdownToolItemSelectionListener;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
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
import com.kms.katalon.composer.webui.recorder.websocket.RecorderAddonSocket;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.dialog.CapturedObjectsView;
import com.kms.katalon.objectspy.dialog.GoToAddonStoreMessageDialog;
import com.kms.katalon.objectspy.dialog.ObjectPropertiesView;
import com.kms.katalon.objectspy.dialog.ObjectSpyEvent;
import com.kms.katalon.objectspy.dialog.ObjectSpySelectorEditor;
import com.kms.katalon.objectspy.dialog.ObjectSpyUrlView;
import com.kms.katalon.objectspy.dialog.ObjectVerifyAndHighlightView;
import com.kms.katalon.objectspy.dialog.SaveToObjectRepositoryDialog;
import com.kms.katalon.objectspy.dialog.SaveToObjectRepositoryDialog.SaveToObjectRepositoryDialogResult;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebFrame;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.element.tree.WebElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.WebElementTreeContentProvider;
import com.kms.katalon.objectspy.exception.IEAddonNotInstalledException;
import com.kms.katalon.objectspy.util.BrowserUtil;
import com.kms.katalon.objectspy.util.UtilitiesAddonUtil;
import com.kms.katalon.objectspy.util.Win32Helper;
import com.kms.katalon.objectspy.util.WinRegistry;
import com.kms.katalon.objectspy.websocket.AddonCommand;
import com.kms.katalon.objectspy.websocket.AddonSocket;
import com.kms.katalon.objectspy.websocket.AddonSocketServer;
import com.kms.katalon.objectspy.websocket.messages.AddonMessage;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.util.listener.EventListener;
import com.kms.katalon.util.listener.EventManager;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

@SuppressWarnings("restriction")
public class RecorderDialog extends AbstractDialog implements EventHandler, EventManager<ObjectSpyEvent> {
    private static final String IE_WINDOW_CLASS = "IEFrame"; //$NON-NLS-1$

    private static final String relativePathToIEAddonSetup = File.separator + "extensions" + File.separator + "IE" //$NON-NLS-1$ //$NON-NLS-2$
            + File.separator + RecordSession.RECORDER_ADDON_NAME + File.separator + "setup.exe"; //$NON-NLS-1$

    private static final String RESOURCES_FOLDER_NAME = "resources"; //$NON-NLS-1$

    private static final String IE_ADDON_BHO_KEY = "{FEA8CA38-7979-4F6A-83E4-2949EDEA96EF}"; //$NON-NLS-1$

    public static final String DIA_INSTANT_BROWSER_CHROME_RECORDER_EXTENSION_PATH = "<Katalon build path>/Resources/extensions/Chrome/Recorder Packed"; //$NON-NLS-1$

    public static final String RECORDER_FIREFOX_ADDON_URL = "https://addons.mozilla.org/en-US/firefox/addon/katalon-recorder/"; //$NON-NLS-1$

    private static final int ANY_PORT_NUMBER = 0;

    private static final String TABLE_COLUMN_ELEMENT_TITLE = StringConstants.DIA_COL_ELEMENT;

    private static final String TABLE_COLUMN_ACTION_DATA_TITLE = StringConstants.DIA_COL_ACTION_DATA;

    private static final String TABLE_COLUMN_ACTION_TITLE = StringConstants.DIA_COL_ACTION;

    private static final String TABLE_COLUMN_NO_TITLE = StringConstants.DIA_COL_NO;

    private static final String RESUME_TOOL_ITEM_LABEL = StringConstants.DIA_TOOLITEM_RESUME;

    private static final String RECORD_TOOL_ITEM_LABEL = StringConstants.DIA_TOOLITEM_RECORD;

    private static Point MIN_DIALOG_SIZE = new Point(665, 630);

    private HTMLElementRecorderServer server;

    private Logger logger;

    private List<WebPage> elements;

    private List<HTMLActionMapping> recordedActions;

    private boolean isPausing;

    private TableViewer actionTableViewer;

    private ToolBar toolBar;

    private ToolItem toolItemBrowserDropdown, tltmPause, tltmStop;

    private RecordSession session;

    private SaveToObjectRepositoryDialogResult targetFolderSelectionResult;

    private CapturedObjectsView capturedObjectComposite;

    private WebUIDriverType selectedBrowser;

    private ToolItem tltmDelete;

    private Text txtStartUrl;

    private AddonSocket currentInstantSocket;

    private IEventBroker eventBroker;

    private ScopedPreferenceStore store;

    private SashForm hSashForm;

    private boolean disposed;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public RecorderDialog(Shell parentShell, Logger logger, IEventBroker eventBroker) {
        super(parentShell);
        store = PreferenceStoreManager.getPreferenceStore(RecorderPreferenceConstants.WEBUI_RECORDER_QUALIFIER);
        setDialogTitle(GlobalMessageConstants.WEB_RECORDER);
        this.logger = logger;
        elements = new ArrayList<>();
        recordedActions = new ArrayList<HTMLActionMapping>();
        isPausing = false;
        disposed = false;
        this.eventBroker = eventBroker;
        eventBroker.subscribe(EventConstants.RECORDER_HTML_ACTION_CAPTURED, this);
        eventBroker.subscribe(EventConstants.RECORDER_ACTION_OBJECT_REORDERED, this);
        eventBroker.subscribe(EventConstants.WORKSPACE_CLOSED, this);
        startSocketServer();
    }
    
    @Override
    protected int getShellStyle() {
        boolean onTop = store.getBoolean(RecorderPreferenceConstants.WEBUI_RECORDER_PIN_WINDOW);
        if (onTop) {
            return SWT.SHELL_TRIM | SWT.ON_TOP | SWT.CENTER;
        } else {
            return SWT.SHELL_TRIM | SWT.CENTER;
        }
    }

    private void startSocketServer() {
        try {
            new ProgressMonitorDialog(getParentShell()).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(ComposerWebuiRecorderMessageConstants.MSG_DLG_INIT_RECORDER, 1);
                    AddonSocketServer.getInstance().start(RecorderAddonSocket.class,
                            UtilitiesAddonUtil.getInstantBrowsersPort());
                }
            });
        } catch (InvocationTargetException e) {
            LoggerSingleton.logError(e.getTargetException());
        } catch (InterruptedException e) {
            // Ignore this
        }
    }

    private void startBrowser() {
        startBrowser(false);
    }

    private void startBrowser(boolean isInstant) {
        if (!BrowserUtil.isBrowserInstalled(selectedBrowser)) {
            MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE,
                    ComposerWebuiRecorderMessageConstants.DIA_MSG_CANNOT_START_BROWSER);
            return;
        }
        try {
            if (selectedBrowser == WebUIDriverType.IE_DRIVER) {
                checkIEAddon();
            }
            if (isInstant) {
                startInstantSession();
                invoke(ObjectSpyEvent.ADDON_SESSION_STARTED, currentInstantSocket);
            } else {
                startServer();
                startRecordSession(selectedBrowser);
                invoke(ObjectSpyEvent.SELENIUM_SESSION_STARTED, session);
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

    private void startInstantSession() throws Exception {
        if (selectedBrowser == WebUIDriverType.IE_DRIVER) {
            runInstantIE();
        }
        currentInstantSocket = AddonSocketServer.getInstance().getAddonSocketByBrowserName(selectedBrowser.toString());
        if (currentInstantSocket == null) {
            return;
        }
        Win32Helper.switchFocusToBrowser(selectedBrowser);
        currentInstantSocket.sendMessage(new AddonMessage(AddonCommand.START_RECORD));
    }

    private void closeInstantSession() {
        if (currentInstantSocket != null && currentInstantSocket.isConnected()) {
            currentInstantSocket.close();
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
        if (checkRegistryKey(ObjectSpyUrlView.IE_WINDOWS_32BIT_BHO_REGISTRY_KEY)
                || checkRegistryKey(ObjectSpyUrlView.IE_WINDOWS_BHO_REGISTRY_KEY)) {
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
                logger, txtStartUrl.getText());
        new Thread(session).start();
    }

    private WebUIDriverType getWebUIDriver() {
        return WebUIDriverType.fromStringValue(
                getPreferenceStore().getString(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_BROWSER));
    }

    private ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(RecorderPreferenceConstants.WEBUI_RECORDER_QUALIFIER);
    }

    private void startServer(int port) throws Exception {
        closeInstantSession();
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

    private void pause() {
        isPausing = true;
        tltmPause.setText(RESUME_TOOL_ITEM_LABEL);
        tltmPause.setImage(ImageConstants.IMG_24_PLAY);
        toolBar.getParent().layout();
    }

    private void resume() {
        isPausing = false;
        tltmPause.setImage(ImageConstants.IMG_24_PAUSE);
        toolBar.getParent().layout();
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout glMain = new GridLayout();
        glMain.marginHeight = 0;
        glMain.marginWidth = 0;
        container.setLayout(glMain);

        createToolbar(container);

        Composite bodyComposite = new Composite(container, SWT.NONE);
        bodyComposite.setLayout(new FillLayout(SWT.VERTICAL));
        bodyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        hSashForm = new SashForm(bodyComposite, SWT.NONE);
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

        hSashForm.setWeights(new int[] { 0, 10 });

        txtStartUrl.setFocus();

        initializeInput();

        return container;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setMinimumSize(MIN_DIALOG_SIZE);
    }

    private void initializeInput() {
        txtStartUrl.setText(store.getString(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_URL));
        txtStartUrl.selectAll();
    }

    private void createLeftPanel(Composite parent) {
        capturedObjectComposite = new CapturedObjectsView(parent, SWT.NONE, eventBroker);
        Sash sash = new Sash(parent, SWT.HORIZONTAL);
        GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
        sash.setLayoutData(layoutData);
        sash.addSelectionListener(new SelectionAdapter() {
            private int CAPTURED_OBJECT_VIEW_MIN_SIZE = 100;

            @Override
            public void widgetSelected(SelectionEvent e) {
                GridLayout parentLayout = (GridLayout) capturedObjectComposite.getParent().getLayout();
                int newHeight = e.y - capturedObjectComposite.getBounds().y - parentLayout.verticalSpacing;
                if (newHeight < CAPTURED_OBJECT_VIEW_MIN_SIZE) {
                    e.doit = false;
                    return;
                }
                GridData gridData = (GridData) capturedObjectComposite.getLayoutData();
                gridData.heightHint = newHeight;
                capturedObjectComposite.getParent().layout();
            }
        });
        ObjectPropertiesView objectPropertiesView = new ObjectPropertiesView(parent, SWT.NONE);
        objectPropertiesView.setRefreshCapturedObjectsTree(new Runnable() {

            @Override
            public void run() {
                capturedObjectComposite.refreshTree(null);
            }
        });

        ObjectSpySelectorEditor selectorEditor = new ObjectSpySelectorEditor();
        selectorEditor.createObjectSelectorEditor(parent);

        ObjectVerifyAndHighlightView verifyView = new ObjectVerifyAndHighlightView();
        verifyView.createVerifyAndHighlightView(parent, GridData.FILL_HORIZONTAL);
        capturedObjectComposite.setInput(elements);

        capturedObjectComposite.addListener(objectPropertiesView,
                Arrays.asList(ObjectSpyEvent.SELECTED_ELEMENT_CHANGED));
        // capturedObjectComposite.addListener(this, Arrays.asList(ObjectSpyEvent.SELECTED_ELEMENT_CHANGED));

        selectorEditor.addListener(verifyView, Arrays.asList(ObjectSpyEvent.SELECTOR_HAS_CHANGED));
        objectPropertiesView.addListener(selectorEditor, Arrays.asList(ObjectSpyEvent.ELEMENT_PROPERTIES_CHANGED));
        objectPropertiesView.addListener(verifyView, Arrays.asList(ObjectSpyEvent.ELEMENT_PROPERTIES_CHANGED));

        this.addListener(verifyView,
                Arrays.asList(ObjectSpyEvent.ADDON_SESSION_STARTED, ObjectSpyEvent.SELENIUM_SESSION_STARTED));
        // objectPropertiesView.addListener(this, Arrays.asList(ObjectSpyEvent.REQUEST_DIALOG_RESIZE));
    }

    private void createDeleteItem(Item deleteMenuItem) {
        deleteMenuItem.setText(StringConstants.DELETE);
        deleteMenuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                deleteSelectedItems();
            }
        });
    }

    private void deleteSelectedItems() {
        if (!(actionTableViewer.getSelection() instanceof IStructuredSelection)) {
            return;
        }
        IStructuredSelection selection = (IStructuredSelection) actionTableViewer.getSelection();
        if (selection.isEmpty()) {
            return;
        }
        List<HTMLActionMapping> selectedActionMappings = Arrays.asList(selection.toArray())
                .stream()
                .filter(selectedObject -> selectedObject instanceof HTMLActionMapping)
                .map(selectedObject -> (HTMLActionMapping) selectedObject)
                .collect(Collectors.toList());
        HTMLActionMapping lastSelectedElement = selectedActionMappings.get(selectedActionMappings.size() - 1);
        int lastSelectedElementIndex = recordedActions.indexOf(lastSelectedElement);
        HTMLActionMapping nextFocusedElement = (lastSelectedElementIndex >= recordedActions.size() - 1) ? null
                : recordedActions.get(lastSelectedElementIndex + 1);
        recordedActions.removeAll(selectedActionMappings);
        actionTableViewer.refresh();
        if (recordedActions.isEmpty()) {
            return;
        }
        if (nextFocusedElement == null) {
            nextFocusedElement = recordedActions.get(recordedActions.size() - 1);
        }
        actionTableViewer.setSelection(new StructuredSelection(nextFocusedElement));
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
            @Override
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
            @Override
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

            @Override
            public void dragSetData(DragSourceEvent event) {
                IStructuredSelection selection = (IStructuredSelection) actionTableViewer.getSelection();
                HTMLActionMapping actionMapping = (HTMLActionMapping) selection.getFirstElement();
                int actionMappingIndex = recordedActions.indexOf(actionMapping);
                if (actionMappingIndex >= 0 && actionMappingIndex < recordedActions.size()) {
                    event.data = String.valueOf(actionMappingIndex);
                }
            }

            @Override
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

    private void addValidationPoint(WebElement element, HTMLActionMapping selectedHTMLActionMapping) {
        String windowId = (selectedHTMLActionMapping != null) ? selectedHTMLActionMapping.getWindowId() : null;
        int addIndex = (selectedHTMLActionMapping != null) ? recordedActions.indexOf(selectedHTMLActionMapping) + 1
                : recordedActions.size();
        addAction(HTMLActionUtil.getDefaultValidationAction(), element, windowId, addIndex);
    }

    // private void addValidationPoint(WebElement element) {
    // addValidationPoint(element,
    // recordedActions.size() > 0 ? recordedActions.get(recordedActions.size() - 1) : null);
    // }

    private void addSynchonizationPoint(WebElement element, HTMLActionMapping selectedHTMLActionMapping) {
        String windowId = (selectedHTMLActionMapping != null) ? selectedHTMLActionMapping.getWindowId() : null;
        int addIndex = (selectedHTMLActionMapping != null) ? recordedActions.indexOf(selectedHTMLActionMapping) + 1
                : recordedActions.size();
        addAction(HTMLActionUtil.getDefaultSynchronizeAction(), element, windowId, addIndex);
    }

    // private void addSynchonizationPoint(WebElement element) {
    // addSynchonizationPoint(element,
    // recordedActions.size() > 0 ? recordedActions.get(recordedActions.size() - 1) : null);
    // }

    private void addDefaultAction(HTMLActionMapping selectedHTMLActionMapping) {
        IHTMLAction defaultHTMLAction = HTMLActionUtil.getAllHTMLActions().get(0);
        addAction(defaultHTMLAction,
                selectedHTMLActionMapping != null ? selectedHTMLActionMapping.getTargetElement() : null,
                selectedHTMLActionMapping != null ? selectedHTMLActionMapping.getWindowId() : null,
                selectedHTMLActionMapping != null ? recordedActions.indexOf(selectedHTMLActionMapping) + 1
                        : Math.max(0, recordedActions.size() - 1));
    }

    private void addAction(IHTMLAction newAction, WebElement element, String windowId, int selectedActionIndex) {
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

    private void createActionToolbar(Composite parent) {
        Composite tbComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        tbComposite.setLayout(layout);
        tbComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        ToolBar actionToolBar = new ToolBar(tbComposite, SWT.FLAT | SWT.RIGHT);
        actionToolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

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
        tltmDelete.setDisabledImage(ImageConstants.IMG_16_DELETE_DISABLED);
        tltmDelete.setEnabled(false);
        createDeleteItem(tltmDelete);

        ToolBar rightToolBar = new ToolBar(tbComposite, SWT.FLAT | SWT.RIGHT);
        rightToolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

        ToolItem tltmCapturedObjects = new ToolItem(rightToolBar, SWT.CHECK);

        
        tltmCapturedObjects.setText(StringConstants.DIA_TITLE_SHOW + StringConstants.DIA_TITLE_CAPTURED_OBJECTS);
        tltmCapturedObjects.setToolTipText(StringConstants.DIA_TOOLTIP_SHOW_HIDE_CAPTURED_OBJECTS);
        tltmCapturedObjects.setSelection(false);
        tltmCapturedObjects.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] sashFormWeights = new int[] { 0, 10 };
                boolean isChecked = tltmCapturedObjects.getSelection();
                String showOrHide = StringConstants.DIA_TITLE_SHOW + StringConstants.DIA_TITLE_CAPTURED_OBJECTS;
                if (isChecked) {
                    sashFormWeights = new int[] { 5, 5 };
                    showOrHide = StringConstants.DIA_TITLE_HIDE + StringConstants.DIA_TITLE_CAPTURED_OBJECTS;
                }
                tltmCapturedObjects.setText(showOrHide);
                hSashForm.setWeights(sashFormWeights);
            }
        });
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
        tableLayout.setColumnData(tableColumnAction, new ColumnWeightData(20, 70));
        tableLayout.setColumnData(tableColumnActionData, new ColumnWeightData(30, 140));
        tableLayout.setColumnData(tableColumnElement, new ColumnWeightData(30, 100));

        tableComposite.setLayout(tableLayout);

        actionTableViewer.setContentProvider(ArrayContentProvider.getInstance());

        tableViewerColumnNo.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof HTMLActionMapping) {
                    return String.valueOf(recordedActions.indexOf(element) + 1);
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
                if (!(value instanceof Integer)) {
                    return;
                }

                HTMLActionMapping actionMapping = (HTMLActionMapping) element;
                int selectionIndex = (int) value;
                if (selectionIndex < 0 || selectionIndex >= htmlActions.size()) {
                    return;
                }
                IHTMLAction newAction = htmlActions.get(selectionIndex);
                if (!actionMapping.getAction().getName().equals(newAction.getName())) {
                    actionMapping.setAction(newAction);
                    actionTableViewer.refresh(actionMapping);
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
                        HTMLActionDataBuilderDialog dialog = new HTMLActionDataBuilderDialog(getShell(), actionMapping);
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
                if (value instanceof WebElement) {
                    HTMLActionMapping actionMapping = (HTMLActionMapping) element;
                    WebElement newElement = (WebElement) value;
                    if (!(newElement.equals(actionMapping.getTargetElement()))) {
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
                WebElement htmlElement = ((HTMLActionMapping) element).getTargetElement();
                return new AbstractDialogCellEditor(actionTableViewer.getTable(),
                        htmlElement != null ? htmlElement.getName() : StringConstants.NULL) {
                    @Override
                    protected Object openDialogBox(Control cellEditorWindow) {
                        ElementTreeSelectionDialog treeDialog = new ElementTreeSelectionDialog(getShell(),
                                new WebElementLabelProvider(), new WebElementTreeContentProvider());
                        treeDialog.setInput(elements);
                        treeDialog.setInitialSelection(getValue());
                        treeDialog.setAllowMultiple(false);
                        treeDialog.setTitle(StringConstants.DIA_TITLE_CAPTURED_OBJECTS);
                        treeDialog.setMessage(StringConstants.DIA_MESSAGE_SELECT_ELEMENT);

                        treeDialog.setValidator(new ISelectionStatusValidator() {

                            @Override
                            public IStatus validate(Object[] selection) {
                                if (selection.length == 1 && !(selection[0] instanceof WebPage)) {
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
                StructuredSelection selection = (StructuredSelection) event.getSelection();
                HTMLActionMapping actionMapping = (HTMLActionMapping) selection.getFirstElement();
                if (actionMapping != null) {
                    WebElement element = actionMapping.getTargetElement();
                    if (element != null) {
                        eventBroker.send(EventConstants.RECORDER_ACTION_SELECTED, element);
                    }
                }
            }
        });
    }

    private void createToolbar(Composite parent) {
        Composite toolbarComposite = new Composite(parent, SWT.NONE);
        toolbarComposite.setLayout(new GridLayout(3, false));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblStartUrl = new Label(toolbarComposite, SWT.NONE);
        lblStartUrl.setText(ObjectspyMessageConstants.LBL_DLG_START_URL);

        txtStartUrl = new Text(toolbarComposite, SWT.BORDER);
        GridData gdTxtStartUrl = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtStartUrl.heightHint = 20;
        gdTxtStartUrl.widthHint = 300;
        txtStartUrl.setLayoutData(gdTxtStartUrl);
        txtStartUrl.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.character == SWT.CR) {
                    startBrowser();
                }
            }
        });

        toolBar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        toolItemBrowserDropdown = new ToolItem(toolBar, SWT.DROP_DOWN);
        toolItemBrowserDropdown.setImage(getWebUIDriverToolItemImage(getWebUIDriver()));
        Dropdown dropdown = new Dropdown(getShell());
        createDropdownContent(dropdown);
        // set default browser
        selectedBrowser = getWebUIDriver();
        toolItemBrowserDropdown.addSelectionListener(new DropdownItemSelectionListener(dropdown) {

            @Override
            public void itemSelected(SelectionEvent event) {
                ToolItem item = (ToolItem) event.widget;
                if (item.getText().equals(StringConstants.ACTIVE_BROWSER_PREFIX)) {
                    startBrowser(true);
                    return;
                }
                startBrowser();
            }
        });

        tltmPause = new ToolItem(toolBar, SWT.PUSH);
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
        tltmStop.setImage(ImageConstants.IMG_24_STOP);
        tltmStop.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                stop();
            }
        });
        tltmStop.setEnabled(false);
    }

    private void createDropdownContent(Dropdown dropdown) {
        DropdownGroup newBrowser = dropdown.addDropdownGroupItem(StringConstants.MENU_ITEM_NEW_BROWSERS,
                ImageManager.getImage(IImageKeys.NEW_BROWSER_16));
        addNewBrowserItem(newBrowser, WebUIDriverType.FIREFOX_DRIVER);
        addNewBrowserItem(newBrowser, WebUIDriverType.CHROME_DRIVER);

        DropdownGroup activeBrowser = dropdown.addDropdownGroupItem(StringConstants.MENU_ITEM_ACTIVE_BROWSERS,
                ImageManager.getImage(IImageKeys.ACTIVE_BROWSER_16));
        addActiveBrowserItem(activeBrowser, WebUIDriverType.CHROME_DRIVER);

        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            addNewBrowserItem(newBrowser, WebUIDriverType.IE_DRIVER);
        }
    }

    private void addNewBrowserItem(DropdownGroup newBrowserGroup, WebUIDriverType webUIDriverType) {
        newBrowserGroup.addItem(webUIDriverType.toString(), getWebUIDriverDropdownImage(webUIDriverType),
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        changeBrowser(webUIDriverType);
                        startBrowser();
                    }
                });
    }

    private void addActiveBrowserItem(DropdownGroup activeBrowserGroup, WebUIDriverType webUIDriverType) {
        activeBrowserGroup.addItem(webUIDriverType.toString(), getWebUIDriverDropdownImage(webUIDriverType),
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        try {
                            if (webUIDriverType != WebUIDriverType.IE_DRIVER
                                    && !UtilitiesAddonUtil.isNotShowingInstantBrowserDialog()
                                    && !showInstantBrowserDialog()) {
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
                                MessageDialogWithToggle messageDialogWithToggle = MessageDialogWithToggle
                                        .openInformation(Display.getCurrent().getActiveShell(),
                                                StringConstants.HAND_ACTIVE_BROWSERS_DIA_TITLE,
                                                StringConstants.DIALOG_RUNNING_INSTANT_IE_MESSAGE,
                                                StringConstants.HAND_ACTIVE_BROWSERS_DIA_TOOGLE_MESSAGE, false, null,
                                                null);
                                UtilitiesAddonUtil
                                        .setNotShowingInstantBrowserDialog(messageDialogWithToggle.getToggleState());
                            }
                        });
                    }

                    private boolean showInstantBrowserDialog() throws IOException, URISyntaxException {
                        if (webUIDriverType == WebUIDriverType.IE_DRIVER) {
                            showMessageForStartingInstantIE();
                            return true;
                        }
                        MessageDialogWithToggle messageDialogWithToggle = new GoToAddonStoreMessageDialog(getShell(),
                                StringConstants.HAND_ACTIVE_BROWSERS_DIA_TITLE,
                                MessageFormat.format(StringConstants.HAND_ACTIVE_BROWSERS_DIA_MESSAGE,
                                        webUIDriverType.toString()),
                                StringConstants.HAND_ACTIVE_BROWSERS_DIA_TOOGLE_MESSAGE) {
                            @Override
                            protected String getNoButtonLabel() {
                                return ComposerWebuiRecorderMessageConstants.LBL_DLG_CONTINUE_WITH_RECORDING;
                            }
                        };
                        int returnCode = messageDialogWithToggle.open();
                        UtilitiesAddonUtil.setNotShowingInstantBrowserDialog(messageDialogWithToggle.getToggleState());
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
                            return ObjectSpyUrlView.OBJECT_SPY_CHROME_ADDON_URL;
                        }
                        if (webUIDriverType == WebUIDriverType.FIREFOX_DRIVER) {
                            return RECORDER_FIREFOX_ADDON_URL;
                        }
                        return null;
                    }

                });
    }

    public void stop() {
        try {
            stopServer();
            stopRecordSession();
            closeInstantSession();
        } catch (Exception e) {
            logger.error(e);
            MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, e.getMessage());
        }
        resume();
        tltmPause.setEnabled(false);
        tltmStop.setEnabled(false);
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
                String label = RECORD_TOOL_ITEM_LABEL;
                if (isInstant) {
                    label = StringConstants.ACTIVE_BROWSER_PREFIX;
                }
                toolItemBrowserDropdown.setText(label);
                toolItemBrowserDropdown.setImage(getWebUIDriverToolItemImage(browserType));
                // reload layout
                toolItemBrowserDropdown.getParent().getParent().layout(true, true);
            }
        });
    }

    private Font getFontBold(Label label) {
        FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
        return boldDescriptor.createFont(label.getDisplay());
    }

    private Image getWebUIDriverDropdownImage(WebUIDriverType webUIDriverType) {
        switch (webUIDriverType) {
            case FIREFOX_DRIVER:
                return ImageConstants.IMG_16_FIREFOX;

            case CHROME_DRIVER:
                return ImageConstants.IMG_16_CHROME;

            case IE_DRIVER:
                return ImageConstants.IMG_16_IE;

            default:
                return null;
        }
    }

    private Image getWebUIDriverToolItemImage(WebUIDriverType webUIDriverType) {
        switch (webUIDriverType) {
            case FIREFOX_DRIVER:
                return ImageConstants.IMG_24_FIREFOX;

            case CHROME_DRIVER:
                return ImageConstants.IMG_24_CHROME;

            case IE_DRIVER:
                return ImageConstants.IMG_24_IE;

            default:
                return null;
        }
    }

    public void dispose() {
        if (server != null && server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        eventBroker.unsubscribe(this);
        stopRecordSession();
        AddonSocketServer.getInstance().stop();
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
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite bottomComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        bottomComposite.setLayout(layout);
        bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new HelpCompositeForDialog(bottomComposite, DocumentationMessageConstants.DIALOG_RECORDER_WEB_UI);
        super.createButtonBar(bottomComposite);

        return bottomComposite;
    }

    private List<WebPage> getCloneCapturedObjects(final List<WebPage> pages) {
        return pages.stream().map(page -> page.softClone()).collect(Collectors.toList());
    }

    @Override
    protected void okPressed() {
        Shell shell = getShell();
        try {
            if (!addElementToObjectRepository(shell)) {
                return;
            }

            super.okPressed();
            dispose();
        } catch (Exception exception) {
            logger.error(exception);
            MessageDialog.openError(shell, StringConstants.ERROR_TITLE, exception.getMessage());
        }
    }

    private boolean addElementToObjectRepository(Shell shell) throws Exception {
        TreeViewer capturedTreeViewer = capturedObjectComposite.getTreeViewer();
        SaveToObjectRepositoryDialog addToObjectRepositoryDialog = new SaveToObjectRepositoryDialog(shell, true,
                getCloneCapturedObjects(elements), capturedTreeViewer.getExpandedElements());
        if (addToObjectRepositoryDialog.open() != Window.OK) {
            return false;
        }
        targetFolderSelectionResult = addToObjectRepositoryDialog.getDialogResult();
        return true;
    }

    @Override
    public boolean close() {
        updateStore();
        disposed = true;
        return super.close();
    }

    private void updateStore() {
        store.setValue(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_URL, txtStartUrl.getText());
        store.setValue(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_BROWSER, selectedBrowser.toString());
        try {
            store.save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
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
        return getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
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
                capturedObjectComposite.getTreeViewer().refresh();
                WebElement targetElement = newAction.getTargetElement();
                if (targetElement != null) {
                    capturedObjectComposite.getTreeViewer().reveal(targetElement);
                }
            }
        });
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

    private void addNewElement(WebElement newElement, HTMLActionMapping newAction) {
        WebPage parentPageElement = findPage(newElement);
        if (parentPageElement != null) {
            if (elements.contains(parentPageElement)) {
                addNewElement(elements.get(elements.indexOf(parentPageElement)), parentPageElement.getChildren().get(0),
                        parentPageElement, newAction);
            } else {
                elements.add(parentPageElement);
            }
        }
    }

    private void addNewElement(WebFrame parentElement, WebElement newElement, WebPage pageElement,
            HTMLActionMapping newAction) {
        if (indexOf(parentElement.getChildren(), newElement) >= 0) {
            if (newElement instanceof WebFrame) {
                WebFrame frameElement = (WebFrame) newElement;
                WebFrame existingFrameElement = (WebFrame) (parentElement.getChildren()
                        .get(indexOf(parentElement.getChildren(), newElement)));
                addNewElement(existingFrameElement, frameElement.getChildren().get(0), pageElement, newAction);
            } else {
                for (WebElement element : parentElement.getChildren()) {
                    if (isEquals(element, newElement)) {
                        newAction.setTargetElement(element);
                        break;
                    }
                }
            }
        } else {
            newElement.setName(getNewElementDisplayName(parentElement, newElement));
            newElement.setParent(parentElement);
            return;
        }
    }

    private String getNewElementDisplayName(WebFrame parentElement, WebElement newElement) {
        String newElementName = newElement.getName();
        List<WebElement> sameLevelElements = parentElement.getChildren();
        int maxPostfixNumber = 0;
        int count = 0;
        for (WebElement element : sameLevelElements) {
            String elementName = element.getName();
            if (elementName.equals(newElementName)) {
                count++;
            } else if (elementName.startsWith(newElementName)) {
                int expectedUnderscoreIndex = newElementName.length();
                if (elementName.length() > expectedUnderscoreIndex + 1
                        && elementName.charAt(expectedUnderscoreIndex) == '_') {
                    try {
                        int postfixNumber = Integer.parseInt(elementName.substring(expectedUnderscoreIndex + 1));
                        if (postfixNumber > maxPostfixNumber) {
                            maxPostfixNumber = postfixNumber;
                        }
                        count++;
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        }
        return newElementName + (count > 0 ? "_" + (++maxPostfixNumber) : "");
    }

    private int indexOf(List<WebElement> elements, WebElement frame) {
        for (int index = 0; index < elements.size(); index++) {
            if (isEquals(frame, elements.get(index))) {
                return index;
            }
        }
        return -1;
    }

    private boolean isEquals(WebElement elm1, WebElement elm2) {
        return new EqualsBuilder().append(elm1.getType(), elm2.getType())
                .append(elm1.getTag(), elm2.getTag())
                .append(elm1.hasProperty(), elm2.hasProperty())
                .append(elm1.getXpath(), elm2.getXpath())
                .isEquals();
    }

    public List<HTMLActionMapping> getActions() {
        return recordedActions;
    }

    public List<WebPage> getElements() {
        return elements;
    }

    public SaveToObjectRepositoryDialogResult getTargetFolderTreeEntity() {
        return targetFolderSelectionResult;
    }

    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public void handleEvent(org.osgi.service.event.Event event) {
        Object dataObject = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
        switch (event.getTopic()) {
            case EventConstants.RECORDER_HTML_ACTION_CAPTURED:
                if (!(dataObject instanceof HTMLActionMapping)) {
                    return;
                }
                addNewActionMapping((HTMLActionMapping) dataObject);
                return;
            case EventConstants.RECORDER_ACTION_OBJECT_REORDERED:
                if (!(dataObject instanceof WebElement[])) {
                    return;
                }

                WebElement[] oldNewElement = (WebElement[]) dataObject;
                if(oldNewElement.length != 2) {
                    return;
                }
                replaceCapturedObjectInActionMapping(oldNewElement[0], oldNewElement[1]);
                actionTableViewer.refresh();
                return;
            case EventConstants.WORKSPACE_CLOSED:
                cancelPressed();
                return;
        }
    }

    private void replaceCapturedObjectInActionMapping(WebElement oldElement, WebElement newElement) {
        if (recordedActions == null || recordedActions.isEmpty()) {
            return;
        }
        for (HTMLActionMapping action : recordedActions) {
            if (oldElement.equals(action.getTargetElement())) {
                action.setTargetElement(newElement);
            }
        }
    }

    @Override
    protected void registerControlModifyListeners() {
        // Do nothing for this
    }

    @Override
    protected void setInput() {
        // Do nothing for this
    }

    private Map<ObjectSpyEvent, Set<EventListener<ObjectSpyEvent>>> eventListeners = new HashMap<>();

    @Override
    public Iterable<EventListener<ObjectSpyEvent>> getListeners(ObjectSpyEvent event) {
        return eventListeners.get(event);
    }

    @Override
    public void addListener(EventListener<ObjectSpyEvent> listener, Iterable<ObjectSpyEvent> events) {
        events.forEach(e -> {
            Set<EventListener<ObjectSpyEvent>> listenerOnEvent = eventListeners.get(e);
            if (listenerOnEvent == null) {
                listenerOnEvent = new HashSet<>();
            }
            listenerOnEvent.add(listener);
            eventListeners.put(e, listenerOnEvent);
        });
    }
}
