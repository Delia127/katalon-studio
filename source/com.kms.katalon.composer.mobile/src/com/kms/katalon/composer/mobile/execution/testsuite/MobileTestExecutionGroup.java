package com.kms.katalon.composer.mobile.execution.testsuite;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.mobile.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionConfigurationProvider;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionGroup;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionItem;

public class MobileTestExecutionGroup implements TestExecutionGroup {

    private static final String GROUP_NAME = "Mobile";

    @Override
    public String getName() {
        return GROUP_NAME;
    }

    @Override
    public String getImageUrlAsString() {
        return ImageConstants.IMG_URL_16_MOBILE;
    }

    @Override
    public int preferredOrder() {
        return 1;
    }

    @Override
    public TestExecutionItem[] getChildren() {
        return getProviders().toArray(new TestExecutionConfigurationProvider[0]);
    }

    private List<TestExecutionItem> getProviders() {
        List<TestExecutionItem> providers = new ArrayList<>();
        providers.add(new AndroidTestExecutionDriverEntry(getName()));
        providers.add(new IosTestExecutionDriverEntry(getName()));
        providers.addAll(MobileExecutionIntegrationCollector.getInstance().getSortedExecutionEntries(getName()));
        return providers;
    }

}
