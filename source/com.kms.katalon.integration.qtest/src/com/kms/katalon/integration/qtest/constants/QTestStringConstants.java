package com.kms.katalon.integration.qtest.constants;

public class QTestStringConstants {
    //# Product
	public static final String PRODUCT_NAME = "qTest";
	
	//# Connection
	public static final String CON_POST_METHOD = "POST";
	public static final String CON_GET_METHOD = "GET";
	
	//# Request property and default value
	public static final String RQ_PROPERTY_USER_AGENT = "User-Agent";	
	public static final String RQ_PROPERTY_AUTHORIZATION = "Authorization";
	public static final String RQ_PROPERTY_CONTENT_TYPE = "Content-Type";
	
	public static final String RQ_DF_VALUE_USER_AGENT = "Mozilla/5.0";
	public static final String RQ_DF_VALUE_CONTENT_TYPE = "application/json";
	
    public static final String DEFAULT_HOST_NAME = "can.not.get.host.name";
    public static final String KMS_SIGNED_THE_TOKEN_ISSUER = "https://www.katalon.com/";
    public static final String HTTPS = "https";
    public static final String HTTP_USER_AGENT = "User-Agent";
    public static final String DEFAULT_USER_AGENT = "Katalon Studio";
    public static final String SSL = "SSL";
    public static final String USERNAME = "username";

    // application.properties
    public static final String APP_PROP_ACTIVATION_CODE ="qTest.activationCode";
    
    // qTest Activation payload properties
    public static final String PAYLOAD_PROP_REQUEST_CODE = "requestCode";
    public static final String PAYLOAD_PROP_IS_OFFLINE = "offline";
    
    public static final String REQUEST_PROP_ACTIVATION_CODE = "activationCode";

    public static final String QTEST_ACTIVATION_PUBLIC_KEY =
"-----BEGIN PUBLIC KEY-----\n"
+ "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClVi5Gck1X4uD9EfpQvPNe4utl"
+ "Rgw9GWhCQNa4gcya6SG3UKwgDvNGyiD3J6w4xM97NUGmDJw7INTKNikVmBjXpTRK"
+ "q/rWlSUpSkOYB9XT/pNvPf2G8U5qOMnXokGmW5r6GIVuYu6OrhAcAh0nSKYxt/vy"
+ "LtIvt1SFDkD320fOBwIDAQAB\n"
+ "-----END PUBLIC KEY-----";
}
