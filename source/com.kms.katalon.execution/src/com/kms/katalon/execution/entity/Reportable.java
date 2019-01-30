package com.kms.katalon.execution.entity;

import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.entity.EmailConfig;

public interface Reportable {

    ReportLocationSetting getReportLocationSetting();

    EmailConfig getEmailConfig(ProjectEntity project);
    
   // EmailConfig setEmailConfig(EmailConfig emailConfig);
}
