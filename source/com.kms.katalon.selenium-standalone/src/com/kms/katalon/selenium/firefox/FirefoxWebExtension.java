package com.kms.katalon.selenium.firefox;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.firefox.internal.Extension;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.io.TemporaryFilesystem;
import org.openqa.selenium.io.Zip;

public class FirefoxWebExtension implements Extension {
    private final File toInstall;

    private String uuid;

    public FirefoxWebExtension(File toInstall, String uuid) {
        this.toInstall = toInstall;
        this.uuid = uuid;
    }

    public void writeTo(File extensionsDir) throws IOException {
        if (!toInstall.isDirectory() && !FileHandler.isZipped(toInstall.getAbsolutePath())
                && !"xpi".equals(FilenameUtils.getExtension(toInstall.getName()))) {
            throw new IOException(String.format("Can only install from a zip file, an XPI or a directory: %s",
                    toInstall.getAbsolutePath()));
        }

        File root = obtainRootDirectory(toInstall);

        String id = uuid;

        File extensionDirectory = new File(extensionsDir, id);

        if (extensionDirectory.exists() && !FileHandler.delete(extensionDirectory)) {
            throw new IOException("Unable to delete existing extension directory: " + extensionDirectory);
        }

        FileHandler.createDir(extensionDirectory);
        FileHandler.makeWritable(extensionDirectory);
        FileHandler.copy(root, extensionDirectory);
        TemporaryFilesystem.getDefaultTmpFS().deleteTempDir(root);
    }

    private File obtainRootDirectory(File extensionToInstall) throws IOException {
        File root = extensionToInstall;
        if (!extensionToInstall.isDirectory()) {
            root = TemporaryFilesystem.getDefaultTmpFS().createTempDir("katalon_addon", "firefox");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(extensionToInstall));
            try {
                Zip.unzip(bis, root);
            } finally {
                bis.close();
            }
        }
        return root;
    }
}
