package com.kms.katalon.execution.mobile.exception;

public class AndroidSDKNotFoundException extends AndroidSetupException {
    
    private static final long serialVersionUID = 6713263611570437562L;
    
    public AndroidSDKNotFoundException() {
        this("");
    }

    public AndroidSDKNotFoundException(String message) {
        super(message);
    }

}
