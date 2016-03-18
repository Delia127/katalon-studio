package com.kms.katalon.core.webui.keyword;

import groovy.transform.CompileStatic

import java.text.MessageFormat
import java.util.concurrent.TimeUnit

import org.apache.commons.io.FileUtils
import org.openqa.selenium.Alert
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.Wait
import org.openqa.selenium.support.ui.WebDriverWait

import com.google.common.base.Function
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.BuiltinKeywords
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.util.ExceptionsUtil
import com.kms.katalon.core.util.PathUtil
import com.kms.katalon.core.webui.common.ScreenUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.WebUIDriverType
import com.kms.katalon.core.webui.exception.BrowserNotOpenedException
import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import com.kms.katalon.core.webui.util.FileUtil

@CompileStatic
public class WebUiBuiltInKeywords extends BuiltinKeywords {
    private static final KeywordLogger logger = KeywordLogger.getInstance();
    private static ScreenUtil screenUtil = new ScreenUtil();

    /**
     * Open browser and navigate to the specified url; if url is left empty then just open browser
     * @param rawUrl
     *         url of the web page to be opened, can be left empty or null. If rawUrl doesn't contain protocol prefix, 
     *         then the protocol will be <code>http://</code>.
     *      </p>Example:
     *      <ul>
     *          <li>http://katalon.kms-technology.com/</li>
     *          <li>https://www.google.com</li>
     *          <li>file:///D:/Development/index.html</li>
     *          <li>kms-technology.com => http://kms-technology.com</li>
     *      </ul>
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
    public static void openBrowser(String rawUrl, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_OPENING_BROWSER);
            DriverFactory.openWebDriver();
            if (rawUrl != null && !rawUrl.isEmpty()) {
                URL url = PathUtil.getUrl(rawUrl, "http");
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_NAVIGATING_BROWSER_TO, url.toString()));
                DriverFactory.getWebDriver().get(url.toString());
            }
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_BROWSER_IS_OPENED_W_URL, rawUrl));
        }
        , flowControl, false, (rawUrl != null) ? MessageFormat.format(StringConstants.KW_MSG_UNABLE_TO_OPEN_BROWSER_W_URL, rawUrl) : StringConstants.KW_MSG_UNABLE_TO_OPEN_BROWSER)
    }


    /**
     * Close the browser. This action will close all windows of the browser.
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
    public static void closeBrowser(FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_CLOSING_BROWSER);
            DriverFactory.closeWebDriver();
            logger.logPassed(StringConstants.KW_LOG_PASSED_BROWSER_IS_CLOSED);
        }
        , flowControl, true, StringConstants.KW_MSG_UNABLE_TO_CLOSE_BROWSER)
    }

    /**
     * Simulate users clicking "back" button on their browser
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
    public static void back(FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_NAVIGATING_BACK);
            DriverFactory.getWebDriver().navigate().back();
            logger.logPassed(StringConstants.KW_LOG_PASSED_NAVIGATE_BACK);
        }
        , flowControl, true, StringConstants.KW_MSG_CANNOT_BACK_TO_PREV_PAGE)
    }

    /**
     * Simulate users clicking "forward" button on their browser
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
    public static void forward(FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_NAVIGATING_FORWARD);
            DriverFactory.getWebDriver().navigate().forward();
            logger.logPassed(StringConstants.KW_LOG_PASSED_NAVIGATE_FORWARD);
        }
        , flowControl, true, StringConstants.KW_MSG_CANNOT_FORWARD_TO_NEXT_PAGE)
    }

    /**
     * Simulate users clicking "refresh" button on their browser
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
    public static void refresh(FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_REFRESHING);
            DriverFactory.getWebDriver().navigate().refresh();
            logger.logPassed(StringConstants.KW_LOG_PASSED_REFRESH);
        }
        , flowControl, true, StringConstants.KW_MSG_CANNOT_REFRESH_PAGE)
    }

    /**
     * Navigate to the specified web page
     * @param rawUrl
     *          url of the web page to navigate to. If rawUrl doesn't contain protocol prefix, then the protocol will be <code>http://</code>.
     *      </p>Example:
     *      <ul>
     *          <li>http://katalon.kms-technology.com/</li>
     *          <li>https://www.google.com</li>
     *          <li>file:///D:/Development/index.html</li>
     *          <li>kms-technology.com => http://kms-technology.com</li>
     *      </ul>
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
    public static void navigateToUrl(String rawUrl, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_URL);
            if (rawUrl == null || rawUrl.isEmpty()) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_URL_CANNOT_BE_NULL_OR_EMPTY);
            }

            URL url = PathUtil.getUrl(rawUrl, "http");
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_NAVIGATING_TO, url.toString()));
            WebDriver webDriver = DriverFactory.getWebDriver();
            webDriver.navigate().to(url.toString());
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_NAVIGATE_TO, url.toString()));
        }
        , flowControl, true, MessageFormat.format(StringConstants.KW_MSG_CANNOT_NAVIGATE_TO, rawUrl))
    }

    /**
     * Get title of the current window
     * @param flowControl
     * @return
     *      title of the current window
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static String getWindowTitle(FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_GETTING_CURR_WINDOW_TITLE);
            String windowTitle = String.valueOf(DriverFactory.getWebDriver().getTitle());
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_CURR_WINDOW_TITLE, windowTitle));
            return windowTitle;
        }
        , flowControl, true, StringConstants.KW_MSG_CANNOT_GET_CURR_WINDOW_TITLE)
    }

    /**
     * Get url of the current window
     * @param flowControl
     * @return
     *      url of the current window
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static String getUrl(FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_GETTING_CURR_WINDOW_URL);
            String url = DriverFactory.getWebDriver().getCurrentUrl();
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_CURR_WINDOW_URL, url));
            return url;
        }
        , flowControl, true, StringConstants.KW_MSG_CANNOT_GET_CURR_WINDOW_URL)
    }

    /**
     * Get index of the current window
     * @param flowControl
     * @return
     *      index of the current window
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static int getWindowIndex(FailureHandling flowControl) throws StepFailedException {
        return (int) WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_GETTING_CURR_WINDOW_INDEX);
            int windowIndex = DriverFactory.getCurrentWindowIndex();
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_CURR_WINDOW_INDEX, windowIndex));
            return windowIndex;
        }
        , flowControl, true, StringConstants.KW_MSG_CANNOT_GET_CURR_WINDOW_INDEX)
    }

    /**
     * Resize current window to take up the entire screen
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static void maximizeWindow(FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_MAX_CURR_WINDOW);
            DriverFactory.getWebDriver().manage().window().maximize();
            logger.logPassed(StringConstants.KW_LOG_PASSED_MAX_CURR_WINDOW);
        }
        , flowControl, true, StringConstants.KW_MSG_CANNOT_MAX_CURR_WINDOW)
    }

    /**
     * Wait for the given element to NOT present (disappear) within the given time in second unit
     * @param to 
     *          represent a web element
     * @param timeOut 
     *          system will wait at most timeout (seconds) to return result
     * @return
     *      true if the element is NOT present, and false if the element is present
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementNotPresent(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                boolean elementNotFound = false;
                final By locator = WebUiCommonHelper.buildLocator(to);
                try {
                    if (locator != null) {
                        logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_FINDING_WEB_ELEMENT_W_ID, to.getObjectId(), locator.toString(), timeOut));
                        elementNotFound = new FluentWait<WebDriver>(DriverFactory.getWebDriver())
                                .pollingEvery(500, TimeUnit.MILLISECONDS).withTimeout(timeOut, TimeUnit.SECONDS)
                                .until(new Function<WebDriver, Boolean>() {
                                    @Override
                                    public Boolean apply(WebDriver webDriver) {
                                        try {
                                            webDriver.findElement(locator);
                                            return false;
                                        } catch (NoSuchElementException e) {
                                            return true;
                                        }
                                    }
                                });
                    } else {
                        throw new IllegalArgumentException(MessageFormat.format(StringConstants.KW_EXC_WEB_ELEMENT_W_ID_DOES_NOT_HAVE_SATISFY_PROP, to.getObjectId()));
                    }
                } catch (TimeoutException e) {
                    // timeOut, do nothing
                }
                if (elementNotFound) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_WEB_ELEMT_W_ID_IS_NOT_PRESENT_AFTER, to.getObjectId(), locator.toString(), timeOut));
                    return true;
                } else {
                    logger.logWarning(MessageFormat.format(StringConstants.KW_MSG_WEB_ELEMT_W_ID_IS_NOT_PRESENT_AFTER, to.getObjectId(), locator.toString(), timeOut));
                    return false;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_TO_BE_NOT_PRESENT, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_WAIT_FOR_OBJ_TO_BE_NOT_PRESENT)
    }

    /**
     * Wait for the given element to present (appear) within the given time in second unit
     * @param to
     *      represent a web element
     * @param timeOut
     *       system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return
     *      true if the element is present, and false if the element is NOT present
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementPresent(TestObject to, int timeOut, FailureHandling flowControl)
    throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement foundElement = null;
                try {
                    foundElement = findWebElement(to, timeOut);
                    if (foundElement != null) {
                        logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_PRESENT, to.getObjectId()));
                    }
                    return true;
                } catch (WebElementNotFoundException e) {
                    logger.logWarning(MessageFormat.format(StringConstants.KW_MSG_OBJ_IS_NOT_PRESENT_AFTER_X_SEC, to.getObjectId(), timeOut));
                    return false;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_TO_BE_PRESENT, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_WAIT_FOR_OBJ_TO_BE_PRESENT)
    }

    /***
     * Verify if given web element is visible
     * @param to
     *      represent a web element
     * @param flowControl
     * @return
     *     true if the element is present and visible; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementVisible(TestObject to, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, RunConfiguration.getTimeOut());
                try {
                    WebElement foundElement = findWebElement(to, RunConfiguration.getTimeOut());
                    if (foundElement.isDisplayed()) {
                        logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_VISIBLE, to.getObjectId()));
                        return true;
                    } else {
                        WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_NOT_VISIBLE, to.getObjectId()),
                                flowControl, null);
                        return false;
                    }
                    return true;
                } catch (WebElementNotFoundException e) {
                    WebUIKeywordMain.stepFailed(e.getMessage(), flowControl, null);
                    return false;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_TO_BE_VISIBLE, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_TO_BE_VISIBLE)
    }

    /***
     * Verify if given web element is NOT visible
     * @param to
     *      represent a web element
     * @param flowControl
     * @return
     *     true if the element is present and NOT visible; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotVisible(TestObject to, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, RunConfiguration.getTimeOut());
                try {
                    WebElement foundElement = findWebElement(to, RunConfiguration.getTimeOut());
                    if (!foundElement.isDisplayed()) {
                        logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_NOT_VISIBLE, to.getObjectId()));
                        return true;
                    } else {
                        WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_VISIBLE, to.getObjectId()),
                                flowControl, null);
                        return false;
                    }
                    return true;
                } catch (WebElementNotFoundException e) {
                    WebUIKeywordMain.stepFailed(e.getMessage(), flowControl, null);
                    return false;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_TO_BE_NOT_VISIBLE, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_TO_BE_NOT_VISIBLE)
    }

    /***
     * Wait until the given web element is visible within timeout.
     * @param to 
     * 		represent a web element
     * @param timeOut
     * 		how many seconds to wait (maximum)
     * @param flowControl
     * @return
     *     true if the element is present and visible; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementVisible(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                try {
                    WebElement foundElement = findWebElement(to, timeOut);
                    WebDriverWait wait = new WebDriverWait(DriverFactory.getWebDriver(), timeOut);
                    foundElement = wait.until(ExpectedConditions.visibilityOf(foundElement));
                    if (foundElement != null) {
                        logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_VISIBLE, to.getObjectId()));
                    }
                    return true;
                } catch (WebElementNotFoundException e) {
                    logger.logWarning(e.getMessage());
                    return false;
                } catch (TimeoutException e) {
                    logger.logWarning(MessageFormat.format(StringConstants.KW_MSG_OBJ_IS_NOT_VISIBLE_AFTER_X_SEC, to.getObjectId(), timeOut));
                    return false;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_TO_BE_VISIBLE, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_WAIT_FOR_OBJ_TO_BE_VISIBLE)
    }

    /***
     * Wait until the given web element is NOT visible within timeout.
     * @param to
     *      represent a web element
     * @param timeOut
     *      how many seconds to wait (maximum)
     * @param flowControl
     * @return
     *     true if the element is present but is NOT visible; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementNotVisible(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                try {
                    WebElement foundElement = findWebElement(to, timeOut);
                    WebDriverWait wait = new WebDriverWait(DriverFactory.getWebDriver(), timeOut);
                    foundElement = wait.until(new ExpectedCondition<WebElement>() {
                                @Override
                                public WebElement apply(WebDriver driver) {
                                    return foundElement.isDisplayed() ? null : foundElement;
                                }

                                @Override
                                public String toString() {
                                    return "visibility of " + foundElement;
                                }
                            });
                    if (foundElement != null) {
                        logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_NOT_VISIBLE, to.getObjectId()));
                    }
                    return true;
                } catch (WebElementNotFoundException e) {
                    logger.logWarning(e.getMessage());
                    return false;
                } catch (TimeoutException e) {
                    logger.logWarning(MessageFormat.format(StringConstants.KW_MSG_OBJ_IS_VISIBLE_AFTER_X_SEC, to.getObjectId(), timeOut));
                    return false;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_TO_BE_NOT_VISIBLE, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_WAIT_FOR_OBJ_TO_BE_NOT_VISIBLE)
    }

    /***
     * Wait for the given element to be clickable within the given time in second
     * @param to
     *         represent a web element
     * @param timeOut
     *         how many seconds to wait
     * @param flowControl
     * @return
     *         true if the element is present and clickable; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementClickable(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                try {
                    isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                    WebElement foundElement = findWebElement(to, timeOut);
                    WebDriverWait wait = new WebDriverWait(DriverFactory.getWebDriver(), timeOut);
                    foundElement = wait.until(new ExpectedCondition<WebElement>() {
                                @Override
                                public WebElement apply(WebDriver driver) {
                                    if (foundElement.isEnabled()) {
                                        return foundElement;
                                    } else {
                                        return null;
                                    }
                                }
                            });
                    if (foundElement != null) {
                        logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_CLICKABLE, to.getObjectId()));
                    }
                    return true;
                } catch (WebElementNotFoundException e) {
                    logger.logWarning(e.getMessage());
                    return false;
                } catch (TimeoutException e) {
                    logger.logWarning(MessageFormat.format(StringConstants.KW_MSG_OBJ_IS_NOT_CLICKABLE_AFTER_X_SEC, to.getObjectId(), timeOut));
                    return false;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_TO_BE_CLICKABLE, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_WAIT_FOR_OBJ_TO_BE_CLICKABLE)
    }

    /***
     * Wait for the given element to be not clickable within the given time in second
     * @param to
     *         represent a web element
     * @param timeOut
     *         how many seconds to wait
     * @param flowControl
     * @return
     *         true if the element is present but is NOT clickable; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementNotClickable(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                try {
                    isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                    WebElement foundElement = findWebElement(to, timeOut);
                    WebDriverWait wait = new WebDriverWait(DriverFactory.getWebDriver(), timeOut);
                    foundElement = wait.until(new ExpectedCondition<WebElement>() {
                                @Override
                                public WebElement apply(WebDriver driver) {
                                    if (foundElement.isEnabled()) {
                                        return null;
                                    } else {
                                        return foundElement;
                                    }
                                }
                            });
                    if (foundElement != null) {
                        logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_NOT_CLICKABLE, to.getObjectId()));
                    }
                    return true;
                } catch (WebElementNotFoundException e) {
                    logger.logWarning(e.getMessage());
                    return false;
                } catch (TimeoutException e) {
                    logger.logWarning(MessageFormat.format(StringConstants.KW_MSG_OBJ_IS_CLICKABLE_AFTER_X_SEC, to.getObjectId(), timeOut));
                    return false;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_TO_BE__NOTCLICKABLE, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_WAIT_FOR_OBJ_TO_BE_NOT_CLICKABLE)
    }

    /***
     * Verify if the given element is clickable
     * @param to
     *         represent a web element
     * @param flowControl
     * @return
     *         true if the element is present and clickable; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementClickable(TestObject to, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                try {
                    isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, RunConfiguration.getTimeOut());
                    WebElement foundElement = findWebElement(to, RunConfiguration.getTimeOut());
                    if (foundElement.isEnabled()) {
                        logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_CLICKABLE, to.getObjectId()));
                        return true;
                    } else {
                        WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_NOT_CLICKABLE, to.getObjectId()),
                                flowControl, null);
                        return false;
                    }
                } catch (WebElementNotFoundException e) {
                    WebUIKeywordMain.stepFailed(e.getMessage(), flowControl, null);
                    return false;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_VERIFY_OBJ_X_TO_BE_CLICKABLE, to.getObjectId())
        : StringConstants.KW_MSG_VERIFY_OBJ_TO_BE_CLICKABLE)
    }

    /***
     * Verify if the given element is NOT clickable
     * @param to
     *         represent a web element
     * @param flowControl
     * @return
     *         true if the element is present and NOT clickable; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotClickable(TestObject to, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                try {
                    isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, RunConfiguration.getTimeOut());
                    WebElement foundElement = findWebElement(to, RunConfiguration.getTimeOut());
                    if (foundElement.isEnabled()) {
                        WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_CLICKABLE, to.getObjectId()),
                                flowControl, null);
                        return false;
                    } else {
                        logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_NOT_CLICKABLE, to.getObjectId()));
                        return true;
                    }
                } catch (WebElementNotFoundException e) {
                    WebUIKeywordMain.stepFailed(e.getMessage(), flowControl, null);
                    return false;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_VERIFY_OBJ_X_TO_BE_NOT_CLICKABLE, to.getObjectId())
        : StringConstants.KW_MSG_VERIFY_OBJ_TO_BE_NOT_CLICKABLE)
    }

    /**
     * Click on the given element
     * @param to
     *       represent a web element
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void click(TestObject to, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CLICKING_ON_OBJ, to.getObjectId()));
                webElement.click();
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_CLICKED, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_CLICK_ON_OBJ_X, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_CLICK_ON_OBJ)
    }

    /**
     * If this current element is a form, or an element within a form, then this will be submitted. 
     * If this causes the current page to change, then this method will block until the new page is loaded.
     * @param to
     *      represent a web element
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_FORM)
    public static void submit(TestObject to, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SUBMITTING_ON_FORM_CONTAINING_OBJ, to.getObjectId()));
                webElement.submit();
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_FORM_CONTAINING_OBJ_IS_SUBMITTED, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SUBMIT_FORM_CONTAINING_OBJ_X, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_SUBMIT_FORM_CONTAINING_OBJ)
    }

    /**
     * Double click on the given web element
     * @param to
     *      represent a web element
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void doubleClick(TestObject to, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_DOUBLE_CLICK_ON_OBJ, to.getObjectId()));
                Actions action = new Actions(DriverFactory.getWebDriver());
                action.doubleClick(webElement).build().perform();
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_IS_DOUBLE_CLICKED_ON, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_DOUBLE_CLICK_ON_OBJ_X, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_DOUBLE_CLICK_ON_OBJ)
    }

    /**
     * Right click on the given web element
     * @param to
     *      represent a web element
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void rightClick(TestObject to, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_RIGHT_CLICKING_ON_OBJ, to.getObjectId()));
                Actions action = new Actions(DriverFactory.getWebDriver());
                action.contextClick(webElement).build().perform();
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_IS_RIGHT_CLICKED_ON, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_RIGHT_CLICK_ON_OBJ_X, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_RIGHT_CLICK_ON_OBJ)
    }

    /**
     * Simulate users hovering a mouse over the given element
     * @param to
     *       represent a web element
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void mouseOver(TestObject to, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                if (DriverFactory.getExecutedBrowser() == WebUIDriverType.IE_DRIVER) {
                    WebUiCommonHelper.focusOnBrowser();
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement hoverElement = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_MOVING_MOUSE_OVER_OBJ, to.getObjectId()));
                Actions builder = new Actions(DriverFactory.getWebDriver());
                builder.moveToElement(hoverElement).perform();
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_IS_HOVERED, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_MOVE_MOUSE_OVER_OBJ_X, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_MOVE_MOUSE_OVER_OBJ)
    }

    /**
     * Simulates keystroke events on the specified element, as though you typed the value key-by-key
     * @param to
     *      represent a web element
     * @param strKeys
     *      the combination of keys to type
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_KEYBOARD)
    public static void sendKeys(TestObject to, String strKeys, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                if (to == null) {
                    to = new TestObject("tempBody").addProperty("css", ConditionType.EQUALS, "body");
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SENDING_KEYS_TO_OBJ, strKeys, to.getObjectId()));
                WebElement webElement = findWebElement(to);
                webElement.sendKeys(strKeys);
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_KEYS_SENT_TO_OBJ, strKeys, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SED_KEYS_TO_OBJ_X, strKeys,to.getObjectId())
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_SED_KEYS_TO_OBJ, strKeys))
    }

    /**
     * Move the focus to the specified element; for example, if the element is an input field, move the cursor to that field
     * @param to
     *      represent a web element
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void focus(TestObject to, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement element = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_FOCUSING_ON_OBJ, to.getObjectId()));
                if ("input".equals(element.getTagName())) {
                    element.sendKeys("");
                } else {
                    new Actions(DriverFactory.getWebDriver()).moveToElement(element).perform();
                }
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_IS_FOCUSED, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_FOCUS_ON_OBJ_X, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_FOCUS_ON_OBJ)
    }

    /**
     * Get the visible (i.e. not hidden by CSS) innerText of the web element, including sub-elements, without any leading or trailing whitespace.
     * @param to
     *      represent a web element
     * @param flowControl
     * @return
     *       innerText of the web element
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static String getText(TestObject to, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            String text = "";
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement element = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_GETTING_OBJ_TXT, to.getObjectId()));
                text = element.getText();
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_TXT_IS, to.getObjectId(), text));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
                return text;
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_GET_TXT_OF_OBJ_X, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_GET_OBJ_TXT)
    }

    /**
     * Get attribute value of a web element
     * @param to
     *       represent a web element
     * @param attribute
     *      name of the attribute
     * @param flowControl
     * @return
     *      value of the attribute
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ATTRIBUTE)
    public static String getAttribute(TestObject to, String attribute, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            String attrValue = "";
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_ATTR);
                if (attribute == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_ATTR_IS_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement element = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_GETTING_OBJ_ATTR, attribute, to.getObjectId()));
                attrValue = element.getAttribute(attribute);
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_ATTR_IS, attribute, to.getObjectId(), attrValue));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
                return attrValue;
            }
        }
        , flowControl, true, (to != null && attribute != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_GET_ATTR_X_OF_OBJ_Y, attribute, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_GET_OBJ_ATTR)
    }

    /**
     * Set the value of an input field, as though you type it in. It also clears the previous value of the input field
     * @param to
     *      represent a web element
     * @param text
     *      the text to type
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static void setText(TestObject to, String text, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_TXT);
                if (text == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_TXT_IS_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CLEARING_OBJ_TXT, to.getObjectId()));
                webElement.clear();

                webElement = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SETTING_OBJ_TXT_TO_VAL, to.getObjectId(), text));
                webElement.sendKeys(text);
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TXT_IS_SET_ON_OBJ, text, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SET_TXT_X_OF_OBJ_Y, text, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_SET_TXT)
    }

    /**
     * Check a toggle-button (check-box/radio-button)
     * @param to
     *      represent a web element
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_CHECKBOX)
    public static void check(TestObject to, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CHECKING_ON_OBJ, to.getObjectId()));
                if (!webElement.isSelected()) {
                    webElement.click();
                }
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_IS_CHECKED, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_CHECK_OBJ_X, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_CHECK_OBJ)
    }

    /**
     * Uncheck a toggle-button (check-box/radio-button)
     * @param to
     *      represent a web element
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_CHECKBOX)
    public static void uncheck(TestObject to, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_UNCHECKING_ON_OBJ, to.getObjectId()));
                if (webElement.isSelected()) {
                    webElement.click();
                }
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_IS_UNCHECKED, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_UNCHECK_OBJ_X, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_UNCHECK_OBJ)
    }

    /**
     * Select the options at the given indices. Index starts from 0.
     *
     * @param to
     *          represent a web element
     * @param range
     *          index range of the options to select. Index starts from 0.
     <p>Example: 
     <p>2 - index 2
     <p>"2,3" - index 2 and 3
     <p>"2-5" - index 2 to 5 (2, 3, 4, 5)
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static void selectOptionByIndex(TestObject to, Object range, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_INDEX_RANGE);
                if (range == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_INDEX_RANGE_IS_NULL);
                }
                Integer[] indexes = WebUiCommonHelper.indexRangeToArray(String.valueOf(range));
                if (indexes.length > 0) {
                    isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                    WebElement webElement = findWebElement(to);
                    WebUiCommonHelper.selectOrDeselectOptionsByIndex(new Select(webElement), indexes, true, to);
                }
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OPTS_W_INDEX_IN_X_ARE_SELECTED_ON_OBJ_Y, WebUiCommonHelper.integerArrayToString(indexes), to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null && range != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SEL_OPT_BY_INDEX_X_OF_OBJ_Y, range, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_SEL_OPT_BY_INDEX)
    }

    /**
     * Select all options that have a value matching the "value" argument.
     *
     * @param to
     *           represent a web element
     * @param value
     *           value of the options to select
     * @param isRegex
     *            true if value is regular expression, false if not
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static void selectOptionByValue(TestObject to, String value, boolean isRegex, FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_VAL_PARAM);
                if (value == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_VAL_IS_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                Select select = new Select(webElement);
                WebUiCommonHelper.selectOrDeselectOptionsByValue(new Select(webElement), value, isRegex, true, to, regularExpressionLog)
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SELECTED_ALL_OPT_W_VAL_X_IN_OBJ_Y, value, to.getObjectId(), regularExpressionLog));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null && value != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SELECT_OPT_BY_VAL_OF_OBJ, value, to.getObjectId(), regularExpressionLog)
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_SEL_OPT_BY_VAL, regularExpressionLog))
    }

    /**
     * Selection all options of an object.
     *
     * @param to
     *         represent a web element
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static void selectAllOption(TestObject to, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                WebUiCommonHelper.selectOrDeselectAllOptions(new Select(webElement), true, to)
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ALL_OBJ_OPTS_ARE_SELECTED, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SELECT_ALL_OBJ_OPTS, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_SELECT_ALL_OPTS)
    }

    /**
     * Select all options with the given label (displayed text)
     *
     * @param to
     *         represent a web element
     * @param labelText
     *          displayed text of the options to select
     * @param isRegex
     *         true if label is regular expression, false if not
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static void selectOptionByLabel(TestObject to, String labelText, boolean isRegex, FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_LBL_PARAM);
                if (labelText == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_LBL_IS_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                WebUiCommonHelper.selectOrDeselectOptionsByLabel(new Select(webElement), labelText, isRegex, true, to, regularExpressionLog)
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SELECTED_ALL_OPT_W_LBL_X_IN_OBJ_Y, labelText, to.getObjectId(), regularExpressionLog));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null && labelText != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SEL_OPT_BY_LBL_OF_OBJ, labelText, to.getObjectId(), regularExpressionLog)
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_SEL_OPT_BY_LBL, regularExpressionLog))
    }

    /**
     * Deselect the options at the given indices. Index starts from 0.
     *
     * @param to
     *         represent a web element
     * @param index
     *  index range of the options to be deselected
     *  <p>Example: 
     *  <p>2 - index 2
     *  <p>"2,3" - index 2 and 3
     *  <p>"2-5" - index 2 to 5 (2, 3, 4, 5)
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static void deselectOptionByIndex(TestObject to, Object range, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_INDEX_RANGE);
                if (range == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_INDEX_RANGE_IS_NULL);
                }
                Integer[] indexes = WebUiCommonHelper.indexRangeToArray(String.valueOf(range));
                if (indexes.length > 0) {
                    isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                    WebElement webElement = findWebElement(to);
                    Select select = new Select(webElement);
                    WebUiCommonHelper.selectOrDeselectOptionsByIndex(select, indexes, false, to);
                }
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OPTS_W_IDX_IN_X_ARE_DESELECTED_ON_OBJ, WebUiCommonHelper.integerArrayToString(indexes), to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null && range != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_DESELECT_OPT_BY_IDX_OF_OBJ, range, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_DESELECT_OPT_BY_IDX)
    }

    /**
     * Deselect all options with the given value
     * @param to
     *         represent a web element
     * @param value
     *         value of the options to be deselected
     * @param isRegex
     *         true if value is regular expression, false if not
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static void deselectOptionByValue(TestObject to, String value, boolean isRegex, FailureHandling flowControl)
    throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_VAL_PARAM);
                if (value == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_VAL_IS_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                WebUiCommonHelper.selectOrDeselectOptionsByValue(new Select(webElement), value, isRegex, false, to, regularExpressionLog)
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OPTS_W_VAL_ARE_DESELECTED_ON_OBJ, value, to.getObjectId(), regularExpressionLog));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null && value != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_DESELECT_OPT_BY_VAL_OF_OBJ, value, to.getObjectId(), regularExpressionLog)
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_DESELECT_OPT_BY_VAL, regularExpressionLog))
    }

    /**
     * Deselect the options with the given label (displayed text)
     *
     * @param to
     *         represent a web element
     * @param labelText
     *         displayed text of the options to be deselected
     * @param isRegex
     *         true if label is regular expression, false if not
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static void deselectOptionByLabel(TestObject to, String labelText, boolean isRegex,
            FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_LBL_PARAM);
                if (labelText == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_LBL_IS_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                Select select = new Select(webElement);
                WebUiCommonHelper.selectOrDeselectOptionsByLabel(new Select(webElement), labelText, isRegex, false, to, regularExpressionLog)
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DESELECTED_OPTS_W_LBL_X_ON_OBJ, labelText, to.getObjectId(), regularExpressionLog));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null && labelText != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_DESEL_OPT_BY_LBL_OF_OBJ, labelText, to.getObjectId(), regularExpressionLog)
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_DESEL_OPT_BY_LBL, regularExpressionLog))
    }

    /**
     * Deselect all options
     *
     * @param to
     *         represent a web element
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static void deselectAllOption(TestObject to, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                Select selection = new Select(webElement);
                WebUiCommonHelper.selectOrDeselectAllOptions(new Select(webElement), false, to)
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DESELECTED_ALL_OPTS_ON_OBJ, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SEL_ALL_OPTS_ON_OBJ, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_SEL_ALL_OPTS)
    }

    /**
     * Verify if the given web element is checked.
     *
     * @param to
     *         represent a web element
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element is checked; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_CHECKBOX)
    public static boolean verifyElementChecked(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement webElement = findWebElement(to, timeOut);
                boolean isChecked = webElement.isSelected();
                if (!isChecked) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_X_IS_NOT_CHECKED, to.getObjectId()), flowControl, null, true);
                    return false;
                } else {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_CHECKED, to.getObjectId()));
                    return true;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_IS_CHECKED, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_IS_CHECKED)
    }

    /**
     * Verify if the given web element is NOT checked.
     *
     * @param to
     *         represent a web element
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element is not checked; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_CHECKBOX)
    public static boolean verifyElementNotChecked(TestObject to, int timeOut, FailureHandling flowControl)
    throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement webElement = findWebElement(to, timeOut);
                boolean isChecked = webElement.isSelected();
                if (isChecked) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_X_IS_CHECKED, to.getObjectId()), flowControl, null, true);
                    return false;
                } else {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_NOT_CHECKED, to.getObjectId()));
                    return true;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_IS_NOT_CHECKED, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_IS_NOT_CHECKED)
    }

    /**
     * Verify if the given web element presents on the DOM
     *
     * @param to
     *          represent a web element
     * @param timeOut
     *          system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element presents; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementPresent(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement foundElement = null;
                foundElement = findWebElement(to, timeOut);
                if (foundElement != null) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_PRESENT, to.getObjectId()));
                }
                return true;
            } catch (WebElementNotFoundException ex) {
                WebUIKeywordMain.stepFailed(ExceptionsUtil.getMessageForThrowable(ex), flowControl, null, true);
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        } , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_IS_PRESENT, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_IS_PRESENT)
    }

    /**
     * Verify if the given web element does NOT present on the DOM
     *
     * @param to
     *         represent a web element
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if the element does NOT present; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotPresent(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                boolean elementNotFound = false;
                final By locator = WebUiCommonHelper.buildLocator(to);
                try {
                    if (locator != null) {
                        logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_FINDING_WEB_ELEMENT_W_ID, to.getObjectId(), locator.toString(), timeOut));
                        elementNotFound = new FluentWait<WebDriver>(DriverFactory.getWebDriver())
                                .pollingEvery(500, TimeUnit.MILLISECONDS).withTimeout(timeOut, TimeUnit.SECONDS)
                                .until(new Function<WebDriver, Boolean>() {
                                    @Override
                                    public Boolean apply(WebDriver webDriver) {
                                        try {
                                            webDriver.findElement(locator);
                                            return false;
                                        } catch (NoSuchElementException e) {
                                            return true;
                                        }
                                    }
                                });
                    } else {
                        throw new IllegalArgumentException(MessageFormat.format(StringConstants.KW_EXC_WEB_ELEMENT_W_ID_DOES_NOT_HAVE_SATISFY_PROP, to.getObjectId()));
                    }
                } catch (TimeoutException e) {
                    // timeOut, do nothing
                }
                if (elementNotFound) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_WEB_ELEMT_W_ID_IS_NOT_PRESENT_AFTER, to.getObjectId(), locator.toString(), timeOut));
                    return true;
                } else {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_WEB_ELEMT_W_ID_IS_NOT_PRESENT_AFTER, to.getObjectId(), locator.toString(), timeOut), flowControl, null, true);
                    return false;
                }
            } catch (WebElementNotFoundException e) {
                WebUIKeywordMain.stepFailed(ExceptionsUtil.getMessageForThrowable(e), flowControl, null, true);
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        } , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_IS_NOT_PRESENT, to.getObjectId()) : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_IS_NOT_PRESENT)
    }

    /**
     * Simulate users clicking on "OK" button of alert class (alert,
     * confirmation popup, prompt popup)
     * 
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ALERT)
    public static void acceptAlert(FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            Alert alert = DriverFactory.getAlert();
            if (alert != null) {
                logger.logInfo(StringConstants.KW_LOG_INFO_ACCEPTING_ALERT);
                alert.accept();
                logger.logPassed(StringConstants.KW_LOG_PASSED_ALERT_ACCEPTED);
            } else {
                WebUIKeywordMain.stepFailed(StringConstants.KW_MSG_NO_ALERT_FOUND, flowControl, null, true);
            }
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_ACCEPT_ALERT)
    }

    /**
     * Simulate users clicking on "Cancel" button of alert class (alert,
     * confirmation popup, prompt popup).
     *
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ALERT)
    public static void dismissAlert(FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            Alert alert = DriverFactory.getAlert();
            if (alert != null) {
                logger.logInfo(StringConstants.KW_LOG_INFO_DISMISSING_ALERT);
                alert.dismiss();
                logger.logPassed(StringConstants.KW_LOG_PASSED_ALERT_DISMISSED);
            } else {
                WebUIKeywordMain.stepFailed(StringConstants.KW_MSG_NO_ALERT_FOUND, flowControl, null, true);
            }
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_DISMISS_ALERT)
    }

    /**
     * Get displayed text of the alert class (alert, confirmation popup, prompt
     * popup).
     *
     * @param flowControl
     * @return value
     *      text of the alert
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ALERT)
    public static String getAlertText(FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            Alert alert = DriverFactory.getAlert();
            String text = "";
            if (alert != null) {
                logger.logInfo(StringConstants.KW_LOG_INFO_GETTING_ALERT_TXT);
                text = alert.getText();
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_GET_ALERT_TXT_SUCCESSFULLY, text));
            } else {
                WebUIKeywordMain.stepFailed(StringConstants.KW_MSG_NO_ALERT_FOUND, flowControl, null, true);
                return ''
            }
            return text;
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_GET_ALERT_TXT)
    }

    /**
     * Simulate users typing text into prompt popup.
     *
     * @param text
     *            text to type into the prompt popup
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ALERT)
    public static void setAlertText(String text, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_TXT);
            if (text == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_TXT_IS_NULL);
            }
            Alert alert = DriverFactory.getAlert();
            if (alert != null) {
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SETTING_ALERT_TXT, text));
                alert.sendKeys(text);
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SET_ALERT_TXT_SUCCESSFULLY, text));
            } else {
                WebUIKeywordMain.stepFailed(StringConstants.KW_MSG_NO_ALERT_FOUND, flowControl, null, true);
            }
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_SET_ALERT_TXT)
    }

    /**
     * Wait for alert to present
     *
     * @param timeout
     *            timeout waiting for alert to present
     * @param flowControl
     * @return true if alert is present and false if alert is not present
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ALERT)
    public static boolean waitForAlert(int timeOut, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            timeOut = WebUiCommonHelper.checkTimeout(timeOut);
            boolean isAlertPresent = DriverFactory.waitForAlert(timeOut);
            if (isAlertPresent) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ALERT_IS_PRESENT_AFTER_X_SEC, timeOut));
                return true;
            } else {
                logger.logWarning(MessageFormat.format(StringConstants.KW_MSG_NO_ALERT_FOUND_AFTER_X_SEC, timeOut));
                return false;
            }
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_WAIT_FOR_ALERT)
    }

    /**
     * Verify if alert presents
     *
     * @param timeout
     *            timeout waiting for alert to present
     * @param flowControl
     * @return true if alert is present and false if alert is not present
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ALERT)
    public static boolean verifyAlertPresent(int timeOut, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            timeOut = WebUiCommonHelper.checkTimeout(timeOut);
            boolean isAlertPresent = DriverFactory.waitForAlert(timeOut);
            if (isAlertPresent) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ALERT_IS_PRESENT_AFTER_X_SEC, timeOut));
                return true;
            } else {
                WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_NO_ALERT_FOUND_AFTER_X_SEC, timeOut), flowControl, null, true);
                return false;
            }
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_VERIFY_ALERT_PRESENT)
    }

    /**
     * Verify if alert does not present
     *
     * @param timeout
     *            timeout waiting for alert to not present
     * @param flowControl
     * @return true if alert is not present and false if alert is present
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ALERT)
    public static boolean verifyAlertNotPresent(int timeOut, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            timeOut = WebUiCommonHelper.checkTimeout(timeOut);
            boolean isAlertPresent = DriverFactory.waitForAlert(timeOut);
            if (isAlertPresent) {
                WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ALERT_IS_PRESENT_AFTER_X_SEC, timeOut), flowControl, null, true);
                return false;
            } else {
                logger.logPassed(MessageFormat.format(StringConstants.KW_MSG_NO_ALERT_FOUND_AFTER_X_SEC, timeOut));
                return true;
            }
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_VERIFY_ALERT_NOT_PRESENT)
    }

    /**
     * Verify if the given texts present anywhere in the page source
     *
     * @param text
     *            to be verified if existing anywhere in page source
     * @param isRegex
     *             true if text is regular expression; otherwise, false
     * @param flowControl
     * @return true if text presents anywhere in the page source; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static boolean verifyTextPresent(String text, boolean isRegex, FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        WebUIKeywordMain.runKeyword({
            boolean isContains = WebUiCommonHelper.isTextPresent(DriverFactory.getWebDriver(), text, isRegex);
            if (isContains) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TXT_X_IS_PRESENT_ON_PAGE_Y, text, regularExpressionLog));
            } else {
                WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_TXT_X_IS_NOT_PRESENT_ON_PAGE_Y, text, regularExpressionLog), flowControl, null,
                        false);
            }
            return isContains;
        } , flowControl, true, (text != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_TXT_X_IS_PRESENT_Y, text, regularExpressionLog)
        : StringConstants.KW_MSG_CANNOT_VERIFY_TXT_IS_PRESENT)
    }

    /**
     * Verify if the given texts do NOT present anywhere in the page source
     *
     * @param text
     *         text to be verified if NOT presenting anywhere in the page source
     * @param isRegex
     *         true if text is regular expression; otherwise, false
     * @param flowControl
     * @return true if text does NOT present anywhere in the page source; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static boolean verifyTextNotPresent(String text, boolean isRegex, FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        WebUIKeywordMain.runKeyword({
            boolean isContains = WebUiCommonHelper.isTextPresent(DriverFactory.getWebDriver(), text, isRegex);
            if (isContains) {
                WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_TXT_X_IS_PRESENT_ON_PAGE_Y, text, regularExpressionLog), flowControl, null, false);
            } else {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TXT_X_IS_NOT_PRESENT_ON_PAGE, text) + regularExpressionLog);
            }
            return !isContains;
        } , flowControl, true,  (text != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_TXT_IS_NOT_PRESENT_Y, text, regularExpressionLog)
        : StringConstants.KW_MSG_CANNOT_VERIFY_TXT_IS_NOT_PRESENT)
    }

    /**
     * Switch to the window with given title.
     *
     * @param title
     *            title of the window to switch to
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static void switchToWindowTitle(String title, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_TITLE);
            if (title == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_TITLE_IS_NULL);
            }
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SWITCHING_TO_WINDOW_W_TITLE_X, title));
            boolean switched = WebUiCommonHelper.switchToWindowUsingTitle(DriverFactory.getWebDriver(), title);
            if (switched) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SWITCHED_TO_WINDOW_W_TITLE_X, title));
            } else {
                WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_CANNOT_FIND_WINDOW_W_TITLE_X, title), flowControl, null, true);
            }
        } , flowControl, true, (title != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SWITCH_TO_WINDOW_W_TITLE_X, title)
        : StringConstants.KW_MSG_CANNOT_SWITCH_TO_WINDOW_TITLE)
    }

    /**
     * Close the window with given title.
     *
     * @param title
     *            title of the window to close
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static void closeWindowTitle(String title, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_TITLE);
            if (title == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_TITLE_IS_NULL);
            }
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CLOSING_WINDOW_W_TITLE_X, title));
            boolean switched = WebUiCommonHelper.closeWindowUsingTitle(DriverFactory.getWebDriver(), title);
            if (switched) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_CLOSED_WINDOW_W_TITLE_X, title));
            } else {
                WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_CANNOT_FIND_WINDOW_W_TITLE_X, title), flowControl, null, true);
            }
        } , flowControl, true, (title != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_CLOSE_WINDOW_W_TITLE_X, title)
        : StringConstants.KW_MSG_CANNOT_CLOSE_WINDOW_TITLE)
    }

    /**
     * Switch to the window with given url.
     *
     * @param url
     *            url of the window to switch to
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static void switchToWindowUrl(String url, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_URL);
            if (url == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_URL_IS_NULL);
            }
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SWITCHING_TO_WINDOW_W_URL_X, url));
            boolean switched = WebUiCommonHelper.switchToWindowUsingUrl(DriverFactory.getWebDriver(), url);
            if (switched) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SWITCHED_TO_WINDOW_W_URL_X, url));
            } else {
                WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_CANNOT_FIND_WINDOW_W_URL_X, url), flowControl, null, true);
            }
        } , flowControl, true, (url != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SWITCH_TO_WINDOW_W_URL_X, url)
        : StringConstants.KW_MSG_CANNOT_SWITCH_TO_WINDOW_URL)
    }

    /**
     * Close the window with given url.
     *
     * @param url
     *            url of the window to close
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static void closeWindowUrl(String url, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_URL);
            if (url == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_URL_IS_NULL);
            }
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CLOSING_WINDOW_W_URL_X, url));
            boolean switched = WebUiCommonHelper.closeWindowUsingUrl(DriverFactory.getWebDriver(), url);
            if (switched) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_CLOSED_WINDOW_W_URL_X, url));
            } else {
                WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_CANNOT_FIND_WINDOW_W_URL_X, url), flowControl, null, true);
            }
        } , flowControl, true, (url != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_CLOSE_WINDOW_W_URL_X, url)
        : StringConstants.KW_MSG_CANNOT_CLOSE_WINDOW_URL)
    }

    /**
     * Switch to the window with given index.
     *
     * @param index
     *            the index of the window to switch to, index is 0-based number
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static void switchToWindowIndex(Object index, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_INDEX);
            if (index == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_INDEX_IS_NULL);
            }
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_WITCHING_WINDOW_W_IDX_X, index));
            boolean switched = WebUiCommonHelper.switchToWindowUsingIndex(DriverFactory.getWebDriver(),
                    Integer.parseInt(String.valueOf(index)));
            if (switched) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SWITCHED_WINDOW_W_IDX_X, index));
            } else {
                WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_CANNOT_FIND_WINDOW_W_IDX_X, index), flowControl, null, true);
            }
        } , flowControl, true, (index != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SWITCH_TO_WINDOW_W_IDX_X, index)
        : StringConstants.KW_MSG_CANNOT_SWITCH_TO_WINDOW_IDX)
    }

    /**
     * Close window with the given index.
     *
     * @param index
     *            the index of the window to close, index is 0-based number
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static void closeWindowIndex(Object index, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_INDEX);
            if (index == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_INDEX_IS_NULL);
            }
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CLOSING_WINDOW_W_IDX_X, index));
            boolean switched = WebUiCommonHelper.closeWindowUsingIndex(DriverFactory.getWebDriver(),
                    Integer.parseInt(String.valueOf(index)));
            if (switched) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_CLOSED_WINDOW_W_IDX_X, index));
            } else {
                WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_CANNOT_FIND_WINDOW_W_IDX_X, index), flowControl, null, true);
            }
        } , flowControl, true, (index != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_CLOSE_WINDOW_W_IDX_X, index)
        : StringConstants.KW_MSG_CANNOT_CLOSE_WINDOW_IDX)
    }

    /**
     * Count the total number of options the given web element has
     *
     * @param to
     *         represent a web element
     * @param flowControl
     * @return the total number of options
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static int getNumberOfTotalOption(TestObject to, FailureHandling flowControl) throws StepFailedException {
        def result = WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_COUNTING_TOTAL_OPTS_OF_OBJ_X, to.getObjectId()));
                Select select = new Select(webElement);
                int num = select.getOptions().size();
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_X_OPTS_OF_OBJ_Y_COUNTED, Integer.toString(num), to.getObjectId()));
                return num;
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return 0;
        } , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_GET_TOTAL_OPTS_OF_OBJ_X, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_GET_TOTAL_OPTS_OF_OBJ)
        return Integer.valueOf(result.toString());
    }

    /**
     * Count the number of options which are being selected the given web element has.
     *
     * @param to
     *         represent a web element
     * @param flowControl
     * @return number the number of selected options
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static int getNumberOfSelectedOption(TestObject to, FailureHandling flowControl) throws StepFailedException {
        def result = WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                Select select = new Select(webElement);
                int num = 0;
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_COUNTING_TOTAL_SELECTED_OPTS_OF_OBJ_X, to.getObjectId()));
                List<WebElement> elements = select.getOptions();
                for (WebElement child : elements) {
                    if (child.isSelected()) {
                        num++;
                    }
                }
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_X_SELECTED_OPTS_OF_OBJ_X_COUNTED, Integer.toString(num), to.getObjectId()));
                return num;
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return 0;
        } , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_GET_NO_OF_SELECTED_OPTS_OF_OBJ_X, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_GET_NO_OF_SELECTED_OPTS_OF_OBJ)
        return Integer.valueOf(result.toString());
    }

    /**
     * Verify if the option with the given label (displayed text) presents
     *
     * @param to
     *         represent a web element
     * @param label
     *         displayed texts of the options to be verified if existing
     * @param isRegex
     *         true if label is regular expression, false if not
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if all options with given displayed texts exist; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static boolean verifyOptionPresentByLabel(TestObject to, String label, boolean isRegex, int timeOut,
            FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_LBL);
                if (label == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_LBL_CANNOT_BE_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement webElement = findWebElement(to, timeOut);
                int numPresent = WebUiCommonHelper.getNumberOfOptionByLabel(new Select(webElement), label, isRegex,
                        to.getObjectId());

                if (numPresent <= 0) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_THERE_IS_NO_OPT_W_LBL_X_PRESENT_IN_OBJ_Y, label, to.getObjectId(), regularExpressionLog), flowControl, null, true);
                } else {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_X_OPTS_W_LBL_Y_PRESENTED_IN_OBJ_Z, numPresent, label, to.getObjectId(), regularExpressionLog));
                }
                return numPresent > 0;
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        } , flowControl, true, (to != null && label != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_PRESENT_BY_LBL_X_IN_OBJ_Y, label, to.getObjectId(), regularExpressionLog)
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_PRESENT_BY_LBL_X, regularExpressionLog))
    }

    /**
     * Verify if the options with the given value present.
     *
     * @param to
     *         represent a web element.
     * @param value
     *         value of the options to be verified if presenting.
     * @param isRegex
     *         true if value is regular expression, false by default.
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if all options with given value present; otherwise, false.
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static boolean verifyOptionPresentByValue(TestObject to, String value, boolean isRegex, int timeOut,
            FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_VAL);
                if (value == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_VAL_CANNOT_BE_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement webElement = findWebElement(to, timeOut);
                int numPresent = WebUiCommonHelper.getNumberOfOptionByValue(new Select(webElement), value, isRegex,
                        to.getObjectId());

                if (numPresent <= 0) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_THERE_IS_NO_OPT_W_VAL_X_PRESENT_IN_OBJ_Y, value, to.getObjectId(), regularExpressionLog), flowControl, null, true);
                } else {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_X_OPTS_W_VAL_Y_PRESENTED_IN_OBJ_Z, numPresent, value, to.getObjectId(), regularExpressionLog));
                }
                return numPresent > 0;
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        } , flowControl, true, (to != null && value != null) ?
        MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_PRESENT_BY_VAL_X_IN_OBJ_Y, value, to.getObjectId(), regularExpressionLog)
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_PRESENT_BY_VAL_X, regularExpressionLog))
    }

    /**
     * Verify if the options with the given displayed texts do not exist.
     *
     * @param to
     *         represent a web element
     * @param label
     *          displayed texts of the options to be verified if not existing
     * @param isRegex
     *         true if label is regular expression, false by default
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if options with given displayed text do not present; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static boolean verifyOptionNotPresentByLabel(TestObject to, String label, boolean isRegex, int timeOut,
            FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            int numPresent = 0;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_LBL);
                if (label == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_LBL_CANNOT_BE_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement webElement = findWebElement(to, timeOut);
                numPresent = WebUiCommonHelper.getNumberOfOptionByLabel(new Select(webElement), label, isRegex,
                        to.getObjectId());
                if (numPresent == 0) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_THERE_IS_NO_OPT_W_LBL_X_PRESENT_IN_OBJ_Y, label, to.getObjectId(), regularExpressionLog));
                } else {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_X_OPTS_W_LBL_Y_PRESENTED_IN_OBJ_Z, numPresent, label, to.getObjectId(), regularExpressionLog),
                            flowControl, null, true);
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
                return numPresent == 0;
            }
        } , flowControl, true, (to != null && label != null) ? \
					MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_NOT_PRESENT_BY_LBL_X_IN_OBJ_Y, label, to.getObjectId(), regularExpressionLog)
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_NOT_PRESENT_BY_LBL_X, regularExpressionLog))
    }

    /**
     * Verify if the options with the given value do not present.
     *
     * @param to
     *         represent a web element
     * @param value
     *         value of the options to be verified if NOT presenting.
     * @param isRegex
     *         true if label is regular expression, false by default.
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if all options with given value do not present; otherwise, false.
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static boolean verifyOptionNotPresentByValue(TestObject to, String value, boolean isRegex, int timeOut,
            FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_VAL);
                if (value == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_VAL_CANNOT_BE_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement webElement = findWebElement(to, timeOut);
                int numPresent = WebUiCommonHelper.getNumberOfOptionByValue(new Select(webElement), value, isRegex,
                        to.getObjectId());

                if (numPresent == 0) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_THERE_IS_NO_OPT_W_VAL_X_PRESENT_IN_OBJ_Y, value, to.getObjectId(), regularExpressionLog));
                } else {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_X_OPTS_W_VAL_Y_PRESENTED_IN_OBJ, numPresent, value, to.getObjectId(), regularExpressionLog),
                            flowControl, null, true);
                }
                return numPresent == 0;
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        } , flowControl, true, (to != null && value != null) ?
        MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_NOT_PRESENT_BY_VAL_X_IN_OBJ_Y, value, to.getObjectId(), regularExpressionLog)
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_NOT_PRESENT_BY_VAL_X, regularExpressionLog))
    }

    /**
     * Verify if the options with the given displayed texts are selected.
     *
     * @param to
     *         represent a web element
     * @param label
     *         displayed text of the option to be verified if being selected
     * @param isRegex
     *         true if value is regular expression, false by default.
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if all options with given displayed texts are selected; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static boolean verifyOptionSelectedByLabel(TestObject to, String label, boolean isRegex, int timeOut,
            FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_LBL);
                if (label == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_LBL_CANNOT_BE_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement webElement = findWebElement(to, timeOut);
                Select select = new Select(webElement);
                int numLabelOptions = WebUiCommonHelper.getNumberOfOptionByLabel(select, label, isRegex, to.getObjectId());
                int numSelectedOptions = WebUiCommonHelper.getNumberOfSelectedOptionByLabel(select, label, isRegex,
                        to.getObjectId());

                if (numLabelOptions == 0) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_THERE_IS_NO_OPT_W_LBL_X_PRESENT_IN_OBJ_Y, label, to.getObjectId(), regularExpressionLog), flowControl, null, true);
                } else if (numSelectedOptions < numLabelOptions) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_ONLY_X_IN_Y_OPTS_W_LBL_Z_SELECTED_IN_OBJ, numSelectedOptions, numLabelOptions, label, to.getObjectId(), regularExpressionLog), flowControl,
                            null, true);
                } else {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SELECTED_ALL_OPT_W_LBL_X_IN_OBJ_Y, label, to.getObjectId(), regularExpressionLog));
                }
                return (numLabelOptions > 0 && (numSelectedOptions == numLabelOptions));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        } , flowControl, true, (to != null && label != null) ?
        MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_SELECTED_BY_LBL_X_IN_OBJ_Y, label, to.getObjectId(), regularExpressionLog)
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_SELECTED_BY_LBL_X, regularExpressionLog))
    }

    /**
     * Verify if the options with the given value are selected.
     *
     * @param to
     *         represent a web element
     * @param value
     *         value of the options to be verified if being selected.
     * @param isRegex
     *         true if value is regular expression, false by default.
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if all options with given value are selected; otherwise, false.
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static boolean verifyOptionSelectedByValue(TestObject to, String value, boolean isRegex, int timeOut,
            FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_VAL);
                if (value == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_VAL_CANNOT_BE_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement webElement = findWebElement(to, timeOut);
                Select select = new Select(webElement);
                int numValueOptions = WebUiCommonHelper.getNumberOfOptionByValue(select, value, isRegex, to.getObjectId());
                int numSelectedOptions = WebUiCommonHelper.getNumberOfSelectedOptionByValue(select, value, isRegex,
                        to.getObjectId());

                if (numValueOptions == 0) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_THERE_IS_NO_OPT_W_VAL_X_PRESENT_IN_OBJ_Y, value, to.getObjectId(), regularExpressionLog), flowControl, null, true);
                } else if (numSelectedOptions < numValueOptions) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_ONLY_X_IN_Y_OPTS_W_VAL_Z_SELECTED_IN_OBJ, numSelectedOptions, numValueOptions, value, to.getObjectId(), regularExpressionLog), flowControl,
                            null, true);
                } else {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SELECTED_ALL_OPT_W_VAL_X_IN_OBJ_Y, value, to.getObjectId(), regularExpressionLog));
                }
                return (numValueOptions > 0 && (numSelectedOptions == numValueOptions));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        } , flowControl, true, (to != null && value != null) ?
        MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_SELECTED_BY_VAL_X_IN_OBJ_Y, value, to.getObjectId(), regularExpressionLog)
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_SELECTED_BY_VAL_X, regularExpressionLog))
    }

    /**
     * Verify if the options with the given displayed texts are not selected.
     *
     * @param to
     *         represent a web element
     * @param label
     *         labels displayed texts of the options to be verified if not being selected.
     * @param isRegex
     *         true if label is regular expression, false by default.
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if all options with given displayed texts are not selected; otherwise, false.
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static boolean verifyOptionNotSelectedByLabel(TestObject to, String label, boolean isRegex, int timeOut,
            FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_LBL);
                if (label == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_LBL_CANNOT_BE_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement webElement = findWebElement(to, timeOut);
                Select select = new Select(webElement);
                int numLabelOptions = WebUiCommonHelper.getNumberOfOptionByLabel(select, label, isRegex, to.getObjectId());
                int numNotSelectedOptions = WebUiCommonHelper.getNumberOfNotSelectedOptionByLabel(select, label, isRegex,
                        to.getObjectId());

                if (numLabelOptions == 0) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_THERE_IS_NO_OPT_W_LBL_X_PRESENT_IN_OBJ_Y, label, to.getObjectId(), regularExpressionLog),
                            flowControl, null, true);
                } else if (numNotSelectedOptions < numLabelOptions) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_ONLY_X_IN_Y_OPTS_W_LBL_Z_UNSELECTED_IN_OBJ, numNotSelectedOptions, numLabelOptions, label, to.getObjectId(), regularExpressionLog),
                            flowControl, null, true);
                } else {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_UNSELECTED_ALL_OPT_W_LBL_X_IN_OBJ_Y, label, to.getObjectId(), regularExpressionLog));
                }
                return (numLabelOptions > 0 && (numNotSelectedOptions == numLabelOptions));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null && label != null) ?
        MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_UNSELECTED_BY_LBL_X_IN_OBJ_Y, label, to.getObjectId(), regularExpressionLog)
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_UNSELECTED_BY_LBL_X, regularExpressionLog))
    }

    /**
     * Verify if the options with the given value are not selected.
     *
     * @param to
     *         represent a web element
     * @param value
     *         value of the options to be verified if not being selected.
     * @param isRegex
     *         true if label is regular expression, false by default.
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if all options with given value are not selected; otherwise, false.
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static boolean verifyOptionNotSelectedByValue(TestObject to, String value, boolean isRegex, int timeOut,
            FailureHandling flowControl) throws StepFailedException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_VAL);
                if (value == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_VAL_CANNOT_BE_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement webElement = findWebElement(to, timeOut);
                Select select = new Select(webElement);
                int numValueOptions = WebUiCommonHelper.getNumberOfOptionByValue(select, value, isRegex, to.getObjectId());
                int numNotSelectedOptions = WebUiCommonHelper.getNumberOfNotSelectedOptionByValue(select, value, isRegex,
                        to.getObjectId());

                if (numValueOptions == 0) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_THERE_IS_NO_OPT_W_VAL_X_PRESENT_IN_OBJ_Y, value, to.getObjectId(), regularExpressionLog), flowControl, null, true);
                } else if (numNotSelectedOptions < numValueOptions) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_ONLY_X_IN_Y_OPTS_W_VAL_Z_UNSELECTED_IN_OBJ, numNotSelectedOptions, numValueOptions, value, to.getObjectId(), regularExpressionLog),
                            flowControl, null, true);
                } else {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_UNSELECTED_ALL_OPT_W_VAL_X_IN_OBJ_Y, value, to.getObjectId(), regularExpressionLog));
                }
                return (numValueOptions > 0 && (numNotSelectedOptions == numValueOptions));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null && value != null) ?
        MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_UNSELECTED_BY_VAL_X_IN_OBJ_Y, value, to.getObjectId(), regularExpressionLog)
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_UNSELECTED_BY_VAL_X, regularExpressionLog))
    }

    /**
     * Verify if the options at the given indices are selected.
     *
     * @param to
     *         represent a web element
     * @param range
     *            list of indexes of the options to be verified if being
     *            selected
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if all options at given indices are selected; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static boolean verifyOptionSelectedByIndex(TestObject to, Object range, int timeOut,
            FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_INDEX_RANGE);
                if (range == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_INDEX_RANGE_CANNOT_BE_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement webElement = findWebElement(to, timeOut);
                Select select = new Select(webElement);
                Integer[] indexes = WebUiCommonHelper.indexRangeToArray(String.valueOf(range));
                WebUiCommonHelper.checkSelectIndex(indexes, select);
                int numSelectedOptions = WebUiCommonHelper.getNumberOfSelectedOptionByIndex(select, indexes,
                        to.getObjectId());

                if (numSelectedOptions < indexes.length) {
                    WebUIKeywordMain.stepFailed(
                            MessageFormat.format(StringConstants.KW_MSG_ONLY_X_IN_Y_OPTS_W_IDX_RANGE_Z_SELECTED_IN_OBJ, numSelectedOptions, indexes.length, WebUiCommonHelper.integerArrayToString(indexes), to.getObjectId()),
                            flowControl, null, true);

                    return false;
                } else {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SELECTED_ALL_OPT_W_IDX_RANGE_IN_X_IN_OBJ_Y, range, to.getObjectId()));
                    return true;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null && range != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_SELECTED_IN_IDX_RANGE_X_IN_OBJ, range, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_SELECTED_IN_IDX_RANGE)
    }

    /**
     * Verify if the options at the given indices are not selected
     *
     * @param to
     *         represent a web element
     * @param indexes
     *            the indexes of the options to be verified if not being
     *            selected
     * @param timeOut
     *         system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if all options at given indices are not selected; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
    public static boolean verifyOptionNotSelectedByIndex(TestObject to, Object range, int timeOut,
            FailureHandling flowControl) throws StepFailedException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_INDEX_RANGE);
                if (range == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_INDEX_RANGE_CANNOT_BE_NULL);
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement webElement = findWebElement(to, timeOut);
                Select select = new Select(webElement);
                Integer[] indexes = WebUiCommonHelper.indexRangeToArray(String.valueOf(range));
                WebUiCommonHelper.checkSelectIndex(indexes, select);
                int numNotSelectedOptions = WebUiCommonHelper.getNumberOfNotSelectedOptionByIndex(select, indexes,
                        to.getObjectId());

                if (numNotSelectedOptions < indexes.length) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_ONLY_X_IN_Y_OPTS_W_IDX_RANGE_Z_UNSELECTED_IN_OBJ, numNotSelectedOptions,
                            indexes.length, WebUiCommonHelper.integerArrayToString(indexes), to.getObjectId()), flowControl, null, true);
                    return false;
                } else {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_UNSELECTED_ALL_OPT_W_IDX_RANGE_IN_X_IN_OBJ_Y,
                            WebUiCommonHelper.integerArrayToString(indexes), to.getObjectId()));
                    return true;
                }
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, (to != null && range != null) ?
        MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_NOT_SELECTED_IN_IDX_RANGE_X_IN_OBJ, range, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_NOT_SELECTED_IN_IDX_RANGE)
    }

    /**
     * Use this keyword to switch back to default Window, after deal with some
     * framed element
     *
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_FRAME)
    public static void switchToDefaultContent(FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({ WebUiCommonHelper.switchToDefaultContent(); } , flowControl, true, StringConstants.KW_MSG_CANNOT_SWITCH_TO_DEFAULT_CONTENT)
    }

    /**
     * Delete all cookies of all windows.
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
    public static void deleteAllCookies(FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            WebDriver webDriver = null;
            try {
                webDriver = DriverFactory.getWebDriver();
            } catch (WebDriverException e) {
                if (e instanceof BrowserNotOpenedException) {
                    logger.logWarning(StringConstants.KW_LOG_WARNING_CANNOT_DEL_COOKIES_BC_BROWSER_CLOSED);
                } else {
                    throw e;
                }
            }
            if (webDriver != null) {
                webDriver.manage().deleteAllCookies();
            }
            logger.logPassed(StringConstants.KW_LOG_PASSED_DEL_ALL_COOKIE);
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_DEL_ALL_COOKIES)
    }

    /**
     * Wait for the web page to load within the given time in second unit.
     *
     * @param seconds
     *         the number of seconds to wait
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
    public static void waitForPageLoad(int seconds, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            seconds = WebUiCommonHelper.checkTimeout(seconds);
            WebDriver webDriver = DriverFactory.getWebDriver();
            if (webDriver != null) {
                Wait<WebDriver> wait = new WebDriverWait(webDriver, seconds);
                wait.until(new Function<WebDriver, Boolean>() {
                            public Boolean apply(WebDriver driver) {
                                try {
                                    return String.valueOf(
                                            ((JavascriptExecutor) driver).executeScript("return document.readyState")).equals(
                                            "complete");
                                } catch (WebDriverException exception) {
                                    // if ajax calls make page reload html elements
                                    // during waiting for page to load
                                    if (exception.getMessage().startsWith("waiting for doc.body failed")) {
                                        return false;
                                    }
                                    throw exception;
                                }
                            }
                        });
            }
            logger.logPassed(StringConstants.KW_MSG_PASSED_WAIT_FOR_PAGE_LOAD);
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_WAIT_FOR_PAGE_LOAD)
    }

    /**
     * Modify property of test object. If the property is not existed then the
     * property will be created. If the changed value is null then the existed
     * value will not be changed. Use when test object has attributes changing
     * in runtime. This keyword does not modify the object saved in Object
     * Repository, instead, it creates another test object, modify and return
     * this test object. Hence, users must use a variable to get the returned
     * object.
     *
     * @param testObject
     *          represent a web element
     * @param propertyName
     *          name of the property, for example, xpath, id, name,... 
     *          <p>If the property already exists in the object, the keyword will modify its related artifacts; 
     *          if not, the keyword will add new property.
     * @param matchCondition
     *          condition to match property name with property value, for example, equals, not equals,... 
     *          <p>In case the property already exists, input null to this argument to keep the old value of match condition.
     * @param modifyValue
     *          value of the property. 
     *          <p>In case the property already exists, input null to this argument to keep the old property value.
     * @param isActive
     *          true if the property is checked (used to find the test object); otherwise, false. 
     *          <p>In case the property already exists, input null to this argument to keep the old value.
     * @param flowControl
     * @return the newly created TestObject
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static TestObject modifyObjectProperty(TestObject testObject, String propertyName, String matchCondition,
            String modifyValue, boolean isActive, FailureHandling flowControl) {
        Object result = WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOF_INFO_CHK_TO);
            if (testObject == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_TO_IS_NULL);
            }
            logger.logInfo(StringConstants.KW_LOG_INFO_CHK_PROP_NAME);
            if (propertyName == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_PROP_NAME_IS_NULL);
            }
            TestObjectProperty property = testObject.findProperty(propertyName);
            if (property == null) {
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_NOT_FOUND_PROP_CREATING_NEW_PROP, propertyName, testObject.getObjectId(), propertyName));
                property = new TestObjectProperty(propertyName, ConditionType.EQUALS, null, false);
                testObject.addProperty(property);
            }
            logger.logInfo(StringConstants.KW_LOG_INFO_CHK_MODIFY_VAL);
            if (modifyValue == null) {
                logger.logInfo(StringConstants.KW_LOG_INFO_MODIFY_VAL_NULL_SO_NOT_MODIFYING_VAL);
            } else {
                property.setValue(modifyValue);
            }

            logger.logInfo(StringConstants.KW_LOG_INFO_CHK_MATCH_COND);
            if (matchCondition == null) {
                logger.logInfo(StringConstants.KW_LOG_INFO_MATCH_COND_NULL_SO_NOT_MODIFYING_MATCH_COND);
            } else {
                ConditionType conditionType = ConditionType.fromValue(matchCondition);
                if (conditionType == null) {
                    StringBuilder conditionList = new StringBuilder();
                    boolean isFirst = true;
                    for (ConditionType condition : ConditionType.values()) {
                        if (!isFirst) {
                            conditionList.append(", ");
                        }
                        conditionList.append("'");
                        conditionList.append(condition.toString());
                        conditionList.append("'");
                        isFirst = false;
                    }
                    logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_INVALID_MATCH_COND, conditionList.toString()));
                } else {
                    property.setCondition(conditionType);
                }
            }
            property.setActive(isActive);
            logger.logPassed(StringConstants.KW_MSG_MODIFY_OBJ_PROP_SUCESSFULLY);
            return testObject;
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_MODIFY_OBJ_PROP)
        if (result instanceof TestObject) {
            return (TestObject) result;
        }
        return null;
    }

    /**
     * Remove existing property of test object. Use when test object 
     * has attributes changing in runtime. This keyword does not 
     * modify the object saved in Object Repository, instead, it creates 
     * another test object, modify and return this test object. Hence, 
     * users must use a variable to store the returned object.
     *
     * @param testObject
     *          represent a web element
     * @param propertyName
     *          name of the property, for example, xpath, id, name,...
     * @param flowControl
     * @return
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static TestObject removeObjectProperty(TestObject testObject, String propertyName, FailureHandling flowControl) {
        Object result = WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.KW_LOF_INFO_CHK_TO);
            if (testObject == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_TO_IS_NULL);
            }
            logger.logInfo(StringConstants.KW_LOG_INFO_CHK_PROP_NAME);
            if (propertyName == null) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_PROP_NAME_IS_NULL);
            }
            TestObjectProperty property = testObject.findProperty(propertyName);
            if (property == null) {
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_NOT_FOUND_PROP_DO_NOTHING, propertyName, testObject.getObjectId(), propertyName));
            } else {
                logger.logInfo(MessageFormat.format(StringConstants.KW_MSG_REMOVE_OBJ_PROP_X_OF_OBJ_Y, testObject.getObjectId(), propertyName));
                testObject.getProperties().remove(property);
            }
            logger.logPassed(StringConstants.KW_MSG_REMOVE_OBJ_PROP_SUCESSFULLY);
            return testObject;
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_REMOVE_OBJ_PROP)
        if (result instanceof TestObject) {
            return (TestObject) result;
        }
        return null;
    }

    /***
     * Drag an object and drop it to another object
     *
     * @param sourceObject
     *            the source object
     * @param destinationObject
     *            the destination object
     * @param flowControl
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void dragAndDropToObject(TestObject sourceObject, TestObject destinationObject,
            FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                logger.logInfo(StringConstants.KW_LOG_INFO_CHK_SRC_OBJ);
                if (sourceObject == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_SRC_OBJ_IS_NULL);
                }
                logger.logInfo(StringConstants.KW_LOG_INFO_CHK_DEST_OBJ);
                if (destinationObject == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_DEST_OBJ_IS_NULL);
                }
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_START_DRAGGING_OBJ_W_ID_X_TO_OBJ_W_ID_Y, sourceObject.getObjectId(), destinationObject));

                Actions builder = new Actions(DriverFactory.getWebDriver());
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(sourceObject);
                builder.clickAndHold(findWebElement(sourceObject));
                builder.perform();
                Thread.sleep(250);
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(destinationObject);

                WebElement destinationWebElement = findWebElement(destinationObject);
                builder.moveToElement(destinationWebElement, 5, 5);
                builder.perform();
                Thread.sleep(250);
                builder.release(destinationWebElement);
                builder.perform();

                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DRAGGED_OBJ_W_ID_X_TO_OBJ_W_ID_Y, sourceObject.getObjectId(), destinationObject))
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_DRAG_AND_DROP_TO_OBJ)
    }

    /***
     * Drag an object and drop it to an offset location
     *
     * @param sourceObject
     *            the source object
     * @param xOffset
     *            x offset
     * @param yOffset
     *            y offset
     * @param flowControl
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void dragAndDropByOffset(TestObject sourceObject, int xOffset, int yOffset,
            FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                logger.logInfo(StringConstants.KW_LOG_INFO_CHK_SRC_OBJ);
                if (sourceObject == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_SRC_OBJ_IS_NULL);
                }
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_START_DRAGGING_OBJ_BY_OFFSET_DISTANCE_X_Y, sourceObject.getObjectId(), xOffset, yOffset));
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(sourceObject);
                (new Actions(DriverFactory.getWebDriver())).dragAndDropBy(findWebElement(sourceObject), xOffset, yOffset)
                        .perform();
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DRAGGED_OBJ_BY_OFFSET_DISTANCE_X_Y, sourceObject.getObjectId(), xOffset, yOffset));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_DRAG_AND_DROP_BY_OFFSET_DISTANCE)
    }

    /**
     * Navigate to a page that requires authentication. System will enter username and password
     * @param url
     *          url of the page to navigate (optional)
     * @param userName
     *          username to authenticate
     * @param password
     *          password to authenticate
     * @param timeout 
     *          time to wait since navigating to the page until entering username
     * @param flowControl
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
    public static void authenticate(final String url, String userName, String password, int timeout,
            FailureHandling flowControl) {
        //public static void authenticate(String userName, String password, FailureHandling flowControl) {

        WebUIKeywordMain.runKeyword({

            Thread navigateThread = null;

            try{

                if (System.getProperty("os.name") == null || !System.getProperty("os.name").toLowerCase().contains("win")) {
                    throw new Exception("Unsupported platform (only support Windows)");
                }

                if(DriverFactory.getExecutedBrowser() != WebUIDriverType.IE_DRIVER &&
                DriverFactory.getExecutedBrowser() != WebUIDriverType.FIREFOX_DRIVER &&
                DriverFactory.getExecutedBrowser() != WebUIDriverType.CHROME_DRIVER){
                    throw new Exception("Unsupported browser (only support IE, FF, Chrome)");
                }

                timeout = WebUiCommonHelper.checkTimeout(timeout);

                KeywordLogger.getInstance().logInfo(StringConstants.KW_LOG_INFO_CHECKING_USERNAME);
                if (userName == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_USERNAME_IS_NULL);
                }
                KeywordLogger.getInstance().logInfo(StringConstants.KW_LOG_INFO_CHECKING_PASSWORD);
                if (password == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_PASSWORD_IS_NULL);
                }

                WebDriver driver = DriverFactory.getWebDriver();

                if(url != null && !url.equals("")){
                    navigateThread = new Thread() {
                                public void run() {
                                    driver.get(url);
                                }
                            };
                    navigateThread.start();
                    //Wait for secured page is fully loaded
                    Thread.sleep(timeout * 1000);

                    /*if (DriverFactory.getExecutedBrowser() == WebUIDriverType.IE_DRIVER) {
                     if(DriverFactory.getWebDriver().getTitle().trim().startsWith("Certificate Error")){
                     DriverFactory.getWebDriver().get("javascript:{document.getElementById('overridelink').click();}");
                     Thread.sleep(3000);
                     }
                     }*/
                }

                // send username and pasword to authentication popup
                //screenUtil.authenticate(userName, password);
                File kmsIeFolder = FileUtil.getKmsIeDriverDirectory();
                File authFolder = FileUtil.getAuthenticationDirectory();
                File userNameParamFile = new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "set user name" + File.separator + "paramter0");
                File passwordParamFile = new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "set password" + File.separator + "paramter0");

                //Set user name
                FileUtils.writeStringToFile(userNameParamFile, userName, false);
                String[] cmd = [kmsIeFolder.getAbsolutePath() + "/kmsie.exe", userNameParamFile.getParent()];
                Process proc = Runtime.getRuntime().exec(cmd);
                //The default timeout for this task is 10s (implemented inside KMS IE Driver)
                proc.waitFor();
                //Check result
                String resStatus = FileUtils.readFileToString(
                        new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "set user name" + File.separator + "result_status"),
                        "UTF-8");
                if(!"PASSED".equals(resStatus.trim())){
                    //Should consider to read result_message
                    String errMsg = FileUtils.readFileToString(
                            new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "set user name" + File.separator + "result_message"),
                            "UTF-8");
                    throw new Exception("Failed to set user name on Authentication dialog: " + errMsg);
                }

                //Set password
                FileUtils.writeStringToFile(passwordParamFile, password, false);
                cmd = [kmsIeFolder.getAbsolutePath() + "/kmsie.exe", passwordParamFile.getParent()];
                proc = Runtime.getRuntime().exec(cmd);
                proc.waitFor();
                resStatus = FileUtils.readFileToString(
                        new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "set password" + File.separator + "result_status"),
                        "UTF-8");
                if(!"PASSED".equals(resStatus.trim())){
                    String errMsg = FileUtils.readFileToString(
                            new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "set password" + File.separator + "result_message"),
                            "UTF-8");
                    throw new Exception("Failed to set password on Authentication dialog: " + errMsg);
                }

                //Click OK
                cmd = [kmsIeFolder.getAbsolutePath() + "/kmsie.exe", new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "click ok").getAbsolutePath()];
                proc = Runtime.getRuntime().exec(cmd);
                proc.waitFor();
                resStatus = FileUtils.readFileToString(
                        new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "click ok" + File.separator + "result_status"),
                        "UTF-8");
                if(!"PASSED".equals(resStatus.trim())){
                    String errMsg = FileUtils.readFileToString(
                            new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "click ok" + File.separator + "result_message"),
                            "UTF-8");
                    throw new Exception("Failed to click OK button on Authentication dialog: " + errMsg);
                }

                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_NAVIAGTED_TO_AUTHENTICATED_PAGE, userName, password));
            }
            finally{
                if (navigateThread != null && navigateThread.isAlive()) {
                    navigateThread.interrupt();
                }
            }
        } , flowControl, true, StringConstants.KW_MSG_CANNOT_NAV_TO_AUTHENTICATED_PAGE)
    }

    /**
     * Click on an image on the web page
     * @param to
     *       represent an image
     * @param flowControl
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_IMAGE)
    public static void clickImage(TestObject to, FailureHandling flowControl) {
        String imagePath = null;
        WebUIKeywordMain.runKeyword({
            WebUiCommonHelper.checkTestObjectParameter(to)
            imagePath = to.getImagePath();
            if (imagePath == null || imagePath.equals("")) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_NO_IMAGE_FILE_PROP_IN_OBJ);
            }
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CLICKING_ON_IMG_X, imagePath));
            // Relative path?
            if (to.getUseRelativeImagePath()) {
                String currentDirFilePath = new File(RunConfiguration.getProjectDir()).getAbsolutePath();
                imagePath = currentDirFilePath + File.separator + imagePath;
            }
            screenUtil.clickImage(imagePath);
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_CLICKED_IMG_X, imagePath));
        } , flowControl, true, (imagePath != null) ?
        MessageFormat.format(StringConstants.KW_MSG_CANNOT_TYPE_ON_IMG, imagePath) :
        StringConstants.KW_MSG_CANNOT_CLICK_ON_IMG)
    }

    /**
     * Type on an image on the web page
     * @param to
     *       represent an image
     * @param text
     *          text to type on the image
     * @param flowControl
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_IMAGE)
    public static void typeOnImage(TestObject to, String text, FailureHandling flowControl) {
        String imagePath = null;
        WebUIKeywordMain.runKeyword({
            WebUiCommonHelper.checkTestObjectParameter(to)
            if (imagePath == null || imagePath.equals("")) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_NO_IMAGE_FILE_PROP_IN_OBJ);
            }
            if (text == null) {
                throw new IllegalArgumentException(StringConstants.COMM_EXC_TEXT_IS_NULL);
            }
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_TYPING_ON_IMG_X, imagePath));
            // Relative path?
            if (to.getUseRelativeImagePath()) {
                String currentDirFilePath = new File(RunConfiguration.getProjectDir()).getAbsolutePath();
                imagePath = currentDirFilePath + File.separator + imagePath;
            }
            screenUtil.typeOnImage(imagePath, text);
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TYPED_ON_IMG_X, imagePath));
        } , flowControl, true, (imagePath != null) ?
        MessageFormat.format(StringConstants.KW_MSG_CANNOT_TYPE_ON_IMG_X, imagePath) :
        StringConstants.KW_MSG_CANNOT_TYPE_ON_IMG)
    }

    /**
     * Verify if an image is present on page
     * @param to
     *       represent an image
     * @param flowControl
     * @return true if the image if present; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_IMAGE)
    public static boolean verifyImagePresent(TestObject to, FailureHandling flowControl) throws StepFailedException {
        String imagePath = null;
        boolean exist = false;
        WebUIKeywordMain.runKeyword({
            imagePath = to.getImagePath();
            WebUiCommonHelper.checkTestObjectParameter(to)
            if (imagePath == null || imagePath.equals("")) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_NO_IMAGE_FILE_PROP_IN_OBJ);
            }
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_WAITING_FOR_IMG_X_PRESENT, imagePath));
            // Relative path?
            if (to.getUseRelativeImagePath()) {
                String currentDirFilePath = new File(RunConfiguration.getProjectDir()).getAbsolutePath();
                imagePath = currentDirFilePath + File.separator + imagePath;
            }
            exist = screenUtil.isImageExist(imagePath);
            if (exist) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_IMG_X_IS_PRESENT, imagePath));
            } else {
                WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_PASSED_IMG_X_IS_NOT_PRESENT, imagePath), flowControl, null, true)
            }
        } , flowControl, true, (imagePath != null) ?
        MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_IMG_X_PRESENT, imagePath) :
        StringConstants.KW_MSG_CANNOT_VERIFY_IMG_PRESENT)
        return exist;
    }

    /**
     * Wait for an image to be presented on page
     * @param to
     *       represent an image
     * @param timeOutInSeconds 
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if the image if present; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_IMAGE)
    public static boolean waitForImagePresent(TestObject to, int timeOutInSeconds, FailureHandling flowControl) throws StepFailedException {
        String imagePath = null;
        boolean present = false;
        WebUIKeywordMain.runKeyword({
            imagePath = to.getImagePath();
            WebUiCommonHelper.checkTestObjectParameter(to)
            if (imagePath == null || imagePath.equals("")) {
                throw new IllegalArgumentException(StringConstants.KW_EXC_NO_IMAGE_FILE_PROP_IN_OBJ);
            }
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_WAITING_FOR_IMG_X_PRESENT, imagePath));
            // Relative path?
            if (to.getUseRelativeImagePath()) {
                String currentDirFilePath = new File(RunConfiguration.getProjectDir()).getAbsolutePath();
                imagePath = currentDirFilePath + File.separator + imagePath;
            }
            present = screenUtil.waitForImagePresent(imagePath, timeOutInSeconds);
            if (present) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_IMG_X_IS_PRESENT, imagePath));
            } else {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_PASSED_IMG_X_IS_NOT_PRESENT, imagePath))
            }
        } , flowControl, true, (imagePath != null) ?
        MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_FOR_IMG_X_TOBE_PRESENT, imagePath) :
        StringConstants.KW_MSG_CANNOT_WAIT_FOR_IMG_TOBE_PRESENT)
        return present;
    }

    /**
     * Internal method to find web element by test object
     * @param to
     *      represent a web element
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @return
     *      the found web element or null if cannot find any
     * @throws IllegalArgumentException
     * @throws WebElementNotFoundException
     * @throws StepFailedException
     */
    @CompileStatic
    public static WebElement findWebElement(TestObject to, int timeOut = RunConfiguration.getTimeOut()) throws IllegalArgumentException, WebElementNotFoundException, StepFailedException {
        return WebUiCommonHelper.findWebElement(to, timeOut);
    }

    /**
     * Internal method to find web elements by test object
     * @param to
     *      represent a web element
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @return
     *      the found web elements or null if cannot find any
     * 
     * @throws IllegalArgumentException
     * @throws WebElementNotFoundException
     * @throws StepFailedException
     */
    @CompileStatic
    public static List<WebElement> findWebElements(TestObject to, int timeOut) throws WebElementNotFoundException {
        return WebUiCommonHelper.findWebElements(to, timeOut);
    }

    /**
     * Switch the current context into an iframe
     * @param to
     *      represent a web element
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return
     *      true if the current context is switched to the iframe; otherwise, false
     * @throws IllegalArgumentException
     * @throws WebElementNotFoundException
     * @throws StepFailedException
     * @throws WebDriverException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_FRAME)
    public static boolean switchToFrame(TestObject to, int timeOut, FailureHandling flowControl) throws IllegalArgumentException,
    WebElementNotFoundException, StepFailedException, WebDriverException {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SWITCHING_TO_IFRAME_X,
                    to.getObjectId()));
            WebElement frameElement = WebUiCommonHelper.findWebElement(to, timeOut);
            if (frameElement != null) {
                DriverFactory.getWebDriver().switchTo().frame(frameElement);
                isSwitchIntoFrame = true;
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SWITCHED_TO_IFRAME_X,
                        to.getObjectId()));
            }
            return isSwitchIntoFrame;
        } , flowControl, true, (to != null) ?
        MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_IMG_X_PRESENT, to.getObjectId()) :
        StringConstants.KW_LOG_FAILED_SWITCHED_TO_IFRAME)
    }

    /**
     * Take screenshot of the browser
     * @param flowControl
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static void takeScreenshot(FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            String screenFileName = FileUtil.takesScreenshot();
            if (screenFileName != null) {
                Map<String, String> attributes = new HashMap<String, String>();
                attributes.put(com.kms.katalon.core.constants.StringConstants.XML_LOG_ATTACHMENT_PROPERTY, screenFileName)
                logger.logPassed("Taking screenshot successfully", attributes);
            }
        } , flowControl, true, StringConstants.KW_LOG_WARNING_CANNOT_TAKE_SCREENSHOT)

    }

    /**
     * Upload file to an input html element with type = "file"
     * @param to 
     *    represent a web element.
     * @param fileAbsolutePath
     *       absolute path of the file on local machine
     * @param flowControl
     *       flow control
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_KEYBOARD)
    public static void uploadFile(TestObject to, String fileAbsolutePath, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to)
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_UPLOADING_FILE_X_TO_OBJ_Y, fileAbsolutePath, to.getObjectId()));
                WebElement webElement = findWebElement(to);
                webElement.sendKeys(fileAbsolutePath);
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_FILE_X_SENT_TO_OBJ_Y, fileAbsolutePath, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_UPLOAD_FILE_X_TO_OBJ_Y, fileAbsolutePath, to.getObjectId())
        : MessageFormat.format(StringConstants.KW_MSG_CANNOT_UPLOAD_FILE_X, fileAbsolutePath))
    }

    /**
     * scrolls a element into the visible area of the browser window
     * @param to
     *    represent a web element
     * @param fileAbsolutePath
     *       absolute path of the file on local machine
     * @param flowControl
     *       flow control
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void scrollToElement(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to)
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to);
                WebElement webElement = findWebElement(to);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SCROLLING_TO_OBJ_X, to.getObjectId()));
                ((JavascriptExecutor) DriverFactory.getWebDriver()).executeScript("arguments[0].scrollIntoView();", webElement);
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SCROLLING_TO_OBJ_X, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SCROLLING_TO_OBJ_X, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_SCROLLING_TO_OBJ)
    }

    /**
     * Verify if the web element is visible in current view port
     * @param to
     *      represent a web element
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element is present and visible in viewport; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementVisibleInViewport(TestObject to, int timeOut, FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            try {
                if (WebUiCommonHelper.isElementVisibleInViewport(DriverFactory.getWebDriver(), to, timeOut)) {
                    KeywordLogger.getInstance().logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_VISIBLE_IN_VIEWPORT, to.getObjectId()));
                    return true;
                }  else {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_OBJ_X_VISIBLE_IN_VIEWPORT, to.getObjectId()), flowControl, null, true);
                    return false;
                }
            } catch (WebElementNotFoundException ex) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_WARNING_OBJ_X_IS_NOT_PRESENT, to.getObjectId()));
            }
            return false;
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_VISIBLE_IN_VIEWPORT, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_VISIBLE_IN_VIEWPORT)
    }

    /**
     * Verify if the web element is NOT visible in current view port
     * @param to
     *      represent a web element
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element is present and NOT visible in viewport; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotVisibleInViewport(TestObject to, int timeOut, FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            try {
                if (WebUiCommonHelper.isElementVisibleInViewport(DriverFactory.getWebDriver(), to, timeOut)) {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_OBJ_X_NOT_VISIBLE_IN_VIEWPORT, to.getObjectId()), flowControl, null, true);
                    return false;
                }  else {
                    KeywordLogger.getInstance().logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_NOT_VISIBLE_IN_VIEWPORT, to.getObjectId()));
                    return true;
                }
            } catch (WebElementNotFoundException ex) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_WARNING_OBJ_X_IS_NOT_PRESENT, to.getObjectId()));
            }
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_NOT_VISIBLE_IN_VIEWPORT, to.getObjectId())
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_NOT_VISIBLE_IN_VIEWPORT)
    }

    /**
     * Get current viewport's width value
     * @param flowControl
     * @return current viewport's width
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static int getViewportWidth(FailureHandling flowControl) throws StepFailedException {
        return (int) WebUIKeywordMain.runKeyword({
            int viewportWidth = WebUiCommonHelper.getViewportWidth(DriverFactory.getWebDriver());
            KeywordLogger.getInstance().logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_GET_VIEWPORT_WIDTH_X, viewportWidth.toString()));
            return viewportWidth;
        }
        , flowControl, false, StringConstants.KW_MSG_CANNOT_GET_VIEWPORT_WIDTH)
    }

    /** 
     * Get current viewport's height value
     * @param flowControl
     * @return current viewport's height
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static int getViewportHeight(FailureHandling flowControl) throws StepFailedException {
        return (int) WebUIKeywordMain.runKeyword({
            int viewportHeight = WebUiCommonHelper.getViewportHeight(DriverFactory.getWebDriver());
            KeywordLogger.getInstance().logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_GET_VIEWPORT_HEIGHT_X, viewportHeight.toString()));
            return viewportHeight;
        }
        , flowControl, false, StringConstants.KW_MSG_CANNOT_GET_VIEWPORT_HEIGHT)
    }

    /**
     * Verify if the web element has an attribute with the specific name
     * @param to
     *      represent a web element
     * @param attributeName
     *      the name of the attribute to verify
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element has the attribute with the specific name; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementHasAttribute(TestObject to, String attributeName, int timeOut, FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                KeywordLogger.getInstance().logInfo(StringConstants.COMM_LOG_INFO_CHECKING_ATTRIBUTE_NAME);
                if (attributeName == null) {
                    throw new IllegalArgumentException(StringConstants.COMM_EXC_ATTRIBUTE_NAME_IS_NULL);
                }
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement foundElement = WebUiBuiltInKeywords.findWebElement(to, timeOut);
                if (foundElement.getAttribute(attributeName) != null) {
                    KeywordLogger.getInstance().logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName));
                    return true;
                }  else {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName), flowControl, null, true);
                    return false;
                }
            } catch (WebElementNotFoundException ex) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_WARNING_OBJ_X_IS_NOT_PRESENT, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName)
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_HAS_ATTRIBUTE)
    }

    /**
     * Verify if the web element doesn't have an attribute with the specific name
     * @param to
     *      represent a web element
     * @param attributeName
     *      the name of the attribute to verify
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element doesn't have the attribute with the specific name; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotHasAttribute(TestObject to, String attributeName, int timeOut, FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                KeywordLogger.getInstance().logInfo(StringConstants.COMM_LOG_INFO_CHECKING_ATTRIBUTE_NAME);
                if (attributeName == null) {
                    throw new IllegalArgumentException(StringConstants.COMM_EXC_ATTRIBUTE_NAME_IS_NULL);
                }
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement foundElement = WebUiBuiltInKeywords.findWebElement(to, timeOut);
                if (foundElement.getAttribute(attributeName) == null) {
                    KeywordLogger.getInstance().logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_NOT_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName));
                    return true;
                }  else {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_OBJ_X_NOT_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName), flowControl, null, true);
                    return false;
                }
            } catch (WebElementNotFoundException ex) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_WARNING_OBJ_X_IS_NOT_PRESENT, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_NOT_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName)
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_NOT_HAS_ATTRIBUTE)
    }

    /**
     * Verify if the web element has an attribute with the specific name and value
     * @param to
     *      represent a web element
     * @param attributeName
     *      the name of the attribute to verify
     * @param attributeValue
     *      the value of the attribute to verify
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element has the attribute with the specific name and value; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementAttributeValue(TestObject to, String attributeName, String attributeValue, int timeOut, FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                KeywordLogger.getInstance().logInfo(StringConstants.COMM_LOG_INFO_CHECKING_ATTRIBUTE_NAME);
                if (attributeName == null) {
                    throw new IllegalArgumentException(StringConstants.COMM_EXC_ATTRIBUTE_NAME_IS_NULL);
                }
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement foundElement = WebUiBuiltInKeywords.findWebElement(to, timeOut);
                if (foundElement.getAttribute(attributeName) != null) {
                    if (foundElement.getAttribute(attributeName).equals(attributeValue)) {
                        KeywordLogger.getInstance().logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_ATTRIBUTE_Y_VALUE_Z, to.getObjectId(), attributeName, attributeValue));
                        return true;
                    } else {
                        WebUIKeywordMain.stepFailed(
                                MessageFormat.format(
                                StringConstants.KW_LOG_FAILED_OBJ_X_ATTRIBUTE_Y_ACTUAL_VALUE_Z_EXPECTED_VALUE_W,
                                to.getObjectId(), attributeName, foundElement.getAttribute(attributeName), attributeValue), flowControl, null, true);
                        return false;
                    }
                }  else {
                    WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName), flowControl, null, true);
                    return false;
                }
            } catch (WebElementNotFoundException ex) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_WARNING_OBJ_X_IS_NOT_PRESENT, to.getObjectId()));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_ATTRIBUTE_Y_VALUE_Z, to.getObjectId(), attributeName, attributeValue)
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_ATTRIBUTE_VALUE)
    }

    /**
     * Wait until the given web element has an attribute with the specific name
     * @param to
     *      represent a web element
     * @param attributeName
     *      the name of the attribute to wait for
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element has the attribute with the specific name; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementHasAttribute(TestObject to, String attributeName, int timeOut, FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                KeywordLogger.getInstance().logInfo(StringConstants.COMM_LOG_INFO_CHECKING_ATTRIBUTE_NAME);
                if (attributeName == null) {
                    throw new IllegalArgumentException(StringConstants.COMM_EXC_ATTRIBUTE_NAME_IS_NULL);
                }
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement foundElement = WebUiBuiltInKeywords.findWebElement(to, timeOut);
                Boolean hasAttribute = new FluentWait<WebElement>(foundElement)
                        .pollingEvery(500, TimeUnit.MILLISECONDS).withTimeout(timeOut, TimeUnit.SECONDS)
                        .until(new Function<WebElement, Boolean>() {
                            @Override
                            public Boolean apply(WebElement element) {
                                return foundElement.getAttribute(attributeName) != null;
                            }
                        });
                if (hasAttribute) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName));
                    return true;
                }
            } catch (WebElementNotFoundException ex) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_WARNING_OBJ_X_IS_NOT_PRESENT, to.getObjectId()));
            } catch (TimeoutException e) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_FAILED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName)
        : StringConstants.KW_MSG_CANNOT_WAIT_OBJ_HAS_ATTRIBUTE)
    }

    /**
     * Wait until the given web element doesn't have an attribute with the specific name
     * @param to
     *      represent a web element
     * @param attributeName
     *      the name of the attribute to wait for
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element doesn't have the attribute with the specific name; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementNotHasAttribute(TestObject to, String attributeName, int timeOut, FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                KeywordLogger.getInstance().logInfo(StringConstants.COMM_LOG_INFO_CHECKING_ATTRIBUTE_NAME);
                if (attributeName == null) {
                    throw new IllegalArgumentException(StringConstants.COMM_EXC_ATTRIBUTE_NAME_IS_NULL);
                }
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement foundElement = WebUiBuiltInKeywords.findWebElement(to, timeOut);
                Boolean notHasAttribute = new FluentWait<WebElement>(foundElement)
                        .pollingEvery(500, TimeUnit.MILLISECONDS).withTimeout(timeOut, TimeUnit.SECONDS)
                        .until(new Function<WebElement, Boolean>() {
                            @Override
                            public Boolean apply(WebElement element) {
                                return foundElement.getAttribute(attributeName) == null;
                            }
                        });
                if (notHasAttribute) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_FAILED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName));
                    return true;
                }
            } catch (WebElementNotFoundException ex) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_WARNING_OBJ_X_IS_NOT_PRESENT, to.getObjectId()));
            } catch (TimeoutException e) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName));
                return false;
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_NOT_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName)
        : StringConstants.KW_MSG_CANNOT_WAIT_OBJ_NOT_HAS_ATTRIBUTE)
    }

    /**
     * Wait until the given web element has an attribute with the specific name and value
     * @param to
     *      represent a web element
     * @param attributeName
     *      the name of the attribute to wait for
     * @param attributeValue
     *      the value of the attribute to wait for
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element has the attribute with the specific name and value; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementAttributeValue(TestObject to, String attributeName, String attributeValue, int timeOut, FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            try {
                WebUiCommonHelper.checkTestObjectParameter(to);
                KeywordLogger.getInstance().logInfo(StringConstants.COMM_LOG_INFO_CHECKING_ATTRIBUTE_NAME);
                if (attributeName == null) {
                    throw new IllegalArgumentException(StringConstants.COMM_EXC_ATTRIBUTE_NAME_IS_NULL);
                }
                timeOut = WebUiCommonHelper.checkTimeout(timeOut);
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to, timeOut);
                WebElement foundElement = WebUiBuiltInKeywords.findWebElement(to, timeOut);
                Boolean hasAttributeValue = new FluentWait<WebElement>(foundElement)
                        .pollingEvery(500, TimeUnit.MILLISECONDS).withTimeout(timeOut, TimeUnit.SECONDS)
                        .until(new Function<WebElement, Boolean>() {
                            @Override
                            public Boolean apply(WebElement element) {
                                return foundElement.getAttribute(attributeName) == attributeValue;
                            }
                        });
                if (hasAttributeValue) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_ATTRIBUTE_Y_VALUE_Z, to.getObjectId(), attributeName, attributeValue));
                    return true;
                }
            } catch (WebElementNotFoundException ex) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_WARNING_OBJ_X_IS_NOT_PRESENT, to.getObjectId()));
            } catch (TimeoutException e) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_FAILED_WAIT_FOR_OBJ_X_HAS_ATTRIBUTE_Y_VALUE_Z, to.getObjectId(), attributeName, attributeValue));
            } finally {
                if (isSwitchIntoFrame) {
                    WebUiCommonHelper.switchToDefaultContent();
                }
            }
            return false;
        }
        , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_ATTRIBUTE_Y_VALUE_Z, to.getObjectId(), attributeName, attributeValue)
        : StringConstants.KW_MSG_CANNOT_WAIT_OBJ_ATTRIBUTE_VALUE)
    }

    /**
     * Set the size of the current window. This will change the outer window dimension and the viewport, synonymous to window.resizeTo() in JS.
     * @param width
     *      the target viewport width
     * @param height
     *      the target viewport height
     * @param flowControl
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static void setViewPortSize(int width, int height, FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.COMM_LOG_INFO_CHECKING_WIDTH);
            if (width <= 0) {
                throw new IllegalArgumentException(StringConstants.COMM_EXC_WIDTH_MUST_BE_ABOVE_ZERO);
            }
            logger.logInfo(StringConstants.COMM_LOG_INFO_CHECKING_HEIGHT);
            if (height <= 0) {
                throw new IllegalArgumentException(StringConstants.COMM_EXC_HEIGHT_MUST_BE_ABOVE_ZERO);
            }
            DriverFactory.getWebDriver().manage().window().setSize(new Dimension(width, height));
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SET_VIEWPORT_WIDTH_X_HEIGHT_Y, width.toString(), height.toString()));
        }
        , flowControl, true, StringConstants.KW_MSG_CANNOT_SET_VIEWPORT)
    }

    /**
     * Scroll the viewport to a specific position
     * @param x
     *      x position
     * @param y
     *      y position
     * @param flowControl
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static void scrollToPosition(int x, int y, FailureHandling flowControl) {
        WebUIKeywordMain.runKeyword({
            logger.logInfo(StringConstants.COMM_LOG_INFO_CHECKING_X);
            if (x < 0) {
                throw new IllegalArgumentException(StringConstants.COMM_EXC_X_MUST_BE_ABOVE_ZERO);
            }
            logger.logInfo(StringConstants.COMM_LOG_INFO_CHECKING_Y);
            if (y < 0) {
                throw new IllegalArgumentException(StringConstants.COMM_EXC_Y_MUST_BE_ABOVE_ZERO);
            }
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SCROLLING_TO_POSITION_X_Y, x.toString(), y.toString()));
            ((JavascriptExecutor) DriverFactory.getWebDriver()).executeScript("window.scrollTo(" + x.toString() + ", " + y.toString() + ");");
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SCROLL_TO_POSITION_X_Y, x.toString(), y.toString()));
        }
        , flowControl, true, MessageFormat.format(StringConstants.KW_MSG_CANNOT_SCROLL_TO_POSITION_X_Y, x.toString(), y.toString()))
    }

    /**
     * Get current web page's width
     * @param flowControl
     * @return current web page's width
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static int getPageWidth(FailureHandling flowControl) {
        return (int) WebUIKeywordMain.runKeyword({
            int pageWidth = (int) ((JavascriptExecutor) DriverFactory.getWebDriver()).executeScript('''return Math.max(
                document.documentElement["clientWidth"], 
                document.body["scrollWidth"], 
                document.documentElement["scrollWidth"], 
                document.body["offsetWidth"], 
                document.documentElement["offsetWidth"]);''');
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_GET_PAGE_WIDTH_X, pageWidth.toString()));
            return pageWidth;
        }
        , flowControl, true, StringConstants.KW_MSG_CANNOT_GET_PAGE_WIDTH)
    }

    /**
     * Get current web page's height
     * @param flowControl
     * @return current web page's height
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static int getPageHeight(FailureHandling flowControl) {
        return (int) WebUIKeywordMain.runKeyword({
            int pageHeight = (int) ((JavascriptExecutor) DriverFactory.getWebDriver()).executeScript('''return Math.max(
                document.documentElement["clientHeight"], 
                document.body["scrollHeight"], 
                document.documentElement["scrollHeight"], 
                document.body["offsetHeight"], 
                document.documentElement["offsetHeight"]);''');
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_GET_PAGE_HEIGHT_X, pageHeight.toString()));
            return pageHeight;
        }
        , flowControl, true, StringConstants.KW_MSG_CANNOT_GET_PAGE_HEIGHT)
    }

    /**
     * Get current view port left (x) position relatively to the web page
     * @param flowControl
     * @return current view port left (x) position
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static int getViewportLeftPosition(FailureHandling flowControl) {
        return (int) WebUIKeywordMain.runKeyword({
            Number leftPosition = (Number) ((JavascriptExecutor) DriverFactory.getWebDriver()).executeScript('return window.pageXOffset || document.documentElement.scrollLeft;');
            int leftPositionIntValue = leftPosition.intValue();
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_GET_VIEWPORT_LEFT_POSITION_X, leftPositionIntValue.toString()));
            return leftPositionIntValue;
        }
        , flowControl, true, StringConstants.KW_MSG_CANNOT_GET_VIEWPORT_LEFT_POSITION)
    }

    /**
     * Get current view port top (y) position relatively to the web page
     * @param flowControl
     * @return current view port top (y) position
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_WINDOW)
    public static int getViewportTopPosition(FailureHandling flowControl) {
        return (int) WebUIKeywordMain.runKeyword({
            Number topPosition = (Number) ((JavascriptExecutor) DriverFactory.getWebDriver()).executeScript('return window.pageYOffset || document.documentElement.scrollTop;');
            int topPositionIntValue = topPosition.intValue();
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_GET_VIEWPORT_TOP_POSITION_X, topPositionIntValue.toString()));
            return topPositionIntValue;
        }
        , flowControl, true, StringConstants.KW_MSG_CANNOT_GET_VIEWPORT_TOP_POSITION)
    }
}