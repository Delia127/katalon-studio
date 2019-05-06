package com.kms.katalon.execution.console;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.console.entity.PreferenceOptionContributor;
import com.kms.katalon.execution.preferences.KobitonConsoleOptionContributor;
import com.kms.katalon.execution.preferences.ProxyConsoleOptionContributor;
import com.kms.katalon.execution.preferences.WebUIConsoleOptionContributor;

public class ApplicationConfigOptions implements ConsoleOptionContributor {
    private List<PreferenceOptionContributor> preferenceOptions;

    private Map<String, PreferenceOptionContributor> optionNameIndexedByPreference;
    
    public String getConfigOption() {
        return "config";
    }

    public ApplicationConfigOptions() {
        preferenceOptions = new ArrayList<>();
        preferenceOptions.add(new ProxyConsoleOptionContributor());
        preferenceOptions.add(new KobitonConsoleOptionContributor());
        preferenceOptions.add(new WebUIConsoleOptionContributor());
        optionNameIndexedByPreference = new LinkedHashMap<>();
        preferenceOptions.stream().forEach(p -> {
            p.getConsoleOptionList()
                    .stream()
                    .forEach(o -> optionNameIndexedByPreference.put(o.getOption(), p));
        });
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        return preferenceOptions.stream()
                .map(p -> p.getConsoleOptionList())
                .flatMap(l -> l.stream())
                .collect(Collectors.toList());
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        PreferenceOptionContributor preferenceOptions = optionNameIndexedByPreference.get(consoleOption.getOption());
        preferenceOptions.setArgumentValue(consoleOption, argumentValue);
    }
}
