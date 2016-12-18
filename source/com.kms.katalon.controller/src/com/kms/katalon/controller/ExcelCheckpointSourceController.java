package com.kms.katalon.controller;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.controller.constants.StringConstants;
import com.kms.katalon.core.testdata.ExcelData;
import com.kms.katalon.core.testdata.reader.ExcelFactory;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.entity.checkpoint.ExcelCheckpointSourceInfo;

public class ExcelCheckpointSourceController implements CheckpointSourceController<ExcelCheckpointSourceInfo> {

    private static ExcelCheckpointSourceController instance;

    public static ExcelCheckpointSourceController getInstance() {
        if (instance == null) {
            instance = new ExcelCheckpointSourceController();
        }
        return instance;
    }

    @Override
    public ExcelData getSourceData(ExcelCheckpointSourceInfo sourceInfo) throws Exception {
        String excelFileLocation = sourceInfo.getSourceUrl();
        // sheet name can be a blank character but empty
        if (StringUtils.isEmpty(excelFileLocation)) {
            throw new IllegalArgumentException(StringConstants.CTRL_EXC_EXCEL_SHEET_NAME_IS_EMPTY);
        }
        if (sourceInfo.isUsingRelativePath()) {
            excelFileLocation = PathUtil.relativeToAbsolutePath(excelFileLocation, ProjectController.getInstance()
                    .getCurrentProject()
                    .getFolderLocation());
        }
        return ExcelFactory.getExcelDataWithDefaultSheet(excelFileLocation, sourceInfo.getSheetNameOrSeparator(),
                sourceInfo.isUsingFirstRowAsHeader());
    }

}
