package com.kms.katalon.execution.launcher.manager;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
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
import com.kms.katalon.execution.entity.ConsoleOption;
import com.kms.katalon.execution.entity.DefaultRerunSetting;
import com.kms.katalon.execution.entity.ReportLocationSetting;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.integration.ReportIntegrationFactory;
import com.kms.katalon.execution.launcher.ConsoleLauncher;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.model.LauncherResult;
import com.kms.katalon.execution.util.ExecutionUtil;

public class ConsoleMain {
    // Optional but must be process first
    private final static String CONF_FILE_NAME_OPTION = "confFile";

    // Mandatory
    public static final String RUN_MODE_OPTION = "runMode";
    public static final String PROJECT_PK_OPTION = "projectPath";
    public final static String TESTSUITE_ID_OPTION = "testSuitePath";
    public final static String BROWSER_TYPE_OPTION = "browserType";

    // Optional
    public final static String SEND_EMAIL_OPTION = "sendMail";
    public final static String REPORT_FOLDER_OPTION = "reportFolder";
    public final static String REPORT_FILE_NAME_OPTION = "reportFileName";
    private final static String CLEAN_REPORT_FOLDER = "cleanReportFolder";
    private final static String RETRY_OPTION = "retry";
    private final static String RETRY_FAIL_TEST_CASE_ONLY_OPTION = "retryFailedTestCases";
    public final static String SHOW_STATUS_DELAY_OPTION = "statusDelay";

    public static final String ARGUMENT_SPLITTER = "=";
    public static final String ARGUMENT_PREFIX = "-";
    public static final int DEFAULT_SHOW_PROGRESS_DELAY = 15;
    private static final String DEFAULT_REPORT_FOLDER_NAME = "";

    private static int returnCode = 0;

    public static void launch(String[] arguments) {
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();

        // Optional but must be process first
        ArgumentAcceptingOptionSpec<String> configFileOptionSpec = acceptStringArgument(parser, CONF_FILE_NAME_OPTION);

        // Mandatory
        acceptStringArgument(parser, RUN_MODE_OPTION);
        ArgumentAcceptingOptionSpec<String> projectPkOptionSpec = acceptStringArgument(parser, PROJECT_PK_OPTION);
        ArgumentAcceptingOptionSpec<String> testSuiteIdOptionSpec = acceptStringArgument(parser, TESTSUITE_ID_OPTION);

        // Optional
        ArgumentAcceptingOptionSpec<String> browserTypeOptionSpec = acceptStringArgument(parser, BROWSER_TYPE_OPTION);
        ArgumentAcceptingOptionSpec<Integer> showStatusDelayOptionSpec = acceptIntegerArgument(parser,
                SHOW_STATUS_DELAY_OPTION).defaultsTo(DEFAULT_SHOW_PROGRESS_DELAY);
        ArgumentAcceptingOptionSpec<String> reportFolderOptionSpec = acceptStringArgument(parser, REPORT_FOLDER_OPTION)
                .defaultsTo(DEFAULT_REPORT_FOLDER_NAME);
        ArgumentAcceptingOptionSpec<Boolean> cleanReportFolderOptionSpec = acceptBooleanArgument(parser,
                CLEAN_REPORT_FOLDER).defaultsTo(ReportLocationSetting.DEFAULT_CLEAN_REPORT_FOLDER_FLAG);
        ArgumentAcceptingOptionSpec<String> reportFileNameOptionSpec = acceptStringArgument(parser,
                REPORT_FILE_NAME_OPTION).defaultsTo(ReportLocationSetting.DEFAULT_REPORT_FILE_NAME);
        ArgumentAcceptingOptionSpec<Integer> retryOptionSpec = acceptIntegerArgument(parser, RETRY_OPTION);
        ArgumentAcceptingOptionSpec<Boolean> retryFailedTestCaseOptionSpec = acceptBooleanArgument(parser,
                RETRY_FAIL_TEST_CASE_ONLY_OPTION);
        ArgumentAcceptingOptionSpec<String> emailOptionSpec = acceptStringArgument(parser, SEND_EMAIL_OPTION);

        // Additional arguments for run configurations
        acceptConsoleOptionList(parser, RunConfigurationCollector.getInstance().getAllAddionalRequiredArguments());

        // Additional arguments for integration
        acceptConsoleOptionList(parser, ReportIntegrationFactory.getInstance().getIntegrationCommands());

        OptionSet options = parseOptions(arguments, parser);
        if (options == null) {
            return;
        }
        options = processConfigFileParam(parser, configFileOptionSpec, options);

        if (!preCheckForRequiredArgument(options, projectPkOptionSpec,
                MessageFormat.format(StringConstants.MNG_PRT_MISSING_REQUIRED_ARG, PROJECT_PK_OPTION))) {
            return;
        }
        if (!preCheckForRequiredArgument(options, testSuiteIdOptionSpec,
                MessageFormat.format(StringConstants.MNG_PRT_MISSING_REQUIRED_ARG, TESTSUITE_ID_OPTION))) {
            return;
        }
        if (!preCheckForRequiredArgument(options, browserTypeOptionSpec,
                MessageFormat.format(StringConstants.MNG_PRT_MISSING_REQUIRED_ARG, BROWSER_TYPE_OPTION))) {
            return;
        }

        ProjectEntity project = getProject(options.valueOf(projectPkOptionSpec));
        if (project == null) {
            return;
        }

        TestSuiteEntity testSuite = getTestSuite(project, testSuiteIdOptionSpec.value(options));
        if (testSuite == null) {
            return;
        }

        // Set the arguments back to run configurations
        setArgumentToConsoleOptionList(options, RunConfigurationCollector.getInstance()
                .getAllAddionalRequiredArguments());

        IRunConfiguration runConfig = createRunConfiguration(project, testSuite, browserTypeOptionSpec.value(options),
                options);
        if (runConfig == null) {
            return;
        }

        DefaultRerunSetting rerunSetting = new DefaultRerunSetting(0, testSuite.getNumberOfRerun(),
                testSuite.isRerunFailedTestCasesOnly());
        if (options.has(retryOptionSpec)) {
            rerunSetting.setRemainingRerunTimes(retryOptionSpec.value(options));
        }
        if (options.has(retryFailedTestCaseOptionSpec)) {
            rerunSetting.setRerunFailedTestCaseOnly(retryFailedTestCaseOptionSpec.value(options));
        }

        ReportLocationSetting reportLocSetting = new ReportLocationSetting();
        reportLocSetting.setCleanReportFolder(cleanReportFolderOptionSpec.value(options));
        reportLocSetting.setReportFileName(reportFileNameOptionSpec.value(options));
        reportLocSetting.setReportFolderPath(reportFolderOptionSpec.value(options));
        
        String emailRecipients = "";
        if (options.has(emailOptionSpec)) {
            emailRecipients = emailOptionSpec.value(options);
        }

        // Set the arguments back to integration services
        setArgumentToConsoleOptionList(options, ReportIntegrationFactory.getInstance().getIntegrationCommands());

        startExecutionStatusThread(showStatusDelayOptionSpec.value(options));

        try {
            launchTestSuite(testSuite, runConfig, rerunSetting, reportLocSetting, emailRecipients);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            closeWorkbench(LauncherResult.RETURN_CODE_ERROR);
            return;
        }
    }

    private static void setArgumentToConsoleOptionList(OptionSet options, List<ConsoleOption<?>> consoleOptionList) {
        for (ConsoleOption<?> consoleOption : consoleOptionList) {
            if (options.has(consoleOption.getOption())) {
                consoleOption.setEnable();
                if (consoleOption.hasArgument()) {
                    consoleOption.setArgumentValue(String.valueOf(options.valueOf(consoleOption.getOption())));
                }
            }
        }
    }

    private static void acceptConsoleOptionList(OptionParser parser, List<ConsoleOption<?>> consoleOptionList) {
        for (ConsoleOption<?> consoleOption : consoleOptionList) {
            acceptArgument(parser, consoleOption.getOption(), consoleOption.getArgumentType());
        }
    }

    private static ArgumentAcceptingOptionSpec<String> acceptStringArgument(OptionParser parser, String optionName) {
        return parser.accepts(optionName).withRequiredArg().ofType(String.class);
    }

    private static ArgumentAcceptingOptionSpec<Integer> acceptIntegerArgument(OptionParser parser, String optionName) {
        return parser.accepts(optionName).withRequiredArg().ofType(Integer.class);
    }

    private static ArgumentAcceptingOptionSpec<Boolean> acceptBooleanArgument(OptionParser parser, String optionName) {
        return parser.accepts(optionName).withRequiredArg().ofType(Boolean.class);
    }

    private static ArgumentAcceptingOptionSpec<?> acceptArgument(OptionParser parser, String optionName,
            Class<?> argumentType) {
        return parser.accepts(optionName).withRequiredArg().ofType(argumentType);
    }

    private static OptionSet processConfigFileParam(OptionParser parser,
            ArgumentAcceptingOptionSpec<String> configFileOptionSpec, OptionSet options) {
        if (!options.has(configFileOptionSpec)) {
            return options;
        }
        List<String> params = null;
        try {
            params = parseXmlConfFile(configFileOptionSpec.value(options));
        } catch (DocumentException e) {
            System.out.println(e.getMessage());
            closeWorkbench(LauncherResult.RETURN_CODE_ERROR);
            return null;
        }
        if (params == null || params.size() <= 0) {
            System.out.println(StringConstants.MNG_INVALID_CONF_FILE_NAME_ARG);
            closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
            return null;
        }

        // parse again for confFile
        options = parseOptions(params.toArray(new String[params.size()]), parser);
        return options;
    }

    private static boolean preCheckForRequiredArgument(OptionSet options,
            ArgumentAcceptingOptionSpec<?> argumentOptionSpec, String errorMessage) {
        if (options == null || argumentOptionSpec == null) {
            return false;
        }
        if (!options.has(argumentOptionSpec)) {
            System.out.println(errorMessage);
            closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
            return false;
        }
        return true;
    }

    private static OptionSet parseOptions(String[] arguments, OptionParser parser) {
        try {
            return parser.parse(arguments);
        } catch (OptionException optionException) {
            System.out.println(optionException.getMessage());
            closeWorkbench(LauncherResult.RETURN_CODE_ERROR);
        }
        return null;
    }

    private static void startExecutionStatusThread(final int showProgressDelay) {
        final int progressDelayTimeInMiliseconds = ((showProgressDelay < 0) ? DEFAULT_SHOW_PROGRESS_DELAY
                : showProgressDelay) * 1000;
        Thread launcherStatusThread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    printStatus();
                    try {
                        Thread.sleep(progressDelayTimeInMiliseconds);
                    } catch (InterruptedException e) {
                        // Thread interrupted, do nothing
                    }
                } while (LauncherManager.getInstance().isAnyLauncherRunning());
                printStatus();
                List<ILauncher> consoleLaunchers = LauncherManager.getInstance().getSortedLaunchers();
                int exitCode = consoleLaunchers.get(consoleLaunchers.size() - 1).getResult().getReturnCode();
                ConsoleMain.closeWorkbench(exitCode);

            }

            private void printStatus() {
                int consoleWidth = 80;
                System.out.println();
                for (int i = 0; i < consoleWidth; i++) {
                    System.out.print(ARGUMENT_PREFIX);
                }
                System.out.println();

                for (ILauncher launcher : LauncherManager.getInstance().getSortedLaunchers()) {
                    StringBuilder builder = new StringBuilder(launcher.getName());
                    String launcherStatus = launcher.getResult().getExecutedTestCases() + "/"
                            + launcher.getResult().getTotalTestCases();
                    builder.append(launcherStatus);
                    builder.insert(launcher.getName().length(),
                            StringUtils.repeat(".", consoleWidth - builder.length() % consoleWidth));
                    System.out.println(wrap(builder.toString(), consoleWidth));
                }

                for (int i = 0; i < consoleWidth; i++) {
                    System.out.print(ARGUMENT_PREFIX);
                }
                System.out.println("\n");
            }
        });
        launcherStatusThread.start();
    }

    // For default, return error exit code
    public static void closeWorkbench() {
        closeWorkbench(LauncherResult.RETURN_CODE_ERROR);
    }

    public static void closeWorkbench(final int exitCode) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Thread interrupted, do nothing
        }
        LauncherManager.getInstance().removeAllTerminated();

        returnCode = exitCode;

        final IWorkbench workBench = PlatformUI.getWorkbench();
        final Display display = PlatformUI.getWorkbench().getDisplay();
        display.syncExec(new Runnable() {
            public void run() {
                if (display.isDisposed()) {
                    return;
                }
                System.out.println(StringConstants.MNG_PRT_CLOSING_WORKBENCH);
                workBench.close();
                System.out.println(StringConstants.MNG_PRT_WORKBENCH_CLOSED);
            }
        });
    }

    public static int getReturnCode() {
        return returnCode;
    }

    public static void launchTestSuite(TestSuiteEntity testSuite, IRunConfiguration runConfig,
            DefaultRerunSetting rerunSetting, ReportLocationSetting reportLocation,
            String mailRecipients) throws Exception, InterruptedException {
        TestSuiteExecutedEntity testSuiteExecutedEntity = ExecutionUtil.loadTestDataForTestSuite(testSuite,
                testSuite.getProject());
        testSuiteExecutedEntity.setReportLocation(reportLocation);

        // if user didn't config rerun, use default rerun settings of test suite
        if (rerunSetting != null) {
            testSuiteExecutedEntity.setRerunSetting(rerunSetting);
        }
        
        if (StringUtils.isNotEmpty(mailRecipients)) {
            testSuiteExecutedEntity.getEmailConfig().addRecipients(mailRecipients);
        }

        runConfig.build(testSuite, testSuiteExecutedEntity);
        ConsoleLauncher cslauncher = new ConsoleLauncher(runConfig);
        LauncherManager.getInstance().addLauncher(cslauncher);

        Thread.sleep(1000);
    }

    private static ProjectEntity getProject(String projectPk) {
        ProjectEntity projectEntity = null;
        try {
            projectEntity = ProjectController.getInstance().openProject(projectPk);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            closeWorkbench(LauncherResult.RETURN_CODE_ERROR);
        }
        if (projectEntity == null) {
            System.out.println(MessageFormat.format(StringConstants.MNG_PRT_INVALID_ARG_CANNOT_FIND_PROJ_X, projectPk));
            closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
        }
        return projectEntity;
    }

    private static TestSuiteEntity getTestSuite(ProjectEntity projectEntity, String testSuiteID) {
        TestSuiteEntity testSuite = null;
        try {
            testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteID, projectEntity);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            closeWorkbench(LauncherResult.RETURN_CODE_ERROR);
            return null;
        }
        if (testSuite == null) {
            System.out.println(MessageFormat.format(StringConstants.MNG_PRT_TEST_SUITE_X_NOT_FOUND, testSuiteID));
            closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
            return null;
        }
        return testSuite;
    }

    private static IRunConfiguration createRunConfiguration(ProjectEntity projectEntity, TestSuiteEntity testSuite,
            String browserType, OptionSet options) {
        IRunConfiguration runConfig = null;
        try {
            runConfig = RunConfigurationCollector.getInstance().getRunConfiguration(browserType,
                    testSuite.getProject().getFolderLocation());
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            closeWorkbench(LauncherResult.RETURN_CODE_ERROR);
            return null;
        }

        if (runConfig == null) {
            System.out.println(MessageFormat.format(StringConstants.MNG_PRT_INVALID_BROWSER_X, browserType));
            closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
            return null;
        }
        return runConfig;
    }

    private static String wrap(String longString, int maxWidth) {
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

    private static List<String> parseXmlConfFile(String filePath) throws DocumentException {
        File confFile = new File(filePath);
        if (filePath == null || filePath.isEmpty() || !confFile.isFile()) {
            System.out.println(StringConstants.MNG_INVALID_CONF_FILE_NAME_ARG);
            closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
            return null;
        }
        List<String> params = new ArrayList<String>();
        SAXReader reader = new SAXReader();
        Document document = reader.read(confFile);
        // Root element should be "parameters" with children nodes "parameter"
        Element rootElement = document.getRootElement();
        for (Object objElement : rootElement.elements("parameter")) {
            Element pElement = (Element) objElement;
            Element pElementName = pElement.element("name");
            Element pElementValue = pElement.element("value");
            if (pElementName == null) {
                System.out.println(StringConstants.MNG_INVALID_CONF_FILE_NAME_ARG);
                closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
                return null;
            }
            if (pElementValue == null || pElementValue.getText().equalsIgnoreCase("true")) {
                params.add(ARGUMENT_PREFIX + pElementName.getText());
                @SuppressWarnings("unchecked")
                List<Object> subParams = pElement.elements("sub-parameter");
                if (subParams.isEmpty()) {
                    continue;
                }
                for (Object subParam : subParams) {
                    Element subParamName = ((Element) subParam).element("name");
                    Element subParamValue = ((Element) subParam).element("value");
                    if (subParamName == null) {
                        System.out.println(StringConstants.MNG_INVALID_CONF_FILE_NAME_ARG);
                        closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
                        return null;
                    }
                    String paramName = subParamName.getText();
                    if (subParamValue != null && !subParamValue.getText().equalsIgnoreCase("true")) {
                        paramName += ARGUMENT_SPLITTER + subParamValue.getText();
                    }
                    params.add(paramName);
                }
                continue;
            }
            if (pElementValue.getText().equalsIgnoreCase("false")) {
                // Ignore this parameter
                continue;
            }
            params.add(ARGUMENT_PREFIX + pElementName.getText() + ARGUMENT_SPLITTER + pElementValue.getText());
        }
        return params;
    }
}
