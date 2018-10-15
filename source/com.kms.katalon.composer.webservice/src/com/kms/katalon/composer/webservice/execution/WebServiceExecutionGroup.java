package com.kms.katalon.composer.webservice.execution;

import com.kms.katalon.composer.execution.collection.provider.TestExecutionGroup;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionItem;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;

public class WebServiceExecutionGroup implements TestExecutionGroup {

    private TestExecutionItem[] children;

    @Override
    public String getName() {
        return "Web Service";
    }

    @Override
    public String getImageUrlAsString() {
        return null;
    }

    @Override
    public TestExecutionItem[] getChildren() {
        if (children == null) {
            children = new TestExecutionItem[] { new WebServiceContributionProvider() };
        }
        return children;
    }

    @Override
    public int preferredOrder() {
        return 2;
    }

    @Override
    public boolean shouldBeDisplayed(ProjectEntity project) {
        return project != null && project.getType() == ProjectType.WEBSERVICE;
    }
}
