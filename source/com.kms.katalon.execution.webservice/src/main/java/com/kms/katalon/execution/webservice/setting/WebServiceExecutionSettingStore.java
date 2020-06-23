package com.kms.katalon.execution.webservice.setting;

import java.io.IOException;

import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.webservice.constants.WebServiceExecutionSettingConstants;

public class WebServiceExecutionSettingStore extends BundleSettingStore {

    public static final int EXECUTION_DEFAULT_CONNECTION_TIMEOUT_MS = RequestObject.DEFAULT_TIMEOUT;

    public static final int EXECUTION_DEFAULT_SOCKET_TIMEOUT_MS = RequestObject.DEFAULT_TIMEOUT;

    public static WebServiceExecutionSettingStore getStore() {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        if (projectEntity == null) {
            return null;
        }
        return new WebServiceExecutionSettingStore(projectEntity);
    }

    public WebServiceExecutionSettingStore(ProjectEntity projectEntity) {
        super(projectEntity.getFolderLocation(),
                FrameworkUtil.getBundle(WebServiceExecutionSettingStore.class).getSymbolicName(), false);
    }

    public int getConnectionTimeout() throws IOException {
        return getInt(WebServiceExecutionSettingConstants.WEBSERVICE_EXECUTION_CONNECTION_TIMEOUT,
                EXECUTION_DEFAULT_CONNECTION_TIMEOUT_MS);
    }

    public void setConnectionTimeout(int connectionTimeout) throws IOException {
        setProperty(WebServiceExecutionSettingConstants.WEBSERVICE_EXECUTION_CONNECTION_TIMEOUT, connectionTimeout);
    }

    public int getSocketTimeout() throws IOException {
        return getInt(WebServiceExecutionSettingConstants.WEBSERVICE_EXECUTION_SOCKET_TIMEOUT,
                EXECUTION_DEFAULT_SOCKET_TIMEOUT_MS);
    }

    public void setSocketTimeout(int socketTimeout) throws IOException {
        setProperty(WebServiceExecutionSettingConstants.WEBSERVICE_EXECUTION_SOCKET_TIMEOUT, socketTimeout);
    }
}
