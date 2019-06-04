package com.kms.katalon.plugin.models;

public class KStoreClientExceptionWithInfo extends Exception {

    private static final long serialVersionUID = -154534255301013098L;

    private KStoreCredentials credential;

    private String endpoint = "uninitialized";

    public KStoreClientExceptionWithInfo(String message, KStoreCredentials credential, String endpoint) {
        super(message);
        this.credential = credential;
        this.endpoint = endpoint;
    }

    private String getUsername() {
        if (credential == null) {
            return "No username in this Exception";
        }
        if (credential instanceof KStoreUsernamePasswordCredentials) {
            return ((KStoreUsernamePasswordCredentials) credential).getUsername();
        }
        return "No username in this Exception";
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
        return "username: " + getUsername() + ", endpoint: " + getEndpoint();
    }

}
