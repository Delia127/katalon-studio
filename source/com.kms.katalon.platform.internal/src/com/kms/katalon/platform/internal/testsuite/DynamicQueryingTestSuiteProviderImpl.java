package com.kms.katalon.platform.internal.testsuite;

import java.util.List;
import java.util.stream.Collectors;

import com.katalon.platform.api.Extension;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.extension.DynamicQueryingTestSuiteDescription;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.FilteringTestSuiteEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.platform.DynamicQueryingTestSuiteExtensionProvider;
import com.kms.katalon.platform.internal.entity.ProjectEntityImpl;
import com.kms.katalon.platform.internal.entity.TestSuiteEntityImpl;

public class DynamicQueryingTestSuiteProviderImpl implements DynamicQueryingTestSuiteExtensionProvider {

    private TestCaseController testCaseController = TestCaseController.getInstance();

    public List<Extension> getAvailableExtensions(ProjectEntity project) {
        return ApplicationManager.getInstance()
                .getExtensionManager()
                .getExtensions(DynamicQueryingTestSuiteDescription.EXTENSION_ID)
                .stream()
                .filter(e -> {
                    return e.getImplementationClass() instanceof DynamicQueryingTestSuiteDescription;
                })
                .collect(Collectors.toList());
    }

    public DynamicQueryingTestSuiteDescription getSelectedDescription(ProjectEntity project,
            FilteringTestSuiteEntity testSuite) {
        List<Extension> availableExtensions = getAvailableExtensions(project);
        Extension extension = availableExtensions.stream()
                .filter(ext -> ext.getPluginId().equals(testSuite.getFilteringPlugin())
                        && ext.getExtensionId().equals(testSuite.getFilteringExtension()))
                .findFirst()
                .orElse(null);
        return extension != null ? (DynamicQueryingTestSuiteDescription) extension.getImplementationClass() : null;
    }

    @Override
    public List<TestCaseEntity> getFilteredTestCases(ProjectEntity project,
            FilteringTestSuiteEntity filteringTestSuiteEntity) throws ResourceException, ExecutionException {
        DynamicQueryingTestSuiteDescription dynamicQueryingTestSuiteDescription = getSelectedDescription(project,
                filteringTestSuiteEntity);
        if (dynamicQueryingTestSuiteDescription != null) {
            return dynamicQueryingTestSuiteDescription.query(new ProjectEntityImpl(project),
                    new TestSuiteEntityImpl(filteringTestSuiteEntity), filteringTestSuiteEntity.getFilteringText())
                    .stream()
                    .map(tc -> {
                        try {
                            return testCaseController.getTestCaseByDisplayId(tc.getId());
                        } catch (ControllerException e) {
                            return null;
                        }
                    })
                    .filter(tcSource -> tcSource != null)
                    .collect(Collectors.toList());
        }

        throw new ExecutionException("No installed plugin found: " + filteringTestSuiteEntity.getFilteringPlugin());
    }

    @Override
    public DynamicQueryingTestSuiteDescription getDynamicQueryingDescription(Extension extension) {
        return (DynamicQueryingTestSuiteDescription) extension.getImplementationClass();
    }

    @Override
    public List<TestCaseEntity> getFilteredTestCases(ProjectEntity project,
            FilteringTestSuiteEntity filteringTestSuiteEntity,
            DynamicQueryingTestSuiteDescription dynamicQueryingTestSuiteDescription, String fullSearchText)
            throws ExecutionException, ResourceException {
        return dynamicQueryingTestSuiteDescription.query(new ProjectEntityImpl(project),
                new TestSuiteEntityImpl(filteringTestSuiteEntity), fullSearchText).stream().map(tc -> {
                    try {
                        return testCaseController.getTestCaseByDisplayId(tc.getId());
                    } catch (ControllerException e) {
                        return null;
                    }
                }).filter(tcSource -> tcSource != null).collect(Collectors.toList());
    }

    @Override
    public DynamicQueryingTestSuiteDescription getSelectedDynamicQueryingDescription(ProjectEntity project,
            FilteringTestSuiteEntity filteringTestSuiteEntity) {
        return getSelectedDescription(project, filteringTestSuiteEntity);
    }

    @Override
    public Extension getSuggestedExtension(ProjectEntity project, FilteringTestSuiteEntity filteringTestSuiteEntity) {
        List<Extension> availableExtensions = getAvailableExtensions(project);
        if (availableExtensions.isEmpty()) {
            return null;
        }
        return availableExtensions.stream()
                .filter(e -> "com.katalon.katalon-studio-dynamic-execution-plugin".equals(e.getPluginId()))
                .findFirst()
                .orElse(availableExtensions.get(0));
    }
}
