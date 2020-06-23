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

    @Test
    public void configRequestMaxResponseSizeFreeLicenseTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        // Given
        long maxResponseSize = 1234;
        RequestObject request = new RequestObject(RandomStringUtils.random(8));
        request.setMaxResponseSize(maxResponseSize);

        Map<String, Object> executionSettingMap = new HashMap<>();
        Map<String, Object> executionProperty = new HashMap<>();
        executionSettingMap.put(RunConfiguration.EXECUTION_PROPERTY, executionProperty);

        Map<String, Object> generalSettings = new HashMap<>();
        generalSettings.put(RunConfiguration.REQUEST_MAX_RESPONSE_SIZE, maxResponseSize);

        executionProperty.put(RunConfiguration.EXECUTION_GENERAL_PROPERTY, generalSettings);

        RunConfiguration.setExecutionSetting(executionSettingMap);

        Class<?> runConfigurationClass = RunConfiguration.class;
        Field localExecutionSettingMapStorageField = runConfigurationClass
                .getDeclaredField(LOCAL_EXECUTION_SETTING_STORAGE_FIELD);
        localExecutionSettingMapStorageField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ThreadLocal<Map<String, Object>> localExecutionSettingMapStorage = (ThreadLocal<Map<String, Object>>) localExecutionSettingMapStorageField
                .get(RunConfiguration.class);
        localExecutionSettingMapStorage.get().put(RunConfiguration.ALLOW_CUSTOMIZE_REQUEST_RESPONSE_SIZE_LIMIT, false);

        // When
        WebServiceCommonHelper.configRequestResponseSizeLimit(request);

        // Then
        Assert.assertEquals(RequestObject.MAX_RESPONSE_SIZE_UNSET, request.getMaxResponseSize());
    }

    @Test
    public void configRequestMaxResponseSizeEnterpriseLicenseTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        // Given
        long maxResponseSize = 1234;
        RequestObject request = new RequestObject(RandomStringUtils.random(8));

        Map<String, Object> executionSettingMap = new HashMap<>();
        Map<String, Object> executionProperty = new HashMap<>();
        executionSettingMap.put(RunConfiguration.EXECUTION_PROPERTY, executionProperty);

        Map<String, Object> generalSettings = new HashMap<>();
        generalSettings.put(RunConfiguration.REQUEST_MAX_RESPONSE_SIZE, maxResponseSize);

        executionProperty.put(RunConfiguration.EXECUTION_GENERAL_PROPERTY, generalSettings);

        RunConfiguration.setExecutionSetting(executionSettingMap);

        Class<?> runConfigurationClass = RunConfiguration.class;
        Field localExecutionSettingMapStorageField = runConfigurationClass
                .getDeclaredField(LOCAL_EXECUTION_SETTING_STORAGE_FIELD);
        localExecutionSettingMapStorageField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ThreadLocal<Map<String, Object>> localExecutionSettingMapStorage = (ThreadLocal<Map<String, Object>>) localExecutionSettingMapStorageField
                .get(RunConfiguration.class);
        localExecutionSettingMapStorage.get().put(RunConfiguration.ALLOW_CUSTOMIZE_REQUEST_RESPONSE_SIZE_LIMIT, true);

        // When
        WebServiceCommonHelper.configRequestResponseSizeLimit(request);

        // Then
        Assert.assertEquals(maxResponseSize, request.getMaxResponseSize());
    }

    @Test
    public void configRequestMaxResponseSizeEnterpriseLicenseCustomMaxResponseSizeTest() throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        // Given
        int customMaxResponseSize = 1111;
        RequestObject request = new RequestObject(RandomStringUtils.random(8));
        request.setMaxResponseSize(customMaxResponseSize);

        Map<String, Object> executionSettingMap = new HashMap<>();
        Map<String, Object> executionProperty = new HashMap<>();
        executionSettingMap.put(RunConfiguration.EXECUTION_PROPERTY, executionProperty);

        Map<String, Object> generalSettings = new HashMap<>();
        int globalMaxResponseSize = 1234;
        generalSettings.put(RunConfiguration.REQUEST_MAX_RESPONSE_SIZE, globalMaxResponseSize);

        executionProperty.put(RunConfiguration.EXECUTION_GENERAL_PROPERTY, generalSettings);

        RunConfiguration.setExecutionSetting(executionSettingMap);

        Class<?> runConfigurationClass = RunConfiguration.class;
        Field localExecutionSettingMapStorageField = runConfigurationClass
                .getDeclaredField(LOCAL_EXECUTION_SETTING_STORAGE_FIELD);
        localExecutionSettingMapStorageField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ThreadLocal<Map<String, Object>> localExecutionSettingMapStorage = (ThreadLocal<Map<String, Object>>) localExecutionSettingMapStorageField
                .get(RunConfiguration.class);
        localExecutionSettingMapStorage.get().put(RunConfiguration.ALLOW_CUSTOMIZE_REQUEST_RESPONSE_SIZE_LIMIT, true);

        // When
        WebServiceCommonHelper.configRequestResponseSizeLimit(request);

        // Then
        Assert.assertEquals(customMaxResponseSize, request.getMaxResponseSize());
    }
}
