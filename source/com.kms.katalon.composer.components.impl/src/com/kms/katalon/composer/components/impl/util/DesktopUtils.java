package com.kms.katalon.composer.components.impl.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import org.eclipse.core.runtime.Platform;

public class DesktopUtils {
    public static void openUri(URI uri) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(uri);
            return;
        }

        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            openDefaultBrowserOnWindows(uri);
        }
    }

    public static void openDefaultBrowserOnWindows(URI uri) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", "\"\"", "\"" + uri.toString() + "\"");
        builder.start();
    }
}
