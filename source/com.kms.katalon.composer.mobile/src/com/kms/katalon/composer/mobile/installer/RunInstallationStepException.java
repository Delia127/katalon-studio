package com.kms.katalon.composer.mobile.installer;

import java.lang.reflect.InvocationTargetException;

public class RunInstallationStepException extends InvocationTargetException {

    public RunInstallationStepException(String message, String detail) {
        super(new Throwable(detail), message);
    }

    private static final long serialVersionUID = 896324613419531840L;

}
