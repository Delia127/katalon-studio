package com.kms.katalon.execution.launcher;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportItemDescription;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity.ExecutionMode;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.entity.DefaultReportSetting;
import com.kms.katalon.execution.entity.DefaultRerunSetting;
import com.kms.katalon.execution.entity.Reportable;
import com.kms.katalon.execution.entity.Rerunable;
import com.kms.katalon.execution.entity.TestSuiteCollectionExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.service.Trackings;

public class TestSuiteCollectionConsoleLauncher extends TestSuiteCollectionLauncher implements IConsoleLauncher {

    public TestSuiteCollectionConsoleLauncher(TestSuiteCollectionExecutedEntity executedEntity,
            LauncherManager parentManager, List<ReportableLauncher> subLaunchers,
            ReportCollectionEntity reportCollection, String executionUUID) {
        super(executedEntity, parentManager, subLaunchers, executedEntity.getEntity().getExecutionMode(),
                reportCollection, executionUUID);
    }

    public static TestSuiteCollectionConsoleLauncher newInstance(TestSuiteCollectionEntity testSuiteCollection,
            LauncherManager parentManager, Reportable reportable, Rerunable rerunable,
            Map<String, Object> globalVariables, String executionUUID, Map<String, String> additionalInfo) throws ExecutionException {
        TestSuiteCollectionExecutedEntity executedEntity = new TestSuiteCollectionExecutedEntity(testSuiteCollection);
        executedEntity.setReportable(reportable);
        if (rerunable != null) {
            executedEntity.setRerunable(rerunable.mergeWith(executedEntity.getRunnable()));
        }
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String executionSessionId =  dateFormat.format(new Date());
            
            ReportCollectionEntity reportCollection = ReportController.getInstance()
                    .newReportCollection(testSuiteCollection.getProject(), testSuiteCollection, executionSessionId, executedEntity.getId());

            TestSuiteCollectionConsoleLauncher testSuiteCollectionConsoleLauncher = new TestSuiteCollectionConsoleLauncher(
                    executedEntity, parentManager, buildSubLaunchers(testSuiteCollection, executedEntity, parentManager,
                            reportCollection, globalVariables, executionUUID, executionSessionId, additionalInfo, true),
                    reportCollection, executionUUID);

            ReportController.getInstance().updateReportCollection(reportCollection);
            return testSuiteCollectionConsoleLauncher;
        } catch (DALException e) {
            throw new ExecutionException(e);
        }
    }

    public static TestSuiteCollectionLauncher newIDEInstance(TestSuiteCollectionEntity testSuiteCollection,
            LauncherManager parentManager, DefaultReportSetting reportable, DefaultRerunSetting rerunable,
            Map<String, Object> globalVariables, String executionUUID, Map<String, String> additionalInfo) throws ExecutionException {
        TestSuiteCollectionExecutedEntity executedEntity = new TestSuiteCollectionExecutedEntity(testSuiteCollection);
        executedEntity.setReportable(reportable);
        executedEntity.setRerunable(rerunable);

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String executionSessionId =  dateFormat.format(new Date());
            
            ReportCollectionEntity reportCollection = ReportController.getInstance()
                    .newReportCollection(testSuiteCollection.getProject(), testSuiteCollection, executionSessionId, executedEntity.getId());

            List<ReportableLauncher> subLaunchers = buildSubLaunchers(testSuiteCollection, executedEntity,
                    parentManager, reportCollection, globalVariables, executionUUID, executionSessionId, additionalInfo, false);
            TestSuiteCollectionLauncher testSuiteCollectionLauncher = LauncherProviderFactory.getInstance()
                    .getIdeLauncherProvider()
                    .getTestSuiteCollectionIDELauncher(executedEntity, parentManager, subLaunchers,
                            executedEntity.getEntity().getExecutionMode(), reportCollection);

            ReportController.getInstance().updateReportCollection(reportCollection);
            return testSuiteCollectionLauncher;
        } catch (DALException e) {
            throw new ExecutionException(e);
        }
    }

    private static List<ReportableLauncher> buildSubLaunchers(TestSuiteCollectionEntity testSuiteCollection,
            TestSuiteCollectionExecutedEntity executedEntity, LauncherManager launcherManager,
            ReportCollectionEntity reportCollection, Map<String, Object> globalVariables, String executionUUID,
            String executionSessionId, Map<String, String> additionalInfo, boolean isConsole) throws ExecutionException {
        List<ReportableLauncher> tsLaunchers = new ArrayList<>();
        
        for (TestSuiteRunConfiguration tsRunConfig : testSuiteCollection.getTestSuiteRunConfigurations()) {
            if (!tsRunConfig.isRunEnabled()) {
                continue;
            }
            ReportableLauncher subLauncher = buildLauncher(tsRunConfig, launcherManager, reportCollection,
                    globalVariables, executionUUID, isConsole, executionSessionId, additionalInfo);
            final TestSuiteExecutedEntity tsExecutedEntity = (TestSuiteExecutedEntity) subLauncher.getRunConfig()
                    .getExecutionSetting()
                    .getExecutedEntity();
            tsExecutedEntity.setRerunSetting(
                    (DefaultRerunSetting) executedEntity.getRunnable().mergeWith(tsExecutedEntity.getRerunSetting()));
            tsExecutedEntity.setReportLocation(executedEntity.getReportLocationForChildren(subLauncher.getId()));
            tsExecutedEntity.setEmailConfig(executedEntity.getEmailConfig(testSuiteCollection.getProject()));
            if (tsExecutedEntity.getTotalTestCases() == 0) {
                throw new ExecutionException(ExecutionMessageConstants.LAU_MESSAGE_EMPTY_TEST_SUITE);
            }
            executedEntity.addTestSuiteExecutedEntity(tsExecutedEntity);
            tsLaunchers.add(subLauncher);
        }
        return tsLaunchers;
    }

    private static ReportableLauncher buildLauncher(final TestSuiteRunConfiguration tsRunConfig,
            LauncherManager launcherManager, ReportCollectionEntity reportCollection,
            Map<String, Object> globalVariables, String executionUUID, boolean isConsole,
            String executionSessionId, Map<String, String> additionalInfo) throws ExecutionException {
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        try {
            RunConfigurationDescription configDescription = tsRunConfig.getConfiguration();
            IRunConfiguration runConfig = RunConfigurationCollector.getInstance()
                    .getRunConfiguration(configDescription.getRunConfigurationId(), projectDir, configDescription);
            TestSuiteEntity testSuiteEntity = tsRunConfig.getTestSuiteEntity();
            TestSuiteExecutedEntity executedEntity = new TestSuiteExecutedEntity(testSuiteEntity);
            executedEntity.prepareTestCases();
            runConfig.setOverridingGlobalVariables(globalVariables);
            runConfig.setExecutionUUID(executionUUID);
            runConfig.setExecutionSessionId(executionSessionId);
            runConfig.setAdditionalInfo(additionalInfo);
            runConfig.build(testSuiteEntity, executedEntity);
            ReportableLauncher launcher = null;
            if (isConsole) {
                launcher = new SubConsoleLauncher(launcherManager, runConfig, configDescription);
            } else {
                launcher = LauncherProviderFactory.getInstance()
                        .getIdeLauncherProvider()
                        .getSubIDELauncher(launcherManager, runConfig, configDescription);
            }
            reportCollection.getReportItemDescriptions()
                    .add(ReportItemDescription.from(launcher.getReportEntity().getIdForDisplay(), configDescription));
            return launcher;
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
    
    @Override
    protected void postExecution() {
        super.postExecution();

        Date startTime = getStartTime() != null ? getStartTime() : new Date();
        Date endTime = getEndTime() != null ? getEndTime() : new Date();
        String executionResult = getExecutionResult();
        ExecutionMode executionMode = getExecutedEntity().getEntity().getExecutionMode();
        if (executionMode == ExecutionMode.PARALLEL) {
            int maxConcurrentInstances = getExecutedEntity().getEntity().getMaxConcurrentInstances();
            Trackings.trackExecuteParallelTestSuiteCollectionInConsoleMode(!ActivationInfoCollector.isActivated(), executionResult,
                    endTime.getTime() - startTime.getTime(), maxConcurrentInstances);
        } else {
            Trackings.trackExecuteSequentialTestSuiteCollectionInConsoleMode(!ActivationInfoCollector.isActivated(), executionResult,
                    endTime.getTime() - startTime.getTime());
        }
    }
    
    protected String getExecutionResult() {
        String resultExcution = null;
        if (getResult().getNumFailures() > 0) {
            resultExcution = TestStatusValue.FAILED.toString();
        }
        else if (getResult().getNumErrors() > 0) {
            resultExcution = TestStatusValue.ERROR.toString();
        }
        else {
            resultExcution = TestStatusValue.PASSED.toString();
        }
        return resultExcution;
    }
}
