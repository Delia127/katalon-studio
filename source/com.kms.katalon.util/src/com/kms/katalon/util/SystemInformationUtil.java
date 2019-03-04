package com.kms.katalon.util;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class SystemInformationUtil {

    private OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static int  ONE_MB= 1048576;
    private static int  ONE_KB= 1024;
    public SystemInformationUtil() {

        /// [1] com.sun.management.OperatingSystemMXBean
        try {
            SystemInformationUtil.getProcessCpuLoad();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // percent cpu usage
    public static double getProcessCpuLoad() throws MalformedObjectNameException, ReflectionException, InstanceNotFoundException {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

        if (list.isEmpty()) {
            return 0.0;
        }
        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();

        if (value < 0.0) {
            return 0.0;  
        }
        return  value * 100.0; 
    }
    
    public static long totalPhysicalMemorySize() throws InstanceNotFoundException, AttributeNotFoundException,
            MalformedObjectNameException, ReflectionException, MBeanException {
        long totalMemory;
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        Object attribute = mBeanServer.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"),
                "TotalPhysicalMemorySize");

        totalMemory = Long.parseLong(attribute.toString()) / ONE_KB ;

        return totalMemory;
    }

    public static long freePhysicalMemorySize() throws InstanceNotFoundException, AttributeNotFoundException,
            MalformedObjectNameException, ReflectionException, MBeanException {
        long freeMemory;
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        Object attribute2 = mBeanServer.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"),
                "FreePhysicalMemorySize");

        freeMemory = Long.parseLong(attribute2.toString()) / ONE_KB;
        return freeMemory;
    }

    // ram
    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory()/ONE_MB;
    }

    public static long getUsedMemory() {
        return getMaxMemory() - getFreeMemory()/ONE_MB;
    }

    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory()/ONE_MB;
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory()/ONE_MB;
    }

    public static double getPercentageUsed() {
        return ((double) getUsedMemory() / getMaxMemory()) * 100;
    }

    public static double getPercentageUsedFormatted() {
        double usedPercentage = getPercentageUsed();
        return usedPercentage;
    }

    // ram

}
