package com.kms.katalon.execution.entity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;

public abstract class AbstractDriverConnector implements IDriverConnector {
    protected File propertyConfigFile;
    protected Map<String, Object> driverProperties;

    public AbstractDriverConnector(String projectDir) throws IOException {
        propertyConfigFile = new File(projectDir + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME + File.separator + getSettingFileName()
                + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION);
        if (!propertyConfigFile.exists()) {
            propertyConfigFile.createNewFile();
        }
        loadDriverProperties();
    }

    public AbstractDriverConnector(String projectDir, String customProfileName) throws IOException {
        propertyConfigFile = new File(projectDir + File.separator
                + PropertySettingStoreUtil.EXTERNAL_SETTING_ROOT_FOLDLER_NAME + File.separator + customProfileName
                + File.separator + getSettingFileName() + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION);
        if (!propertyConfigFile.exists()) {
            propertyConfigFile.createNewFile();
        }
        loadDriverProperties();
    }

    protected abstract String getSettingFileName();

    @Override
    public Map<String, Object> getExecutionSettingPropertyMap() {
        Map<String, Object> executionSettingPropertyMap = new HashMap<String, Object>();
        Map<String, Object> driverProperties = getDriverProperties();
        executionSettingPropertyMap.put(getDriverType().getPropertyKey(), getDriverType().getPropertyValue());
        executionSettingPropertyMap.put(com.kms.katalon.core.constants.StringConstants.CONF_PROPERTY_EXECUTION_DRIVER_PROPERTY, driverProperties);
        return executionSettingPropertyMap;
    }

    protected void loadDriverProperties() {
        Map<String, Map<String, Object>> allProperties = getAllDriverProperties();
        if (allProperties.get(getDriverType().getName()) == null) {
            driverProperties = new LinkedHashMap<String, Object>();
            return;
        }
        driverProperties = allProperties.get(getDriverType().getName());
    }

    public Map<String, Object> getDriverProperties() {
        return driverProperties;
    }

    public void saveDriverProperties() throws IOException {
        Map<String, Map<String, Object>> driverPropertiesMap = getAllDriverProperties();
        driverPropertiesMap.put(getDriverType().getName(), driverProperties);
        Gson gsonObj = new Gson();
        String strJson = gsonObj.toJson(driverPropertiesMap);
        FileUtils.writeStringToFile(propertyConfigFile, strJson);
    }

    protected Map<String, Map<String, Object>> getAllDriverProperties() {
        Gson gsonObj = new Gson();
        try {
            String propertyConfigFileContent = FileUtils.readFileToString(propertyConfigFile);
            Type collectionType = new TypeToken<Map<String, Map<String, Object>>>() {}.getType();
            Map<String, Map<String, Object>> result = gsonObj.fromJson(propertyConfigFileContent, collectionType);
            return (result == null) ? new LinkedHashMap<String, Map<String, Object>>() : result;
        } catch (IOException | JsonSyntaxException exception) {
            // reading file failed or parsing json failed --> return empty map;
            return new HashMap<String, Map<String, Object>>();
        }
    }

    public Object getDriverPropertyValue(String rawKey) {
        for (Entry<String, Object> driverProperty : getDriverProperties().entrySet()) {
            if (driverProperty.getKey().equals(rawKey)) {
                return driverProperty.getValue();
            }
        }
        return null;
    }
}
