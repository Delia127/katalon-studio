package com.kms.katalon.execution.webui.setting;

import java.io.IOException;

import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.webui.constants.WebUiExecutionSettingConstants;

public class WebUiExecutionSettingStore extends BundleSettingStore {
    public static final int EXECUTION_DEFAULT_ACTION_DELAY = 0;
    public static final boolean EXECUTION_DEFAULT_IGNORE_PAGELOAD_TIMEOUT_EXCEPTION = false;
    public static final int EXECUTION_DEFAULT_PAGE_LOAD_TIMEOUT = 30;
    public static final boolean EXECUTION_DEFAULT_ENABLE_PAGE_LOAD_TIMEOUT = false;

    public WebUiExecutionSettingStore(ProjectEntity projectEntity) {
        super(projectEntity.getFolderLocation(),
                FrameworkUtil.getBundle(WebUiExecutionSettingStore.class).getSymbolicName(), false);
    }

    public boolean getEnablePageLoadTimeout() throws IOException {
        return getBoolean(WebUiExecutionSettingConstants.WEBUI_EXECUTION_ENABLE_PAGE_LOAD_TIMEOUT, EXECUTION_DEFAULT_ENABLE_PAGE_LOAD_TIMEOUT);
    }

    public void setEnablePageLoadTimeout(boolean pageLoadTimeoutEnabled) throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_EXECUTION_ENABLE_PAGE_LOAD_TIMEOUT, pageLoadTimeoutEnabled);
    }
    
    public boolean getIgnorePageLoadTimeout() throws IOException {
        return getBoolean(WebUiExecutionSettingConstants.WEBUI_EXECUTION_IGNORE_PAGE_LOAD_TIMEOUT_EXCEPTION, EXECUTION_DEFAULT_IGNORE_PAGELOAD_TIMEOUT_EXCEPTION);
    }

    public void setIgnorePageLoadTimeout(boolean pageLoadTimeoutIgnored) throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_EXECUTION_IGNORE_PAGE_LOAD_TIMEOUT_EXCEPTION, pageLoadTimeoutIgnored);
    }
    
    public int getPageLoadTimeout() throws IOException {
        return getInt(WebUiExecutionSettingConstants.WEBUI_EXECUTION_DEFAULT_PAGE_LOAD_TIMEOUT, EXECUTION_DEFAULT_PAGE_LOAD_TIMEOUT);
    }

    public void setPageLoadTimeout(int pageLoadTimeout) throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_EXECUTION_DEFAULT_PAGE_LOAD_TIMEOUT, pageLoadTimeout);
    }
    
    public int getActionDelay() throws IOException {
        return getInt(WebUiExecutionSettingConstants.WEBUI_EXECUTION_ACTION_DELAY, EXECUTION_DEFAULT_ACTION_DELAY);
    }

    public void setActionDelay(int actionDelay) throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_EXECUTION_ACTION_DELAY, actionDelay);
    }
}
