package com.kms.katalon.execution.mobile.provider.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.kms.katalon.execution.mobile.configuration.providers.MobileDeviceProvider;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.execution.mobile.device.IosSimulatorInfo;

public class MobileDeviceProviderTest {

    @Test
    public void getIosSimulatorsFromCommandResultTest() throws IOException, InterruptedException {
        Bundle bundle = Platform.getBundle("com.kms.katalon.execution.mobile.test");
        URL url = FileLocator.find(bundle, new Path("resources/ios_simulator_devices.txt"), null);
        List<String> simulatorsList = FileUtils.readLines(new File(FileLocator.resolve(url).getFile()));
        List<IosDeviceInfo> devices = MobileDeviceProvider.getIosSimulatorsFromCommandResult(simulatorsList);
        assertEquals(17, devices.size());

        IosDeviceInfo iphone8Device = devices.get(0);
        assertTrue(iphone8Device instanceof IosSimulatorInfo);
        assertEquals("iPhone 8", iphone8Device.getDeviceName());
        assertEquals("162497A3-F67D-4CD1-B381-11D8A4DCD18C", iphone8Device.getDeviceId());
        assertEquals("13.3", iphone8Device.getDeviceOSVersion());

        IosDeviceInfo iphone8PlusDevice = devices.get(1);
        assertTrue(iphone8PlusDevice instanceof IosSimulatorInfo);
        assertEquals("iPhone 8 Plus", iphone8PlusDevice.getDeviceName());
        assertEquals("243487B3-FF5A-43AB-B3F5-0D966753BFDA", iphone8PlusDevice.getDeviceId());
        assertEquals("13.3", iphone8PlusDevice.getDeviceOSVersion());

        IosDeviceInfo iphone11Device = devices.get(2);
        assertTrue(iphone11Device instanceof IosSimulatorInfo);
        assertEquals("iPhone 11", iphone11Device.getDeviceName());
        assertEquals("E49448E8-D0C1-45B1-BC3E-2EBEA6DA1006", iphone11Device.getDeviceId());
        assertEquals("13.3", iphone11Device.getDeviceOSVersion());

        IosDeviceInfo iphone11ProDevice = devices.get(3);
        assertTrue(iphone11ProDevice instanceof IosSimulatorInfo);
        assertEquals("iPhone 11 Pro", iphone11ProDevice.getDeviceName());
        assertEquals("7A5DCA95-C100-4546-BAED-6C09F78D41BF", iphone11ProDevice.getDeviceId());
        assertEquals("13.3", iphone11ProDevice.getDeviceOSVersion());

        IosDeviceInfo iPadProDevice = devices.get(5);
        assertTrue(iPadProDevice instanceof IosSimulatorInfo);
        assertEquals("iPad Pro (9.7-inch)", iPadProDevice.getDeviceName());
        assertEquals("ACEE2A76-8D69-4785-B7AB-48978C9DBCE3", iPadProDevice.getDeviceId());
        assertEquals("13.3", iPadProDevice.getDeviceOSVersion());

        IosDeviceInfo iPadPro3rdGenDevice = devices.get(8);
        assertTrue(iPadPro3rdGenDevice instanceof IosSimulatorInfo);
        assertEquals("iPad Pro (12.9-inch) (3rd generation)", iPadPro3rdGenDevice.getDeviceName());
        assertEquals("2A800B30-7EEB-49A0-A500-2B2EDAF36B51", iPadPro3rdGenDevice.getDeviceId());
        assertEquals("13.3", iPadPro3rdGenDevice.getDeviceOSVersion());

        IosDeviceInfo appleTVDevice = devices.get(10);
        assertTrue(appleTVDevice instanceof IosSimulatorInfo);
        assertEquals("Apple TV", appleTVDevice.getDeviceName());
        assertEquals("B9FB882F-C698-44E8-983F-C5C2EA320417", appleTVDevice.getDeviceId());
        assertEquals("13.3", appleTVDevice.getDeviceOSVersion());

        IosDeviceInfo appleTV4KDevice = devices.get(12);
        assertTrue(appleTV4KDevice instanceof IosSimulatorInfo);
        assertEquals("Apple TV 4K (at 1080p)", appleTV4KDevice.getDeviceName());
        assertEquals("C82ED800-0F57-4E46-BDE1-9D8DC60EE33B", appleTV4KDevice.getDeviceId());
        assertEquals("13.3", appleTV4KDevice.getDeviceOSVersion());
        
        IosDeviceInfo appleWatchDevice = devices.get(13);
        assertTrue(appleWatchDevice instanceof IosSimulatorInfo);
        assertEquals("Apple Watch Series 4 - 40mm", appleWatchDevice.getDeviceName());
        assertEquals("4DF1EE07-C802-4AFD-B7B1-33D8A8B37C3E", appleWatchDevice.getDeviceId());
        assertEquals("6.1", appleWatchDevice.getDeviceOSVersion());
    }
}
