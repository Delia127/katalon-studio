package com.kms.katalon.core.webui.util.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.kms.katalon.core.webui.util.WebDriverCleanerUtil;

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
		String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("win")) {
        	String basePath = WebDriverCleanerUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        	basePath = basePath.substring(1, basePath.lastIndexOf('/'));
        	basePath = basePath.replace('/', '\\');
        	Path workingPath = Paths.get(basePath, "os_resources", "win64", "resources", "drivers", "edgechromium_win64");

        	
        	ProcessBuilder pb = new ProcessBuilder();
    		pb.directory(workingPath.toFile());
    		pb.command("cmd", "/c", PROCESS_NAME_CHROMIUM);
    		pb.start();

    		boolean check = isProcessExists();
    		assertEquals("Process is existing", true, check);
    		
    		WebDriverCleanerUtil.cleanup(logFile, errorLogFile);
    		
    		check = isProcessExists();
    		assertEquals("Process does not exist anymore", false, check);
    		
        }else{};		
	}
	
	private boolean isProcessExists() throws Exception{
		ProcessBuilder pb = new ProcessBuilder("tasklist", "/fi", "\"IMAGENAME eq " + PROCESS_NAME_CHROMIUM + "\"");
		Process p = pb.start();
		String result = readOutput(p.getInputStream());
		System.out.println(result);
		System.out.println(result.contains(PROCESS_NAME_CHROMIUM));
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
