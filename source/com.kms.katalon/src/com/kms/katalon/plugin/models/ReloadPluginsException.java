package com.kms.katalon.plugin.models;

public class ReloadPluginsException extends Exception {

    private static final long serialVersionUID = -6990485932876639172L;

    public ReloadPluginsException(String message) {
        super(message);
    }
    
    public ReloadPluginsException(String message, Throwable cause) {
        super(message, cause);
    }
}
