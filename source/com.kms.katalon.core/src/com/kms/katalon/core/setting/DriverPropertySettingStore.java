package com.kms.katalon.core.setting;

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
import com.kms.katalon.core.driver.DriverType;

public abstract class DriverPropertySettingStore {
    protected File propertyConfigFile;
    protected DriverType driverType;

    public DriverPropertySettingStore(String projectDir, DriverType driverType) throws IOException {
        propertyConfigFile = new File(projectDir + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME + File.separator + getSettingFileName()
                + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION);
        if (!propertyConfigFile.exists()) {
            propertyConfigFile.createNewFile();
        }
        this.driverType = driverType;
    }

    public DriverPropertySettingStore(String projectDir, DriverType driverType, String customProfileName)
            throws IOException {
        propertyConfigFile = new File(projectDir + File.separator
                + PropertySettingStoreUtil.EXTERNAL_SETTING_ROOT_FOLDLER_NAME + File.separator + customProfileName
                + File.separator + getSettingFileName() + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION);
        if (!propertyConfigFile.exists()) {
            propertyConfigFile.createNewFile();
        }
        this.driverType = driverType;
    }

    protected abstract String getSettingFileName();

    public Map<String, Object> getDriverProperties() {
        Map<String, Map<String, Object>> allProperties = getAllDriverProperties();
        if (allProperties.get(driverType.getName()) == null) {
            return new LinkedHashMap<String, Object>();
        }
        return allProperties.get(driverType.getName());
    }

    public void saveDriverProperties(Map<String, Object> driverProperties) throws IOException {
        // PropertySettingStoreUtil.removeAll(getPropertyKeys(),
        // propertyConfigFile);
        // for (DriverProperty driverProperty : driverProperties) {
        // PropertySettingStoreUtil.addNewProperty(driverProperty.getRawName(),
        // driverProperty.getRawValue(),
        // propertyConfigFile);
        // }
        Map<String, Map<String, Object>> driverPropertiesMap = getAllDriverProperties();
        driverPropertiesMap.put(driverType.getName(), driverProperties);
        Gson gsonObj = new Gson();
        String strJson = gsonObj.toJson(driverPropertiesMap);
        FileUtils.writeStringToFile(propertyConfigFile, strJson);
        // FileInputStream fileInput = null;
        // FileOutputStream fileOutput = null;
        // try {
        // fileInput = new FileInputStream(propertyConfigFile);
        // LinkedProperties properties = new LinkedProperties();
        // properties.load(fileInput);
        // fileInput.close();
        // fileInput = null;
        //
        // properties.put(key, value);
        // fileOutput = new FileOutputStream(propertyFile);
        // properties.store(fileOutput, null);
        // fileOutput.close();
        // fileOutput = null;
        // } finally {
        // if (fileInput != null) {
        // fileInput.close();
        // }
        //
        // if (fileOutput != null) {
        // fileOutput.close();
        // }
        // }
    }

    public Map<String, Map<String, Object>> getAllDriverProperties() {
        // try {
        // return
        // PropertySettingStoreUtil.getPropertyValues(DriverProperty.getParentKey(driverType),
        // propertyConfigFile);
        // } catch (IOException e) {
        // return Collections.emptyMap();
        // }
        Gson gsonObj = new Gson();
        try {
            String propertyConfigFileContent = FileUtils.readFileToString(propertyConfigFile);
            Type collectionType = new TypeToken<Map<String, Map<String, Object>>>() {
            }.getType();
            Map<String, Map<String, Object>> result = gsonObj.fromJson(propertyConfigFileContent, collectionType);
            return (result == null) ? new LinkedHashMap<String, Map<String, Object>>() : result;
        } catch (IOException | JsonSyntaxException exception) {
            // reading file failed or parsing json failed --> return empty map;
            return new HashMap<String, Map<String, Object>>();
        }

    }

    public Object getDriverPropertyValue(String projectDir, String rawKey) {
        for (Entry<String, Object> driverProperty : getDriverProperties().entrySet()) {
            if (driverProperty.getKey().equals(rawKey)) {
                return driverProperty.getValue();
            }
        }
        return null;
    }

    public DriverType getDriverType() {
        return driverType;
    }
}
