package com.kms.katalon.selenium.ide;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.selenium.ide.model.Command;
import com.kms.katalon.selenium.ide.model.TestCase;
import com.kms.katalon.selenium.ide.model.TestSuite;
import com.kms.katalon.selenium.ide.util.FileUtils;

public final class ImportSeleniumIdeService {

	private static final ImportSeleniumIdeService INSTANCE = new ImportSeleniumIdeService();
	
	public static ImportSeleniumIdeService getInstance() {
        return INSTANCE;
    }
	
	public void importFile(File file) {
		File parentFile = file.getParentFile();
		String testSuiteHtmlContent = FileUtils.readFileToString(file.toPath());
		String testSuiteName = parseTitle(testSuiteHtmlContent);
		
		List<TestCase> testCases = new ArrayList<>();
		Map<String, String> testCaseFiles = parseTestCaseFiles(testSuiteHtmlContent);
		testCaseFiles.forEach((key, value) -> {
			String testCaseFilePath = parentFile.getAbsolutePath() + File.separator + value;
			TestCase testCase = parseTestCase(testCaseFilePath);
			testCases.add(testCase);
		});
		
		TestSuite testSuite = new TestSuite();
		testSuite.setName(testSuiteName);
		testSuite.setTestCases(testCases);
		
		System.out.println(testSuite);
	}
	
	private TestCase parseTestCase(final String testCaseFilePath) {
		String testCaseHtmlContent = FileUtils.readFileToString(new File(testCaseFilePath).toPath());
		List<Command> commands = new ArrayList<>();
		String testCaseContent = parseTestCaseContent(testCaseHtmlContent.trim());
        if (StringUtils.isNotBlank(testCaseContent)) {
        	Pattern pattern = Pattern.compile("(<tr>.*?</tr>)");
        	Matcher matcher = pattern.matcher(testCaseContent);
            while (matcher.find()) {
            	Command command = parseCommand(matcher.group(1));
            	if (command != null) {
            		commands.add(command);
            	}
            }
        }
        TestCase testCase = new TestCase();
        testCase.setName(parseTitle(testCaseHtmlContent));
        testCase.setCommands(commands);
		return testCase;
	}
	
	private Command parseCommand(String commandHtmlContent) {
		Command command = null;
		Pattern pattern = Pattern.compile("(<tr>.*?<td>)(.*?)(</td>.+?<td>)(.*?)(</td>.+?<td>)(.*?)(</td>.*?</tr>)");
        Matcher matcher = pattern.matcher(commandHtmlContent);
        while (matcher.find()) {
        	command = new Command();
        	command.setCommand(matcher.group(2));
        	command.setTarget(matcher.group(4));
        	command.setValue(matcher.group(6));
        }
		return command;
	}
	
	private String parseTitle(String htmlContent) {
		String testSuite = StringUtils.EMPTY;
		Pattern pattern = Pattern.compile("(.*?<title>)(.+?)(</title>.*?)");
        Matcher matcher = pattern.matcher(htmlContent);
        if (matcher.find()) {
        	testSuite = matcher.group(2);
        }
		return testSuite;		
	}
	
	private String parseTestCaseContent(String htmlContent) {
		String testCase = StringUtils.EMPTY;
		Pattern pattern = Pattern.compile("(.*?<tbody>)(.*?)(</tbody>.*?)");
        Matcher matcher = pattern.matcher(htmlContent);
        if (matcher.find()) {
        	testCase = matcher.group(0);
        }
		return testCase;
	}
	
	private Map<String, String> parseTestCaseFiles(String testSuiteHtmlContent) {
		Map<String, String> map = new Hashtable<>();
		Pattern pattern = Pattern.compile("(.+?<a href=\")(.+?)(\">)(.+?)(</a>.+?)");
        Matcher matcher = pattern.matcher(testSuiteHtmlContent);
        while (matcher.find()) {
        	map.put(matcher.group(4), matcher.group(2));
        }
		return map;
	}
}
