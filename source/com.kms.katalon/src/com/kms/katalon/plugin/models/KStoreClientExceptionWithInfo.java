package com.kms.katalon.plugin.models;

import org.apache.commons.lang3.StringUtils;

public class KStoreClientExceptionWithInfo extends Exception {

    private static final long serialVersionUID = -154534255301013098L;

    private KStoreCredentials credential;

    private String endpoint = "uninitialized";

    public KStoreClientExceptionWithInfo(String message, KStoreCredentials credential, String endpoint) {
        super(message);
        this.credential = credential;
        this.endpoint = endpoint;
    }
    
    public KStoreClientExceptionWithInfo(String message, KStoreCredentials credential, String endpoint, Throwable cause) {
        super(message, cause);
        this.credential = credential;
        this.endpoint = endpoint;
    }

    private String getCredential() {
        if (credential == null) {
            return StringUtils.EMPTY;
        }
        if (credential instanceof KStoreUsernamePasswordCredentials) {
            return ((KStoreUsernamePasswordCredentials) credential).getUsername();
        }
        return StringUtils.EMPTY;
    }

    private String getEndpoint() {
        return endpoint;
    }

    /**
     * Get a message with the username of the user
     * for which this Exception is applicable and the endpoint
     * that potentially caused this Exception
     * 
     * @return String
     */
    public String getInfoMessage() {
        String credential = getCredential();
        String endpoint = getEndpoint();
        
        if(credential.equals(StringUtils.EMPTY)) {
            return "Invalid API key, please re-check your credential";
        }
        
        return "credential: " + credential + ", endpoint: " + endpoint;
    }

}
