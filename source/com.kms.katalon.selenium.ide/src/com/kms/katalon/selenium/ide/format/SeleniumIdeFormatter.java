package com.kms.katalon.selenium.ide.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.selenium.ide.model.Command;
import com.kms.katalon.selenium.ide.model.TestCase;

public final class SeleniumIdeFormatter {

	private static final SeleniumIdeFormatter INSTANCE = new SeleniumIdeFormatter();
	
	private final Map<String, Formatter> formatters = new HashMap<>();
	
	{
		formatters.put("assert", new AssertFormatter());
		formatters.put("default", new DefaultFormatter());
	}
	
	public static SeleniumIdeFormatter getInstance() {
        return INSTANCE;
    }

	public StringBuilder format(TestCase testCase) {
		StringBuilder builder = new StringBuilder();
		builder.append(getHeader(testCase));
		
		List<String> commands = formatCommands(testCase.getCommands());
		commands.forEach(c -> builder.append(c));
		
		builder.append(getFooter(testCase));
		return builder;
	}
	
	public List<String> formatCommands(List<Command> commands) {
		List<String> formattedCommands = new ArrayList<>();
		commands.forEach(command -> {
			Formatter formatter = getFormatter(command.getCommand());
			String formatted = formatter.format(command);
			formattedCommands.add(formatted);
		});
		return formattedCommands;				
	}

	public String getHeader(TestCase testCase) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(  "import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory\n\n" +
						"import com.thoughtworks.selenium.Selenium\n" +
						"import org.openqa.selenium.firefox.FirefoxDriver\n" +
						"import org.openqa.selenium.WebDriver\n" +
						"import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium\n" +
						"import static org.junit.Assert.*\n" +
						"import java.util.regex.Pattern\n" +
						"import static org.apache.commons.lang3.StringUtils.join\n\n");
		buffer.append("WebUI.openBrowser('"+ testCase.getBaseUrl() +"')\n");
		buffer.append("driver = DriverFactory.getWebDriver()\n");
		buffer.append("String baseUrl = \""+ testCase.getBaseUrl() +"\"\n");
		buffer.append("selenium = new WebDriverBackedSelenium(driver, baseUrl)\n");
		return buffer.toString();
	}

	public String getFooter(TestCase testCase) {
		return StringUtils.EMPTY;
	}
	
	private Formatter getFormatter(String command) {
		for (Map.Entry<String, Formatter> entry : formatters.entrySet()) {
		    String key = entry.getKey();
		    Formatter formatter = entry.getValue();
		    if (command.contains(key)) {
		    	return formatter;
		    }
		}
		return formatters.get("default");
	}

}
