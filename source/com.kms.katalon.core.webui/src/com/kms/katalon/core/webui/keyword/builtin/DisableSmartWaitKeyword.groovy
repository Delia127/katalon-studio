package com.kms.katalon.core.webui.keyword.builtin;


import com.kms.katalon.core.annotation.internal.Action
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.internal.SupportLevel
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.SmartWaitWebDriver
import com.kms.katalon.core.webui.keyword.internal.WebUIAbstractKeyword
import com.kms.katalon.core.webui.keyword.internal.WebUIKeywordMain

import groovy.transform.CompileStatic

@Action(value = "disableSmartWait")
class DisableSmartWaitKeyword extends WebUIAbstractKeyword {

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
            boolean smartWaitEnabled = (boolean) RunConfiguration.getExecutionProperties().get(RunConfiguration.SMART_WAIT_MODE);
            if (smartWaitEnabled) {
                RunConfiguration.getExecutionProperties().put(RunConfiguration.SMART_WAIT_MODE, false);
                SmartWaitWebDriver smartWaitWebDriver = (SmartWaitWebDriver) DriverFactory.getWebDriver();
                smartWaitWebDriver.unregister();
                DriverFactory.changeWebDriverWithoutLog(smartWaitWebDriver.getWrappedDriver());
            }
        }, FailureHandling.CONTINUE_ON_FAILURE, true, "Unable to disable smart wait !");
    }
}
