package com.kms.katalon.platform.internal.controller;

import java.util.List;

import com.katalon.platform.api.controller.TestExecutionController;
import com.katalon.platform.api.exception.PlatformException;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.console.ConsoleExecutor;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.LauncherProviderFactory;
import com.kms.katalon.execution.launcher.ReportableLauncher;
import com.kms.katalon.execution.launcher.TestSuiteCollectionLauncher;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

public class TestExecutionControllerImpl implements TestExecutionController {

    @Override
    public void run(String[] args) throws PlatformException {
        try {
            ConsoleExecutor consoleExecutor = new ConsoleExecutor();
            OptionParser parser = new OptionParser(false);
            parser.allowsUnrecognizedOptions();

            acceptConsoleOptionList(parser, consoleExecutor.getAllConsoleOptions());

            OptionSet optionSet = parser.parse(args);
            ILauncher launcher = consoleExecutor.getUILauncher(ProjectController.getInstance().getCurrentProject(),
                    optionSet);
            LauncherProviderFactory.getInstance().getIdeLauncherProvider().launch(launcher);
        } catch (Exception e) {
            throw new PlatformException(e);
        }
    }

    private static void acceptConsoleOptionList(OptionParser parser, List<ConsoleOption<?>> consoleOptionList) {
        for (ConsoleOption<?> consoleOption : consoleOptionList) {
            OptionSpecBuilder optionSpecBuilder = parser.accepts(consoleOption.getOption());
            if (consoleOption.hasArgument()) {
                optionSpecBuilder.withRequiredArg().ofType(consoleOption.getArgumentType());
            }
        }
    }

    @Override
    public void run(String[] args, TestSuiteInstanceConfiguration testSuiteInstanceConfiguration)
            throws PlatformException {
        try {
            ConsoleExecutor consoleExecutor = new ConsoleExecutor();
            OptionParser parser = new OptionParser(false);
            parser.allowsUnrecognizedOptions();

            // Accept all of katalon console arguments
            acceptConsoleOptionList(parser, consoleExecutor.getAllConsoleOptions());

            OptionSet optionSet = parser.parse(args);
            ILauncher launcher = consoleExecutor.getUILauncher(ProjectController.getInstance().getCurrentProject(),
                    optionSet);
            if (testSuiteInstanceConfiguration != null) {
                if (launcher instanceof ReportableLauncher) {
                    ReportableLauncher reportableLauncher = (ReportableLauncher) launcher;
                    IRunConfiguration runConfig = reportableLauncher.getRunConfig();
                    addRunConfiguration(runConfig, testSuiteInstanceConfiguration);
                } else if (launcher instanceof TestSuiteCollectionLauncher) {
                    TestSuiteCollectionLauncher tscLauncher = (TestSuiteCollectionLauncher) launcher;
                    tscLauncher.getSubLaunchers().stream().forEach(subLauncher -> {
                        IRunConfiguration runConfig = subLauncher.getRunConfig();
                        addRunConfiguration(runConfig, testSuiteInstanceConfiguration);
                    });
                }
            }
            LauncherProviderFactory.getInstance().getIdeLauncherProvider().launch(launcher);
        } catch (Exception e) {
            throw new PlatformException(e);
        }
    }

    private void addRunConfiguration(IRunConfiguration runConfig,
            TestSuiteInstanceConfiguration testSuiteInstanceConfiguration) {
        runConfig.setVmArgs(testSuiteInstanceConfiguration.getVmArgs());
        runConfig.setAdditionalEnvironmentVariables(testSuiteInstanceConfiguration.getAdditionEnvironmentVariables());
        runConfig.setTestSuiteAdditionalData(testSuiteInstanceConfiguration.getAdditionalData());
    }
}
