package com.katalon.platform.internal.console;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.katalon.platform.api.controller.TestCaseController;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.extension.LauncherOptionParserDescription;
import com.katalon.platform.api.model.TestCaseEntity;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.LauncherOptionParser;
import com.kms.katalon.execution.console.entity.TestSuiteLauncherOptionParser;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.exception.InvalidConsoleArgumentException;
import com.kms.katalon.execution.launcher.ConsoleLauncher;
import com.kms.katalon.execution.launcher.IConsoleLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.platform.PlatformLauncherOptionParserBuilder;
import com.kms.katalon.execution.util.ConsoleAdapter;

public class LauncherOptionParserPlatformBuilderImpl implements PlatformLauncherOptionParserBuilder {
	
	@Override
	public LauncherOptionParser getPluginLauncherOptionParser() {
		List<LauncherOptionParserDescription> launcherOptionParserDescriptions = ApplicationManager.getInstance()
				.getExtensionManager().getExtensions(LauncherOptionParserDescription.EXTENSION_POINT_ID).stream()
				.filter(a -> {
					return (a.getImplementationClass() instanceof LauncherOptionParserDescription);
				}).map(b -> (LauncherOptionParserDescription) b.getImplementationClass()).collect(Collectors.toList());

		if(launcherOptionParserDescriptions != null && !launcherOptionParserDescriptions.isEmpty())
			return new PluginTestSuiteLauncherOptionParser(launcherOptionParserDescriptions);
		else
			return new TestSuiteLauncherOptionParser();
	}
	
	public class PluginTestSuiteLauncherOptionParser extends TestSuiteLauncherOptionParser {
		private List<LauncherOptionParserDescription> pluginLauncherOptionParserDescriptions;

		public PluginTestSuiteLauncherOptionParser(
				List<LauncherOptionParserDescription> pluginLauncherOptionParserDescriptions) {
			this.pluginLauncherOptionParserDescriptions = pluginLauncherOptionParserDescriptions;
		}

		@Override
		public List<ConsoleOption<?>> getConsoleOptionList() {
			List<ConsoleOption<?>> allOptions = super.getConsoleOptionList();
			allOptions.addAll(pluginLauncherOptionParserDescriptions.stream().map(a -> a.getConsoleOptionList())
					.map(b -> ConsoleAdapter.adapt(b)).flatMap(List::stream).collect(Collectors.toList()));
			return allOptions;
		}

		@Override
		public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
			super.setArgumentValue(consoleOption, argumentValue);

			Optional<LauncherOptionParserDescription> matched = pluginLauncherOptionParserDescriptions.stream()
					.filter(b -> b.getConsoleOptionList()
							.stream()
							.map(c -> c.getOption())
							.collect(Collectors.toList())
							.contains(consoleOption.getOption())).findFirst();
			
			if (matched.isPresent()) {
				consoleOption.setValue(argumentValue);
				matched.get().onConsoleOptionDetected(ConsoleAdapter.adapt(consoleOption));
			}
		}

		@Override
		public IConsoleLauncher getConsoleLauncher(ProjectEntity project, LauncherManager manager)
				throws InvalidConsoleArgumentException, ExecutionException {
			try {
				TestSuiteEntity clonedTestSuite = super.getTestSuite(project, testSuitePathOption.getValue()).clone();
				
				clonedTestSuite = preExecution(clonedTestSuite);
				
				TestSuiteExecutedEntity executedEntity = new TestSuiteExecutedEntity(clonedTestSuite);
				executedEntity.setReportLocation(reportableSetting.getReportLocationSetting());
				executedEntity.setEmailConfig(reportableSetting.getEmailConfig(project));
				executedEntity.setRerunSetting(rerunSetting);
				executedEntity.prepareTestCases();
				AbstractRunConfiguration runConfig = (AbstractRunConfiguration) createRunConfiguration(project,
						clonedTestSuite, browserTypeOption.getValue());

				String profileName = executionProfileOption.getValue();
				if (StringUtils.isBlank(profileName)) {
					profileName = ExecutionProfileEntity.DF_PROFILE_NAME;
				}
				ExecutionProfileEntity executionProfile = GlobalVariableController.getInstance()
						.getExecutionProfile(profileName, project);
				if (executionProfile == null) {
					throw new ExecutionException(
							MessageFormat.format(ExecutionMessageConstants.CONSOLE_MSG_PROFILE_NOT_FOUND, profileName));
				}
				runConfig.setExecutionProfile(executionProfile);
				runConfig.setOverridingGlobalVariables(getOverridingGlobalVariables());
				runConfig.build(clonedTestSuite, executedEntity);
				return new ConsoleLauncher(manager, runConfig);
			} catch (Exception e) {
				throw new ExecutionException(e);
			}
		}
		
		private TestSuiteEntity preExecution(TestSuiteEntity testSuite) {
			TestCaseController testCaseController = ApplicationManager.getInstance().getControllerManager()
					.getController(TestCaseController.class);

			com.katalon.platform.api.model.ProjectEntity currentProject = ApplicationManager.getInstance()
					.getProjectManager().getCurrentProject();

			List<TestCaseEntity> testCases = testSuite.getTestSuiteTestCaseLinks().stream().map(a -> a.getTestCaseId())
					.map(b -> {
						try {
							return testCaseController.getTestCase(currentProject, b);
						} catch (ResourceException e) {
							LoggerSingleton.logError(e);
						}
						return null;
					}).filter(c -> (c != null)).collect(Collectors.toList());

			List<Set<TestCaseEntity>> listOfSetsOfTestCases = pluginLauncherOptionParserDescriptions.stream()
					.map(a -> a.onPreExecution(testCases)).map(b -> new HashSet<>(b)).collect(Collectors.toList());
			
			Set<String> intersection = listOfSetsOfTestCases.stream().collect(
					() -> new HashSet<TestCaseEntity>(listOfSetsOfTestCases.get(0)), Set::retainAll, Set::retainAll)
					.stream().map(b -> b.getId()).collect(Collectors.toSet());
			
			testSuite.setTestSuiteTestCaseLinks(testSuite.getTestSuiteTestCaseLinks()
					.stream().filter(a -> intersection.contains(a.getTestCaseId())).collect(Collectors.toList()));

			return testSuite;
		}
	}
}
