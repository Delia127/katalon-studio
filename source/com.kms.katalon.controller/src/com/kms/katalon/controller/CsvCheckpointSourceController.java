package com.kms.katalon.controller;

import org.apache.commons.lang.ArrayUtils;

import com.kms.katalon.controller.constants.StringConstants;
import com.kms.katalon.core.testdata.CSVData;
import com.kms.katalon.core.testdata.reader.CSVSeparator;
import com.kms.katalon.core.util.PathUtil;
import com.kms.katalon.entity.checkpoint.CsvCheckpointSourceInfo;

public class CsvCheckpointSourceController implements CheckpointSourceController<CsvCheckpointSourceInfo> {

    private static CsvCheckpointSourceController instance;

    public static CsvCheckpointSourceController getInstance() {
        if (instance == null) {
            instance = new CsvCheckpointSourceController();
        }
        return instance;
    }

    @Override
    public CSVData getSourceData(CsvCheckpointSourceInfo sourceInfo) throws Exception {
        String separator = sourceInfo.getSheetNameOrSeparator();
        if (!ArrayUtils.contains(CSVSeparator.stringValues(), separator)) {
            throw new IllegalArgumentException(StringConstants.CTRL_EXC_INVALID_CSV_SEPARATOR);
        }
        String csvFileLocation = sourceInfo.getSourceUrl();
        if (sourceInfo.isUsingRelativePath()) {
            csvFileLocation = PathUtil.relativeToAbsolutePath(csvFileLocation, ProjectController.getInstance()
                    .getCurrentProject()
                    .getFolderLocation());
        }
        return new CSVData(csvFileLocation, sourceInfo.isUsingFirstRowAsHeader(), CSVSeparator.fromValue(separator));
    }

}
