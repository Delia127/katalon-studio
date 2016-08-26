package com.kms.katalon.composer.checkpoint.dialogs.wizard;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.core.testdata.reader.CSVSeparator;
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;
import com.kms.katalon.entity.checkpoint.CsvCheckpointSourceInfo;

public class NewCheckpointCsvPage extends NewCheckpointExcelPage {

    private static final String[] FILTER_NAMES = { "Comma Separated Values Files (*.csv)", "All Files (*.*)" };

    private static final String[] FILTER_EXTS = { "*.csv", "*.*" };

    public NewCheckpointCsvPage() {
        super(NewCheckpointCsvPage.class.getSimpleName());
        setTitle(StringConstants.WIZ_TITLE_CSV_DATA);
        setDescription(StringConstants.WIZ_CSV_SOURCE_CONFIGURATION);
    }

    @Override
    protected String[] getIndicatorData() {
        return CSVSeparator.stringValues();
    }

    @Override
    protected String getContentIndicatorLabel() {
        return StringConstants.DIA_LBL_SEPARATOR;
    }

    @Override
    protected String[] getFilterNames() {
        return FILTER_NAMES;
    }

    @Override
    protected String[] getFilterExtensions() {
        return FILTER_EXTS;
    }

    @Override
    public CheckpointSourceInfo getSourceInfo() {
        if (this.equals(getContainer().getCurrentPage())) {
            return new CsvCheckpointSourceInfo(getFileLocation(), getContentIndicator(), isRelativeLocation(),
                    isFirstRowHeader());
        }
        return new CsvCheckpointSourceInfo();
    }

}
