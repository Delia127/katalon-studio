package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.console.entity.NoArgumentConsoleOption;
import com.kms.katalon.execution.console.entity.StringConsoleOption;

public class ReportLocationSetting implements ConsoleOptionContributor {
    public static final String DEFAULT_REPORT_FILE_NAME = "report";
    public static final boolean DEFAULT_CLEAN_REPORT_FOLDER_FLAG = false;
    public static final boolean DEFAULT_REPORT_FOLDER_FLAG = false;

    public final static String REPORT_FOLDER_OPTION = "reportFolder";
    public final static String REPORT_FILE_NAME_OPTION = "reportFileName";
    public final static String CLEAN_REPORT_FOLDER = "cleanReportFolder";

    private boolean reportFolderFlag = DEFAULT_REPORT_FOLDER_FLAG;
    private String reportFolderPath;
    private String reportFileName = DEFAULT_REPORT_FILE_NAME;
    private boolean cleanReportFolderFlag = DEFAULT_CLEAN_REPORT_FOLDER_FLAG;

    public static final ConsoleOption<String> REPORT_FOLDER_CONSOLE_OPTION = new StringConsoleOption() {
        @Override
        public String getOption() {
            return REPORT_FOLDER_OPTION;
        }
    };

    public static final ConsoleOption<String> REPORT_FILE_NAME_CONSOLE_OPTION = new StringConsoleOption() {
        @Override
        public String getOption() {
            return REPORT_FILE_NAME_OPTION;
        }

        @Override
        public String getDefaultArgumentValue() {
            return DEFAULT_REPORT_FILE_NAME;
        }
    };

    public static final NoArgumentConsoleOption CLEAN_REPORT_FOLDER_CONSOLE_OPTION = new NoArgumentConsoleOption() {
        @Override
        public String getOption() {
            return CLEAN_REPORT_FOLDER;
        }
    };

    public String getReportFolderPath() {
        return reportFolderPath;
    }

    public void setReportFolderPath(String reportFolderPath) {
        this.reportFolderPath = reportFolderPath;
    }

    public String getReportFileName() {
        if (reportFileName == null) {
            reportFileName = DEFAULT_REPORT_FILE_NAME;
        }
        return reportFileName;
    }

    public void setReportFileName(String reportFileName) {
        this.reportFileName = reportFileName;
    }

    public boolean isCleanReportFolderFlagActive() {
        return cleanReportFolderFlag;
    }

    public void setCleanReportFolder(boolean cleanReportFolderFlag) {
        this.cleanReportFolderFlag = cleanReportFolderFlag;
    }

    public boolean isReportFolderPathSet() {
        return StringUtils.isNotBlank(reportFolderPath);
    }

    public boolean isReportFolderSet() {
        return reportFolderFlag;
    }

    public void enableReportFolder(boolean isEnable) {
        this.reportFolderFlag = isEnable;
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
        consoleOptionList.add(REPORT_FOLDER_CONSOLE_OPTION);
        consoleOptionList.add(REPORT_FILE_NAME_CONSOLE_OPTION);
        consoleOptionList.add(CLEAN_REPORT_FOLDER_CONSOLE_OPTION);
        return consoleOptionList;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        if (consoleOption == CLEAN_REPORT_FOLDER_CONSOLE_OPTION) {
            cleanReportFolderFlag = true;
            return;
        }
        if (StringUtils.isBlank(argumentValue)) {
            return;
        }
        if (consoleOption == REPORT_FOLDER_CONSOLE_OPTION) {
            setReportFolderPath(argumentValue);
            enableReportFolder(true);
            return;
        }
        if (consoleOption == REPORT_FILE_NAME_CONSOLE_OPTION) {
            setReportFileName(argumentValue);
        }
    }

}
