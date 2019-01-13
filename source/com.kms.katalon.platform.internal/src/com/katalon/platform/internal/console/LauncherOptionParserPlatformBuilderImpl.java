package com.katalon.platform.internal.console;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.katalon.platform.api.extension.LauncherOptionParserDescription;
import com.katalon.platform.api.service.ApplicationManager;
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
import com.kms.katalon.platform.internal.entity.TestSuiteEntityImpl;

public class LauncherOptionParserPlatformBuilderImpl implements PlatformLauncherOptionParserBuilder {
	
	@Override
	public LauncherOptionParser getPluginLauncherOptionParser() {
		List<LauncherOptionParserDescription> launcherOptionParserDescriptions = ApplicationManager.getInstance()
				.getExtensionManager().getExtensions(LauncherOptionParserDescription.EXTENSION_POINT_ID).stream()
				.filter(a -> {
                    return (a.getImplementationClass() instanceof LauncherOptionParserDescription);
				})
				.map(b -> (LauncherOptionParserDescription) b.getImplementationClass())
				.collect(Collectors.toList());

		return new PluginTestSuiteLauncherOptionParser(launcherOptionParserDescriptions);
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
			
			pluginLauncherOptionParserDescriptions
			.stream()
			.filter(a -> a.getConsoleOptionList().contains(ConsoleAdapter.adapt(consoleOption)))
			.findFirst().get().onConsoleOptionDetected(ConsoleAdapter.adapt(consoleOption));
		}

		@Override
		public IConsoleLauncher getConsoleLauncher(ProjectEntity project, LauncherManager manager)
				throws InvalidConsoleArgumentException, ExecutionException {
			try {
				TestSuiteEntity testSuite = super.getTestSuite(project, testSuitePathOption.getValue());
				
				pluginLauncherOptionParserDescriptions
				.stream()
				.forEach(a -> a.preExecution(new TestSuiteEntityImpl(testSuite)));
				
				TestSuiteExecutedEntity executedEntity = new TestSuiteExecutedEntity(testSuite);
				executedEntity.setReportLocation(reportableSetting.getReportLocationSetting());
				executedEntity.setEmailConfig(reportableSetting.getEmailConfig(project));
				executedEntity.setRerunSetting(rerunSetting);
				executedEntity.prepareTestCases();
				AbstractRunConfiguration runConfig = (AbstractRunConfiguration) createRunConfiguration(project,
						testSuite, browserTypeOption.getValue());

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
				runConfig.build(testSuite, executedEntity);
				return new ConsoleLauncher(manager, runConfig);
			} catch (Exception e) {
				throw new ExecutionException(e);
			}
		}
	}
}
