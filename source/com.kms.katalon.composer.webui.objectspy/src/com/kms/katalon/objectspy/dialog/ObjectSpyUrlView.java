package com.kms.katalon.objectspy.dialog;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.BindException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.greenrobot.eventbus.EventBus;
import org.openqa.selenium.WebDriver;
import org.osgi.framework.Bundle;

import com.kms.katalon.application.usagetracking.TrackingEvent;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.control.Dropdown;
import com.kms.katalon.composer.components.impl.control.DropdownGroup;
import com.kms.katalon.composer.components.impl.control.DropdownItemSelectionListener;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.event.EventBusSingleton;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.core.HTMLElementCaptureServer;
import com.kms.katalon.objectspy.core.HTMLElementCollector;
import com.kms.katalon.objectspy.core.InspectSession;
import com.kms.katalon.objectspy.exception.IEAddonNotInstalledException;
import com.kms.katalon.objectspy.util.BrowserUtil;
import com.kms.katalon.objectspy.util.UtilitiesAddonUtil;
import com.kms.katalon.objectspy.util.Win32Helper;
import com.kms.katalon.objectspy.util.WinRegistry;
import com.kms.katalon.objectspy.websocket.AddonSocket;
import com.kms.katalon.objectspy.websocket.AddonSocketServer;
import com.kms.katalon.objectspy.websocket.messages.StartInspectAddonMessage;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.listener.EventListener;
import com.kms.katalon.util.listener.EventManager;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

@SuppressWarnings("restriction")
public class ObjectSpyUrlView implements EventManager<ObjectSpyEvent> {
    private static Logger logger = LoggerSingleton.getInstance().getLogger();

    private static final String IE_WINDOW_CLASS = "IEFrame"; //$NON-NLS-1$

    private static final String relativePathToIEAddonSetup = File.separator + "extensions" + File.separator + "IE" //$NON-NLS-1$ //$NON-NLS-2$
            + File.separator + "Object Spy" + File.separator + "setup.exe"; //$NON-NLS-1$ //$NON-NLS-2$

    private static final String RESOURCES_FOLDER_NAME = "resources"; //$NON-NLS-1$

    private static final String IE_ADDON_BHO_KEY = "{8CB0FB3A-8EFA-4F94-B605-F3427688F8C7}"; //$NON-NLS-1$

    public static final String IE_WINDOWS_32BIT_BHO_REGISTRY_KEY = "SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\explorer\\Browser Helper Objects"; //$NON-NLS-1$

    public static final String IE_WINDOWS_BHO_REGISTRY_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\explorer\\Browser Helper Objects"; //$NON-NLS-1$

    public static final String OBJECT_SPY_CHROME_ADDON_URL = "https://chrome.google.com/webstore/detail/katalon-utilities/ljdobmomdgdljniojadhoplhkpialdid"; //$NON-NLS-1$

    public static final String OBJECT_SPY_FIREFOX_ADDON_URL = "https://addons.mozilla.org/en-US/firefox/addon/katalon-object-spy"; //$NON-NLS-1$

    private Text txtStartUrl;

    private ToolItem startBrowser;

    private WebUIDriverType defaultBrowser;

    private boolean isInstant = false;

    private Shell shell;

    private HTMLElementCaptureServer server;

    private InspectSession session;

    private AddonSocket currentInstantSocket;

    private HTMLElementCollector elementCollector;

    private Map<ObjectSpyEvent, Set<EventListener<ObjectSpyEvent>>> eventListeners = new HashMap<>();

    public ObjectSpyUrlView(HTMLElementCollector parent) {
        this.elementCollector = parent;

        // set default browser
        defaultBrowser = getWebUIDriver();
        isInstant = getBrowserActiveFlag();
    }

    public void addStartBrowserToolbar(Composite parentComposite) {
        shell = parentComposite.getShell();

        Composite toolbarRightSideComposite = new Composite(parentComposite, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        toolbarRightSideComposite.setLayout(layout);
        toolbarRightSideComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label lblStartUrl = new Label(toolbarRightSideComposite, SWT.NONE);
        lblStartUrl.setText(ObjectspyMessageConstants.LBL_DLG_START_URL);
        txtStartUrl = new Text(toolbarRightSideComposite, SWT.BORDER);
        GridData gdTxtStartUrl = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdTxtStartUrl.widthHint = 100;
        txtStartUrl.setLayoutData(gdTxtStartUrl);
        txtStartUrl.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.character == SWT.CR) {
                    spy(defaultBrowser);
                }
            }
        });
        txtStartUrl.setText(
                getPreferenceStore().getString(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_STARTING_URL));

        final ToolBar startBrowserToolbar = new ToolBar(toolbarRightSideComposite, SWT.FLAT | SWT.RIGHT);
        startBrowserToolbar.setLayout(new FillLayout(SWT.HORIZONTAL));
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(false, false).applyTo(startBrowserToolbar);
        startBrowser = new ToolItem(startBrowserToolbar, SWT.DROP_DOWN);
        startBrowser.setText(getBrowserToolItemName());
        startBrowser.setImage(getWebUIDriverToolItemImage(getWebUIDriver()));
        Dropdown dropdown = new Dropdown(shell);
        createDropdownContent(dropdown);

        startBrowser.addSelectionListener(new DropdownItemSelectionListener(dropdown) {

            @Override
            public void itemSelected(SelectionEvent event) {
                spy(defaultBrowser);
            }
        });

        txtStartUrl.setFocus();
        txtStartUrl.selectAll();
    }

    private void spy(WebUIDriverType webUIDriverType) {
        if (isInstant) {
            spyInActiveBrowser(webUIDriverType);
        } else {
            spyInNewBrowser(webUIDriverType);
        }
    }

    private void startObjectSpy(WebUIDriverType browser, boolean isInstant) {
        if (!BrowserUtil.isBrowserInstalled(browser)) {
            MessageDialog.openError(shell, StringConstants.ERROR_TITLE,
                    ObjectspyMessageConstants.DIA_MSG_CANNOT_START_BROWSER);
            return;
        }
        try {
            if (browser == WebUIDriverType.IE_DRIVER) {
                checkIEAddon();
            }
            changeBrowserToolItemName();
            if (isInstant) {
                startInstantSession(browser);
                return;
            }
            startServerWithPort(0);
            startInspectSession(browser);

            invoke(ObjectSpyEvent.SELENIUM_SESSION_STARTED, session);
//            sendEventForTracking();
            Trackings.trackSpy("web");
        } catch (final IEAddonNotInstalledException e) {
            stop();
            showMessageForMissingIEAddon();
            try {
                shell.setMinimized(true);
                runIEAddonInstaller();
            } catch (IOException iOException) {
                LoggerSingleton.logError(iOException);
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            MessageDialog.openError(shell, StringConstants.ERROR_TITLE, ex.getMessage());
        }
    }

    private void startBrowser() {
        startObjectSpy(defaultBrowser, isInstant);
    }
    
    private void sendEventForTracking() {
        EventBus eventBus = EventBusSingleton.getInstance().getEventBus();
        eventBus.post(new TrackingEvent(UsageActionTrigger.SPY, "web"));
    }

    public void startServerWithPort(int port) throws Exception {
        closeInstantSession();
        if (server != null && server.isStarted() && isCurrentServerPortUsable(port)) {
            return;
        }
        if (server != null && server.isRunning()) {
            server.stop();
        }
        try {
            server = new HTMLElementCaptureServer(port, logger, elementCollector);
            server.start();
        } catch (BindException e) {
            MessageDialog.openError(shell, StringConstants.ERROR_TITLE,
                    MessageFormat.format(ObjectspyMessageConstants.ERR_DLG_OBJECT_SPY_PORT_IN_USE, port));
            server = null;
        }
    }

    public boolean isCurrentServerPortUsable(int port) {
        return port == 0 || port == server.getServerPort();
    }

    private void changeBrowserToolItemName() {
        String string = getBrowserToolItemName();
        changeBrowserName(string);
    }

    private String getBrowserToolItemName() {
        String string = StringConstants.DIA_BTN_START_BROWSER;
        if (isInstant) {
            string = StringConstants.ACTIVE_BROWSER_PREFIX;
        }
        return string;
    }

    private void changeBrowserName(final String string) {
        UISynchronizeService.getInstance().getSync().asyncExec(new Runnable() {
            @Override
            public void run() {
                // Set browser name into toolbar item label
                startBrowser.setText(string);
                // reload layout
                startBrowser.getParent().getParent().layout(true, true);
            }
        });
    }

    private void checkIEAddon() throws IllegalAccessException, InvocationTargetException, IEAddonNotInstalledException {
        if (checkRegistryKey(IE_WINDOWS_32BIT_BHO_REGISTRY_KEY) || checkRegistryKey(IE_WINDOWS_BHO_REGISTRY_KEY)) {
            return;
        }
        throw new IEAddonNotInstalledException(InspectSession.OBJECT_SPY_ADD_ON_NAME);
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
                MessageDialog.openInformation(shell, StringConstants.INFO,
                        StringConstants.DIALOG_CANNOT_START_IE_MESSAGE);
            }
        });
    }

    private void closeInstantSession() {
        if (currentInstantSocket != null && currentInstantSocket.isConnected()) {
            currentInstantSocket.close();
        }
        currentInstantSocket = null;
    }

    private void createDropdownContent(Dropdown dropdown) {
        DropdownGroup newBrowser = dropdown.addDropdownGroupItem(StringConstants.MENU_ITEM_NEW_BROWSERS,
                ImageConstants.IMG_16_NEW_BROWSER);
        addNewBrowserItem(newBrowser, WebUIDriverType.FIREFOX_DRIVER);
        addNewBrowserItem(newBrowser, WebUIDriverType.CHROME_DRIVER);

        DropdownGroup activeBrowser = dropdown.addDropdownGroupItem(StringConstants.MENU_ITEM_ACTIVE_BROWSERS,
                ImageConstants.IMG_16_ACTIVE_BROWSER);
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
                        spyInNewBrowser(webUIDriverType);
                    }
                });
    }

    private void spyInNewBrowser(WebUIDriverType webUIDriverType) {
        isInstant = false;
        defaultBrowser = webUIDriverType;
        startBrowser.setImage(getWebUIDriverToolItemImage(webUIDriverType));
        startBrowser();
    }

    private void addActiveBrowserItem(DropdownGroup activeBrowserGroup, WebUIDriverType webUIDriverType) {
        activeBrowserGroup.addItem(webUIDriverType.toString(), getWebUIDriverDropdownImage(webUIDriverType),
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        spyInActiveBrowser(webUIDriverType);
                    }
                });
    }

    private void spyInActiveBrowser(WebUIDriverType webUIDriverType) {
        try {
            if (!UtilitiesAddonUtil.isNotShowingInstantBrowserDialog() && !showInstantBrowserDialog(webUIDriverType)) {
                return;
            }
            endInspectSession();
            defaultBrowser = webUIDriverType;
            startBrowser.setImage(getWebUIDriverToolItemImage(webUIDriverType));
            isInstant = true;
            startBrowser();
        } catch (Exception exception) {
            LoggerSingleton.logError(exception);
        }
    }

    private boolean showInstantBrowserDialog(WebUIDriverType webUIDriverType) throws IOException, URISyntaxException {
        if (webUIDriverType == WebUIDriverType.IE_DRIVER) {
            showMessageForStartingInstantIE();
            return true;
        }
        MessageDialogWithToggle messageDialogWithToggle = new GoToAddonStoreMessageDialog(shell,
                StringConstants.HAND_ACTIVE_BROWSERS_DIA_TITLE,
                MessageFormat.format(StringConstants.HAND_ACTIVE_BROWSERS_DIA_MESSAGE, webUIDriverType.toString()),
                StringConstants.HAND_INSTANT_BROWSERS_DIA_TOOGLE_MESSAGE);
        int returnCode = messageDialogWithToggle.open();
        UtilitiesAddonUtil.setNotShowingInstantBrowserDialog(messageDialogWithToggle.getToggleState());
        if (returnCode == IDialogConstants.NO_ID) {
            return true;
        }
        if (returnCode != IDialogConstants.YES_ID) {
            return false;
        }
        openBrowserToAddonUrl(webUIDriverType);
        return true;
    }

    private void openBrowserToAddonUrl(WebUIDriverType webUIDriverType) throws IOException, URISyntaxException {
        String url = getAddonUrl(webUIDriverType);
        if (url == null || !Desktop.isDesktopSupported()) {
            return;
        }
        Desktop.getDesktop().browse(new URI(url));
    }

    protected void showMessageForStartingInstantIE() {
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialogWithToggle messageDialogWithToggle = MessageDialogWithToggle.openInformation(
                        Display.getCurrent().getActiveShell(), StringConstants.HAND_ACTIVE_BROWSERS_DIA_TITLE,
                        StringConstants.DIALOG_RUNNING_INSTANT_IE_MESSAGE,
                        StringConstants.HAND_INSTANT_BROWSERS_DIA_TOOGLE_MESSAGE, false, null, null);
                UtilitiesAddonUtil.setNotShowingInstantBrowserDialog(messageDialogWithToggle.getToggleState());
            }
        });
    }

    private String getAddonUrl(WebUIDriverType webUIDriverType) {
        if (webUIDriverType == WebUIDriverType.CHROME_DRIVER) {
            return OBJECT_SPY_CHROME_ADDON_URL;
        }
        if (webUIDriverType == WebUIDriverType.FIREFOX_DRIVER) {
            return OBJECT_SPY_FIREFOX_ADDON_URL;
        }
        return null;
    }

    private WebUIDriverType getWebUIDriver() {
        String defaultBrowserValue = getPreferenceStore()
                .getString(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_BROWSER);
        if (StringUtils.isEmpty(defaultBrowserValue)) {
            return WebUIDriverType.CHROME_DRIVER;
        } else {
            return WebUIDriverType.fromStringValue(defaultBrowserValue);
        }
    }

    private boolean getBrowserActiveFlag() {
        boolean isActive = getPreferenceStore().getBoolean(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_BROWSER_ACTIVE);
        return isActive;
    }

    private Image getWebUIDriverDropdownImage(WebUIDriverType webUIDriverType) {
        switch (webUIDriverType) {
            case FIREFOX_DRIVER:
                return ImageConstants.IMG_16_BROWSER_FIREFOX;

            case CHROME_DRIVER:
                return ImageConstants.IMG_16_BROWSER_CHROME;

            case IE_DRIVER:
                return ImageConstants.IMG_16_BROWSER_IE;

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

    private void endInspectSession() {
        if (session != null && session.isRunning()) {
            session.stop();
        }
    }

    private void startInspectSession(WebUIDriverType browser) throws Exception {
        if (session != null) {
            session.stop();
        }
        session = new InspectSession(server, browser, ProjectController.getInstance().getCurrentProject(), logger,
                txtStartUrl.getText());
        new Thread(session).start();
    }

    public void stop() {
        if (server != null && server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        endInspectSession();
        AddonSocketServer.getInstance().stop();
    }

    private void startInstantSession(WebUIDriverType browser) throws Exception {
        if (browser == WebUIDriverType.IE_DRIVER) {
            runInstantIE();
        }
        currentInstantSocket = AddonSocketServer.getInstance().getAddonSocketByBrowserName(browser.toString());
        if (currentInstantSocket == null) {
            return;
        }
        Win32Helper.switchFocusToBrowser(browser);
        currentInstantSocket.sendMessage(new StartInspectAddonMessage());
        invoke(ObjectSpyEvent.ADDON_SESSION_STARTED, currentInstantSocket);
//        sendEventForTracking();
        Trackings.trackSpy("web");
    }

    protected void runInstantIE() throws Exception {
        session = new InspectSession(server, WebUIDriverType.IE_DRIVER,
                ProjectController.getInstance().getCurrentProject(), logger);
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

    private void runIEAddonInstaller() throws IOException {
        String ieAddonSetupPath = getResourcesDirectory().getAbsolutePath() + relativePathToIEAddonSetup;
        Desktop desktop = Desktop.getDesktop();
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        desktop.open(new File(ieAddonSetupPath));
    }

    private File getResourcesDirectory() throws IOException {
        Bundle bundleExec = Platform.getBundle(IdConstants.KATALON_WEB_UI_OBJECT_SPY_BUNDLE_ID);
        File bundleFile = FileLocator.getBundleFile(bundleExec);
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(bundleFile + File.separator + RESOURCES_FOLDER_NAME);
        }
        // run as product
        return new File(ClassPathResolver.getConfigurationFolder() + File.separator + RESOURCES_FOLDER_NAME);
    }

    private ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER);
    }

    public void save() {
        ScopedPreferenceStore preferenceStore = getPreferenceStore();
        preferenceStore.setValue(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_STARTING_URL,
                txtStartUrl.getText());
        preferenceStore.setValue(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_BROWSER,
                defaultBrowser.toString());
        preferenceStore.setValue(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_BROWSER_ACTIVE, isInstant);

        try {
            preferenceStore.save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    public boolean isInstant() {
        return isInstant;
    }

    public WebUIDriverType getDefaultBrowser() {
        return defaultBrowser;
    }

    public AddonSocket getCurrentInstanceSocket() {
        return currentInstantSocket;
    }

    public WebDriver getWebDriver() {
        if (session == null) {
            return null;
        }
        return session.getWebDriver();
    }

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
