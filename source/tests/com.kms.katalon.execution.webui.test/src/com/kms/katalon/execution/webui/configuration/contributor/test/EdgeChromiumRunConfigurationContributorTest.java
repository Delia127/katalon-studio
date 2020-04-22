package com.kms.katalon.execution.webui.configuration.contributor.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.kms.katalon.execution.webui.configuration.contributor.EdgeChromiumRunConfigurationContributor;

public class EdgeChromiumRunConfigurationContributorTest {
	private EdgeChromiumRunConfigurationContributor contributor = new EdgeChromiumRunConfigurationContributor();
	
	@Test
	public void getIdTest(){
		String result = contributor.getId();
		String expected = "Edge Chromium";
		assertEquals(expected, result);
	}
	
	@Test
	public void getPreferredOrderTest(){
		int result = contributor.getPreferredOrder();
		int expected = 5;
		assertEquals(expected, result);
	}
	
	/*@Test
	public void getRunConfigurationTest(){
		contributor.getRunConfiguration(projectDir);
	}*/
}
