package com.kms.katalon.core.mobile.keyword.builtin

import java.text.MessageFormat

import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.transform.tailrec.VariableReplacedListener.*
import org.openqa.selenium.Dimension

import com.kms.katalon.core.annotation.internal.Action
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.internal.SupportLevel
import com.kms.katalon.core.mobile.constants.StringConstants
import com.kms.katalon.core.mobile.helper.MobileCommonHelper
import com.kms.katalon.core.mobile.keyword.*
import com.kms.katalon.core.mobile.keyword.internal.MobileAbstractKeyword
import com.kms.katalon.core.mobile.keyword.internal.MobileKeywordMain
import com.kms.katalon.core.model.FailureHandling

import groovy.transform.CompileStatic
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.TouchAction
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.connection.ConnectionState
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.touch.TapOptions
import io.appium.java_client.touch.offset.ElementOption

@Action(value = "toggleAirplaneMode")
public class ToggleAirplaneModeKeyword extends MobileAbstractKeyword {

    @CompileStatic
    @Override
    public SupportLevel getSupportLevel(Object ...params) {
        return super.getSupportLevel(params)
    }

    @CompileStatic
    @Override
    public Object execute(Object ...params) {
        String mode = (String) params[0]
        FailureHandling flowControl = (FailureHandling)(params.length > 1 && params[1] instanceof FailureHandling ? params[1] : RunConfiguration.getDefaultFailureHandling())
        toggleAirplaneMode(mode,flowControl)
    }

    @CompileStatic
    public void toggleAirplaneMode(String mode, FailureHandling flowControl) throws StepFailedException {
        MobileKeywordMain.runKeyword({
            AppiumDriver<?> driver = getAnyAppiumDriver()
            String context = driver.getContext()
            try {
                internalSwitchToNativeContext(driver)

                boolean isTurnOn = false
                if (StringUtils.equalsIgnoreCase("yes", mode) || StringUtils.equalsIgnoreCase("on", mode) || StringUtils.equalsIgnoreCase("true", mode)) {
                    isTurnOn = true
                }
                if (driver instanceof AndroidDriver) {
                    AndroidDriver androidDriver = (AndroidDriver) driver
                    androidDriver.setConnection(isTurnOn ? new ConnectionState(ConnectionState.AIRPLANE_MODE_MASK)
                            : new ConnectionState(ConnectionState.WIFI_MASK))
                } else {
                    IOSDriver iOSDriver = (IOSDriver) driver
                    Dimension size = driver.manage().window().getSize()

                    MobileCommonHelper.swipe(driver,
                            (size.getWidth() / 2) as int,
                            size.getHeight(),
                            (size.getWidth() / 2) as int,
                            (size.getHeight() / 2) as int)

                    MobileElement airplaneButton = iOSDriver.findElementsByXPath("//XCUIElementTypeSwitch[@visible='true' and @label='Airplane Mode']").get(0);
                    boolean isEnabled = airplaneButton.getAttribute("value") == "1" ? true : false
                    if (isTurnOn != isEnabled) {
                        TouchAction tapAtAirPlaneButton = new TouchAction(driver)
                                .tap(TapOptions.tapOptions().withElement(ElementOption.element(airplaneButton, 1, 1)))
                        tapAtAirPlaneButton.release().perform()

                        logger.logInfo("Airplane Mode switched from " + getSwitchStatus(isEnabled) + " to " + getSwitchStatus(isTurnOn))
                    } else {
                        logger.logInfo("Airplane Mode already switched to " + getSwitchStatus(isEnabled))
                    }
                    MobileElement backButton = iOSDriver.findElementsByXPath("//XCUIElementTypeOther[@visible='true' and @name='ControlCenterView']/XCUIElementTypeOther[@visible='true']").get(0);
                    TouchAction tapAtBackButton = new TouchAction(driver)
                            .tap(TapOptions.tapOptions().withElement(ElementOption.element(backButton, backButton.center)))
                    tapAtBackButton.release().perform()
                }
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TOGGLE_AIRPLANE_MODE, mode))
            } finally {
                driver.context(context)
            }
        }, flowControl, true, StringConstants.KW_MSG_CANNOT_TOGGLE_AIRPLANE_MODE)
    }

    @CompileStatic
    String getSwitchStatus(boolean status) {
        return status ? "ON" : "OFF"
    }
}
