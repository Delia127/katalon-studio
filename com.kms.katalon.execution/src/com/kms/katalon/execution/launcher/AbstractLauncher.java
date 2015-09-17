package com.kms.katalon.execution.launcher;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.file.IFileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.IRunConfiguration;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.integration.ReportIntegrationContribution;
import com.kms.katalon.execution.integration.ReportIntegrationFactory;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.launcher.model.LauncherResult;
import com.kms.katalon.execution.launcher.model.LauncherStatus;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.execution.util.MailUtil.EmailConfig;
import com.kms.katalon.execution.util.MailUtil.MailSecurityProtocolType;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public abstract class AbstractLauncher {
    protected LauncherStatus status;
    protected IRunConfiguration runConfig;
    protected boolean stopSignal = false;
    protected boolean forcedStop = false;
    protected IFile scriptFile;
    protected IFileEntity executedEntity;
    protected List<XmlLogRecord> logRecords;
    protected boolean isObserved;
    protected ILaunch launch;
    protected File logFilePath;
    protected int logDepth = 0;
    protected TestSuiteExecutedEntity testSuiteExecutedEntity;
    protected LauncherResult launcherResult;

    public AbstractLauncher(IRunConfiguration runConfig) {
        this.runConfig = runConfig;
        logFilePath = new File(runConfig.getLogFilePath());
    }

    protected CustomGroovyScriptLaunchShortcut getLauncher() {
        return new CustomGroovyScriptLaunchShortcut();
    }

    protected void updateLastRun(TestSuiteEntity testSuite, File logFile) throws Exception {
        if (testSuite != null) {
            String reportFolderName = FilenameUtils.getBaseName(logFile.getParent());
            Date logFileDate = ReportController.getInstance().getDateFromReportFolderName(reportFolderName);
            if (testSuite.getLastRun() == null || logFileDate.after(testSuite.getLastRun())) {
                testSuite.setLastRun(logFileDate);
                TestSuiteController.getInstance().updateTestSuite(testSuite);
            }
        }
    }

    public static void sendReportEmail(TestSuiteEntity testSuite, File logFile) throws Exception {
        // Send report email
        if (testSuite.getMailRecipient() != null && !testSuite.getMailRecipient().equals("")) {
            IPreferenceStore prefs = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                    PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);

            EmailConfig conf = new EmailConfig();
            conf.tos = testSuite.getMailRecipient().split(";");
            conf.host = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_HOST);
            conf.port = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_PORT);
            conf.from = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_USERNAME);
            conf.securityProtocol = MailSecurityProtocolType.valueOf(prefs
                    .getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_SECURITY_PROTOCOL));
            conf.username = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_USERNAME);
            conf.password = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_PASSWORD);
            conf.signature = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_SIGNATURE);
            conf.sendAttachment = prefs.getBoolean(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_ATTACHMENT);
            conf.suitePath = testSuite.getRelativePathForUI();
            conf.logFile = logFile;

            MailUtil.sendHtmlMail(conf);
        }
    }

    public static void sendSummaryEmail(File csvFile, List<Object[]> suitesSummaryForEmail) throws Exception {
        String prefFile = Platform.getInstallLocation()
                .getDataArea("config/.metadata/.plugins/org.eclipse.core.runtime/.settings/com.kms.katalon.dal.prefs")
                .getFile();
        if ((new File(prefFile)).exists()) {
            PreferenceStore prefs = new PreferenceStore(prefFile);
            prefs.load();
            EmailConfig conf = new EmailConfig();
            String receipts = prefs
                    .getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_REPORT_RECIPIENTS);
            if (receipts != null && !receipts.trim().equals("")) {
                conf.tos = receipts.trim().split(";");
            }
            conf.host = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_HOST);
            conf.port = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_PORT);
            conf.from = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_USERNAME);
            conf.securityProtocol = MailSecurityProtocolType.valueOf(prefs
                    .getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_SECURITY_PROTOCOL));
            conf.username = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_USERNAME);
            conf.password = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_PASSWORD);
            conf.signature = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_SIGNATURE);
            conf.sendAttachment = prefs.getBoolean(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_ATTACHMENT);

            if (conf.host != null && !conf.host.equals("") && conf.port != null && !conf.port.equals("")
                    && conf.tos != null && conf.tos.length > 0 && conf.from != null && !conf.from.equals("")) {

                MailUtil.sendSummaryMail(conf, csvFile, suitesSummaryForEmail);
            }
        }
    }

    public LauncherStatus getStatus() {
        return status;
    }

    public void setStatus(LauncherStatus status) {
        this.status = status;
    }

    public IRunConfiguration getRunConfiguration() {
        return runConfig;
    }

    public void forceStop() {
        forcedStop = true;
        stopSignal = true;
    }

    public abstract void execute();

    public String getEntityId() throws Exception {
        if (executedEntity != null) {
            if (executedEntity instanceof TestCaseEntity) {
                return TestCaseController.getInstance().getIdForDisplay((TestCaseEntity) executedEntity);
            } else {
                return TestSuiteController.getInstance().getIdForDisplay((TestSuiteEntity) executedEntity);
            }
        }
        return "";
    }

    public String getId() throws Exception {
        if (getCurrentLogFile().exists()) {
            return getEntityId() + " - " + FilenameUtils.getBaseName(getCurrentLogFile().getParent());
        }
        return getEntityId();
    }

    public int getTotalTestCase() {
        return launcherResult.getTotalTestCases();
    }

    public void setTotalTestCase(int numberExecutedTestCase) {
        launcherResult = new LauncherResult(numberExecutedTestCase);
    }

    public int getNumberExecutedTestCase() {
        return launcherResult.getExecutedTestCases();
    }

    public abstract List<XmlLogRecord> getAllRecords();

    public abstract void addRecords(List<XmlLogRecord> records);

    public void setObserved(boolean observed) {
        isObserved = observed;
    }

    public boolean isObserved() {
        return isObserved;
    }

    public void cleanLauncher() {
        if (launch != null) {
            CustomGroovyScriptLaunchShortcut.cleanConfiguration(launch);
        }

        if (scriptFile != null) {
            deleteScriptFile();
        }
    }

    protected abstract void deleteScriptFile();

    public ILaunch getLaunch() {
        return launch;
    }

    protected CustomGroovyScriptLaunchShortcut execute(ProjectEntity project, IFile scriptFile, LaunchMode launchMode)
            throws Exception {
        CustomGroovyScriptLaunchShortcut launchShortcut = getLauncher();
        launchShortcut.launch(scriptFile, project, launchMode);
        String name = FilenameUtils.getBaseName(scriptFile.getName());

        while (launch == null) {
            for (ILaunch launch : CustomGroovyScriptLaunchShortcut.getLaunchManager().getLaunches()) {
                if (launch.getLaunchConfiguration() != null) {
                    if (launch.getLaunchConfiguration().getName().equals(name)) {
                        this.launch = launch;
                    }
                }
            }
        }
        return launchShortcut;
    }

    protected void stopAndSchedule() throws CoreException, InterruptedException {
        LauncherManager.getInstance().stopRunningAndSchedule(this);
    }

    public String getProgressStatus() {
        return status.name() + " - " + Integer.toString(launcherResult.getExecutedTestCases()) + "/"
                + Integer.toString(launcherResult.getTotalTestCases());
    }

    public boolean isForcedStop() {
        return forcedStop;
    }

    public File getCurrentLogFile() {
        return logFilePath;
    }

    public LauncherResult getResult() {
        return launcherResult;
    }

    protected void uploadReportToIntegratingProduct(TestSuiteLogRecord suiteLog) throws Exception {
        if (executedEntity instanceof TestSuiteEntity) {
            for (Entry<String, ReportIntegrationContribution> reportContributorEntry : ReportIntegrationFactory
                    .getInstance().getIntegrationContributorMap().entrySet()) {
                reportContributorEntry.getValue().uploadTestSuiteResult((TestSuiteEntity) executedEntity, suiteLog);
            }
        }
    }
}
