package com.kms.katalon.composer.windows.spy;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.text.StrMatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.AppiumStreamHandler;
import com.kms.katalon.composer.mobile.objectspy.util.Util;
import com.kms.katalon.composer.windows.element.SnapshotWindowsElement;
import com.kms.katalon.composer.windows.element.TreeWindowsElement;
import com.kms.katalon.core.util.internal.ProxyUtil;
import com.kms.katalon.core.windows.WindowsDriverFactory;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.execution.windows.WindowsDriverConnector;
import com.thoughtworks.selenium.SeleniumException;

import io.appium.java_client.windows.WindowsDriver;

public class WindowsInspectorController {

    private WindowsDriver<WebElement> driver;

    private AppiumStreamHandler streamHandler;

    private Thread appiumTailerThread;

    public WindowsInspectorController() {
    }

    private void closeAppiumTailerThread() {
        if (appiumTailerThread != null && appiumTailerThread.isAlive()) {
            appiumTailerThread.interrupt();
        }
        appiumTailerThread = null;
    }

    public WindowsDriver<WebElement> getDriver() {
        return driver;
    }

    public boolean closeApp() {
        try {
            if (null != driver && null != ((RemoteWebDriver) driver).getSessionId()) {
                driver.quit();
            }
            closeAppiumTailerThread();
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        } finally {
            driver = null;
        }
    }

    public String captureScreenshot() throws Exception {
        String screenshotFolder = Util.getDefaultMobileScreenshotPath();
        File screenshot = driver.getScreenshotAs(OutputType.FILE);
        if (!screenshot.exists()) {
            throw new Exception(StringConstants.DIA_ERROR_MSG_UNABLE_TO_CAPTURE_SCREEN);
        }
        String fileName = new String("screenshot_" + new Date().getTime() + ".jpg");
        String path = screenshotFolder + System.getProperty("file.separator") + fileName;
        FileUtils.copyFile(screenshot, new File(path));
        try {
            FileUtils.forceDelete(screenshot);
        } catch (Exception e) {}
        return path;
    }

    public TreeWindowsElement getWindowsObjectRoot() {
        try {
            String pageSource = driver.getPageSource();
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = null;
            try {
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(pageSource));
                doc = db.parse(is);
            } catch (SAXParseException e) {
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(removeEscapeCharacter(pageSource)));
                doc = db.parse(is);
            }
            Element rootElement = doc.getDocumentElement();

            SnapshotWindowsElement htmlMobileElementRootNode = new SnapshotWindowsElement();
            htmlMobileElementRootNode.render(rootElement);
            return htmlMobileElementRootNode;
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return null;
        }
    }

    public static String removeEscapeCharacter(String contentBuilder) {
        String pattern = "(\\\"([^=])*\\\")";

        Pattern pattern2 = Pattern.compile(pattern);
        Matcher matcher = pattern2.matcher(contentBuilder);
        StrBuilder sb = new StrBuilder(contentBuilder);

        while (matcher.find()) {
            String str = matcher.group(1).substring(1, matcher.group(1).length() - 1);
            sb = sb.replaceFirst(StrMatcher.stringMatcher(str), StringEscapeUtils.escapeXml(str));
        }

        return sb.toString();
    }

    public AppiumStreamHandler getStreamHandler() {
        return streamHandler;
    }

    public void setStreamHandler(AppiumStreamHandler streamHandler) {
        this.streamHandler = streamHandler;
    }

    public void startApplication(WindowsDriverConnector driverConnector, String appFile)
            throws SeleniumException, IOException, URISyntaxException {
        String url = driverConnector.getWinAppDriverUrl();
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities(driverConnector.getDesiredCapabilities());
        Proxy proxy = ProxyUtil.getProxy(ProxyPreferences.getProxyInformation());
        driver = WindowsDriverFactory.startApplication(new URL(url), appFile, desiredCapabilities, proxy);
    }
}
