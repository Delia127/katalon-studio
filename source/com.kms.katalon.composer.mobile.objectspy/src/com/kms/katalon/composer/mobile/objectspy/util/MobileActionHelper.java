package com.kms.katalon.composer.mobile.objectspy.util;

import java.text.MessageFormat;
import java.util.Date;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;

import com.kms.katalon.core.helper.KeywordHelper;
import com.kms.katalon.core.keyword.internal.KeywordMain;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.keyword.internal.MobileSearchEngine;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.TestObject;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.HideKeyboardStrategy;

/**
 * This class duplicated codes from keywords to use for recorder
 * TODO: Merge code from keywords classes and this class
 *
 */
public class MobileActionHelper {
    private static final int timeout = 10;

    private static final FailureHandling flowControl = FailureHandling.STOP_ON_FAILURE;

    private AppiumDriver<?> driver;

    public MobileActionHelper(AppiumDriver<?> driver) {
        this.driver = driver;
    }

    private WebElement findElement(TestObject to, int timeOut) throws Exception {
        Date startTime = new Date();
        Date endTime;
        long span = 0;
        WebElement webElement = null;
        Point elementLocation = null;
        MobileSearchEngine searchEngine = new MobileSearchEngine(driver, to);

        Dimension screenSize = driver.manage().window().getSize();

        while (span < timeOut) {
            webElement = searchEngine.findWebElement(false);
            if (webElement != null) {
                elementLocation = webElement.getLocation();
                if (elementLocation.y < screenSize.height) {
                    break;
                }
                try {
                    if (driver instanceof AndroidDriver) {
                        TouchActions ta = new TouchActions((AndroidDriver<?>) driver);
                        ta.down(screenSize.width / 2, screenSize.height / 2).perform();
                        ta.move(screenSize.width / 2, (int) ((screenSize.height / 2) * 0.5)).perform();
                        ta.release().perform();
                    } else {
                        driver.swipe(screenSize.width / 2, screenSize.height / 2, screenSize.width / 2,
                                (int) ((screenSize.height / 2) * 0.5), 500);
                    }
                } catch (Exception e) {
                    // Ignore exception while finding elements
                }
            }
            endTime = new Date();
            span = endTime.getTime() - startTime.getTime();
        }
        return webElement;
    }

    public void tap(TestObject to) throws Exception {
        KeywordHelper.checkTestObjectParameter(to);
        WebElement element = findElement(to, timeout * 1000);
        if (element == null) {
            KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()),
                    flowControl, null);
            return;
        }
        ((MobileElement) element).tap(1, 1);
    }

    public void tapAndHold(TestObject to) throws Exception {
        WebElement element = findElement(to, timeout);
        TouchAction longPressAction = new TouchAction(driver);
        longPressAction = longPressAction.longPress(element);
        longPressAction.release().perform();
    }

    public void setText(TestObject to, String text) throws Exception {
        KeywordHelper.checkTestObjectParameter(to);
        WebElement element = findElement(to, timeout * 1000);
        if (element == null) {
            KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()),
                    flowControl, null);
            return;
        }
        element.clear();
        element.sendKeys(text);
    }

    public void hideKeyboard() {
        String context = driver.getContext();
        try {
            internalSwitchToNativeContext(driver);
            try {
                driver.hideKeyboard();
            } catch (WebDriverException e) {
                if (!(e.getMessage().startsWith(StringConstants.APPIUM_DRIVER_ERROR_JS_FAILED)
                        && driver instanceof IOSDriver<?>)) {
                    throw e;
                }
                // default hide keyboard strategy (tap outside) failed on iOS, use "Done" button
                IOSDriver<?> iosDriver = (IOSDriver<?>) driver;
                iosDriver.hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Done");
            }
        } finally { 
            driver.context(context);
        }
    }

    public void clearText(TestObject to) throws Exception {
        KeywordHelper.checkTestObjectParameter(to);
        WebElement element = findElement(to, timeout * 1000);
        if (element == null) {
            KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()),
                    flowControl, null);
            return;
        }
        element.clear();
    }

    private boolean internalSwitchToNativeContext(AppiumDriver<?> driver) {
        return internalSwitchToContext(driver, "NATIVE");
    }

    private boolean internalSwitchToContext(AppiumDriver<?> driver, String contextName) {
        try {
            for (String context : driver.getContextHandles()) {
                if (context.contains(contextName)) {
                    driver.context(context);
                    return true;
                }
            }
        } catch (WebDriverException e) {
            // Appium will raise WebDriverException error when driver.getContextHandles() is called but
            // ios-webkit-debug-proxy is not started.
            // Catch it here and ignore
        }
        return false;
    }

    public void pressBack() throws Exception {
        String context = driver.getContext();
        try {
            internalSwitchToNativeContext(driver);
            if (driver instanceof AndroidDriver) {
                ((AndroidDriver<?>) driver).pressKeyCode(AndroidKeyCode.BACK);
            } else {
                KeywordMain.stepFailed(StringConstants.KW_MSG_UNSUPPORT_ACT_FOR_THIS_DEVICE, flowControl, null);
                return;
            }
        } finally {
            driver.context(context);
        }
    }

    public void switchToLandscape() throws Exception {
        String context = driver.getContext();
        try {
            internalSwitchToNativeContext(driver);
            driver.rotate(ScreenOrientation.LANDSCAPE);
        } finally {
            driver.context(context);
        }
    }

    public void switchToPortrait() throws Exception {
        String context = driver.getContext();
        try {
            internalSwitchToNativeContext(driver);
            driver.rotate(ScreenOrientation.PORTRAIT);
        } finally {
            driver.context(context);
        }
    }
}
