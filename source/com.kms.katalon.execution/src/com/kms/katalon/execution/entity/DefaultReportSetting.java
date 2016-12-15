package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.util.MailUtil;

public class DefaultReportSetting implements Reportable, ConsoleOptionContributor {
    private ReportLocationSetting reportLocationSetting;
    private EmailConfig emailConfig;
    
    public DefaultReportSetting() {
        reportLocationSetting = new ReportLocationSetting();
        emailConfig = MailUtil.getDefaultEmailConfig();
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> consoleOptions = new ArrayList<>();
        consoleOptions.addAll(reportLocationSetting.getConsoleOptionList());
        consoleOptions.addAll(emailConfig.getConsoleOptionList());
        return consoleOptions;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        if (reportLocationSetting.getConsoleOptionList().contains(consoleOption)) {
            reportLocationSetting.setArgumentValue(consoleOption, argumentValue);
            return;
        }
        
        if (emailConfig.getConsoleOptionList().contains(consoleOption)) {
            emailConfig.setArgumentValue(consoleOption, argumentValue);;
        }
    }

    @Override
    public ReportLocationSetting getReportLocationSetting() {
        return reportLocationSetting;
    }

    @Override
    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

}
