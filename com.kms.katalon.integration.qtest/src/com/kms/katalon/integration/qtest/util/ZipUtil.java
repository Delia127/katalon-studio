package com.kms.katalon.integration.qtest.util;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class ZipUtil {
	public static ZipFile getZipFile(File zipFile, File folderToZip) throws ZipException {		
		if (zipFile.exists()) {
			zipFile.delete();
		}
		
		ZipFile returnedZipFile = new ZipFile(zipFile);

		ZipParameters parameters = new ZipParameters();

		// set compression method to store compression
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

		// Set the compression level
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

		// Add folder to the zip file
		returnedZipFile.addFolder(folderToZip.getAbsolutePath(), parameters);		
		
		return returnedZipFile;
	}
	
}
