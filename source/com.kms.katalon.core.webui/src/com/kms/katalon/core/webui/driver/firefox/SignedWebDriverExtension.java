package com.kms.katalon.core.webui.driver.firefox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.internal.Extension;
import org.openqa.selenium.io.FileHandler;

public class SignedWebDriverExtension implements Extension {

    private static final String ERR_MSG_UNSUPPORTED_EXT = "Only support installing zipped extensions for now";

    private String loadFrom;

    private Class<?> loadResourcesUsing;

    public SignedWebDriverExtension(Class<?> loadResourcesUsing, String loadFrom) {
        this.loadResourcesUsing = loadResourcesUsing;
        this.loadFrom = loadFrom;
    }

    public void writeTo(File extensionsDir) throws IOException {
        if (!FileHandler.isZipped(loadFrom)) {
            throw new WebDriverException(ERR_MSG_UNSUPPORTED_EXT);
        }
        if (!extensionsDir.exists()) {
            extensionsDir.mkdirs();
        }
        InputStream fis = loadResourcesUsing.getResourceAsStream(loadFrom);
        File toFile = new File(extensionsDir, FilenameUtils.getName(loadFrom));
        FileUtils.copyInputStreamToFile(fis, toFile);
    }
}
