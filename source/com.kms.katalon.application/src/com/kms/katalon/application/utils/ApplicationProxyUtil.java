package com.kms.katalon.application.utils;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.github.markusbernhardt.proxy.ProxySearch;
import com.github.markusbernhardt.proxy.ProxySearch.Strategy;
import com.github.markusbernhardt.proxy.util.PlatformUtil;
import com.github.markusbernhardt.proxy.util.PlatformUtil.Platform;
import com.kms.katalon.application.constants.ApplicationMessageConstants;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.preference.ProxyPreferences;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.logging.LogUtil;

public class ApplicationProxyUtil {
    private static final String USE_SYSTEM_PROXY_PROP = "java.net.useSystemProxies";

    public static void saveProxyInformation(ProxyInformation proxyInfo) throws IOException {
        ProxyPreferences.saveProxyInformation(proxyInfo);
    }

    public static ProxyInformation getProxyInformation() {
        ProxyInformation proxyInfo = ProxyPreferences.getProxyInformation();

        return proxyInfo;
    }

    public static Proxy getProxy() throws IOException {
        ProxyInformation proxyInfo = ApplicationProxyUtil.getProxyInformation();
        Proxy proxy = Proxy.NO_PROXY;

        if (ApplicationMessageConstants.USE_SYSTEM_PROXY.equals(proxyInfo.getProxyOption())) {
            proxy = getSystemProxyFor(ServerAPICommunicationUtil.getAPIUrl());
        } else if (ApplicationMessageConstants.MANUAL_CONFIG_PROXY.equals(proxyInfo.getProxyOption())) {
            System.setProperty(USE_SYSTEM_PROXY_PROP, "false");
            try {
                switch (proxyInfo.getProxyServerType()) {
                    case ApplicationStringConstants.SOCKS_PROXY_TYPE:
                        proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyInfo.getProxyServerAddress(),
                                proxyInfo.getProxyServerPort()));
                        break;
                    case ApplicationStringConstants.HTTP_PROXY_TYPE:
                    case ApplicationStringConstants.HTTPS_PROXY_TYPE:
                        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyInfo.getProxyServerAddress(),
                                proxyInfo.getProxyServerPort()));
                        break;
                    default:
                        throw new IllegalArgumentException(ApplicationMessageConstants.PROXY_SERVER_TYPE_NOT_VALID_MESSAGE);
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

    public static Proxy getRetryProxy() throws URISyntaxException {
        ProxyInformation proxyInfo = ApplicationProxyUtil.getProxyInformation();
        if (StringUtils.isNotEmpty(proxyInfo.getUsername()) && StringUtils.isNotEmpty(proxyInfo.getPassword())) {
            Authenticator.setDefault(new Authenticator() {
                protected java.net.PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxyInfo.getUsername(), proxyInfo.getPassword().toCharArray());
                };
            });
        }

        ProxySelector proxySelector = getProxySearch().getProxySelector();
        Proxy proxy = Proxy.NO_PROXY;
        if (proxySelector != null) {
            proxy = getProxy(proxySelector);
        }

        if (!Proxy.NO_PROXY.equals(proxy)) {
            LogUtil.printOutputLine(
                    MessageFormat.format(ApplicationMessageConstants.PROXY_FOUND, proxy.toString()));
        } else {
            LogUtil.printOutputLine(ApplicationMessageConstants.NO_PROXY_FOUND);
        }
        return proxy;
    }

    private static ProxySearch getProxySearch() {
        ProxySearch proxySearch = ProxySearch.getDefaultProxySearch();
        if (PlatformUtil.getCurrentPlattform() == Platform.WIN) {
            proxySearch.addStrategy(Strategy.IE);
            proxySearch.addStrategy(Strategy.FIREFOX);
        } else if (PlatformUtil.getCurrentPlattform() == Platform.LINUX) {
            proxySearch.addStrategy(Strategy.GNOME);
            proxySearch.addStrategy(Strategy.KDE);
            proxySearch.addStrategy(Strategy.FIREFOX);
        }
        return proxySearch;
    }

    private static Proxy getProxy(ProxySelector proxySelector) throws URISyntaxException {
        Proxy proxy = Proxy.NO_PROXY;
        List<Proxy> proxies = proxySelector.select(new URI(ServerAPICommunicationUtil.getAPIUrl()));
        if (proxySelector != null) {
            if (proxies != null) {
                loop: for (Proxy p : proxies) {
                    switch (p.type()) {
                        case HTTP:
                            proxy = p;
                            break loop;
                        case DIRECT:
                            proxy = p;
                            break;
                        default:
                            break;
                    }
                }
            }
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
