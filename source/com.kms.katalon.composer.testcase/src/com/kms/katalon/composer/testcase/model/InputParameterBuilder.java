package com.kms.katalon.composer.testcase.model;

import java.util.ArrayList;
import java.util.List;

public class InputParameterBuilder {
    private final List<InputParameter> originalParameters;

    private final boolean testObjectParamIgnored;

    public List<InputParameter> getOriginalParameters() {
        return originalParameters;
    }

    private InputParameterBuilder(List<InputParameter> originalParameters, boolean testObjectParamIgnored) {
        this.originalParameters = originalParameters;
        this.testObjectParamIgnored = testObjectParamIgnored;
    }

    public static InputParameterBuilder createForMethodCall(List<InputParameter> inputParameters) {
        return new InputParameterBuilder(inputParameters, true);
    }

    public static InputParameterBuilder createForNestedMethodCall(List<InputParameter> inputParameters) {
        return new InputParameterBuilder(inputParameters, false);
    }

    public List<InputParameter> getFilteredInputParameters() {
        List<InputParameter> filteredInputParameters = new ArrayList<>();
        boolean hasTestObjectParam = false;
        for (InputParameter inputParameter : originalParameters) {
            if (testObjectParamIgnored && inputParameter.isTestObjectInputParameter() && !hasTestObjectParam) {
                hasTestObjectParam = true;
                continue;
            }
            if (!inputParameter.isEditable()) {
                continue;
            }
            filteredInputParameters.add(inputParameter);
        }
        return filteredInputParameters;
    }
}
