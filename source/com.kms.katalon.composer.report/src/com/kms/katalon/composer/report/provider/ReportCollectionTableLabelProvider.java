package com.kms.katalon.composer.report.provider;

import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.CellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.DefaultCellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ImageUtil;
import com.kms.katalon.composer.execution.collection.collector.TestExecutionGroupCollector;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionConfigurationProvider;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.composer.testsuite.collection.constant.ImageConstants;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportItemDescription;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class ReportCollectionTableLabelProvider extends TypeCheckedStyleCellLabelProvider<ReportItemDescription>
        implements ReportItemDescriptionLabelProvider {

    public static final int CLM_NO_IDX = 0;

    public static final int CLM_ID_IDX = 1;

    public static final int CLM_EVN_IDX = 2;
    
    public static final int CLM_PROFILE_IDX = 3;

    public static final int CLM_STATUS_IDX = 4;

    public static final int CLM_FAILED_TESTS_IDX = 5;

    public static final int CLM_ACTION_IDX = 6;
    
    private static final int DF_TABLE_CELL_MARGIN = 5;

    public ReportCollectionTableLabelProvider(int columnIndex) {
        super(columnIndex);
    }

    @Override
    protected Class<ReportItemDescription> getElementType() {
        return ReportItemDescription.class;
    }

    @Override
    protected Image getImage(ReportItemDescription element) {
        switch (columnIndex) {
            case CLM_EVN_IDX:
                return getImageForRunConfigurationColumn(element.getRunConfigDescription());
            case CLM_PROFILE_IDX:
                return ImageConstants.IMG_16_PROFILE;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String getText(ReportItemDescription element) {
        List<ReportItemDescription> input = (List<ReportItemDescription>) getViewer().getInput();
        switch (columnIndex) {
            case CLM_NO_IDX: {
                return Integer.toString(input.indexOf(element) + 1);
            }
            case CLM_ID_IDX: {
                TestSuiteEntity testSuite = getTestSuite(element);
                return testSuite != null ? testSuite.getIdForDisplay() : StringUtils.EMPTY;
            }
            case CLM_EVN_IDX: {
                return element.getRunConfigDescription().getRunConfigurationId();
            }
            case CLM_PROFILE_IDX: {
                return element.getRunConfigDescription().getProfileName();
            }
            
            case CLM_STATUS_IDX: {
                TestSuiteLogRecord logRecord = getTestSuiteLogRecord(element.getReportLocation());
                if (logRecord == null) {
                    return StringConstants.NOT_STARTED;
                }
                if (logRecord.getTotalIncompleteTestCases() > 0) {
                    return StringConstants.INCOMPLETE;
                }
                return StringConstants.COMPLETE;
            }

            case CLM_FAILED_TESTS_IDX: {
                TestSuiteLogRecord logRecord = getTestSuiteLogRecord(element.getReportLocation());
                if (logRecord == null) {
                    return StringUtils.EMPTY;
                }
                int numPassed = logRecord.getTotalFailedTestCases() + logRecord.getTotalErrorTestCases();
                return Integer.toString(numPassed) + "/" + logRecord.getTotalTestCases();
            }
        }
        return StringUtils.EMPTY;
    }

    private Image getImageForRunConfigurationColumn(RunConfigurationDescription configuration) {
        TestExecutionConfigurationProvider executionProvider = TestExecutionGroupCollector.getInstance()
                .getExecutionProvider(configuration);
        try {
            return executionProvider != null ? ImageUtil.loadImage(executionProvider.getImageUrlAsString()) : null;
        } catch (MalformedURLException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    @Override
    protected Color getBackground(Color background, ReportItemDescription element) {

        TestSuiteLogRecord logRecord = getTestSuiteLogRecord(element.getReportLocation());

        switch (columnIndex) {
            case CLM_STATUS_IDX: {
                return getBackgroundForStatusColumn(background, logRecord);
            }
            case CLM_FAILED_TESTS_IDX: {
                return getBackgroundForFailedTestsColumn(background, logRecord);
            }
            default: {
                return super.getBackground(background, element);
            }
        }
    }

    private Color getBackgroundForFailedTestsColumn(Color background, TestSuiteLogRecord logRecord) {
        if (logRecord == null) {
            return background;
        }

        if (logRecord.getTotalFailedTestCases() + logRecord.getTotalErrorTestCases() > 0) {
            return ColorUtil.getFailedStatusBackgroundColor();
        }
        return background;
    }

    private Color getBackgroundForStatusColumn(Color background, TestSuiteLogRecord logRecord) {
        if (logRecord == null) {
            return ColorUtil.getDisabledItemBackgroundColor();
        }

        if (logRecord.getTotalIncompleteTestCases() > 0) {
            return ColorUtil.getIncompleteStatusBackgroundColor();
        }

        return background;
    }
    
    @Override
    protected String getElementToolTipText(ReportItemDescription element) {
        return StringUtils.defaultIfEmpty(getText(element), null);
    }
    
    @Override
    public CellLayoutInfo getCellLayoutInfo() {
        return new DefaultCellLayoutInfo() {
            @Override
            public int getLeftMargin() {
                return DF_TABLE_CELL_MARGIN;
            }
        };
    }
}
