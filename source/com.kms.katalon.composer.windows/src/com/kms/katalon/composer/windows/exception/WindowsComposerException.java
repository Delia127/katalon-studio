package com.kms.katalon.composer.windows.exception;

public class WindowsComposerException extends Exception {

    private static final long serialVersionUID = -1043748127847976495L;

    public WindowsComposerException(String message) {
        super(message);
    }
    
    public WindowsComposerException(Throwable e) {
        super(e);
    }
}
