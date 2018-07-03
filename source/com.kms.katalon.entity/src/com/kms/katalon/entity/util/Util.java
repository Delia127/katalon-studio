package com.kms.katalon.entity.util;

import java.io.File;
import java.util.UUID;

public class Util {
    public static final String STRING_COPY_OF_NAME = " - Copy";
    
    public static final String STRING_DUPLICATE_OF_PACKAGE_NAME = "_copy";

    public static final String DEFAULT_GUID = "00000000-0000-0000-0000-000000000000";

    public static final String DATEFORMAT = "MM/dd/yyyy";

    // System separator
    public static String SEPARATOR = File.separator;

    public static String generateGuid() {
        return UUID.randomUUID().toString();
    }

}
