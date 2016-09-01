package com.kms.katalon.composer.webui.execution.menu;

import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class IEDebugExecutionDynamicContribution extends IEExecutionDynamicContribution {
    protected Map<String, Object> getParametersForCommand() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(IdConstants.RUN_MODE_PARAMETER_ID, LaunchMode.DEBUG.toString());
        return parameters;
    }
}
