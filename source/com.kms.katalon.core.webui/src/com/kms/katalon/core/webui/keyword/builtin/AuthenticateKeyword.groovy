package com.kms.katalon.core.webui.keyword.builtin

import java.text.MessageFormat

import org.apache.commons.lang.StringUtils
import org.openqa.selenium.WebDriver

import com.kms.katalon.core.annotation.internal.Action
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.keyword.internal.SupportLevel
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.util.internal.PathUtil
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.WebUIDriverType
import com.kms.katalon.core.webui.keyword.internal.WebUIAbstractKeyword
import com.kms.katalon.core.webui.keyword.internal.WebUIKeywordMain
import com.kms.katalon.core.webui.util.WinRegistry

import groovy.transform.CompileStatic

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

            Thread navigatedThread = null

            try{

                if (System.getProperty("os.name") == null || !System.getProperty("os.name").toLowerCase().contains("win")) {
                    throw new Exception("Unsupported platform (only support Windows)")
                }

                if(DriverFactory.getExecutedBrowser() != WebUIDriverType.IE_DRIVER &&
                DriverFactory.getExecutedBrowser() != WebUIDriverType.FIREFOX_DRIVER &&
                DriverFactory.getExecutedBrowser() != WebUIDriverType.CHROME_DRIVER){
                    throw new Exception("Unsupported browser (only support IE, FF, Chrome)")
                }
                
                //Pre-check username and password
                KeywordLogger.getInstance().logInfo(StringConstants.KW_LOG_INFO_CHECKING_USERNAME)
                if (userName == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_USERNAME_IS_NULL)
                }
                KeywordLogger.getInstance().logInfo(StringConstants.KW_LOG_INFO_CHECKING_PASSWORD)
                if (password == null) {
                    throw new IllegalArgumentException(StringConstants.KW_EXC_PASSWORD_IS_NULL)
                }
                
                //For only Internet Explorer: to permit type username and password on URL.
                if (DriverFactory.getExecutedBrowser() == WebUIDriverType.IE_DRIVER) {
                    WinRegistry.enableUsernamePasswordOnURL();
                }
                
                //Try to navigate to site destination
                WebDriver driver = DriverFactory.getWebDriver()
                String usernamePasswordURL = getAuthenticatedUrl(PathUtil.getUrl(url, "https"), userName, password)
                String currentUrl = "";
                
                if (!StringUtils.isEmpty(url)) {
                    navigatedThread = new Thread() {
                        public void run() {
                             driver.navigate().to(usernamePasswordURL)
                             currentUrl = driver.getCurrentUrl()
                        }
                    }    
                    navigatedThread.start()
                    Thread.sleep(timeout * 1000)
                }
                
                if (usernamePasswordURL.equals(currentUrl)) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_NAVIAGTED_TO_AUTHENTICATED_PAGE, [userName, password] as Object[]))
                } else {
                    WebUIKeywordMain.stepFailed(StringConstants.KW_MSG_CANNOT_NAV_TO_AUTHENTICATED_PAGE, flowControl, null, false)
                }
        } finally {
             if (navigatedThread != null && navigatedThread.isAlive()) {
                 navigatedThread.interrupt()
             }
         }       
                         
        }, flowControl, false, StringConstants.KW_MSG_CANNOT_NAV_TO_AUTHENTICATED_PAGE)
    }
        
    @CompileStatic
    private String getAuthenticatedUrl(URL url, String userName, String password) {
         StringBuilder getAuthenticatedUrl = new StringBuilder()
         
         getAuthenticatedUrl.append(url.getProtocol())
         getAuthenticatedUrl.append("://")
         getAuthenticatedUrl.append(userName)
         getAuthenticatedUrl.append(":")
         getAuthenticatedUrl.append(password)
         getAuthenticatedUrl.append("@")
         getAuthenticatedUrl.append(url.getHost())
         getAuthenticatedUrl.append(url.getPath())
         
         return getAuthenticatedUrl
     }
    
}
