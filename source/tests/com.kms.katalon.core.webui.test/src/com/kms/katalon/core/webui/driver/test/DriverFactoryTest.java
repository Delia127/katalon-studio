package com.kms.katalon.core.webui.driver.test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.Mockito;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.IDriverConfigurationProvider;

public class DriverFactoryTest {

    @Test
    public void testGetActionDelayIsDelegated() {
        IDriverConfigurationProvider provider = Mockito.mock(IDriverConfigurationProvider.class);
        DriverFactory.setDriverConfigurationProvider(provider);
        DriverFactory.getActionDelay();
        Mockito.verify(provider).getActionDelayInMilisecond();
    }

    @Test
    public void testActionDelayInSecondWouldBeConvertedToMilisecond() {
        int expected = 25;
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> executionProperties = new HashMap<>();
        HashMap<String, Object> executionGeneralProperties = new HashMap<>();
        executionGeneralProperties.put(DriverFactory.ACTION_DELAY, expected);
        executionGeneralProperties.put(DriverFactory.USE_ACTION_DELAY_IN_SECOND, TimeUnit.SECONDS);
        executionProperties.put(RunConfiguration.EXECUTION_GENERAL_PROPERTY, executionGeneralProperties);
        map.put(RunConfiguration.EXECUTION_PROPERTY, executionProperties);
        RunConfiguration.setExecutionSetting(map);

        int actual = DriverFactory.getActionDelay();

        assertThat("Action delay in second is converted to milsecond", actual == (expected * 1000));
    }

    @Test
    public void testActionDelayInMilisecondWouldStayIInMilisecond() {
        int expected = 25;
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> executionProperties = new HashMap<>();
        HashMap<String, Object> executionGeneralProperties = new HashMap<>();
        executionGeneralProperties.put(DriverFactory.ACTION_DELAY, expected);
        executionGeneralProperties.put(DriverFactory.USE_ACTION_DELAY_IN_SECOND, TimeUnit.MILLISECONDS);
        executionProperties.put(RunConfiguration.EXECUTION_GENERAL_PROPERTY, executionGeneralProperties);
        map.put(RunConfiguration.EXECUTION_PROPERTY, executionProperties);
        RunConfiguration.setExecutionSetting(map);

        int actual = DriverFactory.getActionDelay();

        assertThat("Action delay in milisecond stays in milisecond", actual == expected);
    }
}
