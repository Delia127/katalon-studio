package com.kms.katalon.execution.webui.configuration.impl.test;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.webui.configuration.impl.WebUIExecutionSetting;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;

public class WebUiExecutionSettingTest {

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
        Mockito.when(store.getUseDelayActionInSecond()).thenReturn(false);
        Mockito.when(store.getIgnorePageLoadTimeout()).thenReturn(true);
        Mockito.when(store.getImageRecognitionEnabled()).thenReturn(true);

        WebUIExecutionSetting spiedSetting = Mockito.spy(new MyWebUiExecutionSetting(store));
        Mockito.doReturn(new HashMap<>()).when((DefaultExecutionSetting) spiedSetting).getDefaultGeneralProperties();

        assert (boolean) spiedSetting.getGeneralProperties().get(DriverFactory.ENABLE_PAGE_LOAD_TIMEOUT) == false;
        assert ((int) spiedSetting.getGeneralProperties().get(DriverFactory.ACTION_DELAY)) == 12;
        assert (int) spiedSetting.getGeneralProperties().get(DriverFactory.DEFAULT_PAGE_LOAD_TIMEOUT) == 24;
        assert (boolean) spiedSetting.getGeneralProperties().get(DriverFactory.USE_ACTION_DELAY_IN_SECOND) == false;
        assert (boolean) spiedSetting.getGeneralProperties()
                .get(DriverFactory.IGNORE_PAGE_LOAD_TIMEOUT_EXCEPTION) == true;
        assert (boolean) spiedSetting.getGeneralProperties().get(RunConfiguration.IMAGE_RECOGNITION_ENABLED) == true;
    }

    public static class MyWebUiExecutionSetting extends WebUIExecutionSetting {
        private WebUiExecutionSettingStore store;

        public MyWebUiExecutionSetting(WebUiExecutionSettingStore store) {
            this.store = store;
        }

        @Override
        public WebUiExecutionSettingStore getWebUiStore() {
            return store;
        }
    }
}
