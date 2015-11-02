package com.kms.katalon.execution.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
    protected String parentFolderPath;

    public AbstractDriverConnector(String configurationFolderPath) throws IOException {
        parentFolderPath = configurationFolderPath;
        propertyConfigFile = new File(configurationFolderPath + File.separator + getSettingFileName()
                + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION);
        // propertyConfigFile = new File(projectDir + File.separator
        // + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME + File.separator + getSettingFileName()
        // + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION);
        // if (!propertyConfigFile.exists()) {
        // propertyConfigFile.createNewFile();
        // }
        loadDriverProperties();
    }

    // public AbstractDriverConnector(String projectDir, CustomRunConfiguration customRunConfiguration) throws
    // IOException {
    // propertyConfigFile = new File(customRunConfiguration.getAbsoluteFolderPath() + File.separator
    // + getSettingFileName() + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION);
    // if (!propertyConfigFile.exists()) {
    // propertyConfigFile.createNewFile();
    // }
    // loadDriverProperties();
    // }
    
    @Override
    public void setParentFolderPath(String parentFolderPath) {
        this.parentFolderPath = parentFolderPath;
        propertyConfigFile = new File(parentFolderPath + File.separator + getSettingFileName()
                + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION);
    }
    
    @Override
    public String getParentFolderPath() {
        return parentFolderPath;
    }

    @Override
    public Map<String, Object> getExecutionSettingPropertyMap() {
        Map<String, Object> executionSettingPropertyMap = new HashMap<String, Object>();
        Map<String, Object> driverProperties = getDriverProperties();
        executionSettingPropertyMap.put(getDriverType().getPropertyKey(), getDriverType().getPropertyValue());
        executionSettingPropertyMap.put(
                com.kms.katalon.core.constants.StringConstants.CONF_PROPERTY_EXECUTION_DRIVER_PROPERTY,
                driverProperties);
        return executionSettingPropertyMap;
    }

    protected void loadDriverProperties() throws IOException {
        if (propertyConfigFile.exists()) {
            Map<String, Map<String, Object>> allProperties = getAllDriverProperties();
            if (allProperties.get(getDriverType().getName()) == null) {
                driverProperties = new LinkedHashMap<String, Object>();
                return;
            }
            driverProperties = allProperties.get(getDriverType().getName());
        } else {
            driverProperties = new LinkedHashMap<String, Object>();
        }
    }

    public Map<String, Object> getDriverProperties() {
        return driverProperties;
    }

    public void saveDriverProperties() throws IOException {
        if (!propertyConfigFile.exists()) {
            propertyConfigFile.createNewFile();
        }
        Map<String, Map<String, Object>> driverPropertiesMap = getAllDriverProperties();
        driverPropertiesMap.put(getDriverType().getName(), driverProperties);
        Gson gsonObj = new Gson();
        String strJson = gsonObj.toJson(driverPropertiesMap);
        FileUtils.writeStringToFile(propertyConfigFile, strJson);
    }

    protected Map<String, Map<String, Object>> getAllDriverProperties() throws IOException {
        if (propertyConfigFile.exists()) {
            Gson gsonObj = new Gson();
            try {
                String propertyConfigFileContent = FileUtils.readFileToString(propertyConfigFile);
                Type collectionType = new TypeToken<Map<String, Map<String, Object>>>() {
                }.getType();
                Map<String, Map<String, Object>> result = gsonObj.fromJson(propertyConfigFileContent, collectionType);
                return (result == null) ? new LinkedHashMap<String, Map<String, Object>>() : result;
            } catch (IOException | JsonSyntaxException exception) {
                // reading file failed or parsing json failed --> return empty map;
            }
        }
        return new HashMap<String, Map<String, Object>>();
    }

    public Object getDriverPropertyValue(String rawKey) {
        for (Entry<String, Object> driverProperty : getDriverProperties().entrySet()) {
            if (driverProperty.getKey().equals(rawKey)) {
                return driverProperty.getValue();
            }
        }
        return null;
    }

    public void setDriverPropertyValue(String rawKey, String propertyValue) {
        for (Entry<String, Object> driverProperty : getDriverProperties().entrySet()) {
            if (driverProperty.getKey().equals(rawKey)) {
                driverProperty.setValue(propertyValue);
            }
        }
    }

    @Override
    public String toString() {
        return getDriverProperties().toString();
    }
    
    public abstract IDriverConnector clone();
    
    protected Object cloneDriverPropertyValue(Object propertyValue) {
        if (propertyValue instanceof String) {
            return new String((String) propertyValue);
        } else if (propertyValue instanceof List) {
            List<Object> newList = new ArrayList<Object>();
            for (Object object : (List<?>) propertyValue) {
                newList.add(cloneDriverPropertyValue(object));
            }
            return newList;
        } else if (propertyValue instanceof Map) {
            Map<Object, Object> newMap = new LinkedHashMap<Object, Object>();
            for (Entry<?, ?> entry : ((Map<?, ?>) propertyValue).entrySet()) {
                newMap.put(cloneDriverPropertyValue(entry.getKey()), cloneDriverPropertyValue(entry.getValue()));
            }
            return newMap;
        } else {
            return propertyValue;
        }
    }
}
