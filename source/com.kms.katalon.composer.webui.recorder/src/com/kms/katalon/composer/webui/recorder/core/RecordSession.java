package com.kms.katalon.composer.webui.recorder.core;

import java.io.File;

import org.eclipse.e4.core.services.log.Logger;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.objectspy.core.HTMLElementCaptureServer;
import com.kms.katalon.objectspy.core.InspectSession;
import com.kms.katalon.objectspy.websocket.AddonCommand;
import com.kms.katalon.objectspy.websocket.AddonSocket;
import com.kms.katalon.objectspy.websocket.AddonSocketServer;
import com.kms.katalon.objectspy.websocket.messages.AddonMessage;

@SuppressWarnings("restriction")
public class RecordSession extends InspectSession {
    public static final String RECORDER_ADDON_NAME = "Recorder";

    private static final String CHROME_EXTENSION_RELATIVE_PATH = File.separator + "Chrome" + File.separator
            + RECORDER_ADDON_NAME;

    private static final String FIREFOX_ADDON_RELATIVE_PATH = File.separator + "Firefox" + File.separator
            + "recorder.xpi";

    private static final String RECORDER_APPLICATION_DATA_FOLDER = System.getProperty("user.home") + File.separator
            + "AppData" + File.separator + "Local" + File.separator + "KMS" + File.separator + "qAutomate"
            + File.separator + RECORDER_ADDON_NAME;

    public RecordSession(HTMLElementCaptureServer server, WebUIDriverType webUiDriverType, ProjectEntity currentProject,
            Logger logger) throws Exception {
        super(server, webUiDriverType, currentProject, logger);
    }

    public RecordSession(HTMLElementRecorderServer server, WebUIDriverType webUiDriverType,
            ProjectEntity currentProject, Logger logger, String startUrl) {
        super(server, webUiDriverType, currentProject, logger, startUrl);
    }

    protected String getChromeExtensionPath() {
        return CHROME_EXTENSION_RELATIVE_PATH;
    }

    protected String getFirefoxExtensionPath() {
        return FIREFOX_ADDON_RELATIVE_PATH;
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
        firefoxAddonSocket.sendMessage(new AddonMessage(AddonCommand.START_RECORD));
    }
}
