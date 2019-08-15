package com.kms.katalon.core.webui.keyword.builtin

import java.util.Optional

import org.openqa.selenium.WebDriver

import com.kms.katalon.core.annotation.internal.Action
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.internal.SupportLevel
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.common.internal.SmartWaitHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.SmartWaitWebDriver
import com.kms.katalon.core.webui.keyword.internal.WebUIAbstractKeyword
import com.kms.katalon.core.webui.keyword.internal.WebUIKeywordMain
import groovy.transform.CompileStatic

@Action(value = "enableSmartWait")
class EnableSmartWaitKeyword extends WebUIAbstractKeyword {

    @CompileStatic
    @Override
    public SupportLevel getSupportLevel(Object ...params) {
        return super.getSupportLevel(params)
    }

    @CompileStatic
    @Override
    public Object execute(Object ...params) {
        smartWait();
    }

    @CompileStatic
    public void smartWait() throws StepFailedException {
        WebUIKeywordMain.runKeyword({
            boolean smartWaitEnabled = (boolean) Optional
                    .ofNullable(RunConfiguration.getExecutionProperties().get("smartWaitEnabled")).orElse(false);
            if (!smartWaitEnabled) {
                RunConfiguration.getExecutionProperties().put("smartWaitEnabled", true);
                WebDriver currentWebDriver = DriverFactory.getWebDriver();
                SmartWaitWebDriver smartWaitWebDriver = SmartWaitHelper.getSmartWaitWebDriver(currentWebDriver);
                smartWaitWebDriver.register(SmartWaitHelper.getEventListener());
                DriverFactory.changeWebDriverWithoutLog(smartWaitWebDriver);
            }
        }, FailureHandling.CONTINUE_ON_FAILURE, true, "Unable to enable smart wait !");
    }
}
