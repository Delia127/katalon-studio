package com.kms.katalon.core.webservice.helper.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.webservice.helper.WebServiceCommonHelper;

public class WebServiceCommonHelperTest {

    private static final String LOCAL_EXECUTION_SETTING_STORAGE_FIELD = "localExecutionSettingMapStorage";

    @Test
    public void configRequestTimeoutFreeLicenseTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        // Given
        int connectionTimeout = 1234;
        int socketTimeout = 4321;
        RequestObject request = new RequestObject(RandomStringUtils.random(8));

        Map<String, Object> executionSettingMap = new HashMap<>();
        Map<String, Object> executionProperty = new HashMap<>();
        executionSettingMap.put(RunConfiguration.EXECUTION_PROPERTY, executionProperty);

        Map<String, Object> generalSettings = new HashMap<>();
        generalSettings.put(RunConfiguration.REQUEST_CONNECTION_TIMEOUT, connectionTimeout);
        generalSettings.put(RunConfiguration.REQUEST_SOCKET_TIMEOUT, socketTimeout);

        executionProperty.put(RunConfiguration.EXECUTION_GENERAL_PROPERTY, generalSettings);

        RunConfiguration.setExecutionSetting(executionSettingMap);

        Class<?> runConfigurationClass = RunConfiguration.class;
        Field localExecutionSettingMapStorageField = runConfigurationClass
                .getDeclaredField(LOCAL_EXECUTION_SETTING_STORAGE_FIELD);
        localExecutionSettingMapStorageField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ThreadLocal<Map<String, Object>> localExecutionSettingMapStorage = (ThreadLocal<Map<String, Object>>) localExecutionSettingMapStorageField
                .get(RunConfiguration.class);
        localExecutionSettingMapStorage.get().put(RunConfiguration.ALLOW_CUSTOMIZE_REQUEST_TIMEOUT, false);

        // When
        WebServiceCommonHelper.configRequestTimeout(request);

        // Then
        Assert.assertEquals(RequestObject.TIMEOUT_UNSET, request.getConnectionTimeout());
        Assert.assertEquals(RequestObject.TIMEOUT_UNSET, request.getSocketTimeout());
    }

    @Test
    public void configRequestTimeoutEnterpriseLicenseTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        // Given
        int connectionTimeout = 1234;
        int socketTimeout = 4321;
        RequestObject request = new RequestObject(RandomStringUtils.random(8));

        Map<String, Object> executionSettingMap = new HashMap<>();
        Map<String, Object> executionProperty = new HashMap<>();
        executionSettingMap.put(RunConfiguration.EXECUTION_PROPERTY, executionProperty);

        Map<String, Object> generalSettings = new HashMap<>();
        generalSettings.put(RunConfiguration.REQUEST_CONNECTION_TIMEOUT, connectionTimeout);
        generalSettings.put(RunConfiguration.REQUEST_SOCKET_TIMEOUT, socketTimeout);

        executionProperty.put(RunConfiguration.EXECUTION_GENERAL_PROPERTY, generalSettings);

        RunConfiguration.setExecutionSetting(executionSettingMap);

        Class<?> runConfigurationClass = RunConfiguration.class;
        Field localExecutionSettingMapStorageField = runConfigurationClass
                .getDeclaredField(LOCAL_EXECUTION_SETTING_STORAGE_FIELD);
        localExecutionSettingMapStorageField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ThreadLocal<Map<String, Object>> localExecutionSettingMapStorage = (ThreadLocal<Map<String, Object>>) localExecutionSettingMapStorageField
                .get(RunConfiguration.class);
        localExecutionSettingMapStorage.get().put(RunConfiguration.ALLOW_CUSTOMIZE_REQUEST_TIMEOUT, true);

        // When
        WebServiceCommonHelper.configRequestTimeout(request);

        // Then
        Assert.assertEquals(connectionTimeout, request.getConnectionTimeout());
        Assert.assertEquals(socketTimeout, request.getSocketTimeout());
    }

    @Test
    public void configRequestTimeoutEnterpriseLicenseCustomTimeoutTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        // Given
        int customConnectionTimeout = 1111;
        int customSocketTimeout = 4444;
        RequestObject request = new RequestObject(RandomStringUtils.random(8));
        request.setConnectionTimeout(customConnectionTimeout);
        request.setSocketTimeout(customSocketTimeout);

        Map<String, Object> executionSettingMap = new HashMap<>();
        Map<String, Object> executionProperty = new HashMap<>();
        executionSettingMap.put(RunConfiguration.EXECUTION_PROPERTY, executionProperty);

        Map<String, Object> generalSettings = new HashMap<>();
        int connectionTimeout = 1234;
        int socketTimeout = 4321;
        generalSettings.put(RunConfiguration.REQUEST_CONNECTION_TIMEOUT, connectionTimeout);
        generalSettings.put(RunConfiguration.REQUEST_SOCKET_TIMEOUT, socketTimeout);

        executionProperty.put(RunConfiguration.EXECUTION_GENERAL_PROPERTY, generalSettings);

        RunConfiguration.setExecutionSetting(executionSettingMap);

        Class<?> runConfigurationClass = RunConfiguration.class;
        Field localExecutionSettingMapStorageField = runConfigurationClass
                .getDeclaredField(LOCAL_EXECUTION_SETTING_STORAGE_FIELD);
        localExecutionSettingMapStorageField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ThreadLocal<Map<String, Object>> localExecutionSettingMapStorage = (ThreadLocal<Map<String, Object>>) localExecutionSettingMapStorageField
                .get(RunConfiguration.class);
        localExecutionSettingMapStorage.get().put(RunConfiguration.ALLOW_CUSTOMIZE_REQUEST_TIMEOUT, true);

        // When
        WebServiceCommonHelper.configRequestTimeout(request);

        // Then
        Assert.assertEquals(customConnectionTimeout, request.getConnectionTimeout());
        Assert.assertEquals(customSocketTimeout, request.getSocketTimeout());
    }
}
