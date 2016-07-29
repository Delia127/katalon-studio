package com.kms.katalon.composer.checkpoint.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.core.testdata.reader.CSVSeparator;
import com.kms.katalon.entity.checkpoint.CsvCheckpointSourceInfo;

public class EditCheckpointCsvSourceDialog extends EditCheckpointExcelSourceDialog {

    private static final String[] FILTER_NAMES = { "Comma Separated Values Files (*.csv)", "All Files (*.*)" };

    private static final String[] FILTER_EXTS = { "*.csv", "*.*" };

    public EditCheckpointCsvSourceDialog(Shell parentShell, CsvCheckpointSourceInfo sourceInfo) {
        super(parentShell, sourceInfo);
    }

    @Override
    protected String[] getIndicatorData() {
        return CSVSeparator.stringValues();
    }

    @Override
    protected String getIndicatorLabel() {
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
    public CsvCheckpointSourceInfo getSourceInfo() {
        return (CsvCheckpointSourceInfo) super.getSourceInfo();
    }

}
