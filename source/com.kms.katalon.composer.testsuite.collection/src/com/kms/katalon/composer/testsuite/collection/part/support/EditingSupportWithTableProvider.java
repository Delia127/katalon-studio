package com.kms.katalon.composer.testsuite.collection.part.support;

import com.kms.katalon.composer.components.impl.support.TypeCheckedEditingSupport;
import com.kms.katalon.composer.testsuite.collection.part.provider.TableViewerProvider;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public abstract class EditingSupportWithTableProvider extends TypeCheckedEditingSupport<TestSuiteRunConfiguration> {
    protected TableViewerProvider provider;

    protected EditingSupportWithTableProvider(TableViewerProvider provider) {
        super(provider.getTableViewer());
        this.provider = provider;
    }

    @Override
    protected Class<TestSuiteRunConfiguration> getElementType() {
        return TestSuiteRunConfiguration.class;
    }

    protected void refreshElementAndMarkDirty(TestSuiteRunConfiguration element) {
        getViewer().refresh(element);
        provider.markDirty();
    }
}
