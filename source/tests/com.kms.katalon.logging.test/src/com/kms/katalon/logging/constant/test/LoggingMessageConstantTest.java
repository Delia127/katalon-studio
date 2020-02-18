package com.kms.katalon.logging.constant.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kms.katalon.logging.constant.LoggingMessageConstants;

public class LoggingMessageConstantTest {

    @Test
    public void allMessageTests() {
        assertEquals(LoggingMessageConstants.MSG_WARNING_SYSTEM_NOT_SUPPORT_UTF8, "!WARNING The OS doesn't support UTF-8 encoding.");
    }
}
