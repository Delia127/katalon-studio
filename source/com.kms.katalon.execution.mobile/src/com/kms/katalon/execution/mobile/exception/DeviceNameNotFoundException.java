package com.kms.katalon.execution.mobile.exception;

import com.kms.katalon.execution.exception.ExecutionException;

public class DeviceNameNotFoundException extends ExecutionException {
    private static final long serialVersionUID = 6446788412919768180L;

    public DeviceNameNotFoundException(String message) {
        super(message);
    }
}
