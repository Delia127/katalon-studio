package com.kms.katalon.logging.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kms.katalon.logging.LogManager;

public class LogManagerTest {
    
    @Test
    public void activateTest() {
        LogManager.active();

        assertEquals(System.out, LogManager.getOutputLogger());
        assertEquals(System.err, LogManager.getErrorLogger());
    }

    @Test
    public void getErrorLoggerTest() {
        assertEquals(System.err, LogManager.getErrorLogger());
    }

    @Test
    public void getOutputLoggerTest() {
        assertEquals(System.out, LogManager.getOutputLogger());
    }
}
