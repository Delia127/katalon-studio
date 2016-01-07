package com.kms.katalon.core.webui.common;

import java.awt.Rectangle;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.helper.KeywordHelper;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.testobject.ConditionType;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.testobject.TestObjectProperty;
import com.kms.katalon.core.util.ExceptionsUtil;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.exception.WebElementNotFoundException;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;

public class WebUiCommonHelper extends KeywordHelper {
    private static final String WEB_ELEMENT_TAG = "tag";
    private static final String XPATH_PREFIX = "//";
    private static final String XPATH_ATTRIBUTE_PREFIX = "@";
    private static final String XPATH_CONDITION_TYPE_NOT_MATCHES = "not(matches(%s,'%s'))";
    private static final String XPATH_CONDITION_TYPE_MATCHES = "matches(%s,'%s')";
    private static final String XPATH_CONDITION_TYPE_ENDS_WITH = "ends-with(%s,'%s')";
    private static final String XPATH_CONDITION_TYPE_STARTS_WITH = "starts-with(%s,'%s')";
    private static final String XPATH_CONDITION_TYPE_NOT_EQUALS = "%s != '%s'";
    private static final String XPATH_CONDITION_TYPE_NOT_CONTAINS = "not(contains(%s,'%s'))";
    private static final String XPATH_CONDITION_TYPE_EQUALS = "%s = '%s'";
    private static final String XPATH_CONDITION_TYPE_CONTAINS = "contains(%s,'%s')";
    private static final String CSS_METHOD_SUFFIX = "()";
    private static final String XPATH_GET_TEXT_METHOD = "text()";
    private static final String WEB_ELEMENT_ATTRIBUTE_LINK_TEXT = "link_text";
    private static final String WEB_ELEMENT_ATTRIBUTE_TEXT = "text";
    private static final String WEB_ELEMENT_XPATH = "xpath";
    private static KeywordLogger logger = KeywordLogger.getInstance();

    private static final String XPATH_INTESECTION_FORMULA = "%s[count(. | %s) = count(%s)]";

    public static boolean isTextPresent(WebDriver webDriver, String text, boolean isRegex) throws WebDriverException,
            IllegalArgumentException {
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        logger.logInfo(MessageFormat.format(StringConstants.COMM_EXC_CHECKING_TEXT_PRESENT, regularExpressionLog));
        if (text == null) {
            throw new IllegalArgumentException(StringConstants.COMM_EXC_TEXT_IS_NULL);
        }

        boolean isContained = false;
        WebElement bodyElement = webDriver.findElement(By.tagName("body"));
        String pageText = bodyElement.getText();

        logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_FINDING_TEXT_ON_PAGE, text,
                regularExpressionLog));
        if (pageText != null && !pageText.isEmpty()) {
            if (isRegex) {
                Pattern pattern = Pattern.compile(text);
                Matcher matcher = pattern.matcher(pageText);
                while (matcher.find()) {
                    isContained = true;
                    break;
                }
            } else {
                isContained = pageText.contains(text);
            }
        }
        return isContained;
    }

    public static boolean switchToWindowUsingTitle(WebDriver webDriver, String title) throws WebDriverException,
            InterruptedException {
        float timeCount = 0;
        while (timeCount < RunConfiguration.getTimeOut()) {
            Set<String> availableWindows = webDriver.getWindowHandles();
            for (String windowId : availableWindows) {
                webDriver = webDriver.switchTo().window(windowId);
                if (webDriver.getTitle().equals(title)) {
                    return true;
                }
            }
            Thread.sleep(200);
            timeCount += 0.2;
        }
        return false;
    }

    public static boolean closeWindowUsingTitle(WebDriver webDriver, String title) throws InterruptedException {
        float timeCount = 0;
        while (timeCount < RunConfiguration.getTimeOut()) {
            Set<String> availableWindows = webDriver.getWindowHandles();
            for (String windowId : availableWindows) {
                webDriver = webDriver.switchTo().window(windowId);
                if (webDriver.getTitle().equals(title)) {
                    webDriver.close();
                    return true;
                }
            }
            Thread.sleep(200);
            timeCount += 0.2;
        }
        return false;
    }

    public static boolean switchToWindowUsingUrl(WebDriver webDriver, String url) throws InterruptedException {
        float timeCount = 0;
        while (timeCount < RunConfiguration.getTimeOut()) {
            Set<String> availableWindows = webDriver.getWindowHandles();
            for (String windowId : availableWindows) {
                if (webDriver.switchTo().window(windowId).getCurrentUrl().equals(url)) {
                    return true;
                }
            }
            Thread.sleep(200);
            timeCount += 0.2;
        }
        return false;
    }

    public static boolean closeWindowUsingUrl(WebDriver webDriver, String url) throws InterruptedException {
        float timeCount = 0;
        while (timeCount < RunConfiguration.getTimeOut()) {
            Set<String> availableWindows = webDriver.getWindowHandles();
            for (String windowId : availableWindows) {
                if (webDriver.switchTo().window(windowId).getCurrentUrl().equals(url)) {
                    webDriver.close();
                    return true;
                }
            }
            Thread.sleep(200);
            timeCount += 0.2;
        }
        return false;
    }

    public static boolean switchToWindowUsingIndex(WebDriver webDriver, int index) throws InterruptedException {
        float timeCount = 0;
        while (timeCount < RunConfiguration.getTimeOut()) {
            List<String> availableWindows = new ArrayList<String>(webDriver.getWindowHandles());
            if (index >= 0 && index < availableWindows.size()) {
                webDriver.switchTo().window(availableWindows.get(index));
                return true;
            }
            Thread.sleep(200);
            timeCount += 0.2;
        }
        return false;
    }

    public static boolean closeWindowUsingIndex(WebDriver webDriver, int index) throws InterruptedException {
        float timeCount = 0;
        while (timeCount < RunConfiguration.getTimeOut()) {
            List<String> availableWindows = new ArrayList<String>(webDriver.getWindowHandles());
            if (index >= 0 && index < availableWindows.size()) {
                webDriver.switchTo().window(availableWindows.get(index));
                webDriver.close();
                return true;
            }
            Thread.sleep(200);
            timeCount += 0.2;
        }
        return false;
    }

    public static void checkSelectIndex(Integer[] indexes, Select select) throws IllegalArgumentException {
        logger.logInfo(StringConstants.COMM_LOG_INFO_CHECKING_INDEX_PARAMS);
        List<WebElement> allSelectOptions = select.getOptions();
        if (allSelectOptions.size() > 0) {
            for (int index : indexes) {
                if (index < 0 || index >= allSelectOptions.size()) {
                    throw new IllegalArgumentException(MessageFormat.format(StringConstants.COMM_EXC_INVALID_INDEX,
                            index, (allSelectOptions.size() - 1)));
                }
            }
        }
    }

    public static void selectOrDeselectAllOptions(Select select, boolean isSelect, TestObject to) {
        if (isSelect) {
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SELECTING_ALL_OPT_ON_OBJ, to.getObjectId()));
        } else {
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_DESELECTING_ALL_OPTS_ON_OBJ,
                    to.getObjectId()));
        }
        for (int index = 0; index < select.getOptions().size(); index++) {
            if (isSelect) {
                select.selectByIndex(index);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_OPT_W_INDEX_X_IS_SELECTED, index));
            } else {
                select.deselectByIndex(index);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_DESELECTED_OPT_IDX_X, index));
            }
        }
    }

    public static void selectOrDeselectOptionsByIndex(Select select, Integer[] indexes, boolean isSelect, TestObject to) {
        WebUiCommonHelper.checkSelectIndex(indexes, select);
        if (isSelect) {
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SELECTING_OBJ_OPTS_W_INDEX_IN,
                    to.getObjectId(), WebUiCommonHelper.integerArrayToString(indexes)));
        } else {
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_DESELECTING_OPTS_ON_OBJ_W_IDX,
                    to.getObjectId(), WebUiCommonHelper.integerArrayToString(indexes)));
        }
        for (int index : indexes) {
            if (isSelect) {
                select.selectByIndex(index);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_OPT_W_INDEX_X_IS_SELECTED, index));
            } else {
                select.deselectByIndex(index);
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_DESELECTED_OPT_IDX_X, index));
            }
        }
    }

    public static void selectOrDeselectOptionsByValue(Select select, String value, boolean isRegex, boolean isSelect,
            TestObject to, String regularExpressionLog) {
        List<WebElement> allOptions = select.getOptions();
        if (isSelect) {
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SELECTING_OPTS_ON_OBJ_X_W_VAL_Y,
                    to.getObjectId(), value, regularExpressionLog));
        } else {
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_DESELECTING_OPTS_ON_OBJ_W_VAL,
                    to.getObjectId(), value, regularExpressionLog));
        }
        for (int index = 0; index < allOptions.size(); index++) {
            String optionValue = allOptions.get(index).getAttribute("value");
            if (optionValue != null && WebUiCommonHelper.match(optionValue, value, isRegex)) {
                if (isSelect) {
                    select.selectByIndex(index);
                    logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SELECTED_OPT_AT_INDEX_W_VAL, index,
                            optionValue, regularExpressionLog));
                } else {
                    select.deselectByIndex(index);
                    logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_OPT_AT_IDX_X_W_VAL_Y_IS_SELECTED,
                            index, optionValue, regularExpressionLog));
                }
                if (!isRegex) {
                    break;
                }
            }
        }
    }

    public static void selectOrDeselectOptionsByLabel(Select select, String label, boolean isRegex, boolean isSelect,
            TestObject to, String regularExpressionLog) {
        List<WebElement> allOptions = select.getOptions();
        if (isSelect) {
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SELECTING_OPTS_ON_OBJ_X_W_LBL_Y,
                    to.getObjectId(), label, regularExpressionLog));
        } else {
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_DESELECTING_OPTS_ON_OBJ_X_W_LBL_Y,
                    to.getObjectId(), label, regularExpressionLog));
        }
        for (int index = 0; index < allOptions.size(); index++) {
            String optionValue = allOptions.get(index).getText();
            if (optionValue != null && WebUiCommonHelper.match(optionValue, label, isRegex)) {
                if (isSelect) {
                    select.selectByIndex(index);
                    logger.logInfo(MessageFormat.format(
                            StringConstants.KW_LOG_INFO_OPT_AT_IDX_X_W_LBL_TXT_Y_IS_SELECTED, index, optionValue,
                            regularExpressionLog));
                } else {
                    select.deselectByIndex(index);
                    logger.logInfo(MessageFormat.format(
                            StringConstants.KW_LOG_INFO_OPT_AT_IDX_X_W_LBL_TXT_Y_IS_DESELECTED, index, optionValue,
                            regularExpressionLog));
                }
                if (!isRegex) {
                    break;
                }
            }
        }
    }

    public static int getNumberOfOptionByLabel(Select select, String label, boolean isRegex, String objectId) {
        int count = 0;
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_COUNTING_NUM_OPTS_W_LBL_PRESENT_ON_OBJ,
                label, objectId, regularExpressionLog));
        List<WebElement> allOptions = select.getOptions();
        for (int index = 0; index < allOptions.size(); index++) {
            String optionLabel = allOptions.get(index).getText();
            if (optionLabel != null && KeywordHelper.match(optionLabel, label, isRegex)) {
                count++;
                logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_OPT_AT_INDEX_W_LBL_IS_PRESENT, index,
                        optionLabel, regularExpressionLog));
            }
        }
        return count;
    }

    public static int getNumberOfOptionByValue(Select select, String value, boolean isRegex, String objectId) {
        int count = 0;
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_COUNTING_NUM_OPTS_W_VAL_PRESENT_ON_OBJ,
                value, objectId, regularExpressionLog));
        List<WebElement> allOptions = select.getOptions();
        for (int index = 0; index < allOptions.size(); index++) {
            String optionValue = allOptions.get(index).getAttribute("value");
            if (optionValue != null && KeywordHelper.match(optionValue, value, isRegex)) {
                count++;
                logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_OPT_AT_INDEX_W_VAL_IS_PRESENT, index,
                        optionValue, regularExpressionLog));
            }
        }
        return count;
    }

    public static int getNumberOfSelectedOptionByLabel(Select select, String label, boolean isRegex, String objectId) {
        int count = 0;
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_COUNTING_NUM_OPTS_W_LBL_SELECTED_ON_OBJ,
                label, objectId, regularExpressionLog));
        List<WebElement> allOptions = select.getOptions();
        List<WebElement> allSelectedOptions = select.getAllSelectedOptions();
        for (int index = 0; index < allOptions.size(); index++) {
            String optionLabel = allOptions.get(index).getText();
            if (optionLabel != null && KeywordHelper.match(optionLabel, label, isRegex)) {
                if (allSelectedOptions.contains(allOptions.get(index))) {
                    count++;
                    logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_OPT_AT_INDEX_W_LBL_IS_SELECTED,
                            index, optionLabel, regularExpressionLog));
                }
            }
        }
        return count;
    }

    public static int getNumberOfNotSelectedOptionByLabel(Select select, String label, boolean isRegex, String objectId) {
        int count = 0;
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_COUNTING_NUM_OPTS_W_LBL_NOT_SELECTED_ON_OBJ,
                label, objectId, regularExpressionLog));
        List<WebElement> allOptions = select.getOptions();
        List<WebElement> allSelectedOptions = select.getAllSelectedOptions();
        for (int index = 0; index < allOptions.size(); index++) {
            String optionLabel = allOptions.get(index).getText();
            if (optionLabel != null && KeywordHelper.match(optionLabel, label, isRegex)) {
                if (!allSelectedOptions.contains(allOptions.get(index))) {
                    count++;
                    logger.logInfo(MessageFormat.format(
                            StringConstants.COMM_LOG_INFO_OPT_AT_INDEX_W_LBL_IS_NOT_SELECTED, index, optionLabel,
                            regularExpressionLog));
                }
            }
        }
        return count;
    }

    public static int getNumberOfSelectedOptionByValue(Select select, String value, boolean isRegex, String objectId) {
        int count = 0;
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_COUNTING_NUM_OPTS_W_VAL_SELECTED_ON_OBJ,
                value, objectId, regularExpressionLog));
        List<WebElement> allOptions = select.getOptions();
        List<WebElement> allSelectedOptions = select.getAllSelectedOptions();
        for (int index = 0; index < allOptions.size(); index++) {
            String optionValue = allOptions.get(index).getAttribute("value");
            if (optionValue != null && KeywordHelper.match(optionValue, value, isRegex)) {
                if (allSelectedOptions.contains(allOptions.get(index))) {
                    count++;
                    logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_OPT_AT_INDEX_W_VAL_IS_SELECTED,
                            index, optionValue, regularExpressionLog));
                }
            }
        }
        return count;
    }

    public static int getNumberOfNotSelectedOptionByValue(Select select, String value, boolean isRegex, String objectId) {
        int count = 0;
        String regularExpressionLog = ((isRegex) ? " using regular expression" : "");
        logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_COUNTING_NUM_OPTS_W_VAL_NOT_SELECTED_ON_OBJ,
                value, objectId, regularExpressionLog));
        List<WebElement> allOptions = select.getOptions();
        List<WebElement> allSelectedOptions = select.getAllSelectedOptions();
        for (int index = 0; index < allOptions.size(); index++) {
            String optionValue = allOptions.get(index).getAttribute("value");
            if (optionValue != null && KeywordHelper.match(optionValue, value, isRegex)) {
                if (!allSelectedOptions.contains(allOptions.get(index))) {
                    count++;
                    logger.logInfo(MessageFormat.format(
                            StringConstants.COMM_LOG_INFO_OPT_AT_INDEX_W_VAL_IS_NOT_SELECTED, index, optionValue,
                            regularExpressionLog));
                }
            }
        }
        return count;
    }

    public static int getNumberOfSelectedOptionByIndex(Select select, Integer[] indexes, String objectId)
            throws IllegalArgumentException {
        logger.logInfo(MessageFormat.format(
                StringConstants.COMM_LOG_INFO_COUNTING_NUM_OPTS_W_INDEX_RANGE_SELECTED_ON_OBJ,
                integerArrayToString(indexes), objectId));
        int count = 0;
        List<WebElement> allSelectedOptions = select.getAllSelectedOptions();
        for (int index : indexes) {
            // Index is 0-based, lstIndexes is list of 0-based indexing
            // number
            if (allSelectedOptions.contains(select.getOptions().get(index))) {
                count++;
                logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_OPT_AT_INDEX_IS_SELECTED, index));
            }
        }
        return count;
    }

    public static int getNumberOfNotSelectedOptionByIndex(Select select, Integer[] indexes, String objectId) {
        logger.logInfo(MessageFormat.format(
                StringConstants.COMM_LOG_INFO_COUNTING_NUM_OPTS_W_INDEX_RANGE_NOT_SELECTED_ON_OBJ,
                integerArrayToString(indexes), objectId));
        int count = 0;
        List<WebElement> allSelectedOptions = select.getAllSelectedOptions();
        for (int index : indexes) {
            // Index is 0-based, lstIndexes is list of 0-based indexing
            // number
            if (!allSelectedOptions.contains(select.getOptions().get(index))) {
                count++;
                logger.logInfo(MessageFormat.format(StringConstants.COMM_LOG_INFO_OPT_AT_INDEX_IS_NOT_SELECTED, index));
            }
        }
        return count;
    }

    public static void focusOnBrowser() throws WebDriverException, StepFailedException {
        ((JavascriptExecutor) DriverFactory.getWebDriver()).executeScript("window.focus()");
    }

    public static By buildLocator(TestObject to) {
        List<String> xpathList = new ArrayList<String>();
        for (TestObjectProperty property : to.getActiveProperties()) {
            String xpath = buildXpath(property);
            if (xpath != null) {
                xpathList.add(xpath);
            }
        }
        return intersectXpathList(xpathList);
    }

    public static By intersectXpathList(List<String> xpathList) {
        StringBuilder xpathString = new StringBuilder();
        for (String xpath : xpathList) {
            if (xpathString.toString().isEmpty()) {
                xpathString.append(xpath);
            } else {
                String existingXpath = xpathString.toString();
                xpathString = new StringBuilder(String.format(XPATH_INTESECTION_FORMULA, existingXpath, xpath, xpath));
            }
        }
        if (!xpathString.toString().isEmpty()) {
            return By.xpath(xpathString.toString());
        }
        return null;
    }

    public static String buildXpath(TestObjectProperty property) {
        String propertyName = property.getName();
        String propertyValue = property.getValue();
        if (propertyName.equals(WEB_ELEMENT_XPATH)) {
            return propertyValue;
        }
        if (propertyName.equals(WEB_ELEMENT_TAG)) {
            return "//" + propertyValue;
        }
        StringBuilder expression = new StringBuilder();
        if (propertyName.equals(WEB_ELEMENT_ATTRIBUTE_TEXT) || propertyName.equals(WEB_ELEMENT_ATTRIBUTE_LINK_TEXT)) {
            propertyName = XPATH_GET_TEXT_METHOD;
        }
        // If attribute, append '@' before attribute name, skip it if method
        if (!propertyName.endsWith(CSS_METHOD_SUFFIX)) {
            propertyName = XPATH_ATTRIBUTE_PREFIX + propertyName;
        }

        ConditionType conditionType = property.getCondition();
        switch (conditionType) {
        case CONTAINS:
            expression.append(String.format(XPATH_CONDITION_TYPE_CONTAINS, propertyName, propertyValue));
            break;
        case ENDS_WITH:
            expression.append(String.format(XPATH_CONDITION_TYPE_ENDS_WITH, propertyName, propertyValue));
            break;
        case EQUALS:
            expression.append(String.format(XPATH_CONDITION_TYPE_EQUALS, propertyName, propertyValue));
            break;
        case MATCHES_REGEX:
            expression.append(String.format(XPATH_CONDITION_TYPE_MATCHES, propertyName, propertyValue));
            break;
        case NOT_CONTAIN:
            expression.append(String.format(XPATH_CONDITION_TYPE_NOT_CONTAINS, propertyName, propertyValue));
            break;
        case NOT_EQUAL:
            expression.append(String.format(XPATH_CONDITION_TYPE_NOT_EQUALS, propertyName, propertyValue));
            break;
        case NOT_MATCH_REGEX:
            expression.append(String.format(XPATH_CONDITION_TYPE_NOT_MATCHES, propertyName, propertyValue));
            break;
        case STARTS_WITH:
            expression.append(String.format(XPATH_CONDITION_TYPE_STARTS_WITH, propertyName, propertyValue));
            break;
        default:
            break;

        }
        if (expression != null && !expression.toString().isEmpty()) {
            StringBuilder xpath = new StringBuilder();
            xpath.append(XPATH_PREFIX);
            xpath.append("*");
            xpath.append("[" + expression.toString() + "]");
            return xpath.toString();
        }
        return null;
    }

    public static String getBrowserAndVersion(WebDriver webDriver) {
        return (String) ((JavascriptExecutor) webDriver).executeScript("return navigator.sayswho= (function() {"
                + " var ua= navigator.userAgent, tem,"
                + " M= ua.match(/(opera|chrome|safari|firefox|msie|trident)\\/?\\s*(\\.?\\d+(\\.\\d+)*)/i) || [];"
                + " if (/trident/i.test(M[1])) {" + "     tem=  /\\brv[ :]+(\\d+)/g.exec(ua) || [];"
                + "     return 'IE '+(tem[1] || '');" + " }" + " if(M[1]=== 'Chrome') {"
                + "     tem= ua.match(/\b(OPR|Edge)\\/(\\d+)/);"
                + "     if(tem!= null) return tem.slice(1).join(' ').replace('OPR', 'Opera');" + " }"
                + " M= M[2]? [M[1], M[2]]: [navigator.appName, navigator.appVersion, '-?'];"
                + " if((tem= ua.match(/version\\/(\\d+)/i))!= null) M.splice(1, 1, tem[1]);"
                + " return M.join(' ').replace('MSIE', 'IE');" + "})();");
    }

    public static int getViewportWidth(WebDriver webDriver) {
        Long longValue = (Long) (((JavascriptExecutor) DriverFactory.getWebDriver())
                .executeScript("return Math.max(document.documentElement.clientWidth, window.innerWidth || 0);"));
        return longValue.intValue();
    }

    public static int getViewportHeight(WebDriver webDriver) {
        Long longValue = (Long) (((JavascriptExecutor) DriverFactory.getWebDriver())
                .executeScript("return Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"));
        return longValue.intValue();
    }

    public static Rectangle getElementRect(WebDriver webDriver, WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
        Number left = (Number) (javascriptExecutor.executeScript("return arguments[0].getBoundingClientRect().left",
                element));
        Number right = (Number) (javascriptExecutor.executeScript("return arguments[0].getBoundingClientRect().right",
                element));
        Number top = (Number) (javascriptExecutor.executeScript("return arguments[0].getBoundingClientRect().top",
                element));
        Number bottom = (Number) (javascriptExecutor.executeScript(
                "return arguments[0].getBoundingClientRect().bottom", element));
        return new Rectangle(left.intValue(), top.intValue(), right.intValue() - left.intValue(), bottom.intValue()
                - top.intValue());
    }

    public static boolean isElementVisibleInViewport(WebDriver driver, TestObject testObject, int timeOut)
            throws IllegalArgumentException, StepFailedException, WebElementNotFoundException {
        WebUiCommonHelper.checkTestObjectParameter(testObject);
        TestObject parentObject = testObject != null ? testObject.getParentObject() : null;
        List<TestObject> frames = new ArrayList<TestObject>();
        while (parentObject != null) {
            frames.add(parentObject);
            parentObject = parentObject.getParentObject();
        }
        boolean isSwitchIntoFrame = false;
        try {
            if (frames.size() > 0) {
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_OBJ_X_HAS_PARENT_FRAME,
                        testObject.getObjectId()));
                for (int i = frames.size() - 1; i >= 0; i--) {
                    TestObject frameObject = frames.get(i);
                    logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SWITCHING_TO_IFRAME_X,
                            frameObject.getObjectId()));
                    WebElement frameElement = findWebElement(frameObject, timeOut);
                    if (frameElement != null) {
                        logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CHECKING_TO_IFRAME_X_IN_VIEWPORT,
                                frameObject.getObjectId()));
                        if (!WebUiCommonHelper.isElementVisibleInViewport(driver, frameElement)) {
                            logger.logInfo(MessageFormat.format(
                                    StringConstants.KW_MSG_PARENT_OBJECT_IS_NOT_VISIBLE_IN_VIEWPORT,
                                    frameObject.getObjectId()));
                            return false;
                        }
                        driver.switchTo().frame(frameElement);
                        isSwitchIntoFrame = true;
                        logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_SWITCHED_TO_IFRAME_X,
                                frameObject.getObjectId()));
                    }
                }
            }

            WebElement foundElement = WebUiBuiltInKeywords.findWebElement(testObject, timeOut);
            return isElementVisibleInViewport(driver, foundElement);
        } finally {
            if (isSwitchIntoFrame) {
                switchToDefaultContent();
            }
        }
    }

    public static boolean isElementVisibleInViewport(WebDriver driver, WebElement element) {
        Rectangle elementRect = WebUiCommonHelper.getElementRect(driver, element);
        logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_ELEMENT_RECT, elementRect.getX(),
                elementRect.getY(), elementRect.getWidth(), elementRect.getHeight()));
        Rectangle documentRect = new Rectangle(0, 0, WebUiCommonHelper.getViewportWidth(driver),
                WebUiCommonHelper.getViewportHeight(driver));
        logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_VIEWPORT_RECT, documentRect.getWidth(),
                documentRect.getHeight()));
        return documentRect.intersects(elementRect);
    }

    public static List<WebElement> findWebElements(TestObject testObject, int timeOut) {
        timeOut = WebUiCommonHelper.checkTimeout(timeOut);
        final By locator = WebUiCommonHelper.buildLocator(testObject);
        try {
            if (locator != null) {
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_FINDING_WEB_ELEMENT_W_ID,
                        testObject.getObjectId(), locator.toString(), timeOut));
                // Handle firefox problems regarding issue
                // https://code.google.com/p/selenium/issues/detail?id=4757
                WebDriver webDriver = DriverFactory.getWebDriver();

                float timeCount = 0;
                long miliseconds = System.currentTimeMillis();
                while (timeCount < timeOut) {
                    try {
                        List<WebElement> webElements = webDriver.findElements(locator);
                        if (webElements != null && webElements.size() > 0) {
                            logger.logInfo(MessageFormat.format(
                                    StringConstants.KW_LOG_INFO_FINDING_WEB_ELEMENT_W_ID_SUCCESS, webElements.size(),
                                    testObject.getObjectId(), locator.toString(), timeOut));
                            return webElements;
                        }
                    } catch (NoSuchElementException e) {
                        // not found element yet, moving on
                    }

                    timeCount += ((System.currentTimeMillis() - miliseconds) / 1000);

                    Thread.sleep(500);
                    timeCount += 0.5;

                    miliseconds = System.currentTimeMillis();
                }
            } else {
                throw new IllegalArgumentException(MessageFormat.format(
                        StringConstants.KW_EXC_WEB_ELEMENT_W_ID_DOES_NOT_HAVE_SATISFY_PROP, testObject.getObjectId()));
            }
        } catch (TimeoutException e) {
            // timeOut, do nothing
        } catch (InterruptedException e) {
            // interrupted, do nothing
        }
        return Collections.emptyList();
    }

    public static WebElement findWebElement(TestObject testObject, int timeOut) throws WebElementNotFoundException {
        List<WebElement> elements = findWebElements(testObject, timeOut);
        if (elements != null && elements.size() > 0) {
            return elements.get(0);
        } else {
            throw new WebElementNotFoundException(testObject.getObjectId(), buildLocator(testObject));
        }
    }

    /**
     * Internal method to switch to default content
     * 
     * @throws StepFailedException
     */
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
            logger.logWarning(MessageFormat.format(
                    StringConstants.KW_LOG_WARNING_SWITCHING_TO_DEFAULT_CONTENT_FAILED_BC_OF_X,
                    ExceptionsUtil.getMessageForThrowable(e)));
        }
    }
}
