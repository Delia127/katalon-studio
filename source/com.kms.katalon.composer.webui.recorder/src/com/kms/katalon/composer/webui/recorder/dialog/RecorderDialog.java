package com.kms.katalon.composer.webui.recorder.dialog;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.BindException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
import org.eclipse.swt.program.Program;
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
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.control.Dropdown;
import com.kms.katalon.composer.components.impl.control.DropdownGroup;
import com.kms.katalon.composer.components.impl.control.DropdownItemSelectionListener;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.handler.WorkbenchUtilizer;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.KeyEventUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants.AddAction;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.decoration.DecoratedKeyword;
import com.kms.katalon.composer.testcase.parts.decoration.KeywordDecorationService;
import com.kms.katalon.composer.testcase.preferences.StoredKeyword;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.TestCaseMenuUtil;
import com.kms.katalon.composer.webui.recorder.action.HTMLAction;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.ast.RecordedElementMethodCallWrapper;
import com.kms.katalon.composer.webui.recorder.constants.ComposerWebuiRecorderMessageConstants;
import com.kms.katalon.composer.webui.recorder.constants.ImageConstants;
import com.kms.katalon.composer.webui.recorder.constants.RecorderPreferenceConstants;
import com.kms.katalon.composer.webui.recorder.constants.StringConstants;
import com.kms.katalon.composer.webui.recorder.core.HTMLElementRecorderServer;
import com.kms.katalon.composer.webui.recorder.core.RecordSession;
import com.kms.katalon.composer.webui.recorder.core.RecordSession.BrowserStoppedListener;
import com.kms.katalon.composer.webui.recorder.websocket.RecorderAddonSocket;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.CustomRunConfigurationContributor;
import com.kms.katalon.execution.webservice.RecordingScriptGenerator;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.execution.webui.util.WebUIExecutionUtil;
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
import com.kms.katalon.objectspy.util.WebElementUtils;
import com.kms.katalon.objectspy.util.Win32Helper;
import com.kms.katalon.objectspy.util.WinRegistry;
import com.kms.katalon.objectspy.websocket.AddonCommand;
import com.kms.katalon.objectspy.websocket.AddonSocket;
import com.kms.katalon.objectspy.websocket.AddonSocketServer;
import com.kms.katalon.objectspy.websocket.messages.AddonMessage;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.listener.EventListener;
import com.kms.katalon.util.listener.EventManager;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

public class RecorderDialog extends AbstractDialog implements EventHandler, EventManager<ObjectSpyEvent> {
	private static final String IE_WINDOW_CLASS = "IEFrame"; //$NON-NLS-1$

	private static final String RELATIVE_PATH_TO_IE_ADDON_SETUP = File.separator + "extensions" + File.separator + "IE" //$NON-NLS-1$ //$NON-NLS-2$
			+ File.separator + RecordSession.RECORDER_ADDON_NAME + File.separator + "setup.exe"; //$NON-NLS-1$

	private static final String RESOURCES_FOLDER_NAME = "resources"; //$NON-NLS-1$

	private static final String IE_ADDON_BHO_KEY = "{FEA8CA38-7979-4F6A-83E4-2949EDEA96EF}"; //$NON-NLS-1$

	public static final String DIA_INSTANT_BROWSER_CHROME_RECORDER_EXTENSION_PATH = "<Katalon build path>/Resources/extensions/Chrome/Recorder Packed"; //$NON-NLS-1$

	public static final String RECORDER_FIREFOX_ADDON_URL = "https://addons.mozilla.org/en-US/firefox/addon/katalon-automation-record/"; //$NON-NLS-1$

	private static final int ANY_PORT_NUMBER = 0;

    private static final int HORIZONTAL_SASH_FORM_WIDTH = 2;

    private static final String RECORD_SESSION_ID;


	private static final String RECORD_TOOL_ITEM_LABEL = StringConstants.DIA_TOOLITEM_RECORD;


    private static Point MIN_DIALOG_SIZE = new Point(600, 800);

    static {
        RECORD_SESSION_ID = UUID.randomUUID().toString();
    }

    private HTMLElementRecorderServer server;

    private List<WebPage> elements;

    private boolean isPauseRecording;

    private ToolBar toolBar;

    private ToolItem toolItemBrowserDropdown, tltmPauseAndResume, tltmStop;

    private RecordSession session;

    private SaveToObjectRepositoryDialogResult targetFolderSelectionResult;

    private IDriverConnector selectedBrowser;

    private Text txtStartUrl;

    private AddonSocket currentInstantSocket;

    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    private ScopedPreferenceStore store;

    private SashForm hSashForm;

    private boolean disposed;

    private RecordedStepsView recordStepsView;

    private ObjectPropertiesView objectPropertiesView;

    private CapturedObjectsView capturedObjectComposite;

    private CTabFolder bottomTabFolder;

    private int logTabItemIdex;

    private List<? extends ASTNodeWrapper> nodeWrappers;

    private List<VariableEntity> variables;

    private TestCaseEntity testCaseEntity;
    
    private boolean isOkPressed = false;
    
    private boolean isUsingIE = false;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public RecorderDialog(Shell parentShell, TestCaseEntity testCaseEntity, List<? extends ASTNodeWrapper> nodeWrappers,
            List<VariableEntity> variables) {
        super(parentShell);
        this.nodeWrappers = nodeWrappers;
        this.variables = variables;
        this.testCaseEntity = testCaseEntity;
        store = PreferenceStoreManager.getPreferenceStore(RecorderPreferenceConstants.WEBUI_RECORDER_QUALIFIER);
        elements = new ArrayList<>();
        isPauseRecording = false;
        disposed = false;
        setDialogTitle(GlobalMessageConstants.WEB_RECORDER);
        registerEventListener();
        startSocketServer();
    }

    private void registerEventListener() {
        eventBroker.subscribe(EventConstants.RECORDER_HTML_ACTION_CAPTURED, this);
        eventBroker.subscribe(EventConstants.RECORDER_ACTION_OBJECT_REORDERED, this);
        eventBroker.subscribe(EventConstants.WORKSPACE_CLOSED, this);
        eventBroker.subscribe(EventConstants.WEBUI_VERIFICATION_EXECUTION_FINISHED, this);
        eventBroker.subscribe(EventConstants.WEBUI_VERIFICATION_RUN_ALL_STEPS_CMD, this);
        eventBroker.subscribe(EventConstants.WEBUI_VERIFICATION_RUN_SELECTED_STEPS_CMD, this);
        eventBroker.subscribe(EventConstants.WEBUI_VERIFICATION_RUN_FROM_STEP_CMD, this);
    }

    @Override
    protected int getShellStyle() {
        boolean onTop = store.getBoolean(RecorderPreferenceConstants.WEBUI_RECORDER_PIN_WINDOW);
        if (onTop && !Platform.OS_LINUX.equals(Platform.getOS())) {
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
        if (!BrowserUtil.isBrowserInstalled(getSelectedBrowserType())) {
            MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE,
                    ComposerWebuiRecorderMessageConstants.DIA_MSG_CANNOT_START_BROWSER);
            return;
        }
        try {    	
            if (getSelectedBrowserType() == WebUIDriverType.IE_DRIVER) {
            	isUsingIE = true;
                checkIEAddon();
            }else{
            	isUsingIE = false;
            }
            
            if (isInstant) {
                startInstantSession();
                invoke(ObjectSpyEvent.ADDON_SESSION_STARTED, currentInstantSocket);
            } else {
                startServer();
                startRecordSession();
                invoke(ObjectSpyEvent.SELENIUM_SESSION_STARTED, session);
            }
            if (!isPauseRecording) {
                recordStepsView.addSimpleKeyword("openBrowser", true);
            }

            tltmPauseAndResume.setEnabled(true);
            tltmStop.setEnabled(true);
            resume();
            Trackings.trackWebRecord(getSelectedBrowserType(), isInstant, getWebLocatorConfig());
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
            LoggerSingleton.logError(e);
            MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, e.getMessage());
        }
    }

    private void startInstantSession() throws Exception {
        if (getSelectedBrowserType() == WebUIDriverType.IE_DRIVER) {
            runInstantIE();
        }
        currentInstantSocket = AddonSocketServer.getInstance().getAddonSocketByBrowserName(getSelectedBrowserType().toString());
        if (currentInstantSocket == null) {
            return;
        }
        Win32Helper.switchFocusToBrowser(getSelectedBrowserType());
        currentInstantSocket.sendMessage(new AddonMessage(AddonCommand.START_RECORD));
    }

    private void closeInstantSession() {
        if (currentInstantSocket != null && currentInstantSocket.isConnected()) {
            currentInstantSocket.close();
        }
    }

    protected void runInstantIE() throws Exception {
        session = new RecordSession(server, selectedBrowser);
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
        String ieAddonSetupPath = getResourcesDirectory().getAbsolutePath() + RELATIVE_PATH_TO_IE_ADDON_SETUP;
        Desktop desktop = Desktop.getDesktop();
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        desktop.open(new File(ieAddonSetupPath));
    }

    private void startRecordSession() throws Exception {
        stopRecordSession();
        session = new RecordSession(server, selectedBrowser, txtStartUrl.getText());
        session.addBrowserStoppedListener(new BrowserStoppedListener() {

            @Override
            public void onBrowserStopped() {
                UISynchronizeService.syncExec(() -> {
                    if (getShell() != null && !getShell().isDisposed() && session.isRunning()) {
                        stop();
                    }
                });
            }
        });
        new Thread(session).start();
    }

    private IDriverConnector getDefaultBrowser() {
        try {
            WebUIDriverType browserType = WebUIDriverType.fromStringValue(getPreferenceStore().getString(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_BROWSER));
            return WebUIExecutionUtil.getBrowserDriverConnector(browserType, ProjectController.getInstance().getCurrentProject().getFolderLocation());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, e.getMessage());
        }
        return null;
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
			server = new HTMLElementRecorderServer(port, this, RecorderAddonSocket.class);
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
        isPauseRecording = true;
        tltmPauseAndResume.setToolTipText(ComposerWebuiRecorderMessageConstants.DIA_TOOLTIP_RESUME_RECORDING);
        tltmPauseAndResume.setImage(ImageConstants.IMG_24_RESUME_RECORDING);
        toolBar.getParent().layout();
    }

    private void resume() {
        isPauseRecording = false;
        tltmPauseAndResume.setToolTipText(ComposerWebuiRecorderMessageConstants.DIA_TOOLTIP_PAUSE_RECORDING);
        tltmPauseAndResume.setImage(ImageConstants.IMG_24_PAUSE_RECORDING);
        toolBar.getParent().layout();
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setBackground(ColorUtil.getCompositeBackgroundColorForDialog());
        container.setBackgroundMode(SWT.INHERIT_FORCE);

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

        createStepsPanel(leftPanelComposite);

        Composite rightPanelComposite = new Composite(hSashForm, SWT.NONE);
        GridLayout glObjectComposite = new GridLayout();
        glObjectComposite.marginLeft = 5;
        glObjectComposite.marginWidth = 0;
        glObjectComposite.marginBottom = 0;
        glObjectComposite.marginHeight = 0;
        glObjectComposite.verticalSpacing = 0;
        glObjectComposite.horizontalSpacing = 0;
        rightPanelComposite.setLayout(glObjectComposite);

        createObjectsPanel(rightPanelComposite);

        hSashForm.setWeights(new int[] { 10, 0 });

        txtStartUrl.setFocus();

        initializeInput();

        return container;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
    }

    private void initializeInput() {
        txtStartUrl.setText(store.getString(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_URL));
        txtStartUrl.selectAll();

        getTreeTableInput().refresh();
    }

    private void createObjectsPanel(Composite parent) {
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
        objectPropertiesView.setRefreshCapturedObjectsTree(() -> capturedObjectComposite.refreshTree(null));

        ObjectSpySelectorEditor selectorEditor = new ObjectSpySelectorEditor();
        selectorEditor.createObjectSelectorEditor(parent);

        ObjectVerifyAndHighlightView verifyView = new ObjectVerifyAndHighlightView();
        verifyView.createVerifyAndHighlightView(parent, GridData.FILL_HORIZONTAL);

        capturedObjectComposite.addListener(objectPropertiesView,
                Arrays.asList(ObjectSpyEvent.SELECTED_ELEMENT_CHANGED));
        
        selectorEditor.addListener(verifyView, Arrays.asList(ObjectSpyEvent.SELECTOR_HAS_CHANGED));
        objectPropertiesView.addListener(selectorEditor, Arrays.asList(ObjectSpyEvent.ELEMENT_PROPERTIES_CHANGED));
        verifyView.addListener(objectPropertiesView, Arrays.asList(ObjectSpyEvent.ELEMENT_PROPERTIES_CHANGED));

        this.addListener(verifyView,
                Arrays.asList(ObjectSpyEvent.ADDON_SESSION_STARTED, ObjectSpyEvent.SELENIUM_SESSION_STARTED));
        this.addListener(recordStepsView,
                Arrays.asList(ObjectSpyEvent.ADDON_SESSION_STARTED, ObjectSpyEvent.SELENIUM_SESSION_STARTED));

        objectPropertiesView.addListener(recordStepsView, Arrays.asList(ObjectSpyEvent.ELEMENT_NAME_CHANGED));

        recordStepsView.setCapturedObjectsView(capturedObjectComposite);
    }

    private void createActionToolbar(Composite parent) {
        ToolBar rightToolBar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        rightToolBar.setForeground(ColorUtil.getToolBarForegroundColor());
        rightToolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));

        ToolItem tltmCapturedObjects = new ToolItem(rightToolBar, SWT.PUSH);
        tltmCapturedObjects
                .setText(StringConstants.DIA_TITLE_SHOW + StringConstants.DIA_TITLE_CAPTURED_OBJECTS + " >>");
        tltmCapturedObjects.setToolTipText(StringConstants.DIA_TOOLTIP_SHOW_HIDE_CAPTURED_OBJECTS);
        tltmCapturedObjects.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String showOrHide = StringConstants.DIA_TITLE_SHOW + StringConstants.DIA_TITLE_CAPTURED_OBJECTS + " >>";
                int[] sashFormWeights;
                int sashWidth;
                Point currentSize = hSashForm.getSize();
                Point shellSize = getShell().getSize();
                int widthDiff;
                if (tltmCapturedObjects.getText().contains(StringConstants.DIA_TITLE_SHOW)) {
                    widthDiff = currentSize.x * 100 / 60 + HORIZONTAL_SASH_FORM_WIDTH - currentSize.x;
                    sashFormWeights = new int[] { 60, 40 };
                    showOrHide = "<< " + StringConstants.DIA_TITLE_HIDE + StringConstants.DIA_TITLE_CAPTURED_OBJECTS;
                    sashWidth = HORIZONTAL_SASH_FORM_WIDTH;
                } else {
                    widthDiff = (currentSize.x - HORIZONTAL_SASH_FORM_WIDTH) * 60 / 100 - currentSize.x;
                    sashFormWeights = new int[] { 10, 0 };
                    sashWidth = 0;
                }
                tltmCapturedObjects.setText(showOrHide);
                hSashForm.setWeights(sashFormWeights);
                hSashForm.setSashWidth(sashWidth);
                getShell().setSize(shellSize.x + widthDiff, shellSize.y);
                tltmCapturedObjects.getParent().layout(true);
            }
        });
    }

    private void createStepsPanel(Composite parent) {
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

        SashForm compositeSteps = new SashForm(parent, SWT.VERTICAL);
        compositeSteps.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite compositeStepsTab = new Composite(compositeSteps, SWT.NONE);
        compositeStepsTab.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout glStepsTab = new GridLayout(1, false);
        glStepsTab.marginWidth = 0;
        glStepsTab.marginHeight = 0;
        glStepsTab.marginBottom = 0;
        compositeStepsTab.setLayout(glStepsTab);

        createStepButtons(compositeStepsTab);

        Composite tableComposite = new Composite(compositeStepsTab, SWT.None);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        tableComposite.setLayout(layout);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        recordStepsView = new RecordedStepsView();
        recordStepsView.createContent(tableComposite);

        bottomTabFolder = new CTabFolder(compositeSteps, SWT.NONE);

        CTabItem variablesTabItem = new CTabItem(bottomTabFolder, SWT.NONE);
        variablesTabItem.setText("Variables");
        Composite variablesViewComposite = recordStepsView.createVariableTab(bottomTabFolder);
        variablesViewComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        variablesTabItem.setControl(variablesViewComposite);

        CTabItem logTabItem = new CTabItem(bottomTabFolder, SWT.NONE);
        logTabItem.setText("Logs");

        RecordedLogView logsView = new RecordedLogView();
        Composite logsViewComposite = logsView.createLogsView(bottomTabFolder);
        logTabItem.setControl(logsViewComposite);

        bottomTabFolder.setSelection(variablesTabItem);

        logTabItemIdex = bottomTabFolder.indexOf(logTabItem);

        compositeSteps.setWeights(new int[] { 60, 40 });

        IStylingEngine styleEngine = WorkbenchUtilizer.getService(IStylingEngine.class);
        styleEngine.setId(compositeSteps, "DefaultCTabFolder");
    }

    private ToolItem tltmAddStep, tltmRemoveStep, tltmUp, tltmDown, tltmRecent;

    private ToolItem tltmPlay;

    private boolean playingState;

    private void createStepButtons(Composite compositeSteps) {
        Composite compositeToolbars = new Composite(compositeSteps, SWT.NONE);
        compositeToolbars.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        compositeToolbars.setLayout(layout);

        ToolBar toolbar = new ToolBar(compositeToolbars, SWT.FLAT | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        toolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

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
        TestCaseMenuUtil.fillActionMenu(TreeTableMenuItemConstants.AddAction.Add, selectionListener, addMenu,
                new int[] { TreeTableMenuItemConstants.METHOD_MENU_ITEM_ID,
                        TreeTableMenuItemConstants.getBuildInKeywordID("Mobile"),
                        TreeTableMenuItemConstants.getBuildInKeywordID("WS") });

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
        
        new HelpCompositeForDialog(compositeToolbars, DocumentationMessageConstants.RECORDER_RUN_STEPS) {
            @Override
            protected GridData createGridData() {
                GridData gridData = new GridData(SWT.RIGHT, SWT.CENTER, true, true);
                gridData.widthHint = 32;
                return gridData;
            }
            
            @Override
            protected GridLayout createLayout() {
                GridLayout layout = new GridLayout();
                layout.marginHeight = 0;
                layout.marginBottom = 0;
                layout.marginWidth = 0;
                return layout;
            }
        };

        ToolBar playToolbar = new ToolBar(compositeToolbars, SWT.FLAT | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        GridLayout glPlayToolbar = new GridLayout(1, false);
        glPlayToolbar.marginWidth = 0;
        playToolbar.setLayout(glPlayToolbar);
        playToolbar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        tltmPlay = new ToolItem(playToolbar, SWT.DROP_DOWN);
        tltmPlay.setImage(ImageConstants.IMG_24_PLAY);
        tltmPlay.addSelectionListener(selectionListener);
        tltmPlay.setToolTipText(ComposerWebuiRecorderMessageConstants.DIA_ITEM_RUN_ALL_STEPS);

        //setPlayButtonState(false);
    }

    private void createRunScripContextMenu() {
        if (tltmPlay.getData() instanceof Menu) {
            ((Menu) tltmPlay.getData()).dispose();
        }
        Rectangle rect = tltmPlay.getBounds();

        ToolBar playToolbar = tltmPlay.getParent();
        Point pt = playToolbar.toDisplay(new Point(rect.x + rect.width, rect.y));

        Menu playMenu = new Menu(playToolbar);
        playMenu.setLocation(pt.x, pt.y + rect.height);
        playMenu.setVisible(true);
        playToolbar.setMenu(playMenu);

        tltmPlay.setData(playMenu);

        MenuItem runAllStepsItem = new MenuItem(playMenu, SWT.CHECK);
        runAllStepsItem
                .setText(ControlUtils.createMenuItemText(ComposerWebuiRecorderMessageConstants.DIA_ITEM_RUN_ALL_STEPS,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "E" })));
        runAllStepsItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                runAllSteps();
            }
        });
        runAllStepsItem.setSelection(true);

        MenuItem runSelectedSteps = new MenuItem(playMenu, SWT.PUSH);
        runSelectedSteps.setText(
                ControlUtils.createMenuItemText(ComposerWebuiRecorderMessageConstants.DIA_ITEM_RUN_SELECTED_STEPS,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, IKeyLookup.ALT_NAME, "E" })));
        runSelectedSteps.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                runSelectedSteps();
            }
        });
        if (recordStepsView.getTreeTable().getStructuredSelection().isEmpty()) {
            runSelectedSteps.setEnabled(false);
        }

        MenuItem runFromSelectedStep = new MenuItem(playMenu, SWT.PUSH);
        runFromSelectedStep.setText(ControlUtils.createMenuItemText(
                ComposerWebuiRecorderMessageConstants.DIA_ITEM_RUN_FROM_SELECTED_STEP,
                KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, IKeyLookup.SHIFT_NAME, "E" })));
        runFromSelectedStep.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                runFromStep();
            }
        });
        if (recordStepsView.getTreeTable().getStructuredSelection().isEmpty()) {
            runFromSelectedStep.setEnabled(false);
        }
    }

    private void runFromStep() {
        ScriptNodeWrapper cloneScript = recordStepsView.getWrapper().clone();
        cloneScript.setMainBlock(cloneScript.getBlock().clone());
        BlockStatementWrapper block = cloneScript.getBlock();
        block.clearStaments();
        for (ASTNodeWrapper node : recordStepsView.getTreeTableInput().getNodeWrappersFromFirstSelected()) {
            block.addChild(node.clone());
        }
        Trackings.trackRecordRunSteps("from");
        executeSelectedSteps(cloneScript);
    }

    private void runAllSteps() {
        Trackings.trackRecordRunSteps("all");
        executeSelectedSteps(recordStepsView.getWrapper());
    }

    private void runSelectedSteps() {
        ScriptNodeWrapper cloneScript = recordStepsView.getWrapper().clone();
        cloneScript.setMainBlock(cloneScript.getBlock().clone());
        BlockStatementWrapper block = cloneScript.getBlock();
        block.clearStaments();
        for (ASTNodeWrapper node : recordStepsView.getTreeTableInput().getSelectedNodeWrappers()) {
            block.addChild(node.clone());
        }
        Trackings.trackRecordRunSteps("selected");
        executeSelectedSteps(cloneScript);
    }

    public void setPlayButtonState(boolean sendingState) {
        this.playingState = sendingState;
        if (this.playingState) {
            tltmPlay.setText(StringConstants.STOP);
            tltmPlay.setImage(ImageConstants.IMG_24_STOP);
        } else {
            tltmPlay.setImage(ImageConstants.IMG_24_PLAY);
            tltmPlay.setText(StringConstants.RUN);
        }
        tltmPlay.getParent().getParent().layout(true, true);
    }

    public boolean getSendingState() {
        return playingState;
    }

    private void setRecentKeywordItemState() {
        tltmRecent.setEnabled(!TestCasePreferenceDefaultValueInitializer.getRecentKeywords().isEmpty());
    }

    public void performToolItemSelected(ToolItem toolItem, SelectionEvent selectionEvent) {
        getTreeTable().applyEditorValue();
        if (toolItem.equals(tltmAddStep)) {
            openToolItemForAddMenu(toolItem, selectionEvent);
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
            return;
        }
        if (toolItem.equals(tltmRecent)) {
            openRecentKeywordItems();
            return;
        }
        if (toolItem.equals(tltmPlay)) {
            openToolItemForRunMenu(toolItem, selectionEvent);
            return;
        }
    }

    private RecordingScriptGenerator generator;

    private boolean shouldResumeRecordingAfterRunning = false;

    private void executeSelectedSteps(ScriptNodeWrapper nodeWrapper) {
        if (playingState) {
            if (generator != null) {
                generator.stopLauncher();
            }
            setPlayButtonState(false);
            if (shouldResumeRecordingAfterRunning) {
                resume();
                shouldResumeRecordingAfterRunning = false;
            }
            return;
        }
        shouldResumeRecordingAfterRunning = !isPauseRecording;
        pause();
        setPlayButtonState(true);
        if (session == null || session.getWebDriver() == null || !session.isRunning()) {
            startBrowser();
        }

        Job job = new Job(ComposerWebuiRecorderMessageConstants.JOB_RUNNING_RECORDED_STEPS) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    while (session == null || session.getWebDriver() == null || !session.isRunning()) {
                        Thread.sleep(200L);
                    }
                    UISynchronizeService.syncExec(() -> {
                        pause();
                        bottomTabFolder.setSelection(logTabItemIdex);
                    });
                    // Saved captured object to temporary directory and put it in RunConfigration as a property
                    // to let the ObjectRepository.findTestObject can recognize captured objects instead of saved
                    // objects.
                    File recordSessionFolder = new File(ProjectController.getInstance().getTempDir(),
                            "record/" + RECORD_SESSION_ID);
                    recordSessionFolder.mkdirs();

                    File capturedObjectsFile = new File(recordSessionFolder, "captured_objects.json");
                    Map<String, TestObject> capturedObjectsCache = new HashMap<>();
                    for (WebElement we : capturedObjectComposite.flattenWebElements()) {
                        capturedObjectsCache.put(we.getScriptId(), WebElementUtils.buildTestObject(we));
                    }
                    FileUtils.write(capturedObjectsFile, JsonUtil.toJson(capturedObjectsCache));

                    // Generate script and execute
                    generator = new RecordingScriptGenerator(capturedObjectsFile.getAbsolutePath());
                    StringBuilder stringBuilder = new StringBuilder();

                    new GroovyWrapperParser(stringBuilder).parseGroovyAstIntoScript(nodeWrapper);
                    generator.execute(stringBuilder.toString(), Arrays.asList(recordStepsView.getVariables()),
                            session.getWebDriver(), session.getWebUiDriverType(),
                            ProjectController.getInstance().getCurrentProject());
                    return Status.OK_STATUS;
                } catch (Exception ex) {
                    UISynchronizeService.syncExec(() -> setPlayButtonState(false));
                    if (shouldResumeRecordingAfterRunning) {
                        resume();
                        shouldResumeRecordingAfterRunning = false;
                    }
                    return Status.CANCEL_STATUS;
                }
            }

        };
        job.schedule();
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

    private void openToolItemForAddMenu(ToolItem toolItem, SelectionEvent selectionEvent) {
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

    private void openToolItemForRunMenu(ToolItem toolItem, SelectionEvent selectionEvent) {
        if (selectionEvent.detail == SWT.ARROW) {
            createRunScripContextMenu();
        } else {
            executeSelectedSteps(recordStepsView.getWrapper());
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
        // set default browser
        selectedBrowser = getDefaultBrowser();
        
        toolBar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
        toolBar.setForeground(ColorUtil.getToolBarForegroundColor());
        toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        toolItemBrowserDropdown = new ToolItem(toolBar, SWT.DROP_DOWN);
        toolItemBrowserDropdown.setToolTipText(ComposerWebuiRecorderMessageConstants.DIA_TOOLTIP_START_RECORDING);
        toolItemBrowserDropdown.setImage(getWebUIDriverToolItemImage(getSelectedBrowserType()));
        Dropdown dropdown = new Dropdown(getShell());
        createDropdownContent(dropdown);
        
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

        tltmPauseAndResume = new ToolItem(toolBar, SWT.PUSH);
        tltmPauseAndResume.setToolTipText(ComposerWebuiRecorderMessageConstants.DIA_TOOLTIP_PAUSE_RECORDING);
        tltmPauseAndResume.setImage(ImageConstants.IMG_24_PAUSE_RECORDING);
        tltmPauseAndResume.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isPauseRecording) {
                    resume();
                } else {
                    pause();
                }
            }

        });
        tltmPauseAndResume.setEnabled(false);

        tltmStop = new ToolItem(toolBar, SWT.PUSH);
        tltmStop.setImage(ImageConstants.IMG_24_STOP_RECORDING);
        tltmStop.setToolTipText(ComposerWebuiRecorderMessageConstants.DIA_TOOLTIP_STOP_RECORDING);
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
        addActiveBrowserItem(activeBrowser, WebUIDriverType.FIREFOX_DRIVER);
        
        DropdownGroup customCapabilities = dropdown.addDropdownGroupItem(StringConstants.MENU_ITEM_CUSTOM_BROWSERS, null);
        addCustomCapabilitiesItem(customCapabilities);
        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            addNewBrowserItem(newBrowser, WebUIDriverType.IE_DRIVER);
        }
    }
    
    private void addCustomCapabilitiesItem(DropdownGroup customCapabilities) {
        boolean isAdded = false;

        for (CustomRunConfigurationContributor customRunConfigContributor : RunConfigurationCollector.getInstance()
                .getAllCustomRunConfigurationContributors()) {
            IDriverConnector driverConnector = getBrowser(customRunConfigContributor);
            customCapabilities.addItem(customRunConfigContributor.getId(), null,
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            if (driverConnector != null) {
                                changeBrowser(driverConnector);
                                startBrowser();
                            }
                        }
                    });
            isAdded = true;
        }
        if (!isAdded) {
            customCapabilities.addItem("Empty", null, new SelectionAdapter() {});
        }
    }
    
    private IDriverConnector getBrowser(CustomRunConfigurationContributor customRunConfigContributor) {
        IRunConfiguration runConfiguration = null;
        
        try {
            runConfiguration = (AbstractRunConfiguration) customRunConfigContributor
                    .getRunConfiguration(ProjectController.getInstance().getCurrentProject().getFolderLocation());      
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        } 
        
        return Optional.ofNullable(runConfiguration)
                .map(IRunConfiguration::getDriverConnectors)
                .map(driverConnectors -> driverConnectors.get(DriverFactory.WEB_UI_DRIVER_PROPERTY))
                .orElse(null);
    }

    private void addNewBrowserItem(DropdownGroup newBrowserGroup, WebUIDriverType webUIDriverType) {
        String itemText = webUIDriverType == WebUIDriverType.CHROME_DRIVER ? 
                webUIDriverType.toString() + StringConstants.RECOMMENDED_BROWSER_POSTFIX :
                webUIDriverType.toString();
                
        newBrowserGroup.addItem(itemText, getWebUIDriverDropdownImage(webUIDriverType),
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
                        if (StringUtils.isEmpty(url)) {
                            return;
                        }
                        Program.launch(url);
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
            if (!isPauseRecording) {
                recordStepsView.addSimpleKeyword("closeBrowser", false);
            }
            stopServer();
            stopRecordSession();
            closeInstantSession();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, e.getMessage());
        }
        resume();
        tltmPauseAndResume.setEnabled(false);
        tltmStop.setEnabled(false);
    }
  
    private void changeBrowser(final WebUIDriverType browserType) {
        changeBrowser(browserType, false);
    }
    
    private void changeBrowser(final WebUIDriverType browserType, final boolean isInstant) {     
        try {
            IDriverConnector driverConnector = WebUIExecutionUtil.getBrowserDriverConnector(browserType, ProjectController.getInstance().getCurrentProject().getFolderLocation());
            changeBrowser(driverConnector, isInstant);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, e.getMessage());
        }     
    }
    
    private void changeBrowser(IDriverConnector driverConnector) {
        changeBrowser(driverConnector, false);
    }

    private void changeBrowser(final IDriverConnector driverConnector, final boolean isInstant) {
        if (driverConnector == null) {
            return;
        }
        selectedBrowser = driverConnector;
        UISynchronizeService.getInstance().getSync().asyncExec(new Runnable() {
            @Override
            public void run() {
                // Set browser name into toolbar item label
                String label = RECORD_TOOL_ITEM_LABEL;
                if (isInstant) {
                    label = StringConstants.ACTIVE_BROWSER_PREFIX;
                }
                toolItemBrowserDropdown.setText(label);
                toolItemBrowserDropdown.setImage(getWebUIDriverToolItemImage(getSelectedBrowserType()));
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
                LoggerSingleton.logError(e);
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

        new HelpCompositeForDialog(bottomComposite, DocumentationMessageConstants.DIALOG_RECORDER_WEB_UI) {
            @Override
            protected GridData createGridData() {
                return new GridData(SWT.RIGHT, SWT.CENTER, true, false);
            }

            @Override
            protected GridLayout createLayout() {
                GridLayout layout = new GridLayout();
                layout.marginHeight = 0;
                layout.marginBottom = 0;
                layout.marginWidth = 0;
                return layout;
            }
        };
        
        super.createButtonBar(bottomComposite);

        return bottomComposite;
    }

    private List<WebPage> getCloneCapturedObjects(final List<WebPage> pages) {
        return pages.stream().map(page -> page.softClone()).collect(Collectors.toList());
    }

    @Override
    protected void okPressed() {
        Shell shell = getShell();
        int stepCount = countAllSteps();
        isOkPressed = true;
        try {
            if (!addElementToObjectRepository(shell)) {
                return;
            }
            super.okPressed();
            
            dispose();
            
            Trackings.trackCloseWebRecord("ok", stepCount, getWebLocatorConfig());
        } catch (Exception exception) {
            LoggerSingleton.logError(exception);
            MessageDialog.openError(shell, StringConstants.ERROR_TITLE, exception.getMessage());
        } 
    }
    
    private int countAllSteps() {
        return recordStepsView.getTreeTable().getTree().getItemCount();
//        return recordedActions.size();
    }

    @SuppressWarnings("unchecked")
    private boolean addElementToObjectRepository(Shell shell) throws Exception {
        TreeViewer capturedTreeViewer = capturedObjectComposite.getTreeViewer();
        if (capturedTreeViewer.getTree().getItemCount() == 0) {
            return true;
        }      
        
        SaveToObjectRepositoryDialog addToObjectRepositoryDialog = new SaveToObjectRepositoryDialog(shell, true,
                getCloneCapturedObjects((List<WebPage>) capturedTreeViewer.getInput()),
                capturedTreeViewer.getExpandedElements());
        if (addToObjectRepositoryDialog.open() != Window.OK) {
            return false;
        }

        targetFolderSelectionResult = addToObjectRepositoryDialog.getDialogResult();

        ObjectRepositoryService objectRepositoryService = new ObjectRepositoryService();
        refeshExplorer(objectRepositoryService.saveObject(targetFolderSelectionResult),
                addToObjectRepositoryDialog.getSelectedParentFolderResult());

        return true;
    }

    private void refeshExplorer(SaveActionResult saveResult, FolderTreeEntity selectedParentFolder) {
        // Refresh tree explorer
        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, selectedParentFolder);

        // Refesh updated object.
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
        boolean result = super.close();
        if (!isOkPressed) {
            try {
                Trackings.trackCloseWebRecord("cancel", 0, getWebLocatorConfig());
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
        }
        return result;
    }

    private void updateStore() {
        store.setValue(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_URL, txtStartUrl.getText());
        store.setValue(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_BROWSER, getSelectedBrowserType().toString());
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
        if (isPauseRecording || (newAction.getAction().equals(HTMLAction.Navigate)
                && (recordStepsView.getNodes().size() > 0 || (newAction.getData().length == 0
                        || String.valueOf(newAction.getData()[0].getValue()).equals("\"about:blank\""))))) {
            return;
        }
        WebElement targetElement = newAction.getTargetElement();
        if (targetElement != null) {
        	
        	if(isUsingIE == true){
        		targetElement.setSelectorMethod(SelectorMethod.BASIC);
        	}
            addNewElement(targetElement, newAction);
        }
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
                .append(elm1.getName(), elm2.getName())
                .isEquals();
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
            case EventConstants.WORKSPACE_CLOSED:
                cancelPressed();
                return;
            case EventConstants.WEBUI_VERIFICATION_EXECUTION_FINISHED:
                setPlayButtonState(false);

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ignored) {
                }
                if (shouldResumeRecordingAfterRunning && session.isRunning() && session.isDriverRunning()) {
                    resume();
                    shouldResumeRecordingAfterRunning = false;
                }
                return;
            case EventConstants.WEBUI_VERIFICATION_RUN_ALL_STEPS_CMD:
                runAllSteps();
                return;
            case EventConstants.WEBUI_VERIFICATION_RUN_SELECTED_STEPS_CMD:
                runSelectedSteps();
                return;
            case EventConstants.WEBUI_VERIFICATION_RUN_FROM_STEP_CMD:
                runFromStep();
                return;
        }
    }

    @Override
    protected void registerControlModifyListeners() {
        // Do nothing for this
    }

    @Override
    protected void setInput() {
        Boolean continueRecording = null;
        if (testCaseEntity != null && nodeWrappers.size() > 0) {
            MessageDialog dialog = new MessageDialog(getShell(), StringConstants.CONFIRMATION, null,
                    MessageFormat.format(ComposerWebuiRecorderMessageConstants.DIA_CONFIRM_CONTINUE_RECORDING,
                            testCaseEntity.getName()),
                    MessageDialog.CONFIRM, MessageDialog.OK,
                    ComposerWebuiRecorderMessageConstants.DIA_CONFIRM_ACCEPT_CONTINUE_RECORDING,
                    ComposerWebuiRecorderMessageConstants.DIA_CONFIRM_DECLINE_CONTINUE_RECORDING);
            if (dialog.open() != MessageDialog.OK) {
                variables = Collections.emptyList();
                nodeWrappers.clear();
                continueRecording = false;
            } else {
                continueRecording = true;
            }
        }
        recordStepsView.addVariables(variables.toArray(new VariableEntity[variables.size()]));

        Map<String, List<RecordedElementMethodCallWrapper>> keywordNodeMaps = getTestObjectReferences(nodeWrappers);

        Map<String, WebElement> webElementIndex = new HashMap<>();
        Map<String, WebPage> pageIndex = new HashMap<>();

        List<WebElementEntity> webElementEntities = keywordNodeMaps.keySet().stream().map(testObjectId -> {
            try {
                return ObjectRepositoryController.getInstance().getWebElementByDisplayPk(testObjectId);
            } catch (Exception ex) {
                return null;
            }
        }).filter(we -> we != null).collect(Collectors.toList());
        for (WebElementEntity entity : webElementEntities) {
            FolderEntity folder = entity.getParentFolder();
            WebPage webPage = pageIndex.getOrDefault(folder.getIdForDisplay(), null);
            if (webPage == null) {
                webPage = new WebPage(folder.getName());
                webPage.setFolderAlias(folder);
                pageIndex.put(folder.getIdForDisplay(), webPage);
            }
            WebElement we = webElementIndex.getOrDefault(entity.getIdForDisplay(), null);
            if (we == null) {
                we = WebElementUtils.createWebElementFromTestObject(entity, false, webPage, webElementIndex);
            }
            
            for (RecordedElementMethodCallWrapper refNode : keywordNodeMaps.get(entity.getIdForDisplay())) {
                refNode.setWebElement(we);
            }
        }

        recordStepsView.getTreeTableInput().addNewAstObjects(nodeWrappers, null, NodeAddType.Add);
        recordStepsView.getTreeTableInput().refresh();

        List<WebPage> allPages = pageIndex.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
        elements = new ArrayList<>();
        elements.addAll(allPages);
        capturedObjectComposite.setInput(elements);
        capturedObjectComposite.refreshTree(null);
        
        try {
            Trackings.trackOpenWebRecord(continueRecording, getWebLocatorConfig());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }
    
    private SelectorMethod getWebLocatorConfig() throws IOException {
        WebUiExecutionSettingStore webUiSettingStore = WebUiExecutionSettingStore.getStore();
        return webUiSettingStore.getCapturedTestObjectSelectorMethod();
    }

    private Map<String, List<RecordedElementMethodCallWrapper>> getTestObjectReferences(ASTNodeWrapper wrapper) {
        if (wrapper instanceof RecordedElementMethodCallWrapper) {
            return Collections.emptyMap();
        }
        Map<String, List<RecordedElementMethodCallWrapper>> keywordNodeIndex = new HashMap<>();
        if (wrapper.hasAstChildren() && wrapper.getAstChildren() != null) {
            merge(keywordNodeIndex, getTestObjectReferences(wrapper.getAstChildren()));
        }
        if (wrapper.getInput() instanceof MethodCallExpressionWrapper) {
            MethodCallExpressionWrapper methodCall = (MethodCallExpressionWrapper) wrapper.getInput();
            if (!methodCall.isFindTestObjectMethodCall()) {
                return keywordNodeIndex;
            }
            String testObjectId = AstEntityInputUtil.getEntityRelativeIdFromMethodCall(methodCall);

            try {
                testObjectId = testObjectId.startsWith("Object Repository/") ? testObjectId
                        : "Object Repository/" + testObjectId;
                WebElementEntity entity = ObjectRepositoryController.getInstance()
                        .getWebElementByDisplayPk(testObjectId);
                if (entity != null) {
                    List<RecordedElementMethodCallWrapper> refNodes = keywordNodeIndex
                            .getOrDefault(entity.getIdForDisplay(), null);
                    if (refNodes == null) {
                        refNodes = new ArrayList<>();
                    }
                    RecordedElementMethodCallWrapper newWrapper = new RecordedElementMethodCallWrapper(
                            wrapper.getParent(), null);

                    wrapper.getParent().replaceChild(wrapper, newWrapper);
                    refNodes.add(newWrapper);
                    keywordNodeIndex.put(entity.getIdForDisplay(), refNodes);
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        } else if (wrapper.getInput() instanceof ArgumentListExpressionWrapper) {
            ArgumentListExpressionWrapper argumentList = (ArgumentListExpressionWrapper) wrapper.getInput();
            merge(keywordNodeIndex, getTestObjectReferences(argumentList.getAstChildren()));
        }
        return keywordNodeIndex;

    }

    private void merge(Map<String, List<RecordedElementMethodCallWrapper>> keywordNodeIndex,
            Map<String, List<RecordedElementMethodCallWrapper>> childNodeMap) {
        childNodeMap.entrySet().stream().forEach(e -> {
            String objectId = e.getKey();
            List<RecordedElementMethodCallWrapper> newAstNodes = e.getValue();
            if (keywordNodeIndex.containsKey(objectId)) {
                List<RecordedElementMethodCallWrapper> currentAstNodes = keywordNodeIndex.get(objectId);
                currentAstNodes.addAll(newAstNodes);
                keywordNodeIndex.put(objectId, currentAstNodes);
            } else {
                keywordNodeIndex.put(objectId, newAstNodes);
            }
        });
    }

    private Map<String, List<RecordedElementMethodCallWrapper>> getTestObjectReferences(
            List<? extends ASTNodeWrapper> nodes) {
        Map<String, List<RecordedElementMethodCallWrapper>> keywordNodeIndex = new HashMap<>();
        for (ASTNodeWrapper wrapper : nodes) {
            Map<String, List<RecordedElementMethodCallWrapper>> childNodeMap = getTestObjectReferences(wrapper);
            merge(keywordNodeIndex, childNodeMap);
        }
        return keywordNodeIndex;
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

    public VariableEntity[] getVariables() {
        return recordStepsView.getVariables();
    }
    
    private WebUIDriverType getSelectedBrowserType() {
        if (selectedBrowser == null || !(selectedBrowser.getDriverType() instanceof WebUIDriverType)) {
            return null;
        }
        return (WebUIDriverType) selectedBrowser.getDriverType();
    }

}
