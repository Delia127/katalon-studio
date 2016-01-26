package com.kms.katalon.composer.components.impl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class PropertiesUtil {

    /**
     * Create Properties file
     * 
     * @param fileLocation
     * @param mapKeyValue <code>Map&lt;String, String&gt;</code>
     */
    public static Properties create(String fileLocation, Map<String, String> mapKeyValue) {
        Properties prop = null;
        OutputStream output = null;
        try {
            output = new FileOutputStream(fileLocation);
            if (new File(fileLocation).exists()) {
                prop = read(fileLocation);
            } else {
                prop = new Properties();
            }
            if (mapKeyValue != null && !mapKeyValue.isEmpty()) {
                for (String propKey : mapKeyValue.keySet().toArray(new String[mapKeyValue.keySet().size()])) {
                    // set the properties value
                    prop.setProperty(propKey, mapKeyValue.get(propKey));
                }
            }

            // save properties
            prop.store(output, null);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    LoggerSingleton.logError(e);
                }
            }
        }
        return prop;
    }

    /**
     * Read Properties file
     * 
     * @param fileLocation
     * @return Properties object
     */
    public static Properties read(String fileLocation) {
        Properties prop = null;
        if (new File(fileLocation).exists()) {
            InputStream input = null;
            try {
                input = new FileInputStream(fileLocation);
                prop = new Properties();
                prop.load(input);
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        LoggerSingleton.logError(e);
                    }
                }
            }
        } else {
            prop = create(fileLocation, null);
        }
        return prop;
    }

    /**
     * Update Properties file with single value
     * 
     * @param fileLocation
     * @param propKey
     * @param propVal
     */
    public static Properties update(String fileLocation, String propKey, String propVal) {
        Properties prop = read(fileLocation);
        OutputStream output = null;
        try {
            if (prop != null) {
                prop.setProperty(propKey, propVal);
                // save properties
                output = new FileOutputStream(fileLocation);
                prop.store(output, null);
            }
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    LoggerSingleton.logError(e);
                }
            }
        }
        return prop;
    }
}
