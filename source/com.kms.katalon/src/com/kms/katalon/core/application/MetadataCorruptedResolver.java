package com.kms.katalon.core.application;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class MetadataCorruptedResolver {

    private static final String SNAP = ".snap";

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
            if (!resourcesFolder.exists()) {
                return false;
            }
            cleanSnapFiles(resourcesFolder);
            return true;
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return false;
        }

    }

    private void cleanSnapFiles(File resourcesFolder) {
        File[] snapFiles = resourcesFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(SNAP);
            }
        });

        if (snapFiles == null) {
            return;
        }

        for (File snap : snapFiles) {
            FileUtils.deleteQuietly(snap);
        }
    }
}
