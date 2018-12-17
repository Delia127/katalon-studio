package com.kms.katalon.composer.webui.recorder.core;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.openqa.selenium.WebDriverException;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.objectspy.core.HTMLElementCaptureServer;
import com.kms.katalon.objectspy.core.InspectSession;
import com.kms.katalon.objectspy.util.FileUtil;
import com.kms.katalon.objectspy.websocket.AddonCommand;
import com.kms.katalon.objectspy.websocket.AddonSocket;
import com.kms.katalon.objectspy.websocket.AddonSocketServer;
import com.kms.katalon.objectspy.websocket.messages.AddonMessage;

public class RecordSession extends InspectSession {
    public static final String RECORDER_ADDON_NAME = "Recorder";

    private static final String CHROME_EXTENSION_RELATIVE_PATH = File.separator + "Chrome" + File.separator
            + RECORDER_ADDON_NAME + File.separator + "KR";

    private static final String RECORDER_APPLICATION_DATA_FOLDER = System.getProperty("user.home") + File.separator
            + "AppData" + File.separator + "Local" + File.separator + "KMS" + File.separator + "qAutomate"
            + File.separator + RECORDER_ADDON_NAME;

    public RecordSession(HTMLElementCaptureServer server, IDriverConnector driverConnector) throws Exception {
        super(server, driverConnector);
    }
    
    public RecordSession(HTMLElementRecorderServer server, IDriverConnector driverConnector, String startUrl) {
        super(server, driverConnector, startUrl);
    }

    protected String getChromeExtensionPath() {
    	return CHROME_EXTENSION_RELATIVE_PATH;
    }     
    
    @Override
    protected File getChromeExtensionFile() throws IOException {
        File chromeExtension = null;
        File extensionFolder = FileUtil.getExtensionsDirectory(FrameworkUtil.getBundle(RecordSession.class));
        if (extensionFolder.exists() && extensionFolder.isDirectory()) {
            chromeExtension = new File(extensionFolder.getAbsolutePath() + getChromeExtensionPath());
        }
        return chromeExtension;
    }

    @Override
    protected String getAddOnName() {
        return RECORDER_ADDON_NAME;
    }

    @Override
    protected String getIEApplicationDataFolder() {
        return RECORDER_APPLICATION_DATA_FOLDER;
    }
    
    @Override
    protected void handleForFirefoxAddon() throws InterruptedException {
        final AddonSocketServer socketServer = AddonSocketServer.getInstance();
        while (socketServer.getAddonSocketByBrowserName(webUiDriverType.toString()) == null && isRunFlag) {
            // wait for web socket to connect
            Thread.sleep(500);
        }
        final AddonSocket firefoxAddonSocket = socketServer
                .getAddonSocketByBrowserName(webUiDriverType.toString());
        if (firefoxAddonSocket != null) {
            firefoxAddonSocket.sendMessage(new AddonMessage(AddonCommand.START_RECORD));
        }
    }

    public interface BrowserStoppedListener {
        void onBrowserStopped();
    }
    
    @Override
    public void run() {
        super.run();
        handleBrowserStopped();
    }

    private Set<BrowserStoppedListener> browserStoppedListeners = new LinkedHashSet<>();
    private void handleBrowserStopped() {
        browserStoppedListeners.parallelStream().forEach(l -> l.onBrowserStopped());
    }
    
    public void addBrowserStoppedListener(BrowserStoppedListener listener) {
        browserStoppedListeners.add(listener);
    }

    public boolean isDriverRunning() {
        try {
            getWebDriver().getWindowHandle();
            return true;
        } catch (WebDriverException e) {
            return false;
        }
    }
}
