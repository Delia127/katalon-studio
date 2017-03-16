package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;

public class DefaultReportSetting implements Reportable, ConsoleOptionContributor {

    private ReportLocationSetting reportLocationSetting;

    private EmailSettings emailSettings;

    public DefaultReportSetting() {
        reportLocationSetting = new ReportLocationSetting();
        emailSettings = new EmailSettings();
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> consoleOptions = new ArrayList<>();
        consoleOptions.addAll(reportLocationSetting.getConsoleOptionList());
        consoleOptions.addAll(emailSettings.getConsoleOptionList());
        return consoleOptions;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        if (reportLocationSetting.getConsoleOptionList().contains(consoleOption)) {
            reportLocationSetting.setArgumentValue(consoleOption, argumentValue);
            return;
        }
        if (emailSettings.getConsoleOptionList().contains(consoleOption)) {
            emailSettings.setArgumentValue(consoleOption, argumentValue);
            return;
        }
    }

    @Override
    public ReportLocationSetting getReportLocationSetting() {
        return reportLocationSetting;
    }

    @Override
    public EmailConfig getEmailConfig(ProjectEntity project) {
        return emailSettings.getEmailConfig(project);
    }

}
