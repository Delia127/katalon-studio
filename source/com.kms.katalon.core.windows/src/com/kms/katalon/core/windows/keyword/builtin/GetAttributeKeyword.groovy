package com.kms.katalon.core.windows.keyword.builtin

import groovy.transform.CompileStatic
import io.appium.java_client.windows.WindowsDriver
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

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.annotation.internal.Action
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.BuiltinKeywords
import com.kms.katalon.core.keyword.internal.AbstractKeyword
import com.kms.katalon.core.keyword.internal.KeywordMain
import com.kms.katalon.core.keyword.internal.KeywordExecutor
import com.kms.katalon.core.keyword.internal.SupportLevel
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.WindowsTestObject
import com.kms.katalon.core.util.internal.ExceptionsUtil
import com.kms.katalon.core.windows.constants.StringConstants
import com.kms.katalon.core.windows.driver.WindowsDriverFactory
import com.kms.katalon.core.windows.keyword.helper.WindowsActionHelper
import com.kms.katalon.core.util.internal.PathUtil

@Action(value = StringConstants.GET_ATTRIBUTE_KEYWORD)
public class GetAttributeKeyword extends AbstractKeyword {
    
    private KeywordLogger logger = KeywordLogger.getInstance(GetTextKeyword.class)
    
    @Override
    public SupportLevel getSupportLevel(Object ...params) {
        return SupportLevel.NOT_SUPPORT
    }

    @Override
    public Object execute(Object ...params) {
        WindowsTestObject testObject = (WindowsTestObject) params[0]
        String attribute = (String) params[1]
        FailureHandling flowControl = (FailureHandling)(params.length > 2 && params[2] instanceof FailureHandling ? params[2] : RunConfiguration.getDefaultFailureHandling())
        return getAttribute(testObject,attribute,flowControl)
    }

    public String getAttribute(WindowsTestObject testObject, String attribute, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            String attrValue = ""
            
            logger.logDebug("Checking attribute")
            if (attribute == null) {
                throw new IllegalArgumentException("Attribute is null")
            }
            
            WindowsDriver windowsDriver = WindowsDriverFactory.getWindowsDriver()
            if (windowsDriver == null) {
                KeywordMain.stepFailed("WindowsDriver has not started. Please try Windows.startApplication first.", flowControl)
            }
            
            WebElement element = WindowsActionHelper.create(WindowsDriverFactory.getWindowsSession()).findElement(testObject)
            logger.logDebug("Getting attribute " + attribute + " of object " + testObject.getObjectId())
           
             attrValue = element.getAttribute(attribute)
             logger.logPassed("Attribute " + attribute + " of object " + testObject.getObjectId() +" is: " + attrValue)
             return attrValue
        }, flowControl, (testObject != null && attribute != null) ? "Unable to get attribute " + attribute + " of object " + testObject.getObjectId()
        : "Unable to get attribute")
    }
}
