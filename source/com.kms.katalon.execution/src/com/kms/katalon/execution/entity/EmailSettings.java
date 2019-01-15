package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.console.entity.StringConsoleOption;
import com.kms.katalon.execution.util.MailUtil;

public class EmailSettings implements ConsoleOptionContributor {
    public final static String SEND_EMAIL_OPTION = "sendMail";

    private static ConsoleOption<String> sendEmailConsoleOption = new StringConsoleOption() {
        @Override
        public String getOption() {
            return SEND_EMAIL_OPTION;
        }
    };
    
    private EmailConfig emailConfig;
    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> consoleOptions = new ArrayList<>();
        consoleOptions.add(sendEmailConsoleOption);
        return consoleOptions;
    }
    
    public EmailConfig getEmailConfig(ProjectEntity project) {
        if (emailConfig == null) {
            emailConfig = MailUtil.getDefaultEmailConfig(project);
        }
        return emailConfig;
    }
    
    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

    public void setEmailConfig(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }


    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        if (consoleOption == sendEmailConsoleOption) {
            EmailConfig currentEmailConfig = getEmailConfig(ProjectController.getInstance().getCurrentProject());
            if (currentEmailConfig == null) {
                return;
            }
            currentEmailConfig.addRecipients(argumentValue);
        }
    }
}
