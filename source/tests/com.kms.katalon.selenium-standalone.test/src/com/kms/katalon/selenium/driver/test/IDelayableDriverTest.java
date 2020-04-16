package com.kms.katalon.selenium.driver.test;

import org.junit.Test;

import com.kms.katalon.selenium.driver.IDelayableDriver;

public class IDelayableDriverTest {

    @Test
    public void testDelayActionIsAccurateWithin100Miliseconds() {
        int expected = 375;
        IDelayableDriver driver = new IDelayableDriver() {

            @Override
            public int getActionDelay() {
                return expected;
            }
        };
        long startTime = System.currentTimeMillis();
        driver.delay();
        long endTime = System.currentTimeMillis();
        int actualDelayed = (int) (endTime - startTime);
        assert (actualDelayed - expected < 100);
    }
}
