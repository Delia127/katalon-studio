package com.kms.katalon.composer.report.parts.integration;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.CellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.DefaultCellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;

public abstract class IntegrationTestCaseColumnLabelProvider
        extends TypeCheckedStyleCellLabelProvider<TestCaseLogRecord> {

    public IntegrationTestCaseColumnLabelProvider(int columnIndex) {
        super(columnIndex);
    }

    @Override
    protected final Class<TestCaseLogRecord> getElementType() {
        return TestCaseLogRecord.class;
    }

    @Override
    protected String getText(TestCaseLogRecord element) {
        return StringUtils.EMPTY;
    }

    @Override
    protected Image getImage(TestCaseLogRecord element) {
        return null;
    }

    @Override
    protected String getElementToolTipText(TestCaseLogRecord element) {
        return null;
    }

    @Override
    public CellLayoutInfo getCellLayoutInfo() {
        return new DefaultCellLayoutInfo() {
            @Override
            public int getLeftMargin() {
                return 5;
            }
        };
    }
}
