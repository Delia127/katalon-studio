package com.kms.katalon.core.webui.util.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;

import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.osgi.framework.Bundle;

import com.kms.katalon.core.webui.util.OSUtil;
import com.kms.katalon.core.webui.util.WebDriverCleanerUtil;
import com.kms.katalon.execution.webui.driver.SeleniumWebDriverProvider;

public class WebDriverCleanerUtilTest {
	private final String PROCESS_NAME_CHROMIUM = "msedgedriver.exe";
			
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception{
		folder.create();
	}
	@Test
	public void cleanUpTest() throws Exception{		
		File logFile = folder.newFile();
		File errorLogFile = folder.newFile();
		File path = getDriverPath();
		if (OSUtil.isWindows() && OSUtil.is64Bit()) {
			
        	ProcessBuilder pb = new ProcessBuilder();
    		pb.directory(new File(path + File.separator + "edgechromium_win64"));
    		pb.command("cmd", "/c", PROCESS_NAME_CHROMIUM);
    		//pb.command(PROCESS_NAME_CHROMIUM);
    		pb.start();

    		boolean check = isProcessExists();
    		assertEquals("Process is existing", true, check);
    		
    		WebDriverCleanerUtil.cleanup(logFile, errorLogFile);
    		
    		check = isProcessExists();
    		assertEquals("Process does not exist anymore", false, check);
    		
        }	
	}
	
	private File getDriverPath(){
		//String packageName = this.getClass().getPackage().getName();
		String packageName = FileSystems.getDefault().getPath("").toAbsolutePath().toString();
		System.out.println(packageName);
		File f = new File(packageName + File.separator + "os_resources" + File.separator + "win64" + File.separator + "resources" + File.separator + "drivers");
		return f;
	}
	
	private boolean isProcessExists() throws Exception{
		ProcessBuilder pb = new ProcessBuilder("tasklist", "/fi", "\"IMAGENAME eq " + PROCESS_NAME_CHROMIUM + "\"");
		Process p = pb.start();
		String result = readOutput(p.getInputStream());	
		return result.contains(PROCESS_NAME_CHROMIUM);
	}
	
	
	private String readOutput(InputStream is) throws Exception{
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String s = null;
		StringBuilder builder = new StringBuilder();
		while ((s = br.readLine()) != null) {
			builder = builder.append(s);
		}
		br.close();
		isr.close();
		return builder.toString();
	}
}
