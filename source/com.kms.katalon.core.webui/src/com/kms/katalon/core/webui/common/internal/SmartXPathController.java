package com.kms.katalon.core.webui.common.internal;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.testobject.TestObjectXpath;
import com.kms.katalon.core.webui.common.WebUiCommonHelper;
import com.kms.katalon.core.webui.constants.StringConstants;

/**
 * A controller used by Smart XPath Plug-in.
 *
 */
public class SmartXPathController {
	/**
	 * Register a Test Object as broken, register this information along with a
	 * proposed XPath to an internal file provided by Smart Path Plug-in.
	 * 
	 * @param testObject
	 *            The broken Test Object to be registered
	 * @param thisXPath
	 *            The proposed XPath for the broken Test Object
	 */
	public static void registerBrokenTestObject(TestObject testObject, TestObjectXpath thisXPath) {
		String jsAutoHealingPath = getSmartXPathInternalFilePath();
		BrokenTestObject brokenTestObject = buildBrokenTestObject(testObject, thisXPath.getValue());
		BrokenTestObjects existingBrokenTestObjects = readExistingBrokenTestObjects(jsAutoHealingPath);
		if (existingBrokenTestObjects != null) {
			existingBrokenTestObjects.getBrokenTestObjects().add(brokenTestObject);
			writeBrokenTestObjects(existingBrokenTestObjects, jsAutoHealingPath);
		} else {
			KeywordLogger.getInstance(SmartXPathController.class)
					.logError(jsAutoHealingPath + " does not exist or is provided by Smart XPath Plugin!");
		}
	}

	private static void writeBrokenTestObjects(BrokenTestObjects brokenTestObjects, String filePath) {
		try {
			Writer writer = new FileWriter(filePath);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(brokenTestObjects, writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			KeywordLogger.getInstance(WebUiCommonHelper.class).logError(e.getMessage());
		}
	}

	private static BrokenTestObjects readExistingBrokenTestObjects(String filePath) {
		try {
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new FileReader(filePath));
			return gson.fromJson(reader, BrokenTestObjects.class);
		} catch (Exception e) {
			KeywordLogger.getInstance(WebUiCommonHelper.class).logError(e.getMessage());
		}
		return null;
	}

	private static BrokenTestObject buildBrokenTestObject(TestObject testObject, String newXPath) {
		String oldXPath = testObject.getSelectorCollection().get(testObject.getSelectorMethod());
		BrokenTestObject brokenTestObject = new BrokenTestObject();
		brokenTestObject.setTestObjectId(testObject.getObjectId());
		brokenTestObject.setApproved(false);
		brokenTestObject.setBrokenXPath(oldXPath);
		brokenTestObject.setProposedXPath(newXPath);
		return brokenTestObject;
	}

	public static String getSmartXPathFolderPath() {
		return RunConfiguration.getProjectDir() + "/Reports/smart_xpath";
	}

	public static String getSmartXPathInternalFilePath() {
		return RunConfiguration.getProjectDir() + "/Reports/smart_xpath/waiting-for-approval.json";
	}

	/**
	 * Take screenshot of a web element and saved to an internal folder provided
	 * by Smart XPath Plug-in
	 * 
	 * @param webDriver
	 *            A WebDriver instance that's being used at the time calling
	 *            this function
	 * @param ele
	 *            The web element to be taken screenshot of
	 * @param name
	 * 			  Name of the screenshot
	 */
	public static void takeScreenShot(WebDriver webDriver, WebElement ele, String name) {
		String smartXPathFolder = getSmartXPathFolderPath();
		try {
			String fullPath = smartXPathFolder + "/" + name;
			fullPath = WebUiCommonHelper.saveWebElementScreenshot(webDriver, ele, name, smartXPathFolder);
			KeywordLogger.getInstance(WebUiCommonHelper.class).logInfo("Screenshot: " + fullPath);

		} catch (Exception ex) {
			KeywordLogger.getInstance(SmartXPathController.class).logError(MessageFormat
					.format(StringConstants.KW_LOG_INFO_COULD_NOT_SAVE_SCREENSHOT, smartXPathFolder, ex.getMessage()));
		}
	}
}
