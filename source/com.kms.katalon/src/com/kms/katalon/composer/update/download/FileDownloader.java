package com.kms.katalon.composer.update.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.kms.katalon.composer.update.UpdateException;
import com.kms.katalon.composer.update.util.NetworkUtils;
import com.kms.katalon.core.util.internal.ProxyUtil;
import com.kms.katalon.execution.preferences.ProxyPreferences;

public class FileDownloader {
    
    public static final long UNKNOWN_SIZE = -1L;

    public static final long NOTIFICATION_DELAY = 200L;

    private final long fileSizeInBytes;

    private List<DownloadProgressListener> listeners;

    private Boolean notificationSending = false;

    {
        listeners = new LinkedList<>();
    }

    public FileDownloader(long fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
    }

    public void addListener(DownloadProgressListener l) {
        listeners.add(l);
    }

    private void notifyProgressChanges(long progress, long total, long speedInKBPs) {
        synchronized (notificationSending) {
            if (notificationSending) {
                return;
            }
        }
        new Thread(() -> {
            notificationSending = true;
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime <= NOTIFICATION_DELAY) {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException ignored) {}
            }

            listeners.stream().forEach(l -> l.onProgressUpdate(progress, total, speedInKBPs));
            notificationSending = false;
        }).start();
    }

    public void download(String url, OutputStream os) throws UpdateException {
        InputStream inputStream = null;
        URLConnection connection = null;
        try {
            connection = NetworkUtils.createURLConnection(url,
                    ProxyUtil.getProxy(ProxyPreferences.getProxyInformation()));
            inputStream = connection.getInputStream();

            byte data[] = new byte[1024 * 4];

            int count;
            long progress = 0L;
            long startDownloadTime = System.currentTimeMillis();

            while ((count = inputStream.read(data)) != -1) {
                progress += count;

                os.write(data, 0, count);

                long speedInKBPs = progress * 1000L / Math.max(System.currentTimeMillis() - startDownloadTime, 1L);
                notifyProgressChanges(progress, fileSizeInBytes, speedInKBPs);
            }
        } catch (IOException | GeneralSecurityException | URISyntaxException e) {
            throw new UpdateException(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ignored) {}

            IOUtils.close(connection);
        }
    }
}
