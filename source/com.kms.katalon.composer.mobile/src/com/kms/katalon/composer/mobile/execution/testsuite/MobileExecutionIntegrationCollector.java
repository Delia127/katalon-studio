package com.kms.katalon.composer.mobile.execution.testsuite;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MobileExecutionIntegrationCollector {

    private List<MobileIntegrationProvider> executionEntries;

    private static MobileExecutionIntegrationCollector instance;

    private MobileExecutionIntegrationCollector() {
        executionEntries = new ArrayList<>();
    }

    public static MobileExecutionIntegrationCollector getInstance() {
        if (instance == null) {
            instance = new MobileExecutionIntegrationCollector();
        }
        return instance;
    }

    public void addNewProvider(MobileIntegrationProvider executionEntry) {
        executionEntries.add(executionEntry);
    }

    public List<MobileTestExecutionDriverEntry> getSortedExecutionEntries(String groupName) {
        return executionEntries.stream().sorted(new Comparator<MobileIntegrationProvider>() {
            @Override
            public int compare(MobileIntegrationProvider entryA, MobileIntegrationProvider entryB) {
                return entryA.getPreferedOrder() - entryB.getPreferedOrder();
            }
        }).map(provider -> provider.getExecutionEntry(groupName))
                .filter(entry -> entry != null)
                .collect(Collectors.toList());
    }
}
