package com.kms.katalon.execution.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.collector.ConsoleOptionCollector;
import com.kms.katalon.execution.console.entity.ConsoleMainOptionContributor;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.exception.InvalidConsoleArgumentException;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.result.LauncherResult;
import com.kms.katalon.logging.LogUtil;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

public class ConsoleMain {
    public static final String ARGUMENT_SPLITTER = "=";

    public static final String ARGUMENT_PREFIX = "-";

    public static final String PROPERTIES_FILE_OPTION = "propertiesFile";

    public static final String PROJECT_PK_OPTION = "projectPath";

    public final static String TESTSUITE_ID_OPTION = "testSuitePath";

    public final static String TESTSUITE_COLLECTION_ID_OPTION = "testSuiteCollectionPath";

    public final static String BROWSER_TYPE_OPTION = "browserType";

    public static final int DEFAULT_SHOW_PROGRESS_DELAY = 15;

    public final static String SHOW_STATUS_DELAY_OPTION = "statusDelay";

    private ConsoleMain() {
        // hide constructor
    }

    /**
     * Launch the console execution process
     * 
     * @param arguments
     * @return the exit code for the console execution
     */
    public static int launch(String[] arguments) {
        ConsoleExecutor consoleExecutor = new ConsoleExecutor();
        ApplicationConfigOptions applicationConfigOptions = new ApplicationConfigOptions();
        OptionParser parser = createParser(consoleExecutor, applicationConfigOptions);
        try {
            OptionSet options = parser.parse(arguments);
            Map<String, String> consoleOptionValueMap = new HashMap<String, String>();

            if (options.has(PROPERTIES_FILE_OPTION)) {
                readPropertiesFileAndSetToConsoleOptionValueMap(String.valueOf(options.valueOf(PROPERTIES_FILE_OPTION)),
                        consoleOptionValueMap);
                List<String> addedArguments = buildArgumentsForPropertiesFile(arguments, consoleOptionValueMap);
                options = parser.parse(addedArguments.toArray(new String[addedArguments.size()]));
            }
            
            // Set option value to application configuration
            for (ConsoleOption<?> opt : applicationConfigOptions.getConsoleOptionList()) {
                String optionName = opt.getOption();
                if (options.hasArgument(optionName)) {
                    applicationConfigOptions.setArgumentValue(opt, String.valueOf(options.valueOf(optionName)));
                }
            }

            ProjectEntity project = findProject(options);
//            Trackings.trackOpenApplication(project,
//                    !ActivationInfoCollector.isActivated(), "console");
            setDefaultExecutionPropertiesOfProject(project, consoleOptionValueMap);
            consoleExecutor.execute(project, options);

            waitForExecutionToFinish(options);

            List<ILauncher> consoleLaunchers = LauncherManager.getInstance().getSortedLaunchers();
            int exitCode = consoleLaunchers.get(consoleLaunchers.size() - 1).getResult().getReturnCode();
            return exitCode;
        } catch (InvalidConsoleArgumentException e) {
            LogUtil.printErrorLine(e.getMessage());
            return LauncherResult.RETURN_CODE_INVALID_ARGUMENT;
        } catch (Exception e) {
            LogUtil.printErrorLine(ExceptionUtils.getStackTrace(e));
            return LauncherResult.RETURN_CODE_ERROR;
        } finally {
            LauncherManager.getInstance().removeAllTerminated();
        }
    }

    private static List<String> buildArgumentsForPropertiesFile(String[] arguments,
            Map<String, String> consoleOptionValueMap) {
        List<String> addedArguments = new ArrayList<>(Arrays.asList(arguments));
        consoleOptionValueMap.forEach((key, value) -> {
            addedArguments.add("-" + key + "=" + value);
        });
        return addedArguments;
    }

    private static OptionParser createParser(ConsoleExecutor executor, ApplicationConfigOptions 
            applicationConfigOptions) {
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();

        // Accept properties file option
        parser.accepts(PROPERTIES_FILE_OPTION).withRequiredArg().ofType(String.class);
        // Accept all of katalon console arguments
        acceptConsoleOptionList(parser, new ConsoleMainOptionContributor().getConsoleOptionList());
        acceptConsoleOptionList(parser, executor.getAllConsoleOptions());

        OptionSpecBuilder configSpec = parser.accepts(applicationConfigOptions.getConfigOption());
        applicationConfigOptions.getConsoleOptionList().stream().forEach(consoleOption -> {
            OptionSpecBuilder optionSpecBuilder = parser.accepts(consoleOption.getOption()).availableIf(configSpec);
            if (consoleOption.hasArgument()) {
                optionSpecBuilder.withRequiredArg().ofType(consoleOption.getArgumentType());
            }
        });

        return parser;
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
            throw new InvalidConsoleArgumentException(
                    MessageFormat.format(StringConstants.MNG_PRT_MISSING_REQUIRED_ARG, PROJECT_PK_OPTION));
        }
        return getProject(projectPath);
    }

    private static void setDefaultExecutionPropertiesOfProject(ProjectEntity project,
            Map<String, String> consoleOptionValueMap) throws IOException, InvalidConsoleArgumentException {
        ConsoleOptionCollector.getInstance().writeDefaultPropertyFile(project);
        readPropertiesFileAndSetToConsoleOptionValueMap(project.getFolderLocation() + File.separator
                + ConsoleOptionCollector.DEFAULT_EXECUTION_PROPERTY_FILE_NAME, consoleOptionValueMap);
    }

    private static void readPropertiesFileAndSetToConsoleOptionValueMap(String fileLocation,
            Map<String, String> consoleOptionValueMap) throws IOException, InvalidConsoleArgumentException {
        if (!validateFileLocation(fileLocation)) {
            throw new InvalidConsoleArgumentException(
                    MessageFormat.format(ExecutionMessageConstants.MNG_PRT_INVALID_PROPERTY_FILE_ARG, fileLocation));
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
        return StringUtils.isNotBlank(fileLocation) && new File(fileLocation).exists();
    }

    private static void acceptConsoleOptionList(OptionParser parser, List<ConsoleOption<?>> consoleOptionList) {
        for (ConsoleOption<?> consoleOption : consoleOptionList) {
            OptionSpecBuilder optionSpecBuilder = parser.accepts(consoleOption.getOption());
            if (consoleOption.hasArgument()) {
                optionSpecBuilder.withRequiredArg().ofType(consoleOption.getArgumentType());
            }
        }
    }

    private static void waitForExecutionToFinish(OptionSet options) {
        int progressDelay = DEFAULT_SHOW_PROGRESS_DELAY;
        if (options.has(SHOW_STATUS_DELAY_OPTION)) {
            String progressDelayString = String.valueOf(options.valueOf(SHOW_STATUS_DELAY_OPTION));
            try {
                progressDelay = Integer.valueOf(progressDelayString);
            } catch (NumberFormatException e) {
                LogUtil.printErrorLine(
                        MessageFormat.format(StringConstants.MNG_PRT_INVALID_ARG_CANNOT_PARSE_X_FOR_Y_TO_INTEGER,
                                progressDelayString, SHOW_STATUS_DELAY_OPTION));
            }
        }
        waitForExecutionToFinish(progressDelay);
    }

    private static void waitForExecutionToFinish(final int showProgressDelay) {
        final int progressDelayTimeInMiliseconds = ((showProgressDelay < 0) ? DEFAULT_SHOW_PROGRESS_DELAY
                : showProgressDelay) * 1000;
        do {
            printStatus();
            try {
                Thread.sleep(progressDelayTimeInMiliseconds);
            } catch (InterruptedException e) {
                // Thread interrupted, do nothing
            }
        } while (LauncherManager.getInstance().isAnyLauncherRunning());
        printStatus();
    }

    private static void printStatus() {
        int consoleWidth = 80;
        LogUtil.printOutputLine(LauncherManager.getInstance().getStatus(consoleWidth));
    }

    private static ProjectEntity getProject(String projectPk) throws Exception {
        File projectFile = new File(projectPk);
        if (projectFile.isDirectory()) {
            for (File file : projectFile.listFiles()) {
                if (file.isFile() && file.getName().endsWith(ProjectEntity.getProjectFileExtension())) {
                    projectPk = file.getAbsolutePath();
                }
            }
        }
        ProjectEntity projectEntity = ProjectController.getInstance().openProject(projectPk);
        if (projectEntity == null) {
            throw new InvalidConsoleArgumentException(
                    MessageFormat.format(StringConstants.MNG_PRT_INVALID_ARG_CANNOT_FIND_PROJ_X, projectPk));
        }
        return projectEntity;
    }
}
