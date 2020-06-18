package com.kms.katalon.selenium.ide;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.selenium.ide.model.TestCase;
import com.kms.katalon.selenium.ide.model.TestSuite;
import com.kms.katalon.selenium.ide.util.FileUtils;
import com.kms.katalon.selenium.ide.util.HtmlParser;
import com.kms.katalon.selenium.ide.util.JsonParser;
import com.kms.katalon.selenium.ide.util.ParsedResult;
import com.kms.katalon.selenium.ide.util.TestObjectParser;

public final class SeleniumIdeParser {

	private static final SeleniumIdeParser INSTANCE = new SeleniumIdeParser();
	
	public static SeleniumIdeParser getInstance() {
        return INSTANCE;
    }
	
	public boolean isTestSuiteFile(File file) {
		String extension = com.google.common.io.Files.getFileExtension(file.getName());
		return StringUtils.isBlank(extension) || HtmlParser.hasSuiteTable(file) || !HtmlParser.hasBaseUrl(file);
	}
	
	public boolean isTestCaseFile(File file) {
		String extension = com.google.common.io.Files.getFileExtension(file.getName());
		return "html".equalsIgnoreCase(extension) && HtmlParser.hasBaseUrl(file);
	}
	
    public boolean isSeleniumIdeV3File(File file) {
        String extension = com.google.common.io.Files.getFileExtension(file.getName());
        return "side".equalsIgnoreCase(extension);
    }

	public TestSuite parseTestSuite(File file) {
		if (!isTestSuiteFile(file)) {
			return null;
		}
		return HtmlParser.parseTestSuite(file);
	}
	
	public TestCase parseTestCase(File file) {
		if (!isTestCaseFile(file)) {
			return null;
		}
		return HtmlParser.parseTestCaseFromFile(file.getAbsolutePath());
	}
	
	public String parseTestObjectName(String target) {
		return FileUtils.encode(target);
	}
	
	public String parseLocator(String target) {
		return TestObjectParser.parse(target);
	}

    public ParsedResult parseSeleniumIdeV3File(File file) {
        return JsonParser.parse(file);
    }
}
