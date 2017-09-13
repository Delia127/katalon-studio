package com.kms.katalon.composer.execution.collection.provider;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public interface TestExecutionItem {

    String getName();

    String getImageUrlAsString();

    TestExecutionItem[] getChildren();
    
    default Optional<TestExecutionItem> getItem(String name) {
        if (StringUtils.isEmpty(name)) {
            return Optional.empty();
        }
        TestExecutionItem[] children = getChildren();
        if (children == null) {
            return Optional.empty();
        }
        for (TestExecutionItem item : children) {
            if (name.equals(item.getName())) {
                return Optional.of(item);
            }
            Optional<TestExecutionItem> foundInChild = item.getItem(name);
            if (foundInChild.isPresent()) {
                return foundInChild;
            }
        }
        return Optional.empty();
    }
}
