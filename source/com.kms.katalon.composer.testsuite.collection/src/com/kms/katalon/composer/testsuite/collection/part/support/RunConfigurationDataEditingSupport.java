package com.kms.katalon.composer.testsuite.collection.part.support;

import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.collection.collector.TestExecutionGroupCollector;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionConfigurationProvider;
import com.kms.katalon.composer.execution.util.MapUtil;
import com.kms.katalon.composer.testsuite.collection.part.provider.TableViewerProvider;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class RunConfigurationDataEditingSupport extends EditingSupportWithTableProvider {

    public RunConfigurationDataEditingSupport(TableViewerProvider provider) {
        super(provider);
    }

    @Override
    protected CellEditor getCellEditorByElement(TestSuiteRunConfiguration element) {
        TestExecutionConfigurationProvider executionProvider = TestExecutionGroupCollector.getInstance()
                .getExecutionProvider(element.getConfiguration());
        if (executionProvider == null) {
            return null;
        }
        return executionProvider.getRunConfigurationDataCellEditor((Composite) getViewer().getControl());
    }

    @Override
    protected boolean canEditElement(TestSuiteRunConfiguration element) {
        return true;
    }

    @Override
    protected Object getElementValue(TestSuiteRunConfiguration element) {
        return element.getConfiguration().getRunConfigurationData();
    }

    @Override
    protected void setElementValue(TestSuiteRunConfiguration element, Object value) {
        if (value == null || !(value instanceof Map<?, ?>)) {
            return;
        }
        if (ObjectUtils.equals(element.getConfiguration().getRunConfigurationData(), value)) {
            return;
        }
        Map<String, String> newValueMap = MapUtil.convertObjectToStringMap(value);
        element.getConfiguration().setRunConfigurationData(newValueMap);
        refreshElementAndMarkDirty(element);
    }

}
