package com.kms.katalon.composer.execution.jobs;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.execution.mobile.device.AndroidSDKDownloadManager;
import com.kms.katalon.execution.mobile.device.AndroidSDKDownloadMessage;
import com.kms.katalon.execution.mobile.device.AndroidSDKLocator;
import com.kms.katalon.execution.mobile.exception.AndroidSetupException;
import com.kms.katalon.util.listener.EventListener;

public class DownloadSDKProgress implements IRunnableWithProgress, EventListener<String> {

    private IProgressMonitor monitor;
    
    private AndroidSDKLocator sdkLocator;
    
    public DownloadSDKProgress(AndroidSDKLocator sdkLocator) {
        this.sdkLocator = sdkLocator;
    }
    
    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        this.monitor = monitor;
        monitor.beginTask(ComposerExecutionMessageConstants.DIA_JOB_DOWNLOAD_AND_INSTALL_ANDROID_SDK, 100);
        
        AndroidSDKDownloadManager downloadManager = new AndroidSDKDownloadManager(sdkLocator);
        downloadManager.addListener(this, Arrays.asList(AndroidSDKDownloadMessage.EVENT_NAME));
        try {
            downloadManager.downloadAndInstall();
        } catch (AndroidSetupException e) {
            throw new InvocationTargetException(e);
        }
    }

    @Override
    public void handleEvent(String event, Object object) {
        AndroidSDKDownloadMessage message = (AndroidSDKDownloadMessage) object;
        monitor.setTaskName(message.getMessage());
        monitor.worked(message.getWorkingProgess());
    }
}