package com.kms.katalon.composer.webservice.execution;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.execution.collection.provider.TestExecutionEntryItem;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.webservice.contribution.WebServiceConfigurationContributor;

public class WebServiceContributionProvider extends TestExecutionEntryItem {

    @Override
    public String getName() {
        return "Web Service";
    }

    @Override
    public String getImageUrlAsString() {
        return null;
    }

    @Override
    public IRunConfigurationContributor getRunConfigurationContributor() {
        return new WebServiceConfigurationContributor();
    }

    @Override
    public RunConfigurationDescription toConfigurationEntity(RunConfigurationDescription previousDescription) {
        Map<String, String> runConfigurationData = new HashMap<>();
        if (previousDescription != null && previousDescription.getRunConfigurationId().equals(getName())) {
            runConfigurationData.clear();
        }
        return RunConfigurationDescription.from("Web Service", getName(), runConfigurationData,
                (previousDescription == null) ? ExecutionProfileEntity.DF_PROFILE_NAME
                        : previousDescription.getProfileName());
    }

    @Override
    public CellEditor getRunConfigurationDataCellEditor(Composite parent) {
        return null;
    }

    @Override
    public Map<String, String> changeRunConfigurationData(Shell shell, Map<String, String> runConfigurationData) {
        return Collections.emptyMap();
    }

    @Override
    public String displayRunConfigurationData(Map<String, String> runConfigurationData) {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean requiresExtraConfiguration() {
        return false;
    }

    @Override
    public boolean shouldBeDisplayed(ProjectEntity project) {
        return project != null && project.getType() == ProjectType.WEBSERVICE;
    }
}
