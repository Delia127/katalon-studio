package com.kms.katalon.application.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.logging.LogUtil;

public class LocalStorage {

    private static Properties localStorage;

    private static final String TRUE_VALUE = "true";

    private static final String FALSE_VALUE = "false";

    public static final String LOCAL_STORAGE_FILE_LOCATION = ApplicationStringConstants.LOCAL_STORAGE_FILE_LOCATION;

    protected static String getStorageFileLocation() {
        return LOCAL_STORAGE_FILE_LOCATION;
    }

    public static <T> T get(String key, Type typeOfT) {
        String jsonValue = get(key);
        if (StringUtils.isBlank(jsonValue)) {
            return null;
        }
        return JsonUtil.fromJson(jsonValue, typeOfT);
    }

    public static boolean getBoolean(String key) {
        String value = get(key);
        return TRUE_VALUE.equalsIgnoreCase(value);
    }

    public static String get(String key) {
        Properties storage = getStorage();
        if (storage == null || !storage.containsKey(key)) {
            return null;
        }
        return storage.getProperty(key);
    }

    public static void set(String key, Object value) {
        set(key, value, true);
    }

    public static void set(String key, Object value, boolean autoSave) {
        String jsonObject = JsonUtil.toJson(value);
        set(key, jsonObject, autoSave);
    }

    public static void set(String key, boolean value) {
        set(key, value, true);
    }

    public static void set(String key, boolean value, boolean autoSave) {
        set(key, value ? TRUE_VALUE : FALSE_VALUE, autoSave);
    }

    public static void set(String key, String value) {
        set(key, value, true);
    }

    public static void set(String key, String value, boolean autoSave) {
        Properties storage = getStorage();
        storage.setProperty(key, value);
        if (autoSave) {
            save();
        }
    }

    public static void remove(String key) {
        Properties storage = getStorage();
        if (storage != null && storage.containsKey(key)) {
            storage.remove(key);
            save();
        }
    }

    public static void clear() {
        Properties storage = getStorage();
        storage.clear();
        save();
    }

    public static void save() {
        try (FileOutputStream out = new FileOutputStream(getStorageFileLocation())) {
            localStorage.store(out, installLocation());
        } catch (Exception ex) {
            LogUtil.logError(ex);
        }
    }

    private static Properties getStorage() {
        if (localStorage != null) {
            return localStorage;
        }

        File localStorageFile = new File(getStorageFileLocation());
        File katalonDir = new File(localStorageFile.getParent());

        if (!localStorageFile.exists()) {
            if (!katalonDir.exists()) {
                katalonDir.mkdir();
            }
            try {
                localStorageFile.createNewFile();
            } catch (Exception ex) {
                LogUtil.logError(ex);
            }
        }

        if (localStorageFile.exists()) {
            try (FileInputStream inputStream = new FileInputStream(localStorageFile)) {
                localStorage = new Properties();
                localStorage.load(inputStream);
            } catch (Exception ex) {
                localStorage = null;
                LogUtil.logError(ex);
            }
        }

        return localStorage;
    }

    public static String installLocation() {
        try {
            return Paths.get(new URI(Platform.getInstallLocation().getURL().toString().replace(" ", "%20"))).toString();
        } catch (NullPointerException e) {
            // do nothing
        } catch (URISyntaxException e) {
            // do nothing
        }
        return ApplicationStringConstants.EMPTY;
    }
}
