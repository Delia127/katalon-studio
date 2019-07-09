package com.kms.katalon.core.windows.keyword.builtin;

import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions

import com.kms.katalon.core.annotation.internal.Action
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.keyword.internal.AbstractKeyword;
import com.kms.katalon.core.keyword.internal.KeywordMain
import com.kms.katalon.core.keyword.internal.SupportLevel;
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.WindowsTestObject
import com.kms.katalon.core.windows.WindowsDriverFactory
import com.kms.katalon.core.windows.keyword.helper.WindowsElementHelper

import io.appium.java_client.windows.WindowsDriver

@Action(value = "doubleClick")
public class DoubleClickKeyword extends AbstractKeyword {

    private KeywordLogger logger = KeywordLogger.getInstance(DoubleClickKeyword.class)

    @Override
    public SupportLevel getSupportLevel(Object ...params) {
        return SupportLevel.NOT_SUPPORT
    }

    @Override
    public Object execute(Object ...params) {
        WindowsTestObject testObject = (WindowsTestObject) params[0]
        FailureHandling flowControl = (FailureHandling)(params.length > 1 && params[1] instanceof FailureHandling ? params[1] : RunConfiguration.getDefaultFailureHandling())
        doubleClick(testObject, flowControl)
    }

    public void doubleClick(WindowsTestObject testObject, FailureHandling flowControl) throws StepFailedException {

        KeywordMain.runKeyword({
            WindowsDriver windowsDriver = WindowsDriverFactory.getWindowsDriver()

            WebElement webElement = WindowsElementHelper.findElement(testObject)

            if (webElement == null) {
                throw new StepFailedException("Element: " + testObject.getObjectId() + " not found")
            }


            logger.logInfo('Double clicking on element: ' + testObject.getObjectId())

            Actions action = new Actions(windowsDriver)
            action.moveToElement(webElement)
            action.doubleClick()
            action.perform()

            logger.logPassed('Double click on element: ' + testObject.getObjectId() + ' succesfully')
        }, flowControl)
    }
}
