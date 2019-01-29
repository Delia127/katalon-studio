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

public class SystemInforUtil {

    private OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public SystemInforUtil() {

        /// [1] com.sun.management.OperatingSystemMXBean
        try {
            SystemInforUtil.getProcessCpuLoad();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // percent cpu usage
    public static double getProcessCpuLoad() throws Exception {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });

        if (list.isEmpty())
            return Double.NaN;

        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)
            return Double.NaN;
        // returns a percentage value with 1 decimal point precision
        return ((int) (value * 1000) / 10.0);
    }

    public static String TotalPhysicalMemorySize() throws InstanceNotFoundException, AttributeNotFoundException,
            MalformedObjectNameException, ReflectionException, MBeanException {
        String totalmemory;
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        Object attribute = mBeanServer.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"),
                "TotalPhysicalMemorySize");

        totalmemory = Long.parseLong(attribute.toString()) / 1024 + "MB";

        return totalmemory;
    }

    public static String FreePhysicalMemorySize() throws InstanceNotFoundException, AttributeNotFoundException,
            MalformedObjectNameException, ReflectionException, MBeanException {
        String Freememory;
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        Object attribute2 = mBeanServer.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"),
                "FreePhysicalMemorySize");

        Freememory = Long.parseLong(attribute2.toString()) / 1024 + "MB";
        return Freememory;
    }

    // ram
    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getUsedMemory() {
        return getMaxMemory() - getFreeMemory();
    }

    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
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
