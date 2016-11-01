package com.kms.katalon.integration.kobiton.constants;

public class KobitonStringConstants {
    public static final String KOBITON_SCHEME_SEPARATOR = "://";

    public static final String KOBITON_SERVER_REMOTE_HUB = "/wd/hub";

    public static final String KOBITON_SCHEME_HTTPS = "https";
    
    public static final String KOBITON_SCHEME_HTTP = "http";
    
    public static final String KOBITON_HOST = "api.kobiton.com";
    
    public static final String KOBITON_SERVER_URL_AUTHENTICATE_SEPARATOR = ":";
    
    public static final String KOBITON_SERVER_URL_SEPARATOR = "@";
    
    public static final String KOBITON_SERVER_TARGET_ENDPOINT = KOBITON_SCHEME_HTTPS + KOBITON_SCHEME_SEPARATOR + KOBITON_HOST + KOBITON_SERVER_REMOTE_HUB;

    public static final String KOBITON_API_LOGIN = "/v1/users/login";

    public static final String KOBITON_API_GET_KEYS = "/v1/users/keys";

    public static final String KOBITON_API_GET_FAVORITE_DEVICES = "/v1/devices/favorites";

}
