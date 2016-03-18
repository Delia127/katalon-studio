package com.kms.katalon.execution.launcher;

import static com.kms.katalon.execution.util.MailUtil.getDistinctRecipients;

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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.core.testdata.reader.CSVReader;
import com.kms.katalon.core.testdata.reader.CSVSeparator;
import com.kms.katalon.core.testdata.reader.CsvWriter;
import com.kms.katalon.core.util.PathUtil;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.entity.ReportLocationSetting;
import com.kms.katalon.execution.entity.Reportable;
import com.kms.katalon.execution.entity.Rerunnable;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.integration.ReportIntegrationContribution;
import com.kms.katalon.execution.integration.ReportIntegrationFactory;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LauncherStatus;
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.execution.util.MailUtil.EmailConfig;
import com.kms.katalon.execution.util.MailUtil.MailSecurityProtocolType;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public abstract class ReportableLauncher extends LoggableLauncher {

    public ReportableLauncher(IRunConfiguration runConfig) {
        super(runConfig);
    }

    public abstract ReportableLauncher clone(IRunConfiguration runConfig);

    @Override
    protected void preExecutionComplete() {
        if (getStatus() == LauncherStatus.TERMINATED) {
            return;
        }
        

        if (!(getExecutedEntity() instanceof Reportable)) {
            return;
        }

        try {
            Date startTime = ReportController.getInstance().getDateFromReportFolderName(
                    getRunConfig().getExecutionSetting().getName());

            updateLastRun(startTime);

            prepareReport();

            sendReport();

        } catch (Exception e) {
            writeError(MessageFormat.format(StringConstants.LAU_RPT_ERROR_TO_GENERATE_REPORT, e.getMessage()));
            LogUtil.logError(e);
        }

        if (needToRerun()) {
            Rerunnable rerun = (Rerunnable) getExecutedEntity();

            TestSuiteEntity testSuite = getTestSuite();
            IExecutedEntity newTestSuiteExecutedEntity = ExecutionUtil.getRerunExecutedEntity(
                    (TestSuiteExecutedEntity) getExecutedEntity(), getResult());

            try {
                writeLine("\n");
                writeLine(MessageFormat.format(StringConstants.LAU_RPT_RERUN_TEST_SUITE, getExecutedEntity()
                        .getSourceId(), String.valueOf(rerun.getPreviousRerunTimes() + 1)));

                IRunConfiguration newConfig = getRunConfig().cloneConfig();
                newConfig.build(testSuite, newTestSuiteExecutedEntity);
                ReportableLauncher rerunLauncher = clone(newConfig);
                LauncherManager.getInstance().addLauncher(rerunLauncher);
            } catch (IOException | ExecutionException e) {
                writeError(MessageFormat.format(StringConstants.MSG_RP_ERROR_TO_RERUN_TEST_SUITE, e.getMessage()));
                LogUtil.logError(e);
            }
        }
    }

    private boolean needToRerun() {
        if (getResult().getNumErrors() + getResult().getNumFailures() > 0 && getExecutedEntity() instanceof Rerunnable) {
            Rerunnable rerun = (Rerunnable) getExecutedEntity();

            return rerun.getRemainingRerunTimes() > 0;
        } else {
            return false;
        }
    }

    private void sendReport() {
        try {
            setStatus(LauncherStatus.SENDING_EMAIL);

            File testSuiteReportSourceFolder = new File(getRunConfig().getExecutionSetting().getFolderPath());

            File csvFile = new File(testSuiteReportSourceFolder, FilenameUtils.getBaseName(testSuiteReportSourceFolder
                    .getName()) + ".csv");

            List<String> csvReports = new ArrayList<String>();

            csvReports.add(csvFile.getAbsolutePath());

            List<Object[]> suitesSummaryForEmail = collectSummaryData(csvReports);

            sendReportEmail(csvFile, suitesSummaryForEmail);
        } catch (Exception e) {
            writeError(MessageFormat.format(StringConstants.MSG_RP_ERROR_TO_EMAIL_REPORT, e.getMessage()));
        }
    }

    public void sendReportEmail(File csvFile, List<Object[]> suitesSummaryForEmail) throws Exception {
        IPreferenceStore prefs = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstants.QUALIFIER);

        // Return if user doesn't need to send email
        if (!prefs.getBoolean(PreferenceConstants.ExecutionPreferenceConstants.MAIL_CONFIG_ATTACHMENT)) {
            return;
        }

        TestSuiteEntity testSuite = getTestSuite();
        String[] mailRecipients = getDistinctRecipients(testSuite.getMailRecipient(),
                prefs.getString(PreferenceConstants.ExecutionPreferenceConstants.MAIL_CONFIG_REPORT_RECIPIENTS));
        if (mailRecipients.length > 0) {
            EmailConfig conf = new EmailConfig();
            conf.tos = mailRecipients;
            conf.host = prefs.getString(PreferenceConstants.ExecutionPreferenceConstants.MAIL_CONFIG_HOST);
            conf.port = prefs.getString(PreferenceConstants.ExecutionPreferenceConstants.MAIL_CONFIG_PORT);
            conf.from = prefs.getString(PreferenceConstants.ExecutionPreferenceConstants.MAIL_CONFIG_USERNAME);
            conf.securityProtocol = MailSecurityProtocolType.valueOf(prefs
                    .getString(PreferenceConstants.ExecutionPreferenceConstants.MAIL_CONFIG_SECURITY_PROTOCOL));
            conf.username = prefs.getString(PreferenceConstants.ExecutionPreferenceConstants.MAIL_CONFIG_USERNAME);
            conf.password = prefs.getString(PreferenceConstants.ExecutionPreferenceConstants.MAIL_CONFIG_PASSWORD);
            conf.signature = prefs.getString(PreferenceConstants.ExecutionPreferenceConstants.MAIL_CONFIG_SIGNATURE);
            conf.sendAttachment = prefs
                    .getBoolean(PreferenceConstants.ExecutionPreferenceConstants.MAIL_CONFIG_ATTACHMENT);
            conf.suitePath = testSuite.getRelativePathForUI();

            writeLine(MessageFormat.format(StringConstants.LAU_PRT_SENDING_EMAIL_RPT_TO,
                    Arrays.toString(mailRecipients)));

            // Send report email
            MailUtil.sendSummaryMail(conf, csvFile, getReportFolder(), suitesSummaryForEmail);

            writeLine(StringConstants.LAU_PRT_EMAIL_SENT);
        }
    }

    protected void updateLastRun(Date startTime) throws Exception {
        TestSuiteEntity testSuite = getTestSuite();

        if (testSuite.getLastRun() == null || startTime.after(testSuite.getLastRun())) {
            testSuite.setLastRun(startTime);
            TestSuiteController.getInstance().updateTestSuite(testSuite);
        }
    }

    protected void prepareReport() {
        try {
            TestSuiteLogRecord suiteLog = ReportUtil.generate(getRunConfig().getExecutionSetting().getFolderPath());
            ReportUtil.writeLogRecordToFiles(suiteLog, getReportFolder());
            uploadReportToIntegratingProduct(suiteLog);
            copyReport();
        } catch (Exception e) {
            LogUtil.logError(e);
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

                    if (reportLocSetting.isReportFileNameSet()
                            && (fileExtension.equals("csv") || fileExtension.equals("html"))) {
                        fileName = reportLocSetting.getReportFileName();
                    }

                    // Copy child file to user's report folder
                    FileUtils.copyFile(reportChildSourceFile,
                            new File(userReportFolder, fileName + "." + fileExtension));
                }

                writeLine(StringConstants.LAU_PRT_CANNOT_SEND_EMAIL);
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
            try {
                writeLine(MessageFormat.format(StringConstants.LAU_PRT_SENDING_RPT_TO, reportContributorEntry.getKey()));

                reportContributorEntry.getValue().uploadTestSuiteResult(getTestSuite(), suiteLog);

                writeLine(MessageFormat.format(StringConstants.LAU_PRT_REPORT_SENT, reportContributorEntry.getKey()));
            } catch (Exception e) {
                writeError(MessageFormat.format(StringConstants.MSG_RP_ERROR_TO_SEND_INTEGRATION_REPORT,
                        reportContributorEntry.getKey(), e.getMessage()));
            }
        }
    }

    private File getUserReportFolder(ReportLocationSetting rpLocSetting) {

        if (!rpLocSetting.isReportFolderPathSet()) {
            return null;
        }

        File reportFolder = new File(PathUtil.relativeToAbsolutePath(rpLocSetting.getReportFolderPath(), getTestSuite()
                .getProject().getFolderLocation()));

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
            Deque<String[]> datas = new ArrayDeque<String[]>();
            datas.addAll(csvReader.getData());
            String[] suiteRow = datas.pollFirst();
            String suiteName = (suiteIndex + 1) + "." + suiteRow[0];
            String browser = suiteRow[1];

            String hostName = getRunConfig().getHostConfiguration().getHostName();
            String os = getRunConfig().getHostConfiguration().getOS();

            Object[] arrSuitesSummaryForEmail = new Object[] { suiteRow[0], 0, 0, 0, 0, hostName, os, browser };
            suitesSummaryForEmail.add(arrSuitesSummaryForEmail);

            int testIndex = 0;
            while (datas.size() > 0) {
                String[] row = datas.pollFirst();
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
                    String[] testRow = datas.pollFirst();
                    String testName = testIndex + "." + testRow[0];
                    newDatas.add(ArrayUtils.addAll(new String[] { suiteName, testName, browser },
                            Arrays.copyOfRange(testRow, 2, testRow.length)));

                    String testStatus = testRow[6];
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
}
