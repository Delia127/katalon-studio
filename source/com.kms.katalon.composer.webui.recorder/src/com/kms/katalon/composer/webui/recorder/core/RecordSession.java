package com.kms.katalon.composer.webui.recorder.core;

import java.io.File;

import org.eclipse.e4.core.services.log.Logger;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.objectspy.core.HTMLElementCaptureServer;
import com.kms.katalon.objectspy.core.InspectSession;

@SuppressWarnings("restriction")
public class RecordSession extends InspectSession {
	private static final String CHROME_EXTENSION_RELATIVE_PATH = File.separator + "Chrome" + File.separator
			+ "Recorder";
	private static final String FIREFOX_ADDON_RELATIVE_PATH = File.separator + "Firefox" + File.separator
			+ "recorder.xpi";
	
	private static final String IE_ADDON_BHO_KEY = "{FEA8CA38-7979-4F6A-83E4-2949EDEA96EF}";
	
	private static final String RECORDER_APPLICATION_DATA_FOLDER = System.getProperty("user.home") + File.separator + "AppData" + File.separator
			+ "Local" + File.separator + "KMS" + File.separator + "qAutomate" + File.separator + "Recorder";

	public RecordSession(HTMLElementCaptureServer server, WebUIDriverType webUiDriverType, ProjectEntity currentProject, Logger logger) throws Exception {
		super(server, webUiDriverType, currentProject, logger);
	}
	
	protected String getChromeExtensionPath() {
		return CHROME_EXTENSION_RELATIVE_PATH;
	}

	protected String getFirefoxExtensionPath() {
		return FIREFOX_ADDON_RELATIVE_PATH;
	}

	protected String getIEAddonRegistryKey() {
		return IE_ADDON_BHO_KEY;
	}
	
	@Override
	protected String getAddOnName() {
		return "Recorder";
	}
	
	@Override
	protected String getIEApplicationDataFolder() {
		return RECORDER_APPLICATION_DATA_FOLDER;
	}
}
