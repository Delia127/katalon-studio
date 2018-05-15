package com.kms.katalon.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class NetworkUtil {
    private static final List<NetworkInterface> NETWORK_INTERFACES;

    static {
        NETWORK_INTERFACES = collectNetworkInterface();
    }

    private static List<NetworkInterface> collectNetworkInterface() {
        try {
            return Collections.list(NetworkInterface.getNetworkInterfaces());
        } catch (SocketException se) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Returns the local host address of current machine.
     * @throws UnknownHostException 
     */
    public static String getLocalHostAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
    
    /**
     * @return the first non loopback IP.
     * @throws SocketException 
     */
    public static String getFirstLocalNonLoopbackIpAddress() throws SocketException {
        SortedSet<String> addresses = new TreeSet<>();
        Iterator<NetworkInterface> iterator = NETWORK_INTERFACES.iterator();
        while (iterator.hasNext()) {
            NetworkInterface ni = iterator.next();
            Enumeration<InetAddress> addressEnumeration = ni.getInetAddresses();
            while (addressEnumeration.hasMoreElements()) {
                InetAddress address = addressEnumeration.nextElement();

                if (!address.isLoopbackAddress() && !address.getHostAddress().contains(":")) {
                    addresses.add(address.getHostAddress());
                }
            }
        }

        if (addresses.isEmpty()) {
            throw new SocketException("Failed to get non-loopback IP address!");
        }

        return addresses.first();
    }

    /**
     * @return the mac address of IP address if exist, Otherwise, null is returned
     * @throws SocketException
     */
    public static String getMacAddress(String ipAddress) throws SocketException {
        Iterator<NetworkInterface> iterator = NETWORK_INTERFACES.iterator();
        while (iterator.hasNext()) {
            NetworkInterface ni = iterator.next();
            Enumeration<InetAddress> addressEnumeration = ni.getInetAddresses();
            while (addressEnumeration.hasMoreElements()) {
                InetAddress address = addressEnumeration.nextElement();
                if (address.getHostAddress().equalsIgnoreCase(ipAddress) && (ni.getHardwareAddress() != null)) {
                    byte[] macAddress = ni.getHardwareAddress();

                    // build mac address.
                    StringBuilder sb = new StringBuilder();
                    for (int index = 0; index < macAddress.length - 1; index++) {
                        sb.append(String.format("%02X:", macAddress[index]));
                    }
                    sb.append(String.format("%02X", macAddress[macAddress.length - 1]));

                    return sb.toString();
                }
            }
        }
        return null;
    }
}
