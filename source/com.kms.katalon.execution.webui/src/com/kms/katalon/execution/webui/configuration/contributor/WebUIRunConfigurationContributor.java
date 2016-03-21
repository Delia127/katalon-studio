package com.kms.katalon.execution.webui.configuration.contributor;

import java.util.Collections;
import java.util.List;

import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.entity.ConsoleOption;

public abstract class WebUIRunConfigurationContributor implements IRunConfigurationContributor {
    @Override
    public List<ConsoleOption<?>> getRequiredArguments() {
        return Collections.emptyList();
    }

}
