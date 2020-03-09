package com.kms.katalon.execution.webui.configuration.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.logging.LogUtil;

public class WebUIExecutionSetting extends DefaultExecutionSetting {
    @Override
    public Map<String, Object> getGeneralProperties() {
        Map<String, Object> generalProperties = super.getGeneralProperties();
        generalProperties.putAll(getWebUiExecutionProperties());
        return generalProperties;
    }
    
    public WebUiExecutionSettingStore getWebUiStore() {
        return new WebUiExecutionSettingStore(getCurrentProject());
    }

    private Map<String, Object> getWebUiExecutionProperties() {
        Map<String, Object> reportProps = new HashMap<String, Object>();
        WebUiExecutionSettingStore webUiSettingStore = getWebUiStore();
        try {
            reportProps.put(DriverFactory.ENABLE_PAGE_LOAD_TIMEOUT, webUiSettingStore.getEnablePageLoadTimeout());
            reportProps.put(DriverFactory.DEFAULT_PAGE_LOAD_TIMEOUT, webUiSettingStore.getPageLoadTimeout());
            int val = webUiSettingStore.getActionDelay();
            reportProps.put(DriverFactory.ACTION_DELAY, webUiSettingStore.getActionDelay());
            reportProps.put(DriverFactory.USE_ACTION_DELAY_IN_SECOND, webUiSettingStore.getUseDelayActionInSecond());
            reportProps.put(DriverFactory.IGNORE_PAGE_LOAD_TIMEOUT_EXCEPTION,
                    webUiSettingStore.getIgnorePageLoadTimeout());
            reportProps.put(RunConfiguration.IMAGE_RECOGNITION_ENABLED, webUiSettingStore.getImageRecognitionEnabled());
        } catch (IOException e) {
            LogUtil.logError(e);
        }

        return reportProps;
    }
}
