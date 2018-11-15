package com.kms.katalon.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;

public class LogbackUtil {

    public static File getLogbackConfigFile() throws IOException {
        URL logbackFileUrl = FileLocator.find(FrameworkUtil.getBundle(
                LogbackUtil.class),
                new Path("/resources/logback/logback.xml"),
                null);
        return FileUtils.toFile(FileLocator.toFileURL(logbackFileUrl));
    }
}
