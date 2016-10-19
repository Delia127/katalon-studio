package com.kms.katalon.composer.testsuite.collection.execution.provider;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.testsuite.collection.constant.ImageConstants;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionConfigurationProvider;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionGroup;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionItem;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.CustomRunConfigurationContributor;

public class CustomTestExecutionGroup implements TestExecutionGroup {

    private static final String IMG_URL16_CUSTOM = ImageConstants.IMG_URL_16_CUSTOM;

    private static final String GROUP_NAME = "Custom";

    @Override
    public String getName() {
        return GROUP_NAME;
    }

    @Override
    public String getImageUrlAsString() {
        return IMG_URL16_CUSTOM;
    }

    @Override
    public int preferredOrder() {
        return 3;
    }

    @Override
    public TestExecutionItem[] getChildren() {
        return getProviders().toArray(new TestExecutionConfigurationProvider[0]);
    }

    private List<TestExecutionItem> getProviders() {
        List<TestExecutionItem> providers = new ArrayList<>();
        for (CustomRunConfigurationContributor customRunConfigContributor : RunConfigurationCollector.getInstance()
                .getAllCustomRunConfigurationContributors()) {
            providers.add(CustomTestExecutionEntry.from(GROUP_NAME, customRunConfigContributor.getId(),
                    IMG_URL16_CUSTOM));
        }
        return providers;
    }

}
