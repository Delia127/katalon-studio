package com.kms.katalon.plugin.models;

public class KStoreClientAuthException extends Exception {

    private static final long serialVersionUID = 2536823844450784968L;

    public KStoreClientAuthException(String message) {
        super(message);
    }
    
    public KStoreClientAuthException(Exception ex) {
        super(ex);
    }
}
