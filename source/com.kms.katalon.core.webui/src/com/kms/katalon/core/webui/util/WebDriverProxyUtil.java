package com.kms.katalon.core.webui.util;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;

import com.google.gson.JsonObject;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.network.ProxyOption;
import com.kms.katalon.core.network.ProxyServerType;
import com.machinepublishers.jbrowserdriver.ProxyConfig;

public class WebDriverProxyUtil {

    private static final String PROP_SOCKS_PASSWORD = "socksPassword";

    private static final String PROP_SOCKS_USERNAME = "socksUsername";

    private static final String PROP_SOCKS_PROXY_PORT = "socksProxyPort";

    private static final String PROP_SOCKS_PROXY = "socksProxy";

    private static final String PROP_HTTP_PROXY_PORT = "httpProxyPort";

    private static final String PROP_HTTP_PROXY = "httpProxy";

    private static final String PROP_PROXY_TYPE = "proxyType";

    /**
     * Returns an instance of Selenium Proxy based on proxy settings.
     * <br/>
     * 
     * @param proxyInfomation: Proxy settings
     * @return an instance of {@link Proxy}. if the param is null, return a no Proxy.
     */
    public static Proxy getSeleniumProxy(ProxyInformation proxyInformation) {
        Proxy noProxy = new Proxy().setProxyType(ProxyType.DIRECT);
        if (proxyInformation == null) {
            return noProxy;
        }
        switch (ProxyOption.valueOf(proxyInformation.getProxyOption())) {
            case MANUAL_CONFIG:
                switch (ProxyServerType.valueOf(proxyInformation.getProxyServerType())) {
                    case HTTP:
                    case HTTPS:
                        return new Proxy().setHttpProxy(String.format("%s:%d", proxyInformation.getProxyServerAddress(),
                                proxyInformation.getProxyServerPort()));
                    case SOCKS:
                        return new Proxy()
                                .setSocksProxy(String.format("%s:%d", proxyInformation.getProxyServerAddress(),
                                        proxyInformation.getProxyServerPort()))
                                .setSocksUsername(proxyInformation.getUsername())
                                .setSocksPassword(proxyInformation.getPassword());
                }
            case NO_PROXY:
                return noProxy;
            case USE_SYSTEM:
                return new Proxy().setProxyType(ProxyType.SYSTEM);
        }
        return noProxy;
    }

    /**
     * Returns a proxy object as JSON string that follows geko driver
     * 
     * @param proxyInformation
     * <a href="https://github.com/mozilla/geckodriver#proxy-object">
     * Official link
     * </a>
     */
    public static JsonObject getProxyForGekoDriver(ProxyInformation proxyInformation) {
        JsonObject jsonObject = new JsonObject();
        switch (ProxyOption.valueOf(proxyInformation.getProxyOption())) {
            case MANUAL_CONFIG:
                jsonObject.addProperty(PROP_PROXY_TYPE, "manual");
                switch (ProxyServerType.valueOf(proxyInformation.getProxyServerType())) {
                    case HTTP:
                    case HTTPS:
                        jsonObject.addProperty(PROP_HTTP_PROXY, proxyInformation.getProxyServerAddress());
                        jsonObject.addProperty(PROP_HTTP_PROXY_PORT, proxyInformation.getProxyServerPort());
                        break;
                    case SOCKS:
                        jsonObject.addProperty(PROP_SOCKS_PROXY, proxyInformation.getProxyServerAddress());
                        jsonObject.addProperty(PROP_SOCKS_PROXY_PORT, "111");
                        jsonObject.addProperty(PROP_SOCKS_USERNAME, proxyInformation.getUsername());
                        jsonObject.addProperty(PROP_SOCKS_PASSWORD, proxyInformation.getPassword());
                        break;
                }
                break;
            case USE_SYSTEM:
                jsonObject.addProperty(PROP_PROXY_TYPE, "system");
                break;
            case NO_PROXY:
                jsonObject.addProperty(PROP_PROXY_TYPE, "noproxy");
                break;
        }
        return jsonObject;
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
