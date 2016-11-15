package com.kms.katalon.core.webui.driver.existings;

import com.kms.katalon.core.webui.driver.safari.CSafariDriverCommandExecutor;
import com.kms.katalon.core.webui.driver.safari.CSafariOptions;

public class ExistingSafariDriver extends ExistingRemoteWebDriver {
    public ExistingSafariDriver(String oldSessionId, CSafariOptions safariOptions) {
        super(oldSessionId, new CSafariDriverCommandExecutor(safariOptions), safariOptions.toCapabilities());
        setSessionId("");
    }
}
