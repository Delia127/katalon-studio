package com.kms.katalon.composer.components.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

public class FileUtil {
	public static IFile getFileFromFileLocation(String fileLocation) {
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileLocation));
	}
}
