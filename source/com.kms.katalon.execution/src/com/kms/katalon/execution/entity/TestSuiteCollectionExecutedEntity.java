package com.kms.katalon.execution.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class TestSuiteCollectionExecutedEntity extends ExecutedEntity implements Reportable {

    private List<IExecutedEntity> executedItems;

    private TestSuiteCollectionEntity testSuiteCollectionEntity;

    private Reportable reportable;

    private Rerunable rerunable;

    public TestSuiteCollectionExecutedEntity(TestSuiteCollectionEntity entity) {
        super(entity);
        executedItems = new ArrayList<>();
        testSuiteCollectionEntity = entity;
    }

    public void addTestSuiteExecutedEntity(TestSuiteExecutedEntity testSuiteExecuted) {
        executedItems.add(testSuiteExecuted);
    }

    @Override
    public List<IExecutedEntity> getExecutedItems() {
        return executedItems;
    }

    @Override
    public int mainTestCaseDepth() {
        return 2;
    }

    public TestSuiteCollectionEntity getEntity() {
        return testSuiteCollectionEntity;
    }

    @Override
    public int getTotalTestCases() {
        int totalTestCases = 0;
        for (IExecutedEntity childItem : getExecutedItems()) {
            totalTestCases += childItem.getTotalTestCases();
        }
        return totalTestCases;
    }

    public void setRerunable(Rerunable rerunable) {
        this.rerunable = rerunable;
    }

    public Rerunable getRunnable() {
        return rerunable;
    }

    public void setReportable(Reportable reportable) {
        this.reportable = reportable;
    }

    @Override
    public ReportLocationSetting getReportLocationSetting() {
        return reportable.getReportLocationSetting();
    }

    public ReportLocationSetting getReportLocationForChildren(String childPath) {
        ReportLocationSetting childReportLocation = new ReportLocationSetting();
        ReportLocationSetting reportLocationSetting = reportable.getReportLocationSetting();
        childReportLocation.setReportFileName(reportLocationSetting.getReportFileName());
        childReportLocation.enableReportFolder(reportLocationSetting.isReportFolderSet());
        childReportLocation.setCleanReportFolder(reportLocationSetting.isCleanReportFolderFlagActive());
        if (reportLocationSetting.isReportFolderPathSet()) {
            childReportLocation.setReportFolderPath(reportLocationSetting.getReportFolderPath() + File.separator + childPath);
        }
        return childReportLocation;
    }

    @Override
    public EmailConfig getEmailConfig(ProjectEntity project) {
        return reportable.getEmailConfig(project);
    }
}
