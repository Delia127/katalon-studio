package com.kms.katalon.composer.testsuite.collection.execution.provider;

public interface TestExecutionItem {

    String getName();

    String getImageUrlAsString();

    TestExecutionItem[] getChildren();
}
