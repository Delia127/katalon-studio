package com.kms.katalon.execution.entity;

import com.kms.katalon.execution.entity.EmailConfig;

public interface Reportable {

    ReportLocationSetting getReportLocationSetting();

    EmailConfig getEmailConfig();
}
