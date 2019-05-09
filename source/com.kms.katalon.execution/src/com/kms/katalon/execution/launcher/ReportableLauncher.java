package com.kms.katalon.execution.launcher;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.katalon.platform.api.event.ExecutionEvent;
import com.katalon.platform.api.execution.TestCaseExecutionContext;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.logging.model.TestStatus;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.core.testdata.reader.CSVReader;
import com.kms.katalon.core.testdata.reader.CSVSeparator;
import com.kms.katalon.core.testdata.reader.CsvWriter;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.EmailConfig;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.entity.ReportLocationSetting;
import com.kms.katalon.execution.entity.Reportable;
import com.kms.katalon.execution.entity.Rerunable;
import com.kms.katalon.execution.entity.TestCaseExecutedEntity;
import com.kms.katalon.execution.entity.TestCaseExecutionContextImpl;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutionContextImpl;
import com.kms.katalon.execution.entity.TestSuiteExecutionEvent;
import com.kms.katalon.execution.integration.ReportIntegrationContribution;
import com.kms.katalon.execution.integration.ReportIntegrationFactory;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.result.LauncherStatus;
import com.kms.katalon.execution.setting.EmailVariableBinding;
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.logging.LogUtil;

public abstract class ReportableLauncher extends LoggableLauncher {
    private ReportEntity reportEntity;

    private Date startTime;

    private Date endTime;

    public ReportableLauncher(LauncherManager manager, IRunConfiguration runConfig) {
        super(manager, runConfig);
        this.setExecutionUUID(runConfig.getExecutionUUID());
    }

    public abstract ReportableLauncher clone(IRunConfiguration runConfig);

    @Override
    protected void onStartExecution() {
        super.onStartExecution();

        startTime = new Date();
        fireTestSuiteExecutionEvent(ExecutionEvent.TEST_SUITE_STARTED_EVENT);
    }

    @Override
    protected void preExecutionComplete() {
        if (getStatus() == LauncherStatus.TERMINATED) {
            return;
        }

        if (!(getExecutedEntity() instanceof Reportable)) {
            return;
        }

        try {
            setStatus(LauncherStatus.PREPARE_REPORT);
            this.endTime = new Date();

            TestSuiteLogRecord suiteLogRecord = prepareReport();

            uploadReportToIntegratingProduct(suiteLogRecord);

            sendReport(suiteLogRecord);

        } catch (Exception e) {
            writeError(MessageFormat.format(StringConstants.LAU_RPT_ERROR_TO_GENERATE_REPORT, e.getMessage()));
            LogUtil.printAndLogError(e);
        }

        waitForLoggingFinished();

        fireTestSuiteExecutionEvent(ExecutionEvent.TEST_SUITE_FINISHED_EVENT);

        if (needToRerun()) {
            Rerunable rerun = (Rerunable) getExecutedEntity();

            TestSuiteEntity testSuite = getTestSuite();

            try {
                IExecutedEntity newTestSuiteExecutedEntity = ExecutionUtil
                        .getRerunExecutedEntity((TestSuiteExecutedEntity) getExecutedEntity(), getResult());
                writeLine("\n");
                writeLine(MessageFormat.format(StringConstants.LAU_RPT_RERUN_TEST_SUITE,
                        getExecutedEntity().getSourceId(), String.valueOf(rerun.getPreviousRerunTimes() + 1)));

                IRunConfiguration newConfig = getRunConfig().cloneConfig();
                if (getRunConfig() instanceof AbstractRunConfiguration
                        && newConfig instanceof AbstractRunConfiguration) {
                    ((AbstractRunConfiguration) newConfig).setExecutionProfile(getRunConfig().getExecutionProfile());
                }
                newConfig.build(testSuite, newTestSuiteExecutedEntity);
                ReportableLauncher rerunLauncher = clone(newConfig);
                rerunLauncher.getManager().addLauncher(rerunLauncher);
            } catch (Exception e) {
                writeError(MessageFormat.format(StringConstants.MSG_RP_ERROR_TO_RERUN_TEST_SUITE,
                        ExceptionUtils.getStackTrace(e)));
                LogUtil.logError(e);
            }
        }
    }

    private boolean needToRerun() {
        if (getResult().getNumErrors() + getResult().getNumFailures() > 0 && getExecutedEntity() instanceof Rerunable) {
            Rerunable rerun = (Rerunable) getExecutedEntity();

            return rerun.getRemainingRerunTimes() > 0;
        } else {
            return false;
        }
    }

    private void waitForLoggingFinished() {
        try {
            long startTime = System.currentTimeMillis();
            while (!loggingFinished && System.currentTimeMillis() - startTime < TimeUnit.MINUTES.toMillis(1)) {
                Thread.sleep(200);
            }
        } catch (Exception ignored) {

        }
    }

    private void sendReport(TestSuiteLogRecord testSuiteLogRecord) {
        try {
            sendReportEmail(testSuiteLogRecord);
        } catch (Exception e) {
            writeError(MessageFormat.format(StringConstants.MSG_RP_ERROR_TO_EMAIL_REPORT,
                    ExceptionUtils.getStackTrace(e)));
            LogUtil.logError(e);
        }
    }

    private void sendReportEmail(TestSuiteLogRecord testSuiteLogRecord) throws Exception {

        if (!(getExecutedEntity() instanceof TestSuiteExecutedEntity)) {
            return;
        }

        EmailConfig emailConfig = ((TestSuiteExecutedEntity) getExecutedEntity())
                .getEmailConfig(ProjectController.getInstance().getCurrentProject());
        if (emailConfig == null || !emailConfig.canSend()) {
            return;
        }

        if (emailConfig.isSendEmailTestFailedOnly() && testSuiteLogRecord.getStatus() != null
                && testSuiteLogRecord.getStatus().getStatusValue() != TestStatusValue.FAILED) {
            return;
        }

        setStatus(LauncherStatus.SENDING_REPORT, StringConstants.LAU_MESSAGE_SENDING_EMAIL);
        writeLine(MessageFormat.format(StringConstants.LAU_PRT_SENDING_EMAIL_RPT_TO,
                Arrays.toString(emailConfig.getTos())));

        // Send report email
        MailUtil.sendSummaryMail(emailConfig, testSuiteLogRecord, new EmailVariableBinding(testSuiteLogRecord));

        writeLine(StringConstants.LAU_PRT_EMAIL_SENT);
    }

    protected void updateLastRun(Date startTime) throws Exception {
    }

    protected TestSuiteLogRecord prepareReport() {
        try {
            File reportFolder = getReportFolder();
            LogUtil.logInfo("Start writing reports to folder: " + reportFolder.getAbsolutePath()); 
            setStatus(LauncherStatus.PREPARE_REPORT, ExecutionMessageConstants.MSG_PREPARE_GENERATE_REPORT);
            TestSuiteLogRecord suiteLog = ReportUtil.generate(getRunConfig().getExecutionSetting().getFolderPath());

//            setStatus(LauncherStatus.PREPARE_REPORT, ExecutionMessageConstants.MSG_PREPARE_REPORT_HTML);
//            ReportUtil.writeHtmlReport(suiteLog, reportFolder);
//
//            setStatus(LauncherStatus.PREPARE_REPORT, ExecutionMessageConstants.MSG_PREPARE_REPORT_CSV);
//            ReportUtil.writeCSVReport(suiteLog, reportFolder);
//
//            setStatus(LauncherStatus.PREPARE_REPORT, ExecutionMessageConstants.MSG_PREPARE_REPORT_UUID);
//            ReportUtil.writeExecutionUUIDToFile(this.getExecutionUUID(), reportFolder);

            // setStatus(LauncherStatus.PREPARE_REPORT, ExecutionMessageConstants.MSG_PREPARE_REPORT_JSON);
            // ReportUtil.writeJsonReport(suiteLog, reportFolder);

//            setStatus(LauncherStatus.PREPARE_REPORT, ExecutionMessageConstants.MSG_PREPARE_REPORT_JUNIT);
//            ReportUtil.writeJUnitReport(suiteLog, reportFolder);
//            LogUtil.logInfo("Reports were generated at folder: " + reportFolder.getAbsolutePath());

            copyReport();
            return suiteLog;
        } catch (Exception e) {
            LogUtil.printAndLogError(e);
            return null;
        }
    }

    protected void copyReport() {
        try {
            ReportLocationSetting reportLocSetting = ((Reportable) getExecutedEntity()).getReportLocationSetting();
            if (reportLocSetting == null || !reportLocSetting.isReportFolderPathSet()) {
                return;
            }

            File userReportFolder = getUserReportFolder(reportLocSetting);

            if (userReportFolder != null) {
                writeLine(MessageFormat.format(StringConstants.LAU_PRT_COPYING_RPT_TO_USR_RPT_FOLDER,
                        userReportFolder.getAbsolutePath()));

                if (reportLocSetting.isCleanReportFolderFlagActive()) {
                    writeLine(StringConstants.LAU_PRT_CLEANING_USR_RPT_FOLDER);
                    FileUtils.cleanDirectory(userReportFolder);
                }

                for (File reportChildSourceFile : getReportFolder().listFiles()) {
                    String fileName = FilenameUtils.getBaseName(reportChildSourceFile.getName());
                    String fileExtension = FilenameUtils.getExtension(reportChildSourceFile.getName());

                    // ignore LOCK file
                    if (fileExtension.equalsIgnoreCase("lck")) {
                        continue;
                    }

                    if (fileExtension.equalsIgnoreCase("csv") || fileExtension.equalsIgnoreCase("html")) {
                        fileName = reportLocSetting.getReportFileName();
                    }

                    // Copy child file to user's report folder
                    if (reportChildSourceFile.isFile()) {
                        FileUtils.copyFile(reportChildSourceFile,
                                new File(userReportFolder, fileName + "." + fileExtension));
                    } else if (reportChildSourceFile.isDirectory()) {
                        File newCoppiedFolder = new File(userReportFolder, fileName);
                        newCoppiedFolder.mkdirs();
                        FileUtils.copyDirectory(reportChildSourceFile, newCoppiedFolder);
                    }
                }
            }
        } catch (IOException ex) {
            LogUtil.logError(ex);
        }
    }

    protected void uploadReportToIntegratingProduct(TestSuiteLogRecord suiteLog) {
        if (!(getExecutedEntity() instanceof Reportable)) {
            return;
        }
        for (Entry<String, ReportIntegrationContribution> reportContributorEntry : ReportIntegrationFactory
                .getInstance().getIntegrationContributorMap().entrySet()) {
            ReportIntegrationContribution contribution = reportContributorEntry.getValue();
            if (contribution == null || !contribution.isIntegrationActive(getTestSuite())) {
                continue;
            }
            String integratingProductName = reportContributorEntry.getKey();
            setStatus(LauncherStatus.UPLOAD_REPORT,
                    MessageFormat.format(StringConstants.LAU_MESSAGE_UPLOADING_RPT, integratingProductName));
            try {
                writeLine(MessageFormat.format(StringConstants.LAU_PRT_SENDING_RPT_TO, integratingProductName));

                reportContributorEntry.getValue().uploadTestSuiteResult(getTestSuite(), suiteLog);

                writeLine(MessageFormat.format(StringConstants.LAU_PRT_REPORT_SENT, integratingProductName));
            } catch (Exception e) {
                writeError(MessageFormat.format(StringConstants.MSG_RP_ERROR_TO_SEND_INTEGRATION_REPORT,
                        integratingProductName, ExceptionUtils.getStackTrace(e)));
                LogUtil.logError(e);
            }
        }
    }

    private File getUserReportFolder(ReportLocationSetting rpLocSetting) {
        if (!rpLocSetting.isReportFolderPathSet()) {
            return null;
        }

        File reportFolder = new File(PathUtil.relativeToAbsolutePath(rpLocSetting.getReportFolderPath(),
                getTestSuite().getProject().getFolderLocation()));

        if (reportFolder != null && !reportFolder.exists()) {
            reportFolder.mkdirs();
        }

        return reportFolder;
    }

    public TestSuiteEntity getTestSuite() {
        try {
            return TestSuiteController.getInstance().getTestSuiteByDisplayId(
                    getRunConfig().getExecutionSetting().getExecutedEntity().getSourceId(),
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LogUtil.logError(e);
            return null;
        }
    }

    protected IExecutedEntity getExecutedEntity() {
        return getRunConfig().getExecutionSetting().getExecutedEntity();
    }

    protected File getReportFolder() {
        return new File(getRunConfig().getExecutionSetting().getFolderPath());
    }

    protected List<Object[]> collectSummaryData(List<String> csvReports) throws Exception {
        List<Object[]> newDatas = new ArrayList<Object[]>();
        // PASSED, FAILED, ERROR, NOT_RUN
        List<Object[]> suitesSummaryForEmail = new ArrayList<Object[]>();
        for (int suiteIndex = 0; suiteIndex < csvReports.size(); suiteIndex++) {
            String file = csvReports.get(suiteIndex);
            File csvReportFile = new File(file);
            if (!csvReportFile.isFile()) {
                continue;
            }
            // Collect result and send mail here
            CSVReader csvReader = new CSVReader(file, CSVSeparator.COMMA, true);
            Deque<List<String>> datas = new ArrayDeque<List<String>>();
            datas.addAll(csvReader.getData());
            List<String> suiteRow = datas.pollFirst();
            String suiteName = (suiteIndex + 1) + "." + suiteRow.get(0);
            String browser = suiteRow.get(1);

            String hostName = getRunConfig().getHostConfiguration().getHostName();
            String os = getRunConfig().getHostConfiguration().getOS();

            Object[] arrSuitesSummaryForEmail = new Object[] { suiteRow.get(0), 0, 0, 0, 0, hostName, os, browser };
            suitesSummaryForEmail.add(arrSuitesSummaryForEmail);

            int testIndex = 0;
            while (datas.size() > 0) {
                List<String> row = datas.pollFirst();
                // Check empty line
                boolean isEmptyLine = true;
                for (String col : row) {
                    if (col != null && !col.trim().equals("")) {
                        isEmptyLine = false;
                        break;
                    }
                }
                if (isEmptyLine && !datas.isEmpty()) {
                    testIndex++;
                    List<String> testRow = datas.pollFirst();
                    String testName = testIndex + "." + testRow.get(0);
                    newDatas.add(ArrayUtils.addAll(new String[] { suiteName, testName, browser },
                            Arrays.copyOfRange(testRow.toArray(new String[0]), 2, testRow.size())));

                    String testStatus = testRow.get(6);
                    if (TestStatusValue.PASSED.toString().equals(testStatus)) {
                        arrSuitesSummaryForEmail[1] = (Integer) arrSuitesSummaryForEmail[1] + 1;
                    } else if (TestStatusValue.FAILED.toString().equals(testStatus)) {
                        arrSuitesSummaryForEmail[2] = (Integer) arrSuitesSummaryForEmail[2] + 1;
                    } else if (TestStatusValue.ERROR.toString().equals(testStatus)) {
                        arrSuitesSummaryForEmail[3] = (Integer) arrSuitesSummaryForEmail[3] + 1;
                    } else if (TestStatusValue.INCOMPLETE.toString().equals(testStatus)) {
                        arrSuitesSummaryForEmail[4] = (Integer) arrSuitesSummaryForEmail[4] + 1;
                    }
                }
            }
        }

        File csvSummaryFile = new File(System.getProperty("java.io.tmpdir") + "Summary.csv");
        if (csvSummaryFile.exists()) {
            csvSummaryFile.delete();
        }
        CsvWriter.writeArraysToCsv(CsvWriter.SUMMARY_HEADER, newDatas, csvSummaryFile);

        return suitesSummaryForEmail;
    }

    public ReportEntity getReportEntity() {
        try {
            if (reportEntity == null) {
                reportEntity = ReportController.getInstance().getReportEntity(getTestSuite(), getId());
            }
            return reportEntity;
        } catch (Exception e) {
            LogUtil.logError(e);
            return null;
        }
    }

    @Override
    protected void onStartExecutionComplete() {
        super.onStartExecutionComplete();
    }

    public void setReportEntity(ReportEntity reportEntity) {
        this.reportEntity = reportEntity;
    }

    protected TestSuiteExecutionEvent fireTestSuiteExecutionEvent(String eventName) {
        if (executedEntity instanceof TestSuiteExecutedEntity) {
            TestSuiteExecutionContextImpl executionContext = getTestSuiteExecutionContext();
            TestSuiteExecutionEvent eventObject = new TestSuiteExecutionEvent(eventName, executionContext);
            EventBrokerSingleton.getInstance().getEventBroker().post(eventName, eventObject);
        }
        return null;
    }

    public TestSuiteExecutionContextImpl getTestSuiteExecutionContext() {
        if (!(executedEntity instanceof TestSuiteExecutedEntity)) {
            return null;
        }
        TestSuiteExecutedEntity testSuiteEx = (TestSuiteExecutedEntity) executedEntity;

        List<TestCaseExecutionContext> testCaseContexts = new ArrayList<>();
        int iterationIndex = 0;
        for (int index = 0; index < testSuiteEx.getExecutedItems().size(); index++) {
            TestCaseExecutedEntity testCaseExecutedEntity = (TestCaseExecutedEntity) testSuiteEx.getExecutedItems()
                    .get(index);
            for (int loopTime = 0; loopTime < testCaseExecutedEntity.getLoopTimes(); loopTime++) {
                TestStatus testStatus = getResult().getStatuses()[iterationIndex];
                testCaseContexts.add(TestCaseExecutionContextImpl.Builder
                        .create(testCaseExecutedEntity.getSourceId(), testCaseExecutedEntity.getSourceId())
                        .withTestCaseStatus(testStatus.getStatusValue().name())
                        .withMessage(testStatus.getStackTrace())
                        .build());
                iterationIndex++;
            }
        }

        TestSuiteExecutionContextImpl executionContext = TestSuiteExecutionContextImpl.Builder
                .create(getId(), testSuiteEx.getSourceId(), testSuiteEx.getEntity().getProject().getFolderLocation())
                .withReportId(getReportEntity().getIdForDisplay())
                .withTestCaseContext(testCaseContexts)
                .withStartTime(startTime != null ? startTime.getTime() : 0L)
                .withEndTime(endTime != null ? endTime.getTime() : 0L)
                .build();
        return executionContext;
    }
}
