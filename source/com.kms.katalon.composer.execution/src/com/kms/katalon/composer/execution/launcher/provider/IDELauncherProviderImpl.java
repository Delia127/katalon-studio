package com.kms.katalon.composer.execution.launcher.provider;

import java.util.List;

import com.kms.katalon.composer.execution.handlers.UIExecutionHandler;
import com.kms.katalon.composer.execution.launcher.IDELauncher;
import com.kms.katalon.composer.execution.launcher.IDETestSuiteCollectionLauncher;
import com.kms.katalon.composer.execution.launcher.SubIDELauncher;
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
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.launcher.provider.IDELauncherProvider;

public class IDELauncherProviderImpl implements IDELauncherProvider {

    @Override
    public ReportableLauncher getTestSuiteIDELauncher(LauncherManager launcherManager, IRunConfiguration runConfig) {
        return new IDELauncher(launcherManager, runConfig, LaunchMode.RUN);
    }

    @Override
    public TestSuiteCollectionLauncher getTestSuiteCollectionIDELauncher(TestSuiteCollectionExecutedEntity executedEntity,
            LauncherManager parentManager, List<ReportableLauncher> subLaunchers, ExecutionMode executionMode,
            ReportCollectionEntity reportCollection) {
        return new IDETestSuiteCollectionLauncher(executedEntity, parentManager, subLaunchers, executionMode,
                reportCollection);
    }

    @Override
    public void launch(ILauncher launcher) throws ExecutionException {
        new UIExecutionHandler().launch(launcher);
    }

    @Override
    public ReportableLauncher getSubIDELauncher(LauncherManager manager, IRunConfiguration runConfiguration,
            RunConfigurationDescription configDescription) {
        return new SubIDELauncher(manager, runConfiguration, LaunchMode.DEBUG, configDescription);
    }
}
