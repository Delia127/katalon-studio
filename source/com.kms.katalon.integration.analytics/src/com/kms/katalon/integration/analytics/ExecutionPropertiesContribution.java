package com.kms.katalon.integration.analytics;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

import com.kms.katalon.execution.configuration.contributor.IExecutionPropertiesContributor;
import com.kms.katalon.integration.analytics.report.AnalyticsReportIntegration;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class ExecutionPropertiesContribution implements IExecutionPropertiesContributor {
    
    public static final String EXECUTION_PROPERTIES_KEY = "testops";
    
    @Override
    public String getKey() {
        return EXECUTION_PROPERTIES_KEY;
    }

    @Override
    public Map<String, Object> getExecutionProperties() {
        Map<String, Object> props = new HashMap<>();
        Long releaseId = AnalyticsReportIntegration.TESTOPS_RELEASE_ID_CONSOLE_OPTION.getValue();
        if(releaseId != null) {
            props.put(AnalyticsReportIntegration.TESTOPS_RELEASE_ID_CONSOLE_OPTION.getOption(), releaseId);
        }
        
        return props;
    }

}
