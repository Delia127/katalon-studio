package com.kms.katalon.selenium.ide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.selenium.ide.format.DefaultFormatter;
import com.kms.katalon.selenium.ide.format.EchoFormatter;
import com.kms.katalon.selenium.ide.format.Formatter;
import com.kms.katalon.selenium.ide.format.PauseFormatter;
import com.kms.katalon.selenium.ide.format.StoreFormatter;
import com.kms.katalon.selenium.ide.format.VerifyAndAssertFormatter;
import com.kms.katalon.selenium.ide.format.WaitForFormatter;
import com.kms.katalon.selenium.ide.model.Command;
import com.kms.katalon.selenium.ide.model.TestCase;

public final class SeleniumIdeFormatter {
	
	private static final String DATE_FORMAT = "dd-MMM-yyyy hh:mm:ss a";
	
	private String email;

	private static final SeleniumIdeFormatter INSTANCE = new SeleniumIdeFormatter();
	
	private final Map<String, Formatter> formatters = new LinkedHashMap<>();
	
	{
		formatters.put("assert", new VerifyAndAssertFormatter("assert"));
		formatters.put("verify", new VerifyAndAssertFormatter("verify"));
		formatters.put("store", new StoreFormatter());
		
		formatters.put("sendKeys", new DefaultFormatter());
		formatters.put("chooseCancelOnNextPrompt", new DefaultFormatter());
		
		formatters.put("waitForPageToLoad", new DefaultFormatter());
		formatters.put("waitForCondition", new DefaultFormatter());
		formatters.put("waitForFrameToLoad", new DefaultFormatter());
		formatters.put("waitForPopUp", new DefaultFormatter());
		
		formatters.put("waitFor", new WaitForFormatter());
		
		formatters.put("echo", new EchoFormatter());
		formatters.put("pause", new PauseFormatter());
		formatters.put("default", new DefaultFormatter());
	}
	
	public static SeleniumIdeFormatter getInstance() {
        return INSTANCE;
    }

	public String format(TestCase testCase) {
		StringBuilder builder = new StringBuilder();
		builder.append(getHeader(testCase));
		
		List<String> commands = formatCommands(testCase.getCommands());
		commands.forEach(c -> builder.append(c));
		
		builder.append(getFooter(testCase));
		return builder.toString();
	}
	
	public List<String> formatCommands(List<Command> commands) {
		List<String> formattedCommands = new ArrayList<>();
		commands.forEach(command -> {
			String formatted = formatCommand(command);
			formattedCommands.add(formatted);
		});
		return formattedCommands;				
	}
	
	public String formatCommand(Command command) {
		Formatter formatter = getFormatter(command.getCommand());
		if (formatter == null) {
			return String.format("Method %s is not found", command.getCommand());
		}
		String comment = String.format("\n\"%s | %s | %s\"\n", 
				encodeString(command.getCommand()), 
				encodeString(command.getTarget()), 
				encodeString(command.getValue()));
		String formatted = formatter.format(command);
		if (StringUtils.isBlank(formatted)) {
			return String.format("Method %s is not found\n", command.getCommand());
		}
		return comment + formatted;
	}

	public String getHeader(TestCase testCase) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(  "import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint\n" +
						"import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase\n" +
						"import static com.kms.katalon.core.testdata.TestDataFactory.findTestData\n" +
						"import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject\n" +
						"import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint\n" +
						"import com.kms.katalon.core.checkpoint.CheckpointFactory as CheckpointFactory\n" +
						"import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as MobileBuiltInKeywords\n" +
						"import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile\n" +
						"import com.kms.katalon.core.model.FailureHandling as FailureHandling\n" +
						"import com.kms.katalon.core.testcase.TestCase as TestCase\n" +
						"import com.kms.katalon.core.testcase.TestCaseFactory as TestCaseFactory\n" +
						"import com.kms.katalon.core.testdata.TestData as TestData\n" +
						"import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory\n" +
						"import com.kms.katalon.core.testobject.ObjectRepository as ObjectRepository\n" +
						"import com.kms.katalon.core.testobject.TestObject as TestObject\n" +
						"import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WSBuiltInKeywords\n" +
						"import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS\n" +
						"import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUiBuiltInKeywords\n" +
						"import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI\n" +
						"import internal.GlobalVariable as GlobalVariable\n");
		
		buffer.append(  "import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory\n" +
						"import static com.kms.katalon.core.webui.driver.KatalonWebDriverBackedSelenium.WAIT_FOR_PAGE_TO_LOAD_IN_SECONDS\n" + 
						"import com.kms.katalon.core.webui.driver.KatalonWebDriverBackedSelenium\n\n" +
						"import com.thoughtworks.selenium.Selenium\n" +
						"import org.openqa.selenium.firefox.FirefoxDriver\n" +
						"import org.openqa.selenium.WebDriver\n" +						
						"import static org.junit.Assert.*\n" +
						"import java.util.regex.Pattern\n" +
						"import static org.apache.commons.lang3.StringUtils.join\n\n");
		buffer.append("'----------------------------------------------------'\n");
		buffer.append("'This test case script is generated by Katalon Studio'\n");
		buffer.append(String.format("'Generated date: %s'\n", getCurrentDateTime()));
		buffer.append(String.format("'File path: %s'\n", encodeString(testCase.getFilePath())));
		buffer.append(String.format("'Generated by user email: %s'\n", this.email));
		buffer.append("'----------------------------------------------------'\n\n");
		buffer.append("String baseUrl = \""+ testCase.getBaseUrl() +"\"\n\n");
		buffer.append("WebUI.openBrowser(baseUrl)\n\n");
		buffer.append("selenium = new KatalonWebDriverBackedSelenium(baseUrl)\n");
		return buffer.toString();
	}

	public String getFooter(TestCase testCase) {
		return "\nWebUI.closeBrowser()";
	}
	
	private String encodeString(String filePath) {
		return filePath.replace("\\", Matcher.quoteReplacement(File.separator));
	}
	
	private String getCurrentDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        return formatter.format(Calendar.getInstance().getTime());
	}
	
	private Formatter getFormatter(String command) {
		if (StringUtils.isBlank(command)) {
			return null;
		}
		for (Map.Entry<String, Formatter> entry : formatters.entrySet()) {
		    String key = entry.getKey();
		    Formatter formatter = entry.getValue();
		    if (command.contains(key)) {
		    	return formatter;
		    }
		}
		return formatters.get("default");
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}
	
}
