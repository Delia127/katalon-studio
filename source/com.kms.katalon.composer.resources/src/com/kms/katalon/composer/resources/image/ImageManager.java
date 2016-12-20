package com.kms.katalon.composer.resources.image;

import java.net.URL;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.util.ImageUtil;

public class ImageManager {

    public static final String IMAGE_PATH = "/icons/";

    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageManager.class);

    private static ImageRegistry imageRegistry = JFaceResources.getImageRegistry();

    private ImageManager() {
        // do nothing
    }

    public static ImageRegistry getImageRegistry() {
        return imageRegistry;
    }

    /**
     * Get the Katalon image URL string
     * 
     * @param key Image key. Normally, it's the name of image
     * @return The image URL string
     * @see IImageKeys
     */
    public static String getImageURLString(String key) {
        return ImageUtil.getImageURLString(currentBundle, IMAGE_PATH + key);
    }

    /**
     * Get the Katalon image URL
     * 
     * @param key Image key. Normally, it's the name of image
     * @return The image URL
     * @see IImageKeys
     */
    public static URL getImageURL(String key) {
        return ImageUtil.getImageURL(currentBundle, IMAGE_PATH + key);
    }

    /**
     * Returns the Katalon image in JFace's image registry with the given key, or
     * <code>null</code> if none. Convenience method equivalent to
     *
     * <pre>
     * JFaceResources.getImageRegistry().get(key)
     * ImageManager.getImageRegistry().get(key)
     * </pre>
     *
     * @param key the key
     * @return the image, or <code>null</code> if none
     * @see IImageKeys
     */
    public static Image getImage(String key) {
        Image registeredImage = imageRegistry.get(key);
        if (registeredImage != null && registeredImage.isDisposed()) {
            removeImage(key);
            registerImage(key);
        }
        return imageRegistry.get(key);
    }

    /**
     * Register Katalon image
     * 
     * @param key Image key. Normally, it's the name of image
     * @param image Image
     * @see IImageKeys
     */
    public static void registerImage(String key, Image image) {
        Image registeredImage = getImage(key);
        if (registeredImage != null && registeredImage.isDisposed()) {
            removeImage(key);
        }

        if (registeredImage == null) {
            imageRegistry.put(key, image);
        }
    }

    /**
     * Register image for this bundle (com.kms.katalon.composer.resources) only
     * 
     * @param key Image key. Normally, it's the name of image
     * @see #registerImage(String, Image)
     * @see IImageKeys
     */
    public static void registerImage(String key) {
        registerImage(key, ImageUtil.loadImage(currentBundle, IMAGE_PATH + key));
    }

    /**
     * Remove Katalon image
     * 
     * @param key Image key. Normally, it's the name of image
     */
    public static void removeImage(String key) {
        imageRegistry.remove(key);
    }

    /**
     * Update/Replace the Katalon registered image
     * 
     * @param key Image key. Normally, it's the name of image
     * @param image new image
     * @see IImageKeys
     */
    public static void updateImage(String key, Image image) {
        imageRegistry.remove(key);
        imageRegistry.put(key, image);
    }

}
