package com.kms.katalon.console.utils;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.console.constants.ConsoleMessageConstants;
import com.kms.katalon.console.constants.ConsoleStringConstants;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.logging.LogUtil;

public class ProxyUtil {
    private static final String USE_SYSTEM_PROXY_PROP = "java.net.useSystemProxies";
    
    public static void saveProxyInformation(ProxyInformation proxyInfo) {
        ApplicationInfo.setAppProperty(ConsoleStringConstants.PROXY_OPTION, proxyInfo.getProxyOption(), false);
        ApplicationInfo.setAppProperty(ConsoleStringConstants.PROXY_SERVER_TYPE, proxyInfo.getProxyServerType(), false);
        ApplicationInfo.setAppProperty(ConsoleStringConstants.PROXY_SERVER_ADDRESS, proxyInfo.getProxyServerAddress(),
                false);
        ApplicationInfo.setAppProperty(ConsoleStringConstants.PROXY_SERVER_PORT, proxyInfo.getProxyServerPort() + "",
                false);
        ApplicationInfo.setAppProperty(ConsoleStringConstants.PROXY_USERNAME, proxyInfo.getUsername(), false);
        ApplicationInfo.setAppProperty(ConsoleStringConstants.PROXY_PASSWORD, proxyInfo.getPassword(), true);
    }

    public static ProxyInformation getProxyInformation() {
        ProxyInformation proxyInfo = new ProxyInformation();

        final String proxyOption = ApplicationInfo.getAppProperty(ConsoleStringConstants.PROXY_OPTION);
        proxyInfo.setProxyOption(StringUtils.isEmpty(proxyOption) ? ConsoleMessageConstants.NO_PROXY : proxyOption);
        proxyInfo.setProxyServerType(ApplicationInfo.getAppProperty(ConsoleStringConstants.PROXY_SERVER_TYPE));
        proxyInfo.setProxyServerAddress(ApplicationInfo.getAppProperty(ConsoleStringConstants.PROXY_SERVER_ADDRESS));
        proxyInfo.setProxyServerPort(ApplicationInfo.getAppProperty(ConsoleStringConstants.PROXY_SERVER_PORT));
        proxyInfo.setUsername(ApplicationInfo.getAppProperty(ConsoleStringConstants.PROXY_USERNAME));
        proxyInfo.setPassword(ApplicationInfo.getAppProperty(ConsoleStringConstants.PROXY_PASSWORD));

        return proxyInfo;
    }

    public static Proxy getProxy() throws IOException {
        ProxyInformation proxyInfo = ProxyUtil.getProxyInformation();
        Proxy proxy = Proxy.NO_PROXY;

        if (ConsoleMessageConstants.USE_SYSTEM_PROXY.equals(proxyInfo.getProxyOption())) {
            proxy = getSystemProxyFor(ServerAPICommunicationUtil.getAPIUrl());
        } else if (ConsoleMessageConstants.MANUAL_CONFIG_PROXY.equals(proxyInfo.getProxyOption())) {
            System.setProperty(USE_SYSTEM_PROXY_PROP, "false");
            try {
                switch (proxyInfo.getProxyServerType()) {
                    case ConsoleStringConstants.SOCKS_PROXY_TYPE:
                        proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyInfo.getProxyServerAddress(),
                                proxyInfo.getProxyServerPort()));
                        break;
                    case ConsoleStringConstants.HTTP_PROXY_TYPE:
                    case ConsoleStringConstants.HTTPS_PROXY_TYPE:
                        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyInfo.getProxyServerAddress(),
                                proxyInfo.getProxyServerPort()));
                        break;
                    default:
                        throw new IllegalArgumentException(ConsoleMessageConstants.PROXY_SERVER_TYPE_NOT_VALID_MESSAGE);
                }
            } catch (IllegalArgumentException | SecurityException ex) {
                throw new IOException(ex);
            }
        }

        if (StringUtils.isNotEmpty(proxyInfo.getUsername()) && StringUtils.isNotEmpty(proxyInfo.getPassword())) {
            Authenticator.setDefault(new Authenticator() {
                protected java.net.PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxyInfo.getUsername(), proxyInfo.getPassword().toCharArray());
                };
            });
        }
        
        return proxy;
    }
    
    public static Proxy getSystemProxyFor(String url) {
        try {
            System.setProperty(USE_SYSTEM_PROXY_PROP, "true");
            List<Proxy> systemProxies = ProxySelector.getDefault().select(new URI(url));
            Optional<Proxy> sysProxy = systemProxies.stream().filter(proxy -> proxy.address() != null).findFirst();
            return sysProxy.isPresent() ? sysProxy.get() : Proxy.NO_PROXY;

        } catch (Exception e) {
            LogUtil.logError(e);
        }

        return Proxy.NO_PROXY;
    }
}
