package com.kms.katalon.execution.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.collector.ConsoleOptionCollector;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.console.entity.ConsoleMainOptionContributor;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.exception.InvalidConsoleArgumentException;
import com.kms.katalon.execution.integration.ReportIntegrationFactory;
import com.kms.katalon.execution.launcher.ConsoleLauncher;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LauncherResult;

public class ConsoleMain {
    public static final String ARGUMENT_SPLITTER = "=";

    public static final String ARGUMENT_PREFIX = "-";

    public static final String PROPERTIES_FILE_OPTION = "propertiesFile";

    public static final String PROJECT_PK_OPTION = "projectPath";

    public final static String TESTSUITE_ID_OPTION = "testSuitePath";

    public final static String BROWSER_TYPE_OPTION = "browserType";

    public static final int DEFAULT_SHOW_PROGRESS_DELAY = 15;

    public final static String SHOW_STATUS_DELAY_OPTION = "statusDelay";

    private int returnCode = LauncherResult.RETURN_CODE_PASSED;

    public void launch(String[] arguments) {
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();

        // Accept properties file option
        parser.accepts(PROPERTIES_FILE_OPTION).withRequiredArg().ofType(String.class);
        // Accept all of katalon console arguments
        acceptConsoleOptionList(parser, ConsoleOptionCollector.getInstance().getConsoleOptionList());

        try {
            OptionSet options = parser.parse(arguments);
            Map<String, String> consoleOptionValueMap = new HashMap<String, String>();
            ProjectEntity project = findProject(options);
            setDefaultExecutionPropertiesOfProject(project, consoleOptionValueMap);

            if (options.has(PROPERTIES_FILE_OPTION)) {
                readPropertiesFileAndSetToConsoleOptionValueMap(
                        String.valueOf(options.valueOf(PROPERTIES_FILE_OPTION)), consoleOptionValueMap);
            }

            TestSuiteExecutedEntity testSuiteExecutedEntity = new TestSuiteExecutedEntity();

            List<ConsoleOptionContributor> consoleOptionContributors = new ArrayList<ConsoleOptionContributor>();
            consoleOptionContributors.add(testSuiteExecutedEntity);
            populateConsoleOptionContributors(consoleOptionContributors);

            setConsoleArgumentToConsoleValueMap(options, consoleOptionContributors, consoleOptionValueMap);

            preCheckForRequiredArgumentFromConsoleMapValue(TESTSUITE_ID_OPTION, consoleOptionValueMap);
            preCheckForRequiredArgumentFromConsoleMapValue(BROWSER_TYPE_OPTION, consoleOptionValueMap);

            TestSuiteEntity testSuite = getTestSuite(project, consoleOptionValueMap.get(TESTSUITE_ID_OPTION));
            testSuiteExecutedEntity.setTestSuite(testSuite);
            
            // Set the arguments back to console options contributors
            returnConsoleArgumentToConsoleContributors(consoleOptionContributors, consoleOptionValueMap);

            IRunConfiguration runConfig = createRunConfiguration(project, testSuite,
                    consoleOptionValueMap.get(BROWSER_TYPE_OPTION));

            startExecutionStatusThread(options);

            launchTestSuite(testSuite, runConfig, testSuiteExecutedEntity);
        } catch (InvalidConsoleArgumentException e) {
            handleInvalidArgument(e.getMessage());
        } catch (Exception e) {
            handleError(e);
        }
    }

    private static ProjectEntity findProject(OptionSet options) throws Exception {
        String projectPath = null;
        if (options.has(PROJECT_PK_OPTION)) {
            projectPath = String.valueOf(options.valueOf(PROJECT_PK_OPTION));
        } else if (options.has(PROPERTIES_FILE_OPTION)) {
            projectPath = readPropertyFileAndGetProperty(String.valueOf(options.valueOf(PROPERTIES_FILE_OPTION)),
                    PROJECT_PK_OPTION);
        }
        if (projectPath == null) {
            throw new InvalidConsoleArgumentException(MessageFormat.format(
                    StringConstants.MNG_PRT_MISSING_REQUIRED_ARG, PROJECT_PK_OPTION));
        }
        return getProject(projectPath);
    }

    private static void populateConsoleOptionContributors(List<ConsoleOptionContributor> consoleOptionContributors) {
        consoleOptionContributors.add(new ConsoleMainOptionContributor());
        consoleOptionContributors.addAll(RunConfigurationCollector.getInstance().getConsoleOptionContributorList());
        consoleOptionContributors.addAll(ReportIntegrationFactory.getInstance().getConsoleOptionContributorList());
    }

    private static void setDefaultExecutionPropertiesOfProject(ProjectEntity project,
            Map<String, String> consoleOptionValueMap) throws IOException {
        ConsoleOptionCollector.getInstance().writeDefaultPropertyFile(project);
        readPropertiesFileAndSetToConsoleOptionValueMap(project.getFolderLocation() + File.separator
                + ConsoleOptionCollector.DEFAULT_EXECUTION_PROPERTY_FILE_NAME, consoleOptionValueMap);
    }

    private void handleError(Exception e) {
        System.out.println(e.getMessage());
        closeWorkbench(LauncherResult.RETURN_CODE_ERROR);
    }

    private void handleInvalidArgument(String message) {
        System.out.println(message);
        closeWorkbench(LauncherResult.RETURN_CODE_INVALID_ARGUMENT);
    }

    private static void setConsoleArgumentToConsoleValueMap(OptionSet options,
            List<ConsoleOptionContributor> consoleOptionContributors, Map<String, String> consoleOptionValueMap) {
        List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
        for (ConsoleOptionContributor consoleOptionContributor : consoleOptionContributors) {
            consoleOptionList.addAll(consoleOptionContributor.getConsoleOptionList());
        }
        for (ConsoleOption<?> consoleOption : consoleOptionList) {
            if (!options.has(consoleOption.getOption())) {
                continue;
            }
            String value = "";
            if (consoleOption.hasArgument()) {
                value = String.valueOf(options.valueOf(consoleOption.getOption()));
            }
            consoleOptionValueMap.put(consoleOption.getOption(), value);
        }
    }

    private static void returnConsoleArgumentToConsoleContributors(
            List<ConsoleOptionContributor> consoleOptionContributors, Map<String, String> consoleOptionValueMap)
            throws Exception {
        for (ConsoleOptionContributor consoleOptionContributor : consoleOptionContributors) {
            for (ConsoleOption<?> consoleOption : consoleOptionContributor.getConsoleOptionList()) {
                validateRequiredArgument(consoleOptionValueMap, consoleOption);
                if (consoleOptionValueMap.get(consoleOption.getOption()) == null) {
                    continue;
                }
                consoleOptionContributor.setArgumentValue(consoleOption,
                        consoleOptionValueMap.get(consoleOption.getOption()));
            }
        }
    }

    private static void validateRequiredArgument(Map<String, String> consoleOptionValueMap,
            ConsoleOption<?> consoleOption) throws InvalidConsoleArgumentException {
        if (isMissingRequiredArgument(consoleOptionValueMap, consoleOption)) {
            throw new InvalidConsoleArgumentException(MessageFormat.format(
                    StringConstants.MNG_PRT_MISSING_REQUIRED_ARG, consoleOption.getOption()));
        }
    }

    private static boolean isMissingRequiredArgument(Map<String, String> consoleOptionValueMap,
            ConsoleOption<?> consoleOption) {
        return consoleOptionValueMap.get(consoleOption.getOption()) == null && consoleOption.isRequired();
    }

    private static void readPropertiesFileAndSetToConsoleOptionValueMap(String fileLocation,
            Map<String, String> consoleOptionValueMap) throws IOException {
        if (validateFileLocation(fileLocation)) {
            return;
        }
        try (InputStream input = new FileInputStream(fileLocation)) {
            Properties prop = new Properties();
            prop.load(input);
            for (Entry<Object, Object> propertyEntry : prop.entrySet()) {
                // set the properties value
                consoleOptionValueMap.put(String.valueOf(propertyEntry.getKey()),
                        String.valueOf(propertyEntry.getValue()));
            }
        }
    }

    private static String readPropertyFileAndGetProperty(String fileLocation, String propertyName) throws IOException {
        if (validateFileLocation(fileLocation)) {
            return null;
        }
        try (InputStream input = new FileInputStream(fileLocation)) {
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty(propertyName);
        }
    }

    private static boolean validateFileLocation(String fileLocation) {
        return StringUtils.isBlank(fileLocation) || !new File(fileLocation).exists();
    }

    private static void acceptConsoleOptionList(OptionParser parser, List<ConsoleOption<?>> consoleOptionList) {
        for (ConsoleOption<?> consoleOption : consoleOptionList) {
            OptionSpecBuilder optionSpecBuilder = parser.accepts(consoleOption.getOption());
            if (consoleOption.hasArgument()) {
                optionSpecBuilder.withRequiredArg().ofType(consoleOption.getArgumentType());
            }
        }
    }

    private static void preCheckForRequiredArgumentFromConsoleMapValue(String option,
            Map<String, String> consoleOptionValueMap) throws InvalidConsoleArgumentException {
        if (option == null) {
            return;
        }
        if (consoleOptionValueMap.get(option) == null) {
            throw new InvalidConsoleArgumentException(MessageFormat.format(
                    StringConstants.MNG_PRT_MISSING_REQUIRED_ARG, option));
        }
    }

    private void startExecutionStatusThread(OptionSet options) {
        int progressDelay = DEFAULT_SHOW_PROGRESS_DELAY;
        if (options.has(SHOW_STATUS_DELAY_OPTION)) {
            String progressDelayString = String.valueOf(options.valueOf(SHOW_STATUS_DELAY_OPTION));
            try {
                progressDelay = Integer.valueOf(progressDelayString);
            } catch (NumberFormatException e) {
                System.out.println(MessageFormat.format(
                        StringConstants.MNG_PRT_INVALID_ARG_CANNOT_PARSE_X_FOR_Y_TO_INTEGER, progressDelayString,
                        SHOW_STATUS_DELAY_OPTION));
            }
        }
        startExecutionStatusThread(progressDelay);
    }

    private void startExecutionStatusThread(final int showProgressDelay) {
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
                closeWorkbench(exitCode);
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

    // For default, return error exit code
    public void closeWorkbench() {
        closeWorkbench(LauncherResult.RETURN_CODE_ERROR);
    }

    public void closeWorkbench(final int exitCode) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Thread interrupted, do nothing
        }
        LauncherManager.getInstance().removeAllTerminated();

        returnCode = exitCode;

        final IWorkbench workBench = PlatformUI.getWorkbench();
        final Display display = workBench.getDisplay();
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

    public int getReturnCode() {
        return returnCode;
    }

    private static void launchTestSuite(TestSuiteEntity testSuite, IRunConfiguration runConfig,
            TestSuiteExecutedEntity testSuiteExecutedEntity) throws Exception, InterruptedException {
        runConfig.build(testSuite, testSuiteExecutedEntity);
        ConsoleLauncher cslauncher = new ConsoleLauncher(runConfig);
        LauncherManager.getInstance().addLauncher(cslauncher);
        Thread.sleep(1000);
    }

    private static ProjectEntity getProject(String projectPk) throws Exception {
        ProjectEntity projectEntity = ProjectController.getInstance().openProject(projectPk);
        if (projectEntity == null) {
            throw new InvalidConsoleArgumentException(MessageFormat.format(
                    StringConstants.MNG_PRT_INVALID_ARG_CANNOT_FIND_PROJ_X, projectPk));
        }
        return projectEntity;
    }

    private static TestSuiteEntity getTestSuite(ProjectEntity projectEntity, String testSuiteID) throws Exception {
        TestSuiteEntity testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteID,
                projectEntity);

        if (testSuite == null) {
            throw new InvalidConsoleArgumentException(MessageFormat.format(
                    StringConstants.MNG_PRT_TEST_SUITE_X_NOT_FOUND, testSuiteID));
        }
        return testSuite;
    }

    private static IRunConfiguration createRunConfiguration(ProjectEntity projectEntity, TestSuiteEntity testSuite,
            String browserType) throws IOException, ExecutionException, InterruptedException,
            InvalidConsoleArgumentException {
        IRunConfiguration runConfig = RunConfigurationCollector.getInstance().getRunConfiguration(browserType,
                testSuite.getProject().getFolderLocation());

        if (runConfig == null) {
            throw new InvalidConsoleArgumentException(MessageFormat.format(StringConstants.MNG_PRT_INVALID_BROWSER_X,
                    browserType));
        }
        return runConfig;
    }
}
