package com.kms.katalon.core.windows.keyword.builtin

import io.appium.java_client.windows.WindowsDriver
import java.text.MessageFormat
import java.util.concurrent.TimeUnit
import java.util.function.Function
import java.time.Duration;

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

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.annotation.internal.Action
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.BuiltinKeywords
import com.kms.katalon.core.keyword.internal.AbstractKeyword
import com.kms.katalon.core.keyword.internal.KeywordExecutor
import com.kms.katalon.core.keyword.internal.SupportLevel
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.WindowsTestObject
import com.kms.katalon.core.util.internal.ExceptionsUtil
import com.kms.katalon.core.util.internal.PathUtil
import com.kms.katalon.core.windows.driver.WindowsDriverFactory
import com.kms.katalon.core.windows.driver.WindowsSession
import com.kms.katalon.core.keyword.internal.KeywordMain
import com.kms.katalon.core.helper.KeywordHelper
import com.kms.katalon.core.windows.keyword.helper.WindowsActionHelper

@Action(value = "waitForElementAttributeValue")
public class WaitForElementAttributeValueKeyword extends AbstractKeyword {
    
    private KeywordLogger logger = KeywordLogger.getInstance(WaitForElementAttributeValueKeyword.class)
    
    @Override
    public SupportLevel getSupportLevel(Object ...params) {
        return SupportLevel.NOT_SUPPORT
    }

    @Override
    public Object execute(Object ...params) {
        WindowsTestObject testObject = (WindowsTestObject) params[0]
        String attributeName = (String) params[1]
        String attributeValue = (String) params[2]
        int timeOut = (int) params[3]
        FailureHandling flowControl = (FailureHandling)(params.length > 4 && params[4] instanceof FailureHandling ? params[4] : RunConfiguration.getDefaultFailureHandling())
        waitForElementAttributeValue(testObject, attributeName,attributeValue,timeOut,flowControl)
    }

    public void waitForElementAttributeValue(WindowsTestObject testObject, String attributeName, String attributeValue, int timeOut, FailureHandling flowControl) {
        KeywordMain.runKeyword({
            WindowsDriver windowsDriver = WindowsDriverFactory.getWindowsDriver()
            if (windowsDriver == null) {
                KeywordMain.stepFailed("WindowsDriver has not started. Please try Windows.startApplication first.", flowControl)
            }

            logger.logDebug("Checking attribute name")
            if (attributeName == null) {
                throw new IllegalArgumentException("Attribute name is null")
            }
            
            logger.logDebug("Checking attribute value")
            if (attributeValue == null) {
                throw new IllegalArgumentException("Attribute value is null")
            }
            
            timeOut = WindowsActionHelper.checkTimeout(timeOut)
            
            try {
                WebElement foundElement = WindowsActionHelper.create(WindowsDriverFactory.getWindowsSession()).findElement(testObject, timeOut, true)
                logger.logDebug(String.format("Getting attribute '%s' of object '%s'", attributeName, testObject.getObjectId()))

                FluentWait<WebElement> wait = new FluentWait<WebElement>(foundElement)
                        .withTimeout(Duration.ofSeconds(timeOut))
                        .pollingEvery(Duration.ofMillis(500))

                Boolean hasAttribute = wait.until(new Function<WebElement, Boolean>(){
                            @Override
                            public Boolean apply(WebElement element){
                                return element.getAttribute(attributeName) == attributeValue
                            }
                        })
                if (hasAttribute){
                    logger.logPassed(String.format("Object '%s' has attribute '%s' with value '%s'", testObject.getObjectId(), attributeName, attributeValue))
                }
            } catch (TimeoutException e) {
                KeywordMain.stepFailed(String.format("Unable to wait for object '%s' to have attribute '%s' with value '%s'", testObject.getObjectId(), attributeName, attributeValue), flowControl)
            }
        }, flowControl, (testObject != null) ? String.format("Unable to wait for object '%s' to have attribute '%s' with value '%s'", testObject.getObjectId(), attributeName, attributeValue)
        : "Unable to wait for element to have attribute with value");
    }
}
