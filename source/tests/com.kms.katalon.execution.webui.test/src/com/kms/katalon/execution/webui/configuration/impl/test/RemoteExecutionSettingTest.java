package com.kms.katalon.execution.webui.configuration.impl.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.webui.configuration.impl.RemoteExecutionSetting;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;

public class RemoteExecutionSettingTest {

    private WebUiExecutionSettingStore store;

    @Before
    public void prepare() {
        store = Mockito.mock(WebUiExecutionSettingStore.class);
    }

    @Test
    public void testGetWebUiExecutionProperties() throws IOException {
        Mockito.when(store.getEnablePageLoadTimeout()).thenReturn(false);
        Mockito.when(store.getPageLoadTimeout()).thenReturn(24);
        Mockito.when(store.getActionDelay()).thenReturn(12);
        Mockito.when(store.getUseDelayActionTimeUnit()).thenReturn(TimeUnit.SECONDS);
        Mockito.when(store.getIgnorePageLoadTimeout()).thenReturn(true);

        RemoteExecutionSetting spiedSetting = Mockito.spy(new MyWebUiExecutionSetting(store));
        Mockito.doReturn(new HashMap<>()).when((DefaultExecutionSetting) spiedSetting).getDefaultGeneralProperties();

        assert (boolean) spiedSetting.getGeneralProperties().get(DriverFactory.ENABLE_PAGE_LOAD_TIMEOUT) == false;
        assert ((int) spiedSetting.getGeneralProperties().get(DriverFactory.ACTION_DELAY)) == 12;
        assert (int) spiedSetting.getGeneralProperties().get(DriverFactory.DEFAULT_PAGE_LOAD_TIMEOUT) == 24;
        assert (TimeUnit) spiedSetting.getGeneralProperties().get(DriverFactory.USE_ACTION_DELAY_IN_SECOND) == TimeUnit.SECONDS;
        assert (boolean) spiedSetting.getGeneralProperties()
                .get(DriverFactory.IGNORE_PAGE_LOAD_TIMEOUT_EXCEPTION) == true;
    }

    public static class MyWebUiExecutionSetting extends RemoteExecutionSetting {
        private WebUiExecutionSettingStore store;

        public MyWebUiExecutionSetting(WebUiExecutionSettingStore store) {
            this.store = store;
        }

        @Override
        public WebUiExecutionSettingStore getStore() {
            return store;
        }
    }
}
