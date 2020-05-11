package com.kms.katalon.core.webui.util.test;

import static org.eclipse.core.runtime.Platform.getOS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.katalon.core.webui.util.WebDriverPropertyUtil;


public class WebDriverPropertyUtilTest {
	private final String EDGE_OPTIONS_CAPABILITY_TEST = "ms:edgeOptions";
	private final String CHROME_ARGUMENT_PROPERTY_KEY_TEST = "args";
	
	@Test
	public void getDesiredCapabilitiesForEdgeChromiumTest() throws Exception{
		Map<String, Object> testMap = new HashMap<>();
		testMap.put("test", 100);
		DesiredCapabilities capabilities = WebDriverPropertyUtil.getDesiredCapabilitiesForEdgeChromium(testMap, true);
		String os = getOS();
		if(os.equals(org.eclipse.core.runtime.Platform.OS_WIN32)){
			assertEquals(capabilities.getPlatform(), Platform.WINDOWS);
			assertEquals(capabilities.getCapability("test"), 100);
		}else if (os.equals(org.eclipse.core.runtime.Platform.OS_MACOSX)){
			assertEquals(capabilities.getPlatform(), Platform.MAC);
			assertEquals(capabilities.getCapability("test"), 100);
		}
	}
	
	@Test
	public void addArgumentsForEdgeChromiumTest() throws Exception{
		Map<String, Object> testMap = new HashMap<>();
		testMap.put("test", 100);
		DesiredCapabilities capabilities = WebDriverPropertyUtil.getDesiredCapabilitiesForEdgeChromium(testMap, false);
		WebDriverPropertyUtil.addArgumentsForEdgeChromium(capabilities, "a", "b", "c");
		
		
		assertNotNull(capabilities.getCapability(EDGE_OPTIONS_CAPABILITY_TEST));
		@SuppressWarnings("unchecked")
		Map<String, Object> edgeOptionsTest = (Map<String, Object>) capabilities.getCapability(EDGE_OPTIONS_CAPABILITY_TEST);
		
		assertNotNull(edgeOptionsTest.get(CHROME_ARGUMENT_PROPERTY_KEY_TEST));
		@SuppressWarnings("unchecked")
		List<String> argsEntryTest = (List<String>) edgeOptionsTest.get(CHROME_ARGUMENT_PROPERTY_KEY_TEST);
		
		assertTrue(argsEntryTest.contains("a"));
		assertTrue(argsEntryTest.contains("b"));
		assertTrue(argsEntryTest.contains("c"));
	}
	
	@Test
	public void removeArgumentsForEdgeChromiumTest() throws Exception{
		Map<String, Object> testMap = new HashMap<>();
		testMap.put("test", 100);
		DesiredCapabilities capabilities = WebDriverPropertyUtil.getDesiredCapabilitiesForEdgeChromium(testMap, false);
		WebDriverPropertyUtil.addArgumentsForEdgeChromium(capabilities, "a", "b", "c");
		WebDriverPropertyUtil.removeArgumentsForEdgeChromium(capabilities, "b", "c");
		
		assertNotNull(capabilities.getCapability(EDGE_OPTIONS_CAPABILITY_TEST));
		@SuppressWarnings("unchecked")
		Map<String, Object> edgeOptionsTest = (Map<String, Object>) capabilities.getCapability(EDGE_OPTIONS_CAPABILITY_TEST);

		assertNotNull(edgeOptionsTest.get(CHROME_ARGUMENT_PROPERTY_KEY_TEST));
		@SuppressWarnings("unchecked")
		List<String> argsEntryTest = (List<String>) edgeOptionsTest.get(CHROME_ARGUMENT_PROPERTY_KEY_TEST);
		
		assertTrue(argsEntryTest.contains("a"));
		assertTrue(!argsEntryTest.contains("b"));
		assertTrue(!argsEntryTest.contains("c"));
	}
}
