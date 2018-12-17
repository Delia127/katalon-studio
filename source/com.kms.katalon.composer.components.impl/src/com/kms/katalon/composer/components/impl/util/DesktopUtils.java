package com.kms.katalon.composer.components.impl.util;

import java.io.IOException;
import java.net.URI;

import org.eclipse.swt.program.Program;

public class DesktopUtils {
    public static void openUri(URI uri) throws IOException {
        Program.launch(uri.toString());
    }

    public static void openDefaultBrowserOnWindows(URI uri) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", "\"\"", "\"" + uri.toString() + "\"");
        builder.start();
    }
}
