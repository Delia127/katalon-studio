package com.kms.katalon.composer.explorer.integration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LabelDecoratorManager {

    private static LabelDecoratorManager instance;

    private Map<String, IntegrationLabelDecorator> decorators;

    private LabelDecoratorManager() {
        decorators = new HashMap<>();
    }

    public static LabelDecoratorManager getInstance() {
        if (instance == null) {
            instance = new LabelDecoratorManager();
        }
        return instance;
    }

    public void addDecorator(String productName, IntegrationLabelDecorator decorator) {
        decorators.put(productName, decorator);
    }

    public List<IntegrationLabelDecorator> getSortedDecorator() {
        return decorators.entrySet()
                .stream()
                .sorted((a, b) -> a.getValue().getPreferredOrder() - b.getValue().getPreferredOrder())
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());
    }
}
