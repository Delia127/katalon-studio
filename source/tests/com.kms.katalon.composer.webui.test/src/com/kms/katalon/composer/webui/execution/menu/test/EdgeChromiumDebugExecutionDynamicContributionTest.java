package com.kms.katalon.composer.webui.execution.menu.test;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

import com.kms.katalon.composer.webui.execution.menu.EdgeChromiumDebugExecutionDynamicContribution;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class EdgeChromiumDebugExecutionDynamicContributionTest {
	
	private EdgeChromiumDebugExecutionDynamicContribution contribution = new EdgeChromiumDebugExecutionDynamicContribution();
	
	@Test
	public void getLaunchModeTest() throws Exception{
		Method method = contribution.getClass().getDeclaredMethod("getLaunchMode");
		method.setAccessible(true);
		LaunchMode mode = (LaunchMode) method.invoke(contribution);
		
		assertEquals(LaunchMode.DEBUG, mode);
	}
}
