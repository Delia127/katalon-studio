package com.kms.katalon.selenium.ide.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.selenium.ide.model.Command;
import com.kms.katalon.selenium.ide.model.TestCase;

public final class Formatter {

	private static final Formatter INSTANCE = new Formatter();

	private final Map<String, String> commands = new HashMap<>();

	{
		commands.put("click", "click");
	}
	
	public StringBuilder format(TestCase testCase) {
		StringBuilder builder = new StringBuilder();
		builder.append(getHeader(testCase));
		
		List<String> commands = formatCommands(testCase.getCommands());
		commands.forEach(c -> builder.append(c));
		
		builder.append(getFooter(testCase));
		return builder;
	}
	
	public String formatCommand(Command command) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("selenium.");
		buffer.append(command.getCommand());
		buffer.append("(");
		if (StringUtils.isNotBlank(command.getTarget())) {
			buffer.append("\"" + command.getTarget() + "\"");
			if (StringUtils.isNotBlank(command.getValue())) {
				buffer.append(", \"" + command.getValue() + "\"");
			}
		}
		
		buffer.append(")\n");
		return buffer.toString();
	}
	
	public List<String> formatCommands(List<Command> commands) {
		List<String> formattedCommands = new ArrayList<>();
		commands.forEach(command -> {
			String formatted = formatCommand(command);
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

	public static Formatter getInstance() {
        return INSTANCE;
    }

}
