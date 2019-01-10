package com.kms.katalon.execution.console.entity;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.katalon.platform.api.extension.PluginConsoleOptionRegister;
import com.katalon.platform.api.model.PluginConsoleOption;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.TestCaseEntityImpl;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.exception.InvalidConsoleArgumentException;
import com.kms.katalon.execution.launcher.ConsoleLauncher;
import com.kms.katalon.execution.launcher.IConsoleLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.util.ConsoleAdapter;

public class TestSuiteLauncherOptionParser extends ReportableLauncherOptionParser {
    private static final String EXECUTION_PROFILE_OPTION = "executionProfile";
    
    private static final String OVERRIDING_GLOBAL_VARIABLE_PREFIX = "g_";
    
    private static final String PLUGIN_CONSOLE_OPTION_PREFIX = "plugin_";

    private List<ConsoleOption<?>> overridingOptions = new ArrayList<>();
    
    private Map<String, List<StringConsoleOption>> pluginConsoleOptionsMap = new HashMap<>();
    
    private StringConsoleOption testSuitePathOption = new StringConsoleOption() {
    	
        @Override
        public String getOption() {
            return ConsoleMain.TESTSUITE_ID_OPTION;
        }

        public boolean isRequired() {
            return true;
        }
    };

    private StringConsoleOption browserTypeOption = new StringConsoleOption() {
        @Override
        public String getOption() {
            return ConsoleMain.BROWSER_TYPE_OPTION;
        }

        public boolean isRequired() {
            return true;
        }
    };

    private StringConsoleOption executionProfileOption = new StringConsoleOption() {
		@Override
		public String getOption() {
			return EXECUTION_PROFILE_OPTION;
		}
		
    	public boolean isRequired() {
            return false;
        }

        @Override
        public String getDefaultArgumentValue() {
            return ExecutionProfileEntity.DF_PROFILE_NAME;
        }
    };

    
    @Override
    public synchronized List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> allOptions = super.getConsoleOptionList();
        allOptions.add(testSuitePathOption);
        allOptions.add(browserTypeOption);
        allOptions.add(executionProfileOption);
        collectOverridingGlobalVariablesContributor();
        collectPluginConsoleOptionContributors(IdConstants.PLUGIN_TAGS_2);
        allOptions.addAll(overridingOptions);
        pluginConsoleOptionsMap.entrySet().stream()
        		.map(a -> a.getValue())
        		.forEach(b -> allOptions.addAll(b));
        return allOptions;
    }

    private void collectPluginConsoleOptionContributors(String pluginId) {
		if(ApplicationManager.getInstance().getPluginManager().getPlugin(pluginId) != null){
			PluginConsoleOptionRegister tagsPluginConsoleRegister = 
					ApplicationManager.getInstance().getConsoleManager()
					.getRegisteredConsoleOption(pluginId);
			if(tagsPluginConsoleRegister != null && !pluginConsoleOptionsMap.containsKey(pluginId)){
				pluginConsoleOptionsMap.put(pluginId
						, ConsoleAdapter
						.adaptToStringConsoleOptions(tagsPluginConsoleRegister.getPluginConsoleOptionList()));
				System.out.println(pluginId + " registered console options");
			}
		}
	}

	@Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
		super.setArgumentValue(consoleOption, argumentValue);
		if (consoleOption == testSuitePathOption || consoleOption == browserTypeOption
				|| consoleOption == executionProfileOption) {
			consoleOption.setValue(argumentValue);
		} else if(overridingOptions.contains(consoleOption)){
			consoleOption.setValue(argumentValue);
		} else{
			for(Entry<String, List<StringConsoleOption>> entry : pluginConsoleOptionsMap.entrySet()){
				if(entry.getValue().contains(consoleOption)){
					consoleOption.setValue(argumentValue);
				}
			}
		}
    }
    
    @Override
    public IConsoleLauncher getConsoleLauncher(ProjectEntity project, LauncherManager manager)
            throws InvalidConsoleArgumentException, ExecutionException {
        try {
            TestSuiteEntity testSuite = getTestSuite(project, testSuitePathOption.getValue()).clone();
            
            filterTestCasesByPlugins(testSuite);
            
            TestSuiteExecutedEntity executedEntity = new TestSuiteExecutedEntity(testSuite);
            executedEntity.setReportLocation(reportableSetting.getReportLocationSetting());
            executedEntity.setEmailConfig(reportableSetting.getEmailConfig(project));
            executedEntity.setRerunSetting(rerunSetting);
            executedEntity.prepareTestCases();
            AbstractRunConfiguration runConfig = (AbstractRunConfiguration) createRunConfiguration(project, testSuite,
                    browserTypeOption.getValue());
            
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
    
    private void filterTestCasesByPlugins(TestSuiteEntity testSuite) throws ControllerException {
        List<com.katalon.platform.api.model.TestCaseEntity> testCases = getTestCasesFromTestSuite(testSuite);
        
		for (ConsoleOption<?> consoleOption : getConsoleOptionList()) {
			if (consoleOption.getOption().startsWith(PLUGIN_CONSOLE_OPTION_PREFIX)) {
				filterTestCasesByPlugin(IdConstants.PLUGIN_TAGS_2, testSuite, testCases, consoleOption);
			}
		}
	}

	private void filterTestCasesByPlugin(String pluginId, TestSuiteEntity testSuite,
			List<com.katalon.platform.api.model.TestCaseEntity> testCases, ConsoleOption<?> consoleOption) {

		if (ApplicationManager.getInstance().getPluginManager().getPlugin(pluginId) != null) {
			PluginConsoleOptionRegister tagsPluginConsoleOptionRegister = ApplicationManager.getInstance()
					.getConsoleManager().getRegisteredConsoleOption(pluginId);

			if (tagsPluginConsoleOptionRegister != null) {
				PluginConsoleOption<?> recognizedPluginConsoleOption = ConsoleAdapter
						.adaptToPluginConsoleOption(consoleOption);
				
				List<com.katalon.platform.api.model.TestCaseEntity> filteredTestCases = tagsPluginConsoleOptionRegister
						.filterTestCasesOnPluginConsoleOptionRecognized(recognizedPluginConsoleOption, testCases);
				
				for(com.katalon.platform.api.model.TestCaseEntity testCase : filteredTestCases){
					System.out.println(testCase.getName() +  " " + testCase.getId());
				}
				
				List<TestSuiteTestCaseLink> filteredTestCaseLinks = testSuite.getTestSuiteTestCaseLinks().stream()
						.filter(b -> filteredTestCases.stream().map(c -> c.getId())
								.collect(Collectors.toCollection(() -> new ArrayList<String>())).contains(b.getTestCaseId()))
						.collect(Collectors.toCollection(() -> new ArrayList<TestSuiteTestCaseLink>()));
				
				if(!filteredTestCaseLinks.isEmpty()){
					testSuite.setTestSuiteTestCaseLinks(filteredTestCaseLinks);
				}
			}
		}
	}

	private List<com.katalon.platform.api.model.TestCaseEntity> getTestCasesFromTestSuite(TestSuiteEntity testSuite) throws ControllerException {
    	List<com.katalon.platform.api.model.TestCaseEntity> testCases = new ArrayList<>();
		for(TestSuiteTestCaseLink testCaseLink : testSuite.getTestSuiteTestCaseLinks()){
			TestCaseEntity testCase = TestCaseController.getInstance()
					.getTestCaseByDisplayId(testCaseLink.getTestCaseId());
			testCases.add(new TestCaseEntityImpl(testCase));			
		}
		return testCases;
	}

	private Map<String, Object> getOverridingGlobalVariables(){
    	Map<String, Object> overridingGlobalVariables = new HashMap<>();
		overridingOptions.forEach(a -> {
			if (a.getOption().startsWith(OVERRIDING_GLOBAL_VARIABLE_PREFIX) 
					&& a.getValue() != null) {
				overridingGlobalVariables.put(a.getOption().
						replace(OVERRIDING_GLOBAL_VARIABLE_PREFIX, ""),
						String.valueOf(a.getValue()));
			}
		});
    	return overridingGlobalVariables;
    }

    
	private void collectOverridingGlobalVariablesContributor() {
		ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
		if (currentProject != null && overridingOptions.isEmpty()) {
			overridingOptions = new OverridingParametersConsoleOptionContributor(currentProject).getConsoleOptionList();
		}
	}
    
    private IRunConfiguration createRunConfiguration(ProjectEntity projectEntity, TestSuiteEntity testSuite,
            String browserType) throws ExecutionException, InvalidConsoleArgumentException {
        IRunConfiguration runConfig = RunConfigurationCollector.getInstance().getRunConfiguration(browserType,
                projectEntity.getFolderLocation());

        if (runConfig == null) {
            throw new InvalidConsoleArgumentException(
                    MessageFormat.format(StringConstants.MNG_PRT_INVALID_BROWSER_X, browserType));
        }
        return runConfig;
    }

    private static TestSuiteEntity getTestSuite(ProjectEntity projectEntity, String testSuiteID)
            throws InvalidConsoleArgumentException {
        try {
            TestSuiteEntity testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteID,
                    projectEntity);

            if (testSuite == null) {
                throw throwInvalidTestSuiteIdException(testSuiteID);
            }

            return testSuite;
        } catch (Exception e) {
            throw throwInvalidTestSuiteIdException(testSuiteID);
        }
    }

    private static InvalidConsoleArgumentException throwInvalidTestSuiteIdException(String testSuiteID) {
        return new InvalidConsoleArgumentException(
                MessageFormat.format(StringConstants.MNG_PRT_TEST_SUITE_X_NOT_FOUND, testSuiteID));
    }
}
