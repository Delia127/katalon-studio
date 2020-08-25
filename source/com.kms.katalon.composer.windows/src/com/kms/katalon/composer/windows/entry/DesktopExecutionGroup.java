package com.kms.katalon.composer.windows.entry;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.core.windows.driver.WindowsDriverType;
import com.kms.katalon.execution.collection.provider.TestExecutionConfigurationProvider;
import com.kms.katalon.execution.collection.provider.TestExecutionDriverEntry;
import com.kms.katalon.execution.collection.provider.TestExecutionGroup;
import com.kms.katalon.execution.collection.provider.TestExecutionItem;

public class DesktopExecutionGroup implements TestExecutionGroup {

    private List<TestExecutionItem> providers;

    @Override
    public String getName() {
        return "Desktop";
    }

    @Override
    public String getImageUrlAsString() {
        return ImageManager.getImageURLString(IImageKeys.DESKTOP_16);
    }

    @Override
    public int preferredOrder() {
        return 4;
    }

    @Override
    public TestExecutionItem[] getChildren() {
        return getProviders().toArray(new TestExecutionConfigurationProvider[0]);
    }

    private List<TestExecutionItem> getProviders() {
        if (providers == null) {
            providers = new ArrayList<>();
            providers.add(TestExecutionDriverEntry.from(getName(), WindowsDriverType.getInstance(),
                    ImageManager.getImageURLString(IImageKeys.WINDOWS_ENTITY_16)));
        }
        return providers;
    }
}
