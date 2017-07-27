package com.kms.katalon.integration.jira;

import java.util.Optional;

import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.integration.jira.constant.StringConstants;
import com.kms.katalon.integration.jira.entity.JiraIntegratedIssue;
import com.kms.katalon.integration.jira.entity.JiraIntegratedObject;
import com.kms.katalon.integration.jira.entity.JiraIssue;
import com.kms.katalon.integration.jira.entity.JiraIssueCollection;
import com.kms.katalon.integration.jira.entity.JiraReport;

public class JiraObjectToEntityConverter {
    private static <T extends JiraIntegratedObject> Optional<T> getJiraObject(IntegratedFileEntity entity,
            Class<T> clazz) {
        IntegratedEntity integratedEntity = entity.getIntegratedEntity(StringConstants.JIRA_NAME);
        if (integratedEntity == null) {
            return Optional.empty();
        }
        return Optional.of((T) JsonUtil
                .fromJson(integratedEntity.getProperties().get(StringConstants.INTEGRATED_VALUE_NAME), clazz));
    }

    public static Optional<JiraReport> getOptionalJiraReport(ReportEntity report) {
        return getJiraObject(report, JiraReport.class);
    }

    public static JiraReport getJiraReport(ReportEntity report) {
        return getJiraObject(report, JiraReport.class).map(jiraReport -> jiraReport).orElse(new JiraReport());
    }

    public static JiraIssue getJiraIssue(TestCaseEntity testCase) {
        return getJiraObject(testCase, JiraIntegratedIssue.class).map(integrated -> integrated.getJiraIssue())
                .orElse(null);
    }

    public static TestCaseEntity updateTestCase(JiraIssue issue, TestCaseEntity testCase)
            throws JiraIntegrationException {
        IntegratedEntity jiraIntegratedEntity = testCase.getIntegratedEntity(StringConstants.JIRA_NAME);
        if (jiraIntegratedEntity == null) {
            jiraIntegratedEntity = new IntegratedEntity();
            jiraIntegratedEntity.setProductName(StringConstants.JIRA_NAME);
        }
        jiraIntegratedEntity.setType(IntegratedType.REPORT);
        jiraIntegratedEntity.setProperties(new JiraIntegratedIssue(issue).getIntegratedValue());
        int index = testCase.getIntegratedEntities().indexOf(jiraIntegratedEntity);
        if (index >= 0) {
            testCase.getIntegratedEntities().remove(index);
        }
        testCase.getIntegratedEntities().add(Math.max(0, testCase.getIntegratedEntities().size() - 1),
                jiraIntegratedEntity);
        return testCase;
    }

    public static Optional<JiraIssueCollection> getOptionalJiraIssueCollection(ReportEntity report,
            int testCaseLogIndex) {
        return getOptionalJiraReport(report)
                .map(jiraReport -> Optional.ofNullable(jiraReport.getIssueCollectionMap().get(testCaseLogIndex)))
                .orElse(Optional.empty());
    }

    public static void updateJiraReport(JiraReport jiraReport, ReportEntity report) throws JiraIntegrationException {
        IntegratedEntity jiraIntegratedEntity = report.getIntegratedEntity(StringConstants.JIRA_NAME);
        if (jiraIntegratedEntity == null) {
            jiraIntegratedEntity = new IntegratedEntity();
            jiraIntegratedEntity.setProductName(StringConstants.JIRA_NAME);
        }
        jiraIntegratedEntity.setType(IntegratedType.REPORT);
        jiraIntegratedEntity.setProperties(jiraReport.getIntegratedValue());
        int index = report.getIntegratedEntities().indexOf(jiraIntegratedEntity);
        if (index >= 0) {
            report.getIntegratedEntities().remove(index);
        }
        report.getIntegratedEntities().add(Math.max(0, report.getIntegratedEntities().size() - 1),
                jiraIntegratedEntity);
        try {
            ReportController.getInstance().updateReport(report);
        } catch (Exception e) {
            throw new JiraIntegrationException(e);
        }
    }
}
