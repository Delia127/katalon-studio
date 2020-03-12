package com.kms.katalon.composer.webui.execution.handler.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.eclipse.core.runtime.Platform.getOS;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;

import com.kms.katalon.composer.webui.execution.handler.EdgeChromiumExecutionHandler;

public class EdgeChromiumExecutionHandlerTest {

	@Test
	public void canExecuteTest() throws Exception{
		final String EXPECTED_OS = Platform.OS_WIN32;				
		String currentOS = getOS();

		EdgeChromiumExecutionHandler handler = new EdgeChromiumExecutionHandler();
		
		
		assertEquals(EXPECTED_OS, currentOS);
		System.out.println(handler.canExecute());
		
		//assertTrue(handler.canExecute());
	}
}
