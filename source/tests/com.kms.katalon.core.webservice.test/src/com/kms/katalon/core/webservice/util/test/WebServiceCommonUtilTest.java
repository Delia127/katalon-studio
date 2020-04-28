package com.kms.katalon.core.webservice.util.test;

import org.junit.Assert;
import org.junit.Test;

import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.webservice.util.WebServiceCommonUtil;

public class WebServiceCommonUtilTest {

    @Test
    public void isUnsetRequestTimeoutTest() {
        // Given
        int timeoutUnset = RequestObject.TIMEOUT_UNSET;

        // When
        boolean isUnsetTimeout = WebServiceCommonUtil.isUnsetRequestTimeout(timeoutUnset);

        // Then
        Assert.assertEquals(true, isUnsetTimeout);
    }

    @Test
    public void isUnsetRequestMaxResponseSizeTest() {
        // Given
        long maxResponseSizeUnset = RequestObject.MAX_RESPONSE_SIZE_UNSET;

        // When
        boolean isUnsetMaxResponseSize = WebServiceCommonUtil.isUnsetMaxRequestResponseSize(maxResponseSizeUnset);

        // Then
        Assert.assertEquals(true, isUnsetMaxResponseSize);
    }
}
