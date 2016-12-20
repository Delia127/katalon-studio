package com.kms.katalon.execution.launcher;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.entity.DefaultRerunSetting;
import com.kms.katalon.execution.entity.Reportable;
import com.kms.katalon.execution.entity.Rerunable;
import com.kms.katalon.execution.entity.TestSuiteCollectionExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.logging.LogUtil;

public class TestSuiteCollectionConsoleLauncher extends TestSuiteCollectionLauncher implements IConsoleLauncher {

    public TestSuiteCollectionConsoleLauncher(TestSuiteCollectionExecutedEntity executedEntity,
            LauncherManager parentManager, List<? extends ReportableLauncher> subLaunchers) {
        super(executedEntity, parentManager, subLaunchers, executedEntity.getEntity().getExecutionMode());
    }

    public static TestSuiteCollectionConsoleLauncher newInstance(TestSuiteCollectionEntity testSuiteCollection,
            LauncherManager parentManager, Reportable reportable, Rerunable rerunable) throws ExecutionException {
        TestSuiteCollectionExecutedEntity executedEntity = new TestSuiteCollectionExecutedEntity(
                testSuiteCollection);
        executedEntity.setReportable(reportable);
        executedEntity.setRerunable(rerunable);
        return new TestSuiteCollectionConsoleLauncher(executedEntity,
                parentManager, buildSubLaunchers(testSuiteCollection, executedEntity, parentManager));
    }

    private static List<ConsoleLauncher> buildSubLaunchers(TestSuiteCollectionEntity testSuiteCollection,
            TestSuiteCollectionExecutedEntity executedEntity, LauncherManager launcherManager)
            throws ExecutionException {
        List<ConsoleLauncher> tsLaunchers = new ArrayList<>();
        for (TestSuiteRunConfiguration tsRunConfig : testSuiteCollection.getTestSuiteRunConfigurations()) {
            if (!tsRunConfig.isRunEnabled()) {
                continue;
            }
            ConsoleLauncher subLauncher = buildLauncher(tsRunConfig, launcherManager);
            final TestSuiteExecutedEntity tsExecutedEntity = (TestSuiteExecutedEntity) subLauncher.getRunConfig()
                    .getExecutionSetting()
                    .getExecutedEntity();
            tsExecutedEntity.setRerunSetting((DefaultRerunSetting) executedEntity.getRunnable());
            tsExecutedEntity.setReportLocation(executedEntity.getReportLocationForChildren(subLauncher.getId()));
            tsExecutedEntity.setEmailConfig(executedEntity.getEmailConfig());
            if (tsExecutedEntity.getTotalTestCases() == 0) {
                throw new ExecutionException(ExecutionMessageConstants.LAU_MESSAGE_EMPTY_TEST_SUITE);
            }
            executedEntity.addTestSuiteExecutedEntity(tsExecutedEntity);
            tsLaunchers.add(subLauncher);
        }
        return tsLaunchers;
    }

    private static ConsoleLauncher buildLauncher(final TestSuiteRunConfiguration tsRunConfig, LauncherManager launcherManager)
            throws ExecutionException {
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        try {
            RunConfigurationDescription configDescription = tsRunConfig.getConfiguration();
            IRunConfiguration runConfig = RunConfigurationCollector.getInstance().getRunConfiguration(
                    configDescription.getRunConfigurationId(), projectDir, configDescription);
            TestSuiteEntity testSuiteEntity = tsRunConfig.getTestSuiteEntity();
            runConfig.build(testSuiteEntity, new TestSuiteExecutedEntity(testSuiteEntity));
            return new SubConsoleLauncher(launcherManager, runConfig);
        } catch (final Exception e) {
            LogUtil.logError(e);
            throw new ExecutionException(
                    MessageFormat.format(ExecutionMessageConstants.LAU_MESSAGE_UNABLE_TO_EXECUTE_TEST_SUITE,
                            tsRunConfig.getTestSuiteEntity().getIdForDisplay()));
        }
    }

    @Override
    public String getStatusMessage(int consoleWidth) {
        return new StringBuilder().append(getDefaultStatusMessage(consoleWidth))
                .append(GlobalStringConstants.CR_EOL)
                .append(StringUtils.repeat(GlobalStringConstants.CR_HYPHEN, consoleWidth))
                .append(GlobalStringConstants.CR_EOL)
                .append(subLauncherManager.getChildrenLauncherStatus(consoleWidth))
                .toString();
    }
}
