package com.kms.katalon.composer.integration.analytics.uploadProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipHelper {
	
	public static void Compress(String srcFolder, String destZipFile, List<String> ignoreFileArray) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWrite = null;
		
		fileWrite = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWrite);
		
		addFolderToZip("", srcFolder, zip, ignoreFileArray);
		zip.flush();
		zip.close();
	}
	
	private static void addFileToZip(String path, String srcFile, ZipOutputStream zip, List<String> ignoreFileArray) throws Exception {
		File folder = new File(srcFile);
		
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip, ignoreFileArray);
		} else {
			String extension = getFileExtension(folder);
			if (!ignoreFileArray.contains(extension)) {
				byte[] buf = new byte[1024];
				int len;
				FileInputStream in = new FileInputStream(srcFile);
				zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
				while((len = in.read(buf)) > 0) {
					zip.write(buf, 0, len);
				}
			}		
		}
	}
	
	private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip, List<String> ignoreFileArray) throws Exception {
		File folder = new File(srcFolder);
		
		for (String fileName : folder.list()) {
			if (!ignoreFileArray.contains(fileName)) {
				if (path.equals("")) {
					addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, ignoreFileArray);
				} else {
					addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, ignoreFileArray);
				}
			}
		}
	}
	
	private static String getFileExtension(File file) {
        String extension = "";
 
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }
 
        return extension;
 
    }
}
