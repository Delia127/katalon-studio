package com.kms.katalon.execution.launcher.manager;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.ExecutionEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.KatalonArgumentNotValidException;
import com.kms.katalon.execution.integration.IntegrationCommand;
import com.kms.katalon.execution.integration.ReportIntegrationFactory;
import com.kms.katalon.execution.launcher.AbstractLauncher;
import com.kms.katalon.execution.launcher.ConsoleLauncher;
import com.kms.katalon.execution.launcher.model.LauncherResult;
import com.kms.katalon.execution.util.ExecutionUtil;

public class ConsoleMain {
    private static final String ARGUMENT_SPLITTER = "=";
    private static final String ARGUMENT_PREFIX = "-";
    private final static String PROJECT_PK_ARGUMENT = "-projectPk=";
    private final static String START_REPORT_ARGUMENT = "-startSummaryReport";
    private final static String END_REPORT_ARGUMENT = "-endSummaryReport";
    private final static String SUMMARY_REPORT_ARGUMENT = "-summaryReport";
    public final static String BATCH_RUNNING_FILE = "batch_run";
    private final static String EXECUTE_ARGUMENT = "-execute";
    private final static String TESTSUITE_ID_ARGUMENT = "-testSuiteID=";
    private final static String BROWSER_TYPE_ARGUMENT = "-browserType=";
    private final static String SHOW_STATUS_DELAY_ARGUMENT = "-statusDelay=";
    private final static String REPORT_FOLDER_ARGUMENT = "-reportFolder=";
    private final static String REPORT_FILE_NAME_ARGUMENT = "-reportFileName=";

    public static boolean startSummaryReport = false;
    public static boolean endSummaryReport = false;
    public static boolean summaryReport = false;
    private static int showProgressDelay = 15;
    private static String reportFileName = "report";
    private static int returnCode = 0;

    public void launch(String[] arguments) throws Exception {
        if (arguments == null) {
            System.out.println("Arguments cannot be null or empty");
            closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
        }

        List<ExecutionEntity> executionEntities = new ArrayList<ExecutionEntity>();
        ProjectEntity projectEntity = null;
        int offset = 0;
        Map<String, String> runInput = new HashMap<String, String>();
        String testSuiteID = null;
        String argBrowserType = null;
        String testSuiteReportFolder = null;
        boolean foundExecutionArgument = false;

        List<IntegrationCommand> integrationCmds = ReportIntegrationFactory.getInstance().getIntegrationCommands();
        Options opts = new Options();
        for (IntegrationCommand integrationCmd : integrationCmds) {
            opts.addOption(integrationCmd.getOption());
        }

        try {
            while (offset < arguments.length) {
                if (arguments[offset].startsWith(PROJECT_PK_ARGUMENT)) {
                    String projectPk = arguments[offset].substring(PROJECT_PK_ARGUMENT.length());

                    System.out.println(StringConstants.MNG_PRT_LOADING_PROJ);
                    projectEntity = getProject(projectPk);
                    if (projectEntity == null) {
                        return;
                    }

                    System.out.println(StringConstants.MNG_PRT_PROJ_LOADED);

                    offset++;
                } else if (arguments[offset].startsWith(START_REPORT_ARGUMENT)) {
                    startSummaryReport = true;
                    offset++;
                } else if (arguments[offset].startsWith(END_REPORT_ARGUMENT)) {
                    endSummaryReport = true;
                    offset++;
                } else if (arguments[offset].startsWith(SUMMARY_REPORT_ARGUMENT)) {
                    summaryReport = true;
                    offset++;
                } else if (arguments[offset].startsWith(EXECUTE_ARGUMENT)) {
                    foundExecutionArgument = true;
                    if (offset + 1 < arguments.length && arguments[offset + 1].startsWith(TESTSUITE_ID_ARGUMENT)) {
                        testSuiteID = arguments[offset + 1].substring(TESTSUITE_ID_ARGUMENT.length());
                    }
                    if (offset + 2 < arguments.length && arguments[offset + 2].startsWith(BROWSER_TYPE_ARGUMENT)) {
                        argBrowserType = arguments[offset + 2].substring(BROWSER_TYPE_ARGUMENT.length());
                    }
                    offset += 3;
                    testSuiteReportFolder = null;
                    if ((arguments.length > offset + 3) && arguments[offset + 3].startsWith(REPORT_FOLDER_ARGUMENT)) {
                        testSuiteReportFolder = arguments[offset + 3].substring(REPORT_FOLDER_ARGUMENT.length());
                        offset++;
                    }
                } else if (arguments[offset].startsWith(SHOW_STATUS_DELAY_ARGUMENT)) {
                    showProgressDelay = Integer.valueOf(arguments[offset]
                            .substring(SHOW_STATUS_DELAY_ARGUMENT.length()).trim());
                    if (showProgressDelay < 0) {
                        System.out.println(StringConstants.MNG_PRT_INVALID_DELAY_TIME_ARG);
                        closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
                    }
                    offset++;
                } else if (arguments[offset].startsWith(REPORT_FILE_NAME_ARGUMENT)) {
                    String reportFileName = arguments[offset].substring(REPORT_FILE_NAME_ARGUMENT.length());
                    if (reportFileName != null && !reportFileName.isEmpty()) {
                        setReportFileName(reportFileName);
                    } else {
                        System.out.println(StringConstants.MNG_PRT_INVALID_FILE_NAME_ARG);
                        closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
                    }
                    offset++;
                } else {
                    int intIndx = processIntegrationArgs(opts, integrationCmds, arguments, offset);
                    if (intIndx == offset) {
                        String otherArgument = arguments[offset];
                        if (otherArgument.startsWith(ARGUMENT_PREFIX)) {
                            otherArgument = otherArgument.substring(1);
                        }
                        String[] argumentValues = otherArgument.split(ARGUMENT_SPLITTER);
                        if (argumentValues.length == 2) {
                            runInput.put(argumentValues[0], argumentValues[1]);
                        }
                        offset++;
                    } else {
                        offset = intIndx;
                    }
                }
            }
        } catch (KatalonArgumentNotValidException ex) {
            System.out.println(ex.getMessage());
            closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
            return;
        }

        if (!foundExecutionArgument) {
            System.out.println(StringConstants.MNG_PRT_MISSING_EXECUTION_ARG);
            closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
            return;
        }

        ExecutionEntity executionEntity = addExecutionEntities(testSuiteID, argBrowserType, projectEntity,
                testSuiteReportFolder, runInput);
        if (executionEntity == null) {
            return;
        }
        executionEntities.add(executionEntity);
        execute(executionEntities.get(0), projectEntity, 0);
    }

    private int processIntegrationArgs(Options opts, List<IntegrationCommand> integrationCmds, String[] arguments,
            int currentOffset) throws KatalonArgumentNotValidException {
        Option currentOpt = null;
        List<String> tokens = new ArrayList<String>();
        while (currentOffset < arguments.length) {
            if (arguments[currentOffset].startsWith(ARGUMENT_PREFIX)) {
                if (currentOpt != null) {
                    break;
                }
                String optNameAndValue = arguments[currentOffset].substring(ARGUMENT_PREFIX.length(),
                        arguments[currentOffset].length());

                if (opts.hasOption(optNameAndValue)) {
                    currentOpt = opts.getOption(optNameAndValue);
                } else {
                    if (optNameAndValue.indexOf(ARGUMENT_SPLITTER) != -1
                            && opts.hasOption(optNameAndValue.substring(0, optNameAndValue.indexOf(ARGUMENT_SPLITTER)))) {
                        currentOpt = opts.getOption(optNameAndValue.substring(0,
                                optNameAndValue.indexOf(ARGUMENT_SPLITTER)));
                        tokens.add(optNameAndValue.substring(optNameAndValue.indexOf(ARGUMENT_SPLITTER),
                                optNameAndValue.length()));
                    } else {
                        return currentOffset;
                    }
                }
                currentOffset++;
            } else {
                if (currentOpt != null) {
                    if (tokens.size() < currentOpt.getArgs()) {
                        tokens.add(arguments[currentOffset]);
                        currentOffset++;
                    }
                } else {
                    return currentOffset;
                }
            }
        }

        if (currentOpt != null) {
            for (IntegrationCommand integrationCmd : integrationCmds) {
                if (currentOpt.equals(integrationCmd.getOption())) {
                    integrationCmd.setValues(tokens.toArray(new String[tokens.size()]));
                }
            }
        }
        return currentOffset;
    }

    private void execute(final ExecutionEntity executionEntity, final ProjectEntity projectEntity, final int reRunTime)
            throws Exception {
        createLauncher(executionEntity, projectEntity);
        Thread launcherStatusThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int consoleWidth = 80;
                    do {
                        System.out.println();
                        for (int i = 0; i < consoleWidth; i++) {
                            System.out.print(ARGUMENT_PREFIX);
                        }
                        System.out.println();

                        for (AbstractLauncher launcher : LauncherManager.getInstance().getConsoleLaunchers()) {
                            StringBuilder builder = new StringBuilder();
                            builder.append(((ConsoleLauncher) launcher).getDisplayID());
                            int infoLength = builder.toString().length() + launcher.getProgressStatus().length();
                            for (int index = 80; index > infoLength % 80; index--) {
                                builder.append(".");
                            }
                            builder.append(launcher.getProgressStatus());
                            System.out.println(wrap(builder.toString(), consoleWidth));
                        }

                        for (int i = 0; i < consoleWidth; i++) {
                            System.out.print(ARGUMENT_PREFIX);
                        }
                        System.out.println("\n");

                        Thread.sleep(showProgressDelay * 1000);
                    } while (LauncherManager.getInstance().isAnyLauncherRunning());

                    // Send summary email
                    List<String> csvReports = new ArrayList<String>();
                    for (AbstractLauncher launcher : LauncherManager.getInstance().getConsoleLaunchers()) {
                        File logFolder = launcher.getCurrentLogFile().getParentFile();
                        File csvFile = new File(logFolder, logFolder.getName() + ".csv");
                        csvReports.add(csvFile.getAbsolutePath());
                    }

                    // sendReport(csvReports);

                    // @author: Tuan Nguyen Manh.
                    // Exit code is 0 if the executed test suite is passed, 1 if
                    // the executed test suite failed.
                    int exitCode = LauncherManager.getInstance().getConsoleLaunchers().get(0).getResult()
                            .getReturnCode();

                    if (exitCode != LauncherResult.RETURN_CODE_PASSED
                            && reRunTime < executionEntity.getTestSuite().getNumberOfRerun()) {
                        Display.getDefault().syncExec(new Runnable() {
                            public void run() {
                                System.out.println("Re-run test suite #" + (reRunTime + 1));
                                try {
                                    execute(executionEntity, projectEntity, reRunTime + 1);
                                } catch (Exception e) {
                                    return;
                                }
                            }
                        });
                    } else {
                        ConsoleMain.closeWorkbench(exitCode);
                    }

                } catch (Exception e) {
                    return;
                }

            }
        });
        launcherStatusThread.start();
    }

    public static void closeWorkbench(final int exitCode) throws InterruptedException, CoreException {
        Thread.sleep(5000);
        LauncherManager.getInstance().removeAllTerminated();

        returnCode = exitCode;

        final IWorkbench workBench = PlatformUI.getWorkbench();
        final Display display = PlatformUI.getWorkbench().getDisplay();
        display.syncExec(new Runnable() {
            public void run() {
                if (!display.isDisposed()) {
                    System.out.println(StringConstants.MNG_PRT_CLOSING_WORKBENCH);
                    workBench.close();
                    System.out.println(StringConstants.MNG_PRT_WORKBENCH_CLOSED);
                }
            }
        });
    }

    public static int getReturnCode() {
        return returnCode;
    }

    /**
     * Only accept to execute one test suite in a test run.
     * 
     * @author: Tuan Nguyen Manh.
     * @param executionEntities
     * @param project
     * @throws Exception
     */
    private void createLauncher(ExecutionEntity executionEntity, ProjectEntity project) throws Exception {
        TestSuiteEntity testSuite = executionEntity.getTestSuite();
        for (IRunConfiguration runConfig : executionEntity.getRunConfigurations()) {

            ConsoleLauncher launcher = new ConsoleLauncher(runConfig);
            TestSuiteExecutedEntity testSuiteExecutedEntity = ExecutionUtil
                    .loadTestDataForTestSuite(testSuite, project);
            testSuiteExecutedEntity.setReportFolderPath(executionEntity.getReportFolderPath());
            launcher.setTotalTestCase(testSuiteExecutedEntity.getTotalTestCases());
            launcher.launch(testSuite, testSuiteExecutedEntity);
            Thread.sleep(1000);
        }
    }

    private ProjectEntity getProject(String projectPk) throws Exception {
        if (projectPk == null) {
            System.out.println(MessageFormat.format(StringConstants.MNG_PRT_MISSING_PROJ_ARG, PROJECT_PK_ARGUMENT));
            closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
            return null;
        }

        ProjectEntity projectEntity = ProjectController.getInstance().openProject(projectPk);

        if (projectEntity == null) {
            System.out.println(MessageFormat.format(StringConstants.MNG_PRT_INVALID_ARG_CANNOT_FIND_PROJ_X, projectPk));
            closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
        }
        return projectEntity;
    }

    private ExecutionEntity addExecutionEntities(String testSuiteID, String argBrowserType,
            ProjectEntity projectEntity, String reportFolderPath, Map<String, String> runInput)
            throws InterruptedException, CoreException {
        try {
            if (testSuiteID == null) {
                System.out.println(StringConstants.MNG_PRT_MISSING_TESTSUITE_ARG);
                closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
                return null;
            }
            if (argBrowserType == null) {
                System.out.println(StringConstants.MNG_PRT_MISSING_BROWSERTYPE_ARG);
                closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
                return null;
            }
            TestSuiteEntity testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteID,
                    projectEntity);

            if (testSuite == null) {
                System.out.println(MessageFormat.format(StringConstants.MNG_PRT_TEST_SUITE_X_NOT_FOUND, testSuiteID));
                closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
                return null;
            }

            ExecutionEntity executionEntity = new ExecutionEntity();
            executionEntity.setTestSuite(testSuite);
            for (String sBrowserType : argBrowserType.split(";")) {
                IRunConfiguration runConfig = RunConfigurationCollector.getInstance().getRunConfiguration(sBrowserType,
                        testSuite, runInput);
                if (runConfig != null) {
                    executionEntity.getRunConfigurations().add(runConfig);
                    executionEntity.setReportFolderPath(reportFolderPath);
                } else {
                    System.out.println(MessageFormat.format(StringConstants.MNG_PRT_INVALID_BROWSER_X, sBrowserType));
                    closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
                    return null;
                }
            }
            return executionEntity;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
            return null;
        }
    }

    private String wrap(String longString, int maxWidth) {
        List<String> childrenString = new ArrayList<String>();
        int multiplier = 1;
        while (longString.length() > maxWidth * multiplier) {
            childrenString.add(longString.substring((multiplier - 1) * maxWidth, multiplier * maxWidth));
            multiplier++;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < multiplier - 1; i++) {
            builder.append(childrenString.get(i));
            builder.append("\n");
        }
        builder.append(longString.substring((multiplier - 1) * maxWidth, longString.length()));
        return builder.toString();
    }

    /*
     * private void sendReport(List<String> csvReports) throws Exception { List<Object[]> newDatas = new
     * ArrayList<Object[]>(); // PASSED, FAILED, ERROR, NOT_RUN List<Object[]> suitesSummaryForEmail = new
     * ArrayList<Object[]>(); for (int suiteIndex = 0; suiteIndex < csvReports.size(); suiteIndex++) { String line =
     * csvReports.get(suiteIndex); File csvReportFile = new File(line); if (!csvReportFile.isFile()) { continue; } //
     * Collect result and send mail here CSVReader csvReader = new CSVReader(line, CSVSeperator.COMMA, true);
     * Deque<String[]> datas = new ArrayDeque<String[]>(); datas.addAll(csvReader.getData()); String[] suiteRow =
     * datas.pollFirst(); String suiteName = (suiteIndex + 1) + "." + suiteRow[0]; String browser = suiteRow[1];
     * 
     * String hostName = "Unknown"; try { InetAddress addr; addr = InetAddress.getLocalHost(); hostName =
     * addr.getCanonicalHostName(); } catch (UnknownHostException ex) { }
     * 
     * String os = System.getProperty("os.name") + " " + System.getProperty("sun.arch.data.model") + "bit";
     * 
     * Object[] arrSuitesSummaryForEmail = new Object[] { suiteRow[0], 0, 0, 0, 0, hostName, os, browser };
     * suitesSummaryForEmail.add(arrSuitesSummaryForEmail);
     * 
     * int testIndex = 0; while (datas.size() > 0) { String[] row = datas.pollFirst(); // Check empty line boolean
     * isEmptyLine = true; for (String col : row) { if (col != null && !col.trim().equals("")) { isEmptyLine = false;
     * break; } } if (isEmptyLine && !datas.isEmpty()) { testIndex++; String[] testRow = datas.pollFirst(); String
     * testName = testIndex + "." + testRow[0]; newDatas.add(ArrayUtils.addAll(new String[] { suiteName, testName,
     * browser }, Arrays.copyOfRange(testRow, 2, testRow.length)));
     * 
     * String testStatus = testRow[5]; if (TestStatusValue.PASSED.toString().equals(testStatus)) {
     * arrSuitesSummaryForEmail[1] = (Integer) arrSuitesSummaryForEmail[1] + 1; } else if
     * (TestStatusValue.FAILED.toString().equals(testStatus)) { arrSuitesSummaryForEmail[2] = (Integer)
     * arrSuitesSummaryForEmail[2] + 1; } else if (TestStatusValue.ERROR.toString().equals(testStatus)) {
     * arrSuitesSummaryForEmail[3] = (Integer) arrSuitesSummaryForEmail[3] + 1; } else { arrSuitesSummaryForEmail[4] =
     * (Integer) arrSuitesSummaryForEmail[4] + 1; } } } }
     * 
     * File csvSummaryFile = new File(System.getProperty("java.io.tmpdir") + "Summary.csv"); if
     * (csvSummaryFile.exists()) { csvSummaryFile.delete(); } CsvWriter.writeArraysToCsv(newDatas, csvSummaryFile);
     * 
     * AbstractLauncher.sendSummaryEmail(csvSummaryFile, suitesSummaryForEmail);
     * 
     * }
     */

    public static String getReportFileName() {
        return reportFileName;
    }

    private static void setReportFileName(String reportFileName) {
        ConsoleMain.reportFileName = reportFileName;
    }
}
