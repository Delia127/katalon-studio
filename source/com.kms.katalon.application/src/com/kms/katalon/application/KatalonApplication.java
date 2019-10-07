package com.kms.katalon.application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.core.model.KatalonPackage;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.FileHashUtil;
import com.kms.katalon.util.NetworkUtil;

public class KatalonApplication {

    private static final String UNKNOWN_HOST = "Unknown";

    public static final String SESSION_ID;

    private static final String MAC_ADDRESS;

    public static final String USER_KEY;

    static {
        SESSION_ID = UUID.randomUUID().toString();

        MAC_ADDRESS = getMacAddress();

        USER_KEY = hashMacAndHostName();
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

    /**
     * TODO: Implement to get the unique Machine ID. The USER_KEY is the temporary ID.
     */
    public static String getMachineId() {
        return USER_KEY;
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
    
    public static KatalonPackage getKatalonPackage() {
        if (!isRunningInKatalonC()) {
            return KatalonPackage.KSE;
        } else {
            return KatalonPackage.ENGINE;
        }
    }
    
    private static boolean isRunningInKatalonC() {
        Properties props = System.getProperties();
        String launcherName = props.getProperty("eclipse.launcher.name");
        return launcherName.equalsIgnoreCase("katalonc");
    }

    public static String getMacAddress() {
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
