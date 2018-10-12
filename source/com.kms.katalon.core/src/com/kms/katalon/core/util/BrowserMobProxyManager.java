package com.kms.katalon.core.util;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.logging.KeywordLogger;

import groovy.json.JsonOutput;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.proxy.CaptureType;

public class BrowserMobProxyManager {
    
    private static KeywordLogger logger = KeywordLogger.getInstance();

    private static final ThreadLocal<BrowserMobProxy> browserMobProxyLookup = new ThreadLocal<BrowserMobProxy>();
    
    private static void logError(String message, Exception e) {
        logger.logError(message + ": " + e.getClass().getName() + " - " + e.getMessage());
    }
    
    private static AtomicInteger requestNumber;
    
    public static void init() {
    	requestNumber = new AtomicInteger();
    }
    
    public static final Proxy getWebServiceProxy(Proxy systemProxy) {
        try {
            BrowserMobProxy browserMobProxy = getOrCreateBrowserMobProxy(systemProxy);
            InetAddress connectableAddress = ClientUtil.getConnectableAddress();
            int browserMobProxyPort = browserMobProxy.getPort();
            Proxy proxy = new Proxy(
                    Type.HTTP,
                    new InetSocketAddress(connectableAddress, browserMobProxyPort));
            return proxy;
        } catch (Exception e) {
            logError("Cannot start BrowserMob proxy", e);
            return systemProxy;
        }
    }
    
    public static final synchronized void shutdownProxy() {
        try {
            BrowserMobProxy browserMobProxy = browserMobProxyLookup.get();
            if (browserMobProxy != null) {
                browserMobProxyLookup.remove();
                if (!browserMobProxy.isStarted()) {
                    browserMobProxy.stop();
                    browserMobProxy.abort();
                }
            }
        } catch (Exception e) {
            logError("Cannot shutdown BrowserMob proxy", e);
        }
    }
    
    public static final void newHar() {
        try {
            BrowserMobProxy browserMobProxy = browserMobProxyLookup.get();
            if (browserMobProxy != null) {
                browserMobProxy.newHar();
            }
        } catch (Exception e) {
            logError("Cannot create new HAR entry", e);
        }
    }
    
    public static final void endHar(RequestInformation requestInformation) {
        try {
            BrowserMobProxy browserMobProxy = browserMobProxyLookup.get();
            if (browserMobProxy != null) {
                
                requestInformation.setId(String.valueOf(requestNumber.incrementAndGet()));
                String threadName = Thread.currentThread().getName();
                String directoryPath = RunConfiguration.getReportFolder();
                File directory = new File(directoryPath, "requests" + File.separator + threadName);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File file = new File(directory, requestInformation.getId() + ".har");
                file.createNewFile();
                
                logger.logInfo(file.getAbsolutePath());
                
                Har har = browserMobProxy.endHar();
                String comment = JsonOutput.toJson(requestInformation);
                HarLog harLog = har.getLog();
                List<HarEntry> originalEntries = harLog.getEntries();
                List<KatalonHarEntry> newEntries = originalEntries.stream()
                        .map(entry -> {
                            KatalonHarEntry katalonEntry = new KatalonHarEntry(entry);
                            katalonEntry.set_katalonRequestInformation(requestInformation);
                            return katalonEntry;
                        })
                        .collect(Collectors.toList());
                originalEntries.clear();
                originalEntries.addAll(newEntries);
                har.writeTo(file);
            }
        } catch (Exception e) {
            logError("Cannot close HAR entry", e);
        }
    }

    private static BrowserMobProxy getOrCreateBrowserMobProxy(Proxy systemProxy) {
        BrowserMobProxy browserMobProxy = browserMobProxyLookup.get();
        if (browserMobProxy == null) {
            browserMobProxy = createBrowserMobProxy(systemProxy);
        }
        return browserMobProxy;
    }

    private static synchronized BrowserMobProxy createBrowserMobProxy(Proxy systemProxy) {
        BrowserMobProxy browserMobProxy = new BrowserMobProxyServer();
        if (!systemProxy.equals(Proxy.NO_PROXY)) {
            browserMobProxy.setChainedProxy((InetSocketAddress) systemProxy.address());
        }
        browserMobProxy.setTrustAllServers(true);
        browserMobProxy.start(0);
        browserMobProxy.newHar();
        browserMobProxy.setHarCaptureTypes(CaptureType.values());
        browserMobProxyLookup.set(browserMobProxy);
        logger.logInfo("Requests will be captured by BrowserMob proxy at port " + browserMobProxy.getPort());
        return browserMobProxy;
    }
}
