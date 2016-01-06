package com.kms.katalon.execution.launcher;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.testdata.reader.CSVReader;
import com.kms.katalon.core.testdata.reader.CSVSeperator;
import com.kms.katalon.core.testdata.reader.CsvWriter;
import com.kms.katalon.entity.file.IFileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.generator.TestCaseScriptGenerator;
import com.kms.katalon.execution.generator.TestSuiteScriptGenerator;
import com.kms.katalon.execution.integration.ReportIntegrationContribution;
import com.kms.katalon.execution.integration.ReportIntegrationFactory;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.launcher.model.LauncherResult;
import com.kms.katalon.execution.launcher.model.LauncherStatus;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.execution.util.MailUtil.EmailConfig;
import com.kms.katalon.execution.util.MailUtil.MailSecurityProtocolType;
import com.kms.katalon.groovy.util.GroovyUtil;
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
    protected List<String> passedTestCaseIds;
    protected int reRunTime;

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

    public static void sendReportEmail(TestSuiteEntity testSuite, File csvFile, File logFile,
            List<Object[]> suitesSummaryForEmail) throws Exception {
        IPreferenceStore prefs = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
        String[] mailRecipients = getRecipients(testSuite.getMailRecipient(),
                prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_REPORT_RECIPIENTS));
        if (mailRecipients.length > 0) {
            EmailConfig conf = new EmailConfig();
            conf.tos = mailRecipients;
            conf.host = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_HOST);
            conf.port = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_PORT);
            conf.from = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_USERNAME);
            conf.securityProtocol = MailSecurityProtocolType.valueOf(prefs
                    .getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_SECURITY_PROTOCOL));
            conf.username = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_USERNAME);
            conf.password = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_PASSWORD);
            conf.signature = prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_SIGNATURE);
            conf.sendAttachment = prefs
                    .getBoolean(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_ATTACHMENT);
            conf.suitePath = testSuite.getRelativePathForUI();
            conf.logFile = logFile;
            // Send report email
            MailUtil.sendSummaryMail(conf, csvFile, logFile, suitesSummaryForEmail);
        }
    }

    /*public static void sendSummaryEmail(TestSuiteEntity testSuite, File csvFile, List<Object[]> suitesSummaryForEmail) throws Exception {
        String prefFile = Platform.getInstallLocation()
                .getDataArea("config/.metadata/.plugins/org.eclipse.core.runtime/.settings/com.kms.katalon.dal.prefs")
                .getFile();
        if ((new File(prefFile)).exists()) {
            PreferenceStore prefs = new PreferenceStore(prefFile);
            prefs.load();
            EmailConfig conf = new EmailConfig();
            conf.tos = getRecipients(testSuite.getMailRecipient(),
                    prefs.getString(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_REPORT_RECIPIENTS));
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

                MailUtil.sendSummaryMail(conf, csvFile, null, suitesSummaryForEmail);
            }
        }
    }*/

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
            CSVReader csvReader = new CSVReader(file, CSVSeperator.COMMA, true);
            Deque<String[]> datas = new ArrayDeque<String[]>();
            datas.addAll(csvReader.getData());
            String[] suiteRow = datas.pollFirst();
            String suiteName = (suiteIndex + 1) + "." + suiteRow[0];
            String browser = suiteRow[1];

            String hostName = "Unknown";
            try {
                InetAddress addr;
                addr = InetAddress.getLocalHost();
                hostName = addr.getCanonicalHostName();
            } catch (UnknownHostException ex) {
            }

            String os = System.getProperty("os.name") + " " + System.getProperty("sun.arch.data.model") + "bit";

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
                    //String testStatus = testRow[5];
                    String testStatus = testRow[6];
                    if (TestStatusValue.PASSED.toString().equals(testStatus)) {
                        arrSuitesSummaryForEmail[1] = (Integer) arrSuitesSummaryForEmail[1] + 1;
                    } else if (TestStatusValue.FAILED.toString().equals(testStatus)) {
                        arrSuitesSummaryForEmail[2] = (Integer) arrSuitesSummaryForEmail[2] + 1;
                    } else if (TestStatusValue.ERROR.toString().equals(testStatus)) {
                        arrSuitesSummaryForEmail[3] = (Integer) arrSuitesSummaryForEmail[3] + 1;
                    } else {
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
                return ((TestCaseEntity) executedEntity).getIdForDisplay();
            } else {
                return ((TestSuiteEntity) executedEntity).getIdForDisplay();
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

    /**
     * Get all recipient email address from Preference and Test Suite without duplication
     * 
     * @param testSuiteRecipients recipients from Test Suite
     * @param reportRecipients recipients from Preferences > Execution > Email > Report Recipients
     * @return non-duplicated recipients
     */
    private static String[] getRecipients(String testSuiteRecipients, String reportRecipients) {
        String[] tsRecipients = StringUtils.split(testSuiteRecipients, ";");
        String[] rptRecipients = StringUtils.split(reportRecipients, ";");
        List<String> recipientList = new ArrayList<String>(Arrays.asList((rptRecipients != null) ? rptRecipients
                : new String[] {}));
        if (tsRecipients != null) {
            for (String recipient : tsRecipients) {
                if (recipientList.contains(recipient.trim())) continue;
                recipientList.add(recipient);
            }
        }
        return recipientList.toArray(new String[recipientList.size()]);
    }
    
    protected static IFile generateTempTestSuiteScript(TestSuiteEntity testSuite, IRunConfiguration config,
            TestSuiteExecutedEntity testSuiteExecutedEntity) throws Exception {
        if (testSuite != null) {
            File tempTestSuiteFile = new TestSuiteScriptGenerator(testSuite, config, testSuiteExecutedEntity)
                    .generateScriptFile();
            return GroovyUtil.getTempScriptIFile(tempTestSuiteFile, testSuite.getProject());
        }
        return null;
    }
    
    protected static IFile generateTempTestCaseScript(TestCaseEntity testCase, IRunConfiguration runConfig)
            throws Exception {
        if (testCase != null) {
            File testSuiteScriptFile = new TestCaseScriptGenerator(testCase, runConfig).generateScriptFile();
            return GroovyUtil.getTempScriptIFile(testSuiteScriptFile, testCase.getProject());
        }
        return null;
    }
}
