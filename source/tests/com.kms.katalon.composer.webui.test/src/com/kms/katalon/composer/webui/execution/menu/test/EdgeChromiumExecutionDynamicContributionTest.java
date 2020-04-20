package com.kms.katalon.composer.webui.execution.menu.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.webui.constants.ImageConstants;
import com.kms.katalon.composer.webui.execution.menu.EdgeChromiumExecutionDynamicContribution;

public class EdgeChromiumExecutionDynamicContributionTest {

	private EdgeChromiumExecutionDynamicContribution contribution;
	
	@Before
	public void setUp(){
		contribution = new EdgeChromiumExecutionDynamicContribution();
	}
	
	@Test
	public void getIconUriTest() throws Exception{		
		//ImageConstants.
		Method method = contribution.getClass().getDeclaredMethod("getIconUri");
		method.setAccessible(true);
		String result = (String) method.invoke(contribution);
		String expected = "/icons/execution/edge_chromium_16.png";
		assertEquals(result.endsWith(expected), true);
	}
	
	@Test
	public void getDriverTypeNameTest() throws Exception{		
		Method method = contribution.getClass().getDeclaredMethod("getDriverTypeName");
		method.setAccessible(true);
		String result = (String) method.invoke(contribution);
		String expected = "Edge Chromium";
		assertEquals(expected, result);
	}
	
	@Test
	public void getCommandIdTest() throws Exception{
		Method method = contribution.getClass().getDeclaredMethod("getCommandId");
		method.setAccessible(true);
		String result = (String) method.invoke(contribution);
		String expected = "com.kms.katalon.composer.webui.execution.command.edge.chromium";
		assertEquals(expected, result);
	}
	
	@Test
	public void getMenuLabelTest() throws Exception{
		Method method = contribution.getClass().getDeclaredMethod("getMenuLabel");
		method.setAccessible(true);
		String result = (String) method.invoke(contribution);
		String expected = "Edge Chromium";
		assertEquals(expected, result);
	}
}
