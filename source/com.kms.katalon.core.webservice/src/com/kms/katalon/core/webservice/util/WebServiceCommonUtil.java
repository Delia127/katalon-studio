package com.kms.katalon.core.webservice.util;

import com.kms.katalon.core.testobject.RequestObject;

public class WebServiceCommonUtil {
    
    public static int getValidRequestTimeout(int timemout) {
        return isUnsetRequestTimeout(timemout)
                ? RequestObject.DEFAULT_TIMEOUT
                : timemout;
    }
    
    public static boolean isUnsetRequestTimeout(int timeout) {
        return timeout == RequestObject.TIMEOUT_UNSET;
    }
}
