package com.kms.katalon.composer.webui.recorder.util;

import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;

public final class HTMLInputValueTypeProvider {
    private HTMLInputValueTypeProvider() {
        // hide constructor
    }

    public static InputValueType getAssignableValueType(Class<?> paramClass) {
        for (InputValueType inputValueType : AstInputValueTypeOptionsProvider.getInputValueTypeOptions(AstInputValueTypeOptionsProvider.ARGUMENT_OPTIONS)) {
            if (inputValueType.isAssignableTo(paramClass)) {
                return inputValueType;
            }
        }
        return null;
    }
}
