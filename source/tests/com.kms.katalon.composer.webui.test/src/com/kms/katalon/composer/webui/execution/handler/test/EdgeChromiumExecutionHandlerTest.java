package com.kms.katalon.composer.webui.execution.handler.test;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.eclipse.core.runtime.Platform.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.kms.katalon.composer.webui.execution.handler.EdgeChromiumExecutionHandler;
import com.kms.katalon.execution.configuration.IRunConfiguration;

public class EdgeChromiumExecutionHandlerTest {

	private EdgeChromiumExecutionHandler handler = new EdgeChromiumExecutionHandler();
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void canExecuteTest() throws Exception{
		boolean result = handler.canExecute();
	}
	
	/*@Test
	public void getRunConfigurationForExecutionTest() throws Exception{
		folder.create();
		File newFolder = folder.newFolder("testTempFolder");
		Method method = handler.getClass().getDeclaredMethod("getRunConfigurationForExecution", String.class);
		method.setAccessible(true);
		IRunConfiguration config = (IRunConfiguration) method.invoke(handler, newFolder.getAbsolutePath());
		
	}*/
}
