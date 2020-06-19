package com.kms.katalon.execution.constants;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class ImageConstants {
    public static final String IMG_URL_16_CUSTOM;
    
    static {
        Bundle resourceBundle = Platform.getBundle("com.kms.katalon.composer.resources");
        if (resourceBundle != null) {
            IMG_URL_16_CUSTOM = FileLocator.find(resourceBundle, new Path("/icons/custom_execution_16.png"), null).toString();
        } else {
            IMG_URL_16_CUSTOM = null;
        }
    }
}
