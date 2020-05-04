package com.kms.katalon.core.windows.keyword.builtin;

import groovy.transform.CompileStatic
import io.appium.java_client.windows.WindowsDriver
import java.text.MessageFormat
import org.openqa.selenium.WebElement

import com.kms.katalon.core.annotation.internal.Action;
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.internal.KeywordMain
import com.kms.katalon.core.keyword.internal.AbstractKeyword
import com.kms.katalon.core.keyword.internal.SupportLevel
import com.kms.katalon.core.windows.constants.StringConstants
import com.kms.katalon.core.windows.driver.WindowsDriverFactory
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.WindowsTestObject
import com.kms.katalon.core.windows.keyword.helper.WindowsActionHelper
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.util.CryptoUtil

@Action(value = StringConstants.SET_ENCRYPTED_TEXT_KEYWORD)
public class SetEncryptedTextKeyword extends AbstractKeyword {
    
    private KeywordLogger logger = KeywordLogger.getInstance(SetTextKeyword.class)

    @Override
    public SupportLevel getSupportLevel(Object ...params) {
        return SupportLevel.NOT_SUPPORT;
    }

    @Override
    public Object execute(Object ...params) {
        WindowsTestObject testObject = (WindowsTestObject) params[0]
        String encryptedText = (String) params[1]
        FailureHandling flowControl = (FailureHandling)(params.length > 2 && params[2] instanceof FailureHandling ? params[2] : RunConfiguration.getDefaultFailureHandling())
        setEncryptedText(testObject, encryptedText, flowControl)
    }
    
    public String setEncryptedText(WindowsTestObject testObject, String encryptedText, FailureHandling flowControl) throws StepFailedException {
        return (String) KeywordMain.runKeyword({
            WindowsDriver windowsDriver = WindowsDriverFactory.getWindowsDriver()
            if (windowsDriver == null) {
                KeywordMain.stepFailed(StringConstants.COMM_WINDOWS_HAS_NOT_STARTED, flowControl)
            }
            
            if(encryptedText == null){
                KeywordMain.stepFailed(StringConstants.KW_ENCRYPTED_TEXT_IS_NULL, flowControl)
            }
            
            CryptoUtil.CrytoInfo cryptoInfo = CryptoUtil.getDefault(encryptedText)
            String rawText = CryptoUtil.decode(cryptoInfo)

            WindowsActionHelper windowsActionHelper = WindowsActionHelper.create(WindowsDriverFactory.getWindowsSession())
            
            logger.logDebug("Clearing text of object " + testObject.getObjectId())
            windowsActionHelper.clearText(testObject);
            logger.logDebug("Setting  text of object " + testObject.getObjectId() + " to value ******")
            windowsActionHelper.setText(testObject, rawText)

            logger.logPassed("Text ****** has been set on object " + testObject.getObjectId())

        }, flowControl, (testObject != null) ? "Unable to set encrypted text for object " + testObject.getObjectId()
        : "Unable to set text")
    }
}
