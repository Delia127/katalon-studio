package com.kms.katalon.composer.testsuite.collection.execution.collector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionConfigurationProvider;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionGroup;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionItem;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.util.ExecutionUtil;

public class TestExecutionGroupCollector {
    private static TestExecutionGroupCollector instance;

    private Map<String, TestExecutionGroup> testExecutionGroupCollector;

    private TestExecutionGroupCollector() {
        testExecutionGroupCollector = new HashMap<>();
    }

    public static TestExecutionGroupCollector getInstance() {
        if (instance == null) {
            instance = new TestExecutionGroupCollector();
        }
        return instance;
    }

    public void addGroup(TestExecutionGroup groupTestRunExecution) {
        testExecutionGroupCollector.put(groupTestRunExecution.getName(), groupTestRunExecution);
    }

    private List<TestExecutionGroup> getUnsortedGroups() {
        List<TestExecutionGroup> groups = new ArrayList<>();
        for (Entry<String, TestExecutionGroup> entry : testExecutionGroupCollector.entrySet()) {
            groups.add(entry.getValue());
        }
        return groups;
    }

    public List<TestExecutionGroup> getSortedGroups() {
        List<TestExecutionGroup> groups = getUnsortedGroups();
        Collections.sort(groups, new Comparator<TestExecutionGroup>() {

            @Override
            public int compare(TestExecutionGroup group1, TestExecutionGroup group2) {
                return group1.preferredOrder() - group2.preferredOrder();
            }
        });

        return groups;
    }

    public TestExecutionGroup[] getGroupAsArray() {
        List<TestExecutionGroup> sortedGroups = getSortedGroups();
        return sortedGroups.toArray(new TestExecutionGroup[sortedGroups.size()]);
    }

    public TestExecutionConfigurationProvider getExecutionProvider(RunConfigurationDescription configuration) {
        TestExecutionGroup group = testExecutionGroupCollector.get(configuration.getGroupName());
        if (group == null) {
            return null;
        }
        for (TestExecutionItem child : group.getChildren()) {
            if (child instanceof TestExecutionConfigurationProvider
                    && StringUtils.equals(configuration.getRunConfigurationId(), child.getName())) {
                return (TestExecutionConfigurationProvider) child;
            }
        }
        return null;
    }

    public TestExecutionGroup getGroup(String groupName) {
        return testExecutionGroupCollector.get(groupName);
    }

    public RunConfigurationDescription getDefaultConfiguration() {
        IRunConfigurationContributor defaultRunConfiguration = ExecutionUtil.getDefaultExecutionConfiguration();
        for (TestExecutionGroup group : getUnsortedGroups()) {
            for (TestExecutionItem item : group.getChildren()) {
                if (!(item instanceof TestExecutionConfigurationProvider)) {
                    continue;
                }
                TestExecutionConfigurationProvider executionProvider = (TestExecutionConfigurationProvider) item;
                if (ObjectUtils.equals(executionProvider.getRunConfigurationContributor(), defaultRunConfiguration)) {
                    return executionProvider.toConfigurationEntity();
                }
            }
        }
        return null;
    }
}
