package com.kms.katalon.core.webui.keyword.builtin;

import groovy.transform.CompileStatic
import java.text.MessageFormat
import org.openqa.selenium.WebElement

import com.kms.katalon.core.annotation.internal.Action;
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.keyword.internal.SupportLevel
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.keyword.internal.WebUIAbstractKeyword
import com.kms.katalon.core.webui.keyword.internal.WebUIKeywordMain
import com.kms.katalon.core.logging.KeywordLogger

@Action(value = "setSecuredText")
public class SetSecuredTextKeyword extends WebUIAbstractKeyword {

    @CompileStatic
    @Override
    public SupportLevel getSupportLevel(Object ...params) {
        return super.getSupportLevel(params)
    }

    @CompileStatic
    @Override
    public Object execute(Object... params) {
        TestObject to = getTestObject(params[0])
        String rawText = (String) params[1]
        FailureHandling flowControl = (FailureHandling)(params.length > 2 && params[2] instanceof FailureHandling ? params[2] : RunConfiguration.getDefaultFailureHandling())
//        return setSecuredText(to, rawText, flowControl)
        return null
    }
    
    @CompileStatic
    public void setSecuredText(TestObject to, String encryptedText, FailureHandling flowControl) {
        return WebUIKeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false
            try {
                WebUiCommonHelper.checkTestObjectParameter(to)
                logger.logInfo(StringConstants.KW_LOG_INFO_CHECKING_TXT)
                if (encryptedText == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_TXT_IS_NULL)
                }
                isSwitchIntoFrame = WebUiCommonHelper.switchToParentFrame(to)
                WebElement webElement = WebUIAbstractKeyword.findWebElement(to)
                logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_CLEARING_OBJ_TXT, to.getObjectId()))
                webElement.clear()

                webElement = WebUIAbstractKeyword.findWebElement(to)
                String encryte
            } finally {
            
            }
        })
    }
}
