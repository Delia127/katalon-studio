package com.kms.katalon.core.webui.util;

import org.openqa.selenium.Proxy;

import com.google.gson.JsonObject;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.network.ProxyOption;
import com.kms.katalon.core.network.ProxyServerType;
import com.machinepublishers.jbrowserdriver.ProxyConfig;

public class WebDriverProxyUtil {

    private static final String PROP_SOCKS_PASSWORD = "socksPassword";

    private static final String PROP_SOCKS_USERNAME = "socksUsername";

    private static final String PROP_SOCKS_PROXY = "socksProxy";

    private static final String PROP_HTTP_PROXY = "httpProxy";

    private static final String PROP_SSL_PROXY = "sslProxy";

    private static final String PROP_FTP_PROXY = "ftpProxy";

    private static final String PROP_PROXY_TYPE = "proxyType";

    /**
     * Returns an instance of Selenium Proxy based on proxy settings.
     * <br/>
     * 
     * @param proxyInfomation: Proxy settings
     * @return an instance of {@link Proxy}. if the param is null, return a no Proxy.
     */
    public static JsonObject getSeleniumProxy(ProxyInformation proxyInformation) {
        JsonObject jsonObject = new JsonObject();

        String proxyString = getProxyString(proxyInformation);
        switch (ProxyOption.valueOf(proxyInformation.getProxyOption())) {
            case MANUAL_CONFIG:
                jsonObject.addProperty(PROP_PROXY_TYPE, "manual");
                switch (ProxyServerType.valueOf(proxyInformation.getProxyServerType())) {
                    case HTTP:
                    case HTTPS:
                        jsonObject.addProperty(PROP_HTTP_PROXY, proxyString);
                        jsonObject.addProperty(PROP_FTP_PROXY, proxyString);
                        jsonObject.addProperty(PROP_SSL_PROXY, proxyString);
                        break;
                    case SOCKS:
                        jsonObject.addProperty(PROP_SOCKS_PROXY, proxyString);
                        jsonObject.addProperty(PROP_SOCKS_USERNAME, proxyInformation.getUsername());
                        jsonObject.addProperty(PROP_SOCKS_PASSWORD, proxyInformation.getPassword());
                        break;
                }
                break;
            case USE_SYSTEM:
                jsonObject.addProperty(PROP_PROXY_TYPE, "system");
                break;
            case NO_PROXY:
                jsonObject.addProperty(PROP_PROXY_TYPE, "direct");
                break;
        }
        return jsonObject;
    }

    public static String getProxyString(ProxyInformation proxyInformation) {
        return String.format("%s:%d", proxyInformation.getProxyServerAddress(), proxyInformation.getProxyServerPort());
    }

    public static boolean isManualSocks(ProxyInformation proxyInformation) {
        return proxyInformation != null
                && ProxyOption.valueOf(proxyInformation.getProxyOption()) == ProxyOption.MANUAL_CONFIG
                && ProxyServerType.valueOf(proxyInformation.getProxyServerType()) == ProxyServerType.SOCKS;
    }

    /**
     * Returns {@link proxyInformation} to construct a JBrowserDriver.
     */
    public static ProxyConfig getProxyConfigForJBrowser(ProxyInformation proxyInformation) {
        if (proxyInformation == null) {
            return new ProxyConfig();
        }
        switch (ProxyOption.valueOf(proxyInformation.getProxyOption())) {
            case MANUAL_CONFIG:
                ProxyConfig.Type proxyConfigType = ProxyServerType
                        .valueOf(proxyInformation.getProxyServerType()) == ProxyServerType.SOCKS
                                ? ProxyConfig.Type.SOCKS : ProxyConfig.Type.HTTP;
                return new ProxyConfig(proxyConfigType, proxyInformation.getProxyServerAddress(),
                        proxyInformation.getProxyServerPort(), proxyInformation.getUsername(),
                        proxyInformation.getPassword());
            default:
                return new ProxyConfig();
        }
    }
}
