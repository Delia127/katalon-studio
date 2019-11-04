package com.kms.katalon.composer.artifact.core;

public class FileCompressionException extends Exception {

    private static final long serialVersionUID = -6494232220859297938L;

    public FileCompressionException(Throwable cause) {
        super(cause);
    }
    
    public FileCompressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
