package com.kms.katalon.execution.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

import com.katalon.platform.internal.api.PluginInstaller;
import com.kms.katalon.application.constants.ApplicationMessageConstants;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.collector.ConsoleOptionCollector;
import com.kms.katalon.execution.console.entity.ConsoleMainOptionContributor;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.OverridingParametersConsoleOptionContributor;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.exception.InvalidConsoleArgumentException;
import com.kms.katalon.execution.handler.ApiKeyHandler;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.result.LauncherResult;
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.execution.util.LocalInformationUtil;
import com.kms.katalon.execution.util.OSUtil;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.TestOpsFeatureKey;
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

    public final static String INSTALL_PLUGIN_OPTION = "installPlugin";

    public final static String TESTSUITE_COLLECTION_ID_OPTION = "testSuiteCollectionPath";

    public final static String BROWSER_TYPE_OPTION = "browserType";

    public static final int DEFAULT_SHOW_PROGRESS_DELAY = 15;

    public final static String SHOW_STATUS_DELAY_OPTION = "statusDelay";

    public final static String TESTSUITE_QUERY = "testSuiteQuery";

    public static final String KATALON_API_KEY_OPTION = "apiKey";

    public static final String KATALON_API_KEY_SECOND_OPTION = "apikey";

    public static final String KATALON_ANALYTICS_LICENSE_FILE_OPTION = "license";

    public static final String KATALON_ANALYTICS_LICENSE_FILE_VAR = "KATALON_LICENSE";

    public static final String KATALON_ORGANIZATION_ID_OPTION = "orgId";

    public static final String KATALON_ORGANIZATION_ID_SECOND_OPTION = "orgID";

    public static final String EXECUTION_UUID_OPTION = "executionUUID";

    public static final String KATALON_ANALYTICS_PROJECT_ID = "analyticsProjectId";

    public static final String BUILD_LABEL_OPTION = "buildLabel";

    public static final String BUILD_URL_OPTION = "buildURL";

    public static final String KATALON_TESTOP_SERVER = "serverUrl";

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
        try {
            boolean isDevelopmentMode = Platform.inDevelopmentMode();
            boolean isRunningInKatalonC = ExecutionUtil.isRunningInKatalonC();
            if (!isDevelopmentMode && !isRunningInKatalonC) {
                String extension = OSUtil.getExecutableExtension();
                String katalon = "katalon" + extension;
                String katalonc = "katalonc" + extension;
                LogUtil.printErrorLine(MessageFormat.format(ExecutionMessageConstants.ACTIVATE_MOVE_TO_KATALONC, katalon, katalonc));
                return LauncherResult.RETURN_CODE_INVALID_ARGUMENT;
            }

            ConsoleExecutor consoleExecutor = new ConsoleExecutor();
            ApplicationConfigOptions applicationConfigOptions = new ApplicationConfigOptions();
            OptionParser parser = createParser(consoleExecutor, applicationConfigOptions);

            List<String> addedArguments = Arrays.asList(arguments);
            OptionSet options = parser.parse(arguments);
            Map<String, String> consoleOptionValueMap = new HashMap<String, String>();
            
            if (options.has(KATALON_TESTOP_SERVER)) {
                String serverUrl = String.valueOf(options.valueOf(KATALON_TESTOP_SERVER));
                ApplicationInfo.setTestOpsServer(serverUrl);
            }
            //Set server URL before show in log
            LocalInformationUtil.printSystemInformation();

            String apiKeyValue = null;
            if (options.has(KATALON_API_KEY_OPTION)) {
                apiKeyValue = String.valueOf(options.valueOf(KATALON_API_KEY_OPTION));
            }
            
            if (options.has(KATALON_API_KEY_SECOND_OPTION)) {
                apiKeyValue = String.valueOf(options.valueOf(KATALON_API_KEY_SECOND_OPTION));
            }

            LogUtil.logInfo(ExecutionMessageConstants.ACTIVATE_IN_ACTIVATING);
            
            if (!ActivationInfoCollector.isActivated()) {
                boolean isActivated = false;
                
                if (!isActivated) {
                    LogUtil.logInfo(ExecutionMessageConstants.ACTIVATE_START_ACTIVATE_OFFLINE);
                    StringBuilder errorMessage = new StringBuilder();
                    isActivated = ActivationInfoCollector.activateOfflineForEngine(errorMessage);

                    String error = errorMessage.toString();
                    if (StringUtils.isNotBlank(error)) {
                        LogUtil.printErrorLine(error);
                    }

                    if (!isActivated) {
                        LogUtil.printErrorLine(ExecutionMessageConstants.ACTIVATE_FAIL_OFFLINE);
                    }
                }
                
                if (!isActivated) {
                    LogUtil.logInfo(ExecutionMessageConstants.ACTIVATE_START_ACTIVATE_ONLINE);
                    StringBuilder errorMessage = new StringBuilder();
                    isActivated = ActivationInfoCollector.checkAndMarkActivatedForConsoleMode(apiKeyValue, errorMessage);

                    String error = errorMessage.toString();
                    if (StringUtils.isNotBlank(error)) {
                        LogUtil.printErrorLine(error);
                    }

                    if (!isActivated) {
                        LogUtil.printErrorLine(ExecutionMessageConstants.ACTIVATE_FAIL_ONLINE);
                    }
                }

                if (!isActivated) {
                    LogUtil.printErrorLine(ExecutionMessageConstants.ACTIVATE_FAIL_RUNTIME_ENGINE);
                    return LauncherResult.RETURN_CODE_FAILED_AND_ERROR;
                }
            }

            if (options.has(PROPERTIES_FILE_OPTION)) {
                readPropertiesFileAndSetToConsoleOptionValueMap(String.valueOf(options.valueOf(PROPERTIES_FILE_OPTION)),
                        consoleOptionValueMap);
                addedArguments = buildArgumentsForPropertiesFile(arguments, consoleOptionValueMap);
            }
            
            // Set option value to application configuration
            for (ConsoleOption<?> opt : applicationConfigOptions.getConsoleOptionList()) {
                String optionName = opt.getOption();
                if (options.hasArgument(optionName)) {
                    applicationConfigOptions.setArgumentValue(opt, String.valueOf(options.valueOf(optionName)));
                }
            }

            boolean isCliEnabled = FeatureServiceConsumer.getServiceInstance().canUse(TestOpsFeatureKey.CLI);
            if (!isCliEnabled) {
                LogUtil.printErrorLine(ExecutionMessageConstants.RE_DONT_PERMISSION_TO_USE);
                return LauncherResult.RETURN_CODE_INVALID_ARGUMENT;
            }

            ProjectEntity project = findProject(options);
            // Trackings.trackOpenApplication(project,
            // !ActivationInfoCollector.isActivated(), "console");
            setDefaultExecutionPropertiesOfProject(project, consoleOptionValueMap);
            
            if (apiKeyValue != null) {
                ApiKeyHandler.setApiKeyToProject(apiKeyValue);
            }
            
            reloadPlugins(apiKeyValue);
            consoleExecutor.addAndPrioritizeLauncherOptionParser(LauncherOptionParserFactory.getInstance().getBuilders().stream()
                    .map(a -> a.getPluginLauncherOptionParser()).collect(Collectors.toList()));
            acceptConsoleOptionList(parser, consoleExecutor.getAllConsoleOptions());

            // If a plug-in is installed, then add plug-in launcher option parser and re-accept the console options
            if (options.has(INSTALL_PLUGIN_OPTION)) {
                installPlugin(String.valueOf(options.valueOf(INSTALL_PLUGIN_OPTION)));
                consoleExecutor.addAndPrioritizeLauncherOptionParser(LauncherOptionParserFactory.getInstance().getBuilders().stream()
                    .map(a -> a.getPluginLauncherOptionParser()).collect(Collectors.toList()));
                acceptConsoleOptionList(parser, consoleExecutor.getAllConsoleOptions());
            }
            
            // installBasicReportPluginIfNotAvailable();

            // Project information is necessary to accept overriding parameters for that project
            acceptConsoleOptionList(parser,
                    new OverridingParametersConsoleOptionContributor(project).getConsoleOptionList());

            // Parse all arguments before execute
            options = parser.parse(addedArguments.toArray(new String[addedArguments.size()]));

            Map<String, String> localStore = new HashMap<>();
            localStore.put(KATALON_API_KEY_OPTION, apiKeyValue);
            localStore.put("lastActivateErrorMessage", ActivationInfoCollector.DEFAULT_REASON);
            ActivationInfoCollector.scheduleCheckLicense(() -> {
                String lastActivateErrorMessage = localStore.get("lastActivateErrorMessage");
                LogUtil.printErrorLine(MessageFormat.format(ApplicationMessageConstants.LICENSE_EXPIRED_MESSAGE, lastActivateErrorMessage));
                LauncherManager.getInstance().stopAllLauncher();
            }, () -> {
                StringBuilder errorMessage = new StringBuilder();
                String apiKey = localStore.get(KATALON_API_KEY_OPTION);
                ActivationInfoCollector.checkAndMarkActivatedForConsoleMode(apiKey, errorMessage);

                String error = errorMessage.toString();
                if (StringUtils.isNotBlank(error)) {
                    LogUtil.printErrorLine(error);
                    localStore.put("lastActivateErrorMessage", error);
                }
            });
            
            consoleExecutor.execute(project, options);

            waitForExecutionToFinish(options);

            List<ILauncher> consoleLaunchers = LauncherManager.getInstance().getSortedLaunchers();
            
            int exitCode = consoleLaunchers.get(consoleLaunchers.size() - 1).getResult().getReturnCode();
            LogUtil.logInfo(MessageFormat.format(ExecutionMessageConstants.RE_EXECUTE_COMPLETED, exitCode));

            ActivationInfoCollector.postEndSession();
            ActivationInfoCollector.releaseLicense();
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
    
    private static String getLicenseFilePath(OptionSet options) {
        String licenseFile = null;
        String environmentVariable = System.getenv(KATALON_ANALYTICS_LICENSE_FILE_VAR);
        if (options.has(KATALON_ANALYTICS_LICENSE_FILE_OPTION)) {
            licenseFile = String.valueOf(options.valueOf(KATALON_ANALYTICS_LICENSE_FILE_OPTION));
            LogUtil.logInfo(MessageFormat.format(ExecutionMessageConstants.ACTIVATE_LICENSE_FILE_FROM_OPTIONS, licenseFile));
        } else if (environmentVariable != null) {
            licenseFile = environmentVariable;
            LogUtil.logInfo(MessageFormat.format(ExecutionMessageConstants.ACTIVATE_LICENSE_FILE_FROM_ENVIRONMENT, licenseFile));
        } else {
            licenseFile = readLicenseFromDefaultLocation();
            LogUtil.logInfo(MessageFormat.format(ExecutionMessageConstants.ACTIVATE_LICENSE_FILE_DEFAULT_PATH, licenseFile));
        }
        return licenseFile;
    }
    
    private static String readLicenseFromDefaultLocation() {
        File defaultLicenseFile = new File(ApplicationInfo.userDirLocation() + "/license/katalon.lic");
        return defaultLicenseFile.exists() ? defaultLicenseFile.getAbsolutePath() : "";
    }

    private static void reloadPlugins(String apiKey) throws Exception {
        Bundle katalonBundle = Platform.getBundle("com.kms.katalon");
        Class<?> reloadPluginsHandlerClass = katalonBundle
                .loadClass("com.kms.katalon.composer.handlers.ConsoleModeReloadPluginsHandler");
        Object handler = reloadPluginsHandlerClass.newInstance();
        Method reloadMethod = Arrays.asList(reloadPluginsHandlerClass.getMethods()).stream()
                .filter(method -> method.getName().equals("reload"))
                .findAny()
                .orElse(null);
        if (reloadMethod != null) {
            reloadMethod.invoke(handler, apiKey);
        }
    }
    
    private static void installBasicReportPluginIfNotAvailable() throws Exception {
        Bundle katalonBundle = Platform.getBundle("com.kms.katalon");
        Class<?> installBasicReportPluginHandlerClass = katalonBundle
                .loadClass("com.kms.katalon.composer.handlers.InstallBasicReportPluginHandler");
        Object handler = installBasicReportPluginHandlerClass.newInstance();
        Method method = installBasicReportPluginHandlerClass.getMethod("installIfNotAvailable");
        if (method != null) {
            method.invoke(handler);
        }
    }

    private static void installPlugin(String filePath) throws InterruptedException, BundleException {
        BundleContext context = Platform.getBundle("com.katalon.platform").getBundleContext();
        ServiceReference<PluginInstaller> serviceReference = context
                .getServiceReference(PluginInstaller.class);
        PluginInstaller pluginInstaller = context.getService(serviceReference);
        if (!filePath.equals("")) {
            pluginInstaller.installPlugin(context, new File(filePath).toURI().toString());
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
            LogUtil.printOutputLine(StringConstants.MNG_PRT_PROJECT_PATH_IS_FOLDER);
            File[] projectFiles = projectFile.listFiles();
            if (projectFiles != null) {
                for (File file : projectFiles) {
                    if (file.isFile()) {
                        LogUtil.printOutputLine(MessageFormat.format(StringConstants.MNG_PR_EXAMINE_FILE, file.getName()));
                    } else {
                        LogUtil.printOutputLine(MessageFormat.format(StringConstants.MNG_PR_EXAMINE_FOLDER, file.getName()));
                    }
                    if (file.isFile() && file.getName().endsWith(ProjectEntity.getProjectFileExtension())) {
                        projectPk = file.getAbsolutePath();
                        LogUtil.printOutputLine(MessageFormat.format(StringConstants.MNG_PRT_FOUND_PROJECT_FILE, projectPk));
                    }
                }
            }
        }
        deleteLibFolders(projectPk);
        boolean allowSourceAttachment = false;
        ProjectEntity projectEntity = ProjectController.getInstance().openProject(projectPk, allowSourceAttachment);
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.PROJECT_OPENED, null);
        if (projectEntity == null) {
            throw new InvalidConsoleArgumentException(
                    MessageFormat.format(StringConstants.MNG_PRT_INVALID_ARG_CANNOT_FIND_PROJ_X, projectPk));
        }
        return projectEntity;
    }

    private static void deleteLibFolders(String projectPk) {
        try {
            File projectFile = new File(projectPk);
            if (projectFile.isFile() && projectFile.exists()) {
                File projectFolder = projectFile.getParentFile();
                if (projectFolder.exists()) {
                    deleteLibFolder(projectFolder, "bin");
                    deleteLibFolder(projectFolder, "Libs");
                }
            }
        } catch (Throwable t) {
            LogUtil.printAndLogError(t, ExecutionMessageConstants.RE_ERROR_DELETE_LIB_FOLDERS);
        }
    }

    private static void deleteLibFolder(File projectFolder, String libFolderName) {
        File libFolder = new File(projectFolder, libFolderName);
        if (libFolder.exists()) {
            LogUtil.printOutputLine(MessageFormat.format(ExecutionMessageConstants.RE_DELETE_FOLDER, libFolderName));
            try {
                FileUtils.forceDelete(libFolder);
            } catch (IOException e) {
                LogUtil.printAndLogError(e, MessageFormat.format(ExecutionMessageConstants.RE_ERROR_DELETE_FOLDERS, libFolderName));
            }
        }
    }
}