package com.kms.katalon.application;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.logging.LogUtil;

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
            System.out.println("> MetadataCorruptedResolver.resolve(...) > before resolve");
            File resourcesFolder = new File(
                    Platform.getInstanceLocation().getDataArea(ResourcesPlugin.PI_RESOURCES).getFile());
            FileUtils.deleteDirectory(resourcesFolder);
            System.out.println("> MetadataCorruptedResolver.resolve(...) > resolve OK");
            return true;
        } catch (IOException e) {
            System.out.println("> MetadataCorruptedResolver.resolve(...) > resolve error");
            LogUtil.logError(e);
            return false;
        }

    }
}
