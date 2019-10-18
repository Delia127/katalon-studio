package com.kms.katalon.execution.mobile.device;

import static com.kms.katalon.execution.mobile.device.AndroidSDKDownloadMessage.EVENT_NAME;
import static com.kms.katalon.execution.mobile.device.AndroidSDKDownloadMessage.create;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.runtime.Platform;
import org.xml.sax.InputSource;

import com.google.common.collect.ImmutableList;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.core.util.internal.ProxyUtil;
import com.kms.katalon.core.util.internal.ZipUtil;
import com.kms.katalon.execution.mobile.constants.ExecutionMobileMessageConstants;
import com.kms.katalon.execution.mobile.exception.AndroidSetupException;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.listener.EventListener;
import com.kms.katalon.util.listener.EventManager;

public class AndroidSDKDownloadManager implements EventManager<String> {

    private static final String REPO_ROOT_URL = "http://dl-ssl.google.com/android/repository/";

    private static final String REPO_LIST = "repository-12.xml";

    private static final String SDK_ELEMENT_PATTERN = "sdk:";
    
    private static final int CONNECTION_TIMEOUT = 60000;
    
    private static final int READ_TIMEOUT = 10000;

    private AndroidSDKLocator sdkLocator;

    private Map<String, Set<EventListener<String>>> eventListeners = new HashMap<>();

    public AndroidSDKDownloadManager(AndroidSDKLocator sdkLocator) {
        this.sdkLocator = sdkLocator;
    }

    @Override
    public Iterable<EventListener<String>> getListeners(String event) {
        return eventListeners.get(event);
    }

    @Override
    public void addListener(EventListener<String> listener, Iterable<String> events) {
        events.forEach(e -> {
            Set<EventListener<String>> listenerOnEvent = eventListeners.get(e);
            if (listenerOnEvent == null) {
                listenerOnEvent = new HashSet<>();
            }
            listenerOnEvent.add(listener);
            eventListeners.put(e, listenerOnEvent);
        });
    }

    private class SDKArchivePack {
        String name;

        private int major;

        private int minor;

        private int micro;
        
        private int preview;

        private List<SDKArchive> archives;

        private SDKArchive getSuitableArchive() {
            if (archives == null) {
                return null;
            }
            String systemOS = getOS();
            return archives.stream().filter(arch -> systemOS.equals(arch.hostOS)).findFirst().get();
        }

        private String version() {
            return String.format("%d.%d.%d", major, minor, micro);
        }
    }

    private static String getOS() {
        String osName = StringUtils.deleteWhitespace(System.getProperty("os.name")).toLowerCase();
        if (osName.contains("windows")) {
            return "windows";
        }
        if (osName.contains("macos")) {
            return "macosx";
        }
        return "linux";
    }

    private class SDKArchive {
        private long size;

        private String checksum;

        private String checksumType;

        private String url;

        private String hostOS;
    }

    private class SDKUrlHolder {
        private SDKArchivePack platformToolPack;

        private List<SDKArchivePack> buildToolPacks;
    }

    private void throwHelperException(String errorMessage, String sdkRepo) throws AndroidSetupException {
        throw new AndroidSetupException(MessageFormat.format(ExecutionMobileMessageConstants.MSG_SDK_COULD_NOT_CONNECT,
                sdkRepo, errorMessage, getOS(), sdkLocator.getPlatformToolsFolder(), sdkLocator.getBuildToolsFolder()));
    }

    private Proxy getProxy() throws IOException, AndroidSetupException {
        try {
            return ProxyUtil.getProxy(ProxyPreferences.getProxyInformation());
        } catch (URISyntaxException e) {
            throw new AndroidSetupException(e.getMessage());
        }
    }

    public void downloadAndInstall() throws AndroidSetupException {
        String sdkRepo = REPO_ROOT_URL + REPO_LIST;
        HttpURLConnection httpConnection = null;
        InputStream is = null;
        try {
            URL url = new URL(sdkRepo);
            logAndInvoke(create(
                    MessageFormat.format(ExecutionMobileMessageConstants.MSG_SDK_FETCHING_SDK_INFO, sdkRepo), 0));

            is = url.openConnection(getProxy()).getInputStream();
            String responseBody = IOUtils.toString(is);

            logAndInvoke(create(ExecutionMobileMessageConstants.MSG_SDK_FETCH_COMPLETED, 10));
            SDKUrlHolder urlHolder = parseSDKInfo(responseBody);
            downloadSDK(urlHolder);
        } catch (IOException e) {
            throwHelperException(e.getMessage(), sdkRepo);
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }

            if (is != null) {
                IOUtils.closeQuietly(is);
            }
        }
    }

    private void downloadUsingProxy(Proxy proxy, URL url, File file) throws IOException {
        InputStream inputStream = null;
        try (final OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));) {
            URLConnection connection = url.openConnection(proxy);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            inputStream = connection.getInputStream();
            final byte[] buffer = new byte[65536];
            while (true) {
                final int len = inputStream.read(buffer);
                if (len < 0) {
                    break;
                }
                outputStream.write(buffer, 0, len);
            }
        } catch (final IOException ex) {
            file.delete();
            throw ex;
        } finally {
            inputStream.close();
        }
    }

    private SDKUrlHolder parseSDKInfo(String xmlContent) throws AndroidSetupException {
        try {
            logAndInvoke(create(ExecutionMobileMessageConstants.MSG_SDK_PARSING_SDK_INFO, 0));
            Element rootElement = new SAXReader().read(new InputSource(new StringReader(xmlContent))).getRootElement();
            SDKUrlHolder urlHolder = new SDKUrlHolder();

            Element platfromToolElement = rootElement.element("platform-tool");
            urlHolder.platformToolPack = parseArchPack(platfromToolElement);

            List<SDKArchivePack> buildToolArchLst = new ArrayList<>();
            @SuppressWarnings("unchecked")
            Iterator<Element> buildToolElementIterator = rootElement.elementIterator("build-tool");
            while (buildToolElementIterator.hasNext()) {
                Element buildToolElement = buildToolElementIterator.next();
                buildToolArchLst.add(parseArchPack(buildToolElement));
            }
            urlHolder.buildToolPacks = ImmutableList.copyOf(buildToolArchLst);

            logAndInvoke(create(ExecutionMobileMessageConstants.MSG_SDK_PARSE_COMPLETED, 10));
            return urlHolder;
        } catch (DocumentException e) {
            throw new AndroidSetupException(e.getMessage());
        }
    }

    private SDKArchivePack parseArchPack(Element archPackElement) {
        SDKArchivePack archPack = new SDKArchivePack();
        archPack.name = archPackElement.getName().replaceFirst(SDK_ELEMENT_PATTERN, "");
        Element revElement = archPackElement.element("revision");
        archPack.major = Integer.valueOf(revElement.elementText("major"));
        archPack.minor = Integer.valueOf(revElement.elementText("minor"));
        archPack.micro = Integer.valueOf(revElement.elementText("micro"));
        if (revElement.elementText("preview") != null) {
            archPack.preview = Integer.valueOf(revElement.elementText("preview"));
        }

        List<SDKArchive> archiveLst = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Iterator<Element> archiveElementIterator = archPackElement.element("archives").elementIterator("archive");
        while (archiveElementIterator.hasNext()) {
            Element archElement = archiveElementIterator.next();
            SDKArchive archive = new SDKArchive();
            archive.size = Long.valueOf(archElement.elementText("size"));
            archive.url = archElement.elementText("url");
            archive.hostOS = archElement.elementText("host-os");

            Element checksumElement = archElement.element("checksum");
            archive.checksum = checksumElement.getText();
            archive.checksumType = checksumElement.attributeValue("type");

            archiveLst.add(archive);
        }
        archPack.archives = ImmutableList.copyOf(archiveLst);
        return archPack;
    }

    private void logAndInvoke(AndroidSDKDownloadMessage sdkMessage) {
        LogUtil.printOutputLine(sdkMessage.getMessage());
        invoke(EVENT_NAME, sdkMessage);
    }

    private void downloadAndExtract(String fileName, String url, File downloadFolder, File extractedFolder,
            int startFragment) throws IOException, AndroidSetupException {
        File downloadedFile = new File(downloadFolder, fileName);
        try {
            logAndInvoke(create(
                    MessageFormat.format(ExecutionMobileMessageConstants.MSG_SDK_DOWNLOADING_X_FROM_Y, fileName, url),
                    0));
            Proxy proxy = getProxy();
            if (proxy != null && proxy != Proxy.NO_PROXY && proxy.type() != Proxy.Type.DIRECT) { 
                downloadUsingProxy(getProxy(), new URL(url), downloadedFile);
            } else {
                FileUtils.copyURLToFile(new URL(url), downloadedFile, CONNECTION_TIMEOUT, READ_TIMEOUT);
            }
            logAndInvoke(create(ExecutionMobileMessageConstants.MSG_SDK_DOWNLOAD_COMPLETED, 30));

            logAndInvoke(create(MessageFormat.format(ExecutionMobileMessageConstants.MSG_SDK_EXTRACTING_X_TO_Y,
                    fileName, extractedFolder.getAbsolutePath()), 0));
            ZipUtil.extractContent(downloadedFile, extractedFolder, startFragment);
            logAndInvoke(create(ExecutionMobileMessageConstants.MSG_SDK_EXTRACT_COMPLETED, 10));
        } finally {
            if (downloadedFile.exists()) {
                try {
                    FileUtils.forceDelete(downloadedFile);
                } catch (IOException ignored) {}
            }
        }
    }

    private void downloadSDK(SDKUrlHolder urlHolder) throws IOException, AndroidSetupException {
        logAndInvoke(create(ExecutionMobileMessageConstants.MSG_SDK_DONWLOADING_SDK, 0));
        File downloadFolder = new File(GlobalStringConstants.APP_TEMP_DIR, "zip/android-sdk");

        SDKArchivePack platformToolPack = urlHolder.platformToolPack;
        SDKArchive platformArchive = platformToolPack.getSuitableArchive();
        File platformToolFolder = sdkLocator.getPlatformToolsFolder();
        if (!platformToolFolder.exists()) {
            platformToolFolder.mkdirs();
        }
        int ignoredFragement = 1;
        downloadAndExtract(platformArchive.url, REPO_ROOT_URL + platformArchive.url, downloadFolder, platformToolFolder,
                ignoredFragement);

        SDKArchivePack buildToolPack = getSuitableBuildToolPack(urlHolder.buildToolPacks);
        SDKArchive buildToolArchive = buildToolPack.getSuitableArchive();
        File buildToolsFolder = sdkLocator.getBuildToolsFolder();
        File buildToolRevFolder = new File(buildToolsFolder, buildToolPack.version());
        if (!buildToolRevFolder.exists()) {
            buildToolRevFolder.mkdirs();
        }
        downloadAndExtract(buildToolArchive.url, REPO_ROOT_URL + buildToolArchive.url, downloadFolder,
                buildToolRevFolder, ignoredFragement);

        String sdkToolsUrl = getSdkTooksUrl();

        File toolsFolder = sdkLocator.getToolsFolder();
        if (!toolsFolder.exists()) {
            toolsFolder.mkdirs();
        }
        downloadAndExtract(sdkToolsUrl, REPO_ROOT_URL + sdkToolsUrl, downloadFolder, sdkLocator.getToolsFolder(), ignoredFragement);
        logAndInvoke(create(ExecutionMobileMessageConstants.MSG_SDK_DOWNLOAD_AND_INSTALL_SDK_COMPELTED, 0));
    }
    
    private SDKArchivePack getSuitableBuildToolPack(List<SDKArchivePack> buildToolPacks) {
        for (SDKArchivePack archivePack : buildToolPacks) {
            if (archivePack.preview > 0) {
                continue;
            }
            return archivePack;
        }
        return buildToolPacks.get(0);
    }

    private String getSdkTooksUrl() {
        switch (Platform.getOS()) {
            case Platform.OS_WIN32:
                return "sdk-tools-windows-4333796.zip";
            case Platform.OS_MACOSX:
                return "sdk-tools-darwin-4333796.zip";
            default:
                return "sdk-tools-linux-4333796.zip";
        }
    }
}
