package com.kms.katalon.core.application;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class MetadataCorruptedResolver {

    public boolean isMetaFolderCorrupted() {
        try {
            ResourcesPlugin.getPlugin();
            return false;
        } catch (Error e) {
            return true;
        }
    }

    public boolean resolve() {
        try {
            File resourcesFolder = new File(
                    Platform.getInstanceLocation().getDataArea(ResourcesPlugin.PI_RESOURCES).getFile());
            FileUtils.deleteDirectory(resourcesFolder);
            return true;
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return false;
        }

    }
}
