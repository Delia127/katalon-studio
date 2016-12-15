package com.kms.katalon.execution.collector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.console.entity.ConsoleMainOptionContributor;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.integration.ReportIntegrationFactory;
import com.kms.katalon.execution.util.ExecutionUtil;

public class ConsoleOptionCollector {
    public static final String DEFAULT_EXECUTION_PROPERTY_FILE_NAME = "console.properties";

    private static ConsoleOptionCollector _instance;

    private List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
    
    private List<ConsoleOptionContributor> optionContributors = new ArrayList<>();

    public List<ConsoleOptionContributor> getOptionContributors() {
        return Collections.unmodifiableList(optionContributors);
    }

    private ConsoleOptionCollector() {
        for (ConsoleOptionContributor consoleOptionContributor : RunConfigurationCollector.getInstance()
                .getConsoleOptionContributorList()) {
            addConsoleOptionContributor(consoleOptionContributor);
            optionContributors.add(consoleOptionContributor);
        }
        for (ConsoleOptionContributor consoleOptionContributor : ReportIntegrationFactory.getInstance()
                .getConsoleOptionContributorList()) {
            addConsoleOptionContributor(consoleOptionContributor);
            optionContributors.add(consoleOptionContributor);
        }
    }

    public void addConsoleOptionContributor(ConsoleOptionContributor consoleOptionContributor) {
        consoleOptionList.addAll(consoleOptionContributor.getConsoleOptionList());
    }

    public List<ConsoleOption<?>> getConsoleOptionList() {
        return Collections.unmodifiableList(consoleOptionList);
    }

    public static ConsoleOptionCollector getInstance() {
        if (_instance == null) {
            _instance = new ConsoleOptionCollector();
        }
        return _instance;
    }

    public void writeDefaultPropertyFile(ProjectEntity project) throws IOException {
        if (project == null) {
            return;
        }
        String propertyFileLocation = project.getFolderLocation() + File.separator
                + DEFAULT_EXECUTION_PROPERTY_FILE_NAME;
        if (new File(propertyFileLocation).exists()) {
            return;
        }
        Map<String, String> consoleOptionMaps = new HashMap<String, String>();
        for (ConsoleOption<?> consoleOption : consoleOptionList) {
            // Not adding project path to default properties file
            if (ConsoleMainOptionContributor.PROJECT_PATH_CONSOLE_OPTION == consoleOption) {
                continue;
            }
            consoleOptionMaps.put(consoleOption.getOption(),
                    Strings.nullToEmpty(consoleOption.getDefaultArgumentValue()));
        }
        ExecutionUtil.savePropertiesFile(consoleOptionMaps, propertyFileLocation);
    }
}
