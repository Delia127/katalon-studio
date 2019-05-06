package com.kms.katalon.composer.integration.analytics.uploadProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipHelper {
	
	private static List<String> ignoreFileArray = Arrays.asList(".class", ".log",
			".ctxt", ".jar", ".war", ".ear", ".zip", ".tar.gz", ".rar");
	
	private static List<String> ignoreFolderArray = Arrays.asList(".classpath", 
			".project", ".mtj.tmp", "hs_err_pid", "Libs", "bin", ".git");
	
	public static void Compress(String srcFolder, String destZipFile) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWrite = null;
		
		fileWrite = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWrite);
		
		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
	}
	
	private static void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFile);
		
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
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
	
	private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);
		
		for (String fileName : folder.list()) {
			if (!ignoreFolderArray.contains(fileName)) {
				if (path.equals("")) {
					addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
				} else {
					addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
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
                if (name.equals("gz")) {
                	if (extension.equals("tar.gz")) {
                		extension = ".tar.gz";
                	}
                }
            }
        } catch (Exception e) {
            extension = "";
        }
 
        return extension;
 
    }
}
