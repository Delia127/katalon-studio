package com.kms.katalon.core.webui.keyword;

import groovy.transform.CompileStatic

import java.text.MessageFormat
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

import org.openqa.selenium.Alert
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.Wait
import org.openqa.selenium.support.ui.WebDriverWait

import com.google.common.base.Function
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.driver.DriverType
import com.kms.katalon.core.exception.ExceptionsUtil
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.BuiltinKeywords
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.webui.common.ScreenUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.core.webui.exception.BrowserNotOpenedException
import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import com.kms.katalon.core.webui.util.FileUtil

@CompileStatic
public class WebUiBuiltInKeywords extends BuiltinKeywords {
	private static final KeywordLogger logger = KeywordLogger.getInstance();
	private static ScreenUtil screenUtil = new ScreenUtil();

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
	public static void openBrowser(String rawUrl, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			logger.logInfo(StringConstants.KW_LOG_INFO_OPENING_BROWSER);
			DriverFactory.openWebDriver();
			if (rawUrl != null && !rawUrl.isEmpty()) {
				StringBuilder url = new StringBuilder(rawUrl);
				if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
					url.insert(0, "http://");
				}
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_NAVIGATING_BROWSER_TO, url));
				DriverFactory.getWebDriver().get(url.toString());
			}
			logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_BROWSER_IS_OPENED_W_URL, rawUrl));
		}
		, flowControl, true, (rawUrl != null) ? MessageFormat.format(StringConstants.KW_MSG_UNABLE_TO_OPEN_BROWSER_W_URL, rawUrl) : StringConstants.KW_MSG_UNABLE_TO_OPEN_BROWSER)
	}


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


	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
	public static void navigateToUrl(String rawUrl, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_URL);
			if (rawUrl == null || rawUrl.isEmpty()) {
				throw new IllegalArgumentException(StringConstants.KW_EXC_URL_CANNOT_BE_NULL_OR_EMPTY);
			}

			StringBuilder url = new StringBuilder(rawUrl);
			if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
				url.insert(0, "http://");
			}
			logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_NAVIGATING_TO, url));
			WebDriver webDriver = DriverFactory.getWebDriver();
			webDriver.navigate().to(url.toString());
			logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_NAVIGATE_TO, url));
		}
		, flowControl, true, MessageFormat.format(StringConstants.KW_MSG_CANNOT_NAVIGATE_TO, rawUrl))
	}


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


	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
	public static boolean waitForElementNotPresent(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
		return WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				timeOut = WebUiCommonHelper.checkTimeout(timeOut);
				isSwitchIntoFrame = switchToFrame(to, timeOut);
				boolean elementNotFound = false;
				final By locator = buildLocator(to);
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
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		}
		, flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_TO_BE_NOT_PRESENT, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_WAIT_FOR_OBJ_TO_BE_NOT_PRESENT)
	}


	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
	public static boolean waitForElementPresent(TestObject to, int timeOut, FailureHandling flowControl)
	throws StepFailedException {
		return WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to, timeOut);
				WebElement foundElement = null;
				try {
					foundElement = findWebElement(to, timeOut);
					if (foundElement != null) {
						logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_PRESENT, to.getObjectId()));
					}
					return true;
				} catch (WebElementNotFoundException e) {
					WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_IS_NOT_PRESENT_AFTER_X_SEC, to.getObjectId(), timeOut),
							flowControl, null);
					return false;
				}
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		}
		, flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_TO_BE_PRESENT, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_WAIT_FOR_OBJ_TO_BE_PRESENT)
	}

	/***
	 * Wait until the given web element is visible within timeout.
	 * @param to 
	 * 		a web element
	 * @param timeOut
	 * 		how many seconds to wait
	 * @param flowControl
	 * @return
	 * @throws StepFailedException
	 */
	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
	public static boolean waitForElementVisible(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
		return WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to, timeOut);
				WebElement foundElement = null;
				try {
					WebDriverWait wait = new WebDriverWait(DriverFactory.getWebDriver(), timeOut);
					foundElement = wait.until(ExpectedConditions.visibilityOf(findWebElement(to, timeOut)));
					if (foundElement != null) {
						logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_VISIBLE, to.getObjectId()));
					}
					return true;
				} catch (TimeoutException | WebElementNotFoundException e) {
					WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_IS_NOT_VISIBLE_AFTER_X_SEC, to.getObjectId(), timeOut),
							flowControl, null);
					return false;
				}
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		}
		, flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_TO_BE_VISIBLE, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_WAIT_FOR_OBJ_TO_BE_VISIBLE)
	}

	/***
	 * Wait for the given element to be clickable within the given time in second
	 * @param to
	 * 		a web element
	 * @param timeOut
	 * 		how many seconds to wait
	 * @param flowControl
	 * @return
	 * @throws StepFailedException
	 */
	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
	public static boolean waitForElementClickable(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
		return WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to, timeOut);
				WebElement foundElement = null;
				try {
					WebDriverWait wait = new WebDriverWait(DriverFactory.getWebDriver(), timeOut);
					foundElement = wait.until(ExpectedConditions.elementToBeClickable(findWebElement(to, timeOut)));
					if (foundElement != null) {
						logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_IS_CLICKABLE, to.getObjectId()));
					}
					return true;
				} catch (TimeoutException | WebElementNotFoundException e) {
					WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_IS_NOT_CLICKABLE_AFTER_X_SEC, to.getObjectId(), timeOut),
							flowControl, null);
					return false;
				}
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		}
		, flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_TO_BE_CLICKABLE, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_WAIT_FOR_OBJ_TO_BE_CLICKABLE)
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
	public static void click(TestObject to, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CLICKING_ON_OBJ, to.getObjectId()));
				webElement.click();
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_CLICKED, to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		}
		, flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_CLICK_ON_OBJ_X, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_CLICK_ON_OBJ)
	}


	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_FORM)
	public static void submit(TestObject to, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SUBMITTING_ON_FORM_CONTAINING_OBJ, to.getObjectId()));
				webElement.submit();
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_FORM_CONTAINING_OBJ_IS_SUBMITTED, to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		}
		, flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SUBMIT_FORM_CONTAINING_OBJ_X, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_SUBMIT_FORM_CONTAINING_OBJ)
	}


	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
	public static void doubleClick(TestObject to, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_DOUBLE_CLICK_ON_OBJ, to.getObjectId()));
				Actions action = new Actions(DriverFactory.getWebDriver());
				action.doubleClick(webElement).build().perform();
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_IS_DOUBLE_CLICKED_ON, to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		}
		, flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_DOUBLE_CLICK_ON_OBJ_X, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_DOUBLE_CLICK_ON_OBJ)
	}


	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
	public static void rightClick(TestObject to, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_RIGHT_CLICKING_ON_OBJ, to.getObjectId()));
				Actions action = new Actions(DriverFactory.getWebDriver());
				action.contextClick(webElement).build().perform();
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_IS_RIGHT_CLICKED_ON, to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		}
		, flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_RIGHT_CLICK_ON_OBJ_X, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_RIGHT_CLICK_ON_OBJ)
	}


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
				isSwitchIntoFrame = switchToFrame(to);
				WebElement hoverElement = findWebElement(to);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_MOVING_MOUSE_OVER_OBJ, to.getObjectId()));
				Actions builder = new Actions(DriverFactory.getWebDriver());
				builder.moveToElement(hoverElement).perform();
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_IS_HOVERED, to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		}
		, flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_MOVE_MOUSE_OVER_OBJ_X, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_MOVE_MOUSE_OVER_OBJ)
	}


	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_KEYBOARD)
	public static void sendKeys(TestObject to, String strKeys, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				if (to == null) {
					to = new TestObject("tempBody").addProperty("css", ConditionType.EQUALS, "body");
				}
				isSwitchIntoFrame = switchToFrame(to);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SENDING_KEYS_TO_OBJ, strKeys, to.getObjectId()));
				WebElement webElement = findWebElement(to);
				webElement.sendKeys(strKeys);
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_KEYS_SENT_TO_OBJ, strKeys, to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		}
		, flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SED_KEYS_TO_OBJ_X, strKeys,to.getObjectId())
		: MessageFormat.format(StringConstants.KW_MSG_CANNOT_SED_KEYS_TO_OBJ, strKeys))
	}


	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
	public static void focus(TestObject to, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to);
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
					switchToDefaultContent();
				}
			}
		}
		, flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_FOCUS_ON_OBJ_X, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_FOCUS_ON_OBJ)
	}


	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
	public static String getText(TestObject to, FailureHandling flowControl) throws StepFailedException {
		return WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			String text = "";
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to);
				WebElement element = findWebElement(to);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_GETTING_OBJ_TXT, to.getObjectId()));
				text = element.getText();
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_TXT_IS, to.getObjectId(), text));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
				return text;
			}
		}
		, flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_GET_TXT_OF_OBJ_X, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_GET_OBJ_TXT)
	}


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
				isSwitchIntoFrame = switchToFrame(to);
				WebElement element = findWebElement(to);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_GETTING_OBJ_ATTR, attribute, to.getObjectId()));
				attrValue = element.getAttribute(attribute);
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_ATTR_IS, attribute, to.getObjectId(), attrValue));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
				return attrValue;
			}
		}
		, flowControl, true, (to != null && attribute != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_GET_ATTR_X_OF_OBJ_Y, attribute, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_GET_OBJ_ATTR)
	}


	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
	public static setText(TestObject to, String text, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_TXT);
				if (text == null) {
					throw new IllegalArgumentException(StringConstants.KW_EXC_TXT_IS_NULL);
				}
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SETTING_OBJ_TXT_TO_VAL, to.getObjectId(), text));
				webElement.clear();
				webElement.sendKeys(text);
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TXT_IS_SET_ON_OBJ, text, to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SET_TXT_X_OF_OBJ_Y, text, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_SET_TXT)
	}


	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_CHECKBOX)
	public static check(TestObject to, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CHECKING_ON_OBJ, to.getObjectId()));
				if (!webElement.isSelected()) {
					webElement.click();
				}
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_IS_CHECKED, to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_CHECK_OBJ_X, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_CHECK_OBJ)
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_CHECKBOX)
	public static void uncheck(TestObject to, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_UNCHECKING_ON_OBJ, to.getObjectId()));
				if (webElement.isSelected()) {
					webElement.click();
				}
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_IS_UNCHECKED, to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_UNCHECK_OBJ_X, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_UNCHECK_OBJ)
	}

	/**
	 * Select the option at the given indexes.
	 *
	 * @param to
	 *            : @see {@link TestObject}
	 * @param range
	 *            : string format of an integer array. Example [1, 3, 4].
	 * @param flowControl
	 *            : @see {@link FailureHandling}
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
					isSwitchIntoFrame = switchToFrame(to);
					WebElement webElement = findWebElement(to);
					WebUiCommonHelper.selectOrDeselectOptionsByIndex(new Select(webElement), indexes, true, to);
				}
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OPTS_W_INDEX_IN_X_ARE_SELECTED_ON_OBJ_Y, WebUiCommonHelper.integerArrayToString(indexes), to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null && range != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SEL_OPT_BY_INDEX_X_OF_OBJ_Y, range, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_SEL_OPT_BY_INDEX)
	}

	/**
	 * Select all options that have a value matching the "value" argument.
	 *
	 * @param to
	 *            : @see {@link TestObject}
	 * @param value
	 *            : value of option in a object
	 * @param isRegex
	 *            : is value a regular expression or not?
	 * @param flowControl
	 *            : @see {@link FailureHandling}
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
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				Select select = new Select(webElement);
				WebUiCommonHelper.selectOrDeselectOptionsByValue(new Select(webElement), value, isRegex, true, to, regularExpressionLog)
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SELECTED_ALL_OPT_W_VAL_X_IN_OBJ_Y, value, to.getObjectId(), regularExpressionLog));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null && value != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SELECT_OPT_BY_VAL_OF_OBJ, value, to.getObjectId(), regularExpressionLog)
		: MessageFormat.format(StringConstants.KW_MSG_CANNOT_SEL_OPT_BY_VAL, regularExpressionLog))
	}

	/**
	 * Selection all options of an object.
	 *
	 * @param to
	 *            : @see {@link TestObject}
	 * @param flowControl
	 *            : @see {@link FailureHandling}
	 * @throws StepFailedException
	 */
	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
	public static void selectAllOption(TestObject to, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				WebUiCommonHelper.selectOrDeselectAllOptions(new Select(webElement), true, to)
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ALL_OBJ_OPTS_ARE_SELECTED, to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SELECT_ALL_OBJ_OPTS, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_SELECT_ALL_OPTS)
	}

	/**
	 * Select all options that display text matching the "labelText" argument.
	 *
	 * @param to
	 *            : @see {@link TestObject}
	 * @param labelText
	 *            : text of the label
	 * @param isRegex
	 *            : is labelText regular expression or not?
	 * @param flowControl
	 *            : @see {@link FailureHandling}
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
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				WebUiCommonHelper.selectOrDeselectOptionsByLabel(new Select(webElement), labelText, isRegex, true, to, regularExpressionLog)
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SELECTED_ALL_OPT_W_LBL_X_IN_OBJ_Y, labelText, to.getObjectId(), regularExpressionLog));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null && labelText != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SEL_OPT_BY_LBL_OF_OBJ, labelText, to.getObjectId(), regularExpressionLog)
		: MessageFormat.format(StringConstants.KW_MSG_CANNOT_SEL_OPT_BY_LBL, regularExpressionLog))
	}

	/**
	 * Deselect the option at the given indexes.
	 *
	 * @param to
	 *            : @see {@link TestObject}
	 * @param index
	 *            : index of the option in the object
	 * @param flowControl
	 *            : @see Fail
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
					isSwitchIntoFrame = switchToFrame(to);
					WebElement webElement = findWebElement(to);
					Select select = new Select(webElement);
					WebUiCommonHelper.selectOrDeselectOptionsByIndex(select, indexes, false, to);
				}
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OPTS_W_IDX_IN_X_ARE_DESELECTED_ON_OBJ, WebUiCommonHelper.integerArrayToString(indexes), to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null && range != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_DESELECT_OPT_BY_IDX_OF_OBJ, range, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_DESELECT_OPT_BY_IDX)
	}

	/**
	 * Deselect all options that have a value matching the "value" argument.
	 *
	 * @param to
	 *            : @see {@link TestObject}
	 * @param value
	 *            : value of option in the object
	 * @param isRegex
	 *            : is value a regular expression or not?
	 * @param flowControl
	 * @see {@link FailureHandling}
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
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				WebUiCommonHelper.selectOrDeselectOptionsByValue(new Select(webElement), value, isRegex, false, to, regularExpressionLog)
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OPTS_W_VAL_ARE_DESELECTED_ON_OBJ, value, to.getObjectId(), regularExpressionLog));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null && value != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_DESELECT_OPT_BY_VAL_OF_OBJ, value, to.getObjectId(), regularExpressionLog)
		: MessageFormat.format(StringConstants.KW_MSG_CANNOT_DESELECT_OPT_BY_VAL, regularExpressionLog))
	}

	/**
	 * Deselect all options that display text matching the "value" argument.
	 *
	 * @param to
	 *            : @see {@link TestObject}
	 * @param labelText
	 *            : label of the option in the object
	 * @param isRegex
	 *            : is value a regular expression or not?
	 * @param flowControl
	 *            : @see {@link FailureHandling}
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
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				Select select = new Select(webElement);
				WebUiCommonHelper.selectOrDeselectOptionsByLabel(new Select(webElement), labelText, isRegex, false, to, regularExpressionLog)
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DESELECTED_OPTS_W_LBL_X_ON_OBJ, labelText, to.getObjectId(), regularExpressionLog));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null && labelText != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_DESEL_OPT_BY_LBL_OF_OBJ, labelText, to.getObjectId(), regularExpressionLog)
		: MessageFormat.format(StringConstants.KW_MSG_CANNOT_DESEL_OPT_BY_LBL, regularExpressionLog))
	}

	/**
	 * Clear all selected entries of a test object.
	 *
	 * @param to
	 *            : @see {@link TestObject}
	 * @param flowControl
	 *            : @see {@link FailureHandling}
	 * @throws StepFailedException
	 */
	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
	public static void deselectAllOption(TestObject to, FailureHandling flowControl) throws StepFailedException {
		WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				Select selection = new Select(webElement);
				WebUiCommonHelper.selectOrDeselectAllOptions(new Select(webElement), false, to)
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DESELECTED_ALL_OPTS_ON_OBJ, to.getObjectId()));
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_SEL_ALL_OPTS_ON_OBJ, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_SEL_ALL_OPTS)
	}

	/**
	 * Verify if the given web element is checked.
	 *
	 * @param to
	 *            Test Object
	 * @param timeOut
	 *            timeOut value in seconds
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_IS_CHECKED, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_IS_CHECKED)
	}

	/**
	 * Verify if the given web element is not checked.
	 *
	 * @param to
	 *            Test Object
	 * @param timeOut
	 *            timeOut value in seconds
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_IS_NOT_CHECKED, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_IS_NOT_CHECKED)
	}

	/**
	 * Verify if the given web element exists.
	 *
	 * @param to
	 *            Test Object
	 * @param timeOut
	 *            timeOut value in seconds
	 * @param flowControl
	 * @return true if element exists; otherwise, false
	 * @throws StepFailedException
	 */
	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
	public static boolean verifyElementPresent(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
		return WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
				}
			}
			return false;
		} , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_IS_PRESENT, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_IS_PRESENT)
	}

	/**
	 * Verify if the given web element does not exist.
	 *
	 * @param to
	 *            Test Object
	 * @param timeOut
	 *            timeOut value in seconds
	 * @param flowControl
	 * @return true if element does not exist; otherwise, false
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
				boolean elementNotFound = false;
				final By locator = buildLocator(to);
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
					switchToDefaultContent();
				}
			}
			return false;
		} , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_IS_NOT_PRESENT, to.getObjectId()) : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_IS_NOT_PRESENT)
	}

	/**
	 * Simulate users clicking on "OK" button of alert class (alert,
	 * confirmation popup, prompt popup)
	 *
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
	 * @return value - text of the alert
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
	 *            the text to be set
	 * @param flowControl
	 * @return value - text to type into prompt popup
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
				WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_NO_ALERT_FOUND_AFTER_X_SEC, timeOut), flowControl, null, true);
				return false;
			}
		} , flowControl, true, StringConstants.KW_MSG_CANNOT_WAIT_FOR_ALERT)
	}

	/**
	 * Verify if the given text exist anywhere in page source.
	 *
	 * @param text
	 *            to be verified if existing anywhere in page source
	 * @param isRegex
	 *            : true if value is regular expression, false by default.
	 * @param flowControl
	 *            : @see {@link FailureHandling}
	 * @return result - true if all texts exist anywhere in page source;
	 *         otherwise, false
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
	 * Verify if the given text do not exist anywhere in page source.
	 *
	 * @param text
	 *            : text to be verified if not existing anywhere in page source.
	 * @param isRegex
	 *            : true if value is regular expression, false by default.
	 * @param flowControl
	 * @return result - true if all texts do not exist anywhere in page source;
	 *         otherwise, false.
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
	 * Count the number of options the given web element has.
	 *
	 * @param to
	 *            a test object
	 * @param flowControl
	 * @return number - the number of options
	 * @throws StepFailedException
	 */
	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_COMBOBOX)
	public static int getNumberOfTotalOption(TestObject to, FailureHandling flowControl) throws StepFailedException {
		def result = WebUIKeywordMain.runKeyword({
			boolean isSwitchIntoFrame = false;
			try {
				WebUiCommonHelper.checkTestObjectParameter(to);
				isSwitchIntoFrame = switchToFrame(to);
				WebElement webElement = findWebElement(to);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_COUNTING_TOTAL_OPTS_OF_OBJ_X, to.getObjectId()));
				Select select = new Select(webElement);
				int num = select.getOptions().size();
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_X_OPTS_OF_OBJ_Y_COUNTED, Integer.toString(num), to.getObjectId()));
				return num;
			} finally {
				if (isSwitchIntoFrame) {
					switchToDefaultContent();
				}
			}
			return 0;
		} , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_GET_TOTAL_OPTS_OF_OBJ_X, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_GET_TOTAL_OPTS_OF_OBJ)
		return Integer.valueOf(result.toString());
	}

	/**
	 * Count the number of selected options the given web element has.
	 *
	 * @param to
	 *            test object
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
				isSwitchIntoFrame = switchToFrame(to);
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
					switchToDefaultContent();
				}
			}
			return 0;
		} , flowControl, true, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_GET_NO_OF_SELECTED_OPTS_OF_OBJ_X, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_GET_NO_OF_SELECTED_OPTS_OF_OBJ)
		return Integer.valueOf(result.toString());
	}

	/**
	 * Verify if the options with the given displayed texts exist.
	 *
	 * @param to
	 *            : test object
	 * @param label
	 *            : displayed texts of the options to be verified if existing
	 * @param isRegex
	 *            : true if the label is regular expression, false by default
	 * @param timeOut
	 *            : timeOut value in seconds
	 * @param flowControl
	 *            : @see {@link FailureHandling}
	 * @return true if all options with given displayed texts exist; otherwise,
	 *         false
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
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
	 *            : test object.
	 * @param value
	 *            : value of the options to be verified if presenting.
	 * @param isRegex
	 *            : true if value is regular expression, false by default.
	 * @param timeOut
	 *            : timeOut value in seconds
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
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
	 *            test object: @see {@link TestObject}
	 * @param label
	 *            displayed texts of the options to be verified if not existing
	 * @param isRegex
	 *            - true if label is regular expression, false by default
	 * @param timeOut
	 *            : timeOut value in seconds
	 * @param flowControl
	 * @return true if options with given displayed text do not present;
	 *         otherwise, false
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
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
	 *            : test object: @see {@link TestObject}
	 * @param value
	 *            : value of the options to be verified if not presenting.
	 * @param isRegex
	 *            : - true if label is regular expression, false by default.
	 * @param timeOut
	 *            : timeOut value in seconds
	 * @param flowControl
	 * @return true if all options with given value do not present; otherwise,
	 *         false.
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
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
	 *            : test object
	 * @param label
	 *            : displayed text of the option to be verified if being
	 *            selected
	 * @param isRegex
	 *            : true if value is regular expression, false by default.
	 * @param timeOut
	 *            : timeOut value in seconds
	 * @param flowControl
	 * @return true if all options with given displayed texts are selected;
	 *         otherwise, false
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
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
	 *            : test object
	 * @param value
	 *            : value of the options to be verified if being selected.
	 * @param isRegex
	 *            : true if value is regular expression, false by default.
	 * @param timeOut
	 *            : timeOut value in seconds
	 * @param flowControl
	 * @return true if all options with given value are selected; otherwise,
	 *         false.
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
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
	 *            : test object
	 * @param label
	 *            : labels displayed texts of the options to be verified if not
	 *            being selected.
	 * @param isRegex
	 *            : true if label is regular expression, false by default.
	 * @param timeOut
	 *            : timeOut value in seconds
	 * @param flowControl
	 * @return true if all options with given displayed texts are not selected;
	 *         otherwise, false.
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
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
	 *            test object
	 * @param value
	 *            : value of the options to be verified if not being selected.
	 * @param isRegex
	 *            : true if label is regular expression, false by default.
	 * @param timeOut
	 *            : timeOut value in seconds
	 * @param flowControl
	 * @return true if all options with given value are not selected; otherwise,
	 *         false.
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
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
	 *            test object
	 * @param range
	 *            list of indexes of the options to be verified if being
	 *            selected
	 * @param timeOut
	 *            : timeOut value in seconds
	 * @param flowControl
	 * @return true if all options at given indices are selected; otherwise,
	 *         false
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
				}
			}
		} , flowControl, true, (to != null && range != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_SELECTED_IN_IDX_RANGE_X_IN_OBJ, range, to.getObjectId())
		: StringConstants.KW_MSG_CANNOT_VERIFY_OPT_IS_SELECTED_IN_IDX_RANGE)
	}

	/**
	 * Verify if the options at the given indices are not selected
	 *
	 * @param to
	 *            test object
	 * @param indexes
	 *            the indexes of the options to be verified if not being
	 *            selected
	 * @param timeOut
	 *            : timeOut value in seconds
	 * @param flowControl
	 * @return true if all options at given indices are not selected; otherwise,
	 *         false
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
				isSwitchIntoFrame = switchToFrame(to, timeOut);
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
					switchToDefaultContent();
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
		WebUIKeywordMain.runKeyword({ switchToDefaultContent(); } , flowControl, true, StringConstants.KW_MSG_CANNOT_SWITCH_TO_DEFAULT_CONTENT)
	}

	@CompileStatic
	public static void switchToDefaultContent() throws StepFailedException {
		try {
			if (DriverFactory.getAlert() != null) {
				logger.logWarning(StringConstants.KW_LOG_WARNING_SWITCHING_TO_DEFAULT_CONTENT_FAILED_BC_ALERT_ON_PAGE);
				return;
			}
			logger.logInfo(StringConstants.KW_LOG_INFO_SWITCHING_TO_DEFAULT_CONTENT);
			DriverFactory.getWebDriver().switchTo().defaultContent();
		} catch (NoSuchWindowException e) {
			// Switching to default content in IE without in frame will raise
			// this exception, so do nothing here.
		} catch (WebDriverException e) {
			// Switching to default content is optional, so exception will not
			// make it fail, therefore only warn user about the exception
			logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_WARNING_SWITCHING_TO_DEFAULT_CONTENT_FAILED_BC_OF_X, ExceptionsUtil.getMessageForThrowable(e)));
		}
	}

	@CompileStatic
	private static By buildLocator(TestObject to) {
		By locator = null;
		Map<String, String> binding = buildLocator(to.getActiveProperties());
		if (binding.containsKey("id")) {
			locator = By.id(binding.get("id"));
		} else if (binding.containsKey("name")) {
			locator = By.name(binding.get("name"));
		} else if (binding.containsKey("xpath")) {
			locator = By.xpath(binding.get("xpath"));
		} else if (binding.containsKey("css")) {
			locator = By.cssSelector(binding.get("css"));
		} else if (binding.containsKey("cssSelector")) {
			locator = By.cssSelector(binding.get("cssSelector"));
		}
		return locator;
	}

	@CompileStatic
	private static Map<String, String> buildLocator(List<TestObjectProperty> propEntities) {
		Map<String, String> binding = new HashMap<String, String>();
		StringBuilder exp = new StringBuilder();
		String tagName = "";
		for (int i = 0; i < propEntities.size(); i++) {
			TestObjectProperty prop = propEntities.get(i);
			String propName = prop.getName();
			String propVal = prop.getValue();
			String mCondition = prop.getCondition().toString();
			if (propName.equals("id") || propName.equals("name")) {
				if (propEntities.size() == 1 && mCondition.equals(ConditionType.EQUALS.toString())) {
					binding.put(propName, propVal);
					break;
				}
			} else if (propName.equals("xpath") || propName.equals("css") || propName.equals("cssSelector")) {
				binding.put(propName, propVal);
				break;
			}
			if (propName.equalsIgnoreCase("ref_element") || propName.equalsIgnoreCase("parent_frame")) {
				continue;
			}
			if (propName.equalsIgnoreCase("tagName") || propName.equalsIgnoreCase("tag")) {
				tagName = propVal;
				continue;
			}
			if (!exp.toString().isEmpty()) {
				exp.append(" and ");
			}
			if (propName.equals("text") || propName.equals("link_text")) {
				propName = "text()";
			}
			// If attribute, append '@' before attribute name, skip it if method
			if (!propName.endsWith("()")) {
				propName = "@" + propName;
			}
			if (mCondition.equals(ConditionType.EQUALS.toString())) {
				exp.append(String.format("%s = '%s'", propName, propVal));
			} else if (mCondition.equals(ConditionType.NOT_EQUAL.toString())) {
				exp.append(String.format("%s != '%s'", propName, propVal));
			} else if (mCondition.equals(ConditionType.CONTAINS.toString())) {
				exp.append(String.format("contains(%s,'%s')", propName, propVal));
			} else if (mCondition.equals(ConditionType.NOT_CONTAIN.toString())) {
				exp.append(String.format("not(contains(%s,'%s'))", propName, propVal));
			} else if (mCondition.equals(ConditionType.STARTS_WITH.toString())) {
				exp.append(String.format("starts-with(%s,'%s')", propName, propVal));
			}
		}
		if (!binding.containsKey("name") && !binding.containsKey("id") && !binding.containsKey("xpath")
		&& !binding.containsKey("css") && !binding.containsKey("cssSelector") && !exp.toString().equals("")) {
			StringBuilder xpath = new StringBuilder();
			xpath.append("//");
			xpath.append(tagName.equals("") ? "*" : tagName);
			xpath.append("[" + exp + "]");

			binding.put("xpath", xpath.toString());
		}

		return binding;
	}

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
	 *            : the number of seconds to wait
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
	 *            a test object
	 * @param propertyName
	 *            name of the property, for example, xpath, id, name, ...
	 * @param matchCondition
	 *            condition to match property name with property value, for
	 *            example, equals, not equals, ...
	 * @param modifyValue
	 *            new value of the property to set
	 * @param isActive
	 *            true if the property is active, otherwise, false
	 * @param flowControl
	 * @return
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
	 * Modify property of test object. If the property is not existed then the
	 * property will be created. If the changed value is null then the existed
	 * value will not be changed. Use when test object has attributes changing
	 * in runtime. This keyword does not modify the object saved in Object
	 * Repository, instead, it creates another test object, modify and return
	 * this test object. Hence, users must use a variable to get the returned
	 * object.
	 *
	 * @param testObject
	 *            a test object
	 * @param propertyName
	 *            name of the property, for example, xpath, id, name, ...
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
			boolean isSwitchToFrame = false;
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
				isSwitchToFrame = switchToFrame(sourceObject);
				builder.clickAndHold(findWebElement(sourceObject));
				builder.perform();
				Thread.sleep(250);
				if (isSwitchToFrame) {
					switchToDefaultContent();
				}
				isSwitchToFrame = switchToFrame(destinationObject);

				WebElement destinationWebElement = findWebElement(destinationObject);
				builder.moveToElement(destinationWebElement, 5, 5);
				builder.perform();
				Thread.sleep(250);
				builder.release(destinationWebElement);
				builder.perform();

				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DRAGGED_OBJ_W_ID_X_TO_OBJ_W_ID_Y, sourceObject.getObjectId(), destinationObject))
			} finally {
				if (isSwitchToFrame) {
					switchToDefaultContent();
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
			boolean isSwitchToFrame = false;
			try {
				logger.logInfo(StringConstants.KW_LOG_INFO_CHK_SRC_OBJ);
				if (sourceObject == null) {
					throw new IllegalArgumentException(StringConstants.KW_EXC_SRC_OBJ_IS_NULL);
				}
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_START_DRAGGING_OBJ_BY_OFFSET_DISTANCE_X_Y, sourceObject.getObjectId(), xOffset, yOffset));
				isSwitchToFrame = switchToFrame(sourceObject);
				(new Actions(DriverFactory.getWebDriver())).dragAndDropBy(findWebElement(sourceObject), xOffset, yOffset)
						.perform();
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DRAGGED_OBJ_BY_OFFSET_DISTANCE_X_Y, sourceObject.getObjectId(), xOffset, yOffset));
			} finally {
				if (isSwitchToFrame) {
					switchToDefaultContent();
				}
			}
		} , flowControl, true, StringConstants.KW_MSG_CANNOT_DRAG_AND_DROP_BY_OFFSET_DISTANCE)
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
	public static void authenticate(final String url, String userName, String password, int timeout,
			FailureHandling flowControl) {
		WebUIKeywordMain.runKeyword({
			Thread navigateThread = null;
			try {
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
				logger.logInfo(MessageFormat.format(StringConstants.KW_LGO_INFO_NAVIGATING_TO_AUTHENTICATED_PAGE, url, userName, password));
				navigateThread = new Thread() {
							public void run() {
								driver.get(url);
							}
						};
				navigateThread.start();
				Thread.sleep(timeout * 1000);
				if (DriverFactory.getExecutedBrowser() == WebUIDriverType.IE_DRIVER) {
					DriverFactory.getWebDriver().get("javascript:{document.getElementById('overridelink').click();}");
					// ((JavascriptExecutor)DriverFactory.getWebDriver()).executeScript("document.getElementById('overridelink').click();");
					Thread.sleep(3000);
				}
				// send username and pasword to authentication popup
				screenUtil.authenticate(userName, password);
				logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_NAVIAGTED_TO_AUTHENTICATED_PAGE, url, userName, password));
			} finally {
				if (navigateThread != null && navigateThread.isAlive()) {
					navigateThread.interrupt();
				}
			}
		} , flowControl, true, StringConstants.KW_MSG_CANNOT_NAV_TO_AUTHENTICATED_PAGE)
	}

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
				WebUIKeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_PASSED_IMG_X_IS_NOT_PRESENT, imagePath), flowControl, null, true)
			}
		} , flowControl, true, (imagePath != null) ?
		MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_FOR_IMG_X_TOBE_PRESENT, imagePath) :
		StringConstants.KW_MSG_CANNOT_WAIT_FOR_IMG_TOBE_PRESENT)
		return present;
	}

	@CompileStatic
	public static WebElement findWebElement(TestObject to, int timeOut = RunConfiguration.getTimeOut()) throws IllegalArgumentException, WebElementNotFoundException, StepFailedException {
		List<WebElement> elements = findWebElements(to, timeOut);
		if (elements != null && elements.size() > 0) {
			return elements.get(0);
		} else {
			throw new WebElementNotFoundException(to.getObjectId(), buildLocator(to));
		}
	}

	@CompileStatic
	public static List<WebElement> findWebElements(TestObject to, int timeOut) throws WebElementNotFoundException {
		timeOut = WebUiCommonHelper.checkTimeout(timeOut);
		final By locator = buildLocator(to);
		try {
			if (locator != null) {
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_FINDING_WEB_ELEMENT_W_ID, to.getObjectId(), locator.toString(), timeOut));
				// Handle firefox problems regarding issue
				// https://code.google.com/p/selenium/issues/detail?id=4757
				WebDriver webDriver = DriverFactory.getWebDriver();

				float timeCount = 0;
				while (timeCount < timeOut) {
					try {
						List<WebElement> webElements = webDriver.findElements(locator);
						if (webElements != null && webElements.size() > 0) {
							logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_FINDING_WEB_ELEMENT_W_ID_SUCCESS, webElements.size(),
									to.getObjectId(), locator.toString(), timeOut));
							return webElements;
						}
					} catch (NoSuchElementException e) {
						// not found element yet, moving on
					}
					Thread.sleep(500);
					timeCount += 0.5;
				}
			} else {
				throw new IllegalArgumentException(MessageFormat.format(StringConstants.KW_EXC_WEB_ELEMENT_W_ID_DOES_NOT_HAVE_SATISFY_PROP, to.getObjectId()));
			}
		} catch (TimeoutException e) {
			// timeOut, do nothing
		} catch (InterruptedException e) {
			// interrupted, do nothing
		}
		return Collections.emptyList();
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_FRAME)
	public static boolean switchToFrame(TestObject to, int timeOut = RunConfiguration.getTimeOut()) throws IllegalArgumentException,
	WebElementNotFoundException, StepFailedException, WebDriverException {
		TestObject parentObject = to != null ? to.getParentObject() : null;
		List<TestObject> frames = new ArrayList<TestObject>();
		while (parentObject != null) {
			frames.add(parentObject);
			parentObject = parentObject.getParentObject();
		}
        boolean isSwitchIntoFrame = false;
		if (frames.size() > 0) {
			logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_OBJ_X_HAS_PARENT_FRAME, to.getObjectId()));
			WebDriver webDriver = DriverFactory.getWebDriver();
			for (int i = frames.size() - 1; i >= 0; i--) {
				TestObject frameObject = frames.get(i);
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SWITCHING_TO_IFRAME_X, frameObject.getObjectId()));
				WebElement frameElement = findWebElement(frameObject, timeOut);
				if (frameElement != null) {
					webDriver.switchTo().frame(frameElement);
					isSwitchIntoFrame = true;
					logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SWITCHED_TO_IFRAME_X, frameObject.getObjectId()));
				}
			}
		}
		return isSwitchIntoFrame;
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
	public static void takeScreenShot(FailureHandling flowControl) {
		WebUIKeywordMain.runKeyword({
			String screenFileName = FileUtil.takesScreenshot();
			if (screenFileName != null) {
				Map<String, String> attributes = new HashMap<String, String>();
				attributes.put(com.kms.katalon.core.constants.StringConstants.XML_LOG_ATTACHMENT_PROPERTY, screenFileName)
				logger.logPassed("Taking screenshot successfully", attributes);
			}
		} , flowControl, true, StringConstants.KW_LOG_WARNING_CANNOT_TAKE_SCREENSHOT)
		
	}
}