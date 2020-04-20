package com.kms.katalon.execution.webui.driver.test;

import static org.junit.Assert.*;

import java.io.File;

import static org.eclipse.core.runtime.Platform.*;

import org.junit.Test;

import com.kms.katalon.execution.webui.driver.SeleniumWebDriverProvider;

public class SeleniumWebDriverProviderTest {
	
	
	@Test
	public void getEdgeChromiumDriverPathTest(){
		String OS = getOS();
		String driverPath = SeleniumWebDriverProvider.getEdgeChromiumDriverPath();
		
		switch (OS) {
		case OS_WIN32:
			if (ARCH_X86_64.equals(getOSArch())){
				String check = "edgechromium_win64" + File.separator + "msedgedriver.exe";
				assertEquals(driverPath.endsWith(check), true);
			}
			else
			{
				String check = "edgechromium_win32" + File.separator + "msedgedriver.exe";	
				assertEquals(driverPath.endsWith(check), true);
			}
			break;
		case OS_MACOSX:			
			String check = "edgechromium_mac" + File.separator + "msedgedriver";
			assertEquals(driverPath.endsWith(check), true);	
			break;
		default:
			assertEquals(driverPath, "");
		}
	}
	
	@Test
	public void getTempEdgeChromiumDriverPathTest() throws Exception{
		String path = SeleniumWebDriverProvider.getTempEdgeChromiumDriverPath();
		//System.out.println(path);
		
		switch (getOS()) {
		case OS_WIN32:	
			assertEquals(path.endsWith("msedgedriver.exe"), true);
			break;
		default:
			assertEquals(path.endsWith("msedgedriver"), true);
			break;
		}
	}
}
