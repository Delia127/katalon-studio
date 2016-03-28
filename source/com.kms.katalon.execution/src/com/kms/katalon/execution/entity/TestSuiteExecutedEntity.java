package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.EmailConfig;
import com.kms.katalon.execution.util.MailUtil;

public class TestSuiteExecutedEntity implements IExecutedEntity, Reportable, Rerunnable {
    private List<TestCaseExecutedEntity> testCaseExecutedEntities;
    private Map<String, TestData> testDataMap;

    private ReportLocationSetting reportLocationSetting;
    private DefaultRerunSetting rerunSetting;
    private EmailConfig emailConfig;
    
    private String testSuiteId;
    private String testSuiteName;
    private String description;

    public TestSuiteExecutedEntity(TestSuiteEntity entity) {
        testSuiteId = entity.getIdForDisplay();
        testSuiteName = entity.getName();
        description = entity.getDescription();

        emailConfig = MailUtil.getEmailConfig(entity);

        rerunSetting = new DefaultRerunSetting(0, entity.getNumberOfRerun(), entity.isRerunFailedTestCasesOnly());
    }

    public TestSuiteExecutedEntity(TestSuiteEntity entity, Rerunnable rerunnable) {
        this(entity);

        rerunSetting = new DefaultRerunSetting(rerunnable.getPreviousRerunTimes(), rerunnable.getRemainingRerunTimes(),
                rerunnable.isRerunFailedTestCasesOnly());
    }

    public String getTestSuiteId() {
        return testSuiteId;
    }

    public void setTestSuiteId(String testSuiteId) {
        this.testSuiteId = testSuiteId;
    }

    @Override
    public String getSourceName() {
        return testSuiteName;
    }

    @Override
    public String getSourceId() {
        return testSuiteId;
    }

    @Override
    public String getSourceDescription() {
        return description;
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new LinkedHashMap<String, Object>();
        attributes.put(StringConstants.ID.toLowerCase(), getSourceId());
        attributes.put(StringConstants.NAME.toLowerCase(), getSourceName());
        attributes.put(StringConstants.DESCRIPTION.toLowerCase(), getSourceDescription());
        return attributes;
    }

    public List<TestCaseExecutedEntity> getTestCaseExecutedEntities() {
        if (testCaseExecutedEntities == null) {
            testCaseExecutedEntities = new ArrayList<TestCaseExecutedEntity>();
        }
        return testCaseExecutedEntities;
    }

    public void setTestCaseExecutedEntities(List<TestCaseExecutedEntity> testCaseExecutedEntities) {
        this.testCaseExecutedEntities = testCaseExecutedEntities;
    }

    public int getTotalTestCases() {
        int total = 0;

        for (TestCaseExecutedEntity testCaseExecutionEntity : getTestCaseExecutedEntities()) {
            total += testCaseExecutionEntity.getLoopTimes();
        }
        return total;
    }

    public Map<String, TestData> getTestDataMap() {
        return testDataMap;
    }

    public void setTestDataMap(Map<String, TestData> testDataMap) {
        this.testDataMap = testDataMap;
    }

    public ReportLocationSetting getReportLocationSetting() {
        return reportLocationSetting;
    }

    public void setReportLocation(ReportLocationSetting reportLocation) {
        this.reportLocationSetting = reportLocation;
    }

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int mainTestCaseDepth() {
        return 1;
    }

    @Override
    public boolean isRerunFailedTestCasesOnly() {
        return rerunSetting.isRerunFailedTestCasesOnly();
    }

    public DefaultRerunSetting getRerunSetting() {
        return rerunSetting;
    }

    public void setRerunSetting(DefaultRerunSetting rerunSetting) {
        this.rerunSetting = rerunSetting;
    }

    @Override
    public int getPreviousRerunTimes() {
        return rerunSetting.getPreviousRerunTimes();
    }

    @Override
    public int getRemainingRerunTimes() {
        return rerunSetting.getRemainingRerunTimes();
    }

    public EmailConfig getEmailConfig() {
        return emailConfig;
    }
}
