package com.kms.katalon.core.webservice.util;

import com.kms.katalon.core.testobject.RequestObject;

public class WebServiceCommonUtil {
    public static int getValidRequestTimeout(int timeout) {
        return timeout >= 0
                ? timeout
                : RequestObject.DEFAULT_TIMEOUT;
    }

    public static boolean isUnsetRequestTimeout(int timeout) {
        return timeout == RequestObject.TIMEOUT_UNSET;
    }
}
