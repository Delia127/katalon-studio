package com.kms.katalon.composer.testsuite.util;

import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testsuite.FilteringTestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuiteEntityUtil {
    public static void copyTestSuiteProperties(TestSuiteEntity src, TestSuiteEntity des) {
        des.setName(src.getName());
        des.setDescription(src.getDescription());
        des.setPageLoadTimeout(src.getPageLoadTimeout());
        des.setMailRecipient(src.getMailRecipient());
        des.setPageLoadTimeoutDefault(src.isPageLoadTimeoutDefault());
        des.setNumberOfRerun(src.getNumberOfRerun());
        des.setRerunFailedTestCasesOnly(src.isRerunFailedTestCasesOnly());

        des.getTestSuiteTestCaseLinks().clear();

        for (TestSuiteTestCaseLink testCaseLink : src.getTestSuiteTestCaseLinks()) {
            des.getTestSuiteTestCaseLinks().add(testCaseLink);
        }

        des.getIntegratedEntities().clear();
        for (IntegratedEntity integratedEntity : src.getIntegratedEntities()) {
            des.getIntegratedEntities().add(integratedEntity);
        }
    }

    public static void copyFilteringTestSuiteProperties(FilteringTestSuiteEntity src, FilteringTestSuiteEntity des) {
        copyTestSuiteProperties(src, des);
        des.setFilteringText(src.getFilteringText());
        des.setFilteringPlugin(src.getFilteringPlugin());
        des.setFilteringExtension(src.getFilteringExtension());
    }
}
