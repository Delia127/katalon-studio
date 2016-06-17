package com.kms.katalon.composer.components.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class ImageUtil {
    public static Image loadImage(String imageUrl) throws MalformedURLException {
        ImageDescriptor image = ImageDescriptor.createFromURL(new URL(imageUrl));
        return image.createImage();
    }

    public static Image loadImage(Bundle bundle, String imageURI) {
        URL url = FileLocator.find(bundle, new Path(imageURI), null);
        ImageDescriptor image = ImageDescriptor.createFromURL(url);
        return image.createImage();
    }

    public static String getImageUrl(Bundle bundle, String imageURI) {
        return FileLocator.find(bundle, new Path(imageURI), null).toString();
    }
}
