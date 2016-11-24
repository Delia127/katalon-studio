package com.kms.katalon.selenium.firefox;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.openqa.selenium.Beta;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.UnableToCreateProfileException;
import org.openqa.selenium.io.FileHandler;

import com.google.common.annotations.VisibleForTesting;

public class CFirefoxProfile extends FirefoxProfile {
    private File model;

    public CFirefoxProfile() {
        this(null);
    }

    /**
     * Constructs a firefox profile from an existing profile directory.
     * <p>
     * Users who need this functionality should consider using a named profile.
     *
     * @param profileDir The profile directory to use as a model.
     */
    public CFirefoxProfile(File profileDir) {
        this(null, profileDir);
    }

    @VisibleForTesting
    @Beta
    protected CFirefoxProfile(Reader defaultsReader, File profileDir) {
        super(defaultsReader, profileDir);
        this.model = profileDir;
    }

    @Override
    protected void cleanTemporaryModel() {
        clean(model);
    }

    /**
     * Override default method to avoid profile dir being deleted
     */
    @Override
    public File layoutOnDisk() {
        try {
            File profileDir = createTempDir("anonymous", "webdriver-profile");
            File userPrefs = new File(profileDir, "user.js");

            copyModel(model, profileDir);
            installExtensions(profileDir);
            deleteLockFiles(profileDir);
            deleteExtensionsCacheIfItExists(profileDir);
            updateUserPrefs(userPrefs);
            return profileDir;
        } catch (IOException e) {
            throw new UnableToCreateProfileException(e);
        }
    }

    private File createTempDir(String prefix, String suffix) {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        try {
            File dir = new File(baseDir, prefix + System.currentTimeMillis() + suffix);
            if (!dir.mkdirs()) {
                throw new WebDriverException("Cannot create profile directory at " + dir.getAbsolutePath());
            }

            // Create the directory and mark it writable.
            FileHandler.createDir(dir);

            return dir;
        } catch (IOException e) {
            throw new WebDriverException("Unable to create temporary file at " + baseDir.getAbsolutePath());
        }
    }
}
