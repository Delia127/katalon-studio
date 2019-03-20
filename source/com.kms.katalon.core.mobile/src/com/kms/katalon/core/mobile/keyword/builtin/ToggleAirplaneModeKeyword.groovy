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
import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.mobile.keyword.internal.MobileKeywordMain
import com.kms.katalon.core.model.FailureHandling

import groovy.transform.CompileStatic
import io.appium.java_client.AppiumDriver
import io.appium.java_client.TouchAction
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.connection.ConnectionState
import io.appium.java_client.touch.offset.PointOption

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
                    String deviceModel = MobileDriverFactory.getDeviceModel()
                    //ResourceBundle resourceBundle = ResourceBundle.getBundle("resource")
                    //String[] point = resourceBundle.getString(deviceModel).split(";")
                    if(MobileCommonHelper.deviceModels.get(deviceModel) == null){
                        throw new StepFailedException("Device info not found. Please use ideviceinfo -u <udid> to read ProductType of iOS devices")
                    }
                    if(MobileCommonHelper.airPlaneButtonCoords.get(MobileCommonHelper.deviceModels.get(deviceModel)) == null || MobileCommonHelper.airPlaneButtonCoords.get(MobileCommonHelper.deviceModels.get(deviceModel)).equals("")) {
                        throw new StepFailedException("AirplaneMode button coordinator not found.")
                    }

                    String[] point = MobileCommonHelper.airPlaneButtonCoords.get(MobileCommonHelper.deviceModels.get(deviceModel)).split(";")
                    int x = Integer.parseInt(point[0])
                    int y = Integer.parseInt(point[1])
                    Dimension size = driver.manage().window().getSize()
                    MobileCommonHelper.swipe(driver, 50, size.height, 50, size.height - 300)
                    Thread.sleep(500)
                    TouchAction tap = new TouchAction(driver).tap(PointOption.point(x, y)).release()
                    tap.perform()
                    MobileCommonHelper.swipe(driver, 50, 1, 50, size.height)
                }
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TOGGLE_AIRPLANE_MODE, mode))
            } finally {
                driver.context(context)
            }

        }, flowControl, true, StringConstants.KW_MSG_CANNOT_TOGGLE_AIRPLANE_MODE)
    }
}
