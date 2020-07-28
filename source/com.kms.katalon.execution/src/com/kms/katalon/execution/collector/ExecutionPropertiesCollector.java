package com.kms.katalon.execution.collector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kms.katalon.execution.configuration.contributor.IExecutionPropertiesContributor;

public class ExecutionPropertiesCollector {

    private static ExecutionPropertiesCollector _instance;

    private Map<String, IExecutionPropertiesContributor> contributors;

    private ExecutionPropertiesCollector() {
    }

    public static ExecutionPropertiesCollector getInstance() {
        if (_instance == null) {
            _instance = new ExecutionPropertiesCollector();
        }
        return _instance;
    }

    public List<IExecutionPropertiesContributor> getPropertiesContributors() {
        return contributors.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
    }

    public void addContributor(IExecutionPropertiesContributor contributor) {
        if (contributors == null) {
            contributors = new HashMap<>();
        }

        contributors.put(contributor.getKey(), contributor);
    }

}
