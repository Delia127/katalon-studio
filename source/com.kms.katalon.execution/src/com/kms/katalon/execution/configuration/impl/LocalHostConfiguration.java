package com.kms.katalon.execution.configuration.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.execution.configuration.IHostConfiguration;
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.logging.LogUtil;

public class LocalHostConfiguration implements IHostConfiguration {
    
    private int port;

    public LocalHostConfiguration() {
        port = -1;
    }

    @Override
    public String getOS() {
        return ExecutionUtil.getLocalOS();
    }

    @Override
    public String getHostName() {
        return ExecutionUtil.getLocalHostName();
    }

    @Override
    public String getHostAddress() {
        return ExecutionUtil.getLocalHostAddress();
    }

    @Override
    public int getHostPort() {
        if (port >= 0) {
            return port;
        }

        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            port = socket.getLocalPort();
            return port;
        } catch (IOException e) {
            LogUtil.logError(e);
        } finally {
            IOUtils.closeQuietly(socket);
        }
        return -1;
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(RunConfiguration.HOST_NAME, getHostName());
        properties.put(RunConfiguration.HOST_OS, getOS());
        properties.put(RunConfiguration.HOST_ADDRESS, getHostAddress());
        properties.put(RunConfiguration.HOST_PORT, getHostPort());
        
        return properties;
    }
}
