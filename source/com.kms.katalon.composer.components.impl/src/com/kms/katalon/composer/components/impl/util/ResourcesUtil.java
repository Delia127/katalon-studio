package com.kms.katalon.composer.components.impl.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.GlobalStringConstants;

public class ResourcesUtil {
    public static String getFileContent(Class<?> clazz, String filePath) {
        URL url = FileLocator.find(FrameworkUtil.getBundle(clazz), new Path(filePath), null);
        try {
            return StringUtils.join(IOUtils.readLines(new BufferedInputStream(url.openStream()),
                            GlobalStringConstants.DF_CHARSET), "\n");
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return StringUtils.EMPTY;
        }
    }
}
