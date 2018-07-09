package com.kms.katalon.composer.webui.execution.testsuite;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.execution.collection.provider.TestExecutionConfigurationProvider;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionDriverEntry;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionGroup;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionItem;
import com.kms.katalon.composer.webui.constants.ImageConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class WebDesktopExecutionGroup implements TestExecutionGroup {

    private static final String GROUP_NAME = "Web Desktop";

    private List<TestExecutionItem> providers;

    @Override
    public String getName() {
        return GROUP_NAME;
    }

    @Override
    public String getImageUrlAsString() {
        return ImageConstants.IMG_URL_16_WEB_DESKTOP;
    }

    @Override
    public int preferredOrder() {
        return 0;
    }

    @Override
    public TestExecutionItem[] getChildren() {
        return getProviders().toArray(new TestExecutionConfigurationProvider[0]);
    }

    private List<TestExecutionItem> getProviders() {
        if (providers == null) {
            providers = new ArrayList<>();
            providers.add(TestExecutionDriverEntry.from(getName(), WebUIDriverType.CHROME_DRIVER,
                    ImageConstants.IMG_URL_16_CHROME));
            providers.add(TestExecutionDriverEntry.from(getName(), WebUIDriverType.FIREFOX_DRIVER,
                    ImageConstants.IMG_URL_16_FIREFOX));
            providers.add(TestExecutionDriverEntry.from(getName(), WebUIDriverType.IE_DRIVER, 
                    ImageConstants.IMG_URL_16_IE));
            providers.add(TestExecutionDriverEntry.from(getName(), WebUIDriverType.SAFARI_DRIVER,
                    ImageConstants.IMG_URL_16_SAFARI));
            providers.add(TestExecutionDriverEntry.from(getName(), WebUIDriverType.EDGE_DRIVER, 
                    ImageConstants.IMG_URL_16_EDGE));
            providers.add(TestExecutionDriverEntry.from(getName(), WebUIDriverType.HEADLESS_DRIVER,
                    ImageConstants.IMG_URL_16_CHROME_HEADLESS));
            providers.add(TestExecutionDriverEntry.from(getName(), WebUIDriverType.FIREFOX_HEADLESS_DRIVER,
                    ImageConstants.IMG_URL_16_FIREFOX_HEADLESS));
            providers.add(new RemoteExecutionDriverEntry(getName()));
        }
        return providers;
    }
}
