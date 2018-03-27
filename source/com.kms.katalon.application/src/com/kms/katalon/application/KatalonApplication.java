package com.kms.katalon.application;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.util.NetworkUtil;

public class KatalonApplication {

    private static final String UNKNOWN_HOST = "Unknown";

    public static final String SESSION_ID;

    public static final String MAC_ADDRESS;

    static {
        SESSION_ID = UUID.randomUUID().toString();

        MAC_ADDRESS = getMacAddress();
    }

    private static String getMacAddress() {
        String macAdress = "";
        try {
            macAdress = NetworkUtil.getMacAddress(NetworkUtil.getLocalHostAddress());
        } catch (SocketException | UnknownHostException e) {
        }
        return StringUtils.defaultString(macAdress, UNKNOWN_HOST);
    }

}
