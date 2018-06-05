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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.framework.Bundle;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.controls.HelpCompositeForDialog;
import com.kms.katalon.composer.components.impl.control.Dropdown;
import com.kms.katalon.composer.components.impl.control.DropdownGroup;
import com.kms.katalon.composer.components.impl.control.DropdownItemSelectionListener;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants.AddAction;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.decoration.DecoratedKeyword;
import com.kms.katalon.composer.testcase.parts.decoration.KeywordDecorationService;
import com.kms.katalon.composer.testcase.preferences.StoredKeyword;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.composer.testcase.util.TestCaseMenuUtil;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
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
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.dialog.CapturedObjectsView;
import com.kms.katalon.objectspy.dialog.GoToAddonStoreMessageDialog;
import com.kms.katalon.objectspy.dialog.ObjectPropertiesView;
import com.kms.katalon.objectspy.dialog.ObjectRepositoryService;
import com.kms.katalon.objectspy.dialog.ObjectRepositoryService.SaveActionResult;
import com.kms.katalon.objectspy.dialog.ObjectSpyEvent;
import com.kms.katalon.objectspy.dialog.ObjectSpySelectorEditor;
import com.kms.katalon.objectspy.dialog.ObjectSpyUrlView;
import com.kms.katalon.objectspy.dialog.ObjectVerifyAndHighlightView;
import com.kms.katalon.objectspy.dialog.SaveToObjectRepositoryDialog;
import com.kms.katalon.objectspy.dialog.SaveToObjectRepositoryDialog.SaveToObjectRepositoryDialogResult;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebFrame;
import com.kms.katalon.objectspy.element.WebPage;
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

    private static final String RECORD_TOOL_ITEM_LABEL = StringConstants.DIA_TOOLITEM_RECORD;

    private static Point MIN_DIALOG_SIZE = new Point(600, 600);

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

    private Text txtStartUrl;

    private AddonSocket currentInstantSocket;

    private IEventBroker eventBroker;

    private ScopedPreferenceStore store;

    private SashForm hSashForm;

    private boolean disposed;

    private RecordedStepsView recordStepsView;

    private ObjectPropertiesView objectPropertiesView;

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
                getShell().setMinimized(true);
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
                MessageDialog.openInformation(getShell(), StringConstants.INFO,
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
        // actionTableViewer.refresh();
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
        hSashForm.setSashWidth(0);

        Composite leftPanelComposite = new Composite(hSashForm, SWT.NONE);
        GridLayout glHtmlDomComposite = new GridLayout();
        glHtmlDomComposite.marginBottom = 5;
        glHtmlDomComposite.marginRight = 0;
        glHtmlDomComposite.marginWidth = 0;
        glHtmlDomComposite.marginHeight = 0;
        glHtmlDomComposite.horizontalSpacing = 0;
        leftPanelComposite.setLayout(glHtmlDomComposite);

        createRightPanel(leftPanelComposite);

        Composite rightPanelComposite = new Composite(hSashForm, SWT.NONE);
        GridLayout glObjectComposite = new GridLayout();
        glObjectComposite.marginLeft = 5;
        glObjectComposite.marginWidth = 0;
        glObjectComposite.marginBottom = 0;
        glObjectComposite.marginHeight = 0;
        glObjectComposite.verticalSpacing = 0;
        glObjectComposite.horizontalSpacing = 0;
        rightPanelComposite.setLayout(glObjectComposite);

        createLeftPanel(rightPanelComposite);

        hSashForm.setWeights(new int[] { 10, 0 });

        txtStartUrl.setFocus();

        initializeInput();

        return container;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
       // newShell.setMinimumSize(MIN_DIALOG_SIZE);
        //newShell.setSize(MIN_DIALOG_SIZE);
    }

    private void initializeInput() {
        txtStartUrl.setText(store.getString(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_URL));
        txtStartUrl.selectAll();

        getTreeTableInput().refresh();
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
        objectPropertiesView = new ObjectPropertiesView(parent, SWT.NONE);
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
        this.addListener(recordStepsView, 
                Arrays.asList(ObjectSpyEvent.ADDON_SESSION_STARTED, ObjectSpyEvent.SELENIUM_SESSION_STARTED));

        objectPropertiesView.addListener(recordStepsView, Arrays.asList(ObjectSpyEvent.ELEMENT_NAME_CHANGED));
        
        recordStepsView.setCapturedObjectsView(capturedObjectComposite);
    }

    private void createActionToolbar(Composite parent) {
        ToolBar rightToolBar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        rightToolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));

        ToolItem tltmCapturedObjects = new ToolItem(rightToolBar, SWT.PUSH);
        tltmCapturedObjects.setText(StringConstants.DIA_TITLE_SHOW + StringConstants.DIA_TITLE_CAPTURED_OBJECTS + " >>");
        tltmCapturedObjects.setToolTipText(StringConstants.DIA_TOOLTIP_SHOW_HIDE_CAPTURED_OBJECTS);
        tltmCapturedObjects.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] sashFormWeights = new int[] { 10, 0 };
                String showOrHide = StringConstants.DIA_TITLE_SHOW + StringConstants.DIA_TITLE_CAPTURED_OBJECTS + " >>";
                hSashForm.setSashWidth(0);
                if (tltmCapturedObjects.getText().contains(StringConstants.DIA_TITLE_SHOW)) {
                    sashFormWeights = new int[] { 55, 45 };
                    showOrHide = "<< " + StringConstants.DIA_TITLE_HIDE + StringConstants.DIA_TITLE_CAPTURED_OBJECTS;
                    hSashForm.setSashWidth(5);
                }
                tltmCapturedObjects.setText(showOrHide);
                hSashForm.setWeights(sashFormWeights);
                getShell().pack();
            }
        });
    }

    private void createRightPanel(Composite parent) {
        Composite labelComposite = new Composite(parent, SWT.NONE);
        GridLayout glLabelComposite = new GridLayout(2, false);
        glLabelComposite.marginWidth = 0;
        glLabelComposite.marginHeight = 0;
        labelComposite.setLayout(glLabelComposite);

        Label lblRecordedActions = new Label(labelComposite, SWT.NONE);
        lblRecordedActions.setFont(getFontBold(lblRecordedActions));
        lblRecordedActions.setText(StringConstants.DIA_LBL_RECORED_ACTIONS);
        labelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createActionToolbar(labelComposite);

        Composite compositeSteps = new Composite(parent, SWT.NONE);
        compositeSteps.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout glCompositeSteps = new GridLayout(1, false);
        glCompositeSteps.marginWidth = 0;
        glCompositeSteps.marginHeight = 0;
        compositeSteps.setLayout(glCompositeSteps);

        createStepButtons(compositeSteps);

        Composite tableComposite = new Composite(compositeSteps, SWT.None);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        tableComposite.setLayout(layout);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        recordStepsView = new RecordedStepsView();
        recordStepsView.createContent(tableComposite);
    }

    private ToolItem tltmAddStep, tltmRemoveStep, tltmUp, tltmDown, tltmRecent;

    private void createStepButtons(Composite compositeSteps) {
        Composite compositeToolbars = new Composite(compositeSteps, SWT.NONE);
        compositeToolbars.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        compositeToolbars.setLayout(layout);

        Composite compositeTableButtons = new Composite(compositeToolbars, SWT.NONE);
        GridLayout glCompositeTableButtons = new GridLayout(4, false);
        glCompositeTableButtons.marginHeight = 0;
        glCompositeTableButtons.marginWidth = 0;
        compositeTableButtons.setLayout(glCompositeTableButtons);
        compositeTableButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        ToolBar toolbar = toolBarManager.createControl(compositeTableButtons);

        SelectionListener selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object item = e.getSource();
                if (item instanceof ToolItem) {
                    performToolItemSelected((ToolItem) e.getSource(), e);
                    return;
                }
                if (item instanceof MenuItem) {
                    performMenuItemSelected((MenuItem) e.getSource());
                }
            }
        };

        tltmAddStep = new ToolItem(toolbar, SWT.DROP_DOWN);
        tltmAddStep.setText(StringConstants.ADD);
        tltmAddStep.setImage(ImageConstants.IMG_16_ADD);
        tltmAddStep.addSelectionListener(selectionListener);

        Menu addMenu = new Menu(tltmAddStep.getParent().getShell());
        tltmAddStep.setData(addMenu);
        TestCaseMenuUtil.fillActionMenu(TreeTableMenuItemConstants.AddAction.Add, selectionListener, addMenu);
        
        tltmRecent = new ToolItem(toolbar, SWT.DROP_DOWN);
        tltmRecent.setText(ComposerTestcaseMessageConstants.PA_BTN_TIP_RECENT);
        tltmRecent.setImage(ImageConstants.IMG_16_RECENT);
        tltmRecent.addSelectionListener(selectionListener);
        setRecentKeywordItemState();

        tltmRemoveStep = new ToolItem(toolbar, SWT.NONE);
        tltmRemoveStep.setText(StringConstants.REMOVE);
        tltmRemoveStep.setImage(ImageConstants.IMG_16_DELETE);
        tltmRemoveStep.addSelectionListener(selectionListener);

        tltmUp = new ToolItem(toolbar, SWT.NONE);
        tltmUp.setText(StringConstants.DIA_ITEM_MOVE_UP);
        tltmUp.setImage(ImageConstants.IMG_16_MOVE_UP);
        tltmUp.addSelectionListener(selectionListener);

        tltmDown = new ToolItem(toolbar, SWT.NONE);
        tltmDown.setText(StringConstants.DIA_ITEM_MOVE_DOWN);
        tltmDown.setImage(ImageConstants.IMG_16_MOVE_DOWN);
        tltmDown.addSelectionListener(selectionListener);
    }

    private void setRecentKeywordItemState() {
        tltmRecent.setEnabled(!TestCasePreferenceDefaultValueInitializer.getRecentKeywords().isEmpty());
    }

    public void performToolItemSelected(ToolItem toolItem, SelectionEvent selectionEvent) {
        getTreeTable().applyEditorValue();
        if (toolItem.equals(tltmAddStep)) {
            openToolItemMenu(toolItem, selectionEvent);
            return;
        }
        if (toolItem.equals(tltmRemoveStep)) {
            getTreeTableInput().removeSelectedRows();
            return;
        }
        if (toolItem.equals(tltmUp)) {
            getTreeTableInput().moveUp();
            return;
        }
        if (toolItem.equals(tltmDown)) {
            getTreeTableInput().moveDown();
        }
        if (toolItem.equals(tltmRecent)) {
            openRecentKeywordItems();
        }
    }

    private Menu recentMenu;

    private void openRecentKeywordItems() {
        List<StoredKeyword> recentKeywords = TestCasePreferenceDefaultValueInitializer.getRecentKeywords();
        if (recentKeywords.isEmpty()) {
            return;
        }
        if (recentMenu != null && !recentMenu.isDisposed()) {
            recentMenu.dispose();
        }
        recentMenu = new Menu(tltmRecent.getParent());
        recentKeywords.forEach(keyword -> {
            addRecentMenuItem(keyword);
        });
        Rectangle rect = tltmRecent.getBounds();
        Point pt = tltmRecent.getParent().toDisplay(new Point(rect.x, rect.y));
        recentMenu.setLocation(pt.x, pt.y + rect.height);
        recentMenu.setVisible(true);
    }

    private void addRecentMenuItem(StoredKeyword keyword) {
        MenuItem recentMenuItem = new MenuItem(recentMenu, SWT.PUSH);
        final DecoratedKeyword decoratedKeyword = KeywordDecorationService.getDecoratedKeyword(keyword);
        recentMenuItem.setText(decoratedKeyword.getLabel());
        recentMenuItem.setToolTipText(decoratedKeyword.getTooltip());
        recentMenuItem.setImage(decoratedKeyword.getImage());

        recentMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TestCaseTreeTableInput treeTableInput = recordStepsView.getTreeTableInput();
                AstTreeTableNode destination = treeTableInput.getSelectedNode();
                treeTableInput.addNewAstObject(
                        decoratedKeyword.newStep(treeTableInput.getParentNodeForNewMethodCall(destination)),
                        destination, NodeAddType.Add);
            }
        });
    }

    public void performMenuItemSelected(MenuItem menuItem) {
        getTreeTable().applyEditorValue();
        NodeAddType addType = NodeAddType.Add;
        Object value = menuItem.getData(TreeTableMenuItemConstants.MENU_ITEM_ACTION_KEY);
        if (value instanceof AddAction) {
            switch ((AddAction) value) {
                case Add:
                    addType = NodeAddType.Add;
                    break;
                case InsertAfter:
                    addType = NodeAddType.InserAfter;
                    break;
                case InsertBefore:
                    addType = NodeAddType.InserBefore;
                    break;
            }
        }
        switch (menuItem.getID()) {
            case TreeTableMenuItemConstants.CHANGE_FAILURE_HANDLING_MENU_ITEM_ID:
                Object failureHandlingValue = menuItem.getData(TreeTableMenuItemConstants.FAILURE_HANDLING_KEY);
                if (failureHandlingValue instanceof FailureHandling) {
                    getTreeTableInput().changeFailureHandling((FailureHandling) failureHandlingValue);
                }
                break;
            case TreeTableMenuItemConstants.COPY_MENU_ITEM_ID:
                getTreeTableInput().copy(getTreeTableInput().getSelectedNodes());
                break;
            case TreeTableMenuItemConstants.CUT_MENU_ITEM_ID:
                getTreeTableInput().cut(getTreeTableInput().getSelectedNodes());
                break;
            case TreeTableMenuItemConstants.PASTE_MENU_ITEM_ID:
                getTreeTableInput().paste(getTreeTableInput().getSelectedNode(), addType);
                ;
                break;
            case TreeTableMenuItemConstants.REMOVE_MENU_ITEM_ID:
                getTreeTableInput().removeSelectedRows();
                break;
            case TreeTableMenuItemConstants.ENABLE_MENU_ITEM_ID:
                getTreeTableInput().enable();
                break;
            case TreeTableMenuItemConstants.DISABLE_MENU_ITEM_ID:
                getTreeTableInput().disable();
                break;
            default:
                getTreeTableInput().addNewAstObject(menuItem.getID(), getTreeTableInput().getSelectedNode(), addType);
                break;
        }
    }

    private TestCaseTreeTableInput getTreeTableInput() {
        return recordStepsView.getTreeTableInput();
    }

    private void openToolItemMenu(ToolItem toolItem, SelectionEvent selectionEvent) {
        if (selectionEvent.detail == SWT.ARROW && toolItem.getData() instanceof Menu) {
            Rectangle rect = toolItem.getBounds();
            Point pt = toolItem.getParent().toDisplay(new Point(rect.x, rect.y));
            Menu menu = (Menu) toolItem.getData();
            menu.setLocation(pt.x, pt.y + rect.height);
            menu.setVisible(true);
        } else {
            recordStepsView.getTreeTableInput().addNewDefaultBuiltInKeyword(NodeAddType.Add);
        }
    }

    private ColumnViewer getTreeTable() {
        return recordStepsView.getTreeTable();
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
                                        .openInformation(getShell(), StringConstants.HAND_ACTIVE_BROWSERS_DIA_TITLE,
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
        if (capturedTreeViewer.getTree().getItemCount() == 0) {
            return true;
        }
        SaveToObjectRepositoryDialog addToObjectRepositoryDialog = new SaveToObjectRepositoryDialog(shell, true,
                getCloneCapturedObjects(elements), capturedTreeViewer.getExpandedElements());
        if (addToObjectRepositoryDialog.open() != Window.OK) {
            return false;
        }
        
        targetFolderSelectionResult = addToObjectRepositoryDialog.getDialogResult();

        ObjectRepositoryService objectRepositoryService = new ObjectRepositoryService();
        refeshExplorer(objectRepositoryService.saveObject(targetFolderSelectionResult), addToObjectRepositoryDialog.getSelectedParentFolderResult());
        
        return true;
    }
    
    private void refeshExplorer(SaveActionResult saveResult, FolderTreeEntity selectedParentFolder) {
        // Refresh tree explorer
        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, selectedParentFolder);
        
        //Refesh updated object.
        for (Object[] testObj : saveResult.getUpdatedTestObjectIds()) {
            eventBroker.post(EventConstants.TEST_OBJECT_UPDATED, testObj);
        }
        if (saveResult.getNewSelectionOnExplorer() == null) {
            return;
        }
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
        return MIN_DIALOG_SIZE;
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
        WebElement targetElement = newAction.getTargetElement();
        if (targetElement != null) {
            addNewElement(targetElement, newAction);
        }
        recordedActions.add(newAction);
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    recordStepsView.addNewStep(newAction);
                } catch (ClassNotFoundException ignored) {}
                capturedObjectComposite.getTreeViewer().refresh();
                if (targetElement != null) {
                    capturedObjectComposite.getTreeViewer().setSelection(new StructuredSelection(targetElement), true);
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

    public ScriptNodeWrapper getScriptWrapper() {
        return recordStepsView.getWrapper();
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
                if (oldNewElement.length != 2) {
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
