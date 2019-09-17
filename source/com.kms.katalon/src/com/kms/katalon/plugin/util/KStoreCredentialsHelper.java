package com.kms.katalon.plugin.util;

import com.kms.katalon.plugin.models.KStoreApiKeyCredentials;
import com.kms.katalon.plugin.models.KStoreBasicCredentials;
import com.kms.katalon.plugin.models.KStoreCredentials;

public class KStoreCredentialsHelper {

    static public boolean isValidCredential(KStoreCredentials credential) {
        if (credential == null) {
            return false;
        }
        
        if (credential instanceof KStoreApiKeyCredentials) {
            return isValidApiKeyCredential((KStoreApiKeyCredentials)credential);
        }
        
        if (credential instanceof KStoreBasicCredentials) {
            return isValidBasicCredential((KStoreBasicCredentials)credential);
        }
        
        return false;
    }
    
    static public boolean isValidBasicCredential(KStoreBasicCredentials credential) {
        return credential != null
                && isValidApiKeyCredential(credential) || isValidUsernamePasswordCredential(credential);
    }
    
    static public boolean isValidApiKeyCredential(KStoreApiKeyCredentials credential) {
        return credential != null
                && credential.getApiKey() != null && !credential.getApiKey().isEmpty();
    }
    
    static public boolean isValidApiKeyCredential(KStoreBasicCredentials credential) {
        return credential != null
                && credential.getApiKey() != null && !credential.getApiKey().isEmpty();
    }
    
    static public boolean isValidUsernamePasswordCredential(KStoreBasicCredentials credential) {
        return credential != null
                && (credential.getUsername() != null && !credential.getUsername().isEmpty())
                && (credential.getPassword() != null && !credential.getUsername().isEmpty());
    }
}
