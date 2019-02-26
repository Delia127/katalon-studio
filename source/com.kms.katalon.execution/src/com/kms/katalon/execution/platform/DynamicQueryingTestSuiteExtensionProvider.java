package com.kms.katalon.execution.platform;

import java.util.List;

import com.katalon.platform.api.Extension;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.extension.DynamicQueryingTestSuiteDescription;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.FilteringTestSuiteEntity;
import com.kms.katalon.execution.exception.ExecutionException;

public interface DynamicQueryingTestSuiteExtensionProvider {

    List<Extension> getAvailableExtensions(ProjectEntity project);

    List<TestCaseEntity> getFilteredTestCases(ProjectEntity project, FilteringTestSuiteEntity filteringTestSuiteEntity)
            throws ExecutionException, ResourceException;

    List<TestCaseEntity> getFilteredTestCases(ProjectEntity project, FilteringTestSuiteEntity filteringTestSuiteEntity,
            DynamicQueryingTestSuiteDescription extensionDescription, String fullSearchText)
            throws ExecutionException, ResourceException;

    DynamicQueryingTestSuiteDescription getDynamicQueryingDescription(Extension extension);

    DynamicQueryingTestSuiteDescription getSelectedDynamicQueryingDescription(ProjectEntity project,
            FilteringTestSuiteEntity filteringTestSuiteEntity);

    Extension getSuggestedExtension(ProjectEntity project, FilteringTestSuiteEntity filteringTestSuiteEntity);
}
