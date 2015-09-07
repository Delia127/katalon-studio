package com.kms.katalon.entity.util;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class ZipManager {

	public static void unzip(File tempZipFile, String destination)
			throws Exception {
		try {
			ZipFile zipFile = new ZipFile(tempZipFile);
			zipFile.extractAll(destination);
		} finally {
			tempZipFile.delete();
		}
	}

	public static File zip(String directory, String zipName) throws Exception {
		File folder = new File(directory);
		if (folder.isDirectory()) {
			File file = new File(folder.getParent() + Util.SEPARATOR + zipName
					+ ".zip");
			if (file.exists())
				file.delete();
			ZipFile zipFile = new ZipFile(folder.getParent() + Util.SEPARATOR
					+ zipName + ".zip");
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			zipFile.addFolder(directory, parameters);
			return new File(folder.getParent() + System.getProperty("file.separator") 
					+ zipName + ".zip");
		}
		return null;
	}
}
