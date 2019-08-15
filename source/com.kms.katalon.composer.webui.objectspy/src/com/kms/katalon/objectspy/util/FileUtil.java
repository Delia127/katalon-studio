package com.kms.katalon.objectspy.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.kms.katalon.execution.classpath.ClassPathResolver;

public class FileUtil {
	private static final String EXTENSIONS_FOLDER_NAME = "resources/extensions";

	public static Image loadImage(Bundle bundle, String imageURI) {
		URL url = FileLocator.find(bundle, new Path(imageURI), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}
	
	public static File getExtensionsDirectory(Bundle bundle) throws IOException {
		File bundleFile = FileLocator.getBundleFile(bundle);
		if (bundleFile.isDirectory()) { // run by IDE
			return new File(bundleFile + File.separator + EXTENSIONS_FOLDER_NAME);
		} else { // run as product
			return new File(ClassPathResolver.getConfigurationFolder(), EXTENSIONS_FOLDER_NAME);
		}
	}
	
	public static File getExtensionBuildFolder() throws IOException {
	    return new File(ClassPathResolver.getConfigurationFolder(), EXTENSIONS_FOLDER_NAME);
	}
}
