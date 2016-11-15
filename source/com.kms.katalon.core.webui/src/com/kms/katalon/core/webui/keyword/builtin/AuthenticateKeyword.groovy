package com.kms.katalon.core.webui.keyword.builtin

import groovy.transform.CompileStatic

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

import com.google.common.base.Function
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.BuiltinKeywords
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.util.ExceptionsUtil
import com.kms.katalon.core.util.PathUtil
import com.kms.katalon.core.webui.common.ScreenUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.WebUIDriverType
import com.kms.katalon.core.webui.exception.BrowserNotOpenedException
import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import com.kms.katalon.core.webui.util.FileUtil
import com.kms.katalon.core.webui.keyword.WebUIKeywordMain
import com.kms.katalon.core.annotation.Action
import com.kms.katalon.core.webui.keyword.WebUIAbstractKeyword
import com.kms.katalon.core.keyword.SupportLevel
import com.kms.katalon.core.keyword.KeywordExecutor

@Action(value = "authenticate")
public class AuthenticateKeyword extends WebUIAbstractKeyword {

    @CompileStatic
    @Override
    public SupportLevel getSupportLevel(Object ...params) {
        return super.getSupportLevel(params)
    }

    @CompileStatic
    @Override
    public Object execute(Object ...params) {
        String url = (String) params[0]
        String userName = (String) params[1]
        String password = (String) params[2]
        int timeout = (int) params[3]
        FailureHandling flowControl = (FailureHandling)(params.length > 4 && params[4] instanceof FailureHandling ? params[4] : RunConfiguration.getDefaultFailureHandling())
        authenticate(url,userName,password,timeout,flowControl)
    }

    @CompileStatic
    public void authenticate(final String url, String userName, String password, int timeout,
            FailureHandling flowControl) {

        WebUIKeywordMain.runKeyword({

            Thread navigateThread = null

            try{

                if (System.getProperty("os.name") == null || !System.getProperty("os.name").toLowerCase().contains("win")) {
                    throw new Exception("Unsupported platform (only support Windows)")
                }

                if(DriverFactory.getExecutedBrowser() != WebUIDriverType.IE_DRIVER &&
                DriverFactory.getExecutedBrowser() != WebUIDriverType.FIREFOX_DRIVER &&
                DriverFactory.getExecutedBrowser() != WebUIDriverType.CHROME_DRIVER){
                    throw new Exception("Unsupported browser (only support IE, FF, Chrome)")
                }

                timeout = WebUiCommonHelper.checkTimeout(timeout)

                KeywordLogger.getInstance().logInfo(StringConstants.KW_LOG_INFO_CHECKING_USERNAME)
                if (userName == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_USERNAME_IS_NULL)
                }
                KeywordLogger.getInstance().logInfo(StringConstants.KW_LOG_INFO_CHECKING_PASSWORD)
                if (password == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_PASSWORD_IS_NULL)
                }

                WebDriver driver = DriverFactory.getWebDriver()

                if(url != null && !url.equals("")){
                    navigateThread = new Thread() {
                                public void run() {
                                    driver.get(url)
                                }
                            }
                    navigateThread.start()
                    //Wait for secured page is fully loaded
                    Thread.sleep(timeout * 1000)
                }

                // send username and pasword to authentication popup
                //screenUtil.authenticate(userName, password)
                File kmsIeFolder = FileUtil.getKmsIeDriverDirectory()
                File authFolder = FileUtil.getAuthenticationDirectory()
                File userNameParamFile = new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "set user name" + File.separator + "paramter0")
                File passwordParamFile = new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "set password" + File.separator + "paramter0")

                //Set user name
                FileUtils.writeStringToFile(userNameParamFile, userName, false)
                String[] cmd = [kmsIeFolder.getAbsolutePath() + "/kmsie.exe", userNameParamFile.getParent()]
                Process proc = Runtime.getRuntime().exec(cmd)
                //The default timeout for this task is 10s (implemented inside KMS IE Driver)
                proc.waitFor()
                //Check result
                String resStatus = FileUtils.readFileToString(
                        new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "set user name" + File.separator + "result_status"),
                        "UTF-8")
                if(!"PASSED".equals(resStatus.trim())){
                    //Should consider to read result_message
                    String errMsg = FileUtils.readFileToString(
                            new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "set user name" + File.separator + "result_message"),
                            "UTF-8")
                    throw new Exception("Failed to set user name on Authentication dialog: " + errMsg)
                }

                //Set password
                FileUtils.writeStringToFile(passwordParamFile, password, false)
                cmd = [kmsIeFolder.getAbsolutePath() + "/kmsie.exe", passwordParamFile.getParent()]
                proc = Runtime.getRuntime().exec(cmd)
                proc.waitFor()
                resStatus = FileUtils.readFileToString(
                        new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "set password" + File.separator + "result_status"),
                        "UTF-8")
                if(!"PASSED".equals(resStatus.trim())){
                    String errMsg = FileUtils.readFileToString(
                            new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "set password" + File.separator + "result_message"),
                            "UTF-8")
                    throw new Exception("Failed to set password on Authentication dialog: " + errMsg)
                }

                //Click OK
                cmd = [kmsIeFolder.getAbsolutePath() + "/kmsie.exe", new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "click ok").getAbsolutePath()]
                proc = Runtime.getRuntime().exec(cmd)
                proc.waitFor()
                resStatus = FileUtils.readFileToString(
                        new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "click ok" + File.separator + "result_status"),
                        "UTF-8")
                if(!"PASSED".equals(resStatus.trim())){
                    String errMsg = FileUtils.readFileToString(
                            new File(authFolder, DriverFactory.getExecutedBrowser().toString() + File.separator + "click ok" + File.separator + "result_message"),
                            "UTF-8")
                    throw new Exception("Failed to click OK button on Authentication dialog: " + errMsg)
                }

                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_NAVIAGTED_TO_AUTHENTICATED_PAGE, [userName, password] as Object[]))
            }
            finally{
                if (navigateThread != null && navigateThread.isAlive()) {
                    navigateThread.interrupt()
                }
            }
        }, flowControl, true, StringConstants.KW_MSG_CANNOT_NAV_TO_AUTHENTICATED_PAGE)
    }
}
