package com.kms.katalon.dal;

public interface IQTestAuthenticationDataProvider {
    public String getQTestToken(String serverURL, String username, String password) throws Exception ;
}
