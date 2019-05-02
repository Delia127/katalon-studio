package com.kms.katalon.execution.launcher.provider;

import java.util.List;

import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity.ExecutionMode;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestSuiteCollectionExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.ReportableLauncher;
import com.kms.katalon.execution.launcher.TestSuiteCollectionLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public interface IDELauncherProvider {

    void launch(ILauncher launcher) throws ExecutionException;

    ReportableLauncher getTestSuiteIDELauncher(LauncherManager manager, IRunConfiguration runConfiguration);

    ReportableLauncher getSubIDELauncher(LauncherManager manager, IRunConfiguration runConfiguration,
            RunConfigurationDescription configDescription);

    TestSuiteCollectionLauncher getTestSuiteCollectionIDELauncher(TestSuiteCollectionExecutedEntity executedEntity,
            LauncherManager parentManager, List<ReportableLauncher> subLaunchers, ExecutionMode executionMode,
            ReportCollectionEntity reportCollection);
}
