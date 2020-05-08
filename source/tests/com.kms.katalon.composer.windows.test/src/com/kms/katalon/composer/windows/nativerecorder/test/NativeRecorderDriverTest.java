package com.kms.katalon.composer.windows.nativerecorder.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.kms.katalon.composer.windows.nativerecorder.NativeRecorderDriver;

public class NativeRecorderDriverTest {
    
    @Before
    public void setUp() {
    }

    @Test
    public void startTest() throws IOException {
        NativeRecorderDriver nativeDriver = new NativeRecorderDriver();
        nativeDriver.start();
        assertTrue("Windows Native Recorder Driver should be running", nativeDriver.isRunning());
    }
}
