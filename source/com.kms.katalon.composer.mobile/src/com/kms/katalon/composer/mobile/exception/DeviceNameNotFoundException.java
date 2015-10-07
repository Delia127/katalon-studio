package com.kms.katalon.composer.mobile.exception;

import com.kms.katalon.entity.exception.KatalonException;

public class DeviceNameNotFoundException extends KatalonException {
    private static final long serialVersionUID = 6446788412919768180L;

    public DeviceNameNotFoundException(String message) {
        super(message);
    }
}
