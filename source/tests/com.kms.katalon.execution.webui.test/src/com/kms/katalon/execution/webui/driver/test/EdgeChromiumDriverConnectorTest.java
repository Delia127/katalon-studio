package com.kms.katalon.execution.webui.driver.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.EdgeChromiumDriverConnector;

public class EdgeChromiumDriverConnectorTest {
	private EdgeChromiumDriverConnector driverConnector;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	public EdgeChromiumDriverConnectorTest() throws Exception {
		setUp();
	}
	
	private void setUp() throws Exception{		
		folder.create();
		File newFolder = folder.newFolder("config");
		driverConnector = new EdgeChromiumDriverConnector(newFolder.getAbsolutePath());
	}
	
	@Test
	public void getDriverTypeTest(){
		DriverType driverType = driverConnector.getDriverType();
		String result = driverType.getName();
		String expected = "EDGE_CHROMIUM_DRIVER";
		assertEquals(expected, result);
	}
	
	@Test
	public void getSystemPropertiesTest(){
		Map<String, Object> testMap = driverConnector.getSystemProperties();
		String keyTest = StringConstants.CONF_PROPERTY_EDGE_CHROMIUM_DRIVER_PATH;
		Object valueTest = driverConnector.getEdgeDriverPath();
		
		assertTrue(testMap.containsKey(keyTest));
		assertTrue(testMap.containsValue(valueTest));
	}
	
	@Test
	public void cloneTest(){
		driverConnector.getUserConfigProperties().put("testKey", "testValue");
		IDriverConnector cloneConnector = driverConnector.clone();
		
		assertEquals(driverConnector.getParentFolderPath(), cloneConnector.getParentFolderPath());
		assertEquals("testValue", cloneConnector.getDriverPropertyValue("testKey"));
	}
}
