package com.kms.katalon.util;

import java.io.File;

//Copied from SoapUI
public class Tools {
    /**
     * Joins a relative url to a base url.. needs improvements..
     */
    public static String joinRelativeUrl(String baseUrl, String url) {
        if (baseUrl.indexOf('?') > 0) {
            baseUrl = baseUrl.substring(0, baseUrl.indexOf('?'));
        }

        boolean isWindowsUrl = baseUrl.indexOf('\\') >= 0;
        boolean isUsedInUnix = File.separatorChar == '/';

        if (isUsedInUnix && isWindowsUrl) {
            baseUrl = baseUrl.replace('\\', '/');
            url = url.replace('\\', '/');
        }

        boolean isFile = baseUrl.startsWith("file:");

        int ix = baseUrl.lastIndexOf('\\');
        if (ix == -1) {
            ix = baseUrl.lastIndexOf('/');
        }

        // absolute?
        if (url.startsWith("/") && !isFile) {
            ix = baseUrl.indexOf("/", baseUrl.indexOf("//") + 2);
            return baseUrl.substring(0, ix) + url;
        }

        // remove leading "./"
        while (url.startsWith(".\\") || url.startsWith("./")) {
            url = url.substring(2);
        }

        // remove leading "../"
        while (url.startsWith("../") || url.startsWith("..\\")) {
            int ix2 = baseUrl.lastIndexOf('\\', ix - 1);
            if (ix2 == -1) {
                ix2 = baseUrl.lastIndexOf('/', ix - 1);
            }
            if (ix2 == -1) {
                break;
            }

            baseUrl = baseUrl.substring(0, ix2 + 1);
            ix = ix2;

            url = url.substring(3);
        }

        // remove "/./"
        while (url.contains("/./") || url.contains("\\.\\")) {
            int ix2 = url.indexOf("/./");
            if (ix2 == -1) {
                ix2 = url.indexOf("\\.\\");
            }

            url = url.substring(0, ix2) + url.substring(ix2 + 2);
        }

        // remove "/../"
        while (url.contains("/../") || url.contains("\\..\\")) {
            int ix2 = -1;

            int ix3 = url.indexOf("/../");
            if (ix3 == -1) {
                ix3 = url.indexOf("\\..\\");
                ix2 = url.lastIndexOf('\\', ix3 - 1);
            } else {
                ix2 = url.lastIndexOf('/', ix3 - 1);
            }

            if (ix2 == -1) {
                break;
            }

            url = url.substring(0, ix2) + url.substring(ix3 + 3);
        }

        String result = baseUrl.substring(0, ix + 1) + url;
        if (isFile) {
            result = result.replace('/', File.separatorChar);
        }

        return result;
    }
}
