package com.kms.katalon.selenium.ide.util;

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

public final class HtmlParser {
	
	public static TestSuite parseTestSuite(File file) {
		File parentFile = file.getParentFile();
		String testSuiteHtmlContent = FileUtils.readFileToString(file.toPath());
		
		String testSuiteName = HtmlParser.parseTitle(testSuiteHtmlContent);
		
		List<TestCase> testCases = new ArrayList<>();
		Map<String, String> testCaseFiles = HtmlParser.parseTestCaseFiles(testSuiteHtmlContent);
		testCaseFiles.forEach((key, value) -> {
			String testCaseFilePath = parentFile.getAbsolutePath() + File.separator + value;
			TestCase testCase = HtmlParser.parseTestCase(testCaseFilePath);
			testCase.setFilePath(testCaseFilePath);
			testCases.add(testCase);
		});
		
		TestSuite testSuite = new TestSuite();
		testSuite.setName(testSuiteName);
		testSuite.setTestCases(testCases);
		testSuite.setFilePath(file.getAbsolutePath());
		
		return testSuite;
	}

	public static TestCase parseTestCase(final String testCaseFilePath) {
		String testCaseHtmlContent = FileUtils.readFileToString(new File(testCaseFilePath).toPath());
		List<Command> commands = new ArrayList<>();
		String testCaseBodyContent = parseTestCaseHtmlBodyContent(testCaseHtmlContent);
        if (StringUtils.isNotBlank(testCaseBodyContent)) {
        	Pattern pattern = Pattern.compile("(<tr>.*?</tr>)");
        	Matcher matcher = pattern.matcher(testCaseBodyContent);
            while (matcher.find()) {
            	Command command = parseCommand(matcher.group(1));
            	if (command != null) {
            		commands.add(command);
            	}
            }
        }
        TestCase testCase = new TestCase();
        testCase.setName(parseTitle(testCaseHtmlContent));
        testCase.setBaseUrl(parseBaseUrl(testCaseHtmlContent));
        testCase.setCommands(commands);
		return testCase;
	}
	
	public static Command parseCommand(String commandHtmlContent) {
		Command command = null;
		Pattern pattern = Pattern.compile("(<tr>.*?<td>)(.*?)(</td>.+?<td>)(.*?)(<datalist>)(.*?)(</datalist)(.*?</td>.+?<td>)(.*?)(</td>.*?</tr>)");
        Matcher matcher = pattern.matcher(commandHtmlContent);
        if (matcher.find()) {
        	List<String> options = parseOptions(matcher.group(6));
        	command = createCommand(matcher.group(2), matcher.group(4), matcher.group(9), options);
        } else {
        	pattern = Pattern.compile("(<tr>.*?<td>)(.*?)(</td>.+?<td>)(.*?)(</td>.+?<td>)(.*?)(</td>.*?</tr>)");
        	matcher = pattern.matcher(commandHtmlContent);
        	if (matcher.find()) {
            	command = createCommand(matcher.group(2), matcher.group(4), matcher.group(6), new ArrayList<>());
            } 
        }
		return command;
	}
	
	private static List<String> parseOptions(String dataList) {
		List<String> options = new ArrayList<>();
		Pattern pattern = Pattern.compile("(.*?<option>)(.*?)(</option>.*?)");
        Matcher matcher = pattern.matcher(dataList);
        while (matcher.find()) {
        	options.add(matcher.group(2));
        }
		return options;
	}
	
	private static Command createCommand(String command, String target, String value, List<String> options) {
		Command ret = new Command();
		ret.setCommand(command);
		ret.setTarget(StringUtils.trim(target));
		ret.setValue(StringUtils.trim(value));
		ret.setOptions(options);
		return ret;
	}
	
	public static String parseTitle(String htmlContent) {
		String testSuite = StringUtils.EMPTY;
		Pattern pattern = Pattern.compile("(.*?<title>)(.+?)(</title>.*?)");
        Matcher matcher = pattern.matcher(htmlContent);
        if (matcher.find()) {
        	testSuite = matcher.group(2);
        }
		return testSuite;		
	}
	
	public static String parseBaseUrl(String htmlContent) {
		String testSuite = StringUtils.EMPTY;
		Pattern pattern = Pattern.compile("<link rel=\"selenium.base\" href=\"(.*?)\" />");
        Matcher matcher = pattern.matcher(htmlContent);
        if (matcher.find()) {
        	testSuite = matcher.group(1);
        }
		return testSuite;		
	}
	
	public static String parseTestCaseHtmlBodyContent(String htmlContent) {
		String testCase = StringUtils.EMPTY;
		Pattern pattern = Pattern.compile("(.*?<tbody>)(.*?)(</tbody>.*?)");
        Matcher matcher = pattern.matcher(htmlContent);
        if (matcher.find()) {
        	testCase = matcher.group(0);
        }
		return testCase;
	}
	
	public static Map<String, String> parseTestCaseFiles(String testSuiteHtmlContent) {
		Map<String, String> map = new Hashtable<>();
		Pattern pattern = Pattern.compile("(.+?<a href=\")(.+?)(\">)(.+?)(</a>.+?)");
        Matcher matcher = pattern.matcher(testSuiteHtmlContent);
        while (matcher.find()) {
        	map.put(matcher.group(4), matcher.group(2));
        }
		return map;
	}
	
	public static boolean isSelector(String selector) {
		Pattern pattern = Pattern.compile("(id|name|title|css|class|xpath)(=)(.*?)");
        Matcher matcher = pattern.matcher(selector);
        if (matcher.find()) {
        	return matcher.groupCount() == 3;
        }
        return false;
	}
	
	public static String[] paseSelector(String selector) {
		String[] matches = new String[3];
		Pattern pattern = Pattern.compile("(.*?)(=)(.*?)$");
        Matcher matcher = pattern.matcher(selector);
        if (matcher.find() && matcher.groupCount() == 3) {
        	matches[0] = replaceSpecialChars(removeMarks(matcher.group(1)));
        	matches[1] = removeMarks(matcher.group(2));
        	matches[2] = removeMarks(matcher.group(3));
        }
        return matches;
	}
	
	public static String replaceSpecialChars(String a) {
		return a.replace("link", "text");
	}
	
	public static String removeMarks(String value) {
		return value.replace("'", "").replace("'", "");
	}
	
	
	public static String encodeSelector(String selector) {
		if (StringUtils.isNotBlank(selector)) {
			return FileUtils.encode(selector.replace("=", "_"));
		}
		return StringUtils.EMPTY;
	}
	
}
