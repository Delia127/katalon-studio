package com.kms.katalon.composer.components.impl.exception;

import java.lang.reflect.InvocationTargetException;

public class RunInstallationStepException extends InvocationTargetException {

    public RunInstallationStepException(String message, Throwable target) {
        super(target, message);
    }

    private static final long serialVersionUID = 896324613419531840L;

}
