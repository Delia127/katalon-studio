package com.kms.katalon.application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.FileHashUtil;
import com.kms.katalon.util.NetworkUtil;

public class KatalonApplication {

    private static final String UNKNOWN_HOST = "Unknown";

    public static final String SESSION_ID;

    public static final String MAC_ADDRESS;

    static {
        SESSION_ID = UUID.randomUUID().toString();

        MAC_ADDRESS = hashMacAndHostName();
    }
    
    private static String hashMacAndHostName() {
        String hostName = getDefaultLocalName();
        String macAddress = getMacAddress();
        
        LocalMacAdress localMacAddress = new LocalMacAdress(hostName, macAddress);
        try {
            return FileHashUtil.hash(JsonUtil.toJson(localMacAddress), "MD5");
        } catch (NoSuchAlgorithmException | IOException e) {
            LogUtil.logError(e);
            return macAddress;
        }
    }

    private static String getDefaultLocalName() {
        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostName = UNKNOWN_HOST;
        }
        return hostName;
    }

    private static String getMacAddress() {
        String macAdress = "";
        try {
            macAdress = NetworkUtil.getMacAddress(NetworkUtil.getLocalHostAddress());
        } catch (SocketException | UnknownHostException e) {
        }
        return StringUtils.defaultString(macAdress, UNKNOWN_HOST);
    }
    
    private static class LocalMacAdress {
        private String hostName;
        private String macAddress;
        
        public LocalMacAdress(String hostName, String macAddress) {
            this.setHostName(hostName);
            this.setMacAddress(macAddress);
        }

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }
    }
}
