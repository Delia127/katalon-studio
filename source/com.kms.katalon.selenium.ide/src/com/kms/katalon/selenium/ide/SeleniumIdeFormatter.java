package com.kms.katalon.selenium.ide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private static final SeleniumIdeFormatter INSTANCE = new SeleniumIdeFormatter();
	
	private final Map<String, Formatter> formatters = new HashMap<>();
	
	{
		formatters.put("assert", new VerifyAndAssertFormatter("assert"));
		formatters.put("verify", new VerifyAndAssertFormatter("verify"));
		formatters.put("store", new StoreFormatter());
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
			return "// Error: command is not found";
		}
		String formatted = formatter.format(command);
		if (StringUtils.isBlank(formatted)) {
			return "// Error: command is not found";
		}
		return formatted;
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

}
