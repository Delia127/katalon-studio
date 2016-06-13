package com.kms.katalon.core.mobile.helper;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;

import java.text.MessageFormat;
import java.util.Date;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;

import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.helper.KeywordHelper;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.keyword.MobileDriverFactory;
import com.kms.katalon.core.mobile.keyword.MobileSearchEngine;
import com.kms.katalon.core.testobject.TestObject;

public class MobileElementCommonHelper {
    // Added this method here as duplicated for common methods to use
    // TODO: merge this method with MobileBuiltinKeywors.findElement() and
    // re-factor for better code
    @SuppressWarnings("rawtypes")
    public static WebElement findElement(TestObject to, int timeOut) throws Exception {
        Date startTime = new Date();
        Date endTime;
        long span = 0;
        WebElement webElement = null;
        Point elementLocation = null;
        AppiumDriver<?> driver = MobileDriverFactory.getDriver();
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
                        TouchActions ta = new TouchActions((AndroidDriver) driver);
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
    
    public static void tapAndHold(TestObject to, int duration, int timeout) throws StepFailedException, Exception {
        KeywordHelper.checkTestObjectParameter(to);
        timeout = KeywordHelper.checkTimeout(timeout);
        boolean useCustomDuration = true;
        KeywordLogger logger = KeywordLogger.getInstance();
        logger.logInfo(StringConstants.COMM_LOG_INFO_CHECKING_DURATION);
        if (duration <= 0) {
            logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_WARNING_INVALID_DURATION, duration));
            useCustomDuration = false;
        }
        WebElement element = findElement(to, timeout * 1000);
        if (element == null){
            throw new StepFailedException(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()));
        }
        TouchAction longPressAction = new TouchAction(MobileDriverFactory.getDriver());
        longPressAction = (useCustomDuration) ? longPressAction.longPress(element, duration) : longPressAction.longPress(element);
        longPressAction.release().perform();
        logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TAP_AND_HOLD_ON_ELEMENT_X, to.getObjectId()));
    }
}
