package com.kms.katalon.selenium.ide;

import java.io.File;

import com.kms.katalon.selenium.ide.model.Command;
import com.kms.katalon.selenium.ide.model.TestSuite;
import com.kms.katalon.selenium.ide.util.FileUtils;
import com.kms.katalon.selenium.ide.util.HtmlParser;

public final class SeleniumIdeParser {

	private static final SeleniumIdeParser INSTANCE = new SeleniumIdeParser();
	
	public static SeleniumIdeParser getInstance() {
        return INSTANCE;
    }
	
	public TestSuite parseTestSuite(File file) {
		return HtmlParser.parseTestSuite(file);
	}
	
	public String parseTestObjectName(Command command) {
		return FileUtils.encode(command.getTarget());
	}
	
}
