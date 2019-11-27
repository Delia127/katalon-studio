package com.kms.katalon.composer.mobile.objectspy.util;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import com.kms.katalon.core.testobject.MobileTestObject;
import com.kms.katalon.core.testobject.MobileTestObject.MobileLocatorStrategy;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.testobject.WindowsTestObject;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.HideKeyboardStrategy;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import io.appium.java_client.windows.WindowsDriver;

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

    public WebElement findElement(TestObject to, int timeOut) throws Exception {
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
                        TouchAction<?> swipe = new TouchAction<>(driver).press(PointOption.point(screenSize.width / 2, screenSize.height / 2))
                                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
                                .moveTo(PointOption.point(screenSize.width / 2, (int) ((screenSize.height / 2) * 0.5)))
                                .release();
                        swipe.perform();
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

    public List<WebElement> findElements(MobileTestObject testObject)
            throws Exception
//            , DriverNotStartedException
    {
        if (testObject == null) {
            throw new IllegalArgumentException("Test object cannot be null");
        }

        MobileSearchEngine searchEngine = new MobileSearchEngine(driver, testObject);
        return searchEngine.findWebElements(false);
    }

    public void tap(TestObject to) throws Exception {
        KeywordHelper.checkTestObjectParameter(to);
        WebElement element = findElement(to, timeout * 1000);
        if (element == null) {
            KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()),
                    flowControl, null);
            return;
        }
        TouchAction<?> tap = new TouchAction<>(driver)
                .tap(TapOptions.tapOptions().withElement(ElementOption.element(element, 1, 1)));
        tap.perform();
    }

    public void tapAndHold(TestObject to) throws Exception {
        WebElement element = findElement(to, timeout);
        TouchAction<?> longPressAction = new TouchAction<>(driver);
        longPressAction = longPressAction
                .longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(element)));
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
                ((AndroidDriver<?>) driver).pressKey(new KeyEvent(AndroidKey.BACK));
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
