package com.kms.katalon.core.webui.driver.safari;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.Platform;
import org.openqa.selenium.io.FileHandler;

import com.google.common.collect.ImmutableList;

/**
 * This class copy code from CSessionData class to overcome it's package-protected fields
 * This class should not be re-factor
 * TODO: This class should be change when our com.kms.katalon.selenium-standalone project is updated
 *
 */
public class CSessionData {
    private final Iterable<File> sessionDataFiles;

    private CSessionData(Iterable<File> sessionDataFiles) {
      this.sessionDataFiles = sessionDataFiles;
    }

    /**
     * @return The SessionData container for the current platform.
     */
    public static CSessionData forCurrentPlatform() {
      Platform current = Platform.getCurrent();

      Iterable<File> files = ImmutableList.of();
      if (current.is(Platform.MAC)) {
        File libraryDir = new File("/Users", System.getenv("USER") + "/Library");
        files = ImmutableList.of(
            new File(libraryDir, "Caches/com.apple.Safari/Cache.db"),
            new File(libraryDir, "Cookies/Cookies.binarycookies"),
            new File(libraryDir, "Cookies/Cookies.plist"),
            new File(libraryDir, "Safari/History.plist"),
            new File(libraryDir, "Safari/LastSession.plist"),
            new File(libraryDir, "Safari/LocalStorage"),
            new File(libraryDir, "Safari/Databases"));
      }

      if (current.is(Platform.WINDOWS)) {
        File appDataDir = new File(System.getenv("APPDATA"), "Apple Computer/Safari");
        File localDataDir = new File(System.getenv("LOCALAPPDATA"), "Apple Computer/Safari");

        files = ImmutableList.of(
            new File(appDataDir, "History.plist"),
            new File(appDataDir, "LastSession.plist"),
            new File(appDataDir, "Cookies/Cookies.plist"),
            new File(appDataDir, "Cookies/Cookies.binarycookies"),
            new File(localDataDir, "Cache.db"),
            new File(localDataDir, "Databases"),
            new File(localDataDir, "LocalStorage"));
      }

      return new CSessionData(files);
    }

    /**
     * Deletes all of the existing session data.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void clear() throws IOException {
      for (File file : sessionDataFiles) {
        FileHandler.delete(file);
      }
    }
}
