package com.kms.katalon.composer.testsuite.collection.part.provider;

import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.TypeCheckStyleCellTableLabelProvider;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ImageUtil;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testsuite.collection.constant.ComposerTestsuiteCollectionMessageConstants;
import com.kms.katalon.composer.testsuite.collection.constant.ImageConstants;
import com.kms.katalon.composer.testsuite.collection.execution.collector.TestExecutionGroupCollector;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionConfigurationProvider;
import com.kms.katalon.composer.testsuite.collection.util.MapUtil;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class TestSuiteRunConfigLabelProvider extends TypeCheckStyleCellTableLabelProvider<TestSuiteRunConfiguration> {

    public static final int NO_COLUMN_IDX = 0;

    public static final int ID_COLUMN_IDX = 1;

    public static final int RUN_WITH_COLUMN_IDX = 2;

    public static final int RUN_WITH_DATA_COLUMN_IDX = 3;

    public static final int RUN_COLUMN_IDX = 4;

    private static final Image IMG_16_WARN_TABLE_ITEM = ImageManager.getImage(IImageKeys.WARNING_16);

    private TableViewerProvider provider;

    public TestSuiteRunConfigLabelProvider(TableViewerProvider provider, int columnIndex) {
        super(columnIndex);
        this.provider = provider;
    }

    @Override
    protected Class<TestSuiteRunConfiguration> getElementType() {
        return TestSuiteRunConfiguration.class;
    }

    @Override
    protected Image getImage(TestSuiteRunConfiguration element) {
        switch (columnIndex) {
            case RUN_COLUMN_IDX:
                return element.isRunEnabled() ? ImageConstants.IMG_16_CHECKED : ImageConstants.IMG_16_UNCHECKED;
            case RUN_WITH_COLUMN_IDX:
                return getImageForRunConfigurationColumn(element);
            default:
                return null;
        }
    }

    private TestExecutionConfigurationProvider getOriginalConfigProvider(TestSuiteRunConfiguration runConfiguration) {
        if (runConfiguration == null) {
            return null;
        }
        return TestExecutionGroupCollector.getInstance().getExecutionProvider(runConfiguration.getConfiguration());
    }

    private Image getImageForRunConfigurationColumn(TestSuiteRunConfiguration element) {
        TestExecutionConfigurationProvider executionProvider = getOriginalConfigProvider(element);
        try {
            return executionProvider != null ? ImageUtil.loadImage(executionProvider.getImageUrlAsString())
                    : IMG_16_WARN_TABLE_ITEM;
        } catch (MalformedURLException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    @Override
    protected String getText(TestSuiteRunConfiguration element) {
        switch (columnIndex) {
            case NO_COLUMN_IDX:
                return Integer.toString(provider.getTableItems().indexOf(element) + 1);
            case ID_COLUMN_IDX:
                return element.getTestSuiteEntity() != null ? element.getTestSuiteEntity().getIdForDisplay()
                        : StringUtils.EMPTY;
            case RUN_WITH_DATA_COLUMN_IDX:
                return MapUtil.buildStringForMap(element.getConfiguration().getRunConfigurationData());
            case RUN_WITH_COLUMN_IDX:
                RunConfigurationDescription configuration = element.getConfiguration();
                return configuration != null ? configuration.getRunConfigurationId() : StringUtils.EMPTY;
            case RUN_COLUMN_IDX:
            default:
                return StringUtils.EMPTY;
        }
    }

    @Override
    protected String getElementToolTipText(TestSuiteRunConfiguration element) {
        TestExecutionConfigurationProvider executionProvider = getOriginalConfigProvider(element);
        return executionProvider != null ? StringUtils.defaultIfEmpty(getText(element), null)
                : ComposerTestsuiteCollectionMessageConstants.ERR_TOOLTIP_UNABLE_TO_LOCATE_RUN_CONFIG;
    }
}
