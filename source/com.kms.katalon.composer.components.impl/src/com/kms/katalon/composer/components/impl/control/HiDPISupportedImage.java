package com.kms.katalon.composer.components.impl.control;

import static com.kms.katalon.constants.GlobalStringConstants.ENTITY_ID_SEPARATOR;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ImageUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class HiDPISupportedImage {

    /** High resolution image suffix. E.g icon@2x.png */
    private static final String X2_SUFFIX = "@2x.";

    private static long sel_imageRepWithContentsOfFile_ = 0;

    private static final Object[] EMPTY_OBJECT_ARR = new Object[] {};

    private static final String MAC_NS_THREAD_CLASS = "org.eclipse.swt.internal.cocoa.NSThread";

    private static final String MAC_NS_AUTO_RELEASE_POOL_CLASS = "org.eclipse.swt.internal.cocoa.NSAutoreleasePool";

    private static final String MAC_NS_IMG_REP_CLASS = "org.eclipse.swt.internal.cocoa.NSImageRep";

    private static final String MAC_NS_STRING_CLASS = "org.eclipse.swt.internal.cocoa.NSString";

    private static final String MAC_COCOA_OS_CLASS = "org.eclipse.swt.internal.cocoa.OS";

    private static final String MAC_COCOA_ID_CLASS = "org.eclipse.swt.internal.cocoa.id";

    private Bundle bundle;

    private Image image;

    private String imagePath1x;

    private String tempDirLocation;

    private HiDPISupportedImage(String absImageURI) {
        this(getBundle(absImageURI), getLocation(absImageURI));
    }

    private HiDPISupportedImage(Bundle bundle, String imageURI) {
        try {
            this.bundle = bundle;

            // Normal image resolution for Windows, Mac, Linux
            image = ImageUtil.loadImage(this.bundle, imageURI);

            imagePath1x = imageURI;

            // for macOS only
            if (!Platform.OS_MACOSX.equals(Platform.getOS())) {
                return;
            }

            // for HDPI
            String imageURI2x = get2xImageURI(imageURI);
            if (imageURI2x == null) {
                // No 2x image found
                return;
            }

            try {
                tempDirLocation = Paths.get(Platform.getInstallLocation().getURL().toURI()).toString() + "/temp/";
            } catch (URISyntaxException e) {
                // this cannot happen
                LoggerSingleton.logError(e);
            }
            Image temp2xImage = ImageUtil.loadImage(this.bundle, imageURI2x);
            if (temp2xImage != null) {
                writeImageToTemp(imageURI, image);
                writeImageToTemp(imageURI2x, temp2xImage);
            }
            initHighResolutionImageForMac(imageURI2x);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    public static Image loadImage(String absImageURI) {
        return new HiDPISupportedImage(absImageURI).getImage();
    }

    public static Image loadImage(Bundle bundle, String imageURI) {
        return new HiDPISupportedImage(bundle, imageURI).getImage();
    }

    private static Bundle getBundle(String uri) {
        if (StringUtils.isBlank(uri)) {
            return null;
        }
        return Platform.getBundle(uri.split(ENTITY_ID_SEPARATOR)[2]);
    }

    private static String getLocation(String uri) {
        StringBuilder locationBuilder = new StringBuilder();
        String[] splitSegments = uri.split(ENTITY_ID_SEPARATOR);
        for (int i = 3; i < splitSegments.length; i++) {
            locationBuilder.append(ENTITY_ID_SEPARATOR).append(splitSegments[i]);
        }
        return locationBuilder.toString();
    }

    /**
     * Handle image for macOS
     * <p>
     * <strong>Explanation for inner reflection code</strong>
     * </p>
     * 
     * <pre>
     * public Image(Device device, String fileName) {
     *     super(device);
     *     if (filename == null)
     *         SWT.error(SWT.ERROR_INVALID_ARGUMENT);
     *     NSAutoreleasePool pool = null;
     *     if (!NSThread.isMainThread())
     *         pool = (NSAutoreleasePool) new NSAutoreleasePool().alloc().init();
     *     try {
     *         initNative(filename);
     *         if (this.handle == null) {
     *             init(new ImageData(filename));
     *         }
     *         init();
     *         String filename2x = get2xFileName(fileName);
     *         if (filename2x != null) {
     *             id id = NSImageRep.imageRepWithContentsOfFile(NSString.stringWith(filename2x));
     *             NSImageRep rep = new NSImageRep(id);
     *             handle.addRepresentation(rep);
     *         }
     *     } finally {
     *         if (pool != null) {
     *             pool.release();
     *         }
     *     }
     * }
     * </pre>
     * 
     * @param imagePath2x
     * @throws Exception
     */
    private void initHighResolutionImageForMac(String imagePath2x) throws Exception {
        imagePath2x = StringUtils.removeStart(imagePath2x, "/");

        // If MacOS & Retina. Hook into the image creating process, add more representation for HDPI
        image = createInstance(Image.class, new Class[] { Device.class }, new Object[] { Display.getCurrent() });

        Object pool = null;
        // !NSThread.isMainThread()
        if (!Boolean.parseBoolean(ObjectUtils.toString(invokeStaticMethodOfClass(MAC_NS_THREAD_CLASS, "isMainThread",
                EMPTY_OBJECT_ARR)))) {
            Object nsAutoreleasePool = createInstance(MAC_NS_AUTO_RELEASE_POOL_CLASS, null, null);
            pool = invokeMethodOfInstance(nsAutoreleasePool, nsAutoreleasePool.getClass().getSuperclass(), "init",
                    EMPTY_OBJECT_ARR);
        }

        String absolutePath1x = tempDirLocation + FilenameUtils.getName(imagePath1x);
        String absolutePath2x = tempDirLocation + FilenameUtils.getName(imagePath2x);
        try {
            // org.eclipse.swt.graphics.Image.initNative(imageURI2x)
            invokeMethodOfInstance(image, image.getClass(), "initNative", absolutePath1x);

            if (getHandle() == null) {
                // org.eclipse.swt.graphics.Image.init(new ImageData(imageURI2x))
                invokeMethodOfInstance(image, image.getClass(), "init", new ImageData(absolutePath2x));
            }

            // org.eclipse.swt.graphics.Resource.init()
            invokeMethodOfInstance(image, image.getClass().getSuperclass(), "init", EMPTY_OBJECT_ARR);

            Object nsStringImageURI2x = invokeStaticMethodOfClass(MAC_NS_STRING_CLASS, "stringWith", new Path(absolutePath2x).toOSString());
            long stringHandleId = nsStringImageURI2x == null ? 0 : nsStringImageURI2x.getClass()
                    .getSuperclass()
                    .getSuperclass()
                    .getDeclaredField("id")
                    .getLong(nsStringImageURI2x);
            Object id = imageRepWithContentsOfFile(stringHandleId);
            Object nsImageRep = createInstance(MAC_NS_IMG_REP_CLASS, new Class[] { id.getClass() }, new Object[] { id });
            Object imageHandle = getHandle();
            invokeMethodOfInstance(imageHandle, imageHandle.getClass(), "addRepresentation", nsImageRep);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        } finally {
            if (pool == null) {
                return;
            }
            invokeMethodOfInstance(pool, pool.getClass().getSuperclass(), "release", EMPTY_OBJECT_ARR);
        }
    }
    
    private Object getHandle() throws Exception {
        return image.getClass().getDeclaredField("handle").get(image);
    }

    private String get2xImageURI(String imageURI) {
        if (StringUtils.isEmpty(imageURI)) {
            return null;
        }
        return FilenameUtils.removeExtension(imageURI) + X2_SUFFIX + FilenameUtils.getExtension(imageURI);
    }

    private Object invokeMethodOfInstance(Object instance, Class ownerClassOfMethod, String methodName,
            Object... params) throws Exception {
        Class[] types = getParamTypes(params);
        Method method = ownerClassOfMethod.getDeclaredMethod(methodName, types);
        method.setAccessible(true);
        return method.invoke(instance, params);
    }

    private Object invokeStaticMethodOfClass(String className, String methodName, Object... params) throws Exception {
        Class[] types = getParamTypes(params);
        return Class.forName(className).getDeclaredMethod(methodName, types).invoke(null, params);
    }

    private Class[] getParamTypes(Object... params) {
        if (params == null || params.length == 0) {
            return new Class[] {};
        }
        Class[] types = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            types[i] = params[i].getClass();
        }
        return types;
    }

    private Object createInstance(String className, Class[] paramTypes, Object[] params) throws Exception {
        return createInstance(Class.forName(className), paramTypes, params);
    }

    private <T> T createInstance(Class<T> creator, Class[] paramTypes, Object[] params) throws Exception {
        if (params == null || params.length == 0) {
            return creator.newInstance();
        }
        Class[] types = null;
        if (paramTypes != null) {
            types = paramTypes;
        } else {
            types = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                types[i] = params[i].getClass();
            }
        }
        Constructor<T> constructor = creator.getDeclaredConstructor(types);
        constructor.setAccessible(true);
        return constructor.newInstance(params);
    }

    /**
     * @param nsStringFilenameId
     * @return return instance of org.eclipse.swt.internal.cocoa.id
     * @throws Exception
     */
    private Object imageRepWithContentsOfFile(long nsStringFilenameId) throws Exception {
        Class os = Class.forName(MAC_COCOA_OS_CLASS);

        if (sel_imageRepWithContentsOfFile_ == 0) {
            sel_imageRepWithContentsOfFile_ = (long) invokeStaticMethodOfClass(MAC_COCOA_OS_CLASS, "sel_registerName",
                    "imageRepWithContentsOfFile:");
        }

        long result = (long) os.getDeclaredMethod("objc_msgSend", long.class, long.class, long.class).invoke(null,
                os.getDeclaredField("class_NSImageRep").getLong(null), sel_imageRepWithContentsOfFile_,
                nsStringFilenameId);

        return result != 0 ? Class.forName(MAC_COCOA_ID_CLASS).getConstructor(long.class).newInstance(result) : null;
    }

    public Image getImage() {
        return image;
    }

    private void writeImageToTemp(String imagePath, Image image) {
        // write 2x image to temp dir
        try {
            if (tempDirLocation == null) {
                return;
            }

            File tempDir = new File(tempDirLocation);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            String tempImageLocation = tempDirLocation + FilenameUtils.getName(imagePath);

            // File imageFile = new File(tempImageLocation);
            // if (imageFile.exists()) {
            // return;
            // }

            ImageLoader loader = new ImageLoader();
            loader.data = new ImageData[] { image.getImageData() };
            // We have PNG images only, except for splash screen (BMP)
            loader.save(tempImageLocation, SWT.IMAGE_PNG);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
